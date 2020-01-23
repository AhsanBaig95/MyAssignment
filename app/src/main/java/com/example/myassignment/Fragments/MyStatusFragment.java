package com.example.myassignment.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.myassignment.Adapter.StatusAdapter;
import com.example.myassignment.BuildConfig;
import com.example.myassignment.FTP.FTPFunctions;
import com.example.myassignment.Helper.ImageFilePath;
import com.example.myassignment.Model.UserStatus;
import com.example.myassignment.R;
import com.example.myassignment.Retrofit.MyService;
import com.example.myassignment.Retrofit.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyStatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Uri videoUri;
    String videoPath,cameraImageFilePath, imageFileAbsolutePath, videoFileAbsolutePath;
    FTPFunctions ftpFunctions;
    MyService myService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<UserStatus> userStatusList;
    StatusAdapter statusAdapter;
    RecyclerView recyclerView;



    public MyStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyStatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyStatusFragment newInstance(String param1, String param2) {
        MyStatusFragment fragment = new MyStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_status, container, false);
         return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button btn_uploadImage,btn_uploadVideo;

        btn_uploadImage = view.findViewById(R.id.button_UploadImage);
        btn_uploadVideo = view.findViewById(R.id.button_UploadVideo);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userStatusList = new ArrayList<>();


        sharedPreferences = this.getActivity().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
        editor = sharedPreferences.edit();

 //       Toast.makeText(getActivity(), "User ID: "+sharedPreferences.getInt("userId",0), Toast.LENGTH_SHORT).show();

        try {
            ftpFunctions = new FTPFunctions(RetrofitClient.ipAddress,21,"anonymous","");
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Image VIew Clicked", Toast.LENGTH_SHORT).show();


                String[] options = {"Open Gallery", "Open Camera"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0)
                        {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,"Select Video"),2);

                        }
                        if (which==1)
                        {
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            videoUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
//                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            startActivityForResult(intent, 3);
                        }
                    }
                });
                builder.show();
            }
        });

        btn_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Open Gallery", "Open Camera"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0)
                        {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 0);
                        }
                        if (which==1)
                        {
                            captureImageFromCamera();
                        }
                    }
                });
                builder.show();
            }
        });

        getAUserStatus(sharedPreferences.getInt("userId",0));

//        FragmentManager fm = getFragmentManager();
//        FriendStatusFragment fragem = (FriendStatusFragment)fm.getFragment();
//        fragem.otherList();

