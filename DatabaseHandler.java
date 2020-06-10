package com.example.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contactsManager",
            TABLE_CONTACTS = "contacts",
            KEY_ID = "id",
            KEY_NAME = "name",
            KEY_PHONE = "phone",
            KEY_EMAIL = "email",
            KEY_ADDRESS = "address",
            KEY_IMAGEURI = "imageUri";


    public DatabaseHandler(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //methods when using inheritent
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_EMAIL + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_IMAGEURI + " TEXT)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion , int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        onCreate(db);

    }

    //allow database to create, delete, update
    public void createContact(Contacts contact){
        SQLiteDatabase db = getWritableDatabase();

        //creating instance
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE , contact.getNumber());
        values.put(KEY_EMAIL , contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());
        values.put(KEY_IMAGEURI, contact.getImageURI().toString());
        db.insert(TABLE_CONTACTS, null, values);
        db.close();

    }

    //read value
    public Contacts getContact(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] {KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS, KEY_IMAGEURI }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null , null, null, null);

        //check the cursor, if its null -> move to the first
        if(cursor != null)
            cursor.moveToFirst();
        Contacts contact = new Contacts(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
        db.close();
        cursor.close();
        return contact;

    }

    //get num of contact
    public int getContactsCount(){
        //Select * from the contact in  normal database
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS , null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    //delete a contact
    public void deleteContact(Contacts contact){
        //since delete we would use writable database
        SQLiteDatabase db = getWritableDatabase();
        //=? is basically asking where or what idex inside the databse should i actually
        db.delete(TABLE_CONTACTS, KEY_ID + "=?" , new String[] {String.valueOf(contact.getid())});

    }

    //contact update
    public int updateContact(Contacts contact){
        int status = 0;
        SQLiteDatabase db = getWritableDatabase();
        //creating instance
        ContentValues values;
        values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE , contact.getNumber());
        values.put(KEY_EMAIL , contact.getEmail());
        values.put(KEY_ADDRESS, contact.getAddress());
        values.put(KEY_IMAGEURI, contact.getImageURI().toString());

        status = db.update(TABLE_CONTACTS, values , KEY_ID + "=?", new String[] {String.valueOf(contact.getid())});

        return  status;
    }

    //get contact
    public List<Contacts> getAllContacts(){
        List<Contacts> contacts = new ArrayList<Contacts>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

        //goes through all rows of the table
        if (cursor.moveToFirst()){
            do {

                contacts.add(new Contacts(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5))));

            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }
}
