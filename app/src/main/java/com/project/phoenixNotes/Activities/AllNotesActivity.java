package com.project.phoenixNotes.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;


import com.project.phoenixNotes.Adapters.AllNotesListAdapter;
import com.project.phoenixNotes.Database.DBNote;
import com.project.phoenixNotes.Listeners.OnListItemClickListeners;
import com.project.phoenixNotes.Modals.Note;
import com.project.phoenixNotes.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllNotesActivity extends AppCompatActivity implements OnListItemClickListeners, SearchView.OnQueryTextListener {
    public ArrayList<Note> savedNoteArrayList = new ArrayList<>();
    @BindView(R.id.etSearch)
    SearchView etSearch;
    @BindView(R.id.categories_rv)
    RecyclerView categoriesRv;
    Context mContext;
    DBNote dbNote = new DBNote(this);
    public AllNotesListAdapter notesListAdapter;
    LinearLayoutManager linearLayoutManager;
    private int clickCount = 0;
    Toolbar tb1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notes);
        ButterKnife.bind(this);
        mContext = AllNotesActivity.this;
        etSearch.setOnQueryTextListener(this);
        setupDescAdapter();

        tb1 = findViewById(R.id.toolbar);
        setSupportActionBar(tb1);
        getSupportActionBar().setTitle("All Notes");
        tb1.setTitleTextColor(getResources().getColor(R.color.white));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort) {


            Collections.sort(savedNoteArrayList,new Comparator<Note>(){
                @Override
                public int compare(Note o1, Note o2) {
                    return o2.getDateTime().compareTo(o1.getDateTime());
                }

            });

            notesListAdapter.notifyDataSetChanged();
            Toast.makeText(AllNotesActivity.this, "Action Sort clicked", Toast.LENGTH_LONG).show();
            return true;
        }
//        if (id == R.id.sortdate) {
//            Toast.makeText(AllNotesActivity.this, "Action Sort Date clicked", Toast.LENGTH_LONG).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }



    private void setupDescAdapter() {
        savedNoteArrayList.clear();

        savedNoteArrayList = dbNote.getAllNote(this);
        notesListAdapter = new AllNotesListAdapter(mContext, savedNoteArrayList, this);
        linearLayoutManager = new LinearLayoutManager(mContext);
        categoriesRv.setLayoutManager(linearLayoutManager);
        categoriesRv.setAdapter(notesListAdapter);
    }


    @OnClick({ R.id.etSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.etSearch:
                break;
//            case R.id.add_event_iv:
//                Intent intentToNote = new Intent(getApplicationContext(), AddNote.class);
//                Bundle b = new Bundle();
//                b.putString("subjectName", subjectName);
//                intentToNote.putExtras(b);
//                startActivityForResult(intentToNote, 45);
//                break;
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
        alertDialogBuilder.setTitle("Confirm to delete this note");
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

    public void onViewCategoriesClick(View view){

        Intent inent = new Intent(this, MainActivity.class);

        // calling an activity using <intent-filter> action name
        //  Intent inent = new Intent("com.hmkcode.android.ANOTHER_ACTIVITY");

        startActivity(inent);
    }







}
