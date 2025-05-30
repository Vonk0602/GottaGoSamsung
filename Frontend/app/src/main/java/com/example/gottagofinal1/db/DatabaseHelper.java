package com.example.gottagofinal1.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.gottagofinal1.model.Review;
import com.example.gottagofinal1.model.data.ReviewData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GottaGo.db";
    private static final int DATABASE_VERSION = 77;

    private static final String TABLE_REVIEWS = "reviews";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FROM_USER_ID = "from_user_id";
    private static final String COLUMN_TO_USER_ID = "to_user_id";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE " + TABLE_REVIEWS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FROM_USER_ID + " TEXT, " +
            COLUMN_TO_USER_ID + " TEXT, " +
            COLUMN_TEXT + " TEXT, " +
            COLUMN_RATING + " REAL, " +
            COLUMN_TIMESTAMP + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REVIEWS, null);
            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            Log.d("DatabaseHelper", "Number of reviews in database: " + count);
            if (count == 0) {
                Log.d("DatabaseHelper", "Table reviews is empty, populating with data from ReviewData");
                populateReviews(db);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking reviews table: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating database and table reviews");
        db.execSQL(CREATE_TABLE_REVIEWS);
        populateReviews(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        onCreate(db);
    }

    private void populateReviews(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Populating reviews table");
        List<Review> reviews = ReviewData.getReviewsForUser(null);
        Log.d("DatabaseHelper", "Found " + reviews.size() + " reviews in ReviewData");
        for (Review review : reviews) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_FROM_USER_ID, review.getFromUserId());
            values.put(COLUMN_TO_USER_ID, review.getUserId());
            values.put(COLUMN_TEXT, review.getText());
            values.put(COLUMN_RATING, review.getRating());
            values.put(COLUMN_TIMESTAMP, review.getTimestamp());
            long id = db.insert(TABLE_REVIEWS, null, values);
            Log.d("DatabaseHelper", "Inserted review with ID: " + id);
        }
    }

    public boolean hasReviewFromUser(String fromUserId, String toUserId) {
        Log.d("DatabaseHelper", "Checking if review exists from " + fromUserId + " to " + toUserId);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(
                    TABLE_REVIEWS,
                    new String[]{COLUMN_ID},
                    COLUMN_FROM_USER_ID + "=? AND " + COLUMN_TO_USER_ID + "=?",
                    new String[]{fromUserId, toUserId},
                    null, null, null
            );
            boolean exists = cursor != null && cursor.getCount() > 0;
            Log.d("DatabaseHelper", "Review exists: " + exists);
            return exists;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in hasReviewFromUser: " + e.getMessage(), e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public long addReview(String fromUserId, String toUserId, String text, double rating) {
        Log.d("DatabaseHelper", "Adding review fromUserId: " + fromUserId + ", toUserId: " + toUserId + ", text: " + text + ", rating: " + rating);
        if (fromUserId == null || toUserId == null || fromUserId.equals(toUserId)) {
            Log.w("DatabaseHelper", "Invalid input: fromUserId or toUserId is null or equal");
            return -1;
        }
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_FROM_USER_ID, fromUserId);
            values.put(COLUMN_TO_USER_ID, toUserId);
            values.put(COLUMN_TEXT, text);
            values.put(COLUMN_RATING, rating);
            values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
            long id = db.insert(TABLE_REVIEWS, null, values);
            Log.d("DatabaseHelper", "Review insertion result: " + id);
            return id;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding review: " + e.getMessage(), e);
            return -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<Review> getReviewsForUser(String toUserId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(
                    TABLE_REVIEWS,
                    new String[]{COLUMN_ID, COLUMN_FROM_USER_ID, COLUMN_TO_USER_ID, COLUMN_TEXT, COLUMN_RATING, COLUMN_TIMESTAMP},
                    COLUMN_TO_USER_ID + "=?",
                    new String[]{toUserId},
                    null, null, COLUMN_TIMESTAMP + " DESC"
            );

            Log.d("DatabaseHelper", "Fetching reviews for toUserId: " + toUserId);
            if (cursor != null && cursor.moveToFirst()) {
                Log.d("DatabaseHelper", "Found " + cursor.getCount() + " reviews for toUserId: " + toUserId);
                do {
                    int idIndex = cursor.getColumnIndex(COLUMN_ID);
                    int fromUserIdIndex = cursor.getColumnIndex(COLUMN_FROM_USER_ID);
                    int toUserIdIndex = cursor.getColumnIndex(COLUMN_TO_USER_ID);
                    int textIndex = cursor.getColumnIndex(COLUMN_TEXT);
                    int ratingIndex = cursor.getColumnIndex(COLUMN_RATING);
                    int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);

                    if (idIndex == -1 || fromUserIdIndex == -1 || toUserIdIndex == -1 ||
                            textIndex == -1 || ratingIndex == -1 || timestampIndex == -1) {
                        Log.e("DatabaseHelper", "Invalid column index for reviews query");
                        continue;
                    }

                    Review review = new Review(
                            cursor.getLong(idIndex),
                            cursor.getString(fromUserIdIndex),
                            cursor.getString(toUserIdIndex),
                            cursor.getString(textIndex),
                            cursor.getDouble(ratingIndex),
                            cursor.getLong(timestampIndex)
                    );
                    reviews.add(review);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching reviews: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        Log.d("DatabaseHelper", "Returning " + reviews.size() + " reviews for toUserId: " + toUserId);
        return reviews;
    }

    public double getAverageRatingForUser(String toUserId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT AVG(" + COLUMN_RATING + ") as avg_rating FROM " + TABLE_REVIEWS +
                            " WHERE " + COLUMN_TO_USER_ID + "=?",
                    new String[]{toUserId}
            );

            double averageRating = 0.0;
            if (cursor != null && cursor.moveToFirst()) {
                int avgRatingIndex = cursor.getColumnIndex("avg_rating");
                if (avgRatingIndex != -1) {
                    averageRating = cursor.getDouble(avgRatingIndex);
                }
            }
            Log.d("DatabaseHelper", "Average rating for toUserId " + toUserId + ": " + averageRating);
            return averageRating;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error calculating average rating: " + e.getMessage(), e);
            return 0.0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}