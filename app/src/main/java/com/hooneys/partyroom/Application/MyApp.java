package com.hooneys.partyroom.Application;

import android.Manifest;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MyApp extends Application {
    public static String[] permissions = {//import android.Manifest;
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static BitmapDescriptor appoint_marker;

    public static BitmapDescriptor getAppointMarker(Drawable drawable){
        if(appoint_marker == null){
            Canvas canvas = new Canvas();
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()*2, drawable.getIntrinsicHeight()*2, Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth()*2, drawable.getIntrinsicHeight()*2);
            drawable.draw(canvas);
            appoint_marker = BitmapDescriptorFactory.fromBitmap(bitmap);
        }
        return appoint_marker;
    }



}
