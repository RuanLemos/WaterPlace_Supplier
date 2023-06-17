package waterplace.finalproj.util;

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
    public static final int action_option1 = 2131230788;
    public static final int action_option2 = 2131230789;

    public static final int action_option3 = 2131230790;

    public BottomNavigationManager(Activity activity) {
        this.activity = activity;
    }

    public void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println(item.getItemId());
        System.out.println(action_option3);
        switch (item.getItemId()) {
            case action_option1:
                startActivity(MainMenu.class);
                return true;
            case action_option2:
                startActivity(Orders.class);
                return true;
            case action_option3:
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