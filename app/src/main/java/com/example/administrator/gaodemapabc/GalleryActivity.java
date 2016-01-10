package com.example.administrator.gaodemapabc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.gaodemapabc.util.CommonUtils;
//import com.example.mapphoto.mapphotos.util.CommonUtils;

/**
 * Created by Base on 2015/12/22.
 */
public class GalleryActivity extends Activity {
    private LinearLayout gallery;///底部显示照片缩略图的“图库”
    private ImageView pictureView;//显示照片的组件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        //初始化界面组件
        gallery = (LinearLayout) findViewById(R.id.gallery);
        pictureView = (ImageView) findViewById(R.id.imageview_picture);
        //获取传递过来的相册id
        int albumId = getIntent().getIntExtra("album_id", -1);
        if (albumId == -1) {
            //如果相册无效，显示所有相册中的照片
            getAllPicture();
        } else {
            //显示相册id为albumId的所有照片
            getAllPictureById(albumId);
        }

    }

    /*
    * 根据传入的照片文件名，动态生成View组件*/
    private LinearLayout getImageView(final String path) {
        int width = dip2px(80);
        int height = dip2px(80);
        //从照片解码80.*80的缩略图
        Bitmap bitmap = CommonUtils.decodeBitmapFromFile(path, width, height);
        //创建ImageView组件以显示缩略图，这个组件将被加到底7n1）的线性布局中
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);
        //将ImageView加入到LinearLayout中
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        layout.setGravity(Gravity.CENTER);
        layout.addView(imageView);
        //点击缩略图则显示对应的照片
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int w = pictureView.getWidth();
                int h = pictureView.getHeight();

                Bitmap picture = CommonUtils.decodeBitmapFromFile(path, w, h);
                pictureView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                pictureView.setImageBitmap(picture);
            }
        });
        layout.setPadding(0,0,dip2px(5),0);

        return layout;
    }
    /*
    * 获取所有照片
    * */
    private void getAllPicture(){
        //检查存储卡是否有效
        if (!(Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState()))){
            return;
        }
        //从数据库中获取所有拍照的照片的文件名
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
                Context.MODE_PRIVATE,null);
        String sql = "select * from t_album_picture order by _id desc";
        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            String picture =
                    cursor.getString(cursor.getColumnIndex("picture"));
            String path = CommonUtils.PICTURE_PATH + picture;
            //获取照片图像并创建缩略图View对象，然后7f1入到gallery布局中
            gallery.addView(getImageView(path));
        }
        cursor.close();
        db.close();
    }
    /*
    * 获取指定相册的照片*/
    private void getAllPictureById(int albumId){
        //检查存储卡是否有效
        if (!(Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState()))){
            return;
        }
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
                Context.MODE_PRIVATE,null);
        //从数据库中获取特定相册中的照片文件名，SQL字符串中包含“；”占位符
        String sql = "select * from t_album_picture" +
                "where album_id=? order by _id desc";
        //执行SQL语句时，设定占位符对应的实际参数值
        Cursor cursor = db.rawQuery(sql,
                new String[]{String.valueOf(albumId)});
        //循环处理每一条记录，获取照片文件名，并生成Itiew组件，加入到线性布局组件
        while (cursor.moveToNext()){
            String picture =
                    cursor.getString(cursor.getColumnIndex("picture"));
            String path = CommonUtils.PICTURE_PATH + picture;
            gallery.addView(getImageView(path));
        }
        //关闭游标、数据库
        cursor.close();
        db.close();
    }
    /*
    * 将dp/dip为单位的长度转换为px绝对像素值*/
    private int dip2px(float dip){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(dip*scale+0.5f);
    }
}
