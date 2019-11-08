package com.wkq.media.ui;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wkq.media.PickerConfig;
import com.wkq.media.R;
import com.wkq.media.adapter.PreviewBottomAdapter;
import com.wkq.media.data.ImageLoader;
import com.wkq.media.data.MediaLoader;
import com.wkq.media.data.VideoLoader;
import com.wkq.media.entity.Media;
import com.wkq.media.utils.AndroidQUtil;
import com.wkq.media.utils.DoublePressed;
import com.wkq.media.utils.FileTypeUtil;
import com.wkq.media.utils.FileUtils;
import com.wkq.media.utils.StatusBarUtil;
import com.wkq.media.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.wkq.media.PickerConfig.DEFAULT_MAX_TIME;
import static com.wkq.media.PickerConfig.DEFAULT_SELECT_GIF;


/**
 * Created by dmcBig on 2017/8/9.
 */

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    public static final int MAX_TRIM_DURATION = 10 * 60 * 1000;

    Button done;
    LinearLayout check_layout;
    RelativeLayout bottomEditLayout;
    ImageView check_image;
    ViewPager viewpager;
    TextView bar_title;
    TextView edit_btn;
    TextView edit_hint;
    TextView edit_title;
    int preRawListType, preRawListTypeIndex;
    ArrayList<Media> preRawList, preSelectedList, selects;
    //跳转到预览页面的position
    int currentPosition;
    //从哪跳转过来的
    String fromWhere;
    String videoTrimPath;//剪辑视频存储路径

    int maxCount;

    boolean doneClickable;

    RelativeLayout bottomLayout, topLayout;
    RecyclerView bottomRecyclerView;
    PreviewBottomAdapter bottomAdapter;

    ArrayList<Media> deleteList;

    int resultCode;

    int mediaType;

    boolean isFriendCircle;

    boolean isSinglePick;
    long maxDataSize;
    long maxImageSize;
    int maxTime;
    boolean selectGift;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.status_bar_color),0);
        setContentView(R.layout.preview_main);
        findViewById(R.id.btn_back).setOnClickListener(this);
        check_image = (ImageView) findViewById(R.id.check_image);
        check_layout = (LinearLayout) findViewById(R.id.check_layout);
        check_layout.setOnClickListener(this);
        bar_title = (TextView) findViewById(R.id.bar_title);
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(this);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        edit_btn = findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(this);
        edit_hint = findViewById(R.id.edit_hint);
        edit_title = findViewById(R.id.edit_title);

        bottomRecyclerView = findViewById(R.id.bottom_recycler_view);
        bottomLayout = findViewById(R.id.bottom);
        bottomEditLayout = findViewById(R.id.edit_bottom);

        topLayout = findViewById(R.id.top);

        mediaType = getIntent().getIntExtra(PickerConfig.MEDIA_TYPE, 0);
        preRawListType = getIntent().getIntExtra(PickerConfig.PRE_RAW_LIST_TYPE, PickerConfig.PICKER_IMAGE);
        preRawListTypeIndex = getIntent().getIntExtra(PickerConfig.PRE_RAW_LIST_TYPE_INDEX, 0);
        preRawList = getIntent().getParcelableArrayListExtra(PickerConfig.PRE_RAW_LIST);
        preSelectedList = getIntent().getParcelableArrayListExtra(PickerConfig.SELECTED_LIST);
        currentPosition = getIntent().getIntExtra(PickerConfig.CURRENT_POSITION, 0);
        fromWhere = getIntent().getStringExtra(PickerConfig.FROM_WHERE);
        resultCode = getIntent().getIntExtra(PickerConfig.RESULT_CODE, 0);
        maxCount = getIntent().getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        isFriendCircle = getIntent().getBooleanExtra(PickerConfig.FRIEND_CIRCLE, false);
        isSinglePick = getIntent().getBooleanExtra(PickerConfig.SINGLE_PICK, false);
        videoTrimPath = getIntent().getStringExtra(PickerConfig.VIDEO_TRIM_PATH) == null ? "" : getIntent().getStringExtra(PickerConfig.VIDEO_TRIM_PATH);

        maxDataSize = getIntent().getLongExtra(PickerConfig.MAX_SELECT_VIDEO_SIZE, PickerConfig.DEFAULT_MAX_SELECT_SIZE);
        maxImageSize = getIntent().getLongExtra(PickerConfig.MAX_SELECT_IMAGE_SIZE, PickerConfig.DEFAULT_MAX_SELECT_SIZE);
        maxTime = getIntent().getIntExtra(PickerConfig.MAX_TIME, DEFAULT_MAX_TIME);
        selectGift = getIntent().getBooleanExtra(PickerConfig.SELECT_GIF, DEFAULT_SELECT_GIF);

        if (preRawList == null || preRawList.size() == 0) {
            initMediaData();
        } else {
            setView(preRawList);
        }
    }

    @AfterPermissionGranted(119)
    void initMediaData() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (preRawListType == PickerConfig.PICKER_IMAGE_VIDEO) {
                getLoaderManager().initLoader(preRawListType, null, new MediaLoader(this, list -> {
                    if (mediaType != 0) {
                        setView(getPreviewList(list.get(preRawListTypeIndex).getMedias(), mediaType));
                    } else {
                        setView(list.get(preRawListTypeIndex).getMedias());
                    }
                }));
            } else if (preRawListType == PickerConfig.PICKER_IMAGE) {
                getLoaderManager().initLoader(preRawListType, null, new ImageLoader(this, list -> setView(list.get(preRawListTypeIndex).getMedias())));
            } else if (preRawListType == PickerConfig.PICKER_VIDEO) {
                getLoaderManager().initLoader(preRawListType, null, new VideoLoader(this, list -> setView(list.get(preRawListTypeIndex).getMedias())));
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.READ_EXTERNAL_STORAGE), 119, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    void setView(ArrayList<Media> default_list) {
        preRawList = default_list;
        mediaType = default_list.get(0).mediaType;
        check_layout.setVisibility(View.VISIBLE);

        if ((mediaType == 3 && isFriendCircle) || isSinglePick) {
            check_layout.setVisibility(View.GONE);
        }

        showTrimBottomLayout(default_list.get(currentPosition));

        selects = new ArrayList<>();
        selects.addAll(preSelectedList);

        deleteList = new ArrayList<>();
        deleteList.addAll(preSelectedList);

        setDoneView(selects.size());
        bar_title.setText(1 + "/" + preRawList.size());
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

//        for (Media media : default_list) {
//            fragmentArrayList.add(PreviewFragment.newInstance(media));
//        }
        AdapterFragment adapterFragment = new AdapterFragment(getSupportFragmentManager(), default_list);
        viewpager.setAdapter(adapterFragment);
        viewpager.addOnPageChangeListener(this);
        viewpager.setOffscreenPageLimit(3);
        viewpager.setCurrentItem(currentPosition);

        if (isSelect(default_list.get(currentPosition), selects) >= 0) {
            check_image.setImageResource(R.drawable.xc_xuanzhong);
        }

        initBottomRecyclerViewData();
    }

    void initBottomRecyclerViewData() {
        setBottomLayoutVisible();
        bottomRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bottomAdapter = new PreviewBottomAdapter(this);
        bottomAdapter.setOnItemClickListener(new PreviewBottomAdapter.OnBottomItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Media data, ArrayList<Media> selectMedias) {
                //点击小图，viewpager跳转到小图对应的大图
                viewpager.setCurrentItem(getIndex(preRawList, data.id), false);
            }
        });
        bottomAdapter.addItems(selects);
        bottomRecyclerView.setAdapter(bottomAdapter);
    }

    void setBottomLayoutVisible() {
        if (selects.size() > 0 && selects.get(0).mediaType != 3) {
            bottomLayout.setVisibility(View.VISIBLE);
        } else {
            bottomLayout.setVisibility(View.GONE);
        }
    }

    void setDoneView(int num1) {
        if (isFriendCircle) {
            if (mediaType == 1) {
                done.setText(getString(R.string.done) + "(" + num1 + "/" + maxCount + ")");
                chooseMedias(num1);
            } else {
                done.setText(getString(R.string.done));
                chooseMedias(1);
            }
        } else {
            done.setText(getString(R.string.done) + "(" + num1 + "/" + maxCount + ")");
            chooseMedias(num1);
        }

        if (isSinglePick) {
            done.setText(getString(R.string.done));
            chooseMedias(1);
        }
     //   chooseMedias(num1);

    }

    //获取mediaId对应的位置
    private int getIndex(ArrayList<Media> arrayList, int mediaId) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (mediaId == arrayList.get(i).id) {
                return arrayList.indexOf(arrayList.get(i));
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            done(selects, PickerConfig.RESULT_UPDATE_CODE);
        } else if (id == R.id.done) {
            Media currentMedia = preRawList.get(viewpager.getCurrentItem());
//            if (currentMedia.mediaType == 3 && currentMedia.size > maxDataSize) {
//                Toast.makeText(this, getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxDataSize)), Toast.LENGTH_LONG).show();
//                return;
//            }
            if (currentMedia.mediaType == 1 && currentMedia.size > maxImageSize) {
                Toast.makeText(this, getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxImageSize)), Toast.LENGTH_LONG).show();
                return;
            }
            if (!selectGift && FileTypeUtil.getFileType(currentMedia.path).equals("gif")) {
                if(DoublePressed.onDoublePressed())return;
                Toast.makeText(this, getString(R.string.msg_gif_limit), Toast.LENGTH_LONG).show();
                return;
            }
            if (currentMedia.duration > maxTime) {
                Toast.makeText(this, getString(R.string.msg_time_limit) + (StringUtils.gennerMinSec(maxTime / 1000)), Toast.LENGTH_LONG).show();
                return;
            }
            if ((mediaType == 3 && isFriendCircle) || isSinglePick) {
//                Media currentMedia = preRawList.get(viewpager.getCurrentItem());
//                if (currentMedia.mediaType == 3 && currentMedia.size > maxDataSize) {
//                    Toast.makeText(this, getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxDataSize)), Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if (currentMedia.mediaType == 1 && currentMedia.size > maxImageSize) {
//                    Toast.makeText(this, getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxImageSize)), Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if (currentMedia.duration > maxTime) {
//                    Toast.makeText(this, getString(R.string.msg_time_limit) + (StringUtils.gennerTimeSecond(maxTime / 1000)), Toast.LENGTH_LONG).show();
//                    return;
//                }
                if (selects.size() == 0) {
                    selects.add(currentMedia);
                }

            }
            if (doneClickable) {
                done(selects, resultCode);
            } else {
                Toast.makeText(this, "请选择媒体文件", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.check_layout) {
            Media media = preRawList.get(viewpager.getCurrentItem());
            int select = isSelect(media, selects);
            if (select < 0) {
                if (selects.size() > maxCount - 1) {
                    Toast.makeText(this, getString(R.string.msg_amount_limit), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (media.mediaType == 3 && media.size > maxDataSize) {
                    Toast.makeText(this, getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxDataSize)), Toast.LENGTH_LONG).show();
                    return;
                }

                if (media.mediaType == 1 && media.size > maxImageSize) {
                    Toast.makeText(this, getString(R.string.msg_size_limit) + (FileUtils.fileSize(maxImageSize)), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!selectGift && FileTypeUtil.getFileType(media.path).equals("gif")) {
                    if(DoublePressed.onDoublePressed())return;
                    Toast.makeText(this, getString(R.string.msg_gif_limit), Toast.LENGTH_LONG).show();
                    return;
                }

                if (media.duration > maxTime) {
                    Toast.makeText(this, getString(R.string.msg_time_limit) + (StringUtils.gennerMinSec((int) (maxTime / 1000))), Toast.LENGTH_LONG).show();
                    return;
                }
                check_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.xc_xuanzhong));
                if (fromWhere.equals(PickerConfig.FROM_GRID_VIEW)) {
                    selects.add(media);
                    bottomAdapter.addItem(media);
                    bottomRecyclerView.smoothScrollToPosition(selects.size());
                    bottomAdapter.setItemBackground(getIndex(selects, media.id));
                } else {
                    selects.add(media);
                    bottomAdapter.setItemMasking(getIndex(deleteList, media.id), false);
                }

            } else {
                check_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.xc_weixuan));
                if (fromWhere.equals(PickerConfig.FROM_GRID_VIEW)) {
                    selects.remove(select);
                    bottomAdapter.removeItem(select);
                    bottomAdapter.setItemBackground(getIndex(selects, preRawList.get(viewpager.getCurrentItem()).id));
                } else {
                    selects.remove(select);
                    bottomAdapter.setItemMasking(getIndex(deleteList, preRawList.get(viewpager.getCurrentItem()).id), true);
                }

            }
            setBottomLayoutVisible();
            setDoneView(selects.size());
        } else if (id == R.id.edit_btn) {
            Media media = preRawList.get(viewpager.getCurrentItem());
            if (!new File(media.path).exists()) {
                Toast.makeText(PreviewActivity.this, "文件已损坏", Toast.LENGTH_SHORT).show();
                return;
            }
            if (AndroidQUtil.isAndroidQ()){
                String path= AndroidQUtil.copyMp4(this,media.fileUri);
                EditVideoActivity.startEditActivity(this,media.fileUri.toString(), path, maxTime, videoTrimPath, resultCode);
            }else {
                EditVideoActivity.startEditActivity(this,"", media.path, maxTime, videoTrimPath, resultCode);
            }
        }
    }

    /**
     * @param media
     * @return 大于等于0 就是表示以选择，返回的是在selectMedias中的下标
     */
    public int isSelect(Media media, ArrayList<Media> list) {
        int is = -1;
        if (list.size() <= 0) {
            return is;
        }
        for (int i = 0; i < list.size(); i++) {
            Media m = list.get(i);
            if (m.path.equals(media.path)) {
                is = i;
                break;
            }
        }
        return is;
    }

    public void done(ArrayList<Media> list, int code) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, list);
        setResult(code, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        done(selects, PickerConfig.RESULT_UPDATE_CODE);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {

            if (resultCode == 0) {
                return;
            } else {
                ArrayList<Media> selects = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
                if (selects == null) {
                    return;
                }
                if (resultCode == this.resultCode) {
                    done(selects, resultCode);
                }

            }
        }
    }

    public class AdapterFragment extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments;

        private ArrayList<Media> mediaList;

        public AdapterFragment(FragmentManager fm, ArrayList<Media> mFragments) {
            super(fm);
            this.mediaList = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
//            return mFragments.get(position);
            return PreviewFragment.newInstance(mediaList.get(position));
        }

        @Override
        public int getCount() {
            return mediaList.size();
        }

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (fromWhere.equals(PickerConfig.FROM_PREVIEW_BUTTON)) {
            bottomAdapter.setItemBackground(getIndex(deleteList, preRawList.get(position).id));
        } else {
            bottomAdapter.setItemBackground(getIndex(selects, preRawList.get(position).id));
        }
    }

    @Override
    public void onPageSelected(int position) {

        showTrimBottomLayout(preRawList.get(position));

        bar_title.setText((position + 1) + "/" + preRawList.size());
        check_image.setImageDrawable(isSelect(preRawList.get(position), selects) < 0 ? ContextCompat.getDrawable(this, R.drawable.xc_weixuan) : ContextCompat.getDrawable(this, R.drawable.xc_xuanzhong));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private ArrayList<Media> getPreviewList(ArrayList<Media> medias, int type) {
        ArrayList<Media> previewList = new ArrayList<>();
        for (int i = 0; i < medias.size(); i++) {
            if (medias.get(i).mediaType == type) {
                previewList.add(medias.get(i));
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

        setDoneBackground(doneClickable);
    }

    private void setDoneBackground(boolean doneClickable) {
        if (doneClickable)
            done.setBackgroundResource(R.drawable.bg_done);
        else done.setBackgroundResource(R.drawable.bg_done_clock);
    }

    private void showTrimBottomLayout(Media media) {
        if ((media.mediaType == 3 && isFriendCircle)) {
            if (media.duration > maxTime) {
                setDoneBackground(false);
                bottomEditLayout.setVisibility(View.VISIBLE);
                if (media.duration > MAX_TRIM_DURATION) {
                    edit_btn.setVisibility(View.GONE);
                    edit_hint.setText(getResources().getString(R.string.edit_can_not_trim_hint));
                    edit_title.setText(getResources().getString(R.string.edit_can_not_trim_title));
                } else {
                    edit_btn.setVisibility(View.VISIBLE);
                    edit_title.setText(getResources().getString(R.string.edit_trim_title));
                    edit_hint.setText(String.format("只能分享%s内的视频,需进行编辑", StringUtils.gennerMinSec(maxTime / 1000)));
                }
            } else {
                setDoneBackground(true);
                bottomEditLayout.setVisibility(View.GONE);
            }
        }
    }
}
