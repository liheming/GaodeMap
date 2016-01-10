package com.example.administrator.gaodemapabc;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.example.administrator.gaodemapabc.bean.RowInfoBean;


import java.util.ArrayList;
import java.util.List;

public class MapPhotoGE extends AppCompatActivity {
    public static final int REQUEST_MAPVIEW = 11;//请求码
    public static final int RESULT_MAPVIEW_BACK=12;//返回码
    private MapView mapView;
    private AMap aMap;
    private LocationManager locationManager;
    //private Location currentlocation = null;
    //private EditText editText = null;
    //private ImageView imageView = null;
    private LinearLayout cameraBar = null;
    LinearLayout previewArea=null;
    LinearLayout snapArea=null;
    ImageView snap=null;

    private android.hardware.Camera camera=null;

    private ListView photoLishtView;
    //ListView显示数据行
    private List<RowInfoBean> photoList = new ArrayList<RowInfoBean>();
    //用于ListView组件显示Adapter
    private PhotoAdapter photoAdapter;

    private int seledRowIndex = -1;
    private MenuItem editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_ptoto_ge);
        //动态添加5个RowInfoBean，以便ListView组件显示
        //这是为了测试的目的，以便在功能未实现之前看到效果
        //初始化数据库
        initDB();
        //从数据库获取相册以便ListView组件显示
        photoList.clear();
        loadAlbumFromDB();

