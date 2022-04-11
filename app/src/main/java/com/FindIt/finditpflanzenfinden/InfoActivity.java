package com.FindIt.finditpflanzenfinden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Base64InputStream;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.FindIt.finditpflanzenfinden.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class InfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView plantPicture;
    private ImageButton editImageBtn;
    private TextView objectName;
    private Spinner categorySpinner;
    private TextView harvestDateRangeLabel;
    private TextView locationModeLabel;
    private TextView noticeLabel;
    private ImageButton harvestDateRangeBtn;
    private ImageButton locationModeBtn;
    private String[] locationModeSelector = {"aktueller Standort", "benutzerdefinierter Standort"};
    private TextView describtionLabel;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private double longitude;
    private double latitude;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    private Button confirmBtn;

    private ImageButton editBtn;
    private ImageButton deleteBtn;
    private String[] imageSelector = {"Kamera", "Gallerie"};
    private static final int pic_id = 123;
    private int SELECT_PICTURE = 200;

    private ListViewAdapter adapter;
    private ListView listView;
    private HashMap<String, ArrayList<Integer>> searchMap;
    private Pair<Date, Date> rangeDate;


    private PlantItem obj;
    private String gImage;
    private String gName;

    private GoogleMap mMap;
    private static final float DEFAULT_LONGITUDE = 11;
    private static final float DEFAULT_LATITUDE = 46;
    private static final float DEFAULT_ZOOM = 70;
    private ImageButton confirmLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         String plantItem= (String) getIntent().getSerializableExtra("Info");
        searchMap= (HashMap<String, ArrayList<Integer>>) getIntent().getSerializableExtra("ADA");
        gImage= (String) getIntent().getSerializableExtra("URL");
        gName= (String) getIntent().getSerializableExtra("NAME");
        adapter = new ListViewAdapter(InfoActivity.this, searchMap);

        obj= (PlantItem) stringToObject(plantItem);

        plantPicture = findViewById(R.id.imagePlant);
        byte[] decodedString = Base64.decode(obj.getImgBase64String(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        plantPicture.setImageBitmap(decodedByte);

        editImageBtn= (ImageButton) findViewById(R.id.addPicture_btn);
        editImageBtn.setClickable(false);
        editImageBtn.setEnabled(false);
        editImageBtn.setVisibility(View.INVISIBLE);

        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.GONE);
                categorySpinner.setEnabled(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                builder.setTitle("Bild einfügen mit");
                builder.setItems(imageSelector, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            imageCamera();
                        } else {
                            imageChooser();
                        }
                    }
                });
                builder.show();
            }
        });

        objectName = findViewById(R.id.inputObjectName);
        categorySpinner=findViewById(R.id.categorySpinner);
        noticeLabel=findViewById(R.id.inputNoticeinfo);
        harvestDateRangeLabel = findViewById(R.id.harvestDateRangeLabel);
        objectName.setFocusable(false);
        objectName.setClickable(false);
        objectName.setFocusableInTouchMode(false);
        editBtn = findViewById(R.id.editBtn);
        describtionLabel=findViewById(R.id.describtionfield);

        listView=(ListView) findViewById(R.id.listview);
        listView.setVisibility(View.GONE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                objectName.setText(adapter.getItem(i));
                listView.setVisibility(View.GONE);
                categorySpinner.setEnabled(true);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objectName.setFocusable(true);
                objectName.setClickable(true);
                objectName.setFocusableInTouchMode(true);

                noticeLabel.setFocusable(true);
                noticeLabel.setClickable(true);
                noticeLabel.setFocusableInTouchMode(true);

                describtionLabel.setFocusable(true);
                describtionLabel.setClickable(true);
                describtionLabel.setFocusableInTouchMode(true);

                harvestDateRangeLabel.setFocusable(true);
                harvestDateRangeLabel.setClickable(true);
                harvestDateRangeLabel.setFocusableInTouchMode(true);

                harvestDateRangeBtn.setClickable(true);
                harvestDateRangeBtn.setEnabled(true);
                harvestDateRangeBtn.setVisibility(View.VISIBLE);
                harvestDateRangeBtn.bringToFront();
                locationModeBtn.setClickable(true);
                locationModeBtn.setEnabled(true);
                locationModeBtn.setVisibility(View.VISIBLE);
                locationModeBtn.bringToFront();
                editImageBtn.setClickable(true);
                editImageBtn.setEnabled(true);
                editImageBtn.setVisibility(View.VISIBLE);
                editImageBtn.bringToFront();

                categorySpinner.setEnabled(true);
                categorySpinner.setClickable(true);

                confirmBtn.setClickable(true);
                confirmBtn.setEnabled(true);
                confirmBtn.setVisibility(View.VISIBLE);
                confirmBtn.bringToFront();

                objectName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b){
                            listView.setVisibility(View.VISIBLE);
                            categorySpinner.setEnabled(false);
                        }else {
                            listView.setVisibility(View.GONE);
                            categorySpinner.setEnabled(true);
                        }
                    }
                });

                objectName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        listView.setVisibility(View.VISIBLE);
                        categorySpinner.setEnabled(false);
                        String text = charSequence.toString();
                        adapter.filter(text);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                Toast.makeText(InfoActivity.this, "Bearbeitung aktiviert", Toast.LENGTH_SHORT).show();
            }
        });
        objectName.setText(obj.getName());

        for (int i=0;i<categorySpinner.getCount();i++){
            if (categorySpinner.getItemAtPosition(i).toString().equalsIgnoreCase(obj.getCategory())){
                categorySpinner.setSelection(i);
            }
        }
        categorySpinner.setEnabled(false);
        categorySpinner.setClickable(false);

        harvestDateRangeLabel.setFocusable(false);
        harvestDateRangeLabel.setClickable(false);
        harvestDateRangeLabel.setFocusableInTouchMode(false);

        SimpleDateFormat simpleFormat = new SimpleDateFormat("MMM");
        harvestDateRangeLabel.setText("Erntezeit: " + simpleFormat.format(obj.getDateRangeFirst())+" bis "+simpleFormat.format(obj.getGetDateRangeSecond()));
        locationModeLabel = findViewById(R.id.loacationModeLabel);
        locationModeLabel.setText("Aktueller Standort: "+ obj.getLongitude() +" "+ obj.getLatitude());
        noticeLabel.setFocusable(false);
        noticeLabel.setClickable(false);
        noticeLabel.setFocusableInTouchMode(false);
        noticeLabel.setText(obj.getNotice());

        describtionLabel.setFocusable(false);
        describtionLabel.setClickable(false);
        describtionLabel.setFocusableInTouchMode(false);
        describtionLabel.setText(obj.getDescribtion());

        harvestDateRangeBtn = findViewById(R.id.harvestDateRange_btn);
        harvestDateRangeBtn.setClickable(false);
        harvestDateRangeBtn.setEnabled(false);
        harvestDateRangeBtn.setVisibility(View.INVISIBLE);
        rangeDate=new Pair<Date, Date>(obj.getDateRangeFirst(), obj.getGetDateRangeSecond());

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Wähle die Erntezeit aus");
        final MaterialDatePicker materialDatePicker = builder.build();

        harvestDateRangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.GONE);
                categorySpinner.setEnabled(true);
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Pair selectedDates = (Pair) materialDatePicker.getSelection();
//              then obtain the startDate & endDate from the range
                rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
                SimpleDateFormat simpleFormat = new SimpleDateFormat("MMM");
                harvestDateRangeLabel.setText("Erntezeit: " + simpleFormat.format(rangeDate.first)+" bis "+simpleFormat.format(rangeDate.second));
            }
        });

        locationModeBtn = findViewById(R.id.locationMode_btn);
        locationModeBtn.setClickable(false);
        locationModeBtn.setEnabled(false);
        locationModeBtn.setVisibility(View.INVISIBLE);
        latitude=obj.getLatitude();
        longitude=obj.getLongitude();

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        locationModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.GONE);
                categorySpinner.setEnabled(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                builder.setTitle("Standort Modus festlegen..");
                builder.setItems(locationModeSelector, new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            locationTrack = new LocationTrack(InfoActivity.this);
                            if (locationTrack.canGetLocation()) {


                                Handler handler = new Handler(Looper.getMainLooper());
                                locationModeLabel.setText("Standort wird berechnet..");
                                locationModeBtn.setClickable(false);
                                confirmBtn.setClickable(false);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (true) {
                                            locationTrack = getLocationTrack();
                                            longitude = locationTrack.getLongitude();
                                            latitude = locationTrack.getLatitude();
                                            if (longitude!=0.0 || latitude!=0.0){
                                                break;
                                            }else {
                                                Toast.makeText(InfoActivity.this, "Standort wird berechnet..", Toast.LENGTH_SHORT).show();
                                                SystemClock.sleep(1000);
                                            }
                                        }
                                        locationModeLabel.setText("Aktueller Standort gesetzt: "+ longitude +" "+ latitude);
                                        locationModeBtn.setClickable(true);
                                        confirmBtn.setClickable(true);
                                    }
                                }, 0 );
                            } else {

                                locationTrack.showSettingsAlert();
                            }
                        } else {
                            mapAlertBox();
                        }
                    }
                });
                builder.show();
            }
        });

        confirmBtn=findViewById(R.id.confirm_btn);
        confirmBtn.setClickable(false);
        confirmBtn.setEnabled(false);
        confirmBtn.setVisibility(View.INVISIBLE);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlantItem plantItem1=new PlantItem(plantPicture.getDrawable(), objectName.getText().toString(),categorySpinner.getSelectedItem().toString(),rangeDate, latitude, longitude,noticeLabel.getText().toString(), describtionLabel.getText().toString(), gName, gImage);
                plantItem1.setId(obj.getId());
                openMapsActivity(plantItem1);
            }
        });

        deleteBtn=(ImageButton) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlert();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (objectName.isFocused()){
            listView.setVisibility(View.GONE);
            categorySpinner.setEnabled(true);
        }else {
            Intent intent=new Intent(this, MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private LocationTrack getLocationTrack(){
        return new LocationTrack(this);
    }

    private Object stringToObject(String str) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(str.getBytes()), 1
                    | 2)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showDeleteAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InfoActivity.this);


        alertDialog.setTitle("Löschen der Markierung");

        alertDialog.setMessage("Möchten sie diese Markierung wirklich löschen?");


        alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                obj.setDeleted(true);
                openMapsActivity(obj);
            }
        });


        alertDialog.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("Diese Berechtigungen sind essentiell für die Anwendung. Bitte erlaube Zugriff.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(InfoActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Abbrechen", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationTrack!=null)
            locationTrack.stopListener();
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Bild auswählen"), SELECT_PICTURE);
    }
    void imageCamera(){
        Intent camera_intent
                = new Intent(MediaStore
                .ACTION_IMAGE_CAPTURE);

        // Start the activity with camera_intent,
        // and request pic id
        startActivityForResult(camera_intent, pic_id);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    try {
                        plantPicture.setImageBitmap( decodeUri(this, selectedImageUri, 80));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (requestCode == pic_id) {

            // BitMap is data structure of image file
            // which stor the image in memory
            Bitmap photo = (Bitmap)data.getExtras()
                    .get("data");

            // Set the image in imageview for display
            plantPicture.setImageBitmap(photo);
        }
    }

    private void openMapsActivity(PlantItem plantItem) {
        Intent intent=new Intent(this, MapsActivity.class);
        intent.putExtra("NewItem", plantItem);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        SharedPreferences sharedPref = getSharedPreferences("Camera",0);
        double longitudeCam = sharedPref.getFloat("longitude", DEFAULT_LONGITUDE);
        double latitudeCam = sharedPref.getFloat("latitude", DEFAULT_LATITUDE);
        float zoom = sharedPref.getFloat("zoom", DEFAULT_ZOOM);
        LatLng startPosition = new LatLng(latitudeCam, longitudeCam);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startPosition)
                .zoom(zoom)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        LatLng newMark = new LatLng(obj.getLatitude(), obj.getLongitude());
        mMap.addMarker(new MarkerOptions().position(newMark).title(obj.getName()));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                latitude=point.latitude;
                longitude=point.longitude;
                confirmLoc.setVisibility(View.VISIBLE);
            }
        });

    }


    public void mapAlertBox(){
        final Dialog view1 = new Dialog(this);
        view1.setContentView(R.layout.alertbox_selectmarker);
        view1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.alertmap);
        mapFragment.getMapAsync(this);

        ImageButton close = (ImageButton) view1.findViewById(R.id.mapWindowcloseGoogleInfo);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1.dismiss();
                longitude=0.0;
                latitude=0.0;
                if (mapFragment != null)
                    getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
            }
        });

        confirmLoc=view1.findViewById(R.id.confirmLoc);
        confirmLoc.setVisibility(View.GONE);

        confirmLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationModeLabel.setText("Aktueller Standort gesetzt: "+ longitude +" "+ latitude);
                if (mapFragment != null)
                    getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
                view1.dismiss();
            }
        });

        view1.show();
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

}