//package com.marsIT.collection_app.MainProgram;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.telephony.SmsManager;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.Priority;
//
//import com.google.android.material.textfield.TextInputEditText;
//import com.marsIT.collection_app.CollectionStartUp.StartUp;
//import com.marsIT.collection_app.R;
//import com.marsIT.collection_app.ToolBar.BaseToolbar;
//import com.marsIT.collection_app.UpdateSQLpackages.UrcData;
//
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.TimeZone;
//
//public class MainActivity extends BaseToolbar {
//
//    private final Handler distanceHandler = new Handler(Looper.getMainLooper());
//    private Runnable distanceRunnable;
//    private LocationRequest locationRequest;
//
//    boolean customerSelected = false; // <-- Add this at class level (global)
//    private final LocationListener locationListener = new syncLocationListener();
//
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//
//    private final Handler mHandler = new Handler();
//
//    private Boolean syncLocation = false; //LoopGetL
//    private Boolean PressLocation = false;
//
//    private boolean enabledGPS = false; //gps_enabled
//    private boolean enabledNetwork = false; //network_enabled
//
//    private LocationManager locationManager;
//
//    private double startLatitude; //= 7.0137417;
//    private double startLongitude; //= 125.4941049;
//    private double endLatitude; //= 7.0136241;
//    private double endLongitude; // = 125.4921331;
//
//    private String custdistance_meter; // = 125.4921331;
//
//    private String newLongitudeLoop = "0";
//    private String newLatitudeLoop = "0";
//
//    private String newLongitude = "0";
//    private String newLatitude = "0";
//
//    private String newnewLongitudeIncr = "0";
//    private String newLatitudeAddIncr = "0";
//
//    private String newLongitudeDcrs = "0";
//    private String newLatitudeDcrs = "0";
//
//    private String longitude = "";
//    private String latitude = "";
//
//    private String simNumber = "";
//    private String syncMessage = "";
//
//    private String synBranchName;
//
//    private List<String> lables;
//    private SQLiteDatabase db;
//
//    private String
//            SalesmanID = "",
//            Department = "",
//            Status = "",
//            BranchName = "",
//            LockLocation = "";
//
//    AutoCompleteTextView searchCustomer;
//    AutoCompleteTextView selectCustomer;
//    TextInputEditText customerDistance;
//    TextInputEditText customerCode;
//    TextInputEditText customerAddress;
//    Button btnClearSearch;
//
//    private String selectQuery;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mainlayout);
//        setupToolbar("MAVCIV13");
//
//        try {
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//
//            //TODO: Initialize views
//            searchCustomer = findViewById(R.id.mainCustomerName);
//            selectCustomer = findViewById(R.id.mainCustomerList);
//            customerDistance = findViewById(R.id.mainCustomerDistance);
//            customerCode = findViewById(R.id.mainCustomerCode);
//            customerAddress = findViewById(R.id.mainCustomerAddress);
//            btnClearSearch = findViewById(R.id.btnClearSearch);
//
//            //TODO: Ensure InstallValue table exists
//            db.execSQL("CREATE TABLE IF NOT EXISTS InstallValue (" +
//                    "SalesmanID TEXT, " +
//                    "Department TEXT, " +
//                    "Status TEXT, " +
//                    "BranchName TEXT, " +
//                    "LockLocation TEXT);");
//
//            //TODO: Check if salesman is already installed
//            Cursor c2 = db.rawQuery("SELECT * FROM InstallValue", null);
//            c2.moveToFirst();
//            int cnt2 = c2.getCount();
//
//            if (cnt2 > 0) {
//                SalesmanID = c2.getString(0);
//                Department = c2.getString(1);
//                Status = c2.getString(2);
//                BranchName = c2.getString(3);
//                LockLocation = c2.getString(4);
//
//                Toast.makeText(this, "Welcome " + SalesmanID, Toast.LENGTH_LONG).show();
//
//                //TODO: If not yet installed, run setup
//                if ("N".equals(Status)) {
//                    Toast.makeText(getApplicationContext(), "Installing Please Wait", Toast.LENGTH_LONG).show();
//
//                    //TODO: Call the method that creates all required tables
//                    createRequiredTables();
//
//                    //TODO: Department-specific data installation
//                    if ("UC".equals(Department)) {
//                        Intent intent = new Intent(MainActivity.this, UrcData.class);
//                        intent.putExtra("transinv", "Booking");
//                        intent.putExtra("DeptCode", Department);
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(this, "No matching department found", Toast.LENGTH_LONG).show();
//                    }
//
//                    //TODO: Update InstallValue status
//                    db.execSQL("UPDATE InstallValue SET Status = 'Y';");
//                    Toast.makeText(this, "Installation Finished", Toast.LENGTH_LONG).show();
//                    MainActivity.this.finish();
//
//                } else {
////                    Toast.makeText(this, "Already installed. Welcome back, " + SalesmanID, Toast.LENGTH_SHORT).show();
//                    Log.d("INSTALLATION", "Already installed. Welcome back, " + SalesmanID);
//                }
//
//            } else {
//                //TODO: No data -> go to StartUp screen
//                mHandler.removeCallbacks(loopSyncLocation);
//                Intent intent = new Intent(MainActivity.this, StartUp.class);
//                startActivity(intent);
//                MainActivity.this.finish();
//            }
//            c2.close();
//
//        } catch (Exception e) {
//            Log.e("MainActivity", "Error: " + e.getMessage());
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//        // =========================================================
//        /** SEARCH CUSTOMER + LIVE GPS COORDINATES */
//        // =========================================================
//        if (searchCustomer != null) {
//            searchCustomer.addTextChangedListener(new TextWatcher() {
//                private final Handler handler = new Handler(Looper.getMainLooper());
//                private Runnable searchRunnable;
//
//                @Override
//                public void onTextChanged(CharSequence cs, int start, int before, int count) {
//                    if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
//
//                    searchRunnable = () -> {
//                        String text = cs.toString().trim();
//
//                        if (!text.isEmpty()) {
//                            //TODO: START GPS UPDATES WHEN USER TYPES
//                            if (text.length() == 1 && !syncLocation) {
//                                if (ContextCompat.checkSelfPermission(MainActivity.this,
//                                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//                                        ContextCompat.checkSelfPermission(MainActivity.this,
//                                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                                    try {
//                                        if (locationManager != null) {
//                                            locationManager.requestLocationUpdates(
//                                                    LocationManager.GPS_PROVIDER,
//                                                    1000, //TODO: every 1 second
//                                                    1,    //TODO: every 1 meter
//                                                    locationListener
//                                            );
//                                            syncLocation = true; //TODO: mark as started
//                                            Log.d("GPS_AUTO", "GPS started after first letter typed");
//                                        }
//                                    } catch (Exception e) {
//                                        Log.e("GPS_AUTO", "Error starting GPS updates: " + e.getMessage());
//                                    }
//                                } else {
//                                    ActivityCompat.requestPermissions(MainActivity.this,
//                                            new String[]{
//                                                    Manifest.permission.ACCESS_FINE_LOCATION,
//                                                    Manifest.permission.ACCESS_COARSE_LOCATION
//                                            }, 1001);
//                                }
//                            }
//
//                            //TODO: SEARCH CUSTOMER IN DATABASE
//                            selectQuery = "SELECT cname FROM customers WHERE cname LIKE '%" + text + "%' ORDER BY CNAME";
////                            customerDistance.setText("Please! Select Customer First.");
//                            syncData(); //TODO: update dropdown list
//
//                        } else {
//                            //TODO: STOP GPS WHEN INPUT CLEARED
//                            if (locationManager != null && locationListener != null) {
//                                try {
//                                    locationManager.removeUpdates(locationListener);
//                                    syncLocation = false;
//                                    Log.d("GPS_AUTO", "Stopped GPS updates (input cleared).");
//                                } catch (Exception e) {
//                                    Log.e("GPS_AUTO", "Error stopping GPS: " + e.getMessage());
//                                }
//                            }
//                        }
//                    };
//
//                    //TODO: Delay database query slightly to prevent lag while typing fast
//                    handler.postDelayed(searchRunnable, 1000);
//                }
//
//                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//                @Override public void afterTextChanged(Editable s) {}
//            });
//        }
//
//        // =========================================================
//        /** HANDLE CUSTOMER SELECTION FROM DROPDOWN */
//        // =========================================================
//        if (selectCustomer != null) {
//            selectCustomer.setOnItemClickListener((parent, view, position, id) -> {
//                String selectedName = parent.getItemAtPosition(position).toString();
//                showCustomerDetails(selectedName); // show details automatically
//
//                // Update linked search field (if used)
//                if (searchCustomer != null) {
//                    searchCustomer.setText(selectedName);
//                }
//
//                // Optionally trigger distance recalculation
//                new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                    try {
//                        syncCustomerDistance();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        if (customerDistance != null)
//                            customerDistance.setText("Error fetching distance");
//                    }
//                }, 1000);
//                syncLocation = true;
//                Log.d("CustomerSelect", "Selected customer: " + selectedName);
//            });
//        }
//
//        // =========================================================
//        /** CLEAR ALL DATA BUTTON LOGIC */
//        // =========================================================
//        if (btnClearSearch != null) {
//            btnClearSearch.setOnClickListener(v -> {
//                try {
//                    //TODO: Clear text fields
//                    searchCustomer.setText("");
//                    selectCustomer.setText("");
//                    customerDistance.setText("");
//                    customerCode.setText("");
//                    customerAddress.setText("");
//
//                    //TODO: Stop GPS updates
//                    if (locationManager != null && locationListener != null) {
//                        locationManager.removeUpdates(locationListener);
//                        Log.d("GPS_CLEAR", "GPS updates stopped.");
//                    }
//
//                    //TODO: Reset coordinates
//                    newLongitudeDcrs = "0";
//                    newLatitudeDcrs = "0";
//                    newLongitude = "0";
//                    newLatitude = "0";
//                    newLongitudeLoop = "0";
//                    newLatitudeLoop = "0";
//
//                    //TODO: Stop any sync loops if active
//                    syncLocation = false;
//                    PressLocation = false;
//
//                    Toast.makeText(MainActivity.this, "All data cleared", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Log.e("CLEAR_ALL", "Error clearing data: " + e.getMessage());
//                }
//            });
//        }
//
//        // Start the automatic distance update loop
//        mHandler.post(loopSyncLocation);
//    }
//
//    // =========================================================
//    /** Create all required database tables for installation setup. */
//    // =========================================================
//    private void createRequiredTables() {
//        try {
//            db.execSQL("CREATE TABLE IF NOT EXISTS CUSTOMERS(" +
//                    "CID VARCHAR, CNAME VARCHAR, CTYPE VARCHAR, ATYPE VARCHAR, TERMS VARCHAR, CPERSON VARCHAR," +
//                    "CBDAY DATE, CTELLNUM VARCHAR, CCELLNUM VARCHAR, OWNER VARCHAR, OBDAY DATE, OTELLNUM VARCHAR," +
//                    "OCELLNUM VARCHAR, STREET VARCHAR, BARANGAY VARCHAR, MUNICIPALITY VARCHAR, PROVINCE VARCHAR," +
//                    "SUBPOCKET VARCHAR, CALTEX VARCHAR(1), MOBIL VARCHAR(1), CASTROL VARCHAR(1), PETRON VARCHAR(1)," +
//                    "OTHER VARCHAR, CAF VARCHAR(1), DTI VARCHAR(1), BPERMIT VARCHAR(1), SIGNAGE VARCHAR(1)," +
//                    "POSTER VARCHAR(1), STREAMER VARCHAR(1), STICKER VARCHAR(1), DISRACK VARCHAR(1), PRODISPLAY VARCHAR(1)," +
//                    "Latitude VARCHAR, Longitude VARCHAR, status VARCHAR(1), creditline VARCHAR, balCredit VARCHAR," +
//                    "OCheck VARCHAR, CreditStatus VARCHAR);");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS OtherCUSTOMERS(" +
//                    "CID VARCHAR, CNAME VARCHAR, CTYPE VARCHAR, CPERSON VARCHAR, CTELLNUM VARCHAR, CCELLNUM VARCHAR," +
//                    "STREET VARCHAR, BARANGAY VARCHAR, MUNICIPALITY VARCHAR, PROVINCE VARCHAR, Latitude VARCHAR," +
//                    "Longitude VARCHAR, Grouptype VARCHAR, RouteCode VARCHAR, Source VARCHAR);");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS PaymentDetails(" +
//                    "Refid VARCHAR, PaymentType VARCHAR, BankInitial VARCHAR, checkNumber VARCHAR," +
//                    "accountNumber VARCHAR, checkdate VARCHAR, amount VARCHAR);");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS PaymentDetailsTemp(" +
//                    "PaymentType VARCHAR, BankInitial VARCHAR, checkNumber VARCHAR, accountNumber VARCHAR," +
//                    "checkdate VARCHAR, amount VARCHAR);");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS Barangay(" +
//                    "CODE VARCHAR(20), BARANGAY VARCHAR(100), MUNICIPALITY_CODE VARCHAR(100), " +
//                    "MUNICIPALITY VARCHAR(100), PROVINCE_CODE VARCHAR(100), PROVINCE VARCHAR(100));");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS customerUnlock(CustomerID VARCHAR);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS receiverNumber(NUM VARCHAR);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS department(Department TEXT);");
//
//        } catch (Exception e) {
//            Log.e("DBSetup", "Error creating tables: " + e.getMessage());
//        }
//    }
//    // =========================================================
//    /** Create all required database tables for installation setup. */
//    // =========================================================
//
//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
//    @Override
//    protected void onResume() { //TODO: Register the Receiver
//        super.onResume();
////        registerReceiver(notificationReceiver, intentFilter);
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    protected void onPause() { //TODO: Unregister the Receiver
//        super.onPause();
////        unregisterReceiver(notificationReceiver);
//    }
//
//    // =========================================================
//    /** CUSTOMER DISTANCE: FETCHING DISTANCE */
//    // =========================================================
//
//    @SuppressLint("MissingPermission")
//    private void syncCustomerDistance() {
//        if (distanceRunnable != null) {
//            distanceHandler.removeCallbacks(distanceRunnable); // remove previous loop if any
//        }
//
//        distanceRunnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String queryCustomer = searchCustomer.getText().toString().trim();
//                    if (queryCustomer.isEmpty()) {
//                        // Do nothing silently when no customer selected
//                        distanceHandler.postDelayed(this, 1000);
//                        return;
//                    }
//
//                    // Get database customer coordinates
//                    if (db == null || !db.isOpen()) {
//                        db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//                    }
//
//                    Cursor c2 = db.rawQuery(
//                            "SELECT status, Latitude, Longitude FROM Customers WHERE UPPER(cname) LIKE '%' || UPPER(?) || '%' LIMIT 1",
//                            new String[]{queryCustomer}
//                    );
//
//                    if (!c2.moveToFirst()) {
//                        runOnUiThread(() -> customerDistance.setText("Customer not found."));
//                        custdistance_meter = "OUT";
//                        c2.close();
//                        distanceHandler.postDelayed(this, 1000);
//                        return;
//                    }
//
//                    String status = c2.getString(0);
//                    String latStr = c2.getString(1);
//                    String longStr = c2.getString(2);
//                    c2.close();
//
//                    if (!"M".equals(status)) {
//                        runOnUiThread(() -> customerDistance.setText("Customer is not mapped."));
//                        custdistance_meter = "OUT";
//                        distanceHandler.postDelayed(this, 1000);
//                        return;
//                    }
//
//                    try {
//                        double targetLat = Double.parseDouble(latStr);
//                        double targetLong = Double.parseDouble(longStr);
//
//                        if (targetLat == 0.0 || targetLong == 0.0) {
//                            runOnUiThread(() -> customerDistance.setText("Invalid customer location."));
//                            custdistance_meter = "OUT";
//                            distanceHandler.postDelayed(this, 1000);
//                            return;
//                        }
//
//                        // Use FusedLocationProviderClient for current user position
//                        FusedLocationProviderClient fusedLocationClient =
//                                LocationServices.getFusedLocationProviderClient(MainActivity.this);
//
//                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//                                .addOnSuccessListener(location -> {
//                                    if (location != null) {
//                                        double currentLat = location.getLatitude();
//                                        double currentLong = location.getLongitude();
//
//                                        // Calculate real-time distance
//                                        float[] results = new float[1];
//                                        Location.distanceBetween(currentLat, currentLong, targetLat, targetLong, results);
//                                        float distance = results[0];
//
//                                        // Update IN/OUT status
//                                        custdistance_meter = (distance > 0 && distance < 6) ? "IN" : "OUT";
//
//                                        // Format readable distance
//                                        String dis = (distance >= 1000)
//                                                ? String.format(Locale.getDefault(), "%.2f Kilometers", distance / 1000)
//                                                : String.format(Locale.getDefault(), "%.2f Meters", distance);
//
//                                        // Show real-time distance on screen
//                                        runOnUiThread(() -> customerDistance.setText(dis));
//
//                                        Log.d("DISTANCE_REALTIME", "Updated distance: " + dis);
//                                    } else {
//                                        runOnUiThread(() -> customerDistance.setText("Unable to get current location."));
//                                        custdistance_meter = "OUT";
//                                    }
//                                })
//                                .addOnFailureListener(e -> {
//                                    runOnUiThread(() -> customerDistance.setText("Location error."));
//                                    Log.e("DISTANCE_ERROR", e.getMessage());
//                                });
//
//                    } catch (NumberFormatException e) {
//                        runOnUiThread(() -> customerDistance.setText("Invalid coordinates."));
//                        custdistance_meter = "OUT";
//                    }
//
//                } catch (Exception e) {
//                    Log.e("DistanceError", "Unexpected error", e);
//                    runOnUiThread(() -> customerDistance.setText("Unexpected error."));
//                    custdistance_meter = "OUT";
//                }
//
//                // Repeat real-time check every 500ms (half-second)
//                distanceHandler.postDelayed(this,1000);
//            }
//        };
//
//        // Start continuous real-time loop
//        distanceHandler.post(distanceRunnable);
//    }
//
//
//    /** CONTINUOUS LOCATION UPDATER LOOP (UPDATES DISTANCE PER SECOND) */
//    private final Runnable loopSyncLocation = new Runnable() {
//        @Override
//        public void run() {
//            syncLocation = true;
//
//            try {
//                enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            } catch (Exception e) {
//                Log.e("LoopGetL", "GPS ENABLED: " + e.getMessage(), e);
//            }
//
//            try {
//                enabledNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            } catch (Exception e) {
//                Log.e("LoopGetL", "NETWORK ENABLED: " + e.getMessage(), e);
//            }
//
//            // Skip if no provider enabled
//            if (!enabledGPS && !enabledNetwork) {
//                Log.d("GPS PROVIDER", "GPS disabled, skipping distance check.");
//                mHandler.postDelayed(this, 1000); // Try again in 1 second
//                return;
//            }
//
//            if (enabledGPS) {
//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                    // Request permissions if not granted
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
//                    return;
//                }
//
//                try {
//                    // Re-request GPS updates for accuracy
//                    locationManager.removeUpdates(locationListener);
//                    locationManager.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER,
//                            1000, // Minimum time between updates (1 second)
//                            1,    // Minimum distance change in meters
//                            locationListener
//                    );
//
//                    // Auto-update distance only if a customer is selected
//                    if (searchCustomer != null && !searchCustomer.getText().toString().trim().isEmpty()) {
//                        syncCustomerDistance();  // update distance automatically
//                        Log.d("DISTANCE_LOOP", "Distance updated automatically for: " + searchCustomer.getText().toString());
//                    } else {
//                        Log.d("DISTANCE_LOOP", "No customer selected, skipping distance update.");
//                    }
//
//                } catch (Exception e) {
//                    Log.e("LoopGetL", "Error updating GPS: " + e.getMessage(), e);
//                }
//            }
//
//            // Repeat this loop every second
//            mHandler.postDelayed(this, 1000);
//        }
//    };
//
//    /** LOCATION LISTENER */
//    class syncLocationListener implements LocationListener {
//        @Override
//        public void onLocationChanged(Location location) {
//            if (location != null) {
//                locationManager.removeUpdates(locationListener);
//
//                longitude = String.valueOf(location.getLongitude());
//                latitude = String.valueOf(location.getLatitude());
//                String accuracy = String.valueOf(location.getAccuracy());
//
//                //TODO: Latitude/Longitude computations preserved from your logic
//                //TODO: (shortened for clarity — your original substring trimming and formatting remain)
//                DecimalFormat formatter = new DecimalFormat("###.0000");
//                startLatitude = location.getLatitude();
//                startLongitude = location.getLongitude();
//
//                newLatitudeAddIncr = formatter.format(startLatitude + 0.0001);
//                newLatitudeDcrs = formatter.format(startLatitude - 0.0001);
//                newnewLongitudeIncr = formatter.format(startLongitude + 0.0001);
//                newLongitudeDcrs = formatter.format(startLongitude - 0.0001);
//
//                //TODO: Update your UI or variables continuously
//                Log.d("GPS_LOOP", "Lat: " + latitude + " | Lng: " + longitude);
//
//                if (PressLocation) {
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setIcon(R.drawable.mars_logo)
//                            .setTitle("Location Details")
//                            .setMessage(
//                                    "LATITUDE DETAILS " + "\n" +
//                                            "Latitude: " + latitude + "\n" +
//                                            "Lat +: " + newLatitudeAddIncr + "\n" +
//                                            "Lat -: " + newLatitudeDcrs + "\n\n" +
//                                            "LONGITUDE DETAILS " + "\n" +
//                                            "Longitude: " + longitude + "\n" +
//                                            "Long +: " + newnewLongitudeIncr + "\n" +
//                                            "Long -: " + newLongitudeDcrs + "\n\n" +
//                                            "Accuracy: " + accuracy
//                            )
//                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                            .show();
//
//                    //TODO: SMS + sync preserved
//                    if ("MARS2".equals(synBranchName)) {
//                        syncMessage = "C2LOC!";
//                    } else {
//                        syncMessage = "CLOC!";
//                    }
//
//                    SimpleDateFormat datetimesyntax = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
//                    datetimesyntax.setTimeZone(TimeZone.getTimeZone("UTC"));
//                    String datetimesyntaxval = datetimesyntax.format(new Date());
//
//                    syncMessage = syncMessage + latitude + "!" + longitude + "!" + datetimesyntaxval;
////                    sendSMS(simNumber, syncMessage);
//
//                    //TODO: Refresh customer list
//                    selectQuery = "SELECT cname FROM customers " +
//                            "WHERE (latitude LIKE '%" + newLatitude + "%' AND longitude LIKE '%" + newLongitude + "%') " +
//                            "ORDER BY CNAME";
//
//                    syncData();
//                    loopSyncLocation.run();
//                    PressLocation = false;
//                }
//
//            } else {
//                new AlertDialog.Builder(MainActivity.this)
//                        .setIcon(R.drawable.mars_logo)
//                        .setTitle("Location Error")
//                        .setMessage("GPS not enabled or can't get location!")
//                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                        .show();
//
//                if (enabledNetwork) {
//                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                            ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                                1001);
//                        return;
//                    }
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//                }
//            }
//        }
//
//        @Override public void onProviderDisabled(@NonNull String provider) {
//            // TODO Auto-generated method stub
//        }
//        @Override public void onProviderEnabled(@NonNull String provider) {
//            // TODO Auto-generated method stub
//        }
//        @Override public void onStatusChanged(@NonNull String provider, int status, @NonNull Bundle extras) {
//            // TODO Auto-generated method stub
//        }
//    }
//    // =========================================================
//    /** CUSTOMER DISTANCE: FETCHING DISTANCE */
//    // =========================================================
//
//
//    // =========================================================
//    /** SYNC DATA: DISPLAY CUSTOMER DETAILS ON SELECTION */
//    // =========================================================
//    @SuppressLint("SetTextI18n")
//    private void syncData() {
//        new Thread(() -> {
//            List<String> tempLabels = new ArrayList<>();
//
//            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
//                Log.d("SYNC_DATA", "Running query: " + selectQuery); // Debug line
//                if (cursor.moveToFirst()) {
//                    do {
//                        String cname = cursor.getString(0);
//                        if (cname != null) tempLabels.add(cname);
//                    } while (cursor.moveToNext());
//                }
//            } catch (Exception e) {
//                Log.e("SYNC_DATA", "DB Error: " + e.getMessage(), e);
//            }
//
//            runOnUiThread(() -> {
//                lables = new ArrayList<>(tempLabels);
//
//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
//                        this,
//                        android.R.layout.simple_dropdown_item_1line,
//                        lables
//                );
//                selectCustomer.setAdapter(dataAdapter);
//
//                /** HANDLE CUSTOMER SELECTION AND FETCH DISTANCE */
//                selectCustomer.setOnItemClickListener((parent, view, position, id) -> {
//                    String selectedName = (String) parent.getItemAtPosition(position);
//
//                    //TODO: Update UI for selected customer
//                    showCustomerDetails(selectedName);
//
//                    //TODO: Sync searchCustomer (the one used in custgetdistance)
//                    if (searchCustomer != null) {
//                        searchCustomer.setText(selectedName);
//                    }
//
//                    //TODO: Show temporary message
//                    if (customerDistance != null) {
//                        // customerDistance.setText("Calculating distance...");
//                        customerDistance.setText("");
//                    }
//
//                    //TODO: Run distance calculation immediately (no delay)
//                    new Thread(() -> {
//                        try {
//                            syncCustomerDistance(); //TODO: instant distance display
//                            Log.d("DISTANCE_IMMEDIATE", "Distance displayed for: " + selectedName);
//                        } catch (Exception e) {
//                            Log.e("DISTANCE_IMMEDIATE", "Error displaying distance: " + e.getMessage());
//                        }
//                    }).start();
//
//                    //TODO: Continuously auto-refresh distance every 0.5 second
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                syncCustomerDistance(); // 🔹 auto-refresh
//                                Log.d("DISTANCE_LOOP", "Distance auto-refreshed for: " + selectedName);
//                            } catch (Exception e) {
//                                Log.e("DISTANCE_LOOP", "Error refreshing distance: " + e.getMessage());
//                            }
//                            mHandler.postDelayed(this, 500); // every 0.5 second
//                        }
//                    }, 500);
//                });
//                /** HANDLE CUSTOMER SELECTION AND FETCH DISTANCE */
//
//                if (!lables.isEmpty()) {
//                    String firstCustomer = lables.get(0);
//                    selectCustomer.setText(firstCustomer, false);
//                    showCustomerDetails(firstCustomer);
//
//                    //TODO: Automatically calculate and display distance for the first customer
//                    new Thread(() -> {
//                        try {
//                            syncCustomerDistance();
//                            Log.d("DISTANCE_INIT", "Initial distance displayed for: " + firstCustomer);
//                        } catch (Exception e) {
//                            Log.e("DISTANCE_INIT", "Error showing initial distance: " + e.getMessage());
//                        }
//                    }).start();
//
//                } else {
//                    customerCode.setText("");
//                    customerAddress.setText("");
//                    if (customerDistance != null) {
//                        customerDistance.setText("No customer found.");
//                    }
//                }
//            });
//        }).start();
//    }
//    // =========================================================
//    /** SYNC DATA: DISPLAY CUSTOMER DETAILS ON SELECTION */
//    // =========================================================
//
//
//    private void showCustomerDetails(String customerName) {
//        try {
//            Cursor c2 = db.rawQuery(
//                    "SELECT street, barangay, municipality, province, CID FROM Customers WHERE cname = ? LIMIT 1",
//                    new String[]{customerName});
//
//            if (c2.moveToFirst()) {
//                String provinceCode = c2.getString(3);
//                String provinceName;
//
//                switch (provinceCode) {
//                    case "0": provinceName = "DAVAO DEL NORTE"; break;
//                    case "1": provinceName = "DAVAO ORIENTAL"; break;
//                    case "2": provinceName = "COMPOSTELA VALLEY"; break;
//                    case "3": provinceName = "NORTH COTABATO"; break;
//                    case "4": provinceName = "SOUTH COTABATO"; break;
//                    case "5": provinceName = "SARANGANI"; break;
//                    case "6": provinceName = "SULTAN KUDARAT"; break;
//                    case "7": provinceName = "MAGUINDANAO"; break;
//                    case "8": provinceName = "AGUSAN DEL SUR"; break;
//                    case "9": provinceName = "SURIGAO DEL SUR"; break;
//                    default: provinceName = "DAVAO DEL SUR"; break;
//                }
//
//                String fullAddress = "(" + c2.getString(4) + ") "
//                        + c2.getString(0) + ", "
//                        + c2.getString(1) + ", "
//                        + c2.getString(2) + ", "
//                        + provinceName;
//
//                customerCode.setText(c2.getString(4));
//                customerAddress.setText(fullAddress);
//                Log.d("CustomerDetails", "Auto Display: " + fullAddress);
//            } else {
//                customerCode.setText("");
//                customerAddress.setText("");
//            }
//            c2.close();
//        } catch (Exception e) {
//            Log.e("CustomerDetails", "Error showing data: " + e.getMessage());
//        }
//    }
//}
//
//
//
//
