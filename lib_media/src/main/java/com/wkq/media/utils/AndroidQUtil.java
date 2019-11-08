package com.wkq.media.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 作者: 吴奎庆
 * <p>
 * 时间: 2019/11/1
 * <p>
 * 简介:
 */
public class AndroidQUtil {
    /**
     * 判断是不是Android Q  版本
     *
     * @return
     */
    public static boolean isAndroidQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * URI转换为 Bitmap
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 通过MediaStore.Images.Media.insertImage接口可以将图片文件保存到/sdcard/Pictures/，
     * 但是只有图片文件保存可以通过MediaStore的接口保存，其他类型文件无法通过该接口保存；
     *
     * @param context
     * @param bitmap
     * @param title       标题
     * @param discription 简介
     */
    public static String saveBitmapToFile(Context context, Bitmap bitmap, String title, String discription) {
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, discription);
    }

    private static String signImage = "crop";


    /**
     * //注意：
     * //1. 这里的文件操作不再需要申请权限
     * //2. 沙盒中新建文件夹只能再系统指定的子文件夹中新建
     *
     * @param context
     * @param fileName
     * @param bitmap
     */
    public static String saveSignImageBox(Context context, String fileName, Bitmap bitmap) {
        //图片沙盒文件夹

        try {
            File PICTURES = context.getExternalCacheDir();
            File imageFileDirctory = new File(PICTURES + "/" + signImage);
            if (imageFileDirctory.exists()) {
                File imageFile = new File(PICTURES + "/" + signImage + "/" + fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile, false);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                return imageFile.getAbsolutePath();
            } else if (imageFileDirctory.mkdir()) {//如果该文件夹不存在，则新建
                //new一个文件
                File imageFile = new File(PICTURES + "/" + signImage + "/" + fileName);
                //通过流将图片写入
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return imageFile.getAbsolutePath();
            }
        } catch (Exception e) {
            return "";
        }
        return "";

    }

    public static void clearInfoForFile(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 判断Uri 是否存在
     *
     * @param context
     * @param uri
     * @return
     */

    public static boolean isContentUriExists(Context context, Uri uri) {
        if (null == context) {
            return false;
        }
        ContentResolver cr = context.getContentResolver();
        try {
            AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
            if (null == afd) {
                return false;
            } else {
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        return true;
    }

    /**
     * 视频保存到公共区域
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Uri insertVideoIntoMediaStore(Context context, String fileName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");

        Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        return uri;
    }

    //
    public static String copyMp4(Context context, String fileUri) {
        String videoPath = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath() + System.currentTimeMillis();
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(Uri.parse(fileUri), "r");
            FileOutputStream fileInputStream = new FileOutputStream(videoPath);
            int fds = parcelFileDescriptor.detachFd();
            fileInputStream.write(fds);
            fileInputStream.close();
            parcelFileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return videoPath;

    }

    ;

    /**
     *    复制沙盒私有文件到Download公共目录下
     *
     *
     * @param context
     * @param orgFilePath orgFilePath是要复制的文件私有目录路径
     * @param displayName  displayName复制后文件要显示的文件名称带后缀（如xx.txt）
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void copyPrivateToDownload(Context context, String orgFilePath, String displayName) {
        ContentValues values = new ContentValues();
        //values.put(MediaStore.Images.Media.DESCRIPTION, "This is a file");
        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");//MediaStore对应类型名
        values.put(MediaStore.Files.FileColumns.TITLE, displayName);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Download/Test");//公共目录下目录名

        Uri external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;//内部存储的Download路径
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);//使用ContentResolver创建需要操作的文件
        //Log.i("Test--","insertUri: " + insertUri);

        InputStream ist = null;
        OutputStream ost = null;
        try {
            ist = new FileInputStream(new File(orgFilePath));
            if (insertUri != null) {
                ost = resolver.openOutputStream(insertUri);
            }
            if (ost != null) {
                byte[] buffer = new byte[4096];
                int byteCount = 0;
                while ((byteCount = ist.read(buffer)) != -1) {  // 循环从输入流读取 buffer字节
                    ost.write(buffer, 0, byteCount);        // 将读取的输入流写入到输出流
                }
            }
        } catch (IOException e) {
        } finally {
            try {
                if (ist != null) {
                    ist.close();
                }
                if (ost != null) {
                    ost.close();
                }
            } catch (IOException e) {
            }
        }

    }
}
