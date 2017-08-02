// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.properties.XMPAliasInfo;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.options.PropertyOptions;
import java.util.HashMap;
import java.util.Calendar;
import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPUtils;
import java.util.Iterator;
import com.adobe.xmp.XMPException;
import java.util.Map;

public class XMPNormalizer
{
    private static Map dcArrayForms;
    
    static {
        initDCArrays();
    }
    
    private static void compareAliasedSubtrees(final XMPNode xmpNode, final XMPNode xmpNode2, final boolean b) {
        final int n = 203;
        if (xmpNode.getValue().equals(xmpNode2.getValue()) || xmpNode.getChildrenLength() != xmpNode2.getChildrenLength()) {
            throw new XMPException("Mismatch between alias and base nodes", n);
        }
        if (!b && (!xmpNode.getName().equals(xmpNode2.getName()) || !xmpNode.getOptions().equals(xmpNode2.getOptions()) || xmpNode.getQualifierLength() != xmpNode2.getQualifierLength())) {
            throw new XMPException("Mismatch between alias and base nodes", n);
        }
        final Iterator iterateChildren = xmpNode.iterateChildren();
        final Iterator iterateChildren2 = xmpNode2.iterateChildren();
        while (iterateChildren.hasNext() && iterateChildren2.hasNext()) {
            compareAliasedSubtrees(iterateChildren.next(), iterateChildren2.next(), false);
        }
        final Iterator iterateQualifier = xmpNode.iterateQualifier();
        final Iterator iterateQualifier2 = xmpNode2.iterateQualifier();
        while (iterateQualifier.hasNext() && iterateQualifier2.hasNext()) {
            compareAliasedSubtrees(iterateQualifier.next(), iterateQualifier2.next(), false);
        }
    }
    
    private static void deleteEmptySchemas(final XMPNode xmpNode) {
        final Iterator iterateChildren = xmpNode.iterateChildren();
        while (iterateChildren.hasNext()) {
            if (!iterateChildren.next().hasChildren()) {
                iterateChildren.remove();
            }
        }
    }
    
    private static void fixGPSTimeStamp(final XMPNode xmpNode) {
        final XMPNode childNode = XMPNodeUtils.findChildNode(xmpNode, "exif:GPSTimeStamp", false);
        if (childNode == null) {
            return;
        }
        try {
            final String value = childNode.getValue();
            try {
                final XMPDateTime convertToDate = XMPUtils.convertToDate(value);
                try {
                    if (convertToDate.getYear() != 0 || convertToDate.getMonth() != 0 || convertToDate.getDay() != 0) {
                        return;
                    }
                    XMPNode xmpNode2 = XMPNodeUtils.findChildNode(xmpNode, "exif:DateTimeOriginal", false);
                    Label_0176: {
                        if (xmpNode2 == null) {
                            break Label_0176;
                        }
                        while (true) {
                            final String value2 = xmpNode2.getValue();
                            try {
                                final XMPDateTime convertToDate2 = XMPUtils.convertToDate(value2);
                                try {
                                    final Calendar calendar = convertToDate.getCalendar();
                                    calendar.set(1, convertToDate2.getYear());
                                    calendar.set(2, convertToDate2.getMonth());
                                    calendar.set(5, convertToDate2.getDay());
                                    childNode.setValue(XMPUtils.convertFromDate(new XMPDateTimeImpl(calendar)));
                                    return;
                                    xmpNode2 = XMPNodeUtils.findChildNode(xmpNode, "exif:DateTimeDigitized", false);
                                }
                                catch (XMPException ex) {}
                            }
                            catch (XMPException ex2) {}
                        }
                    }
                }
                catch (XMPException ex3) {}
            }
            catch (XMPException ex4) {}
        }
        catch (XMPException ex5) {}
    }
    
