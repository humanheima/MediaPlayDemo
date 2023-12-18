package com.humanheima.videoplayerdemo.ui.activity;

import android.content.Context;
import android.media.browse.MediaBrowser.MediaItem;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TestMediaSessionAdapter extends RecyclerView.Adapter<TestMediaSessionAdapter.ViewHolder> {


    private List<MediaItem> list;

    public TestMediaSessionAdapter(Context context, List<MediaItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(new android.view.View(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
        }

    }

}