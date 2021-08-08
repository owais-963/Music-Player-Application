package com.example.musicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    String[] arr;
    Context context;

    public PlaylistAdapter(String[] arr, Context context) {
        this.arr = arr;
        this.context = context;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.song_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull
                                             PlaylistAdapter.ViewHolder holder, int position) {

        holder.textView.setText((String)Array.get(arr,position));
        holder.textView.setSelected(true);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return arr.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

           textView=itemView.findViewById(R.id.textView);
        }
    }
}
