//package com.marsIT.collection_app.CollectionStartUp;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.pm.PackageManager;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.content.Intent;
//import android.database.Cursor;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.Toast;
//import android.widget.Filter;
//import android.widget.ArrayAdapter;
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
//
//    private final ActivityResultLauncher<Intent> gpsLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> checkAndEnableGPS());
//
//    @SuppressLint("Range")
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
//        // Check saved installation status
//        boolean installed = getSharedPreferences("APP_SETUP", MODE_PRIVATE)
//                .getBoolean("INSTALLED", false);
//
//        boolean installDone = getSharedPreferences("APP_SETUP", MODE_PRIVATE)
//                .getBoolean("INSTALL_DONE", false);
//
//        // If installation fully done → go directly to MainActivity
//        if (installed && installDone) {
//            String installedSalesmanID = getSharedPreferences("APP_SETUP", MODE_PRIVATE)
//                    .getString("INSTALLED_SALESMAN_ID", "");
//            if (!installedSalesmanID.isEmpty()) {
//                try (Cursor cursor = dbHelper.getReadableDatabase()
//                        .rawQuery("SELECT * FROM InstallValue WHERE SalesmanID = ?", new String[]{installedSalesmanID})) {
//                    if (cursor.moveToFirst()) {
//                        Intent intent = new Intent(this, MainActivity.class);
//                        intent.putExtra("SalesmanID", cursor.getString(cursor.getColumnIndex("SalesmanID")));
//                        intent.putExtra("Department", cursor.getString(cursor.getColumnIndex("Department")));
//                        intent.putExtra("BranchName", cursor.getString(cursor.getColumnIndex("BranchName")));
//                        intent.putExtra("LockLocation", cursor.getString(cursor.getColumnIndex("LockLocation")));
//                        startActivity(intent);
//                        finish();
//                        return;
//                    }
//                }
//            }
//        }
//
//        // If installed but NOT completed → go to InstallationProcess
//        if (installed && !installDone) {
//            startActivity(new Intent(this, InstallationProcess.class));
//            finish();
//            return;
//        }
//
//        checkPermissions();
//        insertDefaultSalesmen();
//        syncSalesmanData();
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
//    @SuppressLint("Range")
//    private void installSelectedSalesman() {
//        String salesman = salesmanId.getText().toString().trim();
//        if (salesman.isEmpty()) {
//            Toast.makeText(this, "Select a salesman", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        try (Cursor c = dbHelper.getReadableDatabase()
//                .rawQuery("SELECT * FROM salesmanInstallValue WHERE SalesmanID = ?", new String[]{salesman})) {
//
//            if (c.moveToFirst()) {
//                // Save installation started (not finished yet)
//                getSharedPreferences("APP_SETUP", MODE_PRIVATE)
//                        .edit()
//                        .putBoolean("INSTALLED", true)
//                        .putBoolean("INSTALL_DONE", false)
//                        .putString("INSTALLED_SALESMAN_ID", salesman) // Save selected salesman
//                        .apply();
//
//                // Pass selected salesman info to InstallationProcess
//                Intent intent = new Intent(this, InstallationProcess.class);
//                intent.putExtra("SalesmanID", c.getString(c.getColumnIndex("SalesmanID")));
//                intent.putExtra("Department", c.getString(c.getColumnIndex("Department")));
//                intent.putExtra("BranchName", c.getString(c.getColumnIndex("BranchName")));
//                intent.putExtra("LockLocation", c.getString(c.getColumnIndex("LockLocation")));
//                startActivity(intent);
//                finish();
//
//            } else {
//                Toast.makeText(this, "Salesman not found.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    /** Default data insertion */
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
//    /** Permissions & GPS */
//    private void checkPermissions() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { checkAndEnableGPS(); return; }
//
//        String[] permissions = {
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
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
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
//            boolean allGranted = true;
//
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                    allGranted = false;
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                        Toast.makeText(this, permissions[i] + " denied permanently. Enable it in settings.", Toast.LENGTH_LONG).show();
//                        openAppSettings();
//                        return;
//                    }
//                }
//                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i]) &&
//                        grantResults[i] == PackageManager.PERMISSION_GRANTED) locationGranted = true;
//            }
//
//            if (!allGranted) {
//                Toast.makeText(this, "Some permissions were denied. Please enable them in settings.", Toast.LENGTH_LONG).show();
//                openAppSettings();
//            }
//
//            if (locationGranted) checkAndEnableGPS();
//        }
//    }
//
//    private void openAppSettings() {
//        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(android.net.Uri.fromParts("package", getPackageName(), null));
//        startActivity(intent);
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
