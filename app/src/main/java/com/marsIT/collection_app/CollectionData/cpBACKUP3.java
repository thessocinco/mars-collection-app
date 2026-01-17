//package com.marsIT.collection_app.CollectionData;
//
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Build;
//import android.os.Bundle;
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
//import com.marsIT.collection_app.DatabaseHelper.DBHelper;
//import com.marsIT.collection_app.InvoiceAdapter.InvoiceAdapter;
//import com.marsIT.collection_app.InvoiceAdapter.InvoiceItem;
//import com.marsIT.collection_app.PaymentAdapter.cpPaymentAdapter;
//import com.marsIT.collection_app.PaymentAdapter.PaymentItem;
//import com.marsIT.collection_app.R;
//import com.marsIT.collection_app.ToolBar.BaseToolbar;
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
//    private String cpInstalledSalesman, cpInstalledDepartment, selectedCustomerID, selectedCustomerName, syntaxDateTime;
//
//    /** Table data */
//    private ArrayList<String> collectionCategoryList, collectionDetailsList, collectionHeaderList, collectionInvoiceItem;
//
//    /** UI references */
//    private TextView cpSalesMan, cpDepartment, cpCustomerID, cpCustomerName, cpSyntaxDateTime;
//    private AutoCompleteTextView spInvoiceItem, categoryDropdown, cpPaymentType, cpBankInitial;
//    private EditText inputInvoice, invoiceAmount, deductionAmount, cpBankName, cpChkNumber, cpPaymentAmount;
//    private Button btnAddInvoice, btnDeleteInvoice, btnAddDeduction, btnDeleteDeduction, btnAddPayment, btnDltPayment;
//    private TextInputLayout layoutBankInitial, layoutBankName, layoutChkNumber;
//    private RecyclerView invoiceRecyclerView, deductionRecyclerView, listOfCollectionsRecycler;
//    private TextView totalInvoiceAmount, totalDeductionAmount, totalPaymentAmount;
//
//    /** Invoice storage */
//    private ArrayList<String> salesHeaderList, invoiceNumberList = new ArrayList<>(), invoiceAmountList = new ArrayList<>(),
//            invoiceDeptList = new ArrayList<>(), invoiceCategoryList;
//    private ArrayList<InvoiceItem> invoiceItemList = new ArrayList<>();
//    private InvoiceAdapter invoiceAdapter;
//
//    /** Deduction storage */
//    private ArrayList<CategoryItem> deductionList = new ArrayList<>();
//    private DeductionAdapter deductionAdapter;
//
//    /** Payment storage */
//    private ArrayList<PaymentItem> paymentItemList = new ArrayList<>();
//    private cpPaymentAdapter paymentAdapter;
//
//    /** SQLiteDatabase Helper */
//    private DBHelper dbHelper;
//    private SQLiteDatabase db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.collection_payment);
//        setupToolbar("Collection Payment");
//
//        try {
//            /** Initialize DBHelper */
//            dbHelper = new DBHelper(this);
//            db = dbHelper.getWritableDatabase();
//
//            /** Initialize views */
//            initializeViews();
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
//            cpSyntaxDateTime.setText(formatDateTime(syntaxDateTime));
//
//            /** Set customer info */
//            cpSalesMan.setText(cpInstalledSalesman);
//            convertDepartment();
//            cpCustomerID.setText(selectedCustomerID != null ? selectedCustomerID : "N/A");
//            cpCustomerName.setText(selectedCustomerName != null ? selectedCustomerName : "N/A");
//
//            /** Receive collection tables */
//            collectionCategoryList = getIntent().getStringArrayListExtra("collectionCategoryList");
//            collectionDetailsList = getIntent().getStringArrayListExtra("collectionDetailsList");
//            collectionHeaderList = getIntent().getStringArrayListExtra("collectionHeaderList");
//            collectionInvoiceItem = getIntent().getStringArrayListExtra("collectionInvoiceItem");
//
//            /** Receive invoice list */
//            salesHeaderList = getIntent().getStringArrayListExtra("salesHeaderList");
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
//            setupPaymentTypeDropdown(getIntent().getStringArrayListExtra("paymentTypeList"));
//            setupBankInitialDropdown(getIntent().getStringArrayListExtra("bankList"));
//
//            /** Amount Formatter */
//            applyAmountFormatter(cpPaymentAmount);
//            applyAmountFormatter(invoiceAmount);
//            applyAmountFormatter(deductionAmount);
//
//            /** Delete Deduction button logic */
//            btnDeleteDeduction.setOnClickListener(v -> {
//                deductionAdapter.deleteSelected();
//                recalcTotalDeduction();
//                showToast("Selected deductions deleted!");
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
//                showToast("Selected invoices deleted!");
//            });
//
//            /** RecyclerView setup */
//            listOfCollectionsRecycler.setLayoutManager(new LinearLayoutManager(this));
//            listOfCollectionsRecycler.setAdapter(paymentAdapter);
//
//            /** Add Payment button */
//            btnAddPayment.setOnClickListener(v -> addPayment());
//
//            /** Delete Payment button */
//            btnDltPayment.setOnClickListener(v -> {
//                paymentAdapter.deleteSelected();
//                recalcTotalPayment();
//                showToast("Selected payments deleted!");
//            });
//
//        } catch (Exception e) {
//            Log.e("COLLECTION_PAYMENT", "Error initializing CollectionPayment", e);
//            Toast.makeText(this, "Error loading collection data: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (db != null && db.isOpen()) db.close();
//    }
//
//    /** =========================== Initialize Views =========================== */
//    private void initializeViews() {
//        cpSalesMan = findViewById(R.id.salesmanName);
//        cpDepartment = findViewById(R.id.sDepartment);
//        cpCustomerID = findViewById(R.id.customerID);
//        cpCustomerName = findViewById(R.id.customerName);
//        cpSyntaxDateTime = findViewById(R.id.syntaxDateTime);
//
//        /** Invoice UI */
//        spInvoiceItem = findViewById(R.id.salesmanlist1);
//        inputInvoice = findViewById(R.id.inputInvoice);
//        invoiceAmount = findViewById(R.id.invoiceAmount);
//        btnAddInvoice = findViewById(R.id.btnAddInvoice);
//        btnDeleteInvoice = findViewById(R.id.btnDltInvoice);
//        invoiceRecyclerView = findViewById(R.id.listOfInvoiceRecycler);
//        totalInvoiceAmount = findViewById(R.id.totalInvoiceAmount);
//
//        /** Category UI */
//        categoryDropdown = findViewById(R.id.category);
//        deductionAmount = findViewById(R.id.deductionAmount);
//        btnAddDeduction = findViewById(R.id.btnAddDeduction);
//        btnDeleteDeduction = findViewById(R.id.btnDltDeduction);
//        deductionRecyclerView = findViewById(R.id.listOfDeductionRecycler);
//        totalDeductionAmount = findViewById(R.id.totalDeductionAmount);
//
//        /** Payment UI */
//        cpPaymentType = findViewById(R.id.paymentType);
//        cpBankInitial = findViewById(R.id.bankInitial);
//        cpBankName = findViewById(R.id.bankName);
//        cpChkNumber = findViewById(R.id.chkNumber);
//        cpPaymentAmount = findViewById(R.id.paymentAmount);
//        layoutBankInitial = findViewById(R.id.layoutBankInitial);
//        layoutBankName = findViewById(R.id.layoutBankName);
//        layoutChkNumber = findViewById(R.id.layoutChkNumber);
//        btnAddPayment = findViewById(R.id.btnAddPayment);
//        btnDltPayment = findViewById(R.id.btnDltPayment);
//
//        /** Collections RecyclerView */
//        listOfCollectionsRecycler = findViewById(R.id.listOfCollectionsRecycler);
//        totalPaymentAmount = findViewById(R.id.totalPaymentAmount);
//
//        /** Adapter initialization */
//        paymentAdapter = new cpPaymentAdapter(paymentItemList);
//    }
//
//    /** =========================== Convert Department =========================== */
//    private void convertDepartment() {
//        switch (cpInstalledDepartment) {
//            case "A": cpDepartment.setText("AUTO-SUPPLY"); break;
//            case "L": cpDepartment.setText("LUBRICANTS"); break;
//            case "CC": cpDepartment.setText("CENTURY"); break;
//            case "PL": cpDepartment.setText("PEERLESS"); break;
//            case "MI": cpDepartment.setText("MONTOSCO"); break;
//            case "LY": cpDepartment.setText("LAMOIYAN"); break;
//            case "AG": cpDepartment.setText("AGRICHEM"); break;
//            case "CL": cpDepartment.setText("COLUMBIA"); break;
//            case "SO": cpDepartment.setText("SOLANE"); break;
//            case "ZS": cpDepartment.setText("ZESTAR"); break;
//            case "BL": cpDepartment.setText("BELO"); break;
//            case "GT": cpDepartment.setText("GTAM"); break;
//            case "KL": cpDepartment.setText("KOHL"); break;
//            case "UC": cpDepartment.setText("URC"); break;
//            case "RM": cpDepartment.setText("RAM"); break;
//            case "EQ": cpDepartment.setText("EQ"); break;
//            default: cpDepartment.setText(cpInstalledDepartment);
//        }
//    }
//
//    /** =========================== Format DateTime =========================== */
//    private String formatDateTime(String syntaxDateTime) {
//        if (syntaxDateTime == null) return "N/A";
//        try {
//            SimpleDateFormat incomingFormat = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
//            Date date = incomingFormat.parse(syntaxDateTime);
//            SimpleDateFormat displayFormat = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
//            return displayFormat.format(date);
//        } catch (ParseException e) {
//            Log.e("COLLECTION_PAYMENT", "Error parsing syntaxDateTime", e);
//            return "N/A";
//        }
//    }
//
//    /** =========================== Load Invoice Data =========================== */
//    private void loadInvoiceData() {
//        for (String row : salesHeaderList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 3) {
//                invoiceNumberList.add(parts[0]);
//                invoiceAmountList.add(parts[2]);
//                invoiceDeptList.add(parts.length >= 4 ? parts[3] : "N/A");
//            }
//        }
//    }
//
//    /** =========================== Setup Invoice RecyclerView =========================== */
//    private void setupInvoiceRecyclerView() {
//        invoiceAdapter = new InvoiceAdapter(invoiceItemList, item -> {
//            inputInvoice.setText(item.getInvoiceNo());
//            invoiceAmount.setText(formatAmount(item.getAmount()));
//        });
//        invoiceRecyclerView.setAdapter(invoiceAdapter);
//    }
//
//    /** =========================== Setup Deduction RecyclerView =========================== */
//    private void setupDeductionRecyclerView() {
//        deductionAdapter = new DeductionAdapter(deductionList);
//        deductionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        deductionRecyclerView.setAdapter(deductionAdapter);
//    }
//
//    /** =========================== Refresh Dropdown =========================== */
//    private void refreshInvoiceDropdown() {
//        ArrayList<String> dropdownList = new ArrayList<>();
//        for (String invoiceNo : invoiceNumberList) {
//            boolean alreadyAdded = false;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                alreadyAdded = invoiceItemList.stream()
//                        .anyMatch(item -> item.getInvoiceNo().equalsIgnoreCase(invoiceNo));
//            }
//            if (!alreadyAdded) dropdownList.add(invoiceNo);
//        }
//        dropdownList.add("OTHER");
//        spInvoiceItem.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dropdownList));
//    }
//
//    /** =========================== Setup Invoice Dropdown =========================== */
//    private void setupInvoiceDropdown() {
//        refreshInvoiceDropdown();
//        spInvoiceItem.setThreshold(1);
//        spInvoiceItem.setOnClickListener(v -> spInvoiceItem.showDropDown());
//        spInvoiceItem.setOnItemClickListener((parent, view, position, id) -> {
//            String selected = spInvoiceItem.getText().toString();
//            if ("OTHER".equalsIgnoreCase(selected)) {
//                inputInvoice.setText("");
//                invoiceAmount.setText("");
//                inputInvoice.setEnabled(true);
//                invoiceAmount.setEnabled(true);
//                inputInvoice.requestFocus();
//            } else {
//                int index = invoiceNumberList.indexOf(selected);
//                inputInvoice.setText(invoiceNumberList.get(index));
//                invoiceAmount.setText(formatAmount(invoiceAmountList.get(index)));
//                inputInvoice.setEnabled(false);
//                invoiceAmount.setEnabled(true);
//            }
//        });
//    }
//
//    /** =========================== Setup Category Dropdown =========================== */
//    private void setupCategoryDropdown() {
//        if (invoiceCategoryList == null || invoiceCategoryList.isEmpty()) return;
//        ArrayList<String> onlyCategories = new ArrayList<>();
//        for (String row : invoiceCategoryList) onlyCategories.add(row.split("\\|")[0]);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, onlyCategories);
//        categoryDropdown.setAdapter(adapter);
//        categoryDropdown.setThreshold(1);
//        categoryDropdown.setOnClickListener(v -> categoryDropdown.showDropDown());
//        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> deductionAmount.requestFocus());
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
//        layoutBankInitial.setEnabled(isPDC);
//        cpBankName.setEnabled(isPDC);
//        layoutBankName.setEnabled(isPDC);
//        cpChkNumber.setEnabled(isPDC);
//        layoutChkNumber.setEnabled(isPDC);
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
//        inputInvoice.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                String typed = inputInvoice.getText().toString().trim();
//                if (typed.isEmpty()) {
//                    invoiceAmount.setText("");
//                    return;
//                }
//                int index = invoiceNumberList.indexOf(typed);
//                invoiceAmount.setText(index >= 0 ? formatAmount(invoiceAmountList.get(index)) : "");
//            }
//        });
//    }
//
//    /** =========================== Add Invoice Item =========================== */
//    private void addInvoiceItem() {
//        String tempInvoice = inputInvoice.getText().toString().trim();
//        String tempAmount = invoiceAmount.getText().toString().trim();
//        String dept;
//
//        if ("OTHER".equalsIgnoreCase(spInvoiceItem.getText().toString())) {
//            if (tempInvoice.isEmpty() || tempAmount.isEmpty()) {
//                showToast("Please input Invoice Number and Amount!");
//                return;
//            }
//            dept = "N/A";
//        } else {
//            if (tempInvoice.isEmpty()) {
//                showToast("Please select Invoice Number!");
//                return;
//            }
//            int index = invoiceNumberList.indexOf(tempInvoice);
//            tempAmount = invoiceAmountList.get(index);
//            dept = invoiceDeptList.get(index);
//        }
//
//        invoiceItemList.add(new InvoiceItem(tempInvoice, tempAmount, dept));
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
//        refreshInvoiceDropdown();
//        showToast("Invoice Added!");
//    }
//
//    /** =========================== Setup Add Deduction Button =========================== */
//    private void setupAddDeductionButton() {
//        btnAddDeduction.setOnClickListener(v -> {
//            String selectedCategory = categoryDropdown.getText().toString().trim();
//            String amount = deductionAmount.getText().toString().trim();
//
//            if (selectedCategory.isEmpty()) { showToast("Please select a category!"); return; }
//            if (amount.isEmpty()) { showToast("Please input amount!"); return; }
//
//            deductionList.add(new CategoryItem(selectedCategory, formatAmount(amount)));
//            deductionAdapter.notifyDataSetChanged();
//            recalcTotalDeduction();
//
//            categoryDropdown.setText("");
//            deductionAmount.setText("");
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
//        paymentItemList.add(new PaymentItem(paymentType, bankInitial, chkNumber, formatAmount(amount)));
//        paymentAdapter.notifyDataSetChanged();
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
//            total = invoiceItemList.stream().mapToDouble(item -> parseDouble(item.getAmount())).sum();
//        }
//        totalInvoiceAmount.setText(formatAmount(String.valueOf(total)));
//    }
//
//    private void recalcTotalDeduction() {
//        double total = 0;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            total = deductionList.stream().mapToDouble(item -> parseDouble(item.getAmount())).sum();
//        }
//        totalDeductionAmount.setText(formatAmount(String.valueOf(total)));
//    }
//
//    private void recalcTotalPayment() {
//        double total = 0;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            total = paymentItemList.stream().mapToDouble(item -> parseDouble(item.getAmount())).sum();
//        }
//        totalPaymentAmount.setText(formatAmount(String.valueOf(total)));
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
//                    prNumber,
//                    selectedCustomerID != null ? selectedCustomerID : "",
//                    cpInstalledSalesman != null ? cpInstalledSalesman : "",
//                    "", // SyntaxID placeholder
//                    syntaxDateTime != null ? syntaxDateTime : ""
//            });
//
//            // Save Invoice Items
//            String insertInvoice = "INSERT INTO SavedCollectionInvoice (PRnumber, CustomerID, InvoiceNo, Amount, Department) VALUES (?, ?, ?, ?, ?)";
//            for (InvoiceItem item : invoiceItemList) {
//                db.execSQL(insertInvoice, new Object[]{
//                        prNumber,
//                        selectedCustomerID != null ? selectedCustomerID : "",
//                        item.getInvoiceNo(),
//                        parseDouble(item.getAmount()),
//                        item.getDepartment()
//                });
//            }
//
//            // Save Deductions
//            String insertDeduction = "INSERT INTO SavedCollectionDeduction (PRnumber, CustomerID, Category, Amount) VALUES (?, ?, ?, ?)";
//            for (CategoryItem item : deductionList) {
//                db.execSQL(insertDeduction, new Object[]{
//                        prNumber,
//                        selectedCustomerID != null ? selectedCustomerID : "",
//                        item.getCategory(),
//                        parseDouble(item.getAmount())
//                });
//            }
//
//            // Save Payments
//            String insertPayment = "INSERT INTO SavedCollectionPayment (PRnumber, CustomerID, PaymentType, BankInitial, CheckNumber, Amount) VALUES (?, ?, ?, ?, ?, ?)";
//            for (PaymentItem item : paymentItemList) {
//                db.execSQL(insertPayment, new Object[]{
//                        prNumber,
//                        selectedCustomerID != null ? selectedCustomerID : "",
//                        item.getPaymentType(),
//                        item.getBankInitial(),
//                        item.getChkNumber(),
//                        parseDouble(item.getAmount())
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
//}
//
//
