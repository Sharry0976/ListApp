package com.example.listapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.listapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    static ArrayList<String> notes=new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    static Set<String> set;

    private static final int EDIT_NOTE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ListView listView = findViewById(R.id.listView);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.listapp", Context.MODE_PRIVATE);
        set = (HashSet<String>) sharedPreferences.getStringSet("notes", null);

        if (set == null) {
            notes.add("Example note");
        } else {
            notes = new ArrayList<>(set);
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditNote.class);
                intent.putExtra("noteId", position);
                startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes.remove(position);
                                arrayAdapter.notifyDataSetChanged();

                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.listapp", Context.MODE_PRIVATE);
                                set = new HashSet<>(notes);

                                sharedPreferences.edit().remove("notes").apply();
                                sharedPreferences.edit().putStringSet("notes", set).apply();
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notes.add("");
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.listapp", Context.MODE_PRIVATE);
                set = new HashSet<>(notes);
                sharedPreferences.edit().remove("notes").apply();
                sharedPreferences.edit().putStringSet("notes", set).apply();
                arrayAdapter.notifyDataSetChanged();
                Intent intent = new Intent(getApplicationContext(), EditNote.class);
                intent.putExtra("noteId", notes.size() - 1);
                startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh list if necessary
            arrayAdapter.notifyDataSetChanged();
        }
    }


}