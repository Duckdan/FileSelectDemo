package com.study.fileselectlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.utils.PickerConfig;

import java.util.ArrayList;


public class AllFileActivity extends AppCompatActivity {

    private final static int FILE_CODE = 100;
    private final static int RESULT_CODE = 200;
    //默认最大选择文件数量
    private final static int FILE_SELECT_MAX_COUNT = 5;

    private int defaultCount = FILE_SELECT_MAX_COUNT;

    TextView tvTitle;
    TextView tvCancel;
    RelativeLayout rlMine;
    RelativeLayout rlDevice;
    RelativeLayout rlSd;
    TextView tvSize;
    Button btSend;

    private ArrayList<FileItem> allFileItemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_file);
        PickerConfig.checkImageLoaderConfig(this);
        ImagePipelineConfig config = ImagePipelineConfig.
                newBuilder(this).
                setDownsampleEnabled(true).
                build();
        Fresco.initialize(this,config);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        rlMine = (RelativeLayout) findViewById(R.id.rl_mine);
        rlDevice = (RelativeLayout) findViewById(R.id.rl_device);
        rlSd = (RelativeLayout) findViewById(R.id.rl_sd);
        tvSize = (TextView) findViewById(R.id.tv_size);
        btSend = (Button) findViewById(R.id.bt_send);

        Intent intent = getIntent();
        defaultCount = intent.getIntExtra("max", defaultCount);

        initListener();
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        rlMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpActivity(null, LocalFileActivity.class);
            }
        });
        rlDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpActivity("/", DeviceFileActivity.class);
            }
        });
        rlSd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpActivity(Environment.getExternalStorageDirectory().getPath(), DeviceFileActivity.class);
            }
        });
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putParcelableArrayListExtra("file", allFileItemList);
                setResult(RESULT_CODE, data);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    private void jumpActivity(String path, Class clazz) {
        Intent intent = new Intent(this, clazz);
        if (path != null) {
            intent.putExtra("path", path);
        }
        intent.putExtra("max", defaultCount);
        intent.putParcelableArrayListExtra("file", allFileItemList);
        startActivityForResult(intent, FILE_CODE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_CODE) {
                ArrayList<FileItem> resultFileList = data.getParcelableArrayListExtra("file");
                defaultCount = data.getIntExtra("max", defaultCount);
                allFileItemList.clear();
                if (resultFileList != null && resultFileList.size() > 0) {
                    tvTitle.setText("已选" + resultFileList.size() + "个");
                    allFileItemList.addAll(resultFileList);
                    btSend.setEnabled(true);
                } else {
                    btSend.setEnabled(false);
                }
                long totalSize = 0;
                for (FileItem item : allFileItemList) {
                    totalSize += item.getFileSize();
                }
                tvSize.setText(Formatter.formatFileSize(this, totalSize));
            }
        }
    }
}
