// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.io.Reader;
import java.io.PushbackReader;

public class FixASCIIControlsReader extends PushbackReader
{
    private static final int BUFFER_SIZE = 8;
    private static final int STATE_AMP = 1;
    private static final int STATE_DIG1 = 4;
    private static final int STATE_ERROR = 5;
    private static final int STATE_HASH = 2;
    private static final int STATE_HEX = 3;
    private static final int STATE_START;
    private int control;
    private int digits;
    private int state;
    
    public FixASCIIControlsReader(final Reader reader) {
        super(reader, 8);
        this.state = 0;
        this.control = 0;
        this.digits = 0;
    }
    
    private char processChar(final char c) {
        final char c2 = '9';
        final char c3 = '0';
        final int n = 4;
        final int n2 = 5;
        switch (this.state) {
            default: {
                return c;
            }
            case 0: {
                if (c == '&') {
                    this.state = 1;
                }
                return c;
            }
            case 1: {
                if (c != '#') {
                    this.state = n2;
                }
                else {
                    this.state = 2;
                }
                return c;
            }
            case 2: {
                if (c != 'x') {
                    if (c3 <= c && c <= c2) {
                        this.control = Character.digit(c, 10);
                        this.digits = 1;
                        this.state = n;
                    }
                    else {
                        this.state = n2;
                    }
                }
                else {
                    this.control = 0;
                    this.digits = 0;
                    this.state = 3;
                }
                return c;
            }
            case 4: {
                if (c3 <= c && c <= c2) {
                    this.control = this.control * 10 + Character.digit(c, 10);
                    ++this.digits;
                    if (this.digits > n2) {
                        this.state = n2;
                    }
                    else {
                        this.state = n;
                    }
                }
                else {
                    if (c == ';' && Utils.isControlChar((char)this.control)) {
                        this.state = 0;
                        return (char)this.control;
                    }
                    this.state = n2;
                }
                return c;
            }
            case 3: {
                Label_0375: {
                    if (c3 <= c && c <= c2) {
                        break Label_0375;
                    }
                    if ('a' <= c && c <= 'f') {
                        break Label_0375;
                    }
                    if ('A' <= c && c <= 'F') {
                        break Label_0375;
                    }
                    if (c == ';' && Utils.isControlChar((char)this.control)) {
                        this.state = 0;
                        return (char)this.control;
                    }
                    this.state = n2;
                    return c;
                }
                this.control = this.control * 16 + Character.digit(c, 16);
                ++this.digits;
                if (this.digits > n) {
                    this.state = n2;
                    return c;
                }
                this.state = 3;
                return c;
            }
            case 5: {
                this.state = 0;
                return c;
            }
        }
    }
    
    public int read(final char[] array, int n, final int n2) {
        final int state = 5;
        final int n3 = 1;
        final char[] array2 = new char[8];
        int n4 = n3;
        int n5 = 0;
        int n6 = 0;
        while (n4 != 0 && n5 < n2) {
            if (super.read(array2, n6, n3) != n3) {
                n4 = 0;
            }
            else {
                n4 = n3;
            }
            if (n4 == 0) {
                if (n6 <= 0) {
                    continue;
                }
                this.unread(array2, 0, n6);
                this.state = state;
                n4 = n3;
                n6 = 0;
            }
            else {
                char processChar = this.processChar(array2[n6]);
                if (this.state != 0) {
                    if (this.state != state) {
                        ++n6;
                    }
                    else {
                        this.unread(array2, 0, n6 + 1);
                        n6 = 0;
                    }
                }
                else {
                    if (Utils.isControlChar(processChar)) {
                        processChar = ' ';
                    }
                    final int n7 = n + 1;
                    array[n] = processChar;
                    ++n5;
                    n = n7;
                    n6 = 0;
                }
            }
        }
        int n8;
        if (n5 <= 0 && n4 == 0) {
            n8 = -1;
        }
        else {
            n8 = n5;
        }
        return n8;
    }
}
