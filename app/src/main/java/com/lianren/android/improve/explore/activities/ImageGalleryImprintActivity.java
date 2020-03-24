package com.lianren.android.improve.explore.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lianren.android.R;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.base.activities.BackActivity;
import com.lianren.android.improve.bean.ImprintsBean;
import com.lianren.android.improve.bean.ImprintsDetailBean;
import com.lianren.android.improve.user.activities.ImprintsDetailActivity;
import com.lianren.android.util.pickimage.media.ImagePreviewView;
import com.lianren.android.widget.PageNumberPoint;
import com.lianren.android.widget.circle.ExpandTextView;

import net.oschina.common.utils.BitmapUtil;
import net.oschina.common.widget.Loading;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import butterknife.Bind;


/**
 * 图片预览Activity
 */
public class ImageGalleryImprintActivity extends BackActivity {
    public static final String KEY_IMAGE = "images";
    public static final String KEY_POSITION = "position";
    public static final String KEY_DETAIL = "detail";
    @Bind(R.id.page_number_point)
    PageNumberPoint pageNumberPoint;
    @Bind(R.id.vp_image)
    ViewPager vpImage;
    @Bind(R.id.tv_content)
    ExpandTextView tvTitle;
    private ImprintsBean mImageSources;
    private int mCurPosition;
    private ImageAdapter imageAdapter;
    private List<String> images;
    private ImprintsDetailBean imprintsDetailBean;

    public static void show(Context mContext, ImprintsBean images) {
        Intent intent = new Intent();
        intent.putExtra(KEY_IMAGE, images);
        intent.setClass(mContext, ImageGalleryImprintActivity.class);
        mContext.startActivity(intent);
    }

    public static void show(Context mContext, ImprintsDetailBean images, int mCurPosition) {
        Intent intent = new Intent();
        intent.putExtra(KEY_DETAIL, images);
        intent.putExtra(KEY_POSITION, mCurPosition);
        intent.setClass(mContext, ImageGalleryImprintActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setSwipeBackEnable(true);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_image_gallery_imprint;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        vpImage.addOnPageChangeListener(new ViewPagerOnPageChangeListener());

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        super.initData();
        mImageSources = (ImprintsBean) getIntent().getSerializableExtra(KEY_IMAGE);
        imprintsDetailBean = (ImprintsDetailBean) getIntent().getSerializableExtra(KEY_DETAIL);
        mCurPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        if (mImageSources != null) {
            if (mImageSources.content != null) {
                bindView(mImageSources.content.text, mImageSources.content.image_url);
            }
            return;
        }
        if (imprintsDetailBean != null) {
            if (imprintsDetailBean.content != null)
                bindView(imprintsDetailBean.content.text, imprintsDetailBean.content.image_url);
        }
    }

    private void bindView(String content, List<String> urls) {
        tvTitle.setText(content, false, new ExpandTextView.Callback() {
            @Override
            public void onExpand() {

            }

            @Override
            public void onCollapse() {

            }

            @Override
            public void onLoss() {

            }

            @Override
            public void onExpandClick() {
                tvTitle.setChanged(true);
            }

            @Override
            public void onCollapseClick() {
                tvTitle.setChanged(false);
            }
        });
        images = urls;
        if (urls != null && urls.size() > 0) {
            int len = urls.size();
            if (mCurPosition < 0 || mCurPosition >= len)
                mCurPosition = 0;
            pageNumberPoint.addDot(urls);
            vpImage.setAdapter(imageAdapter = new ImageAdapter());
            vpImage.setCurrentItem(mCurPosition);
        }
    }


    private Point mDisplayDimens;

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @SuppressWarnings("deprecation")
    private synchronized Point getDisplayDimens() {
        if (mDisplayDimens != null) {
            return mDisplayDimens;
        }
        Point displayDimens;
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            displayDimens = new Point();
            display.getSize(displayDimens);
        } else {
            displayDimens = new Point(display.getWidth(), display.getHeight());
        }
        mDisplayDimens = displayDimens;
        return mDisplayDimens;
    }


    interface DoOverrideSizeCallback {
        void onDone(int overrideW, int overrideH, boolean isTrue);
    }


    public class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int currPosition = 0; // 当前滑动到了哪一页
        boolean canJump = false;
        boolean canLeft = true;

