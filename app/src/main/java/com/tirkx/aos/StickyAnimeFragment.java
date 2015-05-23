package com.tirkx.aos;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

/**
 * Created by Pakkapon on 17/5/2558.
 */

// use for Display pinned anime from tirkx (It is preload data)

public class StickyAnimeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private StickyAnimeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private StickyAnimeAdapter.SlideIn mAdapterSlidein;
    Vector<ThreadAnimeDataSet> myDataset;

    public static StickyAnimeFragment newInstance(int sectionNumber) {
        StickyAnimeFragment fragment = new StickyAnimeFragment();
        Bundle args = new Bundle();
        args.putInt(AppConfig.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StickyAnimeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stickyanime, container, false);
        ActionBar actionBar =((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(getActivity().getString(R.string.title_section1));
        ((MainActivity)getActivity()).mTitle = getActivity().getString(R.string.title_section1);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if(((MainActivity)getActivity()).isOnDrawerStateBack()||fm.getBackStackEntryCount()==2){
            ((MainActivity)getActivity()).setDrawerState(true);
        }
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(getActivity(),2);
        }else{
            mLayoutManager = new GridLayoutManager(getActivity(),1);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new Vector<ThreadAnimeDataSet>();
        if(savedInstanceState != null&&savedInstanceState.getParcelable("StickyAnime")!=null){
           myDataset = ((ParcelableThreadAnimeDataSet)savedInstanceState.getParcelable("StickyAnime")).get();
        } else {
            SQLiteDatabase db = new AnimeListPreload(getActivity()).getReadableDatabase();
            Cursor cursor = db.query("STICKY",new String[]{"AID","NAME"},"1 = 1",null,null,null,"NAME ASC");
            if(cursor.moveToFirst()) {
                do{
                    ThreadAnimeDataSet a = new ThreadAnimeDataSet(
                            cursor.getString(cursor.getColumnIndex("AID")),
                            cursor.getString(cursor.getColumnIndex("NAME"))
                    );
                    myDataset.add(a);
                }while(cursor.moveToNext());
            }else{

            }
            db.close();
        }
        mAdapter = new StickyAnimeAdapter(getActivity(),myDataset);
        mAdapterSlidein = new StickyAnimeAdapter.SlideIn(mAdapter);
        mRecyclerView.setAdapter(mAdapterSlidein);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, ThreadListFragment.newInstance(mAdapter.get(position)))
                                .addToBackStack("THREADSTACK")
                                .commit();
                    }
                })
        );
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Integer i = 0;
        if(mAdapter!=null) {
            Vector<ThreadAnimeDataSet> temp = mAdapter.getAll();
            outState.putParcelable("StickyAnime", new ParcelableThreadAnimeDataSet(temp));
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(AppConfig.ARG_SECTION_NUMBER));
    }
}
