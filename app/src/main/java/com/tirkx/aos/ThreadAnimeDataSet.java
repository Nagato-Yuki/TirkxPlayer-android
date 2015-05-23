package com.tirkx.aos;

/**
 * Created by Pakkapon on 17/5/2558.
 */

// DataSet For ThreadListFragment

public class ThreadAnimeDataSet {
    String id,name;
    ThreadAnimeDataSet(){

    }
    ThreadAnimeDataSet(String i,String n){
        id = i;
        name = n;
    }
    public String getName(){
        return name;
    }
    public String getId(){
        return id;
    }
    public void setName(String n){
        name = n;
    }
    public void setId(String i){
        id = i;
    }
}