    private static void initDCArrays() {
        final boolean arrayAltText = true;
        XMPNormalizer.dcArrayForms = new HashMap();
        final PropertyOptions propertyOptions = new PropertyOptions();
        propertyOptions.setArray(arrayAltText);
        XMPNormalizer.dcArrayForms.put("dc:contributor", propertyOptions);
        XMPNormalizer.dcArrayForms.put("dc:language", propertyOptions);
        XMPNormalizer.dcArrayForms.put("dc:publisher", propertyOptions);
        XMPNormalizer.dcArrayForms.put("dc:relation", propertyOptions);
        XMPNormalizer.dcArrayForms.put("dc:subject", propertyOptions);
        XMPNormalizer.dcArrayForms.put("dc:type", propertyOptions);
        final PropertyOptions propertyOptions2 = new PropertyOptions();
        propertyOptions2.setArray(arrayAltText);
        propertyOptions2.setArrayOrdered(arrayAltText);
        XMPNormalizer.dcArrayForms.put("dc:creator", propertyOptions2);
        XMPNormalizer.dcArrayForms.put("dc:date", propertyOptions2);
        final PropertyOptions propertyOptions3 = new PropertyOptions();
        propertyOptions3.setArray(arrayAltText);
        propertyOptions3.setArrayOrdered(arrayAltText);
        propertyOptions3.setArrayAlternate(arrayAltText);
        propertyOptions3.setArrayAltText(arrayAltText);
        XMPNormalizer.dcArrayForms.put("dc:description", propertyOptions3);
        XMPNormalizer.dcArrayForms.put("dc:rights", propertyOptions3);
        XMPNormalizer.dcArrayForms.put("dc:title", propertyOptions3);
    }
    
    private static void migrateAudioCopyright(final XMPMeta xmpMeta, final XMPNode xmpNode) {
    Label_0129:
        while (true) {
            XMPNode child = null;
            StringBuilder append3 = null;
            try {
                final XMPNode schemaNode = XMPNodeUtils.findSchemaNode(((XMPMetaImpl)xmpMeta).getRoot(), "http://purl.org/dc/elements/1.1/", true);
                try {
                    final String value = xmpNode.getValue();
                    final String s = "\n\n";
                    final XMPNode childNode = XMPNodeUtils.findChildNode(schemaNode, "dc:rights", false);
                    Label_0140: {
                        if (childNode != null) {
                            break Label_0140;
                        }
                    Label_0168_Outer:
                        while (true) {
                            try {
                                final StringBuilder sb = new StringBuilder(String.valueOf(s));
                                try {
                                    final StringBuilder append = sb.append(value);
                                    try {
                                        xmpMeta.setLocalizedText("http://purl.org/dc/elements/1.1/", "rights", "", "x-default", append.toString(), null);
                                        xmpNode.getParent().removeChild(xmpNode);
                                        return;
                                        // iftrue(Label_0292:, n < 0)
                                        // iftrue(Label_0064:, !childNode.hasChildren())
                                    Label_0168:
                                        while (true) {
                                            int n;
                                            while (true) {
                                                n = XMPNodeUtils.lookupLanguageItem(childNode, "x-default");
                                                break Label_0168;
                                                continue Label_0168_Outer;
                                            }
                                            child = childNode.getChild(n);
                                            try {
                                                final String value2 = child.getValue();
                                                final int index = value2.indexOf(s);
                                                Label_0358: {
                                                    if (index < 0) {
                                                        break Label_0358;
                                                    }
                                                    if (value2.substring(index + 2).equals(value)) {
                                                        continue Label_0129;
                                                    }
                                                    final String substring = value2.substring(0, index + 2);
                                                    try {
                                                        final StringBuilder sb2 = new StringBuilder(String.valueOf(substring));
                                                        try {
                                                            final StringBuilder append2 = sb2.append(value);
                                                            try {
                                                                child.setValue(append2.toString());
                                                                continue Label_0129;
                                                                final XMPNode child2;
                                                                Label_0292: {
                                                                    child2 = childNode.getChild(1);
                                                                }
                                                                try {
                                                                    xmpMeta.setLocalizedText("http://purl.org/dc/elements/1.1/", "rights", "", "x-default", child2.getValue(), null);
                                                                    n = XMPNodeUtils.lookupLanguageItem(childNode, "x-default");
                                                                    continue Label_0168;
                                                                    try {
                                                                        final StringBuilder sb3 = new StringBuilder(String.valueOf(value2));
                                                                        try {
                                                                            append3 = sb3.append(s).append(value);
                                                                        }
                                                                        catch (XMPException ex) {}
                                                                    }
                                                                    catch (XMPException ex2) {}
                                                                }
                                                                // iftrue(Label_0129:, value.equals((Object)value2))
                                                                catch (XMPException ex3) {}
                                                            }
                                                            catch (XMPException ex4) {}
                                                        }
                                                        catch (XMPException ex5) {}
                                                    }
                                                    catch (XMPException ex6) {}
                                                }
                                            }
                                            catch (XMPException ex7) {}
                                            break;
                                        }
                                    }
                                    catch (XMPException ex8) {}
                                }
                                catch (XMPException ex9) {}
                            }
                            catch (XMPException ex10) {}
                        }
                    }
                }
                catch (XMPException ex11) {}
            }
            catch (XMPException ex12) {}
            child.setValue(append3.toString());
            continue Label_0129;
        }
    }
    
