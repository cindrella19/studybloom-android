package com.example.studybloom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InsightsActivity extends AppCompatActivity {

    private TextView timeText, quoteText, placeholderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        timeText = findViewById(R.id.insightTime);
        quoteText = findViewById(R.id.insightQuote);
        placeholderText = findViewById(R.id.insightPlaceholder);
        Button refreshBtn = findViewById(R.id.refreshInsightsBtn);

        fetchData();

        refreshBtn.setOnClickListener(v -> fetchData());
    }

    private void fetchData() {
        new FetchApiTask("https://worldtimeapi.org/api/timezone/Etc/UTC", 1).execute();
        new FetchApiTask("https://api.adviceslip.com/advice", 2).execute();
        new FetchApiTask("https://jsonplaceholder.typicode.com/posts/1", 3).execute();
    }

    private class FetchApiTask extends AsyncTask<Void, Void, String> {
        private String apiUrl;
        private int type;

        public FetchApiTask(String url, int type) {
            this.apiUrl = url;
            this.type = type;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                return result.toString();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) return;
            try {
                JSONObject json = new JSONObject(result);
                if (type == 1) { // World Time
                    timeText.setText("UTC Time: " + json.getString("datetime").substring(11, 19));
                } else if (type == 2) { // Advice (Quote)
                    JSONObject slip = json.getJSONObject("slip");
                    quoteText.setText("\"" + slip.getString("advice") + "\"");
                } else if (type == 3) { // Placeholder
                    placeholderText.setText(json.getString("title"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}