package com.example.user.ex_inmobiles;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ex_inmobiles.entity.ImageEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private List<ImageEntity> imgs = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize and run timer
        timerValue = (TextView) findViewById(R.id.timerValue);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        //initialize database
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "db").build();

        //run api call to get updated data from server
        try {
            new ApiCall(new URL("http://test.inmobiles.net/testapi/api/Initialization/SelectAllImages")).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //set onclick listener for recycler view to open fragment for selected item
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerViewClickListener listener = (view, position) -> {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ImageEntity img = adapter.getList().get(position);
            MyDialogFragment dialogFragment = MyDialogFragment.newInstance(img.getTitle(),img.getDescription(),img.getLink());
            dialogFragment.show(fragmentManager, "Sample Fragment");

        };



        //observe database data to update view after api call
        LiveData<List<ImageEntity>> imageLiveData = appDatabase.daoAccess().fetchAllData();
        imageLiveData.observe(this, new Observer<List<ImageEntity>>() {
            @Override
            public void onChanged(@Nullable List<ImageEntity> imgs) {
                adapter = new ListAdapter(imgs,listener);
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
                recyclerView.setAdapter(adapter);
            }
        });

    }


    //timer thread
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }

    };

    //save timer config to survive rotation
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putLong("start", startTime);
        outState.putLong("timeswap", timeSwapBuff);
        super.onSaveInstanceState(outState);
    }

    //restore timer info after rotation
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        startTime = savedInstanceState.getLong("start");
        timeSwapBuff = savedInstanceState.getLong("timeswap");
    }

    //pause timer on application pause state
    @Override
    public void onPause() {
        super.onPause();
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);


    }

    //resume timer on resume state
    @Override
    public void onResume() {
        super.onResume();
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }


    //API call thread to get data from server
    private class ApiCall extends AsyncTask<Void, Void, String> {

        private Exception exception;
        private URL url;

        public ApiCall(URL url){
            this.url = url;
        }

        protected String doInBackground(Void... urls) {

            try {
                String response;
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();


                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    ImageEntity imgs[] = new ImageEntity[jsonArray.length()];
                    Log.i("INFO", appDatabase.daoAccess().fetchAllData().toString());
                    for(int n = 0; n < jsonArray.length(); n++)
                    {
                        jsonObject = jsonArray.getJSONObject(n);
                        ImageEntity temp = new ImageEntity();
                        temp.setId(jsonObject.getInt("Id"));
                        temp.setTitle(jsonObject.getString("title"));
                        temp.setDescription(jsonObject.getString("description"));
                        temp.setLink(jsonObject.getString("link"));
                        imgs[n] = temp;
                    }
                    appDatabase.daoAccess().insertImages(imgs);

                    return null;
                }catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return e.getMessage();}
                finally{
                    urlConnection.disconnect();
                }
            }catch (Exception e){
                return e.getMessage();
            }
        }
        protected void onPostExecute(String error){
            if(error != null)
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
        }

    }

}
