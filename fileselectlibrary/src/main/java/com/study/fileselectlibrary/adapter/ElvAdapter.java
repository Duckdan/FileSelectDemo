package com.study.fileselectlibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.bean.FileResultInfo;
import com.study.fileselectlibrary.emnu.Type;
import com.study.fileselectlibrary.imageLoader.ImageLoader;
import com.study.fileselectlibrary.imageLoader.VideoThumbnailLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */

public class ElvAdapter extends BaseExpandableListAdapter {
    public final Context context;
    public final List<FileResultInfo> lists;
    public String type;
    public Type fileType;

    public ElvAdapter(Context context, List<FileResultInfo> lists) {
        this.context = context;
        this.lists = lists;
    }

    public void setType(Type fileType) {
        this.fileType = fileType;
    }


    @Override
    public int getGroupCount() {
        return lists != null ? lists.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lists.get(groupPosition).getFileItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lists.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return lists.get(groupPosition).getFileItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_group_layout, null);
        }
        FileResultInfo resultInfo = lists.get(groupPosition);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvName.setText(resultInfo.getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_file_item, null);
        }
        FileItem fileItem = lists.get(groupPosition).getFileItems().get(childPosition);
        String name = fileItem.getName();
        String path = fileItem.getPath();
        final ImageView iv = (ImageView) convertView.findViewById(R.id.iv);

//        if (name.contains(".") && name.substring(name.lastIndexOf("."), name.length()).contains(".jpg")) {
//            iv.setImageDrawable(new BitmapDrawable(fileItem.getPath()));
//        } else {
//            iv.setImageResource(R.drawable.type_other);
//        }
//        TextView tvSize = (TextView) convertView.findViewById(R.id.tv_size);
//        tvSize.setText(fileItem.getDataSize());
//        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
//        tvTime.setText(fileItem.getDataDate());
        switch (fileType) {
            case VIDEO:
                VideoThumbnailLoader.getInstance(context).displayBitmap(path, name, 60, 60,
                        new VideoThumbnailLoader.OnThumbnailListener() {
                            @Override
                            public void loaderThumbnail(Bitmap bitmap, String error) {
                                iv.setImageBitmap(bitmap);
                            }
                        });
                break;
            case AUDIO:
                iv.setImageResource(R.drawable.jmui_audio);
                break;
            case IMAGE:
                ImageLoader.getInstance(context).loaderBitmap("image", path, name, 60, 60,
                        new ImageLoader.OnImageLoaderListener() {
                            @Override
                            public void loaderBitmap(Bitmap bitmap, String error) {
                                if (bitmap != null) {
                                    iv.setImageBitmap(bitmap);
                                }
                            }
                        });

                break;
        }

        TextView tvSize = (TextView) convertView.findViewById(R.id.tv_size);
        tvSize.setText(Formatter.formatFileSize(context, fileItem.getFileSize()));
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        tvTime.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", fileItem.getLastModifyTime()));
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.cb);
        if (fileItem.isChecked()) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvName.setText(name);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}
