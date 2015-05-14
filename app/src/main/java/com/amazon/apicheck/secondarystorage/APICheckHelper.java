package com.amazon.apicheck.secondarystorage;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
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

    public void check_getExternalStorageDirectory() {
        MainActivity.appendTextView(Environment.getExternalStorageDirectory().toString());
    }

    public void check_getExternalStoragePublicDirectory() {
        MainActivity.appendTextView("<for Pictures directory>", Color.DKGRAY);
        MainActivity.appendTextView(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
    }

    public void check_getExternalStorageState() {
        MainActivity.appendTextView("Primary/Internal Storage : " + Environment.getExternalStorageState().toString());
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
        MainActivity.appendTextView("<for Documents directory>", Color.DKGRAY);
        MainActivity.appendTextView(mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void check_getExternalFilesDirs() {
        File files[] = mContext.getExternalFilesDirs(null);
        if (files != null)
            for (File file : files)
                MainActivity.appendTextView((file!=null)?file.getAbsolutePath():"null");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void check_getExternalCacheDirs() {
        File files[] = mContext.getExternalCacheDirs();
        if (files != null)
            for (File file : files)
                MainActivity.appendTextView((file!=null)?file.getAbsolutePath():"null");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_getExternalMediaDirs() {
        File files[] = mContext.getExternalMediaDirs();
        if (files != null)
            for (File file : files)
                MainActivity.appendTextView((file!=null)?file.getAbsolutePath():"null");
    }

    public void check_getenv_sec_storage() {
        MainActivity.appendTextView(System.getenv("SECONDARY_STORAGE"));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_getExternalStorageState_secondary() {
        MainActivity.appendTextView(Environment.getExternalStorageState(new File(System.getenv("SECONDARY_STORAGE"))));
    }

    public void check_write_to_app_data_path() {
        check_write_file(mContext.getExternalFilesDir(null), true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void check_write_to_dirs_path() {
        File[] files;
        files = mContext.getExternalFilesDirs(null);
        copyImageToPaths("getExternalFilesDirs", files);

        files = mContext.getExternalCacheDirs();
        copyImageToPaths("getExternalCacheDirs", files);

        files = mContext.getExternalMediaDirs();
        copyImageToPaths("getExternalMediaDirs", files);
    }

    private void copyImageToPaths(String apiName, File files[]) {
        if (files == null) {
            MainActivity.appendTextView(apiName + "returned no paths.");
        } else {
            MainActivity.appendTextView("Copying an image to " + apiName + " paths", Color.WHITE, true);
            int count = 1;
            for (File file : files) {
                MainActivity.appendTextView("============" + count++ + "============");
                check_write_file(file, true);
            }
        }
    }

    public void check_write_to_inaccessible_path() {
        String sec_path = System.getenv("SECONDARY_STORAGE");
        if (sec_path == null)
            MainActivity.appendTextView("SECONDARY_STORAGE path is null. Copy aborted.. ");
        else
            check_write_file(new File(sec_path), false);
    }

    public void check_write_to_pictures_path() {
        String sec_path = "/sdcard/Pictures/";
        if (sec_path == null)
            MainActivity.appendTextView("/sdcard/Pictures/ path is null. Copy aborted.. ");
        else
            check_write_file(new File(sec_path), true);
    }

    public void check_write_file(File filepath, boolean shouldPass) {

        InputStream is = null;
        OutputStream out = null;
        String filename = "sample.txt";
        try {
            if (filepath == null) {
                MainActivity.appendTextView("Path: " + filepath);
                return;
            }
            MainActivity.appendTextView("Writing file to the path: \n" + filepath.getAbsolutePath(), Color.rgb(0,102,102));

            File outFile = new File(filepath, filename);
            outFile.createNewFile();

            // Writing a text file into the path given
            FileWriter writer = new FileWriter(outFile);
            writer.append("Some Sample Text");
            writer.flush();
            writer.close();

            // Writing file using BufferedWriter
            /*BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
            bw.write("Sample Text");
            bw.flush();
            bw.close();*/

            MainActivity.appendTextView(" >>>>>> Copied Successfully <<<<<", Color.rgb(0, 102, 51));
            MainActivity.appendTextView("\t\t\t\tTestcase Passed!!!", Color.rgb(0, 102, 51));
        } catch (Exception e) {
            MainActivity.appendTextView(" >>>>>> Copy Failed <<<<<< ", Color.RED);
            MainActivity.appendTextView(e.toString(), Color.RED);
            if(shouldPass) {
                MainActivity.appendTextView("\t\t\t\tTestcase Failed!!!", Color.rgb(153, 0, 0));
            }
            else    {
                MainActivity.appendTextView("\n3P apps shouldn't have permission to write to this location\n\t\t\t\tTestcase Passed!!!", Color.rgb(0,102,51));
            }
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

    private void listFiles(String path) {
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
