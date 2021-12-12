package com.example.qlthuchi;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ScreenActivity extends Activity {
    Database  db= new Database(this,"money.sqlite",null,1);

    private void updateCalendar()
    {
        SQLiteDatabase liteDB = db.getReadableDatabase();
        String select = "SELECT * FROM money ORDER BY Ngay ASC";
        Cursor c = liteDB.rawQuery(select,null);
        CalendarView calendarView = (CalendarView) findViewById(R.id.appCalendar);
        List<EventDay> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        long oldDate = -1L;
        boolean increase = false;
        boolean decrease= false;
        long num_thunhap=0;
        long num_chitieu=0;
        if(c.moveToFirst())
        {
            do {
                String money = c.getString(2);
                String date = c.getString(3);
                long unixDate = Long.parseLong(date);
                unixDate = unixDate - (unixDate % 86400);
                if(unixDate != oldDate)
                {
                    oldDate = unixDate;
                    increase=false;
                    decrease=false;
                    calendar.setTimeInMillis(unixDate*1000L);
                    if(Integer.parseInt(money)>0) {
                        increase = true;
                        events.add(new EventDay(calendar, R.drawable.ic_baseline_arrow_drop_up_24, Color.parseColor("#00FFFF")));
                        num_thunhap+=Long.parseLong(money);
                    }
                    else {
                        decrease=true;
                        events.add(new EventDay(calendar, R.drawable.ic_baseline_arrow_drop_down_24, Color.parseColor("#FF0000")));
                        num_chitieu+=Long.parseLong(money);
                    }
                }
                else
                {
                    if(increase==true&&decrease==true) {
                        long change = Long.parseLong(money);
                        if(change>0)
                            num_thunhap+=change;
                        else num_chitieu+=change;
                        continue;
                    }
                    if(Integer.parseInt(money)>0)
                    {
                        if(increase==true) {
                            num_thunhap+=Long.parseLong(money);
                            continue;
                        }
                        increase=true;
                        events.add(new EventDay(calendar, R.drawable.ic_baseline_arrow_drop_up_24, Color.parseColor("#00FFFF")));
                    }
                    else
                    {
                        if(decrease==true) {
                            num_chitieu+=Long.parseLong(money);
                            continue;
                        }
                        decrease=true;
                        events.add(new EventDay(calendar, R.drawable.ic_baseline_arrow_drop_down_24, Color.parseColor("#FF0000")));
                    }
                }
            }while(c.moveToNext());
            calendarView.setEvents(events);
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
            DecimalFormat df = (DecimalFormat)nf;
            df.applyPattern("###,### đ");
            TextView txt_thunhap = findViewById(R.id.num_thunhap);
            txt_thunhap.setText(df.format(num_thunhap));
            TextView txt_chitieu = findViewById(R.id.num_chitieu);
            txt_chitieu.setText(df.format(-num_chitieu));
            TextView txt_tong = findViewById(R.id.num_tong);
            txt_tong.setText(df.format(num_thunhap+num_chitieu));
            if(num_thunhap<num_chitieu)
                txt_tong.setTextColor(Color.parseColor("#FF0000"));
            else txt_tong.setTextColor(Color.parseColor("#00FFFF"));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        String sql ="CREATE TABLE IF NOT EXISTS money (Id INTEGER PRIMARY KEY AUTOINCREMENT, NoiDung VARCHAR(30), Tien INTEGER NOT NULL , Ngay INTEGER)";
        db.QueryData(sql);
        updateCalendar();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert1 = new AlertDialog.Builder(ScreenActivity.this);
                final EditText editTextND = new EditText(ScreenActivity.this);
                final String[] txtNoiDung = new String[1];
                alert1.setTitle("Thêm bản ghi");
                alert1.setMessage("Nhập nội dung:");
                alert1.setView(editTextND);
                alert1.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txtNoiDung[0] =editTextND.getText().toString();
                        AlertDialog.Builder alert2 = new AlertDialog.Builder(ScreenActivity.this);
                        final EditText editTextMoney = new EditText(ScreenActivity.this);
                        final String[] txtMoney = new String[1];
                        alert2.setTitle("Thêm bản ghi");
                        alert2.setMessage("Nhập số lượng tiền:");
                        alert2.setView(editTextMoney);
                        alert2.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(editTextMoney.getText().toString().equals(""))
                                    Toast.makeText(ScreenActivity.this,"Không thể để trống số tiền",Toast.LENGTH_SHORT).show();
                                else {
                                    try {
                                        txtMoney[0] = editTextMoney.getText().toString();
                                        int check = Integer.parseInt(txtMoney[0]);
                                        if(check==0) {
                                            Toast.makeText(ScreenActivity.this, "Không thể để trống số tiền", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        String command = "INSERT INTO money VALUES(null,'" + txtNoiDung[0] + "','" + txtMoney[0] + "',strftime('%s','now'))";
                                        db.QueryData(command);
                                        Toast.makeText(ScreenActivity.this,"Nhập thành công",Toast.LENGTH_SHORT).show();
                                        updateCalendar();
                                    }
                                    catch(Exception ex)
                                    {
                                        Toast.makeText(ScreenActivity.this,"Nhập tiền sai",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        alert2.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert2.setCancelable(true);
                            }
                        });
                        alert2.show();
                    }
                });
                alert1.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert1.setCancelable(true);
                    }
                });
                alert1.show();
            }
        });
    }
}
