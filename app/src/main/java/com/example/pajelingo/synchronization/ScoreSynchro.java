package com.example.pajelingo.synchronization;

import static com.example.pajelingo.utils.Tools.getAuthToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreSynchro {

    private final Context context;
    private final String username;
    private final String password;
    private final OnResultListener<List<Score>> onResultListener;
    private final Handler handler = new Handler();
    private AlertDialog dialog;
    private final SharedPreferences sp;
    private final DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT, Locale.ENGLISH);

    public ScoreSynchro(Context context, String username, String password, OnResultListener<List<Score>> onResultListener) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.onResultListener = onResultListener;
        this.sp = context.getSharedPreferences(context.getString(R.string.sp_file_name), Context.MODE_PRIVATE);

    }

    public void execute(){
        dialog = new AlertDialog.Builder(context).setTitle(R.string.update_ranking_dialog_title)
                .setMessage(R.string.update_ranking_dialog_message).setCancelable(false).create();
        dialog.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                launchSynchro();
            }
        }, 2000);
    }

    private void launchSynchro() {
        Call<List<Score>> call = LanguageSchoolAPIHelper.getApiObject().getScores(getAuthToken(username, password));
        call.enqueue(new Callback<List<Score>>() {
            @Override
            public void onResponse(Call<List<Score>> call, Response<List<Score>> response) {
                if (response.isSuccessful()){
                    List<Score> scores = response.body();
                    ScoreDao scoreDao = AppDatabase.getInstance(context).getScoreDao();

                    scoreDao.getSaveAsyncTask(scores, new OnResultListener<List<Score>>() {
                        @Override
                        public void onResult(List<Score> result) {
                            setLastSynchroDate();
                            dialog.dismiss();
                            onResultListener.onResult(result);
                        }
                    }).execute();
                }else{
                    if (response.code() == 401){
                        dialog.setMessage(context.getString(R.string.warning_invalid_credientials));
                    }else{
                        dialog.setMessage(context.getString(R.string.warning_fail_login));
                    }
                    dismissDialogDelayed();
                }
            }

            @Override
            public void onFailure(Call<List<Score>> call, Throwable t) {
                dialog.setMessage(context.getString(R.string.warning_fail_login));
                dismissDialogDelayed();
            }
        });
    }

    private void setLastSynchroDate() {
        SharedPreferences.Editor editor = sp.edit();
        Date currentTime = Calendar.getInstance().getTime();
        editor.putString(context.getString(R.string.last_score_synchro_sp), formatter.format(currentTime));
        editor.apply();
    }

    private void dismissDialogDelayed(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },2000);
    }

}
