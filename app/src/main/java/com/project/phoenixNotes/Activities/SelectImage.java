package com.project.phoenixNotes.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.TextView;


import com.project.phoenixNotes.Adapters.ImageGalleryAdapter;
import com.project.phoenixNotes.R;
import com.project.phoenixNotes.Utils.PermissionUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SelectImage extends AppCompatActivity {
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.image_gallery_rv)
    RecyclerView imageGalleryRv;
    @BindView(R.id.heading)
    TextView heading;
    private Context mContext;
    private ArrayList image_path;
    private String path;
    private File file;
    private Uri uri;
    private int CAMERA_REQUEST = 67;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        ButterKnife.bind(this);
        mContext = SelectImage.this;

        if (PermissionUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                && PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            getAllShownImagesPath();
        } else {
            PermissionUtils.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
        }

    }


    public void GetClickedImage() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        path = Environment.getExternalStorageDirectory() + "/IMG" + timeStamp + ".jpg";
        file = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(mContext, "com.goodcompany.group5_finalproject_w2019_mad3125.provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("uri", uri.toString());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        } else if (resultCode == RESULT_OK && requestCode == 12) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        }

    }

    private void getAllShownImagesPath() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID + " DESC";

        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);

        //Total number of images
        int count = cursor.getCount();
        image_path = new ArrayList();
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            image_path.add(cursor.getString(dataColumnIndex));
        }
        setAdapter();
        cursor.close();
    }

    private void setAdapter() {
        ImageGalleryAdapter imageGalleryAdapter = new ImageGalleryAdapter(this, image_path);
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        imageGalleryRv.setLayoutManager(staggeredGridLayoutManager);
        imageGalleryRv.setAdapter(imageGalleryAdapter);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }
}
