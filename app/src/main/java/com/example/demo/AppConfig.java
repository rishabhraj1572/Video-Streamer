package com.example.demo;

public class AppConfig {
    //fetching jsonURL and latest app update URL from the response of these pages
    public static final String jsonURLFetch = ""; //fetch json url
    public static final String updateURLFetch = "";  //fetch latest app download url


    //check weather will will start normally or do maintenance activity or do the update activity
    //if response is 1 then it will start normally
    //if response is 2 then it will start the maintenance activity
    //if url is not accessible then it will start update activity
    public static final String startURL = "";
    
}
