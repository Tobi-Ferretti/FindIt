package com.FindIt.finditpflanzenfinden;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64InputStream;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.FindIt.finditpflanzenfinden.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.FindIt.finditpflanzenfinden.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.User;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.content.ContentValues.TAG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener{

    private static final String PLANTITEMS = "PlantItems";
    private static final String SEARCHITEMS = "Searchitems";
    private static final float DEFAULT_LONGITUDE = 11.647180643297641F;
    private static final float DEFAULT_LATITUDE = 46.78446323864903F;
    private static final float DEFAULT_ZOOM = 11.653437F;
    private static final int RC_AUTHORIZE_DRIVE = 1;
    private GoogleMap mMap;
    private HorizontalScrollView scrollView;
    private RelativeLayout backgrnd;
    private TextView lastSearchTxt;
    private ActivityMapsBinding binding;
    private Button newMarker_btn;
    private Button switchMapType;
    private PlantItem plantItem;
    private Button moreBtn;
    private HashMap<Integer, PlantItem> markersHashMap;
    private HashMap<String, ArrayList<Integer>> searchMap;

    private ListView list;
    private ListViewAdapter adapter;
    private SearchView editsearch;

    private final static String SHARED_PREFS_FILE="MarkersList";
    private final static String SHARED_PREFS_FILE2="SearchList";

    private Button treeFilterBtn;
    private Button flowerFilterBtn;
    private Button knfilterBtn;
    private Button wfilterbtn;
    private final ArrayList<Button> quickFilterBtns=new ArrayList<>();
    private Button selecteted;
    private boolean doubleBackToExitPressedOnce;
    private String searchedName;

    private ImageButton googleButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;

    private GoogleSignInAccount account;

    Scope ACCESS_DRIVE_SCOPE = new Scope("https://www.googleapis.com/auth/drive");
    Scope ACCESS_DRIVE_SCOPE2 = new Scope(DriveScopes.DRIVE_FILE);
    Scope SCOPE_EMAIL = new Scope(Scopes.EMAIL);
    private Drive googleDriveService;
    private DriveServiceHelper mDriveServiceHelper;

    private AppCompatCheckBox checkBoxSyncDrive;
    private RadioGroup radioGroup;
    private RadioButton radio1;
    private RadioButton radio2;
    private EditText driveLink;
    private Button confirmDriveLinkBtn;
    private ImageButton shareDriveLink;
    private TextView linkText;
    private TextView roleText;

    private File file;
    private String fileId=null;
    private String fileOnDrive;
    private String linkTextString=null;
    private boolean sharedDrive;
    private String imageViewShared;
    private String nameShared;
    private String emailShared;
    private String role="unbekannt";
    private String folderId;

    private TextView iconLetter;
    private User sharedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == markersHashMap) {
            markersHashMap=new HashMap<Integer, PlantItem>();
            searchMap=new HashMap<>();
        }
        // load tasks from preference
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences prefs2 = getSharedPreferences(SHARED_PREFS_FILE2, Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getSharedPreferences("FileID",0);


        try {
            markersHashMap = (HashMap<Integer, PlantItem>) ObjectSerializer.deserialize(prefs.getString(PLANTITEMS, ObjectSerializer.serialize(new HashMap<Integer, PlantItem>())));
            searchMap = (HashMap<String, ArrayList<Integer>>) ObjectSerializer.deserialize(prefs2.getString(SEARCHITEMS, ObjectSerializer.serialize(new HashMap<String, ArrayList<Integer>>())));
            adapter = new ListViewAdapter(MapsActivity.this, searchMap);
            searchedName= (String) getIntent().getSerializableExtra("Search");
            fileId = sharedPref.getString("ID",null);
            sharedDrive=sharedPref.getBoolean("Shared",false);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        list = (ListView) findViewById(R.id.listview);
        list.setVisibility(View.GONE);
        list.setAdapter(adapter);
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        googleButton=(ImageButton) findViewById(R.id.googleAccount);

        silentLogin();
        //checkFileOnDrive();

        plantItem= (PlantItem) getIntent().getSerializableExtra("NewItem");
        if (plantItem!=null){
            if (!plantItem.isDeleted()) {
                if (markersHashMap.containsKey(plantItem.getId())){
                    if (searchMap.containsKey(Objects.requireNonNull(markersHashMap.get(plantItem.getId())).getName())) {
                        Objects.requireNonNull(searchMap.get(Objects.requireNonNull(markersHashMap.get(plantItem.getId())).getName())).remove((Object) plantItem.getId());
                        if (Objects.requireNonNull(searchMap.get((Objects.requireNonNull(markersHashMap.get(plantItem.getId()))).getName())).isEmpty()) {
                            searchMap.remove(Objects.requireNonNull(markersHashMap.get(plantItem.getId())).getName());
                        }
                    }
                }
                markersHashMap.put(plantItem.getId(),plantItem);
                updateFile();
                if (searchMap.containsKey(plantItem.getName())){
                    if (!searchMap.get(plantItem.getName()).contains((Object) plantItem.getId())) {
                        Objects.requireNonNull(searchMap.get(plantItem.getName())).add(plantItem.getId());
                    }
                }else {
                    searchMap.put(plantItem.getName(), new ArrayList<>());
                    Objects.requireNonNull(searchMap.get(plantItem.getName())).add(plantItem.getId());

                }
                adapter = new ListViewAdapter(MapsActivity.this, searchMap);
                list.setAdapter(adapter);
                //plantItem = null;
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        scrollView=(HorizontalScrollView) findViewById(R.id.scrollView);
        backgrnd=(RelativeLayout) findViewById(R.id.background);
        lastSearchTxt=(TextView) findViewById(R.id.letzteSuche);
        backgrnd.setVisibility(View.GONE);
        lastSearchTxt.setVisibility(View.GONE);
        switchMapType=(Button) findViewById(R.id.switchType);
        switchMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap.getMapType()==GoogleMap.MAP_TYPE_HYBRID) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else if (mMap.getMapType()==GoogleMap.MAP_TYPE_SATELLITE){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }else if (mMap.getMapType()==GoogleMap.MAP_TYPE_NORMAL){
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }else if (mMap.getMapType()==GoogleMap.MAP_TYPE_TERRAIN){
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });
        editsearch= (SearchView) findViewById(R.id.idSearchView);
        final String[] item = new String[1];

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item[0] =adapter.getItem(i);
                editsearch.setQuery(item[0], false);
                editsearch.clearFocus();
                filterMap(item[0]);
                list.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                backgrnd.setVisibility(View.GONE);
                lastSearchTxt.setVisibility(View.GONE);
                newMarker_btn.setVisibility(View.VISIBLE);
                switchMapType.setVisibility(View.VISIBLE);
            }
        });
        editsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.setVisibility(View.VISIBLE);
                backgrnd.setVisibility(View.VISIBLE);
                lastSearchTxt.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                newMarker_btn.setVisibility(View.GONE);
                switchMapType.setVisibility(View.GONE);
            }
        });
        editsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                list.setVisibility(View.GONE);
                backgrnd.setVisibility(View.GONE);
                lastSearchTxt.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                newMarker_btn.setVisibility(View.VISIBLE);
                switchMapType.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                list.setVisibility(View.VISIBLE);
                backgrnd.setVisibility(View.VISIBLE);
                lastSearchTxt.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                newMarker_btn.setVisibility(View.GONE);
                switchMapType.setVisibility(View.GONE);
                String text = newText;
                adapter.filter(text);
                return false;
            }
        });

        ImageView clearButton = editsearch.findViewById(androidx.appcompat.R.id.search_close_btn);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editsearch.setQuery("", false);
                editsearch.clearFocus();
                filterMapReset();
                list.setVisibility(View.GONE);
                backgrnd.setVisibility(View.GONE);
                lastSearchTxt.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                newMarker_btn.setVisibility(View.VISIBLE);
                switchMapType.setVisibility(View.VISIBLE);
                SharedPreferences sharedPref = getSharedPreferences("Camera",0);
                double longitude = sharedPref.getFloat("longitude", DEFAULT_LONGITUDE);
                double latitude = sharedPref.getFloat("latitude", DEFAULT_LATITUDE);
                float zoom = sharedPref.getFloat("zoom", DEFAULT_ZOOM);
                LatLng startPosition = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(startPosition)
                        .zoom(zoom)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        newMarker_btn=(Button) findViewById(R.id.newMarker_btn);
        newMarker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewMarkerActivity();
            }
        });

        moreBtn=(Button) findViewById(R.id.morebtn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMoreActivity();
            }
        });

        treeFilterBtn=(Button) findViewById(R.id.bbtn);
        flowerFilterBtn=(Button) findViewById(R.id.blbtn);
        knfilterBtn=(Button) findViewById(R.id.kbtn);
        wfilterbtn=(Button) findViewById(R.id.wbtn);

        quickFilterBtns.addAll(Arrays.asList(treeFilterBtn, flowerFilterBtn, knfilterBtn, wfilterbtn));

        treeFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterMapCategory("Baum", treeFilterBtn);
            }
        });

        flowerFilterBtn=(Button) findViewById(R.id.blbtn);
        flowerFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterMapCategory("Blüte", flowerFilterBtn);

            }
        });

        knfilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterMapCategory("Knospe", knfilterBtn);
            }
        });

        wfilterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterMapCategory("Wurzel", wfilterbtn);
            }
        });


    }

    public void openGoogleInfo(View view){

        final Dialog view1 = new Dialog(this);
        view1.setContentView(R.layout.activity_google_account);
        view1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView=(ImageView) view1.findViewById(R.id.accountImage);

        if (account.getPhotoUrl()!=null) {
            Picasso.with(this).load(account.getPhotoUrl()).resize(150, 150).transform(new CropCircleTransformation()).into(imageView);
        }else {
            if (account.getDisplayName()!=null) {
                imageView.setImageBitmap(getDefaultIcon());
            }
        }

        TextView name=(TextView) view1.findViewById(R.id.accountName);
        name.setText(account.getDisplayName());
        TextView email=(TextView) view1.findViewById(R.id.accountEmail);
        email.setText(account.getEmail());

        ImageView imageViewS=(ImageView) view1.findViewById(R.id.sharedaccountImage);
        Picasso.with(MapsActivity.this).load(imageViewShared).resize(150, 150).transform(new CropCircleTransformation()).into(imageViewS);

        if (sharedDrive && imageViewShared==null && nameShared!=null){
            imageViewS.setImageBitmap(getDefaultIcon(nameShared, emailShared));
        }
        TextView nameS=(TextView) view1.findViewById(R.id.sharedaccountName);
        nameS.setText(nameShared);

        TextView emailS=(TextView) view1.findViewById(R.id.sharedaccountEmail);
        emailS.setText(emailShared);

        ImageButton deleteShared=view1.findViewById(R.id.deleteShared);
        deleteShared.setVisibility(View.GONE);

        deleteShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameS.setVisibility(View.GONE);
                imageViewS.setVisibility(View.GONE);
                emailS.setVisibility(View.GONE);
                roleText.setVisibility(View.GONE);
                deleteShared.setVisibility(View.GONE);
                driveLink.setVisibility(View.VISIBLE);
                confirmDriveLinkBtn.setVisibility(View.VISIBLE);
            }
        });

        checkBoxSyncDrive=view1.findViewById(R.id.driveChecked);
        radioGroup=view1.findViewById(R.id.radioGroup);
        radio1=view1.findViewById(R.id.radio1);
        radio2=view1.findViewById(R.id.radio2);
        driveLink=view1.findViewById(R.id.inputDriveLink);
        linkText=(TextView)view1.findViewById(R.id.linkText);
        linkText.setVisibility(View.GONE);
        if (linkTextString!=null){
            linkText.setText(linkTextString);
        }
        confirmDriveLinkBtn=(Button) view1.findViewById(R.id.confirmDriveLink_Btn);
        shareDriveLink=(ImageButton) view1.findViewById(R.id.shareDriveLink_Btn);
        shareDriveLink.setVisibility(View.GONE);
        driveLink.setVisibility(View.GONE);
        confirmDriveLinkBtn.setVisibility(View.GONE);
        radio1.setEnabled(false);
        radio2.setEnabled(false);
        roleText=view1.findViewById(R.id.sharedaccountRole);
        roleText.setText(role);
        roleText.setVisibility(View.GONE);
        if (fileId!=null) {
            if(!sharedDrive) {
                checkBoxSyncDrive.setChecked(true);
                radio1.setEnabled(true);
                radio1.setChecked(true);
                radio2.setEnabled(true);
                shareDriveLink.setVisibility(View.VISIBLE);
                linkText.setVisibility(View.VISIBLE);
                nameS.setVisibility(View.GONE);
                imageViewS.setVisibility(View.GONE);
                emailS.setVisibility(View.GONE);
                roleText.setVisibility(View.GONE);
            }else{
                checkBoxSyncDrive.setChecked(true);
                radio1.setEnabled(true);
                radio2.setChecked(true);
                radio2.setEnabled(true);
                deleteShared.setVisibility(View.VISIBLE);
                nameS.setVisibility(View.VISIBLE);
                imageViewS.setVisibility(View.VISIBLE);
                emailS.setVisibility(View.VISIBLE);
                roleText.setVisibility(View.VISIBLE);
            }
        }else if(sharedDrive){
            checkForGooglePermissions();
            driveSetUp();
            checkBoxSyncDrive.setChecked(true);
            radio1.setEnabled(true);
            radio2.setChecked(true);
            radio2.setEnabled(true);
            deleteShared.setVisibility(View.VISIBLE);
            roleText.setVisibility(View.VISIBLE);
        }
        checkBoxSyncDrive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBoxSyncDrive.isChecked()) {
                    driveSetUp();
                    radio1.setEnabled(true);
                    radio2.setEnabled(true);
                    checkForGooglePermissions();
                    Toast.makeText(MapsActivity.this, "Zugriff auf Drive erlaubt", Toast.LENGTH_SHORT).show();
                    driveSetUp();
                }else {
                    radio1.setEnabled(false);
                    radio2.setEnabled(false);
                    confirmDriveLinkBtn.setVisibility(View.GONE);
                    driveLink.setVisibility(View.GONE);
                    shareDriveLink.setVisibility(View.GONE);
                    linkText.setVisibility(View.GONE);
                }
            }
        });
        shareDriveLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Drive Link", linkTextString);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MapsActivity.this, "Link in Zwischenablage kopiert", Toast.LENGTH_SHORT).show();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.findViewById(i).equals(radio2)){
                    driveLink.setVisibility(View.VISIBLE);
                    confirmDriveLinkBtn.setVisibility(View.VISIBLE);
                    shareDriveLink.setVisibility(View.GONE);
                    linkText.setVisibility(View.GONE);
                }else {
                    confirmDriveLinkBtn.setVisibility(View.GONE);
                    driveLink.setVisibility(View.GONE);
                    shareDriveLink.setVisibility(View.VISIBLE);
                    linkText.setVisibility(View.VISIBLE);
                    roleText.setVisibility(View.GONE);
                    deleteShared.setVisibility(View.GONE);
                    if (sharedDrive){
                        file = new File(getApplicationContext().getFilesDir(), "FindIt");
                        file.delete();
                        sharedDrive=false;
                    }
                    checkFileOnDrive();
                    Toast.makeText(MapsActivity.this, "Mit Drive verbunden", Toast.LENGTH_SHORT).show();
                    if (linkTextString!=null){
                        linkText.setText(linkTextString);
                    }
                    SharedPreferences settings = getSharedPreferences("FileID", 0);
                    SharedPreferences.Editor editor3 = settings.edit();
                    editor3.putString("ID", (String) fileId);
                    editor3.putBoolean("Shared", false);
                    editor3.commit();
                    saveData();
                    view1.dismiss();
                }
            }
        });
        confirmDriveLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linkTextString= String.valueOf(driveLink.getText());
                //linkTextString="https://drive.google.com/file/d/1bpJt_o-nlZbrtq66R9xwXX0OqBuv1tlG/view?usp=sharing";
                String[] id=linkTextString.split("d/");
                String[] id2=id[1].split("/");
                file = new File(getApplicationContext().getFilesDir(),"FindIt");
                fileId = id2[0];
                SharedPreferences settings = getSharedPreferences("FileID", 0);
                SharedPreferences.Editor editor3 = settings.edit();
                editor3.putString("ID", (String) fileId);
                editor3.putBoolean("Shared", (Boolean)true);
                editor3.commit();
                downloadFile(id2[0], file);
                sharedDrive=true;
                saveData();
                checkFileOnDrive();
                deleteShared.setVisibility(View.VISIBLE);
                nameS.setVisibility(View.VISIBLE);
                imageViewS.setVisibility(View.VISIBLE);
                emailS.setVisibility(View.VISIBLE);
                roleText.setVisibility(View.VISIBLE);
                view1.dismiss();
            }
        });
        ImageButton close=(ImageButton) view1.findViewById(R.id.closeGoogleInfo);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1.dismiss();
            }
        });
        Button logout =(Button) view1.findViewById(R.id.accoutLogOutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Toast.makeText(getApplicationContext(),"Abgemeldet",Toast.LENGTH_SHORT).show();

                                markersHashMap.clear();
                                searchMap.clear();
                                fileId=null;
                                sharedDrive=false;
                                file = new File(getApplicationContext().getFilesDir(),"FindIt");
                                clearFile(file);
                                file.delete();
                                saveData();
                                openMapsActivity();
                            }
                        });
            }
        });
        view1.show();
    }

    private void checkFileOnDrive() {
        try {
            mDriveServiceHelper.listDriveImageFiles().addOnSuccessListener(new OnSuccessListener<List<com.google.api.services.drive.model.File>>() {
                @Override
                public void onSuccess(List<com.google.api.services.drive.model.File> files) {
                    Long fileOnDriveSize = -1L;
                    for (com.google.api.services.drive.model.File f:files){
                        if (f.getName().equals("FindIt")){
                            if (sharedDrive){
                                if (!f.getOwners().get(0).getEmailAddress().equals(account.getEmail())) {
                                    fileOnDrive =f.getId();
                                    fileOnDriveSize = f.getSize();
                                    linkTextString=f.getWebViewLink();
                                    if (fileId==null){
                                        fileId=f.getId();
                                        mDriveServiceHelper.setFileId(fileId);
                                    }
                                }
                            }else {
                                if (f.getOwners().get(0).getEmailAddress().equals(account.getEmail())) {
                                    fileOnDrive = f.getId();
                                    fileOnDriveSize = f.getSize();
                                    linkTextString = f.getWebViewLink();
                                    newMarker_btn.setEnabled(true);
                                }
                            }
                            if (fileOnDrive!=null){
                                if (sharedDrive && fileOnDrive.equals(fileId)) {
                                    if (f.size() == 6) {
                                        role = "Lesen und Schreiben";
                                        newMarker_btn.setEnabled(true);
                                    } else if (f.size() == 5) {
                                        role = "Lesen";
                                        newMarker_btn.setEnabled(false);
                                    }
                                    sharedAccount=f.getOwners().get(0);
                                    imageViewShared = f.getOwners().get(0).getPhotoLink();
                                    nameShared = f.getOwners().get(0).getDisplayName();
                                    emailShared = f.getOwners().get(0).getEmailAddress();
                                    break;
                                }
                            }
                        }
                    }
                        if (fileId != null) {
                            if (fileId.equals(fileOnDrive)) {
                                mDriveServiceHelper.setFileId(fileId);
                                file = new File(getApplicationContext().getFilesDir(),"FindIt");
                                if (file.length()!=fileOnDriveSize && fileOnDriveSize>0 && plantItem==null){
                                    downloadFile(fileId,file);
                                    readFromFile(file);
                                    filterMapReset();
                                }else if (fileOnDriveSize==0){
                                    updateFile();
                                }
                            } else if (fileOnDrive == null) {
                                fileId = null;
                                mDriveServiceHelper.setFileId(fileId);
                                file = new File(getApplicationContext().getFilesDir(),"FindIt");
                                clearFile(file);
                                markersHashMap.clear();
                                searchMap.clear();
                                filterMapReset();
                                saveData();
                                role="Keine Brechtigungen, kontaktiern Sie den Eigentümer";
                                newMarker_btn.setEnabled(false);
                            } else {
                                fileId = fileOnDrive;
                                mDriveServiceHelper.setFileId(fileOnDrive);
                            }
                        } else {
                            if (fileOnDrive != null) {
                                fileId = fileOnDrive;
                                mDriveServiceHelper.setFileId(fileOnDrive);
                            }
                        }
                        SharedPreferences settings = getSharedPreferences("FileID", 0);
                        SharedPreferences.Editor editor3 = settings.edit();
                        editor3.putString("ID", (String) fileId);
                        editor3.commit();
                        createFile(account.getDisplayName());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  checkForGooglePermissions();
                    if (sharedDrive) {
                        file = new File(getApplicationContext().getFilesDir(), "FindIt");
                        clearFile(file);
                        markersHashMap.clear();
                        searchMap.clear();
                        saveData();
                        filterMapReset();
                        role = "Keine Brechtigungen, kontaktiern Sie den Eigentümer";

                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openNewMarkerActivity() {
        saveCamPos();
        Intent intent=new Intent(this, NewMarkerActivity.class);
        intent.putExtra("ADA", searchMap);
        if (account.getPhotoUrl()!=null) {
            intent.putExtra("URL", account.getPhotoUrl().toString());
        }else {
            intent.putExtra("URL", getImageUri(this, getDefaultIcon()).toString());
        }
        intent.putExtra("NAME", account.getGivenName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void openMapsActivity() {
        Intent intent=new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openInfoActivity(String obj) {
        saveCamPos();
        Intent intent=new Intent(this, InfoActivity.class);
        intent.putExtra("Info", obj);
        intent.putExtra("ADA", searchMap);
        if (account.getPhotoUrl()!=null) {
            intent.putExtra("URL", account.getPhotoUrl().toString());
        }else {
            intent.putExtra("URL", getImageUri(this, getDefaultIcon()).toString());
        }
        intent.putExtra("NAME", account.getGivenName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openMoreActivity() {
        saveCamPos();
        Intent intent=new Intent(this, MoreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        HashMap<String, String> hashMap=new HashMap<>();
        for(Map.Entry<String, ArrayList<Integer>> entry : searchMap.entrySet()) {
            if (markersHashMap.get(entry.getValue().get(0))!=null) {
                hashMap.put(entry.getKey(), markersHashMap.get(entry.getValue().get(0)).getCategory());
            }
        }
        intent.putExtra("ADA", hashMap);
        startActivity(intent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setInfoWindowAdapter(new CustomInfoWindowForGoogleMaps(MapsActivity.this));
        SharedPreferences sharedPref = getSharedPreferences("Camera",0);
        double longitude = sharedPref.getFloat("longitude", DEFAULT_LONGITUDE);
        double latitude = sharedPref.getFloat("latitude", DEFAULT_LATITUDE);
        float zoom = sharedPref.getFloat("zoom", DEFAULT_ZOOM);
        LatLng startPosition = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startPosition)
                .zoom(zoom)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Add a marker in Sydney and move the camera
        if (searchedName==null) {
            if (plantItem != null) {
                if (!plantItem.isDeleted()) {
                    LatLng newMark = new LatLng(plantItem.getLatitude(), plantItem.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(newMark).title(plantItem.getName()).icon(bitmapDescriptorfromVector(getApplicationContext(), getMarkerImg(plantItem), plantItem.getName())).snippet(plantItem.objectToString())).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newMark));
                    plantItem=null;
                    setupMap();
                } else {
                    markersHashMap.remove(plantItem.getId());
                    updateFile();
                    if (searchMap.containsKey(plantItem.getName())) {
                        Objects.requireNonNull(searchMap.get(plantItem.getName())).remove((Object) plantItem.getId());
                        if (Objects.requireNonNull(searchMap.get(plantItem.getName())).isEmpty()) {
                            searchMap.remove(plantItem.getName());
                        }
                    }
                    adapter.updateAdapter(searchMap);
                    list.setAdapter(adapter);
                    plantItem = null;
                    setupMap();
                }
                SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                try {
                    editor.putString(PLANTITEMS, ObjectSerializer.serialize(markersHashMap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editor.commit();

                SharedPreferences prefs2 = getSharedPreferences(SHARED_PREFS_FILE2, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = prefs.edit();
                try {
                    editor.putString(SEARCHITEMS, ObjectSerializer.serialize(searchMap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editor.commit();
            } else {
                if (fileId!=null || sharedDrive){
                    checkForGooglePermissions();
                    driveSetUp();
                    checkFileOnDrive();
                }
                setupMap();
            }
        }else {
            editsearch.setQuery(searchedName, false);
            list.setVisibility(View.GONE);
            backgrnd.setVisibility(View.GONE);
            lastSearchTxt.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            newMarker_btn.setVisibility(View.VISIBLE);
            switchMapType.setVisibility(View.VISIBLE);
            filterMap(searchedName);
            searchedName=null;
        }
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                openInfoActivity(marker.getSnippet());
            }
        });
    }

    private void setupMap(){
        new Handler().post(() -> {
            if (!markersHashMap.isEmpty()) {
                for (Map.Entry<Integer, PlantItem> entry : markersHashMap.entrySet()) {
                    int key = entry.getKey();
                    PlantItem plantItem = entry.getValue();
                    if (!plantItem.isDeleted()) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(plantItem.getLatitude(), plantItem.getLongitude())).title(plantItem.getName()).icon(bitmapDescriptorfromVector(getApplicationContext(), getMarkerImg(plantItem), "xy")).snippet(plantItem.objectToString()));
                        //searchMap.clear();
                        if (searchMap.containsKey(plantItem.getName())) {
                            if (!searchMap.get(plantItem.getName()).contains((Object) plantItem.getId())) {
                                Objects.requireNonNull(searchMap.get(plantItem.getName())).add(plantItem.getId());
                            }
                        } else {
                            searchMap.put(plantItem.getName(), new ArrayList<>());
                            Objects.requireNonNull(searchMap.get(plantItem.getName())).add(plantItem.getId());
                        }
                    } else {
                        markersHashMap.remove(plantItem.getId());
                        updateFile();
                        if (searchMap.containsKey(plantItem.getName())) {
                            Objects.requireNonNull(searchMap.get(plantItem.getName())).remove((Object) plantItem.getId());
                            if (Objects.requireNonNull(searchMap.get(plantItem.getName())).isEmpty()) {
                                searchMap.remove(plantItem.getName());
                            }
                        }
                    }
                }
                adapter = new ListViewAdapter(MapsActivity.this, searchMap);
                list.setAdapter(adapter);
            }
        });

    }

    private void updateFile() {
        if (fileId!=null) {
            checkForGooglePermissions();
            driveSetUp();
            //checkFileOnDrive();
            file = new File(getApplicationContext().getFilesDir(),"FindIt");
            writeDataToFile(file);
            mDriveServiceHelper.updateFile(file);
        }
    }

    private void filterMap(String string){
        mMap.clear();

        for (int i: Objects.requireNonNull(searchMap.get(string))){
            LatLng newMark = new LatLng(Objects.requireNonNull(markersHashMap.get(i)).getLatitude(), Objects.requireNonNull(markersHashMap.get(i)).getLongitude());
            mMap.addMarker(new MarkerOptions().position(newMark).title(Objects.requireNonNull(markersHashMap.get(i)).getName()).icon(bitmapDescriptorfromVector(this, getMarkerImg(Objects.requireNonNull(markersHashMap.get(i))), "xy")).snippet(Objects.requireNonNull(markersHashMap.get(i)).objectToString())).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newMark));
        }
    }

    private void filterMapReset(){
        mMap.clear();

        new Handler().post(() -> {
            for(Map.Entry<Integer, PlantItem> entry : markersHashMap.entrySet()) {
                PlantItem plantItem = entry.getValue();
                mMap.addMarker(new MarkerOptions().position(new LatLng(plantItem.getLatitude(), plantItem.getLongitude())).title(plantItem.getName()).icon(bitmapDescriptorfromVector(getApplicationContext(), getMarkerImg(plantItem), "xy")).snippet(plantItem.objectToString()));
            }
        });

    }

    private void filterMapCategory(String string, Button button){

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        @ColorInt int colorAccent = typedValue.data;

        theme.resolveAttribute(R.attr.colorOnPrimarySurface, typedValue, true);
        @ColorInt int colorNormal = typedValue.data;

        mMap.clear();

        if (button.equals(selecteted)){
            filterMapReset();
            for (Button b:quickFilterBtns){
                b.setTextColor(colorNormal);
                Drawable[] drawablesprev1  =b.getCompoundDrawables();
                drawablesprev1[0].setColorFilter(colorNormal, PorterDuff.Mode.SRC_ATOP);
            }
            selecteted=null;
        }else {

            button.setTextColor(colorAccent);
            Drawable[] drawablesprev = button.getCompoundDrawables();
            drawablesprev[0].setColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP);

            for (Button b : quickFilterBtns) {
                if (!b.equals(button)) {
                    b.setTextColor(colorNormal);
                    Drawable[] drawablesprev1 = b.getCompoundDrawables();
                    drawablesprev1[0].setColorFilter(colorNormal, PorterDuff.Mode.SRC_ATOP);
                }
            }

            for (Map.Entry<Integer, PlantItem> entry : markersHashMap.entrySet()) {
                PlantItem plantItem = entry.getValue();
                if (plantItem.getCategory().equals(string)) {
                    LatLng newMark = new LatLng(plantItem.getLatitude(), plantItem.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(newMark).icon(bitmapDescriptorfromVector(this, getMarkerImg(plantItem), "xy")).snippet(plantItem.objectToString()).title(plantItem.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newMark));
                }
            }
            selecteted=button;
        }
    }

    private int getMarkerImg(PlantItem plantItem){
        switch (plantItem.getCategory()){
            case "Baum":
                return R.drawable._585px_google_maps_pin_tree;
            case "Blüte":
                return R.drawable._585px_google_maps_pin__1_;
            case "Knospe":
                return R.drawable._585px_google_maps_pin_svg;
            case "Wurzel":
                return R.drawable._585px_google_maps_pin_root;
        }
        return R.drawable._585px_google_maps_pin_tree;
    }

    private BitmapDescriptor bitmapDescriptorfromVector(Context context, int vectorResId, String mText){
        /*Drawable vectorDrawable= ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);*/
        try
        {
            Resources resources = context.getResources();
            float scale = resources.getDisplayMetrics().density;
            Drawable vectorDrawable= ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);


            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.RED);
            paint.setTextSize((int) (10* scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/2;
            int y = (bitmap.getHeight() + bounds.height())/2-10;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return BitmapDescriptorFactory.fromBitmap(bitmap);

        }
        catch (Exception e)
        {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_create_24);
        }
    }

    private void setupMapIfNeeded() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save the task list to preference
        saveData();
    }

    public void saveData(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(PLANTITEMS, ObjectSerializer.serialize(markersHashMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();

        SharedPreferences prefs2 = getSharedPreferences(SHARED_PREFS_FILE2, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs2.edit();
        try {
            editor2.putString(SEARCHITEMS, ObjectSerializer.serialize(searchMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor2.commit();

        SharedPreferences settings = getSharedPreferences("FileID", 0);
        SharedPreferences.Editor editor3 = settings.edit();
        editor3.putString("ID", (String) fileId);
        editor3.putBoolean("Shared", sharedDrive);
        editor3.commit();

        saveCamPos();
    }

    public void saveCamPos(){
        CameraPosition mMyCam = mMap.getCameraPosition();
        double longitude = mMyCam.target.longitude;
        double latidude= mMyCam.target.latitude;
        float zoom = mMyCam.zoom;

        SharedPreferences settings = getSharedPreferences("Camera", 0);
        SharedPreferences.Editor editor3 = settings.edit();
        editor3.putFloat("longitude", (float) longitude);
        editor3.putFloat("latitude", (float) latidude);
        editor3.putFloat("zoom", (float) zoom);

        editor3.apply();
    }

    @Override
    public void onBackPressed() {
        if (list.getVisibility()==View.VISIBLE){
            editsearch.setQuery("", false);
            editsearch.clearFocus();
            filterMapReset();
            list.setVisibility(View.GONE);
            backgrnd.setVisibility(View.GONE);
            lastSearchTxt.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            newMarker_btn.setVisibility(View.VISIBLE);
            switchMapType.setVisibility(View.VISIBLE);
        }else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Bitte klicke ZURÜCK erneut zum Beenden", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            account=result.getSignInAccount();
            if (account != null) {
                if (account.getPhotoUrl()!=null) {
                    Picasso.with(this).load(account.getPhotoUrl()).resize(150, 150).transform(new CropCircleTransformation()).into(googleButton);
                    //Glide.with(getApplicationContext()).load(account.getPhotoUrl()).into(googleButton);
                }else {
                    if (account.getDisplayName()!=null) {
                        googleButton.setImageBitmap(getDefaultIcon());
                    }
                }
            }
        }else{
            Toast.makeText(getApplicationContext(),"Anmeldung abgebrochen",Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap getDefaultIcon(String familyName, String displayName){
        Bitmap bg = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        // paint background with the trick
        Paint rect_paint = new Paint();
        rect_paint.setStyle(Paint.Style.FILL);

        if((int) (familyName.charAt(0)%3)==0){
            if((int) (displayName.charAt(0)%3)==0){
                rect_paint.setColor(Color.rgb(255, 255, 0));
            }else if ((int) (displayName.charAt(0)%3)==1){
                rect_paint.setColor(Color.rgb(255, 128, 0));
            }else {
                rect_paint.setColor(Color.rgb(128, 255, 0));
            }
        }else if ((int) (familyName.charAt(0)%3)==1){
            if((int) (displayName.charAt(0)%3)==0){
                rect_paint.setColor(Color.rgb(0, 255, 255));
            }else if ((int) (displayName.charAt(0)%3)==1){
                rect_paint.setColor(Color.rgb(0, 128, 255));
            }else {
                rect_paint.setColor(Color.rgb(0, 255, 128));
            }
        }else {
            if((int) (displayName.charAt(0)%3)==0){
                rect_paint.setColor(Color.rgb(255, 0, 255));
            }else if ((int) (displayName.charAt(0)%3)==1){
                rect_paint.setColor(Color.rgb(255, 0, 128));
            }else {
                rect_paint.setColor(Color.rgb(255, 0, 128));
            }

        }
        rect_paint.setTextAlign(Paint.Align.CENTER);
        rect_paint.setAlpha(0x80); // optional
        canvas.drawCircle(75, 75, 75,  rect_paint);
        rect_paint.setColor(Color.WHITE);
        rect_paint.setTextSize(100);
        canvas.drawText(account.getDisplayName().substring(0,1), 75, 110, rect_paint);
        return bg;
    }
    private Bitmap getDefaultIcon(){
        return getDefaultIcon(account.getFamilyName(), account.getDisplayName());
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        File tempDir= inContext.getCacheDir();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        tempDir.mkdir();
        File tempFile = null;
        try {
            tempFile = File.createTempFile("profilPicture", ".jpg", tempDir);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] bitmapData = bytes.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(tempFile);


        /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "ProfilPhoto", null);
        return Uri.parse(path);*/

    }

    public void silentLogin() {
        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (pendingResult != null) {
            handleGooglePendingResult(pendingResult);
        } else {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent,RC_SIGN_IN);
        }
    }

    private void handleGooglePendingResult(OptionalPendingResult<GoogleSignInResult> pendingResult) {
        if (pendingResult.isDone()) {
            // There's immediate result available.
            GoogleSignInResult signInResult = pendingResult.get();
            onSilentSignInCompleted(signInResult);
        } else {
            // There's no immediate result ready,  waits for the async callback.
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult signInResult) {
                    onSilentSignInCompleted(signInResult);
                }
            });
        }
    }

    private void onSilentSignInCompleted(GoogleSignInResult signInResult) {
        GoogleSignInAccount signInAccount = signInResult.getSignInAccount();
        if (signInAccount != null) {
            handleSignInResult(signInResult);
            account=signInAccount;

        } else {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent,RC_SIGN_IN);
        }
    }

    private void checkForGooglePermissions() {

        if (!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                ACCESS_DRIVE_SCOPE,
                SCOPE_EMAIL, ACCESS_DRIVE_SCOPE2)) {
            GoogleSignIn.requestPermissions(
                    MapsActivity.this,
                    RC_AUTHORIZE_DRIVE,
                    GoogleSignIn.getLastSignedInAccount(getApplicationContext()),
                    ACCESS_DRIVE_SCOPE,
                    SCOPE_EMAIL, ACCESS_DRIVE_SCOPE2);
        }

    }

    private void driveSetUp() {

        GoogleSignInAccount mAccount = GoogleSignIn.getLastSignedInAccount(MapsActivity.this);

        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Collections.singleton(Scopes.DRIVE_FILE));
        credential.setSelectedAccount(mAccount.getAccount());
        googleDriveService =
                new com.google.api.services.drive.Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("GoogleDriveIntegration 3")
                        .build();
        mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
        mDriveServiceHelper.setFileId(fileId);

    }

    public void uploadFile(File file, String folderId) {
        if (mDriveServiceHelper.getFileId()==null) {
            mDriveServiceHelper.uploadFile(file, "text/html", folderId).addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                @Override
                public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                    linkTextString=googleDriveFileHolder.getWebLink();
                    fileId = googleDriveFileHolder.getId();
                    SharedPreferences settings = getSharedPreferences("FileID", 0);
                    SharedPreferences.Editor editor3 = settings.edit();
                    editor3.putString("ID", (String) fileId);
                    editor3.putBoolean("Shared", false);

                    editor3.apply();
                    Log.i(TAG, "onSuccess of Folder creation: ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.i(TAG, "onFailure of Folder creation: " + e.getMessage());
                }
            });
            Toast.makeText(MapsActivity.this, "Datei hochgeladen", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MapsActivity.this, "Datei existiert bereits", Toast.LENGTH_SHORT).show();
        }

    }

    public void createFolderInDrive(File fileX) {

        mDriveServiceHelper.createFolder("FindItApp", null)
                .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                    @Override
                    public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                        folderId=googleDriveFileHolder.getId();
                        uploadFile(fileX, folderId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public boolean deleteFile(String fileId) {

        mDriveServiceHelper.deleteFolderFile(fileId);
        return false;
    }

    public void downloadFile(String fileId, File gfile) {
        mDriveServiceHelper.downloadFile(gfile, fileId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void file) {
                        readFromFile(gfile);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (sharedDrive) {
                            file = new File(getApplicationContext().getFilesDir(), "FindIt");
                            clearFile(file);
                            markersHashMap.clear();
                            searchMap.clear();
                            saveData();
                            filterMapReset();
                            role = "Keine Brechtigungen, kontaktiern Sie den Eigentümer";
                            newMarker_btn.setEnabled(false);
                        }
                    }
                });
    }


    private void createFile(String accountName){
        String yourFile = "FindIt";

        file = new File(getApplicationContext().getFilesDir(),yourFile);
        boolean exists = file.exists();

        if(!exists) {
            try {
                if (mDriveServiceHelper.getFileId()==null) {
                    if (!sharedDrive) {
                        file.createNewFile();
                        writeDataToFile(file);
                        createFolderInDrive(file);
                    }
                }else {
                    file.createNewFile();
                    downloadFile(mDriveServiceHelper.getFileId(), file);
                    readFromFile(file);
                    filterMapReset();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            if (mDriveServiceHelper.getFileId()==null && !sharedDrive){
                createFolderInDrive(file);
            }
        }
    }

    private void writeDataToFile(File file){
        try {
            clearFile(file);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(file.getName(), Context.MODE_PRIVATE));
            for (Map.Entry<Integer, PlantItem> entry : markersHashMap.entrySet()) {
                int key = entry.getKey();
                outputStreamWriter.write(Objects.requireNonNull(markersHashMap.get(key)).objectToString());
                outputStreamWriter.flush();
                outputStreamWriter.write('\n');
                outputStreamWriter.flush();
            }
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void clearFile(File file){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
            pw.print("");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readFromFile(File file) {

        try {
            InputStream inputStream = this.openFileInput(file.getName());

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                markersHashMap.clear();
                searchMap.clear();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    PlantItem obj= (PlantItem) stringToObject(receiveString);
                    markersHashMap.put(obj.getId(), obj);
                    if (searchMap.containsKey(obj.getName())){
                        Objects.requireNonNull(searchMap.get(obj.getName())).add(obj.getId());
                    }else {
                        searchMap.put(obj.getName(), new ArrayList<Integer>());
                        Objects.requireNonNull(searchMap.get(obj.getName())).add(obj.getId());
                    }
                }
                filterMapReset();
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

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
}