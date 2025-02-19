package com.example.srisimulation;

import static com.example.srisimulation.MainViewModel.BOX_DELETE_MOT_VIDE;
import static com.example.srisimulation.MainViewModel.BOX_clipByAllSpecialChar;
import static com.example.srisimulation.MainViewModel.BOX_clipByAllSpecialCharButIgnoreChar;
import static com.example.srisimulation.MainViewModel.BOX_clipBySpaces;
import static com.example.srisimulation.MainViewModel.BOX_ignoreCase;
import static com.example.srisimulation.MainViewModel.BOX_n_gram;
import static com.example.srisimulation.MainViewModel.BOX_trimWordsSoItHasOnly7Char;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.srisimulation.databinding.ActivityMainBinding;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configActivity();
        setTopMenus();
        recyclerView();
    }

    private void calculusResults() {
        viewModel.getBruit().observe(this,v->{
            if (v>=0) {
                String text = "= " + v;
                binding.bruit.setText(text);
            } else
                binding.bruit.setText(getString(R.string.nc));
        });
        viewModel.getSilence().observe(this, v->{
            if (v<0)
                binding.silence.setText(getString(R.string.nc));
            else{
                String string = v.toString();
                String text = "= " + string;
                binding.silence.setText(text);
            }
        });
        viewModel.getRappel().observe(this, v->{
            if (v>=0) {
                String string = v.toString();
                String text = "= " + string;
                binding.rappel.setText(text);
                binding.rappelResult.setText(string);
            }
            else {
                binding.rappel.setText(getString(R.string.nc));
                binding.rappelResult.setText(getString(R.string._nc));
            }
            viewModel.calculateSilence();
        });
        viewModel.getPrecision().observe(this,v->{
            if (v>=0) {
                String string = v.toString();
                String text = "= " + string;
                binding.precision.setText(text);
                binding.precisionResult.setText(string);
            }
            else {
                binding.precision.setText(getString(R.string.nc));
                binding.precisionResult.setText(getString(R.string._nc));
            }
            viewModel.calculateBruit();
        });
    }

    private void recyclerView() {
        setRecyclerView(viewModel.docs,null);
        binding.addDoc.setOnClickListener(e->showAddDocPopup());
        viewModel.getNumberDocs().observe(this,n->{
            String text = getString(R.string.total_docs) + n;
            binding.totalDocs.setText(text);
            String v = viewModel.getSearchString().getValue();
            assert v != null;
            String[] values = viewModel.processInput(v);
            viewModel.nGram(viewModel.docs);
            setRecyclerView(viewModel.getBox(BOX_n_gram).getValue() ? ngramFilterList(viewModel.docs,viewModel.getDocsCopy().getValue(),values)  : filterList(viewModel.docs, values),values);
        });
    }

    private void setParamsCheckBoxes() {
        binding.ignoreCase.setOnClickListener(v-> viewModel.getBox(BOX_ignoreCase).setValue(binding.ignoreCase.isChecked()));
        binding.clipBySpaces.setOnClickListener(v-> viewModel.getBox(BOX_clipBySpaces).setValue(binding.clipBySpaces.isChecked()));
        binding.suppMotVide.setOnClickListener(v-> viewModel.getBox(BOX_DELETE_MOT_VIDE).setValue(binding.suppMotVide.isChecked()));
        binding.clipByAllSpecialChar.setOnClickListener(v-> viewModel.getBox(BOX_clipByAllSpecialChar).setValue(binding.clipByAllSpecialChar.isChecked()));
        binding.clipByAllSpecialCharButIgnoreChar.setOnClickListener(v-> viewModel.getBox(BOX_clipByAllSpecialCharButIgnoreChar).setValue(binding.clipByAllSpecialCharButIgnoreChar.isChecked()));
        binding.trimWordsSoItHasOnly7Char.setOnClickListener(v-> viewModel.getBox(BOX_trimWordsSoItHasOnly7Char).setValue(binding.trimWordsSoItHasOnly7Char.isChecked()));
        binding.nGram.setOnClickListener(v-> viewModel.getBox(BOX_n_gram).setValue(binding.nGram.isChecked()));

        for (int i = BOX_ignoreCase ; i <= BOX_DELETE_MOT_VIDE ; i++)
            viewModel.getBox(i).observe(this,v-> viewModel.setSearchString(viewModel.getSearchString().getValue()));

        viewModel.getBox(BOX_clipByAllSpecialChar).observe(this,v->{
            viewModel.setSearchString(viewModel.getSearchString().getValue());
            binding.clipByAllSpecialCharButIgnoreChar.setEnabled(v);
            if (!v){
                binding.clipByAllSpecialCharButIgnoreChar.setChecked(v);
                binding.clipByAllSpecialCharButIgnoreChar.callOnClick();
            }
        });

        viewModel.getBox(BOX_trimWordsSoItHasOnly7Char).observe(this,v-> binding.nGram.setEnabled(!v));
        viewModel.getBox(BOX_n_gram).observe(this,v-> {
            binding.trimWordsSoItHasOnly7Char.setEnabled(!v);
            binding.seek.setVisibility(v? View.VISIBLE: View.GONE);
        });

        binding.seek.setMax(100);
        binding.seek.setProgress(1);
        binding.seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewModel.getTHRESHOLD().setValue((double) progress/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ReentrantLock lock = new ReentrantLock();

        viewModel.getTHRESHOLD().observe(this,v->{
            binding.nGram.setText(this.getString(R.string.n_gram_algo_n_2)+(v*100)+"%)");
            lock.lock();
            try {
                // Critical section of code
                viewModel.setSearchString(viewModel.getSearchString().getValue());
            } finally {
                lock.unlock();
            }
        });
    }

    private void configActivity() {
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setTopMenus() {
        viewModel.getIsSettingsShown().observe(this,v-> binding.settingsDetails.setVisibility(v? View.VISIBLE : View.GONE));
        viewModel.getIsCalculusShown().observe(this,v-> binding.calculsDetails.setVisibility(v? View.VISIBLE : View.GONE));

        binding.settingsBtn.setOnClickListener(e-> showHideSettings());
        binding.settingsBtnContainer.setOnClickListener(e-> showHideSettings());

        binding.calculsBtn.setOnClickListener(e-> showHideCalculus());
        binding.calculsBtnContainer.setOnClickListener(e-> showHideCalculus());

        setSearchBox();
        setParamsCheckBoxes();
        setCalculusPart();
        calculusResults();
    }

    private void setCalculusPart() {
        viewModel.getDps().observe(this,v->{
            binding.dnps.setText((viewModel.getDs().getValue()-viewModel.getDps().getValue())+"");
            binding.dpns.setText((viewModel.getDpt().getValue()-viewModel.getDps().getValue())+"");
            viewModel.calculatePricision();
            viewModel.calculateRappel();
        });
        binding.dps.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = binding.dps.getText().toString();
                if (!string.equals(binding.dps1.getText().toString()))
                    binding.dps1.setText(string);
                if (!string.isEmpty()) {
                    viewModel.setDps(Integer.parseInt(string));
                }else{
                    viewModel.setDps(0);
                }
            }
        });
        binding.dps1.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = binding.dps1.getText().toString();
                if (!string.equals(binding.dps.getText().toString()))
                    binding.dps.setText(string);
                if (!string.isEmpty()) {
                    viewModel.setDps(Integer.parseInt(string));
                }else{
                    viewModel.setDps(0);
                }
            }
        });

        viewModel.getDnps().observe(this, v->{
            if (v == 0 && !binding.dnps.getText().toString().isEmpty())
                binding.dnps.setText("");
            viewModel.calculateBruit();
        });
        binding.dnps.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = binding.dnps.getText().toString();
                if (!string.isEmpty())
                    viewModel.setDnps(Integer.parseInt(string));
                else
                    viewModel.setDnps(0);
            }
        });

        viewModel.getDpns().observe(this,v->{
            if (v == 0 && !binding.dpns.getText().toString().isEmpty())
                binding.dpns.setText("");
            viewModel.calculateSilence();
        });
        binding.dpns.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = binding.dpns.getText().toString();
                if (!string.isEmpty())
                    viewModel.setDpns(Integer.parseInt(string));
                else
                    viewModel.setDpns(0);
            }
        });

        viewModel.getDpt().observe(this, v->{
            binding.dpns.setText((viewModel.getDpt().getValue()-viewModel.getDps().getValue())+"");
            viewModel.calculateRappel();
            viewModel.calculateSilence();
        });
        binding.dpt.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = binding.dpt.getText();
                binding.dpt1.setText(text.toString());
                if (!text.toString().isEmpty()) {
                    viewModel.setDpt(Integer.parseInt(text.toString()));
                }else
                    viewModel.setDpt(0);
            }
        });

        viewModel.getDs().observe(this, v->{
            binding.dnps.setText((viewModel.getDs().getValue()-viewModel.getDps().getValue())+"");
            viewModel.calculatePricision();
            viewModel.calculateBruit();
        });
        binding.ds.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = binding.ds.getText();
                binding.ds2.setText(text.toString());
                if (!text.toString().isEmpty()) {
                    viewModel.setDs(Integer.parseInt(text.toString()));
                }else
                    viewModel.setDs(0);
            }
        });
    }

    private void setSearchBox() {
        binding.searchBox.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchString(binding.searchBox.getText().toString());
            }
        });
        viewModel.getSearchString().observe(this,v->{
            clearAllTokens();
            String[] values = new String[]{""};
            if (!v.isEmpty()){
                values = viewModel.processInput(v);
                for (String s : values) {
                    TextView text = new TextView(this);
                    text.setText("{"+s+"}");
                    text.setPadding(20,0,20,0);
                    binding.tokensList.addView(text);
                }
            }
            viewModel.nGram(viewModel.docs);
            List<String> docs = viewModel.getBox(BOX_n_gram).getValue() ? ngramFilterList(viewModel.docs,viewModel.getDocsCopy().getValue(),values)  : filterList(viewModel.docs, values);
            setRecyclerView(docs,values);
            binding.selectedDocs.setText("Selected : "+(v.isEmpty() ? 0 : docs.size()) );
        });
    }

    private void showAddDocPopup() {
        // Create a new Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_add_doc);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Initialize views
        EditText input = dialog.findViewById(R.id.doc);
        Button save  = dialog.findViewById(R.id.save);
        Button cancel  = dialog.findViewById(R.id.cancel);

        // Set button click listener
        save.setOnClickListener(v -> {
            String userInput = input.getText().toString();
            viewModel.docs.add(userInput);
            viewModel.incrementDocs();
            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    private List<String> filterList(List<String> docs, String[] values) {
        if (values == null ||values.length == 0 || values[0].isEmpty()) return docs;
        List<String> result = new LinkedList<>();
        for (int i = 0; i < docs.size(); i++) {
            String[] v = viewModel.processInput(docs.get(i));
            if(MainViewModel.checkStringInList(v,values))
                result.add(docs.get(i));
        }
        return result;
    }

    private List<String> ngramFilterList(List<String> docs,List<LinkedList<String>> ngramResult, String[] values) {
        if (values == null || values.length == 0 || values[0].isEmpty()) return docs;
        List<String> result = new LinkedList<>();
        for (int i = 0; i < docs.size(); i++) {
            for (int j = 0; j < ngramResult.get(i).size(); j++) {
                boolean ended = false;
                for (int k = 0; k < values.length; k++) {
                    String root = MainViewModel.sharedRoot(
                            viewModel.clipToGram(values[k], viewModel.getGRAM().getValue()),
                            viewModel.clipToGram(ngramResult.get(i).get(j), viewModel.getGRAM().getValue()),
                            viewModel.getTHRESHOLD().getValue());

                    if (root != null){
                        result.add(docs.get(i));
                        ended = true;
                        break;
                    }
                }
                if (ended)
                    break;
            }
        }
        return result;
    }

    private void setRecyclerView(List<String> docs, String[] values) {
        binding.recycler.setAdapter(null);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        if (values != null)
            binding.recycler.setAdapter(new Adapter(docs,viewModel,values));
        else
            binding.recycler.setAdapter(new Adapter(docs,viewModel));

    }

    private void clearAllTokens() {
        View firstElement = binding.tokensList.getChildAt(0);
        binding.tokensList.removeAllViews();
        binding.tokensList.addView(firstElement);
    }
    private void showHideSettings(){
        if (Boolean.TRUE.equals(viewModel.getIsSettingsShown().getValue()))
            viewModel.hideSettings();
        else
            viewModel.showSettings();
    }
    private void showHideCalculus(){
        if (Boolean.TRUE.equals(viewModel.getIsCalculusShown().getValue()))
            viewModel.hideCalculus();
        else
            viewModel.showCalculus();
    }
}