package com.example.studybloom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsActivity extends AppCompatActivity {
    private TextView contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_page);

        TextView title = findViewById(R.id.pageTitle);
        TextView contentTitle = findViewById(R.id.contentTitle);
        contentText = findViewById(R.id.contentText);
        Button actionBtn = findViewById(R.id.actionBtn);

        title.setText("Study Stats");
        contentTitle.setText("Number Trivia");
        
        loadFact();

        actionBtn.setText("New Fact");
        actionBtn.setOnClickListener(v -> loadFact());
    }

    private void loadFact() {
        contentText.setText("Fetching data...");
        RetrofitClient.getApiService().getNumberFact().enqueue(new Callback<NumberFact>() {
            @Override
            public void onResponse(Call<NumberFact> call, Response<NumberFact> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contentText.setText(response.body().text);
                }
            }
            @Override
            public void onFailure(Call<NumberFact> call, Throwable t) {
                contentText.setText("Stats unavailable at the moment. Keep tracking your progress!");
            }
        });
    }
}