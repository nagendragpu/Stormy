package nagendra.com.stormy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG=MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;

    @InjectView(R.id.temperatureLabel) TextView mtemperatureLabel;
    @InjectView(R.id.timeLabel) TextView mtimeLabel;
    @InjectView(R.id.humidityValue) TextView mhumidityValue;
    @InjectView(R.id.precipValue) TextView mprecipValue;
    @InjectView(R.id.summaryLabel) TextView msummaryLabel;
    @InjectView(R.id.iconImageView) ImageView miconImageView;
    @InjectView(R.id.refreshImageView) ImageView mrefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.locationLabel) TextView mlocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);
        //final double latitude=37.8267;
        final double latitude=19.285;
        //final double longitude=-122.423;
        final double longitude=72.869;
        mrefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude,longitude);
            }
        });

        getForecast(latitude,longitude);
        Log.e(TAG,"Main Thread which is UI Running 0");

    }

    private void getForecast(double latitude,double longitude) {
        String apiKey="f26959a54749690b5a8d366e3534dea0";

        String forecastUrl="https://api.forecast.io/forecast/"+apiKey+
                "/"+latitude+","+longitude;

        if(isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        //Syncronize method
                        // Response response=call.execute();
                        String jsonData=response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                        mCurrentWeather=getCurrentDetails(jsonData);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

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

            Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mrefreshImageView.setVisibility(View.INVISIBLE);
        }else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mrefreshImageView.setVisibility(View.VISIBLE);
        }
    }
    private void updateDisplay() {
        mtemperatureLabel.setText(mCurrentWeather.getTemperature()+ "");
        mtimeLabel.setText("At "+mCurrentWeather.getFormattedTime()+" It will be");
        mhumidityValue.setText(mCurrentWeather.getHumidity()+"");
        mprecipValue.setText(mCurrentWeather.getPercipchance() + "%");
        msummaryLabel.setText(mCurrentWeather.getSummary());
        mlocationLabel.setText(mCurrentWeather.getTimeZone());

        Drawable drawable=getResources().getDrawable(mCurrentWeather.getIconId());
        miconImageView.setImageDrawable(drawable);

        //new thing


    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{

        JSONObject forecast=new JSONObject(jsonData);
        String timezone=forecast.getString("timezone");
        Log.i(TAG,"Returned Data "+timezone);

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
