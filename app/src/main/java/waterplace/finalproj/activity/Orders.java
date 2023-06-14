package waterplace.finalproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import waterplace.finalproj.R;

public class Orders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_footer);
        bottomNavigationView.setSelectedItemId(R.id.lista);
    }
}