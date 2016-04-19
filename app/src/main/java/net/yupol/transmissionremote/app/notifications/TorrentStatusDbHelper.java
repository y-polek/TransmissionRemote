package net.yupol.transmissionremote.app.notifications;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class TorrentStatusDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TorrentStatus.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME_FINISHED_STATUS = "finished_status";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME_FINISHED_STATUS + " (" +
            Columns._ID + " INTEGER PRIMARY KEY," +
            Columns.SERVER_ID + " TEXT," +
            Columns.TORRENT_ID + " INTEGER," +
            Columns.TORRENT_IS_FINISHED + " INTEGER" +
            ")";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME_FINISHED_STATUS;

    public TorrentStatusDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void clearServerData(SQLiteDatabase db, String serverId) {
        db.delete(TABLE_NAME_FINISHED_STATUS, Columns.SERVER_ID + " = ?", new String[] { serverId });
    }

    public static abstract class Columns implements BaseColumns {
        public static final String SERVER_ID = "serverId";
        public static final String TORRENT_ID = "torrentId";
        public static final String TORRENT_IS_FINISHED = "torrentIsFinished";
    }
}
