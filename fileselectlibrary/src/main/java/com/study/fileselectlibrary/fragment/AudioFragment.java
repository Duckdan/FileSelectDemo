package com.study.fileselectlibrary.fragment;


import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;

import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.emnu.Type;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFragment extends BaseFragment {


    @Override
    public void setContentView() {
        first = MediaStore.Audio.AudioColumns.DISPLAY_NAME;
        second = MediaStore.Audio.AudioColumns.DATA;
        third = MediaStore.Audio.AudioColumns.SIZE;
        forth = MediaStore.Audio.AudioColumns.DATE_MODIFIED;
        setView(R.layout.fragment_video);
    }

    @Override
    protected void findViewById(View view) {
        lvAdapter.setAdapterType(Type.AUDIO);
        lv.setAdapter(lvAdapter);
    }


    @Override
    protected Cursor querySearchData() {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{first, second, third, forth};

        String selection = MediaStore.Audio.AudioColumns.MIME_TYPE + "= ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? ";

        String[] selectionArgs = new String[]{
                "audio/mpeg", "audio/x-ms-wma", "audio/x-wav", "audio/midi"};


        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, MediaStore.Audio.AudioColumns.DATE_MODIFIED + " desc ");
        return cursor;

    }

    @Override
    protected void updateView(List<FileItem> items) {
        if (items != null && items.size() > 0) {
            Collections.sort(lists);
            lvAdapter.notifyDataSetChanged();
        } else {
            lv.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }
}
