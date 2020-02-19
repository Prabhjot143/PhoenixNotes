package com.project.phoenixNotes.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project.phoenixNotes.Adapters.MyRecyclerViewAdapter;
import com.project.phoenixNotes.Database.DBImage;
import com.project.phoenixNotes.Database.DBNote;
import com.project.phoenixNotes.Modals.Image;
import com.project.phoenixNotes.Modals.Note;
import com.project.phoenixNotes.R;
import com.project.phoenixNotes.Singelton.AudioSingleton;
import com.project.phoenixNotes.Singelton.SubjectSingleton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddNote extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private static final String IMAGE_DIRECTORY = "/dailynote";
    private int GALLERY = 1, CAMERA = 2;
    Button saveNote;
    EditText txtNoteTitle;
    EditText txtNoteContent;
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
    private MyRecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    ArrayList<Bitmap> mImgIds = new ArrayList<Bitmap>();

    DBNote dbSubject = new DBNote(this);

    Note myNoteObj = new Note();
    ArrayList<String> myImagesUrl = new ArrayList<String>(); // use array to save in database
    AudioSingleton audioSingleton = null;
    String audioUrl;
    String subjectName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        saveNote = findViewById(R.id.saveNote);
        txtNoteTitle = findViewById(R.id.txtNoteTitle);
        txtNoteContent = findViewById(R.id.txtNoteContent);

        recyclerView = findViewById(R.id.rvAnimals);

        getLocationPermission();
        getDeviceLocation();

        requestMultiplePermissions();

        Intent intent = this.getIntent();
        Bundle noteData = intent.getExtras();
        subjectName = SubjectSingleton.getInstance().getSubjectName();

        if (noteData != null) {
            isEdit = noteData.getBoolean("isEdit");
            noteIsEdit = (Note) noteData.getSerializable("NoteData");
        }

        if (isEdit == true) {
            Toast.makeText(this, "Note ID " + noteIsEdit.getNoteId(), Toast.LENGTH_SHORT).show();
            txtNoteTitle.setText(noteIsEdit.getNoteTitle());
            txtNoteContent.setText(noteIsEdit.getNoteContent());
            audioUrl = noteIsEdit.getAudio();
            recentLatLng = new LatLng(noteIsEdit.getLatitude(), noteIsEdit.getLongitude());
            if (noteIsEdit.getImage1()!=null){
                myImagesUrl.add(0, noteIsEdit.getImage1());

            }
            if (noteIsEdit.getImage2()!=null){
                myImagesUrl.add(0, noteIsEdit.getImage2());

            }
            if (noteIsEdit.getImage3()!=null){
                myImagesUrl.add(0, noteIsEdit.getImage3());

            }


        }

        /* save image */
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    //UPDATE Database
                    getImageUrl();
                    dbNote.updateNote(populateDataNote());
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
//                    dbImage.updateImage(image);
                } else {
                    //SAVE Database
                    getImageUrl();
                    dbNote.insertNote(populateDataNote());
//                    populateDataImage();
                    Toast.makeText(AddNote.this, "Data saved", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        mInflater = LayoutInflater.from(this);

        setupRecyclerView();

        audioSingleton = AudioSingleton.getInstance();
        Toast.makeText(AddNote.this, "Name " + subjectName, Toast.LENGTH_SHORT).show();

    }

    private void getImageUrl() {
        String imageURL;
        if (mImgIds.size() > 0) {
            for (int i = 0; i < mImgIds.size(); i++) {
                imageURL = saveImage(mImgIds.get(i));
                myImagesUrl.add(imageURL);

            }
        }
    }

    public Note populateDataNote() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date()); // pass date that get from database

        if (isEdit == true) {
            noteIsEdit.setSubjectName(subjectName);
            noteIsEdit.setNoteTitle(txtNoteTitle.getText().toString());
            noteIsEdit.setNoteContent(txtNoteContent.getText().toString());
            noteIsEdit.setAudio(audioSingleton.getAudioUrl());
            noteIsEdit.setDateTime(strDate);
            noteIsEdit.setLatitude(latitude);
            noteIsEdit.setLongitude(longitude);
            if (myImagesUrl.size() > 0) {
                if (myImagesUrl.size() >= 1) {
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

        } else {
            Note note = new Note();
            note.setSubjectName(subjectName);
            note.setNoteTitle(txtNoteTitle.getText().toString());
            note.setNoteContent(txtNoteContent.getText().toString());
            note.setAudio(audioSingleton.getAudioUrl());
            note.setDateTime(strDate);
            note.setLatitude(latitude);
            note.setLongitude(longitude);
            if (myImagesUrl.size() > 0) {
                if (myImagesUrl.size() == 1) {
                    note.setImage1(myImagesUrl.get(0));
                }
                if (myImagesUrl.size() == 2) {
                    note.setImage2(myImagesUrl.get(1));
                }
                if (myImagesUrl.size() == 3) {
                    note.setImage3(myImagesUrl.get(2));
                }
            }
            return note;
        }
    }

    public void populateDataImage() {
        for (int j = 0; j < myImagesUrl.size(); j++) {
            Image image = new Image();
            image.setImageLocation(myImagesUrl.get(j));
//            image.setNoteId();
            dbImage.insertImage(image);
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;


            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddNote.this);
        try {
            if (mLocationPermissionsGranted) {
                final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Toast.makeText(getApplicationContext(), "Longitude" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(AddNote.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        recentLatLng = latLng;
    }


    private void setupRecyclerView() {
        ArrayList<String> noteImgNames = new ArrayList<>();
        // set up the RecyclerView
        if (!isEdit) {
            if (mImgIds.size() > 0) {
                for (int i = 0; i < mImgIds.size(); i++) {
                    noteImgNames.add("");
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(AddNote.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(horizontalLayoutManager);
                    adapter = new MyRecyclerViewAdapter(this, mImgIds, noteImgNames);
                    adapter.setClickListener(this);
                    recyclerView.setAdapter(adapter);
                }
            }
        } else {
            for (int i = 0; i < myImagesUrl.size(); i++) {
                mImgIds.clear();
                mImgIds.add(returnImageBitmap(myImagesUrl.get(i)));
            }

            if (mImgIds.size() > 0) {
                for (int i = 0; i < mImgIds.size(); i++) {
                    noteImgNames.add("");
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(AddNote.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(horizontalLayoutManager);
                    adapter = new MyRecyclerViewAdapter(this, mImgIds, noteImgNames);
                    adapter.setClickListener(this);
                    recyclerView.setAdapter(adapter);
                }
            }
        }
    }

    public Bitmap returnImageBitmap(String imgURL) {
        File imgFile = new File(imgURL);
        Bitmap myBitmap = null;

        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return myBitmap;
    }

    // create an action bar button

    private void appendImgData(Bitmap img) {
        mImgIds.add(img);
    }


    /* Button clicks */
    public void galleryButtonClick(View view) {
        choosePhotoFromGallary();
    }

    public void audioButtonClick(View view) {
        Intent intent = new Intent(AddNote.this, StoredAudioActivity.class);
        intent.putExtra("audiourl", audioUrl);
        intent.putExtra("isEdit", isEdit);
        startActivity(intent);
//        finish();
    }

    public void mapButtonClick(View view) {
        Intent intent = new Intent(AddNote.this, ShowUserLocationActivity.class);
        Toast.makeText(this, recentLatLng.latitude + " "+ recentLatLng.longitude, Toast.LENGTH_LONG).show();
        intent.putExtra("Latlng", recentLatLng);
        startActivity(intent);
    }

    public void cameraButtonClick(View view) {
        takePhotoFromCamera();
    }


    /* Take image from gallery and camera */
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    appendImgData(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            appendImgData(thumbnail);
        }
//        initView();
        setupRecyclerView();
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


    private void requestMultiplePermissions() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                }

                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // show alert dialog navigating to Settings
                    //openSettingsDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
            }
        }).onSameThread()
                .check();
    }

    @Override
    public void onItemClick(View view, int position) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        mImgIds.get(position).compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();

        Intent anotherIntent = new Intent(this, ShowFullImageActivity.class);
        anotherIntent.putExtra("image", byteArray);
        startActivity(anotherIntent);
    }

    /* Record Audio file */

}