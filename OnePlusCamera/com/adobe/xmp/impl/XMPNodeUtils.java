// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.util.Calendar;
import com.adobe.xmp.XMPDateTimeFactory;
import com.adobe.xmp.XMPUtils;
import java.util.GregorianCalendar;
import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.impl.xpath.XMPPathSegment;
import com.adobe.xmp.impl.xpath.XMPPath;
import java.util.Iterator;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.XMPConst;

public class XMPNodeUtils implements XMPConst
{
    static final int CLT_FIRST_ITEM = 5;
    static final int CLT_MULTIPLE_GENERIC = 3;
    static final int CLT_NO_VALUES = 0;
    static final int CLT_SINGLE_GENERIC = 2;
    static final int CLT_SPECIFIC_MATCH = 1;
    static final int CLT_XDEFAULT = 4;
    
    static {
        boolean $assertionsDisabled = false;
        if (!XMPNodeUtils.class.desiredAssertionStatus()) {
            $assertionsDisabled = true;
        }
    }
    
    static void appendLangItem(final XMPNode xmpNode, final String s, final String s2) {
        final XMPNode xmpNode2 = new XMPNode("[]", s2, null);
        final XMPNode xmpNode3 = new XMPNode("xml:lang", s, null);
        xmpNode2.addQualifier(xmpNode3);
        if ("x-default".equals(xmpNode3.getValue())) {
            xmpNode.addChild(1, xmpNode2);
        }
        else {
            xmpNode.addChild(xmpNode2);
        }
    }
    
    static Object[] chooseLocalizedText(final XMPNode xmpNode, final String s, final String s2) {
        final int n = 102;
        final int n2 = 2;
        final int n3 = 1;
        if (!xmpNode.getOptions().isArrayAltText()) {
            throw new XMPException("Localized text array is not alt-text", n);
        }
        if (!xmpNode.hasChildren()) {
            final Object[] array = new Object[n2];
            array[0] = new Integer(0);
            return array;
        }
        final Iterator iterateChildren = xmpNode.iterateChildren();
        Object o = null;
        XMPNode xmpNode2 = null;
        int n4 = 0;
        while (iterateChildren.hasNext()) {
            XMPNode xmpNode3 = iterateChildren.next();
            if (xmpNode3.getOptions().isCompositeProperty()) {
                throw new XMPException("Alt-text array item is not simple", n);
            }
            if (!xmpNode3.hasQualifier() || !"xml:lang".equals(xmpNode3.getQualifier(n3).getName())) {
                throw new XMPException("Alt-text array item has no language qualifier", n);
            }
            final String value = xmpNode3.getQualifier(n3).getValue();
            if (s2.equals(value)) {
                final Object[] array2 = new Object[n2];
                array2[0] = new Integer(n3);
                array2[n3] = xmpNode3;
                return array2;
            }
            if (s != null && value.startsWith(s)) {
                if (xmpNode2 != null) {
                    xmpNode3 = xmpNode2;
                }
                ++n4;
                xmpNode2 = xmpNode3;
            }
            else {
                if (!"x-default".equals(value)) {
                    continue;
                }
                o = xmpNode3;
            }
        }
        if (n4 == n3) {
            final Object[] array3 = new Object[n2];
            array3[0] = new Integer(n2);
            array3[n3] = xmpNode2;
            return array3;
        }
        if (n4 > n3) {
            final Object[] array4 = new Object[n2];
            array4[0] = new Integer(3);
            array4[n3] = xmpNode2;
            return array4;
        }
        if (o == null) {
            final Object[] array5 = new Object[n2];
            array5[0] = new Integer(5);
            array5[n3] = xmpNode.getChild(n3);
            return array5;
        }
        final Object[] array6 = new Object[n2];
        array6[0] = new Integer(4);
        array6[n3] = o;
        return array6;
    }
    
