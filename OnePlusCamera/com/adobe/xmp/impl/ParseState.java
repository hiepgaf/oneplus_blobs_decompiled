// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;

class ParseState
{
    private int pos;
    private String str;
    
    public ParseState(final String str) {
        this.pos = 0;
        this.str = str;
    }
    
    public char ch() {
        char char1;
        if (this.pos >= this.str.length()) {
            char1 = '\0';
        }
        else {
            char1 = this.str.charAt(this.pos);
        }
        return char1;
    }
    
    public char ch(final int n) {
        char char1;
        if (n >= this.str.length()) {
            char1 = '\0';
        }
        else {
            char1 = this.str.charAt(n);
        }
        return char1;
    }
    
    public int gatherInt(final String s, final int n) {
        char c = this.ch(this.pos);
        int n2 = 0;
        int n3 = 0;
        while ('0' <= c && c <= '9') {
            n3 = n3 * 10 + (c - 48);
            n2 = 1;
            ++this.pos;
            c = this.ch(this.pos);
        }
        if (n2 == 0) {
            throw new XMPException(s, 5);
        }
        if (n3 > n) {
            return n;
        }
        if (n3 >= 0) {
            return n3;
        }
        return 0;
    }
    
    public boolean hasNext() {
        return this.pos < this.str.length();
    }
    
    public int length() {
        return this.str.length();
    }
    
    public int pos() {
        return this.pos;
    }
    
    public void skip() {
        ++this.pos;
    }
}
