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

import com.project.phoenixNotes.Activities.AllNotesActivity;
import com.project.phoenixNotes.Activities.NotesActivity;
import com.project.phoenixNotes.Listeners.OnListItemClickListeners;
import com.project.phoenixNotes.Modals.Note;
import com.project.phoenixNotes.R;

import java.util.ArrayList;
import java.util.Locale;

public class AllNotesListAdapter extends RecyclerView.Adapter<AllNotesListAdapter.NotesListViewHolder> {
    private Context mContext;
    OnListItemClickListeners onListItemClickListeners;
    ArrayList<Note> notesArray;
    private boolean isSelectedAll;
    private int checkCount;
    private AllNotesActivity notesActivity;
    ArrayList<Note> allNotes;



    public AllNotesListAdapter(Context mContext, ArrayList subjectList, OnListItemClickListeners onListItemClickListeners) {
        this.notesArray = subjectList;
        allNotes =  new ArrayList<>();
        allNotes.addAll(notesArray);
        notesActivity = (AllNotesActivity) mContext;
        this.mContext = mContext;
        this.onListItemClickListeners = onListItemClickListeners;
    }

    @NonNull
    @Override
    public AllNotesListAdapter.NotesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.custom_notes_row, parent, false);
        return new AllNotesListAdapter.NotesListViewHolder(rootView);
    }



    @Override
    public void onBindViewHolder(@NonNull final AllNotesListAdapter.NotesListViewHolder holder, final int position) {
        holder.title_tv.setText(notesArray.get(position).getNoteTitle());
        holder.title_tv.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/SF-UI-DISPLAY-BOLD.OTF"));
        holder.date_tv.setText(notesArray.get(position).getDateTime());
        holder.date_tv.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/SF-UI-DISPLAY-BOLD.OTF"));

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
        return notesArray.size();
    }

    public void deleteItem(int index) {
        notesArray.remove(index);
        notifyItemRemoved(index);
    }

    public void addItem(int index) {
//        activeList.add("String");
        notifyItemInserted(index);
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        notesActivity.savedNoteArrayList.clear();
        if (charText.length() == 0) {
            notesActivity.savedNoteArrayList.addAll(allNotes);
        } else {
            for (Note wp: allNotes){
                if (wp.getNoteTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    notesActivity.savedNoteArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
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

    public class NotesListViewHolder extends RecyclerView.ViewHolder {
        private TextView title_tv,date_tv;
        private CheckBox checkBox;
        private ImageView delete_iv, edit_iv;

        public NotesListViewHolder(final View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.title_tv);
            checkBox = itemView.findViewById(R.id.desc_check_box);
            delete_iv = itemView.findViewById(R.id.delete_iv);
            edit_iv = itemView.findViewById(R.id.edit_iv);
            date_tv = itemView.findViewById(R.id.date_tv);

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
                    onListItemClickListeners.onListItemDelted("" + notesArray.get(getAdapterPosition()).getNoteId(), getAdapterPosition());
                }
            });

            edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListeners.onListItemEdited("" + notesArray.get(getAdapterPosition()).getNoteId(), getAdapterPosition(), "");
                }
            });
        }
    }
}
