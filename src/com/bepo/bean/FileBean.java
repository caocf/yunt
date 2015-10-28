package com.bepo.bean;

import com.zhy.tree.bean.TreeNodeId;
import com.zhy.tree.bean.TreeNodeLabel;
import com.zhy.tree.bean.TreeNodePid;

public class FileBean
{
	@TreeNodeId
	private String _id;
	@TreeNodePid
	private String parentId;
	@TreeNodeLabel
	private String name;
	private long length;
	private String desc;

	public FileBean(String _id, String parentId, String name)
	{
		super();
		this._id = _id;
		this.parentId = parentId;
		this.name = name;
	}

}
