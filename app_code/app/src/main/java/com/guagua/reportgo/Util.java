package com.guagua.reportgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shangrenyang on 15/12/16.
 */
public class Util extends ActivityCompat {


    public static float dp2px(int dip, Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        return dip * scale + 0.5f;
    }

    public static String getStringImage(Uri uri, Context context){
        Bitmap imageBmp = null;
        try {
             imageBmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public static String getTokenString(String... tokenGroup){
        String result = "";
        switch (tokenGroup.length){
            case 1:
                result = "?token="+tokenGroup[0];
                break;
            case 2:
                result = "?token="+tokenGroup[0]+"&token_id="+tokenGroup[1];
                break;
            case 3:
                result = "?token="+tokenGroup[0]+"&token_id="+tokenGroup[1]+"&token_id_type="+tokenGroup[2];
                break;
        }
        return result;
    }
    public static Bitmap ResizeImageUri(Context c, Uri uri)
            throws FileNotFoundException {
        int requiredSize = 500;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scales = 1;

        if(width_tmp>requiredSize||height_tmp>requiredSize) {
            float a = ((float) requiredSize / (float) width_tmp);
            float b = ((float) requiredSize / (float) height_tmp);
            scales = (int) (1 / Math.sqrt(a * a + b * b));
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scales;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }
    public static Bitmap ResizeImageUrl(Context c , String url , final float resizePercent  ){
        Bitmap bitmap = null;
        try {
            int resize = (int)(1/resizePercent);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = resize; // 1 = 100% if you write 4 means 1/4 = 25%
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent(),
                    null, bmOptions);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage ) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100 , bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "iMotorImage", null);
        return Uri.parse(path);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    public static Bitmap getBitmapFromView(View view){
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b = view.getDrawingCache();
        view.destroyDrawingCache();
        return b;
    }

    public static boolean CompareDate(String dateA , String timeA , String dateB , String timeB){
        try {
            String[] date = dateA.split("-");
            String[] time = timeA.split(":");
            Date today = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
            Calendar a = Calendar.getInstance();
            a.set(Calendar.YEAR, Integer.parseInt(date[0]));
            a.set(Calendar.MONTH, Integer.parseInt(date[1]));
            a.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[2]));
            a.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            a.set(Calendar.MINUTE, Integer.parseInt(time[1]));

            date = dateB.split("-");
            time = timeB.split(":");

            Calendar b = Calendar.getInstance();
            b.set(Calendar.YEAR, Integer.parseInt(date[0]));
            b.set(Calendar.MONTH, Integer.parseInt(date[1]));
            b.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[2]));
            b.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            b.set(Calendar.MINUTE, Integer.parseInt(time[1]));

            if(a.getTime().after(b.getTime()))
                return true;
            else
                return false;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

}
