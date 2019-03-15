package com.huidf.slimming.dynamic.model;

import java.util.LinkedList;
import java.util.List;

/**
 * 创建动态，选择照片
 * @author ZhuTao
 * @date 2018/12/18 
 * @params 
*/

public class CreDynSelectPicEntity {
	
	public String path;
	public String url;

	public CreDynSelectPicEntity(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
