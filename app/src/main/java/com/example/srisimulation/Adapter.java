package com.example.srisimulation;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

    private final List<String> items;
    private  List<String> searchValues = null;
    private final MainViewModel viewModel;
    public Adapter(List<String> items, MainViewModel viewModel){
        this.items = items;
        this.viewModel = viewModel;
    }

    public Adapter(List<String> items, MainViewModel viewModel, String[] values) {
        this.items = items;
        this.viewModel = viewModel;
        this.searchValues = Arrays.asList(values);
    }

    @NonNull
    @Override
    public Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new Holder(inflater.inflate(R.layout.doc_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.Holder holder, int position) {
        ((TextView) holder.view.findViewById(R.id.text)).setText(items.get(position));
        clearAllElements(holder.view);
        if (items.get(position) != null && !items.get(position).isEmpty()){
            String[] values;
            if (viewModel.getBox(MainViewModel.BOX_n_gram).getValue()){
                String currentItem = items.get(position);
                values = viewModel.getDocsCopy().getValue().get(viewModel.docs.indexOf(currentItem)).toArray(new String[0]);
            }else {
                values = viewModel.processInput(items.get(position));
            }

            for (String s : values) {
                TextView text = new TextView(holder.view.getContext());
                text.setText("{"+s+"}");
                if (searchValues != null && (MainViewModel.checkStringInList(s,searchValues) || viewModel.sharedRoot(s,searchValues))){
                    text.setTextColor(Color.BLUE);
                }
                text.setPadding(20,0,20,0);
                ((LinearLayout) holder.view.findViewById(R.id.tokens_list)).addView(text);
            }
        }

        holder.view.findViewById(R.id.imageButton).setOnClickListener(e-> holder.view.findViewById(R.id.tokens_list).setVisibility(holder.view.findViewById(R.id.tokens_list).getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        holder.view.setOnClickListener(e-> holder.view.findViewById(R.id.tokens_list).setVisibility(holder.view.findViewById(R.id.tokens_list).getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        holder.view.setOnLongClickListener(e->{
            items.remove(position);
            viewModel.decrementDocs();
            notifyItemRemoved(position);
            return true;
        });
    }


    public class Holder extends RecyclerView.ViewHolder {
        View view;
        public Holder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
    }
    private void clearAllElements(View itemView) {
        View firstElement = ((LinearLayout) itemView.findViewById(R.id.tokens_list)).getChildAt(0);
        ((LinearLayout) itemView.findViewById(R.id.tokens_list)).removeAllViews();
        ((LinearLayout) itemView.findViewById(R.id.tokens_list)).addView(firstElement);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
