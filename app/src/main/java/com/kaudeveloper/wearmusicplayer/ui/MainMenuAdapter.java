package com.kaudeveloper.wearmusicplayer.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kaudeveloper.wearmusicplayer.R;

import java.util.ArrayList;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.RecyclerViewHolder> {

    private final AdapterCallback callback;
    private final Context context;
    private ArrayList<com.kaudeveloper.wearmusicplayer.service.Track> dataSource = new ArrayList<com.kaudeveloper.wearmusicplayer.service.Track>();
//    private String drawableIcon;
//    public TextView prevselectedTv =null;
    public int selectedPosition=-1;
    View viewAdapt;
    Typeface defTypeFace=null;
    public MainMenuAdapter(Context context, ArrayList<com.kaudeveloper.wearmusicplayer.service.Track> dataArgs, AdapterCallback callback) {
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewAdapt = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_item, parent, false);

        return new RecyclerViewHolder(viewAdapt);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        com.kaudeveloper.wearmusicplayer.service.Track data_provider = dataSource.get(position);
        holder.menuItem.setText(data_provider.getName());
        //holder.itemView.setBackgroundColor((position == selectedPosition) ? Color.GREEN : Color.WHITE);
        holder.menuIcon.setImageResource(data_provider.getBitmapResId());
        holder.menuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (callback != null) {
                    callback.onItemClicked(position);
                    if(defTypeFace==null)
                    {
                        defTypeFace = holder.menuItem.getTypeface();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public interface AdapterCallback {
        void onItemClicked(Integer menuPosition);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout menuContainer;
        TextView menuItem;
        ImageView menuIcon;

        public RecyclerViewHolder(View view) {
            super(view);
            menuContainer = view.findViewById(R.id.menu_container);
            menuItem = view.findViewById(R.id.menu_item);
            menuItem.setTextColor(Color.WHITE);
            menuIcon = view.findViewById(R.id.menu_icon);
        }
    }
}
