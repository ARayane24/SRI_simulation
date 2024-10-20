package com.example.srisimulation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    final static  int BOX_DELETE_MOT_VIDE= 5;
    private final MutableLiveData<Boolean>[] classicParams =new MutableLiveData[]{new MutableLiveData(false),new MutableLiveData(false)
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


}
