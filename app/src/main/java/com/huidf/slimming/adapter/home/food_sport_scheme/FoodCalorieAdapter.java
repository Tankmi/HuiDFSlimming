package com.huidf.slimming.adapter.home.food_sport_scheme;

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
import com.huidf.slimming.entity.home.food_sport_scheme.FoodCalorieEntity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.LayoutUtil;

public class FoodCalorieAdapter extends RecyclerView.Adapter<FoodCalorieAdapter.MyViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<FoodCalorieEntity.Data.FoodCalorie> mList;
    private View headerView;

    public FoodCalorieAdapter(Context context, List<FoodCalorieEntity.Data.FoodCalorie> data)
    {
//        hasHeaderView = true;
        this.mContext = context;
        this.mList = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addDataToFirst();
    }

    public void setListData(List<FoodCalorieEntity.Data.FoodCalorie> data){
        this.mList = data;
        LOGUtils.LOG("setListData mList.size():  " + mList.size());
        addDataToFirst();
    }

    public void setLoadNextData(List<FoodCalorieEntity.Data.FoodCalorie> data){
        this.mList.addAll(data);
        notifyDataSetChanged();
    }

    private void addDataToFirst(){
        FoodCalorieEntity.Data.FoodCalorie data = null;
        mList.add(0,data);   //position == 1 时显示暂无数据提示
        notifyDataSetChanged();
    }

    @Override
    public FoodCalorieAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(viewType == TYPE_CONTENT){
            return new MyViewHolder(inflater.inflate(R.layout.item_food_calorie_normal, parent, false),viewType);
        }
//        else if(viewType == TYPE_NODATA){
//            return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_nodata, parent, false),viewType);
//        }
        else{
            return new MyViewHolder(inflater.inflate(R.layout.item_food_calorie_head, parent, false),viewType);
        }
    }

    @Override
    public void onBindViewHolder(FoodCalorieAdapter.MyViewHolder holder, final int position)
    {
        //        holder.setIsRecyclable(false);  //禁止复用
        final FoodCalorieEntity.Data.FoodCalorie mData = mList.get(position);
//        if(getItemViewType(position) == TYPE_NODATA){
//            holder.mTVhint.setText("暂无数据");
//        }else
        if(getItemViewType(position) == TYPE_HEAD){
//            holder.mTVtitle.setText("" +(position+1) + "." + mData.planName);
//            Glide.with(mContext).load(mData.image).into(holder.mIVimage);
//            holder.mTVhint.setText(mData.restTime);
        }
        else{
            holder.mTVtitle.setText("" + mData.name);
//            holder.mTVhint.setText(mData.restTime);
            holder.mTVcontent.setText( mData.kcal + "千卡");
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
        private TextView mTVtitle;
        private TextView mTVcontent;
        private TextView mTVhint;

        public MyViewHolder(View view,int viewType)
        {
            super(view);
            if(viewType == TYPE_CONTENT){
                mainView = view.findViewById(R.id.lin_food_calorie_normal_main);
                mTVtitle = view.findViewById(R.id.tv_food_calorie_normal_title);
                mTVcontent = view.findViewById(R.id.tv_food_calorie_normal_content);

                mainView.setMinimumHeight(LayoutUtil.getInstance().getWidgetHeight(124));
            }
//            else if(viewType == TYPE_NODATA){
//                mTVhint = view.findViewById(R.id.tv_item_nodata);
//            }
            else{
                mainView = view.findViewById(R.id.lin_food_calorie_head_main);
                mTVtitle = view.findViewById(R.id.tv_food_calorie_head_title);
                mTVcontent = view.findViewById(R.id.tv_food_calorie_head_content);

                mainView.setMinimumHeight(LayoutUtil.getInstance().getWidgetHeight(70));
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
    private final int TYPE_CONTENT = 1;
//    /** 暂无数据 */
//    private final int TYPE_NODATA = 2;

    @Override
    public int getItemViewType(int position) {
//        final FoodCalorieEntity.Data.FoodCalorie mData = mList.get(position);
//
//        if(mData == null) return TYPE_NODATA;

        if(position == 0)
            return TYPE_HEAD;
        else
            return TYPE_CONTENT;
    }

    public void setOnMovementClickListener(onViewAllClickListener onItemClick){
//        this.onViewAllClick = onItemClick;
    }

    //查看全部
    public interface onViewAllClickListener {
        void onViewAllClick(FoodCalorieEntity.Data.FoodCalorie mEntity);
    }

}
