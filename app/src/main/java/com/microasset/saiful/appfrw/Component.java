package com.microasset.saiful.appfrw;

public class Component extends DataObject {
	protected String mName;
	private Component mParent;

	/**
	 * @return the mParent
	 */
	public Component getmParent() {
		return mParent;
	}

	/**
	 * @param mParent the mParent to set
	 */
	public void setmParent(Component mParent) {
		this.mParent = mParent;
	}

	public Component(long id, String title) {
		super(id);
		// TODO Auto-generated constructor stub
		mName = title;
		this.setValue("title", title);
	}

	public String getTitle() {
		return mName;
	}
	public void setTitle(String title) {
		mName = title;
		this.setValue("title", title);
	}

	public void add(Component c) {
	}

	public boolean remove(Component c) {
		return false;
	}
}
