package idea;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String key = "idea cipher- cbc"; // 16 byte
        String iv = "initialV"; // 8 byte
        String text = "The text to be encrypted using the IDEA CBC method must be set in ASCII encoding";

        IdeaCBCImpl ideaCBC = new IdeaCBCImpl(key, iv);

        byte[] encrypt = ideaCBC.encrypt(text);
        String decrypt = ideaCBC.decrypt(encrypt);

        System.out.println(text);
        System.out.println(Arrays.toString(encrypt));
        System.out.println(decrypt);

        System.out.println("==============");
        String enc2 = byteArrayToString(encrypt);
        System.out.println(enc2);
        System.out.println(Arrays.toString(stringIntArrayToByteArray(enc2)));
    }

    public static String byteArrayToString(byte[] data) {
        return Arrays.stream(
                Arrays.toString(data)
                        .replace("[", "")
                        .replace("]", "")
                        .replace(",", "")
                        .split(" "))
                .map(Integer::parseInt)
                .map(Integer::toHexString)
                .collect(Collectors.joining(" "));
    }

    public static byte[] stringIntArrayToByteArray(String str) {
        List<Integer> intBytes = Arrays.stream(str.split(" "))
                .map(s -> Integer.parseInt(s, 16))
                .toList();
        byte[] result = new byte[intBytes.size()];

        for (int i = 0; i < intBytes.size(); i++) {
            result[i] = intBytes.get(i).byteValue();
        }
        return result;
    }
}
