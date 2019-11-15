package com.wkq.media.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wkq.media.PickerConfig;
import com.wkq.media.R;
import com.wkq.media.adapter.FolderAdapter;
import com.wkq.media.adapter.MediaGridAdapter;
import com.wkq.media.adapter.SpacingDecoration;
import com.wkq.media.data.DataCallback;
import com.wkq.media.data.ImageLoader;
import com.wkq.media.data.ImagePickerOptions;
import com.wkq.media.data.MediaLoader;
import com.wkq.media.data.VideoLoader;
import com.wkq.media.entity.Folder;
import com.wkq.media.entity.Media;
import com.wkq.media.ui.camera.DiyCameraActivity;
import com.wkq.media.ui.crop.ImageCropActivity;
import com.wkq.media.utils.AndroidQUtil;
import com.wkq.media.utils.FileUtils;
import com.wkq.media.utils.ImagePickerComUtils;
import com.wkq.media.utils.PermissionChecker;
import com.wkq.media.utils.ScreenUtils;
import com.wkq.media.utils.StatusBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by dmcBig on 2017/6/9.
 */

public class PickerActivity extends AppCompatActivity implements DataCallback, View.OnClickListener {


        /**
         * sdk23获取sd卡拍照权限的requestCode
         */
        public static final int REQUEST_CODE_PERMISSION_CAMERA = 111;

        Intent argsIntent;
        RecyclerView recyclerView;
        Button done, category_btn, preview;
        MediaGridAdapter gridAdapter;
        ListPopupWindow mFolderPopupWindow;
        private FolderAdapter mFolderAdapter;
        int maxSelect;

        boolean showCamera;
        boolean isFrendCircle;

        File tempFile;

        boolean doneClickable;

        /**
         * 拍照存储路径
         */
        private String cachePath;
        /**
         * 剪辑视频存储路径
         */
        private String videoTrimPath;

        private int resultCode;
        public ImagePickerOptions mOptions;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            argsIntent = getIntent();

            setContentView(R.layout.main);
            StatusBarUtil.setColor(this, getResources().getColor(R.color.status_bar_color), 0);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            findViewById(R.id.btn_back).setOnClickListener(this);
            setTitleBar();
            done = (Button) findViewById(R.id.done);
            category_btn = (Button) findViewById(R.id.category_btn);
            preview = (Button) findViewById(R.id.preview);
            done.setOnClickListener(this);
            category_btn.setOnClickListener(this);
            preview.setOnClickListener(this);
            //get view end
            createAdapter();
            createFolderAdapter();
            getMediaData();
        }


        public void setTitleBar() {
            int type = argsIntent.getIntExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
            if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
                ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_title));
            } else if (type == PickerConfig.PICKER_IMAGE) {
                ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_image_title));
            } else if (type == PickerConfig.PICKER_VIDEO) {
                ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_video_title));
            }
        }

        void createAdapter() {
            //创建默认的线性LayoutManager
            GridLayoutManager mLayoutManager = new GridLayoutManager(this, PickerConfig.GridSpanCount);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new SpacingDecoration(PickerConfig.GridSpanCount, PickerConfig.GridSpace));
            //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
            recyclerView.setHasFixedSize(true);
            //创建并设置Adapter
            ArrayList<Media> medias = new ArrayList<>();
            ArrayList<Media> select = argsIntent.getParcelableArrayListExtra(PickerConfig.DEFAULT_SELECTED_LIST);

            mOptions = argsIntent.getParcelableExtra(PickerConfig.INTENT_KEY_OPTIONS);
            resultCode = argsIntent.getIntExtra(PickerConfig.RESULT_CODE, PickerConfig.DEFAULT_RESULT_CODE);
            maxSelect = mOptions.getMaxNum();
            long maxVideoSize = mOptions.getMaxVideoSize();
            long maxImageSize = mOptions.getMaxImageSize();
            boolean selectGift = mOptions.isSelectGift();
