package org.example;

import org.example.model.DocumentData;
import org.example.search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class SequentialSearch
{
    private static final String BOOKS_DIRECTORY="./resources/books";
    private static final String SEARCH_QUERY_1 = "The best detective that catches many criminals using his deductive methods";
    private static final String SEARCH_QUERY_2 = "The girl that falls through a rabbit hole into a fantasy wonderland";
    private static final String SEARCH_QUERY_3 = "A war between Russian and France in the cold winter";

    public static void main( String[] args ) throws FileNotFoundException {
        File documentsDirectory = new File(BOOKS_DIRECTORY);

        List<String> documents = Arrays.asList(documentsDirectory.list())
                        .stream()
                        .map(documentName-> BOOKS_DIRECTORY+"/"+documentName)
                        .collect(Collectors.toList());

        List<String> searchTerms = TFIDF.getWordsFromLine(SEARCH_QUERY_1);

        findMostRelevantDocuments(documents, searchTerms);
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> searchTerms) throws FileNotFoundException {
        Map<String, DocumentData> documentsResults = new HashMap<>();

        for(String document: documents){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));

            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = TFIDF.getWordsFromLines(lines);
            DocumentData documentData = TFIDF.createDocumentData(words, searchTerms);
            documentsResults.put(document, documentData);
        }

        //getdocumentssorted by score for the current search query
        //score->list of documents
        Map<Double, List<String>> documentsByScore = TFIDF.getDocumentsSortedByScore(searchTerms, documentsResults);

        //print the document by score
        printResults(documentsByScore);
    }

    private static void printResults(Map<Double, List<String>> documentsByScore) {
        for(Map.Entry<Double, List<String>> docScorePair: documentsByScore.entrySet()){
            double score = docScorePair.getKey();
            for(String document: docScorePair.getValue()){
                System.out.println(String.format("Book: %s - score : %f", document.split("/")[3], score));
            }
        }
    }
}
