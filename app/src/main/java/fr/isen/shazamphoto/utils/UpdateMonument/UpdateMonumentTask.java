package fr.isen.shazamphoto.utils.UpdateMonument;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;

import fr.isen.shazamphoto.database.Monument;
import fr.isen.shazamphoto.utils.ConfigurationShazam;

public abstract class UpdateMonumentTask extends AsyncTask<Monument, Void, Boolean> {


    @Override
    protected Boolean doInBackground(Monument... params) {
        Monument monument = params[0];
        Boolean returnValue = false;

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(createArguments(monument));
            entity.setContentType("application/x-www-form-urlencoded");

            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut("http://"+ ConfigurationShazam.IP_SERVER+"/shazam/api.php");
            httpPut.setEntity(entity);

            // Executing HTTP Post Request
            HttpResponse response = httpclient.execute(httpPut);

            if(response.getStatusLine().getStatusCode() == 200) {
                returnValue = true;
            }
        }
        catch(Exception e) {}

        return returnValue;
    }

    public abstract List<NameValuePair> createArguments(Monument monument);
}