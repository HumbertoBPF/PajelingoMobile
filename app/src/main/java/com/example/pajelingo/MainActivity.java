package com.example.pajelingo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.AsyncTasks.CategoryTask;

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
            builder.setMessage(R.string.dialog_download_resources)
                    .setPositiveButton(R.string.dialog_download_resources_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            AlertDialog downloadDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("Preparing your download").create();
                            downloadDialog.show();
                            new CategoryTask(getApplicationContext(), downloadDialog).execute();
                        }
                    });
            AlertDialog confirmationDialog = builder.create();
            confirmationDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}