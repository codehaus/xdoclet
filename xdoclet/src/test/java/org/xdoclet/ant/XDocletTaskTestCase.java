package org.xdoclet.ant;

import org.xdoclet.TestPlugin;
import org.xdoclet.QDoxMetadataProvider;
import org.xdoclet.ConfigurableDocletTagFactory;
import org.nanocontainer.ant.PicoContainerTask;
import org.nanocontainer.ant.AbstractPicoContainerTaskTestCase;
import org.nanocontainer.ant.Component;
import org.apache.tools.ant.types.FileSet;
import org.picocontainer.PicoInitializationException;

import java.io.File;
import java.util.Collection;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * @foo bla bla
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDocletTaskTestCase /*extends AbstractPicoContainerTaskTestCase*/ {
//MAKE ABSTRACT
//REMOVE REDUNDANT ASSERTS WITH XDOCLETTESTCASE. TEST ONLY ANT SPECIFIC THING
//THERE ARE LOTS OF GOOD ASSERTS THOUGH LIKE NO UNKNOWN TAGS
//    FIX THE FILE SUBS IN THE CONTENT!?!?!


//    protected PicoContainerTask createPicoContainerTask() {
//        return new XDocletTask();
//    }
//
//    public void testXDocletTasks() throws PicoInitializationException {
//        String basedir = System.getProperty("xdoclet.home");
//        assertNotNull("The xdoclet.home system property should be defined when tests are run", basedir);
//
//        Component test = new Component();
//        test.setClassname(SingleOutputPlugin.class.getName());
//        test.setDynamicAttribute("multioutput", "true");
//
//        test.setDynamicAttribute("destdir", basedir + "/target/test-output");
//
//        task.addConfiguredComponent(test);
//
//        // Make a fileset containing this very source
//        FileSet fileSet = new FileSet();
//        File testRoot = new File(basedir, "src/test/java");
//        fileSet.setDir(testRoot);
//        fileSet.setIncludes("**/XDocletTaskTestCase.java");
//        ((XDocletTask)task).addFileset(fileSet);
//
//        XDocletTask.Tag foo = ((XDocletTask)task).createTag();
//        foo.setName("foo");
//
//        task.execute();
//
//        // test that sources were parsed NOT NEEDED?
//        QDoxMetadataProvider qDoxMetadataProvider = (QDoxMetadataProvider) task.getPicoContainer().getComponentInstance(QDoxMetadataProvider.class);
//        assertNotNull(qDoxMetadataProvider);
//        Collection metadata = qDoxMetadataProvider.getMetadata();
//        assertEquals(1, metadata.size());
//
//        JavaClass thisClass = (JavaClass) metadata.toArray()[0];
//        assertEquals(getClass().getName(), thisClass.getFullyQualifiedName());
//
//        // test that the plugin was executed
//        SingleOutputPlugin testPlugin = (SingleOutputPlugin) task.getPicoContainer().getComponentInstance(SingleOutputPlugin.class);
//        assertTrue(testPlugin.wasExecuted);
//
//        // test that we do NOT get an unknown tag for the foo tag
//        ConfigurableDocletTagFactory tagFactory = (ConfigurableDocletTagFactory) task.getPicoContainer().getComponentInstance(ConfigurableDocletTagFactory.class);
//        assertEquals(0, tagFactory.getUnknownTags().size());
//    }
}
