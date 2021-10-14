package com.itheima.mobilesafe.clean.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class RubbishInfo implements Serializable {
    private  static  final  long serialVersionUID = 1L;
    public  String packagename;
    public  long rubbishSize;
    public  transient Drawable appIcon;
    public String appName;
}
