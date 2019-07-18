package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    private TodoContract() {
    }

    public static class List_todo implements BaseColumns
    {
        public static final String TABLE_NAME="LIST";
        public static final String THINGS_NAME="todo_name";
        public static final String TIME="todo_time";
        public static final String STATE="todo_state";
    }

    public   static  final String SQL_CREATE_LIST_TODO=
            "CREATE TABLE "+List_todo.TABLE_NAME+" ("
                    +List_todo._ID +" INTEGER PRIMARY KEY,"+
                    List_todo.THINGS_NAME +" TEXT,"+
                    List_todo.STATE+" TEXT,"+
                    List_todo.TIME +" DATE)";

    public   static  final  String SQ_DELETE_LIST_TODO=
            "DROP TABLE IF EXISTS " + List_todo.TABLE_NAME;
}
