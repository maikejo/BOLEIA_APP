package app.boleia.passageiro.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.boleia.passageiro.R;
import app.boleia.passageiro.common.Common;
import app.boleia.passageiro.model.Viagens;

public class AvaliacaoActivity extends AppCompatActivity {

    String idViagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            idViagem = bundle.getString("idViagem");
        }


        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button button = (Button) findViewById(R.id.buttonGetRatingBarResult);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final float result = ratingBar.getRating();


                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Common.viagens_tbl);
                databaseReference.child(idViagem).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       // Viagens bdviagens = dataSnapshot.getValue(Viagens.class);


                        databaseReference.child(idViagem).child("avaliacao").setValue(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(AvaliacaoActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
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

            ;
        });
    }

}