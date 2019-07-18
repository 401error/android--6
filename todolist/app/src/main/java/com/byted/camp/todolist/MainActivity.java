package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper mDbHelper ;
   private  SQLiteDatabase db ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbHelper = new TodoDbHelper(this);
        db = mDbHelper.getReadableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }





    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans

        if(db==null)
        {
            return Collections.emptyList();

        }
        List<Note> result =new LinkedList<>();
        Cursor cursor = null;
        try
        {
            cursor=db.query(TodoContract.List_todo.TABLE_NAME,
                   new  String[]{TodoContract.List_todo.THINGS_NAME, TodoContract.List_todo._ID,
                           TodoContract.List_todo.TIME,
                    TodoContract.List_todo.STATE},null,
                    null,null,null,
                    TodoContract.List_todo.TIME+" DESC"
                    );

            while (cursor.moveToNext())
            {
                long ID =cursor.getLong(cursor.getColumnIndex(TodoContract.List_todo._ID));
                String content =cursor.getString(cursor.getColumnIndex(TodoContract.List_todo.THINGS_NAME));
                long date = cursor.getLong(cursor.getColumnIndex(TodoContract.List_todo.TIME));
                int intdata =cursor.getInt(cursor.getColumnIndex(TodoContract.List_todo.STATE));

                Note note =new Note(ID) ;
                note.setContent(content);
                note.setDate(new Date(date));
                note.setState(State.from(intdata));
                result.add(note);

            }



        }
        finally {
            if(cursor != null)
            {
                cursor.close();
            }
        }
        return result;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据

        String selection = TodoContract.List_todo.THINGS_NAME+" LIKE ?";

        String[] selectionArgs={ note.getContent()};

        int deletedROWS =db.delete(TodoContract.List_todo.TABLE_NAME,selection,selectionArgs);
    }

    private void updateNode(Note note) {



   ContentValues values = new ContentValues();
   values.put(TodoContract.List_todo.STATE,note.getState().intValue);

   String selection = TodoContract.List_todo._ID+" LIKE ?";
   String[] selectionArgs ={String.valueOf(note.id)};

   int count=db.update(
                TodoContract.List_todo.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        // 更新数据
    }


}
