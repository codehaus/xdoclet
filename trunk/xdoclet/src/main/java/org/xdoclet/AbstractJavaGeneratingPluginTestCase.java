/*
 * AbstractJavaGeneratingPluginTestCase.java
 * Copyright 2004-2004 Bill2, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xdoclet;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.thoughtworks.qdox.junit.APITestCase;
import org.generama.MetadataProvider;
import org.generama.astunit.ASTTestCase;
import org.generama.tests.AbstractPluginTestCase;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * Baseclass for testing generation of Java sources. Uses QDox'
 * APITestCase internally to compare equality of Java Sources.
 *
 * TODO move to Generama, this will be needed for RdbmsTableFromJdbcMetadataProvider too.
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractJavaGeneratingPluginTestCase extends AbstractPluginTestCase {

    protected final void compare(URL expected, URL actual) throws RecognitionException, TokenStreamException, IOException {
        APITestCase.assertApiEquals(expected, actual);
        ASTTestCase.assertAstEquals(expected, actual);
    }

    private char[] toCharArray(Reader reader) throws IOException {
        CharArrayWriter expectedMemory = new CharArrayWriter();
        char[] buffer = new char[1024];
        int read;
        while( (read = reader.read(buffer)) != -1 ) {
            expectedMemory.write(buffer, 0, read);
        }
        return expectedMemory.toCharArray();
    }

    protected MetadataProvider createMetadataProvider() throws Exception {
        return new QDoxMetadataProvider(getTestSource());
    }

    protected URL getTestSource() throws IOException {
        return null;
    }
}