        boolean isObjAnmatitor = true;
        boolean isObjAnmatitor2 = false;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == (images.size() - 1)) {
                if (positionOffset > 0.35) {
                    canJump = true;
                    if (imageAdapter.arrowImage != null && imageAdapter.slideText != null) {
                        if (isObjAnmatitor) {
                            isObjAnmatitor = false;
                            ObjectAnimator animator = ObjectAnimator.ofFloat(imageAdapter.arrowImage, "rotation", 0f, 180f);
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    imageAdapter.slideText.setText("松开跳到详情");
                                    isObjAnmatitor2 = true;
                                }
                            });
                            animator.setDuration(500).start();
                        }
                    }
                } else if (positionOffset <= 0.35 && positionOffset > 0) {
                    canJump = false;
                    if (imageAdapter.arrowImage != null && imageAdapter.slideText != null) {
                        if (isObjAnmatitor2) {
                            isObjAnmatitor2 = false;
                            ObjectAnimator animator = ObjectAnimator.ofFloat(imageAdapter.arrowImage, "rotation", 180f, 360f);
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    imageAdapter.slideText.setText("继续滑动跳到详情");
                                    isObjAnmatitor = true;
                                }
                            });
                            animator.setDuration(500).start();
                        }
                    }
                }
                canLeft = false;
            } else {
                canLeft = true;
            }
        }

        @Override
        public void onPageSelected(int position) {
            currPosition = position;
            if (currPosition < images.size())
                pageNumberPoint.setSelectPoint(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            if (currPosition == (images.size() - 1) && !canLeft) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {

                    if (canJump) {
                        if (mImageSources != null) {
                            ImprintsDetailActivity.show(ImageGalleryImprintActivity.this, mImageSources.user.id, mImageSources.id, "");
                            finish();
                        } else if (imprintsDetailBean != null) {
                            ImprintsDetailActivity.show(ImageGalleryImprintActivity.this, imprintsDetailBean.user.id, imprintsDetailBean.id, "");
                            finish();
                        }
                    }

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            // 在handler里调用setCurrentItem才有效
                            vpImage.setCurrentItem(images.size() - 1);
                        }
                    });

                }
            }
        }
    }


    public class ImageAdapter extends PagerAdapter {
        private View.OnClickListener mFinishClickListener;
        private TextView slideText;
        private ImageView arrowImage;

        @Override
        public int getCount() {
            return images.size() + 1; // 这里要加1，是因为多了一个隐藏的view
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position < images.size()) {

                View view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.lay_gallery_page_item_contener, container, false);
                ImagePreviewView previewView = view.findViewById(R.id.iv_preview);
                Loading loading = view.findViewById(R.id.loading);
                ImageView defaultView = view.findViewById(R.id.iv_default);
                loadImage(position, images.get(position), previewView, defaultView, loading);
                previewView.setOnClickListener(getListener());
                container.addView(view);
                return view;
            } else {
                View hintView = LayoutInflater.from(container.getContext()).inflate(R.layout.more_view, container, false);

                slideText = (TextView) hintView.findViewById(R.id.tv);
                arrowImage = (ImageView) hintView.findViewById(R.id.iv);
                container.addView(hintView);
                return hintView;
            }
        }

        private View.OnClickListener getListener() {
            if (mFinishClickListener == null) {
                mFinishClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                };
            }
            return mFinishClickListener;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private <T> void loadImage(final int pos, final T urlOrPath,
                                   final ImageView previewView,
                                   final ImageView defaultView,
                                   final Loading loading) {

            loadImageDoDownAndGetOverrideSize(urlOrPath, new DoOverrideSizeCallback() {
                @Override
                public void onDone(int overrideW, int overrideH, boolean isTrue) {
                    DrawableRequestBuilder builder = getImageLoader()
                            .load(urlOrPath)
                            .listener(new RequestListener<T, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e,
                                                           T model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFirstResource) {
                                    if (e != null)
                                        e.printStackTrace();
                                    loading.stop();
                                    loading.setVisibility(View.GONE);
                                    defaultView.setVisibility(View.VISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource,
                                                               T model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache,
                                                               boolean isFirstResource) {
                                    loading.stop();
                                    loading.setVisibility(View.GONE);
                                    return false;
                                }
                            }).diskCacheStrategy(DiskCacheStrategy.SOURCE);

                    // If download or get option error we not set override
                    if (isTrue && overrideW > 0 && overrideH > 0) {
                        builder = builder.override(overrideW, overrideH).fitCenter();
                    }

                    builder.into(previewView);
                }
            });
        }

        private <T> void loadImageDoDownAndGetOverrideSize(final T urlOrPath, final DoOverrideSizeCallback callback) {
            // In this save max image size is source
            final Future<File> future = getImageLoader().load(urlOrPath)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        File sourceFile = future.get();

                        BitmapFactory.Options options = BitmapUtil.createOptions();
                        // First decode with inJustDecodeBounds=true to checkShare dimensions
                        options.inJustDecodeBounds = true;
                        // First decode with inJustDecodeBounds=true to checkShare dimensions
                        BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), options);

                        int width = options.outWidth;
                        int height = options.outHeight;
                        BitmapUtil.resetOptions(options);

                        if (width > 0 && height > 0) {
                            // Get Screen
                            final Point point = getDisplayDimens();

                            // This max size
                            final int maxLen = Math.min(Math.min(point.y, point.x) * 5, 1366 * 3);

                            // Init override size
                            final int overrideW, overrideH;

                            if ((width / (float) height) > (point.x / (float) point.y)) {
                                overrideH = Math.min(height, point.y);
                                overrideW = Math.min(width, maxLen);
                            } else {
                                overrideW = Math.min(width, point.x);
                                overrideH = Math.min(height, maxLen);
                            }

                            // Call back on main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(overrideW, overrideH, true);
                                }
                            });
                        } else {
                            // Call back on main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(0, 0, false);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        // Call back on main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onDone(0, 0, false);
                            }
                        });
                    }
                }
            });
        }
    }
}
