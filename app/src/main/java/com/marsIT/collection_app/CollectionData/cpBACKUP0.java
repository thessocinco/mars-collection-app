//package com.marsIT.collection_app.CollectionData;
//
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
//    private TextInputLayout layoutBankInitial, layoutBankName, layoutChkNumber;
//    private Button btnAddPayment, btnDltPayment;
//
//    /** RecyclerView */
//    private RecyclerView listOfCollectionsRecycler;
//
//    /** Total Payment Amount */
//    private TextView totalPaymentAmount;
//
//    /** Storage */
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
//            /** Customer info */
//            cpSalesMan.setText(cpInstalledSalesman);
//            cpDepartment.setText(cpInstalledDepartment);
//            /** Convert Department */
//            convertDepartment();
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
//            /** Delete Deduction button logic */
//            btnDeleteDeduction.setOnClickListener(v -> {
//                deductionAdapter.deleteSelected();
//                recalcTotalDeduction();
//                Toast.makeText(this, "Selected deductions deleted!", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(this, "Selected payments deleted!", Toast.LENGTH_SHORT).show();
//            });
//
//        } catch (Exception e) {
//            Log.e("COLLECTION_PAYMENT", "Error initializing CollectionPayment", e);
//            Toast.makeText(this, "Error loading collection data: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
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
//    private void convertDepartment () {
//        cpDepartment.setText(cpInstalledDepartment);
//
//        if ("A".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("AUTO-SUPPLY");
//        } else if ("L".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("LUBRICANTS");
//        } else if ("CC".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("CENTURY");
//        } else if ("PL".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("PEERLESS");
//        } else if ("MI".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("MONTOSCO");
//        } else if ("LY".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("LAMOIYAN");
//        } else if ("AG".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("AGRICHEM");
//        } else if ("CL".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("COLUMBIA");
//        } else if ("SO".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("SOLANE");
//        } else if ("ZS".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("ZESTAR");
//        } else if ("BL".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("BELO");
//        } else if ("GT".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("GTAM");
//        } else if ("KL".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("KOHL");
//        } else if ("UC".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("URC");
//        } else if ("RM".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("RAM");
//        } else if ("EQ".equals(cpInstalledDepartment)) {
//            cpDepartment.setText("EQ");
//        } else {
//            cpDepartment.setText(cpInstalledDepartment);
//        }
//    }
//
//    /** =========================== Load Invoice Data =========================== */
//    private void loadInvoiceData() {
//        for (String row : salesHeaderList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 3) {
//                String invoiceNo = parts[0];
//                String amount = parts[2];
//                String dept = parts.length >= 4 ? parts[3] : "N/A";
//                invoiceNumberList.add(invoiceNo);
//                invoiceAmountList.add(amount);
//                invoiceDeptList.add(dept);
//            }
//        }
//    }
//
//    /** =========================== Setup Invoice RecyclerView =========================== */
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
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_dropdown_item_1line, dropdownList
//        );
//        spInvoiceItem.setAdapter(adapter);
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
//                try {
//                    double amt = Double.parseDouble(invoiceAmountList.get(index));
//                    DecimalFormat df = new DecimalFormat("#,###.00");
//                    invoiceAmount.setText(df.format(amt));
//                } catch (Exception e) {
//                    invoiceAmount.setText(invoiceAmountList.get(index));
//                }
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
//        for (String row : invoiceCategoryList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 1) {
//                onlyCategories.add(parts[0]);
//            }
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_dropdown_item_1line, onlyCategories
//        );
//        categoryDropdown.setAdapter(adapter);
//        categoryDropdown.setThreshold(1);
//        categoryDropdown.setOnClickListener(v -> categoryDropdown.showDropDown());
//        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
//            deductionAmount.requestFocus();
//        });
//    }
//
//    /** =========================== Setup Payment Type Dropdown =========================== */
//    private void setupPaymentTypeDropdown(ArrayList<String> qPType) {
//        if (qPType == null) return;
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_dropdown_item_1line, qPType
//        );
//        cpPaymentType.setAdapter(adapter);
//        cpPaymentType.setThreshold(1);
//        cpPaymentType.setOnClickListener(v -> cpPaymentType.showDropDown());
//        cpPaymentType.setOnItemClickListener((parent, view, position, id) -> {
//            String selectedType = cpPaymentType.getText().toString().trim();
//            if (selectedType.equalsIgnoreCase("CASH")) {
//                cpBankInitial.setEnabled(false);
//                layoutBankInitial.setEnabled(false);
//                cpBankName.setEnabled(false);
//                layoutBankName.setEnabled(false);
//                cpChkNumber.setEnabled(false);
//                layoutChkNumber.setEnabled(false);
//                cpBankInitial.setText("");
//                cpBankName.setText("");
//                cpChkNumber.setText("");
//            } else if (selectedType.equalsIgnoreCase("PDC")) {
//                cpBankInitial.setEnabled(true);
//                layoutBankInitial.setEnabled(true);
//                cpBankName.setEnabled(true);
//                layoutBankName.setEnabled(true);
//                cpChkNumber.setEnabled(true);
//                layoutChkNumber.setEnabled(true);
//                cpPaymentAmount.setEnabled(true);
//                cpBankInitial.requestFocus();
//            }
//        });
//    }
//
//    /** =========================== Setup Bank Initial Dropdown =========================== */
//    private void setupBankInitialDropdown(ArrayList<String> qBankList) {
//        if (qBankList == null) return;
//        ArrayList<String> onlyInitials = new ArrayList<>();
//        final ArrayList<String> bankNameList = new ArrayList<>();
//        for (String row : qBankList) {
//            String[] parts = row.split("\\|");
//            if (parts.length >= 2) {
//                onlyInitials.add(parts[0]);
//                bankNameList.add(parts[1]);
//            }
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_dropdown_item_1line, onlyInitials
//        );
//        cpBankInitial.setAdapter(adapter);
//        cpBankInitial.setThreshold(1);
//        cpBankInitial.setOnClickListener(v -> cpBankInitial.showDropDown());
//        cpBankInitial.setOnItemClickListener((parent, view, position, id) -> {
//            cpBankName.setText(bankNameList.get(position));
//            cpChkNumber.requestFocus();
//        });
//    }
//
//    /** =========================== Amount Formatter =========================== */
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
//    /** =========================== Add Invoice Item =========================== */
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
//        refreshInvoiceDropdown();
//        Toast.makeText(this, "Invoice Added!", Toast.LENGTH_SHORT).show();
//    }
//
//    /** =========================== Setup Add Deduction Button =========================== */
//    private void setupAddDeductionButton() {
//        btnAddDeduction.setOnClickListener(v -> {
//            String selectedCategory = categoryDropdown.getText().toString().trim();
//            String amount = deductionAmount.getText().toString().trim();
//
//            if (selectedCategory.isEmpty()) {
//                Toast.makeText(this, "Please select a category!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (amount.isEmpty()) {
//                Toast.makeText(this, "Please enter deduction amount!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            /** Add CategoryItem with checkbox false initially */
//            CategoryItem item = new CategoryItem(selectedCategory, amount);
//            item.setSelected(false);
//            deductionList.add(item);
//            deductionAdapter.notifyDataSetChanged();
//
//            /** Recalculate total deduction */
//            recalcTotalDeduction();
//
//            /** Clear inputs */
//            categoryDropdown.setText("");
//            deductionAmount.setText("");
//            categoryDropdown.requestFocus();
//            Toast.makeText(this, "Deduction added!", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(this, "Please enter payment type and amount!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        /** For CASH, bank details are ignored */
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
//        /** Recalculate total payment */
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
//        Toast.makeText(this, "Payment added!", Toast.LENGTH_SHORT).show();
//    }
//
//    /** =========================== Recalculate Total Invoice =========================== */
//    private void recalcTotal() {
//        double total = 0;
//        for (InvoiceItem item : invoiceItemList) {
//            try {
//                total += Double.parseDouble(item.getAmount().replace(",", ""));
//            } catch (Exception ignored) {}
//        }
//        totalInvoiceAmount.setText(new DecimalFormat("###,###,###.##").format(total));
//    }
//
//    /** =========================== Recalculate Total Deduction =========================== */
//    private void recalcTotalDeduction() {
//        double totalDeduction = 0;
//        for (CategoryItem item : deductionList) {
//            try {
//                double amt = Double.parseDouble(item.getAmount().replace(",", ""));
//                totalDeduction += amt;
//            } catch (Exception ignored) {}
//        }
//        totalDeductionAmount.setText(new DecimalFormat("###,###,###.##").format(totalDeduction));
//    }
//
//    /** =========================== Recalculate Total Payment =========================== */
//    private void recalcTotalPayment() {
//        double total = 0;
//        for (PaymentItem item : paymentItemList) {
//            try {
//                total += Double.parseDouble(item.getAmount().replace(",", ""));
//            } catch (Exception ignored) {}
//        }
//        totalPaymentAmount.setText(new DecimalFormat("#,###.00").format(total));
//    }
//}
