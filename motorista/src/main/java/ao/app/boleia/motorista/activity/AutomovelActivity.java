package ao.app.boleia.motorista.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ao.app.boleia.motorista.BuildConfig;
import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.common.Common;
import ao.app.boleia.motorista.helper.RecyclerTouchListener;
import ao.app.boleia.motorista.helper.RecyclerviewAdapter;
import ao.app.boleia.motorista.model.Automovel;
import ao.app.boleia.motorista.model.AutomovelDetails;
import ao.app.boleia.motorista.model.Categoria;
import ao.app.boleia.motorista.model.User;
import ao.app.boleia.motorista.model.UserDetails;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class AutomovelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public boolean isClickable = true;
    DatabaseReference databaseReference;
    List<Automovel> list;
    RecyclerView recyclerview;
    Automovel automovel;
    String init;
    DatabaseReference onlineRef, currentUserRef;
    User listdataUser = new User();
    private String avatarUrl;
    private ImageView imageAvatar;
    private String cdUtilizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());

        /*Bugfender.init(this, "fGscf6jKP9rqWP5PsiSMO0SdnxNqNV6B", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableCrashReporting();
        Bugfender.getDeviceIdentifier();*/

        setContentView(R.layout.activity_automovel);
        recyclerview = (RecyclerView) findViewById(R.id.rview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_automovel);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navagationHeaderView = navigationView.getHeaderView(0);
        TextView txtName = (TextView) navagationHeaderView.findViewById(R.id.txtDriverName);
        imageAvatar = (CircleImageView) navagationHeaderView.findViewById(R.id.image_avatar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            init = bundle.getString("init");
        }


        //RECUPERA AVATAR
        final DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
        driverInformation.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot issue : dataSnapshot.getChildren()) {

                    avatarUrl = issue.child("avatarUrl").getValue().toString();
                    UserDetails userDetails = issue.getValue(UserDetails.class);

                    cdUtilizador = userDetails.getUid();

                    if (avatarUrl != null && !avatarUrl.equals("")) {
                        Picasso.with(AutomovelActivity.this)
                                .load(avatarUrl)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(imageAvatar);
                    }

                    for (Categoria categoriaUsuario : userDetails.getCategorias()) {
                        listdataUser.setCategorias(userDetails.getCategorias());
                    }

                    if(listdataUser.getCategorias() != null) {

                        //QUANDO ADICIONADO VEICULO
                        databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.automoveis_tbl);
                        databaseReference.orderByChild("cdstatus").equalTo("ACT").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                list = new ArrayList<>();

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    final Automovel listdata = new Automovel();
                                    AutomovelDetails automovelDetails = dataSnapshot1.getValue(AutomovelDetails.class);

                                    String cdautomovel = dataSnapshot1.getKey();
                                    String cdrgb = automovelDetails.getCdrgb();
                                    final String cdstatus = automovelDetails.getCdstatus();
                                    String dsmarca = automovelDetails.getDsmarca();
                                    String dsmodelo = automovelDetails.getDsmodelo();
                                    String dsplaca = automovelDetails.getDsplaca();
                                    String uidmotorista = automovelDetails.getUidmotorista();

                                    listdata.setCdautomovel(cdautomovel);
                                    listdata.setCdrgb(cdrgb);
                                    listdata.setCdstatus(cdstatus);
                                    listdata.setDsmarca(dsmarca);
                                    listdata.setDsmodelo(dsmodelo);
                                    listdata.setDsplaca(dsplaca);
                                    listdata.setUidmotorista(uidmotorista);


                                    for (Categoria categoria : automovelDetails.getCategorias()) {
                                        listdata.setCategorias(automovelDetails.getCategorias());
                                    }

                                    if (cdstatus.equals("ACT")) {
                                        listdata.setCdstatus("Activo");
                                    } else if (cdstatus.equals("INA")) {
                                        listdata.setCdstatus("Inactivo");
                                    } else if (cdstatus.equals("MAN")) {
                                        listdata.setCdstatus("Manutenção");
                                    } else if (cdstatus.equals("USO") && uidmotorista.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        listdata.setCdstatus("Em uso");
                                    } else {
                                        listdata.setCdstatus("Em uso");
                                    }

                                    if(listdataUser.getCategorias() != null) {

                                        for (Categoria catUser : listdataUser.getCategorias()) {
                                            for (Categoria cat : listdata.getCategorias()) {

                                                if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                                        && cat.getSgcategoria().equals("AEP") && catUser.getSgcategoria().equals("AEP")
                                                        && cat.getSgcategoria().equals("VAN") && catUser.getSgcategoria().equals("VAN")
                                                        && cat.getSgcategoria().equals("APO") && catUser.getSgcategoria().equals("APO")) {

                                                    list.add(listdata);

                                                } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                                        && cat.getSgcategoria().equals("AEP") && catUser.getSgcategoria().equals("AEP")
                                                        && cat.getSgcategoria().equals("VAN") && catUser.getSgcategoria().equals("VAN")) {

                                                    list.add(listdata);

                                                } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                                        && cat.getSgcategoria().equals("AEP") && catUser.getSgcategoria().equals("AEP")) {

                                                    list.add(listdata);

                                                } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                                        && cat.getSgcategoria().equals("VAN") && catUser.getSgcategoria().equals("VAN")) {

                                                    list.add(listdata);

                                                } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                                        && cat.getSgcategoria().equals("APO") && catUser.getSgcategoria().equals("APO")) {

                                                    list.add(listdata);


                                                } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")) {
                                                    list.add(listdata);
                                                } else if (cat.getSgcategoria().equals("AEP") && catUser.getSgcategoria().equals("AEP")) {
                                                    list.add(listdata);
                                                } else if (cat.getSgcategoria().equals("VAN") && catUser.getSgcategoria().equals("VAN")) {
                                                    list.add(listdata);
                                                } else if (cat.getSgcategoria().equals("APO") && catUser.getSgcategoria().equals("APO")) {
                                                    list.add(listdata);
                                                }
                                            }
                                        }
                                    }

                                    //list.add(listdata);

                                    RecyclerviewAdapter recycler = new RecyclerviewAdapter(list);
                                    RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(AutomovelActivity.this);
                                    recyclerview.setLayoutManager(layoutmanager);
                                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                                    recyclerview.setAdapter(recycler);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        txtName.setText(Common.currentUser.getApelido());


