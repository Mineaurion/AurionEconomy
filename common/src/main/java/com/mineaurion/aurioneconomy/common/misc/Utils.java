package com.mineaurion.aurioneconomy.common.misc;

import java.util.regex.Pattern;

public class Utils {

    private final static Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(str).matches();
    }
}

/**
 * @Testable
 * public class UtilTests {
 *     @Test
 *     public void is_valid_uuid_ok(){
 *         assertTrue(Utils.isValidUUID("009692ee-f930-4a74-bbd0-63b8baa5a927"));
 *     }
 *     @Test
 *     public void is_valid_uuid_false(){
 *         assertTrue(!Utils.isValidUUID(null));
 *         assertTrue(!Utils.isValidUUID(""));
 *         assertTrue(!Utils.isValidUUID("test-ss-ss-ss-s"));
 *         assertTrue(!Utils.isValidUUID("009692ee-f9309-4a74-bbd0-63b8baa5a927"));
 *         assertTrue(!Utils.isValidUUID("1-1-1-1"));
 *     }
 * }
 */
