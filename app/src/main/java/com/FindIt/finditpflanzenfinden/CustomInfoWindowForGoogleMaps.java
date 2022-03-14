package com.FindIt.finditpflanzenfinden;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Base64;
import android.util.Base64InputStream;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.FindIt.finditpflanzenfinden.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public final class CustomInfoWindowForGoogleMaps implements GoogleMap.InfoWindowAdapter {

    private final View mView;
    private Context mContext;

    public CustomInfoWindowForGoogleMaps(Context context) {
        this.mContext = context;
        this.mView= LayoutInflater.from(context).inflate(R.layout.marker_info, null);
    }

    private void rendowWindowText(Marker marker, View view){
        TextView name=(TextView) view.findViewById(R.id.infoWindowName);
        name.setText(marker.getTitle());

        PlantItem obj= (PlantItem) stringToObject(marker.getSnippet());

        //PlantItem{imageView=null, imgBase64String='iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAIAAAAC64paAAAAAXNSR0IArs4c6QAAAANzQklUCAgI2+FP4AAAA3hJREFUOI0lkjuLnVUUhte79u27nDOZSSTJRMRRExMsjCIqomIqIVjYWthESKNB9BfY2AiCCnaxSmVhK4hY2cRCUEgVxAiOt8QZM2fOOd9lf3utZTH9+8ADz4vKJWZWK2YGcgAUpKpwXFVVqpqnn3/h1OmHUt045+AcgG7/3s3vv+36FaJPUFMwM5kZETGpEhMRkRIR4Iiobptmfvzicy8+svOYMYj0y+ufo3LJSAAoERMdYWCvqgyDGhEBEDIARERwIcQTp05vbm7gzOYxM+sn6vLIRuxIhY52CiI474OqquQjLwCAUzMA7vU33/79l1tVcG0dZ02KMaW2IedKKQA8kcqopmbGzEbsXDAjMyUifPXNz7OteTWr53Vz//7+1ddeMrNiqqrETsyMwnK5VMlERERmRnBmBrDvhwUWYmYkNNs49vVPd5o2tXNoocvPvvrv3VsieXMeVaMYitgwDKoKsJlic/vChx9/dvL4Vjuf+xja+WzrxAmTqU5VTJg1FIiE6NOPrn/xyQeTFDNT1aJEDFx55ZlFt2qbjaLlu1t/Xrz8xrVrb7d1qmd18sE5570XkeAdgBDcNMl6uXrnylu7t3/A1UtPmiHFerU+nDcbcDxJ6bIsLN68K9dv3AhmzKxKomSpee/d988+urNaHui4wtVLT6UQ2UNzTrEyBhVxKUoWNLUKj3larIfbu7tqrpRiJjVzm5wSvHcR7E3EsQegU/FHKU1gitw1oQq1mz+8PZnkosuhLFdD3/coI7NkUsg4MDlVYmbyfuoywKUf2QWGFckhhN9+vaNj56ExwHLPMFZhLQMjMjNcYsGdv/ZTCOqTacmF+lWveRTg3LnzlGqOcSpj8BxC4EnK0PVMKkbWr4vK9rH5qutSXYGjiCDEYm5YrXLOQzce7u2vll1gblLFRFTFmHM2s6ISUmtm4Hhw796QCxH16xX7xD6ICHtnZpBCRBfOn+Xl4VpV2UebsqoO48ikIkef02nsZZry0BOcq2vvQp5K2zQxeQH7rTZ4hpDqVFwMpV/4EKGjsSMax75XUF23xoXN+dksxhgdnTt7Xov4yMZspqWCwooPZp5nD5wyxWQWipF3gCMgtPPDvb0zOzv7//xBjn/stv2Djz9RSpGS2bnU1P1qTURCMM1hdnw6PNC+L2LK2Pt7F5wWB/+17RwpvnzS/Q/1DRJKg5h6nQAAAABJRU5ErkJggg==', name='ggbb', category='Baum', dateRange='Erntezeit: 13. Jan. – 10. Feb.', latitude=46.789214785209595, longitude=11.650950637700603, notice='vbb'}

        TextView harvestTime=(TextView) view.findViewById(R.id.infoWindowHarvestTime);
        assert obj != null;
        SimpleDateFormat simpleFormat = new SimpleDateFormat("MMMM");
        LocalDate currentdate = LocalDate.now();
        Date now= Date.from(currentdate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (obj.getDateRangeFirst().getMonth()==obj.getGetDateRangeSecond().getMonth()){
            harvestTime.setText("Erntezeit: " + simpleFormat.format(obj.getDateRangeFirst()));
        }else {
            harvestTime.setText("Erntezeit: " + simpleFormat.format(obj.getDateRangeFirst()) + " - " + simpleFormat.format(obj.getGetDateRangeSecond()));
        }

        TextView notice=(TextView) view.findViewById(R.id.infoWindowNotice);
        assert obj != null;
        notice.setText(obj.getNotice());

        LinearLayout bg= (LinearLayout) view.findViewById(R.id.infoWindowHarvestStateColor);
        if (obj.getDateRangeFirst().getMonth()<=now.getMonth() && obj.getGetDateRangeSecond().getMonth()>=now.getMonth() || obj.getGetDateRangeSecond().getMonth()==now.getMonth()) {
            bg.setBackgroundColor(Color.parseColor("#78C37B"));
            harvestTime.setTextColor(Color.parseColor("#78C37B"));
        } else if (obj.getDateRangeFirst().getMonth()>now.getMonth() && obj.getGetDateRangeSecond().getMonth()>now.getMonth()){
            bg.setBackgroundColor(Color.rgb(255, 165, 0));
            harvestTime.setTextColor(Color.rgb(255, 165, 0));
        }else {
            bg.setBackgroundColor(Color.rgb(255, 0, 0));
            harvestTime.setTextColor(Color.rgb(255, 0, 0));
        }

        ImageView image=(ImageView) view.findViewById(R.id.infoWindowImage);
        byte[] decodedString = Base64.decode(obj.getImgBase64String(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        image.setImageBitmap(decodedByte);

        TextView gName =view.findViewById(R.id.infoWindowGoogleName);
        gName.setText(obj.getgName());

        ImageView gImage=view.findViewById(R.id.infoWindowGoogleImage);
        Picasso.with(mContext.getApplicationContext()).load(Uri.parse(obj.getgImageUrl())).resize(150,150).transform(new CropCircleTransformation()).into(gImage);

    }

    public Object stringToObject(String str) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(str.getBytes()), 1
                    | 2)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, mView);
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, mView);
        return mView;
    }
}
