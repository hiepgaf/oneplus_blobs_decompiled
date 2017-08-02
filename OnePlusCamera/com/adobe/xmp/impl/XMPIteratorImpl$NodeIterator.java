// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.util.NoSuchElementException;
import java.util.Collections;
import com.adobe.xmp.properties.XMPPropertyInfo;
import java.util.Iterator;

class XMPIteratorImpl$NodeIterator implements Iterator
{
    protected static final int ITERATE_CHILDREN = 1;
    protected static final int ITERATE_NODE = 0;
    protected static final int ITERATE_QUALIFIER = 2;
    private Iterator childrenIterator;
    private int index;
    private String path;
    private XMPPropertyInfo returnProperty;
    private int state;
    private Iterator subIterator;
    final /* synthetic */ XMPIteratorImpl this$0;
    private XMPNode visitedNode;
    
    public XMPIteratorImpl$NodeIterator(final XMPIteratorImpl this$0) {
        this.this$0 = this$0;
        this.state = 0;
        this.childrenIterator = null;
        this.index = 0;
        this.subIterator = Collections.EMPTY_LIST.iterator();
        this.returnProperty = null;
    }
    
    public XMPIteratorImpl$NodeIterator(final XMPIteratorImpl this$0, final XMPNode visitedNode, final String s, final int n) {
        this.this$0 = this$0;
        this.state = 0;
        this.childrenIterator = null;
        this.index = 0;
        this.subIterator = Collections.EMPTY_LIST.iterator();
        this.returnProperty = null;
        this.visitedNode = visitedNode;
        this.state = 0;
        if (visitedNode.getOptions().isSchemaNode()) {
            this$0.setBaseNS(visitedNode.getName());
        }
        this.path = this.accumulatePath(visitedNode, s, n);
    }
    
    private boolean iterateChildren(final Iterator iterator) {
        if (this.this$0.skipSiblings) {
            this.this$0.skipSiblings = false;
            this.subIterator = Collections.EMPTY_LIST.iterator();
        }
        if (!this.subIterator.hasNext() && iterator.hasNext()) {
            final XMPNode xmpNode = iterator.next();
            ++this.index;
            this.subIterator = new XMPIteratorImpl$NodeIterator(this.this$0, xmpNode, this.path, this.index);
        }
        if (!this.subIterator.hasNext()) {
            return false;
        }
        this.returnProperty = this.subIterator.next();
        return true;
    }
    
    protected String accumulatePath(final XMPNode xmpNode, final String s, final int n) {
        if (xmpNode.getParent() == null || xmpNode.getOptions().isSchemaNode()) {
            return null;
        }
        String s2;
        String s3;
        if (!xmpNode.getParent().getOptions().isArray()) {
            s2 = "/";
            s3 = xmpNode.getName();
        }
        else {
            s2 = "";
            s3 = "[" + String.valueOf(n) + "]";
        }
        if (s == null || s.length() == 0) {
            return s3;
        }
        if (!this.this$0.getOptions().isJustLeafname()) {
            return String.valueOf(s) + s2 + s3;
        }
        if (s3.startsWith("?")) {
            s3 = s3.substring(1);
        }
        return s3;
    }
    
    protected XMPPropertyInfo createPropertyInfo(final XMPNode xmpNode, final String s, final String s2) {
        String value;
        if (!xmpNode.getOptions().isSchemaNode()) {
            value = xmpNode.getValue();
        }
        else {
            value = null;
        }
        return new XMPIteratorImpl$NodeIterator$1(this, s, s2, value, xmpNode);
    }
    
    protected Iterator getChildrenIterator() {
        return this.childrenIterator;
    }
    
    protected XMPPropertyInfo getReturnProperty() {
        return this.returnProperty;
    }
    
    public boolean hasNext() {
        final boolean b = true;
        if (this.returnProperty != null) {
            return b;
        }
        if (this.state == 0) {
            return this.reportNode();
        }
        if (this.state != (b ? 1 : 0)) {
            if (this.childrenIterator == null) {
                this.childrenIterator = this.visitedNode.iterateQualifier();
            }
            return this.iterateChildren(this.childrenIterator);
        }
        if (this.childrenIterator == null) {
            this.childrenIterator = this.visitedNode.iterateChildren();
        }
        boolean b2 = this.iterateChildren(this.childrenIterator);
        if (!b2 && this.visitedNode.hasQualifier() && !this.this$0.getOptions().isOmitQualifiers()) {
            this.state = 2;
            this.childrenIterator = null;
            b2 = this.hasNext();
        }
        return b2;
    }
    
    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("There are no more nodes to return");
        }
        final XMPPropertyInfo returnProperty = this.returnProperty;
        this.returnProperty = null;
        return returnProperty;
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    protected boolean reportNode() {
        final boolean state = true;
        this.state = (state ? 1 : 0);
        if (this.visitedNode.getParent() != null && (!this.this$0.getOptions().isJustLeafnodes() || !this.visitedNode.hasChildren())) {
            this.returnProperty = this.createPropertyInfo(this.visitedNode, this.this$0.getBaseNS(), this.path);
            return state;
        }
        return this.hasNext();
    }
    
    protected void setChildrenIterator(final Iterator childrenIterator) {
        this.childrenIterator = childrenIterator;
    }
    
    protected void setReturnProperty(final XMPPropertyInfo returnProperty) {
        this.returnProperty = returnProperty;
    }
}
