package org.xdoclet;

import org.generama.*;

import java.io.IOException;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class TestPlugin implements Plugin {
    public boolean wasExecuted = false;

    public void execute() throws IOException, GeneramaException {
        wasExecuted = true;
    }
}
