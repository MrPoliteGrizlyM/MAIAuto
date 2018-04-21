package com.example.android.maiauto;



import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class QuizDbHelper extends SQLiteOpenHelper {

    String DB_PATH = null;
    private static String DB_NAME = "MAIAuto";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public QuizDbHelper(Context context) {
        super(context, DB_NAME, null, 10);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        context.deleteDatabase("MAIAuto");
        context.deleteDatabase("example");
        Log.e("Path 1", DB_PATH);
    }


    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }



    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return myDataBase.query(QuizContract.QuestionsTable.TABLE_NAME, null, null, null, null, null, null);
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();
        myDataBase = getReadableDatabase();
        Cursor c = myDataBase.rawQuery("SELECT * FROM " + QuizContract.QuestionsTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
                question.setImage(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_IMAGE)));
                question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION4)));
                question.setOption5(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION5)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER_NR)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;


        }

    public ArrayList<Question> getTopicQuestions(String index) {
        ArrayList<Question> questionList = new ArrayList<>();
        myDataBase = getReadableDatabase();
        Cursor c = myDataBase.rawQuery("SELECT * FROM " + QuizContract.QuestionsTable.TABLE_NAME + " WHERE TOPIC = " + index, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
                question.setImage(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_IMAGE)));
                question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION4)));
                question.setOption5(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION5)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER_NR)));
                question.setHint(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_HINT)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;


    }
}


//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class QuizDbHelper extends SQLiteOpenHelper {
//
//    String DB_PATH = null;
//    private static final String DATABASE_NAME = "MAIAuto.db";
//    private final Context myContext;
//
//    private SQLiteDatabase db;
//
//    public QuizDbHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        this.db = db;
//
//        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
//                QuizContract.QuestionsTable.TABLE_NAME + " ( " +
//                QuizContract.QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                QuizContract.QuestionsTable.COLUMN_QUESTION + " TEXT, " +
//                QuizContract.QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
//                QuizContract.QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
//                QuizContract.QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
//                QuizContract.QuestionsTable.COLUMN_ANSWER_NR + " INTEGER" +
//                ")";
//
//        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
//        fillQuestionsTable();
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuestionsTable.TABLE_NAME);
//        onCreate(db);
//    }
//
//    private void fillQuestionsTable() {
//        Question q1 = new Question("A is correct", "A", "B", "C", 1);
//        addQuestion(q1);
//        Question q2 = new Question("B is correct", "A", "B", "C", 2);
//        addQuestion(q2);
//        Question q3 = new Question("C is correct", "A", "B", "C", 3);
//        addQuestion(q3);
//        Question q4 = new Question("A is correct again", "A", "B", "C", 1);
//        addQuestion(q4);
//        Question q5 = new Question("B is correct again", "A", "B", "C", 2);
//        addQuestion(q5);
//    }
//
//    private void addQuestion(Question question) {
//        ContentValues cv = new ContentValues();
//        cv.put(QuizContract.QuestionsTable.COLUMN_QUESTION, question.getQuestion());
//        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION1, question.getOption1());
//        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION2, question.getOption2());
//        cv.put(QuizContract.QuestionsTable.COLUMN_OPTION3, question.getOption3());
//        cv.put(QuizContract.QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
//        db.insert(QuizContract.QuestionsTable.TABLE_NAME, null, cv);
//    }
//
//
//    public List<Question> getAllQuestions() {
//        List<Question> questionList = new ArrayList<>();
//        db = getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT * FROM " + QuizContract.QuestionsTable.TABLE_NAME, null);
//
//        if (c.moveToFirst()) {
//            do {
//                Question question = new Question();
//                question.setQuestion(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
//                question.setOption1(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
//                question.setOption2(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
//                question.setOption3(c.getString(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
//                question.setAnswerNr(c.getInt(c.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER_NR)));
//                questionList.add(question);
//            } while (c.moveToNext());
//        }
//
//        c.close();
//        return questionList;
//    }
//}

