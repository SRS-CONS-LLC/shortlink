package com.srscons.shortlink.auth.util;

import java.io.InputStream;
import java.io.InputStreamReader;

public class Resources {

    public static InputStreamReader resourceAsReader(String path) {
        return new InputStreamReader(Resources.class.getClassLoader()
                .getResourceAsStream(path));
    }

    public static InputStream resourceAsStream(String path) {
        return Resources.class.getClassLoader()
                .getResourceAsStream(path);
    }


}
