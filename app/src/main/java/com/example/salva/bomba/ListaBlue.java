package com.example.salva.bomba;

/**
 * Created by Salva on 10/02/2017.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class ListaBlue extends Activity {
    //widgets
    Button btnPaired;
    ListView devicelist;
    String address;
    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //Calling widgets
        btnPaired = (Button)findViewById(R.id.button);
        devicelist = (ListView)findViewById(R.id.listView);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });

        if(!hayDatos()) {
            introducirD();
        }

    }

    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);

            // Make an intent to start next activity.
            //Intent i = new Intent(ListaBlue.this, MainActivity.class);

            //Change the activity.
            //i.putExtra(EXTRA_ADDRESS, address);
            //startActivity(i);
            findViewById(R.id.button4).setVisibility(View.VISIBLE);

        }
    };

    public void start(View view){
        Intent i = new Intent(ListaBlue.this, MainActivity.class);
        i.putExtra(EXTRA_ADDRESS,address);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void introducirD() {
        BDPreguntas admin = new BDPreguntas(this,"Preguntas",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues respuestasV = new ContentValues();
        ContentValues preguntas =new ContentValues();
        ContentValues respuestasF = new ContentValues();
        //TABLA PREGUNTAS
        preguntas.put("idPreg",1);
        preguntas.put("Pregunta","¿Año de descubrimiento de América?");
        db.insert("Preguntas",null,preguntas);
        preguntas.put("idPreg",2);
        preguntas.put("Pregunta","¿Año que terminó la WWII?");
        db.insert("Preguntas",null,preguntas);
        preguntas.put("idPreg",3);
        preguntas.put("Pregunta","¿3x43+45?");
        db.insert("Preguntas",null,preguntas);
        //TABLA RESPUESTA VERDADERA
        respuestasV.put("idRV",1);
        respuestasV.put("Respuesta","1492");
        db.insert("RespuestasV",null,respuestasV);
        respuestasV.put("idRV",2);
        respuestasV.put("Respuesta","1945");
        db.insert("RespuestasV",null,respuestasV);
        respuestasV.put("idRV",3);
        respuestasV.put("Respuesta","174");
        db.insert("RespuestasV",null,respuestasV);
        //TABLA RESPUESTA FALSA
        respuestasF.put("idRF",1);
        respuestasF.put("Respuesta","1942");
        db.insert("RespuestasF",null,respuestasF);
        respuestasF.put("idRF",1);
        respuestasF.put("Respuesta","1433");
        db.insert("RespuestasF",null,respuestasF);
        respuestasF.put("idRF",2);
        respuestasF.put("Respuesta","1492");
        db.insert("RespuestasF",null,respuestasF);
        respuestasF.put("idRF",2);
        respuestasF.put("Respuesta","1942");
        db.insert("RespuestasF",null,respuestasF);
        respuestasF.put("idRF",3);
        respuestasF.put("Respuesta","171");
        db.insert("RespuestasF",null,respuestasF);
        respuestasF.put("idRF",3);
        respuestasF.put("Respuesta","224");
        db.insert("RespuestasF",null,respuestasF);

    }
    public boolean hayDatos(){
        BDPreguntas admin1 = new BDPreguntas(this,"Preguntas",null,1);
        SQLiteDatabase db = admin1.getReadableDatabase();
        String selectString = "SELECT * FROM PREGUNTAS";
        Cursor cursor = db.rawQuery(selectString,null);
        //admin1.onUpgrade(db,1,2);
        if(cursor.getCount()<=0){
            return false;
        }
        return true;
    }
}
