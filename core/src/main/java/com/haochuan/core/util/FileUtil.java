package com.haochuan.core.util;

import android.util.Log;

import com.haochuan.core.Logger;

import java.io.File;
import java.io.IOException;

public final class FileUtil {
    /**
     * Create a file, if the directory of the file does not exist, create a superior directory
     *
     * @param filePath file path
     * @return Returning true indicates that the file was created successfully or already exists.
     */
    private static String TAG = "FileUtil";
    public static boolean createFile(String filePath) {
        Logger.d("FileUtil,createFile(),filePath:" + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                boolean mkDirsResult = makeDirs(file.getParent());
                if (!mkDirsResult) {
                    Logger.d( "create File[" + filePath + "] fail because it's parent dir created failed");
                    return false;
                }
            }
            try {
                boolean createFileResult = file.createNewFile();
                Logger.d("create File[" + filePath + "] result " + createFileResult);
                return createFileResult;
            } catch (IOException e) {
                Logger.d("create File[" + filePath + "]cause exception : " + e.getLocalizedMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            Logger.d("File[" + filePath + "] had exists!");
            return true;
        }
    }

    /**
     * Create a directory, if the directory has a parent directory, create the same
     *
     * @param dirPath Directory path
     * @return Return true to create a successful one.
     */
    public static boolean makeDirs(String dirPath) {
        Logger.d("FileUtil,makeDirs(),dirPath:" + dirPath);
        File folder = new File(dirPath);
        if (!folder.exists()) {
            boolean mkDirsResult = folder.mkdirs();
            Logger.d( "makeDirs[" + dirPath + "] result " + mkDirsResult);
            return mkDirsResult;
        }
        Logger.d("Dirs[" + dirPath + "] had exists!");
        return true;
    }

    /**
     * Delete the file, if the file is a directory and there is a subordinate directory or file, the deletion will fail
     *
     * @param filePath file path
     * @return Returning true indicates that the file was deleted successfully or the file does not exist.
     */
    public static boolean delete(String filePath) {
        Logger.d("FileUtil,delete(),filePath:" + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleteFileResult = file.delete();
            Logger.d("deleted File[" + filePath + "] result " + deleteFileResult);
            return deleteFileResult;
        }
        Logger.d("File[" + filePath + "] is not exists");
        return true;
    }


    /**
     * Be cautious! Force the file to be deleted. If the file is a directory and there is a subordinate directory or file, it will be deleted together.
     *
     * @param filePath file path
     * @return Return true to indicate that the file or all files in the file directory are deleted successfully.
     */
    public static boolean deleteForce(String filePath) {
        Logger.d("FileUtil,deleteForce(),filePath:" + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                boolean deleteDirsResult = file.delete();
                Log.d(TAG, "deleted file[" + filePath + "] result " + deleteDirsResult);
                return deleteDirsResult;
            }
            for (File childFile : childFiles) {
                deleteForce(childFile.getPath());
            }
            boolean deleteDirsResult = file.delete();
            Log.d(TAG, "deleted file[" + filePath + "] result " + deleteDirsResult);
            return deleteDirsResult;
        }
        Log.d(TAG, "file[" + filePath + "] is not exists");
        return true;
    }

    /**
     * Rename file
     *
     * @param filePath file path
     * @param newName  New file name
     * @return Returning true indicates that the file was named successfully.
     */
    public static boolean rename(String filePath, String newName) {
        Logger.d(String.format("FileUtil,rename('%s','%s')",filePath,newName));
        File file = new File(filePath);
        File newFile = new File(file.getParent(), newName);
        boolean renameFileResult = file.renameTo(newFile);
        Log.d(TAG, "rename File[" + filePath + "] result " + renameFileResult);
        Log.d(TAG, "newFile Name[" + file.getName() + "] ");
        return renameFileResult;
    }
}
