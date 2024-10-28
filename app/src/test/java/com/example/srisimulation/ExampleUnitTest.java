package com.example.srisimulation;

import androidx.lifecycle.MutableLiveData;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        LinkedList<String> values = new LinkedList<>(List.of("buyer","economie","blablabla","economique","economiquement","tech"));

        System.out.println(nGram(values));

    }

    private HashMap<String,LinkedList<String>> baseConnessons = new HashMap<>();
    private LinkedList<LinkedList<String>> docsCopy= new LinkedList<>();
    public LinkedList<LinkedList<String>>  nGram(LinkedList<String> docs){

        LinkedList<String> tokens_List= new LinkedList<>();

        for (int i = 0; i < docs.size(); i++) {
            LinkedList<String> doc = processDocs(docs.get(i));
            docsCopy.add(doc);
            tokens_List.addAll(doc);
        }

        int size = tokens_List.size();
        for (int i = 0; i < size; i++) {
            LinkedList<WordRoot> similarRoots = getAllTokensWithSameRoot(i, tokens_List);

            if (similarRoots.isEmpty()) continue;

            String root= shortestRoot(similarRoots);
            baseConnessons.put(root,new LinkedList<>());
            for (int j = 0; j < similarRoots.size(); j++){
                Objects.requireNonNull(baseConnessons.get(root)).add(similarRoots.get(j).word);
                tokens_List.remove(similarRoots.get(j).word);
                replaceValue(similarRoots.get(j).word,similarRoots.get(j).root,docsCopy);
                size--;
            }

            //findAndReplaceByRoot(root, );String
        }

        return docsCopy;
    }

    public void replaceValue(String valueToFind, String newValue, LinkedList<LinkedList<String>> docs){
        for (int i = 0; i < docs.size(); i++) {
            for (int j = 0; j < docs.get(i).size(); j++) {
                if (docs.get(i).get(j).equals(valueToFind)){
                    docs.get(i).remove(j);
                    docs.get(i).add(j,newValue);
                }
            }
        }
    }
    public LinkedList<String> processDocs(String string) {
        string = string.trim();
        LinkedList<String> result = new LinkedList<>();
        string = ignoreCase(string);
        clip(string, result);

        if (result.isEmpty())
            result.add(string);

        clearMotVides(result);
        return result;
    }
    private LinkedList<WordRoot> getAllTokensWithSameRoot(int i, LinkedList<String> tokens_List) {
        LinkedList<WordRoot> similarRoots = new LinkedList<>();
        String root ="";
        for (int j = i + 1; j < tokens_List.size(); j++) {
             root = sharedRoot(
                    clipToGram(tokens_List.get(i), 2),
                    clipToGram(tokens_List.get(j), 2),
                    0.3);
            if (root == null) continue;

            similarRoots.add(new WordRoot(root,tokens_List.get(j)));
        }

        if (!similarRoots.isEmpty())
            similarRoots.addFirst(new WordRoot(sharedRoot(
                    clipToGram(tokens_List.get(i), 2),
                    clipToGram(similarRoots.get(0).root, 2),
                    0.3),tokens_List.get(i)));

    return similarRoots;
    }
    private static String shortestRoot(LinkedList<WordRoot> similarRoots) {
        String root= similarRoots.get(0).root;
        for (int j = 0; j < similarRoots.size(); j++) {
            if (root.length()>similarRoots.get(j).root.length())
                root = similarRoots.get(j).root;
        }
        return root;
    }
    private String[] clipToGram(String string, int n) {
        if (n <= 0) throw new IllegalArgumentException("n value !!");
        if (string.length() <= 2|| string.length() < n) return new String[]{string};
        LinkedList<String> values = new LinkedList<>();

        for (int i = 0; i+(n) <= string.length(); i++) {
            StringBuilder v = new StringBuilder();

            for (int j = i; j < (n)+i; j++)
                v.append(string.charAt(j));

            values.add(v.toString());
        }

        return values.toArray(new String[0]);
    }
    private static String sharedRoot(String[] grams1, String[] grams2, double threshold){
        String[] small = grams1.length < grams2.length ? grams1 : grams2 ;
        double similar = 0 ;
        StringBuilder root= new StringBuilder();

        int i;
        for ( i = 0; i < small.length; i++) {
            if (!grams1[i].equals(grams2[i]))
                break;
            root.append(small[i].charAt(0));
            similar++;
        }
        String lastPart = small[i>0 ? i - 1:0];
        root.append(lastPart.charAt(lastPart.length()-1));
        return calculateSimilarity(similar,grams1.length,grams2.length) > threshold ? root.toString() : null;
    }
    private static double calculateSimilarity(double numberSimilar, int lengthGram1, int lengthGram2){
        return 2*numberSimilar/(lengthGram1+lengthGram2);
    }



    private final LinkedList<String> motvides = new LinkedList<>(Arrays.asList("la","La","le","Le","un","une","Un","Une","des","Des","Ces","ces","ma","Ma","Mon","mon",
            "l'","L'","Je","je","Tu","tu","il","Il","elle","Elle","nous","Nous","vous","Vous","Ils","ils","a","A","quel","Quel","est","Est","ont","Ont"));

    private void clearMotVides(LinkedList<String> result) {
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).length() <= 3 || checkStringInList(result.get(i),motvides))
                    result.remove(i);
            }
    }

    private void clip(String string, LinkedList<String> result) {
            result.addAll(Arrays.asList(string.split("[\\s|\\W&&[^-]]+")));
    }

    private String ignoreCase(String string) {
            string = string.toLowerCase();
        return string;
    }

    public static boolean checkStringInList(String searchFor, List<String> listToSearchIn){
        for (String v : listToSearchIn) {
            if (searchFor.equals(v))
                return true;
        }
        return false;
    }
    public static boolean getValueOfMutable(MutableLiveData<Boolean> data){
        return Boolean.TRUE.equals(data.getValue());
    }
}