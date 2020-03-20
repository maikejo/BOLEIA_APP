package app.boleia.passageiro.activity;

/**
 * Created by maike.silva on 22/03/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.boleia.passageiro.R;
import app.boleia.passageiro.common.Common;
import app.boleia.passageiro.model.Automovel;
import app.boleia.passageiro.model.Passageiros;
import app.boleia.passageiro.model.Viagens;
import de.hdodenhof.circleimageview.CircleImageView;

class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Context context;
    private List<Automovel> items;
    List<Viagens> listViagens;


    public CardAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public void setItems(List<Automovel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMotorista;
        private TextView txtModelo,txtPlaca;
        private TextView txtStatus;
        private TextView btSolicitacoes;
        private CircleImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.txtMotorista = (TextView)itemView.findViewById(R.id.text_title);
            this.txtStatus = (TextView)itemView.findViewById(R.id.text_staus);
            this.txtPlaca = (TextView)itemView.findViewById(R.id.text_placa);
            this.btSolicitacoes = (Button)itemView.findViewById(R.id.btSolicitacoes);
            this.imgView = (CircleImageView) itemView.findViewById(R.id.profile_motorista);
        }

        public void bind(final Automovel automovel) {
            //this.txtMotorista.setText(automovel.getDsmarca());
            this.txtStatus.setText(automovel.getCdstatus());
            this.txtPlaca.setText(automovel.getDsplaca());


            DatabaseReference databaseReferenceViagem = FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
            databaseReferenceViagem.orderByChild("cdautomovel").equalTo(automovel.getCdautomovel()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listViagens = new ArrayList<>();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (dataSnapshot1.child("cdautomovel").getValue().equals(automovel.getCdautomovel()) && dataSnapshot1.child("stviagem").getValue().equals("ACE")) {

                            Viagens viagensDetails = dataSnapshot1.getValue(Viagens.class);

                            listViagens.add(viagensDetails);
                            automovel.setQtdSolicitacoes(listViagens.size());

                        }
                    }

                    if(listViagens.size() == 0){
                        btSolicitacoes.setText("0");
                    }else{
                        btSolicitacoes.setText(Integer.toString(automovel.getQtdSolicitacoes()) );
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(Common.utilizador_tbl);
            databaseReferenceUser.orderByChild("uid").equalTo(automovel.getUidmotorista()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (dataSnapshot1.child("uid").getValue().equals(automovel.getUidmotorista())) {

                            Passageiros passageirosDetails = dataSnapshot1.getValue(Passageiros.class);

                            if (passageirosDetails.getAvatarUrl() != null && !TextUtils.isEmpty(passageirosDetails.getAvatarUrl())) {
                                Picasso.with(context)
                                        .load(passageirosDetails.getAvatarUrl())
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(imgView);


                            }else{
                                imgView.setImageResource(R.drawable.user_placeholder);
                            }

                            txtMotorista.setText(passageirosDetails.getNome());

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.layout_card_automovel, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(this.items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
