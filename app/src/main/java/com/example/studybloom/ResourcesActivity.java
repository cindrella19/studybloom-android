package com.example.studybloom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourcesActivity extends AppCompatActivity {
    private TextView contentText;
    private String[] words = {"Knowledge", "Study", "Focus", "Success", "Growth", "Learning", "Wisdom"};
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_page);

        TextView title = findViewById(R.id.pageTitle);
        TextView contentTitle = findViewById(R.id.contentTitle);
        contentText = findViewById(R.id.contentText);
        Button actionBtn = findViewById(R.id.actionBtn);

        title.setText("Resources");
        contentTitle.setText("Word of the Moment");
        
        loadDefinition();

        actionBtn.setText("Next Word");
        actionBtn.setOnClickListener(v -> {
            index = (index + 1) % words.length;
            loadDefinition();
        });
    }

    private void loadDefinition() {
        String word = words[index];
        contentText.setText("Looking up '" + word + "'...");
        RetrofitClient.getApiService().getDefinition(word).enqueue(new Callback<List<DictionaryWord>>() {
            @Override
            public void onResponse(Call<List<DictionaryWord>> call, Response<List<DictionaryWord>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    DictionaryWord dw = response.body().get(0);
                    if (!dw.meanings.isEmpty() && !dw.meanings.get(0).definitions.isEmpty()) {
                        String def = dw.meanings.get(0).definitions.get(0).definition;
                        contentText.setText(dw.word.toUpperCase() + ":\n\n" + def);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<DictionaryWord>> call, Throwable t) {
                contentText.setText("Dictionary service unavailable. Keep expanding your vocabulary!");
            }
        });
    }
}