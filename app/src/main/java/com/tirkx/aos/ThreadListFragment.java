package com.tirkx.aos;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by Pakkapon on 17/5/2558.
 */

// use for This play Video Filelist From Anime ID

public class ThreadListFragment extends Fragment {
    private  ThreadAnimeDataSet currentAnime;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private Vector<VideoFileDataSet> myDataset;
    private ThreadListAdapter mAdapter;
    private ThreadListSectionHeader mSectionedAdapter;
    private CircleProgressBar threadLoading;
    private int durationTime;

    public static ThreadListFragment newInstance(ThreadAnimeDataSet t) {
        ThreadListFragment fragment = new ThreadListFragment();
        Bundle args = new Bundle();
        Vector<ThreadAnimeDataSet> ax = new Vector<ThreadAnimeDataSet>();
        ax.add(t);
        args.putParcelable("ThreadAnimeDataset",new ParcelableThreadAnimeDataSet(ax));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ParcelableThreadAnimeDataSet xcurrentAnime = (getArguments().getParcelable("ThreadAnimeDataset"));
        currentAnime = xcurrentAnime.get().get(0);
    }

    public ThreadListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_threadlist, container, false);
            ActionBar actionBar =((ActionBarActivity)getActivity()).getSupportActionBar();
            ((MainActivity)getActivity()).setDrawerState(false);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.threadlist);
            threadLoading = (CircleProgressBar) rootView.findViewById(R.id.threadloading);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            durationTime = getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);

            myDataset = new Vector<VideoFileDataSet>();
            if(savedInstanceState != null){
                myDataset = ((ParcelableVideoFileDataSet)savedInstanceState.getParcelable("ThreadList")).get();
                currentAnime = ((ParcelableThreadAnimeDataSet)savedInstanceState.getParcelable("CurrentAnime")).get().get(0);
            } else {
                    SQLiteDatabase db = new CacheSQLiteHelper(getActivity()).getReadableDatabase();
                    Cursor lastfetch = db.query("LASTFETCH",new String[]{"AID"},"FETCHTIME >= datetime('now','-1 day') AND AID = ?",new String[]{currentAnime.getId()},null,null,null);
                    if(lastfetch.moveToFirst()) {
                        Cursor cursor = db.query("THREADLIST", new String[]{"FILENAME", "FANSUB", "FILELANG", "FILELINK"}, "AID = ?", new String[]{currentAnime.getId()}, null, null, "FANSUB ASC");
                        if (cursor.moveToFirst()) {
                            do {
                                VideoFileDataSet a = new VideoFileDataSet(
                                        cursor.getString(cursor.getColumnIndex("FILENAME")),
                                        cursor.getString(cursor.getColumnIndex("FANSUB")),
                                        cursor.getString(cursor.getColumnIndex("FILELANG")),
                                        cursor.getString(cursor.getColumnIndex("FILELINK"))
                                );
                                myDataset.add(a);
                            } while (cursor.moveToNext());
                        }
                    }else {
                        threadLoading.setVisibility(View.VISIBLE);
                        new LoadRest().execute("http://forum.tirkx.com/main/tirkx_load_anime_per_topic.php?aid=" + currentAnime.getId());
                    }
                    db.close();
            }
            ((MainActivity)getActivity()).mTitle = currentAnime.getName();
            actionBar.setTitle(currentAnime.getName());
            List<ThreadListSectionHeader.Section> sections =
                new ArrayList<ThreadListSectionHeader.Section>();
            if(myDataset.size()>0){
                String prevfs = myDataset.get(0).getFansub();
                sections.add(new ThreadListSectionHeader.Section(0,prevfs));
                for(int i=1;i<myDataset.size();i++){
                    if(!prevfs.equals(myDataset.get(i).getFansub())){
                        prevfs = myDataset.get(i).getFansub();
                        sections.add(new ThreadListSectionHeader.Section(i,prevfs));
                    }
                }
            }
            ThreadListSectionHeader.Section[] dummy = new ThreadListSectionHeader.Section[sections.size()];
            mAdapter = new ThreadListAdapter(getActivity(),myDataset);
            mSectionedAdapter = new ThreadListSectionHeader(getActivity(),R.layout.section_text,R.id.section_text,new ThreadListAdapter.SlideIn(mAdapter));
            mSectionedAdapter.setSections(sections.toArray(dummy));

            mRecyclerView.setAdapter(mSectionedAdapter);

        return rootView;
    }

    private class LoadRest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Cookie", "bb_userid="+AppConfig.bb_userid+"; bb_password="+AppConfig.bb_password);
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    total.append(line);
                }
                String result = total.toString();
                JSONArray videolist = new JSONArray(result);
                boolean isNotHaveLink = false;
                for (int i = 0; i < videolist.length(); i++) {
                    JSONObject a = videolist.getJSONObject(i);
                    if (!a.getString("phpbb_link").equals("")) {
                        VideoFileDataSet b = new VideoFileDataSet(a.getString("fname"), a.getString("fansubName"), a.getString("type"), a.getString("phpbb_link"));
                        mAdapter.addIfnotExist(i, b);
                    } else {
                        isNotHaveLink = true;
                    }
                }
                Vector<VideoFileDataSet> a = mAdapter.getAll();
                Collections.sort(a, new Comparator<VideoFileDataSet>() {
                    @Override
                    public int compare(VideoFileDataSet o1, VideoFileDataSet o2) {
                        return o1.getFansub().compareTo(o2.getFansub());
                    }
                });
                mAdapter.addAll(a);
                if (isNotHaveLink && getActivity() != null) {
                    DialogManager.authenError(getActivity(), (MainActivity) getActivity());
                }
                if (mAdapter.getItemCount() == 0) {
                    mAdapter.add(new VideoFileDataSet(getString(R.string.file_anime_not_found),"", "", ""));
                } else {
                    if (getActivity() != null) {
                        SQLiteDatabase db = new CacheSQLiteHelper(getActivity()).getWritableDatabase();
                        db.delete("THREADLIST", "AID = " + currentAnime.getId(), null);
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            ContentValues values = new ContentValues();
                            values.put("AID", currentAnime.getId());
                            values.put("FANSUB", mAdapter.get(i).getFansub());
                            values.put("FILENAME", mAdapter.get(i).getFilename());
                            values.put("FILELANG", mAdapter.get(i).getLang());
                            values.put("FILELINK", mAdapter.get(i).getLink());
                            db.insert("THREADLIST", null, values);
                        }
                        ContentValues fetchtime = new ContentValues();
                        fetchtime.put("AID",currentAnime.getId());
                        fetchtime.put("FETCHTIME",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        db.insert("LASTFETCH", null, fetchtime);
                        db.close();
                    }
                }
                List<ThreadListSectionHeader.Section> sections =
                        new ArrayList<ThreadListSectionHeader.Section>();
                String prevfs = myDataset.get(0).getFansub();
                if(!prevfs.equals("")) {
                    sections.add(new ThreadListSectionHeader.Section(0, prevfs));
                }
                for(int i=1;i<myDataset.size();i++){
                    if(!prevfs.equals(myDataset.get(i).getFansub())){
                        prevfs = myDataset.get(i).getFansub();
                        sections.add(new ThreadListSectionHeader.Section(i,prevfs));
                    }
                }
                ThreadListSectionHeader.Section[] dummy = new ThreadListSectionHeader.Section[sections.size()];
                mSectionedAdapter.setSections(sections.toArray(dummy));
                return total.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute(String result){
            if(result==null) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                }
            }else{
                threadLoading.animate().scaleX(0).scaleY(0).setDuration(durationTime);
                mSectionedAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Integer i = 0;
        if(mAdapter!=null) {
            Vector<VideoFileDataSet> temp = mAdapter.getAll();
            outState.putParcelable("ThreadList", new ParcelableVideoFileDataSet(temp));
            Vector<ThreadAnimeDataSet> a = new Vector<ThreadAnimeDataSet>();
            a.add(currentAnime);
            outState.putParcelable("CurrentAnime", new ParcelableThreadAnimeDataSet(a));
        }
    }
}
