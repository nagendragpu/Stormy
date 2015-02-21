package nagendra.com.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    public static final String TAG=MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        String apiKey="f26959a54749690b5a8d366e3534dea0";
        double latitude=37.8267;
        double longitude=-122.423;
        String forecastUrl="https://api.forecast.io/forecast/"+apiKey+
                "/"+latitude+","+longitude;

        if(isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(forecastUrl).build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {

                    try {
                        //Syncronize method
                        // Response response=call.execute();
                        String jsonData=response.body().string();
                        Log.v(TAG,jsonData );
                        if (response.isSuccessful()) {

                        mCurrentWeather=getCurrentDetails(jsonData);


                        } else {

                           alertUserAboutError();
                        }


                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception Caught", e);
                    }

                    catch (JSONException e){
                        Log.e(TAG, "Exception Caught", e);
                    }


                }
            });

        }else{

            Toast.makeText(this,getString(R.string.network_unavailable),Toast.LENGTH_LONG).show();
        }
        Log.e(TAG,"Main Thread which is UI Running 0");

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{

        JSONObject forecast=new JSONObject(jsonData);
        String timezone=forecast.getString("timezone");
        Log.i(TAG,"Returned Data"+timezone);

        JSONObject currently=forecast.getJSONObject("currently");
       // JSONObject min=forecast.getJSONObject("minutely");
       // JSONObject Data =forecast.getJSONObject("data");
        CurrentWeather currentWeather=new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPercipchance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG,currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        boolean isAvailable=false;

        if(networkInfo!=null && networkInfo.isConnected()){
            isAvailable=true;
        }

        return isAvailable;


    }

    private void alertUserAboutError() {

        AlertDialogFragments dialog=new AlertDialogFragments();
        dialog.show(getFragmentManager(),"Err0r");


    }


}
