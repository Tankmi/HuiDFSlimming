package com.huidf.slimming.adapter.movement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.sel_photo.SimpleItemTouchHelperCallback;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.entity.today_movement.MovementEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.UnitConversion;


public class TodayMovementAddAdapter extends RecyclerView.Adapter<TodayMovementAddAdapter.MyViewHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private LinkedList<MovementEntity> mList;
    private onMovementAddClick onItemClick;
    private List<String> selectPhoto = new ArrayList<>();
    private View headerView;
    private float mUserWeight;

    public TodayMovementAddAdapter(Context context, LinkedList<MovementEntity> data)
    {
        this.mContext = context;
        this.mList = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUserWeight = MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_CURRENT_WEIGHT, 66.0f);
        selectPhoto.clear();
    }

    @Override
    public TodayMovementAddAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new MyViewHolder(inflater.inflate(R.layout.item_recycleview_add_movement, parent, false));
    }

    @Override
    public void onBindViewHolder(TodayMovementAddAdapter.MyViewHolder holder, final int position)
    {

//        holder.setIsRecyclable(false);  //禁止复用
        final MovementEntity mData = mList.get(position);
        Glide.with(mContext).load(mData.icon).into(holder.icon);
        holder.name.setText(mData.name);
        int equalCalorie = (int) (mUserWeight * mData.equValue);
//        equalCalorie = UnitConversion.preciseNumber(equalCalorie, 0);
        NewWidgetSetting.setIdenticalLineTvColor(
                holder.name,mContext.getResources().getColor(R.color.text_color_hint), 0.8f, equalCalorie + " (千卡)/60分钟", true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onItemClick.onMovementClick(mData);
            }
        });

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
        private RelativeLayout mainView;
        private ImageView icon;
        private TextView name;

        public MyViewHolder(View view)
        {
            super(view);
            mainView = view.findViewById(R.id.rel_item_add_movement_main);
            icon = view.findViewById(R.id.iv_item_add_movement);
            name = view.findViewById(R.id.tv_item_add_movement);

            LayoutUtil.getInstance().drawViewRBLayout(icon, 80, 80, -1, -1, -1, -1);
//            mainView.setMinimumHeight(LayoutUtil.getInstance().getWidgetHeight());
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


    @Override
    public int getItemViewType(int position)
    {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if(headerView == null)
//            return TYPE_NOMAL;
//        if(position == 0)
//            return TYPE_HEAD;
//        return TYPE_NOMAL;
//    }

    public void setOnMovementClickListener(onMovementAddClick onItemClick){
        this.onItemClick = onItemClick;
    }

    //获取拍照后的照片
    public interface onMovementAddClick {
        void onMovementClick(MovementEntity mEntity);
    }

}
