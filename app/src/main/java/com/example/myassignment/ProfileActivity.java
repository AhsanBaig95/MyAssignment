package com.example.myassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myassignment.FTP.FTPFunctions;
import com.example.myassignment.Helper.ImageFilePath;
import com.example.myassignment.Model.User;
import com.example.myassignment.Retrofit.MyService;
import com.example.myassignment.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    ImageView img_ProfileImage;
    EditText edt_Name, edt_Email;
    Button btn_Save;
    boolean editable = false;
    private String cameraFilePath, imageFileAbsolutePath, profilePictureName;
    String email, password;
    MyService myService;
    ArrayList<User> userList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FTPFunctions ftpFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        img_ProfileImage = findViewById(R.id.imageView_profileImage);
        edt_Name = findViewById(R.id.editText_UserName);
        edt_Email = findViewById(R.id.editText_UserEmail);
        btn_Save = findViewById(R.id.button_Save);

        sharedPreferences = getApplicationContext().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
        editor = sharedPreferences.edit();


        getAUser(sharedPreferences.getInt("userId",0));

//        edt_Name.setText(sharedPreferences.getString("name",""));
//        edt_Email.setText(sharedPreferences.getString("email",""));
//        email = sharedPreferences.getString("email","");
//        password = sharedPreferences.getString("password","");
//        imageFileAbsolutePath = sharedPreferences.getString("imageURL","");

        img_ProfileImage.setEnabled(false);
        edt_Name.setEnabled(false);
        edt_Email.setEnabled(false);
        btn_Save.setEnabled(false);



        userList = new ArrayList<>();

        img_ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] colors = {"Open Gallery", "Open Camera"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Choose");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0)
                        {
//                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(takePicture, 0);
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 0);
                        }
                        if (which==1)
                        {
                            captureFromCamera();
                        }
                    }
                });
                builder.show();



            }
        });

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_Name.getText().toString().equals(""))
                {
                    updateUser(edt_Name.getText().toString(),email,password,profilePictureName);
                }
            }
        });


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {
            ftpFunctions = new FTPFunctions(RetrofitClient.ipAddress,21,"anonymous","");
        } catch (Exception e) {
            e.printStackTrace();
        }


//        FtpType ftpType = new FtpType("192.168.7.229/team.jpg");
//        Glide.with(this).load(ftpType)
//                .into(img_ProfileImage);

//        ftpFunctions.downloadFTPFile("ftp://192.168.1.106/MyPic","/storage/emulated/0/DCIM/Camera/MyPic.png");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.editProfile:
            editable();
//            if (!editable) {
//                editable = true;
//                img_ProfileImage.setEnabled(true);
//                edt_Name.setEnabled(true);
// //               edt_Email.setEnabled(true);
//                btn_Save.setEnabled(true);
//            }
//            else
//            {
//                editable = false;
//                img_ProfileImage.setEnabled(false);
//                edt_Name.setEnabled(false);
//                btn_Save.setEnabled(false);
////                edt_Email.setEnabled(false);
//            }
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    img_ProfileImage.setImageURI(selectedImage);
                    img_ProfileImage.setImageURI(Uri.parse(cameraFilePath));
                    Log.d("ImagePath",""+ imageFileAbsolutePath);
                    uploadImage(imageFileAbsolutePath);
//                    try {
//                        boolean done = ftpFunctions.uploadFTPFile(imageFileAbsolutePath,"MyPicc.jpg","/");
//                        if (done)
//                        {
//                            Toast.makeText(this, "Successfully uploaded on FTP Server", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                            Toast.makeText(this, "File upload unsuccessful on FTP Server", Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }

                break;
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageFileAbsolutePath = ImageFilePath.getPath(ProfileActivity.this, imageReturnedIntent.getData());
                    Log.d("ImagePath",""+ imageFileAbsolutePath);
                    img_ProfileImage.setImageURI(selectedImage);
 //                   uploadFileInFTP(imageFileAbsolutePath);
                    uploadImage(imageFileAbsolutePath);
