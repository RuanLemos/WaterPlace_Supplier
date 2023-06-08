package waterplace.finalproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import waterplace.finalproj.R;
import waterplace.finalproj.model.Address;
import waterplace.finalproj.model.Supplier;
import waterplace.finalproj.util.GeocodeUtil;
import waterplace.finalproj.util.viacepUtil;

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
                filterInputs(true);
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

    public void filterInputs(boolean canregister){
        String userEmail = ((EditText)findViewById(R.id.input_email_3)).getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            // Formato de Email inválido
            EditText inputField = findViewById(R.id.input_email_3);
            TextView verificationText = findViewById(R.id.error_util);
            inputField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
            verificationText.setVisibility(View.VISIBLE);
        } else {
            // Formato de Email é válido, manda para o firebase para checar a existencia do email
            firebaseAuth.fetchSignInMethodsForEmail(userEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            // Pode registrar o email
                            String userPhone = ((EditText)findViewById(R.id.input_telefone)).getText().toString();
                            String userCNPJ = ((EditText)findViewById(R.id.input_cnpj_2)).getText().toString();

                            if(verifyPhone(userPhone) && verifyCNPJ(userCNPJ)){
                                register();
                            }
                        } else {
                            // Email já foi utilizado, mostra o texto vermelho e trocar cor de borda do campo
                            EditText inputField = findViewById(R.id.input_email_3);
                            TextView verificationText = findViewById(R.id.error_util);
                            inputField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
                            verificationText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Informação Invalida, mostra o texto vermelho e trocar cor de borda do campo
                        EditText inputField = findViewById(R.id.input_email_3);
                        TextView verificationText = findViewById(R.id.error);
                        inputField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
                        verificationText.setVisibility(View.VISIBLE);
                    }
                });
        }
    }

    public boolean verifyPhone(String phoneNumber){
        String phonePattern = "^[0-9]{2} [0-9]{9}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phoneNumber);

        if (matcher.matches()){
            return true;
        } else {
            EditText inputField = findViewById(R.id.input_telefone);
            TextView verificationText = findViewById(R.id.error_2);
            inputField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
            verificationText.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public boolean verifyCNPJ(String cnpj) {
        String cnpjPattern = "\\d{14}";
        Pattern pattern = Pattern.compile(cnpjPattern);
        Matcher matcher = pattern.matcher(cnpj);

        if(matcher.matches()){
            return true;
        } else {
            EditText inputField = findViewById(R.id.input_cnpj_2);
            TextView verificationText = findViewById(R.id.error_3);
            inputField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
            verificationText.setVisibility(View.VISIBLE);
            return false;
        }
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
                        String cep = ((android.widget.EditText)findViewById(R.id.input_cep)).getText().toString();
                        address.setCEP(Integer.parseInt(cep));

                        //int CEP = viacepUtil.CEPcheck(address.getCEP());

                        //Gera um UID para o endereço dentro do documento do fornecedor
                        //String addressUid = usersRef.child(uid).child("Address").push().getKey();

                        /*
                        double[] coords = GeocodeUtil.geocode(address.getAvenue() + " " + address.getNum());
                        if (coords != null) {
                            address.setLatitude(coords[0]);
                            address.setLongitude(coords[1]);
                        } else {
                            EditText inputField = findViewById(R.id.input_rua);
                            TextView verificationText = findViewById(R.id.error_5);
                            inputField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
                            verificationText.setVisibility(View.VISIBLE);
                        }
                         */

                        System.out.println(uid);
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
            EditText inputField = findViewById(R.id.input_password_2);
            EditText inputField2 = findViewById(R.id.input_confirm_password);
            TextView verificationText = findViewById(R.id.error_4);
            inputField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
            inputField2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
            verificationText.setVisibility(View.VISIBLE);
        }
    }
}