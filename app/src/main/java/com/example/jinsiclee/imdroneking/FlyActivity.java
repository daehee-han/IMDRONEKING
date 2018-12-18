package com.example.jinsiclee.imdroneking;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class FlyActivity extends AppCompatActivity implements SensorEventListener {
    public static final String Drone_IP = "192.168.10.1";
    public static final int Command_PORT = 8889;
    boolean takeofff = false;


    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    float[] mGravity;
    float[] mGeomagnetic;
    final Handler handler = new Handler();
    final Handler handler2 = new Handler();

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic) && takeofff) {
                final float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                orientation[0] = (float)Math.toDegrees(orientation[0]);
                orientation[1] = (float)Math.toDegrees(orientation[1]);//앞+뒤-
                orientation[2] = (float)Math.toDegrees(orientation[2]);//좌-우+

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SendData move = new SendData("");
                        if(orientation[1] > 0)
                            move.setMsg("forward "+orientation[1]/2);
                        else
                            move.setMsg("back "+(-orientation[1]/2));
                        move.start();
                    }
                }, 2000);

                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SendData move = new SendData("");
                        if(orientation[2] > 0)
                            move.setMsg("right "+orientation[2]/2);
                        else
                            move.setMsg("left "+(-orientation[2]/2));
                        move.start();
                    }
                }, 2000);
            }
        }



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly);
        takeofff = false;
        SendData init = new SendData("command");
        init.start();

        ImageButton upbtn = (ImageButton) findViewById(R.id.upButton);
        ImageButton downbtn = (ImageButton) findViewById(R.id.downButton);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendData takeoff = new SendData("takeoff");
                takeoff.start();
                takeofff = true;
            }
        });
        downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendData land = new SendData("land");
                land.start();
            }
        });


    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    class SendData extends Thread{
        private String msg;
        public void setMsg(String string){
            this.msg = string;
        }
        SendData(String in){
            this.msg=in;
        }
        public void run(){
            try{
                DatagramSocket socket = new DatagramSocket();
                //drone(server) ip
                InetAddress serverAddr = InetAddress.getByName(Drone_IP);

                Log.d("log", msg);

                byte[] buf = (this.msg).getBytes();

                //transform to packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, Command_PORT);

                //send packet
                socket.send(packet);

                byte[] Rbuf = new byte[256];

                DatagramPacket Rpacket = new DatagramPacket(Rbuf, Rbuf.length);

                //wait receive data
                socket.receive(Rpacket);

                String ans = new String(Rpacket.getData(),StandardCharsets.UTF_8);

                Log.d("log", ans);

            }catch (Exception e){
            }
        }
    }



}

