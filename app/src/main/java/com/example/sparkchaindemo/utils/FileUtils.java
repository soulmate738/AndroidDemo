package com.example.sparkchaindemo.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final int MAX_LOG_LENGTH = 4000; // 最大日志长度
    public  static boolean renameFile(String directoryPath, String oldFileName, String newFileName) {
        // 构建原文件和目标文件的对象
        File oldFile = new File(directoryPath, oldFileName);
        File newFile = new File(directoryPath, newFileName);

        // 检查原文件是否存在且为文件类型
        if (!oldFile.exists() || !oldFile.isFile()) {
            Log.e("Storage", "原文件不存在或不是文件: " + oldFile.getAbsolutePath());
            return false;
        }

        // 检查目标文件是否已存在
        if (newFile.exists()) {
            Log.e("Storage", "目标文件已存在: " + newFile.getAbsolutePath());
            return false;
        }

        // 执行重命名操作
        return oldFile.renameTo(newFile);
    }
    public static boolean deleteFile(String directoryPath, String fileName) {
        // 构建文件对象
        File fileToDelete = new File(directoryPath, fileName);

        // 检查文件是否存在且为文件类型
        if (!fileToDelete.exists() || !fileToDelete.isFile()) {
            Log.e("Storage", "文件不存在或不是文件: " + fileToDelete.getAbsolutePath());
            return false;
        }

        // 执行删除操作
        return fileToDelete.delete();
    }

    public static List<String> getFileNames(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(directoryPath);

        // 检查目录是否存在且为目录类型
        if (!directory.exists() || !directory.isDirectory()) {
            return fileNames; // 返回空列表
        }

        // 获取目录下的所有文件
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // 直接添加完整文件名（包含后缀）
                    fileNames.add(file.getName());
                }
            }
        }

        return fileNames;
    }
    public static List<String> getSubfolderNames(String parentPath) {
        List<String> subfolderNames = new ArrayList<>();
        File parentDir = new File(parentPath);

        // 检查父目录是否存在且为目录类型
        if (!parentDir.exists() || !parentDir.isDirectory()) {
            return subfolderNames; // 返回空列表
        }

        // 获取目录下的所有文件和文件夹
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    subfolderNames.add(file.getName()); // 添加子文件夹名称
                }
            }
        }

        return subfolderNames;
    }
    public static boolean renameFolder(String parentPath, String oldName, String newName) {
        // 构建原文件夹和目标文件夹的文件对象
        File oldFolder = new File(parentPath, oldName);
        File newFolder = new File(parentPath, newName);

        // 检查原文件夹是否存在且为目录
        if (!oldFolder.exists() || !oldFolder.isDirectory()) {
            Log.e("Storage", "原文件夹不存在或不是目录: " + oldFolder.getAbsolutePath());
            return false;
        }

        // 检查目标文件夹是否已存在
        if (newFolder.exists()) {
            Log.e("Storage", "目标文件夹已存在: " + newFolder.getAbsolutePath());
            return false;
        }

        // 执行重命名操作
        return oldFolder.renameTo(newFolder);
    }
    public static boolean deleteFolder(String parentPath, String folderName) {
        // 构建要删除的文件夹对象
        File folderToDelete = new File(parentPath, folderName);

        // 检查文件夹是否存在且为目录
        if (!folderToDelete.exists() || !folderToDelete.isDirectory()) {
            Log.e("Storage", "要删除的文件夹不存在或不是目录: " + folderToDelete.getAbsolutePath());
            return false;
        }

        // 递归删除文件夹及其内容
        return deleteRecursive(folderToDelete);
    }
    private static boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            // 递归删除所有子文件和子文件夹
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    boolean success = deleteRecursive(child);
                    if (!success) {
                        return false; // 只要有一个删除失败，则整体失败
                    }
                }
            }
        }

        // 删除当前文件或空文件夹
        return file.delete();
    }
    public static void writeFile(String path, byte[] bytes) {
        boolean append = false;
        try {
            File file = new File(path);
            if (file.exists()) {
                append = true;
            }else {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(path,append);//指定写到哪个路径中
            FileChannel fileChannel = out.getChannel();
            fileChannel.write(ByteBuffer.wrap(bytes)); //将字节流写入文件中
            fileChannel.force(true);//强制刷新
            fileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    public static void longLog(String tag, String message) {
        if (message.length() > MAX_LOG_LENGTH) {
            int chunkCount = message.length() / MAX_LOG_LENGTH; // 计算需要分成多少段
            for (int i = 0; i <= chunkCount; i++) {
                int max = MAX_LOG_LENGTH * (i + 1);
                if (max >= message.length()) {
                    Log.d(tag, message.substring(MAX_LOG_LENGTH * i));
                } else {
                    Log.d(tag, message.substring(MAX_LOG_LENGTH * i, max));
                }
            }
        } else {
            Log.d(tag, message);
        }
    }


    public static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = file.delete();
                if (!flag) break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        // 删除当前目录
        return dirFile.delete();
    }



    public static void saveToFile(String fileName,String inputText) {
        File file = new File(fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(inputText.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
