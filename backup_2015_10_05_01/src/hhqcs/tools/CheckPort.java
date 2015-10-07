/*
 * A Stringként kapott portszámot ellenőrzi, hogy érvényes -e
 */
package hhqcs.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Gabesz
 */
public class CheckPort {

    private static final String PATTERN = "^[0-9]*";
    private static Pattern p;

    /**
     *
     * @param port String
     * @return match boolean
     */
    public static boolean check(String port) {
        p = Pattern.compile(PATTERN);
        Matcher m = p.matcher(port);
        boolean match = m.matches();
        if (match) {
            try {
                Integer i = Integer.parseInt(port);
                if (i > 65535) {
                    match = false;
                }

            } catch (Exception e) {
                match = false;
            }
        } else {
            match = false;
        }
        return match;
    }
}
