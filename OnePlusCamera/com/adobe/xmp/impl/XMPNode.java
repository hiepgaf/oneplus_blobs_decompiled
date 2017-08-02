// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.util.ListIterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.PropertyOptions;
import java.util.List;

class XMPNode implements Comparable
{
    private boolean alias;
    private List children;
    private boolean hasAliases;
    private boolean hasValueChild;
    private boolean implicit;
    private String name;
    private PropertyOptions options;
    private XMPNode parent;
    private List qualifier;
    private String value;
    
    static {
        boolean $assertionsDisabled2 = false;
        if (!XMPNode.class.desiredAssertionStatus()) {
            $assertionsDisabled2 = true;
        }
        $assertionsDisabled = $assertionsDisabled2;
    }
    
    public XMPNode(final String s, final PropertyOptions propertyOptions) {
        this(s, null, propertyOptions);
    }
    
    public XMPNode(final String name, final String value, final PropertyOptions options) {
        this.children = null;
        this.qualifier = null;
        this.options = null;
        this.name = name;
        this.value = value;
        this.options = options;
    }
    
    private void assertChildNotExisting(final String s) {
        if (!"[]".equals(s) && this.findChildByName(s) != null) {
            throw new XMPException("Duplicate property or field node '" + s + "'", 203);
        }
    }
    
    private void assertQualifierNotExisting(final String s) {
        if (!"[]".equals(s) && this.findQualifierByName(s) != null) {
            throw new XMPException("Duplicate '" + s + "' qualifier", 203);
        }
    }
    
    private void dumpNode(final StringBuffer sb, final boolean b, final int n, final int n2) {
        final char c = ')';
        int i = 0;
        for (int j = 0; j < n; ++j) {
            sb.append('\t');
        }
        if (this.parent == null) {
            sb.append("ROOT NODE");
            if (this.name != null && this.name.length() > 0) {
                sb.append(" (");
                sb.append(this.name);
                sb.append(c);
            }
        }
        else if (!this.getOptions().isQualifier()) {
            if (!this.getParent().getOptions().isArray()) {
                sb.append(this.name);
            }
            else {
                sb.append('[');
                sb.append(n2);
                sb.append(']');
            }
        }
        else {
            sb.append('?');
            sb.append(this.name);
        }
        if (this.value != null && this.value.length() > 0) {
            sb.append(" = \"");
            sb.append(this.value);
            sb.append('\"');
        }
        if (this.getOptions().containsOneOf(-1)) {
            sb.append("\t(");
            sb.append(this.getOptions().toString());
            sb.append(" : ");
            sb.append(this.getOptions().getOptionsString());
            sb.append(c);
        }
        sb.append('\n');
        if (b && this.hasQualifier()) {
            XMPNode[] array;
            int n3;
            for (array = this.getQualifier().toArray(new XMPNode[this.getQualifierLength()]), n3 = 0; array.length > n3 && ("xml:lang".equals(array[n3].getName()) || "rdf:type".equals(array[n3].getName())); ++n3) {}
            Arrays.sort(array, n3, array.length);
            for (int k = 0; k < array.length; ++k) {
                array[k].dumpNode(sb, b, n + 2, k + 1);
            }
        }
        if (b && this.hasChildren()) {
            final XMPNode[] array2 = this.getChildren().toArray(new XMPNode[this.getChildrenLength()]);
            if (!this.getOptions().isArray()) {
                Arrays.sort(array2);
            }
            while (i < array2.length) {
                array2[i].dumpNode(sb, b, n + 1, i + 1);
                ++i;
            }
        }
    }
    
    private XMPNode find(final List list, final String s) {
        if (list != null) {
            for (final XMPNode xmpNode : list) {
                if (xmpNode.getName().equals(s)) {
                    return xmpNode;
                }
            }
        }
        return null;
    }
    
    private List getChildren() {
        if (this.children == null) {
            this.children = new ArrayList(0);
        }
        return this.children;
    }
    
    private List getQualifier() {
        if (this.qualifier == null) {
            this.qualifier = new ArrayList(0);
        }
        return this.qualifier;
    }
    
    private boolean isLanguageNode() {
        return "xml:lang".equals(this.name);
    }
    
    private boolean isTypeNode() {
        return "rdf:type".equals(this.name);
    }
    
