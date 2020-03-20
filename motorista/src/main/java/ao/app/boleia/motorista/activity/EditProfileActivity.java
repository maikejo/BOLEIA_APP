package ao.app.boleia.motorista.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ao.app.boleia.motorista.BuildConfig;
import ao.app.boleia.motorista.R;
import ao.app.boleia.motorista.common.Common;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;


public class EditProfileActivity extends AppCompatActivity {

    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    private String avatarUrl;
    private ImageView imageUpload2;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());

      /*  Bugfender.init(this, "fGscf6jKP9rqWP5PsiSMO0SdnxNqNV6B", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableCrashReporting();
        Bugfender.getDeviceIdentifier();*/

        setContentView(R.layout.activity_edit_profile);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final MaterialEditText edtNome = (MaterialEditText) findViewById(R.id.edtNome);
        final MaterialEditText edtTelefone = (MaterialEditText) findViewById(R.id.edtTelefone);
        final ImageView imageUpload = (ImageView) findViewById(R.id.image_upload);
        //imageUpload2 = (ImageView) findViewById(R.id.image_upload);
        imageUpload2 = (CircleImageView) findViewById(R.id.image_upload);
        final Button btSalvar = (Button) findViewById(R.id.btnSalvar);
        final Button btCancelar = (Button) findViewById(R.id.btnCancelar);

        edtNome.setText(Common.currentUser.getNome());
        edtTelefone.setText(Common.currentUser.getNutelefone());


        final DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
        driverInformation.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot issue : dataSnapshot.getChildren()) {
                            avatarUrl = issue.child("avatarUrl").getValue().toString();

                            if (avatarUrl != null && !avatarUrl.equals("")) {
                                Picasso.with(EditProfileActivity.this)
                                        .load(avatarUrl)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(imageUpload2);

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseImage();
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = edtNome.getText().toString();
                String telefone = edtTelefone.getText().toString();

                final Map<String,Object> updateInfo = new HashMap<>();
                if(!TextUtils.isEmpty(nome)){
                    updateInfo.put("nome",nome);
                }
                if(!TextUtils.isEmpty(telefone)){
                    updateInfo.put("telefone",telefone);
                }

                final DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
                driverInformation.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    String key = issue.getKey();

                                    driverInformation.child(key).child("nome").setValue(updateInfo.get("nome")).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EditProfileActivity.this,"Atualizado com sucesso!",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditProfileActivity.this,"Erro ao salvar!",Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    driverInformation.child(key).child("nutelefone").setValue(updateInfo.get("telefone")).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                Intent intent = new Intent(getApplicationContext(), ViagemActivity.class);
                                intent.putExtra("avatarUrl", Common.currentUser.getAvatarUrl());
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        });

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri saveUri = data.getData();
            if (saveUri != null) {

                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Carregando Imagem...");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("images/" + imageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();
                                Toast.makeText(EditProfileActivity.this,"Uploade Realizado!",Toast.LENGTH_SHORT).show();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final Map<String,Object> avatarUpdate = new HashMap<>();
                                        avatarUpdate.put("avatarUrl",uri.toString());


                                        final DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
                                        driverInformation.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        for (final DataSnapshot issue : dataSnapshot.getChildren()) {
                                                            String key = issue.getKey();

                                                            driverInformation.child(key).child("avatarUrl").setValue(avatarUpdate.get("avatarUrl")).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(EditProfileActivity.this,"Upload realizado com sucesso!",Toast.LENGTH_SHORT).show();
                                                                    Common.currentUser.setAvatarUrl(issue.child("avatarUrl").getValue().toString());

                                                                    final DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl);
                                                                    driverInformation.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    for (final DataSnapshot issue : dataSnapshot.getChildren()) {
                                                                                        avatarUrl = issue.child("avatarUrl").getValue().toString();

                                                                                        if (avatarUrl != null && !avatarUrl.equals("")) {
                                                                                            Picasso.with(EditProfileActivity.this)
                                                                                                    .load(avatarUrl)
                                                                                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                                                                                    .into(imageUpload2);

                                                                                        }

                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(EditProfileActivity.this,"Erro ao realizar Uploade!",Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("Upload "+progress+"%");
                            }
                        });
            }
        }
    }

    private void choseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione a imagem"), Common.PICK_IMAGE_REQUEST);

    }


}
