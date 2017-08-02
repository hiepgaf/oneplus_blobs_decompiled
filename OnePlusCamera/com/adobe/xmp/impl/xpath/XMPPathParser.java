// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl.xpath;

import com.adobe.xmp.impl.Utils;
import com.adobe.xmp.properties.XMPAliasInfo;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPException;

public final class XMPPathParser
{
    public static XMPPath expandXPath(final String s, final String path) {
        final char c = '@';
        final char c2 = '?';
        final int kind = 2;
        final int n = 1;
        if (s != null && path != null) {
            final XMPPath xmpPath = new XMPPath();
            final PathPosition pathPosition = new PathPosition();
            pathPosition.path = path;
            parseRootNode(s, pathPosition, xmpPath);
            while (pathPosition.stepEnd < path.length()) {
                pathPosition.stepBegin = pathPosition.stepEnd;
                skipPathDelimiter(path, pathPosition);
                pathPosition.stepEnd = pathPosition.stepBegin;
                XMPPathSegment xmpPathSegment;
                if (path.charAt(pathPosition.stepBegin) == '[') {
                    xmpPathSegment = parseIndexSegment(pathPosition);
                }
                else {
                    xmpPathSegment = parseStructSegment(pathPosition);
                }
                if (xmpPathSegment.getKind() != n) {
                    if (xmpPathSegment.getKind() == 6) {
                        if (xmpPathSegment.getName().charAt(n) == c) {
                            xmpPathSegment.setName("[?" + xmpPathSegment.getName().substring(kind));
                            if (!xmpPathSegment.getName().startsWith("[?xml:lang=")) {
                                throw new XMPException("Only xml:lang allowed with '@'", 102);
                            }
                        }
                        if (xmpPathSegment.getName().charAt(n) == c2) {
                            ++pathPosition.nameStart;
                            xmpPathSegment.setKind(5);
                            verifyQualName(pathPosition.path.substring(pathPosition.nameStart, pathPosition.nameEnd));
                        }
                    }
                }
                else {
                    if (xmpPathSegment.getName().charAt(0) == c) {
                        xmpPathSegment.setName("?" + xmpPathSegment.getName().substring(n));
                        if (!"?xml:lang".equals(xmpPathSegment.getName())) {
                            throw new XMPException("Only xml:lang allowed with '@'", 102);
                        }
                    }
                    if (xmpPathSegment.getName().charAt(0) == c2) {
                        ++pathPosition.nameStart;
                        xmpPathSegment.setKind(kind);
                    }
                    verifyQualName(pathPosition.path.substring(pathPosition.nameStart, pathPosition.nameEnd));
                }
                xmpPath.add(xmpPathSegment);
            }
            return xmpPath;
        }
        throw new XMPException("Parameter must not be null", 4);
    }
    
