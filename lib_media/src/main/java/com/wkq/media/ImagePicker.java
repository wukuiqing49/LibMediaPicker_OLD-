package com.wkq.media;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import com.wkq.media.data.ImagePickerCropParams;
import com.wkq.media.data.ImagePickerOptions;
import com.wkq.media.entity.Media;
import com.wkq.media.ui.PickerActivity;

import java.util.ArrayList;


/**
 * Created by Lynn on 2018/1/18.
 */

public class ImagePicker {

    /**
     * 默认的ResultCode
     */
    public static final int DEF_RESULT_CODE = 136;

    private ImagePickerOptions mOptions;

    public ImagePickerOptions getOptions() {
        return mOptions;
    }

    private ImagePicker() {

    }

    private ImagePicker(ImagePickerOptions options) {
        this.mOptions = options;
    }

    /**
     * 图片选择
     *
     * @param activity
     * @param requestCode 请求码
     * @param resultCode  结果码
     */
    public void start(Activity activity, int requestCode, int resultCode) {
        Intent intent = new Intent(activity, PickerActivity.class);
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
//        intent.putExtra(PickerConfig.SELECT_MODE, mOptions.selectMode);//default image and video (Optional)
//        intent.putExtra(PickerConfig.MAX_SELECT_VIDEO_SIZE, mOptions.maxVideoSize); //default 180MB (Optional)
//        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, mOptions.maxNum);  //default 40 (Optional)
//        intent.putExtra(PickerConfig.NEED_CAMERA, mOptions.needCamera);
        intent.putParcelableArrayListExtra(PickerConfig.DEFAULT_SELECTED_LIST, mOptions.selects); // (Optional)
        intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
//        intent.putExtra(PickerConfig.FRIEND_CIRCLE, mOptions.friendCircle);
//        intent.putExtra(PickerConfig.CACHE_PATH, mOptions.cachePath);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 图片选择
     *
     * @param fragment
     * @param requestCode 请求码
     * @param resultCode  结果码
     */
    public void start(Fragment fragment, int requestCode, int resultCode) {
        Intent intent = new Intent(fragment.getActivity(), PickerActivity.class);
        intent.putExtra(PickerConfig.INTENT_KEY_OPTIONS, mOptions);
//        intent.putExtra(PickerConfig.SELECT_MODE, mOptions.selectMode);//default image and video (Optional)
//        intent.putExtra(PickerConfig.MAX_SELECT_VIDEO_SIZE, mOptions.maxVideoSize); //default 180MB (Optional)
//        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, mOptions.maxNum);  //default 40 (Optional)
//        intent.putExtra(PickerConfig.NEED_CAMERA, mOptions.needCamera);
        intent.putParcelableArrayListExtra(PickerConfig.DEFAULT_SELECTED_LIST, mOptions.selects); // (Optional)
        intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
//        intent.putExtra(PickerConfig.FRIEND_CIRCLE, mOptions.friendCircle);
//        intent.putExtra(PickerConfig.CACHE_PATH, mOptions.cachePath);
        fragment.getActivity().startActivityForResult(intent, requestCode);
    }

    public static final class Builder {
        private ImagePickerOptions mOptions;

        public Builder() {
            mOptions = new ImagePickerOptions();
        }

        public Builder selectMode(int selectMode) {
            mOptions.setSelectMode(selectMode);
            return this;
        }

        public Builder maxVideoSize(int maxSize) {
            mOptions.setMaxVideoSize(maxSize);
            return this;
        }

        public Builder maxImageSize(int maxImageSize) {
            mOptions.setMaxImageSize(maxImageSize);
            return this;
        }

        public Builder showTime(boolean showTime) {
            mOptions.setShowTime(showTime);
            return this;
        }

        public Builder setSelectGif(boolean selectGif) {
            mOptions.setSelectGif(selectGif);
            return this;
        }

        public Builder maxTime(int maxTime) {
            mOptions.setMaxTime(maxTime);
            return this;
        }

        public Builder maxNum(int maxNum) {
            mOptions.setMaxNum(maxNum);
            return this;
        }

        public Builder needCamera(boolean needCamera) {
            mOptions.setNeedCamera(needCamera);
            return this;
        }

        public Builder defaultSelectList(ArrayList<Media> select) {
            mOptions.setSelects(select);
            return this;
        }

        public Builder cachePath(String cachePath) {
            mOptions.setCachePath(cachePath);
            return this;
        }

        public Builder videoTrimPath(String videoTrimPath) {
            mOptions.setVideoTrimPath(videoTrimPath);
            return this;
        }

        public Builder isFriendCircle(boolean isFriendCircle) {
            mOptions.setFriendCircle(isFriendCircle);
            return this;
        }

        public Builder doCrop(ImagePickerCropParams cropParams) {
            mOptions.setNeedCrop(cropParams != null);
            mOptions.setCropParams(cropParams);
            return this;
        }

        public Builder doCrop(int aspectX, int aspectY, int outputX, int outputY) {
            mOptions.setNeedCrop(true);
            mOptions.setCropParams(new ImagePickerCropParams(aspectX, aspectY, outputX, outputY));
            return this;
        }

        public Builder isSinglePick(boolean isSinglePick) {
            mOptions.setSinglePick(isSinglePick);
            return this;
        }

        public ImagePicker builder() {
            return new ImagePicker(mOptions);
        }
    }
}
