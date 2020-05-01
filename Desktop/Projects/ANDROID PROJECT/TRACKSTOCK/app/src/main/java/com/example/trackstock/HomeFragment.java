package com.example.trackstock;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView homeRecyclerView;
    HomeAdapter homeAdapter;
    ArrayList<MyItem> myItems;
    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        DatabaseHelper db = new DatabaseHelper(view.getContext());
        myItems = new ArrayList<>();
        Cursor cursor = db.getMyItem();
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                myItems.add(new MyItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getBlob(4)));
            } while (cursor.moveToNext());
        }
        homeAdapter = new HomeAdapter(myItems);
        homeRecyclerView = view.findViewById(R.id.home_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        homeRecyclerView.setLayoutManager(layoutManager);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setAdapter(homeAdapter);
        Toolbar toolbar = view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolabar_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                homeAdapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}
