package com.example.card_project;


import android.widget.ScrollView;

public class MemberInfo {
    private String name;
    private String num1;
    private String num2;
    private String rank;
    private String address;
    private String date1;
    private String photoUrl;




    public MemberInfo(String name, String num1, String num2, String rank, String address, String date1, String photoUrl){
        this.name = name;
        this.num1 = num1;
        this.num2 = num2;
        this.rank = rank;
        this.address = address;
        this.date1 = date1;
        this.photoUrl = photoUrl;


    }

    public MemberInfo(String name, String num1, String num2, String rank, String address, String date1){
        this.name = name;
        this.num1 = num1;
        this.num2 = num2;
        this.rank = rank;
        this.address = address;
        this.date1 = date1;


    }




    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getNum1(){
        return num1;
    }
    public void setNum1(String num1){
        this.num1 = num1;
    }
    public String getNum2(){
        return num2;
    }
    public void setNum2(String num2){
        this.num2 = num2;
    }
    public String getRank(){
        return rank;
    }
    public void setRank(String rank){
        this.rank = rank;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getDate1(){
        return date1;
    }
    public void setDate1(String date1){
        this.date1 = date1;
    }
    public String getPhotoUrl(){
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }


}
