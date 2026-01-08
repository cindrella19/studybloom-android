package com.example.studybloom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MotivationActivity extends AppCompatActivity {
    private TextView contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_page);

        TextView title = findViewById(R.id.pageTitle);
        TextView contentTitle = findViewById(R.id.contentTitle);
        contentText = findViewById(R.id.contentText);
        Button actionBtn = findViewById(R.id.actionBtn);

        title.setText("Motivation");
        contentTitle.setText("Daily Quote");
        
        loadQuote();

        actionBtn.setText("New Quote");
        actionBtn.setOnClickListener(v -> loadQuote());
    }

    private void loadQuote() {
        contentText.setText("Fetching inspiration...");
        RetrofitClient.getApiService().getRandomQuote().enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Quote q = response.body().get(0);
                    contentText.setText("\"" + q.q + "\"\n\n— " + q.a);
                }
            }
            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                contentText.setText("Could not load quote. You are doing great anyway!");
            }
        });
    }
}