    private static XMPPathSegment parseIndexSegment(final PathPosition pathPosition) {
        final char c = '9';
        final char c2 = '0';
        final char c3 = ']';
        final int n = 102;
        ++pathPosition.stepEnd;
        XMPPathSegment xmpPathSegment;
        if (c2 <= pathPosition.path.charAt(pathPosition.stepEnd) && pathPosition.path.charAt(pathPosition.stepEnd) <= c) {
            while (pathPosition.stepEnd < pathPosition.path.length()) {
                if (c2 > pathPosition.path.charAt(pathPosition.stepEnd)) {
                    break;
                }
                if (pathPosition.path.charAt(pathPosition.stepEnd) > c) {
                    break;
                }
                ++pathPosition.stepEnd;
            }
            xmpPathSegment = new XMPPathSegment(null, 3);
        }
        else {
            while (pathPosition.stepEnd < pathPosition.path.length()) {
                if (pathPosition.path.charAt(pathPosition.stepEnd) == c3) {
                    break;
                }
                if (pathPosition.path.charAt(pathPosition.stepEnd) == '=') {
                    break;
                }
                ++pathPosition.stepEnd;
            }
            if (pathPosition.stepEnd >= pathPosition.path.length()) {
                throw new XMPException("Missing ']' or '=' for array index", n);
            }
            if (pathPosition.path.charAt(pathPosition.stepEnd) != c3) {
                pathPosition.nameStart = pathPosition.stepBegin + 1;
                pathPosition.nameEnd = pathPosition.stepEnd;
                ++pathPosition.stepEnd;
                final char char1 = pathPosition.path.charAt(pathPosition.stepEnd);
                if (char1 != '\'' && char1 != '\"') {
                    throw new XMPException("Invalid quote in array selector", n);
                }
                ++pathPosition.stepEnd;
                while (pathPosition.stepEnd < pathPosition.path.length()) {
                    if (pathPosition.path.charAt(pathPosition.stepEnd) == char1) {
                        if (pathPosition.stepEnd + 1 >= pathPosition.path.length() || pathPosition.path.charAt(pathPosition.stepEnd + 1) != char1) {
                            break;
                        }
                        ++pathPosition.stepEnd;
                    }
                    ++pathPosition.stepEnd;
                }
                if (pathPosition.stepEnd >= pathPosition.path.length()) {
                    throw new XMPException("No terminating quote for array selector", n);
                }
                ++pathPosition.stepEnd;
                xmpPathSegment = new XMPPathSegment(null, 6);
            }
            else {
                if (!"[last()".equals(pathPosition.path.substring(pathPosition.stepBegin, pathPosition.stepEnd))) {
                    throw new XMPException("Invalid non-numeric array index", n);
                }
                xmpPathSegment = new XMPPathSegment(null, 4);
            }
        }
        if (pathPosition.stepEnd < pathPosition.path.length() && pathPosition.path.charAt(pathPosition.stepEnd) == c3) {
            ++pathPosition.stepEnd;
            xmpPathSegment.setName(pathPosition.path.substring(pathPosition.stepBegin, pathPosition.stepEnd));
            return xmpPathSegment;
        }
        throw new XMPException("Missing ']' for array index", n);
    }
    
    private static void parseRootNode(final String s, final PathPosition pathPosition, final XMPPath xmpPath) {
        final int n = -1 << -1;
        final boolean alias = true;
        while (pathPosition.stepEnd < pathPosition.path.length() && "/[*".indexOf(pathPosition.path.charAt(pathPosition.stepEnd)) < 0) {
            ++pathPosition.stepEnd;
        }
        if (pathPosition.stepEnd != pathPosition.stepBegin) {
            final String verifyXPathRoot = verifyXPathRoot(s, pathPosition.path.substring(pathPosition.stepBegin, pathPosition.stepEnd));
            final XMPAliasInfo alias2 = XMPMetaFactory.getSchemaRegistry().findAlias(verifyXPathRoot);
            if (alias2 != null) {
                xmpPath.add(new XMPPathSegment(alias2.getNamespace(), n));
                final XMPPathSegment xmpPathSegment = new XMPPathSegment(verifyXPathRoot(alias2.getNamespace(), alias2.getPropName()), alias ? 1 : 0);
                xmpPathSegment.setAlias(alias);
                xmpPathSegment.setAliasForm(alias2.getAliasForm().getOptions());
                xmpPath.add(xmpPathSegment);
                if (!alias2.getAliasForm().isArrayAltText()) {
                    if (alias2.getAliasForm().isArray()) {
                        final XMPPathSegment xmpPathSegment2 = new XMPPathSegment("[1]", 3);
                        xmpPathSegment2.setAlias(alias);
                        xmpPathSegment2.setAliasForm(alias2.getAliasForm().getOptions());
                        xmpPath.add(xmpPathSegment2);
                    }
                }
                else {
                    final XMPPathSegment xmpPathSegment3 = new XMPPathSegment("[?xml:lang='x-default']", 5);
                    xmpPathSegment3.setAlias(alias);
                    xmpPathSegment3.setAliasForm(alias2.getAliasForm().getOptions());
                    xmpPath.add(xmpPathSegment3);
                }
            }
            else {
                xmpPath.add(new XMPPathSegment(s, n));
                xmpPath.add(new XMPPathSegment(verifyXPathRoot, (int)(alias ? 1 : 0)));
            }
            return;
        }
        throw new XMPException("Empty initial XMPPath step", 102);
    }
    
