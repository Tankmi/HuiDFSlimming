package com.huidf.slimming.adapter.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.sel_photo.SimpleItemTouchHelperCallback;
import com.huidf.slimming.entity.home.sport.SportHisEntity;
import com.huidf.slimming.entity.today_movement.MovementEntity;

import java.util.Collections;
import java.util.LinkedList;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.NewWidgetSetting;

public class SportHistoryAdapter extends RecyclerView.Adapter<SportHistoryAdapter.MyViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private LinkedList<SportHisEntity.Data.SportGenre> mList;
    private onMovementAddClick onItemClick;
    private View headerView;
    private boolean isShownoData;   //标记是否需要显示暂无数据提示
    private boolean hasHeaderView;

    public SportHistoryAdapter(Context context, LinkedList<SportHisEntity.Data.SportGenre> data)
    {
        hasHeaderView = true;
        this.mContext = context;
        this.mList = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addDataToFirst();
    }

    public void setListData(LinkedList<SportHisEntity.Data.SportGenre> data){
        this.mList = data;
        isShownoData = false;
        addDataToFirst();
    }

    private void addDataToFirst(){
        LOGUtils.LOG("mList.size()first  : " + mList.size());
        SportHisEntity.Data.SportGenre data = null;
        mList.add(0,data);
        LOGUtils.LOG("mList.size()second  : " + mList.size());
        if(mList.size()==1){
            isShownoData = true;
            mList.add(0,data);   //position == 1 时显示暂无数据提示
        }else{
            if(isShownoData){   //已经显示了暂无数据提示，需要删除
                mList.removeFirst();
            }
            isShownoData = false;
        }
        LOGUtils.LOG("mList.size(): " + mList.size());
        notifyDataSetChanged();
    }

    @Override
    public SportHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(viewType == TYPE_HEAD){
            return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_sport_history_title, parent, false),viewType);
        } else if(viewType == TYPE_NODATA){
            return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_nodata, parent, false),viewType);
        } else {
            return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_sport_history, parent, false),viewType);
        }
    }

    @Override
    public void onBindViewHolder(SportHistoryAdapter.MyViewHolder holder, final int position)
    {

        if(getItemViewType(position) == TYPE_HEAD){
            holder.name.setText("日记录");
        }else if(getItemViewType(position) == TYPE_NODATA){
            holder.name.setText("暂无数据");
        }
        else{
            //        holder.setIsRecyclable(false);  //禁止复用
            final SportHisEntity.Data.SportGenre mData = mList.get(position);
            Glide.with(mContext).load(mData.sportIcon).into(holder.icon);
            holder.name.setText(mData.sportName);
            NewWidgetSetting.getInstance().setIdenticalLineTvColor(
                    holder.name,mContext.getResources().getColor(R.color.weight_main_color), 1.5f, mData.sporttime, true);
            NewWidgetSetting.getInstance().setIdenticalLineTvColor(
                    holder.name,mContext.getResources().getColor(R.color.text_color_hint), 0.8f, "  分钟", false);
            holder.data.setText(mData.sportkcal + "千卡");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
//                    onItemClick.onMovementClick(mData);
                }
            });
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder)
    {
        int position = holder.getLayoutPosition();
        return headerView == null ? position : position - 1;
    }

    @Override
    public int getItemCount()
    {
        int count = (mList == null ? 0 : mList.size());
        if (headerView != null) {
            count++;
        }
        return count;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainView;
        private ImageView icon;
        private TextView name;
        private TextView data;

        public MyViewHolder(View view,int viewType)
        {
            super(view);
            if(viewType == TYPE_HEAD){
                name = view.findViewById(R.id.tv_item_sport_his_title);
            }else if(viewType == TYPE_NODATA){
                name = view.findViewById(R.id.tv_item_nodata);
            }
            else{
                mainView = view.findViewById(R.id.lin_item_sport_his_main);
                icon = view.findViewById(R.id.iv_item_sport_his);
                name = view.findViewById(R.id.tv_item_sport_his);
                data = view.findViewById(R.id.tv_item_sport_his_data);

                LayoutUtil.getInstance().drawViewRBLinearLayout(icon, 80, 80, -1, -1, -1, -1);
            }
        }
    }

    @Override
    public void onItemDismiss(int position)
    {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int from, int to)
    {
        Collections.swap(mList, from, to);
        notifyItemMoved(from, to);
    }

    public void setHeaderView(View headerView)
    {
        this.headerView = headerView;
        headerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        notifyItemInserted(0);
    }

    public View getHeaderView()
    {
        return headerView;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private final int TYPE_HEAD = 0;
    private final int TYPE_NOMAL = 1;
    private final int TYPE_NODATA = 2;

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && hasHeaderView)
            return TYPE_HEAD;
        else if(position == 1 && isShownoData)
            return TYPE_NODATA;
        return TYPE_NOMAL;
    }

    public void setOnMovementClickListener(onMovementAddClick onItemClick){
        this.onItemClick = onItemClick;
    }

    //获取拍照后的照片
    public interface onMovementAddClick {
        void onMovementClick(MovementEntity mEntity);
    }

}
