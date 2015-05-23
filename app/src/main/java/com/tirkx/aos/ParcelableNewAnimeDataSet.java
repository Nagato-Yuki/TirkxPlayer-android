package com.tirkx.aos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Vector;

/**
 * Created by Pakkapon on 18/5/2558.
 */

// use for pass NewAnimeDataSet Over bundle object

public class ParcelableNewAnimeDataSet implements Parcelable {
    Vector<NewAnimeDataSet> vex = new Vector<NewAnimeDataSet>();
    ParcelableNewAnimeDataSet(){
        vex = new Vector<NewAnimeDataSet>();
    }
    ParcelableNewAnimeDataSet(Vector<NewAnimeDataSet> a){
        vex = a;
    }
    public Vector<NewAnimeDataSet> get(){
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
