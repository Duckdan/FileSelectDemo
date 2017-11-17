package com.study.definefileselectdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.study.fileselectlibrary.AllFileActivity;
import com.study.fileselectlibrary.bean.FileItem;
import com.study.fileselectlibrary.utils.PermissionCheckUtils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = (TextView) findViewById(R.id.tv_result);
        PermissionCheckUtils.setOnOnWantToOpenPermissionListener(new PermissionCheckUtils.OnWantToOpenPermissionListener() {
            @Override
            public void onWantToOpenPermission() {
                Toast.makeText(MainActivity.this, "请去设置的应用管理中打开应用读取内存的权限", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void file(View view) {
        int size = PermissionCheckUtils.checkActivityPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                100, null);
        if (size == 0) {
            jumpActivity();
        }
    }


    private void jumpActivity() {
        Intent intent = new Intent(this, AllFileActivity.class);
        startActivityForResult(intent, 200);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            boolean flag = true;

            for (int i = 0; i < permissions.length; i++) {
                flag &= (grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }

            if (flag) {
                jumpActivity();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 200) {
            if (resultCode == 200) {
                ArrayList<FileItem> resultFileList = data.getParcelableArrayListExtra("file");

                if (resultFileList != null && resultFileList.size() > 0) {
                    tvResult.setText(resultFileList.toString());
                }

            }
        }
    }


}
