package com.marsIT.collection_app.CollectionData;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.marsIT.collection_app.CategoryAdapter.nCategoryItem;
import com.marsIT.collection_app.CategoryAdapter.nDeductionAdapter;
import com.marsIT.collection_app.InvoiceAdapter.nInvoiceAdapter;
import com.marsIT.collection_app.InvoiceAdapter.nInvoiceItem;
import com.marsIT.collection_app.MainProgram.MainActivity;
import com.marsIT.collection_app.PaymentAdapter.nPaymentAdapter;
import com.marsIT.collection_app.PaymentAdapter.nPaymentItem;
import com.marsIT.collection_app.R;
import com.marsIT.collection_app.ToolBar.BaseToolbar;

public class ViewCollection extends BaseToolbar {

    /** Customer info */
    private String vcInstalledSalesman, vcInstalledDepartment, vcInstalledBranchName, vcSelectedCustomerID, vcSelectedCustomerName, vcGenSyntaxDateTime;

    /** UI references */
    private TextView vcGetSalesMan, vcGetDepartment, vcGetCustomerID, vcGetCustomerName, vcGetSyntaxDateTime;
    private AutoCompleteTextView vcSelectPRnumber; // Updated for dropdown PR selection

    private Button btnResendCollection;

    /** RecyclerViews */
    private RecyclerView rvInvoice, rvDeduction, rvPayment;

    /** Adapters */
    private nInvoiceAdapter invoiceAdapter;
    private nDeductionAdapter deductionAdapter;
    private nPaymentAdapter paymentAdapter;

    /** Data lists */
    private ArrayList<nInvoiceItem> invoiceList = new ArrayList<>();
    private ArrayList<nCategoryItem> deductionList = new ArrayList<>();
    private ArrayList<nPaymentItem> paymentList = new ArrayList<>();

    /** Total TextViews */
    private TextView vcTotalInvoiceAmount, vcTotalDeductionAmount, vcTotalPaymentAmount;

