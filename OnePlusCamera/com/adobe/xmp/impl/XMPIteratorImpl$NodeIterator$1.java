// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;

class XMPIteratorImpl$NodeIterator$1 implements XMPPropertyInfo
{
    final /* synthetic */ XMPIteratorImpl$NodeIterator this$1;
    private final /* synthetic */ String val$baseNS;
    private final /* synthetic */ XMPNode val$node;
    private final /* synthetic */ String val$path;
    private final /* synthetic */ Object val$value;
    
    XMPIteratorImpl$NodeIterator$1(final XMPIteratorImpl$NodeIterator this$1, final String val$baseNS, final String val$path, final Object val$value, final XMPNode val$node) {
        this.this$1 = this$1;
        this.val$baseNS = val$baseNS;
        this.val$path = val$path;
        this.val$value = val$value;
        this.val$node = val$node;
    }
    
    public String getLanguage() {
        return null;
    }
    
    public String getNamespace() {
        return this.val$baseNS;
    }
    
    public PropertyOptions getOptions() {
        return this.val$node.getOptions();
    }
    
    public String getPath() {
        return this.val$path;
    }
    
    public Object getValue() {
        return this.val$value;
    }
}
