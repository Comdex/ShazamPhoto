package fr.isen.shazamphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class ImageProcessing /*extends AsyncTask<String, Void, JSONObject>*/ {

    public static final Size size = new Size(640, 480);

    private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost = new HttpPost("http://37.187.216.159/shazam/identify.php");
    private HttpResponse response;
    private String keyPointToSend = null;
    private String descriptorToSend = null;

    // Information about the picture
    private Mat descriptors;
    private KeyPoint[] keyPointArray;
    private String photoPath;

    // Add the several information about the picture to the request to identify the monument
    private ShazamProcessing shazamProcessing;

    private Context activityContext = null;


    public ImageProcessing(Context activityContext, ShazamProcessing shazamProcessing,
                           String photoPath) {
        setActivityContext(activityContext);
        this.shazamProcessing = shazamProcessing;
        this.photoPath = photoPath;
    }

    public Context getActivityContext() {
        return activityContext;
    }

    public void setActivityContext(Context activityContext) {
        this.activityContext = activityContext;
    }

    /*
        public static Mat matFromJson(String json) {
            JsonParser parser = new JsonParser();
            JsonObject JsonObject = parser.parse(json).getAsJsonObject();

            int rows = JsonObject.get("rows").getAsInt();
            int cols = JsonObject.get("cols").getAsInt();
            int type = JsonObject.get("type").getAsInt();

            String dataString = JsonObject.get("data").getAsString();
            byte[] data = Base64.decode(dataString.getBytes(), Base64.DEFAULT);

            Mat mat = new Mat(rows, cols, type);
            mat.put(0, 0, data);

            return mat;
        }

        public static MatOfKeyPoint keypointsFromJson(String json) {
            MatOfKeyPoint result = new MatOfKeyPoint();

            JsonParser parser = new JsonParser();
            JsonArray jsonArr = parser.parse(json).getAsJsonArray();

            int size = jsonArr.size();

            KeyPoint[] kpArray = new KeyPoint[size];

            for (int i = 0; i < size; i++) {
                KeyPoint kp = new KeyPoint();

                JsonObject obj = (JsonObject) jsonArr.get(i);

                Point point = new Point(
                        obj.get("x").getAsDouble(),
                        obj.get("y").getAsDouble()
                );

                kp.pt = point;
                kp.class_id = obj.get("class_id").getAsInt();
                kp.size = obj.get("size").getAsFloat();
                kp.angle = obj.get("angle").getAsFloat();
                kp.octave = obj.get("octave").getAsInt();
                kp.response = obj.get("response").getAsFloat();

                kpArray[i] = kp;
            }

            result.fromArray(kpArray);

            return result;
        }

        public static String keypointsToJson(MatOfKeyPoint mat) {
            if (mat != null && !mat.empty()) {
                Gson gson = new Gson();

                JsonArray jsonArr = new JsonArray();

                KeyPoint[] array = mat.toArray();
                for (int i = 0; i < array.length; i++) {
                    KeyPoint kp = array[i];

                    JsonObject obj = new JsonObject();

                    obj.addProperty("class_id", kp.class_id);
                    obj.addProperty("x", kp.pt.x);
                    obj.addProperty("y", kp.pt.y);
                    obj.addProperty("size", kp.size);
                    obj.addProperty("angle", kp.angle);
                    obj.addProperty("octave", kp.octave);
                    obj.addProperty("response", kp.response);

                    jsonArr.add(obj);
                }

                JSONObject list = new JSONObject();
                try {
                    list.put("keypoints", jsonArr);
                }
                catch(Exception e) {}


                String json = gson.toJson(list);

                return json;
            }
            return "{}";
        }

        public String matToJson(Mat mat) {
            JsonObject obj = new JsonObject();

            if (mat.isContinuous()) {
                int cols = mat.cols();
                int rows = mat.rows();
                int elemSize = (int) mat.elemSize();

                byte[] data = new byte[cols * rows * elemSize];

                mat.get(0, 0, data);

                obj.addProperty("rows", mat.rows());
                obj.addProperty("cols", mat.cols());
                obj.addProperty("type", mat.type());

                // We cannot set binary data to a json object, so:
                // Encoding data byte array to Base64.
                String dataString = new String(Base64.encode(data, Base64.DEFAULT));

                obj.addProperty("data", dataString);

                Gson gson = new Gson();
                String json = gson.toJson(obj);

                return json;
            } else {

            }
            return "{}";
        }
    */
    public void recognise() {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, getActivityContext(), mOpenCVCallBack);
    }

    public final ImageProcessing process = this;
    public BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(getActivityContext()) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    String picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File imgFile = new File(photoPath);
                    if (imgFile != null && imgFile.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);


                        Mat img1 = new Mat();
                        Mat resized = new Mat();
                        descriptors = new Mat();
                        MatOfKeyPoint keypoints = new MatOfKeyPoint();

                        Utils.bitmapToMat(myBitmap, img1);

                        Imgproc.resize(img1, resized, size);
                        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2GRAY, 7);

                        detector.detect(img1, keypoints);

                        descriptor.compute(img1, keypoints, descriptors);
                        descriptors = descriptors.clone();
                        keyPointArray = keypoints.toArray();

                        shazamProcessing.setDescriptors(descriptors);
                        shazamProcessing.setKeyPoints(keyPointArray);
                        /*setKeyPointToSend(keypointsToJson(keypoints));
                        setDescriptorToSend(matToJson(descriptors));
                        setKeyPointArray(keypoints.toArray());*/

                        //process.execute();

                    } else {
                        Toast.makeText(activityContext, "Error : image null or doesn't exist", Toast.LENGTH_LONG).show();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
/*
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonResponse = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("listskeypoints", KeyPoints.toJson(this.keyPointArray).toString()));
            nameValuePairs.add(new BasicNameValuePair("descriptors", Descriptors.toJson(this.descriptors).toString()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);

            entity.setContentType("application/x-www-form-urlencoded");
            httppost.setEntity(entity);

            // Execute HTTP Post Request
            response = httpclient.execute(httppost);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println("Result Identify : " + result.toString());
            jsonResponse = new JSONObject(result.toString());
        } catch (Exception e) {

        }
        return jsonResponse;
    }

    @Override
    public void onPostExecute(JSONObject result) {
        //monument doesn't exist
        Monument monument1 = new Monument();
        monument1.setDescriptors(this.descriptors);
        monument1.setKeyPoints(this.keyPointArray);
        Intent intent = new Intent(this.getActivityContext(), UnidentifiedMonument.class);
        Bundle args = new Bundle();
        args.putSerializable(Monument.NAME_SERIALIZABLE, monument1);
        intent.putExtras(args);
        getActivityContext().startActivity(intent);

        try {

            if(result != null){
                if (result.toString().equals("{}")) {
                    Toast.makeText(this.activityContext, " Monument not identify", Toast.LENGTH_LONG).show();
                    Monument monument = new Monument();
                    monument.setDescriptors(this.descriptors);
                    monument.setKeyPoints(this.keyPointArray);
                    Intent intent = new Intent(this.getActivityContext(), UnidentifiedMonument.class);
                    Bundle args = new Bundle();
                    args.putSerializable(Monument.NAME_SERIALIZABLE, monument);
                    intent.putExtras(args);
                    getActivityContext().startActivity(intent);
                } else{
                    Toast.makeText(this.activityContext, "Monument identified", Toast.LENGTH_LONG).show();
                    Toast.makeText(this.activityContext, "Result : "+result.toString(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this.activityContext, "Answser server NULL", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this.activityContext, "Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
*/

    public HttpPost getHttppost() {
        return httppost;
    }

    public void setHttppost(HttpPost httppost) {
        this.httppost = httppost;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public HttpClient getHttpclient() {
        return httpclient;
    }

    public void setHttpclient(HttpClient httpclient) {
        this.httpclient = httpclient;
    }

    public String getKeyPointToSend() {
        return keyPointToSend;
    }

    public void setKeyPointToSend(String keyPointToSend) {
        this.keyPointToSend = keyPointToSend;
    }

    public String getDescriptorToSend() {
        return descriptorToSend;
    }

    public void setDescriptorToSend(String descriptorToSend) {
        this.descriptorToSend = descriptorToSend;
    }

    public void setKeyPointArray(KeyPoint[] keyPointArray) {
        this.keyPointArray = keyPointArray;
    }
}