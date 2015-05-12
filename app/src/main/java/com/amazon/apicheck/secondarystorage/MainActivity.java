package com.amazon.apicheck.secondarystorage;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {

    private static final String API_CHECK_HELPER_FULLY_QUALIFIED_CLASS_NAME = "com.amazon.apicheck.secondarystorage.APICheckHelper";
    APICheckHelper mApiChecker;
    private static Context context;

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
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        mApiChecker = new APICheckHelper();
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.txt_results)).setTextColor(Color.parseColor("#0000FF"));
    }

    public static Context getAppContext() {
        return MainActivity.context;
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

        TextView result_txt = (TextView) findViewById(R.id.txt_results);

        Class c = Class.forName(API_CHECK_HELPER_FULLY_QUALIFIED_CLASS_NAME);
        Class[] args = new Class[1];
        Method method;


        // Reseting the results text box
        clearResults(null);
        args[0] = TextView.class;

        result_txt.append("\n Invoking APIs... \n ");
        Iterator<String> it = apisToTest.iterator();
        String methodname;
        String apiName;

        // Invoking all the methods that check APIs
        while (it.hasNext()) {
            apiName = it.next();
            methodname = "check_" + apiName;
            result_txt.append("\n\n");
            result_txt.append(Html.fromHtml("<u>" + apiName + "</u>"));
            try {
                method = c.getDeclaredMethod(methodname, args);
                method.invoke(mApiChecker, result_txt);
            } catch (Exception e) {
                result_txt.append("\n Something went wrong while invoking the method "
                        + methodname + ". Error:" + e.toString() + ". Check logs for stack trace");
                e.printStackTrace();
            }
        }
    }

    // This method will be invoked when "Copy File" button is clicked in the app.
    public void copyFileFromAssetsToAppDataPath(View view) {
        clearResults(null);
        mApiChecker.check_write_app_data_path((TextView) findViewById(R.id.txt_results),
                (ImageView) findViewById(R.id.img_result));
    }

        // This method will be invoked when clear button is clicked in the app.
    public void clearResults(View view) {
        TextView tv = (TextView) findViewById(R.id.txt_results);
        tv.setText("");

        ImageView imgVw = (ImageView)findViewById(R.id.img_result);
        imgVw.setImageBitmap(null);
    }

    public void copyFileFromAssetsToInaccessiblePath(View view) {
        clearResults(null);
        mApiChecker.check_write_to_inaccessible_path((TextView) findViewById(R.id.txt_results),
                (ImageView) findViewById(R.id.img_result));
    }
}
