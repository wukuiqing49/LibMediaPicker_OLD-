package com.wkq.media.ui.camera;


/**
 * Author by Yuansiwen.com, Date on 2018/8/28.
 * PS: Not easy to write code, please indicate.
 */
public class CircleChoose {
    private CircleData.ReleaseVideoTypeListBean message;
    public CircleChoose(CircleData.ReleaseVideoTypeListBean message){
        this.message=message;
    }
    public CircleData.ReleaseVideoTypeListBean getMessage() {
        return message;
    }

    public void setMessage(CircleData.ReleaseVideoTypeListBean message) {
        this.message = message;
    }

}
