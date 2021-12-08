package com.example.openquick.demos;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import com.example.openquick.MapApiConst;
import com.example.openquick.R;
import com.kakao.util.helper.Utility;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.kakao.util.maps.helper.Utility.getKeyHash;
import static net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MarkerActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener,
        NavigationView.OnNavigationItemSelectedListener {

    private MapView mMapView;
    MapPoint center;
    double curlat, curlng;
    ArrayList<MapPOIItem> mDefaultMarkerlist_1 = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> mDefaultMarkerlist_2 = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> mDefaultMarkerlist_3 = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> mDefaultMarkerlist_4 = new ArrayList<MapPOIItem>();
    ArrayList<MapPOIItem> mDefaultMarkerlist = new ArrayList<MapPOIItem>();


    JSONArray result = null;


    private MapPOIItem mDefaultMarker;

    public String receiveMsg = "";

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    FloatingActionButton floatingActionButton;
    FloatingActionButton floatingActionButton2;

    CheckBox chk1;
    CheckBox chk2;
    CheckBox chk3;
    CheckBox chk4;

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            String[] arr = poiItem.getItemName().split(",");
            ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(0);
            switch (arr[1]) {
                case "xingxing":
                    ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText("씽씽");
                    break;
                case "kickgoing":
                    ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText("킥고잉" + "(" + arr[0] + ")");
                    break;
                case "swing":
                    ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText("스윙" + "(" + arr[0] + ")");
                    break;
                case "gogossing":
                    ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText("고고씽" + "(" + arr[0] + ")");
                    break;
                default:
                    ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(arr[1] + "(" + arr[0] + ")");
                    break;
            }
            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText("배터리" + arr[2] + "%");
            return mCalloutBalloon;
        }


        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_nested_mapview);
        chk1 = (CheckBox) findViewById(R.id.chk1);
        chk2 = (CheckBox) findViewById(R.id.chk2);
        chk3 = (CheckBox) findViewById(R.id.chk3);
        chk4 = (CheckBox) findViewById(R.id.chk4);
        getAppKeyHash();
        System.out.println("시발");
        Utility.getKeyHash(this);
        System.out.println("시발");

        this.setListener();
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                center = mapPointWithGeoCoord(curlat, curlng);
                mMapView.setMapCenterPointAndZoomLevel(center, 0, true);
            }
        });

        floatingActionButton2 = findViewById(R.id.reload);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMapView.removeAllPOIItems();
            }
        });

        initLayout();

        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCurrentLocationEventListener(this);
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        getThread t1 = new getThread();
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        mMapView.setZoomLevel(0, true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                createDefaultMarker(mMapView);
                mMapView.addPOIItems(mDefaultMarkerlist_1.toArray(new MapPOIItem[mDefaultMarkerlist_1.size()]));
                mMapView.addPOIItems(mDefaultMarkerlist_2.toArray(new MapPOIItem[mDefaultMarkerlist_2.size()]));
                mMapView.addPOIItems(mDefaultMarkerlist_3.toArray(new MapPOIItem[mDefaultMarkerlist_3.size()]));
                mMapView.addPOIItems(mDefaultMarkerlist_4.toArray(new MapPOIItem[mDefaultMarkerlist_4.size()]));
            }
        }).start();


    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    int i;

    private void createDefaultMarker(MapView mapView) {
        try {
            for (i = 0; i < result.length(); i++) {
                mDefaultMarker = new MapPOIItem();
                mDefaultMarker.setItemName((((JSONObject) (result.get(i))).get("kickb_imei").toString()) + "," + (((JSONObject) (result.get(i))).get("kickb_com").toString()) + "," + (((JSONObject) (result.get(i))).get("kickb_bat").toString()));
                mDefaultMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(((JSONObject) (result.get(i))).get("kickb_lat").toString()), Double.parseDouble(((JSONObject) (result.get(i))).get("kickb_lng").toString())));
                mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

                String company = "";
                company = (((JSONObject) (result.get(i))).get("kickb_com").toString());
                switch (company) {
                    case "kickgoing":
                        mDefaultMarker.setCustomImageResourceId(R.drawable.kickgoing_marker);
                        mDefaultMarkerlist_1.add(mDefaultMarker);
                        break;
                    case "xingxing":
                        mDefaultMarker.setCustomImageResourceId(R.drawable.xingxing_marker);
                        mDefaultMarkerlist_2.add(mDefaultMarker);
                        break;
                    case "swing":
                        mDefaultMarker.setCustomImageResourceId(R.drawable.swing_marker);
                        mDefaultMarkerlist_3.add(mDefaultMarker);
                        break;
                    case "gogossing":
                        mDefaultMarker.setCustomImageResourceId(R.drawable.gogo_marker);
                        mDefaultMarkerlist_4.add(mDefaultMarker);
                        break;
                    default:
                        mDefaultMarker.setCustomImageResourceId(R.drawable.map_pin_white);
                        mDefaultMarkerlist.add(mDefaultMarker);
                        break;
                }
                mDefaultMarker.setCustomImageAutoscale(false);
                mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                mDefaultMarker.setLeftSideButtonResourceIdOnCalloutBalloon(300000);

            }
        } catch (JSONException e) {
            System.out.println("오류 발생");
        } catch (Exception e) {
            System.out.println("오류 발생");
        }
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }


    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Toast.makeText(this, "Clicked " + mapPOIItem.getItemName() + " Callout Balloon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        curlat = mapPointGeo.latitude;
        curlng = mapPointGeo.longitude;
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
    }

    public class getThread extends Thread {
        @Override
        public void run() {
            try {
                String str;
                URL url = new URL("http://172.16.22.239:8080/openQuick/kick_list.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "Application/json");
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setDoInput(true);

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    System.out.println("에러 발생");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println("에러 발생");

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("에러 발생");

            }
            try {
                JSONArray res = new JSONArray(receiveMsg);
                result = res;
            } catch (JSONException e) {
            }
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setListener() {
        View.OnClickListener Listener = new View.OnClickListener() {
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.chk1:
                        if (((CheckBox) view).isChecked()) {
                            for (int b = 0; b < mDefaultMarkerlist_1.size(); b++) {
                                mDefaultMarkerlist_1.get(b).setAlpha(1.0f);
                            }
                        } else {
                            for (int b = 0; b < mDefaultMarkerlist_1.size(); b++) {
                                mDefaultMarkerlist_1.get(b).setAlpha(0.0f);
                            }
                        }
                        break;

                    case R.id.chk2:
                        if (((CheckBox) view).isChecked()) {
                            for (int b = 0; b < mDefaultMarkerlist_2.size(); b++) {
                                mDefaultMarkerlist_2.get(b).setAlpha(1.0f);
                            }
                        } else {
                            for (int b = 0; b < mDefaultMarkerlist_2.size(); b++) {
                                mDefaultMarkerlist_2.get(b).setAlpha(0.0f);
                            }
                        }
                        break;

                    case R.id.chk3:
                        if (((CheckBox) view).isChecked()) {
                            for (int b = 0; b < mDefaultMarkerlist_3.size(); b++) {
                                mDefaultMarkerlist_3.get(b).setAlpha(1.0f);
                            }

                        } else {
                            for (int b = 0; b < mDefaultMarkerlist_3.size(); b++) {
                                mDefaultMarkerlist_3.get(b).setAlpha(0.0f);
                            }
                        }
                        break;
                    case R.id.chk4:
                        if (((CheckBox) view).isChecked()) {
                            for (int b = 0; b < mDefaultMarkerlist_4.size(); b++) {
                                mDefaultMarkerlist_4.get(b).setAlpha(1.0f);
                            }

                        } else {
                            for (int b = 0; b < mDefaultMarkerlist_4.size(); b++) {
                                mDefaultMarkerlist_4.get(b).setAlpha(0.0f);
                            }
                        }
                        break;
                }
            }
        };

        chk1.setOnClickListener(Listener);
        chk2.setOnClickListener(Listener);
        chk3.setOnClickListener(Listener);
        chk4.setOnClickListener(Listener);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                break;

            case R.id.item2:
                break;

            case R.id.item3:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void initLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
