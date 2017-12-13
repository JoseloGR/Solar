package com.itesm.digital.solar.Models;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itesm.digital.solar.HomeResults;
import com.itesm.digital.solar.Interfaces.RecyclerViewClickListener;
import com.itesm.digital.solar.R;

import java.util.ArrayList;

public class DataAdapterAlternatives extends RecyclerView.Adapter<DataAdapterAlternatives.ViewHolder>{

    private Context context;
    private ArrayList<Alternatives> alternatives;
    private RecyclerViewClickListener clickListener;

    public DataAdapterAlternatives(ArrayList<Alternatives> alternatives, Context contexts){
        this.alternatives = alternatives;
        this.context = contexts;
    }

    @Override
    public DataAdapterAlternatives.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_alternatives, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataAdapterAlternatives.ViewHolder viewHolder, final int i) {

        viewHolder.alt_gen_energy.setText( String.valueOf( Math.round( Float.valueOf(alternatives.get(i).getGeneratedEnergy())) ));
        viewHolder.alt_roi.setText(String.valueOf( Math.round(Float.valueOf(alternatives.get(i).getRoi())) ));
        viewHolder.alt_payback.setText(alternatives.get(i).getPayback());
        viewHolder.alt_ganancias.setText(String.valueOf( Math.round(Float.valueOf(alternatives.get(i).getGanancias())) ));
    }

    @Override
    public int getItemCount() {
        return alternatives.size();
    }

    public void setClickListener(RecyclerViewClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView alt_gen_energy,alt_roi,alt_payback, alt_ganancias;

        public ViewHolder(View view) {
            super(view);

            alt_gen_energy = (TextView)view.findViewById(R.id.alt_gen_energy);
            alt_roi = (TextView)view.findViewById(R.id.alt_roi);
            alt_payback = (TextView)view.findViewById(R.id.alt_payback);
            alt_ganancias = (TextView)view.findViewById(R.id.alt_ganancias);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());
        }
    }
}