    static void deleteNode(final XMPNode xmpNode) {
        final XMPNode parent = xmpNode.getParent();
        if (!xmpNode.getOptions().isQualifier()) {
            parent.removeChild(xmpNode);
        }
        else {
            parent.removeQualifier(xmpNode);
        }
        if (!parent.hasChildren() && parent.getOptions().isSchemaNode()) {
            parent.getParent().removeChild(parent);
        }
    }
    
    static void detectAltText(final XMPNode xmpNode) {
        final boolean arrayAltText = true;
        if (xmpNode.getOptions().isArrayAlternate() && xmpNode.hasChildren()) {
            final Iterator iterateChildren = xmpNode.iterateChildren();
            while (true) {
                while (iterateChildren.hasNext()) {
                    if (iterateChildren.next().getOptions().getHasLanguage()) {
                        final int n = arrayAltText ? 1 : 0;
                        if (n != 0) {
                            xmpNode.getOptions().setArrayAltText(arrayAltText);
                            normalizeLangArray(xmpNode);
                        }
                        return;
                    }
                }
                final int n = 0;
                continue;
            }
        }
    }
    
    static XMPNode findChildNode(final XMPNode xmpNode, final String s, final boolean b) {
        final int n = 102;
        final boolean b2 = true;
        if (!xmpNode.getOptions().isSchemaNode() && !xmpNode.getOptions().isStruct()) {
            if (!xmpNode.isImplicit()) {
                throw new XMPException("Named children only allowed for schemas and structs", n);
            }
            if (xmpNode.getOptions().isArray()) {
                throw new XMPException("Named children not allowed for arrays", n);
            }
            if (b) {
                xmpNode.getOptions().setStruct(b2);
            }
        }
        XMPNode childByName = xmpNode.findChildByName(s);
        if (childByName == null && b) {
            childByName = new XMPNode(s, new PropertyOptions());
            childByName.setImplicit(b2);
            xmpNode.addChild(childByName);
        }
        assert !b;
        return childByName;
    }
    
    private static int findIndexedItem(final XMPNode xmpNode, final String s, final boolean b) {
        while (true) {
            int n = 102;
            final int implicit = 1;
            final int n2 = 1;
            int int1 = 0;
            try {
                final String substring = s.substring(n2, s.length() - 1);
                try {
                    int1 = Integer.parseInt(substring);
                    if (int1 < implicit) {
                        throw new XMPException("Array index must be larger than zero", 102);
                    }
                    if (!b) {
                        return int1;
                    }
                }
                catch (NumberFormatException ex) {
                    throw new XMPException("Array index not digits.", n);
                }
            }
            catch (NumberFormatException ex2) {}
            if (int1 == xmpNode.getChildrenLength() + 1) {
                final String s2 = "[]";
                n = 0;
                final XMPNode xmpNode2 = new XMPNode(s2, null);
                xmpNode2.setImplicit(implicit != 0);
                xmpNode.addChild(xmpNode2);
                return int1;
            }
            return int1;
        }
    }
    
