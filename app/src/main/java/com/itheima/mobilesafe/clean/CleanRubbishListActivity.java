package com.itheima.mobilesafe.clean;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.clean.adapter.RubbishListAdapter;
import com.itheima.mobilesafe.clean.entity.RubbishInfo;
import com.itheima.mobilesafe.home.HomeActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CleanRubbishListActivity extends AppCompatActivity  implements View.OnClickListener{

    private TextView tv_sacnning, tv_scanned;
    private long rubbishMemory = 0;
    private List<RubbishInfo> rubbishInfos = new ArrayList<RubbishInfo>();
    private List<RubbishInfo> mRubbishInfos = new ArrayList<RubbishInfo>();
    private PackageManager pm;

    private RubbishListAdapter adapter;
    private ListView mRubbishLV;

    private Button mRubbishBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_rubbish_list);
        pm = getPackageManager();
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView() {
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_main_title)).setText("扫描垃圾");
        tv_sacnning = findViewById(R.id.tv_scanning);
        mRubbishLV = findViewById((R.id.lv_rubbish));
        mRubbishBtn = findViewById(R.id.btn_cleanall);
        tv_scanned = findViewById(R.id.tv_scanned);
        adapter = new RubbishListAdapter(this, mRubbishInfos);
        mRubbishLV.setAdapter(adapter);
      tv_back.setOnClickListener(this);
      mRubbishBtn.setOnClickListener(this);

        ActivityCompat.requestPermissions(CleanRubbishListActivity.this, new String[]{
                "android.permission.WRITE_EXTERNAL_STORAGE"
        }, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals("android.permission.WRITE_EXTERNAL_STORAGE") && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
fillDate();
                } else {
                    Toast.makeText(this, "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
            //申请sd卡写权限成功

        }


    }

    public long filePath(File file) {
        long memory = 0;
        if (file != null && file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.getPath().contains("/files")) {
                    if (file2.listFiles() == null) {
                        memory += file2.length();
                    } else {
                        try {
                            memory += getFolderSize(file2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    memory += filePath(file2);
                }
            }
        }
        return memory;
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
        protected static  final  int SCANNING =100;
        protected  static final int FINISH=101;
        private  void fillDate(){
            Thread thread = new Thread(){
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void  run(){
                    rubbishInfos.clear();
                    String filesPath="/sdcard/Android/data";
                    File ppFile = new File(filesPath);
                    File[] files = ppFile.listFiles();
                    if (files ==null)return;
                    PackageManager packageManager =getPackageManager();
                    for (File file:files){
                        RubbishInfo rubbishInfo =new RubbishInfo();
                        try {
                            if (file.getName()==null)return;
                            PackageInfo packageInfo =packageManager.getPackageInfo(
                                    file.getName(),0
                            );
                            rubbishInfo.packagename = packageInfo.packageName;
                            rubbishInfo.appName =packageInfo.applicationInfo.loadLabel(pm).toString();
                            rubbishInfo.appIcon=packageInfo.applicationInfo.loadIcon(pm);
                            rubbishInfo.rubbishSize =filePath(file);
                            if (rubbishInfo.rubbishSize>0&&rubbishInfo.packagename != null){
                                rubbishInfos.add(rubbishInfo);
                                rubbishMemory += rubbishInfo.rubbishSize;

                            }
                        }catch (PackageManager.NameNotFoundException e){
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(300);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        Message msg =Message.obtain();
                        msg.obj=rubbishInfo;
                        msg.what =SCANNING;
                        handler.sendMessage(msg);
                    }
                    Message msg = Message.obtain();
                    msg.what=FINISH;
                    handler.sendMessage(msg);
                }
            };
            thread.start();
        }
        private Handler handler =new Handler(){
            public void  handleMessage(Message msg){
                switch (msg.what){
                    case  SCANNING:
                        RubbishInfo info =(RubbishInfo)msg.obj;
                        if (info.packagename != null)
                            tv_sacnning.setText("正在扫描："+info.packagename);
                        tv_scanned.setText(RubbishListAdapter.FormatFileSize(rubbishMemory));
                        mRubbishInfos.clear();
                        mRubbishInfos.addAll(rubbishInfos);
                        adapter.notifyDataSetChanged();
                        mRubbishLV.setSelection(mRubbishInfos.size());
                        break;
                    case FINISH:
                        tv_sacnning.setText("扫描完成");
                        if (rubbishMemory>0){
                            mRubbishBtn.setEnabled(true);
                        }else {
                            mRubbishBtn.setEnabled(false);
                            Toast.makeText(CleanRubbishListActivity.this,"你的手机已干净",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };

        @Override
        public void  onClick(View v){
            switch (v.getId()){
                case  R.id.tv_back:

                    finish();
                    break;
                case  R.id.btn_cleanall:
                    if (rubbishMemory>0){
                        Intent intent =new Intent(this,CleanRubbishActivity.class);
                        intent.putExtra("rubbishSize",rubbishMemory);
                        intent.putExtra("rubbishInfos",(Serializable)rubbishInfos);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
}

