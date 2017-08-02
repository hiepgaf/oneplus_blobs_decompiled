// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteBuffer
{
    private byte[] buffer;
    private String encoding;
    private int length;
    
    public ByteBuffer(final int n) {
        this.encoding = null;
        this.buffer = new byte[n];
        this.length = 0;
    }
    
    public ByteBuffer(final InputStream inputStream) {
        final int n = 16384;
        this.encoding = null;
        this.length = 0;
        this.buffer = new byte[n];
        while (true) {
            final int read = inputStream.read(this.buffer, this.length, n);
            if (read <= 0) {
                break;
            }
            this.length += read;
            if (read != n) {
                break;
            }
            this.ensureCapacity(this.length + 16384);
        }
    }
    
    public ByteBuffer(final byte[] buffer) {
        this.encoding = null;
        this.buffer = buffer;
        this.length = buffer.length;
    }
    
    public ByteBuffer(final byte[] buffer, final int length) {
        this.encoding = null;
        if (length <= buffer.length) {
            this.buffer = buffer;
            this.length = length;
            return;
        }
        throw new ArrayIndexOutOfBoundsException("Valid length exceeds the buffer length.");
    }
    
    public ByteBuffer(final byte[] array, final int n, final int length) {
        this.encoding = null;
        if (length <= array.length - n) {
            System.arraycopy(array, n, this.buffer = new byte[length], 0, length);
            this.length = length;
            return;
        }
        throw new ArrayIndexOutOfBoundsException("Valid length exceeds the buffer length.");
    }
    
    private void ensureCapacity(final int n) {
        if (n > this.buffer.length) {
            final byte[] buffer = this.buffer;
            System.arraycopy(buffer, 0, this.buffer = new byte[buffer.length * 2], 0, buffer.length);
        }
    }
    
    public void append(final byte b) {
        this.ensureCapacity(this.length + 1);
        this.buffer[this.length++] = b;
    }
    
    public void append(final ByteBuffer byteBuffer) {
        this.append(byteBuffer.buffer, 0, byteBuffer.length);
    }
    
    public void append(final byte[] array) {
        this.append(array, 0, array.length);
    }
    
    public void append(final byte[] array, final int n, final int n2) {
        this.ensureCapacity(this.length + n2);
        System.arraycopy(array, n, this.buffer, this.length, n2);
        this.length += n2;
    }
    
    public byte byteAt(final int n) {
        if (n >= this.length) {
            throw new IndexOutOfBoundsException("The index exceeds the valid buffer area");
        }
        return this.buffer[n];
    }
    
    public int charAt(final int n) {
        if (n >= this.length) {
            throw new IndexOutOfBoundsException("The index exceeds the valid buffer area");
        }
        return this.buffer[n] & 0xFF;
    }
    
    public InputStream getByteStream() {
        return new ByteArrayInputStream(this.buffer, 0, this.length);
    }
    
    public String getEncoding() {
        final int n = 254;
        final int n2 = 1;
        final int n3 = 4;
        final int n4 = 2;
        if (this.encoding == null) {
            if (this.length >= n4) {
                if (this.buffer[0] != 0) {
                    if ((this.buffer[0] & 0xFF) >= 128) {
                        if ((this.buffer[0] & 0xFF) != 0xEF) {
                            if ((this.buffer[0] & 0xFF) != n) {
                                if (this.length >= n3 && this.buffer[n4] == 0) {
                                    this.encoding = "UTF-32";
                                }
                                else {
                                    this.encoding = "UTF-16";
                                }
                            }
                            else {
                                this.encoding = "UTF-16";
                            }
                        }
                        else {
                            this.encoding = "UTF-8";
                        }
                    }
                    else if (this.buffer[n2] == 0) {
                        if (this.length >= n3 && this.buffer[n4] == 0) {
                            this.encoding = "UTF-32LE";
                        }
                        else {
                            this.encoding = "UTF-16LE";
                        }
                    }
                    else {
                        this.encoding = "UTF-8";
                    }
                }
                else if (this.length >= n3 && this.buffer[n2] == 0) {
                    if ((this.buffer[n4] & 0xFF) == n && (this.buffer[3] & 0xFF) == 0xFF) {
                        this.encoding = "UTF-32BE";
                    }
                    else {
                        this.encoding = "UTF-32";
                    }
                }
                else {
                    this.encoding = "UTF-16BE";
                }
            }
            else {
                this.encoding = "UTF-8";
            }
        }
        return this.encoding;
    }
    
    public int length() {
        return this.length;
    }
}
