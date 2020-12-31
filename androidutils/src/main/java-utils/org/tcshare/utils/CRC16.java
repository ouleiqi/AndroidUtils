package org.tcshare.utils;

public class CRC16 {

    /**
     *
     * @param bytes
     * @return 两个字节的校验和，高低位已翻转
     */
    public static byte[] cal(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return new byte[]{(byte) CRC, (byte) ((CRC >> 8) & 0x000000FF)};
    }
}
