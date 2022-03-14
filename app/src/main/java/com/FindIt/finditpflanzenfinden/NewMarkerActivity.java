package com.FindIt.finditpflanzenfinden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class NewMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageButton addPicture;
    private ImageView plantPicture;

    private TextView objectName;

    private GoogleMap mMap;
    private static final float DEFAULT_LONGITUDE = 11;
    private static final float DEFAULT_LATITUDE = 46;
    private static final float DEFAULT_ZOOM = 70;

    private Spinner categorySpinner;

    int SELECT_PICTURE = 200;

    private String[] imageSelector = {"Kamera", "Gallerie"};
    private static final int pic_id = 123;

    private ImageButton harvestDateRangeBtn;
    private TextView harvestDateRangeLabel;

    private ImageButton locationModeBtn;
    private TextView locationModeLabel;
    private String[] locationModeSelector = {"aktueller Standort", "benutzerdefinierter Standort"};

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private double longitude;
    private double latitude;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    private TextView noticeLabel;
    private TextView describtionLabel;

    private Button confirmBtn;
    private TextView errorTextView;

    private PlantItem plantItem;

    private ListViewAdapter adapter;
    private ListView listView;
    private HashMap<String, ArrayList<Integer>> searchMap;

    private Pair<Date, Date> rangeDate;
    private String gImage;
    private String gName;

    private View alertView=null;
    private ImageButton confirmLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchMap= (HashMap<String, ArrayList<Integer>>) getIntent().getSerializableExtra("ADA");
        gImage= (String) getIntent().getSerializableExtra("URL");
        gName= (String) getIntent().getSerializableExtra("NAME");
        adapter = new ListViewAdapter(NewMarkerActivity.this, searchMap);

        addPicture = findViewById(R.id.addPicture_btn);
        plantPicture = findViewById(R.id.imagePlant);

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewMarkerActivity.this);
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
        objectName=findViewById(R.id.inputObjectName);
        listView=(ListView) findViewById(R.id.listview);
        listView.setVisibility(View.GONE);
        RelativeLayout background=findViewById(R.id.bckgrnd);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectName.isFocused()) {
                    listView.setVisibility(View.GONE);
                    categorySpinner.setEnabled(true);
                }
            }
        });
        listView.setAdapter(adapter);
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

        objectName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    listView.setVisibility(View.GONE);
                    categorySpinner.setEnabled(true);
                    objectName.clearFocus();
                }
                return false;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                objectName.setText(adapter.getItem(i));
                listView.setVisibility(View.GONE);
                categorySpinner.setEnabled(true);
            }
        });

        categorySpinner=findViewById(R.id.categorySpinner);

        harvestDateRangeBtn = findViewById(R.id.harvestDateRange_btn);
        harvestDateRangeLabel = findViewById(R.id.harvestDateRangeLabel);

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Wähle die Erntezeit aus");
        final MaterialDatePicker materialDatePicker = builder.build();

        harvestDateRangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        locationModeLabel = findViewById(R.id.loacationModeLabel);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(NewMarkerActivity.this);
                builder.setTitle("Standort Modus festlegen..");
                builder.setItems(locationModeSelector, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            locationTrack = new LocationTrack(NewMarkerActivity.this);
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
                                                Toast.makeText(NewMarkerActivity.this, "Standort wird berechnet..", Toast.LENGTH_SHORT).show();
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
        noticeLabel=findViewById(R.id.inputNotice);

        describtionLabel=findViewById(R.id.describtionfield);

        confirmBtn=findViewById(R.id.confirm_btn);
        errorTextView=findViewById(R.id.errorLine);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int colorNormal=objectName.getCurrentHintTextColor();
                if (plantPicture.getDrawable()!=null) {
                    if (objectName.getText().toString().trim().length() > 0 || !objectName.getText().toString().equals("")) {
                        objectName.setHintTextColor(colorNormal);
                        if (!categorySpinner.getSelectedItem().toString().equals("Kategorie zuordnen..")) {
                            categorySpinner.setBackgroundColor(Color.TRANSPARENT);
                            if (!harvestDateRangeLabel.getText().equals("Erntezeit")) {
                                harvestDateRangeLabel.setTextColor(colorNormal);
                                if (longitude!=0 || latitude!=0) {
                                    locationModeLabel.setTextColor(colorNormal);
                                    plantItem = new PlantItem(plantPicture.getDrawable(), objectName.getText().toString(), categorySpinner.getSelectedItem().toString(), rangeDate, latitude, longitude, noticeLabel.getText().toString(), describtionLabel.getText().toString(), gName, gImage);
                                    openMapsActivity();
                                }else {
                                    locationModeLabel.setTextColor(getResources().getColor(R.color.design_default_color_error));
                                    errorTextView.setText("Nicht alle Pflichtfelder ausgefüllt! (Standort)");
                                }
                            }else {
                                harvestDateRangeLabel.setTextColor(getResources().getColor(R.color.design_default_color_error));
                                errorTextView.setText("Nicht alle Pflichtfelder ausgefüllt! (Erntezeit)");
                            }
                        }else {
                            errorTextView.setText("Nicht alle Pflichtfelder ausgefüllt! (Kategorie)");
                            categorySpinner.setBackgroundColor(getResources().getColor(R.color.design_default_color_error));
                        }
                    }else {
                        errorTextView.setText("Nicht alle Pflichtfelder ausgefüllt! (Name)");
                        objectName.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_error)));
                    }
                }else {
                    errorTextView.setText("Nicht alle Pflichtfelder ausgefüllt! (Bild)");
                    addPicture.setColorFilter(getResources().getColor(R.color.design_default_color_error),
                            PorterDuff.Mode.SRC_ATOP);
                }
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
        new AlertDialog.Builder(NewMarkerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Abbrechen", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTrack!=null) {
            locationTrack.stopListener();
        }
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
    private void openMapsActivity() {
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

