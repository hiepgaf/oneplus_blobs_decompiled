// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.util.Iterator;

class XMPIteratorImpl$NodeIteratorChildren extends XMPIteratorImpl$NodeIterator
{
    private Iterator childrenIterator;
    private int index;
    private String parentPath;
    final /* synthetic */ XMPIteratorImpl this$0;
    
    public XMPIteratorImpl$NodeIteratorChildren(final XMPIteratorImpl this$0, final XMPNode xmpNode, final String s) {
        this.this$0 = this$0;
        super(this$0);
        this.index = 0;
        if (xmpNode.getOptions().isSchemaNode()) {
            this$0.setBaseNS(xmpNode.getName());
        }
        this.parentPath = this.accumulatePath(xmpNode, s, 1);
        this.childrenIterator = xmpNode.iterateChildren();
    }
    
    public boolean hasNext() {
        final boolean b = true;
        String accumulatePath = null;
        if (this.getReturnProperty() != null) {
            return b;
        }
        if (this.this$0.skipSiblings) {
            return false;
        }
        if (!this.childrenIterator.hasNext()) {
            return false;
        }
        final XMPNode xmpNode = this.childrenIterator.next();
        ++this.index;
        if (!xmpNode.getOptions().isSchemaNode()) {
            if (xmpNode.getParent() != null) {
                accumulatePath = this.accumulatePath(xmpNode, this.parentPath, this.index);
            }
        }
        else {
            this.this$0.setBaseNS(xmpNode.getName());
        }
        if (this.this$0.getOptions().isJustLeafnodes() && xmpNode.hasChildren()) {
            return this.hasNext();
        }
        this.setReturnProperty(this.createPropertyInfo(xmpNode, this.this$0.getBaseNS(), accumulatePath));
        return b;
    }
}
