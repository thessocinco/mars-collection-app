//package com.marsIT.collection_app.DatabaseHelper;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class DBHelper extends SQLiteOpenHelper {
//
//    private static final String DB_NAME = "collection_db";
//    private static final int DB_VERSION = 1;
//
//    public DBHelper(Context context) {
//        super(context, DB_NAME, null, DB_VERSION);
//    }
//
//    /** ===================== Create required tables ===================== */
//    @Override
//    public void onCreate(SQLiteDatabase db) {
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
//            /** LIST OF BARANGAYS */
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
//                    "Category VARCHAR, CatID VARCHAR);");
//
//            /** TEMP SALES HEADER TABLE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS TempSALESHEADER(" +
//                    "Refid VARCHAR, CustomerID VARCHAR, Balance NUMERIC, Department VARCHAR);");
//
//            /** =========================== SAVED COLLECTIONS =========================== */
//            /** SAVED COLLECTION INVOICE */
//            db.execSQL("CREATE TABLE IF NOT EXISTS SavedCollectionInvoice(" +
//                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "PRnumber TEXT," +
//                    "CustomerID TEXT," +
//                    "InvoiceNo TEXT," +
//                    "Amount REAL," +
//                    "Department TEXT);");
//
//            /** SAVED COLLECTION DEDUCTION */
//            db.execSQL("CREATE TABLE IF NOT EXISTS SavedCollectionDeduction(" +
//                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "PRnumber TEXT," +
//                    "CustomerID TEXT," +
//                    "Category TEXT," +
//                    "Amount REAL);");
//
//            /** SAVED COLLECTION PAYMENT */
//            db.execSQL("CREATE TABLE IF NOT EXISTS SavedCollectionPayment(" +
//                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "PRnumber TEXT," +
//                    "CustomerID TEXT," +
//                    "PaymentType TEXT," +
//                    "BankInitial TEXT," +
//                    "CheckNumber TEXT," +
//                    "Amount REAL);");
//
//            /** ===================== OTHER TABLES ===================== */
//            db.execSQL("CREATE TABLE IF NOT EXISTS customerUnlock(CustomerID VARCHAR);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS receiverNumber(NUM VARCHAR);");
//            db.execSQL("CREATE TABLE IF NOT EXISTS department(Department VARCHAR);");
//
//            Log.d("DBHelper", "All tables created successfully.");
//
//        } catch (Exception e) {
//            Log.e("DBHelper", "Error creating tables: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        //TODO: This method is called when the database version is incremented.
//        // It drops all existing tables to reset the database schema
//        // and then calls onCreate() to recreate them from scratch.
//        db.execSQL("DROP TABLE IF EXISTS CUSTOMERS");
//        db.execSQL("DROP TABLE IF EXISTS Barangay");
//        db.execSQL("DROP TABLE IF EXISTS ListOfBanks");
//        db.execSQL("DROP TABLE IF EXISTS PaymentType");
//        db.execSQL("DROP TABLE IF EXISTS CollectionCategory");
//        db.execSQL("DROP TABLE IF EXISTS CollectionDetails");
//        db.execSQL("DROP TABLE IF EXISTS CollectionHeader");
//        db.execSQL("DROP TABLE IF EXISTS CollectionInvoiceDetails");
//        db.execSQL("DROP TABLE IF EXISTS LastSyntaxDateTime");
//        db.execSQL("DROP TABLE IF EXISTS SALESHEADER");
//        db.execSQL("DROP TABLE IF EXISTS InvoiceCategory");
//        db.execSQL("DROP TABLE IF EXISTS TempSALESHEADER");
//        db.execSQL("DROP TABLE IF EXISTS SavedCollectionInvoice");
//        db.execSQL("DROP TABLE IF EXISTS SavedCollectionDeduction");
//        db.execSQL("DROP TABLE IF EXISTS SavedCollectionPayment");
//        db.execSQL("DROP TABLE IF EXISTS customerUnlock");
//        db.execSQL("DROP TABLE IF EXISTS receiverNumber");
//        db.execSQL("DROP TABLE IF EXISTS department");
//        //
//        onCreate(db);
//    }
//}
