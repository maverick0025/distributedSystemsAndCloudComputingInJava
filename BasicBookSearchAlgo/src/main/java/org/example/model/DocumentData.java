package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class DocumentData{

    private Map<String, Double> termToFrequency = new HashMap<>();

    public void putTermFrequency(String term, double frequency){
        termToFrequency.put(term, frequency);
    }

    public Double getFrequency(String term){
        return termToFrequency.get(term);
    }
}
