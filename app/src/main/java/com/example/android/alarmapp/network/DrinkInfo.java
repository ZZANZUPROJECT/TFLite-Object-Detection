package com.example.android.alarmapp.network;

import com.google.gson.annotations.SerializedName;

public class DrinkInfo {
    @SerializedName("name")
    public String name;

    @SerializedName("content")
    public String content;

    public DrinkInfo(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
