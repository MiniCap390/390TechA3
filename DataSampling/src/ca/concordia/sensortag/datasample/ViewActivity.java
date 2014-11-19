package ca.concordia.sensortag.datasample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import ca.concordia.sensortag.datasample.RecordService.RecordServiceListener;
import ca.concordia.sensortag.datasample.RecordService.Status;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.EditText;
import android.widget.Toast;

public class ViewActivity extends Activity implements RecordServiceListener {
	static public final String TAG = "ViewAct"; // Tag for Android's logcat
	
	/* Service */
	private RecordService.Binder mRecSvc = null;
	
	// This is the Adapter being used to display the list's data
	private ArrayAdapter<String> mListAdapter;
	
	//GUI list of steps , Init to non-null
	private List<String> StepEvents = new ArrayList<String>();
	
	/**
	 * Called by Android when the Activity is first created. This sets up the GUI for the Activity,
	 * sets up the variables to control the GUI elements in the program, and prepares the Bluetooth
	 * communication to the SensorTag.
	 * 
	 * @see https://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		setupGui();
	}
	
	/**
	 * Binds to the service. Unlike in RecordActivity, in this case we will assume it is already
	 * started; even if it isn't, binding to it will start it, and we don't care about the service
	 * running even if the activity closes (since we just want to get data from it, not start a
	 * recording).
	 */
	private void connectService() {
		// Bind to the RecordService to get data (don't need to start it like in
		// RecordService - assume it's already started)
		Intent recordIntent = new Intent(this, RecordService.class);
		bindService(recordIntent, mSvcConnection, Context.BIND_ABOVE_CLIENT);
	}
	
	/**
	 * Service connector for RecordService. Handles messages about the service being connected or
	 * disconnected.
	 */
	private ServiceConnection mSvcConnection = new ServiceConnection() {
		/**
		 * Called when the service is connected (is bound). Read and analyze data from the service.
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "RecordService connected.");
			mRecSvc = (RecordService.Binder) service;
			mRecSvc.addStepListener(ViewActivity.this);
		}

		/**
		 * Called when the service is disconnected (is unbound).
		 */
		@Override
		public void onServiceDisconnected(android.content.ComponentName name) {
			Log.i(TAG, "RecordService disconnected.");
			mRecSvc = null;
		}
	};
	
	/**
	 * Called in onCreate(). Sets up the GUI before the data is shown, and gets references to all
	 * the GUI elements.
	 */
	private void setupGui() {
		// Show the "back"/"up" button on the Action Bar (top left corner)
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mListAdapter =
                new ArrayAdapter<String>(
                        this, // The current context (this activity)
                        R.layout.list_item_step, // The name of the layout ID.
                        R.id.list_item_step_textview, // The ID of the textview to populate.
                        StepEvents);
		
		ListView listView = (ListView) findViewById(R.id.listview_steps);
        listView.setAdapter(mListAdapter);
	}
	
	/**
	 * Called by Android when the Activity comes back into the foreground (i.e. on-screen). When
	 * called, reconnect to the service in order to update the analysis.
	 * 
	 * @see https://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
	 */
	@Override
	protected void onResume() {
		super.onResume();
		connectService();
	}

	/**
	 * Called by Android when the Activity goes out of focus (for example, if another Application
	 * pops up on top of it and partially obscures it). When called, this method unbinds from the
	 * service.
	 * 
	 * @see https://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
	 */
	@Override
	protected void onPause() {
		super.onPause();
		unbindService(mSvcConnection);
	}

	/**
	 * Called when the Activity is destroyed by Android. Cleans up the Bluetooth connection to the
	 * SensorTag.
	 * 
	 * @see https://developer.android.com/reference/android/app/Activity.html#ActivityLifecycle
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	/**
	 * Called when RecordService's status changes.
	 * @param s The new status value.
	 */
	@Override
	public void onStatusChanged(Status s) {
		//Ignore
	}
	
	/**
	 * Called when RecordService's receives a Step event.
	 * @param s The new status value.
	 */
	@Override
	public void onStepEvent(final String time,final String point3d) {
		runOnUiThread(new Runnable() {
			@Override
			public void run(){
				StepEvents.add(time + " , " + point3d);
				mListAdapter.notifyDataSetChanged ();
			}
		});
	}
	
	/**
	 * Called when a menu item is pressed. In this case we don't have an explicit menu, but we do
	 * have the "back" button in the Action Bar (top bar). We want it to act like the regular Back
	 * button, that is to say, pressing either Back buttons closes the current Activity and returns
	 * to the previous activity in the stack.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId())
	    {
	        case android.R.id.home:
	            this.finish();
	            return true;
            default:
            	return super.onOptionsItemSelected(item);
	    }
	}

	
}