package com.example.contactmanager;

import android.net.Uri;

public class Contacts {

    private String _name, _number , _email , _address;
    private Uri _imageURI;
    private int _id;

    public Contacts(int id ,String name, String number, String email, String address , Uri imageURI){

        //variables required
        _id = id;
        _name = name;
        _number = number;
        _email = email;
        _address = address;
        _imageURI = imageURI;

    }

    //get and returns ID
    public int getid(){
        return _id;
    }

    public String getName(){
        return _name;
    }
    public String getNumber(){
        return _number;
    }
    public String getEmail(){
        return _email;
    }
    public String getAddress(){
        return _address;
    }

    //obtain image
    //set id, name, number, email, address and image
    public Uri getImageURI(){
        return _imageURI;
    }
    public void setId(int id){
        this._id = id;
    }
    public void setName(String name){
        this._name = name;
    }
    public void setNumber(String number){
        this._number = number;
    }
    public void setEmail(String email){
        this._email = email;
    }
    public void setAddress(String address){
        this._address = address;
    }
    public void setImage(Uri imageURI){
        this._imageURI = imageURI;
    }


}

