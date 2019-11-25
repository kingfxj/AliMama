package com.example.alimama.friendOperation.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import com.example.alimama.R;


/**
 * @author Zi Xuan Zhang
 * ContactPage tab's Fragment page.
 * Set up data for each contact card
 * For viewing.
 *
 * */
public class ContactPageFragment extends Fragment implements ContactPageContract.ContactPageView {


    private ArrayList<String> contactDataList;
    private ContactPagePresenter mContactPagePresenter;

    private ContactPageAdapter contactPageAdapter;
    /**
     * Constructor for fragment
     * @param currentParticipant
     *
     * */
    public ContactPageFragment(String currentParticipant) {
        this.mContactPagePresenter = new ContactPagePresenter(this);
        this.mContactPagePresenter.setCurrentLoggedInParticipant(currentParticipant);

    }
    /**
     * Set up view in fragment
     * */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recycler,container,false);

        RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view);
        this.contactDataList = new ArrayList<>();
        this.contactPageAdapter = new ContactPageAdapter(contactDataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(contactPageAdapter);
        this.mContactPagePresenter.registerCurrentFriendsOfAParticipantRealTimeListener();
        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * set Adapter for contactList.
     * add All of the friends to contactDataList
     * @param currentFriendsOfAParticipant
     * */

    @Override
    public void setAdapterData(ArrayList<String> currentFriendsOfAParticipant) {
        this.contactDataList.clear();
        this.contactDataList.addAll(currentFriendsOfAParticipant);
        this.contactPageAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayExistingFriendsRetrievalErrorMessage(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Get contact page adapter
     * @return ContactPageAdapter
     * */

    public ContactPageAdapter getContactPageAdapter() {
        return this.contactPageAdapter;
    }
}