// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;

public class Utils implements XMPConst
{
    public static final int UUID_LENGTH = 36;
    public static final int UUID_SEGMENT_COUNT = 4;
    private static boolean[] xmlNameChars;
    private static boolean[] xmlNameStartChars;
    
    static {
        initCharTables();
    }
    
    static boolean checkUUIDFormat(final String s) {
        final boolean b = true;
        final float n = Float.MIN_VALUE;
        if (s != null) {
            int i = 0;
            int n2 = 0;
            int n3 = b ? 1 : 0;
            float n4 = n;
            while (i < s.length()) {
                int n5;
                float n6;
                if (s.charAt(i) != '-') {
                    n5 = n3;
                    n6 = n4;
                }
                else {
                    final int n7 = n2 + 1;
                    int n8;
                    float n9;
                    if (n3 != 0 && (i == 8 || i == 13 || i == 18 || i == 23)) {
                        n8 = (b ? 1 : 0);
                        n9 = n;
                    }
                    else {
                        n8 = 0;
                        n9 = 0.0f;
                    }
                    final int n10 = n7;
                    n5 = n8;
                    n6 = n9;
                    n2 = n10;
                }
                ++i;
                n3 = n5;
                n4 = n6;
            }
            return n3 != 0 && 4 == n2 && 36 == i && b;
        }
        return false;
    }
    
    public static String escapeXML(final String s, final boolean b, final boolean b2) {
        final char c = '\r';
        final char c2 = '\n';
        final char c3 = '\t';
        int i = 0;
        int j = 0;
        while (true) {
            while (j < s.length()) {
                final char char1 = s.charAt(j);
                Label_0052: {
                    if (char1 != '<' && char1 != '>' && char1 != '&') {
                        if (b2) {
                            if (char1 == c3 || char1 == c2) {
                                break Label_0052;
                            }
                            if (char1 == c) {
                                break Label_0052;
                            }
                        }
                        if (b || char1 != '\"') {
                            ++j;
                            continue;
                        }
                    }
                }
                final int n = 1;
                if (n != 0) {
                    final StringBuffer sb = new StringBuffer(s.length() * 4 / 3);
                    while (i < s.length()) {
                        final char char2 = s.charAt(i);
                        if (b2 && (char2 == c3 || char2 == c2 || char2 == c)) {
                            sb.append("&#x");
                            sb.append(Integer.toHexString(char2).toUpperCase());
                            sb.append(';');
                        }
                        else {
                            switch (char2) {
                                default: {
                                    sb.append(char2);
                                    break;
                                }
                                case '<': {
                                    sb.append("&lt;");
                                    break;
                                }
                                case '>': {
                                    sb.append("&gt;");
                                    break;
                                }
                                case '&': {
                                    sb.append("&amp;");
                                    break;
                                }
                                case '\"': {
                                    String s2;
                                    if (!b) {
                                        s2 = "\"";
                                    }
                                    else {
                                        s2 = "&quot;";
                                    }
                                    sb.append(s2);
                                    break;
                                }
                            }
                        }
                        ++i;
                    }
                    return sb.toString();
                }
                return s;
            }
            final int n = 0;
            continue;
        }
    }
    
    private static void initCharTables() {
        final int n = 90;
        final int n2 = 65;
        final int n3 = 58;
        final boolean b = true;
        Utils.xmlNameChars = new boolean[256];
        Utils.xmlNameStartChars = new boolean[256];
        for (int i = 0; i < Utils.xmlNameChars.length; i = (char)(i + 1)) {
            final boolean[] xmlNameStartChars = Utils.xmlNameStartChars;
            boolean b2 = false;
            Label_0086: {
                if (97 > i || i > 122) {
                    if (n2 > i || i > n) {
                        if (i != n3 && i != 95) {
                            if (192 > i || i > 214) {
                                if (216 > i || i > 246) {
                                    b2 = false;
                                    break Label_0086;
                                }
                            }
                        }
                    }
                }
                b2 = b;
            }
            xmlNameStartChars[i] = b2;
            final boolean[] xmlNameChars = Utils.xmlNameChars;
            boolean b3 = false;
            Label_0135: {
                if (97 > i || i > 122) {
                    if (n2 > i || i > n) {
                        if (48 > i || i > 57) {
                            if (i != n3 && i != 95 && i != 45 && i != 46 && i != 183) {
                                if (192 > i || i > 214) {
                                    if (216 > i || i > 246) {
                                        b3 = false;
                                        break Label_0135;
                                    }
                                }
                            }
                        }
                    }
                }
                b3 = b;
            }
            xmlNameChars[i] = b3;
        }
    }
    
    static boolean isControlChar(final char c) {
        if (c <= '\u001f' || c == '\u007f') {
            if (c != '\t' && c != '\n' && c != '\r') {
                return true;
            }
        }
        return false;
    }
    
