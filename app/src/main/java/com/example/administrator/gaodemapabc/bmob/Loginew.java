package com.example.administrator.gaodemapabc.bmob;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.gaodemapabc.MapPhotoGE;
import com.example.administrator.gaodemapabc.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by haily8.1 on 2016/1/5.
 */
public class Loginew extends AppCompatActivity implements View.OnClickListener {
    private EditText ed_email, ed_pass;
    private Button bt_loagin, bt_to_reg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, "1fe47f6bb8ec6a3eb640c3617952b5a6");

        setContentView(R.layout.login);
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_pass = (EditText) findViewById(R.id.ed_pass);
        bt_loagin = (Button) findViewById(R.id.btn_login);
        bt_to_reg = (Button) findViewById(R.id.btn_to_reg);
        bt_loagin.setOnClickListener(this);
        bt_to_reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                BmobUser bu2 = new BmobUser();
                bu2.setUsername(ed_email.getText().toString());
                bu2.setPassword(ed_pass.getText().toString());
                bu2.login(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
//                        Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(Loginew.this, MainActivity.class));


                        startActivity(new Intent(Loginew.this, MapPhotoGE.class));
                        Log.i("login", "btnLogin  else  ");
                        Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        Toast.makeText(getApplicationContext(), "登陆失败" + msg + "错误码" + code, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_to_reg:
                startActivity(new Intent(this, Regster.class));
                break;
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
////            exitBy2clickBack();
//
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            Toast.makeText(getApplicationContext(), "you click menu button", Toast.LENGTH_SHORT).show();
//        }
//        if (keyCode==KeyEvent.KEYCODE_BACK) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("是否退出");
//            builder.setMessage("确定退出吗");
//            builder.setPositiveButton("确定",new  DialogInterface.OnClickListener(){
//
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                }
//            });
//            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                }
//            });
//            builder.show();
//
//        }

//    }






    //    private static  Boolean isexit = false;
//    private void exitBy2clickBack() {
//        Timer time = null;
//        if (isexit == false) {
//            isexit=true;
//            time = new Timer();
//            Toast.makeText(getApplicationContext(),"再按一次返回键推出程序",Toast.LENGTH_SHORT).show();
//            time.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    isexit = false;
//                }
//            },2000) ;
//        }else {
//            finish();
//            System.exit(0);
//        }
//    }


    private long mPressedTime = 0;

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {//比较两次按键时间差
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
            System.out.println("mNowTime:"+mNowTime  + "mPressedTime:"+ mPressedTime);
        } else {//退出程序
            this.finish();
            System.out.println("mNowTime:"+mNowTime  + "mPressedTime:"+ mPressedTime);
            System.exit(0);
        }
    }

}
