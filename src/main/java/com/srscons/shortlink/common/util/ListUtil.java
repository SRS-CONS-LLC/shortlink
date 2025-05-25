package com.srscons.shortlink.common.util;

import java.util.Arrays;
import java.util.List;

public final class ListUtil {

    private ListUtil() {
        //
    }

    public static <T> List<T> _list(T... elements) {
        return Arrays.asList(elements);
    }

}
