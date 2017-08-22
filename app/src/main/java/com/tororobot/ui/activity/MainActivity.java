package com.tororobot.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tororobot.R;
import com.tororobot.ui.fragment.SettingsActivity;
import com.tororobot.ui.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, MainFragment.newInstance())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connect_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
//            case R.id.action_chat:
//                intent = new Intent(this, ChatActivity.class);
//                startActivity(intent);
//                return true;
        }
        return true;
    }


//    private void initUI(Bundle savedInstanceState) {
//        if (savedInstanceState == null) {
//            setTitle(getString(R.string.main));
//            GoToFragment.callFragment(getSupportFragmentManager(), InitFragment.newInstance());
//        }
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.mainItem:
//                    setTitle(item.getTitle());
//                    GoToFragment.callFragment(getSupportFragmentManager(), InitFragment.newInstance());
//                    break;
//                case R.id.meItem:
//                    setTitle(item.getTitle());
//                    GoToFragment.callFragment(getSupportFragmentManager(), MeFragment.newInstance("Hola", "Perfil"));
//                    break;
//            }
//            return false;
//        });
//    }

}