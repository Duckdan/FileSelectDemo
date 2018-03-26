package com.study.fileselectlibrary.fragment;


import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.study.fileselectlibrary.LocalFileActivity;
import com.study.fileselectlibrary.R;
import com.study.fileselectlibrary.adapter.LvAdapter;
import com.study.fileselectlibrary.bean.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {


    ListView lv;
    TextView tvEmpty;

    private int layoutId;
    public Context context;
    private Dialog progressDialog;
    private File file;
    public LvAdapter lvAdapter;
    public String first = "";
    public String second = "";
    public String third = "";
    public String forth = "";
    public List<FileItem> lists = new ArrayList<FileItem>();
    public int size = 20;
    public int curPage = 1;
    public int allPage = 0;
    public ArrayList<FileItem> selectedList;
    public HashSet<String> pathSet;
    public TextView tvTitle;
    public String titleName;
    public Button btSend;
    public int defaultCount = 5;
    private LocalFileActivity localFileActivity;
    private long totalSize = 0;
    private TextView tvSize;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        setContentView();
        if (view == null) {
            view = createView(inflater);
        }

        if (lvAdapter == null) {
            loadingData();
            lvAdapter = new LvAdapter(context, lists);
        }

        return view;
    }


    public void setView(int layoutId) {
        this.layoutId = layoutId;
    }

    private View createView(LayoutInflater inflater) {
        return layoutId == 0 ? null : inflater.inflate(layoutId, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        localFileActivity = (LocalFileActivity) getActivity();
        selectedList = localFileActivity.getSelectedList();
        selectedList = selectedList == null ? new ArrayList<FileItem>() : selectedList;
        pathSet = localFileActivity.getPathSet();
        pathSet = pathSet == null ? new HashSet<String>() : pathSet;
        tvTitle = localFileActivity.getTvTitle();
        titleName = localFileActivity.getTitleName();
        tvSize = localFileActivity.getTvSize();
        btSend = localFileActivity.getBtSend();
        defaultCount = localFileActivity.getDefaultCount();

        lv = (ListView) view.findViewById(R.id.lv);
        tvEmpty = (TextView) view.findViewById(R.id.tv_empty);

        findViewById(view);
        calculator();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }

    protected void calculator() {
        totalSize = 0;
        for (FileItem item : selectedList) {
            totalSize += item.getFileSize();
        }
        tvSize.setText(Formatter.formatFileSize(context, totalSize));
    }

    protected void removeFileItem(String path) {

        for (Iterator<FileItem> iterator = selectedList.iterator(); iterator.hasNext(); ) {
            FileItem fileItem = iterator.next();
            if (fileItem.getPath().equals(path)) {
                iterator.remove();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(context, R.style.ProgressDialogStyle);
        }
        progressDialog.setContentView(R.layout.dialog_custom_loading);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        ProgressBar progressBar = (ProgressBar) progressDialog.findViewById(R.id.loading_progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Drawable drawable = context.getApplicationContext().getResources().getDrawable(R.drawable.progress_loading_v23);
            progressBar.setIndeterminateDrawable(drawable);
        }


        progressDialog.show();
    }

    public void stopProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    public void loadingData() {
        showProgressDialog();
        Observable.create(new ObservableOnSubscribe<Cursor>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Cursor> e) throws Exception {
                Cursor cursor = querySearchData();

                e.onNext(cursor);

            }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Cursor, List<FileItem>>() {
                    @Override
                    public List<FileItem> apply(@NonNull Cursor cursor) throws Exception {


                        if (cursor != null) {
                            lists.clear();

                            while (cursor.moveToNext()) {
                                String name = cursor.getString(cursor.getColumnIndex(first));
                                String path = cursor.getString(cursor.getColumnIndex(second));
                                long size = cursor.getLong(cursor.getColumnIndex(third));
                                long date = cursor.getLong(cursor.getColumnIndex(forth));
                                name = name == null ? path.substring(path.lastIndexOf("/") + 1, path.length()) : name;
                                if (scannerFile(path) && !name.startsWith(".")) {
                                    FileItem fileItem = new FileItem();
                                    fileItem.setName(TextUtils.isEmpty(name) ? path.substring(path.lastIndexOf("/") + 1, path.length()) : name);
                                    fileItem.setFile(true);
                                    fileItem.setPath(path);
                                    fileItem.setFileSize(size);
                                    fileItem.setLastModifyTime(date * 1000);
                                    fileItem.setData(true);
                                    if (pathSet.contains(fileItem.getPath())) {
                                        fileItem.setChecked(true);
                                    }
                                    lists.add(fileItem);
                                }

                            }
                            cursor.close();
                            cursor = null;
                        }
                        return lists;
                    }
                })

                .subscribe(new Observer<List<FileItem>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<FileItem> items) {
                        stopProgressDialog();
                        updateView(items);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        stopProgressDialog();
                        updateView(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 用于标记文件路径是否有效
     *
     * @param path
     * @return
     */
    public boolean scannerFile(String path) {
        file = new File(path);
        if (file.exists() && file.length() > 0) {
            return true;
        }
        return false;
    }


    public abstract void setContentView();

    protected abstract void findViewById(View view);

    /**
     * 获取数据
     *
     * @return
     */
    protected abstract Cursor querySearchData();

    /**
     * 更新视图
     *
     * @param items
     */
    protected abstract void updateView(List<FileItem> items);
}
