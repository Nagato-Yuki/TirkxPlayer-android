package com.tirkx.aos;

/**
 * Created by Pakkapon on 23/5/2558.
 */

// Data Sturcter for NewAnimeFragment

public class NewAnimeDataSet {
    private String Aid;
    private String checksum;
    private String filename;
    private String lang;
    public NewAnimeDataSet(String aid,String fname,String sum,String l){
        Aid = aid;
        filename = fname;
        checksum = sum;
        l=lang;
    }
    public void setId(String id){
        Aid = id;
    }
    public void setChecksum(String check){
        checksum = check;
    }
    public void setFilename(String fname){
        filename = fname;
    }
    public void setLang(String l){
        lang = l;
    }
    public String getId(){
        return Aid;
    }
    public String getChecksum(){
        return checksum;
    }
    public String getFilename(){
        return filename;
    }
    public String getLang(){
        return lang;
    }
}
