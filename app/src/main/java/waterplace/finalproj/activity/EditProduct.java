package waterplace.finalproj.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import waterplace.finalproj.R;
import waterplace.finalproj.model.Product;
import waterplace.finalproj.model.Supplier;

public class EditProduct extends AppCompatActivity {

    private String supplierUid;
    private Button btn_edit;
    private Button btn_edit_image;
    private Supplier supplier = Supplier.getInstance();
    private Product product;
    private FirebaseDatabase database;
    private DatabaseReference supRef;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();
        supplierUid = intent.getStringExtra("supId");
        product = (Product) intent.getSerializableExtra("product");

        database = FirebaseDatabase.getInstance();
        supRef = database.getReference("Suppliers");

        populateProductFields();

        btn_edit = findViewById(R.id.btn_edit_Product);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProd();
            }
        });

        btn_edit_image = findViewById(R.id.btn_edit_image_product);
        btn_edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChangeCover();
            }
        });

    }

    private void populateProductFields() {
        EditText productNameEditText = findViewById(R.id.input_product_name);
        EditText priceEditText = findViewById(R.id.input_price);
        Spinner productDropdown = findViewById(R.id.product_dropdown);
        EditText volumeEditText = findViewById(R.id.input_volume);
        EditText descriptionEditText = findViewById(R.id.input_description);
        ImageView productImageView = findViewById(R.id.image_product);

        productNameEditText.setText(product.getName());
        priceEditText.setText(String.valueOf(product.getPrice()));
        // Set selected item in dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.dropdown_items,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productDropdown.setAdapter(adapter);
        int selectedPosition = adapter.getPosition(product.getType());
        productDropdown.setSelection(selectedPosition);
        volumeEditText.setText(product.getVolume());
        descriptionEditText.setText(product.getDesc());

        // Load product image from storage and set to ImageView
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        System.out.println(supplierUid);
        StorageReference imageRef = storageRef.child(supplierUid + "/products/" + product.getUid());
        Glide.with(productImageView.getContext()).load(imageRef).into(productImageView);


    }

    private void editProd() {
        String name = ((EditText) findViewById(R.id.input_product_name)).getText().toString();
        float price = Float.parseFloat(((EditText) findViewById(R.id.input_price)).getText().toString());
        String type = ((Spinner) findViewById(R.id.product_dropdown)).getSelectedItem().toString();
        String volume = ((EditText) findViewById(R.id.input_volume)).getText().toString();
        String description = ((EditText) findViewById(R.id.input_description)).getText().toString();

        product.setName(name);
        product.setPrice(price);
        product.setType(type);
        product.setVolume(volume);
        product.setDesc(description);

        supRef.child(supplierUid).child("Products").child(product.getUid()).setValue(product);

        if (selectedImageUri != null) {
            System.out.println("huh");
            UploadDeImagem(selectedImageUri);
        } else {
            goMenu();
        }

        Toast.makeText(this, "Produto atualizado com sucesso", Toast.LENGTH_SHORT).show();
    }

    public void UploadDeImagem(Uri selectedImageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String uid = supplier.getUid();
        System.out.println(supplierUid);
        System.out.println(product.getUid());
        StorageReference imageRef = storageRef.child(supplierUid + "/products/" + product.getUid());
        UploadTask uploadTask = imageRef.putFile(selectedImageUri);
        System.out.println("aaaaaa");
        System.out.println(selectedImageUri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProduct.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("awawawa");
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
            System.out.println("DESGRAÇAAA");
            selectedImageUri = data.getData();
            ImageView imageView = findViewById(R.id.image_product);
            imageView.setImageURI(selectedImageUri);
            System.out.println(selectedImageUri);
        }
    }

    private void goMenu() {
        Intent i = new Intent(this, MainMenu.class);
        i.putExtra("uid", supplierUid);
        startActivity(i);
    }
}
