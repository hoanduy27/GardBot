package com.example.gardbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.jetbrains.annotations.NotNull;

public class myAdapter extends FirebaseRecyclerAdapter<model,myAdapter.myviewholder> {

    public myAdapter(@NonNull @NotNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull myviewholder holder, int position, @NonNull @NotNull model model) {
        holder.sensor.setText(model.getSoilMoistureID());
        holder.pump.setText(model.getName());
        holder.pump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.pump.isChecked())
                {
                    holder.linear.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.linear.setVisibility(View.GONE);
                }
            }
        });
        {

        };
    }

    @NonNull
    @NotNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView sensor;
        CheckBox pump;
        LinearLayout linear;
        public myviewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            sensor=(TextView)itemView.findViewById(R.id.sensor);
            pump=(CheckBox)itemView.findViewById(R.id.pump);
            linear=(LinearLayout)itemView.findViewById(R.id.info);
        }
    }

}