    public void addChild(final int n, final XMPNode xmpNode) {
        this.assertChildNotExisting(xmpNode.getName());
        xmpNode.setParent(this);
        this.getChildren().add(n - 1, xmpNode);
    }
    
    public void addChild(final XMPNode xmpNode) {
        this.assertChildNotExisting(xmpNode.getName());
        xmpNode.setParent(this);
        this.getChildren().add(xmpNode);
    }
    
    public void addQualifier(final XMPNode xmpNode) {
        int n = 1;
        this.assertQualifierNotExisting(xmpNode.getName());
        xmpNode.setParent(this);
        xmpNode.getOptions().setQualifier(n != 0);
        this.getOptions().setHasQualifiers(n != 0);
        if (!xmpNode.isLanguageNode()) {
            if (!xmpNode.isTypeNode()) {
                this.getQualifier().add(xmpNode);
            }
            else {
                this.options.setHasType(n != 0);
                final List qualifier = this.getQualifier();
                if (!this.options.getHasLanguage()) {
                    n = 0;
                }
                qualifier.add(n, xmpNode);
            }
        }
        else {
            this.options.setHasLanguage(n != 0);
            this.getQualifier().add(0, xmpNode);
        }
    }
    
    protected void cleanupChildren() {
        if (this.children.isEmpty()) {
            this.children = null;
        }
    }
    
    public void clear() {
        this.options = null;
        this.name = null;
        this.value = null;
        this.children = null;
        this.qualifier = null;
    }
    
    public Object clone() {
        try {
            PropertyOptions propertyOptions = new(com.adobe.xmp.options.PropertyOptions.class);
            try {
                final PropertyOptions options = this.getOptions();
                try {
                    propertyOptions = new PropertyOptions(options.getOptions());
                    final XMPNode xmpNode = new XMPNode(this.name, this.value, propertyOptions);
                    this.cloneSubtree(xmpNode);
                    return xmpNode;
                }
                catch (XMPException ex) {
                    propertyOptions = new PropertyOptions();
                }
            }
            catch (XMPException ex2) {}
        }
        catch (XMPException ex3) {}
    }
    
    public void cloneSubtree(final XMPNode xmpNode) {
        while (true) {
            Object clone2 = null;
            try {
                final Iterator iterateChildren = this.iterateChildren();
                try {
                    while (true) {
                        Label_0065: {
                            if (!iterateChildren.hasNext()) {
                                break Label_0065;
                            }
                            final XMPNode next = iterateChildren.next();
                            try {
                                final XMPNode xmpNode2 = next;
                                try {
                                    final Object clone = xmpNode2.clone();
                                    try {
                                        xmpNode.addChild((XMPNode)clone);
                                        continue;
                                        try {
                                            final Iterator iterateQualifier;
                                            if (!iterateQualifier.hasNext()) {
                                                return;
                                            }
                                            final XMPNode next2 = iterateQualifier.next();
                                            try {
                                                final XMPNode xmpNode3 = next2;
                                                try {
                                                    clone2 = xmpNode3.clone();
                                                }
                                                catch (XMPException ex) {}
                                            }
                                            catch (XMPException ex2) {}
                                        }
                                        catch (XMPException ex3) {}
                                        final Iterator iterateQualifier = this.iterateQualifier();
                                    }
                                    catch (XMPException ex4) {}
                                }
                                catch (XMPException ex5) {}
                            }
                            catch (XMPException ex6) {}
                        }
                    }
                }
                catch (XMPException ex7) {}
            }
            catch (XMPException ex8) {}
            xmpNode.addQualifier((XMPNode)clone2);
            continue;
        }
    }
    
    public int compareTo(final Object o) {
        if (!this.getOptions().isSchemaNode()) {
            return this.name.compareTo(((XMPNode)o).getName());
        }
        return this.value.compareTo(((XMPNode)o).getValue());
    }
    
    public String dumpNode(final boolean b) {
        final StringBuffer sb = new StringBuffer(512);
        this.dumpNode(sb, b, 0, 0);
        return sb.toString();
    }
    
    public XMPNode findChildByName(final String s) {
        return this.find(this.getChildren(), s);
    }
    
    public XMPNode findQualifierByName(final String s) {
        return this.find(this.qualifier, s);
    }
    
    public XMPNode getChild(final int n) {
        return this.getChildren().get(n - 1);
    }
    
