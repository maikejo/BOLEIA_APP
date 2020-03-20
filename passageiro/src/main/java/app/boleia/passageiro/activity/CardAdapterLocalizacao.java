package app.boleia.passageiro.activity;

/**
 * Created by maike.silva on 22/03/2018.
 */

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.List;

import app.boleia.passageiro.R;
import app.boleia.passageiro.helper.MyPlacesAdapter;
import app.boleia.passageiro.model.MyGooglePlaces;
import app.boleia.passageiro.model.Status;
import app.boleia.passageiro.model.ViagensRequisicao;
import de.hdodenhof.circleimageview.CircleImageView;


class CardAdapterLocalizacao extends RecyclerView.Adapter<CardAdapterLocalizacao.ViewHolder> implements View.OnClickListener {

    private Context context;
    private List<ViagensRequisicao> items;
    public DetailsAdapterListener onClickListener;

    AutoCompleteTextView places;
    MyPlacesAdapter adapter;


    public CardAdapterLocalizacao(Context context, DetailsAdapterListener detailsAdapterListener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.onClickListener = detailsAdapterListener;
    }

    public void setItems(List<ViagensRequisicao> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private Button btCancelar;
        private PlaceAutocompleteFragment place_location;
        private EditText editTextLocalAtual;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.editTextLocalAtual = (EditText) itemView.findViewById(R.id.editTextLocalAtual);

            AutoCompleteTextView autocompleteView = (AutoCompleteTextView) itemView.findViewById(R.id.autocomplete);
            autocompleteView.setAdapter(new MyPlacesAdapter(context, R.layout.autocomplete));

            autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String place = (String) parent.getItemAtPosition(position);
                    onClickListener.selectDestinoClick(place,getAdapterPosition());
                }
            });


            this.btCancelar = (Button) itemView.findViewById(R.id.btnFechar);
            this.btCancelar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   onClickListener.btCancelarClick(v, getAdapterPosition());
               }
           });


        }

        public void bind(ViagensRequisicao viagens) {
            this.editTextLocalAtual.setHint(viagens.getOrigem());
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.layout_card_localizacao, parent, false);
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

        void btCancelarClick(View v, int position);

        void selectDestinoClick(String place,int position);
    }

}
