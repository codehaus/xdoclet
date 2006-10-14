/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet.ant;

import org.apache.tools.ant.types.FileSet;
import org.generama.Generama;
import org.generama.ant.AbstractGeneramaTask;
import org.generama.defaults.FileWriterMapper;
import org.picocontainer.MutablePicoContainer;
import org.xdoclet.QDoxPropertyExpander;
import org.xdoclet.XDoclet;
import org.xdoclet.tools.PropertiesQDoxPropertyExpander;
import org.xdoclet.tools.SystemQDoxPropertyExpander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDocletTask extends AbstractGeneramaTask {
	Collection filesets = new ArrayList();

	String encoding = System.getProperty("file.encoding");

	Boolean verbose = Boolean.TRUE;

	Collection properties = new ArrayList();

	protected Generama createGenerama() {
		return new XDoclet(AntSourceProvider.class, FileWriterMapper.class) {
			public void composeContainer(MutablePicoContainer pico, Object scope) {
				super.composeContainer(pico, scope);
				pico.registerComponentInstance(filesets);
				pico.registerComponentInstance(getProject());
				pico.registerComponentInstance(encoding);
				pico.registerComponentInstance(verbose);
				pico.registerComponentImplementation(PropertyComposer.class);
				pico.registerComponentImplementation(SystemQDoxPropertyExpander.class);
				pico.registerComponentInstance(new MyPropertiesQDoxPropertyExpander());
			}
		};
	}

	public void addFileset(FileSet fileSet) {
		filesets.add(fileSet);
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setVerbose(Boolean verbose) {
		this.verbose = verbose;
	}

	public void addProperties(Properties props) {
		if (props.getId() == null) {
			throw new IllegalArgumentException(
					"Properties \"id\" cannot be null");
		}

		if (props.getFile() == null) {
			throw new IllegalArgumentException(
					"Properties \"file\" cannot be null");
		}

		properties.add(props);
	}

	public class MyPropertiesQDoxPropertyExpander extends
			PropertiesQDoxPropertyExpander {
		public MyPropertiesQDoxPropertyExpander() {
			for (Iterator iter = properties.iterator(); iter.hasNext();) {
				Properties props = (Properties) iter.next();
				super.addProperties(props.getId(), props.getProperties());
			}
		}
	}

	public static class PropertyComposer implements QDoxPropertyExpander {
		private Collection expanders = new ArrayList();

		public PropertyComposer(SystemQDoxPropertyExpander sExp,
				PropertiesQDoxPropertyExpander pExp) {
			expanders.add(sExp);
			expanders.add(pExp);
		}

		public String expand(String value) {
			for (Iterator iter = expanders.iterator(); iter.hasNext();) {
				QDoxPropertyExpander expander = (QDoxPropertyExpander) iter
						.next();

				if (expander != null) {
					value = expander.expand(value);
				}
			}

			return value;
		}
	}
}
