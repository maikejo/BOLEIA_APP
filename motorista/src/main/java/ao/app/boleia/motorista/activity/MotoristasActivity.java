package ao.app.boleia.motorista.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ao.app.boleia.motorista.BuildConfig;
import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.common.Common;
import ao.app.boleia.motorista.helper.RecyclerTouchListener;
import ao.app.boleia.motorista.helper.RecyclerviewAdapterUser;
import ao.app.boleia.motorista.model.User;
import ao.app.boleia.motorista.model.UserDetails;
import io.paperdb.Paper;

public class MotoristasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public boolean isClickable = true;
    List<User> list;
    RecyclerView recyclerview;
    User user;
    String init;
    DatabaseReference onlineRef, currentUserRef;
    ConstraintLayout rootLayout;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    Button btnLogar;
    EditText edtPassword;
    String email;
    String avatarUrl;
    DatabaseReference databaseReference;
    private ImageView imageAvatar;
    private ImageView imageUpload;
    private BroadcastReceiver mReceiverBattery;
    private Boolean conectadoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());

//        Bugfender.init(this, "fGscf6jKP9rqWP5PsiSMO0SdnxNqNV6B", BuildConfig.DEBUG);
//        Bugfender.enableLogcatLogging();
//        Bugfender.enableCrashReporting();
//        Bugfender.getDeviceIdentifier();

        setContentView(R.layout.activity_mainctivity2);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_mainctivity2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navagationHeaderView = navigationView.getHeaderView(0);

        recyclerview = (RecyclerView) findViewById(R.id.rviewmainctivity2);

        mReceiverBattery = new BatteryBroadcastReceiver();
        conectadoInternet = isNetworkAvailable();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            init = bundle.getString("init");
        }


        final ProgressDialog progress = ProgressDialog.show(this, null, null, true, false);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progress.setContentView(R.layout.progress_layout);


        databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.utilizador_tbl);
        databaseReference.orderByChild("cdtipo").equalTo("MTR").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    if (dataSnapshot1.child("plantao").getValue().equals(true)) {

                        final User listdata = new User();
                        UserDetails userDetails = dataSnapshot1.getValue(UserDetails.class);

                        String cduser = userDetails.getUid();
                        String email = userDetails.getEmail();
                        String avatarUrl = userDetails.getAvatarUrl();
                        boolean logado = userDetails.isLogado();

                        listdata.setLogado(logado);
                        listdata.setUid(cduser);
                        listdata.setEmail(email);
                        listdata.setAvatarUrl(avatarUrl);

                        list.add(listdata);
                    }

                    RecyclerviewAdapterUser recycler = new RecyclerviewAdapterUser(getApplicationContext(), list);
                    //RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(MotoristasActivity.this);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                    recyclerview.setLayoutManager(layoutManager);
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                    recyclerview.setAdapter(recycler);

                    progress.dismiss();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(MotoristasActivity.this, recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onLongClick(View child, int childPosition) {

            }

            @Override
            public void onClick(View child, int childPosition) throws FirebaseAuthException {
                user = list.get(childPosition);

                final String uid = user.getUid();
                String email = user.getEmail();
                String avatarUrl = user.getAvatarUrl();

                Intent intent = new Intent(getApplicationContext(), ViagemActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("email", email);
                intent.putExtra("avatarUrl", avatarUrl);

                final ProgressDialog progress = ProgressDialog.show(MotoristasActivity.this, null, null, true, false);
                progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progress.setContentView(R.layout.progress_layout);
                progress.dismiss();
                progress.show();

                //Init Firebase
                auth = FirebaseAuth.getInstance();
                db = FirebaseDatabase.getInstance();
                users = db.getReference(Common.utilizador_tbl);

                //LOGAR
                auth.signInWithEmailAndPassword(email, "123456")
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl).orderByChild("cdtipo").equalTo("MTR")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                    if (dataSnapshot1.child("uid").getValue().equals(uid) && dataSnapshot1.child("cdstatus").getValue().equals("ACT")) {

                                                        Common.currentUser = dataSnapshot1.getValue(User.class);

                                                        String key = dataSnapshot1.getKey();

                                                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
                                                        databaseReference.child(key).child("logado").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //Toast.makeText(MotoristasActivity.this,"Você esta online!!!",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                //Toast.makeText(MotoristasActivity.this,"Erro ao salvar!",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        Intent intent = new Intent(getApplicationContext(), AutomovelActivity.class);
                                                        intent.putExtra("init", init);

                                                        startActivity(intent);
                                                        finish();

                                                        progress.dismiss();

                                                    } else {
                                                        //Snackbar.make(rootLayout, "Seu usuário não está habilitado, entre em contato com a Logística.", Snackbar.LENGTH_SHORT).setDuration(10000).show();
                                                        //Toast.makeText(MotoristasActivity.this,"Seu usuário não está habilitado, entre em contato com a Logística.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MotoristasActivity.this,"Senha incorreta.Tente novamente!",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });


            }

        }));

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
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            System.out.println(level);
            System.out.println(isCharging);

        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
