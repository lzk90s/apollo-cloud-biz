package pers.kun.publish.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : qihang.liu
 * @date 2021-06-04
 */
public class StringExtUtil {
    public static String ignoreCaseReplace(String source, String oldString, String newString) {
        Pattern p = Pattern.compile(oldString, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(source);
        return m.replaceAll(newString);
    }

    public static void main(String[] args) {
        String aa = "test aa Ba ff aaeAF hellO";
        System.out.println(ignoreCaseReplace(aa, "hello", ""));
    }
}
