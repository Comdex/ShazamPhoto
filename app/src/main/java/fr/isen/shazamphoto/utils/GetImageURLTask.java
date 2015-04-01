package fr.isen.shazamphoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;

public class GetImageURLTask extends AsyncTask<String, Void, Bitmap>{

    private ImageView imageView;

    public GetImageURLTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urldisplay = params[0];
        Bitmap result = null;
        try {
            result = LoadPicture.getPictureFromURL(urldisplay, LoadPicture.HDPI_WIDTH, LoadPicture.HDPI_HEIGHT);
            //LoadPicture.setPictureFromURL(urldisplay, imageView, LoadPicture.HDPI_WIDTH, LoadPicture.HDPI_HEIGHT);
        } catch (Exception e) {
            Log.e("Exception in GetImageURLTask", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
    }

}
