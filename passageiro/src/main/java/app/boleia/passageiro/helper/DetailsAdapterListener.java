package app.boleia.passageiro.helper;

import android.view.View;

/**
 * Created by maike.silva on 29/03/2018.
 */

public interface DetailsAdapterListener {

    void btSolicitacaoClick(View v, int position);

    void btCancelarClick(View v, int position);
}
