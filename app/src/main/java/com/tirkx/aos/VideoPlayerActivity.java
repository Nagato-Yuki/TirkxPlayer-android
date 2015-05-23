package com.tirkx.aos;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Pakkapon on 18/5/2558.
 */

// VideoPlayer Activity
// Known Bug : lose buffer when OnPause Call

public class VideoPlayerActivity extends ActionBarActivity {
    private VideoFileDataSet currentAnime;
    private String animeName;
    private int currentVideotTime = 0;
    private int endVideoTime = 0;
    private int lastVideoTime;
    private boolean isMoreThanKitkat;
    private boolean isVideoPlaying;
    private boolean isDisplayMediaController = true;
    private int displayMediaControllerCount = 0;
    private int mShortAnimTime = 0;
    private RelativeLayout controllZone;
    private RelativeLayout nameZone;
    private VideoView vidView;
    private TextView currentTime;
    private TextView endTime;
    private SeekBar seekBar;
    private ImageView loadingImage;
    private Thread backEndThread;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_videoplayer);
        TextView filename = (TextView)findViewById(R.id.videoplayer_filename);
        Bundle extra = getIntent().getExtras();
        currentAnime = new VideoFileDataSet(extra.getString("filename"),extra.getString("fansub"),extra.getString("lang"),extra.getString("link"));
        filename.setText(extra.getString("filename"));
        mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        vidView = (VideoView)findViewById(R.id.videoplayer_videoview);
        seekBar = (SeekBar)findViewById(R.id.videoplayer_seekbar);
        loadingImage = (ImageView)findViewById(R.id.videoplayer_imgloading);
        final ImageView playpauseControl = (ImageView)findViewById(R.id.videoplayer_playpause);
        currentTime = (TextView)findViewById(R.id.videoplayer_timecurrent);
        endTime = (TextView)findViewById(R.id.videoplayer_timeend);
        nameZone = (RelativeLayout)findViewById(R.id.videoplayer_namedisplay);
        controllZone = (RelativeLayout)findViewById(R.id.videoplayer_controller);
        final RelativeLayout rootZone = (RelativeLayout)findViewById(R.id.videoplayer_rootview);
        Uri vidUri = Uri.parse(Uri.encode(currentAnime.getLink(),"@#&=*+-_.,:!?()/~'%[]"));
        vidView.setVideoURI(vidUri);
        playpauseControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoPlaying) {
                    vidView.pause();
                    if (Build.VERSION.SDK_INT >= 22) {
                        playpauseControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp, getTheme()));
                    } else {
                        playpauseControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                    }
                } else {
                    vidView.start();
                    if (Build.VERSION.SDK_INT >= 22) {
                        playpauseControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp, getTheme()));
                    } else {
                        playpauseControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                    }
                }
                isVideoPlaying = !isVideoPlaying;
                displayMediaControllerCount = 0;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    displayMediaControllerCount = 0;
                    vidView.seekTo(progress * vidView.getDuration() / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        vidView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isVideoPlaying = false;
                if(Build.VERSION.SDK_INT>=22) {
                    playpauseControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp, getTheme()));
                }else{
                    playpauseControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                }
                showMediaController();
            }
        });
        if(Build.VERSION.SDK_INT>=17){
            isMoreThanKitkat = true;
        }else{
            isMoreThanKitkat = false;
        }
        if(isMoreThanKitkat) {
            vidView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            loadingImage.setVisibility(View.GONE);
                            return true;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                            loadingImage.setVisibility(View.VISIBLE);
                            return true;
                        }
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                            loadingImage.setVisibility(View.GONE);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        vidView.start();
        rootZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDisplayMediaController){
                    hideMediaController();
                }else{
                    showMediaController();
                }
            }
        });
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                endVideoTime = vidView.getDuration();
                endTime.setText(secToString((long) Math.floor(vidView.getDuration() / 1000)));
                if(Build.VERSION.SDK_INT>=22) {
                    playpauseControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp, getTheme()));
                }else{
                    playpauseControl.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                }
                isVideoPlaying = true;
                backEndThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (!isInterrupted()) {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        currentVideotTime = vidView.getCurrentPosition();
                                        if(isVideoPlaying&&isDisplayMediaController){
                                            if(displayMediaControllerCount==3){
                                                hideMediaController();
                                            }
                                            displayMediaControllerCount++;
                                        }
                                        if(isVideoPlaying&&lastVideoTime == currentVideotTime) {
                                            loadingImage.setVisibility(View.VISIBLE);
                                        }else{
                                            if(!isMoreThanKitkat&&loadingImage.getVisibility() == View.VISIBLE){
                                                loadingImage.setVisibility(View.GONE);
                                            }
                                            lastVideoTime = currentVideotTime;
                                        }
                                        currentTime.setText(secToString((long) Math.floor(vidView.getCurrentPosition() / 1000)));
                                        seekBar.setProgress((int) Math.floor((float) currentVideotTime / (float) vidView.getDuration() * 100));
                                        seekBar.setSecondaryProgress(vidView.getBufferPercentage());

                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                };

                backEndThread.start();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = 0;
        if(Build.VERSION.SDK_INT>=19) {
            uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }else{
            uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }
    private String secToString(long insec){
        long sec = insec;
        long hour =  (sec/3600);
        sec%=3600;
        long min = (sec/60);
        sec%=60;
        String Min,Sec;
        if(min<10){
            Min = "0"+min;
        }else{
            Min = "" + min;
        }
        if(sec<10){
            Sec = "0"+sec;
        }else{
            Sec = ""+sec;
        }
        if(hour>=1){
            return hour+":"+Min+":"+Sec;
        }else{
            return Min+":"+Sec;
        }
    }
    private void hideMediaController(){
        controllZone.animate()
                .translationY(controllZone.getHeight())
                .setDuration(mShortAnimTime);
        nameZone.animate()
                .translationY(nameZone.getHeight()*-1)
                .setDuration(mShortAnimTime);
        displayMediaControllerCount = 0;
        isDisplayMediaController = false;
    }
    private void showMediaController(){
        controllZone.animate()
                .translationY(0)
                .setDuration(mShortAnimTime);
        nameZone.animate()
                .translationY(0)
                .setDuration(mShortAnimTime);
        displayMediaControllerCount = 0;
        isDisplayMediaController = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent in = getIntent();
        if(in.getIntExtra("endVideoTime",-1)!=-1){
            endTime.setText(secToString((long) Math.floor(in.getIntExtra("endVideoTime",0)/1000)));
            currentTime.setText(secToString((long) Math.floor(in.getIntExtra("currentVideoTime", 0) / 1000)));
            vidView.start();
            vidView.seekTo(in.getIntExtra("currentVideoTime", 0));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent in = getIntent();
        in.putExtra("currentVideoTime", vidView.getCurrentPosition());
        in.putExtra("endVideoTime", vidView.getDuration());
        loadingImage.setVisibility(View.VISIBLE);
        seekBar.setSecondaryProgress(0);
        vidView.pause();
        if(backEndThread!=null) {
            backEndThread.interrupt();
        }
    }
}
