package waterplace.finalproj.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import waterplace.finalproj.R;
import waterplace.finalproj.model.Address;
import waterplace.finalproj.model.Order;
import waterplace.finalproj.model.Product;
import waterplace.finalproj.model.Supplier;

public class OrderDetails extends AppCompatActivity {

    private DatabaseReference supRef = FirebaseDatabase.getInstance().getReference("Suppliers");
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
    private String supId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Product product;
    private Order order;
    private TextView prodName;
    private TextView prodDesc;
    private TextView prodAmount;
    private TextView deliveryType;
    private TextView itemsPrice;
    private TextView subtotalValue;
    private TextView deliveryTax;
    private TextView serviceTax;
    private TextView totalValue;
    private TextView deliveryAddress;
    private Intent i;
    private ImageButton back_arrow;
    private Button btn_confirm;
    private Button btn_reject;
    private TextView delivery;
    private TextView status;
    private View divisor;
    private Button btn_delivering;
    private Button btn_delivered;
    private Button btn_edit_confirm;
    private boolean isDeliveringPressed;
    private boolean isDelivering;
    private boolean isDeliveredPressed;
    private ImageButton arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_order_details);

        back_arrow.setOnClickListener(v -> goBack());

        i = getIntent();

        order = (Order) i.getSerializableExtra("order");

        isDelivering = order.getStatus().equals("A caminho");
        btn_delivering = findViewById(R.id.delivering);

        if (isDelivering) {
            btn_delivering.setBackgroundColor(getResources().getColor(R.color.verdinho));
            isDeliveringPressed = true;
        }

        arrow = findViewById(R.id.back_arrow_6);
        arrow.setOnClickListener(v -> goOrders());
        btn_confirm = findViewById(R.id.confirm);
        btn_confirm.setOnClickListener(v -> confirmOrder());
        btn_reject = findViewById(R.id.reject);
        btn_reject.setOnClickListener(v -> cancelOrder());
        delivery = findViewById(R.id.del_type_warning);

        updateUI();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    public void goBack(){
        Intent i = new Intent(this, Orders.class);
        startActivity(i);
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(){
        loadImg();
        updateVisibilities();
        getProduct();
        DecimalFormat pf = new DecimalFormat("0.00");

        prodAmount = findViewById(R.id.amount_tag);
        prodAmount.setText(String.valueOf(order.getQuantity()));
        deliveryType = findViewById(R.id.del_type);

        if (order.isScheduled()) {
            String deliveryDate = order.getDeliveryDateTime();
            deliveryType.setText(deliveryDate);
            delivery.setText(order.getOrderDateTime());
        } else {
            deliveryType.setText("Entrega imediata");
            delivery.setText("Entrega imediata: " + order.getOrderDateTime());
        }

        itemsPrice = findViewById(R.id.item_price);
        itemsPrice.setText("R$ " + pf.format(order.getSubtotal()));
        subtotalValue = findViewById(R.id.subtotal_value);
        subtotalValue.setText("R$ " + pf.format(order.getSubtotal()));
        deliveryTax = findViewById(R.id.tax_del_value);
        deliveryTax.setText("Grátis");
        serviceTax = findViewById(R.id.tax_serv_value);
        double serviceTaxValue = 1.90;
        serviceTax.setText("R$ " + pf.format(serviceTaxValue));
        totalValue = findViewById(R.id.total_value);
        order.setPrice(order.getSubtotal() + serviceTaxValue);
        totalValue.setText("R$ " + pf.format(order.getPrice()));
        deliveryAddress = findViewById(R.id.end_value);
        deliveryAddress.setText(order.getAddress());
    }

    private void confirmOrder() {
        order.setStatus("Confirmado");

        // Atualizando o campo status para confirmado no Supplier
        System.out.println(supId);
        System.out.println(order.getOrderId());
        supRef.child(supId).child("Orders").child(order.getOrderId()).child("status").setValue("Confirmado");

        // Atualizando o campo status para confirmado no User
        userRef.child(order.getUserId()).child("Orders").child(order.getOrderId()).child("status").setValue("Confirmado");

        goOrders();
    }

    private void cancelOrder() {
        supRef.child(supId).child("Orders").child(order.getOrderId()).removeValue();
        userRef.child(order.getUserId()).child("Orders").child(order.getOrderId()).removeValue();

        goOrders();
    }

    private void goOrders(){
        Intent i = new Intent(this, Orders.class);
        startActivity(i);
    }

    private void getProduct(){
        supRef.child(supId).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    if (productSnapshot.getKey().equals(order.getProdId())) {
                        product = productSnapshot.getValue(Product.class);
                    }
                }
                prodName = findViewById(R.id.item_name);
                prodName.setText(product.getName());
                prodDesc = findViewById(R.id.amount);
                prodDesc.setText(product.getDesc());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateVisibilities() {
        if (order.getStatus().equals("Entregue")){
            btn_confirm.setVisibility(View.GONE);
            btn_reject.setVisibility(View.GONE);
            delivery.setVisibility(View.GONE);
            status = findViewById(R.id.del_type_reminder);

            findViewById(R.id.header_status).setVisibility(View.VISIBLE);
            status.setText("Entregue dia " + order.getDeliveryDateTime());
            divisor = findViewById(R.id.divisor_12);
            divisor.setVisibility(View.VISIBLE);
            status.setVisibility(View.VISIBLE);

        } else if (!order.getStatus().equals("Aguardando confirmação")) {
            btn_confirm.setVisibility(View.GONE);
            btn_reject.setVisibility(View.GONE);
            delivery.setVisibility(View.GONE);
            findViewById(R.id.header_status).setVisibility(View.VISIBLE);
            status = findViewById(R.id.del_type_reminder);

            if (order.isScheduled()) {
                status.setText(order.getOrderDateTime());
            } else {
                if (order.getStatus().equals("A caminho")){
                    status.setText(order.getStatus());
                } else {
                    status.setText("Entrega imediata: " + order.getOrderDateTime());
                }
            }
            status.setVisibility(View.VISIBLE);
            divisor = findViewById(R.id.divisor_12);
            divisor.setVisibility(View.VISIBLE);

            btn_delivering.setVisibility(View.VISIBLE);

            btn_delivered = findViewById(R.id.delivered);
            btn_delivered.setVisibility(View.VISIBLE);

            btn_edit_confirm = findViewById(R.id.edit_confirm);

            if (!isDelivering) {
                btn_delivering.setOnClickListener(v -> {
                    //System.out.println(!isDeliveredPressed);
                    //System.out.println(!isDeliveringPressed);
                    //System.out.println(!isDeliveringPressed && !isDeliveredPressed);
                    if (!isDeliveringPressed) {
                        btn_delivering.setBackgroundColor(getResources().getColor(R.color.verdinho));
                        isDeliveringPressed = true;
                        btn_edit_confirm.setVisibility(View.VISIBLE);
                    } else if (!isDeliveredPressed){
                        btn_delivering.setBackgroundColor(getResources().getColor(R.color.cor_ruim));
                        isDeliveringPressed = false;
                        btn_edit_confirm.setVisibility(View.GONE);
                    }
                });
            }

            btn_delivered.setOnClickListener(v -> {
                if (!isDeliveredPressed && isDeliveringPressed) {
                    btn_delivered.setBackgroundColor(getResources().getColor(R.color.verdinho));
                    isDeliveredPressed = true;
                    btn_edit_confirm.setVisibility(View.VISIBLE);
                } else {
                    btn_delivered.setBackgroundColor(getResources().getColor(R.color.cor_ruim));
                    isDeliveredPressed = false;
                    btn_edit_confirm.setVisibility(View.GONE);
                }
            });

            btn_edit_confirm.setOnClickListener(v -> updateDelivery());

        }
    }

    public void updateDelivery() {
        if (isDeliveredPressed) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
            String formattedDateTime = now.format(formatter);

            supRef.child(supId).child("Orders").child(order.getOrderId()).child("status").setValue("Entregue");
            supRef.child(supId).child("Orders").child(order.getOrderId()).child("deliveryDateTime").setValue(formattedDateTime);


            userRef.child(order.getUserId()).child("Orders").child(order.getOrderId()).child("status").setValue("Entregue");
            userRef.child(order.getUserId()).child("Orders").child(order.getOrderId()).child("deliveryDateTime").setValue(formattedDateTime);

            goOrders();
        } else if (isDeliveringPressed){
            supRef.child(supId).child("Orders").child(order.getOrderId()).child("status").setValue("A caminho");

            userRef.child(order.getUserId()).child("Orders").child(order.getOrderId()).child("status").setValue("A caminho");

            goOrders();
        }
    }

    private void loadImg(){
        String location = supId+"/products/"+order.getProdId();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(location);
        ImageView img = findViewById(R.id.img_prod);
        Glide.with(img.getContext()).load(storageReference).into(img);
    }
}