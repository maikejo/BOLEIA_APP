package app.boleia.passageiro.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.boleia.passageiro.R;
import app.boleia.passageiro.common.Common;
import app.boleia.passageiro.helper.CustomInfoWindow;
import app.boleia.passageiro.helper.DirectionJSONParser;
import app.boleia.passageiro.helper.DirectionsJSONParser;
import app.boleia.passageiro.helper.RecyclerTouchListener;
import app.boleia.passageiro.model.Automovel;
import app.boleia.passageiro.model.AutomovelDetails;
import app.boleia.passageiro.model.Passageiros;
import app.boleia.passageiro.model.Rota;
import app.boleia.passageiro.model.Viagens;
import app.boleia.passageiro.model.ViagensRequisicao;
import app.boleia.passageiro.remote.IFCMService;
import app.boleia.passageiro.remote.IGoogleAPI;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    SupportMapFragment mapFragment;

    private GoogleMap mMap;
    private GoogleMap mMapAutomovel;

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    private final static int SPEED_VERY_FAST = 1;
    private final static int SPEED_FAST = 2;
    private final static int SPEED_NORMAL = 3;
    private final static int SPEED_SLOW = 4;
    private final static int SPEED_VERY_SLOW = 5;

    private CardAdapterSolicitacao cardAdapterSolicitacao;
    private CardAdapterAvaliacao cardAdapterAvaliacao;
    private CardAdapterLocalizacao cardAdapterLocalizacao;
    private RecyclerView cardRecyclerViewSolicitacao;
    private RecyclerView cardRecyclerViewLocalizacao;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private PlaceAutocompleteFragment places;
    private IGoogleAPI mService;
    IFCMService mFCMService;
    private Polyline direction;
    String version;
    String placeDestino;
    boolean isMarkerRotating;
    Marker mUserMarker, markerDestination, markerAutomovel;
    List<ViagensRequisicao> listViagensRequisicao;
    Marker marker = null;
    Marker markerUtilizador = null;
    String endAndress;
    List<LatLng> points = null;
    private RecyclerView cardRecyclerView;
    private CardAdapter cardAdapter;
    List<Automovel> listAutomovel;
    private String avatarUrl;
    private String avatarUrlPerfil;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    CircleImageView imageAvatar;
    long cont = 0;
    boolean placesBolean;
    LatLng latLngDestino;
    String distancia;
    String tempo;
    String startAndress;
    String nomeMotorista;
    String uaimotorista;
    String keyAutomovel;

    Automovel automovel;
    List<Automovel> list;

    private LatLng animateMarkerPosition = null;
    ArrayList<Marker> automoveisMarkers;
    private Double latDestinoDigitado;
    private Double longtDestinoDigitado;
    private String cdViagemAvaliacao;
    List<Viagens> listViagens;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navagationHeaderView = navigationView.getHeaderView(0);
        TextView txtName = (TextView) navagationHeaderView.findViewById(R.id.txtDriverName);
        imageAvatar = (CircleImageView) navagationHeaderView.findViewById(R.id.image_avatar);

        isMarkerRotating = false;
        cardRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        cardRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardAdapter = new CardAdapter(this);
        cardRecyclerView.setAdapter(this.cardAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.automoveis_tbl);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    final Automovel listdata = new Automovel();
                    AutomovelDetails automovelDetails = dataSnapshot1.getValue(AutomovelDetails.class);

                    if (automovelDetails.getCdstatus().equals("USO")) {

                        String cdautomovel = dataSnapshot1.getKey();
                        final String cdstatus = automovelDetails.getCdstatus();
                        String dsmarca = automovelDetails.getDsmarca();
                        String dsmodelo = automovelDetails.getDsmodelo();
                        String uidmotorista = automovelDetails.getUidmotorista();
                        String placa = automovelDetails.getDsplaca();

                        listdata.setCdautomovel(cdautomovel);
                        listdata.setCdstatus(cdstatus);
                        //listdata.setDsmarca(dsmarca + "-" + dsmodelo);
                        listdata.setDsmarca(dsmarca);
                        listdata.setDsmodelo(dsmodelo);
                        listdata.setUidmotorista(uidmotorista);
                        listdata.setDsplaca(placa);

                        if (cdstatus.equals("ACT")) {
                            listdata.setCdstatus("Activo");
                        } else if (cdstatus.equals("INA")) {
                            listdata.setCdstatus("Inactivo");
                        } else if (cdstatus.equals("MAN")) {
                            listdata.setCdstatus("Manutenção");
                        } else if (cdstatus.equals("USO")) {
                            listdata.setCdstatus("Online");
                        }

                        list.add(listdata);
                    }
                }

                cardAdapter.setItems(list);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        cardRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(HomeActivity.this, cardRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onLongClick(View child, int childPosition) {

            }

            @Override
            public void onClick(View child, int childPosition) {
                Automovel automovel = new Automovel();
                automovel = list.get(childPosition);

                automovel.setDsmodelo(automovel.getDsmodelo());

                Intent intent = new Intent(getApplicationContext(), SolicitacaoViagemMapsActivity.class);
                intent.putExtra("cdautomovel", automovel.getCdautomovel());
                startActivity(intent);

            }

        }));


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        if (bundle != null) {

            avatarUrl  = bundle.getString("avatarUrl");
        }

        txtName.setText(Common.currentUser.getApelido());


        final DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
        driverInformation.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot issue : dataSnapshot.getChildren()) {
                            avatarUrlPerfil = issue.child("avatarUrl").getValue().toString();

                            if (avatarUrlPerfil != null && !avatarUrlPerfil.equals("")) {
                                Picasso.with(HomeActivity.this)
                                        .load(avatarUrlPerfil)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(imageAvatar);

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mService = Common.getGoogleAPI();
        mFCMService = Common.getFCMService();


        loadAutomoveisTraking();

        setUpLocation();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }


    Marker automovel1;
    /*private void loadAutomoveisTraking() {
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.automoveis_tbl);
        driverLocation.child("-LO8JiDMNsIrcqJWVPfM").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Automovel automovel = new Automovel();

                automovel = dataSnapshot.getValue(Automovel.class);
                if (automovel.getCdstatus().equals("USO")) {

                    String cdautomovel = dataSnapshot.getKey();
                    if (marker == null) {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(automovel.getLatitude(), automovel.getLongitude()))
                                .flat(true)
                                .title(automovel.getDsmarca() + "/" + automovel.getDsmodelo())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_van_01)));

                        marker.setTag(cdautomovel);
                        // marker.setRotation(-45);

                    } else {
                        animateMarker(mMap, marker, new LatLng(automovel.getLatitude(), automovel.getLongitude()), false);
                    }

                }else if(marker!=null){
                    marker.remove();
                    marker = null;
                    //mMap.clear();
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker arg0) {

                        Intent intent = new Intent(getBaseContext(), SolicitacaoViagemMapsActivity.class);
                        //intent.putExtra("cdautomovel", arg0.getSnippet());
                        intent.putExtra("cdautomovel", arg0.getTag().toString());
                        startActivity(intent);
                        Log.d("mGoogleMap1", "Activity_Calling");
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FALTAL", "Failed to read app title value.", error.toException());
            }
        });

        DatabaseReference driverLocation2 = FirebaseDatabase.getInstance().getReference(Common.automoveis_tbl);
        driverLocation2.child("-L8D0duAnWZr9DBt60p_").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Automovel automovel = new Automovel();

                automovel = dataSnapshot.getValue(Automovel.class);
                if (automovel.getCdstatus().equals("USO")) {

                    String cdautomovel = dataSnapshot.getKey();
                    if (automovel1 == null) {
                        automovel1 = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(automovel.getLatitude(), automovel.getLongitude()))
                                .flat(true)
                                .anchor(0.5f,0.5f)
                                .title(automovel.getDsmarca() + "/" + automovel.getDsmodelo())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_van_02)));
                        automovel1.setTag(cdautomovel);

                    } else {
                        //  float bearing = (float) bearingBetweenLocations(new LatLng(automovel.getLatitude(), automovel.getLongitude()), new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                        //  automovel1.setRotation(bearing);

                        animateMarker(mMap, automovel1, new LatLng(automovel.getLatitude(), automovel.getLongitude()), false);
                    }

                }else if(automovel1!=null){
                        automovel1.remove();
                        automovel1 = null;
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker arg0) {

                        Intent intent = new Intent(getBaseContext(), SolicitacaoViagemMapsActivity.class);
                        //intent.putExtra("cdautomovel", arg0.getSnippet());
                        intent.putExtra("cdautomovel", arg0.getTag().toString());
                        startActivity(intent);
                        Log.d("mGoogleMap1", "Activity_Calling");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FALTAL", "Failed to read app title value.", error.toException());
            }
        });

    }*/



    private void loadAutomoveisTraking() {

        listAutomovel = new ArrayList<>();

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.automoveis_tbl);
        driverLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.automoveis_tbl);
                    driverLocation.child(dataSnapshot1.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {

                            automovel = dataSnapshot2.getValue(Automovel.class);

                            listAutomovel.add(automovel);

                            automoveisMarkers = new ArrayList<>();

                            if (automovel.getCdstatus().equals("USO") && automovel.getCdautomovel().equals(dataSnapshot1.getKey())) {

                                for (Automovel automovel : listAutomovel){
                                    Marker marker = null;
                                    Marker marker1 = null;

                                    if (listAutomovel.size() == 1) {
                                        if(marker == null){

                                            marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(automovel.getLatitude(), automovel.getLongitude()))
                                                    .flat(true)
                                                    .anchor(0.5f,0.5f)
                                                    .title(automovel.getDsmarca() + "/" + automovel.getDsmodelo())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_van_01)));

                                        }else  {
                                            animateMarker(mMap, marker, new LatLng(automovel.getLatitude(), automovel.getLongitude()), false);
                                        }


                                    }
                                    if (listAutomovel.size() == 2) {
                                        if(marker1 == null) {

                                            marker1 = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(automovel.getLatitude(), automovel.getLongitude()))
                                                    .flat(true)
                                                    .anchor(0.5f,0.5f)
                                                    .title(automovel.getDsmarca() + "/" + automovel.getDsmodelo())
                                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_van_01)));

                                        }else  {
                                            animateMarker(mMap, marker1, new LatLng(automovel.getLatitude(), automovel.getLongitude()), false);
                                        }

                                    }
                                }
                            }

                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                @Override
                                public void onInfoWindowClick(Marker arg0) {

                                    Intent intent = new Intent(getBaseContext(), SolicitacaoViagemMapsActivity.class);
                                    //intent.putExtra("cdautomovel", arg0.getSnippet());
                                    intent.putExtra("cdautomovel", arg0.getTag().toString());
                                    startActivity(intent);
                                    Log.d("mGoogleMap1", "Activity_Calling");
                                }
                            });


                        }



                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.e("FALTAL", "Failed to read app title value.", error.toException());
                        }
                    });




                }




            }


            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FALTAL", "Failed to read app title value.", error.toException());
            }
        });




    }



    public void addDriverToMap(Automovel automovel,ArrayList<Marker> automoveisMarkers) {
        automoveisMarkers.add(mMap.addMarker(new MarkerOptions()
                .position(new LatLng(automovel.getLatitude(), automovel.getLongitude()))
                .flat(true)
                .title(automovel.getDsmarca() + "/" + automovel.getDsmodelo())
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_van_01))));
    }

    private void setUpLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request runtime permisson
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            final double latitute = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitute, longitude), 17.0f));

            Log.d("LOGDEV", String.format("Sua localizacao mudando : %f / %f", latitute, longitude));
        } else {
            Log.d("LOGDEV", "Não foi possivel pegar sua localização.");
        }
    }


    public void animateMarker(final GoogleMap map, final Marker marker, final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;

                marker.setPosition(new LatLng(lat, lng));


                Location location = null;
                location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(startLatLng.latitude);
                location.setLongitude(startLatLng.longitude);

                marker.setRotation(location.getBearing());
                marker.setFlat(true);
                marker.setAnchor(0.5f, 0.5f);
                marker.setAlpha((float) 0.91);

                float bearing = getBearing(new LatLng(toPosition.latitude,toPosition.longitude), new LatLng(startLatLng.latitude,startLatLng.longitude));
                CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                        .target(animateMarkerPosition).bearing(bearing);


                //  float bearing = (float) bearingBetweenLocations(new LatLng(toPosition.latitude,toPosition.longitude), new LatLng(startLatLng.latitude,startLatLng.longitude));
                //  marker.setRotation(bearing);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);
        if(begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float)(Math.toDegrees(Math.atan(lng / lat)));
        else if(begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float)((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if(begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return  (float)(Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if(begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float)((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "Aparelho não suportado", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_update_info) {
            startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
        } else if (id == R.id.nav_sing_out) {
            singOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void singOut() {
        Paper.init(this);
        Paper.book().destroy();

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapAutomovel = googleMap;
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mMap != null) {
                    if (markerDestination != null) {
                        markerDestination.remove();
                        mMap.clear();
                        marker = null;
                        loadAutomoveisTraking();
                    }

                    latLngDestino = latLng;

                    markerDestination = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .position(latLng)
                            .title("Destino: " + endAndress));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                    LatLng origem = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    //SETA DIRECAO DESTINO
                    String url = getDirectionsUrl(origem, latLngDestino);
                    HomeActivity.DownloadTask downloadTask = new HomeActivity.DownloadTask();
                    downloadTask.execute(url);

                    getAtributosDirectionExtra(latLngDestino.latitude, latLngDestino.longitude, true);

                    // mapFragment.setUpPath(points, mMap, getCurrentAnimType());

                }
            }
        });

    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            HomeActivity.ParserTask parserTask = new HomeActivity.ParserTask();


            parserTask.execute(result);

        }
    }


    private void getAtributosDirectionExtra(double lat, double lng, final boolean solicitacao) {


        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&"
                    + "destination=" + lat + "," + lng + "&"
                    + "key=" + getResources().getString(R.string.google_direction_api);
            Log.d("DEV", requestApi);

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray routes = jsonObject.getJSONArray("routes");

                                JSONObject object = routes.getJSONObject(0);
                                JSONArray legs = object.getJSONArray("legs");
                                JSONObject legsObject = legs.getJSONObject(0);

                                JSONObject distance = legsObject.getJSONObject("distance");
                                distancia = distance.getString("text");

                                if (distancia.contains(" km")) {
                                    distancia = distancia.replace(" km", "");
                                } else {
                                    distancia = distancia.replace(" m", "");
                                }

                                JSONObject time = legsObject.getJSONObject("duration");
                                tempo = time.getString("text");

                                String start_address = legsObject.getString("start_address");
                                startAndress = start_address;

                                String end_address = legsObject.getString("end_address");
                                endAndress = end_address;

                                if(solicitacao){
                                    listarCardSolicitacao(placesBolean,null,null);
                                }else{

                                    listViagensRequisicao = new ArrayList<>();
                                    ViagensRequisicao viagensRequisicao = new ViagensRequisicao();
                                    viagensRequisicao.setOrigem(startAndress);
                                    viagensRequisicao.setDestino("TESTE");
                                    viagensRequisicao.setTempo("1");
                                    viagensRequisicao.setStatusViagem("SOLICITAR");
                                    viagensRequisicao.setMarcaModelo("TESTE");
                                    viagensRequisicao.setNomeMotorista("TESTE");
                                    viagensRequisicao.setStViagens("TESTE");
                                    viagensRequisicao.setStatusViagem("teSTE");

                                    listViagensRequisicao.add(viagensRequisicao);

                                    cardAdapterLocalizacao.setItems(listViagensRequisicao);
                                    cardRecyclerViewLocalizacao.setAdapter(cardAdapterLocalizacao);


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(HomeActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarCardSolicitacao(final boolean places, final Double lat, final Double longt) {

        if (startAndress != null) {
            listViagensRequisicao = new ArrayList<>();

            DatabaseReference databaseReferenceViagem = FirebaseDatabase.getInstance().getReference().child(Common.automoveis_tbl);
            databaseReferenceViagem.orderByChild("cdautomovel").equalTo(keyAutomovel).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        final Automovel automovel = dataSnapshot1.getValue(Automovel.class);
                        automovel.getUidmotorista();

                        DatabaseReference databaseReferenceUtilizador = FirebaseDatabase.getInstance().getReference().child(Common.utilizador_tbl);
                        databaseReferenceUtilizador.orderByChild("uid").equalTo(automovel.getUidmotorista()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot issue : dataSnapshot.getChildren()) {

                                    Passageiros u = issue.getValue(Passageiros.class);
                                    nomeMotorista = u.getNome();
                                    uaimotorista = u.getUid();


                                    ViagensRequisicao viagensRequisicao = new ViagensRequisicao();
                                    viagensRequisicao.setOrigem(startAndress);

                                    if(places){
                                        viagensRequisicao.setDestino(placeDestino);
                                    }else{
                                        viagensRequisicao.setDestino(endAndress);
                                    }

                                    viagensRequisicao.setTempo(tempo);
                                    viagensRequisicao.setStatusViagem("SOLICITAR");
                                    viagensRequisicao.setMarcaModelo(automovel.getDsmarca() + "/" + automovel.getDsmodelo());
                                    viagensRequisicao.setNomeMotorista(nomeMotorista);

                                    listViagensRequisicao.add(viagensRequisicao);
                                    cardAdapterSolicitacao.setItems(listViagensRequisicao);
                                    cardRecyclerViewSolicitacao.setAdapter(cardAdapterSolicitacao);

                                    cardAdapterSolicitacao = new CardAdapterSolicitacao(getBaseContext(),
                                            new CardAdapterSolicitacao.DetailsAdapterListener() {

                                                @Override
                                                public void btSolicitacaoClick(View v, int position) {
                                                    solicitarViagem(places,latDestinoDigitado,longtDestinoDigitado);
                                                }

                                                @Override
                                                public void btCancelarClick(View v, int position) {

                                                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);

                                                    if(cdViagemAvaliacao!=null){
                                                        databaseReference.child(cdViagemAvaliacao).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                databaseReference.child(cdViagemAvaliacao).child("stviagem").setValue("CAN").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        //startActivity(new Intent(SolicitacaoViagemMapsActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                                        //finish();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        return;
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                                    }



                                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    finish();
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void solicitarViagem(boolean places, Double lat, Double longt) {
        String idRotaCriada = null;
        String idViagem = null;


        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.viagens_tbl);
        DatabaseReference refRotas = FirebaseDatabase.getInstance().getReference(Common.rotas);

        if (latLngDestino == null && placeDestino == null) {
            Toast.makeText(this, "Selecione no mapa o destino!", Toast.LENGTH_SHORT).show();
        } else {

            Rota rota = new Rota();
            rota.setDsrota("Destino");
            rota.setKmtotal(new Double(distancia));
            rota.setLatitude_a(mLastLocation.getLatitude());
            rota.setLongitude_a(mLastLocation.getLongitude());

            if(places){
                rota.setDsdestino(placeDestino);
                rota.setLatitude_b(lat);
                rota.setLongitude_b(longt);
            }else{
                rota.setLatitude_b(latLngDestino.latitude);
                rota.setLongitude_b(latLngDestino.longitude);
                rota.setDsdestino(endAndress);
            }

            rota.setDsorigem(startAndress);



            idRotaCriada = refRotas.push().getKey();
            rota.setSqrota(idRotaCriada);
            refRotas.child(idRotaCriada).setValue(rota);

            final Viagens viagens = new Viagens();

            idViagem = dbRequest.push().getKey();

            Date date = new Date();
            String dthoracriado = DateFormat.format("dd/MM/yyyy hh:mm:ss", date).toString();

            viagens.setAvaliacao(new Long(0));
            viagens.setCdautomovel(keyAutomovel);
            viagens.setCdutilizador(Common.currentUser.getCdutilizador());
            viagens.setCdviagem(idViagem);
            viagens.setDthraceita(date.getTime());
            viagens.setDthrinicio(date.getTime());
            viagens.setDthrfim(date.getTime());
            viagens.setDthrcriado(date.getTime());
            viagens.setQtdpassageiros(1);
            viagens.setSqrota(idRotaCriada);
            viagens.setUidmotorista(uaimotorista);
            viagens.setVisto(true);
            viagens.setStviagem("CRI");

            dbRequest.child(idViagem).setValue(viagens);

            DatabaseReference dbRequestAtualizaViagem = FirebaseDatabase.getInstance().getReference(Common.viagens_tbl);

            dbRequestAtualizaViagem.child(idViagem).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listViagens = new ArrayList<>();
                    listViagensRequisicao = new ArrayList<>();
                    Viagens bdviagens = dataSnapshot.getValue(Viagens.class);

                    ViagensRequisicao viagensRequisicao = new ViagensRequisicao();
                    viagensRequisicao.setOrigem(startAndress);
                    viagensRequisicao.setDestino(endAndress);
                    viagensRequisicao.setTempo(tempo);
                    viagensRequisicao.setMarcaModelo(automovel.getDsmarca() + "/" + automovel.getDsmodelo());
                    viagensRequisicao.setNomeMotorista(nomeMotorista);

                    cdViagemAvaliacao = bdviagens.getCdviagem();

                    if (bdviagens.getStviagem().equals("CRI")) {
                        viagensRequisicao.setStatusViagem("Aguardando motorista aceitar...");
                        viagensRequisicao.setStViagens(bdviagens.getStviagem());
                        listViagensRequisicao.add(viagensRequisicao);
                        cardAdapterSolicitacao.setItems(listViagensRequisicao);

                        mMap.setOnMapClickListener(null);
                    }

                    if (bdviagens.getStviagem().equals("ACE")) {
                        viagensRequisicao.setStatusViagem("Viagem foi aceita , aguarde...");
                        viagensRequisicao.setStViagens(bdviagens.getStviagem());
                        listViagensRequisicao.add(viagensRequisicao);
                        cardAdapterSolicitacao.setItems(listViagensRequisicao);
                        cardRecyclerViewSolicitacao.setAdapter(cardAdapterSolicitacao);

                    }

                    if (bdviagens.getStviagem().equals("INI")) {
                        viagensRequisicao.setStatusViagem("Viagem iniciada ao destino");
                        viagensRequisicao.setStViagens(bdviagens.getStviagem());
                        listViagensRequisicao.add(viagensRequisicao);
                        cardAdapterSolicitacao.setItems(listViagensRequisicao);
                        cardRecyclerViewSolicitacao.setAdapter(cardAdapterSolicitacao);
                    }

                    if (bdviagens.getStviagem().equals("CON")) {
                        viagensRequisicao.setStatusViagem("Viagem foi concluida!");
                        viagensRequisicao.setStViagens(bdviagens.getStviagem());
                        listViagensRequisicao.add(viagensRequisicao);
                        cardAdapterSolicitacao.setItems(listViagensRequisicao);
                        cardRecyclerViewSolicitacao.setAdapter(cardAdapterSolicitacao);
                        //cardRecyclerViewSolicitacao.setVisibility(View.INVISIBLE);

                        cardAdapterAvaliacao.setItems(listViagensRequisicao);
                        cardRecyclerViewSolicitacao.setAdapter(cardAdapterAvaliacao);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getResources().getString(R.string.google_direction_api);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


    private String getDirectionsUrlPlace(LatLng origin,String dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getResources().getString(R.string.google_direction_api);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        ProgressDialog mDialog = new ProgressDialog(HomeActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Por favor , aguarde...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList points = null;
            PolylineOptions polylineOptions = null;


            for (int i = 0; i < lists.size(); i++) {

                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }
            if (direction != null) {
                direction = mMap.addPolyline(polylineOptions);
            }

        }
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing =  -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }



}
