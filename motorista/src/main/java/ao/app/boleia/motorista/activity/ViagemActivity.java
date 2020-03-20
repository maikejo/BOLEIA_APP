package ao.app.boleia.motorista.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.common.Common;
import ao.app.boleia.motorista.helper.MessageListener;
import ao.app.boleia.motorista.helper.RecyclerTouchListener;
import ao.app.boleia.motorista.helper.RecyclerviewAdapterAutomovel;
import ao.app.boleia.motorista.helper.RecyclerviewAdapterViagem;
import ao.app.boleia.motorista.model.Automovel;
import ao.app.boleia.motorista.model.AutomovelDetails;
import ao.app.boleia.motorista.model.LogAutomovel;
import ao.app.boleia.motorista.model.Recarga;
import ao.app.boleia.motorista.model.Rota;
import ao.app.boleia.motorista.model.User;
import ao.app.boleia.motorista.model.Viagens;
import ao.app.boleia.motorista.model.ViagensDetails;
import ao.app.boleia.motorista.model.servicedesk.Data;
import ao.app.boleia.motorista.model.servicedesk.Tarefas;
import ao.app.boleia.motorista.remote.RetrofitServiceDeskInstance;
import ao.app.boleia.motorista.service.MyLocationService;
import ao.app.boleia.motorista.service.MyServiceTaskTarefasDataService;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViagemActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        MessageListener {

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 10;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    List<Viagens> list = new ArrayList<>();
    List<LogAutomovel> logAutomovelList = new ArrayList<>();
    Automovel automovel;
    Automovel automovelTroca;
    List<Automovel> listAutmovel;
    List<Automovel> listAutmovelTroca;
    RecyclerView recyclerview;
    RecyclerView recyclerviewAutomovel;
    String keyAutomovel;
    TextView txtOnline;
    DatabaseReference onlineRef, currentUserRef;
    GeoFire geoFire;
    DatabaseReference automoveis;
    String statusUso;
    Button sairAutomovel;
    String init = "";
    String nomeMotorista;
    String cdutilizador;
    String cdViagem;
    String cdAutomovelEmViagem;
    Button btTrocarAutomovel, btSair, btLog, btSairLog;
    CardView cardViewAutomovel, cardViewLog, cardView;
    float bearing = 0;
    float velocidade = 0;
    DatabaseReference databaseReference;
    TextView txtMarca;
    TextView txtPlaca;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private CircleImageView imageAvatar2;
    private String avatarUrl;
    private Ringtone som;
    private BroadcastReceiver mReceiverBattery, smsReceiver;
    private String imeiDispositivo;
    private boolean internet;
    private boolean verificarPlantao = true;
    private StringBuilder log;
    private String cdViagemLog;
    private SmsReceiver smsBroadcastReceiver;
    Date agora = new Date(System.currentTimeMillis() * 1000);
    Date menos24horas = new Date(System.currentTimeMillis() - 86400 * 1000);

    private DatabaseReference dbReferenceAutomovel =  FirebaseDatabase.getInstance().getReference().child(Common.automoveis_tbl);
    private DatabaseReference dbReferenceUtilizador =  FirebaseDatabase.getInstance().getReference().child(Common.utilizador_tbl);
    private DatabaseReference dbReferenceViagens =  FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
    private DatabaseReference dbReferenceDispositivo =  FirebaseDatabase.getInstance().getReference().child(Common.dispositivo_tbl);
    private DatabaseReference dbReferenceRotas =  FirebaseDatabase.getInstance().getReference().child(Common.rotas_tbl);
    private DatabaseReference dbReferenceLogViagem =  FirebaseDatabase.getInstance().getReference().child(Common.logAutomovel_tbl);
    private DatabaseReference dbReferenceRecarga =  FirebaseDatabase.getInstance().getReference().child(Common.recarga_tbl);

    private FirebaseUser currentUserMotorista = FirebaseAuth.getInstance().getCurrentUser();


    public static String getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }

            String imei = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());

     /*   Bugfender.init(this, "fGscf6jKP9rqWP5PsiSMO0SdnxNqNV6B", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableCrashReporting();
        Bugfender.getDeviceIdentifier();*/


        final Handler h = new Handler();
        h.postDelayed(new Runnable(){
            @Override
            public void run(){
                // do stuff then
                // can call h again after work!
                if(verificarPlantao) {
                    checkUserPlantao();
                    h.postDelayed(this, 300000);
                }
            }
        }, 300000);



        setContentView(R.layout.activity_viagem);

        smsBroadcastReceiver = new SmsReceiver();


        imageAvatar2 = (CircleImageView) findViewById(R.id.image_avatar2);
        btTrocarAutomovel = (Button) findViewById(R.id.btTrocarAutomovel);
        btLog = (Button) findViewById(R.id.buttonLog);
        cardViewAutomovel = (CardView) findViewById(R.id.cardViewAutomoveis);
        cardView = (CardView) findViewById(R.id.cardView);
        cardViewLog = (CardView) findViewById(R.id.cardViewLog);
        btSair = (Button) findViewById(R.id.btnFechar);
        btSairLog = (Button) findViewById(R.id.btnFecharLog);
        recyclerviewAutomovel = (RecyclerView) findViewById(R.id.rviewAutomovel);

        mReceiverBattery = new BatteryBroadcastReceiver();
        SmsReceiver.bindListener(this);

        loadIMEI();
        imeiDispositivo = getUniqueIMEIId(getApplicationContext());

        checkForSmsPermission();
        onCallPermission();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //RECUPERA OBJETOS DA ACTIVITY ANTERIOR
        if (bundle != null) {
            String sms = bundle.getString("Message");
            String marca = bundle.getString("marca");
            String placa = bundle.getString("placa");
            String modelo = bundle.getString("modelo");

            nomeMotorista = bundle.getString("nomeMotorista");
            keyAutomovel = bundle.getString("key");
            statusUso = bundle.getString("statusAutomovel");
            init = bundle.getString("init");
            cdutilizador = bundle.getString("cdutilizador");

            txtMarca = (TextView) findViewById(R.id.textViatura);
            txtMarca.setText(marca + "/" + modelo);
            txtPlaca = (TextView) findViewById(R.id.textPlaca);
            txtPlaca.setText(placa);

            TextView txtUtilizador = (TextView) findViewById(R.id.txtConexao2);
            txtUtilizador.setText(nomeMotorista);
        }

        //VERIFICA SE ESTA EM SEGUNDO PLANO
        if (init == null) {
            if (isMyServiceRunning(MyLocationService.class) != true) {
                if (isMyServiceRunning(MyLocationService.class)) return;
                Intent startIntent = new Intent(this, MyLocationService.class);
                startIntent.setAction("start");
                startService(startIntent);
            }
        }

        //INTEGRACAO LISTA TAREFAS - SERVICE_DESK
        //serviceDeskTarefas();

        //BUSCA AVATAR
        dbReferenceUtilizador.orderByChild("uid").equalTo(currentUserMotorista.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot issue : dataSnapshot.getChildren()) {
                            avatarUrl = issue.child("avatarUrl").getValue().toString();

                            if (avatarUrl != null && !avatarUrl.equals("")) {
                                Picasso.with(ViagemActivity.this)
                                        .load(avatarUrl)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(imageAvatar2);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        txtOnline = (TextView) findViewById(R.id.txtOnline);


        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    System.out.println("connected");
                    cardView.setVisibility(View.VISIBLE);
                    isOnline();
                } else {
                    cardView.setVisibility(View.INVISIBLE);
                    isOffline();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


        //Presença ONLINE
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUserRef.onDisconnect().removeValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        currentUserRef = FirebaseDatabase.getInstance().getReference(Common.online_tbl).child(currentUserMotorista.getUid());

        atualizaDadosAutomovel();
        btSairAutomovel();

       dbReferenceAutomovel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listAutmovel = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    if (dataSnapshot1.child("cdstatus").getValue().equals("ACT")) {

                        final Automovel listdataAutomovel = new Automovel();
                        AutomovelDetails automovelDetails = dataSnapshot1.getValue(AutomovelDetails.class);

                        String cdautomovel = dataSnapshot1.getKey();
                        String cdrgb = automovelDetails.getCdrgb();
                        final String cdstatus = automovelDetails.getCdstatus();
                        String dsmarca = automovelDetails.getDsmarca();
                        String dsmodelo = automovelDetails.getDsmodelo();
                        String dsplaca = automovelDetails.getDsplaca();
                        String uidmotorista = automovelDetails.getUidmotorista();

                        listdataAutomovel.setCdautomovel(cdautomovel);
                        listdataAutomovel.setCdrgb(cdrgb);
                        listdataAutomovel.setCdstatus(cdstatus);
                        listdataAutomovel.setDsmarca(dsmarca);
                        listdataAutomovel.setDsmodelo(dsmodelo);
                        listdataAutomovel.setDsplaca(dsplaca);
                        listdataAutomovel.setUidmotorista(uidmotorista);

                        if (cdstatus.equals("ACT")) {
                            listdataAutomovel.setCdstatus("Activo");
                        } else if (cdstatus.equals("INA")) {
                            listdataAutomovel.setCdstatus("Inactivo");
                        } else if (cdstatus.equals("MAN")) {
                            listdataAutomovel.setCdstatus("Manutenção");
                        }

                        listAutmovel.add(listdataAutomovel);

                        RecyclerviewAdapterAutomovel recycler = new RecyclerviewAdapterAutomovel(listAutmovel);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(ViagemActivity.this);
                        recyclerviewAutomovel.setLayoutManager(layoutmanager);
                        recyclerviewAutomovel.setItemAnimator(new DefaultItemAnimator());
                        recyclerviewAutomovel.setAdapter(recycler);

                    } else {
                        listAutmovel.remove(listAutmovel);
                        RecyclerviewAdapterAutomovel recycler = new RecyclerviewAdapterAutomovel(listAutmovel);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(ViagemActivity.this);
                        recyclerviewAutomovel.setLayoutManager(layoutmanager);
                        recyclerviewAutomovel.setItemAnimator(new DefaultItemAnimator());
                        recyclerviewAutomovel.setAdapter(recycler);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //TROCAR DE VAN
        btTrocarAutomovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewAutomovel.setVisibility(View.VISIBLE);
            }
        });

        //LOG
        btLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewLog.setVisibility(View.VISIBLE);
            }
        });


        recyclerviewAutomovel.addOnItemTouchListener(new RecyclerTouchListener(ViagemActivity.this, recyclerviewAutomovel, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onLongClick(View child, int childPosition) {
            }

            @Override
            public void onClick(View child, int childPosition) {
                listAutmovelTroca = new ArrayList<>();
                automovelTroca = listAutmovel.get(childPosition);

                txtMarca.setText(automovelTroca.getDsmarca());
                txtPlaca.setText(automovelTroca.getDsplaca());


                dbReferenceAutomovel.orderByChild(automovelTroca.getCdautomovel()).equalTo(currentUserMotorista.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                HashMap<String, Object> resultAutomovelUso = new HashMap<>();
                                resultAutomovelUso.put("cdstatus", "USO");
                                resultAutomovelUso.put("uidmotorista", currentUserMotorista.getUid());
                                resultAutomovelUso.put("cdutilizador", currentUserMotorista.getUid());

                                dbReferenceAutomovel.child(automovelTroca.getCdautomovel()).updateChildren(resultAutomovelUso);

                                HashMap<String, Object> resultAutomovel = new HashMap<>();
                                resultAutomovel.put("cdstatus", "ACT");
                                resultAutomovel.put("uidmotorista", "");
                                resultAutomovel.put("cdutilizador", "");

                                dbReferenceAutomovel.child(keyAutomovel).updateChildren(resultAutomovel);

                                txtOnline.setText("Em uso");
                                int color = Color.parseColor("#f816a463");
                                txtOnline.setTextColor(color);
                                FirebaseDatabase.getInstance().goOnline();

                                cardViewAutomovel.setVisibility(View.INVISIBLE);
                                keyAutomovel = automovelTroca.getCdautomovel();


                                dbReferenceUtilizador.child(cdutilizador).child("cdautomovel").setValue(keyAutomovel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

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

        }));

        //SAIR DA VAN
        btSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewAutomovel.setVisibility(View.INVISIBLE);
            }
        });


        //Geo FIre
        //automoveis = FirebaseDatabase.getInstance().getReference(Common.online_tbl);
        //geoFire = new GeoFire(automoveis);
        setUpLocation();

        recyclerview = (RecyclerView) findViewById(R.id.rviewviagem);

        //PARA VIAGENS CONCLUIDAS REMOVE DA LISTA
        dbReferenceViagens.orderByChild("dthrcriado").startAt(menos24horas.getTime()).endAt(agora.getTime()).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot1, @Nullable String s) {

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone som = RingtoneManager.getRingtone(getApplicationContext(), notification);

                Log.e("Count ", "" + dataSnapshot1.getChildrenCount());

                if (dataSnapshot1.child("stviagem").getValue().equals("CRI")) {

                    ViagensDetails viagensDetails = dataSnapshot1.getValue(ViagensDetails.class);
                    final Viagens listdata = new Viagens();
                    //final String cdutilizador = viagensDetails.getCdutilizador();
                    final String sqrota = viagensDetails.getSqrota();
                    String stviagem = viagensDetails.getStviagem();
                    Long qtdPassageiros = viagensDetails.getQtdpassageiros();
                    Long dtCriacao = viagensDetails.getDthrcriado();
                    String cdViagem2 = dataSnapshot1.getKey();

                    //listdata.setCdutilizador(cdutilizador);
                    listdata.setCdviagem(cdViagem2);
                    listdata.setSqrota(sqrota);
                    listdata.setStviagem(stviagem);
                    listdata.setQtdpassageiros(qtdPassageiros);
                    listdata.setDthrcriado(dtCriacao);


                    if (dataSnapshot1.child("uidmotorista").getValue().equals(cdutilizador) && dataSnapshot1.child("stviagem").getValue().equals("CRI")) {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                MediaPlayer mp = null;
                                mp = MediaPlayer.create(ViagemActivity.this, R.raw.alarme);
                                mp.start();
                            }
                        }, 5000);

                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.child("stviagem").getValue().equals("CAN")){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            MediaPlayer mp = null;
                            mp = MediaPlayer.create(ViagemActivity.this, R.raw.alarmfault);
                            mp.start();
                        }
                    }, 5000);

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // CONCLUIR VIAGENS

        dbReferenceViagens.orderByChild("dthrcriado").startAt(menos24horas.getTime()).endAt(agora.getTime()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone som = RingtoneManager.getRingtone(getApplicationContext(), notification);

                Log.e("Count ", "" + dataSnapshot1.getChildrenCount());

                final List<Viagens> list = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot1.getChildren()) {
                    MediaPlayer mp = null;
                    ViagensDetails viagensDetails = postSnapshot.getValue(ViagensDetails.class);

                    if (postSnapshot.child("uidmotorista").getValue().equals(cdutilizador) && viagensDetails.getStviagem().equals("CRI")
                            || postSnapshot.child("uidmotorista").getValue().equals(cdutilizador) && viagensDetails.getStviagem().equals("ACE")
                            || postSnapshot.child("uidmotorista").getValue().equals(cdutilizador) && viagensDetails.getStviagem().equals("INI")
                            || postSnapshot.child("uidmotorista").getValue().equals(cdutilizador) && viagensDetails.getStviagem().equals("CAN")
                            || postSnapshot.child("uidmotorista").getValue().equals(cdutilizador) && viagensDetails.getStviagem().equals("CON")
                            ) {


                        final Viagens listdata = new Viagens();
                        final String cdutilizador = viagensDetails.getCdutilizador();
                        final String sqrota = viagensDetails.getSqrota();
                        String stviagem = viagensDetails.getStviagem();
                        Long qtdPassageiros = viagensDetails.getQtdpassageiros();
                        Long dtCriacao = viagensDetails.getDthrcriado();
                        String cdViagem2 = postSnapshot.getKey();

                        listdata.setCdutilizador(cdutilizador);
                        listdata.setCdviagem(cdViagem2);
                        listdata.setSqrota(sqrota);
                        listdata.setStviagem(stviagem);
                        listdata.setQtdpassageiros(qtdPassageiros);
                        listdata.setDthrcriado(dtCriacao);
                        listdata.setUidmotorista(viagensDetails.getUidmotorista());
                        listdata.setCdautomovel(keyAutomovel);


                        if (stviagem.equals("CRI") || stviagem.equals("ACE") || stviagem.equals("INI") || stviagem.equals("CON") || stviagem.equals("CAN")) {

                            dbReferenceUtilizador.orderByChild("uid").equalTo(cdutilizador).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot issue : dataSnapshot.getChildren()) {

                                        User u = issue.getValue(User.class);

                                        u.setNome(issue.getValue(User.class).getNome());
                                        listdata.setCdutilizador(u.getUid());
                                        listdata.setAvatarUrl(u.getAvatarUrl());
                                        listdata.setNomeUtilizador(u.getNome());

                                        cdViagem = listdata.getCdviagem();
                                        if(listdata.getStviagem().equals("ACE") && currentUserMotorista.getUid().equals(listdata.getUidmotorista())) {
                                            cdViagemLog = listdata.getCdviagem();
                                            cdAutomovelEmViagem = listdata.getCdautomovel();
                                        }

                                        if(listdata.getStviagem().equals("CON") && currentUserMotorista.getUid().equals(listdata.getUidmotorista()) && cdViagemLog != null) {
                                            if(cdViagemLog.equals(listdata.getCdviagem())) {
                                                cdViagemLog = "";
                                                cdAutomovelEmViagem = "";
                                            }
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            dbReferenceRotas.orderByChild("sqrota").equalTo(sqrota).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot rotas : dataSnapshot.getChildren()) {

                                        Rota rota = rotas.getValue(Rota.class);
                                        rota.setLatitude_a(rotas.getValue(Rota.class).getLatitude_a());
                                        rota.setLongitude_a(rotas.getValue(Rota.class).getLongitude_a());
                                        rota.setDsdestino(rotas.getValue(Rota.class).getDsdestino());

                                        listdata.setRotas(rota);
                                    }

                                    list.add(listdata);

                                    RecyclerviewAdapterViagem recycler = new RecyclerviewAdapterViagem(getApplicationContext(), list, null, null);
                                    RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(ViagemActivity.this);
                                    recyclerview.setLayoutManager(layoutmanager);
                                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                                    recyclerview.setAdapter(recycler);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Log.e("FALTAL", "Failed to read app title value.", error.toException());
                                }

                            });

                        } else if(stviagem.equals("CON")) {

                        }
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkUserPlantao() {
        dbReferenceUtilizador.child(currentUserMotorista.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    User plantonista = dataSnapshot.getValue(User.class);

                    if (plantonista.isPlantao()) {
                        System.out.println("plantao true");
                    } else {
                        singOut();
                        System.out.println("plantao false");
                    }
                } else {
                    verificarPlantao = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void isOffline() {
        dbReferenceUtilizador.child(cdutilizador).child("logado").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                verificarPlantao = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                return;
            }
        });
    }

    public void isOnline() {
        dbReferenceUtilizador.child(cdutilizador).child("logado").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                return;
            }
        });
    }

    public void serviceDeskTarefas() {

        MyServiceTaskTarefasDataService service = RetrofitServiceDeskInstance.getRetrofitInstance().create(MyServiceTaskTarefasDataService.class);
        Call<Data> call = service.getTarefasData();

        Log.wtf("URL Chamada", call.request().url() + "");

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                geraListaTarefa(response.body().getOperation().getDetails());
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Toast.makeText(ViagemActivity.this, "Erro ao chamar o serviço: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void geraListaTarefa(ArrayList<Tarefas> noticeArrayList) {
        ArrayList<Tarefas> noticeArrayList2 = noticeArrayList;
        Toast.makeText(ViagemActivity.this, "Chamada ServiceDesk com Sucesso!: ", Toast.LENGTH_SHORT).show();
        // recyclerView = findViewById(R.id.recycler_view_notice_list);
        //adapter = new NoticeAdapter(noticeArrayList);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        //recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setAdapter(adapter);
    }

    public void btSairAutomovel() {
        sairAutomovel = (Button) findViewById(R.id.btSairAutomovel);
        sairAutomovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verificarPlantao = false;

                HashMap<String, Object> resultAutomovel = new HashMap<>();
                resultAutomovel.put("cdstatus", "ACT");
                resultAutomovel.put("cdutilizador", "");
                resultAutomovel.put("uidmotorista", "");

                HashMap<String, Object> resultUtilizador = new HashMap<>();
                resultUtilizador.put("cdautomovel", "");
                resultUtilizador.put("isOnline", false);
                resultUtilizador.put("logado", false);

                dbReferenceAutomovel.child(keyAutomovel).updateChildren(resultAutomovel);
                dbReferenceUtilizador.child(cdutilizador).updateChildren(resultUtilizador);

                currentUserRef.removeValue();
                stopLocationUpdates();

                Intent intent = new Intent(getApplicationContext(), MotoristasActivity.class);
                init = "N";
                intent.putExtra("init", init);

                startActivity(intent);
                finish();

            }
        });
    }

    //METODOS DE GEOLOCALIZACAO
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void atualizaDadosAutomovel() {

        HashMap<String, Object> resultAutomovel = new HashMap<>();
        resultAutomovel.put("cdstatus", "USO");
        resultAutomovel.put("uidmotorista", currentUserMotorista.getUid());

        HashMap<String, Object> resultUtilizador = new HashMap<>();
        resultUtilizador.put("cdautomovel", keyAutomovel);
        resultUtilizador.put("cdutilizador", cdutilizador);

        dbReferenceAutomovel.child(keyAutomovel).updateChildren(resultAutomovel);
        dbReferenceUtilizador.child(cdutilizador).updateChildren(resultUtilizador);

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

            LocationManager lm;
            Location location;
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                bearing = location.getBearing();
                velocidade = location.getSpeed();
            }

            //Atualiza firebase location
            /*geoFire.setLocation(currentUserMotorista.getUid(), new GeoLocation(latitute, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {*/

            HashMap<String, Object> resultAutomovel = new HashMap<>();
            resultAutomovel.put("latitude", latitute);
            resultAutomovel.put("longitude", longitude);
            resultAutomovel.put("bearing", bearing);
            resultAutomovel.put("velocidade", velocidade);
            resultAutomovel.put("cdviagemlog", cdViagemLog);

            dbReferenceAutomovel.child(keyAutomovel).updateChildren(resultAutomovel);


        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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
            }
        }

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        displayLocation();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        createLocationRequest();

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

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        displayLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Common.mLastLocation = location;
        displayLocation();
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
    }

    //VOLTAR PARA ACTIVITY
    @Override
    public void onBackPressed() {
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

    @Override
    protected void onStart() {
        registerReceiver(mReceiverBattery, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiverBattery);
        super.onStop();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_update_info) {
            startActivity(new Intent(ViagemActivity.this, EditProfileActivity.class));
        } else if (id == R.id.nav_sing_out) {
            singOut();
        }
        return true;
    }

    //LOGOUT
    private void singOut() {
        Paper.init(this);
        Paper.book().destroy();

        dbReferenceUtilizador.child(currentUserMotorista.getUid()).child("logado").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                verificarPlantao = false;

                HashMap<String, Object> result = new HashMap<>();
                result.put("cdstatus", "ACT");
                result.put("cdutilizador", "");
                result.put("uidmotorista", "");
                dbReferenceAutomovel.child(keyAutomovel).updateChildren(result);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                init = "N";
                intent.putExtra("init", init);
                currentUserRef.removeValue();
                //  FirebaseAuth.getInstance().signOut();
                Toast.makeText(ViagemActivity.this, "Você está offline!", Toast.LENGTH_SHORT).show();

                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                verificarPlantao = false;
                Toast.makeText(ViagemActivity.this, "Erro ao salvar!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void InformacoesDispositivo(final String imei, final int bateria, final boolean carregando, final boolean internet, final String modelo) {

        HashMap<String, Object> result = new HashMap<>();
        result.put("cdutilizador", cdutilizador);
        result.put("numStatusBateria", bateria);
        result.put("internet", internet);
        result.put("carregando", carregando);
        result.put("modelo", modelo);
        dbReferenceDispositivo.child(imei).updateChildren(result);

    }

    public void loadIMEI() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestReadPhoneStatePermission();
        } else {
            doPermissionGrantedStuffs();
        }
    }

    public void doPermissionGrantedStuffs() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            new AlertDialog.Builder(ViagemActivity.this)
                    .setTitle("Permission Request")
                    .setMessage(getString(R.string.project_id))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(ViagemActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void checkForSmsPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
    }

    public void enviarRecargaCall(final String nuCartaoRecarga) {

        checkForSmsPermission();


        /*DdbRequestRecarga.orderByChild("imeia").equalTo("123456").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Recarga recarga = postSnapshot.getValue(Recarga.class);

                    if (recarga.getImei().equals("123456")) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + "*106*" + nuCartaoRecarga + "#"));
                        if (ActivityCompat.checkSelfPermission(ViagemActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        getApplicationContext().startActivity(intent);

                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + "*106*" + nuCartaoRecarga + "#"));
        getApplicationContext().startActivity(intent);

        String idRecarga = null;

        Date date = new Date();

        Recarga recarga = new Recarga();
        recarga.setOperadora("UNITEL");
        recarga.setDthr_recarga(date.getTime());
        recarga.setImei(imeiDispositivo);
        recarga.setNu_recarga("");
        recarga.setStatus("EFETUADA");

        idRecarga = dbReferenceRecarga.push().getKey();
        dbReferenceRecarga.child(idRecarga).setValue(recarga);

    }

    public void onCallPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    Integer.parseInt("123"));
        } else {
            //startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:12345678901")));
        }
    }

    @Override
    public void messageReceived(String message) {
        Toast.makeText(this, "New Message Received: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("smsMessage");

                //enviarRecargaCall("123456789012");

            }
        };
        this.registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.smsReceiver);
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int bateria = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            internet = isNetworkAvailable();


            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        System.out.println("connected");
                        internet = true;
                    } else {
                        internet = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });

            String modelo = Build.MODEL;


            InformacoesDispositivo(imeiDispositivo, bateria, isCharging, internet, modelo);

        }
    }


}
