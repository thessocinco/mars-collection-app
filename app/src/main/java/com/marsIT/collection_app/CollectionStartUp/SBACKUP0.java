//package com.marsIT.collection_app.CollectionStartUp;
//
//import android.Manifest;
//import android.app.AlertDialog;
//import android.app.role.RoleManager;
//import android.content.pm.PackageManager;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.provider.Settings;
//import android.provider.Telephony;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.Toast;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.widget.ArrayAdapter;
//import android.content.BroadcastReceiver;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi; // Target Higher Api - Updated 2025
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.material.textfield.TextInputEditText;
////import com.marsIT.collection_app.InstallProgress.InstallationProcess;
////import com.marsIT.collection_app.InstallProgress.InstallationProcess;
//import com.marsIT.collection_app.MainProgram.MainActivity;
//import com.marsIT.collection_app.R;
//
//public class StartUp extends AppCompatActivity implements OnClickListener, android.content.DialogInterface.OnClickListener {
//    private static final int PERMISSION_REQUEST_CODE = 101;
//
//    // SQLite database instance
//    private SQLiteDatabase db;
//
//    // UI components for selecting various details such as:
//    AutoCompleteTextView salesmanId;
//    TextInputEditText mDepartment;
//    TextInputEditText branchName;
//    TextInputEditText lockLocation;
//
//    // IntentFilter for capturing specific intents (e.g., broadcasts)
//    IntentFilter intentFilter;
//
//    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                Bundle extras = intent.getExtras();
//                if (extras == null) return;
//
//                String syntaxSMS = extras.getString("sms", "");
//                String cellphoneNumber = extras.getString("cellnumber", "");
//
//                if (syntaxSMS.isEmpty() || cellphoneNumber.isEmpty()) return;
//
//                if (db == null || !db.isOpen()) {
//                    db = context.openOrCreateDatabase("collection_db", Context.MODE_PRIVATE, null);
//                }
//
//                Cursor queryvalidNumber = db.rawQuery("SELECT * FROM validNumber WHERE num = ?", new String[]{cellphoneNumber});
//                if (queryvalidNumber.moveToFirst()) {
//
//                    if (syntaxSMS.length() > 6) {
//                        String stchar = syntaxSMS.substring(0, 6);
//
//                        if ("INSTAL".equals(stchar)) {
//                            String rSalesmanId = "";
//                            String rDepartment = "";
//                            String rStatus = "N";
//                            String rBranchName = "";
//                            String rLockLocation = "";
//
//                            int counter = 0;
//                            for (int i = 6; i < syntaxSMS.length(); i++) {
//                                char descr = syntaxSMS.charAt(i);
//                                if (descr == '/') {
//                                    counter++;
//                                    continue;
//                                }
//                                switch (counter) {
//                                    case 1: rSalesmanId += descr; break;
//                                    case 2: rDepartment += descr; break;
//                                    case 3: rStatus += descr; break;
//                                    case 4: rBranchName += descr; break;
//                                    case 5: rLockLocation += descr; break;
//                                }
//                            }
//
//                            db.execSQL("INSERT INTO InstallValue (" +
//                                            "SalesmanID, " +
//                                            "Department, " +
//                                            "Status, " +
//                                            "BranchName, " +
//                                            "LockLocation) " +
//                                            "VALUES (?, ?, ?, ?, ?)",
//                                    new Object[]{rSalesmanId, rDepartment, rStatus, rBranchName, rLockLocation});
//
//                            Toast.makeText(context.getApplicationContext(), "Successfully Installed: " + rSalesmanId, Toast.LENGTH_LONG).show();
//
//                            Intent newIntent = new Intent(context, MainActivity.class);
//                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(newIntent);
//                        }
//                    }
//                }
//                queryvalidNumber.close();
//            } catch (Exception e) {
//                Log.e("SMSReceiver", "Error in onReceive", e);
//            }
//        }
//    };
//
//    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU) // Broadcast Entry - Updated 2025
//    @Override
//    protected void onResume() { // Register the Receiver
//        Log.d("StartupActivity", "onResume: Registering the receiver");
//        registerReceiver(notificationReceiver, intentFilter, RECEIVER_NOT_EXPORTED); // Inserted -- Context.RECEIVER_NOT_EXPORTED
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() { // Unregister the Receiver
//        Log.d("StartupActivity", "onPause: Unregistering the receiver");
//        unregisterReceiver(notificationReceiver);
//        super.onPause();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.collection_startup);
//
//        try {
//            // Hide ActionBar
//            if (getSupportActionBar() != null) getSupportActionBar().hide();
//
//            // =========================================================
//            // CHECK IF ALREADY INSTALLED (BEFORE ANYTHING ELSE)
//            // =========================================================
//            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//            if (isAlreadyInstalled()) {
//                Intent intent = new Intent(StartUp.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//            }
//
//            // =========================================================
//            // PERMISSIONS CHECK
//            // =========================================================
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                checkPermissions();
//            }
//
//            // =========================================================
//            // INITIALIZE UI COMPONENTS
//            // =========================================================
//            salesmanId = findViewById(R.id.salesmanlist);
//            mDepartment = findViewById(R.id.departmentList);
//            branchName = findViewById(R.id.branchList);
//            lockLocation = findViewById(R.id.lockLocation);
//
//            Button btnInstallData = findViewById(R.id.btnInstall);
//
//            // =========================================================
//            // BROADCAST RECEIVER FILTER
//            // =========================================================
//            intentFilter = new IntentFilter();
//            intentFilter.addAction("SMS_RECEIVED_ACTION");
//
//            // =========================================================
//            // INITIALIZE DATABASE AND TABLES
//            // =========================================================
//            initializeDatabase();
//
//            /* === DEFAULT CLASS === */
//            syncData();
//            insertSalesmanList();
//            InsertValidNumber();
//            /* === DEFAULT CLASS === */
//
//            // =========================================================
//            // INSTALL DATA BUTTON ACTION
//            // =========================================================
//            btnInstallData.setOnClickListener(v -> {
//                String salesmanName = salesmanId.getText().toString().trim();
//
//                if (salesmanName.isEmpty()) {
//                    Toast.makeText(getApplicationContext(),
//                            "Please select a salesman", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                Toast.makeText(getApplicationContext(),
//                        salesmanName + " successfully installed",
//                        Toast.LENGTH_LONG).show();
//
//                try {
//                    if (db == null || !db.isOpen()) {
//                        db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//                    }
//
//                    db.execSQL("CREATE TABLE IF NOT EXISTS InstallValue (" +
//                            "SalesmanID TEXT," +
//                            "Department TEXT," +
//                            "Status TEXT," +
//                            "BranchName TEXT," +
//                            "LockLocation TEXT);");
//
//                    Cursor cInstallValue = db.rawQuery(
//                            "SELECT * FROM salesmanInstallValue WHERE SalesmanID = ?",
//                            new String[]{salesmanName});
//
//                    if (cInstallValue.moveToFirst()) {
//                        db.execSQL("DELETE FROM InstallValue;");
//
//                        db.execSQL("INSERT INTO InstallValue VALUES (?, ?, ?, ?, ?)",
//                                new Object[]{
//                                        cInstallValue.getString(0),   // SalesmanID
//                                        cInstallValue.getString(1),   // Department
//                                        "N",                          // Status
//                                        cInstallValue.getString(2),   // BranchName
//                                        cInstallValue.getString(3)    // LockLocation
//                                });
//
////                        Toast.makeText(getApplicationContext(),
////                                "Installation started for " + salesmanName,
////                                Toast.LENGTH_LONG).show();
//
//                        // Go to InstallProgress activity to show installation loading
////                        Intent intent = new Intent(StartUp.this, MainActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                        startActivity(intent);
//                        StartUp.this.finish();
//
//                    } else {
//                        Toast.makeText(getApplicationContext(),
//                                "Salesman data not found in salesmanInstallValue.", Toast.LENGTH_LONG).show();
//                    }
//
//                    cInstallValue.close();
//
//                } catch (Exception e) {
//                    Log.e("INSTALL_DATA", "Error during installation", e);
//                    Toast.makeText(getApplicationContext(),
//                            "Error during installation: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
//            Log.e(getClass().getSimpleName(), "Error during onCreate()", e);
//            Toast.makeText(this, "An error occurred while initializing. Please restart the app.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    // ADD THIS NEW HELPER METHOD
//    private boolean isAlreadyInstalled() {
//        boolean installed = false;
//        try {
//            db.execSQL("CREATE TABLE IF NOT EXISTS InstallValue (" +
//                    "SalesmanID TEXT," +
//                    "Department TEXT," +
//                    "Status TEXT," +
//                    "BranchName TEXT," +
//                    "LockLocation TEXT);");
//
//            Cursor c = db.rawQuery("SELECT * FROM InstallValue", null);
//            if (c.moveToFirst()) {
//                installed = true;
//            }
//            c.close();
//        } catch (Exception e) {
//            Log.e("StartUp", "Error checking installation", e);
//        }
//        return installed;
//    }
//
//    // =========================================================
//    // PRIVATE METHOD: INITIALIZE DATABASE AND TABLES
//    // =========================================================
//    private void initializeDatabase() {
//        try {
//            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//
//            // Create required tables if not already existing
//            db.execSQL("CREATE TABLE IF NOT EXISTS validNumber(Number TEXT, Name TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS salesman(SalesmanID TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS department(Department TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS branch(BranchName TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS salesmanInstallValue(" +
//                    "SalesmanID TEXT," +
//                    "Department TEXT," +
//                    "Status TEXT," +
//                    "BranchName," +
//                    "LockLoction TEXT);");
//
//            Log.d(getClass().getSimpleName(), "Database initialized successfully.");
//
//        } catch (Exception e) {
//            Log.e(getClass().getSimpleName(), "Error initializing database", e);
//            Toast.makeText(this, "Database initialization failed.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    // Function to check and request permissions
//    private final ActivityResultLauncher<Intent> gpsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//        try {
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                askToEnableGPS();
//            }
//        } catch (Exception e) {
//            Log.e("GPSLauncher", "Error checking GPS after result", e);
//        }
//    });
//
//    private void checkPermissions() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            // For Android 5.1 and below, permissions are granted at install time.
//            checkAndEnableGPS();
//            return;
//        }
//
//        try {
//            String[] permissions = {
//                    Manifest.permission.SEND_SMS,
//                    Manifest.permission.RECEIVE_SMS,
//                    Manifest.permission.READ_SMS,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            };
//
//            List<String> permissionsToRequest = new ArrayList<>();
//
//            // Base permissions
//            for (String permission : permissions) {
//                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                    permissionsToRequest.add(permission);
//                }
//            }
//
//            // POST_NOTIFICATIONS for Android 13+
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
//                }
//            }
//
//            if (!permissionsToRequest.isEmpty()) {
//                ActivityCompat.requestPermissions(
//                        this,
//                        permissionsToRequest.toArray(new String[0]),
//                        PERMISSION_REQUEST_CODE
//                );
//            } else {
//                checkAndEnableGPS();
//            }
//        } catch (Exception e) {
//            Log.e("PermissionCheck", "Error checking/requesting permissions", e);
//        }
//    }
//
//    private void checkAndEnableGPS() {
//        try {
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                askToEnableGPS();
//            }
//        } catch (Exception e) {
//            Log.e("GPSCheck", "Error checking GPS status", e);
//        }
//    }
//
//    private void askToEnableGPS() {
//        try {
//            new AlertDialog.Builder(this)
//                    .setIcon(R.drawable.mars_logo)
//                    .setTitle("Enable GPS")
//                    .setMessage("GPS is required for location services. Please turn on GPS.")
//                    .setPositiveButton("Turn On", (dialog, which) -> {
//                        try {
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            gpsActivityResultLauncher.launch(intent);
//                        } catch (Exception e) {
//                            Log.e("GPSIntent", "Error launching location settings", e);
//                        }
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .setCancelable(false)
//                    .show();
//        } catch (Exception e) {
//            Log.e("GPSDialog", "Error showing GPS enable dialog", e);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            try {
//                boolean locationGranted = false;
//                boolean smsGranted = false;
//                boolean allPermissionsGranted = true;
//
//                for (int i = 0; i < permissions.length; i++) {
//                    String permission = permissions[i];
//
//                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                        allPermissionsGranted = false;
//                        Log.w("PermissionResult", "Permission denied: " + permission);
//                    }
//
//                    if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)
//                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        locationGranted = true;
//                    }
//
//                    if ((Manifest.permission.RECEIVE_SMS.equals(permission)
//                            || Manifest.permission.READ_SMS.equals(permission)
//                            || Manifest.permission.SEND_SMS.equals(permission))
//                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        smsGranted = true;
//                    }
//
//                    if (Manifest.permission.POST_NOTIFICATIONS.equals(permission)) {
//                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                            Log.i("PermissionResult", "Notification permission granted");
//                        } else {
//                            Log.w("PermissionResult", "Notification permission denied");
//                        }
//                    }
//                }
//
//                if (locationGranted) checkAndEnableGPS();
//                if (smsGranted) askToBeDefaultSmsApp();
//
//                if (!allPermissionsGranted) {
//                    Toast.makeText(this, "Some permissions were denied. Please enable them in settings.", Toast.LENGTH_LONG).show();
////                    showPermissionSettingsDialog(); // <-- Call this when permanently denied
//                    openAppSettings();
//                } else {
//                    Toast.makeText(this, "All required permissions granted.", Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (Exception e) {
//                Log.e("PermissionResult", "Error processing permission result", e);
//            }
//        }
//    }
//
//    private void askToBeDefaultSmsApp() {
//        // Android 10–13 Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
//            RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);
//            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
//                if (!roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
//                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
//                    startActivity(intent); // or startActivityForResult() if you want a result callback
//                }
//            }
//        } else {
//            // Android 14+ RoleManager (ROLE_SMS)
//            // Fallback for Android 4.4 (KitKat) to Android 9
//            String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(this);
//            if (!getPackageName().equals(defaultSmsPackage)) {
//                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
//                startActivity(intent);
//            }
//        }
//    }
//
//    private void openAppSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", getPackageName(), null); // returns com.marsIT.marsapp
//        intent.setData(uri);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional but recommended
//        startActivity(intent);
//    }
//
//    private void syncData() {
//        try {
//            ArrayList<String> labels = new ArrayList<>();
//
//            // Load all Salesman IDs
//            Cursor cursor = db.rawQuery("SELECT SalesmanID FROM salesmanInstallValue ORDER BY SalesmanID", null);
//            if (cursor.moveToFirst()) {
//                do {
//                    labels.add(cursor.getString(0).trim()); // remove trailing spaces
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//
//            // Adapter for salesman dropdown
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, labels);
//            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            salesmanId.setAdapter(dataAdapter);
//
//            // When a Salesman ID is selected
//            salesmanId.setOnItemClickListener((parent, view, position, id) -> {
//                String selectedSalesmanID = parent.getItemAtPosition(position).toString();
//
//                try {
//                    // Query data for the selected salesman
//                    Cursor c = db.rawQuery(
//                            "SELECT " +
//                                    "Department, " +
//                                    "Status, " +
//                                    "BranchName, " +
//                                    "LockLoction " +
//                                    "FROM salesmanInstallValue WHERE SalesmanID = ?", new String[]{selectedSalesmanID});
//
//                    if (c.moveToFirst()) {
//                        String iDepartment = c.getString(0);
//                        String iBranchName = c.getString(2);
//                        String iLockLocation = c.getString(3);
//
//                        // Auto-display data in dropdowns (user can still edit manually)
//                        mDepartment.setText(iDepartment);
//                        branchName.setText(iBranchName);
//                        lockLocation.setText(iLockLocation);
//
//                        // Auto-filled -> Dept, Branch, Lock
//                        Log.d("SALESMAN SELECT", "Dept: " + iDepartment + ", Branch: " + iBranchName + ", Lock: " + iLockLocation);
//                    }
//                    c.close();
//                } catch (Exception ex) {
//                    Log.e("SALESMAN SELECT", "Error loading salesman details", ex);
//                }
//            });
//
//        } catch (Exception e) {
//            Log.e("LOAD SPINNER ERROR", "Load spinner data ERROR: ", e);
//        }
//    }
//
//    private void insertSalesmanList(){
//        Cursor cSalesmanList = db.rawQuery("SELECT * FROM salesmanInstallValue", null);
//        cSalesmanList.moveToFirst();
//        int iSalesmanList = cSalesmanList.getCount();
//        if(iSalesmanList > 0){
//            Log.d("INSERT SALESMAN LIST", "Inserted salesman list:: " + iSalesmanList);
//        }else{
//            // TODO: insert default URC salesman records here
//            db.execSQL("Insert into salesmanInstallValue values('CO-A','UC','N','MARS1','Y');");
//            db.execSQL("Insert into salesmanInstallValue values('GETUTUA','UC','N','MARS1','Y');");
//            db.execSQL("Insert into salesmanInstallValue values('PELIÑO','UC','N','MARS1','Y');");
//            db.execSQL("Insert into salesmanInstallValue values('BARCELONIA','UC','N','MARS1','Y');");
//            db.execSQL("Insert into salesmanInstallValue values('LLAMELO','UC','N','MARS1','Y');");
//            db.execSQL("Insert into salesmanInstallValue values('TARONGOY','UC','N','MARS1','Y');");
//            db.execSQL("Insert into salesmanInstallValue values('ELARDE','UC','N','MARS1','Y');");
//            db.execSQL("Insert into salesmanInstallValue values('BASAÑEZ','UC','N','MARS1','Y');");
//            db.execSQL("Insert into salesmanInstallValue values('PUNO','UC','N','MARS1','Y');");
//            //
//        }
//        cSalesmanList.close();
//    }
//
//    private void InsertValidNumber() {
//        Cursor cInsertVN = db.rawQuery("SELECT * FROM validNumber", null);
//        cInsertVN.moveToFirst();
//        int insertVN = cInsertVN.getCount();
//        if(insertVN > 0) {
//            Log.d("VALID NUMBER", "Inserted valid number: " + insertVN);
//        } else {
//            // TODO: insert default valid numbers records here
//            db.execSQL("Insert into validNumber values('+639173054435','PROGRAMMER');");
//            db.execSQL("Insert into validNumber values('+639177105901','PROGRAMMER');");
//            //
//        }
//        cInsertVN.close();
//    }
//
//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//    }
//}
//
//
//
