package org.xdoclet.ant;

import org.generama.Generama;
import org.generama.ant.AbstractGeneramaTask;
import org.generama.defaults.FileWriterMapper;
import org.xdoclet.XDoclet;
import org.apache.tools.ant.types.FileSet;
import org.picocontainer.MutablePicoContainer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDocletTask extends AbstractGeneramaTask {
    private Collection filesets = new ArrayList();

    protected Generama createGenerama() {
        return new XDoclet(AntFileProvider.class, FileWriterMapper.class) {
            public void install(MutablePicoContainer pico) {
                super.install(pico);
                pico.registerComponentInstance(filesets);
                pico.registerComponentInstance(getProject());
            }
        };
    }

    public void addFileset(FileSet fileSet) {
        filesets.add(fileSet);
    }

//    public static class Tag {
//        public String name;
//        public Class clazz;
//
//        public void setName(String name) {
//            this.name = name;
//        }
//    }
//    private Collection addedTags = new ArrayList();
//
//    public Tag createTag() {
//        Tag tag = new Tag();
//        addedTags.add(tag);
//        return tag;
//    }

}
