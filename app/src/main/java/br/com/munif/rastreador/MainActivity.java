package br.com.munif.rastreador;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static android.telephony.TelephonyManager.*;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private TextView tvStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        inicia();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void inicia() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                10.0f, new LocationListener() {
                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }

                    public void onLocationChanged(Location location) {
                        double latitute = location.getLatitude();
                        double longitude = location.getLongitude();

                        TextView et = (TextView) findViewById(R.id.tvCoordenadas);
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String deviceId = telephonyManager.getDeviceId();
                        et.setText("" + latitute + " " + longitude);
                        Enviador enviador = new Enviador();
                        enviador.execute(deviceId, "" + latitute, "" + longitude);

                    }

                });

    }


    class Enviador extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String s) {
            tvStatus.setText(tvStatus.getText()+"\n"+s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            tvStatus.setText(tvStatus.getText()+"\n"+values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Inicio");
            try {
                URL url = new URL("http://www.munif.com.br/rastreador/registra?nom="
                        + params[0] + "&lat=" + params[1] + "&lon=" + params[2]);


                // http://www.munif.com.br/rastreador/registra
                publishProgress("Conectando");
                HttpURLConnection urc = (HttpURLConnection) url.openConnection();
                urc.connect();

                publishProgress("Conectado " + urc.getResponseCode());
                urc.disconnect();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                return "Problema " + e.toString();
            }
            return "Sucesso";
        }
    }


}
