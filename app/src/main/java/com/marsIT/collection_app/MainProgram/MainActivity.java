package com.marsIT.collection_app.MainProgram;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.marsIT.collection_app.CollectionData.CollectionPayment;
import com.marsIT.collection_app.CollectionData.ViewCollection;
import com.marsIT.collection_app.CollectionStartUp.StartUp;
import com.marsIT.collection_app.CustomerDetails.CustDetails;
import com.marsIT.collection_app.CustomerStatus.CustomerListCollection;
import com.marsIT.collection_app.R;
import com.marsIT.collection_app.ToolBar.BaseToolbar;
import com.marsIT.collection_app.UpdateSQLpackages.AgriChemData;
import com.marsIT.collection_app.UpdateSQLpackages.BeloData;
import com.marsIT.collection_app.UpdateSQLpackages.Century1Data;
import com.marsIT.collection_app.UpdateSQLpackages.Century2Data;
import com.marsIT.collection_app.UpdateSQLpackages.ColumbiaData;
import com.marsIT.collection_app.UpdateSQLpackages.EqData;
import com.marsIT.collection_app.UpdateSQLpackages.KohlData;
import com.marsIT.collection_app.UpdateSQLpackages.LamoiyanData;
import com.marsIT.collection_app.UpdateSQLpackages.LubricantsData;
import com.marsIT.collection_app.UpdateSQLpackages.MontoscoData;
import com.marsIT.collection_app.UpdateSQLpackages.PeerlessData;
import com.marsIT.collection_app.UpdateSQLpackages.RamData;
import com.marsIT.collection_app.UpdateSQLpackages.SolaneData;
import com.marsIT.collection_app.UpdateSQLpackages.UrcData;
import com.marsIT.collection_app.UpdateSQLpackages.ZestarData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.os.Handler;
import android.content.Context;

public class MainActivity extends BaseToolbar {

    private float locationAccuracy = -1f;

    private static final float DISTANCE_THRESHOLD_METERS = 6f;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private SQLiteDatabase db;

    private boolean PressLocation = false; // trigger location alert

    private double startLatitude, startLongitude, endLatitude, endLongitude;

    private String custdistance_meter = "OUT";
    private String latitude = "", longitude = "";

    private AutoCompleteTextView searchCustomer;
    private TextInputEditText customerDistance, customerCode, customerAddress;

    private Button ViewOnMap, mCollectPayment, clearSearch, viewCollection, customerDetails, customerStatus, btnGetLocation;

