//package com.marsIT.collection_app.MainProgram;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.ActivityNotFoundException;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.location.Location;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Looper;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.Toast;
//import android.os.Handler;
//import android.os.Looper;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.Priority;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.material.textfield.TextInputEditText;
//import com.marsIT.collection_app.CollectionData.CollectionPayment;
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
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//
//    private SQLiteDatabase db;
//
//    private boolean PressLocation = false; // trigger location alert
//
//    private double startLatitude;
//    private double startLongitude;
//    private double endLatitude;
//    private double endLongitude;
//
//    private String custdistance_meter = "OUT";
//    private String latitude = "", longitude = "";
//
//    private AutoCompleteTextView searchCustomer;
//    private TextInputEditText customerDistance;
//    private TextInputEditText customerCode;
//    private TextInputEditText customerAddress;
//    private Button btnClearSearch;
//
//    private String SalesmanID = "", Department = "", Status = "", BranchName = "", LockLocation = "";
//    private String NewLat, NewLatAdd, NewLatLess, NewLong, NewlongAdd, NewlongLess;
//    private String TheMessages = "", theMobNo = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mainlayout);
//        setupToolbar("MAVCIV13"); /** For Android 13 version */
//
//        try {
//            initializeViews();
//            initializeDatabase();
//            checkInstallation();
//
//            // Start real-time location updates
//            if (hasLocationPermission()) {
//                startRealTimeLocationUpdates();
//            } else {
//                requestLocationPermission();
//            }
//
//            setupCustomerSearch(); // setup autocomplete for customer search
//
//            /** Clears all customer-related fields and resets distance */
//            btnClearSearch.setOnClickListener(v -> clearCustomerData());
//
//            // function for google maps - opens google maps with a route (walking mode) - shows distance & path
//            Button ViewOnMap = findViewById(R.id.btnViewOnMap);
//            ViewOnMap.setOnClickListener(v -> {
//                // Make sure a customer is selected first
//                if (searchCustomer.getText().toString().trim().isEmpty()) {
//                    Toast.makeText(this, "Please select a customer first!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Calculate distance to update endLatitude & endLongitude
//                calculateCustomerDistance(searchCustomer.getText().toString().trim());
//
//                // Open Google Maps now that we have a valid customer
//                openGoogleMaps();
//            });
//
//            // ===================== ISSUE COLLECTION PAYMENT BUTTON =====================
//            Button btnAddCollection = findViewById(R.id.btnAddCollection);
//            btnAddCollection.setOnClickListener(v -> {
//                try {
//                    boolean rangeMap = true;
//
//                    /** Get customer info */
//                    String dbCustomerCode = customerCode.getText().toString().trim();
//                    String selectedCustomerName = searchCustomer.getText().toString().trim();
//
//                    /** Check if empty */
//                    if (dbCustomerCode.isEmpty() || selectedCustomerName.isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Please select a customer first.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    /** Calculate distance first */
//                    calculateCustomerDistance(selectedCustomerName); // updates custdistance_meter
//
//                    /** Open database */
//                    SQLiteDatabase dbMain = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//                    SQLiteDatabase dbValidateTime = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//                    createRequiredTables();
//
//                    /** Validate system date/time */
//                    SimpleDateFormat dbLsyntaxDT1 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
//                    dbLsyntaxDT1.setTimeZone(TimeZone.getTimeZone("UTC"));
//                    String dbLsyntaxDT2 = dbLsyntaxDT1.format(new Date());
//
//                    dbValidateTime.execSQL("CREATE TABLE IF NOT EXISTS LastSyntaxDateTime(SyntaxDT VARCHAR);");
//
//                    Cursor checkTime = dbValidateTime.rawQuery("SELECT COUNT(*) FROM LastSyntaxDateTime", null);
//                    if (checkTime != null && checkTime.moveToFirst() && checkTime.getInt(0) == 0) {
//                        dbValidateTime.execSQL("INSERT INTO LastSyntaxDateTime(SyntaxDT) VALUES(?)", new Object[]{dbLsyntaxDT2});
//                    }
//                    if (checkTime != null) checkTime.close();
//
//                    Cursor validateTime = dbValidateTime.rawQuery(
//                            "SELECT * FROM LastSyntaxDateTime WHERE SyntaxDT <= ?",
//                            new String[]{dbLsyntaxDT2}
//                    );
//
//                    if (validateTime != null && validateTime.moveToFirst()) {
//                        dbValidateTime.execSQL("UPDATE LastSyntaxDateTime SET SyntaxDT = ?", new Object[]{dbLsyntaxDT2});
//                    } else {
//                        new AlertDialog.Builder(MainActivity.this)
//                                .setIcon(R.drawable.mars_logo)
//                                .setTitle("Invalid Date/Time")
//                                .setMessage("Invalid System Date Time: " + dbLsyntaxDT2)
//                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                .show();
//                        rangeMap = false;
//                    }
//                    if (validateTime != null) validateTime.close();
//
//                    /** Query selected customer */
//                    Cursor cd = dbMain.rawQuery("SELECT * FROM CUSTOMERS WHERE CID LIKE ? ORDER BY CNAME", new String[]{dbCustomerCode});
//
//                    if (cd != null && cd.moveToFirst()) {
//                        String status = cd.getString(34) != null ? cd.getString(34).trim() : "";
//
//                        if ("M".equalsIgnoreCase(status)) { // Mapped customer
//                            int dbCountCustomers;
//
//                            /** Check if location lock is enabled */
//                            if ("Y".equalsIgnoreCase(LockLocation)) {
//                                Cursor QueryUnlock = dbMain.rawQuery(
//                                        "SELECT * FROM customerUnlock WHERE CustomerID LIKE ?",
//                                        new String[]{cd.getString(0)}
//                                );
//                                dbCountCustomers = (QueryUnlock != null) ? QueryUnlock.getCount() : 0;
//                                if (QueryUnlock != null) QueryUnlock.close();
//
//                                /** Distance check: proceed only if within 6 meters */
//                                if (dbCountCustomers == 0 && !"IN".equals(custdistance_meter)) {
//                                    new AlertDialog.Builder(MainActivity.this)
//                                            .setIcon(R.drawable.mars_logo)
//                                            .setTitle("Out of Range")
//                                            .setMessage("You must be within 6 meters of the customer before proceeding.")
//                                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                            .show();
//                                    rangeMap = false; // stop user from proceeding
//                                }
//                            }
//
//                            /** Proceed if in range */
//                            if (rangeMap) {
//                                dbMain.execSQL("DELETE FROM PaymentDetailsTEMP");
//
//                                SimpleDateFormat dateFormat = new SimpleDateFormat("Mddyyyy", Locale.getDefault());
//                                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//                                String dbLsyntaxDT = dateFormat.format(new Date());
//
//                                Intent intent = new Intent(MainActivity.this, CollectionPayment.class);
//                                startActivity(intent);
//                                Toast.makeText(getApplicationContext(), "Proceeding to collection payment", Toast.LENGTH_LONG).show();
//                            }
//
//                        } else {
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setIcon(R.drawable.mars_logo)
//                                    .setTitle("Customer Not Mapped")
//                                    .setMessage("This customer is not yet mapped. Please map them before issuing a collection payment.")
//                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                    .show();
//                        }
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No matching customer found.", Toast.LENGTH_LONG).show();
//                    }
//
//                    if (cd != null) cd.close();
//                    dbMain.close();
//                    dbValidateTime.close();
//
//                } catch (Exception e) {
//                    Log.e("btnAddCollection", "Error: ", e);
//                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//
//        } catch (Exception e) {
//            Log.e("MainActivity", "Error: " + e.getMessage());
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /** Clears all customer-related fields and resets distance */
//    private void clearCustomerData() {
//        searchCustomer.setText("");
//        customerDistance.setText("");
//        customerCode.setText("");
//        customerAddress.setText("");
//        custdistance_meter = "OUT";
//        startLatitude = startLongitude = endLatitude = endLongitude = 0.0;
//        NewLat = NewLatAdd = NewLatLess = "";
//        NewLong = NewlongAdd = NewlongLess = "";
//        Toast.makeText(this, "Customer data cleared", Toast.LENGTH_SHORT).show();
//    }
//
//    /** ===================== CREATE REQUIRED TABLES ===================== */
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
//            /** COLLECTION SYNTAX CATEGORY */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionCategory(" +
//                    "CTagID VARCHAR, RefID VARCHAR, CategoryID VARCHAR, Amount NUMERIC);");
//
//            /** COLLECTION SYNTAX DETAILS */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionDetails(" +
//                    "DTagID VARCHAR, RefID VARCHAR, PaymentType VARCHAR, BankInitial VARCHAR, " +
//                    "CheckNumber VARCHAR, Amount NUMERIC);");
//
//            /** COLLECTION SYNTAX HEADER */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionHeader(" +
//                    "HTagID VARCHAR, PRnumber VARCHAR, CustomerID VARCHAR, Salesman VARCHAR, SyntaxID VARCHAR, SyntaxDateTime VARCHAR);");
//
//            /** COLLECTION SYNTAX INVOICE DETAILS */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionInvoiceDetails(" +
//                    "INVTagID VARCHAR, RefID VARCHAR, InvoiceNumber VARCHAR, Amount NUMERIC);");
//
//            /** DATE/TIME VALIDATION TABLE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS LastSyntaxDateTime(" +
//                    "SyntaxDT VARCHAR);");
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
//    /** Initialize view references */
//    private void initializeViews() {
//        searchCustomer = findViewById(R.id.mainCustomerName);
//        customerDistance = findViewById(R.id.mainCustomerDistance);
//        customerCode = findViewById(R.id.mainCustomerCode);
//        customerAddress = findViewById(R.id.mainCustomerAddress);
//        btnClearSearch = findViewById(R.id.btnClearSearch);
//    }
//
//    /** Initialize or open database */
//    private void initializeDatabase() {
//        db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//    }
//
//    /** Check installation status and create required tables */
//    private void checkInstallation() {
//        db.execSQL("CREATE TABLE IF NOT EXISTS InstallValue(" +
//                "SalesmanID TEXT, Department TEXT, Status TEXT, BranchName TEXT, LockLocation TEXT);");
//
//        Cursor chkInstall = db.rawQuery("SELECT * FROM InstallValue", null);
//        if (chkInstall.moveToFirst()) {
//            SalesmanID = chkInstall.getString(0);
//            Department = chkInstall.getString(1);
//            Status = chkInstall.getString(2);
//            BranchName = chkInstall.getString(3);
//            LockLocation = chkInstall.getString(4);
//
//            Toast.makeText(this, "Welcome " + SalesmanID, Toast.LENGTH_LONG).show();
//
//            if ("N".equals(Status)) {
//                Toast.makeText(this, "Installing Please Wait.", Toast.LENGTH_LONG).show();
//                createRequiredTables();
//                if ("UC".equals(Department)) {
//                    startActivity(new Intent(this, UrcData.class)
//                            .putExtra("transinv", "Booking")
//                            .putExtra("DeptCode", Department));
//                }
//                db.execSQL("UPDATE InstallValue SET Status = 'Y';");
//                Toast.makeText(this, "Installation Finished.", Toast.LENGTH_LONG).show();
//                finish();
//            }
//
//        } else {
//            startActivity(new Intent(this, StartUp.class));
//            finish();
//        }
//        chkInstall.close();
//    }
//
//    /** Setup AutoCompleteTextView for searching customers */
//    private void setupCustomerSearch() {
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
//        searchCustomer.setAdapter(dataAdapter);
//        searchCustomer.setThreshold(1); // show suggestions after 1 character
//
//        searchCustomer.addTextChangedListener(new SimpleTextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence cs) {
//                String inputText = cs.toString().trim();
//                if (inputText.isEmpty()) return;
//
//                List<String> suggestions = new ArrayList<>();
//                Cursor cursor = db.rawQuery(
//                        "SELECT cname FROM Customers WHERE cname LIKE ? ORDER BY cname",
//                        new String[]{"%" + inputText + "%"});
//                if (cursor.moveToFirst()) {
//                    do {
//                        suggestions.add(cursor.getString(0));
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();
//
//                dataAdapter.clear();
//                dataAdapter.addAll(suggestions);
//                dataAdapter.notifyDataSetChanged();
//                searchCustomer.showDropDown(); // auto-display dropdown
//            }
//        });
//
//        searchCustomer.setOnItemClickListener((parent, view, position, id) -> {
//            String selectedName = parent.getItemAtPosition(position).toString();
//            searchCustomer.setText(selectedName);
//            showCustomerDetails(selectedName); // populate code & address
//            calculateCustomerDistance(selectedName); // calculate distance
//        });
//    }
//
//    /** Simple TextWatcher base class to reduce boilerplate */
//    private abstract class SimpleTextWatcher implements android.text.TextWatcher {
//        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//        @Override public void afterTextChanged(android.text.Editable s) {}
//        public abstract void onTextChanged(CharSequence cs);
//        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
//            onTextChanged(s);
//        }
//    }
//
//    /** Reusable check for location permission */
//    private boolean hasLocationPermission() {
//        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    /** Request location permission */
//    private void requestLocationPermission() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
//        customerDistance.setText("Location permission not granted!");
//        custdistance_meter = "OUT";
//    }
//
//    /** ===================== REAL-TIME LOCATION UPDATES ===================== */
//    @SuppressLint("MissingPermission")
//    private void startRealTimeLocationUpdates() {
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
////        locationRequest = LocationRequest.create()
////                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
////                .setInterval(5000) // update every 5 seconds
////                .setFastestInterval(2000);
//
//        // Create a LocationRequest using the new builder API
//        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500) // interval in ms
//                .setMinUpdateIntervalMillis(1000)  // fastest interval in ms
//                .setWaitForAccurateLocation(false) // optional
//                .build();
//
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) return;
//
//                Location location = locationResult.getLastLocation();
//                if (location == null) return;
//
//                // Get accuracy and filter out weak signals
//                float accuracy = location.getAccuracy();
//                if (!location.hasAccuracy() || accuracy > 50) { // ignore weak GPS (>50m)
//                    Log.w("GPS", "Weak GPS signal (" + accuracy + "m), skipping update");
//                    runOnUiThread(() -> customerDistance.setText("Waiting for accurate GPS (" + (int) accuracy + "m)..."));
//                    return;
//                }
//
//                // Update coordinates only if accuracy is acceptable
//                startLatitude = location.getLatitude();
//                startLongitude = location.getLongitude();
//                latitude = String.valueOf(startLatitude);
//                longitude = String.valueOf(startLongitude);
//
//                // Update debug info
//                updateLatLongDebugValues(latitude, longitude);
//
//                // Recalculate customer distance (only if customer is selected)
//                String selectedCustomer = searchCustomer.getText().toString().trim();
//                if (!selectedCustomer.isEmpty()) {
//                    calculateCustomerDistance(selectedCustomer);
//                }
//
//                // Log details for debugging
//                Log.d("GPS", "Accurate update: Lat=" + startLatitude +
//                        ", Long=" + startLongitude + ", accuracy=" + accuracy + "m");
//            }
//        };
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    /** Update NewLat/NewLong debugging values and show AlertDialog if triggered */
//    private void updateLatLongDebugValues(String latStr, String longStr) {
//        try {
//            DecimalFormat formatter = new DecimalFormat("###.0000");
//            NewLat = latStr.substring(0, Math.min(7, latStr.length()));
//            NewLong = longStr.substring(0, Math.min(10, longStr.length()));
//            NewLatAdd = formatter.format(Double.parseDouble(NewLat));
//            NewLatLess = formatter.format(Double.parseDouble(NewLat));
//            NewlongAdd = formatter.format(Double.parseDouble(NewLong));
//            NewlongLess = formatter.format(Double.parseDouble(NewLong));
//
//            if (PressLocation) {
//                new AlertDialog.Builder(MainActivity.this)
//                        .setIcon(R.drawable.mars_logo)
//                        .setTitle("Location Details")
//                        .setMessage(
//                                "LATITUDE DETAILS\n" +
//                                        "New Latitude: " + NewLat + "\n" +
//                                        "New Latitude Add: " + NewLatAdd + "\n" +
//                                        "New Latitude Less: " + NewLatLess + "\n\n" +
//                                        "LONGITUDE DETAILS\n" +
//                                        "New Longitude: " + NewLong + "\n" +
//                                        "New Longitude Add: " + NewlongAdd + "\n" +
//                                        "New Longitude Less: " + NewlongLess + "\n\n" +
//                                        "YOUR CURRENT LOCATION\n" +
//                                        "Latitude: " + latitude + "\n" +
//                                        "Longitude: " + longitude
//                        )
//                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                        .show();
//
//                PressLocation = false;
//            }
//        } catch (Exception e) {
//            Log.e("LatLongDebug", "Error formatting lat/long: " + e.getMessage(), e);
//        }
//    }
//
//    /** ===================== CALCULATE CUSTOMER DISTANCE ===================== */
//    private void calculateCustomerDistance(String custName) {
//        Cursor calCustDistance = db.rawQuery(
//                "SELECT street, barangay, municipality, province, cid, status, Latitude, Longitude " +
//                        "FROM Customers WHERE cname = ?", new String[]{custName});
//
//        if (calCustDistance.moveToFirst()) {
//            String status = calCustDistance.getString(5);
//            if ("M".equals(status)) {
//                try {
//                    endLatitude = Double.parseDouble(calCustDistance.getString(6));
//                    endLongitude = Double.parseDouble(calCustDistance.getString(7));
//
//                    if (endLatitude == 0.0 || endLongitude == 0.0) {
//                        customerDistance.setText("Invalid customer location.");
//                        custdistance_meter = "OUT";
//                        return;
//                    }
//
//                    float[] results = new float[1];
//                    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
//                    float distance = results[0];
//
//                    custdistance_meter = (distance > 0 && distance < 6) ? "IN" : "OUT";
//                    String custDistance = distance >= 1000 ?
//                            String.format(Locale.getDefault(),"%.2f KILOMETERS", distance / 1000) :
//                            String.format(Locale.getDefault(),"%.0f METERS", distance);
//
//                    customerDistance.setText(custDistance);
//
//                    Log.d("CalcDistance", "Distance to customer: " + custDistance +
//                            " | custdistance_meter=" + custdistance_meter +
//                            " | distance=" + distance + custName);
//                    Log.d("CalcDistance", "Current Lat=" + startLatitude + ", Lng=" + startLongitude);
//                    Log.d("CalcDistance", "Customer Lat=" + endLatitude + ", Lng=" + endLongitude);
//                    Log.d("CalcDistance", "Distance=" + distance +
//                            " meters, custdistance_meter=" + custdistance_meter);
//
//                } catch (NumberFormatException e) {
//                    customerDistance.setText("Invalid coordinates.");
//                    custdistance_meter = "OUT";
//                    Log.e("CalcDistance", "Invalid coordinates for customer " + custName, e);
//                }
//            } else {
//                customerDistance.setText("Customer is not mapped.");
//                custdistance_meter = "OUT";
//                Log.d("CalcDistance", "Customer not mapped: " + custName);
//            }
//        } else {
//            customerDistance.setText("Customer not found.");
//            custdistance_meter = "OUT";
//            Log.d("CalcDistance", "Customer not found in DB: " + custName);
//        }
//        calCustDistance.close();
//    }
//
//    // function for google maps - opens google maps with a route (walking mode) - shows distance & path
//    /**
//     * this function gets the current location and opens Google Maps for navigation.
//     */
//    private void openGoogleMaps() {
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
//            return;
//        }
//
//        // Use getCurrentLocation for more accurate data
//        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//                .addOnSuccessListener(location -> {
//                    if (location != null) {
//                        double currentLat = location.getLatitude();
//                        double currentLng = location.getLongitude();
//
//                        Log.d("DEBUG", "Current Location: Lat=" + currentLat + ", Lng=" + currentLng);
//                        Log.d("DEBUG", "Destination Location: Lat=" + endLatitude + ", Lng=" + endLongitude);
//
//                        if (endLatitude == 0.0 || endLongitude == 0.0) {
////                            Toast.makeText(this, "No valid customer location found!", Toast.LENGTH_SHORT).show();
//
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setIcon(R.drawable.mars_logo)
//                                    .setTitle("Location Missing")
//                                    .setMessage("No valid customer location found!")
//                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                    .show();
//                            return;
//                        }
//
//                        float[] results = new float[1];
//                        Location.distanceBetween(currentLat, currentLng, endLatitude, endLongitude, results);
//                        float distance = results[0];
//
////                        String distanceText = distance >= 1000 ? (distance / 1000) + " KM" : distance + " M";
//                        @SuppressLint("DefaultLocale") String distanceText = distance >= 1000
//                                ? String.format("%.2f KM", distance / 1000)
//                                : String.format("%.0f M", distance);
//
//                        Toast.makeText(this, "Distance to Customer " + distanceText, Toast.LENGTH_LONG).show();
//
////                        new AlertDialog.Builder(MainActivity.this)
////                                .setIcon(R.drawable.mars_logo)
////                                .setTitle("Distance to Customer")
////                                .setMessage("Distance " + distanceText)
////                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
////                                .show();
//
//                        // Open Google Maps Immediately
//                        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + currentLat + "," + currentLng
//                                + "&destination=" + endLatitude + "," + endLongitude + "&travelmode=walking";
//
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                        intent.setPackage("com.google.android.apps.maps");
//                        startActivity(intent);
//                    } else {
////                        Toast.makeText(this, "Failed to get current location!", Toast.LENGTH_SHORT).show();
//
//                        new AlertDialog.Builder(MainActivity.this)
//                                .setIcon(R.drawable.mars_logo)
//                                .setTitle("Location Failed")
//                                .setMessage("Failed to get current location!")
//                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                .show();
//                    }
//                })
//                .addOnFailureListener(e -> Log.e("ERROR", "Failed to get current location", e));
//    }
//
//    /** ===================== SHOW CUSTOMER DETAILS ===================== */
//    private void showCustomerDetails(String customerName) {
//        Cursor custDetails = db.rawQuery(
//                "SELECT street, barangay, municipality, province, CID " +
//                        "FROM Customers WHERE cname = ? LIMIT 1", new String[]{customerName});
//
//        if (custDetails.moveToFirst()) {
//            String provinceCode = custDetails.getString(3);
//            String provinceName;
//            switch (provinceCode) {
//                case "0": provinceName = "DAVAO DEL NORTE"; break;
//                case "1": provinceName = "DAVAO ORIENTAL"; break;
//                case "2": provinceName = "COMPOSTELA VALLEY"; break;
//                case "3": provinceName = "NORTH COTABATO"; break;
//                case "4": provinceName = "SOUTH COTABATO"; break;
//                case "5": provinceName = "SARANGANI"; break;
//                case "6": provinceName = "SULTAN KUDARAT"; break;
//                case "7": provinceName = "MAGUINDANAO"; break;
//                case "8": provinceName = "AGUSAN DEL SUR"; break;
//                case "9": provinceName = "SURIGAO DEL SUR"; break;
//                default: provinceName = "DAVAO DEL SUR"; break;
//            }
//
//            String fullAddress = "(" + custDetails.getString(4).toUpperCase(Locale.ROOT) + ") "
//                    + custDetails.getString(0).toUpperCase(Locale.ROOT) + ", "
//                    + custDetails.getString(1).toUpperCase(Locale.ROOT) + ", "
//                    + custDetails.getString(2).toUpperCase(Locale.ROOT) + ", "
//                    + provinceName.toUpperCase(Locale.ROOT);
//
//            customerCode.setText(custDetails.getString(4).toUpperCase(Locale.ROOT));
//            customerAddress.setText(fullAddress);
//        } else {
//            customerCode.setText("");
//            customerAddress.setText("");
//        }
//        custDetails.close();
//    }
//}
