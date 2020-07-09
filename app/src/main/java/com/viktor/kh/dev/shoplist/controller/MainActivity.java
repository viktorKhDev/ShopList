package com.viktor.kh.dev.shoplist.controller;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.viktor.kh.dev.shoplist.controller.backup.BackupFragment;
import com.viktor.kh.dev.shoplist.controller.lists.ListsFragment;
import com.viktor.kh.dev.shoplist.controller.recipes.RecipesFragment;
import com.viktor.kh.dev.shoplist.controller.support.SupportFragment;
import com.viktor.kh.dev.shoplist.model.Repository;
import com.viktor.kh.dev.shoplist.utils.BackupListener;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.utils.OnBackPressedListener;
import com.viktor.kh.dev.shoplist.R;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BackupListener {

    Toolbar toolbar;
    private static SharedPreferences myPref;
    private static final String FIRST_RUN = "firstRun";
    private static boolean startDisplayStatus = false;
    private static boolean onStop = false;
    private static boolean settingStarted = false;
    private Repository repository;
    private DrawerLayout drawer;
    FrameLayout centerContainer;
    private ActionBarDrawerToggle toggle;
    ProgressBar progressBar;




    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new Repository(this,this);
        firstRun();
        initStartDisplay();
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ShopList");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        centerContainer = findViewById(R.id.center_container);
        centerContainer.getForeground().setAlpha(40);
        progressBar = findViewById(R.id.progressBar);

    }



    private void initStartDisplay(){

        if(!Helper.onBackupRead){
            changeFragment(new ListsFragment());
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch(requestCode){
            case 1001:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();

                    repository.writeBackup(uri);

                }
                break;
            case 1000:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();

                    repository.readBackup(uri);
                }
        }
    }

    @Override
    protected void onDestroy() {
        moveTaskToBack(true);
        super.onDestroy();
       // System.runFinalizersOnExit(true);
        System.exit(0);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStop = true;
        startDisplayStatus = false;

    }

    private void firstRun(){
        myPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(!myPref.getBoolean(FIRST_RUN,false)){
            repository.firstRun();
            SharedPreferences.Editor editor = myPref.edit();
            editor.putBoolean(FIRST_RUN,true);
            editor.apply();

        }
    }


    @Override
    public void onBackPressed() {
        if(!Helper.onBackupRead){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                FragmentManager fm = getSupportFragmentManager();
                OnBackPressedListener backPressedListener = null;
                for (Fragment fragment: fm.getFragments()) {
                    if (fragment instanceof  OnBackPressedListener) {
                        backPressedListener = (OnBackPressedListener) fragment;
                        break;
                    }
                }

                if (backPressedListener != null) {

                    backPressedListener.onBackPressed();
                } else {
                    super.onBackPressed();
                }
            }
        }



    }
    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if(id == R.id.lists_item_dv){
            changeFragment(new ListsFragment());
        }else if(id == R.id.recipes_item_dv){
            changeFragment(new RecipesFragment());
        }else if(id == R.id.setting_item_dv){
            startActivity(new Intent(MainActivity.this,SettingActivity.class));
            settingStarted = true;
        }else if(id == R.id.backup_item_dv){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new BackupFragment()).addToBackStack(null).commit();
        }else if(id == R.id.support){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new SupportFragment()).addToBackStack(null).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



   /* private void setLocale(String locale){
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(locale.toLowerCase());
        res.updateConfiguration(conf, dm);
    }
*/
    @Override
    public void onBackupRead() {
      Helper.showToast(getString(R.string.backup_read),this);
        progressBar.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onPreBackup() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void onError() {
        progressBar.setVisibility(ProgressBar.GONE);
    }

}
