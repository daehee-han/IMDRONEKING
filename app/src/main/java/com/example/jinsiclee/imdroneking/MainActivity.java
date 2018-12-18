package com.example.jinsiclee.imdroneking;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnwifi = (ImageButton) findViewById(R.id.connectmode);
        ImageButton btnfly = (ImageButton) findViewById(R.id.flymode);
        ImageButton btnsns = (ImageButton) findViewById(R.id.SNSupload);
        ImageButton btnvideo = (ImageButton) findViewById(R.id.videomode);

        btnfly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                String name = wifiInfo.getSSID();
                if(!name.contains("TELLO")) {
                    Toast.makeText(getApplicationContext(),"tello와 연결안됨",Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(new Intent(MainActivity.this, FlyActivity.class));
                }

            }
        });

        btnwifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        btnvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }



}
