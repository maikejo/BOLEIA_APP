package ao.app.boleia.motorista.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.model.Automovel;
import ao.app.boleia.motorista.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by csa on 3/6/2017.
 */

public class RecyclerviewAdapterUser extends RecyclerView.Adapter<RecyclerviewAdapterUser.MyHolder>{

    List<User> listdata;
    ClickListener clickListener;
    private Context context;
    private StorageReference storageRef;

    public RecyclerviewAdapterUser(Context context,List<User> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_mainctivity2_custom,parent,false);

        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    public void setClickListener(ClickListener _clickListener) {
        this.clickListener = _clickListener;
    }


    public void onBindViewHolder(final MyHolder holder, int position) {
        final User data = listdata.get(position);

       if (data.getAvatarUrl() != null && !TextUtils.isEmpty(data.getAvatarUrl())) {
            Picasso.with(context)
                    .load(data.getAvatarUrl())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.imgView);


        }

        if (data.isLogado()){
           holder.txtStatusMotorista.setVisibility(View.VISIBLE);
        }else{
            holder.txtStatusMotorista.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        CircleImageView imgView;
        TextView txtStatusMotorista;

        public MyHolder(View itemView) {
            super(itemView);
            imgView = (CircleImageView) itemView.findViewById(R.id.profile_imageMain2);
            txtStatusMotorista = (TextView) itemView.findViewById(R.id.txtStatusMotorista);

        }
    }


}
