package com.lianren.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.lianren.android.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @package: com.lianren.android.widget
 * @user:xhkj
 * @date:2019/12/19
 * @description:
 **/
public class PortraitView extends CircleImageView {
    private static final String TAG = PortraitView.class.getSimpleName();

    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public static void setup(ImageView imageView, String author) {
        if (imageView != null && imageView instanceof PortraitView) {
            ((PortraitView) imageView).setup(author);
        }
    }

    public void setup(String url) {
        if (url == null)
            return;
        load(url);
    }

    private void load(String path) {
        final Context context = getContext();
        if (context == null)
            return;

        if (path == null) {
            path = "";
        } else {
            String pathTmp = path.toLowerCase();
            if (pathTmp.contains("www.oschina.net/img/portrait".toLowerCase())
                    || pathTmp.contains("secure.gravatar.com/avatar".toLowerCase())) {
                path = "";
            }
        }
        final String finalPath = path;
        Glide.with(context)
                .load(path)
                .asBitmap()
                .placeholder(R.color.black_alpha_48)
                .error(R.mipmap.widget_default_face)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        target.getSize(new SizeReadyCallback() {
                            @Override
                            public void onSizeReady(int width, int height) {
                                final String firstChar = (TextUtils.isEmpty(finalPath) ? "-" : finalPath.trim().substring(0, 1)).toUpperCase();
                                Bitmap bitmap = buildSrcFromName(firstChar, width, height);
                                setScaleType(ScaleType.CENTER_CROP);
                                setImageBitmap(bitmap);
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(this);
    }

    @SuppressWarnings("ResourceAsColor")
    private Bitmap buildSrcFromName(final String firstChar, int w, int h) {
        if (w == Target.SIZE_ORIGINAL || w <= 0)
            w = 80;
        if (h == Target.SIZE_ORIGINAL || h <= 0)
            h = 80;

        final int size = Math.max(Math.min(Math.min(w, h), 220), 64);
        final float fontSize = size * 0.4f;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(fontSize);
        paint.setTypeface(Typeface.SANS_SERIF);

        // check ASCII
        final int charNum = Character.getNumericValue(firstChar.charAt(0));
        if (charNum > 0 && charNum < 177) {
            Typeface typeface = getFont(getContext(), "Numans-Regular.otf");
            if (typeface != null)
                paint.setTypeface(typeface);
        }

        Rect rect = new Rect();
        paint.getTextBounds(firstChar, 0, 1, rect);
        int fontHeight = rect.height();

        int fontHalfH = fontHeight >> 1;
        int centerX = bitmap.getWidth() >> 1;
        int centerY = bitmap.getHeight() >> 1;

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(getBackgroundColor(firstChar));
        canvas.drawText(firstChar, centerX, centerY + fontHalfH, paint);

        return bitmap;
    }

    private static int getBackgroundColor(String firstChar) {
        int len = COLORS.length;
        int index = firstChar.charAt(0) - 64;
        int colorIndex = Math.abs(index) % len;
        return COLORS[colorIndex];
    }

    public static Typeface getFont(Context context, String fontFile) {
        String fontPath = "fonts/" + fontFile;

        try {
            return Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception var4) {
            return null;
        }
    }


    static final int[] COLORS = new int[]{
            0xFF1abc9c, 0xFF2ecc71, 0xFF3498db, 0xFF9b59b6, 0xFF34495e, 0xFF16a085, 0xFF27ae60, 0xFF2980b9, 0xFF8e44ad, 0xFF2c3e50,
            0xFFf1c40f, 0xFFe67e22, 0xFFe74c3c, 0xFFeca0f1, 0xFF95a5a6, 0xFFf39c12, 0xFFd35400, 0xFFc0392b, 0xFFbdc3c7, 0xFF7f8c8d
    };
}

