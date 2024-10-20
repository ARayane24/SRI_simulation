package com.example.srisimulation;

import static com.example.srisimulation.MainViewModel.BOX_DELETE_MOT_VIDE;
import static com.example.srisimulation.MainViewModel.BOX_clipByAllSpecialChar;
import static com.example.srisimulation.MainViewModel.BOX_clipByAllSpecialCharButIgnoreChar;
import static com.example.srisimulation.MainViewModel.BOX_clipBySpaces;
import static com.example.srisimulation.MainViewModel.BOX_ignoreCase;
import static com.example.srisimulation.MainViewModel.BOX_trimWordsSoItHasOnly7Char;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MainViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel.getIsSettingsShown().observe(this,v->{
            binding.settingsDetails.setVisibility(v? View.VISIBLE : View.GONE);
        });
        viewModel.getIsCalculusShown().observe(this,v->{
            binding.calculsDetails.setVisibility(v? View.VISIBLE : View.GONE);
        });

        binding.settingsBtn.setOnClickListener(e-> showHideSettings());
        binding.settingsBtnContainer.setOnClickListener(e-> showHideSettings());

        binding.calculsBtn.setOnClickListener(e-> showHideCalculus());
        binding.calculsBtnContainer.setOnClickListener(e-> showHideCalculus());

        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchString(binding.searchBox.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewModel.getSearchString().observe(this,v->{
            clearAllElements();
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
            List<String> docs = filterList(viewModel.docs, values);
            setRecyclerView(docs,values);
            binding.selectedDocs.setText("Selected : "+(v.isEmpty() ? 0 : docs.size()) );
        });

        binding.ignoreCase.setOnClickListener(v-> viewModel.getBox(BOX_ignoreCase).setValue(binding.ignoreCase.isChecked()));
        binding.clipBySpaces.setOnClickListener(v-> viewModel.getBox(BOX_clipBySpaces).setValue(binding.clipBySpaces.isChecked()));
        binding.suppMotVide.setOnClickListener(v-> viewModel.getBox(BOX_DELETE_MOT_VIDE).setValue(binding.suppMotVide.isChecked()));
        binding.clipByAllSpecialChar.setOnClickListener(v-> viewModel.getBox(BOX_clipByAllSpecialChar).setValue(binding.clipByAllSpecialChar.isChecked()));
        binding.clipByAllSpecialCharButIgnoreChar.setOnClickListener(v-> viewModel.getBox(BOX_clipByAllSpecialCharButIgnoreChar).setValue(binding.clipByAllSpecialCharButIgnoreChar.isChecked()));
        binding.trimWordsSoItHasOnly7Char.setOnClickListener(v-> viewModel.getBox(BOX_trimWordsSoItHasOnly7Char).setValue(binding.trimWordsSoItHasOnly7Char.isChecked()));

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

        setRecyclerView(viewModel.docs,null);
        binding.addDoc.setOnClickListener(e->showAddDocPopup());
        viewModel.numberDocs.observe(this,n->{
            binding.totalDocs.setText("Total docs : "+ n);
            String v = viewModel.getSearchString().getValue();
            assert v != null;
            String[] values = viewModel.processInput(v);
            setRecyclerView(filterList(viewModel.docs,values),values);
        });

        viewModel.getDps().observe(this,v->{
            binding.dnps.setText((viewModel.getDs().getValue()-viewModel.getDps().getValue())+"");
            binding.dpns.setText((viewModel.getDpt().getValue()-viewModel.getDps().getValue())+"");
            viewModel.calculatePricision();
            viewModel.calculateRappel();
        });
        binding.dps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.dps1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        viewModel.getDnps().observe(this, v->{
            if (v == 0 && !binding.dnps.getText().toString().isEmpty())
                binding.dnps.setText("");
            viewModel.calculateBruit();
        });
        binding.dnps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = binding.dnps.getText().toString();
                if (!string.isEmpty())
                    viewModel.setDnps(Integer.parseInt(string));
                else
                    viewModel.setDnps(0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        viewModel.getDpns().observe(this,v->{
            if (v == 0 && !binding.dpns.getText().toString().isEmpty())
                binding.dpns.setText("");
            viewModel.calculateSilence();
        });
        binding.dpns.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = binding.dpns.getText().toString();
                if (!string.isEmpty())
                    viewModel.setDpns(Integer.parseInt(string));
                else
                    viewModel.setDpns(0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        viewModel.getDpt().observe(this, v->{
            binding.dpns.setText((viewModel.getDpt().getValue()-viewModel.getDps().getValue())+"");
            viewModel.calculateRappel();
            viewModel.calculateSilence();
        });
        binding.dpt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = binding.dpt.getText();
                binding.dpt1.setText(text.toString());
                if (!text.toString().isEmpty()) {
                    viewModel.setDpt(Integer.parseInt(text.toString()));
                }else
                    viewModel.setDpt(0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        viewModel.getDs().observe(this, v->{
            binding.dnps.setText((viewModel.getDs().getValue()-viewModel.getDps().getValue())+"");
            viewModel.calculatePricision();
            viewModel.calculateBruit();
        });
        binding.ds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text = binding.ds.getText();
                binding.ds2.setText(text.toString());
                if (!text.toString().isEmpty()) {
                    viewModel.setDs(Integer.parseInt(text.toString()));
                }else
                    viewModel.setDs(0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        viewModel.getBruit().observe(this,v->{
            if (v>=0)
                binding.bruit.setText("= "+v.toString());
            else
                binding.bruit.setText("= NC");
           // viewModel.calculatePricision();
        });
        viewModel.getSilence().observe(this, v->{
            if (v>=0)
                binding.silence.setText("= "+v.toString());
            else
                binding.silence.setText("= NC");
            //viewModel.getRappel();
        });
        viewModel.getRappel().observe(this, v->{
            if (v>=0) {
                binding.rappel.setText("= " + v);
                binding.rappelResult.setText(v.toString());
            }
            else {
                binding.rappel.setText("= NC");
                binding.rappelResult.setText("NC");
            }
            viewModel.calculateSilence();
        });
        viewModel.getPrecision().observe(this,v->{
            if (v>=0) {
                binding.precision.setText("= " + v);
                binding.precisionResult.setText(v.toString());
            }
            else {
                binding.precision.setText("= NC");
                binding.precisionResult.setText("NC");
            }
            viewModel.calculateBruit();
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
        if (values == null || values[0].isEmpty()) return docs;
        List<String> result = new LinkedList<>();
        for (int i = 0; i < docs.size(); i++) {
            String[] v = viewModel.processInput(docs.get(i));
            if(MainViewModel.checkStringInList(v,values))
                result.add(docs.get(i));
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

    private void clearAllElements() {
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