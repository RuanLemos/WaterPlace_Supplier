package waterplace.finalproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import waterplace.finalproj.R;
import waterplace.finalproj.model.Product;
import waterplace.finalproj.model.Supplier;

public class AddProduct extends AppCompatActivity {

    private String supplierUid;
    private Button btn_add;
    private Supplier supplier = Supplier.getInstance();
    private List<Product> products = supplier.getProducts();
    private FirebaseDatabase database;
    private DatabaseReference supRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Intent intent = getIntent();
        supplierUid = intent.getStringExtra("uid");
        database = FirebaseDatabase.getInstance();
        supRef = database.getReference("Suppliers");
        btn_add = (Button)findViewById(R.id.btn_Add_Product);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProd();
                goMenu();
            }
        });
    }

    public void goMenu() {
        Intent i = new Intent(this,MainMenu.class);
        i.putExtra("uid", supplierUid);
        startActivity(i);
    }
    public void addProd(){
        String name = ((EditText)findViewById(R.id.input_product_name)).getText().toString();
        float price = Float.parseFloat(((EditText)findViewById(R.id.input_price)).getText().toString());
        String type = ((EditText)findViewById(R.id.input_type)).getText().toString();
        String volume = ((EditText)findViewById(R.id.input_volume)).getText().toString();
        String description = ((EditText)findViewById(R.id.input_description)).getText().toString();

        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setType(type);
        product.setVolume(volume);
        product.setDesc(description);

        String prodUid = supRef.child(supplierUid).child("Products").push().getKey();
        supRef.child(supplierUid).child("Products").child(prodUid).setValue(product);

        products.add(product);
        supplier.setProducts(products);

        Toast.makeText(this, "Produto adicionado com sucesso", Toast.LENGTH_SHORT).show();
    }
}