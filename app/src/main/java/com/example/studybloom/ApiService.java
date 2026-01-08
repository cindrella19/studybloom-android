package com.example.studybloom;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;

public interface ApiService {
    // API 1: Quotes (for Motivation)
    @GET("https://zenquotes.io/api/random")
    Call<List<Quote>> getRandomQuote();

    // API 2: Number Facts (for Stats)
    @GET("http://numbersapi.com/random/math?json")
    Call<NumberFact> getNumberFact();

    // API 3: Dictionary (for Resources)
    @GET("https://api.dictionaryapi.dev/api/v2/entries/en/{word}")
    Call<List<DictionaryWord>> getDefinition(@Path("word") String word);

    // API 4: Advice (for Settings/About)
    @GET("https://api.adviceslip.com/advice")
    Call<AdviceSlip> getRandomAdvice();
}

class Quote {
    public String q; // quote
    public String a; // author
}

class NumberFact {
    public String text;
    public int number;
}

class DictionaryWord {
    public String word;
    public List<Meaning> meanings;
    static class Meaning {
        public List<Definition> definitions;
    }
    static class Definition {
        public String definition;
    }
}

class AdviceSlip {
    public Slip slip;
    static class Slip {
        public String advice;
    }
}