//                    try {
//                        boolean done = ftpFunctions.uploadFTPFile(imageFileAbsolutePath,"MyPiccc.jpg","/");
//                        if (done)
//                        {
//                            Toast.makeText(this, "Successfully uploaded on FTP Server", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                            Toast.makeText(this, "File upload unsuccessful on FTP Server", Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
                break;
        }
    }

    private void captureFromCamera() {
        try {
 //           Toast.makeText(this, "In function", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, 1);
        } catch (IOException ex) {
            Toast.makeText(this, ""+ex, Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        imageFileAbsolutePath = image.getAbsolutePath();
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

//    public void connnectingwithFTP(String ip, String userName, String pass) {
//        boolean status = false;
//        try {
//            FTPClient mFtpClient = new FTPClient();
//            mFtpClient.setConnectTimeout(10 * 1000);
//            mFtpClient.connect(InetAddress.getByName(ip));
//            status = mFtpClient.login(userName, pass);
//            Log.e("isFTPConnected", String.valueOf(status));
//            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
//                mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
//                mFtpClient.enterLocalPassiveMode();
//                FTPFile[] mFileArray = mFtpClient.listFiles();
//                Log.e("Size", String.valueOf(mFileArray.length));
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public void updateUser(final String name, final String email, final String password, final String imageURL)
    {
        myService = RetrofitClient.getClient().create(MyService.class);

        myService.updateUser(name,email,password,imageURL)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) {
                            Log.d("PostResponse",""+response.body().toString());
                            if (response.body().toString().equals("success"))
                            {
                                editor.putString("name",name);
                                editor.putString("email",email);
                                editor.putString("password",password);
                                editor.putString("imageURL",imageURL);
                                editor.commit();
                                editable();
                                Toast.makeText(ProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(ProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(ProfileActivity.this, "Server error", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("PostResponseError",""+t.getMessage());
                        Toast.makeText(ProfileActivity.this, "Error:"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void editable()
    {
        if (!editable) {
            editable = true;
            img_ProfileImage.setEnabled(true);
            edt_Name.setEnabled(true);
            //               edt_Email.setEnabled(true);
            btn_Save.setEnabled(true);
        }
        else
        {
            editable = false;
            img_ProfileImage.setEnabled(false);
            edt_Name.setEnabled(false);
            btn_Save.setEnabled(false);
//                edt_Email.setEnabled(false);
        }
    }

//    private void uploadFileInFTP(String filePath)
//    {
//        try
//        {
//
//            SimpleFTP ftp = new SimpleFTP();
//
//            // Connect to an FTP server on port 21.
//            ftp.connect("192.168.1.106",21, "FTP-User", "12345");
//
//            // Set binary mode.
//            ftp.bin();
//
//            // Change to a new working directory on the FTP server.
//            ftp.cwd("images");
//
//            // Upload some files.
//            ftp.stor(new File(filePath));
////            ftp.stor(new File("comicbot-latest.png"));
//
//            // You can also upload from an InputStream, e.g.
////            ftp.stor(new FileInputStream(new File("test.png")), "test.png");
////            ftp.stor(someSocket.getInputStream(), "blah.dat");
//
//            // Quit from the FTP server.
//            ftp.disconnect();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            Log.d("Error",""+e.getMessage());
//        }
//
//    }

    private void uploadImage(String imageFileAbsolutePath)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG"+timeStamp+".jpg";
        try {
            boolean done = ftpFunctions.uploadFTPFile(imageFileAbsolutePath,imageName,"/Profile Pictures/");
            if (done)
            {
                profilePictureName = imageName;
//                addUserStatus("Image",imageName,sharedPreferences.getInt("userId",0));
                Toast.makeText(ProfileActivity.this, "Successfully uploaded on FTP Server", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(ProfileActivity.this, "File upload unsuccessful on FTP Server", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAUser(final int userId) {

        myService = RetrofitClient.getClient().create(MyService.class);


        Call<User> call = myService.getAUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                edt_Name.setText(response.body().getName());
                edt_Email.setText(response.body().getEmail());
                email = response.body().getEmail();
                password = response.body().getPassword();
                profilePictureName = response.body().getImageURL();
//                Toast.makeText(ProfileActivity.this, "Name against user id: "+ userId+" is: "+name, Toast.LENGTH_SHORT).show();

                if (!profilePictureName.equals("")) {
                    Toast.makeText(ProfileActivity.this, "Downloading image", Toast.LENGTH_SHORT).show();
                    try {
//                        Glide.with(ProfileActivity.this)
////                            .load("ftp://192.168.1.106/Profile Pictures/" + profilePictureName)
////                            .load("https://img.thedailybeast.com/image/upload/c_crop,d_placeholder_euli9k,h_475,w_845,x_0,y_100/dpr_2.0/c_limit,w_740/fl_lossy,q_auto/v1576033567/108518_D00224a_hxo7tf")
//                                .load(Uri.parse("ftp://192.168.1.106/Profile%20Pictures/IMG20200122_013210"))
//                                .into(img_ProfileImage);

                        Picasso.get().load("192.168.1.106/Profile%20Pictures/team.jpg").into(img_ProfileImage);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(ProfileActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Can not connect to Server", Toast.LENGTH_SHORT).show();
                Log.d("Error", "" + t.getMessage());
            }
        });

    }

}
