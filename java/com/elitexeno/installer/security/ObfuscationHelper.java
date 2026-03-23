package com.elitexeno.installer.security;

public class ObfuscationHelper {

    public static String build(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            sb.append(p);
        }
        return sb.toString();
    }

    public static String hide() {
        return new String(new char[]{'d','a','t','a','.','b','i','n'});
    }
}