package com.teachersspace.search;

public class WordDistance {
    public static int distance( String a, String b) {
        if (b.length() == 0) return a.length();
        if (a.length() == 0) return b.length();

        if (a.charAt(0) == b.charAt(0)) {
            return distance(a.substring(1), b.substring(1));
        }
        else {
            int c1 = Math.min(distance(a, b.substring(1)), (distance(a.substring(1), b.substring(1))));
            int c2 = Math.min(c1, distance(a.substring(1), b));
            return 1 + c2;
        }

    }
}
