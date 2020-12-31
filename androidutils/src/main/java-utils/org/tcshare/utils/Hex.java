package org.tcshare.utils;

import java.io.ByteArrayOutputStream;

public class Hex {

    private static String hexString = "0123456789ABCDEF";

    public static String encode(String str) {

        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            //  int d = (bytes[i]&0x0f)>>0;
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));//1-4
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));//1-2

            if(i!=(bytes.length-1)){
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        for (int i = 0; i < bytes.length(); i += 3) {
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        }
        return new String(baos.toByteArray());
    }

    public static String getHexString(byte[] b) throws Exception {

        String result = "";

        for (int i = 0; i < b.length; i++) {

            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
    public static String getHexString(byte[] b, int length){

        String result = "";

        for (int i = 0; i < length; i++) {

            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static byte[] strToHexBytes(String str){
        int i;
        for(i = 0;i < str.length();i++){
            if(!(str.charAt(i) >= '0' && str.charAt(i) <= '9') && !(str.charAt(i) >= 'a' && str.charAt(i) <= 'f')){
                return null;
            }
        }
        byte [] tmp = new byte[(str.length() + 1)/2];
        for(i = 0;i < tmp.length;i++){
            if(str.length() %2 == 1){
                if(i == (tmp.length -1)){
                    tmp[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 1), 16);
                } else {
                    tmp[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
                }
            } else {
                tmp[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
        }
        return tmp;
    }

}
