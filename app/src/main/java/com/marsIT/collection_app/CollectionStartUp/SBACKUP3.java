//package com.marsIT.collection_app.CollectionStartUp;
//
//import android.Manifest;
//import android.app.AlertDialog;
//import android.app.role.RoleManager;
//import android.content.pm.PackageManager;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.database.Cursor;
//import android.util.Log;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.Toast;
//import android.widget.Filter;
//import android.widget.ArrayAdapter;
//import android.content.BroadcastReceiver;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.material.textfield.TextInputEditText;
//import com.marsIT.collection_app.DatabaseHelper.DBHelper;
//import com.marsIT.collection_app.InstallProgress.InstallationProcess;
//import com.marsIT.collection_app.MainProgram.MainActivity;
//import com.marsIT.collection_app.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StartUp extends AppCompatActivity {
//
//    private static final int PERMISSION_REQUEST_CODE = 101;
//
//    private DBHelper dbHelper;
//    private AutoCompleteTextView salesmanId;
//    private TextInputEditText mDepartment, branchName, lockLocation;
//    private Button btnClearSearch, btnInstallData;
//    private IntentFilter intentFilter;
//
//    /** Broadcast receiver for SMS */
//    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                Bundle extras = intent.getExtras();
//                if (extras == null) return;
//
//                String syntaxSMS = extras.getString("sms", "");
//                String cellphoneNumber = extras.getString("cellnumber", "");
//                if (syntaxSMS.isEmpty() || cellphoneNumber.isEmpty()) return;
//
//                try (Cursor cursor = dbHelper.getReadableDatabase()
//                        .rawQuery("SELECT * FROM validNumber WHERE Number = ?", new String[]{cellphoneNumber})) {
//                    if (cursor.moveToFirst() && syntaxSMS.startsWith("INSTAL")) {
//                        parseAndInsertInstallData(syntaxSMS);
//                        Toast.makeText(context, "Successfully Installed", Toast.LENGTH_LONG).show();
//                        context.startActivity(new Intent(context, InstallationProcess.class)
//                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("SMSReceiver", "Error processing SMS", e);
//            }
//        }
//    };
//
//    private final ActivityResultLauncher<Intent> gpsLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> checkAndEnableGPS());
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.collection_startup);
//        if (getSupportActionBar() != null) getSupportActionBar().hide();
//
//        dbHelper = new DBHelper(this);
//        initializeViews();
//        setupListeners();
//
//        if (isAlreadyInstalled()) {
//            startActivity(new Intent(this, InstallationProcess.class));
//            finish();
//        }
//
//        checkPermissions();
//        syncSalesmanData();
//        insertDefaultSalesmen();
//        insertDefaultValidNumbers();
//
//        intentFilter = new IntentFilter("SMS_RECEIVED_ACTION");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (intentFilter != null)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                registerReceiver(notificationReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
//            }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        try { unregisterReceiver(notificationReceiver); } catch (Exception ignored) {}
//    }
//
//    /** UI Initialization */
//    private void initializeViews() {
//        salesmanId = findViewById(R.id.salesmanlist);
//        mDepartment = findViewById(R.id.departmentList);
//        branchName = findViewById(R.id.branchList);
//        lockLocation = findViewById(R.id.lockLocation);
//        btnClearSearch = findViewById(R.id.btnClearSearch);
//        btnInstallData = findViewById(R.id.btnInstall);
//    }
//
//    private void setupListeners() {
//        btnClearSearch.setOnClickListener(v -> clearCustomerData());
//        btnInstallData.setOnClickListener(v -> installSelectedSalesman());
//    }
//
//    private void clearCustomerData() {
//        salesmanId.setText("");
//        mDepartment.setText("");
//        branchName.setText("");
//        lockLocation.setText("");
//        Toast.makeText(this, "Details cleared", Toast.LENGTH_SHORT).show();
//    }
//
//    /** Installation */
//    private boolean isAlreadyInstalled() {
//        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM InstallValue", null)) {
//            return cursor.moveToFirst();
//        }
//    }
//
//    private void installSelectedSalesman() {
//        String salesman = salesmanId.getText().toString().trim();
//        if (salesman.isEmpty()) {
//            Toast.makeText(this, "Select a salesman", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        try (Cursor c = dbHelper.getReadableDatabase()
//                .rawQuery("SELECT * FROM salesmanInstallValue WHERE SalesmanID = ?", new String[]{salesman})) {
//            if (c.moveToFirst()) {
//                dbHelper.getWritableDatabase().execSQL("DELETE FROM InstallValue;");
//                dbHelper.getWritableDatabase().execSQL("INSERT INTO InstallValue VALUES(?, ?, ?, ?, ?)",
//                        new Object[]{c.getString(0), c.getString(1), "N", c.getString(3), c.getString(4)});
//                Toast.makeText(this, salesman + " successfully installed", Toast.LENGTH_LONG).show();
//                startActivity(new Intent(this, InstallationProcess.class));
//                finish();
//            } else Toast.makeText(this, "Salesman not found.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void parseAndInsertInstallData(String sms) {
//        String[] parts = sms.split("/");
//        if (parts.length >= 6) {
//            dbHelper.getWritableDatabase().execSQL("INSERT INTO InstallValue VALUES (?, ?, ?, ?, ?)",
//                    new Object[]{parts[1], parts[2], parts[3], parts[4], parts[5]});
//        }
//    }
//
//    /** Default data */
//    private void insertDefaultSalesmen() {
//        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM salesmanInstallValue", null)) {
//            if (cursor.getCount() == 0) {
//                String[] defaultSalesmen = {
//                        "('CO-A','UC','N','MARS1','Y')",
//                        "('GETUTUA','UC','N','MARS1','Y')",
//                        "('PELIÑO','UC','N','MARS1','Y')",
//                        "('BARCELONIA','UC','N','MARS1','Y')",
//                        "('LLAMELO','UC','N','MARS1','Y')",
//                        "('TARONGOY','UC','N','MARS1','Y')",
//                        "('ELARDE','UC','N','MARS1','Y')",
//                        "('BASAÑEZ','UC','N','MARS1','Y')",
//                        "('PUNO','UC','N','MARS1','Y')"
//                };
//                for (String s : defaultSalesmen)
//                    dbHelper.getWritableDatabase().execSQL("INSERT INTO salesmanInstallValue VALUES" + s + ";");
//            }
//        }
//    }
//
//    private void insertDefaultValidNumbers() {
//        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM validNumber", null)) {
//            if (cursor.getCount() == 0) {
//                dbHelper.getWritableDatabase().execSQL("INSERT INTO validNumber VALUES('+639173054435','PROGRAMMER');");
//                dbHelper.getWritableDatabase().execSQL("INSERT INTO validNumber VALUES('+639177105901','PROGRAMMER');");
//            }
//        }
//    }
//
//    /** Permissions & GPS */
//    private void checkPermissions() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { checkAndEnableGPS(); return; }
//
//        String[] permissions = {
//                Manifest.permission.SEND_SMS,
//                Manifest.permission.RECEIVE_SMS,
//                Manifest.permission.READ_SMS,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//        };
//
//        List<String> toRequest = new ArrayList<>();
//        for (String p : permissions)
//            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) toRequest.add(p);
//
//        if (!toRequest.isEmpty())
//            ActivityCompat.requestPermissions(this, toRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
//        else checkAndEnableGPS();
//    }
//
//    private void checkAndEnableGPS() {
//        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//        if (lm != null && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) askToEnableGPS();
//    }
//
//    private void askToEnableGPS() {
//        new AlertDialog.Builder(this)
//                .setIcon(R.drawable.mars_logo)
//                .setTitle("Enable GPS")
//                .setMessage("GPS is required. Please enable it.")
//                .setPositiveButton("Turn On", (dialog, which) ->
//                        gpsLauncher.launch(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
//                .setNegativeButton("Cancel", null)
//                .setCancelable(false)
//                .show();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            boolean locationGranted = false;
//            boolean smsGranted = false;
//            boolean allGranted = true;
//
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                    allGranted = false;
//                    // optional: detect "Don't ask again"
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                        Toast.makeText(this, permissions[i] + " denied permanently. Enable it in settings.", Toast.LENGTH_LONG).show();
//                        openAppSettings();
//                        return; // stop further checks
//                    }
//                }
//                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i]) &&
//                        grantResults[i] == PackageManager.PERMISSION_GRANTED) locationGranted = true;
//                if ((permissions[i].contains("SMS")) && grantResults[i] == PackageManager.PERMISSION_GRANTED) smsGranted = true;
//            }
//
//            if (allGranted) {
//                Toast.makeText(this, "All required permissions granted.", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Some permissions were denied. Please enable them in settings.", Toast.LENGTH_LONG).show();
//                openAppSettings();
//            }
//
//            if (locationGranted) checkAndEnableGPS();
//            if (smsGranted) askToBeDefaultSmsApp();
//        }
//    }
//
//    private void openAppSettings() {
//        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(android.net.Uri.fromParts("package", getPackageName(), null));
//        startActivity(intent);
//    }
//
//    private void askToBeDefaultSmsApp() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            RoleManager rm = getSystemService(RoleManager.class);
//            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_SMS) && !rm.isRoleHeld(RoleManager.ROLE_SMS))
//                startActivity(rm.createRequestRoleIntent(RoleManager.ROLE_SMS));
//        }
//    }
//
//    /** Salesman Autocomplete */
//    private void syncSalesmanData() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>()) {
//            @Override
//            public Filter getFilter() {
//                return new Filter() {
//                    @Override
//                    protected FilterResults performFiltering(CharSequence constraint) {
//                        FilterResults results = new FilterResults();
//                        List<String> suggestions = new ArrayList<>();
//                        if (constraint != null && constraint.length() > 0) {
//                            try (Cursor cursor = dbHelper.getReadableDatabase()
//                                    .rawQuery("SELECT SalesmanID FROM salesmanInstallValue WHERE SalesmanID LIKE ? ORDER BY SalesmanID",
//                                            new String[]{"%" + constraint + "%"})) {
//                                while (cursor.moveToNext()) suggestions.add(cursor.getString(0));
//                            }
//                        }
//                        results.values = suggestions;
//                        results.count = suggestions.size();
//                        return results;
//                    }
//
//                    @Override
//                    protected void publishResults(CharSequence constraint, FilterResults results) {
//                        clear();
//                        if (results != null && results.count > 0) addAll((List<String>) results.values);
//                        notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public CharSequence convertResultToString(Object resultValue) {
//                        return resultValue.toString();
//                    }
//                };
//            }
//        };
//
//        salesmanId.setAdapter(adapter);
//        salesmanId.setThreshold(1);
//        salesmanId.setOnItemClickListener((parent, view, position, id) -> {
//            String selected = parent.getItemAtPosition(position).toString();
//            salesmanId.setText(selected);
//            try (Cursor cursor = dbHelper.getReadableDatabase()
//                    .rawQuery("SELECT Department, BranchName, LockLocation FROM salesmanInstallValue WHERE SalesmanID = ?", new String[]{selected})) {
//                if (cursor.moveToFirst()) {
//                    mDepartment.setText(cursor.getString(0));
//                    branchName.setText(cursor.getString(1));
//                    lockLocation.setText(cursor.getString(2));
//                }
//            }
//        });
//    }
//}
