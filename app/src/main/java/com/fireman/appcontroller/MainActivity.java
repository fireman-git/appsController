package com.fireman.appcontroller;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

// 参考URL：https://mikulogi-tomo.hatenablog.com/entry/2018/01/04/033458
public final class MainActivity extends AppCompatActivity {
    private ImageButton btnYnavi;
    private ImageButton btnAmazonmusic;
    private ImageButton btnTablog;

    private BTServerThread btServerThread;
    private BluetoothAdapter bluetoothAdapter = null;
    private BTServerApplication btServerApplication;

    static final String TAG = "BTTest1S";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAmazonmusic = (ImageButton)findViewById(R.id.btnAmazonMusic);
        btnTablog = (ImageButton)findViewById(R.id.btnTablog);
        btnYnavi = (ImageButton)findViewById(R.id.btnYnavi);
        btServerApplication = new BTServerApplication();

        btnAmazonmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((BTServerApplication) MainActivity.this.getApplication()).setTempValue("1");
                btServerApplication.setTempValue("Amazonmusic");
            }
        });

        btnTablog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((BTServerApplication) MainActivity.this.getApplication()).setTempValue("2");
                btServerApplication.setTempValue("Tablog");
            }
        });

        btnYnavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((BTServerApplication) MainActivity.this.getApplication()).setTempValue("3");
                btServerApplication.setTempValue("Ynavi");
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.d(TAG, "This device doesn't support Bluetooth.");
            }
        }

        btServerThread = new BTServerThread();
        btServerThread.start();
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(btServerThread != null){
            btServerThread.cancel();
            btServerThread = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    public class BTServerThread extends Thread {

        BluetoothServerSocket bluetoothServerSocket;
        BluetoothSocket bluetoothSocket;
        InputStream inputStream;
        OutputStream outputStream;

        public void run() {

            byte[] incomingBuff = new byte[64];

            try {
                while (true) {
                    if(Thread.interrupted()){break;}

                    try {
                        bluetoothServerSocket= bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                                                Constants.BT_NAME,
                                                Constants.BT_UUID);

                        bluetoothSocket = bluetoothServerSocket.accept();
                        bluetoothServerSocket.close();
                        bluetoothServerSocket = null;

                        inputStream = bluetoothSocket.getInputStream();
                        outputStream = bluetoothSocket.getOutputStream();
                        while (true) {
                            if(Thread.interrupted()){break;}

                            int incomingBytes = inputStream.read(incomingBuff);
                            byte[] buff = new byte[incomingBytes];
                            System.arraycopy(incomingBuff, 0, buff, 0, incomingBytes);
                            String cmd = new String(buff, StandardCharsets.UTF_8);

                            String resp = processCommand(cmd);
                            outputStream.write(resp.getBytes());
                            btServerApplication.setTempValue("nothing");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (bluetoothSocket != null) {
                        try {
                            bluetoothSocket.close();
                            bluetoothSocket = null;
                        } catch (IOException e) {}
                    }

                    // Bluetooth connection broke. Start Over in a few seconds.
                    Thread.sleep(3 * 1000);
                }
            }
            catch(InterruptedException e){Log.d(TAG, "Cancelled ServerThread");}
            Log.d(TAG, "ServerThread exit");
        }

        public void cancel() {
            if (bluetoothServerSocket != null) {
                try {
                    bluetoothServerSocket.close();
                    bluetoothServerSocket = null;
                    super.interrupt();
                } catch (IOException e) {}
            }
        }

        protected String processCommand(String cmd){

            Log.d(TAG, "processCommand " + cmd);
            String resp = "OK";

            try {
                if (cmd.equals("GET:TEMP")) {
                    String s = btServerApplication.getTempValue();
                    resp = (s == null) ? "n/a" : s;
                } else {Log.d(TAG, "Unknown Command");}

            } catch (Exception e){Log.d(TAG, "Exception - processCommand " + e.getMessage());}
            return resp;
        }
    }
}