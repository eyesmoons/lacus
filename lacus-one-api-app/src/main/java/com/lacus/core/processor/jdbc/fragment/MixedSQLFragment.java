package com.lacus.core.processor.jdbc.fragment;



import com.lacus.core.processor.jdbc.meta.Context;

import java.util.List;

public class MixedSQLFragment extends AbstractFragment {

	private List<AbstractFragment> contents ;

	public MixedSQLFragment(List<AbstractFragment> contents){
		this.contents  = contents ;
	}

	@Override
	public boolean apply(Context context) {

		for(AbstractFragment sf : contents){
			sf.apply(context);
		}

		return true;
	}





}
