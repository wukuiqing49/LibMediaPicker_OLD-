package com.wkq.media.data;

import android.os.Parcel;
import android.os.Parcelable;


import com.wkq.media.PickerConfig;
import com.wkq.media.entity.Media;

import java.util.ArrayList;

/**
 * Created by Lynn on 2018/1/18.
 */

public class ImagePickerOptions implements Parcelable {

    public ImagePickerOptions() {

    }

    public int maxNum = 40;
    public long maxVideoSize = 188743680L;
    public long maxImageSize = 188743680L;
    public boolean needCamera = false;
    public int selectMode = PickerConfig.PICKER_IMAGE_VIDEO;
    public ArrayList<Media> selects;
    public String cachePath;
    public String videoTrimPath;//剪辑视频存储路径
    public boolean friendCircle = false;
    public ImagePickerCropParams cropParams;
    public boolean needCrop = false;
    public boolean singlePick = false;
    public boolean showTime = false;
    public int maxTime = 60 * 60_000;
    public boolean selectGift = true;

    public boolean isSelectGift() {
        return selectGift;
    }

    public void setSelectGif(boolean selectGift) {
        this.selectGift = selectGift;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public long getMaxVideoSize() {
        return maxVideoSize;
    }

    public void setMaxVideoSize(long maxVideoSize) {
        this.maxVideoSize = maxVideoSize;
    }

    public long getMaxImageSize() {
        return maxImageSize;
    }

    public void setMaxImageSize(long maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    public boolean isNeedCamera() {
        return needCamera;
    }

    public void setNeedCamera(boolean needCamera) {
        this.needCamera = needCamera;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public ArrayList<Media> getSelects() {
        return selects;
    }

    public void setSelects(ArrayList<Media> selects) {
        this.selects = selects;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public boolean isFriendCircle() {
        return friendCircle;
    }

    public void setFriendCircle(boolean friendCircle) {
        this.friendCircle = friendCircle;
    }

    public boolean isNeedCrop() {
        return needCrop;
    }

    public void setNeedCrop(boolean needCrop) {
        this.needCrop = needCrop;
    }

    public ImagePickerCropParams getCropParams() {
        return cropParams;
    }

    public void setCropParams(ImagePickerCropParams cropParams) {
        this.cropParams = cropParams;
    }

    public boolean isSinglePick() {
        return singlePick;
    }

    public void setSinglePick(boolean singlePick) {
        this.singlePick = singlePick;
    }

    public String getVideoTrimPath() {
        return videoTrimPath;
    }

    public void setVideoTrimPath(String videoTrimPath) {
        this.videoTrimPath = videoTrimPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.maxNum);
        parcel.writeLong(this.maxVideoSize);
        parcel.writeLong(this.maxImageSize);
        parcel.writeByte(this.needCamera ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.selectMode);
        parcel.writeList(selects);
        parcel.writeString(cachePath);
        parcel.writeByte(this.friendCircle ? (byte) 1 : (byte) 0);
        parcel.writeParcelable(cropParams, i);
        parcel.writeByte(this.needCrop ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.singlePick ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.showTime ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.maxTime);
        parcel.writeByte(this.selectGift ? (byte) 1 : (byte) 0);
        parcel.writeString(this.videoTrimPath);
    }

    protected ImagePickerOptions(Parcel in) {
        this.maxNum = in.readInt();
        this.maxVideoSize = in.readLong();
        this.maxImageSize = in.readLong();
        this.needCamera = in.readByte() != 0;
        this.selectMode = in.readInt();
        this.selects = in.readArrayList(Media.class.getClassLoader());
        this.cachePath = in.readString();
        this.friendCircle = in.readByte() != 0;
        this.cropParams = in.readParcelable(ImagePickerCropParams.class.getClassLoader());
        this.needCrop = in.readByte() != 0;
        this.singlePick = in.readByte() != 0;
        this.showTime = in.readByte() != 0;
        this.maxTime = in.readInt();
        this.selectGift = in.readByte() != 0;
        this.videoTrimPath = in.readString();
    }

    public static final Parcelable.Creator<ImagePickerOptions> CREATOR = new Creator<ImagePickerOptions>() {
        @Override
        public ImagePickerOptions createFromParcel(Parcel parcel) {
            return new ImagePickerOptions(parcel);
        }

        @Override
        public ImagePickerOptions[] newArray(int i) {
            return new ImagePickerOptions[i];
        }
    };

    @Override
    public String toString() {
        return "ImagePickerOptions{" +
                "maxNum=" + maxNum +
                ", maxVideoSize=" + maxVideoSize +
                ", maxImageSize=" + maxImageSize +
                ", needCamera=" + needCamera +
                ", selectMode=" + selectMode +
                ", selects=" + selects +
                ", cachePath='" + cachePath + '\'' +
                ", friendCircle=" + friendCircle +
                ", cropParams=" + cropParams +
                ", needCrop=" + needCrop +
                ", singlePick=" + singlePick +
                ", showTime=" + showTime +
                ", maxTime=" + maxTime +
                ", selectGift" + selectGift +
                ", videoTrimPath" + videoTrimPath +
                '}';
    }
}
