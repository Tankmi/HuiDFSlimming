package com.huidf.slimming.fragment.home.weight;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/26 : 14:20
 * 描述：EventBus事件对象，需要刷新首页时发送事件
 */
public class HomeRefreshData {
    public Boolean isRefreshData;

    public HomeRefreshData(Boolean isRefreshData) {
        this.isRefreshData = isRefreshData;
    }

    public Boolean getRefreshData() {
        return isRefreshData;
    }

}
