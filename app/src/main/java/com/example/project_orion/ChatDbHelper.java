package com.example.project_orion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ChatDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat_history.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_IS_USER = "is_user"; // 1 para user, 0 para AI

    public ChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Cria a tabela
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MESSAGES_TABLE = "CREATE TABLE " +
                TABLE_MESSAGES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXT + " TEXT NOT NULL, " +
                COLUMN_IS_USER + " INTEGER NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_MESSAGES_TABLE);
    }

    // Atualiza a tabela (se a versão mudar)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    // Metodo para salvar uma única mensagem
    public void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, message.text);
        // SQLite armazena boolean como INTEGER (1 = true, 0 = false)
        values.put(COLUMN_IS_USER, message.isUser ? 1 : 0);

        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    // Metodo para carregar todas as mensagens
    public List<Message> getAllMessages() {
        List<Message> messageList = new ArrayList<>();
        // Ordena por ID para garantir a ordem da conversa
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " ORDER BY " + COLUMN_ID + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT));

                boolean isUser = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER)) == 1;

                Message message = new Message(text, isUser);
                messageList.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messageList;
    }
}
