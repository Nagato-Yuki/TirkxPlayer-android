package com.tirkx.aos;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

// Fragment for Display New Anime list

public class NewAnimeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NewAnimeAdapter mAdapter;
    private CircleProgressBar threadLoading;
    private Vector<NewAnimeDataSet> myDataset;

    public static NewAnimeFragment newInstance(int sectionNumber) {
        NewAnimeFragment fragment = new NewAnimeFragment();
        Bundle args = new Bundle();
        args.putInt(AppConfig.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
         View rootView = inflater.inflate(R.layout.fragment_newanime, container, false);
        ((MainActivity)getActivity()).mTitle = getActivity().getString(R.string.title_section4);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        threadLoading = (CircleProgressBar) rootView.findViewById(R.id.threadloading);
        myDataset = new Vector<NewAnimeDataSet>();
        if (savedInstanceState != null && savedInstanceState.getParcelable("NewAnime") != null) {
            myDataset = ((ParcelableNewAnimeDataSet) savedInstanceState.getParcelable("NewAnime")).get();
        }else{
            // Fetch from cache if exist
            SQLiteDatabase db = new CacheSQLiteHelper(getActivity()).getReadableDatabase();
            Cursor lastfetch = db.query("LASTFETCH",new String[]{"AID"},"FETCHTIME >= datetime('now','-1 hour') AND AID = ?",new String[]{AppConfig.NEWANIME_CACHEID},null,null,null);
            if(lastfetch.moveToFirst()) {
                Cursor cursor = db.query("NEWANIME", new String[]{"AID", "FILENAME", "CHECKSUM", "FILELANG"}, "1 = 1", null, null, null, "");
                if (cursor.moveToFirst()) {
                    do {
                        NewAnimeDataSet a = new NewAnimeDataSet(
                                cursor.getString(cursor.getColumnIndex("AID")),
                                cursor.getString(cursor.getColumnIndex("FILENAME")),
                                cursor.getString(cursor.getColumnIndex("CHECKSUM")),
                                cursor.getString(cursor.getColumnIndex("FILELANG"))
                        );
                        myDataset.add(a);
                    } while (cursor.moveToNext());
                }
            }else {
                threadLoading.setVisibility(View.VISIBLE);
                new LoadNewAnime().execute();
            }
            db.close();
        }
        mAdapter = new NewAnimeAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        new FindAnimeOnCheckSum().execute(mAdapter.get(position));
                    }
                })
        );
        return rootView;
    }

    // Load new Animelist from Tirkx
    private class LoadNewAnime extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            new CacheSQLiteHelper(getActivity()).getWritableDatabase().delete("NEWANIME", "1 = 1", null);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://forum.tirkx.com/main/tirkx_anime_list_home.php");
                URLConnection conn = url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    total.append(line);
                }
                String result = total.toString();
                if(getActivity()!=null&&result!=null){
                    String[] values = result.split("`");
                    SQLiteDatabase db = new CacheSQLiteHelper(getActivity()).getWritableDatabase();
                    for(int i=0;i<values.length;i++){
                        String[] currentThread = values[i].split("\\$");
                        mAdapter.add(new NewAnimeDataSet(currentThread[1],currentThread[2],currentThread[0],currentThread[3]));
                        ContentValues dbput = new ContentValues();
                        dbput.put("AID", Integer.parseInt(currentThread[1]));
                        dbput.put("FILENAME", currentThread[2]);
                        dbput.put("CHECKSUM", currentThread[0]);
                        dbput.put("FILELANG",currentThread[3]);
                        db.insert("NEWANIME",null,dbput);
                    }
                    ContentValues fetchtime = new ContentValues();
                    fetchtime.put("AID",AppConfig.NEWANIME_CACHEID);
                    fetchtime.put("FETCHTIME",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    db.insert("LASTFETCH", null, fetchtime);
                    db.close();
                }
                return total.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute(String result){
            if(result==null) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                }
            }
            mAdapter.notifyDataSetChanged();
            threadLoading.setVisibility(View.GONE);
        }
    }

    // find video link from anime id and md5
    private class FindAnimeOnCheckSum extends AsyncTask<NewAnimeDataSet, Void, VideoFileDataSet> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.GONE);
            threadLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected VideoFileDataSet doInBackground(NewAnimeDataSet... params) {
            try {
                URL url = new URL("http://forum.tirkx.com/main/tirkx_load_anime_per_topic.php?aid="+params[0].getId());
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Cookie", "bb_userid="+AppConfig.bb_userid+"; bb_password="+AppConfig.bb_password);
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    total.append(line);
                }
                JSONArray videolist = new JSONArray(total.toString());
                for(int i=0;i<videolist.length();i++){
                    JSONObject a = videolist.getJSONObject(i);
                    if(a.getString("md5_hash").equals(params[0].getChecksum())){
                        VideoFileDataSet b = new VideoFileDataSet(a.getString("fname"), a.getString("fansubName"), a.getString("type"), a.getString("phpbb_link"));
                        return b;
                    }
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute(VideoFileDataSet result){
            if(result == null){
                Toast.makeText(getActivity(),getString(R.string.file_not_found),Toast.LENGTH_LONG).show();
            }else{
                Intent VideoPlayerIntent = new Intent(getActivity(), VideoPlayerActivity.class);
                VideoPlayerIntent.putExtra("filename",result.getFilename());
                VideoPlayerIntent.putExtra("fansub",result.getFansub());
                VideoPlayerIntent.putExtra("link", result.getLink());
                VideoPlayerIntent.putExtra("lang", result.getLang());
                getActivity().startActivity(VideoPlayerIntent);
            }
            threadLoading.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Integer i = 0;
        if(mAdapter!=null) {
            Vector<NewAnimeDataSet> temp = mAdapter.getAll();
            outState.putParcelable("NewAnime", new ParcelableNewAnimeDataSet(temp));
        }
    }

}
