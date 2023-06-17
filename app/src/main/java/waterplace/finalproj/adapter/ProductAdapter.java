package waterplace.finalproj.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.List;

import waterplace.finalproj.R;
import waterplace.finalproj.activity.EditProduct;
import waterplace.finalproj.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private String supplierUid;
    private Context context;

    public ProductAdapter(List<Product> productList, String supplierUid, Context context) {
        this.productList = productList;
        this.supplierUid = supplierUid;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String location = supplierUid+"/products/"+product.getUid();
        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(location);
        Glide.with(holder.img.getContext())
                .load(storageReference)
                .into(holder.img);
        holder.name.setText(product.getName());
        DecimalFormat pf = new DecimalFormat("0.00");
        holder.price.setText("R$ " + pf.format(product.getPrice()));
        holder.desc.setText(product.getDesc());
        holder.btnDelete.setOnClickListener(view -> deleteProd(position, storageReference, product));
        holder.btnEdit.setOnClickListener(view -> goEdit(context, product, supplierUid));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView desc;
        ImageView img;
        ImageButton btnEdit;
        ImageButton btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_prod_name);
            price = itemView.findViewById(R.id.txt_prod_price);
            desc = itemView.findViewById(R.id.txt_prod_desc);
            img = itemView.findViewById(R.id.img_prod);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    private void deleteProd(int position, StorageReference storageReference, Product product) {
        FirebaseDatabase.getInstance().getReference("Suppliers").child(supplierUid).child("Products").child(product.getUid()).removeValue();

        storageReference.delete();

        productList.remove(position);


        notifyItemRemoved(position);
    }

    private void goEdit(Context context, Product product, String supId) {
        Intent i = new Intent(context, EditProduct.class);
        i.putExtra("supId", supId);
        i.putExtra("product", product);
        context.startActivity(i);
    }
}