package idea;

import java.util.Optional;

public class KeyGenerator {
    private static final int rounds = 8;
    private String key;
    private Optional<int[]> encodeKeys = Optional.empty();
    private Optional<int[]> decodeKeys = Optional.empty();

    public KeyGenerator(String key) {
        if (key.length() != 16) {
            throw new IllegalArgumentException("Длина ключа должна составлять 128 бит");
        }
        this.key = key;
    }

    public int[] generateEncodingKeys() {
        if (encodeKeys.isPresent())
            return encodeKeys.get();

        byte[] byteKey = Utils.stringToByteArray(key);

        int[] keys = new int[rounds * 6 + 4];

        for (int i = 0; i < byteKey.length / 2; i++)
            keys[i] = ((byteKey[2 * i] & 0xFF) << 8) | (byteKey[2 * i + 1] & 0xFF);

        for (int i = byteKey.length / 2; i < keys.length; i++)
            keys[i] = ((keys[(i + 1) % 8 != 0 ? i - 7 : i - 15] << 9) | (keys[(i + 2) % 8 < 2 ? i - 14 : i - 6] >> 7)) & 0xFFFF;

        encodeKeys = Optional.of(keys);
        return keys;
    }

    public int[] generateDecodingKeys() {
        if (decodeKeys.isPresent())
            return decodeKeys.get();

        int[] encKeys = generateEncodingKeys();
        int[] decKeys = new int[encKeys.length];
        int p = 0;
        int i = rounds * 6;
        decKeys[i] =     Utils.mulInv(encKeys[p++]);
        decKeys[i + 1] = Utils.addInv(encKeys[p++]);
        decKeys[i + 2] = Utils.addInv(encKeys[p++]);
        decKeys[i + 3] = Utils.mulInv(encKeys[p++]);
        for (int r = rounds - 1; r >= 0; r--) {
            i = r * 6;
            int m = r > 0 ? 2 : 1;
            int n = r > 0 ? 1 : 2;
            decKeys[i + 4] =        encKeys[p++];
            decKeys[i + 5] =        encKeys[p++];
            decKeys[i] =     Utils.mulInv(encKeys[p++]);
            decKeys[i + m] = Utils.addInv(encKeys[p++]);
            decKeys[i + n] = Utils.addInv(encKeys[p++]);
            decKeys[i + 3] = Utils.mulInv(encKeys[p++]);
        }

        decodeKeys = Optional.of(decKeys);
        return decKeys;
    }
}