    private static XMPPathSegment parseStructSegment(final PathPosition pathPosition) {
        pathPosition.nameStart = pathPosition.stepBegin;
        while (pathPosition.stepEnd < pathPosition.path.length() && "/[*".indexOf(pathPosition.path.charAt(pathPosition.stepEnd)) < 0) {
            ++pathPosition.stepEnd;
        }
        pathPosition.nameEnd = pathPosition.stepEnd;
        if (pathPosition.stepEnd != pathPosition.stepBegin) {
            return new XMPPathSegment(pathPosition.path.substring(pathPosition.stepBegin, pathPosition.stepEnd), 1);
        }
        throw new XMPException("Empty XMPPath segment", 102);
    }
    
    private static void skipPathDelimiter(final String s, final PathPosition pathPosition) {
        final int n = 102;
        if (s.charAt(pathPosition.stepBegin) == '/') {
            ++pathPosition.stepBegin;
            if (pathPosition.stepBegin >= s.length()) {
                throw new XMPException("Empty XMPPath segment", n);
            }
        }
        if (s.charAt(pathPosition.stepBegin) == '*') {
            ++pathPosition.stepBegin;
            if (pathPosition.stepBegin >= s.length() || s.charAt(pathPosition.stepBegin) != '[') {
                throw new XMPException("Missing '[' after '*'", n);
            }
        }
    }
    
    private static void verifyQualName(final String s) {
        final int n = 102;
        final int index = s.indexOf(58);
        if (index > 0) {
            final String substring = s.substring(0, index);
            if (Utils.isXMLNameNS(substring)) {
                if (XMPMetaFactory.getSchemaRegistry().getNamespaceURI(substring) == null) {
                    throw new XMPException("Unknown namespace prefix for qualified name", n);
                }
                return;
            }
        }
        throw new XMPException("Ill-formed qualified name", n);
    }
    
    private static void verifySimpleXMLName(final String s) {
        if (Utils.isXMLName(s)) {
            return;
        }
        throw new XMPException("Bad XML name", 102);
    }
    
    private static String verifyXPathRoot(final String s, final String s2) {
        final int n = 102;
        final int n2 = 101;
        if (s == null || s.length() == 0) {
            throw new XMPException("Schema namespace URI is required", n2);
        }
        if (s2.charAt(0) == '?' || s2.charAt(0) == '@') {
            throw new XMPException("Top level name must not be a qualifier", n);
        }
        if (s2.indexOf(47) >= 0 || s2.indexOf(91) >= 0) {
            throw new XMPException("Top level name must be simple", n);
        }
        final String namespacePrefix = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(s);
        if (namespacePrefix == null) {
            throw new XMPException("Unregistered schema namespace URI", n2);
        }
        final int index = s2.indexOf(58);
        if (index < 0) {
            verifySimpleXMLName(s2);
            return String.valueOf(namespacePrefix) + s2;
        }
        verifySimpleXMLName(s2.substring(0, index));
        verifySimpleXMLName(s2.substring(index));
        final String substring = s2.substring(0, index + 1);
        final String namespacePrefix2 = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(s);
        if (namespacePrefix2 == null) {
            throw new XMPException("Unknown schema namespace prefix", n2);
        }
        if (substring.equals(namespacePrefix2)) {
            return s2;
        }
        throw new XMPException("Schema namespace URI and prefix mismatch", n2);
    }
}
