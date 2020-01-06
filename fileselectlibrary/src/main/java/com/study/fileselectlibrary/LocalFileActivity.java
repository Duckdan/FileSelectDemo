package com.study.fileselectlibrary;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.fileselectlibrary.adapter.VpAdapter;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.fragment.AudioFragment;
import com.study.fileselectlibrary.fragment.BaseFragment;
import com.study.fileselectlibrary.fragment.DocumentFragment;
import com.study.fileselectlibrary.fragment.OtherFragment;
import com.study.fileselectlibrary.fragment.PictureFragment;
import com.study.fileselectlibrary.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LocalFileActivity extends AppCompatActivity {

    private final static int RESULT_CODE = 200;
    //默认最大选择文件数量
    private final static int FILE_SELECT_MAX_COUNT = 5;

    private int defaultCount = FILE_SELECT_MAX_COUNT;


    TextView tvBack;
    LinearLayout llBack;
    TextView tvTitle;
    TextView tvCancel;
    TabLayout tl;
    TextView tvEmpty;
    FrameLayout flContainer;
    TextView tvSize;
    Button btSend;
    ViewPager vp;

    List<BaseFragment> fragmentList = new ArrayList<>();
    String[] titles = {"视频", "音乐", "图片", "文档", "其他"};
    private VpAdapter vpAdapter;

    private ArrayList<FileItem> selectedList;
    private HashSet<String> pathSet = new HashSet<>();
    private String titleName = "我的文件";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file);

        tvBack = (TextView) findViewById(R.id.tv_back);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tl = (TabLayout) findViewById(R.id.tl);
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        flContainer = (FrameLayout) findViewById(R.id.fl_container);
        tvSize = (TextView) findViewById(R.id.tv_size);
        btSend = (Button) findViewById(R.id.bt_send);
        vp = (ViewPager) findViewById(R.id.vp);

        Intent intent = getIntent();
        defaultCount = intent.getIntExtra("max", defaultCount);
        selectedList = intent.getParcelableArrayListExtra("file");
        selectedList = selectedList == null ? new ArrayList<FileItem>() : selectedList;
        if (selectedList != null && selectedList.size() > 0) {
            for (FileItem file : selectedList) {
                pathSet.add(file.getPath());
            }
            btSend.setEnabled(true);
        } else {
            btSend.setEnabled(false);
        }

        if (selectedList.size() > 0) {
            tvTitle.setText("已选" + selectedList.size() + "个");
        } else {
            tvTitle.setText(titleName);
        }

        fragmentList.add(new VideoFragment());
        fragmentList.add(new AudioFragment());
        fragmentList.add(new PictureFragment());
        fragmentList.add(new DocumentFragment());
        fragmentList.add(new OtherFragment());

        vpAdapter = new VpAdapter(getSupportFragmentManager(), fragmentList, titles);
        vp.setAdapter(vpAdapter);

        tl.setupWithViewPager(vp);

        initListener();

    }

    private void initListener() {
        tvBack.setOnClickListener(new View.OnClickListener() {
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

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
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

    public ArrayList<FileItem> getSelectedList() {
        return selectedList;
    }

    public HashSet<String> getPathSet() {
        return pathSet;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public String getTitleName() {
        return titleName;
    }

    public TextView getTvSize() {
        return tvSize;
    }

    public Button getBtSend() {
        return btSend;
    }

    public int getDefaultCount() {
        return defaultCount;
    }
}
