package ao.app.boleia.motorista.helper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
import ao.app.boleia.motorista.activity.DriverHome;
import ao.app.boleia.motorista.activity.ViagemActivity;
import ao.app.boleia.motorista.common.Common;
import ao.app.boleia.motorista.model.Automovel;
import ao.app.boleia.motorista.model.User;
import ao.app.boleia.motorista.model.Viagens;
import ao.app.boleia.motorista.service.MyLocationService;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by maike.silva
 */

public class RecyclerviewAdapterViagem extends RecyclerView.Adapter<RecyclerviewAdapterViagem.MyHolder> implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    List<Viagens> listdata;
    ClickListener clickListener;
    Marker markerOrigemUtilizador;
    Marker markerOrigemUtilizadorDestino;
    String customerId;
    LocationManager mLocationManager;
    private Context context;
    private GoogleMap mMap;
    private GoogleMap mMap2;
    private Marker marker;
    private Automovel automovel;
    private User utilizador;
    private  int count = -1;
    private String cdUtilizador;

    public RecyclerviewAdapterViagem(Context context, List<Viagens> listdata, Double lat, Double longt) {
        this.listdata = listdata;
        this.context = context;
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

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_viagem_custom, parent, false);

        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    public void setClickListener(ClickListener _clickListener) {
        this.clickListener = _clickListener;
    }

    public void onBindViewHolder(final MyHolder holder, final int position) {
        final Viagens data = listdata.get(position);

        if (data.getStviagem().equals("CRI")) {
           cdUtilizador = data.getCdutilizador();
        }

        if (data.getStviagem().equals("CON")) {
            holder.cardView.setVisibility(View.GONE);

        }
        if (data.getStviagem().equals("CAN")) {
            holder.cardView.setVisibility(View.GONE);
        }

        if (data.getAvatarUrl() != null && !TextUtils.isEmpty(data.getAvatarUrl())) {
            Picasso.with(context)
                    .load(data.getAvatarUrl())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.imgView);
        }

        //holder.stviagem.setText(data.getStviagem());

        Date date = new Date(data.getDthrcriado());
        String dataFormatada = DateFormat.format("dd/MM/yyyy hh:mm:ss", date).toString();

        holder.dtCriacao.setText("Data Viagem: " + dataFormatada);
        holder.cdutilizador.setText(data.getNomeUtilizador());
        holder.qtdpassageiros.setText(data.getQtdpassageiros().toString());

        if (!data.getStviagem().equals("CAN")) {
            holder.mapView.onCreate(null);
            holder.mapView.getMapAsync(this);
            holder.mapView.onResume();

            //loadUsuarioTraking(cdUtilizador);
        }



        holder.origemConsultor.setText("DE: " + data.getRotas().getDsorigem().toUpperCase());
        holder.destinoConsultor.setText("PARA: " + data.getRotas().getDsdestino().toUpperCase());
        data.getRotas().getLatitude_a();
        data.getRotas().getLongitude_a();


        if (data.getStviagem().equals("ACE")) {
            int color = Color.parseColor("#ffffff");
            holder.constraintLayoutViagem.setBackgroundColor(color);
            holder.stviagem.setText("A ESPERA DA VAN");
            int colorOrigem = Color.parseColor("#0B71C7");
            int colorDestino = Color.parseColor("#519839");
            int color2 = Color.parseColor("#000000");
            holder.cdutilizador.setTextColor(color2);
            holder.origemConsultor.setTextColor(colorOrigem);
            holder.destinoConsultor.setTextColor(colorDestino);
            holder.btAceitarViagem.setVisibility(View.INVISIBLE);
            holder.btIniciarViagem.setVisibility(View.VISIBLE);

        } else if (data.getStviagem().equals("INI")) {
            int color = Color.parseColor("#ffffff");
            holder.constraintLayoutViagem.setBackgroundColor(color);
            holder.stviagem.setText("EM VIAGEM");
            int color2 = Color.parseColor("#9013FE");
            holder.stviagem.setTextColor(color2);
            int color3 = Color.parseColor("#000000");
            holder.cdutilizador.setTextColor(color3);

            holder.origemConsultor.setTextColor(color3);
            holder.destinoConsultor.setTextColor(color3);
            holder.btIniciarViagem.setVisibility(View.INVISIBLE);
            holder.btConcluirViagem.setVisibility(View.VISIBLE);
        } else if (data.getStviagem().equals("CON")) {
            holder.stviagem.setText("A CONCLUIR VIAGEM");
            int color = Color.parseColor("#D45A00");
            holder.stviagem.setTextColor(color);

        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.cardViewMap.getVisibility() == View.GONE) {
                    holder.cardView.setLayoutParams(new ConstraintLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT, 1100));
                    holder.cardViewMap.setVisibility(View.VISIBLE);

                } else {
                    holder.cardView.setLayoutParams(new ConstraintLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT, 530));
                    holder.cardViewMap.setVisibility(View.GONE);

                }
            }
        });

        holder.btAceitarViagem.setVisibility(View.VISIBLE);

        holder.btAceitarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = Color.parseColor("#ffffff");
                holder.constraintLayoutViagem.setBackgroundColor(color);
                holder.stviagem.setText("A ESPERA DA VAN");
                int color2 = Color.parseColor("#000000");
                holder.cdutilizador.setTextColor(color2);
                holder.origemConsultor.setTextColor(color2);
                holder.destinoConsultor.setTextColor(color2);
                holder.btAceitarViagem.setVisibility(View.INVISIBLE);
                holder.btIniciarViagem.setVisibility(View.VISIBLE);
                atualizaViagem(data.getCdviagem(), "ACE");
                atualizaDataAceita(data.getCdviagem());
            }
        });

        holder.btIniciarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.stviagem.setText("EM VIAGEM");
                int color = Color.parseColor("#9013FE");
                holder.stviagem.setTextColor(color);
                holder.btIniciarViagem.setVisibility(View.INVISIBLE);
                holder.btConcluirViagem.setVisibility(View.VISIBLE);
                atualizaViagem(data.getCdviagem(), "INI");
                atualizaDataInicio(data.getCdviagem());
            }
        });

        holder.btConcluirViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.stviagem.setText("A CONCLUIR VIAGEM");
                int color = Color.parseColor("#D45A00");
                holder.stviagem.setTextColor(color);
                atualizaViagem(data.getCdviagem(), "CON");
                atualizaDataConcluida(data.getCdviagem());
                holder.cardView.setVisibility(View.GONE);
            }
        });


       holder.imageViewZoomMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*holder.getCardViewZoomMap.setLayoutParams(new ConstraintLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT, 3100));
                holder.getCardViewZoomMap.setVisibility(View.VISIBLE);*/

                Intent intent = new Intent(context, DriverHome.class);
                intent.putExtra("lat_a", data.getRotas().getLatitude_a());
                intent.putExtra("log_a", data.getRotas().getLongitude_a());
                intent.putExtra("lat_b", data.getRotas().getLatitude_b());
                intent.putExtra("log_b", data.getRotas().getLongitude_b());
                intent.putExtra("key", data.getCdautomovel());
                intent.putExtra("idViagem", data.getCdviagem());
                intent.putExtra("cdutilizador", data.getCdutilizador());

                context.startActivity(intent);

            }
        });
    }

    public void atualizaViagem(final String cdViagem, final String tpViagem) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
        databaseReference.child(cdViagem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                databaseReference.child(cdViagem).child("stviagem").setValue(tpViagem).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void atualizaDataAceita(final String cdViagem) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
        databaseReference.child(cdViagem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Date date = new Date();

                databaseReference.child(cdViagem).child("dthraceita").setValue(date.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // r.stop();
                        return;
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

    public void atualizaDataInicio(final String cdViagem) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
        databaseReference.child(cdViagem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Date date = new Date();

                databaseReference.child(cdViagem).child("dthrinicio").setValue(date.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void atualizaDataConcluida(final String cdViagem) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
        databaseReference.child(cdViagem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Date date = new Date();

                databaseReference.child(cdViagem).child("dthrfim").setValue(date.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // r.stop();
                        return;
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

    public void loadAutomoveisTraking() {

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.automoveis_tbl);
        driverLocation.child("-L8D0duAnWZr9DBt60p_").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                automovel = dataSnapshot.getValue(Automovel.class);

                if (automovel.getCdstatus().equals("ACT") || automovel.getCdstatus().equals("USO")) {

                    if (marker == null) {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(automovel.getLatitude(), automovel.getLongitude()))
                                .flat(true)
                                .title("Automovel: " + automovel.getDsmarca() + "/" + automovel.getDsmodelo())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_vanjupiter)));
                    } else {
                        animateMarker(mMap, marker, new LatLng(automovel.getLatitude(), automovel.getLongitude()), false);
                    }

                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker arg0) {

                        Log.d("mGoogleMap1", "Activity_Calling");
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void loadUsuarioTraking(String cdUtilizador) {

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
        driverLocation.child(cdUtilizador).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                utilizador = dataSnapshot.getValue(User.class);

                    if (marker == null) {
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(utilizador.getLatitude(), utilizador.getLongitude()))
                                .flat(true)
                                .title("Consultor: " + utilizador.getNome())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_consultor)));
                    } else {
                        animateMarker(mMap, marker, new LatLng(utilizador.getLatitude(), utilizador.getLongitude()), false);
                    }



                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker arg0) {

                        Log.d("mGoogleMap1", "Activity_Calling");
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        Marker markerGeneric = null;

        if (mMap != null) {
            count++;
            desenhaRotaUtilizador(listdata.get(count), markerGeneric);
        }
    }

    private void desenhaRotaUtilizador(Viagens listaViagem, Marker marker) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(10);

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        Location location = bestLocation;


        // Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
            Double latitude = location.getLatitude(), longitude = location.getLongitude();
            addLinesMotoristaUtilizador(latitude, longitude, listaViagem.getRotas().getLatitude_a(), listaViagem.getRotas().getLongitude_a(), listaViagem.getRotas().getLatitude_b(), listaViagem.getRotas().getLongitude_b());
        }


      /*
        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Motorista")
                .icon(bitmapDescriptorFromVector(context, R.mipmap.ic_vanjupiter)));
       */


    }

    private void addLinesMotoristaUtilizador(Double lat, Double longi, Double latUtilizador_a, Double longUtilizador_a, Double latUtilizador_b, Double longUtilizador_b) {

        LatLng origemMotorista = new LatLng(lat, longi);
        LatLng origemUtilizador = new LatLng(latUtilizador_a, longUtilizador_a);
        LatLng origemUtilizadorDestino = new LatLng(latUtilizador_b, longUtilizador_b);

        markerOrigemUtilizador = mMap.addMarker(new MarkerOptions()
                .position(origemUtilizador)
                .title("Consultor")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_consultor)));


        markerOrigemUtilizadorDestino = mMap.addMarker(new MarkerOptions()
                .position(origemUtilizadorDestino)
                .title("Destino")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


        //ROTA AUTOMOVEL TO UTILIZADOR
        String url = getDirectionsUrl(origemMotorista, origemUtilizador);
        DownloadTaskOrigemConsultor downloadTask = new DownloadTaskOrigemConsultor();
        downloadTask.execute(url);


        //ROTA AUTOMOVEL TO UTILIZADOR DESTINO
        String url2 = getDirectionsUrl(origemUtilizador, origemUtilizadorDestino);
        DownloadTask downloadTask2 = new DownloadTask();
        downloadTask2.execute(url2);


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

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=AIzaSyBKPDetQSaNHepf3xa9PPSsBAP1hlwQb-U";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    @Override
    public void onLocationChanged(Location location) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        LatLng loc = new LatLng(latitude, longitude);

        if (marker != null) {
            marker.remove();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 13.0f));

       /* marker=  mMap.addMarker(new MarkerOptions().position(loc).title("Sparx IT Solutions"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));*/

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView stviagem, dtCriacao, cdutilizador, destinoConsultor, origemConsultor;
        CircleImageView imgView;
        MapView mapView;
        Button qtdpassageiros, btAceitarViagem, btIniciarViagem, btConcluirViagem;
        CardView cardViewMap, cardView, getCardViewZoomMap;
        ConstraintLayout constraintLayoutViagem;
        ImageView imageViewZoomMap;

        public MyHolder(View itemView) {
            super(itemView);
            stviagem = (TextView) itemView.findViewById(R.id.dsstatus);
            dtCriacao = (TextView) itemView.findViewById(R.id.dtCriacao);
            cdutilizador = (TextView) itemView.findViewById(R.id.dsnome);
            qtdpassageiros = (Button) itemView.findViewById(R.id.dsqtdPassageiro);
            imgView = (CircleImageView) itemView.findViewById(R.id.profile_image_viagem);
            destinoConsultor = (TextView) itemView.findViewById(R.id.destinoUtilizador);
            origemConsultor = (TextView) itemView.findViewById(R.id.origemUtilizador);

            mapView = (MapView) itemView.findViewById(R.id.mapViagem);
            cardViewMap = (CardView) itemView.findViewById(R.id.cardViewMap);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            getCardViewZoomMap = (CardView) itemView.findViewById(R.id.cardViewMapZooMap);

            btAceitarViagem = (Button) itemView.findViewById(R.id.btAceitarViagem);
            btIniciarViagem = (Button) itemView.findViewById(R.id.btIniciarViagem);
            btConcluirViagem = (Button) itemView.findViewById(R.id.btConcluirViagem);
            constraintLayoutViagem = (ConstraintLayout) itemView.findViewById(R.id.constrainLayoutViagem);
            imageViewZoomMap = (ImageView) itemView.findViewById(R.id.imageViewZoomMap);
        }

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

        @RequiresApi(api = Build.VERSION_CODES.M)
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
                lineOptions.color(context.getColor(R.color.lineOptions));
                lineOptions.geodesic(true);
            }

            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }


    private class DownloadTaskOrigemConsultor extends AsyncTask<String, Void, String> {

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
            ParserTaskOrigemConsultor parserTask = new ParserTaskOrigemConsultor();
            parserTask.execute(result);
        }
    }

    private class ParserTaskOrigemConsultor extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

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

        @RequiresApi(api = Build.VERSION_CODES.M)
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
                lineOptions.color(context.getColor(R.color.cardview_dark_background));
                lineOptions.geodesic(true);
            }

            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }

}
