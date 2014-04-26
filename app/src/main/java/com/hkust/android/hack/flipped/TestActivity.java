package com.hkust.android.hack.flipped;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hkust.android.hack.flipped.core.Fingerprint;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends ActionBarActivity {

    private RelativeLayout main;
    private ImageView marker;
    private ImageView imageView;
    private int imageIndex = 1;
    private TextView tv;
    String imgFilePath;
    String filename;
    WifiManager.WifiLock wifiLock;

    private Button detectButton;
//    private Button startButton;
//    private Spinner category;
//    private Spinner map;
//    private Canvas canvas;
//    private Paint paint;
    Boolean flag = false;

    private float x, y;
    private float xx, yy;

    private boolean AP_FILTERING = true;

    private static final int MARKER_WIDTH = 40;
    private static final int MARKER_HEIGHT = 40;

    private float scale = 13;// scale is the count of  pixels per meter.

    private static int scanCount = 0;

    private WifiManager wifiManager;

    private static int TARGET_SCAN_TIMES = 3;
    private static final String[] wifi_names = {"sMobileNet","Universities WiFi","Y5ZONE","Alumni","eduroam","PCCW"};
    private List<ArrayList<Integer>> signals = new ArrayList<ArrayList<Integer>>();
    private List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
    private Fingerprint currentfp;

    private Map<String, Integer> macMap = new HashMap<String, Integer>();
    private int count = 0;//ap count

    private static boolean isExit = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tv = (TextView)findViewById(R.id.tv);
        detectButton = (Button)findViewById(R.id.action_detect);


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

                        fingerprints.add(new Fingerprint(date, macList, medianSignals));

//                        releaseLock();
//                        pickButton.setEnabled(true);
//                        startButton.setEnabled(true);
//                        startButton.setText("开始录入");

                        StringBuilder sb = new StringBuilder();
                        for(Fingerprint fingerprint : fingerprints){
                            sb.append(fingerprint.toString()+"\n");
                        }
                        Log.i("scanResult", ""+fingerprints.size());
                        tv.setText(sb.toString());

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

    public void onDetect(View v) {

        TimerTask task = new TimerTask() {
            public void run() {
                flag = true;
                wifiManager.startScan();
                Log.i("scan",""+fingerprints.size());
                if(fingerprints.size() >= 1){
                    data2json();
                    this.cancel();
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 0, 1000*10);   //任务、时延、间隔时间

//        Toast.makeText(getApplicationContext(), "Collecting Wi-Fi data...", Toast.LENGTH_LONG)
//                .show();
    }

    public void data2json(){

        Log.i("data2json", "start");
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        try {
            String s = mapper.writeValueAsString(fingerprints);
//            System.out.println(mapper.toString());
            Log.i("data2json", s);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFocus(String name) {
        for (int i = 0; i < wifi_names.length; i++) {
            if (wifi_names[i].equals(name)) {
                return true;
            }
        }
        return false;
    }



}
