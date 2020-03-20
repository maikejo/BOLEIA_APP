package app.boleia.passageiro.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.appyvet.materialrangebar.RangeBar;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import com.google.android.gms.vision.CameraSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
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

import app.boleia.passageiro.R;
import app.boleia.passageiro.common.Common;
import app.boleia.passageiro.helper.CustomInfoWindow;
import app.boleia.passageiro.helper.DirectionsJSONParser;
import app.boleia.passageiro.helper.PicassoMarker;
import app.boleia.passageiro.helper.trail.RouteOverlayView;
import app.boleia.passageiro.helper.trail.TrailSupportMapFragment;
import app.boleia.passageiro.model.Automovel;
import app.boleia.passageiro.model.FCMResponse;
import app.boleia.passageiro.model.Notification;
import app.boleia.passageiro.model.Passageiros;
import app.boleia.passageiro.model.Rota;
import app.boleia.passageiro.model.Sender;
import app.boleia.passageiro.model.Token;
import app.boleia.passageiro.model.Viagens;
import app.boleia.passageiro.model.ViagensRequisicao;
import app.boleia.passageiro.remote.IFCMService;
import app.boleia.passageiro.remote.IGoogleAPI;
import app.boleia.passageiro.service.MyFirebaseIdService;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolicitacaoViagemMapsActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        CompoundButton.OnCheckedChangeListener{

   // SupportMapFragment mapFragment;

    private TrailSupportMapFragment mapFragment;

    private CameraSource cameraSource = null;
    private static final String TAG = "LandmarkRecognition";

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    private static final int PERMISSION_REQUESTS = 1;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    Marker marker = null;
    Marker mUserMarker, markerDestination, markerAutomovel;
    Button btnPickupRequest;
    IFCMService mServiceIFCM;
    DatabaseReference driverrsAvaliable;

    PlaceAutocompleteFragment place_location, place_destination;
    String mPLaceLocation, mPlaceDestination;
    private PicassoMarker picassoTarget;
    LatLng latLngDestino;

    private IGoogleAPI mService;
    String keyAutomovel;
    RangeBar qtdPassageiros;

    String startAndress;
    String endAndress;
    String distancia;
    String tempo;
    String placeDestino;
    boolean places;
    CardView cardViewSolicitacao;
    Automovel automovel;
    String nomeMotorista;
    String uaimotorista;

    private RecyclerView cardRecyclerViewSolicitacao;
    private RecyclerView cardRecyclerViewAvaliacao;
    private RecyclerView cardRecyclerViewLocalizacao;

    private CardAdapterSolicitacao cardAdapterSolicitacao;
    private CardAdapterAvaliacao cardAdapterAvaliacao;
    private CardAdapterLocalizacao cardAdapterLocalizacao;
    List<Viagens> listViagens;
    List<ViagensRequisicao> listViagensRequisicao;
    private CardAdapterSolicitacao.DetailsAdapterListener detailsAdapterListener;
    private Viagens viagens;
    private String cdViagemAvaliacao;
    private int qtdPassageiroRange;

    private Button editTextLocalizar;
    private List<LatLng> bangaloreRoute;

    private boolean cardLocalizacaoVisible;

    private Double latDestinoDigitado;
    private Double longtDestinoDigitado;

    List<LatLng> points = null;

    private void createRoute() {
        if (bangaloreRoute == null) {
            bangaloreRoute = new ArrayList<>();
        } else {
            bangaloreRoute.clear();
        }
        bangaloreRoute.add(new LatLng(-8.897009,13.1910995));
        bangaloreRoute.add(new LatLng(-8.894111773766555,13.189943544566633));

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_solicitacao);

        mService = Common.getGoogleAPI();

        mapFragment = (TrailSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSolicitacao);
        mapFragment.getMapAsync(this);

     /*   Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("76950043830-d703gilkki2t25edug2i5abpqub2ur0f.apps.googleusercontent.com"));

        Vision vision = visionBuilder.build();*/


        //-------ML--------------
       /* preview = findViewById(R.id.firePreview);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
        ToggleButton facingSwitch = findViewById(R.id.facingSwitch);
        facingSwitch.setOnCheckedChangeListener(this);

        if (allPermissionsGranted()) {
            createCameraSource();
            startCameraSource();
        } else {
            getRuntimePermissions();
        }*/



       /* qtdPassageiros = (RangeBar) findViewById(R.id.qtdPassageiro);
        qtdPassageiros.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                qtdPassageiroRange = rightPinIndex;
            }

        }); */

        createRoute();

        editTextLocalizar = (Button) findViewById(R.id.editTextLocalizar);
        editTextLocalizar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

             if(cardLocalizacaoVisible){
                 cardRecyclerViewLocalizacao.setVisibility(View.VISIBLE);
             }else{
                 setCardAdapterLocalizacao();
             }

             }
         });


        mService = Common.getGoogleAPI();
        mServiceIFCM = Common.getFCMService();

        cardRecyclerViewSolicitacao = (RecyclerView) findViewById(R.id.recycler_view_solicitacao);
        cardRecyclerViewSolicitacao.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardAdapterSolicitacao = new CardAdapterSolicitacao(this, new CardAdapterSolicitacao.DetailsAdapterListener() {
            @Override
            public void btSolicitacaoClick(View v, int position) {
                solicitarViagem(places, latDestinoDigitado, longtDestinoDigitado);
                sendRequestToDriver(uaimotorista);
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
                                    startActivity(new Intent(SolicitacaoViagemMapsActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
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

                }else{
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();

                }

            }
        });
        cardRecyclerViewSolicitacao.setAdapter(this.cardAdapterSolicitacao);



        //CARD AVALIACAO
        cardRecyclerViewAvaliacao = (RecyclerView) findViewById(R.id.recycler_view_avaliacao);
        cardRecyclerViewAvaliacao.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardAdapterAvaliacao = new CardAdapterAvaliacao(this, new CardAdapterAvaliacao.DetailsAdapterListener() {
            @Override
            public void btFinalizarBoleiaClick(View v, final int position, final float rage) {

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
                databaseReference.child(cdViagemAvaliacao).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        databaseReference.child(cdViagemAvaliacao).child("avaliacao").setValue(rage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(SolicitacaoViagemMapsActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
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

            @Override
            public void btCancelarClick(View v, int position) {
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            keyAutomovel = bundle.getString("cdautomovel");
        }

        loadAutomoveisTraking();
        setUpLocation();

    }

    public void setCardAdapterLocalizacao(){

        cardLocalizacaoVisible = true;


        getAtributosDirectionExtra(mLastLocation.getLatitude(), mLastLocation.getLongitude(),false);

        //CARD LOCALIZACAO
        cardRecyclerViewLocalizacao = (RecyclerView) findViewById(R.id.recycler_view_localizacao);
        cardRecyclerViewLocalizacao.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardAdapterLocalizacao = new CardAdapterLocalizacao(this, new CardAdapterLocalizacao.DetailsAdapterListener() {

            @Override
            public void btCancelarClick(View v, int position) {
                cardRecyclerViewLocalizacao.setVisibility(View.INVISIBLE);
                //onBackPressed();
            }

            @Override
            public void selectDestinoClick(String place, int position) {
                places = true;
                placeDestino = place;

                if (mMap != null) {
                    if (markerDestination != null) {
                        markerDestination.remove();
                        mMap.clear();
                        marker = null;
                        loadAutomoveisTraking();
                    }

                   // markerDestination = mMap.addMarker(new MarkerOptions()
                    //        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    //        .position(place)
                     //       .title("Destino" + mPLaceLocation));

                   // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));

                    LatLng origem = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    //SETA DIRECAO DESTINO
                    String url = getDirectionsUrlPlace(origem, place);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                    getAtributosDirectionExtraPlaces(place,true);

                    cardRecyclerViewLocalizacao.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    public void loadAutomoveisTraking() {

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.automoveis_tbl);
        driverLocation.child(keyAutomovel).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                automovel = dataSnapshot.getValue(Automovel.class);

                if (automovel.getCdstatus().equals("ACT") || automovel.getCdstatus().equals("USO")) {

                    if (marker == null) {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(automovel.getLatitude(), automovel.getLongitude()))
                                .flat(true)
                                .title("Automovel: " + automovel.getDsmarca() + "/" + automovel.getDsmodelo())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_van_01)));
                    } else {
                        animateMarker(mMap, marker, new LatLng(automovel.getLatitude(), automovel.getLongitude()), false);
                    }

                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker arg0) {
                        Intent intent = new Intent(getBaseContext(), SolicitacaoViagemMapsActivity.class);

                        startActivity(intent);
                        Log.d("mGoogleMap1", "Activity_Calling");
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
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

    private void sendRequestToDriver(String driverID) {

        MyFirebaseIdService a = new MyFirebaseIdService();
        a.onTokenRefresh();

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);
        tokens.orderByKey().equalTo(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                            Token token = postSnapShot.getValue(Token.class);

                            String json_lat_lng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            String riderToken = FirebaseInstanceId.getInstance().getToken();
                            Notification data = new Notification(riderToken, json_lat_lng,keyAutomovel);
                            Sender content = new Sender(token.getToken(), data);

                            mServiceIFCM.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(SolicitacaoViagemMapsActivity.this, "Requisição enviada.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                //Toast.makeText(SolicitacaoViagemMapsActivity.this, "Falha ao enviar Requisicao.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

    public static void animateMarker(final GoogleMap map, final Marker marker, final LatLng toPosition, final boolean hideMarker) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_update_info) {
            startActivity(new Intent(SolicitacaoViagemMapsActivity.this, EditProfileActivity.class));
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
        Intent intent = new Intent(SolicitacaoViagemMapsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //   mMap.getUiSettings().setZoomControlsEnabled(true);
        //   mMap.getUiSettings().setZoomGesturesEnabled(true);
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
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);

                    getAtributosDirectionExtra(latLngDestino.latitude, latLngDestino.longitude, true);

                   // mapFragment.setUpPath(points, mMap, getCurrentAnimType());

                }
            }
        });
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    private RouteOverlayView.AnimType getCurrentAnimType() {
            return RouteOverlayView.AnimType.PATH;
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

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
                lineOptions.color(getResources().getColor(R.color.lineOptions));
                lineOptions.geodesic(true);

            }

            if (lineOptions != null) {

                mMap.addPolyline(lineOptions);

              /*  zoomRoute(points);
                mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        mapFragment.onCameraMove(mMap);
                    }
                });

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mapFragment.setUpPath(points, mMap, getCurrentAnimType());
                    }
                }, 1000);

               */

            } else {
                Toast.makeText(SolicitacaoViagemMapsActivity.this, "Não foi possivel traçar rota.Tente traçar a rota novamente !.", Toast.LENGTH_SHORT).show();
            }

        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                  /*  if (allPermissionsGranted()) {
                        createCameraSource();
                        startCameraSource();
                    }
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);*/
                }
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
                                    listarCardSolicitacao(places,null,null);
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
                            Toast.makeText(SolicitacaoViagemMapsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void getAtributosDirectionExtraPlaces(String origem, final boolean solicitacao) {


        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&"
                    + "destination=" + origem + "&"
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



                                JSONObject vllatDestino = legsObject.getJSONObject("end_location");
                                String latDestino = vllatDestino.getString("lat");
                                Double vlLatDestino = Double.parseDouble(latDestino);
                                latDestinoDigitado = vlLatDestino;

                                JSONObject vllongDestino = legsObject.getJSONObject("end_location");
                                String longDestino = vllongDestino.getString("lng");
                                Double vlLongtDestino = Double.parseDouble(longDestino);
                                longtDestinoDigitado = vlLongtDestino;



                                if (mMap != null) {
                                    if (markerDestination != null) {
                                        markerDestination.remove();
                                        mMap.clear();
                                        marker = null;
                                        loadAutomoveisTraking();
                                    }

                                    LatLng destino = new LatLng(latDestinoDigitado,longtDestinoDigitado);

                                    markerDestination = mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .position(destino)
                                            .title("Destino" + placeDestino));

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destino, 15.0f));
                                }

                                    if(solicitacao){
                                    listarCardSolicitacao(places,vlLatDestino,vlLongtDestino);
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
                            Toast.makeText(SolicitacaoViagemMapsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }
    private void getRuntimePermissions() {
        List allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, (String[]) allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }
    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

}
