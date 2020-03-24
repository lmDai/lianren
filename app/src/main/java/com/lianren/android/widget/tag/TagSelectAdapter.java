package com.lianren.android.widget.tag;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.lianren.android.R;
import com.lianren.android.util.pickimage.media.ImageGalleryActivity;
import com.lianren.android.widget.TweetPicturesPreviewerItemTouchCallback;

import net.oschina.common.utils.CollectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/7/15.
 */
public class TagSelectAdapter extends RecyclerView.Adapter<TagSelectAdapter.TagSelectHolder> implements TweetPicturesPreviewerItemTouchCallback.ItemTouchHelperAdapter {
    private final int MAX_SIZE = 1;
    private final int TYPE_NONE = 0;
    private final int TYPE_ADD = 1;
    private final List<Model> mModels = new ArrayList<>();
    private Callback mCallback;

    public TagSelectAdapter(Callback callback) {
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
    public TagSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_publish_imprints, parent, false);
        if (viewType == TYPE_NONE) {
            return new TagSelectHolder(view, new TagSelectHolder.HolderListener() {
                @Override
                public void onDelete(Model model) {
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
                public void onDrag(TagSelectHolder holder) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        // Start a drag whenever the handle view it touched
                        mCallback.onStartDrag(holder);
                    }
                }

                @Override
                public void onClick(Model model) {
                    ImageGalleryActivity.show(mCallback.getContext(), model.path, false);
                }
            });
        } else {
            return new TagSelectHolder(view, new View.OnClickListener() {
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
    public void onBindViewHolder(final TagSelectHolder holder, int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE || size != position) {
            Model model = mModels.get(position);
            holder.bind(position, model, mCallback.getImgLoader());
        }
    }

    @Override
    public void onViewRecycled(TagSelectHolder holder) {

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

    public void add(Model model) {
        if (mModels.size() >= MAX_SIZE)
            return;
        mModels.add(model);
    }

    public void add(String path) {
        add(new Model(path));
    }

    public List<String> getPaths() {
        int size = mModels.size();
        if (size == 0)
            return null;
        List<String> paths = new ArrayList<>();
        int i = 0;
        for (Model model : mModels) {
            paths.add(model.path);
        }
        return paths;
    }

    public List<Model> getmModels() {
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


    public static class Model implements Serializable {
        public Model(String path) {
            this.path = path;
        }

        public String path;
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
     * TagSelectHolder
     */
    static class TagSelectHolder extends RecyclerView.ViewHolder implements TweetPicturesPreviewerItemTouchCallback.ItemTouchHelperViewHolder {
        private ImageView mDelete;
        private TextView tvContent;
        private HolderListener mListener;
        private LinearLayout llContent;

        private TagSelectHolder(View itemView, HolderListener listener) {
            super(itemView);
            mListener = listener;
            tvContent = itemView.findViewById(R.id.tv_ques_detail_tag);
            mDelete = itemView.findViewById(R.id.img_delete);
            llContent = itemView.findViewById(R.id.ll_content);
            llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof TagSelectAdapter.Model) {
                        holderListener.onDelete((TagSelectAdapter.Model) obj);
                    }
                }
            });
//            mDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Object obj = v.getTag();
//                    final HolderListener holderListener = mListener;
//                    if (holderListener != null && obj != null && obj instanceof TagSelectAdapter.Model) {
//                        holderListener.onDelete((TagSelectAdapter.Model) obj);
//                    }
//                }
//            });
        }

        private TagSelectHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);
            mDelete = itemView.findViewById(R.id.img_delete);
            tvContent = itemView.findViewById(R.id.tv_ques_detail_tag);
            llContent = itemView.findViewById(R.id.ll_content);
            mDelete.setVisibility(View.GONE);
            tvContent.setText("#");
            llContent.setOnClickListener(clickListener);
        }

        public void bind(int position, TagSelectAdapter.Model model, RequestManager loader) {
            mDelete.setTag(model);
            llContent.setTag(model);
            // In this we need clear before load
            tvContent.setText(model.path);
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
            void onDelete(TagSelectAdapter.Model model);

            void onDrag(TagSelectHolder holder);

            void onClick(TagSelectAdapter.Model model);
        }
    }

}
