package com.example.qlthuchi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContentInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
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
        long num_thunhap=0;
        long num_chitieu=0;
        long day_thuchi=0;
        if(c.moveToFirst())
        {
            do {
                String money = c.getString(2);
                String date = c.getString(3);
                long unixDate = Long.parseLong(date);
                unixDate = unixDate - (unixDate % 86400);
                if(unixDate != oldDate) {
                    if(oldDate!=-1L)
                    {
                        calendar.setTimeInMillis(oldDate*1000L);
                        if(day_thuchi>0)
                            events.add(new EventDay(calendar,R.drawable.ic_baseline_arrow_drop_up_24,Color.parseColor("#00FFFF")));
                        else if(day_thuchi<0)
                            events.add(new EventDay(calendar,R.drawable.ic_baseline_arrow_drop_down_24,Color.parseColor("#FF0000")));
                        else
                            events.add(new EventDay(calendar,R.drawable.ic_baseline_stop_24,Color.parseColor("#8888CC")));
                        day_thuchi=0;
                    }
                    oldDate = unixDate;
                    calendar.setTimeInMillis(unixDate * 1000L);
                }
                long change = Long.parseLong(money);
                day_thuchi+=change;
                if(change>0)
                    num_thunhap+=change;
                else if(change<0)
                    num_chitieu+=change;
            }while(c.moveToNext());

            if(oldDate!=-1L) {
                calendar.setTimeInMillis(oldDate * 1000L);
                if (day_thuchi > 0)
                    events.add(new EventDay(calendar, R.drawable.ic_baseline_arrow_drop_up_24, Color.parseColor("#00FFFF")));
                else if (day_thuchi < 0)
                    events.add(new EventDay(calendar, R.drawable.ic_baseline_arrow_drop_down_24, Color.parseColor("#FF0000")));
                else
                    events.add(new EventDay(calendar, R.drawable.ic_baseline_stop_24, Color.parseColor("#8888CC")));
            }

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
        liteDB.close();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        //----------------------- get user data (phone number ) from app local data----------------------------------------------------
        SharedPreferences sp = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userPhoneNumber = sp.getString("phone","");

        String sql ="CREATE TABLE IF NOT EXISTS money (Id INTEGER PRIMARY KEY AUTOINCREMENT, NoiDung VARCHAR(30), Tien INTEGER NOT NULL , Ngay INTEGER)";
        db.QueryData(sql);
        updateCalendar();
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("phone");
                editor.commit();
                Intent intent = new Intent(ScreenActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
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
                                        updateCalendar();
                                        Toast.makeText(ScreenActivity.this,"Nhập thành công",Toast.LENGTH_SHORT).show();
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
        CalendarView calendarView = (CalendarView) findViewById(R.id.appCalendar);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                long unixLow = clickedDayCalendar.getTimeInMillis() / 1000L;
                long unixHigh = unixLow + 86400L;
                ArrayList<Item> list = new ArrayList<>();
                String selectCommand = "SELECT * FROM money WHERE Ngay>='" + unixLow + "' AND Ngay<'" + unixHigh + "'";
                SQLiteDatabase liteDB = db.getReadableDatabase();
                Cursor c = liteDB.rawQuery(selectCommand, null);
                if (c.moveToFirst()) {
                    do {
                        list.add(new Item(c.getString(1),c.getString(2)));
                    }
                    while (c.moveToNext());
                }
                List<Calendar> listCalendar = new ArrayList<>();
                listCalendar.add(clickedDayCalendar);
                calendarView.setHighlightedDays(listCalendar);
                ListAdapter adapter = new ListAdapter(list);
                ListView listView = findViewById(R.id.listView);
                listView.setAdapter(adapter);
            }
        });
    }
}

class Item
{
    String noidung;
    String money;

    public Item(String noidung, String money)
    {
        this.noidung=noidung;
        this.money=money;
    }
}

class ListAdapter extends BaseAdapter
{
    final ArrayList<Item> list;

    public ListAdapter(ArrayList<Item> list)
    {
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
    DecimalFormat df = (DecimalFormat)nf;

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View productView;
        if(convertView==null)
            productView = View.inflate(parent.getContext(),R.layout.listitem,null);
        else
            productView = convertView;

        Item item = (Item)getItem(i);
        df.applyPattern("###,### đ");
        ((TextView) productView.findViewById(R.id.list_noidung)).setText(item.noidung);
        long change = Long.parseLong(item.money);
        if(change>0) {
            ((TextView) productView.findViewById(R.id.list_money)).setText(df.format(change));
            ((TextView) productView.findViewById(R.id.list_money)).setTextColor(Color.parseColor("#00FFFF"));
        }
        else{
            ((TextView) productView.findViewById(R.id.list_money)).setText(df.format(-change));
            ((TextView) productView.findViewById(R.id.list_money)).setTextColor(Color.parseColor("#FF0000"));
        }

        return productView;
    }
}