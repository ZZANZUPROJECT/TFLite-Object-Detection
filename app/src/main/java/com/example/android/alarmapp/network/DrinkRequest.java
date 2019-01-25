package com.example.android.alarmapp.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DrinkRequest {
    @GET("{drink}")
    Call<DrinkInfo> getDrinkInfo(@Path("drink") String drink);
}
