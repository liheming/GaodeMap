package com.example.administrator.gaodemapabc;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by haily on 2015/12/28.
 */

public class Login extends Activity implements View.OnClickListener {
    private CheckBox showPaw;
    private Button btnLogin, btnRegister, btnSearch;
    private EditText editUsername;
    private EditText editPassword;
    private SQLiteDatabase db;
    private TextView textView;

    private void initdata() {
        setContentView(R.layout.activity_login);
        editUsername = (EditText) findViewById(R.id.editUN);
        editPassword = (EditText) findViewById(R.id.editMM);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnWriteData);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        textView = (TextView) findViewById(R.id.textView);
        showPaw = (CheckBox) findViewById(R.id.showPaw);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        db = openOrCreateDatabase("mydb.db", Context.MODE_PRIVATE, null);
        db.execSQL(" create table if not exists user (_id integer primary key autoincrement,"
                + "username varchar not null,"
                + "password varchar not null )");
    }

    private void register() {
        ContentValues cv = new ContentValues();
        cv.put("username", editUsername.getText().toString());
        cv.put("password", editPassword.getText().toString());
        db.insert("user", null, cv);
        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initdata();
        showPaw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
//                    Toast.makeText(getApplicationContext(), "我被选择了", Toast.LENGTH_SHORT).show();
                    editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });
//        db.execSQL("delete from user where username = '' ");
//        db.delete("user","username=?",new String[]{"''"});
//        db.delete("user", null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                System.out.println("-------------------------------");
                Cursor cursor = db.query("user", null, null, null, null, null, null);
                if (isTrue(cursor)) {
                    startActivity(new Intent(this, MapPhotoGE.class));
                    Log.i("login", "btnLogin  else  ");
                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("login", "btnLogin 用户名或密码错误  ");
                    Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnWriteData:
                Log.i("login", "onClick:btnRegister  ");
                if (editUsername.length() == 0) {
                    Log.i("login", "onClick:editUsername==null  ");
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (editUsername.length() < 2) {
                    Log.i("login", "onClick:editUsername==null  ");
                    Toast.makeText(getApplicationContext(), "用户名小于2位", Toast.LENGTH_SHORT).show();
                } else if (editPassword.length() == 0) {
                    Log.i("login", "onClick:editPassword==null  ");
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();

                } else if (editPassword.length() < 6) {
                    Log.i("login", "onClick:editPassword==null  ");
                    Toast.makeText(getApplicationContext(), "密码不少于6位", Toast.LENGTH_SHORT).show();

                } else {
                    Log.i("login", "onClick:register  ");
                    register();
                }
                break;
            case R.id.btnSearch:
                textView.setText("");
                String sql3 = "select * from user"; //sql传统语句
                cursor = db.rawQuery(sql3, new String[]{});
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("username"));
                    String pass = cursor.getString(cursor.getColumnIndex("password"));

                    // String age=cursor.getString(2);
                    System.out.println("名字：" + name);
                    // textView.setText("");
                    textView.append("用户名:" + name + "-------------------密码" + pass + "\n");
                }
//                Cursor cursor = db.query("user", null, null,//面向对象
//                        null, null, null, null);
//                while (cursor.moveToNext()) {
//                    String name = cursor.getString(cursor.getColumnIndex("name"));
//                    // String age=cursor.getString(2);
//                    System.out.println("名字：" + name);
//                    // textView.setText("");
//                    textView.append("姓名:" + name + "\n");
//                }
                break;
        }
    }

    private Boolean isTrue(Cursor cursor) {
        Boolean istr = false;
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            if (editUsername.getText().toString().equals(username) && editPassword.getText().toString().equals(password)) {
                Log.i("login", "onClick:false if  ");
                istr = true;
                break;
            } else {
                istr = false;

            }
        }
        Log.i("login", "onClick:false ");
        return istr;
    }
}
