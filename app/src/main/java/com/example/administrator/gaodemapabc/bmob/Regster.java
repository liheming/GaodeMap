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

import com.example.administrator.gaodemapabc.R;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by haily8.1 on 2016/1/5.
 */
public class Regster extends AppCompatActivity {
    private EditText ed_email,ed_pass,ed_pass_again,ed_username,ed_get_yanzm;
    private Button bt_register ,bt_get_yanzm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_pass = (EditText) findViewById(R.id.ed_pass);
        ed_pass_again = (EditText) findViewById(R.id.ed_pass_again);
        ed_username = (EditText) findViewById(R.id.ed_username);
        ed_get_yanzm = (EditText) findViewById(R.id.ed_yanzm);
        bt_register = (Button) findViewById(R.id.btn_register);
        bt_get_yanzm = (Button) findViewById(R.id.bt_get_yanzm);
        bt_get_yanzm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobSMS.requestSMSCode(Regster.this,ed_email.getText().toString(),"mode1", new RequestSMSCodeListener()
                {

                    @Override
                    public void done(Integer integer, BmobException e) {
                        if (e==null) {
                            Toast.makeText(getApplicationContext(),"短信验证码发送成功"+"短信id："+integer,Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                BmobSMS.verifySmsCode(Regster.this,"18942433927", ed_get_yanzm.getText().toString(), new VerifySMSCodeListener() {
//                    @Override
//                    public void done(BmobException ex) {
//                        // TODO Auto-generated method stub
//                        if(ex==null){//短信验证码已验证成功
//                            Log.i("smile", "验证通过");
//
//
//                        }else{
//                            Log.i("smile", "验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
//                        }
//                    }
//                });






                if (ed_email.length() == 0) {
                    Log.i("login", "onClick:editUsername==null  ");
                    Toast.makeText(getApplicationContext(), "请输入11手机号码", Toast.LENGTH_SHORT).show();
                } else if (ed_email.length() != 11) {
                    Log.i("login", "onClick:editUsername==null  ");
                    Toast.makeText(getApplicationContext(), "手机号为11位", Toast.LENGTH_SHORT).show();
                } else if (ed_pass.length() == 0||(ed_pass_again.length()==0 )  ) {
                    Log.i("login", "onClick:editPassword==null  ");
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();

                } else if (ed_pass.length() < 6 ||  (ed_pass_again.length() < 6  )) {
                    Log.i("login", "onClick:editPassword==null  ");
                    Toast.makeText(getApplicationContext(), "密码不少于6位", Toast.LENGTH_SHORT).show();

                } else {
                    Log.i("login", "onClick:register  ");



                    if (ed_pass.getText().toString().equals(ed_pass_again.getText().toString())) {

                        MyUser user = new MyUser();
                        user.setMobilePhoneNumber(ed_email.getText().toString());//设置手机号码（必填）
                user.setUsername(ed_username.getText().toString());                  //设置用户名，如果没有传用户名，则默认为手机号码
                        user.setPassword(ed_pass.getText().toString());                  //设置用户密码
//                user.setAge(18);                        //设置额外信息：此处为年龄
                        user.signOrLogin(Regster.this, ed_get_yanzm.getText().toString(), new SaveListener() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                startActivity(new Intent(Regster.this, Loginew.class));
                                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();

//                        toast("注册或登录成功");
//                        Log.i("smile", ""+user.getUsername()+"-"+user.getAge()+"-"+user.getObjectId());
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Toast.makeText(getApplicationContext(), "注册失败" + msg + "错误码" + code, Toast.LENGTH_SHORT).show();
//                        toast("错误码："+code+",错误原因："+msg);
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
                    }




                }









//                if (ed_pass.getText().toString().equals(ed_pass_again.getText().toString())) {
//
//                    MyUser user = new MyUser();
//                    user.setMobilePhoneNumber(ed_email.getText().toString());//设置手机号码（必填）
////                user.setUsername(xxx);                  //设置用户名，如果没有传用户名，则默认为手机号码
//                    user.setPassword(ed_pass.getText().toString());                  //设置用户密码
////                user.setAge(18);                        //设置额外信息：此处为年龄
//                    user.signOrLogin(Regster.this, ed_get_yanzm.getText().toString(), new SaveListener() {
//
//                        @Override
//                        public void onSuccess() {
//                            startActivity(new Intent(Regster.this, Loginew.class));
//                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
//
////                        toast("注册或登录成功");
////                        Log.i("smile", ""+user.getUsername()+"-"+user.getAge()+"-"+user.getObjectId());
//                        }
//
//                        @Override
//                        public void onFailure(int code, String msg) {
//                            Toast.makeText(getApplicationContext(), "注册失败" + msg + "错误码" + code, Toast.LENGTH_SHORT).show();
////                        toast("错误码："+code+",错误原因："+msg);
//                        }
//                    });
//                } else {
//                    Toast.makeText(getApplicationContext(),"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
//                }








//                BmobUser bu=new BmobUser();
//                bu.setEmail(ed_email.getText().toString());
//                bu.setPassword(ed_pass.getText().toString());
//                bu.setUsername(ed_username.getText().toString());
//                bu.signUp(Regster.this, new SaveListener() {
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(getApplicationContext(), "注册成功",Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public void onFailure(int i, String s) {
//                        Toast.makeText(getApplicationContext(), "注册失败"+s+"错误码"+i,Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });

    }
}
