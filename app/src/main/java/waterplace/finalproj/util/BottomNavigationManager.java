package waterplace.finalproj.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import waterplace.finalproj.R;
import waterplace.finalproj.activity.EditProfile;
import waterplace.finalproj.activity.MainMenu;
import waterplace.finalproj.activity.Orders;

public class BottomNavigationManager implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Activity activity;

    public BottomNavigationManager(Activity activity) {
        this.activity = activity;
    }

    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_option1) {
            startActivity(MainMenu.class);
            return true;
        }
        if(item.getItemId() == R.id.action_option2) {
            startActivity(Orders.class);
            return true;
        }
        if(item.getItemId() == R.id.action_option3) {
                startActivity(EditProfile.class);
                return true;
        }
        return false;
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }
}