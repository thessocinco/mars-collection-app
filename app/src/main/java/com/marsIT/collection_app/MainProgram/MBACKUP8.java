//package com.marsIT.collection_app.MainProgram;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
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
//            /** Initialize UI components */
//            initializeViews();
//
//            /** Initialize database and tables */
//            initializeDatabase();
//            checkInstallation();
//
//            /** Start real-time location updates */
//            if (hasLocationPermission()) {
//                startRealTimeLocationUpdates();
//            } else {
//                requestLocationPermission();
//            }
//
//            /** setup autocomplete for customer search */
//            setupCustomerSearch();
//
//            /** Clears all customer-related fields and resets distance */
//            btnClearSearch.setOnClickListener(v -> clearCustomerData());
//
//            /** function for google maps - opens google maps with a route (walking mode) - shows distance & path */
//            Button ViewOnMap = findViewById(R.id.btnViewOnMap);
//            ViewOnMap.setOnClickListener(v -> {
//                /** Make sure a customer is selected first */
//                if (searchCustomer.getText().toString().trim().isEmpty()) {
//                    Toast.makeText(this, "Please select a customer first!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                /** Calculate distance to update endLatitude & endLongitude */
//                calculateCustomerDistance(searchCustomer.getText().toString().trim());
//
//                /** Open Google Maps now that we have a valid customer */
//                openGoogleMaps();
//            });
//
//            /** ===================== Issue collection payment button ===================== */
//            Button btnAddCollection = findViewById(R.id.btnAddCollection);
//            btnAddCollection.setOnClickListener(v -> {
//                try {
//                    boolean rangeMap = true;
//
//                    /** Get customer info */
//                    String dbSalesmanName = SalesmanID.trim();
//                    String dbDepartment = Department.trim();
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
//                    SimpleDateFormat dbLsyntaxDT1 = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
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
//                                .setMessage("Invalid System Date/Time: " + dbLsyntaxDT2)
//                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                .show();
//                        rangeMap = false;
//                    }
//                    if (validateTime != null) validateTime.close();
//
//                    /** Query selected customer */
//                    Cursor qSelectedCustomer = dbMain.rawQuery("SELECT * FROM CUSTOMERS WHERE CID LIKE ? ORDER BY CNAME", new String[]{dbCustomerCode});
//
//                    if (qSelectedCustomer != null && qSelectedCustomer.moveToFirst()) {
//                        String status = qSelectedCustomer.getString(34) != null ? qSelectedCustomer.getString(34).trim() : "";
//
//                        if ("M".equalsIgnoreCase(status)) { // Mapped customer
//                            int dbCountCustomers;
//
//                            /** Check if location lock is enabled */
//                            if ("Y".equalsIgnoreCase(LockLocation)) {
//                                Cursor QueryUnlock = dbMain.rawQuery(
//                                        "SELECT * FROM customerUnlock WHERE CustomerID LIKE ?",
//                                        new String[]{qSelectedCustomer.getString(0)}
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
//                                    rangeMap = true; // stop user from proceeding
//                                }
//                            }
//
//                            /** Proceed if in range */
//                            if (rangeMap) {
//                                Intent intent = new Intent(MainActivity.this, CollectionPayment.class);
//
//                                SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
//                                String dbLsyntaxDT = dateFormat.format(new Date());
//
//                                // Pass selected customer info
//                                intent.putExtra("installedSalesmanName", dbSalesmanName);
//                                intent.putExtra("installedDepartment", dbDepartment);
//                                intent.putExtra("selectedCustomerID", dbCustomerCode);
//                                intent.putExtra("selectedCustomerName", selectedCustomerName);
//                                intent.putExtra("SyntaxDateTime", dbLsyntaxDT);
//
//                                /** Open database */
//                                SQLiteDatabase dbMainSalesHeader = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//                                SQLiteDatabase dbMainInvoiceCategory = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//                                SQLiteDatabase dbMainListOfBanks = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//                                SQLiteDatabase dbMainPaymentType = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//
//                                createRequiredTables();
//                                invoiceCategory();
//                                listOfBanks();
//                                paymentType();
//
//                                /** ===================== Sales header query ===================== */
//                                Cursor qSalesHeader = null;
//                                try {
//                                    qSalesHeader = dbMainSalesHeader.rawQuery(
//                                            "SELECT Refid, CustomerID, Balance, Department " +
//                                                    "FROM SALESHEADER " +
//                                                    "WHERE CustomerID = ? " +
//                                                    "ORDER BY Refid",
//                                            new String[]{dbCustomerCode.trim()}
//                                    );
//                                } catch (Exception e) {
//                                    Log.e("MAIN_ACTIVITY", "Error querying SALESHEADER", e);
//                                }
//
//                                /** Build list to pass to next Activity */
//                                ArrayList<String> salesHeaderList = new ArrayList<>();
//
//                                if (qSalesHeader != null && qSalesHeader.moveToFirst()) {
//                                    do {
//                                        String refId = qSalesHeader.getString(qSalesHeader.getColumnIndexOrThrow("Refid"));
//                                        String customerID = qSalesHeader.getString(qSalesHeader.getColumnIndexOrThrow("CustomerID"));
//                                        String balance = qSalesHeader.getString(qSalesHeader.getColumnIndexOrThrow("Balance"));
//                                        String department = qSalesHeader.getString(qSalesHeader.getColumnIndexOrThrow("Department"));
//
//                                        String row = refId + "|" + customerID + "|" + balance + "|" + department;
//                                        Log.d("MAIN_ACTIVITY", "Invoice row: " + row);
//
//                                        salesHeaderList.add(row);
//                                    } while (qSalesHeader.moveToNext());
//                                } else {
//                                    Log.d("MAIN_ACTIVITY", "No SALESHEADER records found for: " + dbCustomerCode);
//                                }
//                                if (qSalesHeader != null) qSalesHeader.close();
//
//                                Log.d("MAIN_ACTIVITY", "Total invoices: " + salesHeaderList.size());
//                                /** ===================== Sales header query end ===================== */
//
//                                /** ===================== Invoice category query ===================== */
//                                Cursor qInvoiceCategory = null;
//                                try {
//                                    qInvoiceCategory = dbMainInvoiceCategory.rawQuery(
//                                            "SELECT Category, CatID " +
//                                                    "FROM InvoiceCategory " +
//                                                    "ORDER BY CatID",
//                                            null
//                                    );
//                                } catch (Exception e) {
//                                    Log.e("MAIN_ACTIVITY", "Error querying InvoiceCategory", e);
//                                }
//
//                                /** Build list to pass to next Activity */
//                                ArrayList<String> invoiceCategoryList = new ArrayList<>();
//
//                                if (qInvoiceCategory != null && qInvoiceCategory.moveToFirst()) {
//                                    do {
//                                        String category = qInvoiceCategory.getString(qInvoiceCategory.getColumnIndexOrThrow("Category"));
//                                        String catID = qInvoiceCategory.getString(qInvoiceCategory.getColumnIndexOrThrow("CatID"));
//
//                                        String row = category + "|" + catID;
//                                        Log.d("MAIN_ACTIVITY", "InvoiceCategory row: " + row);
//
//                                        invoiceCategoryList.add(row);
//                                    } while (qInvoiceCategory.moveToNext());
//                                } else {
//                                    Log.d("MAIN_ACTIVITY", "No InvoiceCategory records found.");
//                                }
//                                if (qInvoiceCategory != null) qInvoiceCategory.close();
//
//                                Log.d("MAIN_ACTIVITY", "Total categories: " + invoiceCategoryList.size());
//                                /** ===================== Invoice category query end ===================== */
//
//                                /** ===================== List of banks query ===================== */
//                                Cursor qListOfBanks = null;
//                                try {
//                                    qListOfBanks = dbMainListOfBanks.rawQuery(
//                                            "SELECT BankInitial, BankName " +
//                                                    "FROM ListOfBanks " +
//                                                    "ORDER BY BankInitial",
//                                            null
//                                    );
//                                } catch (Exception e) {
//                                    Log.e("MAIN_ACTIVITY", "Error querying List of banks", e);
//                                }
//
//                                /** Build list to pass to next Activity */
//                                ArrayList<String> qBankList = new ArrayList<>();
//
//                                if (qListOfBanks != null && qListOfBanks.moveToFirst()) {
//                                    do {
//                                        String bankInitial = qListOfBanks.getString(qListOfBanks.getColumnIndexOrThrow("BankInitial"));
//                                        String bankName = qListOfBanks.getString(qListOfBanks.getColumnIndexOrThrow("BankName"));
//
//                                        String row = bankInitial + "|" + bankName;
//                                        Log.d("MAIN_ACTIVITY", "List of banks row: " + row);
//
//                                        qBankList.add(row);
//                                    } while (qListOfBanks.moveToNext());
//                                } else {
//                                    Log.d("MAIN_ACTIVITY", "No List of banks records found.");
//                                }
//                                if (qListOfBanks != null) qListOfBanks.close();
//
//                                Log.d("MAIN_ACTIVITY", "Total List of banks: " + qBankList.size());
//                                /** ===================== List of banks query end ===================== */
//
//                                /** ===================== Payment type query ===================== */
//                                Cursor qPaymentType = null;
//                                try {
//                                    qPaymentType = dbMainPaymentType.rawQuery(
//                                            "SELECT PType FROM PaymentType", null);
//                                } catch (Exception e) {
//                                    Log.e("MAIN_ACTIVITY", "Error querying List of banks", e);
//                                }
//
//                                /** Build list to pass to next Activity */
//                                ArrayList<String> qPType = new ArrayList<>();
//
//                                if (qPaymentType != null && qPaymentType.moveToFirst()) {
//                                    do {
//                                        String paymentType = qPaymentType.getString(qPaymentType.getColumnIndexOrThrow("PType"));
//
//                                        qPType.add(paymentType);
//                                        Log.d("MAIN_ACTIVITY", "Payment type row: " + paymentType);
//                                    } while (qPaymentType.moveToNext());
//                                } else {
//                                    Log.d("MAIN_ACTIVITY", "No payment type records found.");
//                                }
//                                if (qPaymentType != null) qPaymentType.close();
//
//                                Log.d("MAIN_ACTIVITY", "Total payment types: " + qPType.size());
//                                /** ===================== Payment type query end ===================== */
//
//                                /** Pass to next activity */
//                                intent.putStringArrayListExtra("salesHeaderList", salesHeaderList);
//                                intent.putStringArrayListExtra("invoiceCategoryList", invoiceCategoryList);
//                                intent.putStringArrayListExtra("bankList", qBankList);
//                                intent.putStringArrayListExtra("paymentTypeList", qPType);
//
//                                /** Start activity */
//                                startActivity(intent);
//                                Toast.makeText(getApplicationContext(), "Proceeding to collection payment", Toast.LENGTH_LONG).show();
//                            }
//                        } else {
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setIcon(R.drawable.mars_logo)
//                                    .setTitle("Customer Not Mapped")
//                                    .setMessage("This customer is not yet mapped. Please map them before issuing a collection payment.")
//                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                    .show();
//                        }
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No matching customer found.", Toast.LENGTH_LONG).show();
//                    }
//
//                    if (qSelectedCustomer != null) qSelectedCustomer.close();
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
//    /** ===================== Default Payment Type ===================== */
//    private void paymentType(){
//        Cursor dbPaymentType = db.rawQuery("SELECT * FROM PaymentType", null);
//        dbPaymentType.moveToFirst();
//        int iPaymentType = dbPaymentType.getCount();
//        if(iPaymentType > 0){
//            Log.d("INSERT PAYMENT TYPE", "Inserted payment type: " + iPaymentType);
//        }else{
//            // TODO: insert default Payment Type here
//            db.execSQL("INSERT INTO PaymentType VALUES('CASH');");
//            db.execSQL("INSERT INTO PaymentType VALUES('PDC');");
//            //
//        }
//        dbPaymentType.close();
//    }
//
//    /** ===================== Default List of Banks ===================== */
//    private void listOfBanks(){
//        Cursor dblistOfBanks = db.rawQuery("SELECT * FROM ListOfBanks", null);
//        dblistOfBanks.moveToFirst();
//        int ilistOfBanks = dblistOfBanks.getCount();
//        if(ilistOfBanks > 0){
//            Log.d("INSERT LIST OF BANKS", "Inserted list of banks: " + ilistOfBanks);
//        }else{
//            // TODO: insert default list Of Banks here
//            db.execSQL("INSERT INTO ListOfBanks VALUES('AUB','ASIA UNITED BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BDO','BANCO DE ORO');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BANCOFIL','BANCO FILIPINO');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BPI','BANK OF THE PHILIPPINE ISLAND');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('CBC','CHINA BANK CORPORATION');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('EWB','EAST WEST BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('EBI','ENTERPRISE BANK, INCORPORATED');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('FCB','FIRST CONSOLIDATED BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('GREEN BANK','GREEN BANK OF CARAGA');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('MBTC','METROPOLITAN BANK AND TRUST COMPANY');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BDONB','BANCO DE ORO - NETWORK BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-KID','PENINSULA RURAL BANK INC - KIDAPAWAN');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-K','PENINSULA RURAL BANK INC - KORONADAL');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-P','PENINSULA RURAL BANK INC - POLOMOLOK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-I','PENINSULA RURAL BANK INC - SULAN');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-T','PENINSULA RURAL BANK INC - TACURONG');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-CALUMPANG','PENINSULA RURAL BANK INC-CALUMPANG');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-DIGOS','PENINSULA RURAL BANK INC-DIGOS');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK-TAGUM','PENINSULA RURAL BANK INC-TAGUM');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBank Davao','PENINSULA RURAL BANK INC.');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PBB','PHILIPPINE BUSINESS BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PNB','PHILIPPINE NATIONAL BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('QCDB','QUEEN CITY DEVELOPMENT BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RCBC','RIZAL COMMERCIAL BANKING CORPORATION');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBCI','RURAL BANK OF COTABATO INC');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-A','RURAL BANK OF DIGOS - ANTIPAS');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-B','RURAL BANK OF DIGOS - BANSALAN');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-K','RURAL BANK OF DIGOS - KIDAPAWAN');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-M','RURAL BANK OF DIGOS - MALITA');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI-EPCIB-BANSALAN','RURAL BANK OF DIGOS INC-BANSALAN');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBDI','RURAL BANK OF DIGOS INCORPORATED');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('KORBANK','RURAL BANK OF KORONADAL');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBLEBAK','RURAL BANK OF LEBAK(SK)');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RB MIDSAYAP','RURAL BANK OF MIDSAYAP');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBMI','RURAL BANK OF MONTEVISTA MONKAYO COMVAL');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('TRUBANK','TAGUM RURAL BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('TRUBANK-B','TAGUM RURAL BANK - BANAYBANAY');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('TRUBANK-SC','TAGUM RURAL BANK - STA CRUZ');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('TRUBANK-NABUNTURAN','TAGUM RURAL BANK INC-NABUNTURAN');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('UCPBS','UCPB SAVINGS BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('WB','WEALTH BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('SBTC','SECURITY BANK AND TRUST');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BDO','BANCO DE ORO DOLLOR');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('CBI','CANTILAN BANK, INC.');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('LBP','LAND BANK OF THE PHILIPPINES');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('1ST VB','1ST VALLEY BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('CRB','CENTURY RURAL BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('CBS','CHINA BANK SAVINGS');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BMI','BANK OF MAKATI');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PSBC','PRODUCERS SAVINGS BANK CORPORATION');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('MCCB','MINDANAO CONSOLIDATED COOPERATIVE BANKS');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('1VB','1ST VALLEY BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBANK','ROBINSONS BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('CBCA','COOPERATIVE BANK OF COTABATO');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('SBTC','SECURITY BANK CORPORATION');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PTC','PHILIPPINE TRUST COMPANY');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('UBP','UNION BANK OF THE PHILIPPINES');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BC','BANK OF COMMERCE');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('UCPB','UNITED COCONUT PLANTERS BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('NB','NETWORK BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('DBP','DEVELOPMENT BANK OF THE PHILIPPINES');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PBCOM','PHILIPPINE BANK OF COMMUNICATIONS');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('SBA','STERLING BANK OF ASIA');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PSB','PHILIPPINE SAVINGS BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PENBANK','PENINSULA RURAL BANK INC');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('MAYBANK','MAYBANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PRBI','PARTNER RURAL BANK INCORPORATED - PIGCAWAYAN');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PSBANK','PHILIPPINE SAVINGS BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('AIB','AMANAH ISLAMIC BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('GCASH','GCASH');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('UPB','UNION BANK OF THE PHILIPPINES');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RBCOM','RURAL BANK OF COMPOSTELA');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PRBI','PARTNER RURAL BANK INCORPORATED');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('RMB','RIZAL MICROBANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('PBB','PHILIPPINES BUSINESS BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('CTBC BANK','CTBC BANK (PHILIPPINES) CORP.');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BDO-NB','BDO NETWORK BANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('1ST TRUBANK','1ST TRUBANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('CITI','CITIBANK, N.A.,PHILIPPINES');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('MBTC','METROBANK');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BPI','BANK OF PHILIPPINE ISLANDS');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BOC','BANK OF COMMERCE');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('BPI','BANK OF THE PHILIPPINES ISLANDS');");
//            db.execSQL("INSERT INTO ListOfBanks VALUES('CBC','CHINA BANK');");
//            //
//        }
//        dblistOfBanks.close();
//    }
//
//    /** ===================== Default Invoice Category ===================== */
//    private void invoiceCategory(){
//        Cursor dbInvoiceCategory = db.rawQuery("SELECT * FROM InvoiceCategory", null);
//        dbInvoiceCategory.moveToFirst();
//        int iInvoiceCategory = dbInvoiceCategory.getCount();
//        if(iInvoiceCategory > 0){
//            Log.d("INSERT CATEGORY LIST", "Inserted category list: " + iInvoiceCategory);
//        }else{
//            // TODO: insert default invoice category here
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('BAD ORDER','1');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('BUNDLING','2');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('CTP','3');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - OUTRIGHT 1ST QTR','4');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('LISTING FEE','5');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('PRICE OFF','6');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('REBATES','7');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('SALES RETURN','8');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('MERCHANDISER','9');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('PEST CONTROL','10');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('PENALTY','11');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('CONFISCATED ITEM','12');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('HOT LOAD ITEM','13');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('TPD','14');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('BARCODE','15');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('PRICE DIFFERENCE','16');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('DISPLAY ALLOWANCE','17');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('SUPPORT','18');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('INCENTIVE','19');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('ADVANCES TO PRINCIPAL','20');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('UNPAID','21');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - OUTRIGHT 2ND QTR','29');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - OUTRIGHT 3RD QTR','30');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - OUTRIGHT 4TH QTR','31');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - QUARTERLY 1ST QTR','32');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - QUARTERLY 2ND QTR','33');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - QUARTERLY 3RD QTR','34');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('EWT - QUARTERLY 4TH QTR','35');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('RETENTION','36');");
//            db.execSQL("INSERT INTO InvoiceCategory VALUES('LIQUIDATED DAMAGES','37');");
//            //
//        }
//        dbInvoiceCategory.close();
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
//    /** ===================== Create required tables ===================== */
//    private void createRequiredTables() {
//        try {
//            /** CUSTOMER DETAILS */
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
//            /** OTHER CUSTOMER DETAILS */
//            db.execSQL("CREATE TABLE IF NOT EXISTS OtherCUSTOMERS(" +
//                    "CID VARCHAR, CNAME VARCHAR, CTYPE VARCHAR, CPERSON VARCHAR, CTELLNUM VARCHAR, CCELLNUM VARCHAR," +
//                    "STREET VARCHAR, BARANGAY VARCHAR, MUNICIPALITY VARCHAR, PROVINCE VARCHAR, Latitude VARCHAR," +
//                    "Longitude VARCHAR, Grouptype VARCHAR, RouteCode VARCHAR, Source VARCHAR);");
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS Barangay(" +
//                    "CODE VARCHAR(20), BARANGAY VARCHAR(100), MUNICIPALITY_CODE VARCHAR(100), " +
//                    "MUNICIPALITY VARCHAR(100), PROVINCE_CODE VARCHAR(100), PROVINCE VARCHAR(100));");
//
//            /** LIST OF BANKS */
//            db.execSQL("CREATE TABLE IF NOT EXISTS ListOfBanks(" +
//                    "BankInitial VARCHAR, BankName VARCHAR);");
//
//            /** PAYMENT TYPE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS PaymentType(" +
//                    "PType VARCHAR);");
//
//            /** COLLECTION SYNTAX CATEGORY */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionCategory(" +
//                    "RefID VARCHAR, CategoryID VARCHAR, Amount NUMERIC);");
//
//            /** COLLECTION SYNTAX DETAILS */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionDetails(" +
//                    "RefID VARCHAR, PaymentType VARCHAR, BankInitial VARCHAR, " +
//                    "CheckNumber VARCHAR, Amount NUMERIC);");
//
//            /** COLLECTION SYNTAX HEADER */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionHeader(" +
//                    "PRnumber VARCHAR, CustomerID VARCHAR, Salesman VARCHAR, SyntaxID VARCHAR, " +
//                    "SyntaxDateTime VARCHAR);");
//
//            /** COLLECTION SYNTAX INVOICE DETAILS */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionInvoiceDetails(" +
//                    "RefID VARCHAR, InvoiceNumber VARCHAR, Amount NUMERIC);");
//
//            /** DATE/TIME VALIDATION TABLE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS LastSyntaxDateTime(" +
//                    "SyntaxDT TEXT);");
//
//            /** SALES HEADER TABLE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS SALESHEADER(" +
//                    "Refid VARCHAR, CustomerID VARCHAR, Balance NUMERIC, Department VARCHAR);");
//
//            /** INVOICE CATEGORY TABLE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS InvoiceCategory(" +
//                    "Category VARCHAR, DayDue VARCHAR, CatGroup VARCHAR, CatID VARCHAR);");
//
//            /** TEMP SALES HEADER TABLE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS TempSALESHEADER(" +
//                    "Refid VARCHAR, CustomerID VARCHAR, Balance NUMERIC, Department VARCHAR);");
//
//
//            /** COLLECTION SAVED */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionSaved(" +
//                    "SavedID VARCHAR, PrNumber VARCHAR, InvoiceNo VARCHAR, Amount NUMERIC, Dept VARCHAR);");
//
//            /** COLLECTION INVOICE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionInvoice(" +
//                    "SavedID VARCHAR, PrNumber VARCHAR, InvoiceNo VARCHAR, Amount NUMERIC, Dept VARCHAR);");
//
//            /** COLLECTION DEDUCTION */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionDeduction(" +
//                    "SavedID VARCHAR, PrNumber VARCHAR, InvoiceNo VARCHAR, Amount NUMERIC, Dept VARCHAR);");
//
//            /** COLLECTION PAYMENT */
//            db.execSQL("CREATE TABLE IF NOT EXISTS CollectionPayment(" +
//                    "SavedID VARCHAR, PrNumber VARCHAR, InvoiceNo VARCHAR, Amount NUMERIC, Dept VARCHAR);");
//
//
//            db.execSQL("CREATE TABLE IF NOT EXISTS customerUnlock(CustomerID VARCHAR);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS receiverNumber(NUM VARCHAR);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS department(Department VARCHAR);");
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
//                searchCustomer.showDropDown(); //TODO: auto-display dropdown
//            }
//        });
//
//        searchCustomer.setOnItemClickListener((parent, view, position, id) -> {
//            String selectedName = parent.getItemAtPosition(position).toString();
//            searchCustomer.setText(selectedName);
//            showCustomerDetails(selectedName); //TODO: populate code & address
//            calculateCustomerDistance(selectedName); //TODO: calculate distance
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
//    /** ===================== Real-time location updates ===================== */
//    @SuppressLint("MissingPermission")
//    private void startRealTimeLocationUpdates() {
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        /** Create a LocationRequest using the new builder API */
//        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500) //TODO: interval in ms
//                .setMinUpdateIntervalMillis(1000)  //TODO: fastest interval in ms
//                .setWaitForAccurateLocation(false)
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
//                /** Get accuracy and filter out weak signals */
//                float accuracy = location.getAccuracy();
//                if (!location.hasAccuracy() || accuracy > 50) { //TODO: ignore weak GPS (>50m)
//                    Log.w("GPS", "Weak GPS signal (" + accuracy + "m), skipping update");
//                    runOnUiThread(() -> customerDistance.setText("Waiting for accurate GPS (" + (int) accuracy + "m)..."));
//                    return;
//                }
//
//                /** Update coordinates only if accuracy is acceptable */
//                startLatitude = location.getLatitude();
//                startLongitude = location.getLongitude();
//                latitude = String.valueOf(startLatitude);
//                longitude = String.valueOf(startLongitude);
//
//                /** Update debug info */
//                updateLatLongDebugValues(latitude, longitude);
//
//                /** Recalculate customer distance (only if customer is selected) */
//                String selectedCustomer = searchCustomer.getText().toString().trim();
//                if (!selectedCustomer.isEmpty()) {
//                    calculateCustomerDistance(selectedCustomer);
//                }
//
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
//    /** ===================== Calculate customer distance ===================== */
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
//                    /** Logs detailed information for verifying the distance calculation between the device and the customer. */
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
//    /**
//     * function for google maps - opens google maps with a route (walking mode) - shows distance & path
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
//        /** Use getCurrentLocation for more accurate data */
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
//                        @SuppressLint("DefaultLocale")
//                        String distanceText = distance >= 1000
//                                ? String.format("%.2f KM", distance / 1000)
//                                : String.format("%.0f M", distance);
//
//                        Toast.makeText(this, "Distance to Customer " + distanceText, Toast.LENGTH_LONG).show();
//
//                        /** Open Google Maps Immediately */
//                        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + currentLat + "," + currentLng
//                                + "&destination=" + endLatitude + "," + endLongitude + "&travelmode=walking";
//
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                        intent.setPackage("com.google.android.apps.maps");
//                        startActivity(intent);
//                    } else {
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
//    /** ===================== Show customer details ===================== */
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
//            /** Display Address (UpperCase) */
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
