package com.study.fileselectlibrary.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.emnu.Type;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */

public class LvAdapter extends BaseAdapter {
    private final Context context;
    private final List<FileItem> list;
    private Type adapterType = Type.DEFAULT;

    public LvAdapter(Context context, List<FileItem> fileItemList) {
        this.context = context;
        list = fileItemList;
    }

    public void setAdapterType(Type type) {
        this.adapterType = type;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public FileItem getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        FileItem fileItem = list.get(position);
        return fileItem.isFile() ? 1 : 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {

        int type = getItemViewType(position);

        if (convertView == null) {
            switch (type) {
                case 0:
                    convertView = View.inflate(context, R.layout.list_directory_item, null);
                    break;
                case 1:
                    convertView = View.inflate(context, R.layout.list_file_item, null);
                    break;
            }
        }

        FileItem fileItem = list.get(position);

        String name = fileItem.getName();
        switch (type) {
            case 0:
                break;
            case 1:
                SimpleDraweeView iv = (SimpleDraweeView) convertView.findViewById(R.id.iv);

//                if (name.contains(".") && name.substring(name.lastIndexOf("."), name.length()).contains(".jpg")) {
//                    iv.setImageDrawable(new BitmapDrawable(fileItem.getPath()));
//                } else {
//                    iv.setImageResource(R.drawable.type_other);
//                }
//                Picasso.
//                        with(context).
//                        load(new File(fileItem.getPath())).
//                        error(R.drawable.type_other).
//                        into(iv);


                switch (adapterType) {
                    case AUDIO:
                        Uri rui = Uri.parse("res:///" + R.drawable.jmui_audio);
                        iv.setImageURI(rui);
                        break;
                    case DOCUMENT:
                        Uri uri = getUriByName(name);
                        iv.setImageURI(uri);
                        break;
                    case OTHER:
                        Uri other = null;
                        if (!name.endsWith(".zip")) {
                            other = Uri.parse("res:///" + R.drawable.type_txt);
                        }
                        iv.setImageURI(other);
                        break;
                    case DEFAULT:
                        iv.setImageURI(Uri.fromFile(new File(fileItem.getPath())));
                        break;
                    default:
                        iv.setImageURI(Uri.fromFile(new File(fileItem.getPath())));
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
                break;
        }


        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvName.setText(name);
        return convertView;
    }

    /**
     * 根据文件名获取对应文档的图片Uri
     *
     * @param filename
     * @return
     */
    private Uri getUriByName(String filename) {
        Uri uri = null;
        String name = filename.substring(filename.lastIndexOf("."), filename.length());
        switch (name) {
            case ".docx":
            case ".doc":
                uri = Uri.parse("res:///" + R.drawable.type_doc);
                break;
            case ".pdf":
                uri = Uri.parse("res:///" + R.drawable.type_ppt);
                break;
            case ".xlsx":
                uri = Uri.parse("res:///" + R.drawable.type_exe);
                break;
            default:
                break;
        }
        return uri;
    }
}
