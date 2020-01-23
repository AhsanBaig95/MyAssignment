package com.example.myassignment.Adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myassignment.Model.UserStatus;
import com.example.myassignment.R;

import java.util.ArrayList;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {

    private ArrayList<UserStatus> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, statusFileName;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.textView_StatusType);
            statusFileName = (TextView) view.findViewById(R.id.textView_StatusFileName);

        }
    }


    public StatusAdapter(ArrayList<UserStatus> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserStatus userStatus = moviesList.get(position);
        holder.userName.setText(userStatus.getStatusType());
        holder.statusFileName.setText(userStatus.getStatusFilename());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
