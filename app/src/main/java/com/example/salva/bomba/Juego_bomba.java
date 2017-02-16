package com.example.salva.bomba;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class Juego_bomba extends AppCompatActivity {
    OutputStream out = null;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_bomba);
        Intent newint = getIntent();
        address = newint.getStringExtra("addres");

        new ConnectBT().execute();
    }

    public void win(View view) {
        try {
            out = btSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.write("W".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
           // progress = ProgressDialog.show(Juego_bomba.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Conexion Fallida.");
                finish();
            }
            else
            {
                msg("Conectado.");
                isBtConnected = true;
            }
           // progress.dismiss();
        }
    }}
