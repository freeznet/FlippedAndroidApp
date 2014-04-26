package com.hkust.android.hack.flipped.Location;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.hkust.android.hack.flipped.core.Fingerprint;
import com.hkust.android.hack.flipped.util.Ln;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by joshua on 14-4-26.
 */

public class LocationService extends Service{

    IBinder mBinder = new LocationBinder();
    String TAG = "Flipped";
    Context mContext;
    private WifiManager wifiManager;
    boolean flag = false;
    private static int scanCount = 0;
    private boolean AP_FILTERING = true;
    public String provider;
    private Location mCurrentLocation;
    private double currentLatitude;
    private double currentLongitude;
    private double currentRadius;
    private Date gpsUpdateTime = null;


    private LocationManager mLocationManager;
    private static final long MIN_TIME_BW_UPDATES	     = 1000 * 60 * 5; // 1 min
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 50;	   // 10 meters

    private String mcurrentLocationName = "unknown";

    private static int TARGET_SCAN_TIMES = 3;
    private static final String[] wifi_names = {"sMobileNet","Universities WiFi","Y5ZONE","Alumni","eduroam","PCCW"};
    private List<ArrayList<Integer>> signals = new ArrayList<ArrayList<Integer>>();
    private List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
    private Fingerprint currentfp = null;

    private Map<String, Integer> macMap = new HashMap<String, Integer>();
    private int count = 0;//ap count

    private static boolean isExit = false;



