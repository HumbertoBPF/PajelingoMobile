package com.example.pajelingo.activities;

import static com.example.pajelingo.utils.Tools.isUserAuthenticated;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.adapters.GameAdapter;
import com.example.pajelingo.daos.GameDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.synchronization.ArticleSynchro;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TextView greetingTextView;
    private TextView warningNoResourcesTextView;
    private RecyclerView gamesRecyclerView;
    private FloatingActionButton searchButton;
    private SharedPreferences sp;
    private GameDao gameDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greetingTextView = findViewById(R.id.greeting_text_view);
        warningNoResourcesTextView = findViewById(R.id.warning_no_resources_text_view);
        gamesRecyclerView = findViewById(R.id.games_recycler_view);
        searchButton = findViewById(R.id.search_button);

        sp = getSharedPreferences(getString(R.string.sp_file_name),MODE_PRIVATE);

        gameDao = AppDatabase.getInstance(this).getGameDao();

        loadGames();

        searchButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
    }

    private void loadGames(){
        gameDao.getEnabledGamesTask(result -> {
            if (result.isEmpty()){
                warningNoResourcesTextView.setVisibility(View.VISIBLE);
            }else{
                warningNoResourcesTextView.setVisibility(View.GONE);
            }
            gamesRecyclerView.setAdapter(new GameAdapter(MainActivity.this, result));
        }).execute();
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
        }else if (selectedId == R.id.action_rankings){
            startActivity(new Intent(this, RankingActivity.class));
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
        downloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadGames();
            }
        });
        new ArticleSynchro(MainActivity.this, downloadDialog).execute();
    }

    private void swapConnexionMode() {
        if (!isUserAuthenticated(this)){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            SharedPreferences.Editor editor = sp.edit();
            // Remove user from Shared Preferences due to logout
            editor.remove(getString(R.string.username_sp));
            editor.remove(getString(R.string.password_sp));
            editor.apply();
            // Update menu layout
            onResume();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the app is in the online mode, show the option to pass to offline mode
        MenuItem onlineItem = menu.findItem(R.id.action_online);
        MenuItem rankingItem = menu.findItem(R.id.action_rankings);

        if (isUserAuthenticated(this)){
            onlineItem.setIcon(R.drawable.ic_online_mode);
            onlineItem.setTitle(R.string.tab_icon_online_mode_title);
            rankingItem.setVisible(true);
        }else{
            onlineItem.setIcon(R.drawable.ic_offline_mode);
            onlineItem.setTitle(R.string.tab_icon_offline_mode_title);
            rankingItem.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Greet user if it is logged in
        if (isUserAuthenticated(this)){
            String username = sp.getString(getString(R.string.username_sp), null);
            greetingTextView.setText(getString(R.string.greeting_text)+username);
        }else{
            greetingTextView.setText(null);
        }
        // Verify if it is necessary to change the layout of the menu(the online/offline mode icon)
        invalidateOptionsMenu();
    }
}