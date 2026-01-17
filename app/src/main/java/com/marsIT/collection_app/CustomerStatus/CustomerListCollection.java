package com.marsIT.collection_app.CustomerStatus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marsIT.collection_app.R;
import com.marsIT.collection_app.ToolBar.BaseToolbar;

import java.util.ArrayList;
import java.util.List;

public class CustomerListCollection extends BaseToolbar {

    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    private CustStatusAdapter adapter;
    private List<CustStatus> customers;
    private EditText searchCustStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_list_collection);

        setupToolbar("CUSTOMER STATUS");

        // Initialize database
        db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.custStatusRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize search field
        searchCustStatus = findViewById(R.id.searchCustStatus);

        // Load customers
        customers = loadCustomerStatus();
        adapter = new CustStatusAdapter(this, customers);
        recyclerView.setAdapter(adapter);

        // Add search functionality
        searchCustStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void filterCustomers(String query) {
        query = query.toLowerCase();
        List<CustStatus> filteredList = new ArrayList<>();

        for (CustStatus customer : customers) {
            if (customer.getCustomerName().toLowerCase().contains(query) ||
                    customer.getCustomerID().toLowerCase().contains(query)) {
                filteredList.add(customer);
            }
        }

        adapter.updateList(filteredList);
    }

    private List<CustStatus> loadCustomerStatus() {
        List<CustStatus> list = new ArrayList<>();

        String query = "SELECT C.CID, C.CNAME, " +
                "CASE WHEN H.CustomerID IS NOT NULL THEN 1 ELSE 0 END AS collected " +
                "FROM CUSTOMERS C " +
                "LEFT JOIN CollectionHeader H ON C.CID = H.CustomerID";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String cid = cursor.getString(cursor.getColumnIndexOrThrow("CID"));
            String cname = cursor.getString(cursor.getColumnIndexOrThrow("CNAME"));
            boolean collected = cursor.getInt(cursor.getColumnIndexOrThrow("collected")) == 1;

            list.add(new CustStatus(cid, cname, collected));
        }

        cursor.close();
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}
