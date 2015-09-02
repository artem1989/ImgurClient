package pl.mobilab.imgurclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ImgurServiceHelper {
	
	private static final String TAG = "ServiceHelper";

	public String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try { 
            HttpClient httpclient = new DefaultHttpClient();
            
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", "Client-ID 17bb50024fff8fe");
            //
            
            HttpResponse httpResponse = httpclient.execute(request);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
        	}	
 
        } catch (Exception e) {
            Log.e("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
	
	private String convertInputStreamToString(InputStream inputStream) {
		StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        
        try {
			while((line = bufferedReader.readLine()) != null){
				sb.append(line);
			}
		} catch (IOException e) {
			Log.e(TAG, "failed to read input stream");
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.e(TAG, "failed to close input stream");
			}
		}
        
        return sb.toString();
    }
	
	public boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
	
	public List<String> processJsonResult(String json) {
		Log.i(TAG, json);
		List<String> urls = new ArrayList<String>();
		try {
			JSONArray data = new JSONObject(json).getJSONArray("data");
			for (int i = 0; i < data.length(); i++) {
			    JSONObject image = data.getJSONObject(i);
			    urls.add(image.getString("link"));
			}
		} catch (JSONException e) {
			Log.e(TAG, "incorrect Json mapping by client");
		}
		return urls;
	}

}
