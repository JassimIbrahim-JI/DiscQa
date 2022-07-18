package com.gitpro.discoverqa.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitpro.discoverqa.calls.FirestoreQueries;
import com.gitpro.discoverqa.R;
import com.gitpro.discoverqa.activities.AddTourActivity;
import com.gitpro.discoverqa.adapters.TourAdapter;
import com.gitpro.discoverqa.models.Tour;
import com.gitpro.discoverqa.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TopRatedTourFragment extends Fragment {

    List<Tour> mTours = new ArrayList<>();
    RecyclerView.LayoutManager mManger;
    RecyclerView mRecyclerView;
    FloatingActionButton addTourBtn;
    View mView;
    SwipeRefreshLayout swipeRefreshLayout;
    TourAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_top_rated_tour, container, false);

        firebase_connection();

        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.refresh_top_rated);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                firebase_connection();

            }
        });

        addTourBtn = mView.findViewById(R.id.add_tour_btn);
        FirestoreQueries.getUser(new FirestoreQueries.FirestoreUsersCallback() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCallback(User user) {
                if (user.isAdmin)
                    addTourBtn.show();
                else
                    addTourBtn.setVisibility(View.INVISIBLE);
            }
        });

        addTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTourActivity.class);
                startActivity(intent);
            }
        });



        return mView;
    }

    private void firebase_connection() {
        FirestoreQueries.getTours(new FirestoreQueries.FirestoreToursCallback() {
            @Override
            public void onCallback(List<Tour> tours) {
                mAdapter = null;
                mTours = tours;
                mAdapter = new TourAdapter(mTours);
                mRecyclerView = mView.findViewById(R.id.rv_top_rated);
                mManger = new LinearLayoutManager(mView.getContext());
                mRecyclerView.setLayoutManager(mManger);
                mRecyclerView.setAdapter(mAdapter);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

}
