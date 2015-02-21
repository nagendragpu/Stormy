package nagendra.com.stormy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
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

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {



                try {
                     //Syncronize method
                    // Response response=call.execute();

                    if (response.isSuccessful()){
                        Log.v(TAG, response.body().string());


                    }else{

                        alertUserAboutError();
                    }



                } catch (IOException e) {
                    Log.e(TAG,"Exception Caught",e);
                }


            }
        });

        Log.e(TAG,"Main Thread which is UI Running 0");

    }

    private void alertUserAboutError() {

        AlertDialogFragments dialog=new AlertDialogFragments();
        dialog.show(getFragmentManager(),"Err0r");


    }


}
