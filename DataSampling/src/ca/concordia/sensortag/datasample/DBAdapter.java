package ca.concordia.sensortag.datasample;

//------------------------------------ DBADapter.java ---------------------------------------------
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

public class DBAdapter {

	public static final String TAG = "DBAdapter";
	private final Context context;
	private final DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	//
	// ///////////////////////////////////////////////////////////////////
	// Public methods:
	// ///////////////////////////////////////////////////////////////////
	private final List<StepInfo> step_container = new ArrayList<StepInfo>();
	private final int BUFFER_SIZE = 1;

	private void insertBufferRowsStepInfo() {
		if (step_container != null) {
			for (StepInfo current_step : step_container) {
				ContentValues initialValues = new ContentValues();
				initialValues.put(DBConstants.STEP_INFO_ELAPSED_TIME, current_step.getElapsed_time());
				initialValues.put(DBConstants.STEP_INFO_X, current_step.getX());
				initialValues.put(DBConstants.STEP_INFO_Y, current_step.getY());
				initialValues.put(DBConstants.STEP_INFO_Z, current_step.getZ());

				// Insert it into the database.
				db.insert(DBConstants.TABLE_STEP_INFO, null, initialValues);

			}
			step_container.clear();
		}
	}
	
	private List<StepInfo> getAllCurrentRowStepInfo() {
		insertBufferRowsStepInfo();
		Cursor c = 	db.query(true, DBConstants.TABLE_STEP_INFO, DBConstants.STEP_INFO_ALL_KEYS, 
							null, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		
		List<StepInfo> current_step_container = new ArrayList<StepInfo>();
		
		if (c.moveToFirst()) {
			do {
				// Transfer Data
				StepInfo current_step =  new StepInfo();
				
				current_step.setElapsed_time(c.getDouble(DBConstants.COL_STEP_INFO_ELAPSED_TIME));
				current_step.setXYZ(c.getDouble(DBConstants.COL_STEP_INFO_X),
						c.getDouble(DBConstants.COL_STEP_INFO_Y), c.getDouble(DBConstants.COL_STEP_INFO_Z));

				current_step_container.add(current_step);
			} while(c.moveToNext());
		}
		c.close();
		return  current_step_container;
	}
	
	//
	// ///////////////////////////////////////////////////////////////////
	// Public methods:
	// ///////////////////////////////////////////////////////////////////

	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}

	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}
	
	public void pause_workout() {
		insertBufferRowsStepInfo();
	}

	public void deleteAllTableValues() {
		db.execSQL("DELETE FROM " + DBConstants.TABLE_STEP_INFO);
	}
	
	public void bufferStepInfo(StepInfo newStep) {
		step_container.add(newStep);
		if (step_container.size() > BUFFER_SIZE) {
			insertBufferRowsStepInfo();
		}
	}
	
	public String getLastStepInfoString() {
		List<StepInfo> current_step_container = getAllCurrentRowStepInfo();
		StepInfo current_step = current_step_container.get(current_step_container.size() - 1);
		String lastStepString = "Time (ms): " + current_step.getElapsed_time()
				+ " X:" + current_step.getX()
				+ " Y:" + current_step.getY()
				+ " Z:" + current_step.getZ()
				+ "\n"
				;
		
		return lastStepString;
	}
	
	public List<String> getCurrentAllStepInfoString() {
		insertBufferRowsStepInfo();
		List<StepInfo> current_step_container = getAllCurrentRowStepInfo();
		List<String> displayList = new ArrayList<String>(); 
		String display;
		
		for(StepInfo current_step: current_step_container) {
			
			display = "";
			display = display + "Time (ms): " + current_step.getElapsed_time()
					+ " X:" + current_step.getX()
					+ " Y:" + current_step.getY()
					+ " Z:" + current_step.getZ()
					+ "\n"
					;
			displayList.add(display);
		}
		
		return displayList;
	}
	
	public List<Long> getData() {
		
		List<StepInfo> allSteps = getAllCurrentRowStepInfo();
		List<Long> timestamps = new ArrayList<Long>();
		for(StepInfo step: allSteps) {
			long timeStamp = (long) step.getElapsed_time();
			timestamps.add(timeStamp);
		}
		
		return timestamps;
	}
	
	


	// ///////////////////////////////////////////////////////////////////
	// Private Helper Classes:
	// ///////////////////////////////////////////////////////////////////

	/**
	 * Private class which handles database creation and upgrading. Used to
	 * handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DBConstants.DATABASE_NAME, null,
					DBConstants.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DBConstants.CREATE_STEP_INFO_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data!");

			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_STEP_INFO);

			// Recreate new database:
			onCreate(_db);
		}
	}
}
