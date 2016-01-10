package com.example.administrator.gaodemapabc.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by lmxssh on 2015/11/23.
 */
public class RowInfoBean {
    public int id;//分别为相册id----图标---标题
    public Drawable thumb;//图标
    public String title;//相册标题
    public RowInfoBean(Drawable thumb,String title){
        this.thumb=thumb;
        this.title = title;

    }
    public RowInfoBean(){

    }

}
