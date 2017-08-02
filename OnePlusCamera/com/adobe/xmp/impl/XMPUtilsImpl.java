// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import com.adobe.xmp.properties.XMPAliasInfo;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import java.util.Iterator;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPConst;

public class XMPUtilsImpl implements XMPConst
{
    private static final String COMMAS = ",\uff0c\uff64\ufe50\ufe51\u3001\u060c\u055d";
    private static final String CONTROLS = "\u2028\u2029";
    private static final String QUOTES = "\"[]«»\u301d\u301e\u301f\u2015\u2039\u203a";
    private static final String SEMICOLA = ";\uff1b\ufe54\u061b\u037e";
    private static final String SPACES = " \u3000\u303f";
    private static final int UCK_COMMA = 2;
    private static final int UCK_CONTROL = 5;
    private static final int UCK_NORMAL = 0;
    private static final int UCK_QUOTE = 4;
    private static final int UCK_SEMICOLON = 3;
    private static final int UCK_SPACE = 1;
    
    static {
        boolean $assertionsDisabled = false;
        if (!XMPUtilsImpl.class.desiredAssertionStatus()) {
            $assertionsDisabled = true;
        }
    }
    
    public static void appendProperties(final XMPMeta xmpMeta, final XMPMeta xmpMeta2, final boolean b, final boolean b2, final boolean b3) {
        final boolean schemaNode = true;
        ParameterAsserts.assertImplementation(xmpMeta);
        ParameterAsserts.assertImplementation(xmpMeta2);
        final XMPMetaImpl xmpMetaImpl = (XMPMetaImpl)xmpMeta;
        final XMPMetaImpl xmpMetaImpl2 = (XMPMetaImpl)xmpMeta2;
        final Iterator iterateChildren = xmpMetaImpl.getRoot().iterateChildren();
        while (iterateChildren.hasNext()) {
            final XMPNode xmpNode = iterateChildren.next();
            final XMPNode schemaNode2 = XMPNodeUtils.findSchemaNode(xmpMetaImpl2.getRoot(), xmpNode.getName(), false);
            boolean b4;
            XMPNode xmpNode2;
            if (schemaNode2 != null) {
                b4 = false;
                xmpNode2 = schemaNode2;
            }
            else {
                final XMPNode xmpNode3 = new XMPNode(xmpNode.getName(), xmpNode.getValue(), new PropertyOptions().setSchemaNode(schemaNode));
                xmpMetaImpl2.getRoot().addChild(xmpNode3);
                b4 = schemaNode;
                xmpNode2 = xmpNode3;
            }
            final Iterator iterateChildren2 = xmpNode.iterateChildren();
            while (iterateChildren2.hasNext()) {
                final XMPNode xmpNode4 = iterateChildren2.next();
                if (!b && Utils.isInternalProperty(xmpNode.getName(), xmpNode4.getName())) {
                    continue;
                }
                appendSubtree(xmpMetaImpl2, xmpNode4, xmpNode2, b2, b3);
            }
            if (!xmpNode2.hasChildren()) {
                if (!b4 && !b3) {
                    continue;
                }
                xmpMetaImpl2.getRoot().removeChild(xmpNode2);
            }
        }
    }
    
