package com.example.user.capstone.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.user.capstone.R;

import java.util.ArrayList;


public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ItemViewHolder> {

    private ArrayList<Integer> mUrlList;
    private Context mContext;

    public ImageListAdapter(Context mContext) {
        this.mContext = mContext;
        this.mUrlList = new ArrayList<>();
    }

    public void addInsert(ArrayList<Integer> list) {
        mUrlList.clear();
        mUrlList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_image_row, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final int res = mUrlList.get(position);
        holder.photo.setImageResource(res);
    }


    @Override
    public int getItemCount() {
        return mUrlList != null ? mUrlList.size() : 0;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;


        public ItemViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.photo);
        }
    }
}
