package com.example.jinsiclee.imdroneking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    public static final String Drone_IP = "192.168.10.1";
    public static final int Command_PORT = 8889;
    public SendData mSendData = null;
    public TextView txtView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnHello = (Button) findViewById(R.id.Hello);
        txtView = (TextView) findViewById(R.id.textView);

        //button click event Listener
        btnHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create SendData class
                mSendData = new SendData();
                //send data
                mSendData.start();
            }
        });
    }

    class SendData extends Thread{
        public void run(){
            try{
                //create UDP socket
                DatagramSocket socket = new DatagramSocket();
                //drone(server) ip
                InetAddress serverAddr = InetAddress.getByName(Drone_IP);

                //make buffer
                byte[] buf = ("command").getBytes();

                //transform to packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, Command_PORT);

                //send packet
                socket.send(packet);

                //wait receive data
                socket.receive(packet);

                //transfer packet to string
                String msg = new String(packet.getData());

                //write to txtView
                txtView.setText(msg);
            }catch (Exception e){

            }
        }
    }

}
