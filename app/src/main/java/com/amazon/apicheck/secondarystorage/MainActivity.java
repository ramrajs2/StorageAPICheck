package com.amazon.apicheck.secondarystorage;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {

    private static final String API_CHECK_HELPER_FULLY_QUALIFIED_CLASS_NAME
            = "com.amazon.apicheck.secondarystorage.APICheckHelper";
    APICheckHelper mApiChecker;

    final int mNumOfTxtVws = 50;
    final static int ID_START_ADDR = 2000;
    public static int mTxtVwCount;
    public TextView[] mTxtVws;
    public static LinearLayout mResultLayout;

    private static Context mContext;

    // APIs to be checked will be added in this arraylist
    // Before adding a new API here, create a check method in APICheckHelper.java
    public static ArrayList<String> apisToTest = new ArrayList<String>();

    static {
        apisToTest.add("getExternalStorageDirectory");
        apisToTest.add("getExternalStoragePublicDirectory");
        apisToTest.add("getExternalStorageState");
        apisToTest.add("isExternalStorageEmulated");
        apisToTest.add("isExternalStorageRemovable");
        apisToTest.add("getExternalCacheDir");
        apisToTest.add("getExternalFilesDir");
        apisToTest.add("getenv_sec_storage");
        apisToTest.add("getExternalStorageState_secondary");
        apisToTest.add("getExternalFilesDirs");
        apisToTest.add("getExternalCacheDirs");
        apisToTest.add("getExternalMediaDirs");
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initializing values
        mContext = getApplicationContext();
        mTxtVwCount = 0;
        mTxtVws = new TextView[mNumOfTxtVws];
        mResultLayout = (LinearLayout) findViewById(R.id.result_linear_layout);
        mApiChecker = new APICheckHelper();
    }

    public static Context getAppContext() {
        return MainActivity.mContext;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This method will be invoked when "Check All APIs" button is clicked
    public void checkAllAPIs(View view) throws ClassNotFoundException {

        Class c = Class.forName(API_CHECK_HELPER_FULLY_QUALIFIED_CLASS_NAME);
        Method method;

        // Reseting the results text box
        clearResults(null);

        appendTextView("Invoking APIs... ");
        Iterator<String> it = apisToTest.iterator();
        String methodname;
        String apiName;

        // Invoking all the methods that check APIs
        while (it.hasNext()) {
            apiName = it.next();
            methodname = "check_" + apiName;
            appendTextView(apiName, Color.RED);
            try {
                method = c.getDeclaredMethod(methodname, null);
                method.invoke(mApiChecker);
            } catch (Exception e) {
                appendTextView("Something went wrong while invoking the method "
                        + methodname + ". Error:" + e.toString() + ". Check logs for stack trace", Color.MAGENTA);
                e.printStackTrace();
            }
        }
    }

    // This method will be invoked when "Copy File" button is clicked in the app.
    public void copyFileFromAssetsToAppDataPath(View view) {
        clearResults(null);
        mApiChecker.check_write_app_data_path();
    }

    // This method will be invoked when clear button is clicked in the app.
    public void clearResults(View view) {
        View tmp;
        for (int i = 0; i < mTxtVwCount; i++) {
            tmp = findViewById(ID_START_ADDR + i);
            ((LinearLayout) tmp.getParent()).removeView(tmp);
        }
        mTxtVwCount = 0;
    }

    public void copyFileFromAssetsToInaccessiblePath(View view) {
        clearResults(null);
        mApiChecker.check_write_to_inaccessible_path();
    }

    public void copyFileFromAssetsToPicturesPath(View view) {
        clearResults(null);
        mApiChecker.check_write_to_pictures_path();
    }

    public void copyFiletoDirs(View view) {
        clearResults(null);
        appendTextView("");
        mApiChecker.check_write_to_dirs_path();
    }

    public void checkOpenDocumentAction(View view) {
        clearResults(null);
        appendTextView("This action is yet to be implemented. Please wait till its done");

        /*Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setType("text*//*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivity(intent);*/
    }


    public static void appendTextView(String value) {
        appendTextView(value, Color.BLUE);
    }

    public static void appendTextView(String value, int color) {
        appendTextView(value, color, false);
    }

    public static void appendTextView(String value, int color, boolean isTopic) {
        TextView txtVw;
        txtVw = new TextView(mContext);
        txtVw.setId(ID_START_ADDR + mTxtVwCount++);
        txtVw.setTextSize(value.length() + 20);
        txtVw.setTextColor(color);
        txtVw.setTextSize(18);
        txtVw.setPadding(13, 0, 0, 0);
        txtVw.setFreezesText(true);
        txtVw.setText(value);

        if (color != Color.RED && !isTopic)
            txtVw.append("\n");
        if(isTopic)
            txtVw.setBackgroundColor(Color.GRAY);

        mResultLayout.addView(txtVw);
    }

}
