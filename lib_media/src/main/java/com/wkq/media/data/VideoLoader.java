package com.wkq.media.data;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;


import com.wkq.media.R;
import com.wkq.media.entity.Folder;
import com.wkq.media.entity.Media;

import java.io.File;
import java.util.ArrayList;



public class VideoLoader extends LoaderM implements LoaderManager.LoaderCallbacks {
    String[] MEDIA_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Video.VideoColumns.DURATION};

    Context mContext;
    DataCallback mLoader;

    public VideoLoader(Context context, DataCallback loader) {
        this.mContext = context;
        this.mLoader = loader;
    }

    @Override
    public Loader onCreateLoader(int picker_type, Bundle bundle) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                MEDIA_PROJECTION,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        ArrayList<Folder> folders = new ArrayList<>();
        Folder allFolder = new Folder(mContext.getResources().getString(R.string.all_video));
        folders.add(allFolder);
        Cursor cursor = (Cursor) o;
        Log.e("dmc", cursor.getCount() + "数量数量");
        while (cursor.moveToNext()) {
            //获取的视频路径
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
            //todo 过滤掉不存在的文件 杨帅 2019-4-18
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
            if (size < 512 || duration<1 ) continue;
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));

            Uri photoUri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
            String fileUri = photoUri.toString();

            String dirName = getParent(path);
            Media media = new Media(path, name, dateTime, mediaType, size, id, dirName, duration, fileUri);
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
        mLoader.onData(folders);
        cursor.close();
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }
}
