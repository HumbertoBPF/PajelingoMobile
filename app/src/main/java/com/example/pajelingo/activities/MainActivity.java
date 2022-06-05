package com.example.pajelingo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
        int selectedId = item.getItemId();
        // Launch synchronization
        if (selectedId == R.id.action_synchro) {
            askConfirmationSynchroResources();
        }else if (selectedId == R.id.action_search){
            startActivity(new Intent(this, SearchActivity.class));
        }else if (selectedId == R.id.action_online){
            swapConnexionMode();
        }

        return super.onOptionsItemSelected(item);
    }

    private void askConfirmationSynchroResources() {
        // Ask user's confirmation before launching a synchro
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_download_resources_title)
                .setMessage(R.string.dialog_download_resources_message)
                .setPositiveButton(R.string.dialog_download_resources_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        launchSynchroResources();
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
    }

    private void launchSynchroResources() {
        // When the user confirms, launches the synchro steps by using a chain of responsibility design pattern
        AlertDialog downloadDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.progress_download_title).setMessage(R.string.progress_download_initial_msg)
                .setCancelable(false).create();
        downloadDialog.show();
        new ArticleSynchro(MainActivity.this, downloadDialog).execute();
    }

    private void swapConnexionMode() {
        // Swap between online and offline mode
        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name),MODE_PRIVATE);

        boolean isOnlineMode = sp.getBoolean(getString(R.string.is_online_mode_sp), false);

        if (!isOnlineMode){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(getString(R.string.is_online_mode_sp), false);
            editor.apply();
            // Update menu layout
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the app is in the online mode, show the option to pass to offline mode
        SharedPreferences sp = getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        MenuItem onlineItem = menu.findItem(R.id.action_online);
        if (sp.getBoolean(getString(R.string.is_online_mode_sp), false)){
            onlineItem.setIcon(R.drawable.ic_online_mode);
            onlineItem.setTitle(R.string.tab_icon_online_mode_title);
        }else{
            onlineItem.setIcon(R.drawable.ic_offline_mode);
            onlineItem.setTitle(R.string.tab_icon_offline_mode_title);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verify if it is necessary to change the layout of the menu(the online/offline mode icon)
        invalidateOptionsMenu();
    }
}