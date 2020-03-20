package ao.app.boleia.motorista.helper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.model.Automovel;

/**
 * Created by csa on 3/6/2017.
 */

public class RecyclerviewAdapterAutomovel extends RecyclerView.Adapter<RecyclerviewAdapterAutomovel.MyHolder>{

    List<Automovel> listdata;
    ClickListener clickListener;

    public RecyclerviewAdapterAutomovel(List<Automovel> listdata) {
        this.listdata = listdata;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_automovel_viagem_custom,parent,false);

        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    public void setClickListener(ClickListener _clickListener) {
        this.clickListener = _clickListener;
    }


    public void onBindViewHolder(final MyHolder holder, int position) {
        Automovel data = listdata.get(position);
        holder.dsmarca.setText(data.getDsmarca());
        holder.dsmodelo.setText(data.getDsmodelo());

        if(data.getCdstatus().equals("Em uso")){
            holder.ativo.setBackgroundResource(R.drawable.roun_rect_red);
        }

        holder.ativo.setText(data.getCdstatus());
        holder.dsplaca.setText(data.getDsplaca());

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
               TextView dsmarca , ativo,dsmodelo,dsplaca,txtOnline;

        public MyHolder(View itemView) {
            super(itemView);
            dsmarca = (TextView) itemView.findViewById(R.id.dsmarca);
            dsmodelo = (TextView) itemView.findViewById(R.id.dsmodelo);
            ativo = (TextView) itemView.findViewById(R.id.ativo);
            dsplaca = (TextView) itemView.findViewById(R.id.dsplaca);

        }
    }


}
