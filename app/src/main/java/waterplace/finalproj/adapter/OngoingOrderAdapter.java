package waterplace.finalproj.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.List;

import waterplace.finalproj.R;
import waterplace.finalproj.activity.OrderDetails;
import waterplace.finalproj.model.Order;

public class OngoingOrderAdapter extends RecyclerView.Adapter<OngoingOrderAdapter.OrderViewHolder> {
    private List<Order> ongoingOrderList;
    private Context context;
    private String supId;

    public OngoingOrderAdapter(List<Order> ongoingOrderList, Context context, String supId) {
        this.ongoingOrderList = ongoingOrderList;
        this.context = context;
        this.supId = supId;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar o layout do item do pedido em andamento
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = ongoingOrderList.get(position);

        String location = supId+"/products/"+order.getProdId();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(location);

        holder.prodName.setText(order.getProdName());

        DecimalFormat pf = new DecimalFormat("0.00");
        holder.orderPrice.setText("R$ " + pf.format(order.getPrice()));
        System.out.println(order.getStatus());

        holder.deliveryDesc.setText(order.getAddress());

        if (order.getStatus().equals("Confirmado")) {
            if (order.isScheduled()) {
                holder.orderStatus.setText(order.getOrderDateTime());
            } else {
                holder.orderStatus.setText("Aguardando entrega");
            }
        } else if (order.getStatus().equals("Aguardando Confirmação")){
            holder.orderStatus.setText("Aguardando\nConfirmação");
            if (order.isScheduled()) {
                holder.deliveryDesc.setText(order.getOrderDateTime());
            }
        } else {
            holder.orderStatus.setText(order.getStatus());
        }

        Glide.with(holder.img.getContext())
                .load(storageReference)
                .into(holder.img);

        holder.itemView.setOnClickListener(v -> onItemClick(context, order));
    }

    @Override
    public int getItemCount() {
        return ongoingOrderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView prodName;
        TextView deliveryDesc;
        TextView orderPrice;
        TextView orderStatus;
        ImageView img;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            prodName = itemView.findViewById(R.id.item_name);
            deliveryDesc = itemView.findViewById(R.id.prod_delivery);
            orderPrice = itemView.findViewById(R.id.prod_price);
            orderStatus = itemView.findViewById(R.id.prod_status);
            img = itemView.findViewById(R.id.product_pic);
        }
    }

    private void onItemClick(Context context, Order order) {
        Intent i = new Intent(context, OrderDetails.class);
        i.putExtra("order", order);
        context.startActivity(i);
    }
}
