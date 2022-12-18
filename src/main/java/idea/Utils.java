package idea;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Utils {
    private static final Charset charset = StandardCharsets.US_ASCII;

    public static int addInv (int x) {
        return (0x10000 - x) & 0xFFFF;
    }
    public static int mulInv (int x) {
        if (x <= 1) {
            return x;
        }
        int y = 0x10001;
        int t0 = 1;
        int t1 = 0;
        while (true) {
            t1 += y / x * t0;
            y %= x;
            if (y == 1) {
                return 0x10001 - t1;
            }
            t0 += x / y * t1;
            x %= y;
            if (x == 1) {
                return t0;
            }
        }
    }

    public static int add(int a, int b) {
        return (a + b) & 0xFFFF;
    }

    public static int mul(int a, int b) {
        long r = (long) a * (long) b;
        if (r != 0)
            return (int)(r % 0x10001) & 0xFFFF;
        else
            return (1 - a - b) & 0xFFFF;
    }

    public static int xor(int a, int b) {
        return a ^ b;
    }

    public static String bytesToString(byte[] data) {
        return new String(data);
    }

    public static byte[] stringToByteArray(String str) {
        return str.getBytes(charset);
    }
}
