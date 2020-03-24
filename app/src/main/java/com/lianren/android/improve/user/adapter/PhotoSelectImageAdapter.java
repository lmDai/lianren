package com.lianren.android.improve.user.adapter;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lianren.android.R;
import com.lianren.android.improve.bean.QiNiuBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.util.pickimage.media.ImageGalleryActivity;
import com.lianren.android.widget.RoundImageView;
import com.lianren.android.widget.TweetPicturesPreviewerItemTouchCallback;

import net.oschina.common.utils.CollectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/7/15.
 */
public class PhotoSelectImageAdapter extends RecyclerView.Adapter<PhotoSelectImageAdapter.PhotoSelectImageHolder> implements TweetPicturesPreviewerItemTouchCallback.ItemTouchHelperAdapter {
    private final int MAX_SIZE = 9;
    private final int TYPE_NONE = 0;
    private final int TYPE_ADD = 1;
    private final List<UsersInfoBean.PhotoBean> mModels = new ArrayList<>();
    private Callback mCallback;

    public PhotoSelectImageAdapter(Callback callback) {
        mCallback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE)
            return TYPE_NONE;
        else if (position == size) {
            return TYPE_ADD;
        } else {
            return TYPE_NONE;
        }
    }

    @Override
    public PhotoSelectImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet_publish_selecter, parent, false);
        if (viewType == TYPE_NONE) {
            return new PhotoSelectImageHolder(view, new PhotoSelectImageHolder.HolderListener() {
                @Override
                public void onDelete(UsersInfoBean.PhotoBean model) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        int pos = mModels.indexOf(model);
                        if (pos == -1)
                            return;
                        mModels.remove(pos);
                        if (mModels.size() > 0)
                            notifyItemRemoved(pos);
                        else
                            notifyDataSetChanged();
                    }
                }

                @Override
                public void onDrag(PhotoSelectImageHolder holder) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        // Start a drag whenever the handle view it touched
                        mCallback.onStartDrag(holder);
                    }
                }

                @Override
                public void onClick(UsersInfoBean.PhotoBean model) {
                    ImageGalleryActivity.show(mCallback.getContext(), model.img_uri, false);
                }
            });
        } else {
            return new PhotoSelectImageHolder(view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        callback.onLoadMoreClick();
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final PhotoSelectImageHolder holder, int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE || size != position) {
            UsersInfoBean.PhotoBean model = mModels.get(position);
            holder.bind(position, model, mCallback.getImgLoader());
        }
    }

    @Override
    public void onViewRecycled(PhotoSelectImageHolder holder) {
        Glide.clear(holder.mImage);
    }

    @Override
    public int getItemCount() {
        int size = mModels.size();
        if (size == MAX_SIZE) {
            return size;
        } else if (size == 0) {
            return size + 1;
        } else {
            return size + 1;
        }
    }

    public void clear() {
        mModels.clear();
    }

    public void add(UsersInfoBean.PhotoBean model) {
        if (mModels.size() >= MAX_SIZE)
            return;
        mModels.add(model);
    }

    public String[] getPaths() {
        int size = mModels.size();
        if (size == 0)
            return null;
        String[] paths = new String[size];
        int i = 0;
        for (UsersInfoBean.PhotoBean model : mModels) {
            paths[i++] = model.img_uri;
        }
        return paths;
    }

    public List<UsersInfoBean.PhotoBean> getmModels() {
        return mModels;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Collections.swap(mModels, fromPosition, toPosition);
        if (fromPosition == toPosition)
            return false;

        // Move fromPosition to toPosition
        CollectionUtil.move(mModels, fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mModels.remove(position);
        notifyItemRemoved(position);
    }



    public interface Callback {
        void onLoadMoreClick();

        RequestManager getImgLoader();

        Context getContext();

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    /**
     * PhotoSelectImageHolder
     */
    static class PhotoSelectImageHolder extends RecyclerView.ViewHolder implements TweetPicturesPreviewerItemTouchCallback.ItemTouchHelperViewHolder {
        private RoundImageView mImage;
        private ImageView mDelete;
        private ImageView mGifMask;
        private HolderListener mListener;

        private PhotoSelectImageHolder(View itemView, HolderListener listener) {
            super(itemView);
            mListener = listener;
            mImage = itemView.findViewById(R.id.iv_content);
            mDelete = itemView.findViewById(R.id.iv_delete);
            mGifMask = itemView.findViewById(R.id.iv_is_gif);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof UsersInfoBean.PhotoBean) {
                        holderListener.onDelete((UsersInfoBean.PhotoBean) obj);
                    }
                }
            });
            mImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final HolderListener holderListener = mListener;
                    if (holderListener != null) {
                        holderListener.onDrag(PhotoSelectImageHolder.this);
                    }
                    return true;
                }
            });
            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = mDelete.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof UsersInfoBean.PhotoBean) {
                        holderListener.onClick((UsersInfoBean.PhotoBean) obj);
                    }
                }
            });
            mImage.setBackgroundColor(0xffdadada);
        }

        private PhotoSelectImageHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);

            mImage = (RoundImageView) itemView.findViewById(R.id.iv_content);
            mDelete = (ImageView) itemView.findViewById(R.id.iv_delete);

            mDelete.setVisibility(View.GONE);
            mImage.setImageResource(R.mipmap.ic_tweet_add);
            mImage.setOnClickListener(clickListener);
            mImage.setBackgroundDrawable(null);
        }

        public void bind(int position, UsersInfoBean.PhotoBean model, RequestManager loader) {
            mDelete.setTag(model);
            // In this we need clear before load
            Glide.clear(mImage);
            // Load image
            if (model.img_uri.toLowerCase().endsWith("gif")) {
                ImageLoader.loadImage(loader, mImage, model.img_uri);
                // Show gif mask
                mGifMask.setVisibility(View.VISIBLE);
            } else {
                ImageLoader.loadImage(loader, mImage, model.img_uri);
                mGifMask.setVisibility(View.GONE);
            }
        }


        @Override
        public void onItemSelected() {
            try {
                Vibrator vibrator = (Vibrator) itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onItemClear() {

        }

        /**
         * Holder 与Adapter之间的桥梁
         */
        interface HolderListener {
            void onDelete(UsersInfoBean.PhotoBean model);

            void onDrag(PhotoSelectImageHolder holder);

            void onClick(UsersInfoBean.PhotoBean model);
        }
    }

}
