package com.project.phoenixNotes.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.project.phoenixNotes.Activities.SelectImage;
import com.project.phoenixNotes.R;

import java.util.ArrayList;


/**
 * Created by iapp on 5/3/18.
 */

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageGalleryViewHolder> {
    Activity mContext;
    ArrayList image_path;

    public ImageGalleryAdapter(Activity context, ArrayList image_path) {
        mContext = context;
        this.image_path = image_path;
    }

    @Override
    public ImageGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.image_gallery_row, parent, false);
        return new ImageGalleryViewHolder(rootView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ImageGalleryViewHolder holder, final int position) {

        if (position == 0) {
            holder.custom_image_gallery.setScaleType(ImageView.ScaleType.CENTER);
            Glide.with(mContext)
                    .load(R.drawable.camera1)
                    .into(holder.custom_image_gallery);
            holder.custom_image_gallery
                    .setBackground(mContext.getResources().getDrawable(R.drawable.camera_image_background));

        } else {
            holder.custom_image_gallery.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(mContext)
                    .load(image_path.get(position))
                    .into(holder.custom_image_gallery);
            holder.custom_image_gallery
                    .setBackground(mContext.getResources().getDrawable(R.drawable.loading2));
        }


    }

    @Override
    public int getItemCount() {
        return image_path.size();
    }

    public class ImageGalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView custom_image_gallery;

        public ImageGalleryViewHolder(View itemView) {
            super(itemView);
            custom_image_gallery = itemView.findViewById(R.id.custom_image_gallery);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() == 0) {
                        ((SelectImage) mContext).GetClickedImage();
                    } else {
                        String imagePath = image_path.get(getAdapterPosition()).toString();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("gallery", imagePath);
                        mContext.setResult(Activity.RESULT_CANCELED, returnIntent);
                        mContext.finish();
                    }
                }
            });
        }
    }
}
