package com.spq.turtlebug.investingjournal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchResult extends AppCompatActivity {
    private String selectedEntry;
    private ListView listView2;
    private String[] targetList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        listView2 = (ListView) findViewById(
                R.id.targetListView);
        listView2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView2.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int position, long id) {
                        readJournalEntry(position);
                    }
                });
    }


    @Override
    public void onResume(){
        super.onResume();
        refreshList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //return to main activity
    }

    private void refreshList() {
        String target = getIntent().getStringExtra("target");
        target = target.toUpperCase();
        targetList = subList(target);
        ArrayAdapter<String> adapter2 = new
                ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, targetList);
        listView2.setAdapter(adapter2);
    }


    private String[] subList(String target) {
        int j = 0;
        String[] titles = fileList();
        Collections.reverse(Arrays.asList(titles));
        String[] temp = new String[fileList().length];

        for (int i = 0; i < fileList().length; i++){
            if (titles[i].contains(target)){
                temp[j] = titles[i];
                j++;
            }
        }
        return Arrays.copyOf(temp, j);
    }


    protected void readJournalEntry(int position) {
        if (position < targetList.length) {
            selectedEntry = targetList[position];
            File dir = getFilesDir();
            File file = new File(dir, selectedEntry);
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                StringBuilder sb = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = bufferedReader.readLine();
                }
                showAlertDialog(selectedEntry, sb.toString());

            } catch (IOException e) {

            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                    }
                }
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }



    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}
