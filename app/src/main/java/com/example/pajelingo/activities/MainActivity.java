package com.example.pajelingo.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.GamesRecyclerView;
import com.example.pajelingo.synchronization.ArticleSynchro;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView gamesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gamesRecyclerView = findViewById(R.id.games_recycler_view);
        List<String> games = new ArrayList<>();
        games.add("Vocabulary Game");
        games.add("Article Game");
        games.add("Conjugation Game");

        gamesRecyclerView.setAdapter(new GamesRecyclerView(this, games));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_synchro) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_download_resources_title)
                    .setMessage(R.string.dialog_download_resources_message)
                    .setPositiveButton(R.string.dialog_download_resources_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AlertDialog downloadDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(R.string.progress_download_title).setMessage(R.string.progress_download_initial_msg)
                                    .setCancelable(false).create();
                            downloadDialog.show();
                            new ArticleSynchro(MainActivity.this, downloadDialog).execute();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.dialog_download_resources_decline, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog confirmationDialog = builder.create();
            confirmationDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}