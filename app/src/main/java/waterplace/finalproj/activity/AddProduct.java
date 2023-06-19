package waterplace.finalproj.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.view.View;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import waterplace.finalproj.R;
import waterplace.finalproj.model.Product;
import waterplace.finalproj.model.Supplier;

public class AddProduct extends AppCompatActivity {

    private String supplierUid;
    private Button btn_add;
    private Button btn_add_image;
    private ImageButton back_arrow;
    private Supplier supplier = Supplier.getInstance();
    private List<Product> products = supplier.getProducts();
    private FirebaseDatabase database;
    private DatabaseReference supRef;
    private String prodUid;
    private Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_product);

        back_arrow.setOnClickListener(v -> goBack());

        Spinner productDropdown = findViewById(R.id.product_dropdown);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.dropdown_items,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productDropdown.setAdapter(adapter);

        Intent intent = getIntent();
        supplierUid = intent.getStringExtra("uid");
        database = FirebaseDatabase.getInstance();
        supRef = database.getReference("Suppliers");
        btn_add = (Button)findViewById(R.id.btn_Add_Product);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProd();
            }
        });

        btn_add_image = (Button)findViewById(R.id.btn_add_image_product);
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChangeCover();
            }
        });
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    public void goBack(){
        Intent i = new Intent(this, MainMenu.class);
        startActivity(i);
    }

    public void goMenu() {
        Intent i = new Intent(this,MainMenu.class);
        i.putExtra("uid", supplierUid);
        startActivity(i);
    }
    public void addProd(){
        String name = ((EditText)findViewById(R.id.input_product_name)).getText().toString();
        float price = Float.parseFloat(((EditText)findViewById(R.id.input_price)).getText().toString());
        String type = ((Spinner)findViewById(R.id.product_dropdown)).getSelectedItem().toString();
        String volume = ((EditText)findViewById(R.id.input_volume)).getText().toString();
        String description = ((EditText)findViewById(R.id.input_description)).getText().toString();


        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setType(type);
        product.setVolume(volume);
        product.setDesc(description);


        prodUid = supRef.child(supplierUid).child("Products").push().getKey();
        supRef.child(supplierUid).child("Products").child(prodUid).setValue(product);

        UploadDeImagem(selectedImageUri);
        System.out.println("blum");
        products.add(product);
        supplier.setProducts(products);

        Toast.makeText(this, "Produto adicionado com sucesso", Toast.LENGTH_SHORT).show();
    }

    public void UploadDeImagem(Uri selectedImageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        System.out.println("blim");
        String uid = supplier.getUid();
        System.out.println("NAAAAAAAAAAAAAAAAAAAAOOOOO");
        System.out.println(supplierUid);
        System.out.println(prodUid);
        StorageReference imageRef = storageRef.child(supplierUid+"/products/"+prodUid);
        UploadTask uploadTask = imageRef.putFile(selectedImageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(AddProduct.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                goMenu();
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
            selectedImageUri = data.getData();
            ImageView imageView = (ImageView)findViewById(R.id.image_product);
            System.out.println(selectedImageUri);
            imageView.setImageURI(selectedImageUri);
            System.out.println("bla");
        }
    }
}