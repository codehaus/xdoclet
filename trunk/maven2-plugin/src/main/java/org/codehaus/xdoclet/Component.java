package org.codehaus.xdoclet;

import java.util.Map;

/**
 * specifies component to be used  within xdoclet invocation
 * @author Konstantin Pribluda
 * @version $Revision$
 *
 */
public class Component {
	private String classname;
	private Map params;
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
