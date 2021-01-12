package com.fireman.appcontroller;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

// 参考URL：https://mikulogi-tomo.hatenablog.com/entry/2018/01/04/033458
public final class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBTAdapter = null; //Bluetooth通信を行うために必要な情報を格納する
    private BluetoothDevice mBTDevice = null; //実際に通信を行うデバイスの情報を格納する
    private BluetoothSocket mBTSocket = null ;//ソケット情報を格納する
    private OutputStream mOutputStream = null; //出力ストリーム

    private Button btnScan;
    private TextView txtConnectStatus;
    private String MacAddress = "30:45:96:39:FF:40"; //接続先MACアドレス
    private String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; //通信規格がSPPであることを示す数字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = (Button)findViewById(R.id.btnScan);
        txtConnectStatus = (TextView)findViewById(R.id.txtConnectStatus);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBTSocket != null) {
                    Send();
                }
            }
        });

        //ソケットを確立する関数
        BTConnect();

        //ソケットが取得出来たら、出力用ストリームを作成する
        if(mBTSocket != null){
            try{
                mOutputStream = mBTSocket.getOutputStream();
            }catch(IOException e){/*ignore*/}
        }else{
            txtConnectStatus.setText("mBTSocket == null !!");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mBTSocket != null){
            try {
                mBTSocket.connect();
            } catch (IOException connectException) {/*ignore*/}
            mBTSocket = null;
        }
    }

    private void BTConnect(){
        //BTアダプタのインスタンスを取得
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();


        //相手先BTデバイスのインスタンスを取得
        mBTDevice = mBTAdapter.getRemoteDevice(MacAddress);
        //ソケットの設定
        try {
            mBTSocket = mBTDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) {
            mBTSocket = null;
        }

        if(mBTSocket != null) {
            //接続開始
            try {
                mBTSocket.connect();
            } catch (IOException connectException) {
                try {
                    mBTSocket.close();
                    mBTSocket = null;
                } catch (IOException closeException) {
                    return;
                }
            }
        }
    }

    private void Send(){
        //文字列を送信する
        byte[] bytes = {};
        String str = "Hello World!";
        bytes = str.getBytes();
        try {
            //ここで送信
            mOutputStream.write(bytes);
        } catch (IOException e) {
            try{
                mBTSocket.close();
            }catch(IOException e1){/*ignore*/}
        }
    }
}