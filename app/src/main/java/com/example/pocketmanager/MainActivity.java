package com.example.pocketmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

        setSupportActionBar(toolbar);

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
