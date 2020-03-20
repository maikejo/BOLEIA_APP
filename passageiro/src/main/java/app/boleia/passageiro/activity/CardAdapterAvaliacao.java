package app.boleia.passageiro.activity;

/**
 * Created by maike.silva on 22/03/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.boleia.passageiro.R;
import app.boleia.passageiro.model.ViagensRequisicao;


class CardAdapterAvaliacao extends RecyclerView.Adapter<CardAdapterAvaliacao.ViewHolder> implements View.OnClickListener {

    private Context context;
    private List<ViagensRequisicao> items;
    public DetailsAdapterListener onClickListener;


    public CardAdapterAvaliacao(Context context, DetailsAdapterListener detailsAdapterListener) {
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
        private TextView txtMarcaModelo,txtnomeMotorista,txtTempo;
        private Button btFinalizarBoleia,btCancelar;
        private RatingBar ratingBar;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.btFinalizarBoleia = (Button) itemView.findViewById(R.id.btnFinalizarBoleia);
            this.btCancelar = (Button) itemView.findViewById(R.id.btAvaliarBoleia);
            this.txtMarcaModelo = (TextView)itemView.findViewById(R.id.txtMarcaModeloAvaliacao);
            this.txtnomeMotorista = (TextView)itemView.findViewById(R.id.txtNomeMotoristaAvaliacao);
            this.txtTempo = (TextView) itemView.findViewById(R.id.txtTempoAvaliacao);
            this.ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

           final float rage = ratingBar.getRating();;

            btFinalizarBoleia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  ratingBar = itemView.findViewById(R.id.ratingBar);

                    float rage = ratingBar.getRating();

                    onClickListener.btFinalizarBoleiaClick(v, getAdapterPosition(),rage);
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
            this.txtnomeMotorista.setText(viagens.getNomeMotorista());
            this.ratingBar.getRating();
            this.txtTempo.setText(viagens.getTempo().toString());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.layout_card_avaliacao, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViagensRequisicao data = items.get(position);

        holder.bind(data);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //region Interface Details listener
    public interface DetailsAdapterListener {

        void btFinalizarBoleiaClick(View v, int position, float rage);

        void btCancelarClick(View v, int position);
    }

}
