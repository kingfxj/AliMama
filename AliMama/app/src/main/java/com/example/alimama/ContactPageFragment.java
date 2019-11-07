package com.example.alimama;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import com.example.alimama.Controller.ContactPageAdapter;



public class ContactPageFragment extends Fragment {
    View view;

    private RecyclerView recyclerView;
    private ArrayList<String> contactDataList;
    private DatabaseUtil db;
    private String currParticipant;
    ContactPageAdapter contactPageAdapter;





    public ContactPageFragment(String currParticipant) {
        this.currParticipant = currParticipant;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.view_recycler,container,false);

        recyclerView = view.findViewById(R.id.my_recycler_view);
        this.contactDataList = new ArrayList<>();
        this.contactPageAdapter = new ContactPageAdapter(contactDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(contactPageAdapter);
        this.db = new DatabaseUtil();
        // hardcode for now will connect to Controller menu later



        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FriendPageActivity friendPageActivity = (FriendPageActivity)getContext();

        this.db.registerCurrentFriendsOfAParticipantRealTimeListener(this.currParticipant, friendPageActivity);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






    }


    public void setAdapterData(ArrayList<String> currentFriendsOfAParticipant) {
        this.contactDataList.clear();
        this.contactDataList.addAll(currentFriendsOfAParticipant);
    }

    public ContactPageAdapter getContactPageAdapter() {
        return this.contactPageAdapter;
    }
}