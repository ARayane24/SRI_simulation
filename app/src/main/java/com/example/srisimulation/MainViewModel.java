package com.example.srisimulation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainViewModel extends ViewModel {
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
        if (Boolean.TRUE.equals(ignoreCase.getValue()))
           string = string.toLowerCase();
        if (Boolean.TRUE.equals(clipBySpaces.getValue()) && Boolean.TRUE.equals(clipByAllSpecialChar.getValue()) && Boolean.TRUE.equals(clipByAllSpecialCharButIgnoreChar.getValue()))
            result.addAll(Arrays.asList(string.split("[\\s|\\W&&[^-]]+")));
        else if (Boolean.TRUE.equals(clipBySpaces.getValue()) && Boolean.TRUE.equals(clipByAllSpecialChar.getValue()))
            result.addAll(Arrays.asList(string.split("[\\s|\\W]+")));
        else if (Boolean.TRUE.equals(clipBySpaces.getValue()))
            result.addAll(Arrays.asList(string.split(" ")));
        else if (Boolean.TRUE.equals(clipByAllSpecialChar.getValue()) && Boolean.TRUE.equals(clipByAllSpecialCharButIgnoreChar.getValue()))
            result.addAll(Arrays.asList(string.split("[\\W&&[^-]]+")));
        else if (Boolean.TRUE.equals(clipByAllSpecialChar.getValue()))
            result.addAll(Arrays.asList(string.split("[\\W]+")));

        if (result.isEmpty())
            result.add(string);

        if (Boolean.TRUE.equals(trimWordsSoItHasOnly7Char.getValue()))
            for (int i = 0; i < result.size(); i++) {
                String originalString = result.get(i);
                String res = originalString.length() > 7 ? originalString.substring(0, 7) : originalString;
                result.remove(i);
                result.add(i,res);
            }

        if (Boolean.TRUE.equals(DELETE_MOT_VIDE.getValue()))
            for (int i = 0; i < result.size(); i++) {
                if (checkStringInList(result.get(i),motvides))
                    result.remove(i);
            }
        return result.toArray(new String[0]);
    }
    public static boolean checkStringInList(String searchFor, List<String> listToSearchIn){
        for (String v : listToSearchIn) {
            if (searchFor.equals(v))
                return true;
        }
        return false;
    }



    final static  int BOX_ignoreCase= 0;
    final static  int BOX_clipBySpaces= 1;
    final static  int BOX_clipByAllSpecialChar= 2;
    final static  int BOX_clipByAllSpecialCharButIgnoreChar= 3;
    final static  int BOX_trimWordsSoItHasOnly7Char= 4;
    final static  int BOX_DELETE_MOT_VIDE= 5;
    private final MutableLiveData<Boolean> ignoreCase = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> clipBySpaces = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> clipByAllSpecialChar = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> clipByAllSpecialCharButIgnoreChar = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> trimWordsSoItHasOnly7Char = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> DELETE_MOT_VIDE = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> getBox(int index) {
        switch (index){
            case BOX_ignoreCase: return ignoreCase;
            case BOX_clipBySpaces: return clipBySpaces;
            case BOX_clipByAllSpecialChar: return clipByAllSpecialChar;
            case BOX_clipByAllSpecialCharButIgnoreChar: return clipByAllSpecialCharButIgnoreChar;
            case BOX_trimWordsSoItHasOnly7Char: return trimWordsSoItHasOnly7Char;
            case BOX_DELETE_MOT_VIDE: return DELETE_MOT_VIDE;
            default: return null;
        }
    }



    List<String> docs = new LinkedList<>();
    MutableLiveData<Integer> numberDocs = new MutableLiveData<>(0);
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
}
