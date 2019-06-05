package com.wkq.media;

/**
 * Created by dmcBig on 2017/6/9.
 */

public class PickerConfig {
    public static final String LOG_TAG = "MediaPicker";

    /**
     * 最大图片选择次数，int类型，默认40
     */
    public static final String MAX_SELECT_COUNT = "max_select_count";

    public static final int DEFAULT_SELECTED_MAX_COUNT = 40;

    /**
     * 是否需要相机
     */
    public static final String NEED_CAMERA = "need_camera";

    /**
     * 最大文件大小，int类型，默认180m
     */
    public static final String MAX_SELECT_VIDEO_SIZE = "max_select_video_size";

    public static final String MAX_SELECT_IMAGE_SIZE = "max_select_image_size";

    public static final long DEFAULT_MAX_SELECT_SIZE = 188743680L;

    /**
     * 视频文件最大时长
     */
    public static final String MAX_TIME = "max_time";

    public static final int DEFAULT_MAX_TIME = 60 * 1000;

    /**
     * 拍照存储位置
     */
    public static final String CACHE_PATH = "cache_path";

    /**
     * 是否是发朋友圈
     */
    public static final String FRIEND_CIRCLE = "friend_circle";

    public static final long DEFAULT_SELECTED_MAX_SIZE = 188743680;

    /**
     * 选择Gif图像开关
     */

    public static final String SELECT_GIF = "select_gif";

    public static final boolean DEFAULT_SELECT_GIF = true;
    /**
     * 界面跳转options的键值
     */
    public static final String INTENT_KEY_OPTIONS = "options";

    /**
     * 图片选择模式，默认选视频和图片
     */
    public static final String SELECT_MODE = "select_mode";

    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";

    /**
     * 选择的圈子路径
     */
    public static final String CIRCLE_RESULT = "circle_result";

    /**
     * 默认选择集
     */
    public static final String DEFAULT_SELECTED_LIST = "default_list";
    /**
     * 预览集
     */
    public static final String PRE_RAW_LIST = "pre_raw_List";
    /**
     * 预览类型
     */
    public static final String PRE_RAW_LIST_TYPE = "pre_raw_List_type";
    /**
     * 预览類型文件夾索引
     */
    public static final String PRE_RAW_LIST_TYPE_INDEX = "pre_raw_List_type_index";

    /**
     * 当前文件的媒体类型image or video
     */
    public static final String MEDIA_TYPE = "mediaType";

    /**
     * 已选择的媒体预览集
     */
    public static final String SELECTED_LIST = "selected_list";
    /**
     * 跳转到预览页面指定的viewpager页面
     */
    public static final String CURRENT_POSITION = "current_position";

    /**
     * 传递待裁剪图片路径的key
     */
    public static final String INTENT_KEY_ORIGIN_PATH = "originPath";

    /**
     * 裁剪后图片路径的key
     */
    public static final String INTENT_KEY_CROP_PATH = "cropPath";

    public static final String SINGLE_PICK = "single_pick";
    /**
     * 从哪个页面跳转到预览页面的 FROM_GRID_VIEW-->点击gridView的item   FROM_PREVIEW_BUTTON-->点击预览按钮
     */
    public static final String FROM_WHERE = "from_where";
    public static final String FROM_GRID_VIEW = "from_grid_view";
    public static final String FROM_PREVIEW_BUTTON = "from_preview_button";
    public static final String RESULT_CODE = "result_code";
    public static final String OLD_LIST = "old_list";
    public static final String CIRCLElIST = "circlrlist";
    public static final String VIDEO_TRIM_PATH = "video_trim_path";
    public static final String CAMERA_SELECT_MODE = "select_mode";
    public static final String ONLY_CAPTURE_SELECT = "PHOTO_MODE";
    public static final String BOTH_SELECT = "BOTH_MODE";
    public static final int DEFAULT_RESULT_CODE = 19901026;
    public static final int RESULT_UPDATE_CODE = 1990;
    public static final int REQUEST_TAKE_PHOTO = 223;
    public static final int PICKER_IMAGE = 100;
    public static final int PICKER_VIDEO = 102;
    public static final int PICKER_IMAGE_VIDEO = 101;
    public static int GridSpanCount = 3;
    public static int GridSpace = 4;

    /**
     * 裁剪请求码
     */
    public static final int REQUEST_CODE_CROP = 113;

    /**
     * 裁剪结果码
     */
    public static final int RESULT_CODE_CROP_OK = 116;

    /**
     * 裁剪后图片名字前缀
     */
    public static final String CROP_NAME_PREFIX = "CROP_";

    /**
     * 图片文件名后缀
     */
    public static final String IMG_NAME_POSTFIX = ".jpg";
}
