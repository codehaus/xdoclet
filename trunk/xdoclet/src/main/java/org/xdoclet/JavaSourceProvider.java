package org.xdoclet;

import java.util.Collection;
import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface JavaSourceProvider {
    /**
     * @return a Collection of {@link java.io.File} or {@link java.io.Reader}.
     */
    Collection getURLs() throws IOException;

    /**
     * @return encoding
     */
    String getEncoding();
}
