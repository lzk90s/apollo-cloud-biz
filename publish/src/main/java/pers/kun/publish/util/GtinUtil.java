package pers.kun.publish.util;

/**
 * @author : qihang.liu
 * @date 2021-09-29
 */
public class GtinUtil {
    public static String genGtin() {
        int pow = 12;
        long value = randomBetween((long) Math.pow(10, pow), (long) Math.pow(10, pow + 1));
        int chk = cks(value);
        return value + String.valueOf(chk);
    }

    private static int cks(long code) {
        int total = 0;
        String value = String.valueOf(code);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (i % 2 == 1) {
                total = total + c;
            } else {
                total = total + 3 * c;
            }
        }
        return (10 - (total % 10)) % 10;
    }

    private static long randomBetween(long min, long max) {
        double s = min + Math.random() * (max - min);
        return (long) s;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(genGtin());
        }
    }
}
