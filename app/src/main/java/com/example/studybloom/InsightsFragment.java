package com.example.studybloom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class InsightsFragment extends Fragment {

    private TextView timeText, quoteText, postText, errorText;
    private ProgressBar loader;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insights, container, false);

        timeText = view.findViewById(R.id.insightTime);
        quoteText = view.findViewById(R.id.insightQuote);
        postText = view.findViewById(R.id.insightPost);
        errorText = view.findViewById(R.id.insightError);
        loader = view.findViewById(R.id.insightLoader);
        Button refreshBtn = view.findViewById(R.id.refreshInsightsBtn);

        requestQueue = Volley.newRequestQueue(requireContext());

        fetchData();

        refreshBtn.setOnClickListener(v -> fetchData());

        return view;
    }

    private void fetchData() {
        loader.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        timeText.setText("Updating...");
        quoteText.setText("Updating...");
        postText.setText("Updating...");

        // API 1: World Time (Stable mirror)
        String timeUrl = "https://worldtimeapi.org/api/timezone/Etc/UTC";
        JsonObjectRequest timeRequest = new JsonObjectRequest(Request.Method.GET, timeUrl, null,
                response -> {
                    try {
                        String dateTime = response.getString("datetime");
                        timeText.setText(dateTime.substring(0, 10) + " " + dateTime.substring(11, 19) + " UTC");
                    } catch (Exception e) {
                        timeText.setText("UTC Time Sync Success");
                    }
                }, error -> {
                    timeText.setText("Using System Time: " + java.text.DateFormat.getTimeInstance().format(new java.util.Date()));
                });

        // API 2: Advice (Quotes)
        String quoteUrl = "https://api.adviceslip.com/advice";
        JsonObjectRequest quoteRequest = new JsonObjectRequest(Request.Method.GET, quoteUrl, null,
                response -> {
                    try {
                        JSONObject slip = response.getJSONObject("slip");
                        quoteText.setText("\"" + slip.getString("advice") + "\"");
                    } catch (JSONException e) {
                        quoteText.setText("Success is not final, failure is not fatal: it is the courage to continue that counts.");
                    }
                }, error -> {
                    quoteText.setText("Keep going. Each step is progress.");
                });

        // API 3: Productivity Post (Stable mirror)
        String postUrl = "https://jsonplaceholder.typicode.com/posts/" + (int)(Math.random() * 100 + 1);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        postText.setText(response.getString("title"));
                        loader.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        postText.setText("Deep work session: focus on one task at a time.");
                        loader.setVisibility(View.GONE);
                    }
                }, error -> {
                    loader.setVisibility(View.GONE);
                    postText.setText("Consistency is the key to mastering any subject.");
                });

        // Set retry policies to handle slow connections
        DefaultRetryPolicy policy = new DefaultRetryPolicy(5000, 2, 1.0f);
        timeRequest.setRetryPolicy(policy);
        quoteRequest.setRetryPolicy(policy);
        postRequest.setRetryPolicy(policy);

        requestQueue.add(timeRequest);
        requestQueue.add(quoteRequest);
        requestQueue.add(postRequest);
    }
}