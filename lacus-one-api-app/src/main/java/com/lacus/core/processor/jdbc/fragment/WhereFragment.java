package com.lacus.core.processor.jdbc.fragment;

import java.util.Arrays;
import java.util.List;

public class WhereFragment extends TrimFragment{

	private static List<String> prefixList = Arrays.asList("AND ","OR ","AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

	public WhereFragment(AbstractFragment contents) {
		super(contents, "WHERE", null,prefixList , null);
	}

}
