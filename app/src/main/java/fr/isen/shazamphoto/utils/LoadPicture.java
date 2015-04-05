package fr.isen.shazamphoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import fr.isen.shazamphoto.database.Monument;

public class LoadPicture {

    public static final int HDPI_WIDTH = 800;
    public static final int HDPI_HEIGHT = 700;
    public static final int IMAGE_PROCESS_WIDTH = 480;
    public static final int IMAGE_PROCESS_HEIGHT = 640;

    public static void setPictureFromFile(String photoPath, ImageView imageView, int reqWidth, int reqHeight) {
        // Retrieve the bitmap sampled and set the image view with the bitmap
        Bitmap bitmap = getPictureFromFile(photoPath, reqWidth, reqHeight);
        imageView.setImageBitmap(bitmap);
    }

    public static Bitmap setPicture(Monument monument, int reqWidth, int reqHeight, ImageView imageView) {
        Bitmap bitmap = null;
        if (monument.getPhotoPathLocal() != null && !monument.getPhotoPathLocal().isEmpty()) {
            bitmap = getPictureFromFile(monument.getPhotoPathLocal(), reqWidth, reqHeight);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
        if (bitmap == null && monument.getPhotoPath() != null && !monument.getPhotoPath().isEmpty()) {
            GetImageURLTask getImageURLTask = new GetImageURLTask(imageView);
            getImageURLTask.execute(monument.getPhotoPath());
        }
        return bitmap;
    }

    public static Bitmap getPictureFromFile(String photoPath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap oldBitmap = BitmapFactory.decodeFile(photoPath, options);
        Bitmap bitmap = truncateBitmap(oldBitmap);
        Log.v("Shazam", "LP getFromFile : "+bitmap);
        return bitmap;
    }

    public static Bitmap getPictureFromURL(String url, int reqWidth, int reqHeight) {

        Bitmap bitmap = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            InputStream in = new java.net.URL(url).openStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            in.close();
            in = new java.net.URL(url).openStream();
            options.inJustDecodeBounds = false;
            Bitmap oldBitmap = BitmapFactory.decodeStream(in, null, options);
            // Truncate the bitmap
            bitmap = truncateBitmap(oldBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Shazam", "Exception in LP : "+e.getMessage());
        }

        return bitmap;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap checkRotation(Bitmap bitmap, int reqWidth, int reqHeight) {
        Bitmap bitmapFinal = bitmap;
        if (bitmap != null) {
            if (bitmap.getHeight() > bitmap.getWidth()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
                //   bitmapFinal = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            }
        }

        return bitmapFinal;
    }

    public static Bitmap truncateBitmap(Bitmap bitmap){
        Bitmap result = null;
        if(bitmap != null){
            int x = (int)(bitmap.getWidth()*0.3);
            int y =(int)(bitmap.getHeight()*0.3);
            Log.v("Shazam", "LP truncate x: "+x+" width : "+bitmap.getWidth());
            Log.v("Shazam", "LP truncate y: "+y+" width : "+bitmap.getHeight());
            result = Bitmap.createBitmap(bitmap, x, y, LoadPicture.HDPI_WIDTH, LoadPicture.HDPI_HEIGHT);
        }

        return result;

    }
}
