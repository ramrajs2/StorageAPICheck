package com.amazon.apicheck.secondarystorage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class just provides APIs to invoke the requested APIs
 *
 * Whenever new API is included in the MainActivity.apisToTest,
 * a check_<API> method needs to be introduced here
 */
public class APICheckHelper {

    Context mContext;

    public APICheckHelper()    {
        mContext = MainActivity.getAppContext();
    }

    public void check_getExternalStorageDirectory(TextView txt_results)   {
        txt_results.append("\n" + Environment.getExternalStorageDirectory().toString());
    }

    public void check_getExternalStoragePublicDirectory(TextView txt_results)   {
        txt_results.append("\n <for Pictures directory>");
        txt_results.append("\n" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }

    public void check_getExternalStorageState(TextView txt_results)   {
        txt_results.append("\n" + Environment.getExternalStorageState().toString());
    }

    public void check_isExternalStorageEmulated(TextView txt_results)   {
        txt_results.append("\n" + Environment.isExternalStorageEmulated());
    }

    public void check_isExternalStorageRemovable(TextView txt_results)   {
        txt_results.append("\n" + Environment.isExternalStorageRemovable());
    }

    public void check_getExternalCacheDir(TextView txt_results)   {
        txt_results.append("\n" + mContext.getExternalCacheDir().toString());
    }

    public void check_getExternalFilesDir(TextView txt_results)   {
        txt_results.append("\n <for Documents directory>");
        txt_results.append("\n" + mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString());
    }

    public void check_getenv_sec_storage(TextView txt_results)  {
        txt_results.append("\n"+System.getenv("SECONDARY_STORAGE"));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_getExternalStorageState_secondary(TextView txt_results)  {
        txt_results.append("\n"+Environment.getExternalStorageState(new File(System.getenv("SECONDARY_STORAGE"))));
    }

    public void check_write_file()  {
        AssetManager assetMgr = mContext.getAssets();
        String filename = "sample.png";
        try {
            InputStream is = assetMgr.open(filename);
            File file = new File(mContext.getExternalFilesDir(null), filename);
            OutputStream out = new FileOutputStream(file);
            copyFile(is,out);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyFile(InputStream is, OutputStream out) throws IOException {
        byte[] buff = new byte[1024];
        int read;
        while((read = is.read(buff))!=1)    {
            out.write(buff, 0, read);
        }
    }
}
