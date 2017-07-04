package com.spq.turtlebug.investingjournal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {
    private String selectedEntry;
    private String[] titles;
    ListView listView;
    int idPrevious = -1;
    int posPrevious = -1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(
                R.id.listViewMain);
        listView.setChoiceMode(ListView.FOCUSABLES_TOUCH_MODE);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int position, long id) {
                        if (id != idPrevious && position != posPrevious) posPrevious = (int) id;
                        else readJournalEntry(position);
                    }
                });
    }


    @Override
    public void onResume(){
        super.onResume();
        refreshList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                String target = ((EditText) findViewById(R.id.searchAdd)).getText().toString();
                Intent intent = new Intent(getApplicationContext(), SearchResult.class);
                intent.putExtra("target", target);
                startActivity(intent);
                return true;
            case R.id.add_entry:
                String add = ((EditText) findViewById(R.id.searchAdd)).getText().toString();
                Intent intentBravo = new Intent(getApplicationContext(), AddEntry.class);
                intentBravo.putExtra("add", add);
                startActivity(intentBravo);
                return true;
            case R.id.delete:
                deleteEntry();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        TextView tv = (TextView) findViewById(R.id.searchAdd);
        tv.setText("");
        super.onPause();
    }


    private void refreshList() {
        titles = fileList();
        Collections.reverse(Arrays.asList(titles));
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_activated_1,
                        titles);
        listView.setAdapter(arrayAdapter);
    }


    protected void readJournalEntry(int position) {
        if (titles.length > position) {
            selectedEntry = titles[position];
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


    private void deleteEntry() {
        if (selectedEntry != null) {
            deleteFile(selectedEntry);
            selectedEntry = null;
            String[] titles = fileList();
            Collections.reverse(Arrays.asList(titles));
            refreshList();
        }
    }
}
