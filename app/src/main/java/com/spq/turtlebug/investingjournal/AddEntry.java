package com.spq.turtlebug.investingjournal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistQuotesRequest;
import yahoofinance.quotes.stock.StockQuote;

public class AddEntry extends AppCompatActivity {
    String dateTime,
             thesis,
             symbol;

    TextView showTicker,
             showDate;

    Stock stock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormatA = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        dateTime = dateFormatA.format(c.getTime());
        showDate = (TextView) findViewById(R.id.show_date);
        showDate.setText(dateTime);
        showDate = (TextView) findViewById(R.id.show_date);
        showTicker = (TextView) findViewById(R.id.textView2);
        showTicker. setText(getIntent().getStringExtra("add"));
    }


    public void addJournalEntry(View view) throws InterruptedException {
        symbol = getIntent().getStringExtra("add");
        symbol = symbol.toUpperCase();
        thesis = ((EditText)  findViewById(R.id.thesis_input)).getText().toString();
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    stock = YahooFinance.get(symbol);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();

        if (stock.getQuote().getPrice() == null){
            showAlertDialog("***Error***", "the symbol entered could not be found in the " +
                    "yahoo finance database");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else {
            String fileName = (symbol + "   " + dateTime);
            File parent = getFilesDir();
            File file = new File(parent, fileName);
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(file);

                writer.write("price/share  $" + stock.getQuote().getPrice() + "                    " +
                        "                 market cap in Billions "
                        + stock.getStats().getMarketCap().divide(BigDecimal.valueOf(1000000000))
                        + "                       " + thesis);
                finish();
            } catch (Exception e) {
                showAlertDialog("Error", e.getMessage());
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }


    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new
                AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}

