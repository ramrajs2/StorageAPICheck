package com.amazon.apicheck.secondarystorage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public void check_getExternalStorageDirectory(TextView txt_results) {
        txt_results.append("\n" + Environment.getExternalStorageDirectory().toString());
    }

    public void check_getExternalStoragePublicDirectory(TextView txt_results) {
        txt_results.append("\n <for Pictures directory>");
        txt_results.append("\n" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }

    public void check_getExternalStorageState(TextView txt_results) {
        txt_results.append("\n" + Environment.getExternalStorageState().toString());
    }

    public void check_isExternalStorageEmulated(TextView txt_results) {
        txt_results.append("\n" + Environment.isExternalStorageEmulated());
    }

    public void check_isExternalStorageRemovable(TextView txt_results) {
        txt_results.append("\n" + Environment.isExternalStorageRemovable());
    }

    public void check_getExternalCacheDir(TextView txt_results) {
        txt_results.append("\n" + mContext.getExternalCacheDir().toString());
    }

    public void check_getExternalFilesDir(TextView txt_results) {
        txt_results.append("\n <for Documents directory>");
        txt_results.append("\n" + mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void check_getExternalFilesDirs(TextView txt_results) {
        File files[] = mContext.getExternalFilesDirs(null);
        if (files != null)
            for (File file : files)
                txt_results.append("\n" + file.getAbsolutePath());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void check_getExternalCacheDirs(TextView txt_results) {
        File files[] = mContext.getExternalCacheDirs();
        if (files != null)
            for (File file : files)
                txt_results.append("\n" + file.getAbsolutePath());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_getExternalMediaDirs(TextView txt_results) {
        File files[] = mContext.getExternalMediaDirs();
        if (files != null)
            for (File file : files)
                txt_results.append("\n" + file.getAbsolutePath());
    }

    public void check_getenv_sec_storage(TextView txt_results) {
        txt_results.append("\n" + System.getenv("SECONDARY_STORAGE"));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_getExternalStorageState_secondary(TextView txt_results) {
        txt_results.append("\n" + Environment.getExternalStorageState(new File(System.getenv("SECONDARY_STORAGE"))));
    }

    public void check_write_app_data_path(TextView txt_results, ImageView img_result) {
        check_write_file(txt_results, img_result, mContext.getExternalFilesDir(null));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_write_to_dirs_path(TextView txt_results, ImageView img_result) {
        File[] files;
        files = mContext.getExternalFilesDirs(null);
        copyImageToPaths(txt_results, img_result, "getExternalFilesDirs", files);

        files = mContext.getExternalCacheDirs();
        copyImageToPaths(txt_results, img_result, "getExternalCacheDirs", files);

        files = mContext.getExternalMediaDirs();
        copyImageToPaths(txt_results, img_result, "getExternalMediaDirs", files);
    }

    private void copyImageToPaths(TextView txt_results, ImageView img_result, String apiName, File files[]) {
        if (files == null) {
            txt_results.append("\n" + apiName + "returned no paths.");
        } else {
            txt_results.append("\nCopying files to " + apiName + " paths");
            for (File file : files) {
                check_write_file(txt_results, img_result, file);
            }
        }
        txt_results.append("\n------------------------------------------------\n");
    }

    public void check_write_to_inaccessible_path(TextView txt_results, ImageView img_result) {
        String sec_path = System.getenv("SECONDARY_STORAGE");
        if (sec_path == null)
            txt_results.append("\nSECONDARY_STORAGE path is null. Copy aborted.. ");
        else
            check_write_file(txt_results, img_result, new File(sec_path));
    }

    public void check_write_to_pictures_path(TextView txt_results, ImageView img_result) {
        String sec_path = "/sdcard/Pictures/";
        if (sec_path == null)
            txt_results.append("\n/sdcard/Pictures/ path is null. Copy aborted.. ");
        else
            check_write_file(txt_results, img_result, new File(sec_path));
    }

    public void check_write_file(TextView txt_results, ImageView img_result, File filepath) {
        AssetManager assetMgr = mContext.getAssets();
        InputStream is = null;
        OutputStream out = null;
        String filename = "sample.png";
        try {
            if (filepath == null) {
                txt_results.append("\nWorking Path: " + filepath);
                return;
            }
            txt_results.append("\nPath: " + filepath.getAbsolutePath());

            //txt_results.append("\n\n Removing the file to be copied if already exists in the path.. ");
            File file = new File(filepath, filename);
            file.delete();

            //txt_results.append(Html.fromHtml("<br><br><u>Before Copying...</u>"));
            //listFiles(txt_results, filepath.toString());
            //txt_results.append("\n\nCopying the image below(" + filename + ") from Assets folder to the specified location... \n\n");
            is = assetMgr.open(filename);

            //Setting the image to imageView
            Bitmap bmap = BitmapFactory.decodeStream(is);
            img_result.setImageBitmap(bmap);

            //Copying file to Apps data folder
            file = new File(filepath, filename);
            out = new FileOutputStream(file);
            copyFile(is, out);
            //txt_results.append(Html.fromHtml("<br><u>After Copying...</u>"));
            //listFiles(txt_results, filepath.toString());

            txt_results.append(" \n >>>>>> Copied Successfully <<<<<");
            txt_results.append("\n\n                      PASS!!!");
        } catch (IOException e) {
            txt_results.append(" \n >>>>>> Copy Failed <<<<<< \n" + e.toString());
            txt_results.append("\n\n                      FAIL!!!");
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

    private void listFiles(TextView txt_results, String path) {
        txt_results.append(Html.fromHtml("<br><u> Contents of the path above: </u>"));
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null)
            for (int i = 0; i < files.length; i++) {
                txt_results.append("\n" + files[i].getName());
            }
        else
            txt_results.append("\n<empty>");
        txt_results.append("\n");
    }

}
