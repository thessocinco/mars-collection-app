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
//import android.widget.Filter;
//
//import android.widget.ArrayAdapter;
//import android.content.BroadcastReceiver;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi; //TODO: Target Higher Api - Updated 2025
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.material.textfield.TextInputEditText;
//import com.marsIT.collection_app.MainProgram.MainActivity;
//import com.marsIT.collection_app.R;
//
//public class StartUp extends AppCompatActivity implements OnClickListener, android.content.DialogInterface.OnClickListener {
//    private static final int PERMISSION_REQUEST_CODE = 101;
//
//    /** SQLite database instance */
//    private SQLiteDatabase db;
//
//    /** UI components for selecting various details such as: */
//    AutoCompleteTextView salesmanId;
//    TextInputEditText mDepartment;
//    TextInputEditText branchName;
//    TextInputEditText lockLocation;
//    Button btnClearSearch;
//    Button btnInstallData;
//
//    /** IntentFilter for capturing specific intents (e.g., broadcasts) */
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
//                            db.execSQL("INSERT INTO InstallValue(" +
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
//    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
//    @Override
//    protected void onResume() {
//        Log.d("StartupActivity", "onResume: Registering the receiver");
//        //TODO: Register the Receiver
//        registerReceiver(notificationReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        Log.d("StartupActivity", "onPause: Unregistering the receiver");
//        //TODO: Unregister the Receiver
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
//            /** Hide ActionBar */
//            if (getSupportActionBar() != null) getSupportActionBar().hide();
//
//            /** Check if already installed (before Anything else) */
//            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//            if (isAlreadyInstalled()) {
//                Intent intent = new Intent(StartUp.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//            }
//
//            /** Permission check */
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                checkPermissions();
//            }
//
//            /** Initialize UI components */
//            initializeViews();
//
//            /** Clears all customer-related fields */
//            btnClearSearch.setOnClickListener(v -> clearCustomerData());
//
//            /** Broadcast receiver filter */
//            intentFilter = new IntentFilter();
//            intentFilter.addAction("SMS_RECEIVED_ACTION");
//
//            /** Initialize database and tables */
//            initializeDatabase();
//
//            /** Default class */
//            syncData();
//            insertSalesmanList();
//            InsertValidNumber();
//
//            /** Install data button action */
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
//                    db.execSQL("CREATE TABLE IF NOT EXISTS InstallValue(" +
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
//                                        "N",                                     // Status
//                                        cInstallValue.getString(3),   // BranchName
//                                        cInstallValue.getString(4)    // LockLocation
//                                });
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
//    /** Clears all customer-related fields */
//    private void clearCustomerData() {
//        salesmanId.setText("");
//        mDepartment.setText("");
//        branchName.setText("");
//        lockLocation.setText("");
//        Toast.makeText(this, "Details cleared", Toast.LENGTH_SHORT).show();
//    }
//
//    /** Initialize view references */
//    private void initializeViews() {
//        salesmanId = findViewById(R.id.salesmanlist);
//        mDepartment = findViewById(R.id.departmentList);
//        branchName = findViewById(R.id.branchList);
//        lockLocation = findViewById(R.id.lockLocation);
//        btnClearSearch = findViewById(R.id.btnClearSearch);
//        btnInstallData = findViewById(R.id.btnInstall);
//    }
//
//    /** Helper method */
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
//    /** Private method: Initialize database and tables */
//    private void initializeDatabase() {
//        try {
//            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//
//            //TODO: Create required tables if not already existing
//            db.execSQL("CREATE TABLE IF NOT EXISTS validNumber(Number TEXT, Name TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS salesman(SalesmanID TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS DepartmentDetails(Department TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS branch(BranchName TEXT);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS salesmanInstallValue(" +
//                    "SalesmanID TEXT," +
//                    "Department TEXT," +
//                    "Status TEXT," +
//                    "BranchName TEXT," +
//                    "LockLocation TEXT);");
//
//            Log.d(getClass().getSimpleName(), "Database initialized successfully.");
//
//        } catch (Exception e) {
//            Log.e(getClass().getSimpleName(), "Error initializing database", e);
//            Toast.makeText(this, "Database initialization failed.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /** Function to check and request permissions */
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
//            //TODO: For Android 5.1 and below, permissions are granted at install time.
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
//            /** Base permissions */
//            for (String permission : permissions) {
//                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                    permissionsToRequest.add(permission);
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
//    /** Checking and enabling the GPS */
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
//    /** Request permissions */
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
//                }
//
//                if (locationGranted) checkAndEnableGPS();
//                if (smsGranted) askToBeDefaultSmsApp();
//
//                if (!allPermissionsGranted) {
//                    Toast.makeText(this, "Some permissions were denied. Please enable them in settings.", Toast.LENGTH_LONG).show();
//                    /** showPermissionSettingsDialog(); */ //TODO: <-- Call this when permanently denied
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
//        /** Android 10–13 Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { /** Android 10+ */
//            RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);
//            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
//                if (!roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
//                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
//                    startActivity(intent); //TODO: or startActivityForResult() if you want a result callback
//                }
//            }
//        } else {
//            /** Android 14+ RoleManager (ROLE_SMS) */
//            //TODO: Fallback for Android 4.4 (KitKat) to Android 9
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
//        Uri uri = Uri.fromParts("package", getPackageName(), null);
//        intent.setData(uri);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
//
//    private void syncData() {
//        try {
//            /** Custom adapter that handles filtering dynamically from the database */
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                    android.R.layout.simple_dropdown_item_1line, new ArrayList<>()) {
//                @Override
//                public Filter getFilter() {
//                    return new Filter() {
//                        @Override
//                        protected FilterResults performFiltering(CharSequence constraint) {
//                            FilterResults results = new FilterResults();
//                            List<String> suggestions = new ArrayList<>();
//                            if (constraint != null && constraint.length() > 0) {
//                                String queryText = "%" + constraint + "%";
//                                Cursor cursor = db.rawQuery(
//                                        "SELECT SalesmanID FROM salesmanInstallValue WHERE SalesmanID LIKE ? ORDER BY SalesmanID",
//                                        new String[]{queryText});
//                                if (cursor.moveToFirst()) {
//                                    do {
//                                        suggestions.add(cursor.getString(0));
//                                    } while (cursor.moveToNext());
//                                }
//                                cursor.close();
//                            }
//                            results.values = suggestions;
//                            results.count = suggestions.size();
//                            return results;
//                        }
//
//                        @Override
//                        protected void publishResults(CharSequence constraint, FilterResults results) {
//                            clear();
//                            if (results != null && results.count > 0) {
//                                addAll((List<String>) results.values);
//                            }
//                            notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public CharSequence convertResultToString(Object resultValue) {
//                            return resultValue.toString(); //TODO: Show selected item in the text field
//                        }
//                    };
//                }
//            };
//
//            salesmanId.setAdapter(adapter);
//            salesmanId.setThreshold(1); //TODO: show dropdown after first character
//
//            /** TextWatcher to auto-show dropdown every time user types */
//            salesmanId.addTextChangedListener(new android.text.TextWatcher() {
//                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//                @Override public void afterTextChanged(android.text.Editable s) {}
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (s.length() > 0) {
//                        salesmanId.showDropDown(); //TODO: show dropdown below
//                    }
//                }
//            });
//
//            salesmanId.setOnItemClickListener((parent, view, position, id) -> {
//                String selectedSalesmanID = parent.getItemAtPosition(position).toString();
//                salesmanId.setText(selectedSalesmanID);
//
//                try {
//                    Cursor cursor = db.rawQuery(
//                            "SELECT Department, BranchName, LockLocation FROM salesmanInstallValue WHERE SalesmanID = ?",
//                            new String[]{selectedSalesmanID});
//                    if (cursor.moveToFirst()) {
//                        mDepartment.setText(cursor.getString(0));
//                        branchName.setText(cursor.getString(1));
//                        lockLocation.setText(cursor.getString(2));
//                    }
//                    cursor.close();
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
//    /** Salesman List */
//    private void insertSalesmanList(){
//        Cursor dbSalesmanList = db.rawQuery("SELECT * FROM salesmanInstallValue", null);
//        dbSalesmanList.moveToFirst();
//        int iSalesmanList = dbSalesmanList.getCount();
//        if(iSalesmanList > 0){
//            Log.d("INSERT SALESMAN LIST", "Inserted salesman list: " + iSalesmanList);
//        }else{
//            // TODO: insert default URC salesman records here
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('CO-A','UC','N','MARS1','Y');");
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('GETUTUA','UC','N','MARS1','Y');");
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('PELIÑO','UC','N','MARS1','Y');");
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('BARCELONIA','UC','N','MARS1','Y');");
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('LLAMELO','UC','N','MARS1','Y');");
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('TARONGOY','UC','N','MARS1','Y');");
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('ELARDE','UC','N','MARS1','Y');");
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('BASAÑEZ','UC','N','MARS1','Y');");
//            db.execSQL("INSERT INTO salesmanInstallValue VALUES('PUNO','UC','N','MARS1','Y');");
//            //
//        }
//        dbSalesmanList.close();
//    }
//
//    /** Default numbers */
//    private void InsertValidNumber() {
//        Cursor dbInsertVN = db.rawQuery("SELECT * FROM validNumber", null);
//        dbInsertVN.moveToFirst();
//        int insertVN = dbInsertVN.getCount();
//        if(insertVN > 0) {
//            Log.d("INSERT VALID NUMBER", "Inserted valid number: " + insertVN);
//        } else {
//            // TODO: insert default valid numbers records here
//            db.execSQL("INSERT INTO validNumber VALUES('+639173054435','PROGRAMMER');");
//            db.execSQL("INSERT INTO validNumber VALUES('+639177105901','PROGRAMMER');");
//            //
//        }
//        dbInsertVN.close();
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
