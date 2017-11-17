package com.study.fileselectlibrary.imageLoader;

/**
 * Created by Administrator on 2017/11/10.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 处理图片
 */
public class BitmapLoader {

    private final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/yang";

    private Context context;
    private static BitmapLoader bitmapLoader;

    public BitmapLoader(Context context) {
        this.context = context;
    }

    public synchronized static BitmapLoader getInstance(Context context) {
        if (bitmapLoader == null) {
            bitmapLoader = new BitmapLoader(context);
        }
        return bitmapLoader;
    }

    /**
     * 获取给定路径的Bitmap实例
     *
     * @param type
     * @param path   图片路径
     * @param name   图片名称
     * @param width  图片的宽
     * @param height 图片的高
     * @return
     */
    public Bitmap getBitmap(String type, String path, String name, int width, int height) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(name)) {
            String parent = null;
            File file = null;
            if ("image".equals(type)) {
                parent = path;
                file = new File(parent);
            } else {
                parent = IMAGE_PATH + "/" + type;
                file = new File(parent, name);
            }
            if (file.exists() && file.isFile() && file.length() > 0) {
                bitmap = createBitmap(file.getPath(), name, width, height);
            }
        }
        return bitmap;
    }

    /**
     * 创建Bitmap实例
     *
     * @param path
     * @param name
     * @param width
     * @param height
     * @return
     */
    private Bitmap createBitmap(String path, String name, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int imageWidth = opts.outWidth;
        int imageHeight = opts.outHeight;

        int min = Math.min(imageWidth, imageHeight);
        int useMin = Math.min(width, height);

        opts.inSampleSize = calculateSize(min, useMin);
        opts.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(path, opts);

        return bitmap;
    }

    /**
     * 计算缩小比例
     *
     * @param min
     * @param useMin
     * @return
     */
    private int calculateSize(int min, int useMin) {
        if (useMin == 0) {
            return 1;
        }
        int size = min / useMin;
        return size < 1 ? 1 : size;
    }

    /**
     * 保存bitmap对象到本地
     *
     * @param type
     * @param name
     * @param bitmap
     */
    public void saveToDisk(String type, String name, Bitmap bitmap) {
        if (bitmap != null) {
            FileOutputStream fos = null;
            try {
                File fileDir = new File(IMAGE_PATH + "/" + type);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                File file = new File(fileDir, name + ".png");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
