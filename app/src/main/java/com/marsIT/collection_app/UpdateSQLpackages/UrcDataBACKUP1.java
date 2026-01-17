//package com.marsIT.collection_app.UpdateSQLpackages;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.marsIT.collection_app.DatabaseHelper.DBHelper;
//import com.marsIT.collection_app.MainProgram.MainActivity;
//import com.marsIT.collection_app.R;
//import java.util.Objects;
//
//public class UrcData extends AppCompatActivity {
//    private DBHelper dbHelper;
//    private SQLiteDatabase db;
//    private String transinv;
//    private String DeptCode;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.data_urc);
//
//        /** Initialize database */
//        dbHelper = new DBHelper(this);
//        db = dbHelper.getWritableDatabase();
//
//        // Safe extraction of intent extras
//        if (getIntent().getExtras() != null) {
//            transinv = getIntent().getExtras().getString("transinv", "");
//            DeptCode = getIntent().getExtras().getString("DeptCode", "");
//        } else {
//            transinv = "";
//            DeptCode = "";
//        }
//
//        /** ===================== URC Installation ===================== */
//        SaveCustomer();
//        Toast.makeText(getApplicationContext(), "URC Customers Installed.", Toast.LENGTH_LONG).show();
//
//        InvoiceBalanceCategory();
//        Toast.makeText(getApplicationContext(), "URC Invoice Balance Category Installed.", Toast.LENGTH_LONG).show();
//
//        db.execSQL("UPDATE InstallValue SET Status = 'Y';");
//        Toast.makeText(getApplicationContext(), "URC Installation Complete.", Toast.LENGTH_LONG).show();
//
//        /** ===================== Mark installation as done ===================== */
//        getSharedPreferences("APP_SETUP", MODE_PRIVATE)
//                .edit()
//                .putBoolean("INSTALL_DONE", true)
//                .apply();
//
//        /** ===================== Redirect to MainActivity ===================== */
//        Intent intent = new Intent(UrcData.this, MainActivity.class);
//        intent.putExtra("SalesmanID", DeptCode); // pass actual SalesmanID if needed
//        startActivity(intent);
//
//        // Finish UrcData so it doesn’t repeat
//        finish();
//    }
//
//    /** URC customer list */
//    private void SaveCustomer() {
//        Cursor saveCustomerURC = db.rawQuery("SELECT * FROM CUSTOMERS", null);
//        saveCustomerURC.moveToFirst();
//        int scURC = saveCustomerURC.getCount();
//        if (scURC > 0) {
//            Log.d("URC Save customer list", "URC Customer list: " + scURC);
//        } else {
//            // Insert default customers
//            db.execSQL("Insert into CUSTOMERS values('CANCELLED','CANCELLED INVOICE','NOT SET','CASH on DUE','0','','2000-01-01','','','','2000-01-01','','N','J. Saavedra Street','','DAVAO CITY','0','TORIL','0','0','0','0','','1','0','0','0','0','0','0','0','0','','','','0','0','0','26');");
//            db.execSQL("Insert into CUSTOMERS values('CANCLD','CANCELED DOCUMENTS/TRANSACTION','NOT SET','','0','','2000-01-01','','','','2000-01-01','','','','','txtCity','0','TORIL','0','0','0','0','','0','0','0','0','0','0','0','0','0','','','','0','0','0','0');");
//            db.execSQL("Insert into CUSTOMERS values('CASH','CASH SALES','RETAILER','CASH on DUE','0','','2000-01-01','','','','2000-01-01','','','TORIL','Toril (Pob.)','DAVAO CITY','0','TORIL','0','0','0','0','','0','0','0','0','0','0','0','0','0','','','','0','0','0','25');");
//            db.execSQL("Insert into CUSTOMERS values('CCCCP0004','NORAISA MARKETING','GROCERIES','CASH on DUE','0','Ibrahim B. Usman ','2000-01-01','0','09534012433- M` Cor ','Ibrahim B. Usman','2000-01-01','0','0','Don Rufino  Alonzo St.','Poblacion Proper','COTABATO CITY','11','COTABATO CITY','0','0','0','0','','1','1','1','0','0','0','0','0','0','7.225968','124.244565','M','150000','0','0','25');");
//            db.execSQL("Insert into CUSTOMERS values('CCCCP0030','ACH CORPORATION','KEY SUPERMARKET','PDC','0','Manchan Ang','1963-07-07','1','09063843019- M` Alma ','Manchan Ang','1963-07-07','421-88992427','09332645870-Purchaser ','Makakua St.,','Poblacion V','COTABATO CITY','11','COTABATO CITY','0','0','0','0','','1','1','1','0','0','0','0','0','0','7.224652358658799','124.24776889523945','M','2200900','0','0','25');");
//            db.execSQL("Insert into CUSTOMERS values('CCCCPA0001','JAIC GROCERY','GROCERIES','CASH on DUE','0','Mimi; Diana','2000-01-01','','','','2000-01-01','','','','','COTABATO CITY','11','COTABATO CITY','0','0','0','0','','1','0','0','0','0','0','0','0','0','','','','1724813','0','0','25');");
//
//            SaveCustomerURCextended();
//        }
//        saveCustomerURC.close();
//    }
//
//    /** URC customer list extended */
//    private void SaveCustomerURCextended() {
//        db.execSQL("Insert into CUSTOMERS values('CNCMDC4719','MONTOYA','SARI-SARI STORE','','0','Angel Montoya','2018-11-19','none','none','Angel Montoya','2018-11-17','none','','HIGHWAY','Barangay Poblacion 8','MIDSAYAP','4','MIDSAYAP','0','0','0','0','','0','0','0','0','0','0','0','0','0','7.187649','124.537233','M','15000','0','0','0');");
//        db.execSQL("Insert into CUSTOMERS values('CNCMDC4720','MARICEL ( MIDSAYAP )','SARI-SARI STORE','CASH on DUE','0','Maricel Tahir','2018-11-24','n/a','n/a','Maricel Tahir','2018-11-24','n/a','','.','Barangay Poblacion 1','MIDSAYAP','4','MIDSAYAP','0','0','0','0','','0','0','0','0','0','0','0','0','0','7.2063064','124.5427382','M','1000','0','0','0');");
//        db.execSQL("Insert into CUSTOMERS values('CNCMDC4726','JS STORE (MIDSAYAP)','SARI-SARI STORE','','0','Josephine Sibya','2018-11-26','none','none','Josephine Sibya','2018-11-26','none','','HIGHWAY','Barangay Poblacion 8','MIDSAYAP','4','MIDSAYAP','0','0','0','0','','0','0','0','0','0','0','0','0','0','7.188531','124.536541','M','14700','0','0','1');");
//        db.execSQL("Insert into CUSTOMERS values('CNCMDC4728','NASSER','SARI-SARI STORE','CASH on DUE','0','Nasser Solaiman','2018-12-03','none','none','Nasser Solaiman','2018-12-03','none','','HIGHWAY','Barangay Poblacion 8','MIDSAYAP','4','MIDSAYAP','0','0','0','0','','0','0','0','0','0','0','0','0','0','','','','15000','0','0','0');");
//        db.execSQL("Insert into CUSTOMERS values('CNCMDC4731','ANATOLIA POOL','SARI-SARI STORE','CASH on DUE','0','Gerry Gornez','2018-12-19','none','none','Gerry Gornez','2018-12-19','none','','National Highway','Barangay Poblacion 8','MIDSAYAP','4','MIDSAYAP','0','0','0','0','','0','0','0','0','0','0','0','0','0','7.218652','124.564421','M','14700','0','0','1');");
//        db.execSQL("Insert into CUSTOMERS values('CNCMDC4733','CARIAGA 2','SARI-SARI STORE','','0','Francing Cariaga','2018-12-19','none','none','Francing Cariaga','2018-12-19','none','','HIGHWAY','Arizona','MIDSAYAP','4','MIDSAYAP','0','0','0','0','','0','0','0','0','0','0','0','0','0','7.22955717155511','124.61705250729878','M','14700','0','0','1');");
//        db.execSQL("Insert into CUSTOMERS values('CNCMDC4736','SEVEN SEVEN CONV.','SARI-SARI STORE','','0','Imee Rayray','2018-12-19','none','none','Imee Rayray','2018-12-19','none','','HIGHWAY','Barangay Poblacion 8','MIDSAYAP','4','MIDSAYAP','0','0','0','0','','0','0','0','0','0','0','0','0','0','7.202055','124.539981','M','13230','0','0','6');");
//    }
//
//    /** List of Invoice Balance Category */
//    private void InvoiceBalanceCategory() {
//        Cursor invBalCatURC = db.rawQuery("SELECT * FROM SALESHEADER", null);
//        invBalCatURC.moveToFirst();
//        int cntURC = invBalCatURC.getCount();
//        if (cntURC > 0) {
//            Log.d("URC Save InvBalCat list", "URC InvBalCat list: " + cntURC);
//        } else {
//            db.execSQL("Insert into SALESHEADER values('MCRV2011135012','ACVMR076','2108.5','LUBRICANTS');");
//            db.execSQL("Insert into SALESHEADER values('MCRV2011139515','ADCML039','2195.84','LUBRICANTS');");
//            db.execSQL("Insert into SALESHEADER values('MCRV2011143575','ADCTR196','4194.93','LUBRICANTS');");
//            db.execSQL("Insert into SALESHEADER values('ACRV2011140438','ADCCP135','921807.43','AGRICHEM');");
//            db.execSQL("Insert into SALESHEADER values('MCRV2011147634','ADOLP115','6010.91','LUBRICANTS');");
//
//            InvoiceBalanceCategoryExtend1();
//        }
//        invBalCatURC.close();
//    }
//
//    /** List of Invoice Balance Category extended */
//    private void InvoiceBalanceCategoryExtend1() {
//        db.execSQL("Insert into SALESHEADER values('ACRV2025630631','CDCTR0001','88267.61','AGRICHEM');");
//        db.execSQL("Insert into SALESHEADER values('BVCCR2025001962','CDCTR0001','28353.11','CENTURY');");
//        db.execSQL("Insert into SALESHEADER values('APL20253198461','CDCTR0001','51504','LUBRICANTS');");
//        db.execSQL("Insert into SALESHEADER values('VCCR2025611339','CDCTR0001','4865.48','URC');");
//        db.execSQL("Insert into SALESHEADER values('VCCR2025611341','CDCTR0001','2983.7','URC');");
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.kebabmenu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
//}
