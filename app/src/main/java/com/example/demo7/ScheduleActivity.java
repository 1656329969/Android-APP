package com.example.demo7;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class ScheduleActivity extends AppCompatActivity {
    private AppCompatImageButton backButton;
    private Button addScheduleButton;
    private ListView scheduleListView;
    private EditText searchEditText;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> scheduleList;
    private ArrayList<String> filteredScheduleList;
    private final String FILE_NAME = "schedules.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        backButton = findViewById(R.id.back_button);
        addScheduleButton = findViewById(R.id.addScheduleButton);
        scheduleListView = findViewById(R.id.scheduleListView);
        searchEditText = findViewById(R.id.searchEditText);

        scheduleList = loadSchedules();
        filteredScheduleList = new ArrayList<>(scheduleList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredScheduleList);
        scheduleListView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cale1 = Calendar.getInstance();
                new DatePickerDialog(ScheduleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        showAddScheduleDialog(year, monthOfYear, dayOfMonth);
                    }
                }, cale1.get(Calendar.YEAR), cale1.get(Calendar.MONTH), cale1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showEditScheduleDialog(position);
            }
        });

        scheduleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                showDeleteScheduleDialog(position);
                return true;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterScheduleList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void showAddScheduleDialog(int year, int month, int day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加日程");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);
        final EditText scheduleInput = dialogView.findViewById(R.id.scheduleInput);
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String schedule = year + "-" + (month + 1) + "-" + day + ": " + scheduleInput.getText().toString();
                scheduleList.add(schedule);
                sortScheduleList();
                filterScheduleList(searchEditText.getText().toString());
                saveSchedules();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    private void showEditScheduleDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改日程");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);
        final EditText scheduleInput = dialogView.findViewById(R.id.scheduleInput);
        scheduleInput.setText(filteredScheduleList.get(position).split(": ")[1]);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String schedule = filteredScheduleList.get(position).split(": ")[0] + ": " + scheduleInput.getText().toString();
                int originalPosition = scheduleList.indexOf(filteredScheduleList.get(position));
                scheduleList.set(originalPosition, schedule);
                sortScheduleList();
                filterScheduleList(searchEditText.getText().toString());
                saveSchedules();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    private void showDeleteScheduleDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除日程");
        builder.setMessage("您确定要删除此日程吗？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int originalPosition = scheduleList.indexOf(filteredScheduleList.get(position));
                scheduleList.remove(originalPosition);
                filterScheduleList(searchEditText.getText().toString());
                saveSchedules();
            }
        });
        builder.setNegativeButton("否", null);
        builder.create().show();
    }

    private void sortScheduleList() {
        Collections.sort(scheduleList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    private void filterScheduleList(String query) {
        filteredScheduleList.clear();
        for (String schedule : scheduleList) {
            if (schedule.toLowerCase().contains(query.toLowerCase())) {
                filteredScheduleList.add(schedule);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void saveSchedules() {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            for (String schedule : scheduleList) {
                fos.write((schedule + "\n").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> loadSchedules() {
        ArrayList<String> schedules = new ArrayList<>();
        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                schedules.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedules;
    }
}
