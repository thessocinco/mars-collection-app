//package com.marsIT.collection_app.CollectionData;
//
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.textfield.TextInputLayout;
//import com.marsIT.collection_app.CategoryAdapter.CategoryItem;
//import com.marsIT.collection_app.CategoryAdapter.DeductionAdapter;
//import com.marsIT.collection_app.InvoiceAdapter.InvoiceAdapter;
//import com.marsIT.collection_app.InvoiceAdapter.InvoiceItem;
//import com.marsIT.collection_app.PaymentAdapter.cpPaymentAdapter;
//import com.marsIT.collection_app.PaymentAdapter.PaymentItem;
//import com.marsIT.collection_app.R;
//import com.marsIT.collection_app.ToolBar.BaseToolbar;
//
//// -------------------- IMPORTS --------------------
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.telephony.SmsManager;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Locale;
//
//public class CollectionPayment extends BaseToolbar {
//
//    /** Customer info */
//    private String cpInstalledSalesman;
//    private String cpInstalledDepartment;
//    private String selectedCustomerID;
//    private String selectedCustomerName;
//    private String syntaxDateTime;
//
//    /** Table data */
//    private ArrayList<String> collectionCategoryList;
//    private ArrayList<String> collectionDetailsList;
//    private ArrayList<String> collectionHeaderList;
//    private ArrayList<String> collectionInvoiceItem;
//
//    /** UI references */
//    private TextView cpSalesMan, cpDepartment, cpCustomerID, cpCustomerName, cpSyntaxDateTime;
//    private AutoCompleteTextView spInvoiceItem;
//    private EditText inputInvoice, invoiceAmount;
//    private Button btnAddInvoice, btnDeleteInvoice;
//    private RecyclerView invoiceRecyclerView;
//    private TextView totalInvoiceAmount, totalDeductionAmount;
//
//    /** Invoice storage */
//    private ArrayList<String> salesHeaderList;
//    private ArrayList<String> invoiceNumberList = new ArrayList<>();
//    private ArrayList<String> invoiceAmountList = new ArrayList<>();
//    private ArrayList<String> invoiceDeptList = new ArrayList<>();
//
//    private ArrayList<String> invoiceCategoryList;
//    private AutoCompleteTextView categoryDropdown;
//    private EditText deductionAmount;
//
//    /** Deduction storage */
//    private ArrayList<CategoryItem> deductionList = new ArrayList<>();
//    private RecyclerView deductionRecyclerView;
//    private DeductionAdapter deductionAdapter;
//    private Button btnAddDeduction, btnDeleteDeduction;
//
//    /** Payment Type storage */
//    private AutoCompleteTextView cpPaymentType, cpBankInitial;
//    private EditText cpBankName, cpChkNumber, cpPaymentAmount;
//    TextInputLayout layoutBankInitial, layoutBankName,layoutChkNumber;
//    private Button btnAddPayment, btnDltPayment;
//
//    /** Send and save all data from recycle view */
//    private Button btnSendSave;
//
//    private SQLiteDatabase db;
//
//    // RecyclerView
//    private RecyclerView listOfCollectionsRecycler;
//
//    // Total Payment Amount
//    private TextView totalPaymentAmount;
//
//    // Storage
//    private ArrayList<PaymentItem> paymentItemList = new ArrayList<>();
//    private cpPaymentAdapter paymentAdapter;
//
//    /** RecyclerView Adapter */
//    private ArrayList<InvoiceItem> invoiceItemList = new ArrayList<>();
//    private InvoiceAdapter invoiceAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.collection_payment);
//        setupToolbar("Collection Payment");
//
//        try {
//            /** Initialize views */
//            initializeViews();
//
//            /** Receiver number */
//            receiverNumber();
//
//            invoiceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//            /** Customer info */
//            cpInstalledSalesman = getIntent().getStringExtra("installedSalesmanName");
//            cpInstalledDepartment = getIntent().getStringExtra("installedDepartment");
//            selectedCustomerID = getIntent().getStringExtra("selectedCustomerID");
//            selectedCustomerName = getIntent().getStringExtra("selectedCustomerName");
//            syntaxDateTime = getIntent().getStringExtra("SyntaxDateTime");
//
//            /** Format date */
//            String formattedDateTime = "N/A";
//            if (syntaxDateTime != null) {
//                try {
//                    SimpleDateFormat incomingFormat = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
//                    Date date = incomingFormat.parse(syntaxDateTime);
//                    SimpleDateFormat displayFormat = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
//                    formattedDateTime = displayFormat.format(date);
//                } catch (ParseException e) {
//                    Log.e("COLLECTION_PAYMENT", "Error parsing syntaxDateTime", e);
//                }
//            }
//
//            cpSalesMan.setText(cpInstalledSalesman);
//
//            /** Department conversion */
//            cpDepartment.setText(cpInstalledDepartment);
//
//            if ("A".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("AUTO-SUPPLY");
//            } else if ("L".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("LUBRICANTS");
//            } else if ("CC".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("CENTURY");
//            } else if ("PL".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("PEERLESS");
//            } else if ("MI".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("MONTOSCO");
//            } else if ("LY".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("LAMOIYAN");
//            } else if ("AG".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("AGRICHEM");
//            } else if ("CL".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("COLUMBIA");
//            } else if ("SO".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("SOLANE");
//            } else if ("ZS".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("ZESTAR");
//            } else if ("BL".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("BELO");
//            } else if ("GT".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("GTAM");
//            } else if ("KL".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("KOHL");
//            } else if ("UC".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("URC");
//            } else if ("RM".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("RAM");
//            } else if ("EQ".equals(cpInstalledDepartment)) {
//                cpDepartment.setText("EQ");
//            } else {
//                cpDepartment.setText(cpInstalledDepartment);
//            }
//            /** Department conversion end */
//
//            cpCustomerID.setText(selectedCustomerID != null ? selectedCustomerID : "N/A");
//            cpCustomerName.setText(selectedCustomerName != null ? selectedCustomerName : "N/A");
//            cpSyntaxDateTime.setText(formattedDateTime);
//
//            /** Receive collection tables */
//            collectionCategoryList = getIntent().getStringArrayListExtra("collectionCategoryList");
//            collectionDetailsList = getIntent().getStringArrayListExtra("collectionDetailsList");
//            collectionHeaderList = getIntent().getStringArrayListExtra("collectionHeaderList");
//            collectionInvoiceItem = getIntent().getStringArrayListExtra("collectionInvoiceItem");
//
//            /** Receive invoice list */
//            salesHeaderList = getIntent().getStringArrayListExtra("salesHeaderList");
//            Log.d("COLLECTION_PAYMENT", "Received salesHeaderList: " + salesHeaderList);
//
//            /** Receive category list */
//            invoiceCategoryList = getIntent().getStringArrayListExtra("invoiceCategoryList");
//            setupCategoryDropdown();
//
//            if (salesHeaderList != null && !salesHeaderList.isEmpty()) {
//                loadInvoiceData();
//                setupInvoiceDropdown();
//                setupInvoiceInputLogic();
//                setupInvoiceRecyclerView();
//            }
//
//            /** Setup deduction list */
//            setupDeductionRecyclerView();
//            setupAddDeductionButton();
//
//            /** Setup banklist & paymentType list */
//            ArrayList<String> qBankList = getIntent().getStringArrayListExtra("bankList");
//            ArrayList<String> qPType = getIntent().getStringArrayListExtra("paymentTypeList");
//            setupPaymentTypeDropdown(qPType);
//            setupBankInitialDropdown(qBankList);
//
//            /** Amount Formatter */
//            applyAmountFormatter(cpPaymentAmount);
//            applyAmountFormatter(invoiceAmount);
//            applyAmountFormatter(deductionAmount);
//
//            /** Add Delete Deduction button logic */
//            btnDeleteDeduction.setOnClickListener(v -> {
//                deductionAdapter.deleteSelected(); // remove checked deductions
//                recalcTotalDeduction();            // recalc total
//                Toast.makeText(this, "Selected deductions deleted!", Toast.LENGTH_SHORT).show();
//            });
//
//            /** Add Send & Save button logic */
//            btnSendSave.setOnClickListener(v -> {
//
//                // Generate PR number
//                String prNumber = "000" + (System.currentTimeMillis() % 100000);
//
//                // Save PR number to your DB
//                savePRNumberToAllTables(prNumber);
//
//                // Build SMS strings
//                String invoiceSms = cleanForSms(buildInvoiceSms(prNumber));
//                String categorySms = cleanForSms(buildCategorySms(prNumber));
//                String paymentSms  = cleanForSms(buildPaymentSms(prNumber));
//
//                // Your target phone number
//                String recipient = "09171234567";
//
//                // Send SMS
//                sendOrComposeSms(recipient, invoiceSms);
//                sendOrComposeSms(recipient, categorySms);
//                sendOrComposeSms(recipient, paymentSms);
//
//                Toast.makeText(this, "Preparing SMS for sending…", Toast.LENGTH_SHORT).show();
//            });
//
//            /** Add Invoice button */
//            btnAddInvoice.setOnClickListener(v -> addInvoiceItem());
//
//            /** Delete invoices */
//            btnDeleteInvoice.setOnClickListener(v -> {
//                invoiceAdapter.deleteSelected();
//                recalcTotal();
//                refreshInvoiceDropdown();
//                Toast.makeText(this, "Selected invoices deleted!", Toast.LENGTH_SHORT).show();
//            });
//
//            listOfCollectionsRecycler.setLayoutManager(new LinearLayoutManager(this));
//
//            // Adapter
//            paymentAdapter = new cpPaymentAdapter(paymentItemList);
//            listOfCollectionsRecycler.setAdapter(paymentAdapter);
//
//            // Add Payment button
//            btnAddPayment.setOnClickListener(v -> addPayment());
//
//            // Delete Payment button
//            btnDltPayment.setOnClickListener(v -> {
//                paymentAdapter.deleteSelected();
//                recalcTotalPayment();
//                Toast.makeText(this, "Selected payments deleted!", Toast.LENGTH_SHORT).show();
//            });
//
//        } catch (Exception e) {
//            Log.e("COLLECTION_PAYMENT", "Error initializing CollectionPayment", e);
//            Toast.makeText(this, "Error loading collection data: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /** =========================== Initialize views =========================== */
//    private void initializeViews() {
//        cpSalesMan = findViewById(R.id.salesmanName);
//        cpDepartment = findViewById(R.id.sDepartment);
//        cpCustomerID = findViewById(R.id.customerID);
//        cpCustomerName = findViewById(R.id.customerName);
//        cpSyntaxDateTime = findViewById(R.id.syntaxDateTime);
//
//        // Invoice UI
//        spInvoiceItem = findViewById(R.id.salesmanlist1);
//        inputInvoice = findViewById(R.id.inputInvoice);
//        invoiceAmount = findViewById(R.id.invoiceAmount);
//        btnAddInvoice = findViewById(R.id.btnAddInvoice);
//        btnDeleteInvoice = findViewById(R.id.btnDltInvoice);
//        invoiceRecyclerView = findViewById(R.id.listOfInvoiceRecycler);
//        totalInvoiceAmount = findViewById(R.id.totalInvoiceAmount);
//
//        // Category UI
//        categoryDropdown = findViewById(R.id.category);
//        deductionAmount = findViewById(R.id.deductionAmount);
//        btnAddDeduction = findViewById(R.id.btnAddDeduction);
//        btnDeleteDeduction = findViewById(R.id.btnDltDeduction);
//        deductionRecyclerView = findViewById(R.id.listOfDeductionRecycler);
//        totalDeductionAmount = findViewById(R.id.totalDeductionAmount);
//
//        // List of banks UI
//        cpPaymentType = findViewById(R.id.paymentType);
//        cpBankInitial = findViewById(R.id.bankInitial);
//        cpBankName = findViewById(R.id.bankName);
//        cpChkNumber = findViewById(R.id.chkNumber);
//        cpPaymentAmount = findViewById(R.id.paymentAmount);
//
//        layoutBankInitial = findViewById(R.id.layoutBankInitial);
//        layoutBankName = findViewById(R.id.layoutBankName);
//        layoutChkNumber = findViewById(R.id.layoutChkNumber);
//
//        btnAddPayment = findViewById(R.id.btnAddPayment);
//        btnDltPayment = findViewById(R.id.btnDltPayment);
//        listOfCollectionsRecycler = findViewById(R.id.listOfCollectionsRecycler);
//        totalPaymentAmount = findViewById(R.id.totalPaymentAmount);
//
//        // Send & Save
//        btnSendSave = findViewById(R.id.btnSendSave);
//    }
//
//    /** =========================== Check if PR exists =========================== */
//    private boolean isPRNumberExistInTable(String tableName, String prNumber) {
//        boolean exists = false;
//        android.database.Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName + " WHERE PrNumber = ?", new String[]{prNumber});
//        if (cursor != null) {
//            cursor.moveToFirst();
//            exists = cursor.getInt(0) > 0;
//            cursor.close();
//        }
//        return exists;
//    }
//
//    /** =========================== Save PR Number to DB =========================== */
//    private void savePRNumberToAllTables(String prNumber) {
//        if (prNumber == null || prNumber.trim().isEmpty()) {
//            Toast.makeText(this, "PR number is invalid!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            db.beginTransaction();
//
//            // Check if PR exists
//            android.database.Cursor cursor = db.rawQuery(
//                    "SELECT COUNT(*) FROM CollectionHeader WHERE PRnumber = ?",
//                    new String[]{prNumber});
//            boolean exists = false;
//            if (cursor != null) {
//                cursor.moveToFirst();
//                exists = cursor.getInt(0) > 0;
//                cursor.close();
//            }
//
//            if (exists) {
//                Toast.makeText(this, "PR number already exists! Cannot save duplicate.", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            // Save CollectionHeader
//            String insertHeader = "INSERT INTO CollectionHeader (PRnumber, CustomerID, Salesman, SyntaxID, SyntaxDateTime) VALUES (?, ?, ?, ?, ?)";
//            db.execSQL(insertHeader, new Object[]{
//                    prNumber,
//                    selectedCustomerID != null ? selectedCustomerID : "",
//                    "",           // Salesman
//                    "",           // SyntaxID
//                    syntaxDateTime != null ? syntaxDateTime : ""
//            });
//
//            // Save CollectionInvoice
////            for (InvoiceItem item : invoiceItemList) {
////                db.execSQL("INSERT INTO CollectionInvoice (SavedID, PrNumber, InvoiceNo, Amount, Dept) VALUES (?, ?, ?, ?, ?)",
////                        new Object[]{prNumber + "_" + item.getInvoiceNo(), prNumber, item.getInvoiceNo(),
////                                item.getAmount().replace(",", ""), item.getDept()});
////            }
//
//            // Save CollectionDeduction
//            for (CategoryItem item : deductionList) {
//                db.execSQL("INSERT INTO CollectionDeduction (SavedID, PrNumber, InvoiceNo, Amount, Dept) VALUES (?, ?, ?, ?, ?)",
//                        new Object[]{prNumber + "_" + item.getCategory(), prNumber, item.getCategory(),
//                                item.getAmount().replace(",", ""), "N/A"});
//            }
//
//            // Save CollectionPayment
//            for (PaymentItem item : paymentItemList) {
//                db.execSQL("INSERT INTO CollectionPayment (SavedID, PrNumber, InvoiceNo, Amount, Dept) VALUES (?, ?, ?, ?, ?)",
//                        new Object[]{prNumber + "_" + item.getPaymentType(), prNumber, item.getPaymentType(),
//                                item.getAmount().replace(",", ""), "N/A"});
//            }
//
//            db.setTransactionSuccessful();
//            Toast.makeText(this, "Collection saved successfully with PR: " + prNumber, Toast.LENGTH_SHORT).show();
//            Log.d("COLLECTION_PAYMENT", "PR number saved: " + prNumber);
//
//        } catch (Exception e) {
//            Log.e("COLLECTION_PAYMENT", "Error saving PR: " + e.getMessage(), e);
//            Toast.makeText(this, "Error saving collection: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    /** =========================== Load invoice data =========================== */
//    private void loadInvoiceData() {
//        for (String row : salesHeaderList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 3) {
//                String invoiceNo = parts[0];
//                String amount = parts[2];
//                String dept = parts.length >= 4 ? parts[3] : "N/A";
//
//                invoiceNumberList.add(invoiceNo);
//                invoiceAmountList.add(amount);
//                invoiceDeptList.add(dept);
//            }
//        }
//    }
//
//    /** =========================== Setup invoice RecyclerView =========================== */
//    private void setupInvoiceRecyclerView() {
//        invoiceAdapter = new InvoiceAdapter(invoiceItemList, item -> {
//            inputInvoice.setText(item.getInvoiceNo());
//            try {
//                double amt = Double.parseDouble(item.getAmount());
//                DecimalFormat df = new DecimalFormat("#,###.00");
//                invoiceAmount.setText(df.format(amt));
//            } catch (Exception e) {
//                invoiceAmount.setText(item.getAmount());
//            }
//        });
//        invoiceRecyclerView.setAdapter(invoiceAdapter);
//    }
//
//    /** =========================== Setup deduction RecyclerView =========================== */
//    private void setupDeductionRecyclerView() {
//        deductionAdapter = new DeductionAdapter(deductionList);
//        deductionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        deductionRecyclerView.setAdapter(deductionAdapter);
//    }
//
//    /** =========================== Refresh dropdown =========================== */
//    private void refreshInvoiceDropdown() {
//        ArrayList<String> dropdownList = new ArrayList<>();
//        for (String invoiceNo : invoiceNumberList) {
//            boolean alreadyAdded = false;
//            for (InvoiceItem item : invoiceItemList) {
//                if (item.getInvoiceNo().equalsIgnoreCase(invoiceNo)) {
//                    alreadyAdded = true;
//                    break;
//                }
//            }
//            if (!alreadyAdded) {
//                dropdownList.add(invoiceNo);
//            }
//        }
//        dropdownList.add("OTHER");
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_dropdown_item_1line,
//                dropdownList
//        );
//
//        spInvoiceItem.setAdapter(adapter);
//    }
//
//    /** =========================== Setup invoice dropdown =========================== */
//    private void setupInvoiceDropdown() {
//        refreshInvoiceDropdown();
//
//        spInvoiceItem.setThreshold(1);
//        spInvoiceItem.setOnClickListener(v -> spInvoiceItem.showDropDown());
//
//        spInvoiceItem.setOnItemClickListener((parent, view, position, id) -> {
//            String selected = spInvoiceItem.getText().toString();
//
//            if ("OTHER".equalsIgnoreCase(selected)) {
//                inputInvoice.setText("");
//                invoiceAmount.setText("");
//                inputInvoice.setEnabled(true);
//                invoiceAmount.setEnabled(true);
//                inputInvoice.requestFocus();
//            } else {
//                int index = invoiceNumberList.indexOf(selected);
//                inputInvoice.setText(invoiceNumberList.get(index));
//                try {
//                    double amt = Double.parseDouble(invoiceAmountList.get(index));
//                    DecimalFormat df = new DecimalFormat("#,###.00");
//                    invoiceAmount.setText(df.format(amt));
//                } catch (Exception e) {
//                    invoiceAmount.setText(invoiceAmountList.get(index));
//                }
//
//                inputInvoice.setEnabled(false);
//                invoiceAmount.setEnabled(true);
//            }
//        });
//    }
//
//    /** =========================== Setup category dropdown =========================== */
//    private void setupCategoryDropdown() {
//        if (invoiceCategoryList == null || invoiceCategoryList.isEmpty()) return;
//
//        ArrayList<String> onlyCategories = new ArrayList<>();
//        for (String row : invoiceCategoryList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 1) {
//                onlyCategories.add(parts[0]);
//            }
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_dropdown_item_1line,
//                onlyCategories
//        );
//
//        categoryDropdown.setAdapter(adapter);
//        categoryDropdown.setThreshold(1);
//
//        categoryDropdown.setOnClickListener(v -> categoryDropdown.showDropDown());
//
//        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
//            deductionAmount.requestFocus(); // move focus to deduction input
//        });
//    }
//
//    /** ====================== Setup Payment Type Dropdown ====================== */
//    private void setupPaymentTypeDropdown(ArrayList<String> qPType) {
//        if (qPType == null) return;
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_dropdown_item_1line,
//                qPType
//        );
//
//        cpPaymentType.setAdapter(adapter);
//        cpPaymentType.setThreshold(1);
//        cpPaymentType.setOnClickListener(v -> cpPaymentType.showDropDown());
//
//        cpPaymentType.setOnItemClickListener((parent, view, position, id) -> {
//            String selectedType = cpPaymentType.getText().toString().trim();
//
//            if (selectedType.equalsIgnoreCase("CASH")) {
//
//                // Disable bank initial and prevent dropdown popup
//                cpBankInitial.setEnabled(false);
//                layoutBankInitial.setEnabled(false);   // <-- Disable layout
//
//                // Disable bank name
//                cpBankName.setEnabled(false);
//                layoutBankName.setEnabled(false);      // <-- Disable layout
//
//                // Disable check number
//                cpChkNumber.setEnabled(false);
//                layoutChkNumber.setEnabled(false);      // <-- Disable layout
//
//                // Clear values
//                cpBankInitial.setText("");
//                cpBankName.setText("");
//                cpChkNumber.setText("");
//
//                Log.d("PAYMENT_TYPE", "Selected CASH — bank fields disabled");
//
//            } else if (selectedType.equalsIgnoreCase("PDC")) {
//
//                // Enable everything back
//                cpBankInitial.setEnabled(true);
//                layoutBankInitial.setEnabled(true);
//
//                cpBankName.setEnabled(true);
//                layoutBankName.setEnabled(true);
//
//                cpChkNumber.setEnabled(true);
//                layoutChkNumber.setEnabled(true);
//
//                cpPaymentAmount.setEnabled(true);
//
//                cpBankInitial.requestFocus();
//
//                Log.d("PAYMENT_TYPE", "Selected PDC — bank fields enabled");
//            }
//        });
//    }
//
//    /** ====================== Setup Bank Initial Dropdown ====================== */
//    private void setupBankInitialDropdown(ArrayList<String> qBankList) {
//
//        if (qBankList == null) return;
//
//        ArrayList<String> onlyInitials = new ArrayList<>();
//        final ArrayList<String> bankNameList = new ArrayList<>();
//
//        for (String row : qBankList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 2) {
//                onlyInitials.add(parts[0]);
//                bankNameList.add(parts[1]);
//            }
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_dropdown_item_1line,
//                onlyInitials
//        );
//
//        cpBankInitial.setAdapter(adapter);
//        cpBankInitial.setThreshold(1);
//
//        cpBankInitial.setOnClickListener(v -> cpBankInitial.showDropDown());
//
//        cpBankInitial.setOnItemClickListener((parent, view, position, id) -> {
//            cpBankName.setText(bankNameList.get(position));
//            cpChkNumber.requestFocus();
//        });
//    }
//
//    /** ====================== Amount Formatter ====================== */
//    private void applyAmountFormatter(EditText editText) {
//        editText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                try {
//                    String raw = editText.getText().toString().replace(",", "");
//                    if (!raw.isEmpty()) {
//                        double amount = Double.parseDouble(raw);
//                        editText.setText(new DecimalFormat("#,###.00").format(amount));
//                    }
//                } catch (Exception e) {
//                    Log.e("AMOUNT_FORMAT", "Formatting error", e);
//                }
//            }
//        });
//    }
//
//    /** =========================== Invoice input logic =========================== */
//    private void setupInvoiceInputLogic() {
//        inputInvoice.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                String typed = inputInvoice.getText().toString().trim();
//                if (typed.isEmpty()) {
//                    invoiceAmount.setText("");
//                    return;
//                }
//                int index = invoiceNumberList.indexOf(typed);
//                if (index >= 0) {
//                    try {
//                        double amt = Double.parseDouble(invoiceAmountList.get(index));
//                        DecimalFormat df = new DecimalFormat("#,###.00");
//                        invoiceAmount.setText(df.format(amt));
//                    } catch (Exception e) {
//                        invoiceAmount.setText(invoiceAmountList.get(index));
//                    }
//                } else {
//                    invoiceAmount.setText("");
//                }
//            }
//        });
//    }
//
//    /** =========================== Add invoice item =========================== */
//    private void addInvoiceItem() {
//        String tempInvoice = inputInvoice.getText().toString().trim();
//        String tempAmount = invoiceAmount.getText().toString().trim();
//        String dept;
//
//        if (spInvoiceItem.getText().toString().equalsIgnoreCase("OTHER")) {
//            if (tempInvoice.isEmpty() || tempAmount.isEmpty()) {
//                Toast.makeText(this, "Please input Invoice Number and Amount!", Toast.LENGTH_LONG).show();
//                return;
//            }
//            dept = "N/A";
//        } else {
//            if (tempInvoice.isEmpty()) {
//                Toast.makeText(this, "Please select Invoice Number!", Toast.LENGTH_LONG).show();
//                return;
//            }
//            int index = invoiceNumberList.indexOf(tempInvoice);
//            tempAmount = invoiceAmountList.get(index);
//            dept = invoiceDeptList.get(index);
//        }
//
//        InvoiceItem newItem = new InvoiceItem(tempInvoice, tempAmount, dept);
//        invoiceItemList.add(newItem);
//        invoiceAdapter.notifyDataSetChanged();
//        recalcTotal();
//
//        /** Clear inputs */
//        inputInvoice.setText("");
//        invoiceAmount.setText("");
//        spInvoiceItem.setText("");
//        inputInvoice.setEnabled(true);
//        invoiceAmount.setEnabled(true);
//        spInvoiceItem.setEnabled(true);
//        categoryDropdown.setText("");
//        deductionAmount.setText("");
//
//        refreshInvoiceDropdown();
//        Toast.makeText(this, "Invoice Added!", Toast.LENGTH_SHORT).show();
//    }
//
//    /** =========================== Setup add deduction button =========================== */
//    private void setupAddDeductionButton() {
//        btnAddDeduction.setOnClickListener(v -> {
//            String selectedCategory = categoryDropdown.getText().toString().trim();
//            String amount = deductionAmount.getText().toString().trim();
//
//            if (selectedCategory.isEmpty()) {
//                Toast.makeText(this, "Please select a category!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (amount.isEmpty()) {
//                Toast.makeText(this, "Please enter deduction amount!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Add CategoryItem with checkbox false initially
//            CategoryItem item = new CategoryItem(selectedCategory, amount);
//            item.setSelected(false);
//            deductionList.add(item);
//            deductionAdapter.notifyDataSetChanged();
//
//            // Recalculate total deduction
//            recalcTotalDeduction();
//
//            categoryDropdown.setText("");
//            deductionAmount.setText("");
//            categoryDropdown.requestFocus();
//
//            Toast.makeText(this, "Deduction added!", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    private void addPayment() {
//        String paymentType = cpPaymentType.getText().toString().trim();
//        String bankInitial = cpBankInitial.getText().toString().trim();
//        String chkNumber = cpChkNumber.getText().toString().trim();
//        String amount = cpPaymentAmount.getText().toString().trim();
//
//        if (paymentType.isEmpty() || amount.isEmpty()) {
//            Toast.makeText(this, "Please enter payment type and amount!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // For CASH, bank details are ignored
//        if (paymentType.equalsIgnoreCase("CASH")) {
//            bankInitial = "";
//            chkNumber = "";
//        } else if (paymentType.equalsIgnoreCase("PDC")) {
//            if (bankInitial.isEmpty() || chkNumber.isEmpty()) {
//                Toast.makeText(this, "Please enter bank details for PDC!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        PaymentItem newItem = new PaymentItem(paymentType, bankInitial, chkNumber, amount);
//        paymentItemList.add(newItem);
//        paymentAdapter.notifyDataSetChanged();
//
//        recalcTotalPayment();
//
//        // Clear inputs
//        cpPaymentType.setText("");
//        cpBankInitial.setText("");
//        cpBankName.setText("");
//        cpChkNumber.setText("");
//        cpPaymentAmount.setText("");
//        cpPaymentType.requestFocus();
//
//        Toast.makeText(this, "Payment added!", Toast.LENGTH_SHORT).show();
//    }
//
//
//    /** =========================== Recalculate total invoice =========================== */
//    private void recalcTotal() {
//        double total = 0;
//        for (InvoiceItem item : invoiceItemList) {
//            try {
//                total += Double.parseDouble(item.getAmount());
//            } catch (Exception ignored) {}
//        }
//        // Format with commas
//        totalInvoiceAmount.setText(new DecimalFormat("###,###,###.##").format(total));
//    }
//
//    /** =========================== Recalculate total deduction =========================== */
//    private void recalcTotalDeduction() {
//        double totalDeduction = 0;
//        for (CategoryItem item : deductionList) {
//            try {
//                double amt = Double.parseDouble(item.getAmount().replace(",", ""));
//                totalDeduction += amt;
//            } catch (Exception ignored) {}
//        }
//        // Format with commas
//        totalDeductionAmount.setText(new DecimalFormat("###,###,###.##").format(totalDeduction));
//    }
//
//    /** =========================== Recalculate total payment amount =========================== */
//    private void recalcTotalPayment() {
//        double total = 0;
//        for (PaymentItem item : paymentItemList) {
//            try {
//                total += Double.parseDouble(item.getAmount().replace(",", ""));
//            } catch (Exception ignored) {}
//        }
//        totalPaymentAmount.setText(new DecimalFormat("#,###.00").format(total));
//    }
//
//    // -------------------- CLEAN THE SMS TEXT --------------------
//    private String cleanForSms(String raw) {
//        if (raw == null) return "";
//
//        return raw
//                .replace("\n", "")
//                .replace("\r", "")
//                .replace(",", "")
//                .trim();
//    }
//
//    // -------------------- OPEN SMS COMPOSER (Safe for all versions) --------------------
//    private void openSmsComposer(String number, String message) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("smsto:" + number));
//        intent.putExtra("sms_body", message);
//        startActivity(intent);
//    }
//
//    // ------------------ Add these helper methods to your class ------------------
//
//    /** Build COLA SMS for invoices */
//    private String buildInvoiceSms(String prNumber) {
//        // COLA/00001/ADCCP1035/11-12-2025 13:38:00/BCCR2025:2000!VCCR2025:2000!
//        StringBuilder sb = new StringBuilder();
//        sb.append("COLA/").append(prNumber).append("/").append(selectedCustomerID != null ? selectedCustomerID : "").append("/").append(syntaxDateTime != null ? syntaxDateTime : "").append("/");
//
//        // invoiceItemList -> invoiceNo:amount! ...
//        for (InvoiceItem item : invoiceItemList) {
//            String invNo = item.getInvoiceNo();
//            String amtRaw = item.getAmount() != null ? item.getAmount().replace(",", "") : "0";
//            // ensure no trailing decimals displayed unnecessarily
//            if (amtRaw.contains(".")) {
//                // keep as-is
//            }
//            sb.append(invNo).append(":").append(amtRaw).append("!");
//        }
//        return sb.toString();
//    }
//
//    /** Build COLC SMS for categories/deductions */
//    private String buildCategorySms(String prNumber) {
//        // COLC/00001/ADCCP1035/1:2000!2:2000!
//        StringBuilder sb = new StringBuilder();
//        sb.append("COLC/").append(prNumber).append("/").append(selectedCustomerID != null ? selectedCustomerID : "").append("/");
//
//        for (CategoryItem c : deductionList) {
//            String catName = c.getCategory();
//            String tag = getCategoryTag(catName); // tries to obtain numeric tag if available in invoiceCategoryList
//            String amt = c.getAmount() != null ? c.getAmount().replace(",", "") : "0";
//            sb.append(tag).append(":").append(amt).append("!");
//        }
//        return sb.toString();
//    }
//
//    /** Build COLD SMS for payments */
//    private String buildPaymentSms(String prNumber) {
//        // COLD/00001/ADCCP1035/CASH:0:0:2000!PDC:BDO:12345678:2000!
//        StringBuilder sb = new StringBuilder();
//        sb.append("COLD/").append(prNumber).append("/").append(selectedCustomerID != null ? selectedCustomerID : "").append("/");
//
//        for (PaymentItem p : paymentItemList) {
//            String type = p.getPaymentType() != null ? p.getPaymentType() : "";
//            String bankInit = p.getBankInitial() != null ? p.getBankInitial() : "";
//            String chkNo = p.getChkNumber() != null ? p.getChkNumber() : "";
//            String amt = p.getAmount() != null ? p.getAmount().replace(",", "") : "0";
//
//            // For CASH, user spec had "CASH:0:0:2000" - follow that (use 0 for bank/chk)
//            if (type.equalsIgnoreCase("CASH")) {
//                bankInit = "0";
//                chkNo = "0";
//            } else {
//                if (bankInit.isEmpty()) bankInit = "0";
//                if (chkNo.isEmpty()) chkNo = "0";
//            }
//
//            sb.append(type).append(":").append(bankInit).append(":").append(chkNo).append(":").append(amt).append("!");
//        }
//        return sb.toString();
//    }
//
//    /** Try to map a categoryName (e.g. "Bad Order") to a numeric tag using invoiceCategoryList contents.
//     *  Accepts invoiceCategoryList entries like "1:Bad Order" or "1|Bad Order" or "1|Bad Order|something".
//     *  If not found, returns the category name itself (so receiver can still parse), but preferable to have numeric tag.
//     */
//    private String getCategoryTag(String categoryName) {
//        if (invoiceCategoryList == null || invoiceCategoryList.isEmpty() || categoryName == null) {
//            return categoryName != null ? categoryName : "0";
//        }
//
//        for (String row : invoiceCategoryList) {
//            // try common separators
//            String[] tokens;
//            if (row.contains(":")) tokens = row.split(":");
//            else if (row.contains("\\|") || row.contains("|")) tokens = row.split("\\|");
//            else tokens = row.split("\\s+");
//
//            if (tokens.length >= 2) {
//                String tagCandidate = tokens[0].trim();
//                String descCandidate = tokens[1].trim();
//                // compare ignoring case and punctuation
//                if (descCandidate.equalsIgnoreCase(categoryName) || descCandidate.replaceAll("[^A-Za-z0-9]", "").equalsIgnoreCase(categoryName.replaceAll("[^A-Za-z0-9]", ""))) {
//                    return tagCandidate;
//                }
//            }
//        }
//        // fallback: try if categoryName itself starts with a number or is already a tag
//        if (categoryName.matches("^\\d+$")) return categoryName;
//        return categoryName; // fallback - receiver may handle it
//    }
//
//    /** Send SMS directly if permission present, otherwise open SMS composer with the message prefilled. */
//    private void sendOrComposeSms(String phoneNumber, String message) {
//        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
//            Log.w("SMS_SEND", "No recipient number provided - not sending message: " + message);
//            return;
//        }
//
//        if (hasSmsPermission()) {
//            // send directly
//            sendSmsDirectly(phoneNumber, message);
//        } else {
//            // open SMS composer so user can confirm/send
//            try {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
//                intent.putExtra("sms_body", message);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            } catch (Exception e) {
//                Log.e("SMS_SEND", "Failed to open SMS composer", e);
//                Toast.makeText(this, "Unable to open SMS composer: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    /** Check SEND_SMS permission */
//    private boolean hasSmsPermission() {
//        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void receiverNumber() {
//        Cursor rNumber = db.rawQuery("SELECT * FROM receiverNumber", null);
//        rNumber.moveToFirst();
//        int cRNumber = rNumber.getCount();
//        if (cRNumber > 0) {
//            Log.d("INSERT RECEIVER NUMBER", "Inserted receiver number" + cRNumber);
//        } else {
//            //
//            if ("A".equals(cpInstalledDepartment) || "L".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177105901');");
//            } else if ("CC".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177105906');");
//            } else if ("PL".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177105906');");
//            } else if ("MI".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177034043');");
//            } else if ("LY".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177105906');");
//            } else if ("AG".equals(cpInstalledDepartment) || "GT".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177105901');");
//            } else if ("CL".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177034043');");
//            } else if ("SO".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177105906');");
//            } else if ("ZS".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177034043');");
//            } else if ("BL".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177105906');");
//            } else if ("KL".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177034043');");
//            } else if ("UC".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177034043');");
//            } else if ("RM".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177034043');");
//            } else if ("EQ".equals(cpInstalledDepartment)) {
//                db.execSQL("Insert into receivernumber values('+639177105906');");
//            } else {
//                db.execSQL("Insert into receivernumber values('+639177105901');");
//            }
//        }
//        rNumber.close();
//    }
//
//    /** Send text using SmsManager (requires SEND_SMS permission already granted) */
//    private void sendSmsDirectly(String phoneNumber, String message) {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            ArrayList<String> parts = smsManager.divideMessage(message);
//            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
//            Log.d("SMS_SEND", "SMS sent to " + phoneNumber + ": " + message);
//        } catch (Exception e) {
//            Log.e("SMS_SEND", "Error sending SMS directly", e);
//            Toast.makeText(this, "Error sending SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//}
