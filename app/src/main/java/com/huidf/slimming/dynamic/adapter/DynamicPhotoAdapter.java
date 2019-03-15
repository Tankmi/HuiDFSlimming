package com.huidf.slimming.dynamic.adapter;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.sel_photo.SimpleItemTouchHelperCallback;
import com.huidf.slimming.dynamic.model.CreDynSelectPicEntity;

import java.util.Collections;
import java.util.LinkedList;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.LayoutUtil;

public class DynamicPhotoAdapter extends RecyclerView.Adapter<DynamicPhotoAdapter.MyViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private LinkedList<CreDynSelectPicEntity> mList;

    private int mListSize;   //列表数据的大小

    public DynamicPhotoAdapter(Context context, OnPictureOperateInterface onPicOperate, LinkedList<CreDynSelectPicEntity> data) {
        this.mContext = context;
        this.onPicOperate = onPicOperate;
        if(data == null) mList = new LinkedList<>();
            else this.mList = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListSize = mList.size();
        disposeData();
    }

    public void addData(LinkedList<CreDynSelectPicEntity> data) {
        if (data != null) {
            if (mListSize < 4) {
                int listMaxPosition = mList.size() - 1;  //获取list最大的下标
                int listSizePosition = mListSize - 1;    //获取实际有值的list最大的下标
                while (listMaxPosition > listSizePosition) {
                    mList.remove(listMaxPosition);
                    listMaxPosition--;
                }
            }
            mList.addAll(data);
            mListSize = mList.size();
        }
        onPicOperate.onPictureOperate(mListSize);
        disposeData();
    }

    //获取所有数据
    public String getPictureUrls(){
        StringBuilder mUrlBuilders = new StringBuilder("");
        for (CreDynSelectPicEntity mData : mList) {
            if(mData==null) continue;
            mUrlBuilders.append("|").append(mData.getUrl());
        }
        return mUrlBuilders.toString();
    }

    //获取列表大小
    public int getDataSize(){
        return mListSize;
    }

    //删除指定下标的数据
    private void removeData(int position) {
        mList.remove(position);
        mListSize--;
        onPicOperate.onPictureOperate(mListSize);
        disposeData();
    }

    //处理数据
    private void disposeData() {
        int listSize = mList.size();
        if (listSize < 4) {    //空列表，显示占位图片
            CreDynSelectPicEntity data = null;
            while (listSize < 4) {
                mList.add(data);
                listSize++;
            }
        }
        LOGUtils.LOG("mList.size(): " + mList.size());
        notifyDataSetChanged();
    }

    @Override
    public DynamicPhotoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_create_dynamic_picture, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(DynamicPhotoAdapter.MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final CreDynSelectPicEntity mData = mList.get(position);
        if (mData != null) {
            holder.removeBtn.setVisibility(View.VISIBLE);
            holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeData(position);
                }
            });

            Glide.with(mContext).load(mData.getPath()).into(holder.pictureView);
        }

    }


    @Override
    public int getItemCount() {
        int count = (mList == null ? 0 : mList.size());
        return count;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mainView;
        private Button removeBtn;
        private ImageView pictureView;

        public MyViewHolder(View view, int viewType) {
            super(view);
            mainView = view.findViewById(R.id.rel_credyn_picture_main);
            removeBtn = view.findViewById(R.id.btn_credyn_picture);
            pictureView = view.findViewById(R.id.iv_credyn_picture);
            LayoutUtil.getInstance().drawViewRBLinearLayout(mainView, 175, 160, -1, -1, -1, -1);
            LayoutUtil.getInstance().drawViewRBLayout(pictureView, 160, 160, -1, -1, -1, -1);
            LayoutUtil.getInstance().drawViewRBLayout(removeBtn, 15, 15, -1, -1, -1, -1);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int from, int to) {
        Collections.swap(mList, from, to);
        notifyItemMoved(from, to);
    }


    private final int TYPE_NOMAL = 1;

    @Override
    public int getItemViewType(int position) {
        return TYPE_NOMAL;
    }

    private OnPictureOperateInterface onPicOperate;

    public interface OnPictureOperateInterface{
        void onPictureOperate(int PicListSize);
    }

}
