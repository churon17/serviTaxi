package jeancarlosdev.servitaxi.Utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

import jeancarlosdev.servitaxi.R;

public class CustomInfoWindow  implements GoogleMap.InfoWindowAdapter{

    View myView;

    public CustomInfoWindow(Context context) {
        myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_rider_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {

        TextView textPickUpTitle = myView.findViewById(R.id.txtPickUpInfo);

        textPickUpTitle.setText(marker.getTitle());

        TextView textPickUpSnippet = myView.findViewById(R.id.txtPickUpSnippet);

        textPickUpSnippet.setText(marker.getSnippet());

        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
