// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.io.UnsupportedEncodingException;

public class Latin1Converter
{
    private static final int STATE_START = 0;
    private static final int STATE_UTF8CHAR = 11;
    
    public static ByteBuffer convert(final ByteBuffer byteBuffer) {
        final int n = 128;
        final int n2 = 11;
        final int n3 = 8;
        int i = 0;
        if (!"UTF-8".equals(byteBuffer.getEncoding())) {
            return byteBuffer;
        }
        final byte[] array = new byte[n3];
        final ByteBuffer byteBuffer2 = new ByteBuffer(byteBuffer.length() * 4 / 3);
        int j = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        while (j < byteBuffer.length()) {
            final int char1 = byteBuffer.charAt(j);
            int n7 = 0;
            switch (n4) {
                default: {
                    if (char1 < 127) {
                        byteBuffer2.append((byte)char1);
                        n7 = n6;
                        break;
                    }
                    if (char1 < 192) {
                        byteBuffer2.append(convertToUTF8((byte)char1));
                        n7 = n6;
                        break;
                    }
                    int n8 = -1;
                    int n10;
                    for (int n9 = char1; n8 < n3 && (n9 & 0x80) == n; n9 <<= 1, n8 = n10) {
                        n10 = n8 + 1;
                    }
                    final int n11 = n6 + 1;
                    array[n6] = (byte)char1;
                    n7 = n11;
                    n5 = n8;
                    n4 = n2;
                    break;
                }
                case 11: {
                    if (n5 <= 0 || (char1 & 0xC0) != n) {
                        byteBuffer2.append(convertToUTF8(array[0]));
                        j -= n6;
                        n4 = 0;
                        n7 = 0;
                        break;
                    }
                    n7 = n6 + 1;
                    array[n6] = (byte)char1;
                    --n5;
                    if (n5 == 0) {
                        byteBuffer2.append(array, 0, n7);
                        n4 = 0;
                        n7 = 0;
                        break;
                    }
                    break;
                }
            }
            ++j;
            n6 = n7;
        }
        if (n4 == n2) {
            while (i < n6) {
                byteBuffer2.append(convertToUTF8(array[i]));
                ++i;
            }
        }
        return byteBuffer2;
    }
    
    private static byte[] convertToUTF8(final byte b) {
        final int n = 1;
        final int n2 = b & 0xFF;
        if (n2 >= 128) {
            Label_0067: {
                if (n2 != 129) {
                    break Label_0067;
                }
            Label_0042:
                while (true) {
                    final int n3 = 1;
                    try {
                        final byte[] array = new byte[n3];
                        array[0] = 32;
                        return array;
                        // iftrue(Label_0042:, n2 == 141 || n2 == 143 || n2 == 144 || n2 == 157)
                        return new String(new byte[] { b }, "cp1252").getBytes("UTF-8");
                    }
                    catch (UnsupportedEncodingException ex) {
                        break;
                    }
                }
            }
        }
        final byte[] array2 = new byte[n];
        array2[0] = b;
        return array2;
    }
}
