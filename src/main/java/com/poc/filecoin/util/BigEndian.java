package com.poc.filecoin.util;

/**
 * Class for converting long to various BigEndian ranges
 *
 * @author Vijay Pratap Singh
 */
public class BigEndian {

    /**
     * Method to convert long to 16 byte array (Unsigned 16 byte integer)
     *
     * @param v
     * @return byte[]
     */
    public static byte[] putUint16(long v) {
        byte[] b = new byte[2];
        b[0] = (byte) (v >> 8);
        b[1] = (byte) (v);
        return b;
    }


    /**
     * Method to convert long to 32 byte array (Unsigned 32 byte integer)
     *
     * @param v
     * @return byte[]
     */
    public static byte[] putUint32(long v) {
        byte[] b = new byte[4];
        b[0] = (byte) (v >> 24);
        b[1] = (byte) (v >> 16);
        b[2] = (byte) (v >> 8);
        b[3] = (byte) (v);
        return b;
    }


    /**
     * Method to convert long to 64 byte array (Unsigned 64 byte integer)
     *
     * @param v
     * @return byte[]
     */
    public static byte[] putUint64(long v) {
        byte[] b = new byte[8];
        b[0] = (byte) (v >> 56);
        b[1] = (byte) (v >> 48);
        b[2] = (byte) (v >> 40);
        b[3] = (byte) (v >> 32);
        b[4] = (byte) (v >> 24);
        b[5] = (byte) (v >> 16);
        b[6] = (byte) (v >> 8);
        b[7] = (byte) (v);
        return b;
    }
}