    private static void moveExplicitAliases(final XMPNode xmpNode, final ParseOptions parseOptions) {
        final int n = 1;
        if (xmpNode.getHasAliases()) {
            xmpNode.setHasAliases(false);
            final boolean strictAliasing = parseOptions.getStrictAliasing();
            for (final XMPNode xmpNode2 : xmpNode.getUnmodifiableChildren()) {
                if (xmpNode2.getHasAliases()) {
                    final Iterator iterateChildren = xmpNode2.iterateChildren();
                    while (iterateChildren.hasNext()) {
                        final XMPNode xmpNode3 = iterateChildren.next();
                        if (xmpNode3.isAlias()) {
                            xmpNode3.setAlias(false);
                            final XMPAliasInfo alias = XMPMetaFactory.getSchemaRegistry().findAlias(xmpNode3.getName());
                            if (alias == null) {
                                continue;
                            }
                            final XMPNode schemaNode = XMPNodeUtils.findSchemaNode(xmpNode, alias.getNamespace(), null, n != 0);
                            schemaNode.setImplicit(false);
                            final XMPNode childNode = XMPNodeUtils.findChildNode(schemaNode, String.valueOf(alias.getPrefix()) + alias.getPropName(), false);
                            if (childNode != null) {
                                if (!alias.getAliasForm().isSimple()) {
                                    XMPNode xmpNode4;
                                    if (!alias.getAliasForm().isArrayAltText()) {
                                        if (!childNode.hasChildren()) {
                                            xmpNode4 = null;
                                        }
                                        else {
                                            xmpNode4 = childNode.getChild(n);
                                        }
                                    }
                                    else {
                                        final int lookupLanguageItem = XMPNodeUtils.lookupLanguageItem(childNode, "x-default");
                                        if (lookupLanguageItem == -1) {
                                            xmpNode4 = null;
                                        }
                                        else {
                                            xmpNode4 = childNode.getChild(lookupLanguageItem);
                                        }
                                    }
                                    if (xmpNode4 != null) {
                                        if (strictAliasing) {
                                            compareAliasedSubtrees(xmpNode3, xmpNode4, n != 0);
                                        }
                                        iterateChildren.remove();
                                    }
                                    else {
                                        transplantArrayItemAlias(iterateChildren, xmpNode3, childNode);
                                    }
                                }
                                else {
                                    if (strictAliasing) {
                                        compareAliasedSubtrees(xmpNode3, childNode, n != 0);
                                    }
                                    iterateChildren.remove();
                                }
                            }
                            else if (!alias.getAliasForm().isSimple()) {
                                final XMPNode xmpNode5 = new XMPNode(String.valueOf(alias.getPrefix()) + alias.getPropName(), alias.getAliasForm().toPropertyOptions());
                                schemaNode.addChild(xmpNode5);
                                transplantArrayItemAlias(iterateChildren, xmpNode3, xmpNode5);
                            }
                            else {
                                xmpNode3.setName(String.valueOf(alias.getPrefix()) + alias.getPropName());
                                schemaNode.addChild(xmpNode3);
                                iterateChildren.remove();
                            }
                        }
                    }
                    xmpNode2.setHasAliases(false);
                }
            }
        }
    }
    
    private static void normalizeDCArrays(final XMPNode xmpNode) {
        for (int i = 1; i <= xmpNode.getChildrenLength(); ++i) {
            final XMPNode child = xmpNode.getChild(i);
            final PropertyOptions propertyOptions = XMPNormalizer.dcArrayForms.get(child.getName());
            if (propertyOptions != null) {
                if (!child.getOptions().isSimple()) {
                    child.getOptions().setOption(7680, false);
                    child.getOptions().mergeWith(propertyOptions);
                    if (propertyOptions.isArrayAltText()) {
                        repairAltText(child);
                    }
                }
                else {
                    final XMPNode xmpNode2 = new XMPNode(child.getName(), propertyOptions);
                    child.setName("[]");
                    xmpNode2.addChild(child);
                    xmpNode.replaceChild(i, xmpNode2);
                    if (propertyOptions.isArrayAltText() && !child.getOptions().getHasLanguage()) {
                        child.addQualifier(new XMPNode("xml:lang", "x-default", null));
                    }
                }
            }
        }
    }
    
