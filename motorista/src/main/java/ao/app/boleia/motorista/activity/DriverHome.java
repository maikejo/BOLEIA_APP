package ao.app.boleia.motorista.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.common.Common;
import ao.app.boleia.motorista.helper.DirectionsJSONParser;
import ao.app.boleia.motorista.model.Automovel;
import ao.app.boleia.motorista.model.FCMResponse;
import ao.app.boleia.motorista.model.LogAutomovel;
import ao.app.boleia.motorista.model.Notification;
import ao.app.boleia.motorista.model.Sender;
import ao.app.boleia.motorista.model.Token;
import ao.app.boleia.motorista.model.Viagens;
import ao.app.boleia.motorista.model.ViagensDetails;
import ao.app.boleia.motorista.remote.IFCMService;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    String customerId;
    private static int DISPLACEMENT = 10;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    IFCMService mFCMService;

    DatabaseReference automoveis;
    GeoFire geoFire;
    Marker mCurrent;
    SupportMapFragment mapFragment;
    Double lat_a, log_a, lat_b, log_b;
    String keyAutomovel;
    String idViagem;
    TextView btnStatusViagemAceita,btnStatusViagemIniciada,btnStatusViagemConcluida;
    String nomeMotorista;
    float bearing = 0;
    float velocidade = 0;
    private String cdViagemLog;
    String cdutilizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());

        setContentView(ao.app.boleia.motorista.R.layout.activity_driver_home);

      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navagationHeaderView = navigationView.getHeaderView(0);

        TextView txtName = (TextView) navagationHeaderView.findViewById(R.id.txtDriverName);
        CircleImageView imageAvatar = (CircleImageView) navagationHeaderView.findViewById(R.id.image_avatar);


        txtName.setText(keyAutomovel);

         */

      /*  if (Common.currentUser.getAvatarUrl() != null && !TextUtils.isEmpty(Common.currentUser.getAvatarUrl())) {
            Picasso.with(this)
                    .load(Common.currentUser.getAvatarUrl())
                    .into(imageAvatar);

        }
        */
        mFCMService = Common.getFCMService();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        lat_a = bundle.getDouble("lat_a");
        log_a = bundle.getDouble("log_a");
        lat_b = bundle.getDouble("lat_b");
        log_b = bundle.getDouble("log_b");
        keyAutomovel = bundle.getString("key");
        idViagem = bundle.getString("idViagem");
        cdutilizador = bundle.getString("cdutilizador");
        //nomeMotorista = bundle.getString("nomeMotorista");


        btnStatusViagemAceita = (TextView) findViewById(R.id.btnStatusViagemAceita);
        btnStatusViagemIniciada = (TextView) findViewById(R.id.btnStatusViagemIniciada);
        btnStatusViagemConcluida = (TextView) findViewById(R.id.btnStatusViagemConcluida);

        btnStatusViagemAceita.setVisibility(View.INVISIBLE);
        btnStatusViagemIniciada.setVisibility(View.VISIBLE);

        btnStatusViagemIniciada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
                databaseReference.child(idViagem).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Viagens bdviagens = dataSnapshot.getValue(Viagens.class);

                            if (bdviagens.getStviagem().equals("ACE")) {

                                //ATUALIZA lat/log automovel firebase
                                databaseReference.child(idViagem).child("stviagem").setValue("INI").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        return;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        return;
                                    }
                                });

                                btnStatusViagemIniciada.setVisibility(View.INVISIBLE);

                                btnStatusViagemConcluida.setVisibility(View.VISIBLE);
                            }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        btnStatusViagemConcluida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
                databaseReference.child(idViagem).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Viagens bdviagens = dataSnapshot.getValue(Viagens.class);

                            if (bdviagens.getStviagem().equals("INI")) {


                                //ATUALIZA lat/log automovel firebase
                                databaseReference.child(idViagem).child("stviagem").setValue("CON").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        return;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        return;
                                    }
                                });

                                btnStatusViagemIniciada.setVisibility(View.INVISIBLE);
                                btnStatusViagemConcluida.setVisibility(View.VISIBLE);


                               /* Intent myIntent = new Intent(DriverHome.this, AutomovelActivity.class);
                                DriverHome.this.startActivity(myIntent); */


                                DatabaseReference databaseReferenceUtilizador = FirebaseDatabase.getInstance().getReference().child(Common.automoveis_tbl);
                                databaseReferenceUtilizador.orderByChild("cdautomovel").equalTo(bdviagens.getCdautomovel()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot issue : dataSnapshot.getChildren()) {

                                            Automovel automovel = issue.getValue(Automovel.class);

                                            Intent intent = new Intent(DriverHome.this, ViagemActivity.class);

                                            intent.putExtra("key", automovel.getCdautomovel());
                                            intent.putExtra("marca", automovel.getDsmarca());
                                            intent.putExtra("modelo", automovel.getDsmodelo());
                                            intent.putExtra("statusAutomovel", automovel.getCdstatus());
                                            intent.putExtra("init", "N");
                                            intent.putExtra("nomeMotorista" , nomeMotorista);
                                            intent.putExtra("cdutilizador",automovel.getCdutilizador());

                                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            finish();
                                           // onBackPressed();
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
        });

        automoveis = FirebaseDatabase.getInstance().getReference(Common.online_tbl);
        geoFire = new GeoFire(automoveis);
        setUpLocation();

        updateFirebaseToken();
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Common.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (Common.mLastLocation != null) {

            final double latitute = Common.mLastLocation.getLatitude();
            final double longitude = Common.mLastLocation.getLongitude();


                bearing = Common.mLastLocation.getBearing();
                velocidade = Common.mLastLocation.getSpeed();

                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.automoveis_tbl);
                    databaseReference.child(keyAutomovel).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Automovel automovel = dataSnapshot.getValue(Automovel.class);

                            if (automovel.getCdautomovel().equals(keyAutomovel)) {

                                HashMap<String, Object> resultAutomovel = new HashMap<>();
                                resultAutomovel.put("latitude", latitute);
                                resultAutomovel.put("longitude", longitude);
                                resultAutomovel.put("bearing", bearing);
                                resultAutomovel.put("velocidade", velocidade);
                                resultAutomovel.put("cdviagemlog", cdViagemLog);

                                databaseReference.child(keyAutomovel).updateChildren(resultAutomovel);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    if (mMap != null) {
                        mMap.clear();

                        mCurrent = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitute, longitude))
                                .icon(bitmapDescriptorFromVector(DriverHome.this, R.mipmap.ic_vanjupiter))
                                .title("Sua localização"));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitute, longitude), 15.0f));
                    }

                    addLinesMotoristaUtilizador();


                    if(lat_a != null && log_a != null){
                        mMap.addCircle(new CircleOptions()
                                .center(new LatLng(lat_a,log_a))
                                .radius(50)
                                .strokeColor(Color.BLUE)
                                .fillColor(0x220000FF)
                                .strokeWidth(5.0f));
                    }

                   /* geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(Common.notificacao_tbl));
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat_a,log_a),0.05f);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            sendArrivedNotification("cP8Xbq_oGDI:APA91bFAR4PhE5_d-Aq9BEg7wkbeRKNSHfXlwA3ntt0ozpxtTWzdyLFB6YQ4hbRou1b4Maf9x44m28peXvt_uvSDWyxjBBK2OO5TzbAMFOcA7yrgteklYL-08LQ57lUAXSCKX2f-js4b");
                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });*/

               // }



           // });



        } else {
            Log.d("ERROR", "Não foi possivel pegar sua localização.");
        }
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

    private void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
           /* Intent intent = new Intent(getApplicationContext(), ViagemActivity.class);
            startActivity(intent);
            finish();*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_home, menu);
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
        int id = item.getItemId();

        if (id == R.id.nav_update_info) {
            startActivity(new Intent(DriverHome.this, EditProfileActivity.class));
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
        Intent intent = new Intent(DriverHome.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        displayLocation();


    }


    private void sendArrivedNotification(String customerId) {
        Token token = new Token(customerId);
        Notification notification = new Notification("Estou aqui",String.format("O Motorista está em sua localidade!",Common.currentUser.getNome()));
        Sender sender = new Sender(token.getToken(),notification);

        mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if (response.body().success == 1) {
                  //  Toast.makeText(DriverHome.this, "Requisição enviada.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });

    }

    Marker markerOrigemUtilizador;
    Marker markerDestinoUtilizador;
    private void addLinesMotoristaUtilizador() {

        if(Common.mLastLocation != null && mMap != null){

            LatLng origemMotorista = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
            LatLng origemUtilizador = new LatLng(lat_a, log_a);
            LatLng destinoUtilizador = new LatLng(lat_b, log_b);

            //ROTA AUTOMOVEL TO UTILIZADOR
            String url = getDirectionsUrl(origemMotorista, origemUtilizador);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            markerOrigemUtilizador =  mMap.addMarker(new MarkerOptions()
                    .position(origemUtilizador)
                    .title("Consultor")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_consultor)));

            //ROTA UTILIZADOR TO DESTINO
            String url2 = getDirectionsUrlDestinoUtilizador(origemUtilizador, destinoUtilizador);
            DownloadTaskDestinoUtilizador downloadTask2 = new DownloadTaskDestinoUtilizador();
            downloadTask2.execute(url2);

            markerDestinoUtilizador =  mMap.addMarker(new MarkerOptions()
                    .position(destinoUtilizador)
                    .title("Seu Destino !")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
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
        Common.mLastLocation = location;
        displayLocation();

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
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class DownloadTaskDestinoUtilizador extends AsyncTask<String, Void, String> {

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

            ParserTaskDestinoUtilizador parserTask = new ParserTaskDestinoUtilizador();
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            if(lineOptions!=null){
                mMap.addPolyline(lineOptions);
            }
        }
    }

    private class ParserTaskDestinoUtilizador extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);
            }

            if(lineOptions!=null){
                mMap.addPolyline(lineOptions);
            }
        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = getResources().getString(R.string.google_direction_api);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String getDirectionsUrlDestinoUtilizador(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = getResources().getString(R.string.google_direction_api);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
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



}
