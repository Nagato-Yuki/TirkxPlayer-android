package com.tirkx.aos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Vector;

/**
 * Created by Pakkapon on 18/5/2558.
 */

// use for pass VideoFileDataSet Over bundle object

public class ParcelableVideoFileDataSet implements Parcelable {
    Vector<VideoFileDataSet> vex = new Vector<VideoFileDataSet>();
    ParcelableVideoFileDataSet(){
        vex = new Vector<VideoFileDataSet>();
    }
    ParcelableVideoFileDataSet(Vector<VideoFileDataSet> a){
        vex = a;
    }
    public Vector<VideoFileDataSet> get(){
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
