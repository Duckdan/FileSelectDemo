package com.study.fileselectlibrary.imageLoader;

/**
 * Created by Administrator on 2017/11/10.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 图像加载器，用于图片的加载
 */
public class ImageLoader {


    private static ImageLoader imageLoader;
    private Context context;
    private LruCache<String, Bitmap> lruCache;
    private BitmapLoader bitmapLoader;

    public ImageLoader(Context context) {
        this.context = context;
        bitmapLoader = BitmapLoader.getInstance(context);
        //获取应用最大运行内存
        long maxMemory = Runtime.getRuntime().maxMemory();
        //用应用最大运行内存的1/4来缓存图片
        int imageMemory = (int) (maxMemory / 8);
        lruCache = new LruCache<String, Bitmap>(imageMemory) {
            //用于计算每张图片的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight(); //兼容低版本
            }
        };
    }

    public synchronized static ImageLoader getInstance(Context context) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(context);
        }
        return imageLoader;
    }

    /**
     * 加载图片
     *
     * @param type   缩略图类型
     * @param path   图片路径
     * @param name   图片名称
     * @param width  图片的宽
     * @param height 图片的高
     * @return
     */
    public Bitmap loaderBitmap(String type, String path, String name, int width, int height) {
        Bitmap bitmap = null;
        //先从内存加载图片
        bitmap = loaderBitmapFromMemory(type, path, name);
        //bitmap==null说明内存没有该张缩略图，则尝试从本地加载
        if (bitmap == null) {
            bitmap = loaderBitmapFromDisk(type, path, name, width, height);
            if (bitmap != null) {
                //把从本地中加载到的图片放入到缓存中
                lruCache.put(type + name, bitmap);
            }
        }


        return bitmap;
    }

    /**
     * 加载图片
     *
     * @param type   缩略图类型
     * @param path   图片路径
     * @param name   图片名称
     * @param width  图片的宽
     * @param height 图片的高
     * @return
     */
    public void loaderBitmap(String type, String path, String name, int width, int height,
                             OnImageLoaderListener listener) {
        Bitmap bitmap = null;
        //先从内存加载图片
        bitmap = loaderBitmapFromMemory(type, path, name);
        //bitmap==null说明内存没有该张缩略图，则尝试从本地加载
        if (bitmap == null) {
            bitmap = loaderBitmapFromDisk(type, path, name, width, height);
            if (bitmap != null) {
                //把从本地中加载到的图片放入到缓存中
                lruCache.put(type + name, bitmap);
            }
        }

        if (listener != null) {
            listener.loaderBitmap(bitmap, "加载成功");
        } else {
            listener.loaderBitmap(null, "加载失败");
        }
    }

    /**
     * 从内存中加载图片
     *
     * @param type
     * @param path 图片路劲
     * @param name 图片名称
     * @return
     */
    private Bitmap loaderBitmapFromMemory(String type, String path, String name) {
        Bitmap bitmap = null;
        if (lruCache != null) {
            bitmap = lruCache.get(type + name);
        }
        return bitmap;
    }

    /**
     * 从本地加载图片
     *
     * @param type
     * @param path   图片路径
     * @param name   图片名称
     * @param width  图片的宽
     * @param height 图片的高
     * @return
     */
    private Bitmap loaderBitmapFromDisk(String type, String path, String name, int width, int height) {
        Bitmap bitmap = null;
        bitmap = bitmapLoader.getBitmap(type, path, name, width, height);
        return bitmap;
    }

    /**
     * 将bitmap实例保存到本地disk
     *
     * @param type
     * @param path
     * @param name
     * @param bitmap
     */
    public void saveToDisk(String type, String path, String name, Bitmap bitmap) {
        if (lruCache != null) {
            lruCache.put(type + name, bitmap);
        }
        bitmapLoader.saveToDisk(type, name, bitmap);
    }

    public interface OnImageLoaderListener {
        void loaderBitmap(Bitmap bitmap, String error);
    }
}