    public int getChildrenLength() {
        int size;
        if (this.children == null) {
            size = 0;
        }
        else {
            size = this.children.size();
        }
        return size;
    }
    
    public boolean getHasAliases() {
        return this.hasAliases;
    }
    
    public boolean getHasValueChild() {
        return this.hasValueChild;
    }
    
    public String getName() {
        return this.name;
    }
    
    public PropertyOptions getOptions() {
        if (this.options == null) {
            this.options = new PropertyOptions();
        }
        return this.options;
    }
    
    public XMPNode getParent() {
        return this.parent;
    }
    
    public XMPNode getQualifier(final int n) {
        return this.getQualifier().get(n - 1);
    }
    
    public int getQualifierLength() {
        int size;
        if (this.qualifier == null) {
            size = 0;
        }
        else {
            size = this.qualifier.size();
        }
        return size;
    }
    
    public List getUnmodifiableChildren() {
        return Collections.unmodifiableList((List<?>)new ArrayList<Object>(this.getChildren()));
    }
    
    public String getValue() {
        return this.value;
    }
    
    public boolean hasChildren() {
        return this.children != null && this.children.size() > 0;
    }
    
    public boolean hasQualifier() {
        return this.qualifier != null && this.qualifier.size() > 0;
    }
    
    public boolean isAlias() {
        return this.alias;
    }
    
    public boolean isImplicit() {
        return this.implicit;
    }
    
    public Iterator iterateChildren() {
        if (this.children == null) {
            return Collections.EMPTY_LIST.listIterator();
        }
        return this.getChildren().iterator();
    }
    
    public Iterator iterateQualifier() {
        if (this.qualifier == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return new XMPNode$1(this, this.getQualifier().iterator());
    }
    
    public void removeChild(final int n) {
        this.getChildren().remove(n - 1);
        this.cleanupChildren();
    }
    
    public void removeChild(final XMPNode xmpNode) {
        this.getChildren().remove(xmpNode);
        this.cleanupChildren();
    }
    
    public void removeChildren() {
        this.children = null;
    }
    
    public void removeQualifier(final XMPNode xmpNode) {
        final PropertyOptions options = this.getOptions();
        if (!xmpNode.isLanguageNode()) {
            if (xmpNode.isTypeNode()) {
                options.setHasType(false);
            }
        }
        else {
            options.setHasLanguage(false);
        }
        this.getQualifier().remove(xmpNode);
        if (this.qualifier.isEmpty()) {
            options.setHasQualifiers(false);
            this.qualifier = null;
        }
    }
    
    public void removeQualifiers() {
        final PropertyOptions options = this.getOptions();
        options.setHasQualifiers(false);
        options.setHasLanguage(false);
        options.setHasType(false);
        this.qualifier = null;
    }
    
    public void replaceChild(final int n, final XMPNode xmpNode) {
        xmpNode.setParent(this);
        this.getChildren().set(n - 1, xmpNode);
    }
    
    public void setAlias(final boolean alias) {
        this.alias = alias;
    }
    
    public void setHasAliases(final boolean hasAliases) {
        this.hasAliases = hasAliases;
    }
    
    public void setHasValueChild(final boolean hasValueChild) {
        this.hasValueChild = hasValueChild;
    }
    
    public void setImplicit(final boolean implicit) {
        this.implicit = implicit;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setOptions(final PropertyOptions options) {
        this.options = options;
    }
    
    protected void setParent(final XMPNode parent) {
        this.parent = parent;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public void sort() {
        int i = 0;
        if (this.hasQualifier()) {
            XMPNode[] array;
            int n;
            for (array = this.getQualifier().toArray(new XMPNode[this.getQualifierLength()]), n = 0; array.length > n && ("xml:lang".equals(array[n].getName()) || "rdf:type".equals(array[n].getName())); ++n) {
                array[n].sort();
            }
            Arrays.sort(array, n, array.length);
            final ListIterator<XMPNode> listIterator = this.qualifier.listIterator();
            while (i < array.length) {
                listIterator.next();
                listIterator.set(array[i]);
                array[i].sort();
                ++i;
            }
        }
        if (this.hasChildren()) {
            if (!this.getOptions().isArray()) {
                Collections.sort((List<Comparable>)this.children);
            }
            final Iterator iterateChildren = this.iterateChildren();
            while (iterateChildren.hasNext()) {
                iterateChildren.next().sort();
            }
        }
    }
}
