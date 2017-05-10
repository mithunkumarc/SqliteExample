package svs.example.com.sqliteexample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button submitButton,readButton,updateButton;
    EditText titleEditText,subtitleEditText;
    String title,subtitle;
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        submitButton = (Button)findViewById(R.id.submit);
        readButton = (Button)findViewById(R.id.read);
        updateButton = (Button)findViewById(R.id.update);
        titleEditText = (EditText)findViewById(R.id.title);
        subtitleEditText = (EditText)findViewById(R.id.subTitle);

        mDbHelper = new FeedReaderDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleEditText.getText().toString();
                subtitle = subtitleEditText.getText().toString();
                if (title.length() <=0 || subtitle.length() <= 0){
                    Toast.makeText(getApplicationContext(),"enter data",Toast.LENGTH_SHORT).show();
                }else {
                    insertData();
                }
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleEditText.getText().toString();
                subtitle = subtitleEditText.getText().toString();
                if (title.length() <=0 || subtitle.length() <= 0){
                    Toast.makeText(getApplicationContext(),"enter data",Toast.LENGTH_SHORT).show();
                }else {
                    updateDB();
                }
            }
        });

    }

    public void insertData(){
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
        Log.d("newRowid=",newRowId+"");
        Toast.makeText(getApplicationContext(),"newRowid="+newRowId,Toast.LENGTH_SHORT).show();
    }

    public void readData(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };

// Filter results WHERE "title" = 'My Title'
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "jugari cross" };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        //
        List itemIds = new ArrayList<>();
        List itemTitle = new ArrayList();
        List itemSubTitle = new ArrayList();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
            itemIds.add(itemId);
            itemTitle.add(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE)));
            itemSubTitle.add(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE)));
            Log.d("title=",cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE)));
            Log.d("subtitle=",cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE)));
            Toast.makeText(getApplicationContext(),cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE))+"-"+cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE),Toast.LENGTH_SHORT).show();
        }
        cursor.close();

    }

    public void updateDB(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);

// Which row to update, based on the title
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { "jugari cross" };

        int count = db.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Toast.makeText(getApplicationContext(),count+"",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }
}