//        FriendStatusFragment friendStatusFragment = new FriendStatusFragment();
//        friendStatusFragment.otherList();
//        friendStatusFragment.setCommunicator(this);
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }

        // Create a media file name
        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime());

        File mediaFile;
        if(type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");

        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // After camera screen this code will execute
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 ) {

            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                imageFileAbsolutePath = ImageFilePath.getPath(getActivity(), selectedImage);
                Log.d("ImagePath",""+ imageFileAbsolutePath);
                ftpConnect();
                uploadImage(imageFileAbsolutePath);

            } else if (resultCode == RESULT_CANCELED) {

                // User cancelled the video capture
                Toast.makeText(getActivity(), "User cancelled the image selection.", Toast.LENGTH_LONG).show();

            } else {
                // Video capture failed, advise user
                Toast.makeText(getActivity(), "Image selection failed.", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 1 ) {

            if (resultCode == RESULT_OK) {

                ftpConnect();
                uploadImage(imageFileAbsolutePath);

            } else if (resultCode == RESULT_CANCELED) {

                // User cancelled the video capture
                Toast.makeText(getActivity(), "User cancelled the image capture.", Toast.LENGTH_LONG).show();

            } else {
                // Video capture failed, advise user
                Toast.makeText(getActivity(), "Image capture failed.", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 2 ) {

            if (resultCode == RESULT_OK) {
                Uri selectedVideo = data.getData();
                videoFileAbsolutePath = ImageFilePath.getPath(getActivity(), selectedVideo);
                ftpConnect();
                uploadVideo(videoFileAbsolutePath);

            } else if (resultCode == RESULT_CANCELED) {

                // User cancelled the video capture
                Toast.makeText(getActivity(), "User cancelled the video selection.", Toast.LENGTH_LONG).show();

            } else {
                // Video capture failed, advise user
                Toast.makeText(getActivity(), "Video selection failed.", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 3 ) {

            if (resultCode == RESULT_OK) {
                videoUri = data.getData();
                videoPath = ImageFilePath.getPath(getActivity(), data.getData());

                Toast.makeText(getContext(), "Video Saved at:"+videoPath, Toast.LENGTH_SHORT).show();
                ftpConnect();
                uploadVideo(videoPath);


                // Video captured and saved to fileUri specified in the Intent
                //  Toast.makeText(getActivity(), "Video saved to: " +data.getData(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {

                // User cancelled the video capture
                Toast.makeText(getActivity(), "User cancelled the video capture.", Toast.LENGTH_LONG).show();

            } else {
                // Video capture failed, advise user
                Toast.makeText(getActivity(), "Video capture failed.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void ftpConnect()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {
            ftpFunctions = new FTPFunctions(RetrofitClient.ipAddress,21,"anonymous","");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("VideoFTP",""+e.getMessage());
        }
    }

    private void uploadVideo(String videoPath)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoName = "VID"+timeStamp+".mp4";
        try {
            boolean done = ftpFunctions.uploadFTPFile(videoPath,videoName,"/Video Status/");
            if (done)
            {
                addUserStatus("Video",videoName,sharedPreferences.getInt("userId",0));
                Toast.makeText(getActivity(), "Successfully uploaded on FTP Server", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getActivity(), "File upload unsuccessful on FTP Server", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Video",""+e.getMessage());
        }
    }

    private void uploadImage(String imageFileAbsolutePath)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG"+timeStamp+".jpg";
        try {
            boolean done = ftpFunctions.uploadFTPFile(imageFileAbsolutePath,imageName,"/Image Status/");
            if (done)
            {
                addUserStatus("Image",imageName,sharedPreferences.getInt("userId",0));
                Toast.makeText(getActivity(), "Successfully uploaded on FTP Server", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getActivity(), "File upload unsuccessful on FTP Server", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Image",""+e.getMessage());
        }
    }

    private void captureImageFromCamera() {
        try {
            //           Toast.makeText(this, "In function", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, 1);
        } catch (IOException ex) {
            Toast.makeText(getActivity(), ""+ex, Toast.LENGTH_LONG).show();
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
        cameraImageFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    public void addUserStatus(String statusType, String statusFileName, int user_id)
    {
        myService = RetrofitClient.getClient().create(MyService.class);

        myService.addUserStatus(statusType,statusFileName,user_id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) {
                            Log.d("PostResponse",""+response.body().toString());
                            if (response.body().toString().equals("success"))
                            {
//                                editor.putString("name",name);
//                                editor.putString("email",email);
//                                editor.putString("password",password);
//                                editor.putString("imageURL","");
//                                editor.commit();
                                statusAdapter.notifyDataSetChanged();
                                getAUserStatus(sharedPreferences.getInt("userId",0));


                                Toast.makeText(getActivity(), "Record saved in database", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getActivity(), "Email already exists", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("PostResponseError",""+t.getMessage());
                        Toast.makeText(getActivity(), "Error:"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getAUserStatus (final int userId) {

        myService = RetrofitClient.getClient().create(MyService.class);


        Call<ArrayList<UserStatus>> call = myService.getAUserStatus(userId);
        call.enqueue(new Callback<ArrayList<UserStatus>>() {
            @Override
            public void onResponse(Call<ArrayList<UserStatus>> call, Response<ArrayList<UserStatus>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                userStatusList = response.body();
                statusAdapter = new StatusAdapter(userStatusList);

                recyclerView.setAdapter(statusAdapter);


                for (UserStatus userStatus:userStatusList)
                {
                    String userName = userStatus.getUserName();
                    String statusType = userStatus.getStatusType();
                    String statusFileName = userStatus.getStatusFilename();
                    int userId = userStatus.getUserId();

                    Log.d("UsersStatus", " Status Type: "+statusType+
                            " Status File name: "+statusFileName+
                            " User Id: "+userId);
                }

                //               Toast.makeText(LoginActivity.this, "Name against user id: "+ userId+" is: "+name, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ArrayList<UserStatus>> call, Throwable t) {
                Toast.makeText(getActivity(), "Can not connect to Server", Toast.LENGTH_SHORT).show();
                Log.d("Error", "" + t.getMessage());
            }
        });

    }

}
