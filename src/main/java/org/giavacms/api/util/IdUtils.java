package org.giavacms.api.util;

public class IdUtils {
    public static String createPageId(String title) {
        if (title == null)
            return null;
        title = title.trim().replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("[\\s]", "-");
        return title.toLowerCase();
    }
}
