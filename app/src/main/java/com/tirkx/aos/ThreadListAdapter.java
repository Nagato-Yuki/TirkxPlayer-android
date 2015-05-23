package com.tirkx.aos;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Vector;

import jp.wasabeef.recyclerview.animators.adapters.AnimationAdapter;

/**
 * Created by Pakkapon on 18/5/2558.
 */

// Adapter for ThreadListFragmnet

public class ThreadListAdapter extends RecyclerView.Adapter<ThreadListAdapter.ViewHolder>{

    private Context context;
    public Vector<VideoFileDataSet> data;

    public void addAll(Vector<VideoFileDataSet> a){
        data = a;
    }
    public Vector<VideoFileDataSet> getAll(){
        return data;
    }
    public void add(VideoFileDataSet item) {
        data.add(item);
    }
    public boolean addIfnotExist(int position,VideoFileDataSet item) {
        if(getItemCount()<=position) {
            data.add(position,item);
            return true;
        }else if(!data.get(position).getLink().equals(item.getLink())) {
            data.add(position,item);
            return true;
        }else{
            return false;
        }
    }
    public VideoFileDataSet get(int position){
        return data.get(position);
    }

    public ThreadListAdapter(Context c, Vector<VideoFileDataSet> myDataset) {
        data = myDataset;
        context = c;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        CardView cardView;
        public ViewHolder(View v) {
            super(v);
            cardView = (CardView)v.findViewById(R.id.card_threadlist);
            fileName = (TextView)v.findViewById(R.id.threadlist_filename);
       }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_threadlist, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.fileName.setText(data.get(position).getFilename());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(get(position).getLink().equals("")){
                    DialogManager.authenError(context,(MainActivity)context);
                }else {
                    Intent VideoPlayerIntent = new Intent(context, VideoPlayerActivity.class);
                    VideoPlayerIntent.putExtra("filename",get(position).getFilename());
                    VideoPlayerIntent.putExtra("fansub",get(position).getFansub());
                    VideoPlayerIntent.putExtra("link", get(position).getLink());
                    VideoPlayerIntent.putExtra("lang", get(position).getLang());
                    context.startActivity(VideoPlayerIntent);
                }
            }
        });
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
