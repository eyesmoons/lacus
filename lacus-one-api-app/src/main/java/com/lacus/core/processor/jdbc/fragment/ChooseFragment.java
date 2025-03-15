package com.lacus.core.processor.jdbc.fragment;


import com.lacus.core.processor.jdbc.meta.Context;

import java.util.List;

public class ChooseFragment extends AbstractFragment {

	private AbstractFragment defaultSQLFragment;
	private List<AbstractFragment> ifSQLFragments;

	public ChooseFragment(List<AbstractFragment> ifSQLFragments,
						  AbstractFragment defaultSQLFragment) {
		this.ifSQLFragments = ifSQLFragments;
		this.defaultSQLFragment = defaultSQLFragment;
	}

	@Override
	public boolean apply(Context context) {
		for (AbstractFragment sqlNode : ifSQLFragments) {
			if (sqlNode.apply(context)) {
				return true;
			}
		}
		if (defaultSQLFragment != null) {
			defaultSQLFragment.apply(context);
			return true;
		}
		return false;
	}

}
