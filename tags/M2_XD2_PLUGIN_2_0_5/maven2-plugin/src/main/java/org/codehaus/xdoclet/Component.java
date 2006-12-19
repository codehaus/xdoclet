package org.codehaus.xdoclet;

import java.util.Collections;
import java.util.Map;

/**
 * defines component to be used within xdoclet invocation
 * 
 * @author Konstantin Pribluda
 * @version $Revision$
 * 
 */
public class Component {
	/**
	 * @parameter
	 * @required
	 */
	private String classname;
	private Map params = Collections.EMPTY_MAP;

	public String getClassname() {
		return classname;
	}

	public Map getParams() {
		return params;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public void setParams(Map params) {
		this.params = params;
	}
}
