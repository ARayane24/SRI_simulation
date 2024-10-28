package com.example.srisimulation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Integer> dps = new MutableLiveData<>(0);
    public void setDps(int dpsV){
        dps.setValue(dpsV);
    }
    public LiveData<Integer> getDps() {
        return dps;
    }

    private final MutableLiveData<Integer> dnps = new MutableLiveData<>(0);
    public void setDnps(int dnpsV){
        dnps.setValue(dnpsV);
    }
    public LiveData<Integer> getDnps() {
        return dnps;
    }

    private final MutableLiveData<Integer> dpt = new MutableLiveData<>(0);
    public void setDpt(int dptV){
        dpt.setValue(dptV);
    }
    public LiveData<Integer> getDpt() {
        return dpt;
    }

    private final MutableLiveData<Integer> ds = new MutableLiveData<>(0);
    public void setDs(int dsV){
        ds.setValue(dsV);
    }
    public LiveData<Integer> getDs() {
        return ds;
    }

    private final MutableLiveData<Integer> dpns = new MutableLiveData<>(0);
    public void setDpns(int dpnsV){
        dpns.setValue(dpnsV);
    }
    public LiveData<Integer> getDpns() {
        return dpns;
    }

    private final MutableLiveData<Double> precision = new MutableLiveData<>(0.0);
    public void setPrecision(double precisionV){
        precision.setValue(precisionV);
    }
    public LiveData<Double> getPrecision() {
        return precision;
    }

    private final MutableLiveData<Double> rappel = new MutableLiveData<>(0.0);
    public void setRappel(double dpnsV){
        rappel.setValue(dpnsV);
    }
    public LiveData<Double> getRappel() {
        return rappel;
    }

    private final MutableLiveData<Double> bruit = new MutableLiveData<>(0.0);
    public void setBruit(double dpnsV){
        bruit.setValue(dpnsV);
    }
    public LiveData<Double> getBruit() {
        return bruit;
    }

    private final MutableLiveData<Double> silence = new MutableLiveData<>(0.0);
    public void setSilence(double dpnsV){
        silence.setValue(dpnsV);
    }
    public LiveData<Double> getSilence() {
        return silence;
    }


    private final MutableLiveData<Boolean> isSettingsShown = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsSettingsShown() {
        return isSettingsShown;
    }
    public void showSettings(){
        isSettingsShown.setValue(true);
    }
    public void hideSettings(){
        isSettingsShown.setValue(false);
    }

    private final MutableLiveData<Boolean> isCalculusShown = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsCalculusShown() {
        return isCalculusShown;
    }
    public void showCalculus(){
        isCalculusShown.setValue(true);
    }
    public void hideCalculus(){
        isCalculusShown.setValue(false);
    }



    private final MutableLiveData<String> searchString = new MutableLiveData<>("");
    public LiveData<String> getSearchString() {
        return searchString;
    }
    public void setSearchString(String string) {
        searchString.setValue(string);
    }

    private final LinkedList<String> motvides = new LinkedList<>(Arrays.asList("la","La","le","Le","un","une","Un","Une","des","Des","Ces","ces","ma","Ma","Mon","mon",
            "l'","L'","Je","je","Tu","tu","il","Il","elle","Elle","nous","Nous","vous","Vous","Ils","ils","a","A","quel","Quel","est","Est","ont","Ont"));


    public String[] processInput(String string) {
        string = string.trim();
        LinkedList<String> result = new LinkedList<>();
        string = ignoreCase(string);
        clip(string, result);

        if (result.isEmpty())
            result.add(string);

        trim(result);
        clearMotVides(result);
        return result.toArray(new String[0]);
    }



    private void clearMotVides(LinkedList<String> result) {
        if (getValueOfMutable(getBox(BOX_DELETE_MOT_VIDE)))
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).length() <= 3 || checkStringInList(result.get(i),motvides))
                    result.remove(i);
            }
    }

    private void trim(LinkedList<String> result) {
        if (getValueOfMutable(getBox(BOX_trimWordsSoItHasOnly7Char)))
            for (int i = 0; i < result.size(); i++) {
                String originalString = result.get(i);
                String res = originalString.length() > 7 ? originalString.substring(0, 7) : originalString;
                result.remove(i);
                result.add(i,res);
            }
    }

    private void clip(String string, LinkedList<String> result) {
        if (getValueOfMutable(getBox(BOX_clipBySpaces)) && getValueOfMutable(getBox(BOX_clipByAllSpecialChar)) && getValueOfMutable(getBox(BOX_clipByAllSpecialCharButIgnoreChar)))
            result.addAll(Arrays.asList(string.split("[\\s|\\W&&[^-]]+")));
        else if (getValueOfMutable(getBox(BOX_clipBySpaces)) && getValueOfMutable(getBox(BOX_clipByAllSpecialChar)) )
            result.addAll(Arrays.asList(string.split("[\\s|\\W]+")));
        else if (getValueOfMutable(getBox(BOX_clipBySpaces)))
            result.addAll(Arrays.asList(string.split(" ")));
        else if (getValueOfMutable(getBox(BOX_clipByAllSpecialChar)) && getValueOfMutable(getBox(BOX_clipByAllSpecialCharButIgnoreChar)))
            result.addAll(Arrays.asList(string.split("[\\W&&[^-]]+")));
        else if (getValueOfMutable(getBox(BOX_clipByAllSpecialChar)))
            result.addAll(Arrays.asList(string.split("[\\W]+")));
    }

    private String ignoreCase(String string) {
        if (getValueOfMutable(getBox(BOX_ignoreCase)))
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



    final static  int BOX_ignoreCase= 0;
    final static  int BOX_clipBySpaces= 1;
    final static  int BOX_clipByAllSpecialChar= 2;
    final static  int BOX_clipByAllSpecialCharButIgnoreChar= 3;
    final static  int BOX_trimWordsSoItHasOnly7Char= 4;
    final static  int BOX_n_gram= 5;
    final static  int BOX_DELETE_MOT_VIDE= 6;
    private final MutableLiveData<Boolean>[] classicParams =new MutableLiveData[]{new MutableLiveData(false),new MutableLiveData(false),new MutableLiveData(false)
            ,new MutableLiveData(false),new MutableLiveData(false),new MutableLiveData(false),new MutableLiveData(false)};
    public MutableLiveData<Boolean> getBox(int index) {
        return classicParams[index];
    }



    List<String> docs = new LinkedList<>();
    private final MutableLiveData<Integer> numberDocs = new MutableLiveData<>(0);
    public LiveData<Integer> getNumberDocs() {
        return numberDocs;
    }
    public void incrementDocs(){
        numberDocs.setValue(numberDocs.getValue()+1);
    }
    public void decrementDocs(){
        numberDocs.setValue(numberDocs.getValue()-1);
    }

    public static boolean checkStringInList(String[] v, String[] values) {
        for (String value : values) {
            for (String s : v) {
                if (s.equals(value))
                    return true;
            }
        }
        return false;
    }



    public void calculatePricision() {
        if (ds.getValue() == 0)
            precision.setValue((double) -1);
        else
            precision.setValue((double) dps.getValue()/ds.getValue());
    }
    public void calculateRappel() {
        if (dpt.getValue() == 0)
            rappel.setValue((double) -1);
        else
            rappel.setValue((double) dps.getValue()/dpt.getValue());
    }
    public void calculateBruit() {
        if (ds.getValue() == 0)
            bruit.setValue((double) -1);
        else
            bruit.setValue((double) dnps.getValue()/ds.getValue());
    }
    public void calculateSilence() {
        if (dpt.getValue() == 0)
            silence.setValue((double) -1);
        else
            silence.setValue((double) dpns.getValue()/dpt.getValue());
    }


    private final MutableLiveData<Double> THRESHOLD = new MutableLiveData<>(.0);
    public MutableLiveData<Double> getTHRESHOLD() {
        return THRESHOLD;
    }
    private final MutableLiveData<Integer> GRAM = new MutableLiveData<>(2);
    public LiveData<Integer> getGRAM() {
        return GRAM;
    }






    private HashMap<String,LinkedList<String>> baseConnessons = new HashMap<>();
    private MutableLiveData<LinkedList<LinkedList<String>>> docsCopy= new MutableLiveData<>(new LinkedList<>());
    public LiveData<LinkedList<LinkedList<String>>> getDocsCopy() {
        return docsCopy;
    }

    public void nGram(List<String> docs){
        if (!getValueOfMutable(getBox(BOX_n_gram))) return;
        LinkedList<String> tokens_List= new LinkedList<>();
        docsCopy.setValue(new LinkedList<>());

        for (int i = 0; i < docs.size(); i++) {
            LinkedList<String> doc = processDocs(docs.get(i));
            docsCopy.getValue().add(doc);
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
                replaceValue(similarRoots.get(j).word,similarRoots.get(j).root,docsCopy.getValue());
                size--;
            }

            //findAndReplaceByRoot(root, );String
        }
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
                    clipToGram(tokens_List.get(i), GRAM.getValue()),
                    clipToGram(tokens_List.get(j), GRAM.getValue()),
                    THRESHOLD.getValue());

            if (root != null)
                similarRoots.add(new WordRoot(root,tokens_List.get(j)));
        }

        if (!similarRoots.isEmpty()){
            String s = sharedRoot(
                    clipToGram(tokens_List.get(i), GRAM.getValue()),
                    clipToGram(similarRoots.get(0).root, GRAM.getValue()),
                    THRESHOLD.getValue());
            if (s != null)
                similarRoots.addFirst(new WordRoot(s,tokens_List.get(i)));
        }

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
    public String[] clipToGram(String string, int n) {
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
    public static String sharedRoot(String[] grams1, String[] grams2, double threshold){
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
        if (lastPart.isEmpty()) return null;
        root.append(lastPart.charAt(lastPart.length()-1));
        return calculateSimilarity(similar,grams1.length,grams2.length) > threshold ? root.toString() : null;
    }
    public boolean sharedRoot(String s1 , List<String> s2 ){
        for (int i = 0; i < s2.size(); i++) {
            if (sharedRoot(clipToGram(s1,getGRAM().getValue()),clipToGram(s2.get(i),getGRAM().getValue()),getTHRESHOLD().getValue()) != null){
                return true;
            }
        }
        return false;
    }
    private static double calculateSimilarity(double numberSimilar, int lengthGram1, int lengthGram2){
        return 2*numberSimilar/(lengthGram1+lengthGram2);
    }
}
