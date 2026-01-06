package com.example.pocketmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private LinearLayout startupLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startupLayout = findViewById(R.id.startupLayout);
        toolbar = findViewById(R.id.toolbar);
        Button btnStart = findViewById(R.id.btnStart);

        // Check if views are found
        if (startupLayout == null || toolbar == null || btnStart == null) {
            // Log error and finish if critical views are missing
            android.util.Log.e("MainActivity", "Critical views not found in layout");
            finish();
            return;
        }

        setSupportActionBar(toolbar);

        // Handle back button press - compatible with Android 11 and newer
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });

        btnStart.setOnClickListener(v -> {
            startupLayout.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

            showBack(false);
            loadFragment(new ExpenseListFragment());
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();

        showBack(true);
    }

    public void showBack(boolean show) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(show);
            getSupportActionBar().setDisplayShowHomeEnabled(show);
        }
    }

    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        } else if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
