package com.huidf.slimming.adapter.home.food_sport_scheme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.sel_photo.SimpleItemTouchHelperCallback;
import com.huidf.slimming.entity.home.food_sport_scheme.FoodSchemeEntity;

import java.util.Collections;
import java.util.List;

import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.NewWidgetSetting;

public class FoodSchemeAdapter extends RecyclerView.Adapter<FoodSchemeAdapter.MyViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<FoodSchemeEntity.Data.FoodScheme> mList;
    private onViewAllClickListener onViewAllClick;
    private View headerView;
//    private boolean isShowNoData;   //标记是否需要显示暂无数据提示
//    private boolean hasHeaderView;

    public FoodSchemeAdapter(Context context, List<FoodSchemeEntity.Data.FoodScheme> data)
    {
//        hasHeaderView = true;
        this.mContext = context;
        this.mList = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addDataToFirst();
    }

    public void setListData(List<FoodSchemeEntity.Data.FoodScheme> data){
        this.mList = data;
//        isShowNoData = false;
        addDataToFirst();
    }

    private void addDataToFirst(){
        FoodSchemeEntity.Data.FoodScheme data = null;
        if(mList.size() == 0){
            mList.add(0,data);   //position == 1 时显示暂无数据提示
        }
        notifyDataSetChanged();
    }

    @Override
    public FoodSchemeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(viewType == TYPE_IMAGETEXT){
            return new MyViewHolder(inflater.inflate(R.layout.fragment_food_scheme_it, parent, false),viewType);
        } else if(viewType == TYPE_NODATA){
            return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_nodata, parent, false),viewType);
        } else{
            return new MyViewHolder(inflater.inflate(R.layout.fragment_food_scheme_i, parent, false),viewType);
        }
    }

    @Override
    public void onBindViewHolder(FoodSchemeAdapter.MyViewHolder holder, final int position)
    {
        //        holder.setIsRecyclable(false);  //禁止复用
        final FoodSchemeEntity.Data.FoodScheme mData = mList.get(position);
        if(getItemViewType(position) == TYPE_NODATA){
            holder.foodHint.setText("暂无数据");
        }else if(getItemViewType(position) == TYPE_IMAGE){

            Glide.with(mContext).load(mData.image).into(holder.foodImage);
        }
        else{
            Glide.with(mContext).load(mData.image).into(holder.foodImage);

            holder.foodHint.setText("正常饮食（建议不超过");
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.foodHint,mContext.getResources().getColor(R.color.foodscheme_text_redcolor), 1.25f, mData.calorie + "", false);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.foodHint,mContext.getResources().getColor(R.color.foodscheme_text_greencolor), 1f, " 千卡)", false);
        }

        if(getItemViewType(position) == TYPE_IMAGE || getItemViewType(position) == TYPE_IMAGETEXT){
            int type = mData.type;
            if(type == 1) holder.foodTime.setBackgroundColor(mContext.getResources().getColor(R.color.back_breakfast));
            else if(type == 2)  holder.foodTime.setBackgroundColor(mContext.getResources().getColor(R.color.back_lunch));
            else holder.foodTime.setBackgroundColor(mContext.getResources().getColor(R.color.back_dinner));
            holder.foodTime.setText("" + (type == 1?"早餐":type == 2?"午餐":"晚餐"));
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
        private TextView foodTime;
        private RelativeLayout foodITImage;
        private ImageView foodImage;
        private TextView foodHint;
//        private TextView data;

        public MyViewHolder(View view,int viewType)
        {
            super(view);
            if(viewType == TYPE_IMAGETEXT){
                mainView = view.findViewById(R.id.lin_food_scheme_breakfast);
                foodTime = view.findViewById(R.id.tv_food_scheme_breakfast);
                foodITImage = view.findViewById(R.id.rel_food_scheme_breakfast);
                foodImage = view.findViewById(R.id.iv_food_scheme_breakfast);
                foodHint = view.findViewById(R.id.tv_food_scheme_breakfast_hint);

                foodTime.setMinWidth(LayoutUtil.getInstance().getWidgetWidth(222));
                foodTime.setMinHeight(LayoutUtil.getInstance().getWidgetHeight(174));
                LayoutUtil.getInstance().drawViewRBLinearLayout(foodITImage, 482, 176, -1, -1, -1, -1);
                LayoutUtil.getInstance().drawViewRBLayout(foodImage, 482, 176, -1, -1, -1, -1);
            }else if(viewType == TYPE_NODATA){
                foodHint = view.findViewById(R.id.tv_item_nodata);
            }
            else{
                mainView = view.findViewById(R.id.lin_food_scheme_breakfast);
                foodTime = view.findViewById(R.id.tv_food_scheme_breakfast);
                foodImage = view.findViewById(R.id.iv_food_scheme_breakfast);
//                data = view.findViewById(R.id.tv_item_ranking_value);

                foodTime.setMinWidth(LayoutUtil.getInstance().getWidgetWidth(222));
                foodTime.setMinHeight(LayoutUtil.getInstance().getWidgetHeight(174));
                LayoutUtil.getInstance().drawViewRBLinearLayout(foodImage, 482, 176, -1, -1, -1, -1);
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
    private final int TYPE_IMAGE = 0;
    /**普通内容 */
    private final int TYPE_IMAGETEXT = 1;
    /** 暂无数据 */
    private final int TYPE_NODATA = 2;

    @Override
    public int getItemViewType(int position) {
        final FoodSchemeEntity.Data.FoodScheme mData = mList.get(position);

        if(mData == null) return TYPE_NODATA;

        if(mData.flag == 1)
            return TYPE_IMAGE;
        else
            return TYPE_IMAGETEXT;
    }

    public void setOnMovementClickListener(onViewAllClickListener onItemClick){
        this.onViewAllClick = onItemClick;
    }

    //查看全部
    public interface onViewAllClickListener {
        void onViewAllClick(FoodSchemeEntity.Data.FoodScheme mEntity);
    }

}
