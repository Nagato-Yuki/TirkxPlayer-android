package com.tirkx.aos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Vector;

/**
 * Created by Pakkapon on 18/5/2558.
 */

// use for pass ThreadAnimeDataSet Over bundle object

public class ParcelableThreadAnimeDataSet implements Parcelable {
    Vector<ThreadAnimeDataSet> vex = new Vector<ThreadAnimeDataSet>();
    ParcelableThreadAnimeDataSet(){
        vex = new Vector<ThreadAnimeDataSet>();
    }
    ParcelableThreadAnimeDataSet(Vector<ThreadAnimeDataSet> a){
        vex = a;
    }
    public Vector<ThreadAnimeDataSet> get(){
        return vex;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
