/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.DefaultDocletTag;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class XDocletTag extends DefaultDocletTag {
	protected boolean isOnConstructor;

	protected boolean isOnMethod;

	protected boolean isOnField;

	protected boolean isOnClass;

	protected QDoxPropertyExpander expander;

	private Map namedParametersExpanded;

	private String[] parametersExpanded;

	protected XDocletTag(String name, String value, AbstractBaseJavaEntity context,
			int lineNumber) {
		this(name, value, context, lineNumber, null);
	}

	protected XDocletTag(String name, String value, AbstractBaseJavaEntity context,
			int lineNumber, QDoxPropertyExpander expander) {
		super(name, value, context, lineNumber);
		if (JavaMethod.class.isAssignableFrom(getContext().getClass())) {
			JavaMethod method = (JavaMethod) getContext();
			isOnConstructor = method.isConstructor();
			isOnMethod = !method.isConstructor();
		} else if (JavaField.class.isAssignableFrom(getContext().getClass())) {
			isOnField = true;
		} else {
			isOnClass = true;
		}
		this.expander = expander;
		validateLocation();
	}

	protected abstract void validateLocation();

	public final void bomb(String message) {
		throw new RuntimeException("@" + getName() + " " + getValue()
				+ "\n in "
				+ org.generama.ConfigurableDocletTagFactory.getLocation(this)
				+ " (line " + getLineNumber() + "):\n" + message);
	}

	public boolean isOnConstructor() {
		return isOnConstructor;
	}

	public void setOnConstructor(boolean onConstructor) {
		isOnConstructor = onConstructor;
	}

	public boolean isOnMethod() {
		return isOnMethod;
	}

	public void setOnMethod(boolean onMethod) {
		isOnMethod = onMethod;
	}

	public boolean isOnField() {
		return isOnField;
	}

	public void setOnField(boolean onField) {
		isOnField = onField;
	}

	public boolean isOnClass() {
		return isOnClass;
	}

	public void setOnClass(boolean onClass) {
		isOnClass = onClass;
	}

	public abstract void validateModel();
	
	
		public Map getNamedParameterMap() {
	        if (namedParametersExpanded == null) {
	        	namedParametersExpanded = super.getNamedParameterMap();
	            // If exists an expander, expands all map values
	            if (expander != null) {
	           	Set entries = namedParametersExpanded.entrySet();
	                for (Iterator iter = entries.iterator(); iter.hasNext();) {
	                	Entry entry = (Entry) iter.next();
	                   entry.setValue(expander.expand((String)entry.getValue()));
	                }
	            }
	        }
	        return namedParametersExpanded;
		}
	
		public String[] getParameters() {
	        if (parametersExpanded == null) {
	            parametersExpanded = super.getParameters();
	            // If exists an expander, expands all array values
	            if (expander != null) {
	                for (int i = 0; i < parametersExpanded.length; i++) {
	                	parametersExpanded[i] = expander.expand(parametersExpanded[i]);
	                }
	            }
	        }
	        return parametersExpanded;
		}
}
