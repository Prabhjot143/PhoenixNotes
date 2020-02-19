package com.project.phoenixNotes.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.phoenixNotes.Adapters.NotesListAdapter;
import com.project.phoenixNotes.Database.DBNote;
import com.project.phoenixNotes.Listeners.OnListItemClickListeners;
import com.project.phoenixNotes.Modals.Note;
import com.project.phoenixNotes.R;
import com.project.phoenixNotes.Singelton.SubjectSingleton;
import com.project.phoenixNotes.Utils.ShadowLayout;

import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesActivity extends AppCompatActivity implements OnListItemClickListeners, SearchView.OnQueryTextListener {
    public ArrayList<Note> savedNoteArrayList = new ArrayList<>();
    @BindView(R.id.dashboard_tv)
    TextView dashboardTv;
//    @BindView(R.id.settings_iv)
//    ImageView settingsIv;
    @BindView(R.id.dash_parent_rl)
    RelativeLayout dashParentRl;
    @BindView(R.id.etSearch)
    SearchView etSearch;
    @BindView(R.id.categories_rv)
    RecyclerView categoriesRv;
    @BindView(R.id.add_event_iv)
    ImageView addEventIv;
    @BindView(R.id.rel_ll)
    RelativeLayout relLl;
    @BindView(R.id.dash_parent_fl)
    FrameLayout dashParentFl;

    Context mContext;
    String subjectName;
    DBNote dbNote = new DBNote(this);
    LinearLayoutManager linearLayoutManager;
//    @BindView(R.id.no_event_iv)
//    ImageView noEventIv;
    @BindView(R.id.no_event_tv)
    TextView noEventTv;
    @BindView(R.id.can_add_event_tv)
    TextView canAddEventTv;
    @BindView(R.id.spinner_sl)
    ShadowLayout spinnerSl;
    @BindView(R.id.mdw_view_tv)
    TextView mdwViewTv;
    @BindView(R.id.spinner_parent_rl1)
    RelativeLayout spinnerParentRl1;

    private NotesListAdapter notesListAdapter;
    private int clickCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check);
        ButterKnife.bind(this);
        mContext = NotesActivity.this;

        if (getIntent().getExtras() != null) {
            subjectName = getIntent().getExtras().get("SubjectName").toString();
            dashboardTv.setText(subjectName);
        }

        if (subjectName != null) {
            SubjectSingleton.getInstance().setSubjectName(subjectName);
        } else {
            subjectName = SubjectSingleton.getInstance().getSubjectName();
        }
        etSearch.setOnQueryTextListener(this);
        setupDescAdapter();

        getSupportActionBar().setTitle("Category");

    }


    private void setupDescAdapter() {
        savedNoteArrayList.clear();
        savedNoteArrayList = dbNote.getNoteOfSubject(this, subjectName);
        if (savedNoteArrayList.size() > 0) {
       //     noEventIv.setVisibility(View.INVISIBLE);
            noEventTv.setVisibility(View.INVISIBLE);
            canAddEventTv.setVisibility(View.INVISIBLE);
        } else {
        //    noEventIv.setVisibility(View.VISIBLE);
            noEventTv.setVisibility(View.VISIBLE);
            canAddEventTv.setVisibility(View.VISIBLE);
        }
        notesListAdapter = new NotesListAdapter(mContext, savedNoteArrayList, this);
        linearLayoutManager = new LinearLayoutManager(mContext);
        categoriesRv.setLayoutManager(linearLayoutManager);
        categoriesRv.setAdapter(notesListAdapter);
    }

    @OnClick({ R.id.etSearch, R.id.add_event_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.settings_iv:
//                Collections.sort(savedNoteArrayList, new Comparator<Note>() {
//                    public int compare(Note o1, Note o2) {
//                        return o1.getDateTime().compareTo(o2.getDateTime());
//                    }
//                });
//                notesListAdapter.notifyDataSetChanged();
//                break;
            case R.id.etSearch:
                break;
            case R.id.add_event_iv:
                Intent intentToNote = new Intent(getApplicationContext(), AddNote.class);
                Bundle b = new Bundle();
                b.putString("subjectName", subjectName);
                intentToNote.putExtras(b);
                startActivityForResult(intentToNote, 45);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 45) {
            if (resultCode == Activity.RESULT_OK) {
                if (notesListAdapter != null) {
                    setupDescAdapter();
                }
            }
        }
    }

    @Override
    public void onListItemDelted(String id, final int adapterPos) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Do you really want to delete this note");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "No Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbNote.deleteNote(savedNoteArrayList.get(adapterPos));
                //Delete all the notes in this subject here.
                setupDescAdapter();
            }
        });

        AlertDialog mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();


    }

    @Override
    public void onListItemEdited(String id, int adapterPos, String item_name) {
    }

    @Override
    public void onListAllChecked(boolean isAllChecked) {

    }

    @Override
    public void onItemClicked(String id, int adapterPos) {
        Intent intentToEditNote = new Intent(getApplicationContext(), AddNote.class);
        Bundle editNoteBundle = new Bundle();
        editNoteBundle.putBoolean("isEdit", true);
        editNoteBundle.putSerializable("NoteData", savedNoteArrayList.get(adapterPos));
        intentToEditNote.putExtras(editNoteBundle);
        startActivityForResult(intentToEditNote, 45);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String text = s;
        notesListAdapter.filter(text);
        return false;
    }

    @OnClick(R.id.spinner_sl)
    public void onViewClicked() {

//        clickCount++;
//        if (clickCount == 0) {
//            setupOrderByDate();
//        } else if (clickCount == 1) {
//            setupOrderByTitle();
//        } else if (clickCount == 2) {
//            setupOrderByDesc();
//            clickCount = -1;
//
//        }
    }

//    private void setupOrderByDesc() {
//        spinnerParentRl1.setBackgroundResource(R.drawable.custom_daily_view_background);
//        spinnerSl.setShadowColor(getResources().getColor(R.color.daily_view_shadow));
//        mdwViewTv.setText("By Desc");
//    }
//
//    private void setupOrderByTitle() {
//        spinnerParentRl1.setBackgroundResource(R.drawable.custom_daily_view_background);
//        spinnerSl.setShadowColor(getResources().getColor(R.color.daily_view_shadow));
//        mdwViewTv.setText("By Title");
//    }
//
//    private void setupOrderByDate() {
//        spinnerParentRl1.setBackgroundResource(R.drawable.custom_daily_view_background);
//        spinnerSl.setShadowColor(getResources().getColor(R.color.daily_view_shadow));
//        mdwViewTv.setText("By Date");
//    }
}
