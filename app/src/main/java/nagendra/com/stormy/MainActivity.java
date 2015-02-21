package nagendra.com.stormy;

import android.app.DownloadManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    public static final String TAG=MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String apiKey="f26959a54749690b5a8d366e3534dea0";
        double latitude=37.8267;
        double longitude=-122.423;
        String forecastUrl="https://api.forecast.io/forecast/"+apiKey+
                "/"+latitude+","+longitude;

        OkHttpClient client=new OkHttpClient();

        Request request= new Request.Builder().url(forecastUrl).build();

        Call call=client.newCall(request);
        try {
            Response response=call.execute();

            if (response.isSuccessful())
                Log.v(TAG, response.body().string());

        } catch (IOException e) {
            Log.e(TAG,"Exception Caught",e);
        }


    }



}
