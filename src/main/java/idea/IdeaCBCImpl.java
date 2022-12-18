package idea;

public class IdeaCBCImpl {
    private static final String nullSymbol = " ";
    private static final int blockSize = 8;
    private IdeaBase ideaBase;
    private byte[] previousBlock;
    private String initVector;

    public IdeaCBCImpl(String key, String initVector) {
        ideaBase = new IdeaBase(key);
        this.initVector = initVector;
        updateInitVector();
    }

    public void updateInitVector() {
        previousBlock = Utils.stringToByteArray(initVector);
    }

    public byte[] encrypt(String data) {
        data = dataLengthCheck(data);
        byte[] dataBytes = Utils.stringToByteArray(data);
        updateInitVector();

        for (int pos = 0; pos < dataBytes.length; pos += blockSize) {
            xorData(dataBytes, pos, previousBlock);
            ideaBase.encrypt(dataBytes, pos);
            System.arraycopy(dataBytes, pos, previousBlock, 0, blockSize);
        }

        return dataBytes;
    }

    public String decrypt(byte[] data) {
        updateInitVector();

        for (int pos = 0; pos < data.length; pos += blockSize) {
            byte[] startBlock = new byte[blockSize];
            System.arraycopy(data, pos, startBlock, 0, blockSize);
            ideaBase.decrypt(data, pos);
            xorData(data, pos, previousBlock);
            previousBlock = startBlock;
        }
        return Utils.bytesToString(data);
    }

    private String dataLengthCheck(String data) {
        StringBuilder dataBuilder = new StringBuilder(data);
        while (dataBuilder.length() % 8 != 0) {
            dataBuilder.append(nullSymbol);
        }
        return dataBuilder.toString();
    }

    private void xorData(byte[] data, int pos, byte[] second) {
        for (int i = 0; i < blockSize; i++) {
            data[pos + i] = (byte) (data[pos + i] ^ second[i]);
        }
    }
}
