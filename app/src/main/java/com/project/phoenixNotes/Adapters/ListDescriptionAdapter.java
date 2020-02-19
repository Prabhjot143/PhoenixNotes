package com.project.phoenixNotes.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.phoenixNotes.Listeners.OnListItemClickListeners;
import com.project.phoenixNotes.Modals.Subject;
import com.project.phoenixNotes.R;

import java.util.ArrayList;

 
public class ListDescriptionAdapter extends RecyclerView.Adapter<ListDescriptionAdapter.ListDescriptionVieHolder> {
    private Context mContext;
    OnListItemClickListeners onListItemClickListeners;
    ArrayList<Subject> categories;
    private boolean isSelectedAll;
    private int checkCount;


    public ListDescriptionAdapter(Context mContext, ArrayList subjectList, OnListItemClickListeners onListItemClickListeners) {
        this.categories = subjectList;
        this.mContext = mContext;
        this.onListItemClickListeners = onListItemClickListeners;
    }

    @NonNull
    @Override
    public ListDescriptionAdapter.ListDescriptionVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.custom_list_desc_row, parent, false);
        return new ListDescriptionAdapter.ListDescriptionVieHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListDescriptionAdapter.ListDescriptionVieHolder holder, final int position) {
        holder.title_tv.setText(categories.get(position).getSubjectName());
        holder.title_tv.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/SF-UI-DISPLAY-BOLD.OTF"));
        if (categories.get(position).getSubjectName().equalsIgnoreCase("default")){
            holder.delete_iv.setVisibility(View.INVISIBLE);
        }else {
            holder.delete_iv.setVisibility(View.VISIBLE);

        }

//        if (notesArray.get(position).getChecked()) {
//            holder.checkBox.setChecked(true);
//        } else {
//            holder.checkBox.setChecked(false);
//        }
//
//        if (!isSelectedAll)
//            holder.checkBox.setChecked(false);
//        else
//            holder.checkBox.setChecked(true);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void deleteItem(int index) {
        categories.remove(index);
        notifyItemRemoved(index);
    }

    public void addItem(int index) {
//        activeList.add("String");
        notifyItemInserted(index);
    }


//    public void selectAll() {
//        isSelectedAll = true;
//        for (int i = 0; i < notesArray.size(); i++) {
//            notesArray.get(i).setChecked(true);
//            checkCount++;
//        }
//        notifyDataSetChanged();
//        checkCount= notesArray.size();
//    }

//    public StringBuilder getCheckItemsId() {
//        StringBuilder idsBuilder = new StringBuilder();
//        for (int i = 0; i < notesArray.size(); i++) {
//            if (notesArray.get(i).getChecked()) {
//                idsBuilder.append(",").append(notesArray.get(i).getId());
//            }
//        }
//        idsBuilder.deleteCharAt(0);
//        Log.e("Check", "getCheckItemsId: " + idsBuilder);
//        return idsBuilder;
//    }

    public class ListDescriptionVieHolder extends RecyclerView.ViewHolder {
        private TextView title_tv;
        private CheckBox checkBox;
        private ImageView delete_iv, edit_iv;

        public ListDescriptionVieHolder(final View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.title_tv);
            checkBox = itemView.findViewById(R.id.desc_check_box);
            delete_iv = itemView.findViewById(R.id.delete_iv);
            edit_iv = itemView.findViewById(R.id.edit_iv);

//            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    if (notesArray.get(getAdapterPosition()).getChecked()) {
//                        checkBox.setChecked(false);
//                        notesArray.get(getAdapterPosition()).setChecked(false);
//                        checkCount--;
//                        Log.e("unchecked", "onCheckedChanged: " + checkCount);
//                    } else {
//                        checkBox.setChecked(true);
//                        checkCount++;
//                        notesArray.get(getAdapterPosition()).setChecked(true);
//                        Log.e("Checked", "onCheckedChanged: " + checkCount);
//                    }
//
//                    if (checkCount == notesArray.size()) {
//                        onListItemClickListeners.onListAllChecked(true);
//                    } else {
//                        onListItemClickListeners.onListAllChecked(false);
//                    }
//                }
//
//            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListeners.onItemClicked("",getAdapterPosition());
                }
            });

            delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListeners.onListItemDelted("" + categories.get(getAdapterPosition()).getSubjectId(), getAdapterPosition());
                }
            });

            edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListeners.onListItemEdited("" + categories.get(getAdapterPosition()).getSubjectId(), getAdapterPosition(), "");
                }
            });
        }
    }
}