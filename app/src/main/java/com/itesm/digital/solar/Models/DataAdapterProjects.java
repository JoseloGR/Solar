package com.itesm.digital.solar.Models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itesm.digital.solar.Interfaces.RecyclerViewClickListener;
import com.itesm.digital.solar.R;

import java.util.ArrayList;

public class DataAdapterProjects extends RecyclerView.Adapter<DataAdapterProjects.ViewHolder>{

    private RecyclerViewClickListener mListener;
    private ArrayList<Project> projects;

    public DataAdapterProjects(ArrayList<Project> projects, RecyclerViewClickListener listener){
        this.projects = projects;
        this.mListener = listener;
    }

    @Override
    public DataAdapterProjects.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(DataAdapterProjects.ViewHolder viewHolder, int i) {

        viewHolder.tv_name.setText(projects.get(i).getName());
        viewHolder.tv_address.setText(projects.get(i).getAddress());
        viewHolder.tv_cost.setText(projects.get(i).getCost());
        viewHolder.tv_date.setText(projects.get(i).getDate());
        viewHolder.tv_surface.setText(projects.get(i).getSurface());
        viewHolder.tv_id.setText(projects.get(i).getId());
        viewHolder.tv_user_id.setText(projects.get(i).getUserId());

        //viewHolder.bind(projects.get(i), listener);

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;

        private TextView tv_name,tv_address,tv_cost, tv_date, tv_surface, tv_id, tv_user_id;
        public ViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);

            mListener = listener;

            tv_name = (TextView)view.findViewById(R.id.tv_name);
            tv_address = (TextView)view.findViewById(R.id.tv_address);
            tv_cost = (TextView)view.findViewById(R.id.tv_cost);
            tv_date = (TextView)view.findViewById(R.id.tv_date);
            tv_surface = (TextView)view.findViewById(R.id.tv_surface);
            tv_id = (TextView)view.findViewById(R.id.tv_id);
            tv_user_id = (TextView)view.findViewById(R.id.tv_user_id);

            view.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
