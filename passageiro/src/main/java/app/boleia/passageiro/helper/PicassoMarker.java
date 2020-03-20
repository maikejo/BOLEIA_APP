package app.boleia.passageiro.helper;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by maike.silva on 07/03/2018.
 */

public class PicassoMarker implements Target {

    Marker mMarker;

    public PicassoMarker(Marker marker) {
        Log.d("test: ", "init marker");

        mMarker = marker;

    }

    @Override
    public int hashCode() {
        return mMarker.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PicassoMarker) {
            Marker marker = ((PicassoMarker) o).mMarker;
            return mMarker.equals(marker);
        } else {
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Log.d("test: ", "bitmap loaded");
        mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.d("test: ", "bitmap fail");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.d("test: ", "bitmap preload");
    }
}
