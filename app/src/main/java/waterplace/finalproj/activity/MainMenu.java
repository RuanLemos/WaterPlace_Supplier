package waterplace.finalproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
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
    }
}