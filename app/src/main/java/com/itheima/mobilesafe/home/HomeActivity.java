package com.itheima.mobilesafe.home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.clean.CleanRubbishListActivity;
import com.itheima.mobilesafe.home.view.ArcProgressBar;

import java.io.File;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout ll_clean,ll_interception,ll_security,ll_software_manager;
    private RelativeLayout rl_app_lock,rl_speed_test,rl_netraffic;
    private ArcProgressBar pb_sd,pb_rom;
    private TextView tv_title;
    private long total_sd,avail_sd,total_rom,avail_rom;
    private int sd_used,rom_used;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getMemoryFromPhone();
        init();
    }

    private void init() {
        findViewById(R.id.title_bar).setBackgroundResource(R.color.blue_color);
        tv_title = findViewById(R.id.tv_main_title);
        tv_title.setText("手机安全卫士");
        ll_clean = findViewById(R.id.ll_clean);
        ll_interception = findViewById(R.id.ll_interception);
        ll_security = findViewById(R.id.ll_security);
        ll_software_manager = findViewById(R.id.ll_software_manager);
        rl_app_lock = findViewById(R.id.rl_app_lock);
        rl_speed_test = findViewById(R.id.rl_speed_test);
        rl_netraffic = findViewById(R.id.rl_netraffic);
        pb_sd = findViewById(R.id.pb_sd);
        pb_rom = findViewById(R.id.pb_rom);
        pb_sd.setMax(100);
        pb_sd.setTitle("存储空间");
        new MyAsyncSDTask().execute(0);
        pb_rom.setMax(100);
        pb_rom.setTitle("内存");
        new MyAsyncRomTask().execute(0);


        ll_clean.setOnClickListener(this);
        ll_interception.setOnClickListener(this);
        ll_security.setOnClickListener(this);
        ll_software_manager.setOnClickListener(this);
        rl_app_lock.setOnClickListener(this);
        rl_speed_test.setOnClickListener(this);
        rl_netraffic.setOnClickListener(this);
    }

     @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getMemoryFromPhone(){
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        total_sd = blockSize * totalBlocks;
        long availableBlocks = stat.getAvailableBlocksLong();
        avail_sd = blockSize * availableBlocks;
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);
        total_rom = memoryInfo.totalMem;
        avail_rom = memoryInfo.availMem;
        sd_used = 100 - (int)(((double)avail_sd/(double)total_sd)*100);
        rom_used = 100 - (int)(((double)avail_rom/(double)total_rom)*100);

    }

    private class MyAsyncRomTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            Integer timer = 0;
            while (timer <= rom_used){
                try{
                    publishProgress(timer);
                    timer ++;
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            pb_rom.setProgress((int)(values[0]));
        }
    }

    private class MyAsyncSDTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            Integer timer = 0;
            while (timer <= sd_used){
                try{
                    publishProgress(timer);
                    timer ++;
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            pb_sd.setProgress((int)(values[0]));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_clean:
                Intent cleanIntent=new Intent(this, CleanRubbishListActivity.class);
                startActivity(cleanIntent);
                break;
            case R.id.ll_interception:
                break;
            case R.id.ll_security:
                break;
            case R.id.ll_software_manager:
                break;
            case R.id.rl_app_lock:
                break;
            case R.id.rl_speed_test:
                break;
            case R.id.rl_netraffic:
                break;
        }

    }
}