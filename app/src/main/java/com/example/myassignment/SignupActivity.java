package com.example.myassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myassignment.Model.User;
import com.example.myassignment.Retrofit.MyService;
import com.example.myassignment.Retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText edt_Name, edt_Email, edt_Password;
    Button btn_SignUp;
    TextView txt_Login;
    MyService myService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edt_Name = findViewById(R.id.editText_Name);
        edt_Email = findViewById(R.id.editText_Email);
        edt_Password = findViewById(R.id.editText_Password);
        btn_SignUp = findViewById(R.id.button_SignUp);
        txt_Login = findViewById(R.id.textView_Login);

        userList = new ArrayList<>();

        sharedPreferences = getApplicationContext().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
        editor = sharedPreferences.edit();

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_Name.getText().toString().equals(""))
                    edt_Name.setError("Please enter Name");

                if (edt_Email.getText().toString().equals(""))
                    edt_Email.setError("Please enter Email address");

                if (edt_Password.getText().toString().equals(""))
                    edt_Password.setError("Please enter Password");

                if (!edt_Email.getText().toString().equals("")&&!edt_Password.getText().toString().equals("")&&!edt_Name.getText().toString().equals("")) {
//                    if (edt_Email.getText().toString().equals("ahsan@gmail.com")&&edt_Password.getText().toString().equals("12345")&&edt_Name.getText().toString().equals("Ahsan"))
//                    {
//                        startActivity(new Intent(SignupActivity.this,MainActivity.class));
//                    }
//                    else
//                    {
//                        Toast.makeText(SignupActivity.this, "Some fields are Empty", Toast.LENGTH_SHORT).show();
//                    }
                    createUser(edt_Name.getText().toString().trim(),edt_Email.getText().toString().trim(),edt_Password.getText().toString().trim());

                }

            }
        });

        txt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });



    }

    public void createUser(final String name, final String email, final String password)
    {
        myService = RetrofitClient.getClient().create(MyService.class);

        myService.createUser(name,email,password,"")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()) {
                            Log.d("PostResponse",""+response.body().toString());
                            if (response.body().toString().equals("success"))
                            {
                                getAllUsers();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something
                                        if (!userList.isEmpty())
                                        userAuth(email,password);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(SignupActivity.this,MainActivity.class));
                                            }
                                        },2000);
                                    }
                                }, 2000);

                            }
                            else
                                Toast.makeText(SignupActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(SignupActivity.this, "Server error", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("PostResponseError",""+t.getMessage());
                        Toast.makeText(SignupActivity.this, "Error:"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAllUsers() {

        myService = RetrofitClient.getClient().create(MyService.class);

        Call<ArrayList<User>> call = myService.getAllUsers();
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                userList = response.body();

            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Can not connect to Server", Toast.LENGTH_SHORT).show();
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
                editor.putString("imageURL","");
                editor.putInt("userId",user.getId());
                editor.commit();
                return true;
            }
        }
        return false;
    }
}