    private String SalesmanID = "", Department = "", Status = "", BranchName = "", LockLocation = "";
    private String NewLat, NewLatAdd, NewLatLess, NewLong, NewlongAdd, NewlongLess;
    private String theMobNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);

        try {
            setupToolbar("MAVCI COLLECTION");

            /** Initialize database */
            initializeDatabase();

            /** Initialize UI components */
            initializeViews();

            /** Initialize database and tables */
            checkInstallation();

            /** Start real-time location updates */
            if (hasLocationPermission()) {
                startRealTimeLocationUpdates();
            } else {
                requestLocationPermission();
            }

            /** setup autocomplete for customer search */
            setupCustomerSearch();

            /** Initialize receiver number */
            receiverNumber();

            /** Clears all customer-related fields and resets distance */
            clearSearch.setOnClickListener(v -> clearCustomerData());

            /** function for google maps - opens google maps with a route (walking mode) - shows distance & path */
            ViewOnMap.setOnClickListener(v -> {
                /** Make sure a customer is selected first */
                if (searchCustomer.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please select a customer first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                /** Calculate distance to update endLatitude & endLongitude */
                calculateCustomerDistance(searchCustomer.getText().toString().trim());

                /** Open Google Maps now that we have a valid customer */
                openGoogleMaps();
            });

            /** ===================== Issue collection payment button ===================== */
            mCollectPayment.setOnClickListener(v -> {
                try {
                    boolean rangeMap = true;

                    /** Get customer info */
                    String dbSalesmanName = SalesmanID.trim();
                    String dbDepartment = Department.trim();
                    String dbBranchName = BranchName.trim();
                    String dbCustomerCode = customerCode.getText().toString().trim();
                    String selectedCustomerName = searchCustomer.getText().toString().trim();

                    /** Check if empty */
                    if (dbCustomerCode.isEmpty() || selectedCustomerName.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please select a customer first.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    /** Calculate distance first */
                    calculateCustomerDistance(selectedCustomerName); // updates custdistance_meter

                    createRequiredTables();

                    /** Validate system date/time */
                    SimpleDateFormat dbLsyntaxDT1 = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
                    String dbLsyntaxDT2 = dbLsyntaxDT1.format(new Date());

                    db.execSQL("CREATE TABLE IF NOT EXISTS LastSyntaxDateTime(SyntaxDT VARCHAR);");

                    Cursor checkTime = db.rawQuery("SELECT COUNT(*) FROM LastSyntaxDateTime", null);
                    if (checkTime != null && checkTime.moveToFirst() && checkTime.getInt(0) == 0) {
                        db.execSQL("INSERT INTO LastSyntaxDateTime(SyntaxDT) VALUES(?)", new Object[]{dbLsyntaxDT2});
                    }
                    if (checkTime != null) checkTime.close();

                    Cursor validateTime = db.rawQuery(
                            "SELECT * FROM LastSyntaxDateTime WHERE SyntaxDT <= ?",
                            new String[]{dbLsyntaxDT2}
                    );

                    if (validateTime != null && validateTime.moveToFirst()) {
                        db.execSQL("UPDATE LastSyntaxDateTime SET SyntaxDT = ?", new Object[]{dbLsyntaxDT2});
                    } else {
                        showDialog("Invalid Date/Time",
                                "Invalid System Date/Time: " + dbLsyntaxDT2);
                        rangeMap = false;
                    }
                    if (validateTime != null) validateTime.close();

                    /** Query selected customer */
                    Cursor qSelectedCustomer = db.rawQuery("SELECT * FROM CUSTOMERS WHERE CID LIKE ? ORDER BY CNAME", new String[]{dbCustomerCode});

                    if (qSelectedCustomer != null && qSelectedCustomer.moveToFirst()) {
                        String status = qSelectedCustomer.getString(34) != null ? qSelectedCustomer.getString(34).trim() : "";

                        if ("M".equalsIgnoreCase(status)) { // Mapped customer
                            int dbCountCustomers;

                            /** Check if location lock is enabled */
                            if ("Y".equalsIgnoreCase(LockLocation)) {
                                Cursor QueryUnlock = db.rawQuery(
                                        "SELECT * FROM customerUnlock WHERE CustomerID LIKE ?",
                                        new String[]{qSelectedCustomer.getString(0)}
                                );
                                dbCountCustomers = (QueryUnlock != null) ? QueryUnlock.getCount() : 0;
                                if (QueryUnlock != null) QueryUnlock.close();

                                /** Distance check: proceed only if within 6 meters */
                                if (dbCountCustomers == 0 && !"IN".equals(custdistance_meter)) {
                                    showDialog("Out of Range",
                                            "You must be within 6 meters of the customer before proceeding.");
                                    rangeMap = false; // stop user from proceeding
                                }
                            }

                            /** Proceed if in range */
                            if (rangeMap) {
                                Intent intent = new Intent(MainActivity.this, CollectionPayment.class);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd-yyyy HH:mm:ss", Locale.getDefault());
                                String dbLsyntaxDT = dateFormat.format(new Date());

                                // Pass selected customer info
                                intent.putExtra("installedSalesmanName", dbSalesmanName);
                                intent.putExtra("installedDepartment", dbDepartment);
                                intent.putExtra("installedBranchName", dbBranchName);
                                intent.putExtra("selectedCustomerID", dbCustomerCode);
                                intent.putExtra("selectedCustomerName", selectedCustomerName);
                                intent.putExtra("syntaxDateTime", dbLsyntaxDT);

                                createRequiredTables();
                                invoiceCategory();
                                listOfBanks();
                                paymentType();

                                /** ===================== Sales header query ===================== */
                                Cursor qSalesHeader = null;
                                try {
                                    qSalesHeader = db.rawQuery(
                                            "SELECT Refid, CustomerID, Balance, Department " +
                                                    "FROM SALESHEADER " +
                                                    // The save and send invoice number will not be included in selection
                                                    "WHERE CustomerID = ? AND Refid NOT IN (SELECT InvoiceNo FROM SavedCollectionInvoice)" +
//                                                    "WHERE CustomerID = ?" +
                                                    "ORDER BY Refid",
                                            new String[]{dbCustomerCode.trim()}
                                    );
                                } catch (Exception e) {
                                    Log.e("MAIN_ACTIVITY", "Error querying SALESHEADER", e);
                                }

                                /** Build list to pass to next Activity */
                                ArrayList<String> salesHeaderList = new ArrayList<>();

                                if (qSalesHeader != null && qSalesHeader.moveToFirst()) {
                                    do {
                                        String refId = qSalesHeader.getString(qSalesHeader.getColumnIndexOrThrow("Refid"));
                                        String customerID = qSalesHeader.getString(qSalesHeader.getColumnIndexOrThrow("CustomerID"));
                                        String balance = qSalesHeader.getString(qSalesHeader.getColumnIndexOrThrow("Balance"));
                                        String department = qSalesHeader.getString(qSalesHeader.getColumnIndexOrThrow("Department"));

                                        String row = refId + "|" + customerID + "|" + balance + "|" + department;
                                        Log.d("MAIN_ACTIVITY", "Invoice row: " + row);

                                        salesHeaderList.add(row);
                                    } while (qSalesHeader.moveToNext());
                                } else {
                                    Log.d("MAIN_ACTIVITY", "No SALESHEADER records found for: " + dbCustomerCode);
                                }
                                if (qSalesHeader != null) qSalesHeader.close();

                                Log.d("MAIN_ACTIVITY", "Total invoices: " + salesHeaderList.size());
                                /** ===================== Sales header query end ===================== */

                                /** ===================== Invoice category query ===================== */
                                Cursor qInvoiceCategory = null;
                                try {
                                    qInvoiceCategory = db.rawQuery(
                                            "SELECT Category, CatID " +
                                                    "FROM InvoiceCategory " +
                                                    "ORDER BY CatID",
                                            null
                                    );
                                } catch (Exception e) {
                                    Log.e("MAIN_ACTIVITY", "Error querying InvoiceCategory", e);
                                }

                                /** Build list to pass to next Activity */
                                ArrayList<String> invoiceCategoryList = new ArrayList<>();

                                if (qInvoiceCategory != null && qInvoiceCategory.moveToFirst()) {
                                    do {
                                        String category = qInvoiceCategory.getString(qInvoiceCategory.getColumnIndexOrThrow("Category"));
                                        String catID = qInvoiceCategory.getString(qInvoiceCategory.getColumnIndexOrThrow("CatID"));

                                        String row = category + "|" + catID;
                                        Log.d("MAIN_ACTIVITY", "InvoiceCategory row: " + row);

                                        invoiceCategoryList.add(row);
                                    } while (qInvoiceCategory.moveToNext());
                                } else {
                                    Log.d("MAIN_ACTIVITY", "No InvoiceCategory records found.");
                                }
                                if (qInvoiceCategory != null) qInvoiceCategory.close();

                                Log.d("MAIN_ACTIVITY", "Total categories: " + invoiceCategoryList.size());
                                /** ===================== Invoice category query end ===================== */

                                /** ===================== List of banks query ===================== */
                                Cursor qListOfBanks = null;
                                try {
                                    qListOfBanks = db.rawQuery(
                                            "SELECT BankInitial, BankName " +
                                                    "FROM ListOfBanks " +
                                                    "ORDER BY BankInitial",
                                            null
                                    );
                                } catch (Exception e) {
                                    Log.e("MAIN_ACTIVITY", "Error querying List of banks", e);
                                }

                                /** Build list to pass to next Activity */
                                ArrayList<String> qBankList = new ArrayList<>();

                                if (qListOfBanks != null && qListOfBanks.moveToFirst()) {
                                    do {
                                        String bankInitial = qListOfBanks.getString(qListOfBanks.getColumnIndexOrThrow("BankInitial"));
                                        String bankName = qListOfBanks.getString(qListOfBanks.getColumnIndexOrThrow("BankName"));

                                        String row = bankInitial + "|" + bankName;
                                        Log.d("MAIN_ACTIVITY", "List of banks row: " + row);

                                        qBankList.add(row);
                                    } while (qListOfBanks.moveToNext());
                                } else {
                                    Log.d("MAIN_ACTIVITY", "No List of banks records found.");
                                }
                                if (qListOfBanks != null) qListOfBanks.close();

                                Log.d("MAIN_ACTIVITY", "Total List of banks: " + qBankList.size());
                                /** ===================== List of banks query end ===================== */

                                /** ===================== Payment type query ===================== */
                                Cursor qPaymentType = null;
                                try {
                                    qPaymentType = db.rawQuery(
                                            "SELECT PType FROM PaymentType", null);
                                } catch (Exception e) {
                                    Log.e("MAIN_ACTIVITY", "Error querying List of banks", e);
                                }

                                /** Build list to pass to next Activity */
                                ArrayList<String> qPType = new ArrayList<>();

                                if (qPaymentType != null && qPaymentType.moveToFirst()) {
                                    do {
                                        String paymentType = qPaymentType.getString(qPaymentType.getColumnIndexOrThrow("PType"));

                                        qPType.add(paymentType);
                                        Log.d("MAIN_ACTIVITY", "Payment type row: " + paymentType);
                                    } while (qPaymentType.moveToNext());
                                } else {
                                    Log.d("MAIN_ACTIVITY", "No payment type records found.");
                                }
                                if (qPaymentType != null) qPaymentType.close();

                                Log.d("MAIN_ACTIVITY", "Total payment types: " + qPType.size());
                                /** ===================== Payment type query end ===================== */

                                /** Pass to next activity */
                                intent.putStringArrayListExtra("salesHeaderList", salesHeaderList);
                                intent.putStringArrayListExtra("invoiceCategoryList", invoiceCategoryList);
                                intent.putStringArrayListExtra("bankList", qBankList);
                                intent.putStringArrayListExtra("paymentTypeList", qPType);

                                /** Start activity */
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Proceeding to collection payment", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            showDialog("Customer Not Mapped",
                                    "This customer is not yet mapped. Please map them before issuing a collection payment.");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No matching customer found.", Toast.LENGTH_LONG).show();
                    }

                    if (qSelectedCustomer != null) qSelectedCustomer.close();

                } catch (Exception e) {
                    Log.e("Collect Payment", "Error: ", e);
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            viewCollection.setOnClickListener(v -> {

                /** Get customer info */
                String dbSalesmanName = SalesmanID.trim();
                String dbDepartment = Department.trim();
                String dbBranchName = BranchName.trim();
                String dbCustomerCode = customerCode.getText().toString().trim();
                String selectedCustomerName = searchCustomer.getText().toString().trim();

                /** Check if empty */
                if (dbCustomerCode.isEmpty() || selectedCustomerName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select a customer first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                /** Validate system date/time */
                SimpleDateFormat dbLsyntaxDT1 = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
                String dbLsyntaxDT2 = dbLsyntaxDT1.format(new Date());

                db.execSQL("CREATE TABLE IF NOT EXISTS LastSyntaxDateTime(SyntaxDT VARCHAR);");

                Cursor checkTime = db.rawQuery("SELECT COUNT(*) FROM LastSyntaxDateTime", null);
                if (checkTime != null && checkTime.moveToFirst() && checkTime.getInt(0) == 0) {
                    db.execSQL("INSERT INTO LastSyntaxDateTime(SyntaxDT) VALUES(?)", new Object[]{dbLsyntaxDT2});
                }
                if (checkTime != null) checkTime.close();

                Cursor validateTime = db.rawQuery(
                        "SELECT * FROM LastSyntaxDateTime WHERE SyntaxDT <= ?",
                        new String[]{dbLsyntaxDT2}
                );

                if (validateTime != null && validateTime.moveToFirst()) {
                    db.execSQL("UPDATE LastSyntaxDateTime SET SyntaxDT = ?", new Object[]{dbLsyntaxDT2});
                } else {
                    showDialog("Invalid Date/Time",
                            "Invalid System Date/Time: " + dbLsyntaxDT2);
                    if (validateTime != null) validateTime.close();
                    return; // VERY IMPORTANT: STOP HERE
                }
                if (validateTime != null) validateTime.close();

                Intent intent = new Intent(MainActivity.this, ViewCollection.class);

                SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd-yyyy HH:mm:ss", Locale.getDefault());
                String dbLsyntaxDT = dateFormat.format(new Date());

                // Pass selected customer info
                intent.putExtra("installedSalesmanName", dbSalesmanName);
                intent.putExtra("installedDepartment", dbDepartment);
                intent.putExtra("installedBranchName", dbBranchName);
                intent.putExtra("selectedCustomerID", dbCustomerCode);
                intent.putExtra("selectedCustomerName", selectedCustomerName);
                intent.putExtra("syntaxDateTime", dbLsyntaxDT);

                startActivity(intent);
            });

            customerDetails.setOnClickListener(v -> {

                /** Get customer info */
                String selectedCustomerName = searchCustomer.getText().toString().trim();
                String dbCustomerCode = customerCode.getText().toString().trim();

                /** Check if empty */
                if (dbCustomerCode.isEmpty() || selectedCustomerName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select a customer first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                /** Validate system date/time */
                SimpleDateFormat dbLsyntaxDT1 = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
                String dbLsyntaxDT2 = dbLsyntaxDT1.format(new Date());

                db.execSQL("CREATE TABLE IF NOT EXISTS LastSyntaxDateTime(SyntaxDT VARCHAR);");

                Cursor checkTime = db.rawQuery("SELECT COUNT(*) FROM LastSyntaxDateTime", null);
                if (checkTime != null && checkTime.moveToFirst() && checkTime.getInt(0) == 0) {
                    db.execSQL("INSERT INTO LastSyntaxDateTime(SyntaxDT) VALUES(?)", new Object[]{dbLsyntaxDT2});
                }
                if (checkTime != null) checkTime.close();

                Cursor validateTime = db.rawQuery(
                        "SELECT * FROM LastSyntaxDateTime WHERE SyntaxDT <= ?",
                        new String[]{dbLsyntaxDT2}
                );

                if (validateTime != null && validateTime.moveToFirst()) {
                    db.execSQL("UPDATE LastSyntaxDateTime SET SyntaxDT = ?", new Object[]{dbLsyntaxDT2});
                } else {
                    showDialog("Invalid Date/Time",
                            "Invalid System Date/Time: " + dbLsyntaxDT2);
                    if (validateTime != null) validateTime.close();
                    return; // VERY IMPORTANT: STOP HERE
                }
                if (validateTime != null) validateTime.close();

                Intent intent = new Intent(MainActivity.this, CustDetails.class);

                SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd-yyyy HH:mm:ss", Locale.getDefault());
                String dbLsyntaxDT = dateFormat.format(new Date());

                // Pass selected customer info
                intent.putExtra("selectedCustomerID", dbCustomerCode);
                intent.putExtra("selectedCustomerName", selectedCustomerName);
                intent.putExtra("syntaxDateTime", dbLsyntaxDT);

                startActivity(intent);
            });

            customerStatus.setOnClickListener(v -> {
                // Start CustomerListActivity
                Intent intent = new Intent(MainActivity.this, CustomerListCollection.class);
                startActivity(intent);
            });

            btnGetLocation.setOnClickListener(v -> {
                PressLocation = true;
                updateLatLongDebugValues(); // NO params
            });

            ///////////////////////////////////////////////////////////////// CODE DETECTION FOR DEVELOPER OPTIONS
            // Check if developer options are enabled when the app starts
            if (isDeveloperOptionsEnabled(this)) {
                showBlockDeveloperOptionsDialog(this);
            }
            ///////////////////////////////////////////////////////////////// CODE DETECTION FOR DEVELOPER OPTIONS

        } catch (Exception e) {
            Log.e("MainActivity", "Error: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /** ===================== Default Alert Dialog ===================== */
    private void showDialog(String title, String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.mipmap.ic_launcher_foreground)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onResume() {
        ///////////////////////////////////////////////////////////////// CODE DETECTION FOR DEVELOPER OPTIONS
        // Check again when the activity resumes in case developer options were turned on during the app usage
        if (isDeveloperOptionsEnabled(this)) {
            showBlockDeveloperOptionsDialog(this);
        }
        ///////////////////////////////////////////////////////////////// CODE DETECTION FOR DEVELOPER OPTIONS
        super.onResume();
    }

    @Override
    protected void onPause() {
        ///////////////////////////////////////////////////////////////// CODE DETECTION FOR DEVELOPER OPTIONS
        // Recheck each time the app starts to ensure developer options are disabled
        if (isDeveloperOptionsEnabled(this)) {
            showBlockDeveloperOptionsDialog(this);
        }
        ///////////////////////////////////////////////////////////////// CODE DETECTION FOR DEVELOPER OPTIONS
        super.onPause();
    }

    ///////////////////////////////////////////////////////////////// CODE DETECTION FOR DEVELOPER OPTIONS
    // Function to check if developer options are enabled
    @SuppressLint("NewApi") public static boolean isDeveloperOptionsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // API 17+
            return Settings.Global.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1;
        } else { // For API < 17, assume developer options are disabled
            return false;
        }
    }

    // Show a dialog to warn user and provide options
    public void showBlockDeveloperOptionsDialog(final Context context) {
        if (!(context instanceof Activity)) return;

        Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) return;

        new Handler(Looper.getMainLooper()).post(() -> {
            if (activity.isFinishing() || activity.isDestroyed()) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setIcon(R.mipmap.ic_launcher_foreground);
            builder.setTitle("Developer Options Enabled!");
            builder.setMessage("Developer Options must be disabled to use this app. Please disable them in Settings.");

            builder.setPositiveButton("Go to Settings", (dialog, which) -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                    context.startActivity(intent);
                    activity.finish(); // Finish after launching settings
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            builder.setNegativeButton("Exit App", (dialog, which) -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.finish(); // Finish the activity on exit
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            builder.setCancelable(false);
            builder.show();
        });
    }
    ///////////////////////////////////////////////////////////////// CODE DETECTION FOR DEVELOPER OPTIONS

    @Override
    protected void onDestroy() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        super.onDestroy();
    }

    /** ===================== Default Payment Type ===================== */
    private void paymentType(){
        Cursor dbPaymentType = db.rawQuery("SELECT * FROM PaymentType", null);
        dbPaymentType.moveToFirst();
        int iPaymentType = dbPaymentType.getCount();
        if(iPaymentType > 0){
            Log.d("INSERT PAYMENT TYPE", "Inserted payment type: " + iPaymentType);
        }else{
            // TODO: insert default Payment Type here
            db.execSQL("INSERT INTO PaymentType VALUES('CASH');");
            db.execSQL("INSERT INTO PaymentType VALUES('PDC');");
            //
        }
        dbPaymentType.close();
    }

    /** ===================== Default List of Banks ===================== */
    private void listOfBanks(){
        Cursor dblistOfBanks = db.rawQuery("SELECT * FROM ListOfBanks", null);
        dblistOfBanks.moveToFirst();
        int ilistOfBanks = dblistOfBanks.getCount();
        if(ilistOfBanks > 0){
            Log.d("INSERT LIST OF BANKS", "Inserted list of banks: " + ilistOfBanks);
        }else{
            // TODO: insert default list Of Banks here
            db.execSQL("INSERT INTO ListOfBanks VALUES('AUB','ASIA UNITED BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BDO','BANCO DE ORO');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BANCOFIL','BANCO FILIPINO');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BPI','BANK OF THE PHILIPPINE ISLAND');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('CBC','CHINA BANK CORPORATION');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('EWB','EAST WEST BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('EBI','ENTERPRISE BANK, INCORPORATED');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('FCB','FIRST CONSOLIDATED BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('GREEN BANK','GREEN BANK OF CARAGA');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('MBTC','METROPOLITAN BANK AND TRUST COMPANY');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BDONB','BANCO DE ORO - NETWORK BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-KID','PENINSULA RURAL BANK INC - KIDAPAWAN');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-K','PENINSULA RURAL BANK INC - KORONADAL');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-P','PENINSULA RURAL BANK INC - POLOMOLOK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-I','PENINSULA RURAL BANK INC - SULAN');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-T','PENINSULA RURAL BANK INC - TACURONG');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-CALUMPANG','PENINSULA RURAL BANK INC-CALUMPANG');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-DIGOS','PENINSULA RURAL BANK INC-DIGOS');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-TAGUM','PENINSULA RURAL BANK INC-TAGUM');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBank Davao','PENINSULA RURAL BANK INC.');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PBB','PHILIPPINE BUSINESS BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PNB','PHILIPPINE NATIONAL BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('QCDB','QUEEN CITY DEVELOPMENT BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RCBC','RIZAL COMMERCIAL BANKING CORPORATION');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBCI','RURAL BANK OF COTABATO INC');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-A','RURAL BANK OF DIGOS - ANTIPAS');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-B','RURAL BANK OF DIGOS - BANSALAN');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-K','RURAL BANK OF DIGOS - KIDAPAWAN');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-M','RURAL BANK OF DIGOS - MALITA');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-EPCIB-BANSALAN','RURAL BANK OF DIGOS INC-BANSALAN');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI','RURAL BANK OF DIGOS INCORPORATED');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('KORBANK','RURAL BANK OF KORONADAL');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBLEBAK','RURAL BANK OF LEBAK(SK)');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RB MIDSAYAP','RURAL BANK OF MIDSAYAP');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBMI','RURAL BANK OF MONTEVISTA MONKAYO COMVAL');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('TRUBANK','TAGUM RURAL BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('TRUBANK-B','TAGUM RURAL BANK - BANAYBANAY');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('TRUBANK-SC','TAGUM RURAL BANK - STA CRUZ');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('TRUBANK-NABUNTURAN','TAGUM RURAL BANK INC-NABUNTURAN');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('UCPBS','UCPB SAVINGS BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('WB','WEALTH BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('SBTC','SECURITY BANK AND TRUST');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BDO','BANCO DE ORO DOLLOR');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('CBI','CANTILAN BANK, INC.');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('LBP','LAND BANK OF THE PHILIPPINES');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('1ST VB','1ST VALLEY BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('CRB','CENTURY RURAL BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('CBS','CHINA BANK SAVINGS');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BMI','BANK OF MAKATI');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PSBC','PRODUCERS SAVINGS BANK CORPORATION');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('MCCB','MINDANAO CONSOLIDATED COOPERATIVE BANKS');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('1VB','1ST VALLEY BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBANK','ROBINSONS BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('CBCA','COOPERATIVE BANK OF COTABATO');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('SBTC','SECURITY BANK CORPORATION');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PTC','PHILIPPINE TRUST COMPANY');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('UBP','UNION BANK OF THE PHILIPPINES');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BC','BANK OF COMMERCE');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('UCPB','UNITED COCONUT PLANTERS BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('NB','NETWORK BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('DBP','DEVELOPMENT BANK OF THE PHILIPPINES');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PBCOM','PHILIPPINE BANK OF COMMUNICATIONS');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('SBA','STERLING BANK OF ASIA');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PSB','PHILIPPINE SAVINGS BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK','PENINSULA RURAL BANK INC');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('MAYBANK','MAYBANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PRBI','PARTNER RURAL BANK INCORPORATED - PIGCAWAYAN');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PSBANK','PHILIPPINE SAVINGS BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('AIB','AMANAH ISLAMIC BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('GCASH','GCASH');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('UPB','UNION BANK OF THE PHILIPPINES');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RBCOM','RURAL BANK OF COMPOSTELA');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PRBI','PARTNER RURAL BANK INCORPORATED');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('RMB','RIZAL MICROBANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('PBB','PHILIPPINES BUSINESS BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('CTBC BANK','CTBC BANK (PHILIPPINES) CORP.');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BDO-NB','BDO NETWORK BANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('1ST TRUBANK','1ST TRUBANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('CITI','CITIBANK, N.A.,PHILIPPINES');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('MBTC','METROBANK');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BPI','BANK OF PHILIPPINE ISLANDS');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BOC','BANK OF COMMERCE');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('BPI','BANK OF THE PHILIPPINES ISLANDS');");
            db.execSQL("INSERT INTO ListOfBanks VALUES('CBC','CHINA BANK');");
            //
        }
        dblistOfBanks.close();
    }

    /** ===================== Default Invoice Category ===================== */
    private void invoiceCategory(){
        Cursor dbInvoiceCategory = db.rawQuery("SELECT * FROM InvoiceCategory", null);
        dbInvoiceCategory.moveToFirst();
        int iInvoiceCategory = dbInvoiceCategory.getCount();
        if(iInvoiceCategory > 0){
            Log.d("INSERT CATEGORY LIST", "Inserted category list: " + iInvoiceCategory);
        }else{
            // TODO: insert default invoice category here
            db.execSQL("INSERT INTO InvoiceCategory VALUES('BAD ORDER','1');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('BUNDLING','2');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('CTP','3');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - OUTRIGHT 1ST QTR','4');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('LISTING FEE','5');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('PRICE OFF','6');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('REBATES','7');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('SALES RETURN','8');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('MERCHANDISER','9');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('PEST CONTROL','10');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('PENALTY','11');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('CONFISCATED ITEM','12');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('HOT LOAD ITEM','13');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('TPD','14');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('BARCODE','15');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('PRICE DIFFERENCE','16');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('DISPLAY ALLOWANCE','17');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('SUPPORT','18');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('INCENTIVE','19');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('ADVANCES TO PRINCIPAL','20');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('UNPAID','21');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - OUTRIGHT 2ND QTR','29');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - OUTRIGHT 3RD QTR','30');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - OUTRIGHT 4TH QTR','31');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - QUARTERLY 1ST QTR','32');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - QUARTERLY 2ND QTR','33');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - QUARTERLY 3RD QTR','34');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - QUARTERLY 4TH QTR','35');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('RETENTION','36');");
            db.execSQL("INSERT INTO InvoiceCategory VALUES('LIQUIDATED DAMAGES','37');");
            //
        }
        dbInvoiceCategory.close();
    }

    /** Clears all customer-related fields and resets distance */
    private void clearCustomerData() {
        searchCustomer.setText("");
        customerDistance.setText("");
        customerCode.setText("");
        customerAddress.setText("");
        custdistance_meter = "OUT";
        startLatitude = startLongitude = endLatitude = endLongitude = 0.0;
        NewLat = NewLatAdd = NewLatLess = "";
        NewLong = NewlongAdd = NewlongLess = "";
        Toast.makeText(this, "Customer data cleared", Toast.LENGTH_SHORT).show();
    }

    /** ===================== Create required tables ===================== */
    private void createRequiredTables() {
        try {
            /** CUSTOMER DETAILS */
            db.execSQL("CREATE TABLE IF NOT EXISTS CUSTOMERS(" +
                    "CID VARCHAR, CNAME VARCHAR, CTYPE VARCHAR, ATYPE VARCHAR, TERMS VARCHAR, CPERSON VARCHAR," +
                    "CBDAY DATE, CTELLNUM VARCHAR, CCELLNUM VARCHAR, OWNER VARCHAR, OBDAY DATE, OTELLNUM VARCHAR," +
                    "OCELLNUM VARCHAR, STREET VARCHAR, BARANGAY VARCHAR, MUNICIPALITY VARCHAR, PROVINCE VARCHAR," +
                    "SUBPOCKET VARCHAR, CALTEX VARCHAR(1), MOBIL VARCHAR(1), CASTROL VARCHAR(1), PETRON VARCHAR(1)," +
                    "OTHER VARCHAR, CAF VARCHAR(1), DTI VARCHAR(1), BPERMIT VARCHAR(1), SIGNAGE VARCHAR(1)," +
                    "POSTER VARCHAR(1), STREAMER VARCHAR(1), STICKER VARCHAR(1), DISRACK VARCHAR(1), PRODISPLAY VARCHAR(1)," +
                    "Latitude VARCHAR, Longitude VARCHAR, status VARCHAR(1), creditline VARCHAR, balCredit VARCHAR," +
                    "OCheck VARCHAR, CreditStatus VARCHAR);");

            /** OTHER CUSTOMER DETAILS */
            db.execSQL("CREATE TABLE IF NOT EXISTS OtherCUSTOMERS(" +
                    "CID VARCHAR, CNAME VARCHAR, CTYPE VARCHAR, CPERSON VARCHAR, CTELLNUM VARCHAR, CCELLNUM VARCHAR," +
                    "STREET VARCHAR, BARANGAY VARCHAR, MUNICIPALITY VARCHAR, PROVINCE VARCHAR, Latitude VARCHAR," +
                    "Longitude VARCHAR, Grouptype VARCHAR, RouteCode VARCHAR, Source VARCHAR);");

            db.execSQL("CREATE TABLE IF NOT EXISTS Barangay(" +
                    "CODE VARCHAR(20), BARANGAY VARCHAR(100), MUNICIPALITY_CODE VARCHAR(100), " +
                    "MUNICIPALITY VARCHAR(100), PROVINCE_CODE VARCHAR(100), PROVINCE VARCHAR(100));");

            /** LIST OF BANKS */
            db.execSQL("CREATE TABLE IF NOT EXISTS ListOfBanks(" +
                    "BankInitial VARCHAR, BankName VARCHAR);");

            /** PAYMENT TYPE */
            db.execSQL("CREATE TABLE IF NOT EXISTS PaymentType(" +
                    "PType VARCHAR);");

            /** COLLECTION SYNTAX CATEGORY */
            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionCategory(" +
                    "RefID VARCHAR, CategoryID VARCHAR, Amount NUMERIC);");

            /** COLLECTION SYNTAX DETAILS */
            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionDetails(" +
                    "RefID VARCHAR, PaymentType VARCHAR, BankInitial VARCHAR, " +
                    "CheckNumber VARCHAR, Amount NUMERIC);");

            /** COLLECTION SYNTAX HEADER */
            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionHeader(" +
                    "PRnumber VARCHAR, CustomerID VARCHAR, Salesman VARCHAR, SyntaxID VARCHAR, " +
                    "SyntaxDateTime VARCHAR);");

            /** COLLECTION SYNTAX INVOICE DETAILS */
            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionInvoiceDetails(" +
                    "RefID VARCHAR, InvoiceNumber VARCHAR, Amount NUMERIC);");

            /** DATE/TIME VALIDATION TABLE */
            db.execSQL("CREATE TABLE IF NOT EXISTS LastSyntaxDateTime(" +
                    "SyntaxDT TEXT);");

            /** SALES HEADER TABLE */
            db.execSQL("CREATE TABLE IF NOT EXISTS SALESHEADER(" +
                    "Refid VARCHAR, CustomerID VARCHAR, Balance NUMERIC, Department VARCHAR);");

            /** INVOICE CATEGORY TABLE */
            db.execSQL("CREATE TABLE IF NOT EXISTS InvoiceCategory(" +
                    "Category VARCHAR, CatID VARCHAR);");

            /** =========================== SAVED COLLECTIONS =========================== */
            /** SAVED COLLECTION INVOICE */
            db.execSQL("CREATE TABLE IF NOT EXISTS SavedCollectionInvoice(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "PRnumber TEXT," +
                    "CustomerID TEXT," +
                    "InvoiceNo TEXT," +
                    "Amount REAL," +
                    "Department TEXT);");

            /** SAVED COLLECTION DEDUCTION */
            db.execSQL("CREATE TABLE IF NOT EXISTS SavedCollectionDeduction(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "PRnumber TEXT," +
                    "CustomerID TEXT," +
                    "Category TEXT," +
                    "Amount REAL);");

            /** SAVED COLLECTION PAYMENT */
            db.execSQL("CREATE TABLE IF NOT EXISTS SavedCollectionPayment(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "PRnumber TEXT," +
                    "CustomerID TEXT," +
                    "PaymentType TEXT," +
                    "BankInitial TEXT," +
                    "CheckNumber TEXT," +
                    "Amount REAL);");
            /** =========================== SAVED COLLECTIONS =========================== */


            db.execSQL("CREATE TABLE IF NOT EXISTS customerUnlock(CustomerID VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS receiverNumber(Num VARCHAR);");
            db.execSQL("CREATE TABLE IF NOT EXISTS department(Department VARCHAR);");

        } catch (Exception e) {
            Log.e("DBSetup", "Error creating tables: " + e.getMessage());
        }
    }

    /** Initialize view references */
    private void initializeViews() {
        searchCustomer = findViewById(R.id.mainCustomerName);
        customerDistance = findViewById(R.id.mainCustomerDistance);
        customerCode = findViewById(R.id.mainCustomerCode);
        customerAddress = findViewById(R.id.mainCustomerAddress);

        /** Buttons */
        clearSearch = findViewById(R.id.btnClearSearch);
        ViewOnMap = findViewById(R.id.btnViewOnMap);
        mCollectPayment = findViewById(R.id.btnCollectPayment);
        viewCollection = findViewById(R.id.btnViewCollection);
        customerDetails = findViewById(R.id.btnCustomerDetails);
        customerStatus = findViewById(R.id.btnCustomerStatus);
        btnGetLocation = findViewById(R.id.btnGetLocation);
    }

    /** Initialize or open database */
    private void initializeDatabase() {
        if (db == null || !db.isOpen()) {
            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
        }
    }

    /** Check installation status and create required tables */
    private void checkInstallation() {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS InstallValue(" +
                    "SalesmanID TEXT, Department TEXT, Status TEXT, BranchName TEXT, LockLocation TEXT);");

            Cursor chkInstall = db.rawQuery("SELECT * FROM InstallValue", null);
            if (chkInstall.moveToFirst()) {
                SalesmanID = chkInstall.getString(0);
                Department = chkInstall.getString(1);
                Status = chkInstall.getString(2);
                BranchName = chkInstall.getString(3);
                LockLocation = chkInstall.getString(4);

                Toast.makeText(this, "Welcome " + SalesmanID, Toast.LENGTH_LONG).show();

                if ("N".equals(Status)) {
                    Toast.makeText(this, "Installing Please Wait.", Toast.LENGTH_LONG).show();
                    createRequiredTables();
                    if ("AG".equals(Department)) {
                        startActivity(new Intent(this, AgriChemData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("BL".equals(Department)) {
                        startActivity(new Intent(this, BeloData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("CC".equals(Department)) {
                        if ("MARS1".equals(BranchName)) {
                            startActivity(new Intent(this, Century1Data.class)
                                    .putExtra("transinv", "Booking")
                                    .putExtra("DeptCode", Department));

                        } else if ("MARS2".equals(BranchName)) {
                            startActivity(new Intent(this, Century2Data.class)
                                    .putExtra("transinv", "Booking")
                                    .putExtra("DeptCode", Department));
                        } else {
                            Toast.makeText(getApplicationContext(), "Not Found Database " + BranchName, Toast.LENGTH_LONG).show();
                        }
                    } else if ("CL".equals(Department)) {
                        startActivity(new Intent(this, ColumbiaData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("EQ".equals(Department)) {
                        startActivity(new Intent(this, EqData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("KL".equals(Department)) {
                        startActivity(new Intent(this, KohlData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("LY".equals(Department)) {
                        startActivity(new Intent(this, LamoiyanData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("L".equals(Department)) {
                        startActivity(new Intent(this, LubricantsData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("MI".equals(Department)) {
                        startActivity(new Intent(this, MontoscoData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("RM".equals(Department)) {
                        startActivity(new Intent(this, PeerlessData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("PL".equals(Department)) {
                        startActivity(new Intent(this, RamData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("S".equals(Department)) {
                        startActivity(new Intent(this, SolaneData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("UC".equals(Department)) {
                        startActivity(new Intent(this, UrcData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    } else if ("ZT".equals(Department)) {
                        startActivity(new Intent(this, ZestarData.class)
                                .putExtra("transinv", "Booking")
                                .putExtra("DeptCode", Department));
                    }

                    db.execSQL("UPDATE InstallValue SET Status = 'Y';");
                    Toast.makeText(this, "Installation Finished.", Toast.LENGTH_LONG).show();
                    finish();
                }

            } else {
                startActivity(new Intent(this, StartUp.class));
                finish();
            }
            chkInstall.close();
        } catch (Exception e) {
            Log.e("CHKINSTALL", "Installation Error: " + e.getMessage());
        }
    }

    /** Setup AutoCompleteTextView for searching customers */
    private void setupCustomerSearch() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        searchCustomer.setAdapter(dataAdapter);
        searchCustomer.setThreshold(1); // show suggestions after 1 character

        searchCustomer.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs) {
                String inputText = cs.toString().trim();
                if (inputText.isEmpty()) return;

                List<String> suggestions = new ArrayList<>();
                Cursor cursor = db.rawQuery(
                        "SELECT cname FROM Customers WHERE cname LIKE ? ORDER BY cname",
                        new String[]{"%" + inputText + "%"});
                if (cursor.moveToFirst()) {
                    do {
                        suggestions.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                dataAdapter.clear();
                dataAdapter.addAll(suggestions);
                dataAdapter.notifyDataSetChanged();
                searchCustomer.showDropDown(); //TODO: auto-display dropdown
            }
        });

        searchCustomer.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = parent.getItemAtPosition(position).toString();
            searchCustomer.setText(selectedName);
            showCustomerDetails(selectedName); //TODO: populate code & address
            calculateCustomerDistance(selectedName); //TODO: calculate distance
        });
    }

    /** Simple TextWatcher base class to reduce boilerplate */
    private abstract class SimpleTextWatcher implements android.text.TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(android.text.Editable s) {}
        public abstract void onTextChanged(CharSequence cs);
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            onTextChanged(s);
        }
    }

    /** Reusable check for location permission */
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /** Request location permission */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        customerDistance.setText("Location permission not granted!");
        custdistance_meter = "OUT";
    }

    /** ===================== Real-time location updates ===================== */
    @SuppressLint("MissingPermission")
    private void startRealTimeLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /** Create a LocationRequest using the new builder API */
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100) //TODO: interval in ms
                .setMinUpdateIntervalMillis(50)  //TODO: fastest interval in ms
                .setWaitForAccurateLocation(false)
                .setMaxUpdateDelayMillis(0)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                Location location = locationResult.getLastLocation();
                if (location == null) return;

                /** Get accuracy and filter out weak signals */
                float accuracy = location.getAccuracy();
                if (!location.hasAccuracy() || accuracy > 50) { //TODO: ignore weak GPS (>50m)
                    Log.w("GPS", "Weak GPS signal (" + accuracy + "m), skipping update");
                    runOnUiThread(() -> customerDistance.setText("Waiting for accurate GPS (" + (int) accuracy + "m)..."));
                    return;
                }

                /** Update coordinates only if accuracy is acceptable */
                startLatitude = location.getLatitude();
                startLongitude = location.getLongitude();
                latitude = String.valueOf(startLatitude);
                longitude = String.valueOf(startLongitude);

                /** Update debug info */
                updateLatLongDebugValues();

                /** Recalculate customer distance (only if customer is selected) */
                String selectedCustomer = searchCustomer.getText().toString().trim();
                if (!selectedCustomer.isEmpty()) {
                    calculateCustomerDistance(selectedCustomer);
                }

                Log.d("GPS", "Accurate update: Lat=" + startLatitude +
                        ", Long=" + startLongitude + ", accuracy=" + accuracy + "m");
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /** Update NewLat/NewLong debugging values and show AlertDialog if triggered */
    /**
     * Update NewLat/NewLong debugging values
     * Show dialog + send SMS ONLY when PressLocation == true
     * Uses HIGH ACCURACY GPS and waits for 5–6 meters
     */
    /** ===================== Update Lat/Long debug values (Immediate) ===================== */
    private void updateLatLongDebugValues() {

        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }

        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        /** ===================== GET LAST KNOWN LOCATION (IMMEDIATE) ===================== */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                locationAccuracy = location.hasAccuracy() ? location.getAccuracy() : -1;

                /** Process immediately (fast response on Infinix & others) */
                processAndSendLocation(latitude, longitude);
            }
        });

        /** ===================== REQUEST HIGH ACCURACY UPDATE ===================== */
        LocationRequest locationRequest =
                new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500) // fast interval
                        .setMinUpdateIntervalMillis(300)
                        .setWaitForAccurateLocation(false) // DO NOT WAIT LONG
                        .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) return;

                        Location location = locationResult.getLastLocation();
                        if (location == null) return;

                        locationAccuracy = location.hasAccuracy()
                                ? location.getAccuracy()
                                : -1;

                        /** ACCEPT EVEN IF ACCURACY IS NOT PERFECT (INFINIX FIX) */
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());

                        Log.d("GPS",
                                "Immediate fix → acc=" + locationAccuracy +
                                        " lat=" + latitude +
                                        " lng=" + longitude);

                        /** STOP updates immediately */
                        fusedLocationClient.removeLocationUpdates(this);

                        processAndSendLocation(latitude, longitude);
                    }
                },
                Looper.getMainLooper()
        );
    }

    /** ===================== UNIVERSAL LOCATION REQUEST ===================== */
    private LocationRequest buildUniversalLocationRequest() {
        return new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000
        )
                .setMinUpdateIntervalMillis(300)
                .setWaitForAccurateLocation(false)
                .setMaxUpdateDelayMillis(0)
                .build();
    }

    private void processAndSendLocation(String latStr, String longStr) {
        try {
            DecimalFormat formatter = new DecimalFormat("###.0000");

            NewLat = latStr.length() >= 7 ? latStr.substring(0, 7) : latStr;
            NewLong = longStr.length() >= 10 ? longStr.substring(0, 10) : longStr;

            NewLatAdd = formatter.format(Double.parseDouble(NewLat));
            NewLatLess = NewLatAdd;
            NewlongAdd = formatter.format(Double.parseDouble(NewLong));
            NewlongLess = NewlongAdd;


            // Get receiver number
            Cursor getRnumber = db.rawQuery("SELECT Num FROM receiverNumber LIMIT 1", null);
            try {
                if (getRnumber.moveToFirst()) {
                    theMobNo = getRnumber.getString(0);
                } else {
                    Toast.makeText(this, "Receiver number not set", Toast.LENGTH_LONG).show();
                    PressLocation = false;
                    return;
                }
            } finally {
                getRnumber.close();
            }

            // Build SMS message
            String smsMessage = ("MARS2".equals(BranchName) ? "C2LOC!" : "CLOC!") +
                    latitude + "!" + longitude + "!" +
                    new SimpleDateFormat("M-dd-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

            // Build dialog message
            String dialogMessage = "LATITUDE DETAILS\n" +
                    "New Latitude: " + NewLat + "\n" +
                    "New Latitude Add: " + NewLatAdd + "\n" +
                    "New Latitude Less: " + NewLatLess + "\n\n" +
                    "LONGITUDE DETAILS\n" +
                    "New Longitude: " + NewLong + "\n" +
                    "New Longitude Add: " + NewlongAdd + "\n" +
                    "New Longitude Less: " + NewlongLess + "\n\n" +
                    "YOUR CURRENT LOCATION\n" +
                    "Latitude: " + latitude + "\n" +
                    "Longitude: " + longitude;

            if (PressLocation) {
                // Show dialog
                showDialog("Location Details", dialogMessage);

                // Send SMS
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS}, 2001);

                    PressLocation = false;
                    return;
                }

                try {
                    sendSmsMultipart(theMobNo, smsMessage);
                } catch (Exception e) {
                    Log.e("sendSmsMultipart", "SMS send failed", e);
                }
                PressLocation = false;
            }

        } catch (Exception e) {
            Log.e("LatLongDebug", "Error", e);
            PressLocation = false;
        }
    }

    /** ===================== Calculate customer distance ===================== */
    private void calculateCustomerDistance(String custName) {
        Cursor calCustDistance = db.rawQuery(
                "SELECT street, barangay, municipality, province, cid, status, Latitude, Longitude " +
                        "FROM Customers WHERE cname = ?", new String[]{custName});

        if (calCustDistance.moveToFirst()) {
            String status = calCustDistance.getString(5);
            if ("M".equals(status)) {
                try {
                    endLatitude = Double.parseDouble(calCustDistance.getString(6));
                    endLongitude = Double.parseDouble(calCustDistance.getString(7));

                    if (endLatitude == 0.0 || endLongitude == 0.0) {
                        customerDistance.setText("Invalid customer location.");
                        custdistance_meter = "OUT";
                        return;
                    }

                    float[] results = new float[1];
                    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
                    float distance = results[0];

                    custdistance_meter = (distance > 0 && distance < DISTANCE_THRESHOLD_METERS) ? "IN" : "OUT";
                    String custDistance = distance >= 1000 ?
                            String.format(Locale.getDefault(),"%.2f KILOMETERS", distance / 1000) :
                            String.format(Locale.getDefault(),"%.0f METERS", distance);

                    customerDistance.setText(custDistance);

                    /** Logs detailed information for verifying the distance calculation between the device and the customer. */
                    Log.d("CalcDistance", "Distance to customer: " + custDistance +
                            " | custdistance_meter=" + custdistance_meter +
                            " | distance=" + distance + custName);
                    Log.d("CalcDistance", "Current Lat=" + startLatitude + ", Lng=" + startLongitude);
                    Log.d("CalcDistance", "Customer Lat=" + endLatitude + ", Lng=" + endLongitude);
                    Log.d("CalcDistance", "Distance=" + distance +
                            " meters, custdistance_meter=" + custdistance_meter);

                } catch (NumberFormatException e) {
                    customerDistance.setText("Invalid coordinates.");
                    custdistance_meter = "OUT";
                    Log.e("CalcDistance", "Invalid coordinates for customer " + custName, e);
                }
            } else {
                customerDistance.setText("Customer is not mapped.");
                custdistance_meter = "OUT";
                Log.d("CalcDistance", "Customer not mapped: " + custName);
            }
        } else {
            customerDistance.setText("Customer not found.");
            custdistance_meter = "OUT";
            Log.d("CalcDistance", "Customer not found in DB: " + custName);
        }
        calCustDistance.close();
    }

    /**
     * function for google maps - opens google maps with a route (walking mode) - shows distance & path
     * this function gets the current location and opens Google Maps for navigation.
     */
    private void openGoogleMaps() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        /** Use getCurrentLocation for more accurate data */
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double currentLat = location.getLatitude();
                        double currentLng = location.getLongitude();

                        Log.d("DEBUG", "Current Location: Lat=" + currentLat + ", Lng=" + currentLng);
                        Log.d("DEBUG", "Destination Location: Lat=" + endLatitude + ", Lng=" + endLongitude);

                        if (endLatitude == 0.0 || endLongitude == 0.0) {
                            showDialog("Location Missing",
                                    "No valid customer location found!");
                            return;
                        }

                        float[] results = new float[1];
                        Location.distanceBetween(currentLat, currentLng, endLatitude, endLongitude, results);
                        float distance = results[0];

                        @SuppressLint("DefaultLocale")
                        String distanceText = distance >= 1000
                                ? String.format("%.2f KM", distance / 1000)
                                : String.format("%.0f M", distance);

                        Toast.makeText(this, "Distance to Customer " + distanceText, Toast.LENGTH_LONG).show();

                        /** Open Google Maps Immediately */
                        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + currentLat + "," + currentLng
                                + "&destination=" + endLatitude + "," + endLongitude + "&travelmode=walking";

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                    } else {
                        showDialog("Location Failed",
                                "Failed to get current location!");
                    }
                })
                .addOnFailureListener(e -> Log.e("ERROR", "Failed to get current location", e));
    }

    /** ===================== Show customer details ===================== */
    private void showCustomerDetails(String customerName) {
        Cursor custDetails = db.rawQuery(
                "SELECT street, barangay, municipality, province, CID " +
                        "FROM Customers WHERE cname = ? LIMIT 1", new String[]{customerName});

        if (custDetails.moveToFirst()) {
            String provinceCode = custDetails.getString(3);
            String provinceName;
            switch (provinceCode) {
                case "0": provinceName = "DAVAO DEL NORTE"; break;
                case "1": provinceName = "DAVAO ORIENTAL"; break;
                case "2": provinceName = "COMPOSTELA VALLEY"; break;
                case "3": provinceName = "NORTH COTABATO"; break;
                case "4": provinceName = "SOUTH COTABATO"; break;
                case "5": provinceName = "SARANGANI"; break;
                case "6": provinceName = "SULTAN KUDARAT"; break;
                case "7": provinceName = "MAGUINDANAO"; break;
                case "8": provinceName = "AGUSAN DEL SUR"; break;
                case "9": provinceName = "SURIGAO DEL SUR"; break;
                default: provinceName = "DAVAO DEL SUR"; break;
            }

            /** Display Address (UpperCase) */
            String fullAddress = "(" + custDetails.getString(4).toUpperCase(Locale.ROOT) + ") "
                    + custDetails.getString(0).toUpperCase(Locale.ROOT) + ", "
                    + custDetails.getString(1).toUpperCase(Locale.ROOT) + ", "
                    + custDetails.getString(2).toUpperCase(Locale.ROOT) + ", "
                    + provinceName.toUpperCase(Locale.ROOT);

            customerCode.setText(custDetails.getString(4).toUpperCase(Locale.ROOT));
            customerAddress.setText(fullAddress);
        } else {
            customerCode.setText("");
            customerAddress.setText("");
        }
        custDetails.close();
    }

    private void receiverNumber() {
        Cursor receiverNum = db.rawQuery("SELECT * FROM receivernumber", null);
        try {
            if (receiverNum.moveToFirst()) {
                Log.d("receiverNumber", "Record exists: " + receiverNum.getCount());
                return;
            }

            Map<String, String> deptNumberMap = new HashMap<>();
            String defaultNumber = "+639177105901"; // LUBRICANTS

            String[] num1Depts = {"UC", "RM", "CL", "MI"};
            String[] num2Depts = {"LY", "PL", "SO", "EQ"};

            for (String d : num1Depts) deptNumberMap.put(d, "+639177034043"); // URC
            for (String d : num2Depts) deptNumberMap.put(d, "+639177105906"); // CENTURY

            String number;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                number = deptNumberMap.getOrDefault(Department, defaultNumber);
            } else {
                number = deptNumberMap.containsKey(Department)
                        ? deptNumberMap.get(Department)
                        : defaultNumber;
            }

            db.execSQL("INSERT INTO receivernumber VALUES('" + number + "');");
            Log.d("receiverNumber", "Inserted receiver number: " + number);

        } finally {
            receiverNum.close();
        }
    }

    /** =========================== Send SMS in parts if long =========================== */
    private void sendSmsMultipart(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message); // splits automatically if >160 chars

            Log.d("SMS_LOG", "Sending " + parts.size() + " part(s) to " + phoneNumber);

            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);

            Log.d("SMS_LOG", "SMS sent successfully to " + phoneNumber);
            Toast.makeText(this, "SMS sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SMS_LOG", "Error sending SMS to " + phoneNumber, e);
            Toast.makeText(this, "Failed to send SMS to " + phoneNumber, Toast.LENGTH_LONG).show();
        }
    }
}
