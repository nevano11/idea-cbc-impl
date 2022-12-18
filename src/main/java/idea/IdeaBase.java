package idea;

import static idea.Utils.add;
import static idea.Utils.mul;
import static idea.Utils.xor;

public class IdeaBase {
    private KeyGenerator keyGenerator;
    private static final int rounds = 8;

    public IdeaBase(String key) {
        keyGenerator = new KeyGenerator(key);
    }

    public void encrypt(byte[] data, int position){
        crypt(data, position, keyGenerator.generateEncodingKeys());
    }

    public void decrypt(byte[] data, int position){
        crypt(data, position, keyGenerator.generateDecodingKeys());
    }

    private void crypt(byte[] data, int position, int[] cipherKeys){
        int x0 = ((data[position] & 0xFF) << 8) | (data[position+1] & 0xFF);
        int x1 = ((data[position+2] & 0xFF) << 8) | (data[position+3] & 0xFF);
        int x2 = ((data[position+4] & 0xFF) << 8) | (data[position+5] & 0xFF);
        int x3 = ((data[position+6] & 0xFF) << 8) | (data[position+7] & 0xFF);
        //
        int keysCounter = 0;
        for (int round = 0; round < rounds; round++)
        {
            int y0 = mul(x0, cipherKeys[keysCounter++]);
            int y1 = add(x1, cipherKeys[keysCounter++]);
            int y2 = add(x2, cipherKeys[keysCounter++]);
            int y3 = mul(x3, cipherKeys[keysCounter++]);

            int t0 = mul(y0 ^ y2, cipherKeys[keysCounter++]);
            int t1 = add(y1 ^ y3, t0);
            int t2 = mul(t1, cipherKeys[keysCounter++]);
            int t3 = add(t0, t2);

            x0 = xor(y0,t2);
            x1 = xor(y2,t2);
            x2 = xor(y1,t3);
            x3 = xor(y3,t3);
        }
        int r0 = mul(x0, cipherKeys[keysCounter++]);
        int r1 = add(x2, cipherKeys[keysCounter++]);
        int r2 = add(x1, cipherKeys[keysCounter++]);
        int r3 = mul(x3, cipherKeys[keysCounter]);

        data[position] = (byte)(r0 >> 8);
        data[position+1] = (byte)r0;
        data[position+2] = (byte)(r1 >> 8);
        data[position+3] = (byte)r1;
        data[position+4] = (byte)(r2 >> 8);
        data[position+5] = (byte)r2;
        data[position+6] = (byte)(r3 >> 8);
        data[position+7] = (byte)r3;
    }
}
