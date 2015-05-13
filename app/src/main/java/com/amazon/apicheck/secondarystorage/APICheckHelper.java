package com.amazon.apicheck.secondarystorage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * This class just provides APIs to invoke the requested APIs
 * <p/>
 * Whenever new API is included in the MainActivity.apisToTest,
 * a check_<API> method needs to be introduced here
 */
public class APICheckHelper {

    Context mContext;

    public APICheckHelper() {
        mContext = MainActivity.getAppContext();
    }

    public void check_getExternalStorageDirectory() {
        MainActivity.appendTextView(Environment.getExternalStorageDirectory().toString());
    }

    public void check_getExternalStoragePublicDirectory() {
        MainActivity.appendTextView("<for Pictures directory>", Color.RED);
        MainActivity.appendTextView(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
    }

    public void check_getExternalStorageState() {
        MainActivity.appendTextView(Environment.getExternalStorageState().toString());
    }

    public void check_isExternalStorageEmulated() {
        MainActivity.appendTextView("" + Environment.isExternalStorageEmulated());
    }

    public void check_isExternalStorageRemovable() {
        MainActivity.appendTextView("" + Environment.isExternalStorageRemovable());
    }

    public void check_getExternalCacheDir() {
        MainActivity.appendTextView(mContext.getExternalCacheDir().toString());
    }

    public void check_getExternalFilesDir() {
        MainActivity.appendTextView("<for Documents directory>", Color.RED);
        MainActivity.appendTextView(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void check_getExternalFilesDirs() {
        File files[] = mContext.getExternalFilesDirs(null);
        if (files != null)
            for (File file : files)
                MainActivity.appendTextView(file.getAbsolutePath());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void check_getExternalCacheDirs() {
        File files[] = mContext.getExternalCacheDirs();
        if (files != null)
            for (File file : files)
                MainActivity.appendTextView(file.getAbsolutePath());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_getExternalMediaDirs() {
        File files[] = mContext.getExternalMediaDirs();
        if (files != null)
            for (File file : files)
                MainActivity.appendTextView(file.getAbsolutePath());
    }

    public void check_getenv_sec_storage() {
        MainActivity.appendTextView(System.getenv("SECONDARY_STORAGE"));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_getExternalStorageState_secondary() {
        MainActivity.appendTextView(Environment.getExternalStorageState(new File(System.getenv("SECONDARY_STORAGE"))));
    }

    public void check_write_app_data_path() {
        check_write_file(mContext.getExternalFilesDir(null));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_write_to_dirs_path() {
        File[] files;
        files = mContext.getExternalFilesDirs(null);
        copyImageToPaths( "getExternalFilesDirs", files);

        files = mContext.getExternalCacheDirs();
        copyImageToPaths(  "getExternalCacheDirs", files);

        files = mContext.getExternalMediaDirs();
        copyImageToPaths(  "getExternalMediaDirs", files);
    }

    private void copyImageToPaths( String apiName, File files[]) {
        if (files == null) {
            MainActivity.appendTextView(apiName + "returned no paths.");
        } else {
            MainActivity.appendTextView("Copying an image to " + apiName + " paths", Color.WHITE, true);
            int count = 1;
            for (File file : files) {
                MainActivity.appendTextView("============" + count++ + "============");
                check_write_file(file);
            }
        }
    }

    public void check_write_to_inaccessible_path() {
        String sec_path = System.getenv("SECONDARY_STORAGE");
        if (sec_path == null)
            MainActivity.appendTextView("SECONDARY_STORAGE path is null. Copy aborted.. ");
        else
            check_write_file(  new File(sec_path));
    }

    public void check_write_to_pictures_path() {
        String sec_path = "/sdcard/Pictures/";
        if (sec_path == null)
            MainActivity.appendTextView("/sdcard/Pictures/ path is null. Copy aborted.. ");
        else
            check_write_file(  new File(sec_path));
    }

    public void check_write_file( File filepath) {
        AssetManager assetMgr = mContext.getAssets();
        InputStream is = null;
        OutputStream out = null;
        String filename = "sample.txt";
        try {
            if (filepath == null) {
                MainActivity.appendTextView("Working Path: " + filepath);
                return;
            }
            MainActivity.appendTextView("Path: " + filepath.getAbsolutePath(), Color.BLACK);

            /*//MainActivity.appendTextView("Removing the file to be copied if already exists in the path.. ");
            File file;
            file = new File(filepath, filename);
            file.delete();

            //MainActivity.appendTextView(Html.fromHtml("<br><br><u>Before Copying...</u>"));
            //listFiles( filepath.toString());
            //MainActivity.appendTextView("Copying the image below(" + filename + ") from Assets folder to the specified location... ");
            is = assetMgr.open(filename);

            //Copying file to Apps data folder
            file = new File(filepath, filename);
            out = new FileOutputStream(file);
            copyFile(is, out);
            //MainActivity.appendTextView(Html.fromHtml("<br><u>After Copying...</u>"));
            //listFiles( filepath.toString());*/


            // Writing a text file into the path given
            File outFile = new File(filepath, filename);
            FileWriter writer = new FileWriter(outFile);
            writer.append("Some Sample Text");
            writer.flush();
            writer.close();

            MainActivity.appendTextView(" >>>>>> Copied Successfully <<<<<", Color.rgb(0, 102, 51));
            MainActivity.appendTextView("                      PASS!!!" , Color.rgb(0,102,51));
        } catch (Exception e) {
            MainActivity.appendTextView(" >>>>>> Copy Failed <<<<<< " , Color.RED);
            MainActivity.appendTextView(e.toString(), Color.RED);
            MainActivity.appendTextView("                     FAIL!!!", Color.rgb(153,0,0));
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (out != null) out.close();
            } catch (Exception e) {
            }
        }
    }

    private void copyFile(InputStream is, OutputStream out) throws IOException {
        byte[] buff = new byte[1024];
        int read;
        while ((read = is.read(buff)) > 0) {
            out.write(buff, 0, read);
        }
    }

    private void listFiles( String path) {
        MainActivity.appendTextView("Contents of the path above:");
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null)
            for (int i = 0; i < files.length; i++) {
                MainActivity.appendTextView(files[i].getName());
            }
        else
            MainActivity.appendTextView("<empty>");
    }

}
