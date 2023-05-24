package waterplace.finalproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import waterplace.finalproj.R;
import waterplace.finalproj.model.Address;
import waterplace.finalproj.model.Supplier;

public class Register extends AppCompatActivity {

    private ImageButton btn_arrow;
    private Button btn_reg;
    private FirebaseDatabase database;
    private DatabaseReference suppliersRef;
    private FirebaseAuth firebaseAuth;
    private Supplier supplier;
    private Address address;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        suppliersRef = database.getReference("Suppliers");
        firebaseAuth = FirebaseAuth.getInstance();

        btn_arrow = (ImageButton) findViewById(R.id.back_arrow_4);
        btn_reg = (Button) findViewById(R.id.bt_reg_2);

        btn_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }
    public void goBack(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void goLogin(){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    public void register(){
        String userEmail = ((EditText)findViewById(R.id.input_email_3)).getText().toString();
        String userPassword = ((EditText)findViewById(R.id.input_password_2)).getText().toString();
        String userPassword2 = ((EditText)findViewById(R.id.input_confirm_password)).getText().toString();
        if (userPassword.equals(userPassword2)) {
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        supplier = new Supplier();
                        address = new Address();

                        supplier.setName(((android.widget.EditText)findViewById(R.id.input_nome)).getText().toString());
                        supplier.setPhone(((android.widget.EditText)findViewById(R.id.input_telefone)).getText().toString());
                        String cnpj = ((android.widget.EditText)findViewById(R.id.input_cnpj_2)).getText().toString();
                        supplier.setCnpj(Integer.parseInt(cnpj));
                        address.setAvenue(((android.widget.EditText)findViewById(R.id.input_rua)).getText().toString());
                        String num = ((android.widget.EditText)findViewById(R.id.input_num)).getText().toString();
                        address.setNum(Integer.parseInt(num));
                        address.setComp(((android.widget.EditText)findViewById(R.id.input_comp)).getText().toString());

                        //Gera um UID para o endereço dentro do documento do fornecedor
                        //String addressUid = usersRef.child(uid).child("Address").push().getKey();

                        // Salva o fornecedor com o UID como identificador do documento
                        suppliersRef.child(uid).setValue(supplier)
                                .addOnCompleteListener(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        suppliersRef.child(uid).child("Address").setValue(address);
                                        Toast.makeText(Register.this, "Cadastrado realizado com sucesso", Toast.LENGTH_SHORT).show();
                                        goLogin();
                                    } else {
                                        Exception e = saveTask.getException();
                                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    // Ocorreu um erro ao criar o usuário
                    Exception e = task.getException();
                    Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Register.this, "As senhas não são idênticas", Toast.LENGTH_SHORT).show();
        }
    }
}