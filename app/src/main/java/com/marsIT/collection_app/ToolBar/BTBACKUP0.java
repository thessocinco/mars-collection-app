//package com.marsIT.collection_app.ToolBar;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.text.SpannableString;
//import android.text.style.ForegroundColorSpan;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import com.marsIT.collection_app.MainProgram.MainActivity;
//import com.marsIT.collection_app.R;
//
//public class BaseToolbar extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(getLayoutResourceId()); //TODO: Ensure each activity has its layout
//        setupToolbar(getToolbarTitle()); //TODO: Call setupToolbar in BaseActivity
//    }
//
//    protected void setupToolbar(String title) {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//            if (getSupportActionBar() != null) {
//                getSupportActionBar().setDisplayShowTitleEnabled(false); //TODO: Hide default title
//
//                /** Check if the current activity is MainActivity and disable the back arrow */
//                if (this instanceof MainActivity) {
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                    getSupportActionBar().setHomeButtonEnabled(false);
//                } else {
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    getSupportActionBar().setHomeButtonEnabled(true);
//                }
//            }
//            TextView toolbarTitle = findViewById(R.id.toolbar_title);
//            if (toolbarTitle != null) {
//                toolbarTitle.setText(title); //TODO: Set custom title
//            }
//        }
//    }
//
//    protected int getLayoutResourceId() {
//        /** Default layout (override in child activities) */
//        return R.layout.mainlayout;
//    }
//
//    protected String getToolbarTitle() {
//        /** Default title (override in child activities) */
//        return "Default Title";
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.kebabmenu, menu);
//        for (int i = 0; i < menu.size(); i++) {
//            MenuItem item = menu.getItem(i);
//            SpannableString spanString = new SpannableString(item.getTitle());
//            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0);
//            item.setTitle(spanString);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            getOnBackPressedDispatcher().onBackPressed(); //TODO: Navigate back
//            return true;
//        } else if (item.getItemId() == R.id.action_phoneSettings) {
//            openPhoneSettings();
//            return true;
//        } else if (item.getItemId() == R.id.action_restart) {
//            Toast.makeText(this, "MAVCIV13 has restarted", Toast.LENGTH_SHORT).show();
//            restartApp();
//            return true;
//        } else if (item.getItemId() == R.id.action_exit) {
//            Toast.makeText(this, "MAVCIV13 is closed", Toast.LENGTH_SHORT).show();
//            exitApp();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    /** Opens Android settings */
//    private void openPhoneSettings() {
//        Intent intent = new Intent(Settings.ACTION_SETTINGS);
//        startActivity(intent);
//    }
//
//    /** Restarts the app */
//    private void restartApp() {
//        Intent intent = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage(getBaseContext().getPackageName());
//        if (intent != null) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finishAffinity(); //TODO: Close all activities
//        }
//    }
//
//    /** Exits the app */
//    private void exitApp() {
//        finishAffinity();
//        System.exit(0);
//    }
//}
