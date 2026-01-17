//package com.marsIT.collection_app.InstallProgress;
//
//import android.content.Intent;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.marsIT.collection_app.R;
//import com.marsIT.collection_app.DatabaseHelper.DBHelper;
//import com.marsIT.collection_app.UpdateSQLpackages.UrcData;
//
//public class InstallationProcess extends AppCompatActivity {
//
//    private ProgressBar progressBar;
//    private TextView progressText;
//
//    private DBHelper dbHelper;
//    private SQLiteDatabase db;
//
//    private final Handler handler = new Handler(Looper.getMainLooper());
//
//    private String salesmanID;
//    private String department;
//    private String branchName;
//    private String lockLocation;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.install_progress);
//
//        progressBar = findViewById(R.id.progressBar);
//        progressText = findViewById(R.id.progressText);
//
//        dbHelper = new DBHelper(this);
//        db = dbHelper.getWritableDatabase();
//
//        salesmanID = getIntent().getStringExtra("SalesmanID");
//        department = getIntent().getStringExtra("Department");
//        branchName = getIntent().getStringExtra("BranchName");
//        lockLocation = getIntent().getStringExtra("LockLocation");
//
//        startInstallation();
//    }
//
//    private void startInstallation() {
//        new Thread(() -> {
//            try {
//                update("Initializing database...", 10);
//                Thread.sleep(1000);
//
//                update("Checking tables...", 30);
//                Thread.sleep(2000);
//
//                update("Preparing system data...", 60);
//                Thread.sleep(3000);
//
//                update("Finalizing setup...", 90);
//                Thread.sleep(2000);
//
//                db.execSQL("INSERT OR REPLACE INTO InstallValue (SalesmanID, Department, Status, BranchName, LockLocation) " +
//                        "VALUES('" + salesmanID + "', '" + department + "', 'Y', '" + branchName + "', '" + lockLocation + "');");
//
//                // Mark installation as fully done
//                getSharedPreferences("APP_SETUP", MODE_PRIVATE)
//                        .edit()
//                        .putBoolean("INSTALLED", true)
//                        .putBoolean("INSTALL_DONE", true)
//                        .putString("INSTALLED_SALESMAN_ID", salesmanID)
//                        .apply();
//
//                Thread.sleep(500);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            // Launch UrcData
//            handler.post(() -> {
//                Intent urcIntent = new Intent(InstallationProcess.this, UrcData.class);
//                urcIntent.putExtra("DeptCode", department);
//                urcIntent.putExtra("transinv", "");
//                startActivity(urcIntent);
//                finish();
//            });
//
//        }).start();
//    }
//
//    private void update(String message, int progress) {
//        handler.post(() -> {
//            progressText.setText(message + " " + progress + "%");
//            progressBar.setProgress(progress);
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (db != null && db.isOpen()) db.close();
//    }
//}
