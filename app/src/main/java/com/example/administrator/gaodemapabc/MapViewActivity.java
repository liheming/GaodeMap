package com.example.administrator.gaodemapabc;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.administrator.gaodemapabc.util.CommonUtils;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Base on 2015/11/22.
 */
public class MapViewActivity extends FragmentActivity {
    private MapView mapView;
    private AMap aMap;
    //定位要用的对象
    private LocationManager manager;
    private String provider;
    private int albumId;//相机id
    private String albumTitle;//相机标题
    private LocationListener listener;
    //默认地理位置
    private double myLatitude = 23.17032;
    private double myLongitude = 112.87104;
    //拍照界面
    private ImageView popCamera;
    private LinearLayout cameraBar = null;
    private LinearLayout previewArea = null;
    private LinearLayout snapArea = null;
    private ImageView snap = null;
    //相机和拍照的照片
    private CameraSurfaceView cameraSurfaceView = null;

    private android.hardware.Camera camera = null;
    private Bitmap picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        init(savedInstanceState);

    }

    private void init(Bundle savedInstanceState) {
        //初始化地图组件
        //获取从MainActivity传递过来的相册信息，这里直接保存到Intent中
        Intent intent = getIntent();
        albumId = intent.getIntExtra("album_id", -1);
        albumTitle = intent.getStringExtra("album_title");
        //创建位置文化监听对象
        listener = new MyLocationListener();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);//矢量地图模式
            aMap.getUiSettings().setZoomControlsEnabled(true);//启用缩放控件
            aMap.getUiSettings().setZoomControlsEnabled(true);//启用手势进行地图多方;
        }
        //获取系统定位服务
        manager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        //设置定位参数：最大精度，不要求海拔信息，使用省电模式
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //选择最佳定位方式（CPS或NETWORK）
        provider = manager.getBestProvider(criteria, true);


        SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
                Context.MODE_PRIVATE,null);
        String sql ="select * from t_album_picture where album_id="+albumId;
        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            //提取经纬度信息，缩略图文件保存路径
            double latitude =
                    cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude =
                    cursor.getDouble(cursor.getColumnIndex("longitude"));
            String thumb = CommonUtils.THUMB_PATH+
                    cursor.getString(cursor.getColumnIndex("thumb"));
            //得到每一条记录的picture字段，即照片的文件名
            String picture =
                    cursor.getString(cursor.getColumnIndex("picture"));
            //循环将照片缩略图作为地表添加到地图上
            Bitmap bmp = BitmapFactory.decodeFile(thumb);
            MarkerOptions mo = new MarkerOptions();
            mo.position(new LatLng(latitude,longitude));
            mo.icon(BitmapDescriptorFactory.fromBitmap(bmp));
            //将照片文件名设置为地表的title
            mo.title(picture);
            //---------高德修改
            aMap.addMarker(mo);
        }
        //关闭数据库
        cursor.close();
        db.close();
        //点击地表，打开徐彤图库浏览器显示图片
        aMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String picture = marker.getTitle();
                String path = CommonUtils.PICTURE_PATH + picture;
                File file = new File(path);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),"image/*");
                startActivity(intent);
                return false;
            }
        });


        //定位到地图当前位置
        aMap.setMyLocationEnabled(true);
        LatLng latLng = getMyLocation();
        //在地图上是添加Marker标记
        aMap.addMarker(new MarkerOptions().position(latLng));
        //移动观察相机到这个经纬度位置，使之可见
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        //初始化拍照部分
        popCamera = (ImageView) findViewById(R.id.popCamera);
        cameraBar = (LinearLayout) findViewById(R.id.cameraBar);
        previewArea = (LinearLayout) findViewById(R.id.previewArea);
        snapArea = (LinearLayout) findViewById(R.id.snapArea);
        snap = (ImageView) findViewById(R.id.snap);
        //隐藏相机拍照界面
        cameraBar.setVisibility(View.INVISIBLE);
        //动态弹出拍照界面
        popCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraBar.getVisibility() == View.VISIBLE) {
                    //清除拍照界面中的组件并隐藏
                    cameraBar.removeAllViews();
                    cameraBar.setVisibility(View.INVISIBLE);
                } else if (cameraBar.getVisibility() == View.INVISIBLE) {
                    if (cameraSurfaceView == null) {
                        cameraSurfaceView = new CameraSurfaceView(getApplicationContext());
                        //设置取景预览画面置顶显示，否则将被地图覆盖件
                        previewArea.removeAllViews();
                        //将cameraSurfaceView放进previewAra布局中
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        previewArea.addView(cameraSurfaceView, param);


                    }

                }
                //动态构建相机预览界面（取景预览和拍照），使之可见
                cameraBar.removeAllViews();
                cameraBar.addView(snapArea);
                cameraBar.addView(previewArea);
                cameraBar.setVisibility(View.VISIBLE);
                /*Intent intent = new Intent("android.intent.action.MAIN");
                startActivity(intent);*/

            }

        });
        //拍照处理
        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    //启动相机聚焦拍照
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                camera.takePicture(null, null, new PictureTakenCallback());
                            } else {
                                Toast.makeText(getApplicationContext(), "请聚焦", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /*
    *    相机拍照回调接口
     */
    private class PictureTakenCallback implements android.hardware.Camera.PreviewCallback, android.hardware.Camera.PictureCallback {

        @Override
        public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {

        }


        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            takePic(data, camera);
        }

        private void takePic(byte[] data, Camera camera) {
            if (picture != null && !picture.isRecycled()) {
                picture.recycle();
            }
            //暂停相机预览
            camera.stopPreview();
            //将相机取景预览画面解码为图像数据
            System.out.print("data.length:" + data.length);
            picture = BitmapFactory.decodeByteArray(data, 0, data.length);
            //因为横竖屏预览时旋转了90度，故照片虚往回旋转90度
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //构造旋转矩阵，主要用在图像处理中
                Matrix matrix = new Matrix();
                matrix.postRotate(90);/*翻转90度*/
                int w = picture.getWidth();
                int h = picture.getHeight();
                //将照片旋转90度
                try {
                    Bitmap tbmp = Bitmap.createBitmap(picture, 0, 0, w, h, matrix, true);
                    picture.recycle();
                    picture = tbmp;
                } catch (OutOfMemoryError oom) {
                    //rotate failed
                }

            } else {
                //do nothing
            }
            if (picture != null) {
                //保存照片文件
                //释放图片内存
                /*picture.recycle();
                picture = null;
                Toast.makeText(getApplicationContext(), "[Has taken]", Toast.LENGTH_SHORT).show();*/
                String picPath = CommonUtils.savePicture(
                        getApplicationContext(), picture,
                        CommonUtils.PICTURE_PATH);
                Bitmap thumb64 = CommonUtils.getPicture64(picPath);
                //保存缩略图文件到SD卡
                String thumb64Path = CommonUtils.savePicture(
                        getApplicationContext(), thumb64,
                        CommonUtils.THUMB_PATH);
                //获得照片、缩略图文件名（不含所在的目录）
                String picname = new File(picPath).getName();
                String thumb64name = new File(thumb64Path).getName();
                //保存照片数据到数据库
                SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
                        Context.MODE_PRIVATE, null);
                String sql =
                        String.format("insert into t_album_picture(" +
                                        "latitude,longitude,picture,thumb,album_id)" +
                                        "values(%f,%f,'%s','%s',%d)",
                                myLatitude, myLongitude, picname, thumb64name,
                                albumId);
                db.execSQL(sql);
                //修改相册条目的缩略图为最近一次拍照的缩略图
                sql = String.format(
                        "update t_album set thumb='%s' where _id=%d",
                        thumb64name, albumId);
                db.execSQL(sql);
                db.close();
                //在地图上显示照片缩略图地表
                aMap.addMarker(new MarkerOptions().position(
                        new LatLng(myLatitude, myLongitude)).icon(
                        BitmapDescriptorFactory.fromBitmap(thumb64)
                ));
                picture.recycle();
                picture = null;
                Toast.makeText(getApplicationContext(), "[已拍照]",
                        Toast.LENGTH_SHORT).show();
            }
            //拍照结束继续预览
            camera.stopPreview();
        }


    }
    @Override
    public void onBackPressed() {
        //设置按Back键时返回给前一个Activity的结果值
        setResult(MapPhotoGE.RESULT_MAPVIEW_BACK);
        finish();
    }


    //高德地图和地理定位需要用到的一些回调方法
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        //解除位置变化监听(省电目的)
        manager.removeUpdates(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //系统若检测到位置变化，自动触发本方法，从而更新位置值
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //when a provider is unable to fetch a location
            //or the provider becomes available or unavailability
        }

        @Override
        public void onProviderEnabled(String provider) {
            //when the provider is disavled by the user
        }

        @Override
        public void onProviderDisabled(String provider) {
            //when the provider is enabled by the user
        }


    }





    /*
    *        自定义CameraSurfaceView类,实现相机预览和拍照界面
     */

    private class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder surfaceHolder = null;

        public CameraSurfaceView(Context context) {
            super(context);
            //保存serfaceHolder设定毁掉对象
            surfaceHolder = this.getHolder();
            surfaceHolder.addCallback(this);
        }


        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //当界面变化时，暂停取景预览
            camera.stopPreview();
            surfaceHolder = holder;
            //指定相机参数；图片分辨率，横竖屏切换，自动聚焦
            android.hardware.Camera.Parameters param = camera.getParameters();
            //指定拍照图片的大小
            List<Camera.Size> sizes = param.getSupportedPictureSizes();
            Collections.sort(sizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(android.hardware.Camera.Size s1, android.hardware.Camera.Size s2) {
                    //倒排序，确保大的预览分辨率在前
                    return s2.width - s1.width;
                }
            });
            for (android.hardware.Camera.Size size : sizes) {
                //拍照分辨率不能设置过大，否则会造成OutOfMemoryException异常
                if (size.width <= 1200) {
                    param.setPictureSize(size.width, size.height);
                    break;
                    //continue;
                }
            }
            //横竖屏镜头自动调整
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                //设置为竖屏取景预览
                param.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
            } else {
                //设置为横竖屏取景预览
                param.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
            }
            //设置相机为自动聚焦模式
            List<String> foucusModes = param.getSupportedFlashModes();
            if (foucusModes.contains(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO)) {
                param.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
            }
            //使参数设置生效
            camera.setParameters(param);
            //设置相机取景预览的缓冲区内存，取决于画面宽，高和每像素占用的字节数
            int imgformat = param.getPreviewFormat();
            int bitsperpixel = ImageFormat.getBitsPerPixel(imgformat);
            android.hardware.Camera.Size camerasize = param.getPreviewSize();
            int frame_size = ((camerasize.width * camerasize.height) * bitsperpixel) / 8;
            byte[] frame = new byte[frame_size];
            //相机取景预览时会将预览画面图像存到frame变量中
            camera.addCallbackBuffer(frame);
            camera.setPreviewCallbackWithBuffer(previewCallback);
            //启动相机取景预览
            camera.startPreview();
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (camera == null) {
                //获取相机服务
                camera = android.hardware.Camera.open();
            }
            try {
                //设置预览画面的显示场所
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                //若链接相机失败，则释放资源-----------------------------------------------------------
                camera.release();
                camera = null;
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //停止预览，释放系统相机服务
            camera.setPreviewCallback(null);//!!这个必须在前，不然退出出错
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        /*
       * camera取景预览回调接口
        */
        private android.hardware.Camera.PreviewCallback previewCallback = new android.hardware.Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
                //重复讲预览的画面帧图像放到同一个data缓冲区中
                camera.addCallbackBuffer(data);
            }
        };

    }

    private LatLng getMyLocation() {
        LatLng position = null;
        //得到系统最近一次检测到的地理位置
        Location location = manager.getLastKnownLocation(provider);
        //如果系统检测的位置无效，则使用默认位置
        if (location == null) {
            position = new LatLng(myLatitude, myLongitude);
        } else {
            position = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        //记录当前位置经纬度
        myLatitude = position.latitude;
        myLongitude = position.longitude;
        return position;
    }
}
