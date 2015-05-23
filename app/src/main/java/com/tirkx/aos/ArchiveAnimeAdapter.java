package com.tirkx.aos;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Vector;

import jp.wasabeef.recyclerview.animators.adapters.AnimationAdapter;


/**
 * Created by Pakkapon on 17/5/2558.
 */

// Adapter for ArchiveAnimeFragment

public class ArchiveAnimeAdapter extends RecyclerView.Adapter<ArchiveAnimeAdapter.ViewHolder> {

    private Context context;
    public Vector<ThreadAnimeDataSet> data;

    public void addAll(Vector<ThreadAnimeDataSet> a){
        data = a;
    }
    public Vector<ThreadAnimeDataSet> getAll(){
        return data;
    }
    public void add(ThreadAnimeDataSet item) {
        data.add(item);
    }
    public boolean addIfnotExist(int position,ThreadAnimeDataSet item){
        if(getItemCount()<=position) {
            data.add(position,item);
            return true;
        }else if(!data.get(position).getId().equals(item.getId())){
            data.add(position,item);
            return true;
        }else {
            return false;
        }
    }
    public ThreadAnimeDataSet get(int position){
        return data.get(position);
    }

    public ArchiveAnimeAdapter(Vector<ThreadAnimeDataSet> myDataset) {
        data = myDataset;
    }

    public ArchiveAnimeAdapter(Context c, Vector<ThreadAnimeDataSet> myDataset) {
        data = myDataset;
        context = c;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView infoTxt;
        public ViewHolder(View v) {
            super(v);
            infoTxt = (TextView)v.findViewById(R.id.info_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_threadanime, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.infoTxt.setText(data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class SlideIn extends AnimationAdapter {

        public SlideIn(RecyclerView.Adapter adapter) {
            super(adapter);
        }

        @Override
        protected Animator[] getAnimators(View view) {
            return new Animator[]{
                    ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)
            };
        }
    }
}