    static XMPMeta process(final XMPMetaImpl xmpMetaImpl, final ParseOptions parseOptions) {
        final XMPNode root = xmpMetaImpl.getRoot();
        touchUpDataModel(xmpMetaImpl);
        moveExplicitAliases(root, parseOptions);
        tweakOldXMP(root);
        deleteEmptySchemas(root);
        return xmpMetaImpl;
    }
    
    private static void repairAltText(final XMPNode xmpNode) {
        final boolean arrayOrdered = true;
        if (xmpNode != null && xmpNode.getOptions().isArray()) {
            xmpNode.getOptions().setArrayOrdered(arrayOrdered).setArrayAlternate(arrayOrdered).setArrayAltText(arrayOrdered);
            final Iterator iterateChildren = xmpNode.iterateChildren();
            while (iterateChildren.hasNext()) {
                final XMPNode xmpNode2 = iterateChildren.next();
                if (!xmpNode2.getOptions().isCompositeProperty()) {
                    if (xmpNode2.getOptions().getHasLanguage()) {
                        continue;
                    }
                    final String value = xmpNode2.getValue();
                    if (value != null && value.length() != 0) {
                        xmpNode2.addQualifier(new XMPNode("xml:lang", "x-repair", null));
                    }
                    else {
                        iterateChildren.remove();
                    }
                }
                else {
                    iterateChildren.remove();
                }
            }
        }
    }
    
    private static void touchUpDataModel(final XMPMetaImpl xmpMetaImpl) {
        XMPNodeUtils.findSchemaNode(xmpMetaImpl.getRoot(), "http://purl.org/dc/elements/1.1/", true);
        final Iterator iterateChildren = xmpMetaImpl.getRoot().iterateChildren();
        while (iterateChildren.hasNext()) {
            final XMPNode xmpNode = iterateChildren.next();
            if (!"http://purl.org/dc/elements/1.1/".equals(xmpNode.getName())) {
                if (!"http://ns.adobe.com/exif/1.0/".equals(xmpNode.getName())) {
                    if (!"http://ns.adobe.com/xmp/1.0/DynamicMedia/".equals(xmpNode.getName())) {
                        if (!"http://ns.adobe.com/xap/1.0/rights/".equals(xmpNode.getName())) {
                            continue;
                        }
                        final XMPNode childNode = XMPNodeUtils.findChildNode(xmpNode, "xmpRights:UsageTerms", false);
                        if (childNode == null) {
                            continue;
                        }
                        repairAltText(childNode);
                    }
                    else {
                        final XMPNode childNode2 = XMPNodeUtils.findChildNode(xmpNode, "xmpDM:copyright", false);
                        if (childNode2 == null) {
                            continue;
                        }
                        migrateAudioCopyright(xmpMetaImpl, childNode2);
                    }
                }
                else {
                    fixGPSTimeStamp(xmpNode);
                    final XMPNode childNode3 = XMPNodeUtils.findChildNode(xmpNode, "exif:UserComment", false);
                    if (childNode3 == null) {
                        continue;
                    }
                    repairAltText(childNode3);
                }
            }
            else {
                normalizeDCArrays(xmpNode);
            }
        }
    }
    
    private static void transplantArrayItemAlias(final Iterator iterator, final XMPNode xmpNode, final XMPNode xmpNode2) {
        if (xmpNode2.getOptions().isArrayAltText()) {
            if (xmpNode.getOptions().getHasLanguage()) {
                throw new XMPException("Alias to x-default already has a language qualifier", 203);
            }
            xmpNode.addQualifier(new XMPNode("xml:lang", "x-default", null));
        }
        iterator.remove();
        xmpNode.setName("[]");
        xmpNode2.addChild(xmpNode);
    }
    
    private static void tweakOldXMP(final XMPNode xmpNode) {
        if (xmpNode.getName() != null && xmpNode.getName().length() >= 36) {
            String s = xmpNode.getName().toLowerCase();
            if (s.startsWith("uuid:")) {
                s = s.substring(5);
            }
            if (Utils.checkUUIDFormat(s)) {
                final XMPNode node = XMPNodeUtils.findNode(xmpNode, XMPPathParser.expandXPath("http://ns.adobe.com/xap/1.0/mm/", "InstanceID"), true, null);
                if (node == null) {
                    throw new XMPException("Failure creating xmpMM:InstanceID", 9);
                }
                node.setOptions(null);
                node.setValue("uuid:" + s);
                node.removeChildren();
                node.removeQualifiers();
                xmpNode.setName(null);
            }
        }
    }
}
