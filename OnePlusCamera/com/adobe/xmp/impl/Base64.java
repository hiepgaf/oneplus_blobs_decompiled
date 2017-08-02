// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

public class Base64
{
    private static final byte EQUAL = -3;
    private static final byte INVALID = -1;
    private static final byte WHITESPACE = -2;
    private static byte[] ascii;
    private static byte[] base64;
    
    static {
        final int n = 13;
        final int n2 = 10;
        final int n3 = 9;
        int i = 0;
        final byte b = -2;
        final byte[] base64 = new byte[64];
        base64[0] = 65;
        base64[1] = 66;
        base64[2] = 67;
        base64[3] = 68;
        base64[4] = 69;
        base64[5] = 70;
        base64[6] = 71;
        base64[7] = 72;
        base64[8] = 73;
        base64[n3] = 74;
        base64[n2] = 75;
        base64[11] = 76;
        base64[12] = 77;
        base64[n] = 78;
        base64[14] = 79;
        base64[15] = 80;
        base64[16] = 81;
        base64[17] = 82;
        base64[18] = 83;
        base64[19] = 84;
        base64[20] = 85;
        base64[21] = 86;
        base64[22] = 87;
        base64[23] = 88;
        base64[24] = 89;
        base64[25] = 90;
        base64[26] = 97;
        base64[27] = 98;
        base64[28] = 99;
        base64[29] = 100;
        base64[30] = 101;
        base64[31] = 102;
        base64[32] = 103;
        base64[33] = 104;
        base64[34] = 105;
        base64[35] = 106;
        base64[36] = 107;
        base64[37] = 108;
        base64[38] = 109;
        base64[39] = 110;
        base64[40] = 111;
        base64[41] = 112;
        base64[42] = 113;
        base64[43] = 114;
        base64[44] = 115;
        base64[45] = 116;
        base64[46] = 117;
        base64[47] = 118;
        base64[48] = 119;
        base64[49] = 120;
        base64[50] = 121;
        base64[51] = 122;
        base64[52] = 48;
        base64[53] = 49;
        base64[54] = 50;
        base64[55] = 51;
        base64[56] = 52;
        base64[57] = 53;
        base64[58] = 54;
        base64[59] = 55;
        base64[60] = 56;
        base64[61] = 57;
        base64[62] = 43;
        base64[63] = 47;
        Base64.base64 = base64;
        Base64.ascii = new byte[255];
        for (int j = 0; j < 255; ++j) {
            Base64.ascii[j] = -1;
        }
        while (i < Base64.base64.length) {
            Base64.ascii[Base64.base64[i]] = (byte)i;
            ++i;
        }
        Base64.ascii[n3] = b;
        Base64.ascii[n2] = b;
        Base64.ascii[n] = b;
        Base64.ascii[32] = b;
        Base64.ascii[61] = -3;
    }
    
    public static final String decode(final String s) {
        return new String(decode(s.getBytes()));
    }
    
    public static final byte[] decode(final byte[] array) {
        int i = 0;
        int j = 0;
        int n = 0;
        while (j < array.length) {
            final byte b = Base64.ascii[array[j]];
            if (b < 0) {
                if (b == -1) {
                    throw new IllegalArgumentException("Invalid base 64 string");
                }
            }
            else {
                final int n2 = n + 1;
                array[n] = b;
                n = n2;
            }
            ++j;
        }
        while (n > 0 && array[n - 1] == -3) {
            --n;
        }
        final byte[] array2 = new byte[n * 3 / 4];
        int n3 = 0;
        while (i < array2.length - 2) {
            array2[i] = (byte)((array[n3] << 2 & 0xFF) | (array[n3 + 1] >>> 4 & 0x3));
            array2[i + 1] = (byte)((array[n3 + 1] << 4 & 0xFF) | (array[n3 + 2] >>> 2 & 0xF));
            array2[i + 2] = (byte)((array[n3 + 2] << 6 & 0xFF) | (array[n3 + 3] & 0x3F));
            n3 += 4;
            i += 3;
        }
        if (i < array2.length) {
            array2[i] = (byte)((array[n3] << 2 & 0xFF) | (array[n3 + 1] >>> 4 & 0x3));
        }
        final int n4 = i + 1;
        if (n4 < array2.length) {
            array2[n4] = (byte)((array[n3 + 2] >>> 2 & 0xF) | (array[n3 + 1] << 4 & 0xFF));
        }
        return array2;
    }
    
    public static final String encode(final String s) {
        return new String(encode(s.getBytes()));
    }
    
    public static final byte[] encode(final byte[] array) {
        return encode(array, 0);
    }
    
    public static final byte[] encode(final byte[] array, final int n) {
        final int n2 = 16515072;
        final int n3 = 258048;
        final byte b = 61;
        int n4 = 0;
        int n5 = n / 4 * 4;
        if (n5 < 0) {
            n5 = 0;
        }
        int n6 = (array.length + 2) / 3 * 4;
        if (n5 > 0) {
            n6 += (n6 - 1) / n5;
        }
        final byte[] array2 = new byte[n6];
        int n7 = 0;
        int n8 = 0;
        while (n7 + 3 <= array.length) {
            final int n9 = n7 + 1;
            final int n10 = (array[n7] & 0xFF) << 16;
            final int n11 = n9 + 1;
            final int n12 = (array[n9] & 0xFF) << 8 | n10;
            n7 = n11 + 1;
            final int n13 = n12 | (array[n11] & 0xFF) << 0;
            final int n14 = (n13 & n2) >> 18;
            final int n15 = n8 + 1;
            array2[n8] = Base64.base64[n14];
            final int n16 = (n13 & n3) >> 12;
            final int n17 = n15 + 1;
            array2[n15] = Base64.base64[n16];
            final int n18 = (n13 & 0xFC0) >> 6;
            final int n19 = n17 + 1;
            array2[n17] = Base64.base64[n18];
            final int n20 = n13 & 0x3F;
            n8 = n19 + 1;
            array2[n19] = Base64.base64[n20];
            n4 += 4;
            if (n8 < n6 && n5 > 0 && n4 % n5 == 0) {
                final int n21 = n8 + 1;
                array2[n8] = 10;
                n8 = n21;
            }
        }
        if (array.length - n7 != 2) {
            if (array.length - n7 == 1) {
                final int n22 = (array[n7] & 0xFF) << 16;
                final int n23 = (n22 & n2) >> 18;
                final int n24 = n8 + 1;
                array2[n8] = Base64.base64[n23];
                final int n25 = (n22 & n3) >> 12;
                final int n26 = n24 + 1;
                array2[n24] = Base64.base64[n25];
                array2[n26 + 1] = (array2[n26] = b);
            }
        }
        else {
            final int n27 = (array[n7] & 0xFF) << 16 | (array[n7 + 1] & 0xFF) << 8;
            final int n28 = (n27 & n2) >> 18;
            final int n29 = n8 + 1;
            array2[n8] = Base64.base64[n28];
            final int n30 = (n27 & n3) >> 12;
            final int n31 = n29 + 1;
            array2[n29] = Base64.base64[n30];
            final int n32 = (n27 & 0xFC0) >> 6;
            final int n33 = n31 + 1;
            array2[n31] = Base64.base64[n32];
            array2[n33] = b;
        }
        return array2;
    }
}
