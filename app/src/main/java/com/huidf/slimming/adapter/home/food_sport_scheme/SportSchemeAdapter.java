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
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.sel_photo.SimpleItemTouchHelperCallback;
import com.huidf.slimming.entity.home.food_sport_scheme.SportSchemeEntity;

import java.util.Collections;
import java.util.List;

import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.NewWidgetSetting;

public class SportSchemeAdapter extends RecyclerView.Adapter<SportSchemeAdapter.MyViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<SportSchemeEntity.Data.SportScheme> mList;
    private onViewAllClickListener onViewAllClick;
    private View headerView;
//    private boolean isShowNoData;   //标记是否需要显示暂无数据提示
//    private boolean hasHeaderView;

    public SportSchemeAdapter(Context context, List<SportSchemeEntity.Data.SportScheme> data)
    {
//        hasHeaderView = true;
        this.mContext = context;
        this.mList = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addDataToFirst();
    }

    public void setListData(List<SportSchemeEntity.Data.SportScheme> data){
        this.mList = data;
//        isShowNoData = false;
        addDataToFirst();
    }

    private void addDataToFirst(){
        SportSchemeEntity.Data.SportScheme data = null;
        if(mList.size() == 0){
            mList.add(0,data);   //position == 1 时显示暂无数据提示
        }
        notifyDataSetChanged();
    }

    @Override
    public SportSchemeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(viewType == TYPE_TEXT){
            return new MyViewHolder(inflater.inflate(R.layout.item_sport_scheme_text, parent, false),viewType);
        } else if(viewType == TYPE_NODATA){
            return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_nodata, parent, false),viewType);
        } else{
            return new MyViewHolder(inflater.inflate(R.layout.item_sport_scheme_gif, parent, false),viewType);
        }
    }

    @Override
    public void onBindViewHolder(SportSchemeAdapter.MyViewHolder holder, final int position)
    {
        //        holder.setIsRecyclable(false);  //禁止复用
        final SportSchemeEntity.Data.SportScheme mData = mList.get(position);
        if(getItemViewType(position) == TYPE_NODATA){
            holder.mTVhint.setText("暂无数据");
        }else if(getItemViewType(position) == TYPE_GIF){
            holder.mTVtitle.setText("" +(position+1) + "." + mData.planName);
            Glide.with(mContext).load(mData.image).into(holder.mIVimage);
            holder.mTVhint.setText(mData.restTime);
        }
        else{
            holder.mTVtitle.setText("" +(position+1) + ".有氧运动");
            holder.mTVhint.setText(mData.restTime);
            holder.mTVcontent.setText("运动类型：");
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,mContext.getResources().getColor(R.color.main_color), 1f, " 快走、慢跑、游泳、骑车等", false);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,-999, 1f, "运动时间：每天运动不少于", true);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,mContext.getResources().getColor(R.color.main_color), 1f, mData.key4, false);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,-999, 1f, "分钟", false);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,-999, 1f, "运动强度：运动心率维持在", true);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,mContext.getResources().getColor(R.color.main_color), 1f, mData.key5, false);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,-999, 1f, "次/分", false);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,-999, 1f, "运动频率：每周运动不少于", true);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,mContext.getResources().getColor(R.color.main_color), 1f, "3", false);
            NewWidgetSetting.setIdenticalLineTvColor(
                    holder.mTVcontent,-999, 1f, "天", false);
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
//        private RelativeLayout foodITImage;
        private ImageView mIVimage;
        private TextView mTVhint;
//        private TextView data;

        public MyViewHolder(View view,int viewType)
        {
            super(view);
            if(viewType == TYPE_TEXT){
                mainView = view.findViewById(R.id.lin_sport_scheme_main);
                mTVtitle = view.findViewById(R.id.tv_sport_scheme);
                mTVcontent = view.findViewById(R.id.tv_sport_scheme_content);
                mIVimage = view.findViewById(R.id.iv_food_scheme_breakfast);
                mTVhint = view.findViewById(R.id.tv_sport_scheme_hint);

                mainView.setMinimumHeight(LayoutUtil.getInstance().getWidgetHeight(228));
            }else if(viewType == TYPE_NODATA){
                mTVhint = view.findViewById(R.id.tv_item_nodata);
            }
            else{
                mainView = view.findViewById(R.id.lin_sport_scheme_main);
                mTVtitle = view.findViewById(R.id.tv_sport_scheme);
                mIVimage = view.findViewById(R.id.iv_sport_scheme);
                mTVhint = view.findViewById(R.id.tv_sport_scheme_hint);

                mainView.setMinimumHeight(LayoutUtil.getInstance().getWidgetHeight(228));
                LayoutUtil.getInstance().drawViewRBLinearLayout(mIVimage, 200, 200, -1, -1, -1, -1);
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

    private final int TYPE_GIF = 0;
    private final int TYPE_TEXT = 1;
    /** 暂无数据 */
    private final int TYPE_NODATA = 2;

    @Override
    public int getItemViewType(int position) {
        final SportSchemeEntity.Data.SportScheme mData = mList.get(position);

        if(mData == null) return TYPE_NODATA;

        if(mData.type == 1)
            return TYPE_GIF;
        else
            return TYPE_TEXT;
    }

    public void setOnMovementClickListener(onViewAllClickListener onItemClick){
        this.onViewAllClick = onItemClick;
    }

    //查看全部
    public interface onViewAllClickListener {
        void onViewAllClick(SportSchemeEntity.Data.SportScheme mEntity);
    }

}
