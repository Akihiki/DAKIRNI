package com.example.dakirni;

import com.example.dakirni.AdapterContact.Contact;
import com.example.dakirni.msgsAdapter.Message;

import java.lang.reflect.Array;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/api/message/post")
    Call<Void> addMessage(@Body Message sentMessage);
    @GET("/api/message/get")
    Call<List<Message>> getMessages();

    @POST("/contacts/addcontact")
    Call<Void> addContact(@Body Contact newContact);
    @GET("/contacts/allcontacts")
    Call<List<Contact>> getContacts();
    @POST("/contacts/updatecontact")
    Call<Void> updateContact(@Body Contact updatedContact);
    @POST("/contacts/deletecontact")
    Call<Void> deleteContact(@Body Contact deletedContact);
    @GET("/sons/allsons")
    Call<List<Contact>> getSons();
}
