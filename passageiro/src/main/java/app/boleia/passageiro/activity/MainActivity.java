package app.boleia.passageiro.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import app.boleia.passageiro.R;
import app.boleia.passageiro.common.Common;
import app.boleia.passageiro.model.Passageiros;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout rootLayout;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    Button btnLogar;
    Button btnRegistrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init PaperDB
        Paper.init(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.utilizador_tbl);

        //Inicia View
        btnLogar = (Button) findViewById(R.id.btnLogar);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        rootLayout = (ConstraintLayout) findViewById(R.id.rootLayout);

        //Eventos
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });
        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        //Auto Login
        String user = Paper.book().read(Common.user_field);
        String pwd = Paper.book().read(Common.pwd_field);

        if (user != null && pwd != null) {
            if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pwd)) {
                autoLogin(user, pwd);
                //singOut();
            }
        }

    }

    private void singOut() {
        Paper.init(this);
        Paper.book().destroy();

        FirebaseAuth.getInstance().signOut();
        // Intent intent = new Intent(DriverHome.this, MainActivity.class);
        // startActivity(intent);
        finish();
    }


    private void autoLogin(String user, String pwd) {

        final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
        waitingDialog.show();


        //LOGAR
        auth.signInWithEmailAndPassword(user, pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();

                        FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                            if (dataSnapshot1.child("uid").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && dataSnapshot1.child("cdtipo").getValue().equals("PSG") && dataSnapshot1.child("cdstatus").getValue().equals("ACT")) {

                                                Common.currentUser = dataSnapshot1.getValue(Passageiros.class);
                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                finish();

                                            }else{
                                                Snackbar.make(rootLayout, "Seu usuário não está habilitado, entre em contato com a Logística.", Snackbar.LENGTH_SHORT).show();
                                                btnLogar.setEnabled(true);
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
                waitingDialog.dismiss();
                Snackbar.make(rootLayout, "Não foi possivel logar, verifique se está conectado a internet.", Snackbar.LENGTH_SHORT).setDuration(10000).show();

                btnLogar.setEnabled(true);

                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            }
        });

    }

    private void showLoginDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("LOGAR");
        dialog.setMessage("Por favor use o email para registrar.");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText edtEmail = login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPassword);

        dialog.setView(login_layout);

        //Insere Botao Registro e Cancelar
        dialog.setPositiveButton("LOGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                btnLogar.setEnabled(true);

                //Validar Campos
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Inserir o email", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Inserir a senha", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, " A senha é menor que 6 caracteres", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();


                //LOGAR
                auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();

                                FirebaseDatabase.getInstance().getReference(Common.utilizador_tbl)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                    if (dataSnapshot1.child("uid").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && dataSnapshot1.child("cdtipo").getValue().equals("PSG") && dataSnapshot1.child("cdstatus").getValue().equals("ACT")) {

                                                        Common.currentUser = dataSnapshot1.getValue(Passageiros.class);

                                                        //Save value
                                                        Paper.book().write(Common.user_field, edtEmail.getText().toString());
                                                        Paper.book().write(Common.pwd_field, edtPassword.getText().toString());
                                                        btnLogar.setEnabled(true);

                                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                        finish();

                                                    }else{
                                                        Snackbar.make(rootLayout, "Seu usuário não está habilitado, entre em contato com a Logística.", Snackbar.LENGTH_SHORT).setDuration(10000).show();
                                                        btnLogar.setEnabled(true);
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
                        waitingDialog.dismiss();
                        Snackbar.make(rootLayout, "Não foi possivel logar, verifique se está conectado a internet e tente novamente.", Snackbar.LENGTH_SHORT).setDuration(10000).show();
                    }
                });

                dialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        dialog.show();

    }

    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTRO");
        dialog.setMessage("Por favor use o email para registrar.");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_registro, null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtNome = register_layout.findViewById(R.id.edtNome);
        final MaterialEditText edtTelefone = register_layout.findViewById(R.id.edtTelefone);

        dialog.setView(register_layout);

        //Insere Botao Registro e Cancelar
        dialog.setPositiveButton("REGISTRO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                //Validar Campos
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Inserir o email", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Inserir a senha", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, " A senha é menor que 6 caracteres", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtNome.getText().toString())) {
                    Snackbar.make(rootLayout, "Inserir o nome", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtTelefone.getText().toString())) {
                    Snackbar.make(rootLayout, "Inserir o telefone", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //Novo Registro
                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Passageiros user = new Passageiros();
                        user.setEmail(edtEmail.getText().toString());
                        user.setNome(edtNome.getText().toString());
                        user.setNutelefone(edtTelefone.getText().toString());
                        user.setCdtipo("PSG");
                        user.setCdstatus("INA");
                        user.setApelido("");
                        user.setIdade(new Double(0));
                        user.setLatitude(new Double(0));
                        user.setLongitude(new Double(0));
                        user.setAvatarUrl("");
                        user.setCdautomovel("");
                        user.setPlantao(false);
                        user.setLogado(false);

                        String idUtilizador = users.push().getKey();
                        user.setCdutilizador(idUtilizador);

                        user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        users.child(idUtilizador).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(rootLayout, "Cadastro realizado com sucesso.", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Erro ao cadastrar, verifique se está conectado na internet e tente novamente", Snackbar.LENGTH_SHORT).setDuration(10000).show();
                                return;
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Erro ao cadastrar, verifique se está conectado na internet e tente novamente.", Snackbar.LENGTH_SHORT).setDuration(10000).show();
                        return;
                    }

                });

            }
        });

        dialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

}


