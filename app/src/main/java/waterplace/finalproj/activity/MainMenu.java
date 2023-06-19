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
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import waterplace.finalproj.R;
import waterplace.finalproj.adapter.ProductAdapter;
import waterplace.finalproj.model.Product;
import waterplace.finalproj.model.Supplier;
import waterplace.finalproj.util.BottomNavigationManager;

public class MainMenu extends AppCompatActivity {

    private String uid;
    private Supplier supplier;
    private List<Product> products = new ArrayList<>();
    private DatabaseReference supplierRef;
    private BottomNavigationManager bottomNavigationManager;
    private TextView ratingValue;
    private RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_footer);
        bottomNavigationManager = new BottomNavigationManager(this);
        bottomNavigationManager.setupBottomNavigation(bottomNavigationView);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        supplierRef = FirebaseDatabase.getInstance().getReference("Suppliers").child(uid);
        supplierRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    supplier = snapshot.getValue(Supplier.class);
                    ratingValue = findViewById(R.id.nota);
                    rating = findViewById(R.id.ratingBar);
                    DecimalFormat df = new DecimalFormat("0.0");
                    if (supplier.getRating() != 0.0) {
                        ratingValue.setText(df.format(supplier.getRating()));
                        rating.setRating((float) supplier.getRating());
                    } else {
                        ratingValue.setText("Sem avaliações.");
                        rating.setVisibility(View.GONE);
                    }

                    makeProdList();
                    setCapaImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        //Desabilita a seta
    }

    private void setCapaImage() {
        ImageView img_capa = findViewById(R.id.image_capa);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        String location = uid+"/capa/image.jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(location);
        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Glide.with(MainMenu.this)
                        .load(storageReference)
                        .into(img_capa);
                img_capa.setVisibility(View.VISIBLE);
                System.out.println("ablealblealba");
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                img_capa.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void makeProdList(){
        DatabaseReference prodRef = supplierRef.child("Products");
        prodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
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
        System.out.println("Ô CARAIII " + uid);
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

    public void UploadDeImagem(Uri selectedImageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child(uid+"/capa/image.jpg");
        UploadTask uploadTask = imageRef.putFile(selectedImageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainMenu.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainMenu.this, "Upload da imagem feito com sucesso", Toast.LENGTH_SHORT).show();
            }
        });
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
            ImageView imageView = (ImageView)findViewById(R.id.image_capa);
            imageView.setImageURI(selectedImageUri);
            System.out.println("teste");
            UploadDeImagem(selectedImageUri);
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
        ProductAdapter adapter = new ProductAdapter(products, uid, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}