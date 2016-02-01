package com.temnogrudova.chatwaytest;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by oleh on 6/21/15.
 */
public class SivAdapter extends RecyclerView.Adapter<SivAdapter.MyViewHolder> {

    int imagePreviewSize;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> images;
    RecyclerView recyclerView;

    public interface onGridClickListener {
        void onItemClick(int pos);
    }
    onGridClickListener pictureClickListener;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        try {
            pictureClickListener = (onGridClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }
                                                                    //, FragmentManager manager
    public SivAdapter(final Context context, final ArrayList<String> images, int imagePreviewSize) {
        this.inflater = LayoutInflater.from(context);
        this.images = images;
        this.context = context;
        this.imagePreviewSize = imagePreviewSize;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        // setting size of miniature
        view.getLayoutParams().height = imagePreviewSize;
        view.getLayoutParams().width = imagePreviewSize;
        view.requestLayout();
        recyclerView = (RecyclerView) parent.findViewById(R.id.recyclerViewPreviews);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String imagePath = images.get(position);
        Uri uri = Uri.parse("file://" + imagePath);
        Glide.with(context).load(uri).thumbnail(0.05f).into(holder.miniature);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView miniature;

        public MyViewHolder(final View itemView) {
            super(itemView);
            miniature = (ImageView) itemView.findViewById(R.id.miniature);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            pictureClickListener.onItemClick(position);
        }
    }
}
