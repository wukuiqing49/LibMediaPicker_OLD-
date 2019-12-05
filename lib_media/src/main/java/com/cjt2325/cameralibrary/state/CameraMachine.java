package com.cjt2325.cameralibrary.state;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.cjt2325.cameralibrary.CameraInterface;
import com.cjt2325.cameralibrary.view.CameraView;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/9/8
 * 描    述：
 * =====================================
 */
public class CameraMachine implements State {


    private Context context;
    private State state;
    private CameraView view;
//    private CameraInterface.CameraOpenOverCallback cameraOpenOverCallback;

    private State previewState;       //浏览状态(空闲)
    private State borrowPictureState; //浏览图片
    private State borrowVideoState;   //浏览视频

    public CameraMachine(Context context, CameraView view, CameraInterface.CameraOpenOverCallback
            cameraOpenOverCallback) {
        this.context = context;
        previewState = new PreviewState(this);
        borrowPictureState = new BorrowPictureState(this);
        borrowVideoState = new BorrowVideoState(this);
        //默认设置为空闲状态
        this.state = previewState;
//        this.cameraOpenOverCallback = cameraOpenOverCallback;
        this.view = view;
    }

    public CameraView getView() {
        return view;
    }

    public Context getContext() {
        return context;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setDefaultState() {
        this.state = previewState;
    }

    //获取浏览图片状态
    State getBorrowPictureState() {
        return borrowPictureState;
    }

    //获取浏览视频状态
    State getBorrowVideoState() {
        return borrowVideoState;
    }

    //获取空闲状态
    public State getPreviewState() {
        return previewState;
    }

    @Override
    public boolean start(SurfaceHolder holder, float screenProp) {
        return state.start(holder, screenProp);
    }

    @Override
    public void stop() {
        state.stop();
    }

    @Override
    public boolean foucs(float x, float y, CameraInterface.FocusCallback callback) {
        return state.foucs(x, y, callback);
    }

    @Override
    public boolean swtich(SurfaceHolder holder, float screenProp) {
        return state.swtich(holder, screenProp);
    }

    @Override
    public void restart() {
        state.restart();
    }

    @Override
    public boolean capture() {
        return state.capture();
    }

    @Override
    public boolean record(Surface surface, float screenProp,Context context) {
        return state.record(surface, screenProp,context);
    }

    @Override
    public void stopRecord(boolean isShort, long time) {
        state.stopRecord(isShort, time);
    }

    @Override
    public void cancle(SurfaceHolder holder, float screenProp) {
        state.cancle(holder, screenProp);
    }

    @Override
    public void confirm() {
        state.confirm();
    }


    @Override
    public void zoom(float zoom, int type) {
        state.zoom(zoom, type);
    }

    @Override
    public void flash(String mode) {
        state.flash(mode);
    }

    @Override
    public void onDestroy() {
        if (state != null) state.onDestroy();
    }

    public State getState() {
        return this.state;
    }
}
