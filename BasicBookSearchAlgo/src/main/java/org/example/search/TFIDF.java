package org.example.search;

import org.example.model.DocumentData;

import java.util.*;

public class TFIDF {

    public static DocumentData createDocumentData(List<String> documentWords, List<String> searchTerms){
        DocumentData documentData = new DocumentData();

        for(String searchTerm: searchTerms){
            double termFreq = calculateTermFrequency(documentWords, searchTerm);
            documentData.putTermFrequency(searchTerm, termFreq);
        }

        return documentData;
    }

    public static Double calculateTermFrequency(List<String> docWords, String searchTerm){
        long count = 0;
        for(String word: docWords){
            if(word.equalsIgnoreCase(searchTerm)){
                count++;
            }
        }
        double termFrequency = (double) count /docWords.size();
        return termFrequency;
    }

    public static Map<Double, List<String>> getDocumentsSortedByScore(List<String> searchTerms, Map<String, DocumentData> documentResults){
        TreeMap<Double, List<String>> scoreToDoc = new TreeMap<>();
        //score to doc is the score and list of documents with that score

        Map<String, Double> termToInverseDocumentFrequency = getTermToInverseDocumentFrequencyMap(searchTerms, documentResults);

        for(String document: documentResults.keySet()){
            DocumentData documentData = documentResults.get(document);
            double score = calculateDocumentScore(searchTerms, documentData, termToInverseDocumentFrequency);
            addDocumentScoreToTreeMap(scoreToDoc, score, document);
        }
        return scoreToDoc.descendingMap();

    }

    private static void addDocumentScoreToTreeMap(TreeMap<Double, List<String>> scoreToDoc, double score, String document) {
        List<String> booksWithCurrentScore = scoreToDoc.get(score);
        if(booksWithCurrentScore == null){
            booksWithCurrentScore = new ArrayList<>();
        }
        booksWithCurrentScore.add(document);
        scoreToDoc.put(score, booksWithCurrentScore);
    }

    private static double calculateDocumentScore(List<String> searchTerms, DocumentData documentData, Map<String, Double> termToInverseDocumentFrequency) {
        double score = 0;

        for(String term: searchTerms){
            double termFreq = documentData.getFrequency(term);
            double inverseTermFreq = termToInverseDocumentFrequency.get(term);
            score += termFreq*inverseTermFreq;
        }
        return score;
    }

    private static Map<String, Double> getTermToInverseDocumentFrequencyMap(List<String> searchTerms, Map<String, DocumentData> documentResults) {

        Map<String, Double> termToIdf = new HashMap<>();
        for(String searchTerm: searchTerms){
            double idf = getInverseDocumentFrequency(searchTerm, documentResults);
            termToIdf.put(searchTerm, idf);
        }
        return termToIdf;
    }

    private static double getInverseDocumentFrequency(String searchTerm, Map<String, DocumentData> documentResults) {
        double n = 0;

        for(String document: documentResults.keySet()){
            DocumentData documentData = documentResults.get(document);
            double searchTermFreq = documentData.getFrequency(searchTerm);
            if(searchTermFreq > 0.0){
                n++;
            }
        }

        return n ==0 ? 0 : Math.log10(documentResults.size()/n);
    }


    public static List<String> getWordsFromLines(List<String> lines){
        List<String> words = new ArrayList<>();
        for(String line: lines){
            words.addAll(getWordsFromLine(line));
        }
        return words;
    }
    public static List<String> getWordsFromLine(String line){
        return Arrays.asList(line.split("(\\.)+|(,)+|( )+|(-)+|(\\?)+|(!)+|(;)+|(:)+|(/d)+|(/n)+"));
    }

}
