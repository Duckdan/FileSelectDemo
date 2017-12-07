package com.study.fileselectlibrary.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.utils.PickerImageLoadTool;
import com.study.fileselectlibrary.view.RotateImageViewAware;

import java.util.List;


public class ImageAdapter extends BaseAdapter {


    private final Context context;
    private final List<FileItem> list;

    public ImageAdapter(Context context, List<FileItem> list) {
        this.context = context;
        this.list = list;
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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        final FileItem item = list.get(position);
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_pick_picture_detail, null);
            holder = new ViewHolder();
//            holder.icon = (SimpleDraweeView) convertView.findViewById(R.id.child_image);
            holder.icon = (ImageView) convertView.findViewById(R.id.child_image);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
            holder.checkBoxLl = (LinearLayout) convertView.findViewById(R.id.checkbox_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (item.isChecked()) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        try {
            PickerImageLoadTool.disPlay("file://"+item.getPath(), new RotateImageViewAware(holder.icon, item.getPath()),
                    R.drawable.image_default);

//            holder.icon.setImageURI("file://"+item.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }



        return convertView;
    }

    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }


    private class ViewHolder {
        CheckBox checkBox;
        ImageView icon;
//                SimpleDraweeView icon;
        LinearLayout checkBoxLl;
    }
}
