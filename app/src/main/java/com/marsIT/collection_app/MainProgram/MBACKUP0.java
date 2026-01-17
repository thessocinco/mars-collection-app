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
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.telephony.SmsManager;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
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
//    private final LocationListener locationListener = new syncLocationListener();
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
//
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//
//            // Initialize views
//            searchCustomer = findViewById(R.id.mainCustomerName);
//            selectCustomer = findViewById(R.id.mainCustomerList);
//            customerDistance = findViewById(R.id.mainCustomerDistance);
//            customerCode = findViewById(R.id.mainCustomerCode);
//            customerAddress = findViewById(R.id.mainCustomerAddress);
//
//            // Ensure InstallValue table exists
//            db.execSQL("CREATE TABLE IF NOT EXISTS InstallValue (" +
//                    "SalesmanID TEXT, " +
//                    "Department TEXT, " +
//                    "Status TEXT, " +
//                    "BranchName TEXT, " +
//                    "LockLocation TEXT);");
//
//            // Check if salesman is already installed
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
//                // If not yet installed, run setup
//                if ("N".equals(Status)) {
//                    Toast.makeText(getApplicationContext(), "Installing Please Wait", Toast.LENGTH_LONG).show();
//
//                    // Call the method that creates all required tables
//                    createRequiredTables();
//
//                    // Department-specific data installation
//                    if ("UC".equals(Department)) {
//                        Intent intent = new Intent(MainActivity.this, UrcData.class);
//                        intent.putExtra("transinv", "Booking");
//                        intent.putExtra("DeptCode", Department);
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(this, "No matching department found", Toast.LENGTH_LONG).show();
//                    }
//
//                    // Update InstallValue status
//                    db.execSQL("UPDATE InstallValue SET Status = 'Y';");
//                    Toast.makeText(this, "Installation Finished", Toast.LENGTH_LONG).show();
//                    MainActivity.this.finish();
//
//                } else {
//                    Toast.makeText(this, "Already installed. Welcome back, " + SalesmanID, Toast.LENGTH_SHORT).show();
//                }
//
//            } else {
//                // No data → go to StartUp screen
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
//        // Inside onCreate(), after initializing all views
//        if (searchCustomer != null) {
//            searchCustomer.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void onTextChanged(CharSequence cs, int start, int before, int count) {
//                    if (cs.length() >= 1) { // trigger on 1 or more letters
//                        selectQuery = "SELECT cname FROM customers WHERE cname LIKE '%" + cs + "%' ORDER BY CNAME";
//                        syncData(); // pass the typed text
//                    }
//                }
//
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                @Override
//                public void afterTextChanged(Editable s) {}
//            });
//        }
//
//        // When user manually selects a customer from dropdown
//        if (selectCustomer != null) {
//            selectCustomer.setOnItemClickListener((parent, view, position, id) -> {
//                String selectedName = parent.getItemAtPosition(position).toString();
//                showCustomerDetails(selectedName); // show details automatically
//                Log.d("CustomerSelect", "Selected customer: " + selectedName);
//            });
//        }
//    }
//
//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
//    @Override
//    protected void onResume() { // Register the Receiver
//        super.onResume();
//        if (mHandler != null) {
//            mHandler.post(loopSyncLocation); // start looping
//        }
////        registerReceiver(notificationReceiver, intentFilter);
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    protected void onPause() { // Unregister the Receiver
//        super.onPause();
//        if (mHandler != null) {
//            mHandler.removeCallbacks(loopSyncLocation);
//        }
//        if (locationManager != null && locationListener != null) {
//            locationManager.removeUpdates(locationListener);
//        }
////        unregisterReceiver(notificationReceiver);
//    }
//
//    // =========================================================
//    // CUSTOMER DISTANCE: FETCHING DISTANCE
//    // =========================================================
////    private void custgetdistance() {
////        try {
////            // Get customer name from AutoCompleteTextView
////            String tempstr = searchCustomer.getText().toString().trim();
////            Log.d("CustomerCheck", "Selected customer: '" + tempstr + "'");
////
////            if (tempstr.isEmpty()) {
////                custdistance_meter = "OUT";
////                customerDistance.setText("No customer selected.");
////                return;
////            }
////
//////            // Show immediate status while fetching
////            customerDistance.setText("Fetching distance...");
////
////            // Initialize location client
////            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
////
////            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
////                customerDistance.setText("Location permission not granted!");
////                custdistance_meter = "OUT";
////                return;
////            }
////
////            // Fetch current location
////            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
////                    .addOnSuccessListener(location -> {
////                        if (location == null) {
////                            customerDistance.setText("Unable to fetch current location.");
////                            custdistance_meter = "OUT";
////                            return;
////                        }
////
////                        startLatitude = location.getLatitude();
////                        startLongitude = location.getLongitude();
////
////                        // Query customer data
////                        SQLiteDatabase db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
////                        Cursor c2 = db.rawQuery("SELECT street, barangay, municipality, province, cid, status, Latitude, Longitude FROM Customers WHERE cname = ?", new String[]{tempstr});
////
////                        if (c2.moveToFirst()) {
////                            String status = c2.getString(5);
////                            if ("M".equals(status)) {
////                                try {
////                                    endLatitude = Double.parseDouble(c2.getString(6));
////                                    endLongitude = Double.parseDouble(c2.getString(7));
////
////                                    if (endLatitude == 0.0 || endLongitude == 0.0) {
////                                        customerDistance.setText("Invalid customer location.");
////                                        custdistance_meter = "OUT";
////                                        return;
////                                    }
////
////                                    float[] results = new float[1];
////                                    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
////                                    float distance = results[0];
////
////                                    custdistance_meter = (distance > 0 && distance < 6) ? "IN" : "OUT";
////                                    String dis = distance >= 1000 ? (distance / 1000) + " Kilometers" : distance + " Meters";
////                                    customerDistance.setText(dis);
////
////                                } catch (NumberFormatException e) {
////                                    customerDistance.setText("Invalid coordinates.");
////                                    custdistance_meter = "OUT";
////                                }
////                            } else {
////                                customerDistance.setText("Customer is not mapped.");
////                                custdistance_meter = "OUT";
////                            }
////                        } else {
////                            customerDistance.setText("Customer not found.");
////                            custdistance_meter = "OUT";
////                        }
////
////                        c2.close();
////                    })
////                    .addOnFailureListener(e -> {
////                        customerDistance.setText("Error retrieving location.");
////                        custdistance_meter = "OUT";
////                        Log.e("DistanceError", "Location fetch failed", e);
////                    });
////
////        } catch (Exception e) {
////            Log.e("DistanceError", "Unexpected error", e);
////            customerDistance.setText("Unexpected error.");
////            custdistance_meter = "OUT";
////        }
////    }
////
////    private final Runnable loopSyncLocation = new Runnable() {
////        @Override
////        public void run() {
////
////            syncLocation = true;
////            try {
////                enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
////            } catch (Exception e) {
////                Log.e("LoopGetL", "GPS ENABLED: " + e.getMessage(), e);
////            }
////
////            try {
////                enabledNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
////            } catch (Exception e) {
////                Log.e("LoopGetL", "NETWORK ENABLED: " + e.getMessage(), e);
////            }
////
////            // don't start listeners if no provider is enabled
////            if (!enabledGPS && !enabledNetwork) {
////                Log.d("GPS PROVIDER", "run() returned: " + false);
////                Log.d("NETWORK PROVIDER", "run() returned: " + enabledNetwork);
////            }
////
////            if (enabledGPS) {
////                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
////                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                    // request permissions if not granted
////                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
////                            Manifest.permission.ACCESS_FINE_LOCATION,
////                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
////                    return;
////                }
////
////                // remove previous updates to avoid conflicts
////                locationManager.removeUpdates(locationListener);
////                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
////
////                // update distance faster
////                custgetdistance();
////            }
////            mHandler.postDelayed(this, 1000); // run every 1 second for faster updates
////        }
////    };
////
////    class syncLocationListener implements LocationListener {
////        @Override
////        public void onLocationChanged(Location location) {
////            if (location != null) {
////
////                locationManager.removeUpdates(locationListener);
////
////                // locManager.removeUpdates(locListener);
////                longitude = "" + location.getLongitude();
////                latitude = "" + location.getLatitude();
//////                String altitiude = "" + location.getAltitude();
////                String accuracy = "" + location.getAccuracy();
//////                String time = "" + location.getTime();
////
////                // Toast.makeText(getApplicationContext(), "" + longitude + accuracy, Toast.LENGTH_LONG).show();
////                int valqtylenth;
////
////                double LLatValAdd;
////                double LLatValLess;
////
////                // float lessLatlong = 0.0001;
////                double LLongValAdd;
////                double LLongValLess;
////
////                String sb = "";
////
////                String latval = latitude;
////                startLatitude = Double.parseDouble(latval);
////
////                valqtylenth = latval.length();
////                for (int i = 0; i < 7; i++) {
////                    if (valqtylenth - 1 >= i) {
////                        char descr = latval.charAt(i);
////                        sb = sb + descr;
////                    }
////                } // end for loop description
////
////                newLatitude = sb;
////
////                LLatValAdd = Double.parseDouble(newLatitude);
////                // LLatValAdd = LLatValAdd + 0.0001;
////
////                LLatValLess = Double.parseDouble(newLatitude);
////                // LLatValLess = LLatValLess - 0.0001;
////
////                sb = "";
////
////                latval = latitude;
////                valqtylenth = latval.length();
////                for (int i = 0; i < 5; i++) {
////                    if (valqtylenth - 1 >= i) {
////                        char descr = latval.charAt(i);
////                        sb = sb + descr;
////                    }
////                } // end for loop description
////
////                newLatitude = sb;
////
////                newLatitudeAddIncr = "" + LLatValAdd;
////                newLatitudeDcrs = "" + LLatValLess;
////
////                // Toast.makeText(getApplicationContext(), "sample New Lat "+ newLatitude +", latadd " +  newLatitudeAddIncr + ", less add" + newLatitudeDcrs , Toast.LENGTH_LONG).show();
////
////                sb = "";
////
////                latval = newLatitudeAddIncr;
////                valqtylenth = latval.length();
////                for (int i = 0; i < 7; i++) {
////                    if (valqtylenth - 1 >= i) {
////                        char descr = latval.charAt(i);
////                        sb = sb + descr;
////                    }
////                } // end for loop description
////
////                newLatitudeAddIncr = sb;
////
////                sb = "";
////
////                latval = newLatitudeDcrs;
////                valqtylenth = latval.length();
////                for (int i = 0; i < 7; i++) {
////                    if (valqtylenth - 1 >= i) {
////                        char descr = latval.charAt(i);
////                        sb = sb + descr;
////                    }
////                } // end for loop description
////
////                newLatitudeDcrs = sb;
////
////                if (syncLocation) {
////                    // Toast.makeText(getApplicationContext(), "gps loop New Lat "+ newLatitude +", latadd " +  newLatitudeAddIncr + ", less add" + newLatitudeDcrs , Toast.LENGTH_LONG).show();
////                }
////
////                syncLocation = false;
////
////                sb = "";
////
////                String longval = longitude;
////                startLongitude = Double.parseDouble(longval);
////
////                valqtylenth = longval.length();
////                for (int i = 0; i < 10; i++) {
////                    if (valqtylenth - 1 >= i) {
////                        char descr = longval.charAt(i);
////                        sb = sb + descr;
////                    }
////                    // sb = sb + " ";
////                } // end for loop description
////
////                newLongitude = sb;
////
////                LLongValAdd = Double.parseDouble(newLongitude);
////                // LLongValAdd = LLongValAdd + 0.0001;
////
////                LLongValLess = Double.parseDouble(newLongitude);
////                // LLongValLess = LLongValLess - 0.0001;
////
////                // adjust logngitude
////                sb = "";
////
////                longval = longitude;
////                valqtylenth = longval.length();
////                for (int i = 0; i < 7; i++) {
////                    if (valqtylenth - 1 >= i) {
////                        char descr = longval.charAt(i);
////                        sb = sb + descr;
////                    }
////                    // sb = sb + " ";
////                } // end for loop description
////
////                newLongitude = sb;
////
////                newnewLongitudeIncr = "" + LLongValAdd;
////                newLongitudeDcrs = "" + LLongValLess;
////
////                sb = "";
////
////                latval = newnewLongitudeIncr;
////                valqtylenth = latval.length();
////                for (int i = 0; i < 8; i++) {
////                    if (valqtylenth - 1 >= i) {
////                        char descr = latval.charAt(i);
////                        sb = sb + descr;
////                    }
////                } // end for loop description
////
////                newnewLongitudeIncr = sb;
////
////                sb = "";
////
////                latval = newLongitudeDcrs;
////                valqtylenth = latval.length();
////                for (int i = 0; i < 8; i++) {
////                    if (valqtylenth - 1 >= i) {
////                        char descr = latval.charAt(i);
////                        sb = sb + descr;
////                    }
////                } // end for loop description
////
////                newLongitudeDcrs = sb;
////
////                DecimalFormat formatter = new DecimalFormat("###.0000");
////
////                newLongitudeDcrs = formatter.format(LLongValLess);
////                newnewLongitudeIncr = formatter.format(LLongValAdd);
////
////                newLatitudeDcrs = formatter.format(LLatValLess);
////                newLatitudeAddIncr = formatter.format(LLatValAdd);
////
////                // Toast.makeText(getApplicationContext(), "sample New lONG "+ newLongitude +", LONGadd " +  newnewLongitudeIncr + ", less LONG" + newLongitudeDcrs , Toast.LENGTH_LONG).show();
////                // Toast.makeText(getApplicationContext(), "sample New Lat "+ newLatitude +", latadd " +  newLatitudeAddIncr + ", less add" + newLatitudeDcrs , Toast.LENGTH_LONG).show();
////
////                Cursor c1 = db.rawQuery("Select * from RECEIVERNUMBER", null);
////                c1.moveToFirst();
////                int cnt = c1.getCount();
////                if (cnt > 0) {
////                    simNumber = c1.getString(0);
////                }
////
////                if ("MARS2".equals(synBranchName)) {
////                    syncMessage = "C2LOC!";
////                } else {
////                    syncMessage = "CLOC!";
////                }
////
////                String TempLoc = "";
////
////                // original
//////                SimpleDateFormat datetimesyntax = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
//////                String datetimesyntaxval = datetimesyntax.format(new Date());
////
////                // updated
////                SimpleDateFormat datetimesyntax = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
////                datetimesyntax.setTimeZone(TimeZone.getTimeZone("UTC"));
////                String datetimesyntaxval = datetimesyntax.format(new Date());
////
////                syncMessage = syncMessage + latitude + "!" + longitude + "!" + datetimesyntaxval;
////
////                if (PressLocation) {
//////                    Toast.makeText(getApplicationContext(), newLatitude + ", " + newLatitudeAddIncr + "," + newLatitudeDcrs + ", " +  latitude  + ", " + newLongitude + ", " + newnewLongitudeIncr + ", " + newLongitudeDcrs + ", " + longitude + ", Accuracy " + accuracy, Toast.LENGTH_LONG).show();
////
////                    new AlertDialog.Builder(MainActivity.this)
////                            .setIcon(R.drawable.mars_logo)
////                            .setTitle("Location Details")
////                            .setMessage(
////                                    "LATITUDE DETAILS " + "\n" +
////                                            "New Latitude: " + newLatitude + "\n" +
////                                            "New Latitude Add: " + newLatitudeAddIncr + "\n" +
////                                            "New Latitude Less: " + newLatitudeDcrs + "\n\n" +
////
////                                            "LONGITUDE DETAILS " + "\n" +
////                                            "New Longitude: " + newLongitude + "\n" +
////                                            "New Longitude Add: " + newnewLongitudeIncr + "\n" +
////                                            "New Longitude Less: " + newLongitudeDcrs + "\n\n" +
////
////                                            "YOUR CURRENT LOCATION " + "\n" +
////                                            "Latitude: " + latitude + "\n" +
////                                            "Longitude: " + longitude + "\n\n" +
////                                            "Accuracy: " + accuracy
////                            )
////                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
////                            .show();
////
////                    sendSMS(simNumber, syncMessage);
////
////                    selectQuery = " SELECT cname FROM customers " +
////                            " where (latitude like '%" + newLatitude + "%' and longitude like '%" + newLongitude + "%') or " +
////                            " (latitude like '%" + newLatitudeAddIncr + "%' and longitude like '%" + newLongitude + "%') or " +
////                            " (latitude like '%" + newLatitudeDcrs + "%' and longitude like '%" + newLongitude + "%') or " +
////                            " (latitude like '%" + newLatitude + "%' and longitude like '%" + newnewLongitudeIncr + "%') or " +
////                            " (latitude like '%" + newLatitude + "%' and longitude like '%" + newLongitudeDcrs + "%') or " +
////                            " (latitude like '%" + newLatitudeAddIncr + "%' and longitude like '%" + newnewLongitudeIncr + "%') or " +
////                            " (latitude like '%" + newLatitudeAddIncr + "%' and longitude like '%" + newLongitudeDcrs + "%') or " +
////                            " (latitude like '%" + newLatitudeDcrs + "%' and longitude like '%" + newnewLongitudeIncr + "%') or " +
////                            " (latitude like '%" + newLatitudeDcrs + "%' and longitude like '%" + newLongitudeDcrs + "%') " +
////                            " ORDER BY CNAME";
////
//////                    loadSpinnerData();
////                    syncData();
////
////                    loopSyncLocation.run();
////                    PressLocation = false;
////
////                } else {
////                    // inrangelocation
////                }
////
////                // locManager.removeUpdates(locListener);
////
////                // Toast.makeText(getApplicationContext(), "sample  " +  LLatVal, Toast.LENGTH_LONG).show();
////
////                // progress.setVisibility(View.GONE);
////            } else {
//////                Toast.makeText(getApplicationContext(), "GPS Not Enable or Cant Get Location!", Toast.LENGTH_LONG).show();
////
////                new AlertDialog.Builder(MainActivity.this)
////                        .setIcon(R.drawable.mars_logo)
////                        .setTitle("Location Error")
////                        .setMessage("GPS not enabled or can't get location!")
////                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
////                        .show();
////
////                // check permissions before requesting network updates
////                if (enabledNetwork) {
////                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
////                            ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////
////                        // request permissions if not granted
////                        ActivityCompat.requestPermissions(MainActivity.this,
////                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
////                                1001); // request code for permissions
////                        return;
////                    }
////                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
////                }
////            }
////        }
////
////        @Override
////        public void onProviderDisabled(@NonNull String arg0) {
////            // TODO Auto-generated method stub
////        }
////
////        @Override
////        public void onProviderEnabled(@NonNull String provider) {
////            // TODO Auto-generated method stub
////        }
////
////        @Override
////        public void onStatusChanged(@NonNull String provider, int status, @NonNull Bundle extras) {
////            // TODO Auto-generated method stub
////        }
////    }
//
//    // =========================================================
//    /** CUSTOMER DISTANCE: FETCHING DISTANCE */
//    // =========================================================
//    private void custgetdistance() {
//        try {
//            // Get customer name from AutoCompleteTextView
//            String tempstr = searchCustomer.getText().toString().trim();
//            Log.d("CustomerCheck", "Selected customer: '" + tempstr + "'");
//
//            if (tempstr.isEmpty()) {
//                custdistance_meter = "OUT";
//                runOnUiThread(() -> customerDistance.setText("No customer selected."));
//                return;
//            }
//
//            // Show immediate status while fetching
//            runOnUiThread(() -> customerDistance.setText("Fetching distance..."));
//
//            // Initialize location client
//            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//            // Permission check
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
//                runOnUiThread(() -> customerDistance.setText("Location permission not granted!"));
//                custdistance_meter = "OUT";
//                return;
//            }
//
//            // Fetch current location
//            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//                    .addOnSuccessListener(location -> {
//                        if (location == null) {
//                            runOnUiThread(() -> customerDistance.setText("Unable to fetch current location."));
//                            custdistance_meter = "OUT";
//                            return;
//                        }
//
//                        startLatitude = location.getLatitude();
//                        startLongitude = location.getLongitude();
//
//                        // Use the same database instance globally
//                        if (db == null || !db.isOpen()) {
//                            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//                        }
//
//                        // Query customer coordinates
//                        Cursor c2 = db.rawQuery("SELECT street, barangay, municipality, province, cid, status, Latitude, Longitude FROM Customers WHERE cname = ?", new String[]{tempstr});
//
//                        if (c2.moveToFirst()) {
//                            String status = c2.getString(5);
//                            String latStr = c2.getString(6);
//                            String longStr = c2.getString(7);
//
//                            Log.d("CustomerDebug", "Status=" + status + ", Lat=" + latStr + ", Long=" + longStr);
//
//                            if ("M".equals(status)) {
//                                try {
//                                    endLatitude = Double.parseDouble(latStr);
//                                    endLongitude = Double.parseDouble(longStr);
//
//                                    if (endLatitude == 0.0 || endLongitude == 0.0) {
//                                        runOnUiThread(() -> customerDistance.setText("Invalid customer location."));
//                                        custdistance_meter = "OUT";
//                                        c2.close();
//                                        return;
//                                    }
//
//                                    float[] results = new float[1];
//                                    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
//                                    float distance = results[0];
//
//                                    custdistance_meter = (distance > 0 && distance < 6) ? "IN" : "OUT";
//                                    String dis = (distance >= 1000)
//                                            ? String.format(Locale.getDefault(), "%.2f Kilometers", distance / 1000)
//                                            : String.format(Locale.getDefault(), "%.2f Meters", distance);
//
//                                    runOnUiThread(() -> customerDistance.setText(dis));
//
//                                } catch (NumberFormatException e) {
//                                    runOnUiThread(() -> customerDistance.setText("Invalid coordinates."));
//                                    custdistance_meter = "OUT";
//                                }
//                            } else {
//                                runOnUiThread(() -> customerDistance.setText("Customer is not mapped."));
//                                custdistance_meter = "OUT";
//                            }
//                        } else {
//                            runOnUiThread(() -> customerDistance.setText("Customer not found."));
//                            custdistance_meter = "OUT";
//                        }
//
//                        c2.close();
//                    })
//                    .addOnFailureListener(e -> {
//                        runOnUiThread(() -> customerDistance.setText("Error retrieving location."));
//                        custdistance_meter = "OUT";
//                        Log.e("DistanceError", "Location fetch failed", e);
//                    });
//
//        } catch (Exception e) {
//            Log.e("DistanceError", "Unexpected error", e);
//            runOnUiThread(() -> customerDistance.setText("Unexpected error."));
//            custdistance_meter = "OUT";
//        }
//    }
//
//    /** CONTINUOUS LOCATION UPDATER LOOP (UPDATES DISTANCE PER SECOND) */
////    private final Runnable loopSyncLocation = new Runnable() {
////        @Override
////        public void run() {
////            syncLocation = true;
////
////            try {
////                enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
////            } catch (Exception e) {
////                Log.e("LoopGetL", "GPS ENABLED: " + e.getMessage(), e);
////            }
////
////            try {
////                enabledNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
////            } catch (Exception e) {
////                Log.e("LoopGetL", "NETWORK ENABLED: " + e.getMessage(), e);
////            }
////
////            // Don't start listeners if no provider is enabled
////            if (!enabledGPS && !enabledNetwork) {
////                Log.d("GPS PROVIDER", "GPS disabled, skipping distance check.");
////            }
////
////            if (enabledGPS) {
////                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
////                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////
////                    // Request permissions if not granted
////                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
////                            Manifest.permission.ACCESS_FINE_LOCATION,
////                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
////                    return;
////                }
////
////                // Remove previous updates to avoid conflicts
////                locationManager.removeUpdates(locationListener);
////                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
////
////                // Call distance fetch only every 10 seconds (prevent UI flooding)
////                custgetdistance();
////            }
////
////            // Delay next loop (every 10 seconds instead of 1s)
////            mHandler.postDelayed(this, 10000);
////        }
////    };
//
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
//                    // Update distance display
//                    custgetdistance();
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
////                locationManager.removeUpdates(locationListener);
//
//                longitude = String.valueOf(location.getLongitude());
//                latitude = String.valueOf(location.getLatitude());
//                String accuracy = String.valueOf(location.getAccuracy());
//
//                // Latitude/Longitude computations preserved from your logic
//                // (shortened for clarity your original substring trimming and formatting remain)
//                DecimalFormat formatter = new DecimalFormat("###.0000");
//                startLatitude = location.getLatitude();
//                startLongitude = location.getLongitude();
//
//                newLatitudeAddIncr = formatter.format(startLatitude + 0.0001);
//                newLatitudeDcrs = formatter.format(startLatitude - 0.0001);
//                newnewLongitudeIncr = formatter.format(startLongitude + 0.0001);
//                newLongitudeDcrs = formatter.format(startLongitude - 0.0001);
//
//                // Update your UI or variables continuously
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
//                    // SMS + sync preserved
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
//                    sendSMS(simNumber, syncMessage);
//
//                    // Refresh customer list
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
////    @SuppressLint("SetTextI18n")
////    private void syncData() {
////        lables = new ArrayList<>();
////
////        Cursor cursor = db.rawQuery(selectQuery, null);
////        if (cursor.moveToFirst()) {
////            do {
////                String cname = cursor.getString(0);
////                if (cname != null) lables.add(cname);
////            } while (cursor.moveToNext());
////        }
////        cursor.close();
////
////        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
////                android.R.layout.simple_dropdown_item_1line, lables);
////        selectCustomer.setAdapter(dataAdapter);
////
////        // Automatically show first match details
////        if (!lables.isEmpty()) {
////            String firstCustomer = lables.get(0);
////            selectCustomer.setText(firstCustomer, false);
////            showCustomerDetails(firstCustomer);
////
////        } else {
////            customerCode.setText("");
////            customerAddress.setText("");
////        }
////    }
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
//                if (cursor.moveToFirst()) {
//                    do {
//                        String cname = cursor.getString(0);
//                        if (cname != null) tempLabels.add(cname);
//                    } while (cursor.moveToNext());
//                }
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
//                // =========================================================
//                /** HANDLE CUSTOMER SELECTION AND FETCH DISTANCE */
//                // =========================================================
//
//                selectCustomer.setOnItemClickListener((parent, view, position, id) -> {
//                    String selectedName = (String) parent.getItemAtPosition(position);
//
//                    // Update UI for selected customer
//                    showCustomerDetails(selectedName);
//
//                    // Sync searchCustomer (the one used in custgetdistance)
//                    if (searchCustomer != null) {
//                        searchCustomer.setText(selectedName);
//                    }
//
//                    // Show temporary message
//                    if (customerDistance != null) {
//                        customerDistance.setText("Fetching distance...");
//                    }
//
//                    // Run distance calculation after a short delay (prevents UI freeze)
//                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                        try {
//                            custgetdistance();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            if (customerDistance != null)
//                                customerDistance.setText("Error fetching distance.");
//                        }
//                    }, 300);
//                });
//                // =========================================================
//                /** HANDLE CUSTOMER SELECTION AND FETCH DISTANCE */
//                // =========================================================
//
//
//                if (!lables.isEmpty()) {
//                    String firstCustomer = lables.get(0);
//                    selectCustomer.setText(firstCustomer, false);
//                    showCustomerDetails(firstCustomer);
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
//
//    /**
//     * Create all required database tables for installation setup.
//     */
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
//
//    // -------------------- sendSMS with proper actions & flags --------------------
//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
//    public void sendSMS(String phoneNumber, String message) {
//        try {
//            String SENT = "SMS_SENT";
//            String DELIVERED = "SMS_DELIVERED";
//
//            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);
//            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), android.app.PendingIntent.FLAG_IMMUTABLE);
//
//            registerReceiver(new BroadcastReceiver() {
//
//                @Override
//                public void onReceive(Context arg0, Intent arg1) {
//                    switch (getResultCode()) {
//                        case Activity.RESULT_OK:
//                            Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
//                            break;
//                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                            Toast.makeText(getBaseContext(), "Generic failure",
//                                    Toast.LENGTH_SHORT).show();
//                            break;
//                        case SmsManager.RESULT_ERROR_NO_SERVICE:
//                            Toast.makeText(getBaseContext(), "Service", Toast.LENGTH_SHORT).show();
//                            break;
//                        case SmsManager.RESULT_ERROR_NULL_PDU:
//                            Toast.makeText(getBaseContext(), "Null PDU",
//                                    Toast.LENGTH_SHORT).show();
//                            break;
//                        case SmsManager.RESULT_ERROR_RADIO_OFF:
//                            Toast.makeText(getBaseContext(), "Radio off",
//                                    Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                }
//            }, new IntentFilter(SENT));
//            registerReceiver(new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context arg0, Intent arg1) {
//                    switch (getResultCode()) {
//                        case Activity.RESULT_OK:
//                            Toast.makeText(getBaseContext(), "SMS delivered",
//                                    Toast.LENGTH_SHORT).show();
//                            break;
//                        case Activity.RESULT_CANCELED:
//                            Toast.makeText(getBaseContext(), "SMS not delivered",
//                                    Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                }
//            }, new IntentFilter(DELIVERED));
//
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "SMS failed, please try again later!" + ", ERROR :" + e, Toast.LENGTH_LONG).show();
//            Log.e("SEND SMS", "SMS MANAGER: " + e.getMessage());
//        }
//    }
//}
//
//
//
//
