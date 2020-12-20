package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
     public static final String KEY_ITEM_TEXT = "item_text";
     public static final String KEY_ITEM_POSITION = "item_position";
     public static final int EDIT_TEXT_CODE = 114;

    EditText enterTask;
    TextView prompt;
    Button addTask;
    RecyclerView recyclerView;
    TaskAdapter taskAdapter;

    List<String> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterTask = findViewById(R.id.addTaskEditText);
        prompt = findViewById(R.id.prompt);
        addTask = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.tasksRecycler);

        loadTasks();

        TaskAdapter.OnClickListener clickListener = new TaskAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(KEY_ITEM_TEXT, tasks.get(position));
                intent.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(intent, EDIT_TEXT_CODE);

            }
        };

        TaskAdapter.OnLongClickListener longClickListener = new TaskAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                tasks.remove(position);
                taskAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item removed", Toast.LENGTH_SHORT).show();
                saveTasks();
            }
        };
        taskAdapter = new TaskAdapter(tasks, clickListener, longClickListener);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        enterTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String task = v.getText().toString();
                if (task.equals("")) {
                    return true;
                }
                tasks.add(task);
                taskAdapter.notifyItemInserted(tasks.size() - 1);
                enterTask.setText("");
                Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
                saveTasks();
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String updated_task = data.getStringExtra(KEY_ITEM_TEXT);
            int pos = data.getExtras().getInt(KEY_ITEM_POSITION);
            tasks.set(pos, updated_task);
            taskAdapter.notifyItemChanged(pos);
            saveTasks();
            Toast.makeText(getApplicationContext(), "Task updated", Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addTask(View view) {
        enterTask.setVisibility(View.VISIBLE);
        prompt.setVisibility(View.VISIBLE);

    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    private void loadTasks() {
        try {
            tasks = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            tasks = new ArrayList<>();
        }
    }

    private void saveTasks() {
        try {
            FileUtils.writeLines(getDataFile(), tasks);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }

}