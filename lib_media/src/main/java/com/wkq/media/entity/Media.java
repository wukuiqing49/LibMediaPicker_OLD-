package com.wkq.media.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dmcBig on 2017/7/4.
 */

public class Media implements Parcelable {
    public String path;
    public String name;
    public long time;
    public int mediaType;
    public long size;
    public int id;
    public String parentDir;
    public boolean isDeleted;
    public int duration;
    public boolean compressed;
    public String fileUri;

    public Media(String path, String name, long time, int mediaType, long size, int id, String parentDir, int duration,String fileUri) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.mediaType = mediaType;
        this.size = size;
        this.id = id;
        this.parentDir = parentDir;
        this.duration = duration;
        this.compressed = false;
        this.fileUri=fileUri;
    }


    public Media(String path, String name, long time, int mediaType, long size, int id, String parentDir, int duration, boolean compressed) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.mediaType = mediaType;
        this.size = size;
        this.id = id;
        this.parentDir = parentDir;
        this.duration = duration;
        this.compressed = compressed;
        this.fileUri=fileUri;
    }

    public Media(String path, String name, long time, int mediaType, long size, int id, String parentDir,String fileUri) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.mediaType = mediaType;
        this.size = size;
        this.id = id;
        this.parentDir = parentDir;
        this.fileUri=fileUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeLong(this.time);
        dest.writeInt(this.mediaType);
        dest.writeLong(this.size);
        dest.writeInt(this.id);
        dest.writeString(this.parentDir);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeInt(duration);
        dest.writeByte((byte) (compressed ? 1 : 0));
        dest.writeString(this.fileUri);
    }

    protected Media(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.time = in.readLong();
        this.mediaType = in.readInt();
        this.size = in.readLong();
        this.id = in.readInt();
        this.parentDir = in.readString();
        this.isDeleted = in.readByte() != 0;
        this.duration = in.readInt();
        this.compressed = in.readByte() != 0;
        this.fileUri = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
