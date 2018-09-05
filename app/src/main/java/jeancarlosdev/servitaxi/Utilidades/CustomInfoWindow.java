package jeancarlosdev.servitaxi.Utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

import jeancarlosdev.servitaxi.R;

/***
 * Clase que nos permitirá crear una Ventana de información personalizada.
 */
public class CustomInfoWindow  implements GoogleMap.InfoWindowAdapter{

    /***
     * Variable de tipo View utilizada para posteriormente obtener la vista que vamos a inflar y presentar.
     */
    View myView;


    /***
     * Método que nos ayuda a asignar el valor correspondiente al atributo myView, para posteriormente hacer uso de ella.
     * @param context
     */
    public CustomInfoWindow(Context context) {
        myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_rider_info_window, null);
    }

    /***
     * Método que nos ayuda a obtener la Ventana de información
     * @param marker nos recibe un marker, para de esta manera obtener el título y el texto para poder adaptarlos al TextView de esa ventana
     * @return una View.
     */
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
