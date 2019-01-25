package com.example.android.alarmapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.example.android.alarmapp.network.DrinkInfo;
import com.example.android.alarmapp.network.DrinkRequest;
import com.example.android.alarmapp.network.HttpServerConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ResultDialog extends Dialog{
    private String result;
    private TextView drinkNameTv, drinkContentTv;
    private String mDrinkName;
    private String mDrinkContent;

    public ResultDialog(@NonNull Context context) {
        super(context);
    }

    public ResultDialog(Context context, String result) {
        super(context);
        this.result = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_dialog);

        drinkNameTv = findViewById(R.id.result_drink_name);
        drinkContentTv = findViewById(R.id.result_drink_content);

        sendRequest(result);
    }

    private DrinkInfo sendRequest(String drinkName) {
        final String[] name = new String[1];
        final String[] content = { null };

        Retrofit retrofit = HttpServerConnection.getInstance();
        DrinkRequest drinkRequest = retrofit.create(DrinkRequest.class);
        Call<DrinkInfo> call = drinkRequest.getDrinkInfo(drinkName);

        call.enqueue(new Callback<DrinkInfo>() {
            @Override
            public void onResponse(Call<DrinkInfo> call, Response<DrinkInfo> response) {
                drinkNameTv.setText(response.body().name);
                drinkContentTv.setText(response.body().content);
            }

            @Override
            public void onFailure(Call<DrinkInfo> call, Throwable t) {
                drinkNameTv.setText("음료 이름을 받아오지 못했습니다.");
                drinkContentTv.setText("음료 성분을 받아오지 못했습니다.");
            }
        });

        DrinkInfo result = new DrinkInfo(name[0], content[0]);

        return result;
    }
}
