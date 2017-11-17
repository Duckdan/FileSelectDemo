package com.study.fileselectlibrary.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;

import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.emnu.Type;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */

public class VideoFragment extends BaseFragment {


    @Override
    public void setContentView() {
        first = MediaStore.Video.VideoColumns.DISPLAY_NAME;
        second = MediaStore.Video.VideoColumns.DATA;
        third = MediaStore.Video.VideoColumns.SIZE;
        forth = MediaStore.Video.VideoColumns.DATE_MODIFIED;
        setView(R.layout.fragment_video);
    }


    @Override
    protected void findViewById(View view) {
        lvAdapter.setAdapterType(Type.VIDEO);
        lv.setAdapter(lvAdapter);

    }


    @Override
    protected Cursor querySearchData() {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{first, second, third, forth};

        String selection = MediaStore.Audio.AudioColumns.MIME_TYPE + "= ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? ";

        //类型是在http://qd5.iteye.com/blog/1564040找的
        String[] selectionArgs = new String[]{
                "video/quicktime", "video/mp4", "application/vnd.rn-realmedia", "aapplication/vnd.rn-realmedia",
                "video/x-ms-wmv", "video/x-msvideo", "video/3gpp", "video/x-matroska"};

        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, MediaStore.Video.VideoColumns.DATE_MODIFIED + " desc ");

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
