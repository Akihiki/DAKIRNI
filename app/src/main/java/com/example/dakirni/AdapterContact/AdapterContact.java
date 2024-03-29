package com.example.dakirni.AdapterContact;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dakirni.AddContactActivity;
import com.example.dakirni.ParentsDashBoard;
import com.example.dakirni.R;
import com.example.dakirni.RetrofitInterface;
import com.example.dakirni.database.son.SonDbHelper;
import com.example.dakirni.environements.environementVariablesOfDakirni;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ItemViewHolder> {

    private List<Contact> contactList;
    private Context mContext;
    environementVariablesOfDakirni env;


    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = env.BASE_URL;



    public AdapterContact(List<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.mContext = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design_for_contact, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        String resource = contactList.get(position).getImageview();
        String name = contactList.get(position).getTextview1();
        String msg = contactList.get(position).getTextview2();
        String line = contactList.get(position).getDivider();

        holder.setData(resource, name, msg, line);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private TextView textView2;
        private TextView divider;
        //
        private ImageView update_contact;
        private ImageView delete_contact;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_father);
            textView = itemView.findViewById(R.id.name_father);
            textView2 = itemView.findViewById(R.id.Key);
            //
            update_contact = itemView.findViewById(R.id.update_contact);
            delete_contact = itemView.findViewById(R.id.delete_contact);

            divider = itemView.findViewById(R.id.Divider);

            // To update a contact
            update_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Contact contact = contactList.get(pos);
//                    Toast.makeText(mContext, "pos : "+ pos + " --- " + contact.getContactId(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, AddContactActivity.class);
                    intent.putExtra("update", true);

                    Gson gson = new Gson();
                    String updatedContactJson = gson.toJson(contact);

                    intent.putExtra("contact", updatedContactJson);
                    mContext.startActivity(intent);
                }
            });

            delete_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                    retrofitInterface = retrofit.create(RetrofitInterface.class);
                    int pos = getAdapterPosition();
                    Contact deletedContact = contactList.get(pos);
                    deleteContact(deletedContact);
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callFunction();
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callFunction();
                }
            });

            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callFunction();
                }
            });
        }

        public void callFunction() {
            int pos = getAdapterPosition();
            Contact contact = contactList.get(pos);
            String number = contact.getTextview2();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
        }
        public void setData(String resource, String name, String msg, String line) {
           imageView.setImageBitmap(decodeImage(resource));
            textView.setText(name);
            textView2.setText(msg);
            divider.setText(line);
        }
    }

    public void deleteContact(Contact deletedContact) {

        SonDbHelper sonDbHelper = new SonDbHelper(mContext);
        ArrayList<String> arrayList = sonDbHelper.lireToken();
        StringBuffer maListe = new StringBuffer();

        try {
            Iterator<String> iter = arrayList.iterator();
            while (iter.hasNext()) {
                maListe.append(iter.next());
            }
            Toast.makeText(mContext, maListe.toString(), Toast.LENGTH_SHORT).show();
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(mContext, "Aucun Résultat trouvé !", Toast.LENGTH_SHORT).show();
        }

        Call<Void> call = retrofitInterface.deleteContact(maListe.toString(), deletedContact, environementVariablesOfDakirni.key);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(mContext, "Contact deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public Bitmap decodeImage(String stringImage) {
        byte[] bytes = Base64.decode(stringImage, Base64.DEFAULT);
        // Initialize bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        // set bitmap on imageView
        return bitmap;
    }
}
