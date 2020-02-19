package com.project.phoenixNotes.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.project.phoenixNotes.Database.DBImage;
import com.project.phoenixNotes.Database.DBNote;
import com.project.phoenixNotes.Modals.Image;
import com.project.phoenixNotes.Modals.Note;
import com.project.phoenixNotes.R;
import com.project.phoenixNotes.Singelton.SubjectSingleton;
import com.project.phoenixNotes.Utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNoteActivity extends AppCompatActivity {

    @BindView(R.id.txtNoteTitle)
    EditText txtNoteTitle;
    @BindView(R.id.txtNoteContent)
    EditText txtNoteContent;
    @BindView(R.id.saveNote)
    Button saveNote;
    @BindView(R.id.rvAnimals)
    RecyclerView rvAnimals;
    @BindView(R.id.btnMap)
    FloatingActionButton btnMap;
    @BindView(R.id.btn)
    FloatingActionMenu btn;

    private static final String TAG = "AddNote";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    LatLng recentLatLng = null;
    double latitude;
    double longitude;

    boolean isEdit = false;
    DBImage dbImage = new DBImage(this);
    DBNote dbNote = new DBNote(this);

    Note noteIsEdit = new Note();         //if is edit option is selected ude this array
    Image imageIsEdit = new Image();

    private LinearLayout mGallery;
    private LayoutInflater mInflater;
    private HorizontalScrollView horizontalScrollView;

    RecyclerView recyclerView;
    ArrayList<Bitmap> mImgIds = new ArrayList<>();
    ArrayList<String> mImgUrls = new ArrayList<>();

    DBNote dbSubject = new DBNote(this);

    Note myNoteObj = new Note();
    ArrayList<String> myImagesUrl = new ArrayList<String>(); // use array to save in database
//    AudioSingleton audioSingleton = null;

    String audioUrl;

    String subjectName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);


        Intent intent = this.getIntent();
        Bundle noteData = intent.getExtras();
        subjectName = SubjectSingleton.getInstance().getSubjectName();

        if (noteData != null){
            isEdit = noteData.getBoolean("isEdit");
            noteIsEdit = (Note) noteData.getSerializable("NoteData");
        }

        if(isEdit == true){
            txtNoteTitle.setText(noteIsEdit.getNoteTitle());
            txtNoteContent.setText(noteIsEdit.getNoteContent());
        }
        getSupportActionBar().setTitle("Add Note");
    }

    public Note populateDataNote(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date()); // pass date that get from database

        if (isEdit == true){
//            Note note = new Note();
            noteIsEdit.setSubjectName(subjectName);
            noteIsEdit.setNoteTitle(txtNoteTitle.getText().toString());
            noteIsEdit.setNoteContent(txtNoteContent.getText().toString());
//            noteIsEdit.setAudio(audioSingleton.getAudioUrl());
            noteIsEdit.setDateTime(strDate);
//            noteIsEdit.setLatitude(latitude);
//            noteIsEdit.setLongitude(longitude);
//        note.setImageId();
            if (myImagesUrl.size() > 0) {
                if (myImagesUrl.size() >=1) {
                    noteIsEdit.setImage1(myImagesUrl.get(0));
                }
                if (myImagesUrl.size() >= 2) {
                    noteIsEdit.setImage2(myImagesUrl.get(1));
                }
                if (myImagesUrl.size() >= 3) {
                    noteIsEdit.setImage3(myImagesUrl.get(2));
                }
            }
            return noteIsEdit;

        }
        else {
            Note note = new Note();
            note.setSubjectName(subjectName);
            note.setNoteTitle(txtNoteTitle.getText().toString());
            note.setNoteContent(txtNoteContent.getText().toString());
//            note.setAudio(audioSingleton.getAudioUrl());
            note.setDateTime(strDate);
//            note.setLatitude(latitude);
//            note.setLongitude(longitude);
//        note.setImageId();
            if (myImagesUrl.size() > 0)
            {
                if (myImagesUrl.size() == 1)
                {
                    note.setImage1(myImagesUrl.get(0));
                }
                if (myImagesUrl.size() == 2)
                {
                    note.setImage2(myImagesUrl.get(1));
                }
                if (myImagesUrl.size() == 3) {
                    note.setImage3(myImagesUrl.get(2));
                }
            }
            return note;
        }
    }


    @OnClick({R.id.saveNote, R.id.rvAnimals, R.id.btnMap, R.id.btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveNote:
                if (isEdit){
                    //UPDATE Database
                    dbNote.updateNote(populateDataNote());
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
                else{
                    //SAVE Database
                    dbNote.insertNote(populateDataNote());
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
                break;
            case R.id.rvAnimals:
                break;
            case R.id.btnMap:
                break;
            case R.id.btn:
                break;
        }
    }

    public void cameraButtonClick(View view) {
        Intent i = new Intent(this, SelectImage.class);
        startActivityForResult(i, Constants.REQUEST_CODE_FROM_FACE_CHOOSE);
    }

    public void galleryButtonClick(View view) {
    }

    public void audioButtonClick(View view) {
    }

    public void mapButtonClick(View view) {
    }
}
