package com.lacus.core.processor.jdbc.fragment;


import com.lacus.core.processor.jdbc.meta.Context;

public class IfFragment extends AbstractFragment {

	private String test;

	private AbstractFragment contents;

	private ExpressionEvaluator expression ;

	public IfFragment(AbstractFragment contents, String test) {

		this.expression = new ExpressionEvaluator();
		this.contents = contents;
		this.test = test;
	}

	@Override
	public boolean apply(Context context) {
		if (expression.evaluateBoolean(test, context.getBinding())) {

			this.contents.apply(context);

			return true;
		}
		return false;
	}

}
