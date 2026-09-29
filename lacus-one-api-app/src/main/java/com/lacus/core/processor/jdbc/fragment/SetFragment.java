package com.lacus.core.processor.jdbc.fragment;

import java.util.Arrays;
import java.util.List;

public class SetFragment extends TrimFragment {

	private static List<String> suffixList = Arrays.asList(",");

	public SetFragment(AbstractFragment contents) {
		super(contents, "SET", null, null, suffixList);
	}

}
