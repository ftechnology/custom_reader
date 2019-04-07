package com.microasset.saiful.appfrw;

import java.util.ArrayList;
import java.util.Collections;

public class Composite extends Component {
	public final static int TYPE_HEADER = 0;
	private ArrayList<Component> mList = new ArrayList<Component>();

	public ArrayList<Component> getList() {
		return mList;
	}

	public Composite(long id, String title) {
		super(id, title);
		// TODO Auto-generated constructor stub
	}

	public void clear() {
		mList.clear();
	}
	
	public void sort() {
		Collections.sort(mList, new CustomComparator());
		for (Component c : mList) {
			if (c instanceof Composite) {
				Collections.sort(((Composite) c).getList(),
						new CustomComparator());
			}
		}
	}

	@Override
	public void add(Component c) {
		if( !mList.contains(c) ) {
			if (c != null) {
				mList.add(c);
				c.setmParent(this);
			}
		}
	}

	@Override
	public boolean remove(Component c) {
		return mList.remove(c);
	}

	/**
	 * 
	 * @param title
	 * @return
	 */
	public Component getItem(String title) {
		for (Component c : mList) {
			if (c.getTitle() == title) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Check only first character
	 * 
	 * @param title
	 * @return
	 */
	public Component getItemStartWith(String title) {
		String sc = title.substring(0, 1);

		for (Component c : mList) {
			if (c.getTitle().startsWith(sc)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Add item as divider, make parent[divider] when no item is found with title.
	 * 
	 * @param title
	 */
	public Component addItemStartWith(String title) {
		Component com = getItemStartWith(title);
		Component child = null;
		
		if (com == null) {
			String sc = title.substring(0, 1);
			com = new Composite(0, sc);
			com.setValue("type", TYPE_HEADER);
			add(com);
			// Add First child to this item.
			child = new Component(0, title);
			com.add(child);
		} else {
			child = new Component(0, title);
			com.add(child);
		}
		
		return child;
	}
	
	public Composite addComposite(String title) {
		Composite com = new Composite(0, title);
		com.setValue("type", TYPE_HEADER);
		add(com);
		return com;
	}
	
	public Component addLeaf(String title) {
		Component com = new Component(0, title);
		add(com);
		return com;
	}
	
	/**
	 * Always add Component, not Composite.
	 * @param title
	 * @return
	 */
	public Component addItem(String title) {
		Component com = new Component(0, title);
		add(com);
		return com;
	}
}
