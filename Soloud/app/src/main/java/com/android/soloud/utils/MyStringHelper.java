package com.android.soloud.utils;

/**
 * Created by f.stamopoulos on 16/2/2017.
 */

public class MyStringHelper {

    public static boolean isNoE( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

}
