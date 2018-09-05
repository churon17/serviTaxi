package jeancarlosdev.servitaxi.Utilidades;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/***
 * Clase utilizada para transformar una imagen cuadrada en circular.
 * Esta clase nos servira si el usuario se Loguea con facebook, podemos extraer la imagen de su perfil de facebook y de esta manera poderla mostrar en serviTaxi de forma más estética.
 */
public class  TransformarImagen implements Transformation {
    /***
     * Sobreescribimos el método transform para adaptarlo a nuestras necesidades.
     * @param source nos recibe un objeto de tipo Bitmap que va a ser nuestra imagen para transformar
     * @return retorna nuestra imagen pero ya circular.
     */

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}

