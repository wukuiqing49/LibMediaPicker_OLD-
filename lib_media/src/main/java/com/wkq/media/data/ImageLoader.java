package com.wkq.media.data;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;


import com.wkq.media.R;
import com.wkq.media.entity.Folder;
import com.wkq.media.entity.Media;

import java.io.File;
import java.util.ArrayList;
public class ImageLoader extends LoaderM implements LoaderManager.LoaderCallbacks {

    String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};

    Context mContext;
    DataCallback mLoader;

    public ImageLoader(Context context, DataCallback loader) {
        this.mContext = context;
        this.mLoader = loader;
    }

    @Override
    public Loader onCreateLoader(int picker_type, Bundle bundle) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                IMAGE_PROJECTION,
                selection,
                null, // Selection args (none).
                MediaStore.Images.Media.DATE_ADDED + " DESC" // Sort order.
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        ArrayList<Folder> folders = new ArrayList<>();
        Folder allFolder = new Folder(mContext.getResources().getString(R.string.all_image));
        folders.add(allFolder);
        Cursor cursor = null;
        if (o != null) cursor = (Cursor) o;
        while (cursor != null && cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            //todo 过滤掉不存在的文件 杨帅 2019-4-18
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }

            //todo 过滤掉.wbmp格式的图片 杨帅 2019-4-17
            //判断路径的长度是否大于后缀名
            if (path.length() > ".wbmp".length()) {
                //进行判断
                if (path.lastIndexOf(".wbmp") != -1) {
                    continue;
                }
            }
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
            if (size < 512  ) continue;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
            String fileUri=photoUri.toString();
            if (TextUtils.isEmpty(path)) continue;
            String dirName = getParent(path);
            Media media = new Media(path, name, dateTime, mediaType, size, id, dirName,fileUri);
            allFolder.addMedias(media);

            int index = hasDir(folders, dirName);
            if (index != -1) {
                folders.get(index).addMedias(media);
            } else {
                Folder folder = new Folder(dirName);
                folder.addMedias(media);
                folders.add(folder);
            }
        }
        if (mLoader != null) mLoader.onData(folders);
        if (cursor != null) cursor.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


}