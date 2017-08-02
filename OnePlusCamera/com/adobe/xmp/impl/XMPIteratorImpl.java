// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.util.Collections;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.IteratorOptions;
import java.util.Iterator;
import com.adobe.xmp.XMPIterator;

public class XMPIteratorImpl implements XMPIterator
{
    private String baseNS;
    private Iterator nodeIterator;
    private IteratorOptions options;
    protected boolean skipSiblings;
    protected boolean skipSubtree;
    
    public XMPIteratorImpl(final XMPMetaImpl xmpMetaImpl, final String baseNS, final String s, IteratorOptions options) {
        final boolean b = true;
        this.baseNS = null;
        this.skipSiblings = false;
        this.skipSubtree = false;
        this.nodeIterator = null;
        if (options == null) {
            options = new IteratorOptions();
        }
        this.options = options;
        boolean b2;
        if (baseNS != null && baseNS.length() > 0) {
            b2 = b;
        }
        else {
            b2 = false;
        }
        boolean b3;
        if (s != null && s.length() > 0) {
            b3 = b;
        }
        else {
            b3 = false;
        }
        XMPNode xmpNode;
        String string;
        if (!b2 && !b3) {
            xmpNode = xmpMetaImpl.getRoot();
            string = null;
        }
        else if (b2 && b3) {
            final XMPPath expandXPath = XMPPathParser.expandXPath(baseNS, s);
            final XMPPath xmpPath = new XMPPath();
            for (int i = 0; i < expandXPath.size() - 1; ++i) {
                xmpPath.add(expandXPath.getSegment(i));
            }
            xmpNode = XMPNodeUtils.findNode(xmpMetaImpl.getRoot(), expandXPath, false, null);
            this.baseNS = baseNS;
            string = xmpPath.toString();
        }
        else {
            if (!b2 || b3) {
                throw new XMPException("Schema namespace URI is required", 101);
            }
            xmpNode = XMPNodeUtils.findSchemaNode(xmpMetaImpl.getRoot(), baseNS, false);
            string = null;
        }
        if (xmpNode == null) {
            this.nodeIterator = Collections.EMPTY_LIST.iterator();
        }
        else if (this.options.isJustChildren()) {
            this.nodeIterator = new XMPIteratorImpl$NodeIteratorChildren(this, xmpNode, string);
        }
        else {
            this.nodeIterator = new XMPIteratorImpl$NodeIterator(this, xmpNode, string, b ? 1 : 0);
        }
    }
    
    protected String getBaseNS() {
        return this.baseNS;
    }
    
    protected IteratorOptions getOptions() {
        return this.options;
    }
    
    public boolean hasNext() {
        return this.nodeIterator.hasNext();
    }
    
    public Object next() {
        return this.nodeIterator.next();
    }
    
    public void remove() {
        throw new UnsupportedOperationException("The XMPIterator does not support remove().");
    }
    
    protected void setBaseNS(final String baseNS) {
        this.baseNS = baseNS;
    }
    
    public void skipSiblings() {
        this.skipSubtree();
        this.skipSiblings = true;
    }
    
    public void skipSubtree() {
        this.skipSubtree = true;
    }
}
