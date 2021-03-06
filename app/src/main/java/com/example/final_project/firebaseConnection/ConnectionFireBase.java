package com.example.final_project.firebaseConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.final_project.CheckService.Formate;
import com.example.final_project.Database.BlankContract;
import com.example.final_project.Database.BorrowersDB.Home_DataContact;
import com.example.final_project.Database.DatabaseHelper;
import com.example.final_project.Database.useradate.UserDatadbProvider;
import com.example.final_project.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.text.Format;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

//Todo : Add Time limits in the fetching process and low-internet connectivity alert
public class ConnectionFireBase {
    private String TAG = "ConnectionFireBase";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    public Uri downloadImageUri;
    public ConnectionFireBase(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void setData(UserData data,String userEmail){
        StringBuilder l= new StringBuilder();
        for(int i =0 ; i<userEmail.length() ; i++){
            if(userEmail.charAt(i)!='.' && userEmail.charAt(i)!='#' && userEmail.charAt(i)!='$' && userEmail.charAt(i)!='[' && userEmail.charAt(i)!=']')
                l.append(userEmail.charAt(i));
        }
        myRef = database.getReference("Member/"+l+"/UserProfile/");

        myRef.setValue(data);
        Log.e(TAG,"Data Set to database");
    }

    public void pushNotification(Home_DataContact data, String username){
        StringBuilder l= new StringBuilder();
        for(int i =0 ; i<username.length() ; i++){
            if(username.charAt(i)!='.' && username.charAt(i)!='#' && username.charAt(i)!='$' && username.charAt(i)!='[' && username.charAt(i)!=']')
                l.append(username.charAt(i));
        }
        myRef = database.getReference("Member/"+l+"/Notification/");
        myRef.push().setValue(data);
        Log.e(TAG,"Data Set to database");
    }

    public void pushToLends(Home_DataContact data, String username){
        String l= Formate.toUsername(username);
        myRef = database.getReference("Member/"+l+"/lends/");
        myRef.push().setValue(data);
        Log.e(TAG,"Data Set to database");
    }

    public void uploadImageToStorage(String location, String mimageName, Uri imageUrl){
        StringBuilder imageName= new StringBuilder();
        for(int i =0 ; i<mimageName.length() ; i++){
            if(mimageName.charAt(i)!='.' && mimageName.charAt(i)!='#' && mimageName.charAt(i)!='$' && mimageName.charAt(i)!='[' && mimageName.charAt(i)!=']')
                imageName.append(mimageName.charAt(i));
        }

        Log.e(TAG,location+" "+imageName+" "+imageUrl);
        StorageReference riversRef = mStorageRef.child(location+"/"+imageName);
        if(imageUrl != null)
        riversRef.putFile(imageUrl)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    downloadImageUri = taskSnapshot.getUploadSessionUri();
                    Log.e(TAG,"image uploaded");
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG,"Cant upload file");
                    downloadImageUri = null;
                });
        else{
            Log.e(TAG,"Url is empty");
        }
    }

    public void uploadImageToStorage(String location, String mimageName, byte[] imageUrl){
        StringBuilder imageName= new StringBuilder();
        for(int i =0 ; i<mimageName.length() ; i++){
            if(mimageName.charAt(i)!='.' && mimageName.charAt(i)!='#' && mimageName.charAt(i)!='$' && mimageName.charAt(i)!='[' && mimageName.charAt(i)!=']')
                imageName.append(mimageName.charAt(i));
        }
        Log.e(TAG,location+" "+imageName+" "+imageName);
        StorageReference riversRef = mStorageRef.child(location+"/"+imageName);
        if(imageUrl != null)
            riversRef.putBytes(imageUrl)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get a URL to the uploaded content
                        downloadImageUri = taskSnapshot.getUploadSessionUri();
                        Log.e(TAG,"image uploaded");
                    })
                    .addOnFailureListener(exception -> {
                        Log.e(TAG,"Cant upload file");
                        downloadImageUri = null;
                    });
        else{
            Log.e(TAG,"Url is empty");
        }
    }

    public void uploadImageToStorage(String location, String mimageName, byte[] imageUrl, AppCompatActivity view){
        StringBuilder imageName= new StringBuilder();
        for(int i =0 ; i<mimageName.length() ; i++){
            if(mimageName.charAt(i)!='.' && mimageName.charAt(i)!='#' && mimageName.charAt(i)!='$' && mimageName.charAt(i)!='[' && mimageName.charAt(i)!=']')
                imageName.append(mimageName.charAt(i));
        }
        Log.e(TAG,location+" "+imageName+" "+ Arrays.toString(imageUrl));
        ProgressDialog n = new ProgressDialog(view.getApplicationContext());
        StorageReference riversRef = mStorageRef.child(location+"/"+imageName);
        if(imageUrl != null)
            riversRef.putBytes(imageUrl)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    downloadImageUri = taskSnapshot.getUploadSessionUri();
                    Log.e(TAG,"image uploaded");
                    n.dismiss();
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG,"Cant upload file");
                    downloadImageUri = null;
                }).addOnProgressListener(view, taskSnapshot -> {
                    n.show();
                    int progress = (int) (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    n.setProgress(progress);
                });
        else{
            Log.e(TAG,"Url is empty");
        }
    }


    public void downloadProfileImage(String mimageName) {
        String imageName= Formate.toUsername(mimageName);
        StorageReference riversRef = mStorageRef.child("prof_image"+"/"+imageName);
        // Got the download URL for 'users/me/profile.png'
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadImageUri = uri;
            Log.e(TAG, "download image url : " + downloadImageUri.toString()+"\n path : "+uri.getPath()+"\n LPS : "+uri.getLastPathSegment());
        }).addOnFailureListener(exception -> Log.e(TAG,"Can't able to find the photo"));
    }

    @SuppressLint("ShowToast")
    public void uploadImageToStorage(String location, String mimageName, Uri imageUrl, Activity view,ImageView imageView){
        StringBuilder imageName= new StringBuilder();
        for(int i =0 ; i<mimageName.length() ; i++){
            if(mimageName.charAt(i)!='.' && mimageName.charAt(i)!='#' && mimageName.charAt(i)!='$' && mimageName.charAt(i)!='[' && mimageName.charAt(i)!=']')
                imageName.append(mimageName.charAt(i));
        }
        StorageReference riversRef = mStorageRef.child(location+"/"+imageName);
        ProgressDialog n = new ProgressDialog(view);
        n.setTitle("Uploading");
        n.show();
        n.setProgress(0);
        riversRef.putFile(imageUrl)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    downloadImageUri = taskSnapshot.getUploadSessionUri();
                    Glide.with(view)
                            .load(taskSnapshot.getUploadSessionUri())
                            .into(imageView);
                    n.dismiss();
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG,"Cant upload file");
                    Toast.makeText(view.getApplicationContext(),"Can't able to upload file",Toast.LENGTH_SHORT).show();
                    downloadImageUri = null;
                    n.dismiss();
                }).addOnProgressListener(view, taskSnapshot -> {
                    int progress = (int) (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    n.setProgress(progress);
                });
    }
    /**
     * <p>return the url od the image of given location,name</p>
     * @param location ,where image has to be stored in the database
     * @param mimageName name of the image
     * */
    public void downloadProfileImage(String location, String mimageName) {
        StringBuilder imageName= new StringBuilder();
        for(int i =0 ; i<mimageName.length() ; i++){
            if(mimageName.charAt(i)!='.' && mimageName.charAt(i)!='#' && mimageName.charAt(i)!='$' && mimageName.charAt(i)!='[' && mimageName.charAt(i)!=']')
                imageName.append(mimageName.charAt(i));
        }
        StorageReference riversRef = mStorageRef.child(location+"/"+imageName);
        // Got the download URL for 'users/me/profile.png'
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadImageUri = uri;
            Log.e(TAG, "download image url : " + downloadImageUri.toString()+"\n path : "+uri.getPath()+"\n LPS : "+uri.getLastPathSegment());
            }).addOnFailureListener(exception -> Log.e(TAG,"Can't able to find the photo"));
    }

    /**
     * <p>return the url od the image of given location,name</p>
     * @param location ,where image has to be stored in the database
     * @param mimageName name of the image
     * */
    public void downloadProfileImage(String location, String mimageName, ImageView imageView, Context context) {
        String imageName= Formate.toUsername(mimageName);
        StorageReference riversRef = mStorageRef.child(location+"/"+imageName);
        ProgressDialog n = new ProgressDialog(context);
        n.setTitle("Loading");
        n.setMessage("Loading your image");
        n.show();
        // Got the download URL for 'users/me/profile.png'
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadImageUri = uri;
            Glide.with(context.getApplicationContext())
                    .load(uri)
                    .placeholder(R.drawable.account_pic)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            n.dismiss();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            n.dismiss();
                            return false;
                        }
                    })
                    .into(imageView);
            }).addOnFailureListener(exception -> {
            n.dismiss();
            Toast.makeText(context,"Can't able to find your photo",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Can't able to find the photo");
        });
    }

    public void downloadProfileImage(String location, String mimageName, ImageView imageView,View context) {
        StringBuilder imageName= new StringBuilder();
        for(int i =0 ; i<mimageName.length() ; i++){
            if(mimageName.charAt(i)!='.' && mimageName.charAt(i)!='#' && mimageName.charAt(i)!='$' && mimageName.charAt(i)!='[' && mimageName.charAt(i)!=']')
                imageName.append(mimageName.charAt(i));
        }
        StorageReference riversRef = mStorageRef.child(location+"/"+imageName.toString());
        // Got the download URL for 'users/me/profile.png'
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadImageUri = uri;
            Glide.with(context.getContext())
                    .load(uri)
                    .placeholder(R.drawable.account_pic)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            Toast.makeText(context.getContext(),"Can't able to find your photo",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Can't able to find the photo");
        });
    }

 /**
     * <p>return the url od the image of given location,name</p>
     * @param location ,where image has to be stored in the database
     * @param mimageName name of the image
     * */
    public void downloadProfileImage(String location, String mimageName, ImageView imageView, Context context,ProgressBar progressBar) {
        StringBuilder imageName= new StringBuilder();
        for(int i =0 ; i<mimageName.length() ; i++){
            if(mimageName.charAt(i)!='.' && mimageName.charAt(i)!='#' && mimageName.charAt(i)!='$' && mimageName.charAt(i)!='[' && mimageName.charAt(i)!=']')
                imageName.append(mimageName.charAt(i));
        }
        StorageReference riversRef = mStorageRef.child(location+"/"+imageName);
        progressBar.setVisibility(View.VISIBLE);
        // Got the download URL for 'users/me/profile.png'
        riversRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context.getApplicationContext())
                .load(uri)
                .placeholder(R.drawable.account_pic)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView)).addOnFailureListener(exception -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(context,"Can't able to find your photo",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Can't able to find the photo");
        });
    }

    /**Add the notification in the firebase database and storing in local database
    * @param i get the email
     * @param  date get the last date of returning the payment
     * @param  a get the amount
     * @param mdbHelper get the DatabaseHelper object
     * @param context get the context of the activity
     * @return  boolean value for if data inserted properly*/

    public boolean pushNotification(String i, String date, double a, DatabaseHelper mdbHelper,Context context){
        String l= Formate.toUsername(i);
        myRef = database.getReference("Member/"+l+"/");
        Log.e(TAG,"data check : "+"Member/"+l+"/");
        final String[] image = new String[1];
        final String[] name = new String[1];
        final Long time = new Timestamp(new Date().getTime()).getTime();
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().equals("name")){
                        name[0] = ds.getValue(String.class);
                        Log.e(TAG,"Name !: " + name[0]);
                        ConnectionFireBase connection = new ConnectionFireBase();
                        //Adding the notification in the firebase
                        Home_DataContact contact = new Home_DataContact(context);
                        Home_DataContact contact2 = new Home_DataContact(context);
                        contact.setId(new UserDatadbProvider(context).getEmail());
                        contact.setDate(date);
                        contact.setAmount(a);
                        contact.setCount(time);
                        contact.setName(new UserDatadbProvider(context).getName());
                        contact.setImageUrl(new UserDatadbProvider(context).getImage());
                        contact.setPayed(false);
                        //Adding lends to firebase
                        contact2.setId(i);
                        contact2.setDate(date);
                        contact2.setAmount(a);
                        contact2.setCount(time);
                        contact2.setName(name[0]);
                        contact2.setImageUrl(image[0]);
                        contact2.setPayed(false);

                        connection.pushToLends(contact2, Formate.toUsername(new UserDatadbProvider(context).getEmail()));
                        connection.pushNotification(contact,i);

                    }else if(ds.getKey().equals("image")){
                        image[0] = ds.getValue(String.class);
                        Log.e(TAG,"Image !: " + image[0]);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        myRef.addChildEventListener(listener);

        return true;
    }

    /**
     * <p>Add the notification in the firebase database and storing in local database</p>
     * @param username ,name of the user
     * @return the object of UserData*/
    public UserData getUserData(String username) throws InterruptedException {
         StringBuilder l= new StringBuilder();
        for(int i =0 ; i<username.length() ; i++){
            if(username.charAt(i)!='.' && username.charAt(i)!='#' && username.charAt(i)!='$' && username.charAt(i)!='[' && username.charAt(i)!=']')
                l.append(username.charAt(i));
        }
        myRef = database.getReference("Member/"+l+"/UserProfile/");
        Log.e(TAG,"datacheck : "+"Member/"+l+"/UserProfile/");
        final UserData[] result = {new UserData()};

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                result[0] = snapshot.getValue(UserData.class);
                //Log.e(TAG, "sad" + result[0].getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.e(TAG,"The read failed: " + databaseError.getCode());
            }
        }).wait(1000);

       /* myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG,"GetBorrowDbProvider function : " +snapshot.getChildren());
                Log.e(TAG,"GetBorrowDbProvider function : " +snapshot.getValue(UserData.class).getName());
                result[0] = snapshot.getValue(UserData.class);
                //Log.e(TAG, "sad" + result[0].getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });*/
        return result[0];
    }

}
