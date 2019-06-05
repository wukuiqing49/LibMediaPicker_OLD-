package com.wkq.media.ui.camera;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class CircleData implements Serializable {


    private List<ReleaseVideoTypeListBean> releaseVideoTypeList;
    private List<SnowVideoTypeListBean> snowVideoTypeList;

    public List<ReleaseVideoTypeListBean> getReleaseVideoTypeList() {
        return releaseVideoTypeList;
    }

    public void setReleaseVideoTypeList(List<ReleaseVideoTypeListBean> releaseVideoTypeList) {
        this.releaseVideoTypeList = releaseVideoTypeList;
    }

    public List<SnowVideoTypeListBean> getSnowVideoTypeList() {
        return snowVideoTypeList;
    }

    public void setSnowVideoTypeList(List<SnowVideoTypeListBean> snowVideoTypeList) {
        this.snowVideoTypeList = snowVideoTypeList;
    }

    public static class ReleaseVideoTypeListBean implements Serializable {
        /**
         * id : 111
         * path : http://apiwjjtest.cnlive.com/Daren/witness/releaseWitness.action
         * pic : https://wjj.ys1.cnliveimg.com/769/img/2018/0129/grzx_mjz.png
         * text : 目击者
         * videoType : witness
         * multiplecategory:888
         * multipleCategory
         */

        private int id;
        private String path;
        private String pic;
        private String text;
        private String videoType;
        private String changePath;
        private int multipleCategory;
        private List<indextag> categoryTag;

       /* public ReleaseVideoTypeListBean(Parcel in) {
            id = in.readInt();
            path = in.readString();
            pic = in.readString();
            text = in.readString();
            videoType = in.readString();
            changePath = in.readString();
            multipleCategory = in.readInt();
        }*/

       /* public static final Parcelable.Creator<ReleaseVideoTypeListBean> CREATOR = new Parcelable.Creator<ReleaseVideoTypeListBean>() {
            @Override
            public ReleaseVideoTypeListBean createFromParcel(Parcel in) {
                return new ReleaseVideoTypeListBean(in);
            }

            @Override
            public ReleaseVideoTypeListBean[] newArray(int size) {
                return new ReleaseVideoTypeListBean[size];
            }
        };*/

        public List<indextag> getCategoryTag() {
            return categoryTag;
        }

        public void setCategoryTag(List<indextag> categoryTag) {
            this.categoryTag = categoryTag;
        }


        public int getMultiplecategory() {
            return multipleCategory;
        }

        public void setMultiplecategory(int multipleCategory) {
            this.multipleCategory = multipleCategory;
        }


        public String getChangePath() {
            return changePath;
        }

        public void setChangePath(String changePath) {
            this.changePath = changePath;
        }



        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPath() {
            return  TextUtils.isEmpty(path) ? "Daren/moment/releaseMoment.action" : path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPic() {
            return TextUtils.isEmpty(pic) ? "https://wjj.ys1.cnliveimg.com/769/img/2018/0129/grzx_shq.png" : pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getText() {
            return TextUtils.isEmpty(text) ? "生活圈" : text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getVideoType() {
            return videoType;
        }

        public void setVideoType(String videoType) {
            this.videoType = videoType;
        }

     /*   @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(path);
            dest.writeString(pic);
            dest.writeString(text);
            dest.writeString(videoType);
            dest.writeString(changePath);
            dest.writeInt(multipleCategory);
        }*/
    }

    public static class indextag {
        private String tagId;
        private String tagName;

        public String getTagId() {
            return tagId;
        }

        public void setTagId(String tagId) {
            this.tagId = tagId;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }
    }

    public static class SnowVideoTypeListBean {
        /**
         * id : 333
         * pic : https://wjj.ys1.cnliveimg.com/769/img/2018/0129/882612314493918438.jpg
         * text : 上冰雪
         * videoType : snow
         * path : http://apiwjjtest.cnlive.com/Daren/snow/releaseSnow.action
         */

        private int id;
        private String pic;
        private String text;
        private String videoType;
        private String path;
        private String changePath;
        private int multipleCategory;
        public int getMultiplecategory() {
            return multipleCategory;
        }

        public void setMultiplecategory(int multipleCategory) {
            this.multipleCategory = multipleCategory;
        }
        public String getChangePath() {
            return changePath;
        }

        public void setChangePath(String changePath) {
            this.changePath = changePath;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getVideoType() {
            return videoType;
        }

        public void setVideoType(String videoType) {
            this.videoType = videoType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
