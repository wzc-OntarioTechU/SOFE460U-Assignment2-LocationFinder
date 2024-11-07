package wzc.sofe4640u.locationfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_FILE = "locations.db";

    public DatabaseHelper(Context context) {
        super(context, DB_FILE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE `locations` (id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, lat DOUBLE NOT NULL, lon DOUBLE NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // first version, no updates yet.
    }

    public List<String> getAllNames() {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT `name` FROM `locations`;", null);
        ArrayList<String> resultsList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            resultsList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return resultsList;
    }

    public List<String> filter(List<String> names) {
        StringBuilder query = new StringBuilder("SELECT `name` FROM `locations` WHERE");
        List<String> newnames = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (i != 0) {
                query.append(" AND");
            }
            query.append(" `name` LIKE ?");
            String name = "%" + names.get(i) + "%";
            newnames.add(i, name);
        }
        query.append(";");
        Cursor cursor = this.getReadableDatabase().rawQuery(query.toString(), newnames.toArray(new String[0]));
        ArrayList<String> results =  new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            results.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return results;
    }

    public double[] getCoords(String name) {
        String vals[] = new String[1];
        vals[0] = name;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT `lat`, `lon` FROM `locations` WHERE `name` = ? LIMIT 1;", vals);
        double results[] = {0.0, 0.0};
        cursor.moveToFirst();
        if (!cursor.isAfterLast() && cursor.getColumnCount() == 2) {
            results[0] = cursor.getDouble(0);
            results[1] = cursor.getDouble(1);
        }
        cursor.close();
        return results;
    }

    public boolean exists(String name) {
        double coords[] = getCoords(name);
        return coords[0] != 0.0 && coords[1] != 0.0;
    }

    public void addLoc(String name, double lat, double lon) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("lat", lat);
        values.put("lon", lon);
        this.getWritableDatabase().insert("locations", null, values);
    }
}
