package com.example.fgrafic;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;

public class MyOverlay extends Overlay
{
    private static final String TAG = "Test:";
    private Paint myPaint;
    private boolean gpsstart = false;
    final List<MyOverlay.GpsData> gpsData = new ArrayList<MyOverlay.GpsData>();

    public class GpsData{
        double lat;
        double lon;
        public GpsData(double lat, double lon){
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }
    }

    public void saveInList(double lon,double lat){
        gpsData.add(new MyOverlay.GpsData(lat,lon));
    }

    public MyOverlay(ResourceProxy rp)
    {
        super(rp);
        myPaint = new Paint();
        // Paint-Objekt
        myPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void draw(Canvas canvas, MapView mMapView, boolean b)
    {
        Log.v(TAG, "draw(): ");
        if(gpsstart) {
            for(int i=0; i<gpsData.size();i++){
                Log.v(TAG, " Objekt"+(i+1)+"\n");
                /*Log.v(TAG, "Lat: "+gpsData.get(i).getLat()+"");
                Log.v(TAG, "Long: "+gpsData.get(i).getLon()+"");*/

                myPaint.setColor(Color.BLUE);

                double lat = gpsData.get(i).getLat();
                double lon = gpsData.get(i).getLon();

                Log.v(TAG, "Lat: "+(long)gpsData.get(i).getLat()+"");
                Log.v(TAG, "Long: "+(long)gpsData.get(i).getLon()+"");
                // Umwandlung von Gps- zu Pixelkoordinaten
                Point pixel = mMapView.getProjection().toPixels(new GeoPoint(lat, lon) ,null);

                Log.v(TAG, "Pixel X: "+(int)pixel.x+"");
                Log.v(TAG, "Pixel Y: "+(int)pixel.y+"");
                // Hier die Wegpunkte wie in der vorherigen Aufgabe zeichnen


                canvas.drawPoint(pixel.x,pixel.y,myPaint);

                canvas.drawCircle((int)pixel.x, (int)pixel.y , 15, myPaint);



                if(i>=1){
                    Point lastPoint = mMapView.getProjection().toPixels(new GeoPoint(gpsData.get(i-1).getLat(), gpsData.get(i-1).getLon()) ,null);
                    canvas.drawLine(lastPoint.x,lastPoint.y,pixel.x,pixel.y,myPaint);
                }

            }
        }

    }

    public void setGpsstart(boolean gpsstart) {
        this.gpsstart = gpsstart;
    }

    public void positionClear(){
        gpsData.clear();
    }



}
