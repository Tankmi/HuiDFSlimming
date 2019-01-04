package com.huidf.slimming.adapter.ranking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.sel_photo.SimpleItemTouchHelperCallback;
import com.huidf.slimming.entity.home.food_sport_scheme.FoodCalorieEntity;
import com.huidf.slimming.entity.ranking.RankingEntity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import huitx.libztframework.utils.LayoutUtil;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.MyViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private LinkedList<RankingEntity.Data.RankingInfo> mList;
    private onViewAllClickListener onViewAllClick;
    private View headerView;
//    private boolean isShowNoData;   //标记是否需要显示暂无数据提示
//    private boolean hasHeaderView;

    public RankingAdapter(Context context, LinkedList<RankingEntity.Data.RankingInfo> data)
    {
//        hasHeaderView = true;
        this.mContext = context;
        this.mList = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addDataToFirst();
    }

    public void setListData(LinkedList<RankingEntity.Data.RankingInfo> data){
        this.mList = data;
//        isShowNoData = false;
        addDataToFirst();
    }

    public void setLoadNextData(List<RankingEntity.Data.RankingInfo> data){
        this.mList.addAll(data);
        notifyDataSetChanged();
    }

    private void addDataToFirst(){
        RankingEntity.Data.RankingInfo data = null;
        if(mList.size() == 0){
            mList.add(0,data);   //position == 1 时显示暂无数据提示
        }
//        else{
//            if(isShowNoData){   //已经显示了暂无数据提示，需要删除
//                mList.removeFirst();
//            }
//            isShowNoData = false;
//        }
        notifyDataSetChanged();
    }

    @Override
    public RankingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(viewType == TYPE_HEAD){
            return new MyViewHolder(inflater.inflate(R.layout.item_rc_ranking_title, parent, false),viewType);
        } else if(viewType == TYPE_NODATA){
            return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_nodata, parent, false),viewType);
        } else if(viewType == TYPE_FOTTER){
            return new MyViewHolder(inflater.inflate(R.layout.item_rc_ranking_footer, parent, false),viewType);
        } else {
            return new MyViewHolder(inflater.inflate(R.layout.item_rc_ranking, parent, false),viewType);
        }
    }

    @Override
    public void onBindViewHolder(RankingAdapter.MyViewHolder holder, final int position)
    {
                holder.setIsRecyclable(false);  //禁止复用
        final RankingEntity.Data.RankingInfo mData = mList.get(position);
        if(getItemViewType(position) == TYPE_HEAD){
            String value = "日";
            if(mData.type == 2)  value = "周";
            else if(mData.type == 3)  value = "月";
            holder.name.setText(value + "排行");
        }else if(getItemViewType(position) == TYPE_NODATA){
            holder.name.setText("暂无数据");
        }else if(getItemViewType(position) == TYPE_FOTTER){
            holder.name.setText("查看全部");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(onViewAllClick != null) onViewAllClick.onViewAllClick(mData);
                }
            });
        }
        else{
            if(mData.rank == 1)  holder.rank.setBackgroundResource(R.drawable.icon_ranking_1);
            else if(mData.rank == 2)  holder.rank.setBackgroundResource(R.drawable.icon_ranking_2);
            else if(mData.rank == 3)  holder.rank.setBackgroundResource(R.drawable.icon_ranking_3);
            else  holder.rank.setText("" + mData.rank);
            if(mData.rank == 1 || mData.rank == 2 || mData.rank == 3){
                holder.rank.setText("");
                LayoutUtil.getInstance().drawViewRBLinearLayout(holder.rank, 60, 60, -1, -1, -1, -1);
            }

            RequestOptions options = new RequestOptions().circleCrop();
            Glide.with(mContext).load(mData.customerHead).apply(options).into(holder.icon);
            holder.name.setText(mData.customerName);
            if(mData.rank == 0){
                holder.rank.setText("");
                holder.data.setText("暂无排行");
            }else{
                if(mData.tragetType == 1) holder.data.setText(mData.data + "分钟");
                if(mData.tragetType == 2) holder.data.setText(mData.data + "%");
            }



//            NewWidgetSetting.getInstance().setIdenticalLineTvColor(
//                    holder.name,mContext.getResources().getColor(R.color.weight_main_color), 1.5f, mData.sporttime, true);
//            NewWidgetSetting.getInstance().setIdenticalLineTvColor(
//                    holder.name,mContext.getResources().getColor(R.color.text_color_hint), 0.8f, "  分钟", false);
//            holder.data.setText(mData.sportkcal + "千卡");

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
        private TextView rank;
        private ImageView icon;
        private TextView name;
        private TextView data;

        public MyViewHolder(View view,int viewType)
        {
            super(view);
            if(viewType == TYPE_HEAD){
                name = view.findViewById(R.id.tv_item_ranking_title);
            }else if(viewType == TYPE_FOTTER){
                name = view.findViewById(R.id.tv_item_ranking_footer);
            }else if(viewType == TYPE_NODATA){
                name = view.findViewById(R.id.tv_item_nodata);
            }
            else{
                mainView = view.findViewById(R.id.lin_item_ranking_main);
                rank = view.findViewById(R.id.tv_item_ranking_rank);
                icon = view.findViewById(R.id.iv_item_ranking_header);
                name = view.findViewById(R.id.tv_item_ranking_name);
                data = view.findViewById(R.id.tv_item_ranking_value);

                rank.setMinHeight(LayoutUtil.getInstance().getWidgetHeight(60));
                rank.setMinWidth(LayoutUtil.getInstance().getWidgetWidth(60));
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

    /** 头 */
    private final int TYPE_HEAD = 0;
    /**普通内容 */
    private final int TYPE_NOMAL = 1;
    /** 暂无数据 */
    private final int TYPE_NODATA = 2;
//    /**头 */
//    private final int TYPE_TITLE = 3;
    /**尾 */
    private final int TYPE_FOTTER = 4;

    @Override
    public int getItemViewType(int position) {
        final RankingEntity.Data.RankingInfo mData = mList.get(position);

        if(mData == null) return TYPE_NODATA;

        if(mData.targets == 1)
            return TYPE_HEAD;
        else  if(mData.targets == 2)
            return TYPE_NOMAL;
        else  if(mData.targets == 3)
            return TYPE_FOTTER;
        else return TYPE_NOMAL;
//        if(position == 0 && hasHeaderView)
//            return TYPE_HEAD;
//        else if(position == 1 && isShowNoData)
//            return TYPE_NODATA;
//        return TYPE_NOMAL;
    }

    public void setOnMovementClickListener(onViewAllClickListener onItemClick){
        this.onViewAllClick = onItemClick;
    }

    //查看全部
    public interface onViewAllClickListener {
        void onViewAllClick(RankingEntity.Data.RankingInfo mEntity);
    }

}
