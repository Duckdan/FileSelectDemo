package com.study.fileselectlibrary.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.bean.FileItem;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class GvAdapter extends BaseAdapter {
    private final Context context;
    private final List<FileItem> items;

    public GvAdapter(Context context, List<FileItem> items) {

        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.gv_item, null);
        }

        final FileItem fileItem = items.get(position);
        String name = fileItem.getName();
        String path = fileItem.getPath();
        ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
//        final SimpleDraweeView iv = (SimpleDraweeView) convertView.findViewById(R.id.iv);
//        ImageLoader.getInstance(context).loaderBitmap("image", path, name, 60, 60,
//                new ImageLoader.OnImageLoaderListener() {
//                    @Override
//                    public void loaderBitmap(Bitmap bitmap, String error) {
//                        if (bitmap != null) {
//                            iv.setImageBitmap(bitmap);
//                        }
//                    }
//                });
//        iv.setImageURI(Uri.fromFile(new File(path)));
        Picasso.
                with(context).
                load(new File(path)).
                into(iv);
        return convertView;
    }
}