    private static void appendSubtree(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final XMPNode xmpNode2, final boolean b, final boolean b2) {
        final int n = -1;
        final int n2 = 1;
        final XMPNode childNode = XMPNodeUtils.findChildNode(xmpNode2, xmpNode.getName(), false);
        boolean b3;
        if (!b2) {
            b3 = false;
        }
        else if (!xmpNode.getOptions().isSimple()) {
            b3 = (!xmpNode.hasChildren() && n2);
        }
        else {
            b3 = ((xmpNode.getValue() == null || xmpNode.getValue().length() == 0) && n2);
        }
        if (b2 && b3) {
            if (childNode != null) {
                xmpNode2.removeChild(childNode);
            }
        }
        else if (childNode != null) {
            if (!b) {
                final PropertyOptions options = xmpNode.getOptions();
                if (options != childNode.getOptions()) {
                    return;
                }
                if (!options.isStruct()) {
                    if (!options.isArrayAltText()) {
                        if (options.isArray()) {
                            final Iterator iterateChildren = xmpNode.iterateChildren();
                            XMPNode xmpNode3 = childNode;
                            while (iterateChildren.hasNext()) {
                                final XMPNode xmpNode4 = iterateChildren.next();
                                final Iterator iterateChildren2 = xmpNode3.iterateChildren();
                                int n3 = 0;
                                while (iterateChildren2.hasNext()) {
                                    if (itemValuesMatch(xmpNode4, iterateChildren2.next())) {
                                        n3 = n2;
                                    }
                                }
                                if (n3 == 0) {
                                    final XMPNode xmpNode5 = (XMPNode)xmpNode4.clone();
                                    xmpNode2.addChild(xmpNode5);
                                    xmpNode3 = xmpNode5;
                                }
                            }
                        }
                    }
                    else {
                        final Iterator iterateChildren3 = xmpNode.iterateChildren();
                        while (iterateChildren3.hasNext()) {
                            final XMPNode xmpNode6 = iterateChildren3.next();
                            if (xmpNode6.hasQualifier() && "xml:lang".equals(xmpNode6.getQualifier(n2).getName())) {
                                final int lookupLanguageItem = XMPNodeUtils.lookupLanguageItem(childNode, xmpNode6.getQualifier(n2).getValue());
                                if (b2 && (xmpNode6.getValue() == null || xmpNode6.getValue().length() == 0)) {
                                    if (lookupLanguageItem == n) {
                                        continue;
                                    }
                                    childNode.removeChild(lookupLanguageItem);
                                    if (childNode.hasChildren()) {
                                        continue;
                                    }
                                    xmpNode2.removeChild(childNode);
                                }
                                else {
                                    if (lookupLanguageItem != n) {
                                        continue;
                                    }
                                    if ("x-default".equals(xmpNode6.getQualifier(n2).getValue()) && childNode.hasChildren()) {
                                        final XMPNode xmpNode7 = new XMPNode(xmpNode6.getName(), xmpNode6.getValue(), xmpNode6.getOptions());
                                        xmpNode6.cloneSubtree(xmpNode7);
                                        childNode.addChild(n2, xmpNode7);
                                    }
                                    else {
                                        xmpNode6.cloneSubtree(childNode);
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    final Iterator iterateChildren4 = xmpNode.iterateChildren();
                    while (iterateChildren4.hasNext()) {
                        appendSubtree(xmpMetaImpl, iterateChildren4.next(), childNode, b, b2);
                        if (b2 && !childNode.hasChildren()) {
                            xmpNode2.removeChild(childNode);
                        }
                    }
                }
            }
            else {
                xmpMetaImpl.setNode(childNode, xmpNode.getValue(), xmpNode.getOptions(), n2 != 0);
                xmpNode2.removeChild(childNode);
                xmpNode2.addChild((XMPNode)xmpNode.clone());
            }
        }
        else {
            xmpNode2.addChild((XMPNode)xmpNode.clone());
        }
    }
    
    private static String applyQuotes(String string, final char c, final char c2, final boolean b) {
        final boolean b2 = true;
        final int n = 4;
        if (string == null) {
            string = "";
        }
        int i = 0;
        int n2 = 0;
        while (i < string.length()) {
            final int classifyCharacter = classifyCharacter(string.charAt(i));
            if (i == 0 && classifyCharacter == n) {
                break;
            }
            if (classifyCharacter != (b2 ? 1 : 0)) {
                if (classifyCharacter == 3 || classifyCharacter == 5 || (classifyCharacter == 2 && !b)) {
                    break;
                }
                n2 = 0;
            }
            else {
                if (n2 != 0) {
                    break;
                }
                n2 = (b2 ? 1 : 0);
            }
            ++i;
        }
        if (i < string.length()) {
            final StringBuffer sb = new StringBuffer(string.length() + 2);
            int j;
            for (j = 0; j <= i && classifyCharacter(string.charAt(i)) != n; ++j) {}
            sb.append(c).append(string.substring(0, j));
            while (j < string.length()) {
                sb.append(string.charAt(j));
                if (classifyCharacter(string.charAt(j)) == n && isSurroundingQuote(string.charAt(j), c, c2)) {
                    sb.append(string.charAt(j));
                }
                ++j;
            }
            sb.append(c2);
            string = sb.toString();
        }
        return string;
    }
    
    public static String catenateArrayItems(final XMPMeta xmpMeta, final String s, final String s2, String s3, String s4, final boolean b) {
        final int n = 4;
        ParameterAsserts.assertSchemaNS(s);
        ParameterAsserts.assertArrayName(s2);
        ParameterAsserts.assertImplementation(xmpMeta);
        if (s3 == null || s3.length() == 0) {
            s3 = "; ";
        }
        if (s4 == null || s4.length() == 0) {
            s4 = "\"";
        }
        final XMPNode node = XMPNodeUtils.findNode(((XMPMetaImpl)xmpMeta).getRoot(), XMPPathParser.expandXPath(s, s2), false, null);
        if (node == null) {
            return "";
        }
        if (node.getOptions().isArray() && !node.getOptions().isArrayAlternate()) {
            checkSeparator(s3);
            final char char1 = s4.charAt(0);
            final char checkQuotes = checkQuotes(s4, char1);
            final StringBuffer sb = new StringBuffer();
            final Iterator iterateChildren = node.iterateChildren();
            while (iterateChildren.hasNext()) {
                final XMPNode xmpNode = iterateChildren.next();
                if (xmpNode.getOptions().isCompositeProperty()) {
                    throw new XMPException("Array items must be simple", n);
                }
                sb.append(applyQuotes(xmpNode.getValue(), char1, checkQuotes, b));
                if (!iterateChildren.hasNext()) {
                    continue;
                }
                sb.append(s3);
            }
            return sb.toString();
        }
        throw new XMPException("Named property must be non-alternate array", n);
    }
    
    private static char checkQuotes(final String s, final char c) {
        final int n = 1;
        final int n2 = 4;
        if (classifyCharacter(c) != n2) {
            throw new XMPException("Invalid quoting character", n2);
        }
        char char1;
        if (s.length() != n) {
            char1 = s.charAt(n);
            if (classifyCharacter(char1) != n2) {
                throw new XMPException("Invalid quoting character", n2);
            }
        }
        else {
            char1 = c;
        }
        if (char1 == getClosingQuote(c)) {
            return char1;
        }
        throw new XMPException("Mismatched quote pair", n2);
    }
    
    private static void checkSeparator(final String s) {
        final boolean b = true;
        final int n = 4;
        boolean b2 = false;
        for (int i = 0; i < s.length(); ++i) {
            final int classifyCharacter = classifyCharacter(s.charAt(i));
            if (classifyCharacter != 3) {
                if (classifyCharacter != (b ? 1 : 0)) {
                    throw new XMPException("Separator can have only spaces and one semicolon", n);
                }
            }
            else {
                if (b2) {
                    throw new XMPException("Separator can have only one semicolon", n);
                }
                b2 = b;
            }
        }
        if (b2) {
            return;
        }
        throw new XMPException("Separator must have one semicolon", n);
    }
    
    private static int classifyCharacter(final char c) {
        if (" \u3000\u303f".indexOf(c) >= 0 || ('\u2000' <= c && c <= '\u200b')) {
            return 1;
        }
        if (",\uff0c\uff64\ufe50\ufe51\u3001\u060c\u055d".indexOf(c) >= 0) {
            return 2;
        }
        if (";\uff1b\ufe54\u061b\u037e".indexOf(c) < 0) {
            if ("\"[]«»\u301d\u301e\u301f\u2015\u2039\u203a".indexOf(c) < 0) {
                if ('\u3008' > c || c > '\u300f') {
                    if ('\u2018' > c || c > '\u201f') {
                        if (c >= ' ' && "\u2028\u2029".indexOf(c) < 0) {
                            return 0;
                        }
                        return 5;
                    }
                }
            }
            return 4;
        }
        return 3;
    }
    
    private static char getClosingQuote(final char c) {
        switch (c) {
            default: {
                return '\0';
            }
            case '\"': {
                return '\"';
            }
            case '[': {
                return ']';
            }
            case '«': {
                return '»';
            }
            case '»': {
                return '«';
            }
            case '\u2015': {
                return '\u2015';
            }
            case '\u2018': {
                return '\u2019';
            }
            case '\u201a': {
                return '\u201b';
            }
            case '\u201c': {
                return '\u201d';
            }
            case '\u201e': {
                return '\u201f';
            }
            case '\u2039': {
                return '\u203a';
            }
            case '\u203a': {
                return '\u2039';
            }
            case '\u3008': {
                return '\u3009';
            }
            case '\u300a': {
                return '\u300b';
            }
            case '\u300c': {
                return '\u300d';
            }
            case '\u300e': {
                return '\u300f';
            }
            case '\u301d': {
                return '\u301f';
            }
        }
    }
    
    private static boolean isClosingingQuote(final char c, final char c2, final char c3) {
        if (c != c3) {
            if (c2 != '\u301d' || c != '\u301e') {
                if (c != '\u301f') {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean isSurroundingQuote(final char c, final char c2, final char c3) {
        return c == c2 || isClosingingQuote(c, c2, c3);
    }
    
    private static boolean itemValuesMatch(final XMPNode xmpNode, final XMPNode xmpNode2) {
        final int n = 1;
        final PropertyOptions options = xmpNode.getOptions();
        if (!options.equals(xmpNode2.getOptions())) {
            if (options.getOptions() != 0) {
                if (!options.isStruct()) {
                    assert options.isArray();
                    final Iterator iterateChildren = xmpNode.iterateChildren();
                Label_0064:
                    while (iterateChildren.hasNext()) {
                        final XMPNode xmpNode3 = iterateChildren.next();
                        final Iterator iterateChildren2 = xmpNode2.iterateChildren();
                        while (true) {
                            while (iterateChildren2.hasNext()) {
                                if (itemValuesMatch(xmpNode3, iterateChildren2.next())) {
                                    final int n2 = n;
                                    if (n2 == 0) {
                                        return false;
                                    }
                                    continue Label_0064;
                                }
                            }
                            final int n2 = 0;
                            continue;
                        }
                    }
                }
                else {
                    if (xmpNode.getChildrenLength() != xmpNode2.getChildrenLength()) {
                        return false;
                    }
                    final Iterator iterateChildren3 = xmpNode.iterateChildren();
                    while (iterateChildren3.hasNext()) {
                        final XMPNode xmpNode4 = iterateChildren3.next();
                        final XMPNode childNode = XMPNodeUtils.findChildNode(xmpNode2, xmpNode4.getName(), false);
                        if (childNode != null && itemValuesMatch(xmpNode4, childNode)) {
                            continue;
                        }
                        return false;
                    }
                }
            }
            else {
                if (!xmpNode.getValue().equals(xmpNode2.getValue())) {
                    return false;
                }
                if (xmpNode.getOptions().getHasLanguage() != xmpNode2.getOptions().getHasLanguage()) {
                    return false;
                }
                if (xmpNode.getOptions().getHasLanguage() && !xmpNode.getQualifier(n).getValue().equals(xmpNode2.getQualifier(n).getValue())) {
                    return false;
                }
            }
            return n != 0;
        }
        return false;
    }
    
    public static void removeProperties(final XMPMeta xmpMeta, final String s, final String s2, final boolean b, final boolean b2) {
        ParameterAsserts.assertImplementation(xmpMeta);
        final XMPMetaImpl xmpMetaImpl = (XMPMetaImpl)xmpMeta;
        if (s2 != null && s2.length() > 0) {
            if (s == null || s.length() == 0) {
                throw new XMPException("Property name requires schema namespace", 4);
            }
            final XMPPath expandXPath = XMPPathParser.expandXPath(s, s2);
            final XMPNode node = XMPNodeUtils.findNode(xmpMetaImpl.getRoot(), expandXPath, false, null);
            if (node != null) {
                if (b || !Utils.isInternalProperty(expandXPath.getSegment(0).getName(), expandXPath.getSegment(1).getName())) {
                    final XMPNode parent = node.getParent();
                    parent.removeChild(node);
                    if (parent.getOptions().isSchemaNode() && !parent.hasChildren()) {
                        parent.getParent().removeChild(parent);
                    }
                }
            }
        }
        else if (s != null && s.length() > 0) {
            final XMPNode schemaNode = XMPNodeUtils.findSchemaNode(xmpMetaImpl.getRoot(), s, false);
            if (schemaNode != null && removeSchemaChildren(schemaNode, b)) {
                xmpMetaImpl.getRoot().removeChild(schemaNode);
            }
            if (b2) {
                final XMPAliasInfo[] aliases = XMPMetaFactory.getSchemaRegistry().findAliases(s);
                for (int i = 0; i < aliases.length; ++i) {
                    final XMPAliasInfo xmpAliasInfo = aliases[i];
                    final XMPNode node2 = XMPNodeUtils.findNode(xmpMetaImpl.getRoot(), XMPPathParser.expandXPath(xmpAliasInfo.getNamespace(), xmpAliasInfo.getPropName()), false, null);
                    if (node2 != null) {
                        node2.getParent().removeChild(node2);
                    }
                }
            }
        }
        else {
            final Iterator iterateChildren = xmpMetaImpl.getRoot().iterateChildren();
            while (iterateChildren.hasNext()) {
                if (removeSchemaChildren(iterateChildren.next(), b)) {
                    iterateChildren.remove();
                }
            }
        }
    }
    
    private static boolean removeSchemaChildren(final XMPNode xmpNode, final boolean b) {
        final Iterator iterateChildren = xmpNode.iterateChildren();
        while (iterateChildren.hasNext()) {
            final XMPNode xmpNode2 = iterateChildren.next();
            if (!b && Utils.isInternalProperty(xmpNode.getName(), xmpNode2.getName())) {
                continue;
            }
            iterateChildren.remove();
        }
        return !xmpNode.hasChildren();
    }
    
    public static void separateArrayItems(final XMPMeta xmpMeta, final String s, final String s2, final String s3, final PropertyOptions propertyOptions, final boolean b) {
        ParameterAsserts.assertSchemaNS(s);
        ParameterAsserts.assertArrayName(s2);
        if (s3 != null) {
            ParameterAsserts.assertImplementation(xmpMeta);
            final XMPNode separateFindCreateArray = separateFindCreateArray(s, s2, propertyOptions, (XMPMetaImpl)xmpMeta);
            int n = 0;
            char char1 = '\0';
            int i = 0;
            final int length = s3.length();
        Label_0049:
            while (i < length) {
                char char2 = char1;
                int j;
                for (j = i; j < length; ++j) {
                    char2 = s3.charAt(j);
                    n = classifyCharacter(char2);
                    if (n == 0 || n == 4) {
                        break;
                    }
                }
                if (j < length) {
                    String s4;
                    if (n == 4) {
                        final char closingQuote = getClosingQuote(char2);
                        i = j + 1;
                        s4 = "";
                        char1 = char2;
                        while (i < length) {
                            char1 = s3.charAt(i);
                            n = classifyCharacter(char1);
                            if (n == 4 && isSurroundingQuote(char1, char2, closingQuote)) {
                                char char3;
                                if (i + 1 >= length) {
                                    char3 = ';';
                                }
                                else {
                                    char3 = s3.charAt(i + 1);
                                    classifyCharacter(char3);
                                }
                                if (char1 != char3) {
                                    if (isClosingingQuote(char1, char2, closingQuote)) {
                                        ++i;
                                        break;
                                    }
                                    s4 = String.valueOf(s4) + char1;
                                }
                                else {
                                    s4 = String.valueOf(s4) + char1;
                                    ++i;
                                }
                            }
                            else {
                                s4 = String.valueOf(s4) + char1;
                            }
                            ++i;
                        }
                    }
                    else {
                        char c = char2;
                        int classifyCharacter = n;
                        int k;
                        for (k = j; k < length; ++k) {
                            c = s3.charAt(k);
                            classifyCharacter = classifyCharacter(c);
                            if (classifyCharacter != 0 && classifyCharacter != 4 && (classifyCharacter != 2 || !b)) {
                                if (classifyCharacter != 1 || k + 1 >= length) {
                                    break;
                                }
                                c = s3.charAt(k + 1);
                                final int classifyCharacter2 = classifyCharacter(c);
                                if (classifyCharacter2 != 0 && classifyCharacter2 != 4) {
                                    if (classifyCharacter2 != 2) {
                                        break;
                                    }
                                    if (!b) {
                                        break;
                                    }
                                }
                            }
                        }
                        final String substring = s3.substring(j, k);
                        char1 = c;
                        s4 = substring;
                        final int n2 = k;
                        n = classifyCharacter;
                        i = n2;
                    }
                    final int n3 = -1;
                    int l = 1;
                    while (true) {
                        while (l <= separateFindCreateArray.getChildrenLength()) {
                            if (!s4.equals(separateFindCreateArray.getChild(l).getValue())) {
                                ++l;
                            }
                            else {
                                if (l < 0) {
                                    separateFindCreateArray.addChild(new XMPNode("[]", s4, null));
                                    continue Label_0049;
                                }
                                continue Label_0049;
                            }
                        }
                        l = n3;
                        continue;
                    }
                }
                break;
            }
            return;
        }
        throw new XMPException("Parameter must not be null", 4);
    }
    
    private static XMPNode separateFindCreateArray(final String s, final String s2, final PropertyOptions propertyOptions, final XMPMetaImpl xmpMetaImpl) {
        final boolean array = true;
        final int n = 102;
        final PropertyOptions verifySetOptions = XMPNodeUtils.verifySetOptions(propertyOptions, null);
        if (verifySetOptions.isOnlyArrayOptions()) {
            final XMPPath expandXPath = XMPPathParser.expandXPath(s, s2);
            XMPNode xmpNode = XMPNodeUtils.findNode(xmpMetaImpl.getRoot(), expandXPath, false, null);
            if (xmpNode == null) {
                xmpNode = XMPNodeUtils.findNode(xmpMetaImpl.getRoot(), expandXPath, array, verifySetOptions.setArray(array));
                if (xmpNode == null) {
                    throw new XMPException("Failed to create named array", n);
                }
            }
            else {
                final PropertyOptions options = xmpNode.getOptions();
                if (options.isArray() || options.isArrayAlternate()) {
                    throw new XMPException("Named property must be non-alternate array", n);
                }
                if (verifySetOptions.equalArrayTypes(options)) {
                    throw new XMPException("Mismatch of specified and existing array form", n);
                }
            }
            return xmpNode;
        }
        throw new XMPException("Options can only provide array form", 103);
    }
}
