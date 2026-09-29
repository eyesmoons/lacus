package com.lacus.core.processor.jdbc.fragment;

import ognl.*;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OgnlCache {

	private static final Map<String, Node> expressionCache = new ConcurrentHashMap<String, Node>();

	public static Object getValue(String expression, Object root) {
		try {
			return Ognl.getValue(parseExpression(expression), root);
		} catch (OgnlException e) {
			throw new RuntimeException("Error evaluating expression '"
					+ expression + "'. Cause: " + e, e);
		}
	}

	private static Object parseExpression(String expression)
			throws OgnlException {
		try {
			Node node = expressionCache.get(expression);
			if (node == null) {
				node = new OgnlParser(new StringReader(expression))
						.topLevelExpression();
				expressionCache.put(expression, node);
			}
			return node;
		} catch (ParseException e) {
			throw new ExpressionSyntaxException(expression, e);
		} catch (TokenMgrError e) {
			throw new ExpressionSyntaxException(expression, e);
		}
	}

}