//        Drawable thumb = getResources().getDrawable(R.drawable.emblem);
//        for (int i = 0; i < 5; i++) {
//            photoList.add(new RowInfoBean(thumb, ""));
//        }
        //初始化ListView组件，设定其Adapter以便加载数据行显示
        photoLishtView = (ListView) findViewById(R.id.photoListView);
        photoAdapter = new PhotoAdapter(this);
        photoLishtView.setAdapter(photoAdapter);


        //长按条目事件-------------------------------注意！！！！！-----------------------------------
        photoLishtView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //处理选中或取消选中
                if (seledRowIndex == position) {
                    seledRowIndex = -1;
                    editMenu.setEnabled(false);//--闪退就是它*/
                } else {
                    seledRowIndex = position;
                    editMenu.setEnabled(true);//--闪退就是它*/
                }
                //通过ListView更新显示
                photoAdapter.notifyDataSetChanged();
                return true;
            }

        });
    }
    private void  loadAlbumFromDB(){
        //打开数据库
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
                Context.MODE_PRIVATE,null);
        //设定默认的缩略图
          Drawable defaultThumb =
                  getResources().getDrawable(R.drawable.emblem);
        //执行表 查询获取所有相册数据
        String sql = "select * from t_album";
        Cursor cursor = db.rawQuery(sql,null);
        //通过游标，循环处理查询每一条记录，兵生成对应相册条目数据
        while (cursor.moveToNext()){
            RowInfoBean bean = new RowInfoBean();
            bean.id =cursor.getInt(cursor.getColumnIndex("_id"));
            bean.title =cursor.getString(cursor.getColumnIndex("title"));
            //处理缩略图
            String thumb = cursor.getString(cursor.getColumnIndex("thumb"));
            if (thumb==null||thumb.equals("")){
                bean.thumb = defaultThumb;
            }
            else {
                bean.thumb = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(thumb));
            }
            photoList.add(bean);
        }
        //椽笔数据库
        cursor.close();
        db.close();
    }
    private void initDB(){
                //打开数据库（如果不存在自动创建）
        SQLiteDatabase db = openOrCreateDatabase("maphotos.db",
                Context.MODE_PRIVATE,null);
        String sql;

    //构造创建t_album表的SQL语句
    sql = "create table if not exists t_album("+
            "_id integer primary key autoincrement,"+
            "title varchar,thumb varchar)";
    db.execSQL(sql);

        //构造创建t_album_picture表的 SQL语句
        sql = "create table if not exists t_album_picture("+
                "_id integer primary key autoincrement,"+
                "latitude double, longitude double,"+
                "picture varchar,thumb varchar,album_id integer)";
        db.execSQL(sql);
        //关闭数据库
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_ptoto_ge, menu);
        //禁用修改名称菜单项
        editMenu = menu.findItem(R.id.menu_item_edit);
        editMenu.setEnabled(false);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //  菜单项的单击时间响应处理，目前只是显示一个Toast提示
        switch (item.getItemId()) {


            case R.id.menu_item_gallery:
                int albumId = -1;
                //如果选中了数据行，则得到对应相册的id，seledRowIndex不是相册的id
                if (seledRowIndex !=-1){
                    RowInfoBean bean = photoList.get(seledRowIndex);
                    albumId =bean.id;
                }
                Toast.makeText(this, "相册", Toast.LENGTH_SHORT).show();
                //启动相册浏览，同事将相册id传递过去
                Intent intent = new Intent(this,GalleryActivity.class);
                intent.putExtra("album_id", albumId);
                startActivity(intent);

                return true;
            case R.id.menu_item_add:

                //设定输入框
                final EditText txtTitle =new EditText(this);
                txtTitle.setInputType(InputType.TYPE_CLASS_TEXT);

                //动态创建对话框
                AlertDialog.Builder dialog =new AlertDialog.Builder(this);

                //设定对话框中的按钮（修改和返回）
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title =txtTitle.getText().toString();

                        //将新增条目数据保存对象到数据库
                        SQLiteDatabase db=openOrCreateDatabase("maphotos.db",
                                Context.MODE_PRIVATE,null);
                        String sql="insert into t_album(title,thumb) " +
                                "values('" + title + "','')";
                        db.execSQL(sql);
                        db.close();

                        //重新加载数据库数据并显示
                        photoList.clear();
                        loadAlbumFromDB();
                        photoAdapter.notifyDataSetChanged();

                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                //设定对话框标题和输入框，然后显示
                dialog.setTitle("新相册名称");
                dialog.setView(txtTitle);
                dialog.show();
                return  true
                        ;
            case R.id.menu_item_remove:
                // Toast.makeText(this,"remove",Toast.LENGTH_SHORT).show();
                //删除被选中的数据行，更新ListView的显示
              // photoList.remove(seledRowIndex);
               // photoAdapter.notifyDataSetChanged();
                if (seledRowIndex != -1) {
                    photoList.remove(seledRowIndex);
                    //重置选中项
                    seledRowIndex = -1;
                    photoAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "请长按条目",
                            Toast.LENGTH_SHORT).show();
                }


                return true;
            case R.id.menu_item_edit:
                //得到当前选中的数据行
                final RowInfoBean bean = photoList.get(seledRowIndex);//--闪退就是它
                //设定对话框中的输入值
                final EditText input = new EditText(this);//--闪退就是它
                input.setInputType(InputType.TYPE_CLASS_TEXT);//--闪退就是它
                input.setText(bean.title);
                //动态创建对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(this);//--闪退就是它
                //设定对话框长得按钮（修改和返回）
                builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {//--闪退就是它
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//--闪退就是它
                        //禁用修改条目菜单项
                        seledRowIndex=-1;
                        //editMenu.setEnabled(false);
                        //修改选中的数据行，并通知更新界面显示
                        bean.title=input.getText().toString();
                        photoAdapter.notifyDataSetChanged();
                    }
                });//--闪退就是它
                builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {//--闪退就是它
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//--闪退就是它
                        //警用修改条目选项
                        seledRowIndex=-1;
                        //editMenu.setEnabled(false);
                        //关闭对话框
                        photoAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });
                //设定对话框标题和输入框，然后显示
                builder.setTitle("修改名称");
                builder.setView(input);
                builder.show();//--闪退就是它*/
                Toast.makeText(this, "编辑", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * adpter for listView
     */
    protected class PhotoAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;

        public PhotoAdapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return photoList.size();
        }

        @Override
        public Object getItem(int position) {
            return photoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
//           ListView在显示数据时，会反复调用
        /// getView获取界面显示组件对象

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            //如果没有复用的行界面，则动态加载一个“行”布局进行
            if (view == null) {
                LinearLayout layout = (LinearLayout)
                        layoutInflater.inflate(R.layout.activity_main_listview_row, null);
                view = layout;
            }
            //初始化“行”布局中的组件
            ImageView thumbView = (ImageView) view.findViewById(R.id.imageViewThumb);
            TextView title = (TextView) view.findViewById(R.id.textViewTitle);
            //获取点击的数据行
            final RowInfoBean bean = photoList.get(position);
            thumbView.setBackgroundDrawable(bean.thumb);
            title.setText(bean.title);
            //处理被选中行的高亮显示
            if (seledRowIndex == position) {
                view.setBackgroundColor(Color.parseColor("#63B8FF"));
            } else {
                view.setBackgroundColor(Color.parseColor("#F0F8FF"));
            }
            //点击地图图标启动MapViewActivity-------------------------------------
            ImageView imageViewMap=(ImageView) view.findViewById(R.id.imageViewMap);
            imageViewMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapPhotoGE.this, MapViewActivity.class);

                    //将相册id和title传递给MapViewActivity
                    intent.putExtra("album_id",bean.id);
                    intent.putExtra("album_title", bean.title);
                    startActivity(intent);
                    //以REQUE _MAPVIEW 为请求码启动MapViewActivity
                   // startActivity(intent);
                    startActivityForResult(intent ,REQUEST_MAPVIEW);
                }
            });
            return view;
        }

    }
    @Override
    protected void  onActivityResult(int requestCode, int resultCode, Intent data){
        //resultCode 可以识别是从哪个Activity返回的
        switch (resultCode){
            case RESULT_MAPVIEW_BACK:
            //从MapViewActivity返回则重新加载相册条目
            photoList.clear();
            loadAlbumFromDB();
            //更新ListView组件显示
            photoAdapter.notifyDataSetChanged();
            break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}

