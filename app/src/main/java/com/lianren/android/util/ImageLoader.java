package com.lianren.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lianren.android.R;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Glide 图片加载辅助类
 * 适配圆形图片加载情况
 */

public class ImageLoader {
    private ImageLoader() {
    }

    public static void loadImage(RequestManager loader, ImageView view, String url) {
        loadImage(loader, view, url, R.mipmap.ic_squre_placeholder);
    }

    public static void loadImage(RequestManager loader, ImageView view, String url, int placeholder) {
        loadImage(loader, view, url, placeholder, placeholder);
    }

    public static void loadImage(RequestManager loader, ImageView view, String url, int placeholder, int error) {
        boolean isCenterCrop = false;
        if (view instanceof CircleImageView)
            isCenterCrop = true;
        loadImage(loader, view, url, placeholder, error, isCenterCrop);
    }

    public static void loadImage(RequestManager loader, ImageView view, String url, int placeholder, int error, boolean isCenterCrop) {
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(placeholder);
        } else {
            if (view instanceof CircleImageView) {
                BitmapRequestBuilder builder = loader.load(url).asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(placeholder)
                        .error(error);
                if (isCenterCrop)
                    builder.centerCrop();
                builder.into(
                        new BitmapImageViewTarget(view) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(view.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                view.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            } else {
                DrawableRequestBuilder builder = loader.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder).error(error);
                if (isCenterCrop)
                    builder.centerCrop();
                builder.into(view);
            }
        }
    }

    /**
     * 高斯模糊
     */
    public static void loadImage(RequestManager loader, ImageView view, String url, Context mContext) {
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(R.mipmap.ic_squre_placeholder);
        } else {
            BitmapRequestBuilder builder =
                    loader.load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).
                            placeholder(R.mipmap.ic_squre_placeholder).error(R.mipmap.ic_squre_placeholder)
                            .transform(new BlurTransformation(mContext, 23, 8));
            builder.into(new BitmapImageViewTarget(view) {
                @Override
                protected void setResource(Bitmap resource) {
                    view.setImageBitmap(resource);
                    //获取原图的宽高
                    int width = resource.getWidth();
                    int height = resource.getHeight();
                    //获取imageView的宽
                    int imageViewWidth = view.getWidth();
                    //计算缩放比例
                    float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);
                    //计算图片等比例放大后的高
//                    int imageViewHeight = (int) (height * sy);
                    int imageViewHeight = imageViewWidth;
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = imageViewHeight;
                    view.setLayoutParams(params);
                }
            });
        }
    }

    /**
     * 自动适应加载图片
     */
    public static void loadAutoImage(RequestManager loader, ImageView view, String url) {
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(R.mipmap.ic_squre_placeholder);
        } else {
            BitmapRequestBuilder builder =
                    loader.load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).
                            placeholder(R.mipmap.ic_squre_placeholder).error(R.mipmap.ic_squre_placeholder);
            builder.into(new BitmapImageViewTarget(view) {
                @Override
                protected void setResource(Bitmap resource) {
                    view.setImageBitmap(resource);
                    int width = resource.getWidth();
                    int height = resource.getHeight();
                    int imageViewWidth = view.getWidth();
                    float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);
                    int imageViewHeight = (int) (height * sy);
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = imageViewHeight;
                    view.setLayoutParams(params);
                }
            });
        }
    }

    public static void loadAutoHeight(RequestManager loader, ImageView view, String url) {
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(R.mipmap.ic_squre_placeholder);
        } else {
            BitmapRequestBuilder builder =
                    loader.load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).
                            placeholder(R.mipmap.ic_squre_placeholder).error(R.mipmap.ic_squre_placeholder);
            builder.into(new BitmapImageViewTarget(view) {
                @Override
                protected void setResource(Bitmap resource) {
                    view.setImageBitmap(resource);
                    int width = resource.getWidth();
                    int height = resource.getHeight();
                    int imageHeight = view.getHeight();
                    float sy = (float) (imageHeight * 0.1) / (float) (height * 0.1);
                    int imageViewWidth = (int) (width * sy);
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = imageViewWidth;
                    view.setLayoutParams(params);
                }
            });
        }
    }
}
