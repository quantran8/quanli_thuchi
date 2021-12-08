package com.example.qlthuchi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegistedActivity extends AppCompatActivity {
    Database  db= new Database(this,"user.sqlite",null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registed);
        String sql ="CREATE TABLE IF NOT EXISTS users (Id INTEGER PRIMARY KEY AUTOINCREMENT, hoten VARCHAR(30),dt VARCHAR(11) , matkhau VARCHAR(30))";
        db.QueryData(sql);

        Button create = (Button) findViewById(R.id.create);
        Button back = (Button) findViewById(R.id.back);
        TextView name = (TextView)findViewById(R.id.name);
        TextView phone = (TextView)findViewById(R.id.phone);
        TextView pass = (TextView)findViewById(R.id.password);
        TextView confirm_pass = (TextView)findViewById(R.id.confirm_password);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder al = new AlertDialog.Builder(RegistedActivity.this);
                if(name.getText().toString().equals("") || phone.getText().toString().equals("") || pass.getText().toString().equals("")||confirm_pass.getText().toString().equals("")){

                    al.setTitle("Thông báo");
                    al.setMessage("Bạn chưa nhập đầy đủ thông tin");
                    al.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            al.setCancelable(true);
                        }
                    });
                    al.show();


                }
                else {
                    if(pass.getText().toString().equals(confirm_pass.getText().toString())){
                       String sql = "INSERT INTO users VALUES(null,' "+name.getText().toString()+"' , '"+phone.getText().toString()+"', '"+pass.getText().toString()+"' )";
                       db.QueryData(sql);
                        al.setTitle("Thông báo");
                        al.setMessage("Đăng kí thành công");
                        al.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                al.setCancelable(true);
                            }
                        });
                        al.show();
                    }
                    else {
                        al.setTitle("Thông báo");
                        al.setMessage("Mật khẩu không khớpq");
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistedActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}