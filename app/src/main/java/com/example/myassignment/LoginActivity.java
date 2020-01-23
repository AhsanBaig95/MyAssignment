package com.example.myassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myassignment.Model.User;
import com.example.myassignment.Model.UserStatus;
import com.example.myassignment.Retrofit.MyService;
import com.example.myassignment.Retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText edt_Email, edt_Password;
    Button btn_Login;
    TextView txt_SignUp;

    MyService myService;
    ArrayList<User> userList;
    ArrayList<UserStatus> userStatusList;
    boolean isAuthSuccessful = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_Email = findViewById(R.id.editText_Email);
        edt_Password = findViewById(R.id.editText_Password);
        btn_Login = findViewById(R.id.button_Login);
        txt_SignUp = findViewById(R.id.textView_Signup);

        userList = new ArrayList<>();
        userStatusList = new ArrayList<>();
        sharedPreferences = getApplicationContext().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
        editor = sharedPreferences.edit();

        getAllUsers();

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_Email.getText().toString().equals(""))
                    edt_Email.setError("Please enter Email address");

                if (edt_Password.getText().toString().equals(""))
                    edt_Password.setError("Please enter Password");

                if (!edt_Email.getText().toString().equals("") && !edt_Password.getText().toString().equals("")) {
//                    if (edt_Email.getText().toString().equals("ahsan@gmail.com")&&edt_Password.getText().toString().equals("12345"))
//                    {
//                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
//                    }
//                    else
//                    {
//                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
//                    }
                    try {
                        if (!userList.isEmpty())
                        {
                            isAuthSuccessful = userAuth(edt_Email.getText().toString().trim(), edt_Password.getText().toString().trim());

                            if (isAuthSuccessful) {

                                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                            else
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(LoginActivity.this, "No records found", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Log.e("Exception", "" + e.getMessage());
                    }


                }


            }
        });

        txt_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

 //       getAUser(11);
 //       getAllUsersStatus();
//        addUserStatus("Image","Login.jpg",2);
//        getAUserStatus(1);
//        deleteAStatus(5);


    }

    private void getAllUsers() {

        myService = RetrofitClient.getClient().create(MyService.class);

        Call<ArrayList<User>> call = myService.getAllUsers();
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                userList = response.body();
//                for (User user:userList)
//                {
//                    int userID = user.getId();
//                    String userName = user.getName();
////                    String statusFileName = userStatus.getStatusFilename();
////                    String userName = userStatus.getUserName();
////                    int userID = userStatus.getUserId();
//
//                    Log.d("UsersStatus", " User Id: "+userID+
//                            " User Name: "+userName);
//                }

            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Can not connect to Server", Toast.LENGTH_SHORT).show();
                Log.d("Error", "" + t.getMessage());
            }
        });

    }

    private void getAUser(final int userId) {

        myService = RetrofitClient.getClient().create(MyService.class);


        Call<User> call = myService.getAUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = response.body().getName();
                Toast.makeText(LoginActivity.this, "Name against user id: "+ userId+" is: "+name, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Can not connect to Server", Toast.LENGTH_SHORT).show();
                Log.d("Error", "" + t.getMessage());
            }
        });

    }

    private boolean userAuth(String email, String password)
    {
        for (User user : userList) {
            Log.d("Users", user.getEmail());
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                editor.putString("name",user.getName());
                editor.putString("email",user.getEmail());
                editor.putString("password",user.getPassword());
                editor.putString("imageURL",user.getImageURL());
                editor.putInt("userId",user.getId());
                editor.commit();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        finish();
    }

    private void getAllUsersStatus() {

        myService = RetrofitClient.getClient().create(MyService.class);

        Call<ArrayList<UserStatus>> call = myService.getAllUsersStatus();
        call.enqueue(new Callback<ArrayList<UserStatus>>() {
            @Override
            public void onResponse(Call<ArrayList<UserStatus>> call, Response<ArrayList<UserStatus>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                userStatusList = response.body();
                for (UserStatus userStatus:userStatusList)
                {
                    int statusID = userStatus.getStatusId();
                    String statusType = userStatus.getStatusType();
                    String statusFileName = userStatus.getStatusFilename();
                    String userName = userStatus.getUserName();
                    int userID = userStatus.getUserId();

                    Log.d("UsersStatus", "Status Id: "+statusID+
                            " Status Type: "+statusType+
                            " Status File name: "+statusFileName+
                            " User Id: "+userID+
                            " User Name: "+userName);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<UserStatus>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Can not connect to Server", Toast.LENGTH_SHORT).show();
                Log.d("Error", "" + t.getMessage());
            }
        });

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
                                Toast.makeText(LoginActivity.this, "Record saved in database", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(LoginActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Server error", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("PostResponseError",""+t.getMessage());
                        Toast.makeText(LoginActivity.this, "Error:"+t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                userStatusList = response.body();

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
                Toast.makeText(LoginActivity.this, "Can not connect to Server", Toast.LENGTH_SHORT).show();
                Log.d("Error", "" + t.getMessage());
            }
        });

    }

    private void deleteAStatus (final int statusId) {

        myService = RetrofitClient.getClient().create(MyService.class);


        Call<String> call = myService.deleteAStatus(statusId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

//                userStatusList = response.body();
//
//                for (UserStatus userStatus:userStatusList)
//                {
//                    String userName = userStatus.getUserName();
//                    String statusType = userStatus.getStatusType();
//                    String statusFileName = userStatus.getStatusFilename();
//                    int userId = userStatus.getUserId();
//
//                    Log.d("UsersStatus", " Status Type: "+statusType+
//                            " Status File name: "+statusFileName+
//                            " User Id: "+userId);
//                }
                if (response.body().toString().equals("success"))
                Toast.makeText(LoginActivity.this, "Status deleted successfully", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Can not connect to Server", Toast.LENGTH_SHORT).show();
                Log.d("Error", "" + t.getMessage());
            }
        });

    }

}
