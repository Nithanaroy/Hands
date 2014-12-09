package edu.impact.asu.hands.accl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.impact.asu.hands.R;
import com.impact.asu.hands.R.id;
import com.impact.asu.hands.R.layout;
import com.impact.asu.hands.R.menu;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;

import android.support.v7.app.ActionBarActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AcclListenerActivity extends ActionBarActivity implements SensorEventListener{

	private static final int NUMBER_OF_PLOTS = 10;
	Button startAcclBtn, runBtn, stopBtn, resetBtn, uploadBtn;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	boolean databasePopulatedFlag = false;
	private final String DATABASE_NAME = "Patient_Info_Database";
	private String DATABASE_LOCATION, SDCARD_LOCATION;
	int acclCounter = 0;
	double[] x; 
	double[] y;
	double[] z;
	private boolean isWriting= false;
	private boolean isInitializeTable = true;
	private int batchSize;
	private String tableName;
	AsyncTask<double[], Void, String> mDatabaseTask;
	//Graphing related
	private GraphViewSeries seriesX, seriesY, seriesZ;
	private GraphView graphView;
	private double graph2LastXValue = 10d;
	private boolean isGraphInitialized;
	GraphViewData[] gvdx, gvdy, gvdz = new GraphViewData[10];
	Thread waitThread;
	boolean isRunning = true;
	boolean databaseLocked = false;
	boolean databaseDroppedFlag = false;
	boolean uploadComplete; 
	SQLiteDatabase db;
	boolean tableCreated;
	private int serverResponseCode = 0;
	Thread t;
	private final String upLoadServerUri = "http://www.androidexample.com/%20media/UploadToServer.php";
	private final String clientCertPassword = "testing1";
	
	double[] updatedAcclValues;
	private class DatabaseWriterTask extends AsyncTask<double[], Void, String> {

		@Override
	protected String doInBackground(double[]... acclArray) {
			//if(!databaseLocked)
        	//writeToTable(acclArray[0], acclArray[1], acclArray[2]);
        	//databaseLocked = false; 
        	//isWriting = false;
			return "";
		}
	protected void onPostExecute(String aString){
			Toast.makeText(getApplicationContext(), "Done waiting", Toast.LENGTH_SHORT).show();
			//clear the arrays (not needed if the check is done correctly) 
		}
		
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startAcclBtn = (Button)findViewById(R.id.button0);
    	runBtn = (Button)findViewById(R.id.button1);	
    	stopBtn = (Button)findViewById(R.id.button2);	
    	resetBtn = (Button)findViewById(R.id.button3);
    	//uploadBtn = (Button)findViewById(R.id.Upload);
    	isGraphInitialized = false;
        //disable Run and Stop buttons on default.
		stopBtn.setEnabled(false);
		runBtn.setEnabled(false);
		resetBtn.setEnabled(false);
		uploadBtn.setEnabled(false);
		//Accelerometer related tasks
		 //mInitialized = false;
	     mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	     mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	     mSensorManager.registerListener(this, mAccelerometer, 1000000);        
	      x = y = z = new double[100];        
	     //graphing related init     
	     graphView = new LineGraphView(this, "GraphViewDemo");
	     gvdx = new GraphViewData[10];
	     gvdy = new GraphViewData[10];
	     gvdz = new GraphViewData[10];
	     batchSize = 10;
	     updatedAcclValues = new double[3];
	     tableCreated = false;
	     uploadComplete = false; 
	     
	     //get database location
	     SDCARD_LOCATION = Environment.getExternalStorageDirectory().getPath();
	     DATABASE_LOCATION = SDCARD_LOCATION + "/Assignment2DB";
	     //open the singleton database instance. 
	    // db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);

    }
  
    public void startAcclService(View view){
    	
    	//put all this in a method in a thread
    	mSensorManager.registerListener(this, mAccelerometer, 1000000);        
	      x = y = z = new double[100];        
	     //graphing related init     
	     graphView = new LineGraphView(this, "GraphViewDemo");
	     gvdx = new GraphViewData[10];
	     gvdy = new GraphViewData[10];
	     gvdz = new GraphViewData[10];
	     batchSize = 10;
	     updatedAcclValues = new double[3];
	     tableCreated = false;
	     uploadComplete = false; 
	     
	     //get database location
	     SDCARD_LOCATION = Environment.getExternalStorageDirectory().getPath();
	     DATABASE_LOCATION = SDCARD_LOCATION + "/Assignment2DB";
    	
    	EditText patientIdEdit = (EditText)findViewById(R.id.Patient_ID);
		EditText ageEdit = (EditText)findViewById(R.id.Patient_Age);
		EditText patientNameEdit = (EditText)findViewById(R.id.Patient_Name);
		RadioGroup radioSexGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		int selectedId = radioSexGroup.getCheckedRadioButtonId();
		RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
    	
    	//creating a database
    	//createDatabase();
    	
    	//compose the table name
    	tableName = "A" + patientNameEdit.getText().toString() 
    			+ "_" + patientIdEdit.getText().toString() 
    			+ "_" + ageEdit.getText().toString() 
    			+ "_" + radioSexButton.getText().toString();
    	SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.CREATE_IF_NECESSARY); 
    	//SQLiteDatabase.openDatabase("/sdcard/Assigment2DB", null, SQLiteDatabase.CREATE_IF_NECESSARY); 
    	createTable();
    	//readyToWrite = true;	
    	//enabling Run Button 
    	//disable StartBtn -- after implementing a reset functionality
		startAcclBtn.setEnabled(false);	
		
		Toast.makeText(this, "waiting for db update", Toast.LENGTH_LONG).show();
		
    }

    public void runGraph(View view) throws InterruptedException{
    	//disable Run Button
    	runBtn.setEnabled(false);
    	
    	//TODO run code for StartGraph
    	//enable Stop Button 
		stopBtn.setEnabled(true);
		
		//graph.initialize
		isRunning = true;
		
		t = new Thread(){
				@Override
				public void run(){
					try{
						while(!isInterrupted()){
							Thread.sleep(1000);
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									if(!isGraphInitialized){
										initializeGraph();
									}else {
										graph2LastXValue += 1d;
										/*
										if (!databaseLocked) seriesX.appendData(new GraphViewData(graph2LastXValue, getUpdatedAccl()[0]), true,10);
										else seriesX.appendData(new GraphViewData(graph2LastXValue, getRandom()), true,10);
										*/
										/*move all three graphs*/
										seriesX.appendData(new GraphViewData(graph2LastXValue,getUpdatedAccl()[0]), true,10);
										seriesY.appendData(new GraphViewData(graph2LastXValue, getUpdatedAccl()[1]), true,10);
										seriesZ.appendData(new GraphViewData(graph2LastXValue, getUpdatedAccl()[2]), true,10);
										/* get Random Values
										 
										
										seriesY.appendData(new GraphViewData(graph2LastXValue, getRandom()), true,10);
										seriesZ.appendData(new GraphViewData(graph2LastXValue, getRandom()), true,10);
										seriesX.appendData(new GraphViewData(graph2LastXValue, getRandom()), true,10);
										*/
										 
									}
								}
							});
						}
					}catch (InterruptedException e){
						}
					}
		};
		t.start();
    }

    
    public void stopGraph(View view){
    	Log.e("stopGraph", "in DatabaseWriter");
    	isRunning = false;
    	isGraphInitialized = false;
    	//enable Run Button
    	//runBtn.setEnabled(true);
    	//disable StopButton
    	stopBtn.setEnabled(false);
    	//enable Reset Button
    	resetBtn.setEnabled(true);
    	graphView.removeAllSeries(); 
    	graphView.removeAllViews();
    	mSensorManager.unregisterListener(this);
    	acclCounter = 0;
    	t.interrupt();
    	SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	db.close();
    	uploadBtn.setEnabled(true);
    	
    }
    
    public void dropTable(){
    	SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
    	db.beginTransaction();
		try{
			db.execSQL("DROP TABLE IF EXISTS "+ tableName);
			db.setTransactionSuccessful();
			databaseDroppedFlag = true;
		}catch(SQLiteException e){
			Log.e("Database", e.getMessage());
		}finally{
			db.endTransaction();
			Toast.makeText(this, "drop table complete", Toast.LENGTH_SHORT).show();
		}
    	
    }
    
    public void resetAll (View view){
    	/* This
    	 * 1. clears the database (removes it)
    	 * 2. re-enables the fields viz. StartButton.
    	 */
		
    	//drop database
		try{
			this.deleteDatabase(DATABASE_NAME);
		}catch(SQLiteException e){
			Log.e("Database", e.getMessage());
		}
		
		//readyToWrite = false;
    	startAcclBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		runBtn.setEnabled(false);
		resetBtn.setEnabled(false);
		mSensorManager.unregisterListener(this);
		
		
		//Re Initialize
		isGraphInitialized = false;
	    //disable Run and Stop buttons on default.
		stopBtn.setEnabled(false);
		runBtn.setEnabled(false);
		resetBtn.setEnabled(false);
		//Accelerometer related tasks
		 //mInitialized = false;
	     x = y = z = new double[100];        
	     //graphing related init     
	     graphView = new LineGraphView(this, "GraphViewDemo");
	     gvdx = new GraphViewData[10];
	     gvdy = new GraphViewData[10];
	     gvdz = new GraphViewData[10];
	     batchSize = 10;
	     updatedAcclValues = new double[3];
	     tableCreated = false;
	     uploadComplete = false; 
	     acclCounter = 0;
	
		
    }
    
    //responds to button Click
    public void uploadDB(View view){
    	
    	try{
    		
    		new Thread(new Runnable() {
                public void run() {
                	//Toast.makeText(getApplicationContext(), "Uploading to Databse Begin", Toast.LENGTH_SHORT).show();                  
                     uploadDatabase();
                                              
                }
              }).start();  
    		
    		
    		
    	}catch(Exception e){
    		Log.d("Connection Exception", e.getMessage());
    		Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    	}
    	
    	
    }
    
    private int uploadDatabase(){
    	
    	String fileName = DATABASE_LOCATION;
    	HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
    	
        
    	//Thread.sleep(5000);
    	File sourceFile = new File(DATABASE_LOCATION);
    	
    	if (!sourceFile.isFile()) {
             
            Log.e("uploadFile", "Source File not exist :"+DATABASE_LOCATION);
             
          //  Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            return 0;
            
       }
    	 else
         {
              try { 
                   
            	  
            	
                  FileInputStream fileInputStream = new FileInputStream(sourceFile);
                  URL url = new URL(upLoadServerUri);
                 
                  /*HTTPS stuff
                  KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                  FileInputStream fis = new FileInputStream(SDCARD_LOCATION + "/clientCertPassword");
                  keyStore.load(fis, clientCertPassword.toCharArray());
                
                  String algorithm = KeyManagerFactory.getDefaultAlgorithm();
                  KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
                  kmf.init((ManagerFactoryParameters) keyStore);

                  SSLContext context = SSLContext.getInstance("TLS");
                  context.init(kmf.getKeyManagers(), null, null);
  				 */	
                  
                  // Open a HTTP  connection to  the URL
                  conn = (HttpURLConnection) url.openConnection(); 
                 // conn.setSSLSocketFactory(context.getSocketFactory());
                  //InputStream in = conn.getInputStream();
                  conn.setDoInput(true); // Allow Inputs
                  conn.setDoOutput(true); // Allow Outputs
                  conn.setUseCaches(false); // Don't use a Cached Copy
                  conn.setRequestMethod("POST");
                  conn.setRequestProperty("Connection", "Keep-Alive");
                  conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                  conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                  conn.setRequestProperty("uploaded_file", fileName); 
                   
                  dos = new DataOutputStream(conn.getOutputStream());
         
                  dos.writeBytes(twoHyphens + boundary + lineEnd); 
                  dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                                            + fileName + "\"" + lineEnd);
                   
                  dos.writeBytes(lineEnd);
         
                  // create a buffer of  maximum size
                  bytesAvailable = fileInputStream.available(); 
         
                  bufferSize = Math.min(bytesAvailable, maxBufferSize);
                  buffer = new byte[bufferSize];
         
                  // read file and write it into form...
                  bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                     
                  while (bytesRead > 0) {
                       
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                     
                   }
         
                  // send multipart form data necesssary after file data...
                  dos.writeBytes(lineEnd);
                  dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
         
                  // Responses from the server (code and message)
                  serverResponseCode = conn.getResponseCode();
                  String serverResponseMessage = conn.getResponseMessage();
                    
                  Log.i("uploadFile", "HTTP Response is : "
                          + serverResponseMessage + ": " + serverResponseCode);
                   
                  if(serverResponseCode == 200){
                       
                      runOnUiThread(new Runnable() {
                           public void run() {
                              // String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                   //  +" F:/wamp/wamp/www/uploads";
                               //messageText.setText(msg);
                        	   //uploadComplete = true;
                        	   Toast.makeText(getApplicationContext(), "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        	   dropTable();
                           }
                       }); 
                  }    
                   
                  //close the streams //
                  fileInputStream.close();
                  dos.flush();
                  dos.close();
                  
                    
             } catch (MalformedURLException ex) {
                  
                 //dialog.dismiss();  
                 ex.printStackTrace();
                  
                 runOnUiThread(new Runnable() {
                     public void run() {
                         //messageText.setText("MalformedURLException Exception : check script url.");
                         Toast.makeText(getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();
                     }
                 });
                  
                 Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
             } catch (Exception e) {
                  
                 //dialog.dismiss();  
                 e.printStackTrace();
                  
                 runOnUiThread(new Runnable() {
                     public void run() {
                        // messageText.setText("Got Exception : see logcat ");
                         Toast.makeText(getApplicationContext(), "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                     }
                 });
                 Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);  
             }
            // dialog.dismiss();       
             return serverResponseCode; 
             
          }
          
		//return serverResponseCode;
    	
    }
    private void initializeGraph(){
    	isGraphInitialized = true;
		
    	
    	//read from the database and initialize the Graph.
		String[] columns = {"xValues", "yValues", "zvalues", "timeStamp"};
		
    	double[] xValues = new double[AcclListenerActivity.NUMBER_OF_PLOTS];
		double[] yValues = new double[AcclListenerActivity.NUMBER_OF_PLOTS];
		double[] zValues = new double[AcclListenerActivity.NUMBER_OF_PLOTS];
		
		try{
			SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READONLY);
			
			Cursor myCur = db.query(tableName, columns, null, null, null, null, "timeStamp");
			
			//myCur.moveToLast();
			int xValueIndex = myCur.getColumnIndex("xValues");
			int yValueIndex = myCur.getColumnIndex("yValues");
			int zValueIndex = myCur.getColumnIndex("zValues");
			int timeStampIndex = myCur.getColumnIndex("timeStamp");
	    	
	    	int numberOfPlots = 0;
			while(myCur.moveToNext()){
				Log.e("ValuesRead: x, y, z", " are :" + myCur.getDouble(xValueIndex) +" " + myCur.getDouble(yValueIndex)+ " " +  myCur.getDouble(zValueIndex));
				//*TODO remove me and declaration
				xValues[numberOfPlots] = myCur.getDouble(xValueIndex);
				yValues[numberOfPlots] = myCur.getDouble(yValueIndex);
				zValues[numberOfPlots] = myCur.getDouble(zValueIndex);
				//END remove me
				gvdx[numberOfPlots] = new GraphViewData(numberOfPlots, xValues[numberOfPlots]);
				gvdy[numberOfPlots] = new GraphViewData(numberOfPlots, yValues[numberOfPlots]);
				gvdz[numberOfPlots] = new GraphViewData(numberOfPlots, zValues[numberOfPlots]);
				//columns[3] = Integer.toString((myCur.getInt(timeStampIndex)));
				Log.e("ValuesRead","X Value Now" + xValues[numberOfPlots]);
				numberOfPlots++;
				if(numberOfPlots >= AcclListenerActivity.NUMBER_OF_PLOTS) break;	
				db.close();
			}
		}catch(SQLiteException se){
				Log.d("SQLite Exception", se.getMessage());
		}finally{
			
		}
		seriesX = new GraphViewSeries("xValues", null, gvdx);
	    seriesX.getStyle().color = Color.RED; 
	    seriesY = new GraphViewSeries("yValues", null, gvdy);
	    seriesY.getStyle().color = Color.CYAN;
	    seriesZ = new GraphViewSeries("zValues", null, gvdz);
	    seriesZ.getStyle().color = Color.YELLOW;
    	
	    graphView.setShowLegend(true);
	    graphView.setLegendAlign(LegendAlign.BOTTOM);
	    graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.GREEN);
	    graphView.getGraphViewStyle().setVerticalLabelsColor(Color.GREEN);
	    graphView.getGraphViewStyle().setNumHorizontalLabels(6);
	    graphView.getGraphViewStyle().setNumVerticalLabels(4);
	    graphView.getGraphViewStyle().setTextSize(15);
		graphView.setViewPort(10, 10);
		graphView.setScalable(true);
	    
	    graphView.addSeries(seriesX);
	    graphView.addSeries(seriesY);
	    graphView.addSeries(seriesZ);
	     
	    LinearLayout layout = (LinearLayout) findViewById(R.id.graphLayout);
	    	
	    layout.removeAllViews();
	    layout.addView(graphView);	
	    Log.e("Reading From Database", "Time Stamp Index");
		
    }
    
    private double[] getUpdatedAccl(){
    	
    	//databaseLocked = true;
    	
    	String[] columns = {"timeStamp","xValues", "yValues", "zValues"};
    	
    	//SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READONLY);
    	
    	SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READONLY);
    	try{
    	//My cur is geeting the whole database
    	//TODO get only the last value. 	
    	Cursor myCur = db.query(tableName, columns, null, null, null, null, "timeStamp"+" DESC", "1");
		if(myCur!= null)
		{
			myCur.moveToFirst();
		
		int xValueIndex = myCur.getColumnIndex("xValues");
		int yValueIndex = myCur.getColumnIndex("yValues");
		int zValueIndex = myCur.getColumnIndex("zValues");
		int timeStampIndex = myCur.getColumnIndex("timeStamp");
		
		//update the return values
		updatedAcclValues[0] = myCur.getDouble(xValueIndex);
		updatedAcclValues[1] = myCur.getDouble(yValueIndex);
		updatedAcclValues[2] = myCur.getDouble(zValueIndex);
		
		myCur.close();
		}
    	}catch(SQLiteException se){
    		
    	}finally{
    		db.close();
    		//databaseLocked = false;
    	}
    			
		 
			
    	//databaseLocked = false;
			return updatedAcclValues; 
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
    	Log.e("Notice", "InSensor Changed");
    	/*
    	if ( acclCounter < batchSize){
    		x[acclCounter] = event.values[0];
    		y[acclCounter] = event.values[1];
    		z[acclCounter] =  event.values[2];
    		acclCounter++;
    	}else{
    		acclCounter = 0;
    		//make the database call 
    		if(!isWriting && tableCreated){
    			isInitializeTable = false;
   	         //Async task that reads accl values and writes to a database
    			//mDatabaseTask = new DatabaseWriterTask().execute(x, y, z);
   	    	//clear x,y,z arrays
   	    	}
    	}
    	*/
    	
    	
    	
    	if( tableCreated){
    	writeToTable(event.values);
    	}
    	
    	
    	if(acclCounter == 12){
    		runBtn.setEnabled(true);
    	}
    	batchSize = 100;
    	Log.e("Values", "Counter" + acclCounter++);
    	
    	
		
	}

    public void createTable(){
    	//create the database
    	
    	SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
    	Log.e("createTable", "in CreateTable");
		String CREATE_TABLE_SQL = "create table if not exists " + tableName + " (" 
				+ "timeStamp integer PRIMARY KEY autoincrement, "
				+ "xValues double, "
				+ "yValues double, "
				+ "zValues double ); ";
		
		//String INSERT_DUMMY_VALUES_SQL = "insert into " + tableName + "(xValues, yValues, xValues) values (12, 13, 14);";
		
		db.beginTransaction();
		try{
			db.execSQL(CREATE_TABLE_SQL);
			//db.execSQL(INSERT_DUMMY_VALUES_SQL);
			db.setTransactionSuccessful();
		}catch(SQLiteException e){
			Log.e("Database", e.getMessage());
		}finally{
			db.endTransaction();
			tableCreated = true;
			db.close();
		}	
    }
    
   
    private void writeToTable(float[] allArrays){
    	//databaseLocked = true;
    	SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
    	Log.e("writeTable", "in WriteTable");
    		db.beginTransaction();
    		try{
    			db.execSQL("insert into " + tableName + "(xValues, yValues, zValues) values ("+ allArrays[0] + ", " + allArrays[1] + ", " + allArrays[2] + ");");
    			//Log.i("DatabaseWriter", "Writing" + xObject[counter] + ", " + yObjects[counter] + ", " + zObjects[counter] + ");" );
    			//db.execSQL(INSERT_DUMMY_VALUES_SQL);
    			db.setTransactionSuccessful();
    			databasePopulatedFlag = true;
    		}catch(SQLiteException e){
    			Log.e("Database", e.getMessage());
    		}finally{
    			db.endTransaction();
    			db.close();
    		}			
    	
    	
    	
    }
 
    private double getRandom() {
		double high = 3;
		double low = 0.5;
		return Math.random() * (high - low) + low;
	}
    protected void onResume() {
		 
		 super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, 1000000);
    }
	 
	 protected void onPause() {
		 
		 super.onPause();
		 
		 mSensorManager.unregisterListener(this);
		//drop database
			try{
				this.deleteDatabase(DATABASE_NAME);
			}catch(SQLiteException e){
				Log.e("Database", e.getMessage());
			}
			
			//db.close();
			//readyToWrite = false;
	    	startAcclBtn.setEnabled(true);
			stopBtn.setEnabled(false);
			runBtn.setEnabled(false);
			resetBtn.setEnabled(false);
		 
	}
	 
	 protected void onStop(){
		 super.onStop();
		// SQLiteDatabase db = SQLiteDatabase.openDatabase( DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
		// this.deleteDatabase(DATABASE_NAME);
	 }
    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	
    	
    	// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.accl_listener, menu);
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
