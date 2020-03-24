package com.lianren.android.widget.circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianren.android.R;

/**
 * @package: com.lianren.android.widget.circle
 * @user:xhkj
 * @date:2020/1/14
 * @description:
 **/
public class ExpandView extends LinearLayout {
    public static final int DEFAULT_MAX_LINES = 10;
    private TextView contentText;
    private ImageView textPlus;

    private int showLines;

    private ExpandStatusListener expandStatusListener;
    private OnItemClickListener onItemClickListener;
    private boolean isExpand;

    public ExpandView(Context context) {
        super(context);
        initView();
    }

    public ExpandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public ExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_magic_text, this);
        contentText = findViewById(R.id.contentText);
        if (showLines > 0) {
            contentText.setMaxLines(showLines);
        }

        textPlus = findViewById(R.id.textPlus);
        contentText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick();
                }
            }
        });
        textPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpand) {
                    contentText.setMaxLines(Integer.MAX_VALUE);
                    textPlus.setImageResource(R.mipmap.ic_expand);
                    setExpand(false);
                } else {
                    contentText.setMaxLines(showLines);
                    textPlus.setImageResource(R.mipmap.ic_collsepand);
                    setExpand(true);
                }
                //通知外部状态已变更
                if (expandStatusListener != null) {
                    expandStatusListener.statusChange(isExpand());
                }
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandTextView, 0, 0);
        try {
            showLines = typedArray.getInt(R.styleable.ExpandTextView_showLines, DEFAULT_MAX_LINES);
        } finally {
            typedArray.recycle();
        }
    }

    public void setText(final CharSequence content, int color) {
        contentText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                // 避免重复监听
                contentText.getViewTreeObserver().removeOnPreDrawListener(this);

                int linCount = contentText.getLineCount();
                if (linCount > showLines) {

                    if (isExpand) {
                        contentText.setMaxLines(Integer.MAX_VALUE);
                        textPlus.setImageResource(R.mipmap.ic_expand);
                    } else {
                        contentText.setMaxLines(showLines);
                        textPlus.setImageResource(R.mipmap.ic_collsepand);
                    }
                    textPlus.setVisibility(View.VISIBLE);
                } else {
                    textPlus.setVisibility(View.GONE);
                }

                //Log.d("onPreDraw", "onPreDraw...");
                //Log.d("onPreDraw", linCount + "");
                return true;
            }


        });
        contentText.setText(content);
        contentText.setTextColor(getResources().getColor(color));
        contentText.setMovementMethod(new CircleMovementMethod(getResources().getColor(color)));
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public boolean isExpand() {
        return this.isExpand;
    }

    public void setExpandStatusListener(ExpandStatusListener listener) {
        this.expandStatusListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onClick();
    }

    public static interface ExpandStatusListener {

        void statusChange(boolean isExpand);
    }

}
