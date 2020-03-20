package app.boleia.passageiro.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import app.boleia.passageiro.R;

/**
 * Created by maike.silva on 27/12/2017.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{

    View myView;

    public CustomInfoWindow(Context context){
        myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_passageiro_info_window,null);
    }

    @Override
    public View getInfoWindow(Marker marker){

        TextView txtPickupTitle = (TextView)myView.findViewById(R.id.txtPickupInfo);
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnipper = (TextView)myView.findViewById(R.id.txtPickupSnippet);
        txtPickupSnipper.setText(marker.getSnippet());



        return myView;
    }

    @Override
    public View getInfoContents(Marker marker){
        return null;
    }
}
