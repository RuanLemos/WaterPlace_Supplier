package waterplace.finalproj.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import waterplace.finalproj.R;
import waterplace.finalproj.util.BottomNavigationManager;

public class EditProfile extends AppCompatActivity {

    private BottomNavigationManager bottomNavigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_footer);
        bottomNavigationView.setSelectedItemId(R.id.action_option3);
        bottomNavigationManager = new BottomNavigationManager(this);
        bottomNavigationManager.setupBottomNavigation(bottomNavigationView);
    }
}