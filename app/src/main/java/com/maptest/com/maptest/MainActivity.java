package com.maptest.com.maptest;

/*import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;*/

/*premkumarnew80@gmail.com*/

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private TextView mno;
    private ImageView image;
    GoogleMap map;
    private OnInfoWindowElemTouchListener infoButtonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
         map = mapFragment.getMap();


        mapWrapperLayout.init(map, getPixelsFromDp(this, 39 + 20));


        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);
        this.mno = (TextView)infoWindow.findViewById(R.id.type_mno);
        this.image = (ImageView)infoWindow.findViewById(R.id.dealImg);

        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                getResources().getDrawable(R.drawable.direction), //btn_default_normal_holo_light
                getResources().getDrawable(R.drawable.direction)) //btn_default_pressed_holo_light
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {

                Toast.makeText(MainActivity.this, marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);


        map.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                infoTitle.setText(marker.getTitle());

                infoButtonListener.setMarker(marker);
                String[] allIdsArray = TextUtils.split(marker.getSnippet(), ",");
                ArrayList<String> idsList = new ArrayList<String>(Arrays.asList(allIdsArray));

                infoSnippet.setText(idsList.get(0));
                mno.setText(idsList.get(1));

                //Picasso.with(getApplicationContext()).load("your path"+idsList.get(2).toString()).into(image);

                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);


                return infoWindow;
            }
        });

        // Let's add a couple of markers
        SyncMethod("http://byteofearth.com/TestMyShop/APIS/list_Myshop.php");


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.


    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
    public void SyncMethod(final String GetUrl) {
        Log.i("Url.............", GetUrl);
        final Thread background = new Thread(new Runnable() {
            // After call for background.start this run method call
            public void run() {
                try {
                    String url = GetUrl;
                    String SetServerString = "";
                    // document all_stuff = null;

                    SetServerString = fetchResult(url);
                    threadMsg(SetServerString);
                } catch (Throwable t) {
                    Log.e("Animation", "Thread  exception " + t);
                }
            }

            private void threadMsg(String msg) {

                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler11.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler11.sendMessage(msgObj);
                }
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler11 = new Handler() {
                public void handleMessage(Message msg) {
                    try
                    {
                        String aResponse = msg.getData().getString("message");
                        Log.e("Exam", "screen>>" + aResponse);

                        // loading.cancel();

                        JSONObject get_res = new JSONObject(aResponse);


                        JSONArray array = new JSONArray();

                        array = get_res.getJSONArray("idioms");
                        Log.e("mess", "screen>>" + array.toString());


                        for (int aa = 0; aa <= array.length(); aa++) {

                            Double d = new Double(array.getJSONObject(aa).getString("lati"));
                            Double d1 = new Double(array.getJSONObject(aa).getString("loti"));

                            map.addMarker(new MarkerOptions()
                                    .title(array.getJSONObject(aa).getString("shoptype").toString())
                                    .snippet(array.getJSONObject(aa).getString("shopname").toString() + "," + array.getJSONObject(aa).getString("Mobile").toString() + "," + array.getJSONObject(aa).getString("shopphoto").toString())

                                    .position(new LatLng(d, d1)));

                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d, d1), 8.0f));

                        }

                    } catch (Exception e) {

                    }


                }
            };
        });
        // Start Thread
        background.start();
    }

    public String fetchResult(String urlString) throws JSONException {
        StringBuilder builder;
        BufferedReader reader;
        URLConnection connection = null;
        URL url = null;
        String line;
        builder = new StringBuilder();
        reader = null;
        try {
            url = new URL(urlString);
            connection = url.openConnection();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            //Log.d("DATA", builder.toString());
        } catch (Exception e) {

        }
        //JSONArray arr=new JSONArray(builder.toString());
        return builder.toString();
    }
}


