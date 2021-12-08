package com.example.qlthuchi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    Database  db= new Database(this,"user.sqlite",null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        String sql ="CREATE TABLE IF NOT EXISTS users (Id INTEGER PRIMARY KEY AUTOINCREMENT, hoten VARCHAR(30),dt VARCHAR(11) , matkhau VARCHAR(30))";
        db.QueryData(sql);

        Button login = (Button) findViewById(R.id.login);
        Button registed = (Button) findViewById(R.id.registed);
        EditText phone = (EditText) findViewById(R.id.phone);
        EditText pass = (EditText) findViewById(R.id.pass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
                if(!phone.getText().toString().equals("") && !pass.getText().toString().equals("")){
                    String sql = "SELECT * FROM users WHERE dt= '"+phone.getText().toString()+"' AND matkhau ='"+pass.getText().toString()+"'";
                    Cursor data = db.GetData(sql);
                    if(data.moveToNext()){
                        al.setTitle("Thông báo");
                        al.setMessage("Đăng nhập thành công");
                        al.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                al.setCancelable(true);
                            }
                        });
                        al.show();
                        Log.d("tong ba0",data.getString(2));
                    }
                    else{
                        al.setTitle("Thông báo");
                        al.setMessage("Tài khoản hoặc mật khẩu không chính xác");
                        al.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                al.setCancelable(true);
                            }
                        });
                        al.show();
                    }
                }
            }
        });

        registed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegistedActivity.class);
                startActivity(intent);
            }
        });

    }
}