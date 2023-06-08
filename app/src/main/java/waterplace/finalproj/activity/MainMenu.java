package waterplace.finalproj.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import waterplace.finalproj.R;
import waterplace.finalproj.adapter.ProductAdapter;
import waterplace.finalproj.model.Product;
import waterplace.finalproj.model.Supplier;

public class MainMenu extends AppCompatActivity {

    private String uid;
    private Supplier supplier;
    private List<Product> products = new ArrayList<>();
    private DatabaseReference supplierRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_menu);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        supplierRef = FirebaseDatabase.getInstance().getReference("Suppliers").child(uid);
        supplierRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    supplier = snapshot.getValue(Supplier.class);
                    makeProdList(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeProdList(String supplierUid){
        DatabaseReference prodRef = supplierRef.child("Products");
        prodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot prodSnapshot : snapshot.getChildren()) {
                    Product product = prodSnapshot.getValue(Product.class);
                    product.setUid(prodSnapshot.getKey());
                    products.add(product);
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToAddProduct() {
        Intent i = new Intent(this, AddProduct.class);
        i.putExtra("uid", uid);
        startActivity(i);
    }

    private void habilitar_ligacoes(Chip ligacoes) {
        if (ligacoes.getText().equals(getResources().getString(R.string.ligacoes_desabilitadas))) {
            ligacoes.setText(R.string.ligacoes_habilitadas);
            ligacoes.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#C0FF8E")));
        } else {
            ligacoes.setText(R.string.ligacoes_desabilitadas);
            ligacoes.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            ligacoes.setTextColor(Color.parseColor("#000000"));
        }
    }

    private void goToChangeCover() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            ImageView imageView = findViewById(R.id.imagem_capa);
            imageView.setImageURI(selectedImageUri);
        }
    }

    private void updateUI(){
        // Nome do fornecedor
        TextView nome_supplier = findViewById(R.id.supplier_name);
        nome_supplier.setText(supplier.getName());

        // Adicionar produto
        ImageButton add_produto = findViewById(R.id.add);
        add_produto.setOnClickListener(view -> {
            goToAddProduct();
        });

        // Habilitar e desabilitar ligacoes
        Chip ligacoes = findViewById(R.id.ligacoes);
        ligacoes.setOnClickListener(view -> {
            habilitar_ligacoes(ligacoes);
        });

        // Alterar foto da capa
        Chip alterar_foto = findViewById(R.id.alterar_capa);
        alterar_foto.setOnClickListener(view -> {
            goToChangeCover();
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview_products);
        ProductAdapter adapter = new ProductAdapter(products, uid);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}