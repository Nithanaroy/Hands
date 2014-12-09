package com.impact.asu.pocket;





import android.support.v7.app.ActionBarActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DatabaseActivity extends ActionBarActivity {

	
	private String DATABASE_LOCATION, SDCARD_LOCATION, TABLE_NAME;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_database);
		
		
		//get database location
	     SDCARD_LOCATION = Environment.getExternalStorageDirectory().getPath();
	     DATABASE_LOCATION = SDCARD_LOCATION + "/Assignment2DB";
	     TABLE_NAME = "HandsAcclTable";
	     try{
				SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READONLY);
				
				String[] columns = {"timeStamp","xValues", "yValues", "zValues"};
				
				Cursor myCur = db.query(TABLE_NAME, columns, null, null, null, null, "timeStamp");
				
				//myCur.moveToLast();
				int xValueIndex = myCur.getColumnIndex("xValues");
				int yValueIndex = myCur.getColumnIndex("yValues");
				int zValueIndex = myCur.getColumnIndex("zValues");
				int timeStampIndex = myCur.getColumnIndex("timeStamp");
		    	
		    	int numberOfPlots = 0;
				while(myCur.moveToNext()){
					Log.e("ValuesRead: x, y, z", " are :" + myCur.getDouble(xValueIndex) +" " + myCur.getDouble(yValueIndex)+ " " +  myCur.getDouble(zValueIndex));
					//*TODO remove me and declaration
					//xValues[numberOfPlots] = myCur.getDouble(xValueIndex);
					//yValues[numberOfPlots] = myCur.getDouble(yValueIndex);
					//zValues[numberOfPlots] = myCur.getDouble(zValueIndex);
					/*END remove me
					gvdx[numberOfPlots] = new GraphViewData(numberOfPlots, xValues[numberOfPlots]);
					gvdy[numberOfPlots] = new GraphViewData(numberOfPlots, yValues[numberOfPlots]);
					gvdz[numberOfPlots] = new GraphViewData(numberOfPlots, zValues[numberOfPlots]);
					//columns[3] = Integer.toString((myCur.getInt(timeStampIndex)));
					Log.e("ValuesRead","X Value Now" + xValues[numberOfPlots]);
					numberOfPlots++;
					*/
					//if(numberOfPlots >= AcclListenerActivity.NUMBER_OF_PLOTS) break;
					
					Log.e("ValuesRead","X Value Now" + myCur.getDouble(xValueIndex));
					db.close();
				}
			}catch(SQLiteException se){
					Log.d("SQLite Exception", se.getMessage());
			}finally{
				
			}
	
	     
	     
	     
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.database, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
