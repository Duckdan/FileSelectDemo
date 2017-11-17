package com.study.fileselectlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.study.fileselectlibrary.adapter.LvAdapter;
import com.study.fileselectlibrary.bean.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DeviceFileActivity extends AppCompatActivity {

    private final static int FILE_CODE = 100;
    private final static int RESULT_CODE = 200;
    //默认最大选择文件数量
    private final static int FILE_SELECT_MAX_COUNT = 5;

    private int defaultCount = FILE_SELECT_MAX_COUNT;


    LinearLayout llBack;
    ListView lv;
    TextView tvBack;
    TextView tvTitle;
    TextView tvSize;
    TextView tvPath;
    TextView tvEmpty;
    Button btSend;
    private List<FileItem> directoryItemList = new ArrayList<>();
    private List<FileItem> fileItemList = new ArrayList<>();
    private List<FileItem> allFileItemList = new ArrayList<>();
    private String path = "/";
    private String titleName = "";
    private File fileDevice;
    private LvAdapter lvAdapter = new LvAdapter(this, allFileItemList);

    private ArrayList<FileItem> selectedList;
    private HashSet<String> pathSet = new HashSet<>();
    long totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_file);

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        lv = (ListView) findViewById(R.id.lv);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSize = (TextView) findViewById(R.id.tv_size);
        tvPath = (TextView) findViewById(R.id.tv_path);
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        btSend = (Button) findViewById(R.id.bt_send);


        lv.setAdapter(lvAdapter);

        Intent intent = getIntent();
        selectedList = intent.getParcelableArrayListExtra("file");
        defaultCount = intent.getIntExtra("max", defaultCount);
        selectedList = selectedList == null ? new ArrayList<FileItem>() : selectedList;
        if (selectedList != null && selectedList.size() > 0) {
            for (FileItem file : selectedList) {
                pathSet.add(file.getPath());
            }
            btSend.setEnabled(true);
        } else {
            btSend.setEnabled(false);
        }
        calculatorSize();

        Observable.create(new ObservableOnSubscribe<List<FileItem>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<FileItem>> e) throws Exception {
                searchData();
                e.onNext(allFileItemList);
            }
        }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<List<FileItem>>() {
                    @Override
                    public void accept(List<FileItem> items) throws Exception {
                        if (items.size() > 0) {
                            lv.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                            fillView();
                        } else {
                            lv.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                });

        initListener();
    }

    private void initListener() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backLastLevel();
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backLastLevel();
            }
        });
    }

    private void searchData() {
        path = getIntent().getStringExtra("path");
        String sdPath = Environment.getExternalStorageDirectory().getPath();

        titleName = path.contains(sdPath) ? "SD卡" : "手机内存";

        if ("/".equals(this.path) || sdPath.equals(this.path)) {
            tvBack.setText("全部文件");
        } else {
            tvBack.setText("返回上一级");
        }

        if (selectedList.size() > 0) {
            tvTitle.setText("已选" + selectedList.size() + "个");
        } else {
            tvTitle.setText(titleName);
        }

        tvPath.setText(this.path);
        fileDevice = new File(this.path);
        if (fileDevice.exists()) {

            File[] deviceFiles = fileDevice.listFiles();

            fileItemList.clear();
            directoryItemList.clear();
            allFileItemList.clear();
            if (deviceFiles != null) {
                for (int i = 0; i < deviceFiles.length; i++) {
                    File file = deviceFiles[i];
                    FileItem fileItem = new FileItem();
                    fileItem.setFile(file.isFile());
                    fileItem.setName(file.getName());
                    fileItem.setLastModifyTime(file.lastModified());
                    fileItem.setFileSize(file.length());
                    fileItem.setPath(file.getAbsolutePath());
                    fileItem.setRead(file.canRead());
                    if (fileItem.isFile()) {
                        if (pathSet.contains(file.getPath())) {
                            fileItem.setChecked(true);
                        }
                        fileItemList.add(fileItem);
                    } else {
                        if (fileItem.getName().startsWith(".")) {
                            continue;
                        }
                        directoryItemList.add(fileItem);
                    }
                }
            }
            Collections.sort(fileItemList);
            Collections.sort(directoryItemList);

            allFileItemList.addAll(directoryItemList);
            allFileItemList.addAll(fileItemList);
        }

    }


    private void fillView() {
        if (lv != null && lvAdapter != null) {
            lvAdapter.notifyDataSetChanged();
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileItem fileItem = allFileItemList.get(position);
                if (fileItem.isFile()) {
                    CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
                    boolean checked = fileItem.isChecked() ? false : true;
                    if (checked) {
                        if (selectedList.size() >= defaultCount) {
                            Toast.makeText(DeviceFileActivity.this, "发送文件数量不可超过" + defaultCount + "个！", Toast.LENGTH_SHORT).show();
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

                    calculatorSize();
                    cb.setChecked(checked);
                    fileItem.setChecked(checked);
                } else {
                    jumpActivity(fileItem.getPath());
                }
            }
        });

        if (allFileItemList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
        }
    }

    private void calculatorSize() {
        totalSize = 0;
        for (FileItem item : selectedList) {
            totalSize += item.getFileSize();
        }
        tvSize.setText(Formatter.formatFileSize(DeviceFileActivity.this, totalSize));
    }

    private void removeFileItem(String path) {

        for (Iterator<FileItem> iterator = selectedList.iterator(); iterator.hasNext(); ) {
            FileItem fileItem = iterator.next();
            if (fileItem.getPath().equals(path)) {
                iterator.remove();
            }
        }
    }




    /**
     * 返回上一级
     */
    private void backLastLevel() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra("file", selectedList);
        data.putExtra("max", defaultCount);
        setResult(RESULT_CODE, data);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void jumpActivity(String path) {
        Intent intent = new Intent(this, DeviceFileActivity.class);
        intent.putExtra("path", path);
        intent.putParcelableArrayListExtra("file", selectedList);
        intent.putExtra("max", defaultCount);
        startActivityForResult(intent, FILE_CODE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_CODE) {
                ArrayList<FileItem> resultFileList = data.getParcelableArrayListExtra("file");
                defaultCount = data.getIntExtra("max", defaultCount);
                selectedList.clear();
                if (resultFileList != null && resultFileList.size() > 0) {
                    tvTitle.setText("已选" + resultFileList.size() + "个");
                    selectedList.addAll(resultFileList);
                    btSend.setEnabled(true);
                } else {
                    btSend.setEnabled(false);
                }
                long totalSize = 0;
                for (FileItem item : selectedList) {
                    totalSize += item.getFileSize();
                }
                tvSize.setText(Formatter.formatFileSize(this, totalSize));
            }
        }
    }
}
