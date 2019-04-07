package com.microasset.saiful.appfrw;

import java.util.Comparator;

public class CustomComparator implements Comparator<Component> {
    @Override
    public int compare(Component o1, Component o2) {
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
	