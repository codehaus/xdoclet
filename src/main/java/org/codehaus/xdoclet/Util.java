package org.codehaus.xdoclet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gjoseph
 * @version $Revision: $ ($Author: $)
 */
public class Util {

    static String[] toTrimmedStringArray(String commaSeparated) {
        final List list = toTrimmedList(commaSeparated);
        return (String[]) list.toArray(new String[list.size()]);
    }

    static List toTrimmedList(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.length() == 0) {
            return Collections.EMPTY_LIST;
        }
        final List list = new ArrayList();
        final String[] strings = commaSeparated.split(",", 0);
        for (int i = 0; i < strings.length; i++) {
            final String string = strings[i].trim();
            if (string.length() > 0) {
                list.add(string);
            }
        }
        return list;
    }
}
