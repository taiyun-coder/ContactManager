package com.example.contactmanager;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int EDIT=1 ,DELETE = 0;

    private static int contactId;
    private boolean isEditMode;

    EditText inputedName, phoneNum, inputedEmail, inputedAddress;
    ImageView ContactImageImgView;
    Button create, favbtn;

    //list of class type
    List<Contacts> Contact = new ArrayList<Contacts>();
    ListView contactListView;
    List<Fav> fav = new ArrayList<Fav>();

    //list of favorite
    int CLickedItemIndex;
    ListView favListView;
    DatabaseHandler dbHandler;
    ArrayAdapter<Contacts> contactsAdaptor;
    ArrayAdapter<Fav> favAdaptor;
    Uri imageURI = Uri.parse("android.resource://com.example.contactmanager/drawable/userpic.png");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputedName = (EditText) findViewById(R.id.contactName);
        phoneNum = (EditText) findViewById(R.id.phoneNumber);
        inputedEmail = (EditText) findViewById(R.id.email);
        inputedAddress = (EditText) findViewById(R.id.Address);
        create = (Button)findViewById(R.id.create);
        favbtn = (Button)findViewById(R.id.favourites);
        contactListView = (ListView) findViewById(R.id.Phonelist);
        ContactImageImgView =(ImageView) findViewById(R.id.ContactimageView);
        favListView = (ListView) findViewById(R.id.FavList);
        dbHandler = new DatabaseHandler(getApplicationContext());

        registerForContextMenu(contactListView);
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CLickedItemIndex = position;
                return false;
            }
        });

        //Tabs menu
        final TabHost mtabHost = (TabHost) findViewById(R.id.tabhost);
        mtabHost.setup();

        TabHost.TabSpec mSpec =  mtabHost.newTabSpec("creator");
        mSpec.setContent(R.id.tabcreator);
        mSpec.setIndicator("ADD");
        mtabHost.addTab(mSpec);

        mSpec = mtabHost.newTabSpec("list");
        mSpec.setContent(R.id.tabList);
        mSpec.setIndicator("MyList");
        mtabHost.addTab(mSpec);

        mSpec = mtabHost.newTabSpec("Favorites");
        mSpec.setContent(R.id.tabFav);
        mSpec.setIndicator("Favourites");
        mtabHost.addTab(mSpec);


        //create action for the creating process
        create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(isEditMode){
                    Contacts newContact = Contact.get(CLickedItemIndex);
                    newContact.setName(String.valueOf(inputedName.getText()));
                    newContact.setNumber(String.valueOf(phoneNum.getText()));
                    newContact.setEmail(String.valueOf(inputedEmail.getText()));
                    newContact.setAddress(String.valueOf(inputedAddress.getText()));
                    newContact.setImage(imageURI);
                    dbHandler.updateContact(newContact);
                    isEditMode = false;
                    create.setText("Add Contacts");
                    contactsAdaptor.notifyDataSetChanged();
                    resetAddContactPanel();
                    mtabHost.setCurrentTab(1);
                }else{
                    addContact(contactId++, inputedName.getText().toString(), phoneNum.getText().toString(),
                            inputedEmail.getText().toString(), inputedAddress.getText().toString(), imageURI);

                    contactsAdaptor.notifyDataSetChanged();
                }
            }



        });


        inputedName.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                create.setEnabled(!inputedName.getText().toString().trim().isEmpty());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        //implementing action on the favourites button
        favbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //amount of contact we have plus one
                addfavContacts(inputedName.getText().toString());
                populatefavList();
                Toast.makeText(getApplicationContext() , inputedName.getText().toString() +" has been added to favorite", Toast.LENGTH_SHORT).show();
            }
        });

        //allow user to select picture for contact
        ContactImageImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a new intent to create a choose option
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                //get path of image
                startActivityForResult(Intent.createChooser(intent, "Select The Image") , 1 );


            }
        });

        inputedName.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                favbtn.setEnabled(!inputedName.getText().toString().trim().isEmpty());
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(dbHandler.getContactsCount() != 0)
            Contact.addAll(dbHandler.getAllContacts());

        populateList();
    }


    public void onCreateContextMenu(ContextMenu menu, View view , ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);

        //create menu
        menu.setHeaderIcon(R.drawable.edit);
        menu.setHeaderTitle("Options");

        //options for menu
        menu.add(Menu.NONE , EDIT , menu.NONE , "Update Contact");
        menu.add(Menu.NONE , DELETE , menu.NONE , "Delete Contact");

    }

    //validate user option
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case EDIT:
                enableEditMode(Contact.get(CLickedItemIndex));

                break;
            case DELETE:
                dbHandler.deleteContact(Contact.get(CLickedItemIndex));
                Contact.remove(CLickedItemIndex);
                contactsAdaptor.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    //check result of intent
    public void onActivityResult(int reqCode, int resCode, Intent data ) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                ContactImageImgView.setImageURI(data.getData());
                imageURI = data.getData();
            }
        }
    }

    public  void addContact(int id,String name,String phone, String email,String address,Uri imageUri){
        Contacts contactDetail = new Contacts(id,name, phone, email, address , imageUri);
        contactDetail.setId(id);
        dbHandler.createContact(contactDetail);
        Contact.add(contactDetail);
        contactsAdaptor.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), String.valueOf(inputedName.getText()) + " has been saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void  enableEditMode(Contacts contact){
        TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
        tabHost.setCurrentTab(0);
        inputedName.setText(contact.getName());
        phoneNum.setText(contact.getNumber());
        inputedEmail.setText(contact.getEmail());
        inputedAddress.setText(contact.getAddress());
        imageURI = contact.getImageURI();
        ContactImageImgView.setImageURI(imageURI);
        Button edit = (Button)findViewById(R.id.create);
        edit.setText("Update");
        isEditMode = true;
    }

    private void resetAddContactPanel(){
        inputedName.setText("");
        phoneNum.setText("");
        inputedEmail.setText("");
        inputedAddress.setText("");
        ContactImageImgView.setImageURI(Uri.parse(""));
    }

    private  void addfavContacts(String name){
        fav.add(new Fav(name));
    }

    private void populatefavList(){
        favAdaptor = new FavListAdaptor();
        favListView.setAdapter(favAdaptor);
    }

    private void populateList(){
        contactsAdaptor = new ContactListAdaptor();
        contactListView.setAdapter(contactsAdaptor);
    }



    //create contact list adaptor
    public class ContactListAdaptor extends ArrayAdapter<Contacts>{

        private ContactListAdaptor(){
            super(MainActivity.this, R.layout.listview_item , Contact);
        }

        //array to return array list
        @Override
        public View getView(int position, View view , ViewGroup parent){
            if(view == null)
                view= getLayoutInflater().inflate(R.layout.listview_item , parent , false);
            Contacts presentContact = Contact.get(position);

            TextView name = (TextView) view.findViewById(R.id.personname);
            name.setText(presentContact.getName());

            TextView phonenumber = (TextView) view.findViewById(R.id.ContactNumber);
            phonenumber.setText(presentContact.getNumber());

            TextView emailAddress = (TextView) view.findViewById(R.id.Contactemail);
            emailAddress.setText(presentContact.getEmail());

            TextView postaladdress = (TextView) view.findViewById(R.id.ContactAddress);
            postaladdress.setText(presentContact.getAddress());

            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            ivContactImage.setImageURI(presentContact.getImageURI());

            return view;

        }
    }


    //list adaptor for Favorite
    public class  FavListAdaptor extends ArrayAdapter<Fav>{
        private FavListAdaptor(){
            super(MainActivity.this, R.layout.fav_item , fav);
        }
        //array to return array list, also to contain what we need
        @Override
        public View getView(int position, View view , ViewGroup parent){
            if(view == null)
                view= getLayoutInflater().inflate(R.layout.fav_item , parent , false);
            Fav FavContact = fav.get(position);

            TextView name = (TextView) view.findViewById(R.id.FavPerson);
            name.setText(FavContact.getfavName());

            return view;

        }
    }

}