    private LocationListener onLocationChange = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Ln.d(TAG + "Location changed "+(location == null));
            mCurrentLocation = location;
            if( mCurrentLocation != null ){
                Ln.d(TAG + "GPSLocation:" + mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude() + ", " + mCurrentLocation.getAccuracy());
                currentLatitude = mCurrentLocation.getLatitude();
                currentLongitude = mCurrentLocation.getLongitude();
                currentRadius = mCurrentLocation.getAccuracy();
                gpsUpdateTime = Calendar.getInstance().getTime();
                String pos[] = {Double.toString(currentLatitude), Double.toString(currentLongitude)};
                new GeocoderTask().execute(pos);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }

    };

        @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public Fingerprint getCurrentfp() {
        return currentfp;
    }

    public void setCurrentfp(Fingerprint currentfp) {
        this.currentfp = currentfp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate() executed");
        mContext = this;
        prepareWifiData();
        prepareGlobalData();
        startScan();
    }

    @Override
    public void onDestroy() {
//        eventBus.unregister(this);
//        mLocationClient.disconnect();
//        unregisterReceiver();
        super.onDestroy();
        System.out.println("ondestroy-service");
        Ln.d(TAG + "onDestroy() executed");
    }


    private void startScan() {
        TimerTask task = new TimerTask() {
            public void run() {
                flag = true;
                wifiManager.startScan();
                Log.i("scan",""+fingerprints.size());

                if(fingerprints.size() >= 3){
                    fingerprints.clear();
                    uploadWifi();
//                  this.cancel();
                }

            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 1000*10);   //任务、时延、间隔时间

    }

    public void uploadWifi(){
        Log.i("data2json", "start");
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        try {
            String json = mapper.writeValueAsString(fingerprints);
            Log.i("data2json", json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareWifiData() {
        wifiManager = (WifiManager) getSystemService("wifi");

        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (flag) {
                    List<ScanResult> results = wifiManager.getScanResults();
                    for (ScanResult result : results) {
                        if (AP_FILTERING && !isFocus(result.SSID)) {
                            continue;
                        }

                        if (macMap.containsKey(result.BSSID)) {
                            Integer index = (Integer) macMap.get(result.BSSID);
                            signals.get(index).add(result.level);
                        } else {
                            macMap.put(result.BSSID, count);
                            signals.add(new ArrayList<Integer>());
                            signals.get(count).add(result.level);
                            count++;
                        }
                    }
                    scanCount++;
                    if (scanCount < TARGET_SCAN_TIMES) {
                        System.out.println("size scanCount:" + scanCount);
//                        startButton.setText("倒计时 " + (10 - scanCount));
                        wifiManager.startScan();
                    } else {
                        Date now = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
                        String date = dateFormat.format(now);
                        double medianSignals[] = new double[macMap.size()];
                        String macList[] = new String[macMap.size()];
                        Set<String> macs = macMap.keySet();

                        for (String mac : macs) {
                            int index = macMap.get(mac);
                            macList[index] = mac;
                            List<Integer> tmp = signals.get(index);
                            Collections.sort(tmp);
                            if (tmp.size() > 0) {
                                if (tmp.size() % 2 == 1) {
                                    medianSignals[index] = tmp.get(tmp.size() / 2);
                                } else {
                                    medianSignals[index] = (tmp.get(tmp.size() / 2) + tmp
                                            .get((tmp.size() - 1) / 2)) / 2.0;
                                }
                            }
                            tmp.clear();
                        }
                        Fingerprint fp = new Fingerprint(date, macList, medianSignals);
                        fingerprints.add(fp);
                        currentfp = fp;

//                        releaseLock();
//                        pickButton.setEnabled(true);
//                        startButton.setEnabled(true);
//                        startButton.setText("开始录入");

                        StringBuilder sb = new StringBuilder();
                        for(Fingerprint fingerprint : fingerprints){
                            sb.append(fingerprint.toString()+"\n");
                        }
                        Log.i("scanResult", "" + fingerprints.size());

                        scanCount = 0;
                        count = 0;
                        signals.clear();
                        macMap.clear();

                        flag = false;
                    }
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }


    private void prepareGlobalData(){
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = mLocationManager.getBestProvider(criteria, true);
        Ln.d(TAG + "LocationManager: "+(mLocationManager==null) + mLocationManager.isProviderEnabled(provider) + ", provider: " +provider);

        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChange.onLocationChanged(location);
            mLocationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, onLocationChange);
        } else {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if( location != null ){
                onLocationChange.onLocationChanged(location);
                mLocationManager.requestLocationUpdates(location.getProvider(), MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, onLocationChange);
            } else {
                mLocationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, onLocationChange);
            }
        }
    }


    public class LocationBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }

    private boolean isFocus(String name) {
        for (int i = 0; i < wifi_names.length; i++) {
            if (wifi_names[i].equals(name)) {
                return true;
            }
        }
        return false;
    }


    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            String cityName = null;
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> addresses = null;
            try {

                cityName = fetchCityNameUsingGoogleMap(locationName);

            } catch (Exception e) {            }
            if (cityName != null) // i.e., Geocoder succeed
            {
                return cityName;
            }
            else // i.e., Geocoder failed
            {
                return fetchCityNameUsingGoogleMap(locationName);
            }
        }

        private String fetchCityNameUsingGoogleMap(String[] locationName)
        {
            String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + locationName[0] + ","
                    + locationName[1] + "&sensor=false&language=cn";
            AndroidHttpClient ANDROID_HTTP_CLIENT = AndroidHttpClient.newInstance(LocationService.class.getName());
            try
            {
                JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
                        new BasicResponseHandler()));

                // many nested loops.. not great -> use expression instead
                // loop among all results
                JSONArray results = (JSONArray) googleMapResponse.get("results");
                for (int i = 0; i < results.length(); i++)
                {
                    // loop among all addresses within this result
                    JSONObject result = results.getJSONObject(i);
                    if (result.has("address_components"))
                    {
                        String cityName = result.getString("formatted_address");
                        if( cityName != null && cityName.length() > 0){
                            return cityName;
                        }
                    }
                }
            }
            catch (Exception ignored)
            {
                ignored.printStackTrace();
            }
            finally {
                ANDROID_HTTP_CLIENT.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String locationName) {
            // Adding Markers on Google Map for each matching address
            if( locationName == null ){
                mcurrentLocationName = "unknown(service error)";
            } else {
                mcurrentLocationName = locationName;
            }
            Ln.d(TAG + "GPSLocation : "+ mcurrentLocationName);

//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    eventBus.post(produceIsInCampusEvent());
//                    eventBus.post(produceLocationUpdateEvent());
//                }
//            });
            return;
        }
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public String getMcurrentLocationName() {
        return mcurrentLocationName;
    }

    public void setMcurrentLocationName(String mcurrentLocationName) {
        this.mcurrentLocationName = mcurrentLocationName;
    }


}
