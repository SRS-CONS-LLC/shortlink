package com.srscons.shortlink.auth.util;

import org.springframework.util.AntPathMatcher;

public class UrlAntMatcher {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final String url;

    private UrlAntMatcher(String url) {
        this.url = url;
    }

    public static UrlAntMatcher of(String url) {
        return new UrlAntMatcher(url);
    }

    public boolean notContainsAny(String... patterns) {
        for (String pattern : patterns) {
            if (ANT_PATH_MATCHER.match(pattern, url)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAny(String... patterns) {
        for (String pattern : patterns) {
            if (ANT_PATH_MATCHER.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(String... patterns) {
        for (String pattern : patterns) {
            if (!ANT_PATH_MATCHER.match(pattern, url)) {
                return false;
            }
        }
        return true;
    }
}
