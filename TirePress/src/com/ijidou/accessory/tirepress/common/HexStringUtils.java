package com.ijidou.accessory.tirepress.common;

public class HexStringUtils {
    
    private final static String HEX_STRING = "0123456789ABCDEF";
    
    /**
     * 1449826355640 = 0x151906289B8
     * byte[0] byte[1] byte[2]
     * B8      89      62      90 51 01 00 00
     * @param l
     * @return
     */
    public static byte[] long2Bytes(long l) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) ((l >> 0) & 0x00000000000000FFL);
        bytes[1] = (byte) ((l >> 8) & 0x00000000000000FFL);
        bytes[2] = (byte) ((l >> 16) & 0x00000000000000FFL);
        bytes[3] = (byte) ((l >> 24) & 0x00000000000000FFL);
        bytes[4] = (byte) ((l >> 32) & 0x00000000000000FFL);
        bytes[5] = (byte) ((l >> 40) & 0x00000000000000FFL);
        bytes[6] = (byte) ((l >> 48) & 0x00000000000000FFL);
        bytes[7] = (byte) ((l >> 56) & 0x00000000000000FFL);
        return bytes;
    }

    /**
     * 1449826355640 = 0x151906289B8
     * byte[0] byte[1] byte[2]
     * B8      89      62      90 51 01 00 00
     * @param l
     * @return
     */
    public static long bytes2Long(byte[] bytes, int off) {
        return (   ( ((long)bytes[off + 0] << 0)  & 0x00000000000000FFL)
                |  ( ((long)bytes[off + 1] << 8)  & 0x000000000000FF00L)
                |  ( ((long)bytes[off + 2] << 16) & 0x0000000000FF0000L)
                |  ( ((long)bytes[off + 3] << 24) & 0x00000000FF000000L)
                |  ( ((long)bytes[off + 4] << 32) & 0x000000FF00000000L)
                |  ( ((long)bytes[off + 5] << 40) & 0x0000FF0000000000L)
                |  ( ((long)bytes[off + 6] << 48) & 0x00FF000000000000L)
                |  ( ((long)bytes[off + 7] << 56) & 0xFF00000000000000L) );
    }

    public static byte[] int2bytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((i >> 0) & 0x000000FFL);
        bytes[1] = (byte) ((i >> 8) & 0x000000FFL);
        bytes[2] = (byte) ((i >> 16) & 0x000000FFL);
        bytes[3] = (byte) ((i >> 24) & 0x000000FFL);
        return bytes;
    }

    public static int bytes2Int(byte[] bytes, int off) {
        return 
                (bytes[off + 0] <<  0 & 0x000000FF) | 
                (bytes[off + 1] <<  8 & 0x0000FF00) | 
                (bytes[off + 2] << 16 & 0x00FF0000) | 
                (bytes[off + 3] << 24 & 0xFF000000);
    }

    public static byte[] hexString2Bytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) HEX_STRING.indexOf(c);
    }

    public static String bytes2HexString(byte[] bytes, boolean withSpaces) {
        return bytes2HexString(bytes, 0, bytes.length, withSpaces);
    }
    
    public static String bytes2HexString(byte[] bytes) {
        return bytes2HexString(bytes, 0, bytes.length, false);
    }
    
    /**
     * <pre>
     * 1  -> 01;
     * 10 -> 0A
     * -1 -> FF
     * </pre>
     * @param b
     * @return
     */
    public static String byte2HexString(byte b) {
        return "" + HEX_STRING.charAt((b & 0x000000F0) >> 4) + HEX_STRING.charAt((b & 0x0000000F));
    }

    public static String bytes2HexString(byte[] bytes, int off, int len) {
        return bytes2HexString(bytes, off, len, false);
    }
    
    public static String bytes2HexString(byte[] bytes, int off, int len, boolean withSpaces) {
        if (bytes == null || off + len > bytes.length) {
            return null;
        }
        
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < len; i++) {
            int v = bytes[off + i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (withSpaces) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
    

    
//    public static void main(String[] args) {
//        byte[] bytes = {(byte)0xC7, (byte)0x4A,  (byte)0x79, (byte)0x90,  (byte)0x51, (byte)0x01, (byte)0x00, (byte)0x00};
//        
//        byte[] bytes2 = {(byte)0xaa, 0x55, 0x08, 0x73, 0x04, 0x00, (byte)0xd8, (byte)0xd8, 0x00, 0x00, 0x01, 0x01, 0x30};
//        
//        //aa 55 0e 72 19 00 00 00 00 00 ff ff ff 4c 23 0f ff 4b 5d
//        byte[] bytes3 = {
//                (byte)0xaa, 0x55, 0x0e, 0x72, 0x19, 
//                0x00, 0x00, 0x00, 0x00, 0x00, 
//                (byte)0xFF, (byte)0xFF, (byte)0xFF, 
//                (byte)0x4C, 0x23, 0x0f, (byte)0xFF, 0x4b, 0x5d, 0x08};
//        
//        //1449827846855
//        //2423868103
//        System.out.println(bytes2Long(bytes, 0));
//        
//        System.out.println(new Date(1450783478079l));
//        System.out.println(new Date(1450783569991l));
//        System.out.println(new Date(1450783571964l));
//        System.out.println(new Date(1450783575110l));
//        System.out.println(new Date(1450783577936l));
//        System.out.println(new Date(1450783579924l));
//        System.out.println(new Date(1450783582260l));
//        System.out.println(new Date(1450783607342l));
//        System.out.println(new Date(1450783608873l));
//        System.out.println(new Date(1450783610631l));
//        System.out.println(new Date(1450783613994l));
//        System.out.println(new Date(1450783637769l));
//        System.out.println(new Date(1450783639584l));
//        System.out.println(new Date(1450783642398l));
//    }
    
    /**
     * <pre>
     * <0   -> 0x00
     * 0    -> 0x00
     * 1    -> 0x01
     * 127  -> 0x7F
     * 128  -> 0x80
     * 129  -> 0x81
     * 254  -> 0xFE
     * 255  -> 0xFF
     * >255 -> 0xFF
     * </pre>
     * @param i
     * @return
     */
    public static byte unsignedInt2UnsignedByte(int i) {
        if (i < 0) {
            return 0x00;
        } else if (i > 255) {
            return (byte)0xFF;
        } else {
            return (byte)(i & 0x000000FF);
        }
    }

}
