package com.example.pajelingo.activities;

import static com.example.pajelingo.utils.SharedPreferences.deleteUserData;
import static com.example.pajelingo.utils.SharedPreferences.isUserAuthenticated;
import static com.example.pajelingo.utils.Synchronization.launchResourcesSync;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.account.LoginActivity;
import com.example.pajelingo.activities.dictionary.DictionaryActivity;
import com.example.pajelingo.adapters.GameAdapter;
import com.example.pajelingo.daos.GameDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TextView greetingTextView;
    private ConstraintLayout noDataWarning;
    private RecyclerView gamesRecyclerView;
    private FloatingActionButton searchButton;
    private SharedPreferences sp;

    private final ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> launchResourcesSync(this, getLayoutInflater(), this::loadGames));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greetingTextView = findViewById(R.id.greeting_text_view);
        noDataWarning = findViewById(R.id.no_data_constraint_layout);
        gamesRecyclerView = findViewById(R.id.games_recycler_view);
        searchButton = findViewById(R.id.search_button);

        sp = getSharedPreferences(getString(R.string.sp_file_name), MODE_PRIVATE);

        searchButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DictionaryActivity.class)));
    }

    private void loadGames() {
        GameDao gameDao = AppDatabase.getInstance(this).getGameDao();

        gameDao.getEnabledGames(result -> {
            if (result.isEmpty()) {
                noDataWarning.setVisibility(View.VISIBLE);
            } else {
                noDataWarning.setVisibility(View.GONE);
            }
            gamesRecyclerView.setAdapter(new GameAdapter(MainActivity.this, result));
        });
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
        } else if (selectedId == R.id.action_menu) {
            startActivity(new Intent(this, MenuActivity.class));
        } else if (selectedId == R.id.action_login_logout) {
            swapConnexionMode();
        }

        return super.onOptionsItemSelected(item);
    }

    private void askConfirmationSynchroResources() {
        // Ask user's confirmation before launching a synchro
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_confirm_resources_sync_title)
                .setMessage(R.string.dialog_confirm_resources_sync_message)
                .setPositiveButton(R.string.dialog_confirm_resources_sync_confirm, (dialogInterface, id) -> {
                    // When the user confirms, launches the synchro steps by using a chain of responsibility design pattern
                    startResourcesSync();
                })
                .setNegativeButton(R.string.dialog_confirm_resources_sync_decline, (dialog, which) -> dialog.dismiss());
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }

    private void swapConnexionMode() {
        if (!isUserAuthenticated(this)) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            deleteUserData(this);
            // Update menu layout
            onResume();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the app is in the online mode, show the option to pass to offline mode
        MenuItem onlineItem = menu.findItem(R.id.action_login_logout);

        if (!isUserAuthenticated(this)) {
            onlineItem.setIcon(R.drawable.ic_login);
            onlineItem.setTitle(R.string.tab_icon_login_title);
        } else {
            onlineItem.setIcon(R.drawable.ic_logout);
            onlineItem.setTitle(R.string.tab_icon_logout_title);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGames();
        // Greet user if it is logged in
        if (isUserAuthenticated(this)) {
            greetingTextView.setVisibility(View.VISIBLE);
            String username = sp.getString(getString(R.string.username_sp), null);
            greetingTextView.setText(getString(R.string.greeting_text, username));
        } else {
            greetingTextView.setVisibility(View.GONE);
            greetingTextView.setText(null);
        }
        // Verify if it is necessary to change the layout of the menu(the online/offline mode icon)
        invalidateOptionsMenu();
    }

    private void startResourcesSync() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            launchResourcesSync(MainActivity.this, getLayoutInflater(), this::loadGames);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}