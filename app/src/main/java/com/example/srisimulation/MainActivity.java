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

        binding.settingsBtn.setOnClickListener(e-> showHideSettings());
        binding.settingsBtnContainer.setOnClickListener(e-> showHideSettings());

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
}