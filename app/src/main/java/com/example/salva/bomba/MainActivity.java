package com.example.salva.bomba;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    OutputStream out = null;
    ArrayList<Integer> numP;
    ArrayList<Button> btnR;
    ArrayList<TextView> textA;
    int fallos=0;
    int acertadas=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newint = getIntent();
        address = newint.getStringExtra(ListaBlue.EXTRA_ADDRESS);
        setContentView(R.layout.activity_juego_bomba);

        numP = new ArrayList<>();
        btnR = new ArrayList<>();
        textA = new ArrayList<>();

        new ConnectBT().execute();


    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Conectando...", "Espere por favor");
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
                e.printStackTrace();
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
                try {
                    out = btSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.write("E".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inicializarjuego();
            }
            progress.dismiss();

        }
    }

    public void inicializarjuego(){
        btnR.add((Button)findViewById(R.id.br11));
        btnR.add((Button)findViewById(R.id.br12));
        btnR.add((Button)findViewById(R.id.br13));
        btnR.add((Button)findViewById(R.id.br21));
        btnR.add((Button)findViewById(R.id.br22));
        btnR.add((Button)findViewById(R.id.br23));
        btnR.add((Button)findViewById(R.id.br31));
        btnR.add((Button)findViewById(R.id.br32));
        btnR.add((Button)findViewById(R.id.br33));
        textA.add((TextView)findViewById(R.id.pre1));
        textA.add((TextView)findViewById(R.id.pre2));
        textA.add((TextView)findViewById(R.id.pre3));

        BDPreguntas admin1 = new BDPreguntas(this,"Preguntas",null,1);
        SQLiteDatabase db = admin1.getReadableDatabase();
        String pre1  = "SELECT * FROM Preguntas";
        Cursor c1 = db.rawQuery(pre1,null);
        Cursor c2;
        Cursor c3;
        int j=1;
        int boton=1;
        int multi = c1.getCount();
        while(j<=textA.size()) {
            int x = (int) (Math.random() * multi) + 1;
            if(!(numP.contains(x))) {
                int num1=boton;
                int num2=boton+((btnR.size()/textA.size())-1);
                String spre1 = "SELECT * FROM Preguntas WHERE idPreg=" + x;
                c1 = db.rawQuery(spre1, null);
                c1.moveToFirst();
                textA.get(j - 1).setText(c1.getString(1));
                numP.add(x);
                c2 = db.rawQuery("SELECT * FROM RespuestasV WHERE idRV=" + x, null);
                int y = (int) (Math.random()*(num1-(num2+1))+(num2));
                c2.moveToFirst();
                btnR.get(y).setText(c2.getString(1));
                final int aux2 = y;
                btnR.get(y).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        btnR.get(aux2).setEnabled(false);
                        acertadas++;
                        if(acertadas>=3){
                            try {
                                out.write("W".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            TextView tx = (TextView) findViewById(R.id.msg);
                            tx.setText("Has ganado!");
                            for (Button button:btnR) {
                                button.setEnabled(false);
                            }
                        }
                    }
                });
                c3 = db.rawQuery("SELECT * FROM RespuestasF WHERE idRF=" + x, null);
                int z=boton;
                int aux = boton;
                while(z<=(aux+((btnR.size()/textA.size())-1))){
                    if(z!=(y+1)){
                        c3.moveToNext();
                        btnR.get(z-1).setText(c3.getString(1));
                        final int aux3= z-1;
                        btnR.get(z-1).setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                btnR.get(aux3).setEnabled(false);
                                try {
                                    out.write("F".getBytes());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                fallos++;
                                if(fallos>=3){
                                    TextView tx = (TextView) findViewById(R.id.msg);
                                    tx.setText("Has perdido!");
                                    for (Button button:btnR) {
                                        button.setEnabled(false);
                                    }
                                }
                            }
                        });
                        boton++;
                    }
                    z++;
                }
                j++;
                boton++;
            }
        }
    }
    public void finalizar(View view){
        //int id = android.os.Process.myPid();
        //android.os.Process.killProcess(id);
        finish();
        System.exit(0);


    }
    public void otravez(View view){
        fallos=0;
        acertadas=0;
        for (Button button:btnR) {
            button.setEnabled(true);
        }
        try {
            out.write("E".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView tx = (TextView) findViewById(R.id.msg);
        tx.setText(" ");

    }

    public void onBackPressed(){
        System.exit(0);
    }

}
