package waterplace.finalproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import waterplace.finalproj.R;
import waterplace.finalproj.model.Supplier;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_menu);

        Supplier user = Supplier.getInstance();

        TextView nome_supplier = findViewById(R.id.supplier_name);
        nome_supplier.setText(user.getName());

        ImageButton add_produto = findViewById(R.id.add);
        add_produto.setOnClickListener(view -> {
            goToAddProduct();
        });

        Chip ligacoes = findViewById(R.id.ligacoes);
        ligacoes.setOnClickListener(view -> {
            habilitar_ligacoes(ligacoes);
        });
    }

    private void goToAddProduct() {
        Intent i = new Intent(this, AddProduct.class);
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


}