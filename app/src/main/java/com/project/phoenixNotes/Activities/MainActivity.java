package com.project.phoenixNotes.Activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.Toast;

import com.project.phoenixNotes.Adapters.ListDescriptionAdapter;
import com.project.phoenixNotes.Database.DBNote;
import com.project.phoenixNotes.Database.DBSubject;
import com.project.phoenixNotes.Listeners.OnListItemClickListeners;
import com.project.phoenixNotes.Modals.Subject;
import com.project.phoenixNotes.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnListItemClickListeners {

    DBSubject dbSubject = new DBSubject(MainActivity.this);
    DBNote dbNote = new DBNote(MainActivity.this);
    ArrayList<Subject> savedSubjectsArraylist = new ArrayList<>();
//    @BindView(R.id.dashboard_tv)
//    TextView dashboardTv;
//    @BindView(R.id.settings_iv)
//    ImageView settingsIv;
//    @BindView(R.id.dash_parent_rl)
//    RelativeLayout dashParentRl;
//    @BindView(R.id.active_list_parent_ll)
//    LinearLayout activeListParentLl;
    @BindView(R.id.categories_rv)
    RecyclerView categoriesRv;
    @BindView(R.id.add_event_iv)
    ImageView addEventIv;
//    @BindView(R.id.rel_ll)
//    RelativeLayout relLl;
    @BindView(R.id.dash_parent_fl)
    FrameLayout dashParentFl;
    private LinearLayoutManager linearLayoutManager;
    private ListDescriptionAdapter listDescriptionAdapter;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = MainActivity.this;

      //  dbSubject.insertSubject(addSubject("Default"));
//        dbSubject.insertSubject(addSubject("Gmail"));
//        dbSubject.insertSubject(addSubject("Trash"));

        savedSubjectsArraylist = dbSubject.getAllSubject(mContext);
        setupDescAdapter();
        getSupportActionBar().setTitle("Categories");



    }

    private void setupDescAdapter() {
        savedSubjectsArraylist = dbSubject.getAllSubject(mContext);
        listDescriptionAdapter = new ListDescriptionAdapter(mContext, savedSubjectsArraylist, this);
        linearLayoutManager = new LinearLayoutManager(mContext);
        categoriesRv.setLayoutManager(linearLayoutManager);
        categoriesRv.setAdapter(listDescriptionAdapter);
    }

//    public void showDialog() {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//        alertDialogBuilder.setTitle("Add Subject");
//        final View dialogView = getLayoutInflater().inflate(R.layout.add_subject, null);
//        alertDialogBuilder.setView(dialogView);
//
//
//
//        alertDialogBuilder.setCancelable(false);
//
//        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i)
//            {
//                Toast.makeText(getApplicationContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i)
//            {
//                addSubjectEditBox = (EditText) dialogView.findViewById(R.id.addSubjectEditText);
//
//                dbSubject.insertSubject(addSubject(addSubjectEditBox.getText().toString()));
//                Toast.makeText(getApplicationContext(), "Save Clicked " + addSubjectEditBox.getText().toString(), Toast.LENGTH_SHORT).show();
//
//                Intent refresh = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(refresh);
//            }
//        });
//
//        AlertDialog mAlertDialog = alertDialogBuilder.create();
//        mAlertDialog.show();
//
//    }

    public Subject addSubject(String subjectName) {
        Subject addSubject = new Subject();
        addSubject.setSubjectName(subjectName);
        return addSubject;
    }

    @OnClick({ R.id.add_event_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.dashboard_tv:
//                break;
//            case R.id.settings_iv:
//                break;
            case R.id.add_event_iv:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Category");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    private String m_Text;
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        boolean flag = false;
                        if (m_Text!=null && m_Text.length()>0){
                            if (savedSubjectsArraylist!=null && savedSubjectsArraylist.size()>0){
                                for (int i= 0 ; i < savedSubjectsArraylist.size(); i++){
                                    if (m_Text.contains(savedSubjectsArraylist.get(i).getSubjectName())){
                                        flag = true;
                                    }
                                }
                                if (flag == false){
                                    dbSubject.insertSubject(addSubject(m_Text));
                                    setupDescAdapter();
                                }

                            }else {
                                dbSubject.insertSubject(addSubject(m_Text));
                                setupDescAdapter();
                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
        }
    }

    @Override
    public void onListItemDelted(String id, final int adapterPos) {
        showDialogForDelete(adapterPos);

    }

    public void showDialogForDelete(final int i1) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Do you want to delete this Subject, All the notes will be deleted!!!");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "No Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dbSubject.deleteSubject(savedSubjectsArraylist.get(i1));
                dbNote.deleteNoteWithSubject((savedSubjectsArraylist.get(i1)));
                //Delete all the notes in this subject here.
                setupDescAdapter();
                listDescriptionAdapter.notifyDataSetChanged();
            }
        });



        AlertDialog mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();
    }

    @Override
    public void onListItemEdited(String id, final int adapterPos, String item_name) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Do you really want to delete this Subject, All notes will be deleted!!!");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "No Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Subject subject = new Subject();
                subject = savedSubjectsArraylist.get(adapterPos);

                dbSubject.deleteSubject(savedSubjectsArraylist.get(adapterPos));
                dbNote.deleteNoteWithSubject((savedSubjectsArraylist.get(adapterPos)));

                //Delete all the notes in this subject here.
                setupDescAdapter();
                listDescriptionAdapter.notifyDataSetChanged();
            }
        });



        AlertDialog mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();

    }

    @Override
    public void onListAllChecked(boolean isAllChecked) {

    }

    @Override
    public void onItemClicked(String id, int adapterPos) {
        Intent intentToNote = new Intent(mContext, NotesActivity.class);
        intentToNote.putExtra("SubjectName", savedSubjectsArraylist.get(adapterPos).getSubjectName());
        startActivity(intentToNote);
    }

    public void btnAllNotesClick(View v){
        Intent inent = new Intent(this, AllNotesActivity.class);

        startActivity(inent);

    }
}