/*
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.automoveis_tbl);
        databaseReference.orderByChild("cdstatus").equalTo("ACT").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    final Automovel listdata = new Automovel();
                    AutomovelDetails automovelDetails = dataSnapshot1.getValue(AutomovelDetails.class);

                    final String cdautomovel = dataSnapshot1.getKey();

                    String cdrgb = automovelDetails.getCdrgb();
                    final String cdstatus = automovelDetails.getCdstatus();
                    String dsmarca = automovelDetails.getDsmarca();
                    String dsmodelo = automovelDetails.getDsmodelo();
                    String dsplaca = automovelDetails.getDsplaca();
                    String uidmotorista = automovelDetails.getUidmotorista();

                    listdata.setCdautomovel(cdautomovel);
                    listdata.setCdrgb(cdrgb);
                    listdata.setCdstatus(cdstatus);
                    listdata.setDsmarca(dsmarca);
                    listdata.setDsmodelo(dsmodelo);
                    listdata.setDsplaca(dsplaca);
                    listdata.setUidmotorista(uidmotorista);

                    for (Categoria categoria : automovelDetails.getCategorias()) {
                        listdata.setCategorias(automovelDetails.getCategorias());
                    }

                    if (cdstatus.equals("ACT")) {
                        listdata.setCdstatus("Activo");
                    } else if (cdstatus.equals("INA")) {
                        listdata.setCdstatus("Inactivo");
                    } else if (cdstatus.equals("MAN")) {
                        listdata.setCdstatus("Manutenção");
                    } else if (cdstatus.equals("USO") && uidmotorista.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        listdata.setCdstatus("Em uso");

                        Intent intent = new Intent(getApplicationContext(), ViagemActivity.class);

                        if (Common.currentUser != null) {
                            String utilizador = Common.currentUser.getNome();
                            String cdutilizador = Common.currentUser.getCdutilizador();
                            intent.putExtra("nomeMotorista", utilizador);
                            intent.putExtra("cdutilizador", cdutilizador);
                        }

                        intent.putExtra("key", cdautomovel);
                        intent.putExtra("marca", dsmarca);
                        intent.putExtra("modelo", dsmodelo);
                        intent.putExtra("statusAutomovel", cdstatus);
                        intent.putExtra("placa", dsplaca);


                        startActivity(intent);


                    } else {
                        listdata.setCdstatus("Em uso");

                    }

                    if(listdataUser.getCategorias() != null) {

                     for (Categoria catUser : listdataUser.getCategorias()) {
                        for (Categoria cat : listdata.getCategorias()) {

                            if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                    && cat.getSgcategoria().equals("AEP") && catUser.getSgcategoria().equals("AEP")
                                    && cat.getSgcategoria().equals("VAN") && catUser.getSgcategoria().equals("VAN")
                                    && cat.getSgcategoria().equals("APO") && catUser.getSgcategoria().equals("APO")) {

                                list.add(listdata);

                            } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                    && cat.getSgcategoria().equals("AEP") && catUser.getSgcategoria().equals("AEP")
                                    && cat.getSgcategoria().equals("VAN") && catUser.getSgcategoria().equals("VAN")) {

                                list.add(listdata);

                            } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                    && cat.getSgcategoria().equals("AEP") && catUser.getSgcategoria().equals("AEP")) {

                                list.add(listdata);

                            } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                    && cat.getSgcategoria().equals("VAN") && catUser.getSgcategoria().equals("VAN")) {

                                list.add(listdata);

                            } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")
                                    && cat.getSgcategoria().equals("APO") && catUser.getSgcategoria().equals("APO")) {

                                list.add(listdata);


                            } else if (cat.getSgcategoria().equals("GER") && catUser.getSgcategoria().equals("GER")) {
                                list.add(listdata);
                            } else if (cat.getSgcategoria().equals("AEP") && catUser.getSgcategoria().equals("AEP")) {
                                list.add(listdata);
                            } else if (cat.getSgcategoria().equals("VAN") && catUser.getSgcategoria().equals("VAN")) {
                                list.add(listdata);
                            } else if (cat.getSgcategoria().equals("APO") && catUser.getSgcategoria().equals("APO")) {
                                list.add(listdata);
                            }
                        }
                     }

                    }


                    //list.add(listdata);

                    RecyclerviewAdapter recycler = new RecyclerviewAdapter(list);
                    RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(AutomovelActivity.this);
                    recyclerview.setLayoutManager(layoutmanager);
                    recyclerview.setItemAnimator(new DefaultItemAnimator());
                    recyclerview.setAdapter(recycler);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/




        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(AutomovelActivity.this, recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onLongClick(View child, int childPosition) {

            }

            @Override
            public void onClick(View child, int childPosition) {

               /* final SpotsDialog waitingDialog = new SpotsDialog(AutomovelActivity.this);
                waitingDialog.show();
                */
                automovel = list.get(childPosition);
                automovel.setDsmodelo(automovel.getDsmodelo());

                String cdautomovel = automovel.getCdautomovel();
                String dsmarca = automovel.getDsmarca();
                String dsmodelo = automovel.getDsmodelo();
                String dsplca = automovel.getDsplaca();
                String cdstatus = automovel.getCdstatus();
                String cduidautomovel = automovel.getUidmotorista();

                Intent intent = new Intent(getApplicationContext(), ViagemActivity.class);

                if (Common.currentUser != null) {
                    String utilizador = Common.currentUser.getNome();
                    String cdutilizador = cdUtilizador;
                    intent.putExtra("nomeMotorista", utilizador);
                    intent.putExtra("cdutilizador", cdutilizador);
                    intent.putExtra("uid", cduidautomovel);
                }

                intent.putExtra("key", cdautomovel);
                intent.putExtra("marca", dsmarca);
                intent.putExtra("modelo", dsmodelo);
                intent.putExtra("placa", dsplca);
                intent.putExtra("statusAutomovel", cdstatus);

                intent.putExtra("init", init);

                if (cdstatus.equals("Em uso") || cdstatus.equals("INA") || cdstatus.equals("MAN")) {
                    Toast.makeText(getApplicationContext(), automovel.getDsmodelo() + " Este carro já está em uso !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), automovel.getDsmodelo() + " carro selecionado!", Toast.LENGTH_SHORT).show();
                    //waitingDialog.dismiss();
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                }

            }

        }));

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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_update_info) {
            startActivity(new Intent(AutomovelActivity.this, EditProfileActivity.class));
        } else if (id == R.id.nav_sing_out) {
            singOut();
        }

        //   DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //   drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void singOut() {
        Paper.init(this);
        Paper.book().destroy();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("logado").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AutomovelActivity.this, "Você está offline!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AutomovelActivity.this, "Erro ao salvar!", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(AutomovelActivity.this, MainActivity.class);
        startActivity(intent);
        //finish();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AutomovelActivity.this, MainActivity.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
