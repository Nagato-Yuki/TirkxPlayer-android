package com.tirkx.aos;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment; // NavDrawer
    public CharSequence mTitle; // ActionBar Title

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this,"ขออภัยด้วย ขณะนี้ยังไม่สามารถค้นหารายชื่ออนิเมะจากแอปภายนอกได้",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(position==0) {
            if(fragmentManager.findFragmentByTag("STICKYANIME")==null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, StickyAnimeFragment.newInstance(1), "STICKYANIME")
                        .commit();
            }
        }else if(position==1) {
            if(fragmentManager.findFragmentByTag("ARCHIVEANIME")==null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ArchiveAnimeFragment.newInstance(2), "ARCHIVEANIME")
                        .commit();
            }
        }else if(position==2) {
            if (fragmentManager.findFragmentByTag("NEWANIME") == null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, NewAnimeFragment.newInstance(3), "NEWANIME")
                        .commit();
            }
        }else if(position==3) {
            if(fragmentManager.findFragmentByTag("ABOUT")==null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AboutFragment.newInstance(4), "ABOUT")
                        .commit();
            }
        }else{
            Toast.makeText(this,"Not implement yet",Toast.LENGTH_LONG).show();
        }
    }

    public void onSectionAttached(int number) {
       // Change to access mTitle by Fragment self
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Create fragment menu
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Toggle between up and drawer
        if(isOnDrawerStateBack()&&id == android.R.id.home){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStackImmediate();
            setDrawerState(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setDrawerState(boolean status){
        mNavigationDrawerFragment.setDrawerState(status);
    }
    public boolean isOnDrawerStateBack(){
        return mNavigationDrawerFragment.isOnBackState();
    }
}