    static XMPNode findNode(final XMPNode xmpNode, final XMPPath xmpPath, final boolean b, final PropertyOptions propertyOptions) {
        final int n = 1;
        while (true) {
            if (xmpPath != null && xmpPath.size() != 0) {
                Comparable<String> name = xmpPath.getSegment(0).getName();
                XMPNode xmpNode2 = findSchemaNode(xmpNode, (String)name, b);
                while (true) {
                    Label_0083: {
                        Label_0144: {
                            if (xmpNode2 == null) {
                                break Label_0144;
                            }
                            if (!xmpNode2.isImplicit()) {
                                name = null;
                                break Label_0083;
                            }
                            Label_0146: {
                                break Label_0146;
                            Label_0217_Outer:
                                while (true) {
                                Label_0217:
                                    while (true) {
                                        PropertyOptions options2 = null;
                                        try {
                                            Label_0159: {
                                                int i = 0;
                                                while (i < xmpPath.size()) {
                                                    xmpNode2 = followXPathStep(xmpNode2, xmpPath.getSegment(i), b);
                                                    if (xmpNode2 == null) {
                                                        break Label_0159;
                                                    }
                                                    Label_0135: {
                                                        if (xmpNode2.isImplicit()) {
                                                            xmpNode2.setImplicit(false);
                                                            while (true) {
                                                                Label_0306: {
                                                                    Label_0229: {
                                                                        if (i == n) {
                                                                            break Label_0229;
                                                                        }
                                                                        if (i < xmpPath.size() - 1) {
                                                                            break Label_0306;
                                                                        }
                                                                        if (name == null) {
                                                                            name = (Comparable<String>)xmpNode2;
                                                                        }
                                                                        break Label_0135;
                                                                    }
                                                                    final XMPPathSegment segment = xmpPath.getSegment(i);
                                                                    try {
                                                                        if (!segment.isAlias()) {
                                                                            continue Label_0217_Outer;
                                                                        }
                                                                        final XMPPathSegment segment2 = xmpPath.getSegment(i);
                                                                        try {
                                                                            if (segment2.getAliasForm() == 0) {
                                                                                continue Label_0217_Outer;
                                                                            }
                                                                            final PropertyOptions options = xmpNode2.getOptions();
                                                                            final XMPPathSegment segment3 = xmpPath.getSegment(i);
                                                                            try {
                                                                                options.setOption(segment3.getAliasForm(), true);
                                                                                continue Label_0217;
                                                                                final XMPPathSegment segment4 = xmpPath.getSegment(i);
                                                                                try {
                                                                                    if (segment4.getKind() != n) {
                                                                                        continue Label_0217;
                                                                                    }
                                                                                    options2 = xmpNode2.getOptions();
                                                                                }
                                                                                catch (XMPException ex2) {}
                                                                            }
                                                                            catch (XMPException ex3) {}
                                                                        }
                                                                        catch (XMPException ex4) {}
                                                                    }
                                                                    catch (XMPException ex5) {}
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    ++i;
                                                }
                                                break;
                                            }
                                            if (b) {
                                                deleteNode((XMPNode)name);
                                            }
                                            return null;
                                            xmpNode2.setImplicit(false);
                                            name = (Comparable<String>)xmpNode2;
                                            break Label_0083;
                                            throw;
                                            return null;
                                        }
                                        catch (XMPException ex6) {}
                                        if (!options2.isCompositeProperty()) {
                                            xmpNode2.getOptions().setStruct(true);
                                            continue Label_0217;
                                        }
                                        continue Label_0217;
                                    }
                                }
                            }
                        }
                        if (name != null) {
                            xmpNode2.getOptions().mergeWith(propertyOptions);
                            xmpNode2.setOptions(xmpNode2.getOptions());
                        }
                        return xmpNode2;
                    }
                    int i = n;
                    continue;
                }
            }
            throw new XMPException("Empty XMPPath", 102);
            Comparable<String> name = null;
            deleteNode((XMPNode)name);
            continue;
        }
    }
    
    private static XMPNode findQualifierNode(final XMPNode xmpNode, final String s, final boolean b) {
        assert !s.startsWith("?");
        XMPNode qualifierByName = xmpNode.findQualifierByName(s);
        if (qualifierByName == null && b) {
            qualifierByName = new XMPNode(s, null);
            qualifierByName.setImplicit(true);
            xmpNode.addQualifier(qualifierByName);
        }
        return qualifierByName;
    }
    
    static XMPNode findSchemaNode(final XMPNode xmpNode, final String s, final String s2, final boolean b) {
        final boolean b2 = true;
        assert xmpNode.getParent() == null;
        XMPNode childByName = xmpNode.findChildByName(s);
        if (childByName == null && b) {
            final XMPNode xmpNode2 = new XMPNode(s, new PropertyOptions().setSchemaNode(b2));
            xmpNode2.setImplicit(b2);
            String value = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(s);
            if (value == null) {
                if (s2 == null || s2.length() == 0) {
                    throw new XMPException("Unregistered schema namespace URI", 101);
                }
                value = XMPMetaFactory.getSchemaRegistry().registerNamespace(s, s2);
            }
            xmpNode2.setValue(value);
            xmpNode.addChild(xmpNode2);
            childByName = xmpNode2;
        }
        return childByName;
    }
    
    static XMPNode findSchemaNode(final XMPNode xmpNode, final String s, final boolean b) {
        return findSchemaNode(xmpNode, s, null, b);
    }
    
    private static XMPNode followXPathStep(final XMPNode xmpNode, final XMPPathSegment xmpPathSegment, final boolean b) {
        final int n = 1;
        XMPNode xmpNode2 = null;
        final int kind = xmpPathSegment.getKind();
        if (kind != n) {
            if (kind != 2) {
                if (!xmpNode.getOptions().isArray()) {
                    throw new XMPException("Indexing applied to non-array", 102);
                }
                int n2;
                if (kind != 3) {
                    if (kind != 4) {
                        if (kind != 6) {
                            if (kind != 5) {
                                throw new XMPException("Unknown array indexing step in FollowXPathStep", 9);
                            }
                            final String[] splitNameAndValue = Utils.splitNameAndValue(xmpPathSegment.getName());
                            n2 = lookupQualSelector(xmpNode, splitNameAndValue[0], splitNameAndValue[n], xmpPathSegment.getAliasForm());
                        }
                        else {
                            final String[] splitNameAndValue2 = Utils.splitNameAndValue(xmpPathSegment.getName());
                            n2 = lookupFieldSelector(xmpNode, splitNameAndValue2[0], splitNameAndValue2[n]);
                        }
                    }
                    else {
                        n2 = xmpNode.getChildrenLength();
                    }
                }
                else {
                    n2 = findIndexedItem(xmpNode, xmpPathSegment.getName(), b);
                }
                if (n <= n2 && n2 <= xmpNode.getChildrenLength()) {
                    xmpNode2 = xmpNode.getChild(n2);
                }
            }
            else {
                xmpNode2 = findQualifierNode(xmpNode, xmpPathSegment.getName().substring(n), b);
            }
        }
        else {
            xmpNode2 = findChildNode(xmpNode, xmpPathSegment.getName(), b);
        }
        return xmpNode2;
    }
    
    private static int lookupFieldSelector(final XMPNode xmpNode, final String s, final String s2) {
        final int n = 1;
        int n2 = -1;
        int n3 = n;
    Label_0008:
        while (n3 <= xmpNode.getChildrenLength() && n2 < 0) {
            final XMPNode child = xmpNode.getChild(n3);
            if (child.getOptions().isStruct()) {
                while (true) {
                    for (int i = n; i <= child.getChildrenLength(); ++i) {
                        final XMPNode child2 = child.getChild(i);
                        if (s.equals(child2.getName()) && s2.equals(child2.getValue())) {
                            final int n4 = n3;
                            ++n3;
                            n2 = n4;
                            continue Label_0008;
                        }
                    }
                    final int n4 = n2;
                    continue;
                }
            }
            throw new XMPException("Field selector must be used on array of struct", 102);
        }
        return n2;
    }
    
    static int lookupLanguageItem(final XMPNode xmpNode, final String s) {
        final int n = 1;
        if (xmpNode.getOptions().isArray()) {
            for (int i = n; i <= xmpNode.getChildrenLength(); ++i) {
                final XMPNode child = xmpNode.getChild(i);
                if (child.hasQualifier() && "xml:lang".equals(child.getQualifier(n).getName()) && s.equals(child.getQualifier(n).getValue())) {
                    return i;
                }
            }
            return -1;
        }
        throw new XMPException("Language item must be used on array", 102);
    }
    
    private static int lookupQualSelector(final XMPNode xmpNode, final String s, final String s2, final int n) {
        final int n2 = 1;
        if (!"xml:lang".equals(s)) {
            for (int i = n2; i < xmpNode.getChildrenLength(); ++i) {
                final Iterator iterateQualifier = xmpNode.getChild(i).iterateQualifier();
                while (iterateQualifier.hasNext()) {
                    final XMPNode xmpNode2 = iterateQualifier.next();
                    if (s.equals(xmpNode2.getName()) && s2.equals(xmpNode2.getValue())) {
                        return i;
                    }
                }
            }
            return -1;
        }
        final int lookupLanguageItem = lookupLanguageItem(xmpNode, Utils.normalizeLangValue(s2));
        if (lookupLanguageItem < 0 && (n & 0x1000) > 0) {
            final XMPNode xmpNode3 = new XMPNode("[]", null);
            xmpNode3.addQualifier(new XMPNode("xml:lang", "x-default", null));
            xmpNode.addChild(n2, xmpNode3);
            return n2;
        }
        return lookupLanguageItem;
    }
    
    static void normalizeLangArray(final XMPNode xmpNode) {
        final int n = 1;
        final int n2 = 2;
        if (!xmpNode.getOptions().isArrayAltText()) {
            return;
        }
        XMPNode child = null;
        Block_3: {
            for (int i = n2; i <= xmpNode.getChildrenLength(); ++i) {
                child = xmpNode.getChild(i);
                if (child.hasQualifier() && "x-default".equals(child.getQualifier(n).getValue())) {
                    break Block_3;
                }
            }
            return;
        }
        while (true) {
            while (true) {
                try {
                    final int i;
                    xmpNode.removeChild(i);
                    xmpNode.addChild(1, child);
                    if (i != n2) {
                        return;
                    }
                }
                catch (XMPException ex) {
                    assert false;
                    continue;
                }
                break;
            }
            xmpNode.getChild(n2).setValue(child.getValue());
        }
    }
    
    static String serializeNodeValue(final Object o) {
        String removeControlChars = null;
        String s;
        if (o != null) {
            if (!(o instanceof Boolean)) {
                if (!(o instanceof Integer)) {
                    if (!(o instanceof Long)) {
                        if (!(o instanceof Double)) {
                            if (!(o instanceof XMPDateTime)) {
                                if (!(o instanceof GregorianCalendar)) {
                                    if (!(o instanceof byte[])) {
                                        s = o.toString();
                                    }
                                    else {
                                        s = XMPUtils.encodeBase64((byte[])o);
                                    }
                                }
                                else {
                                    s = XMPUtils.convertFromDate(XMPDateTimeFactory.createFromCalendar((Calendar)o));
                                }
                            }
                            else {
                                s = XMPUtils.convertFromDate((XMPDateTime)o);
                            }
                        }
                        else {
                            s = XMPUtils.convertFromDouble((double)o);
                        }
                    }
                    else {
                        s = XMPUtils.convertFromLong((long)o);
                    }
                }
                else {
                    s = XMPUtils.convertFromInteger((int)o);
                }
            }
            else {
                s = XMPUtils.convertFromBoolean((boolean)o);
            }
        }
        else {
            s = null;
        }
        if (s != null) {
            removeControlChars = Utils.removeControlChars(s);
        }
        return removeControlChars;
    }
    
    static void setNodeValue(final XMPNode xmpNode, final Object o) {
        final String serializeNodeValue = serializeNodeValue(o);
        if (xmpNode.getOptions().isQualifier() && "xml:lang".equals(xmpNode.getName())) {
            xmpNode.setValue(Utils.normalizeLangValue(serializeNodeValue));
        }
        else {
            xmpNode.setValue(serializeNodeValue);
        }
    }
    
    static PropertyOptions verifySetOptions(PropertyOptions propertyOptions, final Object o) {
        final boolean array = true;
        if (propertyOptions == null) {
            propertyOptions = new PropertyOptions();
        }
        if (propertyOptions.isArrayAltText()) {
            propertyOptions.setArrayAlternate(array);
        }
        if (propertyOptions.isArrayAlternate()) {
            propertyOptions.setArrayOrdered(array);
        }
        if (propertyOptions.isArrayOrdered()) {
            propertyOptions.setArray(array);
        }
        if (propertyOptions.isCompositeProperty() && o != null && o.toString().length() > 0) {
            throw new XMPException("Structs and arrays can't have values", 103);
        }
        propertyOptions.assertConsistency(propertyOptions.getOptions());
        return propertyOptions;
    }
}
