//package com.marsIT.collection_app.CollectionData;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.InputFilter;
//import android.text.method.DigitsKeyListener;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.textfield.TextInputLayout;
//import com.marsIT.collection_app.CategoryAdapter.CategoryItem;
//import com.marsIT.collection_app.CategoryAdapter.DeductionAdapter;
//import com.marsIT.collection_app.InvoiceAdapter.InvoiceAdapter;
//import com.marsIT.collection_app.InvoiceAdapter.InvoiceItem;
//import com.marsIT.collection_app.MainProgram.MainActivity;
//import com.marsIT.collection_app.PaymentAdapter.PaymentAdapter;
//import com.marsIT.collection_app.PaymentAdapter.PaymentItem;
//import com.marsIT.collection_app.R;
//import com.marsIT.collection_app.ToolBar.BaseToolbar;
//
//import android.telephony.SmsManager;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//public class CollectionPayment extends BaseToolbar {
//
//    /** Customer info */
//    private String cpInstalledSalesman, cpInstalledDepartment, cpInstalledBranchName, cpSelectedCustomerID, cpSelectedCustomerName, cpSyntaxDateTime;
//
//    /** Table data */
//    private ArrayList<String> collectionCategoryList, collectionDetailsList, collectionHeaderList, collectionInvoiceItem;
//
//    /** UI references */
//    private TextView cpTSalesMan, cpTDepartment, cpTCustomerID, cpTCustomerName, cpTSyntaxDateTime;
//    private AutoCompleteTextView cpInvoiceItem, cpCategoryDropdown, cpPaymentType, cpBankInitial;
//    private EditText cpInputInvoice, cpInvoiceAmount, cpDeductionAmount, cpBankName, cpChkNumber, cpPaymentAmount, cpPrNumber;
//    private Button cpBtnAddInvoice, cpBtnDeleteInvoice, cpBtnAddDeduction, cpBtnDeleteDeduction, cpBtnAddPayment, cpBtnDltPayment, cpBtnSendSave;
//    private TextInputLayout cpLayoutBankInitial, cpLayoutBankName, cpLayoutChkNumber;
//    private RecyclerView cpInvoiceRecyclerView, cpDeductionRecyclerView, cpListOfCollectionsRecycler;
//    private TextView cpTotaLInvoiceAmount, cpTotalDeductionAmount, cpTotalPaymentAmount, cpTotalBalance, cpTotalAmountDue;
//
//    /** Invoice storage */
//    private ArrayList<String> cpSalesHeaderList, cpInvoiceNumberList = new ArrayList<>(), cpInvoiceAmountList = new ArrayList<>(),
//        cpInvoiceDeptList = new ArrayList<>(), cpInvoiceCategoryList;
//    private ArrayList<InvoiceItem> cpInvoiceItemList = new ArrayList<>();
//    private InvoiceAdapter cpInvoiceAdapter;
//
//    /** Deduction storage */
//    private ArrayList<CategoryItem> cpDeductionList = new ArrayList<>();
//    private DeductionAdapter cpDeductionAdapter;
//
//    /** Payment storage */
//    private ArrayList<PaymentItem> cpPaymentItemList = new ArrayList<>();
//    private PaymentAdapter cpPaymentAdapter;
//
//    /** Initialize database */
//    private SQLiteDatabase db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.collection_payment);
//
//        try {
//            setupToolbar("COLLECTION PAYMENT");
//
//            /** Initialize database */
//            initializeDatabase();
//
//            /** Initialize views */
//            initializeViews();
//
//            /** Initialize receiver number */
//            receiverNumber();
//            cpInvoiceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//            /** Customer info */
//            cpInstalledSalesman = getIntent().getStringExtra("installedSalesmanName");
//            cpInstalledDepartment = getIntent().getStringExtra("installedDepartment");
//            cpInstalledBranchName = getIntent().getStringExtra("installedBranchName");
//            cpSelectedCustomerID = getIntent().getStringExtra("selectedCustomerID");
//            cpSelectedCustomerName = getIntent().getStringExtra("selectedCustomerName");
//            cpSyntaxDateTime = getIntent().getStringExtra("syntaxDateTime");
//
//            /** Format date */
//            cpTSyntaxDateTime.setText(formatDateTime(cpSyntaxDateTime));
//
//            /** Set customer info */
//            cpTSalesMan.setText(cpInstalledSalesman);
//            convertDepartment();
//            cpTCustomerID.setText(cpSelectedCustomerID != null ? cpSelectedCustomerID : "N/A");
//            cpTCustomerName.setText(cpSelectedCustomerName != null ? cpSelectedCustomerName : "N/A");
//
//            // ================== LOAD CUSTOMER TOTAL AMOUNT DUE ==================
//            if (cpSelectedCustomerID != null && !cpSelectedCustomerID.isEmpty()) {
//                loadCustomerTotalInvoiceBalance(cpSelectedCustomerID);
//            } else {
//                cpTotalAmountDue.setText("0.00");
//            }
//
//            /** Receive collection tables */
//            collectionCategoryList = getIntent().getStringArrayListExtra("collectionCategoryList");
//            collectionDetailsList = getIntent().getStringArrayListExtra("collectionDetailsList");
//            collectionHeaderList = getIntent().getStringArrayListExtra("collectionHeaderList");
//            collectionInvoiceItem = getIntent().getStringArrayListExtra("collectionInvoiceItem");
//
//            /** Receive invoice list */
//            cpSalesHeaderList = getIntent().getStringArrayListExtra("salesHeaderList");
//
//            /** Receive category list */
//            cpInvoiceCategoryList = getIntent().getStringArrayListExtra("invoiceCategoryList");
//            setupCategoryDropdown();
//
////                if (cpSalesHeaderList != null && !cpSalesHeaderList.isEmpty()) {
////                    loadInvoiceData();
////                    setupInvoiceDropdown();
////                    setupInvoiceInputLogic();
////                    setupInvoiceRecyclerView();
////                }
//
//            // Always setup dropdown, even if sales header is empty
//            loadInvoiceData(); // safe even if list empty
//            setupInvoiceDropdown();
//            setupInvoiceInputLogic();
//            setupInvoiceRecyclerView();
//
//            /** Setup deduction list */
//            setupDeductionRecyclerView();
//            setupAddDeductionButton();
//
//            /** Setup banklist & paymentType list */
//            setupPaymentTypeDropdown(getIntent().getStringArrayListExtra("paymentTypeList"));
//            setupBankInitialDropdown(getIntent().getStringArrayListExtra("bankList"));
//
//            /** Amount Formatter */
//            applyAmountFormatter(cpPaymentAmount);
//            applyAmountFormatter(cpInvoiceAmount);
//            applyAmountFormatter(cpDeductionAmount);
//
//            /** Delete Deduction button logic */
//            cpBtnDeleteDeduction.setOnClickListener(v -> {
//                cpDeductionAdapter.deleteSelected();
//                recalcTotalDeduction();
//                showToast("Selected deductions deleted!");
//            });
//
//            /** Add Invoice button */
//            cpBtnAddInvoice.setOnClickListener(v -> addInvoiceItem());
//
//            /** Delete invoices */
//            cpBtnDeleteInvoice.setOnClickListener(v -> {
//                cpInvoiceAdapter.deleteSelected();
//                recalcTotal();
//                refreshInvoiceDropdown();
//                showToast("Selected invoices deleted!");
//            });
//
//            /** RecyclerView setup */
//            cpListOfCollectionsRecycler.setLayoutManager(new LinearLayoutManager(this));
//            cpListOfCollectionsRecycler.setAdapter(cpPaymentAdapter);
//
//            /** Add Payment button */
//            cpBtnAddPayment.setOnClickListener(v -> addPayment());
//
//            /** Delete Payment button */
//            cpBtnDltPayment.setOnClickListener(v -> {
//                cpPaymentAdapter.deleteSelected();
//                recalcTotalPayment();
//                showToast("Selected payments deleted!");
//            });
//
//            /** Send and Save Transaction details */
//            cpBtnSendSave.setOnClickListener(v -> {
//
//                // Recalculate before sending the Total Payment + Deduction must equal Invoice Amount
//                //                if (!recalcAndValidateBalance()) {
//                //                    showToast("Total Payment + Deduction must equal Invoice Amount!");
//                //                    return;
//                //                }
//
//                // Recalculate and display balance with color
//                recalcAndDisplayBalance();
//
//                // Get amounts directly from input fields
//                double deductionAmount = getAmount(cpTotalDeductionAmount);
//                double paymentAmount   = getAmount(cpTotalPaymentAmount);
//                double invoiceAmount   = getAmount(cpTotaLInvoiceAmount); // total invoice
//
//                double totalEntered = deductionAmount + paymentAmount;
//                double balance = invoiceAmount - totalEntered;
//
//                // If balance is not zero, stop user from saving
//                if (Math.abs(balance) > 0.009) {
//                    showToast("Total Payment + Deduction must equal Invoice Amount!");
//                    return;
//                }
//
//                // Ensure PR Number is entered
//                String prNumber = cpPrNumber.getText().toString().trim();
//
//                if (prNumber.isEmpty()) {
//                    showToast("Please enter PR Number");
//                    return;
//                }
//
//                // Check SMS permission (Android M+)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                    checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                    showToast("SMS permission not granted");
//                    requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 1);
//                    return;
//                }
//
//                saveCollectionData(prNumber);       // Save collection data to database
//
//                try {
//                    sendCollectionSMS(prNumber);        // Then send SMS
//                } catch (Exception e) {
//                    Log.e("sendCollectionSMS", "SMS send failed", e);
//                }
//                // Finish activity
//                CollectionPayment.this.finish(); //TODO: Finish after the transaction
//            });
//
//        } catch (Exception e) {
//            Log.e("COLLECTION_PAYMENT", "Error initializing CollectionPayment", e);
//            Toast.makeText(this, "Error loading collection data: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void goBackToMain() {
//        Intent intent = new Intent(CollectionPayment.this, MainActivity.class);
//
//        // Clear back stack so user can't return to CollectionPayment
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//            Intent.FLAG_ACTIVITY_NEW_TASK |
//            Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        startActivity(intent);
//        finish(); // close CollectionPayment
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (db != null && db.isOpen()) {
//            db.close();
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "SMS permission denied. Cannot send SMS.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    /** =========================== Initialize Views =========================== */
//    private void initializeViews() {
//
//        /** Total Balance for Collection */
//        cpTotalAmountDue = findViewById(R.id.totalAmountDue);
//
//        /** Other Details */
//        cpTSalesMan = findViewById(R.id.salesmanName);
//        cpPrNumber = findViewById(R.id.prNumber);
//        cpTDepartment = findViewById(R.id.sDepartment);
//        cpTCustomerID = findViewById(R.id.customerID);
//        cpTCustomerName = findViewById(R.id.customerName);
//        cpTSyntaxDateTime = findViewById(R.id.syntaxDateTime);
//
//        /** Invoice UI */
//        cpInvoiceItem = findViewById(R.id.salesmanlist1);
//        cpInputInvoice = findViewById(R.id.inputInvoice);
//        cpInvoiceAmount = findViewById(R.id.invoiceAmount);
//        cpBtnAddInvoice = findViewById(R.id.btnAddInvoice);
//        cpBtnDeleteInvoice = findViewById(R.id.btnDltInvoice);
//        cpInvoiceRecyclerView = findViewById(R.id.listOfInvoiceRecycler);
//        cpTotaLInvoiceAmount = findViewById(R.id.totalInvoiceAmount);
//
//        /** Category UI */
//        cpCategoryDropdown = findViewById(R.id.category);
//        cpDeductionAmount = findViewById(R.id.deductionAmount);
//        cpBtnAddDeduction = findViewById(R.id.btnAddDeduction);
//        cpBtnDeleteDeduction = findViewById(R.id.btnDltDeduction);
//        cpDeductionRecyclerView = findViewById(R.id.listOfDeductionRecycler);
//        cpTotalDeductionAmount = findViewById(R.id.totalDeductionAmount);
//
//        /** Payment UI */
//        cpPaymentType = findViewById(R.id.paymentType);
//        cpBankInitial = findViewById(R.id.bankInitial);
//        cpBankName = findViewById(R.id.bankName);
//        cpChkNumber = findViewById(R.id.chkNumber);
//        cpPaymentAmount = findViewById(R.id.paymentAmount);
//        cpLayoutBankInitial = findViewById(R.id.layoutBankInitial);
//        cpLayoutBankName = findViewById(R.id.layoutBankName);
//        cpLayoutChkNumber = findViewById(R.id.layoutChkNumber);
//        cpBtnAddPayment = findViewById(R.id.btnAddPayment);
//        cpBtnDltPayment = findViewById(R.id.btnDltPayment);
//
//        /** Total Balance for Collection */
//        cpTotalBalance = findViewById(R.id.totalBalance);
//
//        /** Collections RecyclerView */
//        cpListOfCollectionsRecycler = findViewById(R.id.listOfCollectionsRecycler);
//        cpTotalPaymentAmount = findViewById(R.id.totalPaymentAmount);
//
//        /** Adapter initialization */
//        cpPaymentAdapter = new PaymentAdapter(cpPaymentItemList);
//
//        /** Send and Save Transaction details */
//        cpBtnSendSave = findViewById(R.id.btnSendSave);
//
//        /** =========================== Allow only numbers =========================== */
//        cpPrNumber.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
//        cpPrNumber.setKeyListener(DigitsKeyListener.getInstance("0123456789")); // only digits
//        cpPrNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)}); // optional max length
//    }
//
//    /** Initialize or open database */
//    private void initializeDatabase() {
//        if (db == null || !db.isOpen()) {
//            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
//        }
//    }
//
//    /** =========================== Convert Department =========================== */
//    private void convertDepartment() {
//        switch (cpInstalledDepartment) {
//            case "A": cpTDepartment.setText("AUTO-SUPPLY"); break;
//            case "L": cpTDepartment.setText("LUBRICANTS"); break;
//            case "CC": cpTDepartment.setText("CENTURY"); break;
//            case "PL": cpTDepartment.setText("PEERLESS"); break;
//            case "MI": cpTDepartment.setText("MONTOSCO"); break;
//            case "LY": cpTDepartment.setText("LAMOIYAN"); break;
//            case "AG": cpTDepartment.setText("AGRICHEM"); break;
//            case "CL": cpTDepartment.setText("COLUMBIA"); break;
//            case "SO": cpTDepartment.setText("SOLANE"); break;
//            case "ZS": cpTDepartment.setText("ZESTAR"); break;
//            case "BL": cpTDepartment.setText("BELO"); break;
//            case "GT": cpTDepartment.setText("GTAM"); break;
//            case "KL": cpTDepartment.setText("KOHL"); break;
//            case "UC": cpTDepartment.setText("URC"); break;
//            case "RM": cpTDepartment.setText("RAM"); break;
//            case "EQ": cpTDepartment.setText("EQ"); break;
//            default: cpTDepartment.setText(cpInstalledDepartment);
//        }
//    }
//
//    /** =========================== Format DateTime =========================== */
//    private String formatDateTime(String syntaxDateTime) {
//        if (syntaxDateTime == null) return "N/A";
//        try {
//            SimpleDateFormat incomingFormat = new SimpleDateFormat("M-dd-yyyy HH:mm:ss", Locale.getDefault());
//            Date date = incomingFormat.parse(syntaxDateTime);
//            SimpleDateFormat displayFormat = new SimpleDateFormat("M-dd-yyyy HH:mm:ss", Locale.getDefault());
//            return displayFormat.format(date);
//        } catch (ParseException e) {
//            Log.e("COLLECTION_PAYMENT", "Error parsing syntaxDateTime", e);
//            return "N/A";
//        }
//    }
//
//    /** =========================== Load Invoice Data =========================== */
//    private void loadInvoiceData() {
//        for (String row : cpSalesHeaderList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 3) {
//                cpInvoiceNumberList.add(parts[0]);
//                cpInvoiceAmountList.add(parts[2]);
//                cpInvoiceDeptList.add(parts.length >= 4 ? parts[3] : "N/A");
//            }
//        }
//    }
//
//    /** =========================== Setup Invoice RecyclerView =========================== */
//    private void setupInvoiceRecyclerView() {
//        cpInvoiceAdapter = new InvoiceAdapter(cpInvoiceItemList, item -> {
//            cpInputInvoice.setText(item.getInvoiceNo());
//            cpInvoiceAmount.setText(formatAmount(item.getAmount()));
//        });
//        cpInvoiceRecyclerView.setAdapter(cpInvoiceAdapter);
//    }
//
//    /** =========================== Setup Deduction RecyclerView =========================== */
//    private void setupDeductionRecyclerView() {
//        cpDeductionAdapter = new DeductionAdapter(cpDeductionList);
//        cpDeductionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        cpDeductionRecyclerView.setAdapter(cpDeductionAdapter);
//    }
//
//    /** =========================== Refresh Dropdown =========================== */
//    private void refreshInvoiceDropdown() {
//        ArrayList<String> dropdownList = new ArrayList<>();
//        for (String invoiceNo : cpInvoiceNumberList) {
//            boolean alreadyAdded = false;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                alreadyAdded = cpInvoiceItemList.stream()
//                    .anyMatch(item -> item.getInvoiceNo().equalsIgnoreCase(invoiceNo));
//            }
//            if (!alreadyAdded) dropdownList.add(invoiceNo);
//        }
//        dropdownList.add("OTHER");
//        cpInvoiceItem.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dropdownList));
//    }
//
//    /** =========================== Setup Invoice Dropdown =========================== */
////        private void setupInvoiceDropdown() {
////
////            // If no invoices exist, allow manual entry
////            if (cpInvoiceNumberList == null || cpInvoiceNumberList.isEmpty()) {
////
////                cpInvoiceItem.setText("");              // no dropdown value
////                cpInvoiceItem.setEnabled(false);        // disable dropdown
////                cpInputInvoice.setEnabled(true);        // allow typing
////                cpInvoiceAmount.setEnabled(true);       // allow typing
////                cpInputInvoice.requestFocus();
////
////                return;
////            }
////
////            refreshInvoiceDropdown();
////            cpInvoiceItem.setThreshold(1);
////            cpInvoiceItem.setOnClickListener(v -> cpInvoiceItem.showDropDown());
////            cpInvoiceItem.setOnItemClickListener((parent, view, position, id) -> {
////                String selected = cpInvoiceItem.getText().toString();
////                if ("OTHER".equalsIgnoreCase(selected)) {
////                    cpInputInvoice.setText("");
////                    cpInvoiceAmount.setText("");
////                    cpInputInvoice.setEnabled(true);
////                    cpInvoiceAmount.setEnabled(true);
////                    cpInputInvoice.requestFocus();
////                } else {
////                    int index = cpInvoiceNumberList.indexOf(selected);
////                    cpInputInvoice.setText(cpInvoiceNumberList.get(index));
////                    cpInvoiceAmount.setText(formatAmount(cpInvoiceAmountList.get(index)));
////                    cpInputInvoice.setEnabled(false);
////                    cpInvoiceAmount.setEnabled(true);
////                }
////            });
////        }
//
//    private void setupInvoiceDropdown() {
//        ArrayList<String> dropdownList = new ArrayList<>();
//
//        if (cpInvoiceNumberList != null && !cpInvoiceNumberList.isEmpty()) {
//            for (String invoice : cpInvoiceNumberList) {
//                boolean alreadyAdded = false;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    alreadyAdded = cpInvoiceItemList.stream()
//                        .anyMatch(item -> item.getInvoiceNo().equalsIgnoreCase(invoice));
//                }
//                if (!alreadyAdded) dropdownList.add(invoice);
//            }
//        }
//
//        // Always add OTHER
//        dropdownList.add("OTHER");
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//            android.R.layout.simple_dropdown_item_1line, dropdownList);
//        cpInvoiceItem.setAdapter(adapter);
//        cpInvoiceItem.setThreshold(0);
//        cpInvoiceItem.setEnabled(true);
//
//        // Preselect OTHER if list empty
//        if (cpInvoiceNumberList == null || cpInvoiceNumberList.isEmpty()) {
//            cpInvoiceItem.setText("OTHER", false);
//            cpInputInvoice.setEnabled(true);
//            cpInvoiceAmount.setEnabled(true);
//            cpInputInvoice.requestFocus();
//
//            // Force dropdown to show after layout is ready
//            cpInvoiceItem.post(() -> cpInvoiceItem.showDropDown());
//        }
//
//        cpInvoiceItem.setOnClickListener(v -> cpInvoiceItem.showDropDown());
//
//        cpInvoiceItem.setOnItemClickListener((parent, view, position, id) -> {
//            String selected = cpInvoiceItem.getText().toString();
//            if ("OTHER".equalsIgnoreCase(selected)) {
//                cpInputInvoice.setText("");
//                cpInvoiceAmount.setText("");
//                cpInputInvoice.setEnabled(true);
//                cpInvoiceAmount.setEnabled(true);
//                cpInputInvoice.requestFocus();
//            } else {
//                int index = cpInvoiceNumberList.indexOf(selected);
//                if (index >= 0) {
//                    cpInputInvoice.setText(cpInvoiceNumberList.get(index));
//                    cpInvoiceAmount.setText(formatAmount(cpInvoiceAmountList.get(index)));
//                    cpInputInvoice.setEnabled(false);
//                    cpInvoiceAmount.setEnabled(true);
//                }
//            }
//        });
//    }
//
//    /** =========================== Setup Category Dropdown =========================== */
//    private void setupCategoryDropdown() {
//        if (cpInvoiceCategoryList == null || cpInvoiceCategoryList.isEmpty()) return;
//        ArrayList<String> onlyCategories = new ArrayList<>();
//        for (String row : cpInvoiceCategoryList) onlyCategories.add(row.split("\\|")[0]);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, onlyCategories);
//        cpCategoryDropdown.setAdapter(adapter);
//        cpCategoryDropdown.setThreshold(1);
//        cpCategoryDropdown.setOnClickListener(v -> cpCategoryDropdown.showDropDown());
//        cpCategoryDropdown.setOnItemClickListener((parent, view, position, id) -> cpDeductionAmount.requestFocus());
//    }
//
//    /** =========================== Setup Payment Type Dropdown =========================== */
//    private void setupPaymentTypeDropdown(ArrayList<String> qPType) {
//        if (qPType == null) return;
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, qPType);
//        cpPaymentType.setAdapter(adapter);
//        cpPaymentType.setThreshold(1);
//        cpPaymentType.setOnClickListener(v -> cpPaymentType.showDropDown());
//        cpPaymentType.setOnItemClickListener((parent, view, position, id) -> handlePaymentTypeSelection());
//    }
//
//    /** =========================== Handle Payment Type Selection =========================== */
//    private void handlePaymentTypeSelection() {
//        String selectedType = cpPaymentType.getText().toString().trim();
//        boolean isCash = selectedType.equalsIgnoreCase("CASH");
//        boolean isPDC = selectedType.equalsIgnoreCase("PDC");
//
//        cpBankInitial.setEnabled(isPDC);
//        cpLayoutBankInitial.setEnabled(isPDC);
//        cpBankName.setEnabled(isPDC);
//        cpLayoutBankName.setEnabled(isPDC);
//        cpChkNumber.setEnabled(isPDC);
//        cpLayoutChkNumber.setEnabled(isPDC);
//
//        if (isCash) {
//            cpBankInitial.setText("");
//            cpBankName.setText("");
//            cpChkNumber.setText("");
//        } else if (isPDC) {
//            cpBankInitial.requestFocus();
//        }
//    }
//
//    /** =========================== Setup Bank Initial Dropdown =========================== */
//    private void setupBankInitialDropdown(ArrayList<String> qBankList) {
//        if (qBankList == null) return;
//        ArrayList<String> onlyInitials = new ArrayList<>();
//        ArrayList<String> bankNameList = new ArrayList<>();
//        for (String row : qBankList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 2) {
//                onlyInitials.add(parts[0]);
//                bankNameList.add(parts[1]);
//            }
//        }
//        cpBankInitial.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, onlyInitials));
//        cpBankInitial.setThreshold(1);
//        cpBankInitial.setOnClickListener(v -> cpBankInitial.showDropDown());
//        cpBankInitial.setOnItemClickListener((parent, view, position, id) -> cpBankName.setText(bankNameList.get(position)));
//    }
//
//    /** =========================== Amount Formatter =========================== */
//    private void applyAmountFormatter(EditText editText) {
//        editText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) editText.setText(formatAmount(editText.getText().toString()));
//        });
//    }
//
//    /** =========================== Format Amount =========================== */
//    private String formatAmount(String raw) {
//        if (raw == null || raw.isEmpty()) return "";
//        try {
//            double amount = Double.parseDouble(raw.replace(",", ""));
//            return new DecimalFormat("#,###.00").format(amount);
//        } catch (Exception e) {
//            Log.e("AMOUNT_FORMAT", "Formatting error", e);
//            return raw;
//        }
//    }
//
//    /** =========================== Invoice input logic =========================== */
//    private void setupInvoiceInputLogic() {
//        cpInputInvoice.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                String typed = cpInputInvoice.getText().toString().trim();
//                if (typed.isEmpty()) {
//                    cpInvoiceAmount.setText("");
//                    return;
//                }
//                int index = cpInvoiceNumberList.indexOf(typed);
//                cpInvoiceAmount.setText(index >= 0 ? formatAmount(cpInvoiceAmountList.get(index)) : "");
//            }
//        });
//    }
//
//    /** =========================== Add Invoice Item =========================== */
//    private void addInvoiceItem() {
//
//        try {
//            String tempInvoice = cpInputInvoice.getText().toString().trim();
//            String tempAmount = cpInvoiceAmount.getText().toString().trim();
//            String dept;
//
//            if ("OTHER".equalsIgnoreCase(cpInvoiceItem.getText().toString())) {
//                if (tempInvoice.isEmpty() || tempAmount.isEmpty()) {
//                    showToast("Please input Invoice Number and Amount!");
//                    return;
//                }
//                dept = "N/A";
//            } else {
//                if (tempInvoice.isEmpty()) {
//                    showToast("Please input Invoice Number and Amount!");
//                    return;
//                }
//                int index = cpInvoiceNumberList.indexOf(tempInvoice);
//                tempAmount = cpInvoiceAmountList.get(index);
//                dept = cpInvoiceDeptList.get(index);
//            }
//
//            cpInvoiceItemList.add(new InvoiceItem(tempInvoice, tempAmount, dept));
//            cpInvoiceAdapter.notifyDataSetChanged();
//            recalcTotal();
//
//            /** Clear inputs */
//            cpInputInvoice.setText("");
//            cpInvoiceAmount.setText("");
//            cpInvoiceItem.setText("");
//            cpInputInvoice.setEnabled(true);
//            cpInvoiceAmount.setEnabled(true);
//            cpInvoiceItem.setEnabled(true);
//            cpCategoryDropdown.setText("");
//            cpDeductionAmount.setText("");
//            refreshInvoiceDropdown();
//            showToast("Invoice Added!");
//        } catch (Exception e) {
//            Log.e("ADDED INVOICE", "ADDED ITEM: " + cpInputInvoice + cpInvoiceAmount, e);
//        }
//    }
//
//    /** =========================== Setup Add Deduction Button =========================== */
//    private void setupAddDeductionButton() {
//        cpBtnAddDeduction.setOnClickListener(v -> {
//            String selectedCategory = cpCategoryDropdown.getText().toString().trim();
//            String amount = cpDeductionAmount.getText().toString().trim();
//
//            if (selectedCategory.isEmpty()) { showToast("Please select a category!"); return; }
//            if (amount.isEmpty()) { showToast("Please input amount!"); return; }
//
//            cpDeductionList.add(new CategoryItem(selectedCategory, formatAmount(amount)));
//            cpDeductionAdapter.notifyDataSetChanged();
//            recalcTotalDeduction();
//
//            cpCategoryDropdown.setText("");
//            cpDeductionAmount.setText("");
//        });
//    }
//
//    /** =========================== Add Payment =========================== */
//    private void addPayment() {
//        String paymentType = cpPaymentType.getText().toString().trim();
//        String bankInitial = cpBankInitial.getText().toString().trim();
//        String chkNumber = cpChkNumber.getText().toString().trim();
//        String amount = cpPaymentAmount.getText().toString().trim();
//
//        if (paymentType.isEmpty() || amount.isEmpty()) {
//            showToast("Please enter payment type and amount!");
//            return;
//        }
//
//        /** For CASH, bank details are ignored */
//        if (paymentType.equalsIgnoreCase("CASH")) {
//            bankInitial = "";
//            chkNumber = "";
//        } else if (paymentType.equalsIgnoreCase("PDC")) {
//            if (bankInitial.isEmpty() || chkNumber.isEmpty()) {
//                showToast("Please enter bank details for PDC!");
//                return;
//            }
//        }
//
//        cpPaymentItemList.add(new PaymentItem(paymentType, bankInitial, chkNumber, formatAmount(amount)));
//        cpPaymentAdapter.notifyDataSetChanged();
//        recalcTotalPayment();
//
//        /** Clear inputs */
//        cpPaymentType.setText("");
//        cpBankInitial.setText("");
//        cpBankName.setText("");
//        cpChkNumber.setText("");
//        cpPaymentAmount.setText("");
//        cpPaymentType.requestFocus();
//
//        showToast("Payment added!");
//    }
//
//    /** =========================== Recalculate totals =========================== */
//    private void recalcTotal() {
//        double total = 0;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            total = cpInvoiceItemList.stream().mapToDouble(item -> parseDouble(item.getAmount())).sum();
//        }
//        cpTotaLInvoiceAmount.setText(formatAmount(String.valueOf(total)));
//        recalcAndDisplayBalance(); // recalculate
//    }
//
//    private void recalcTotalDeduction() {
//        double total = 0;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            total = cpDeductionList.stream().mapToDouble(item -> parseDouble(item.getAmount())).sum();
//        }
//        cpTotalDeductionAmount.setText(formatAmount(String.valueOf(total)));
//        recalcAndDisplayBalance(); // recalculate
//    }
//
//    private void recalcTotalPayment() {
//        double total = 0;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            total = cpPaymentItemList.stream().mapToDouble(item -> parseDouble(item.getAmount())).sum();
//        }
//        cpTotalPaymentAmount.setText(formatAmount(String.valueOf(total)));
//        recalcAndDisplayBalance(); // recalculate
//    }
//
//    /** =========================== Recalculate Balance & Validate Balance =========================== */
//    private void recalcAndDisplayBalance() {
//        double deductionAmount = getAmount(cpTotalDeductionAmount);
//        double paymentAmount   = getAmount(cpTotalPaymentAmount);
//        double invoiceAmount   = getAmount(cpTotaLInvoiceAmount); // use total invoice amount
//
//        double totalEntered = deductionAmount + paymentAmount;
//        double balance = invoiceAmount - totalEntered;
//
//        DecimalFormat df = new DecimalFormat("#,##0.00");
//
//        if (Math.round(balance * 100) == 0) {
//            cpTotalBalance.setText("0.00");
//            cpTotalBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
//        } else {
//            cpTotalBalance.setText(df.format(balance));
//            cpTotalBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
//        }
//    }
//
//    /** =========================== Get Amount for the Balance =========================== */
//    private double getAmount(TextView tv) {
//        try {
//            String value = tv.getText().toString().replace(",", "").trim();
//            return value.isEmpty() ? 0.0 : Double.parseDouble(value);
//        } catch (Exception e) {
//            return 0.0;
//        }
//    }
//
//    /** =========================== Customer Total Invoice Balance =========================== */
//    private void loadCustomerTotalInvoiceBalance(String customerID) {
//
//        if (db == null || !db.isOpen()) return;
//
//        double totalBalance = 0.0;
//
//        String query =
//            "SELECT IFNULL(SUM(CAST(REPLACE(Balance, ',', '') AS REAL)), 0) " +
//                "FROM SALESHEADER " +
//                "WHERE CustomerID = ? " +
//                "AND Refid NOT IN (SELECT InvoiceNo FROM SavedCollectionInvoice)";
//
//        Cursor cursor = db.rawQuery(query, new String[]{customerID});
//
//        if (cursor.moveToFirst()) {
//            totalBalance = cursor.getDouble(0);
//        }
//
//        cursor.close();
//
//        DecimalFormat df = new DecimalFormat("#,##0.00");
//        cpTotalAmountDue.setText(df.format(totalBalance));
//    }
//
//    /** =========================== Save Collection Data =========================== */
//    private void saveCollectionData(String prNumber) {
//        if (prNumber == null || prNumber.isEmpty()) {
//            showToast("PR Number is required!");
//            return;
//        }
//
//        try {
//            db.beginTransaction();
//
//            // Save CollectionHeader
//            String insertHeader = "INSERT INTO CollectionHeader (PRnumber, CustomerID, Salesman, SyntaxID, SyntaxDateTime) VALUES (?, ?, ?, ?, ?)";
//            db.execSQL(insertHeader, new Object[]{
//                prNumber,
//                cpSelectedCustomerID != null ? cpSelectedCustomerID : "",
//                cpInstalledSalesman != null ? cpInstalledSalesman : "",
//                "", // SyntaxID placeholder
//                cpSyntaxDateTime != null ? cpSyntaxDateTime : ""
//            });
//
//            // Save Invoice Items
//            String insertInvoice = "INSERT INTO SavedCollectionInvoice (PRnumber, CustomerID, InvoiceNo, Amount, Department) VALUES (?, ?, ?, ?, ?)";
//            for (InvoiceItem item : cpInvoiceItemList) {
//                db.execSQL(insertInvoice, new Object[]{
//                    prNumber,
//                    cpSelectedCustomerID != null ? cpSelectedCustomerID : "",
//                    item.getInvoiceNo(),
//                    parseDouble(item.getAmount()),
//                    item.getDepartment()
//                });
//            }
//
//            // Save Deductions
//            String insertDeduction = "INSERT INTO SavedCollectionDeduction (PRnumber, CustomerID, Category, Amount) VALUES (?, ?, ?, ?)";
//            for (CategoryItem item : cpDeductionList) {
//                db.execSQL(insertDeduction, new Object[]{
//                    prNumber,
//                    cpSelectedCustomerID != null ? cpSelectedCustomerID : "",
//                    item.getCategory(),
//                    parseDouble(item.getAmount())
//                });
//            }
//
//            // Save Payments
//            String insertPayment = "INSERT INTO SavedCollectionPayment (PRnumber, CustomerID, PaymentType, BankInitial, CheckNumber, Amount) VALUES (?, ?, ?, ?, ?, ?)";
//            for (PaymentItem item : cpPaymentItemList) {
//                db.execSQL(insertPayment, new Object[]{
//                    prNumber,
//                    cpSelectedCustomerID != null ? cpSelectedCustomerID : "",
//                    item.getPaymentType(),
//                    item.getBankInitial(),
//                    item.getChkNumber(),
//                    parseDouble(item.getAmount())
//                });
//            }
//
//            db.setTransactionSuccessful();
//            showToast("Collection saved successfully!");
//
//        } catch (Exception e) {
//            Log.e("SAVE_COLLECTION", "Error saving collection data", e);
//            showToast("Error saving collection: " + e.getMessage());
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    /** =========================== Utility =========================== */
//    private double parseDouble(String val) {
//        if (val == null || val.isEmpty()) return 0;
//        try {
//            return Double.parseDouble(val.replace(",", ""));
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    private void showToast(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//    }
//
//    private String getColPrefix(char type) {
//        if ("MARS2".equalsIgnoreCase(cpInstalledBranchName)) {
//            return "COL2" + type;
//        } else {
//            return "COL" + type;
//        }
//    }
//
//    /** =========================== Build SMS Messages =========================== */
//
//    /** Build COLA SMS for invoices */
//    // Example: COLA/00001/ADCCP1035/11-12-2025 13:38:00/BCCR2025:2000!VCCR2025:2000!
//    private String buildInvoiceSms(String prNumber) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getColPrefix('A')).append("/")
//            .append(prNumber).append("/")
//            .append(cpSelectedCustomerID != null ? cpSelectedCustomerID : "").append("/")
//            .append(cpSyntaxDateTime != null ? cpSyntaxDateTime : "").append("/");
//
//        for (InvoiceItem item : cpInvoiceItemList) {
//            // InvoiceNo:Amount!
//            sb.append(item.getInvoiceNo())
//                .append(":")
//                .append(formatAmountNoComma(item.getAmount()))
//                .append("!");
//        }
//        return sb.toString();
//    }
//
//    /** Build COLC SMS for categories/deductions */
//    // Example: COLC/00001/ADCCP1035/1:2000!2:2000!
//    private String buildCategorySms(String prNumber) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getColPrefix('C')).append("/")
//            .append(prNumber).append("/")
//            .append(cpSelectedCustomerID != null ? cpSelectedCustomerID : "").append("/");
//        //                .append(cpSyntaxDateTime != null ? cpSyntaxDateTime : "").append("/");
//
//        for (int i = 0; i < cpDeductionList.size(); i++) {
//            CategoryItem item = cpDeductionList.get(i);
//            // Index:Amount!
//            sb.append(i + 1).append(":").append(formatAmountNoComma(item.getAmount())).append("!");
//        }
//        return sb.toString();
//    }
//
//    /** Build COLD SMS for payments */
//    // Example: COLD/00001/ADCCP1035/CASH:0:0:2000!PDC:BDO:12345678:2000!
//    private String buildPaymentSms(String prNumber) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getColPrefix('D')).append("/")
//            .append(prNumber).append("/")
//            .append(cpSelectedCustomerID != null ? cpSelectedCustomerID : "").append("/");
//        //                .append(cpSyntaxDateTime != null ? cpSyntaxDateTime : "").append("/");
//
//        for (PaymentItem item : cpPaymentItemList) {
//            if (item.getPaymentType().equalsIgnoreCase("CASH")) {
//                // CASH:0:0:Amount!
//                sb.append("CASH:0:0:").append(formatAmountNoComma(item.getAmount())).append("!");
//            } else if (item.getPaymentType().equalsIgnoreCase("PDC")) {
//                // PDC:BankInitial:CheckNumber:Amount!
//                sb.append("PDC:")
//                    .append(item.getBankInitial() != null ? item.getBankInitial() : "0").append(":")
//                    .append(item.getChkNumber() != null ? item.getChkNumber() : "0").append(":")
//                    .append(formatAmountNoComma(item.getAmount()))
//                    .append("!");
//            }
//        }
//        return sb.toString();
//    }
//
//    /** =========================== Helper Methods =========================== */
//    /** Format amount without commas for SMS */
//    private String formatAmountNoComma(String raw) {
//        if (raw == null || raw.isEmpty()) return "0.00";
//        try {
//            double amount = Double.parseDouble(raw.replace(",", ""));
//            return new DecimalFormat("0.00").format(amount); // with decimals
//        } catch (Exception e) {
//            Log.e("SMS_FORMAT", "Error formatting amount for SMS", e);
//            return "0.00";
//        }
//    }
//
//    private void receiverNumber() {
//        Cursor receiverNum = db.rawQuery("SELECT * FROM receivernumber", null);
//        try {
//            if (receiverNum.moveToFirst()) {
//                Log.d("receiverNumber", "Record exists: " + receiverNum.getCount());
//                return; // already exists, no need to insert
//            }
//
//            // Map departments to receiver numbers
//            Map<String, String> deptNumberMap = new HashMap<>();
//            String defaultNumber = "+639177105901";
//
//            // Departments sharing same number
//            String[] num1Depts = {"UC", "RM", "CL", "MI"};
//            String[] num2Depts = {"LY", "CC", "PL", "SO", "EQ"};
//
//            for (String d : num1Depts) deptNumberMap.put(d, "+639177034043");
//            for (String d : num2Depts) deptNumberMap.put(d, "+639177105906");
//
//            // Determine number for current department
//            String number = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                number = deptNumberMap.getOrDefault(cpInstalledDepartment, defaultNumber);
//            }
//
//            // Insert number
//            db.execSQL("INSERT INTO receivernumber VALUES('" + number + "');");
//            Log.d("receiverNumber", "Inserted receiver number: " + number);
//
//        } finally {
//            receiverNum.close();
//        }
//    }
//
//    /** =========================== Send Collection SMS =========================== */
//    private void sendCollectionSMS(String prNumber) {
//        if (prNumber == null || prNumber.isEmpty()) return;
//
//        Log.d("SMS_LOG", "Preparing to send collection SMS for PR: " + prNumber);
//
//        // Ensure receiver numbers exist
//        receiverNumber();
//
//        // Query receiver numbers
//        Cursor sendColSMS = db.rawQuery("SELECT * FROM receivernumber", null);
//        try {
//            if (sendColSMS != null && sendColSMS.moveToFirst()) {
//                do {
//                    String receiver = sendColSMS.getString(0);
//
//                    // =========================== Send COLA (Invoices) ==================  =========
//                    String invoiceMsg = buildInvoiceSms(prNumber); // COLA
//                    if (!invoiceMsg.isEmpty()) {
//                        Log.d("SMS_LOG", "Sending COLA to: " + receiver);
//                        sendSmsMultipart(receiver, invoiceMsg);
//                    }
//
//                    // =========================== Send COLC (Deductions) ===========================
//                    String categoryMsg = buildCategorySms(prNumber); // COLC
//                    if (!categoryMsg.isEmpty()) {
//                        Log.d("SMS_LOG", "Sending COLC to: " + receiver);
//                        sendSmsMultipart(receiver, categoryMsg);
//                    }
//
//                    // =========================== Send COLD (Payments) ===========================
//                    String paymentMsg = buildPaymentSms(prNumber); // COLD
//                    if (!paymentMsg.isEmpty()) {
//                        Log.d("SMS_LOG", "Sending COLD to: " + receiver);
//                        sendSmsMultipart(receiver, paymentMsg);
//                    }
//
//                } while (sendColSMS.moveToNext());
//            }
//        } finally {
//            if (sendColSMS != null) sendColSMS.close();
//        }
//    }
//
//    /** =========================== Send SMS in parts if long =========================== */
//    private void sendSmsMultipart(String phoneNumber, String message) {
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            ArrayList<String> parts = smsManager.divideMessage(message); // splits automatically if >160 chars
//
//            Log.d("SMS_LOG", "Sending " + parts.size() + " part(s) to " + phoneNumber);
//
//            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
//
//            Log.d("SMS_LOG", "SMS sent successfully to " + phoneNumber);
//            Toast.makeText(this, "SMS sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Log.e("SMS_LOG", "Error sending SMS to " + phoneNumber, e);
//            Toast.makeText(this, "Failed to send SMS to " + phoneNumber, Toast.LENGTH_LONG).show();
//        }
//    }
//}
