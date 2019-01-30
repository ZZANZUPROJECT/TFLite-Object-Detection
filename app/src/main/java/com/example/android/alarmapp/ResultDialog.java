package com.example.android.alarmapp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.example.android.alarmapp.network.DrinkInfo;
import com.example.android.alarmapp.network.DrinkRequest;
import com.example.android.alarmapp.network.HttpServerConnection;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

public class ResultDialog extends Dialog{
    private TextToSpeech tts;

    private String result;
    private Context context;
    private TextView drinkNameTv, drinkContentTv;
    private String drinkName;


    public ResultDialog(@NonNull Context context) {
        super(context);
    }

    public ResultDialog(Context context, String result, TextToSpeech tts) {
        super(context);
        this.context = context;
        this.result = result;
        this.tts = tts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_dialog);

        drinkNameTv = findViewById(R.id.result_drink_name);
        drinkContentTv = findViewById(R.id.result_drink_content);

        sendRequestAndTTS(result);
    }

    private DrinkInfo sendRequestAndTTS(final String drinkName) {
        final String[] name = new String[1];
        final String[] content = { null };

        Retrofit retrofit = HttpServerConnection.getInstance();
        DrinkRequest drinkRequest = retrofit.create(DrinkRequest.class);
        Call<DrinkInfo> call = drinkRequest.getDrinkInfo(drinkName);

        call.enqueue(new Callback<DrinkInfo>() {
            @Override
            public void onResponse(Call<DrinkInfo> call, Response<DrinkInfo> response) {
                if(response.body() != null) {
                    drinkNameTv.setText(response.body().name);
                    drinkContentTv.setText(response.body().content);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech(response.body().name + "입니다.");
                    } else {
                        textToSpeechOld(response.body().name + "입니다.");
                    }
                } else {
                    drinkNameTv.setText("다시 한 번 인식시켜주세요");
                    drinkContentTv.setText("😢");

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech("다시 한 번 인식시켜주세요.");
                    } else {
                        textToSpeechOld("다시 한 번 인식시켜주세요.");
                    }
                }

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void textToSpeech(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        Log.d(TAG, "textToSpeech: speak!");
    }

    private void textToSpeechOld(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
        Log.d(TAG, "textToSpeech: under LOLLIPOP");
    }


}
