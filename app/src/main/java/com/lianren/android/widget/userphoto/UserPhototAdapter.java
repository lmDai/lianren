package com.lianren.android.widget.userphoto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.lianren.android.AppContext;
import com.lianren.android.LRApplication;
import com.lianren.android.R;
import com.lianren.android.api.CommonHttpResponseHandler;
import com.lianren.android.api.remote.LRApi;
import com.lianren.android.improve.app.AppOperator;
import com.lianren.android.improve.bean.Constants;
import com.lianren.android.improve.bean.MessageWrap;
import com.lianren.android.improve.bean.PhotoBean;
import com.lianren.android.improve.bean.UsersInfoBean;
import com.lianren.android.improve.bean.base.ResultBean;
import com.lianren.android.util.DialogHelper;
import com.lianren.android.util.ImageLoader;
import com.lianren.android.util.pickimage.media.ImageGalleryActivity;
import com.lianren.android.widget.RoundImageView;

import net.oschina.common.utils.CollectionUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/7/15.
 */
public class UserPhototAdapter extends RecyclerView.Adapter<UserPhototAdapter.UserSelectImageHolder> implements UserPhotoPreviewerItemTouchCallback.ItemTouchHelperAdapter {
    private final int MAX_SIZE = 9;
    private final int TYPE_NONE = 0;
    private final int TYPE_ADD = 1;
    private final List<PhotoBean> mModels = new ArrayList<>();
    private Callback mCallback;
    private Context mContext;
    private ProgressDialog mWaitDialog;

    public UserPhototAdapter(Callback callback, Context mContext) {
        mCallback = callback;
        this.mContext = mContext;
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
    public UserSelectImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet_publish_selecter, parent, false);
        if (viewType == TYPE_NONE) {
            return new UserSelectImageHolder(view, new UserSelectImageHolder.HolderListener() {
                @Override
                public void onDelete(PhotoBean model) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        int pos = mModels.indexOf(model);
                        if (pos == -1)
                            return;
                        deletePhotoDialog(model.photo_id, pos);
                    }
                }

                @Override
                public void onDrag(UserSelectImageHolder holder) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        // Start a drag whenever the handle view it touched
                        mCallback.onStartDrag(holder);
                    }
                }

                @Override
                public void onClick(PhotoBean model) {
                    ImageGalleryActivity.show(mCallback.getContext(), model.img_uri, false);
                }
            });
        } else {
            return new UserSelectImageHolder(view, new View.OnClickListener() {
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

    protected void showLoadingDialog() {
        if (mWaitDialog == null) {
            mWaitDialog = DialogHelper.getProgressDialog(mContext, true);
        }
        mWaitDialog.setMessage("加载中...");
        mWaitDialog.show();
    }

    protected void showLoadingDialog(String message) {
        if (mWaitDialog == null) {
            mWaitDialog = DialogHelper.getProgressDialog(mContext, true);
        }
        mWaitDialog.setMessage(message);
        mWaitDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (mWaitDialog == null) return;
        mWaitDialog.dismiss();
    }

    //删除图片
    private void deletePhotoDialog(final int delPhotoId, final int pos) {
        DialogHelper.getConfirmDialog(mContext, "提示", "确认要删除?", "确认", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePhoto(delPhotoId, pos);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    public void deletePhoto(int photo_id, final int pos) {
        LRApi.photosDelete(photo_id, new CommonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog("正在删除...");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(R.string.request_error_hint);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PhotoBean>>() {
                    }.getType();
                    ResultBean<PhotoBean> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mModels.remove(pos);
                        if (mModels.size() > 0)
                            notifyItemRemoved(pos);
                        else
                            notifyDataSetChanged();
                        EventBus.getDefault().post(new MessageWrap(Constants.REFRESH_USER, null));
                    } else {
                        AppContext.showToast(resultBean.error.message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }
        });
    }

    @Override
    public void onBindViewHolder(final UserSelectImageHolder holder, int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE || size != position) {
            PhotoBean model = mModels.get(position);
            holder.bind(position, model, mCallback.getImgLoader());
        }
    }

    @Override
    public void onViewRecycled(UserSelectImageHolder holder) {
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

    public void add(PhotoBean model) {
        if (mModels.size() >= MAX_SIZE)
            return;
        mModels.add(model);
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

    public List<PhotoBean> getData() {
        return mModels;
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
     * UserSelectImageHolder
     */
    static class UserSelectImageHolder extends RecyclerView.ViewHolder implements UserPhotoPreviewerItemTouchCallback.ItemTouchHelperViewHolder {
        private RoundImageView mImage;
        private ImageView mDelete;
        private ImageView mGifMask;
        private HolderListener mListener;

        private UserSelectImageHolder(View itemView, HolderListener listener) {
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
                    if (holderListener != null && obj != null && obj instanceof PhotoBean) {
                        holderListener.onDelete((PhotoBean) obj);
                    }
                }
            });
            mImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final HolderListener holderListener = mListener;
                    if (holderListener != null) {
                        holderListener.onDrag(UserSelectImageHolder.this);
                    }
                    return true;
                }
            });
            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = mDelete.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof PhotoBean) {
                        holderListener.onClick((PhotoBean) obj);
                    }
                }
            });
            mImage.setBackgroundColor(0xffdadada);
        }

        private UserSelectImageHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);

            mImage = (RoundImageView) itemView.findViewById(R.id.iv_content);
            mDelete = (ImageView) itemView.findViewById(R.id.iv_delete);

            mDelete.setVisibility(View.GONE);
            mImage.setImageResource(R.mipmap.ic_tweet_add);
            mImage.setOnClickListener(clickListener);
            mImage.setBackgroundDrawable(null);
        }

        public void bind(int position, PhotoBean model, RequestManager loader) {
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
            void onDelete(PhotoBean model);

            void onDrag(UserSelectImageHolder holder);

            void onClick(PhotoBean model);
        }
    }

}
