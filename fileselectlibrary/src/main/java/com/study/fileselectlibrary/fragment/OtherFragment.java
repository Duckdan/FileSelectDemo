package com.study.fileselectlibrary.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;

import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.emnu.Type;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */

public class OtherFragment extends BaseFragment {

    @Override
    public void setContentView() {
        first = MediaStore.Files.FileColumns.DISPLAY_NAME;
        second = MediaStore.Files.FileColumns.DATA;
        third = MediaStore.Files.FileColumns.SIZE;
        forth = MediaStore.Files.FileColumns.DATE_MODIFIED;
        setView(R.layout.fragment_video);
    }

    @Override
    protected void findViewById(View view) {
        lvAdapter.setAdapterType(Type.OTHER);
        lv.setAdapter(lvAdapter);
    }


    @Override
    protected Cursor querySearchData() {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED};

        //分别对应 txt doc pdf ppt xls wps docx pptx xlsx 类型的文档
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";

        String[] selectionArgs = new String[]{"text/plain", "application/zip"};

        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                selection, selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");

        return cursor;
    }

    @Override
    public boolean scannerFile(String path) {
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            return true;
        }
        return false;
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
