package com.tirkx.aos;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Pakkapon on 22/5/2558.
 */

// Just Simple fragment for About screen

public class AboutFragment extends Fragment {

    public static AboutFragment newInstance(int sectionNumber) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putInt(AppConfig.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ((MainActivity)getActivity()).mTitle = getActivity().getString(R.string.title_section3);
        CardView github = (CardView)rootView.findViewById(R.id.about_github);
        CardView contributor = (CardView)rootView.findViewById(R.id.about_contributor);
        CardView opensourcelc = (CardView)rootView.findViewById(R.id.about_opensourcelc);
        CardView version = (CardView)rootView.findViewById(R.id.about_version);
        TextView versionname = (TextView)rootView.findViewById(R.id.about_version_name);
        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            versionname.setText("รุ่นแอป : "+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.tirkx.aos"));
                startActivity(i);
            }
        });
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.github.com/pureexe/TirkxPlayer-android"));
                startActivity(i);
            }
        });
        contributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.github.com/pureexe/TirkxPlayer-android/blob/master/CONTRIBUTOR.md"));
                startActivity(i);
            }
        });
        opensourcelc.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.github.com/pureexe/TirkxPlayer-android/blob/master/OPENSOURCE-LICENSE.md"));
                startActivity(i);
            }
        });
        return rootView;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(AppConfig.ARG_SECTION_NUMBER));
    }
}
