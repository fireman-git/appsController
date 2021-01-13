package com.fireman.appcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BTConnecter {

    private String MacAddress = "30:45:96:39:FF:40"; //接続先MACアドレス
    private String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; //通信規格がSPPであることを示す数字

    private BluetoothAdapter mBTAdapter = null; //Bluetooth通信を行うために必要な情報を格納する
    private BluetoothDevice mBTDevice = null; //実際に通信を行うデバイスの情報を格納する
    private BluetoothSocket mBTSocket = null ;//ソケット情報を格納する
    private OutputStream mOutputStream = null; //出力ストリーム


    public BTConnecter(){
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

        //ソケットが取得出来たら、出力用ストリームを作成する
        if(mBTSocket != null){
            try{
                mOutputStream = mBTSocket.getOutputStream();
            }catch(IOException e){/*ignore*/}
        }
    }

    public void send(String str){
        //文字列を送信する
        byte[] bytes = {};
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

    public void finishConnect(){
        if(mBTSocket != null){
            try {
                mBTSocket.connect();
            } catch (IOException connectException) {/*ignore*/}
            mBTSocket = null;
        }
    }

    public boolean checkConnect(){
        if(mBTSocket != null)
            return true;
        else
            return false;
    }

}
