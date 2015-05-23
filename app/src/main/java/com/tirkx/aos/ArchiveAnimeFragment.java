package com.tirkx.aos;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Vector;

/**
 * Created by Pakkapon on 21/5/2558.
 */

// use for display Archive (Completed) Anime

public class ArchiveAnimeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArchiveAnimeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArchiveAnimeAdapter.SlideIn mAdapterSlidein;
    Vector<ThreadAnimeDataSet> myDataset;
    private boolean prevNotEmpty;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.archive, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.archive_action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Hook search submit (Nothing right now)
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // Do search here
                SQLiteDatabase db = new AnimeListPreload(getActivity()).getReadableDatabase();
                Cursor cursor = db.query("ARCHIVE", new String[]{"AID", "NAME"}, "NAME LIKE ?", new String[]{"%"+s+"%"}, null, null, "NAME ASC");
                Vector<ThreadAnimeDataSet> dataset = new Vector<ThreadAnimeDataSet>();
                if (cursor.moveToFirst()) {
                    do {
                        ThreadAnimeDataSet a = new ThreadAnimeDataSet(
                            cursor.getString(cursor.getColumnIndex("AID")),
                            cursor.getString(cursor.getColumnIndex("NAME"))
                        );
                        dataset.add(a);
                    } while (cursor.moveToNext());
                } else {
                    dataset.add(new ThreadAnimeDataSet("","ไม่มีผลลัพธ์ที่ท่านต้องการ"));
                }
                mAdapter.addAll(dataset);
                mAdapterSlidein.notifyDataSetChanged();
                return true;
            }
        });
    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static ArchiveAnimeFragment newInstance(int sectionNumber) {
        ArchiveAnimeFragment fragment = new ArchiveAnimeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ArchiveAnimeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stickyanime, container, false);
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getActivity().getString(R.string.title_section2));
        this.setHasOptionsMenu(true);
        ((MainActivity) getActivity()).mTitle = getActivity().getString(R.string.title_section2);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (((MainActivity) getActivity()).isOnDrawerStateBack() || fm.getBackStackEntryCount() == 2) {
            ((MainActivity) getActivity()).setDrawerState(true);
        }
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new Vector<ThreadAnimeDataSet>();
        if (savedInstanceState != null && savedInstanceState.getParcelable("ArchiveAnime") != null) {
            myDataset = ((ParcelableThreadAnimeDataSet) savedInstanceState.getParcelable("ArchiveAnime")).get();
        } else {
            // load Anime data from sqlite and display to screen
            SQLiteDatabase db = new AnimeListPreload(getActivity()).getReadableDatabase();
            Cursor cursor = db.query("ARCHIVE", new String[]{"AID", "NAME"}, "1 = 1", null, null, null, "NAME ASC");
            if (cursor.moveToFirst()) {
                do {
                    ThreadAnimeDataSet a = new ThreadAnimeDataSet(
                            cursor.getString(cursor.getColumnIndex("AID")),
                            cursor.getString(cursor.getColumnIndex("NAME"))
                    );
                    myDataset.add(a);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(getActivity(),getString(R.string.preload_sqlite_fetchfail),Toast.LENGTH_LONG).show();
            }
        }
        mAdapter = new ArchiveAnimeAdapter(myDataset);
        mAdapterSlidein = new ArchiveAnimeAdapter.SlideIn(mAdapter);
        mRecyclerView.setAdapter(mAdapterSlidein);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(!mAdapter.get(position).getId().equals("")) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, ThreadListFragment.newInstance(mAdapter.get(position)))
                                    .addToBackStack("THREADSTACK")
                                    .commit();
                        }
                    }
                })
        );
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Integer i = 0;
        if (mAdapter != null) {
            Vector<ThreadAnimeDataSet> temp = mAdapter.getAll();
            outState.putParcelable("ArchiveAnime", new ParcelableThreadAnimeDataSet(temp));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}