// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.util.Iterator;

class XMPNode$1 implements Iterator
{
    final /* synthetic */ XMPNode this$0;
    private final /* synthetic */ Iterator val$it;
    
    XMPNode$1(final XMPNode this$0, final Iterator val$it) {
        this.this$0 = this$0;
        this.val$it = val$it;
    }
    
    public boolean hasNext() {
        return this.val$it.hasNext();
    }
    
    public Object next() {
        return this.val$it.next();
    }
    
    public void remove() {
        throw new UnsupportedOperationException("remove() is not allowed due to the internal contraints");
    }
}
