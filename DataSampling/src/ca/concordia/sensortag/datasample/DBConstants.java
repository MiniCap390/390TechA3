package ca.concordia.sensortag.datasample;

public class DBConstants {

	// ///////////////////////////////////////////////////////////////////
	// DB Stuff
	// ///////////////////////////////////////////////////////////////////
	public static final String DATABASE_NAME = "TechnicalAssignment3";
	public static final int DATABASE_VERSION = 1;
	
	// 
	// ///////////////////////////////////////////////////////////////////
	// Tables name
	// ///////////////////////////////////////////////////////////////////

	public static final String TABLE_STEP_INFO = "Step_Info";
	
	//
	// ///////////////////////////////////////////////////////////////////
	// For Step_Info table
	// ///////////////////////////////////////////////////////////////////
	public static final String STEP_INFO_ELAPSED_TIME = "elapsed_time";
	public static final String STEP_INFO_X = "X_accel";
	public static final String STEP_INFO_Y = "Y_accel";
	public static final String STEP_INFO_Z = "Z_accel";
	
	public static final String[] STEP_INFO_ALL_KEYS = new String[] {STEP_INFO_ELAPSED_TIME, STEP_INFO_X, STEP_INFO_Y, STEP_INFO_Z};
	
	public static final int COL_STEP_INFO_ELAPSED_TIME = 0;
	public static final int COL_STEP_INFO_X = 1;
	public static final int COL_STEP_INFO_Y = 2;
	public static final int COL_STEP_INFO_Z = 3;
	
	//
	// ///////////////////////////////////////////////////////////////////
	// Create table SQLite statement
	// ///////////////////////////////////////////////////////////////////
	
	public static final String CREATE_STEP_INFO_SQL =
			"create table " + TABLE_STEP_INFO
			+ " (" +  STEP_INFO_ELAPSED_TIME + " double not null, "
			+ STEP_INFO_X + " double not null, "
			+ STEP_INFO_Y + " double not null, "
			+ STEP_INFO_Z + " double not null"
			+ ");";

}
			
			
			
			
			
			
			