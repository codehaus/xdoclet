package org.codehaus.xdoclet;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * test case for mojo configuration
 * @author ko5tik
 * @version $Revision$
 */
public class TestMojoConfiguration extends AbstractMojoTestCase {

	
	public void testMojoConfiguration()  throws Exception {
	
		XDocletMojo mojo = getMojo("target/test-classes/configuration/plugin-config.xml");
		assertNotNull(mojo);
		
		// test that mojo got what we avaited from it
		
		// it has to have 2 configurations
		assertEquals(2,mojo.getConfigs().size());
		
		// test first configuration
		
		Config config = (Config) mojo.getConfigs().get(0);
		assertEquals(2,config.getParams().size());
		assertEquals("glam",config.getParams().get("blurge"));
		assertEquals("glim",config.getParams().get("glem"));
		
		assertEquals("**/*.java",config.getIncludes());
		assertEquals("**/No.java",config.getExcludes());
		
		
		//encoding must be set
		assertEquals("blabla",config.getEncoding());
		
		// it has to have 2 components
		assertEquals(2,config.getComponents().size());
		
		
	}
	public XDocletMojo getMojo(String pomXml) throws Exception {
		File pom = new File(getBasedir(),pomXml);
		return  (XDocletMojo) lookupMojo("xdoclet",pom);
	}
}
