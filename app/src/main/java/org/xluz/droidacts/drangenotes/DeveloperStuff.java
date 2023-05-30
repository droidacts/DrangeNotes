package org.xluz.droidacts.drangenotes;
/*
From: stackoverflow.com/questions/43910549/backup-sqlite-database-to-sd-card
Usage: DeveloperOption.copyAppDataToLocal(this, getString(R.string.app_name))
 */

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DeveloperStuff {
    public static final String BASE_PATH = Environment.getExternalStoragePublicDirectory
            ("BackUp").getAbsolutePath();
    public static final String SEPARATOR = "/";
    private static boolean operationStatus = true;
    private static String dataDirectory = null;
    private static String appName = "APP_NAME";

    public static boolean copyAppDataToLocal(Activity callingActivity, String appName) {

        dataDirectory = callingActivity.getApplicationInfo().dataDir;
        DeveloperStuff.appName = appName;
        String TAG = "Developers";
        try {
            if(dataDirectory != null) {
                copyAppData(new File(dataDirectory, "shared_prefs"),"shared_prefs");
                copyAppData(new File(dataDirectory, "files"),"files");
                copyAppData(new File(dataDirectory, "databases"),"databases");
            } else {
                Log.e(TAG, "!!!!!Unable to get data directory for ACTIVITY-->" + callingActivity
                        .toString());
            }
        } catch (Exception ex) {
            Log.e(TAG, "!!!!@@@Exception Occurred while copying DATA--->"+ex.getMessage(),ex.fillInStackTrace());
            operationStatus = false;
        }

        return operationStatus;
    }

    private static void copyFileToStorage(String directoryName, String inFile, String fileName, boolean
            isDirectory, String subdirectoryName) {
        try {
            FileInputStream myInput = new FileInputStream(inFile);
            File out_dir;
            if(!isDirectory) {
                out_dir = new File(BASE_PATH + SEPARATOR + appName +
                        SEPARATOR + directoryName);
            } else {
                out_dir = new File(BASE_PATH + SEPARATOR + appName +
                        SEPARATOR + directoryName + SEPARATOR + subdirectoryName);
            }
            if(!out_dir.exists()) {
                operationStatus = out_dir.mkdirs();
            }
            String outFileName = out_dir + "/" + fileName;
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer1 = new byte[1024];
            int length;
            while ((length = myInput.read(buffer1)) > 0) {
                myOutput.write(buffer1, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyAppData(File fileTypeToBeCopied, String outDirectoryName){
        if(fileTypeToBeCopied.exists() && fileTypeToBeCopied.isDirectory()) {
            File[] files = fileTypeToBeCopied.listFiles();
            for (File file : files) {
                if(file.isFile()) {
                    copyFileToStorage(outDirectoryName, file.getAbsolutePath(), file.getName(), false, "");
                } else {
                    String folderName = file.getName();
                    File databaseDirsNew = new File(dataDirectory, outDirectoryName+"/" + folderName);
                    if(databaseDirsNew.exists() && databaseDirsNew.isDirectory()) {
                        File[] filesDB = databaseDirsNew.listFiles();
                        for (File file1 : filesDB) {
                            if(file1.isFile()) {
                                copyFileToStorage(outDirectoryName, file1.getAbsolutePath(), file1.getName(),
                                        true, folderName);
                            }
                        }
                    }
                }
            }
        }
    }
}
