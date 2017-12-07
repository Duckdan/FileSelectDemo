package com.study.fileselectlibrary.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.adapter.ImageAdapter;
import com.study.fileselectlibrary.bean.FileItem;

import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */

public class PictureFragment extends BaseFragment {


    GridView gv;
    private ImageAdapter imageAdapter;
    private ImageLoader imageLoader;

    @Override
    public void setContentView() {
        first = MediaStore.Images.ImageColumns.DISPLAY_NAME;
        second = MediaStore.Images.ImageColumns.DATA;
        third = MediaStore.Images.ImageColumns.SIZE;
        forth = MediaStore.Images.ImageColumns.DATE_MODIFIED;
        imageLoader = ImageLoader.getInstance();
        setView(R.layout.fragment_picture);
    }

    @Override
    protected void findViewById(View view) {
        gv = (GridView) view.findViewById(R.id.gv);

        if (imageAdapter == null) {
            imageAdapter = new ImageAdapter(context, lists);
            gv.setAdapter(imageAdapter);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FileItem fileItem = lists.get(position);

                    CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
                    boolean checked = fileItem.isChecked() ? false : true;
                    if (checked) {
                        if (selectedList.size() >= defaultCount) {
                            Toast.makeText(context, "发送文件数量不可超过" + defaultCount + "个！", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        pathSet.add(fileItem.getPath());
                        selectedList.add(fileItem);
                    } else {
                        pathSet.remove(fileItem.getPath());
                        removeFileItem(fileItem.getPath());
                    }
                    if (selectedList.size() > 0) {
                        tvTitle.setText("已选" + selectedList.size() + "个");
                        btSend.setEnabled(true);
                    } else {
                        btSend.setEnabled(false);
                        tvTitle.setText(titleName);
                    }

                    calculator();
                    cb.setChecked(checked);
                    fileItem.setChecked(checked);
                }
            });

            gv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    switch (scrollState) {
                        //空闲
                        case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                            Fresco.getImagePipeline().resume();
                            imageLoader.resume();
                            break;
                        //飞滑
                        case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
//                            Fresco.getImagePipeline().pause();
                            imageLoader.pause();
                            break;
                        //触摸滚动
                        case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                            imageLoader.pause();
                            break;
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        }
    }

    @Override
    protected Cursor querySearchData() {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{first, second, third, forth};


        Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                forth + " desc ");
        return cursor;
    }

    @Override
    protected void updateView(List<FileItem> items) {
        if (items != null && items.size() > 0) {
            imageAdapter.notifyDataSetChanged();
        } else {
            gv.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }
}
