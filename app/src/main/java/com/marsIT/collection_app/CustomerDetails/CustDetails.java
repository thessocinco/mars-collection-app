package com.marsIT.collection_app.CustomerDetails;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.marsIT.collection_app.R;
import com.marsIT.collection_app.ToolBar.BaseToolbar;

public class CustDetails extends BaseToolbar {

    /** Customer info */
    private String cdSelectedCustomerID, cdSelectedCustomerName;

    /** UI references */
    private TextView cdCustomerID, cdCustomerName, cdCustType, cdSubPocket, cdStreet, cdBarangay, cdCityTown,
    cdProvince, cdLatitude, cdLongitude, cdContactPerson, cdOwnerName,
    cdCellNum, cdBirthday;

    /** Database */
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_details);

        try {
            /** Setup toolbar */
            setupToolbar("CUSTOMER DETAILS");

            /** Initialize database */
            initializeDatabase();

            /** Initialize views */
            initializeViews();

            /** Retrieve customer info from intent */
            cdSelectedCustomerID = getIntent().getStringExtra("selectedCustomerID");
            cdSelectedCustomerName = getIntent().getStringExtra("selectedCustomerName");

            /** Set customer info */
            cdCustomerID.setText(cdSelectedCustomerID != null ? cdSelectedCustomerID : "N/A");
            cdCustomerName.setText(cdSelectedCustomerName != null ? cdSelectedCustomerName : "N/A");

            /** Load full customer details */
            loadCustomerDetails(cdSelectedCustomerID);

        } catch(Exception e){
            Log.e("ViewCollection", "Error: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeDatabase() {
        db = openOrCreateDatabase("collection_db", MODE_PRIVATE, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }

    /** =========================== Initialize Views =========================== */
    private void initializeViews() {
        /** Customer Info */
        cdCustomerID = findViewById(R.id.custID);
        cdCustomerName = findViewById(R.id.custName);

        cdCustType = findViewById(R.id.custType);
        cdSubPocket = findViewById(R.id.custSubPocket);
        cdStreet = findViewById(R.id.custStreet);
        cdBarangay = findViewById(R.id.custBarangay);
        cdCityTown = findViewById(R.id.custCityTown);
        cdProvince = findViewById(R.id.custProvince);
        cdLatitude = findViewById(R.id.custLatitude);
        cdLongitude = findViewById(R.id.custLongitude);
        cdContactPerson = findViewById(R.id.custContactPerson);
        cdOwnerName = findViewById(R.id.custOwnerName);
        cdCellNum = findViewById(R.id.custCellNum);
        cdBirthday = findViewById(R.id.custBirthday);
    }

    private void loadCustomerDetails(String customerID) {

        if (customerID == null || customerID.isEmpty()) return;

        String query = "SELECT * FROM CUSTOMERS WHERE CID = ?";
        try (android.database.Cursor cursor = db.rawQuery(query, new String[]{customerID})) {

            if (cursor.moveToFirst()) {

                cdCustType.setText(cursor.getString(cursor.getColumnIndexOrThrow("CTYPE")));
                cdSubPocket.setText(cursor.getString(cursor.getColumnIndexOrThrow("SUBPOCKET")));
                cdStreet.setText(cursor.getString(cursor.getColumnIndexOrThrow("STREET")));
                cdBarangay.setText(cursor.getString(cursor.getColumnIndexOrThrow("BARANGAY")));
                cdCityTown.setText(cursor.getString(cursor.getColumnIndexOrThrow("MUNICIPALITY")));
                cdProvince.setText(cursor.getString(cursor.getColumnIndexOrThrow("PROVINCE")));
                cdLatitude.setText(cursor.getString(cursor.getColumnIndexOrThrow("Latitude")));
                cdLongitude.setText(cursor.getString(cursor.getColumnIndexOrThrow("Longitude")));
                cdContactPerson.setText(cursor.getString(cursor.getColumnIndexOrThrow("CPERSON")));
                cdOwnerName.setText(cursor.getString(cursor.getColumnIndexOrThrow("OWNER")));
                cdCellNum.setText(cursor.getString(cursor.getColumnIndexOrThrow("CCELLNUM")));

                /** Birthday */
                String bday = cursor.getString(cursor.getColumnIndexOrThrow("CBDAY"));
                cdBirthday.setText(bday != null ? bday : "N/A");

            } else {
                Toast.makeText(this, "Customer not found.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("CustDetails", "Load error: " + e.getMessage());
            Toast.makeText(this, "Error loading customer data.", Toast.LENGTH_LONG).show();
        }
    }
}
