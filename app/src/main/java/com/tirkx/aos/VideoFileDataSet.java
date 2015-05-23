package com.tirkx.aos;

/**
 * Created by Pakkapon on 18/5/2558.
 */

// Data Structer for VideoFile

public class VideoFileDataSet {
    private String filename ="";
    private String lang = "";
    private String fansub = "";
    private String link = "";
    public VideoFileDataSet(){

    }
    public VideoFileDataSet(String fn,String fs,String ln,String lin){
        lang = ln;
        filename = fn;
        fansub = fs;
        link = lin;
    }
    public void setLang(String l){
        lang = l;
    }
    public void setFilename(String f){
        filename = f;
    }
    public void setFansub(String f){
        fansub = f;
    }
    public void setLink(String lin){
        link = lin;
    }
    public String getLang(){
        return lang;
    }
    public String getFilename(){
        return filename;
    }
    public String getFansub(){
        return fansub;
    }
    public String getLink(){
        return link;
    }
}