    static boolean isInternalProperty(final String s, final String s2) {
        final boolean b = true;
        boolean b2 = false;
        if (!"http://purl.org/dc/elements/1.1/".equals(s)) {
            if (!"http://ns.adobe.com/xap/1.0/".equals(s)) {
                if (!"http://ns.adobe.com/pdf/1.3/".equals(s)) {
                    if (!"http://ns.adobe.com/tiff/1.0/".equals(s)) {
                        if (!"http://ns.adobe.com/exif/1.0/".equals(s)) {
                            if (!"http://ns.adobe.com/exif/1.0/aux/".equals(s)) {
                                if (!"http://ns.adobe.com/photoshop/1.0/".equals(s)) {
                                    if (!"http://ns.adobe.com/camera-raw-settings/1.0/".equals(s)) {
                                        if (!"http://ns.adobe.com/StockPhoto/1.0/".equals(s)) {
                                            if (!"http://ns.adobe.com/xap/1.0/mm/".equals(s)) {
                                                if (!"http://ns.adobe.com/xap/1.0/t/".equals(s)) {
                                                    if (!"http://ns.adobe.com/xap/1.0/t/pg/".equals(s)) {
                                                        if (!"http://ns.adobe.com/xap/1.0/g/".equals(s)) {
                                                            if (!"http://ns.adobe.com/xap/1.0/g/img/".equals(s)) {
                                                                if ("http://ns.adobe.com/xap/1.0/sType/Font#".equals(s)) {
                                                                    b2 = b;
                                                                }
                                                            }
                                                            else {
                                                                b2 = b;
                                                            }
                                                        }
                                                        else {
                                                            b2 = b;
                                                        }
                                                    }
                                                    else {
                                                        b2 = b;
                                                    }
                                                }
                                                else {
                                                    b2 = b;
                                                }
                                            }
                                            else {
                                                b2 = b;
                                            }
                                        }
                                        else {
                                            b2 = b;
                                        }
                                    }
                                    else if ("crs:Version".equals(s2) || "crs:RawFileName".equals(s2) || "crs:ToneCurveName".equals(s2)) {
                                        b2 = b;
                                    }
                                }
                                else if ("photoshop:ICCProfile".equals(s2)) {
                                    b2 = b;
                                }
                            }
                            else {
                                b2 = b;
                            }
                        }
                        else if (!"exif:UserComment".equals(s2)) {
                            b2 = b;
                        }
                    }
                    else if (!"tiff:ImageDescription".equals(s2) && !"tiff:Artist".equals(s2) && !"tiff:Copyright".equals(s2)) {
                        b2 = b;
                    }
                }
                else if ("pdf:BaseURL".equals(s2) || "pdf:Creator".equals(s2) || "pdf:ModDate".equals(s2) || "pdf:PDFVersion".equals(s2) || "pdf:Producer".equals(s2)) {
                    b2 = b;
                }
            }
            else if ("xmp:BaseURL".equals(s2) || "xmp:CreatorTool".equals(s2) || "xmp:Format".equals(s2) || "xmp:Locale".equals(s2) || "xmp:MetadataDate".equals(s2) || "xmp:ModifyDate".equals(s2)) {
                b2 = b;
            }
        }
        else if ("dc:format".equals(s2) || "dc:language".equals(s2)) {
            b2 = b;
        }
        return b2;
    }
    
    private static boolean isNameChar(final char c) {
        return c > '\u00ff' || Utils.xmlNameChars[c];
    }
    
    private static boolean isNameStartChar(final char c) {
        return c > '\u00ff' || Utils.xmlNameStartChars[c];
    }
    
    public static boolean isXMLName(final String s) {
        final boolean b = true;
        if (s.length() > 0 && !isNameStartChar(s.charAt(0))) {
            return false;
        }
        for (int i = b ? 1 : 0; i < s.length(); ++i) {
            if (!isNameChar(s.charAt(i))) {
                return false;
            }
        }
        return b;
    }
    
    public static boolean isXMLNameNS(final String s) {
        final char c = ':';
        final boolean b = true;
        if (s.length() > 0 && (isNameStartChar(s.charAt(0)) || s.charAt(0) == c)) {
            return false;
        }
        for (int i = b ? 1 : 0; i < s.length(); ++i) {
            if (isNameChar(s.charAt(i)) || s.charAt(i) == c) {
                return false;
            }
        }
        return b;
    }
    
    public static String normalizeLangValue(final String s) {
        if (!"x-default".equals(s)) {
            final StringBuffer sb = new StringBuffer();
            int n = 1;
            int i = 0;
            while (i < s.length()) {
                Label_0115: {
                    switch (s.charAt(i)) {
                        default: {
                            if (n == 2) {
                                sb.append(Character.toUpperCase(s.charAt(i)));
                                break Label_0115;
                            }
                            sb.append(Character.toLowerCase(s.charAt(i)));
                            break Label_0115;
                        }
                        case '-':
                        case '_': {
                            sb.append('-');
                            ++n;
                        }
                        case ' ': {
                            ++i;
                            continue;
                        }
                    }
                }
            }
            return sb.toString();
        }
        return s;
    }
    
    static String removeControlChars(final String s) {
        int i;
        StringBuffer sb;
        for (i = 0, sb = new StringBuffer(s); i < sb.length(); ++i) {
            if (isControlChar(sb.charAt(i))) {
                sb.setCharAt(i, ' ');
            }
        }
        return sb.toString();
    }
    
    static String[] splitNameAndValue(final String s) {
        final int n = 2;
        final int n2 = 1;
        final int index = s.indexOf(61);
        int n3;
        if (s.charAt(n2) != '?') {
            n3 = n2;
        }
        else {
            n3 = n;
        }
        final String substring = s.substring(n3, index);
        final int n4 = index + 1;
        final char char1 = s.charAt(n4);
        int i = n4 + 1;
        final int n5 = s.length() - 2;
        final StringBuffer sb = new StringBuffer(n5 - index);
        while (i < n5) {
            sb.append(s.charAt(i));
            ++i;
            if (s.charAt(i) == char1) {
                ++i;
            }
        }
        final String[] array = new String[n];
        array[0] = substring;
        array[n2] = sb.toString();
        return array;
    }
}
