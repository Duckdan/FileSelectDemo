package com.study.fileselectlibrary.imageLoader;

/**
 * Created by Administrator on 2017/11/10.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 此类用于处理视频缩略图
 */
public class VideoThumbnailLoader {
    private final String TYPE = "video";
    private Context context;
    private static VideoThumbnailLoader vtLoader;
    private ImageLoader imageLoader;


    private VideoThumbnailLoader(Context context) {
        this.context = context;
        imageLoader = ImageLoader.getInstance(context);
    }

    public synchronized static VideoThumbnailLoader getInstance(Context context) {
        if (vtLoader == null) {
            vtLoader = new VideoThumbnailLoader(context);
        }
        return vtLoader;
    }

    /**
     * 获取视频缩略图的Bitmap的实例
     *
     * @param path   图片路径
     * @param name   图片名称
     * @param width  缩略图的宽
     * @param height 缩略的高
     * @return
     */
    public void displayBitmap(final String path, final String name, final int width, final int height, final OnThumbnailListener listener) {

        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws Exception {
                String fileName = name;
                if (!TextUtils.isEmpty(name) && name.contains(".")) {
                    fileName = name.substring(0, name.lastIndexOf("."));
                }

                Bitmap bitmap = loaderBitmap(TYPE, path, fileName, width, height);
                if (bitmap != null) {
                    e.onNext(bitmap);
                } else {
                    e.onError(new Throwable("获取缩略图失败"));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Bitmap bitmap) {
                        if (listener != null) {
                            listener.loaderThumbnail(bitmap, "获取缩略图成功");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (listener != null) {
                            listener.loaderThumbnail(null, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 加载图片
     *
     * @param type
     * @param path
     * @param name
     * @param width
     * @param height
     * @return
     */
    private Bitmap loaderBitmap(String type, String path, String name, int width, int height) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(name)) {
            bitmap = imageLoader.loaderBitmap(type, path, name, width, height);
            //如果本地和缓存中都获取不到Bitmap的实例，则自己创建缩略图
            if (bitmap == null) {
                bitmap = getVideoThumbnail(path, width, height, MediaStore.Images.Thumbnails.MICRO_KIND);
                if (bitmap != null) {
                    imageLoader.saveToDisk(type, path, name, bitmap);
                }
            }
        }

        return bitmap;
    }

    /**
     * @param videoPath 视频路径
     * @param width
     * @param height
     * @param kind      比如:MediaStore.Video.Thumbnails.MICRO_KIND   MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        // 获取视频的缩略图
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public interface OnThumbnailListener {
        void loaderThumbnail(Bitmap bitmap, String error);
    }
}
