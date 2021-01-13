package com.fireman.appcontroller;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

// 参考URL：https://mikulogi-tomo.hatenablog.com/entry/2018/01/04/033458
public final class MainActivity extends AppCompatActivity {


    private Button btnScan;
    private ImageButton btnYnavi;
    private ImageButton btnAmazonmusic;
    private ImageButton btnTablog;
    private TextView txtConnectStatus;

    private BTConnecter connecter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnScan = (Button)findViewById(R.id.btnScan);
        btnAmazonmusic = (ImageButton)findViewById(R.id.btnAmazonMusic);
        btnTablog = (ImageButton)findViewById(R.id.btnTablog);
        btnYnavi = (ImageButton)findViewById(R.id.btnYnavi);
        txtConnectStatus = (TextView)findViewById(R.id.txtConnectStatus);

        connecter = new BTConnecter();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connecter.checkConnect()) {
                    connecter.send("送りたい文字列");
                }
            }
        });

        if(connecter.checkConnect()){
            txtConnectStatus.setText("mBTSocket == null !!");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        connecter.finishConnect();
    }


}