package com.amazon.apicheck.secondarystorage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.utils.URIUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void checkOpenDocumentAction(View view) {
        clearResults(null);

        appendTextView("ACTION_OPEN_DOCUMENT_TREE is being invoked\n", Color.BLUE, true);
        appendTextView("");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        try {
            startActivityForResult(intent, 42);
            Toast.makeText(mContext,"Choose 'Show SDCARD' option from top right menu",Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            appendTextView("Error While starting the activity for result", Color.RED, true);
            appendTextView(exceptionToString(e), Color.RED);
            e.printStackTrace();
        }
    }

    private String exceptionToString(Exception ex)  {
        StringWriter error = new StringWriter();
        ex.printStackTrace(new PrintWriter(error));
        return error.toString();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            Uri treeUri = resultData.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            appendTextView("Picked Folder :", Color.RED);
            appendTextView(pickedDir.getName());

            // List all existing files inside picked directory
            StringBuilder filesList = new StringBuilder();
            for (DocumentFile file : pickedDir.listFiles()) {
                filesList.append(file.getName() + "\n");
            }
            appendTextView("Files/Folders in the chosen path:" , Color.RED);
            appendTextView(filesList.toString());

            appendTextView("Retrieved URI :", Color.RED);
            appendTextView(pickedDir.getUri().toString());

            /*Uri uuu = DocumentsContract.buildDocumentUriUsingTree(treeUri, DocumentsContract.getDocumentId(pickedDir.getUri()));
            appendTextView(uuu.toString(), Color.RED);
            File file = new File(uuu.getPath());
            mApiChecker.check_write_file(file);*/

        }
        else {
            appendTextView("Result is not OK");
        }
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
        txtVw.setTextSize((value==null)? 0:value.length() + 20);
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
