package app.boleia.passageiro.activity;

/**
 * Created by maike.silva on 22/03/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.boleia.passageiro.R;
import app.boleia.passageiro.model.Viagens;
import app.boleia.passageiro.model.ViagensRequisicao;


class CardAdapterSolicitacao extends RecyclerView.Adapter<CardAdapterSolicitacao.ViewHolder> implements View.OnClickListener {

    private Context context;
    private List<ViagensRequisicao> items;
    public DetailsAdapterListener onClickListener;


    public CardAdapterSolicitacao(Context context, DetailsAdapterListener detailsAdapterListener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.onClickListener = detailsAdapterListener;
    }

    public void setItems(List<ViagensRequisicao> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMarcaModelo,txtnomeMotorista,txtTempo,txtOrigem,txtDestino;
        private Button btsolicitarVan,btCancelar;
        private ImageView imageViewOrigemDest;

        public ViewHolder(View itemView) {
            super(itemView);
            this.btsolicitarVan = (Button) itemView.findViewById(R.id.btnPickupRequest2);
            this.btCancelar = (Button) itemView.findViewById(R.id.btCancelar);
            this.txtMarcaModelo = (TextView)itemView.findViewById(R.id.txtMarcaModelo);
            this.txtnomeMotorista = (TextView)itemView.findViewById(R.id.txtNomeMotorista);
            this.txtTempo = (TextView) itemView.findViewById(R.id.txtTempo);
            this.txtOrigem = (TextView) itemView.findViewById(R.id.txtOrigem);
            this.txtDestino = (TextView) itemView.findViewById(R.id.txtDestino);
            //this.imageViewOrigemDest = (ImageView) itemView.findViewById(R.id.imageViewOrigemDest);

            btsolicitarVan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.btSolicitacaoClick(v, getAdapterPosition());
                    btsolicitarVan.setText("Aguardando motorista aceitar...");
                    btsolicitarVan.setEnabled(false);
                }
            });

            btCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.btCancelarClick(v, getAdapterPosition());

                }
            });

        }

        public void bind(ViagensRequisicao viagens) {
            this.txtMarcaModelo.setText(viagens.getMarcaModelo());
            this.btsolicitarVan.setText(viagens.getStatusViagem());
            this.txtnomeMotorista.setText(viagens.getNomeMotorista());

            this.txtTempo.setText(viagens.getTempo());
            this.txtOrigem.setText("Origem: "+viagens.getOrigem());
            this.txtDestino.setText("Destino: "+viagens.getDestino());

            if(viagens.getStViagens() != null) {
                if (viagens.getStViagens().equals("CRI")) {
                    this.btsolicitarVan.setText("Aguardando motorista aceitar...");
                } else if (viagens.getStViagens().equals("ACE")) {
                    this.btsolicitarVan.setText("Viagem foi aceita , aguarde...");
                    int color = Color.parseColor("#3B4CA2");
                    this.btsolicitarVan.setBackgroundColor(color);
                    this.btsolicitarVan.setEnabled(false);
                } else if (viagens.getStViagens().equals("INI")) {
                    this.btsolicitarVan.setText("Viagem iniciada ao destino");
                    int color = Color.parseColor("#f816a463");
                    this.btsolicitarVan.setBackgroundColor(color);
                    this.btsolicitarVan.setEnabled(false);
                } else if (viagens.getStViagens().equals("CON")) {
                    this.btsolicitarVan.setText("Viagem foi concluida!");
                    int color = Color.parseColor("#83838c");
                    this.btsolicitarVan.setBackgroundColor(color);
                    this.btsolicitarVan.setEnabled(false);
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.layout_card_solicitacao, parent, false);
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

    //region Interface Details listener
    public interface DetailsAdapterListener {

        void btSolicitacaoClick(View v, int position);

        void btCancelarClick(View v, int position);
    }

}
