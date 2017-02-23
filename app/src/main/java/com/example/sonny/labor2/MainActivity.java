package com.example.sonny.labor2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.fgrafic.MyOverlay;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.security.Provider;

import static android.location.LocationProvider.AVAILABLE;
import static android.location.LocationProvider.OUT_OF_SERVICE;
import static android.location.LocationProvider.TEMPORARILY_UNAVAILABLE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Test:";
    private LocationManager locationManager;
    private LocationListener locationListener;

    private MapView mMapView;
    private MyOverlay myOverlay;
    private IMapController mapController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button stopB = (Button) findViewById(R.id.button2);
        stopB.setEnabled(false);


        mMapView = (MapView) findViewById(R.id.mapview);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mapController = mMapView.getController();
        mapController.setZoom(42);
        GeoPoint startPoint = new GeoPoint(54.776627, 9.448113);
        mapController.setCenter(startPoint);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        myOverlay = new MyOverlay(resourceProxy);
        mMapView.getOverlays().add(myOverlay);

    }

    public double runden(double value, int stellen) {
        return Math.round(Math.pow(10.0, stellen)*value) / Math.pow(10.0, stellen);
    }

    public void aktivGPS(View view) {
        Log.v(TAG, " In aktivGPS ");

        logAusgabe("Anwendung wurde gestartet");

        Button startB = (Button) findViewById(R.id.button);
        startB.setEnabled(false);
        Button stopB = (Button) findViewById(R.id.button2);
        stopB.setEnabled(true);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v(TAG, " Location Change : " + location);

                logAusgabe( "Longitude: " + location.getLongitude());
                logAusgabe( "Latitude: " + location.getLatitude());

                myOverlay.saveInList(runden(location.getLongitude(),4),runden(location.getLatitude(),4));

                mMapView.postInvalidate();
                GeoPoint currentPoint = new GeoPoint(runden(location.getLatitude(),4),runden(location.getLongitude(),4));
                mapController.setCenter(currentPoint);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case OUT_OF_SERVICE: logAusgabe("Status changed to Out of Service.");
                    case TEMPORARILY_UNAVAILABLE: logAusgabe("Status changed to temporary unavailable.");
                    case AVAILABLE: logAusgabe("Status changed to available.");
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.v(TAG, " ProviderEnable: " + provider);
                logAusgabe("GPS aktiv");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.v(TAG, " ProviderDisable: " + provider);
                logAusgabe("GPS inaktiv");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "No Permissions!!");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Log.v(TAG, " requested Locations");

        myOverlay.setGpsstart(true);
    }

    public void deaktivGPS(View view) {
        logAusgabe("Anwendung wurde gestopt");
        Button startB = (Button) findViewById(R.id.button);
        startB.setEnabled(true);
        Button stopB = (Button) findViewById(R.id.button2);
        stopB.setEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
        myOverlay.setGpsstart(false);
    }

    public void logAusgabe(String log){
        TextView tempView = (TextView) findViewById(R.id.log);
        tempView.setText(tempView.getText() + log +"\n");
    }

    public void clearFunction(View view){
        TextView tempView = (TextView) findViewById(R.id.log);
        tempView.setText("Debuginformationen:");
        myOverlay.positionClear();
        mMapView.postInvalidate();
    }
}