    /** Database */
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_view_transaction);

        try {
            /** Setup toolbar */
            setupToolbar("COLLECTION DETAILS");

            /** Initialize database */
            initializeDatabase();

            /** Initialize views */
            initializeViews();

            /** Retrieve customer info from intent */
            vcInstalledSalesman = getIntent().getStringExtra("installedSalesmanName");
            vcInstalledDepartment = getIntent().getStringExtra("installedDepartment");
            vcInstalledBranchName = getIntent().getStringExtra("installedBranchName");
            vcSelectedCustomerID = getIntent().getStringExtra("selectedCustomerID");
            vcSelectedCustomerName = getIntent().getStringExtra("selectedCustomerName");
            vcGenSyntaxDateTime = getIntent().getStringExtra("syntaxDateTime");

            /** Format and display date */
            try {
                SimpleDateFormat sdfInput = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.getDefault());
                Date date = sdfInput.parse(vcGenSyntaxDateTime);

                SimpleDateFormat sdfOutput = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
                vcGetSyntaxDateTime.setText(sdfOutput.format(date));

            } catch (Exception e) {
                vcGetSyntaxDateTime.setText(vcGenSyntaxDateTime);
            }

            /** Set customer info */
            vcGetSalesMan.setText(vcInstalledSalesman);
            convertDepartment();
            vcGetCustomerID.setText(vcSelectedCustomerID != null ? vcSelectedCustomerID : "N/A");
            vcGetCustomerName.setText(vcSelectedCustomerName != null ? vcSelectedCustomerName : "N/A");

            /** Initialize RecyclerViews and adapters */
            initializeRecyclerViews();

            /** Load PR numbers for this customer */
            loadPrNumbersForCustomer();

            btnResendCollection.setOnClickListener(v -> {

                String prNumber = vcSelectPRnumber.getText().toString().trim();

                if (prNumber.isEmpty()) {
                    Toast.makeText(this, "Please select PR number first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ensure receiver number exists (safe: exits if already exists)
                receiverNumber();

                try {
                    // Resend saved collection SMS
                    resendSavedCollectionSMS(prNumber);
                } catch (Exception e) {
                    Log.e("sendCollectionSMS", "SMS send failed", e);
                }

                ViewCollection.this.finish(); //TODO: Finish after the transaction

                Toast.makeText(this, "Resending collection SMS...", Toast.LENGTH_SHORT).show();
            });


        } catch(Exception e){
            Log.e("ViewCollection", "Error: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goBackToMain() {
        Intent intent = new Intent(ViewCollection.this, MainActivity.class);

        // Clear back stack so user can't return to CollectionPayment
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish(); // close CollectionPayment
    }

    @Override
    protected void onDestroy() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        super.onDestroy();
    }

    /** =========================== Initialize Views =========================== */
    private void initializeViews() {
        vcGetSalesMan = findViewById(R.id.vcSalesmanName);
        vcGetDepartment = findViewById(R.id.vcDepartment);
        vcGetCustomerID = findViewById(R.id.vcCustomerID);
        vcGetCustomerName = findViewById(R.id.vcCustomerName);
        vcGetSyntaxDateTime = findViewById(R.id.vcSyntaxDateTime);
        vcSelectPRnumber = findViewById(R.id.vcSelectPRnumber); // Updated AutoCompleteTextView

        /** =========================== Allow only numbers =========================== */
        vcSelectPRnumber.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        vcSelectPRnumber.setKeyListener(DigitsKeyListener.getInstance("0123456789.")); // with decimals
        vcSelectPRnumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)}); // optional max length

        /** =========================== Total TextViews =========================== */
        vcTotalInvoiceAmount = findViewById(R.id.vcTotalInvoiceAmount);
        vcTotalDeductionAmount = findViewById(R.id.vcTotalDeductionAmount);
        vcTotalPaymentAmount = findViewById(R.id.vcTotalPaymentAmount);

        btnResendCollection = findViewById(R.id.btnResendCollection);

    }

    /** =========================== Initialize RecyclerViews and Adapters =========================== */
    private void initializeRecyclerViews() {
        rvInvoice = findViewById(R.id.vcListOfInvoiceRecycler);
        rvDeduction = findViewById(R.id.vcListOfDeductionRecycler);
        rvPayment = findViewById(R.id.vcListOfCollectionsRecycler);

        /** =========================== Invoice Adapter =========================== */
        invoiceAdapter = new nInvoiceAdapter(invoiceList, item -> {
            // Optional: handle invoice row click if needed
        });
        rvInvoice.setLayoutManager(new LinearLayoutManager(this));
        rvInvoice.setAdapter(invoiceAdapter);

        /** =========================== Deduction Adapter =========================== */
        deductionAdapter = new nDeductionAdapter(deductionList);
        rvDeduction.setLayoutManager(new LinearLayoutManager(this));
        rvDeduction.setAdapter(deductionAdapter);

        /** =========================== Payment Adapter =========================== */
        paymentAdapter = new nPaymentAdapter(paymentList);
        rvPayment.setLayoutManager(new LinearLayoutManager(this));
        rvPayment.setAdapter(paymentAdapter);
    }

    /** =========================== Initialize Database =========================== */
    private void initializeDatabase() {
        if (db == null || !db.isOpen()) {
            db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
        }
    }

    /** =========================== Convert Department =========================== */
    private void convertDepartment() {
        switch (vcInstalledDepartment) {
            case "A": vcGetDepartment.setText("AUTO-SUPPLY"); break;
            case "L": vcGetDepartment.setText("LUBRICANTS"); break;
            case "CC": vcGetDepartment.setText("CENTURY"); break;
            case "PL": vcGetDepartment.setText("PEERLESS"); break;
            case "MI": vcGetDepartment.setText("MONTOSCO"); break;
            case "LY": vcGetDepartment.setText("LAMOIYAN"); break;
            case "AG": vcGetDepartment.setText("AGRICHEM"); break;
            case "CL": vcGetDepartment.setText("COLUMBIA"); break;
            case "SO": vcGetDepartment.setText("SOLANE"); break;
            case "ZS": vcGetDepartment.setText("ZESTAR"); break;
            case "BL": vcGetDepartment.setText("BELO"); break;
            case "GT": vcGetDepartment.setText("GTAM"); break;
            case "KL": vcGetDepartment.setText("KOHL"); break;
            case "UC": vcGetDepartment.setText("URC"); break;
            case "RM": vcGetDepartment.setText("RAM"); break;
            case "EQ": vcGetDepartment.setText("EQ"); break;
            default: vcGetDepartment.setText(vcInstalledDepartment);
        }
    }

    /** =========================== Load PR Numbers for This Customer =========================== */
    private void loadPrNumbersForCustomer() {
        if (vcSelectedCustomerID == null || vcSelectedCustomerID.isEmpty()) {
            Toast.makeText(this, "No Customer ID found.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> prList = new ArrayList<>();

        try {
            Cursor savedInvoice = db.rawQuery("SELECT DISTINCT PRnumber FROM SavedCollectionInvoice WHERE CustomerID = ?",
                    new String[]{vcSelectedCustomerID});
            while (savedInvoice.moveToNext()) prList.add(savedInvoice.getString(0));
            savedInvoice.close();

            Cursor savedDeduction = db.rawQuery("SELECT DISTINCT PRnumber FROM SavedCollectionDeduction WHERE CustomerID = ?",
                    new String[]{vcSelectedCustomerID});
            while (savedDeduction.moveToNext()) prList.add(savedDeduction.getString(0));
            savedDeduction.close();

            Cursor savedPayment = db.rawQuery("SELECT DISTINCT PRnumber FROM SavedCollectionPayment WHERE CustomerID = ?",
                    new String[]{vcSelectedCustomerID});
            while (savedPayment.moveToNext()) prList.add(savedPayment.getString(0));
            savedPayment.close();
        } catch (Exception e) {
            Toast.makeText(this, "DB Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<String> uniquePR = new ArrayList<>(new java.util.HashSet<>(prList));
        if (uniquePR.isEmpty()) {
            Toast.makeText(this, "No PR numbers found for this customer.", Toast.LENGTH_SHORT).show();
            return;
        }

        /** =========================== Show PR Numbers as suggestions in AutoCompleteTextView =========================== */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, uniquePR);
        vcSelectPRnumber.setAdapter(adapter);
        vcSelectPRnumber.setKeyListener(vcSelectPRnumber.getKeyListener());
        vcSelectPRnumber.setThreshold(1);

        /** =========================== Load selected PR details =========================== */
        vcSelectPRnumber.setOnItemClickListener((parent, view, position, id) -> {
            loadPRDetails(vcSelectPRnumber.getText().toString());
        });
    }

    /** =========================== Load PR Details =========================== */
    private void loadPRDetails(String prNumber) {
        invoiceList.clear();
        deductionList.clear();
        paymentList.clear();

        /** Load Invoice */
        try (Cursor loadInvoice = db.rawQuery("SELECT InvoiceNo, Amount, Department FROM SavedCollectionInvoice WHERE PRnumber = ?",
                new String[]{prNumber})) {
            while (loadInvoice.moveToNext()) {
                invoiceList.add(new nInvoiceItem(loadInvoice.getString(0), loadInvoice.getString(1), loadInvoice.getString(2)));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invoice DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        /** Load Deduction */
        try (Cursor loadDeduction = db.rawQuery("SELECT Category, Amount FROM SavedCollectionDeduction WHERE PRnumber = ?",
                new String[]{prNumber})) {
            while (loadDeduction.moveToNext()) {
                deductionList.add(new nCategoryItem(loadDeduction.getString(0), loadDeduction.getString(1)));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Deduction DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        /** Load Payment */
        try (Cursor loadPayment = db.rawQuery("SELECT PaymentType, BankInitial, CheckNumber, Amount FROM SavedCollectionPayment WHERE PRnumber = ?",
                new String[]{prNumber})) {
            while (loadPayment.moveToNext()) {
                paymentList.add(new nPaymentItem(loadPayment.getString(0), loadPayment.getString(1), loadPayment.getString(2), loadPayment.getString(3)));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Payment DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        /** =========================== Notify adapters =========================== */
        invoiceAdapter.notifyDataSetChanged();
        deductionAdapter.notifyDataSetChanged();
        paymentAdapter.notifyDataSetChanged();

        /** =========================== Recalculate totals =========================== */
        calculateTotals();
    }

    /** =========================== Calculate Totals =========================== */
    private void calculateTotals() {
        double totalInvoice = 0, totalDeduction = 0, totalPayment = 0;
        DecimalFormat df = new DecimalFormat("#,###.00");

        for (nInvoiceItem item : invoiceList) {
            try { totalInvoice += Double.parseDouble(item.getAmount()); } catch(Exception ignored) {}
        }
        for (nCategoryItem item : deductionList) {
            try { totalDeduction += Double.parseDouble(item.getAmount()); } catch(Exception ignored) {}
        }
        for (nPaymentItem item : paymentList) {
            try { totalPayment += Double.parseDouble(item.getAmount().replace(",", "")); } catch(Exception ignored) {}
        }

        vcTotalInvoiceAmount.setText(df.format(totalInvoice));
        vcTotalDeductionAmount.setText(df.format(totalDeduction));
        vcTotalPaymentAmount.setText(df.format(totalPayment));
    }

    /** =========================== COL Prefix Resolver =========================== */
    private String getColPrefix(char type) {
        if ("MARS2".equalsIgnoreCase(vcInstalledBranchName)) {
            return "COL2" + type;
        } else {
            return "COL" + type;
        }
    }

    /** =========================== Build SMS Messages =========================== */

    /** Build COLA SMS for invoices */
    // Example: COLA/00001/ADCCP1035/11-12-2025 13:38:00/BCCR2025:2000!VCCR2025:2000!
    private String buildSavedInvoiceSms(String prNumber) {
        StringBuilder sb = new StringBuilder();

        sb.append(getColPrefix('A')).append("/")
                .append(prNumber).append("/")
                .append(vcSelectedCustomerID != null ? vcSelectedCustomerID : "").append("/")
                .append(vcGenSyntaxDateTime != null ? vcGenSyntaxDateTime : "").append("/");

        try (Cursor invoiceSMS = db.rawQuery(
                "SELECT InvoiceNo, Amount FROM SavedCollectionInvoice WHERE PRnumber = ?",
                new String[]{prNumber})) {

            while (invoiceSMS.moveToNext()) {
                sb.append(invoiceSMS.getString(0))
                        .append(":")
                        .append(formatAmountNoComma(invoiceSMS.getString(1)))
                        .append("!");
            }
        }
        return sb.toString();
    }

    /** Build COLC SMS for categories/deductions */
    // Example: COLC/00001/ADCCP1035/1:2000!2:2000!
    private String buildSavedCategorySms(String prNumber) {
        StringBuilder sb = new StringBuilder();

        sb.append(getColPrefix('C')).append("/")
                .append(prNumber).append("/")
                .append(vcSelectedCustomerID != null ? vcSelectedCustomerID : "").append("/");
//                .append(vcGenSyntaxDateTime != null ? vcGenSyntaxDateTime : "").append("/");

        int index = 1;
        try (Cursor categorySMS = db.rawQuery(
                "SELECT Amount FROM SavedCollectionDeduction WHERE PRnumber = ?",
                new String[]{prNumber})) {

            while (categorySMS.moveToNext()) {
                sb.append(index++)
                        .append(":")
                        .append(formatAmountNoComma(categorySMS.getString(0)))
                        .append("!");
            }
        }
        return sb.toString();
    }

    /** Build COLD SMS for payments */
    // Example: COLD/00001/ADCCP1035/CASH:0:0:2000!PDC:BDO:12345678:2000!
    private String buildSavedPaymentSms(String prNumber) {
        StringBuilder sb = new StringBuilder();

        sb.append(getColPrefix('D')).append("/")
                .append(prNumber).append("/")
                .append(vcSelectedCustomerID != null ? vcSelectedCustomerID : "").append("/");
//                .append(vcGenSyntaxDateTime != null ? vcGenSyntaxDateTime : "").append("/");

        try (Cursor paymentSMS = db.rawQuery(
                "SELECT PaymentType, BankInitial, CheckNumber, Amount FROM SavedCollectionPayment WHERE PRnumber = ?",
                new String[]{prNumber})) {

            while (paymentSMS.moveToNext()) {
                String type = paymentSMS.getString(0);

                if ("CASH".equalsIgnoreCase(type)) {
                    sb.append("CASH:0:0:")
                            .append(formatAmountNoComma(paymentSMS.getString(3)))
                            .append("!");
                } else if ("PDC".equalsIgnoreCase(type)) {
                    sb.append("PDC:")
                            .append(paymentSMS.getString(1) != null ? paymentSMS.getString(1) : "0").append(":")
                            .append(paymentSMS.getString(2) != null ? paymentSMS.getString(2) : "0").append(":")
                            .append(formatAmountNoComma(paymentSMS.getString(3)))
                            .append("!");
                }
            }
        }
        return sb.toString();
    }

    /** =========================== Helper Methods =========================== */
    /** Format amount without commas for SMS */
    private String formatAmountNoComma(String raw) {
        if (raw == null || raw.isEmpty()) return "0.00";
        try {
            double value = Double.parseDouble(raw.replace(",", ""));
            return String.format(Locale.US, "%.2f", value);
        } catch (Exception e) {
            return "0.00";
        }
    }

    private void receiverNumber() {
        Cursor receiverNum = db.rawQuery("SELECT * FROM receivernumber", null);
        try {
            if (receiverNum.moveToFirst()) {
                Log.d("receiverNumber", "Record exists: " + receiverNum.getCount());
                return; // already exists, no need to insert
            }

            // Map departments to receiver numbers
            Map<String, String> deptNumberMap = new HashMap<>();
            String defaultNumber = "+639177105901";

            // Departments sharing same number
            String[] num1Depts = {"UC", "RM", "CL", "MI"};
            String[] num2Depts = {"LY", "CC", "PL", "SO", "EQ"};

            for (String d : num1Depts) deptNumberMap.put(d, "+639177034043");
            for (String d : num2Depts) deptNumberMap.put(d, "+639177105906");

            // Determine number for current department
            String number = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                number = deptNumberMap.getOrDefault(vcInstalledDepartment, defaultNumber);
            }

            // Insert number
            db.execSQL("INSERT INTO receivernumber VALUES('" + number + "');");
            Log.d("receiverNumber", "Inserted receiver number: " + number);

        } finally {
            receiverNum.close();
        }
    }

    /** =========================== Resend Saved Collection SMS =========================== */
    private void resendSavedCollectionSMS(String prNumber) {
        if (prNumber == null || prNumber.isEmpty()) return;

        // Build messages separately
        String invoiceMsg = buildSavedInvoiceSms(prNumber); // COLA
        String categoryMsg = buildSavedCategorySms(prNumber); // COLC
        String paymentMsg = buildSavedPaymentSms(prNumber); // COLD

        // Query receiver numbers
        Cursor resendSMS = db.rawQuery("SELECT * FROM receivernumber", null);
        try {
            if (resendSMS != null && resendSMS.moveToFirst()) {
                do {
                    String receiver = resendSMS.getString(0);

                    // =========================== Send COLA (Invoices) ===========================
                    if (!invoiceMsg.isEmpty()) {
                        Log.d("SMS_LOG", "Resending COLA to: " + receiver);
                        sendSmsMultipart(receiver, invoiceMsg);
                    }

                    // =========================== Send COLC (Deductions) ===========================
                    if (!categoryMsg.isEmpty()) {
                        Log.d("SMS_LOG", "Resending COLC to: " + receiver);
                        sendSmsMultipart(receiver, categoryMsg);
                    }

                    // =========================== Send COLD (Payments) ===========================
                    if (!paymentMsg.isEmpty()) {
                        Log.d("SMS_LOG", "Resending COLD to: " + receiver);
                        sendSmsMultipart(receiver, paymentMsg);
                    }

                } while (resendSMS.moveToNext());
            }
        } finally {
            if (resendSMS != null) resendSMS.close();
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
