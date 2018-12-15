package com.huidf.slimming.entity.today_movement;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Objects;

/**
 * 今日运动，手动选项
 * @author ZhuTao
 * @date 2018/11/29
 * @params
*/

public class MovementEntity {

    public int icon;                 // icon
    public String name;                 // 名
	public float equValue;                   // 换算值    equ * 体重值（千卡）/60分钟
	/** 1快走 2慢跑 3 游泳 4骑车 5跳绳 6 跳舞
	 *  7爬山 8攀岩 9 爬楼梯 10踏板操
	 *  11篮球 12足球 13排球 14 乒乓球 15羽毛球
	 *  16网球 17壁球 18滑板 19 滑冰 20滑雪
	 * 21郑多燕(小红帽版)  22茉莉减肥操 23户外跑 */
	public int sportType;                   // 换算值    equ * 体重值（千卡）/60分钟
	public int type;                   // 录入类型  1手机自动(户外跑) 2手动录入

	public MovementEntity(int icon, String name, float equValue) {
		this.icon = icon;
		this.name = name;
		this.equValue = equValue;
	}

    public MovementEntity(int icon, String name, float equValue, int sportType)
    {
        this.name = name;
        this.icon = icon;
        this.equValue = equValue;
        this.sportType = sportType;
    }
    public MovementEntity(String name, int icon, float equValue, int sportType, int type)
    {
        this.name = name;
        this.icon = icon;
        this.equValue = equValue;
        this.sportType = sportType;
        this.type = type;
    }

    @Override
	public String toString()
	{
		return "MovementEntity{" +
				"name='" + name + '\'' +
				", icon=" + icon +
				", equValue=" + equValue +
				", sportType=" + sportType +
				", type=" + type +
				'}';
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof MovementEntity)) return false;
		MovementEntity that = (MovementEntity) o;
		return getIcon() == that.getIcon() &&
				Float.compare(that.getEquValue(), getEquValue()) == 0 &&
				Float.compare(that.getSportType(), getSportType()) == 0 &&
				Float.compare(that.getType(), getType()) == 0 &&
				Objects.equals(getName(), that.getName());
	}


	public int getSportType()
	{
		return sportType;
	}

	public void setSportType(int sportType)
	{
		this.sportType = sportType;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getIcon()
	{
		return icon;
	}

	public void setIcon(int icon)
	{
		this.icon = icon;
	}

	public float getEquValue()
	{
		return equValue;
	}

	public void setEquValue(float equValue)
	{
		this.equValue = equValue;
	}
}
