package com.example.myassignment.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myassignment.Adapter.FriendsStatusAdapter;
import com.example.myassignment.Adapter.StatusAdapter;
import com.example.myassignment.LoginActivity;
import com.example.myassignment.Model.UserStatus;
import com.example.myassignment.R;
import com.example.myassignment.Retrofit.MyService;
import com.example.myassignment.Retrofit.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendStatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<UserStatus> userStatusList;
    ArrayList<UserStatus> friendsStatusList;
    FriendsStatusAdapter friendsStatusAdapter;
    RecyclerView recyclerView;
    MyService myService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    public FriendStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendStatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendStatusFragment newInstance(String param1, String param2) {
        FriendStatusFragment fragment = new FriendStatusFragment();
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
        return inflater.inflate(R.layout.fragment_friend_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userStatusList = new ArrayList<>();
        friendsStatusList = new ArrayList<>();

        sharedPreferences = this.getActivity().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
        editor = sharedPreferences.edit();

        getAllUsersStatus();
    }

    private void getAllUsersStatus() {

        myService = RetrofitClient.getClient().create(MyService.class);

        Call<ArrayList<UserStatus>> call = myService.getAllUsersStatus();
        call.enqueue(new Callback<ArrayList<UserStatus>>() {
            @Override
            public void onResponse(Call<ArrayList<UserStatus>> call, Response<ArrayList<UserStatus>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                userStatusList = response.body();

                for (UserStatus userStatus:userStatusList)
                {
                    if (userStatus.getUserId()!=sharedPreferences.getInt("userId",0))
                    {
                        friendsStatusList.add(new UserStatus(userStatus.getStatusFilename(),userStatus.getStatusId(),userStatus.getStatusType(),userStatus.getUserName()));
                    }
//                    int statusID = userStatus.getStatusId();
//                    String statusType = userStatus.getStatusType();
//                    String statusFileName = userStatus.getStatusFilename();
//                    String userName = userStatus.getUserName();
//                    int userID = userStatus.getUserId();
//
//                    Log.d("UsersStatus", "Status Id: "+statusID+
//                            " Status Type: "+statusType+
//                            " Status File name: "+statusFileName+
//                            " User Id: "+userID+
//                            " User Name: "+userName);
                }

                friendsStatusAdapter = new FriendsStatusAdapter(friendsStatusList);

                recyclerView.setAdapter(friendsStatusAdapter);

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
                Toast.makeText(getActivity(), "Can not connect to Server", Toast.LENGTH_SHORT).show();
                Log.d("Error", "" + t.getMessage());
            }
        });

    }

//    public void otherList()
//    {
//        getAllUsersStatus();
//    }
}