//        if (AndroidQUtil.isAndroidQ()) {
//            cachePath = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
//            mOptions.setCachePath(cachePath);
//        } else {
//            cachePath = mOptions.getCachePath();
//            if (TextUtils.isEmpty(cachePath)) {
//                cachePath = this.getExternalCacheDir().getAbsolutePath();
//            }
//        }
            cachePath = mOptions.getCachePath();
            videoTrimPath = mOptions.getVideoTrimPath() == null ? "" : mOptions.getVideoTrimPath();
            if (cachePath == null) {
                cachePath = (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ? this.getExternalFilesDir("") : Environment.getExternalStorageDirectory()).getPath() + File.separator + "JCamera";

            } else {
                cachePath = cachePath.substring(0, cachePath.length() - 1);
            }

            showCamera = mOptions.isNeedCamera();
            isFrendCircle = mOptions.isFriendCircle();

            gridAdapter = new MediaGridAdapter(medias, this, select, maxSelect, maxVideoSize, maxImageSize, selectGift, isFrendCircle, mOptions.isSinglePick(), mOptions.isShowTime(), mOptions.getMaxTime());
            gridAdapter.setShowCamera(showCamera);
            recyclerView.setAdapter(gridAdapter);

            if (mOptions.isSinglePick()) {
                done.setVisibility(View.GONE);
                preview.setVisibility(View.GONE);
            }
        }

        void createFolderAdapter() {
            ArrayList<Folder> folders = new ArrayList<>();
            mFolderAdapter = new FolderAdapter(folders, this);
            mFolderPopupWindow = new ListPopupWindow(this);
            mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            mFolderPopupWindow.setAdapter(mFolderAdapter);
            mFolderPopupWindow.setHeight((int) (ScreenUtils.getScreenHeight(this) * 0.6));
            mFolderPopupWindow.setAnchorView(findViewById(R.id.footer));
            mFolderPopupWindow.setModal(true);
            mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mFolderAdapter.setSelectIndex(position);
                    category_btn.setText(mFolderAdapter.getItem(position).name);
                    gridAdapter.updateAdapter(mFolderAdapter.getSelectMedias());
                    mFolderPopupWindow.dismiss();
                }
            });
        }

        @AfterPermissionGranted(119)
        void getMediaData() {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (mOptions.isNeedCrop() && mOptions.isSinglePick()) {
                    mOptions.setSelectMode(PickerConfig.PICKER_IMAGE);
                }
                int type = mOptions.getSelectMode();

                if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
                    getLoaderManager().initLoader(type, null, new MediaLoader(this, this));
                } else if (type == PickerConfig.PICKER_IMAGE) {
                    getLoaderManager().initLoader(type, null, new ImageLoader(this, this));
                } else if (type == PickerConfig.PICKER_VIDEO) {
                    getLoaderManager().initLoader(type, null, new VideoLoader(this, this));
                }
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.READ_EXTERNAL_STORAGE), 119, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        @Override
        public void onData(ArrayList<Folder> list) {
            if (mOptions.isNeedCamera() || (mOptions.isNeedCrop() && mOptions.isSinglePick())) {
                tempFile = new File(cachePath);
                if (!tempFile.exists()) {
                    tempFile.mkdirs();
                }
            }

            setView(list);
            category_btn.setText(list.get(0).name);
            mFolderAdapter.updateAdapter(list);
        }

        void setView(final ArrayList<Folder> list) {
            gridAdapter.updateAdapter(list.get(0).getMedias());
            setButtonText();
            gridAdapter.setOnItemClickListener(new MediaGridAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position, Media data, ArrayList<Media> selectMedias) {
                    if (gridAdapter.isShowCamera() && position == 0 && data == null) {
                        if (mOptions.needCrop && mOptions.isSinglePick()) {
                            showCropCameraAction();
                        } else {
                            showCameraAction();
                        }
                    } else {
                        if (data.path != null) {
                            File file = new File(data.path);
                            if (!file.exists()) {
                                Toast.makeText(PickerActivity.this, "请到设置-文件已损坏", Toast.LENGTH_SHORT).show();
                            } else if (data.path.endsWith("wbmp")) {
                                Toast.makeText(PickerActivity.this, "请到设置-网家家暂不支持此格式图片分享", Toast.LENGTH_SHORT).show();
                            } else {
                                PickerActivity.this.onItemClick(position);
                            }
                        }
                    }
                }

                @Override
                public void onItemCheckClick(View view, int position, Media data, ArrayList<Media> selectMedias) {
                    setButtonText();
                }
            });


        }

        private void onItemClick(int position) {
            if (mOptions.isNeedCrop() && mOptions.singlePick) {
                if (AndroidQUtil.isAndroidQ()) {
                    String uri = gridAdapter.getMedias().get(position).fileUri;
                    String filePath = AndroidQUtil.saveSignImageBox(this, "tempCrop.png", AndroidQUtil.getBitmapFromUri(this, Uri.parse(uri)));
                    if (!TextUtils.isEmpty(filePath) && FileUtils.isFileExists(filePath)) {
                        ImageCropActivity.start(this, filePath, mOptions);
                    } else {
                        Toast.makeText(PickerActivity.this, "文件路径异常", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    ImageCropActivity.start(this, gridAdapter.getMedias().get(position).path, mOptions);
                }

            } else {
                Intent intent = new Intent(PickerActivity.this, PreviewActivity.class);
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, mOptions.getMaxNum()));
                if (isFrendCircle) {
                    //根据点击的item的type传入media列表
//                intent.putParcelableArrayListExtra(PickerConfig.PRE_RAW_LIST, getPreviewList(gridAdapter.getMedias().get(position).mediaType));
                    intent.putExtra(PickerConfig.MEDIA_TYPE, gridAdapter.getMedias().get(position).mediaType);
                    intent.putExtra(PickerConfig.PRE_RAW_LIST_TYPE, mOptions.getSelectMode());
                    intent.putExtra(PickerConfig.PRE_RAW_LIST_TYPE_INDEX, mFolderAdapter.getSelectIndex());
                    intent.putExtra(PickerConfig.CURRENT_POSITION, getIndex(getPreviewList(gridAdapter.getMedias().get(position).mediaType), gridAdapter.getMedias().get(position).id));
                } else {
//                intent.putParcelableArrayListExtra(PickerConfig.PRE_RAW_LIST, gridAdapter.getMedias());
                    intent.putExtra(PickerConfig.PRE_RAW_LIST_TYPE, mOptions.getSelectMode());
                    intent.putExtra(PickerConfig.PRE_RAW_LIST_TYPE_INDEX, mFolderAdapter.getSelectIndex());
                    intent.putExtra(PickerConfig.CURRENT_POSITION, position);
                }
                intent.putExtra(PickerConfig.VIDEO_TRIM_PATH, videoTrimPath);

                intent.putParcelableArrayListExtra(PickerConfig.SELECTED_LIST, gridAdapter.getSelectMedias());
                intent.putExtra(PickerConfig.FROM_WHERE, PickerConfig.FROM_GRID_VIEW);
                intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, maxSelect);
                intent.putExtra(PickerConfig.FRIEND_CIRCLE, isFrendCircle);
                intent.putExtra(PickerConfig.SINGLE_PICK, mOptions.isSinglePick());
                intent.putExtra(PickerConfig.MAX_SELECT_VIDEO_SIZE, mOptions.getMaxVideoSize());
                intent.putExtra(PickerConfig.MAX_SELECT_IMAGE_SIZE, mOptions.getMaxImageSize());
                intent.putExtra(PickerConfig.MAX_TIME, mOptions.getMaxTime());
                intent.putExtra(PickerConfig.SELECT_GIF, mOptions.isSelectGift());
                startActivityForResult(intent, 200);
            }
        }

        private int getIndex(ArrayList<Media> arrayList, int mediaId) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (mediaId == arrayList.get(i).id) {
                    return arrayList.indexOf(arrayList.get(i));
                }
            }
            return -1;
        }

        @AfterPermissionGranted(120)
        private void showCropCameraAction() {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        tempFile = new File(cachePath + File.separator + "picture_" + System.currentTimeMillis() + ".jpg");
                        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", tempFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(cameraIntent, PickerConfig.REQUEST_TAKE_PHOTO);
                    } catch (SecurityException e) {
                        Log.e("DemoView", "Capture", e);
                        Toast.makeText(this, R.string.msg_no_camera_permission, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("DemoView", "Capture", e);
                        Toast.makeText(this, R.string.msg_error_camera, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
                }
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.CAMERA), 120, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        private void showCameraAction() {
            if (!ImagePickerComUtils.isSdExist()) {
                Toast.makeText(this, "SD卡不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean hasPermission = PermissionChecker.checkPermissions(this
                    , new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , REQUEST_CODE_PERMISSION_CAMERA, R.string.dialog_imagepicker_permission_camera_message);

            if (hasPermission) {
                doTakePhoto();
            }
        }


        private boolean hasPermission() {
            return PermissionChecker.checkPermissions(this
                    , new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , REQUEST_CODE_PERMISSION_CAMERA, R.string.dialog_imagepicker_permission_camera_message);
        }

        /**
         * 执行拍照方法
         */
        private void doTakePhoto() {
            DiyCameraActivity.start(this, cachePath, mOptions.getMaxTime(), resultCode);
        }

        void setButtonText() {
//        int max = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
            int max = mOptions.getMaxNum();
            done.setText(getString(R.string.done) + "(" + gridAdapter.getSelectMedias().size() + "/" + max + ")");
            preview.setText(getString(R.string.preview) + "(" + gridAdapter.getSelectMedias().size() + ")");
            chooseMedias(gridAdapter.getSelectMedias().size());
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_back) {
                finish();
            } else if (id == R.id.category_btn) {
                if (hasPermission())
                    if (mFolderPopupWindow.isShowing()) {
                        mFolderPopupWindow.dismiss();
                    } else {
                        mFolderPopupWindow.show();
                    }
            } else if (id == R.id.done) {
                done(gridAdapter.getSelectMedias());
            } else if (id == R.id.preview) {
                if (gridAdapter.getSelectMedias().size() <= 0) {
                    Toast.makeText(this, getString(R.string.select_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, PreviewActivity.class);
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, mOptions.getMaxNum()));
                intent.putParcelableArrayListExtra(PickerConfig.PRE_RAW_LIST, gridAdapter.getSelectMedias());
                intent.putParcelableArrayListExtra(PickerConfig.SELECTED_LIST, gridAdapter.getSelectMedias());
                intent.putExtra(PickerConfig.VIDEO_TRIM_PATH, videoTrimPath);
                intent.putExtra(PickerConfig.CURRENT_POSITION, 0);
                intent.putExtra(PickerConfig.FROM_WHERE, PickerConfig.FROM_PREVIEW_BUTTON);
                intent.putExtra(PickerConfig.FRIEND_CIRCLE, isFrendCircle);
                intent.putExtra(PickerConfig.RESULT_CODE, resultCode);
                intent.putExtra(PickerConfig.SINGLE_PICK, mOptions.isSinglePick());
                intent.putExtra(PickerConfig.MAX_SELECT_VIDEO_SIZE, mOptions.getMaxVideoSize());
                intent.putExtra(PickerConfig.MAX_SELECT_IMAGE_SIZE, mOptions.getMaxImageSize());
                intent.putExtra(PickerConfig.MAX_TIME, mOptions.getMaxTime());
                intent.putExtra(PickerConfig.SELECT_GIF, mOptions.isSelectGift());
                this.startActivityForResult(intent, 200);
            }
        }

        @SuppressLint("CheckResult")
        public void done(ArrayList<Media> selects) {
            if (selects != null && selects.size() > 0) {
                Observable.fromIterable(selects).concatMap(new Function<Media, ObservableSource<Media>>() {

                    private String path;

                    @Override
                    public ObservableSource<Media> apply(Media media) throws Exception {
                        if (media.mediaType == 3) {
                            if (!TextUtils.isEmpty(media.fileUri)&&!TextUtils.isEmpty(media.path)) {
                                path = AndroidQUtil.copyMp4(PickerActivity.this, media.fileUri, media.name);
                            } else {
                                path = media.path;
                            }

                        } else if (media.mediaType == 1) {
                            if (AndroidQUtil.isAndroidQ()) {
                                path = AndroidQUtil.copyAndroidQFile(PickerActivity.this, media.fileUri, media.name + "");
                            } else {
                                path = media.path;
                            }
                        }
                        media.path = path;
                        return Observable.just(media);
                    }
                }).map(new Function<Media, Media>() {
                    @Override
                    public Media apply(Media media) throws Exception {
                        return media;
                    }
                }).toList().flatMapObservable(new Function<List<Media>, ObservableSource<List<Media>>>() {
                    @Override
                    public ObservableSource<List<Media>> apply(List<Media> media) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<List<Media>>() {
                            @Override
                            public void subscribe(ObservableEmitter<List<Media>> emitter) throws Exception {
                                emitter.onNext(media);
                            }
                        });
                    }
                }).subscribe(new Observer<List<Media>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Media> media) {
                        Intent intent = new Intent();
                        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, selects);
                        setResult(resultCode, intent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(PickerActivity.this, "请选择媒体文件", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
//
            } else {
                Toast.makeText(this, "请选择媒体文件", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onDestroy() {
            Glide.get(this).clearMemory();
            super.onDestroy();
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PickerConfig.REQUEST_TAKE_PHOTO) {
                if (tempFile != null && mOptions != null)
                    ImageCropActivity.start(this, tempFile.getPath(), mOptions);
            }

            if (requestCode == PickerConfig.REQUEST_CODE_CROP && resultCode == PickerConfig.RESULT_CODE_CROP_OK && data != null) {
                ArrayList<Media> select = new ArrayList<>();
                File file = new File(data.getStringExtra(PickerConfig.INTENT_KEY_CROP_PATH));
                String uriString = data.getStringExtra(PickerConfig.INTENT_KEY_CROP_URI);
                Media media = new Media(file.getPath(), file.getName(), 0, 1, file.length(), 0, "", uriString);
                select.add(media);
                done(select);
            }
            if (requestCode == 200) {

                if (resultCode == 0) {
                    return;
                } else {
                    ArrayList<Media> selects = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                    if (selects == null) {
                        return;
                    }
                    if (resultCode == PickerConfig.RESULT_UPDATE_CODE) {
                        gridAdapter.updateSelectAdapter(selects);
                        setButtonText();
                    } else if (resultCode == this.resultCode) {
                        done(selects);
                    }

                }
            }
        }

        private ArrayList<Media> getPreviewList(int type) {
            ArrayList<Media> previewList = new ArrayList<>();
            for (int i = 0; i < gridAdapter.getMedias().size(); i++) {
                if (FileUtils.isFileExists(gridAdapter.getMedias().get(i).path) && gridAdapter.getMedias().get(i).mediaType == type) {
                    previewList.add(gridAdapter.getMedias().get(i));
                }
            }
            return previewList;
        }

        private void chooseMedias(int size) {
            if (size > 0) {
                doneClickable = true;
            } else {
                doneClickable = false;
            }

            if (doneClickable)
                done.setBackgroundResource(R.drawable.bg_done);
            else done.setBackgroundResource(R.drawable.bg_done_clock);
        }
}
