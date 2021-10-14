package com.itheima.mobilesafe.clean;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.clean.entity.RubbishInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CleanRubbishActivity extends Activity implements View.OnClickListener {

    protected  static final  int CLEANNING = 100;
    protected  static final  int CLEANNING_FAIL = 101;
    private AnimationDrawable animation;
    private  long rubbishSize;
    private FrameLayout fl_cleaning,fl_finish_clean;
    private TextView tv_rubbish_size,tv_rubbish_unit,tv_clean_size;
    private List<RubbishInfo> mRubbishInfos = new ArrayList<RubbishInfo>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_rubbish);
        initView();
        Intent intent =getIntent();
        rubbishSize =intent.getLongExtra("rubbishSize",0);
        mRubbishInfos =(List<RubbishInfo>) intent.getSerializableExtra("rubbishInfos");
        initData();
    }
    private  void initView(){
        ((TextView)findViewById(R.id.tv_main_title)).setText("清理废物");
        TextView tv_back =findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        animation = (AnimationDrawable) findViewById(R.id.iv_clean_rubbish).getBackground();

        animation.setOneShot(false);
        animation.start();
        tv_rubbish_size=findViewById(R.id.tv_rubbish_size);
        tv_rubbish_unit=findViewById(R.id.tv_rubbish_unit);
        fl_cleaning =findViewById(R.id.fl_cleaning);
        fl_finish_clean=findViewById(R.id.fl_finish_clean);
        tv_clean_size=findViewById(R.id.tv_clean_size);
        tv_back.setOnClickListener(this);
        findViewById(R.id.btn_finish).setOnClickListener(this);
    }
    //格式化垃圾
    private  void formatSize(long size){
        String rubbishSizeStr = Formatter.formatFileSize(this,size);
        String sizeStr;
                 String sizeUnit;
                 if (size>900){
                     sizeStr =rubbishSizeStr.substring(0,rubbishSizeStr.length()-2);
                     sizeUnit =rubbishSizeStr.substring(rubbishSizeStr.length()-2,rubbishSizeStr.length());

                 }else {
                     sizeStr =rubbishSizeStr.substring(0,rubbishSizeStr.length() - 1);
                     sizeUnit =rubbishSizeStr.substring(rubbishSizeStr.length() - 1,rubbishSizeStr.length());

                 }
                 tv_rubbish_unit.setText(sizeUnit);
                 tv_rubbish_size.setText(sizeStr);
    }
    private static  boolean deleteDir(File dir){
        if (dir != null  && dir.isDirectory()){
            String[] children =dir.list();
            for (int i =0; i<children.length; i++){
                boolean success =deleteDir(new File(dir,children[i]));
                if (!success){
                    return false;
                }
            }
        }
        return dir.delete();
    }
    //实现清理
    private  void initData(){
    new Thread() {
        public void run() {
            long size = 0;
            File filePath=getExternalCacheDir().getParentFile().getParentFile();
            for(RubbishInfo info:mRubbishInfos){
                String filesPath = filePath+"/"+info.packagename+"/files";

                File file =new File(filesPath);
                boolean success=deleteDir(file);
                if (success){
                    try {
                        Thread.sleep(300);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    size += info.rubbishSize;
                    if (size >rubbishSize){
                        size =rubbishSize;
                    }
                    Message message = Message.obtain();
                    message.what =CLEANNING;
                    message.obj =size;
                    mHandler.sendMessageDelayed(message,200);

                }else {
                    Message message = Message.obtain();
                    message.what =CLEANNING_FAIL;

                    mHandler.sendMessageDelayed(message,200);
                }
            }
        }
    }.start();
    }


    private Handler mHandler =new  Handler(){
        public  void handleMessage(Message msg){
            switch (msg.what){
                case  CLEANNING:
                    long size =(long) msg.obj;
                    if (size ==rubbishSize){
                        animation.stop();
                        fl_cleaning.setVisibility(View.GONE);
                        fl_finish_clean.setVisibility(View.VISIBLE);
                        tv_clean_size.setText("成功清理："+Formatter.formatFileSize(CleanRubbishActivity.this,rubbishSize));

                    }
                    break;
                case CLEANNING_FAIL:
                    animation.stop();
                    Toast.makeText(CleanRubbishActivity.this,"清理失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.tv_back:
                finish();
                break;
            case R.id.btn_finish:
                finish();
                break;
        }

    }


}