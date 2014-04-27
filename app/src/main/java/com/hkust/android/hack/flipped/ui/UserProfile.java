package com.hkust.android.hack.flipped.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.cengalabs.flatui.FlatUI;
import com.hkust.android.hack.flipped.R;

public class UserProfile extends BootstrapFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);


//        // and to be able to change the whole theme at once
//        FlatUI.setDefaultTheme(FlatUI.DEEP);

        // to change the color of the action bar at runtime
//        FlatUI.setActionBarTheme(this, FlatUI.DEEP, false, false);

        // if you are using ActionBar of compatibility library, get drawable and set it manually to support action bar.
//        getSupportActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(FlatUI.DEEP, false));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
