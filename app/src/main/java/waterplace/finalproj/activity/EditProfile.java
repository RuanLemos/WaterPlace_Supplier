package waterplace.finalproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import waterplace.finalproj.R;
import waterplace.finalproj.model.Address;
import waterplace.finalproj.model.Supplier;
import waterplace.finalproj.util.AddressUtil;
import waterplace.finalproj.util.BottomNavigationManager;

public class EditProfile extends AppCompatActivity {
    private BottomNavigationManager bottomNavigationManager;
    private Supplier user = Supplier.getInstance();
    private Address address = user.getAddress();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String uid = firebaseUser.getUid();
    private EditText name;
    private EditText phone;
    private EditText cnpj;
    private EditText rua;
    private EditText numero;
    private EditText cep;
    private String telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);

        name = findViewById(R.id.input_nome_2);
        phone = findViewById(R.id.input_telefone_2);
        cnpj = findViewById(R.id.input_cnpj_3);
        rua = findViewById(R.id.input_rua_2);
        numero = findViewById(R.id.input_num_2);
        cep = findViewById(R.id.input_cep_2);

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_footer);
        bottomNavigationView.setSelectedItemId(R.id.action_option3);
        bottomNavigationManager = new BottomNavigationManager(this);
        bottomNavigationManager.setupBottomNavigation(bottomNavigationView);

        setCampos();
    }
    public void setCampos() {
        name.setText(user.getName());
        phone.setText(user.getPhone());
        cnpj.setText(user.getCnpj().toString());
        rua.setText(address.getAvenue());
        numero.setText(Integer.toString(address.getNum()));
        cep.setText(Integer.toString(address.getCep()));



        Button btnSalvar = findViewById(R.id.bt_edit);
        btnSalvar.setOnClickListener(v -> {
            telefone = phone.getText().toString();
            System.out.println(telefone);
            if (verifyPhone(telefone)){
                pushEdit();
            }
        });
    }
    @Override
    public void onBackPressed() {
        //Desabilita a seta
    }

    public boolean verifyPhone(String phoneNumber){
        String phonePattern = "^[0-9]{2} [0-9]{9}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phoneNumber);

        if (matcher.matches()){
            return true;
        } else {
            showError(findViewById(R.id.input_telefone_2), findViewById(R.id.error_2));

            return false;
        }
    }

    public void showError(EditText inputField, TextView verificationText) {
        inputField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9494")));
        verificationText.setVisibility(View.VISIBLE);
    }
    private void pushEdit() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Suppliers");
        if (firebaseUser != null) {

            user.setName(name.getText().toString());
            user.setPhone(phone.getText().toString());
            user.setCnpj(Long.parseLong(cnpj.getText().toString()));
            address = AddressUtil.getAddressInfo(Integer.parseInt(cep.getText().toString()));
            rua.setText(address.getAvenue());
            address.setNum(Integer.parseInt(numero.getText().toString()));
            address.setCep(Integer.parseInt(cep.getText().toString()));

            String urlAddress = address.getAvenue() + " " + address.getCity() + " " + address.getCep();
            double[] latlong = AddressUtil.geocode(urlAddress);
            address.setLatitude(latlong[0]);
            address.setLongitude(latlong[1]);

            double[] coords = AddressUtil.geocode(address.getAvenue() + " " + address.getNum());
            if (coords != null) {
                address.setLatitude(coords[0]);
                address.setLongitude(coords[1]);
            }

            user.setAddress(null);

            // Salva o usuário com o UID como identificador do documento
            usersRef.child(uid).setValue(user)
                    .addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            usersRef.child(uid).child("Address").setValue(address);
                            System.out.println("ENTRANDO NA TOCA DO DIABO");
                            Toast.makeText(this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            Exception e = saveTask.getException();
                            Toast.makeText(this, "bugo!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}