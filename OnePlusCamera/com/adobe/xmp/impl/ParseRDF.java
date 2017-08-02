// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import org.w3c.dom.NamedNodeMap;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import java.util.Iterator;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import org.w3c.dom.Node;
import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPError;

public class ParseRDF implements XMPError, XMPConst
{
    public static final String DEFAULT_PREFIX = "_dflt";
    public static final int RDFTERM_ABOUT = 3;
    public static final int RDFTERM_ABOUT_EACH = 10;
    public static final int RDFTERM_ABOUT_EACH_PREFIX = 11;
    public static final int RDFTERM_BAG_ID = 12;
    public static final int RDFTERM_DATATYPE = 7;
    public static final int RDFTERM_DESCRIPTION = 8;
    public static final int RDFTERM_FIRST_CORE = 1;
    public static final int RDFTERM_FIRST_OLD = 10;
    public static final int RDFTERM_FIRST_SYNTAX = 1;
    public static final int RDFTERM_ID = 2;
    public static final int RDFTERM_LAST_CORE = 7;
    public static final int RDFTERM_LAST_OLD = 12;
    public static final int RDFTERM_LAST_SYNTAX = 9;
    public static final int RDFTERM_LI = 9;
    public static final int RDFTERM_NODE_ID = 6;
    public static final int RDFTERM_OTHER = 0;
    public static final int RDFTERM_PARSE_TYPE = 4;
    public static final int RDFTERM_RDF = 1;
    public static final int RDFTERM_RESOURCE = 5;
    
    static {
        boolean $assertionsDisabled = false;
        if (!ParseRDF.class.desiredAssertionStatus()) {
            $assertionsDisabled = true;
        }
    }
    
    private static XMPNode addChildNode(final XMPMetaImpl xmpMetaImpl, XMPNode schemaNode, final Node node, final String s, final boolean b) {
        final int n = 202;
        final int hasValueChild = 1;
        final XMPSchemaRegistry schemaRegistry = XMPMetaFactory.getSchemaRegistry();
        String namespaceURI = node.getNamespaceURI();
        if (namespaceURI == null) {
            throw new XMPException("XML namespace required for all elements and attributes", n);
        }
        if ("http://purl.org/dc/1.1/".equals(namespaceURI)) {
            namespaceURI = "http://purl.org/dc/elements/1.1/";
        }
        String s2 = schemaRegistry.getNamespacePrefix(namespaceURI);
        if (s2 == null) {
            String prefix;
            if (node.getPrefix() == null) {
                prefix = "_dflt";
            }
            else {
                prefix = node.getPrefix();
            }
            s2 = schemaRegistry.registerNamespace(namespaceURI, prefix);
        }
        final String string = String.valueOf(s2) + node.getLocalName();
        final PropertyOptions propertyOptions = new PropertyOptions();
        boolean alias;
        if (!b) {
            alias = false;
        }
        else {
            schemaNode = XMPNodeUtils.findSchemaNode(xmpMetaImpl.getRoot(), namespaceURI, "_dflt", hasValueChild != 0);
            schemaNode.setImplicit(false);
            if (schemaRegistry.findAlias(string) == null) {
                alias = false;
            }
            else {
                xmpMetaImpl.getRoot().setHasAliases(hasValueChild != 0);
                schemaNode.setHasAliases(hasValueChild != 0);
                alias = (hasValueChild != 0);
            }
        }
        final boolean equals = "rdf:li".equals(string);
        final boolean equals2 = "rdf:value".equals(string);
        final XMPNode xmpNode = new XMPNode(string, s, propertyOptions);
        xmpNode.setAlias(alias);
        if (equals2) {
            schemaNode.addChild(hasValueChild, xmpNode);
        }
        else {
            schemaNode.addChild(xmpNode);
        }
        if (equals2) {
            if (b || !schemaNode.getOptions().isStruct()) {
                throw new XMPException("Misplaced rdf:value element", n);
            }
            schemaNode.setHasValueChild(hasValueChild != 0);
        }
        if (equals) {
            if (!schemaNode.getOptions().isArray()) {
                throw new XMPException("Misplaced rdf:li element", n);
            }
            xmpNode.setName("[]");
        }
        return xmpNode;
    }
    
    private static XMPNode addQualifierNode(final XMPNode xmpNode, final String s, String normalizeLangValue) {
        if ("xml:lang".equals(s)) {
            normalizeLangValue = Utils.normalizeLangValue(normalizeLangValue);
        }
        final XMPNode xmpNode2 = new XMPNode(s, normalizeLangValue, null);
        xmpNode.addQualifier(xmpNode2);
        return xmpNode2;
    }
    
    private static void fixupQualifiedNode(final XMPNode xmpNode) {
        int i = 1;
        assert xmpNode.getOptions().isStruct() && xmpNode.hasChildren();
        final XMPNode child = xmpNode.getChild(i);
        assert "rdf:value".equals(child.getName());
        if (child.getOptions().getHasLanguage()) {
            if (xmpNode.getOptions().getHasLanguage()) {
                throw new XMPException("Redundant xml:lang for rdf:value element", 203);
            }
            final XMPNode qualifier = child.getQualifier(i);
            child.removeQualifier(qualifier);
            xmpNode.addQualifier(qualifier);
        }
        while (i <= child.getQualifierLength()) {
            xmpNode.addQualifier(child.getQualifier(i));
            ++i;
        }
        for (int j = 2; j <= xmpNode.getChildrenLength(); ++j) {
            xmpNode.addQualifier(xmpNode.getChild(j));
        }
        assert xmpNode.getOptions().isStruct() || xmpNode.getHasValueChild();
        xmpNode.setHasValueChild(false);
        xmpNode.getOptions().setStruct(false);
        xmpNode.getOptions().mergeWith(child.getOptions());
        xmpNode.setValue(child.getValue());
        xmpNode.removeChildren();
        final Iterator iterateChildren = child.iterateChildren();
        while (iterateChildren.hasNext()) {
            xmpNode.addChild(iterateChildren.next());
        }
    }
    
    private static int getRDFTermKind(final Node node) {
        final String localName = node.getLocalName();
        String namespaceURI = node.getNamespaceURI();
        if (namespaceURI == null) {
            if ("about".equals(localName) || "ID".equals(localName)) {
                if (node instanceof Attr && "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(((Attr)node).getOwnerElement().getNamespaceURI())) {
                    namespaceURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
                }
            }
        }
        if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceURI)) {
            if ("li".equals(localName)) {
                return 9;
            }
            if ("parseType".equals(localName)) {
                return 4;
            }
            if ("Description".equals(localName)) {
                return 8;
            }
            if ("about".equals(localName)) {
                return 3;
            }
            if ("resource".equals(localName)) {
                return 5;
            }
            if ("RDF".equals(localName)) {
                return 1;
            }
            if ("ID".equals(localName)) {
                return 2;
            }
            if ("nodeID".equals(localName)) {
                return 6;
            }
            if ("datatype".equals(localName)) {
                return 7;
            }
            if ("aboutEach".equals(localName)) {
                return 10;
            }
            if ("aboutEachPrefix".equals(localName)) {
                return 11;
            }
            if ("bagID".equals(localName)) {
                return 12;
            }
        }
        return 0;
    }
    
    private static boolean isCoreSyntaxTerm(final int n) {
        final int n2 = 1;
        return n2 <= n && n <= 7 && n2;
    }
    
    private static boolean isOldTerm(final int n) {
        return 10 <= n && n <= 12;
    }
    
    private static boolean isPropertyElementName(final int n) {
        boolean b = false;
        if (n != 8 && !isOldTerm(n)) {
            if (!isCoreSyntaxTerm(n)) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    private static boolean isWhitespaceNode(final Node node) {
        if (node.getNodeType() == 3) {
            final String nodeValue = node.getNodeValue();
            for (int i = 0; i < nodeValue.length(); ++i) {
                if (!Character.isWhitespace(nodeValue.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    static XMPMetaImpl parse(final Node node) {
        final XMPMetaImpl xmpMetaImpl = new XMPMetaImpl();
        rdf_RDF(xmpMetaImpl, node);
        return xmpMetaImpl;
    }
    
    private static void rdf_EmptyPropertyElement(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node, final boolean b) {
        int n = 0;
        float n2 = 0.0f;
        int n3 = 0;
        float n4 = 0.0f;
        int n5 = 0;
        float n6 = 0.0f;
        int n7 = 0;
        float n8 = 0.0f;
        if (!node.hasChildNodes()) {
            int i = 0;
            Node node2 = null;
            while (i < node.getAttributes().getLength()) {
                final Node item = node.getAttributes().item(i);
                int n9 = 0;
                float n10 = 0.0f;
                int n11 = 0;
                float n12 = 0.0f;
                int n13 = 0;
                float n14 = 0.0f;
                int n15 = 0;
                float n16 = 0.0f;
                if ("xmlns".equals(item.getPrefix())) {
                    n9 = n7;
                    n10 = n8;
                    n11 = n5;
                    n12 = n6;
                    n13 = n3;
                    n14 = n4;
                    n15 = n;
                    n16 = n2;
                }
                else if (item.getPrefix() == null && "xmlns".equals(item.getNodeName())) {
                    n9 = n7;
                    n10 = n8;
                    n11 = n5;
                    n12 = n6;
                    n13 = n3;
                    n14 = n4;
                    n15 = n;
                    n16 = n2;
                }
                else {
                    switch (getRDFTermKind(item)) {
                        default: {
                            throw new XMPException("Unrecognized attribute of empty property element", 202);
                        }
                        case 2: {
                            n9 = n7;
                            n10 = n8;
                            n11 = n5;
                            n12 = n6;
                            n13 = n3;
                            n14 = n4;
                            n15 = n;
                            n16 = n2;
                            break;
                        }
                        case 5: {
                            if (n5 != 0) {
                                throw new XMPException("Empty property element can't have both rdf:resource and rdf:nodeID", 202);
                            }
                            if (n7 != 0) {
                                throw new XMPException("Empty property element can't have both rdf:value and rdf:resource", 203);
                            }
                            final boolean b2 = true;
                            final float n17 = Float.MIN_VALUE;
                            if (n7 != 0) {
                                n9 = n7;
                                n10 = n8;
                                n11 = n5;
                                n12 = n6;
                                n13 = (b2 ? 1 : 0);
                                n14 = n17;
                                n15 = n;
                                n16 = n2;
                                break;
                            }
                            node2 = item;
                            n9 = n7;
                            n10 = n8;
                            n11 = n5;
                            n12 = n6;
                            n13 = (b2 ? 1 : 0);
                            n14 = n17;
                            n15 = n;
                            n16 = n2;
                            break;
                        }
                        case 6: {
                            if (n3 == 0) {
                                n13 = n3;
                                n14 = n4;
                                n15 = n;
                                n16 = n2;
                                final int n18 = n7;
                                final float n19 = n8;
                                n11 = 1;
                                n12 = Float.MIN_VALUE;
                                n9 = n18;
                                n10 = n19;
                                break;
                            }
                            throw new XMPException("Empty property element can't have both rdf:resource and rdf:nodeID", 202);
                        }
                        case 0: {
                            if ("value".equals(item.getLocalName()) && "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(item.getNamespaceURI())) {
                                if (n3 == 0) {
                                    final boolean b3 = true;
                                    final float n20 = Float.MIN_VALUE;
                                    n11 = n5;
                                    n12 = n6;
                                    n13 = n3;
                                    n14 = n4;
                                    n15 = n;
                                    n16 = n2;
                                    node2 = item;
                                    n9 = (b3 ? 1 : 0);
                                    n10 = n20;
                                    break;
                                }
                                throw new XMPException("Empty property element can't have both rdf:value and rdf:resource", 203);
                            }
                            else {
                                if ("xml:lang".equals(item.getNodeName())) {
                                    n9 = n7;
                                    n10 = n8;
                                    n11 = n5;
                                    n12 = n6;
                                    n13 = n3;
                                    n14 = n4;
                                    n15 = n;
                                    n16 = n2;
                                    break;
                                }
                                final int n21 = n7;
                                final float n22 = n8;
                                n11 = n5;
                                n12 = n6;
                                n13 = n3;
                                n14 = n4;
                                n15 = 1;
                                n16 = Float.MIN_VALUE;
                                n9 = n21;
                                n10 = n22;
                                break;
                            }
                            break;
                        }
                    }
                }
                ++i;
                n = n15;
                n2 = n16;
                n3 = n13;
                n4 = n14;
                n5 = n11;
                n6 = n12;
                n7 = n9;
                n8 = n10;
            }
            final XMPNode addChildNode = addChildNode(xmpMetaImpl, xmpNode, node, "", b);
            boolean b4;
            if (n7 == 0 && n3 == 0) {
                if (n == 0) {
                    b4 = false;
                }
                else {
                    addChildNode.getOptions().setStruct(true);
                    b4 = true;
                }
            }
            else {
                String nodeValue;
                if (node2 == null) {
                    nodeValue = "";
                }
                else {
                    nodeValue = node2.getNodeValue();
                }
                addChildNode.setValue(nodeValue);
                if (n7 != 0) {
                    b4 = false;
                }
                else {
                    addChildNode.getOptions().setURI(true);
                    b4 = false;
                }
            }
            for (int j = 0; j < node.getAttributes().getLength(); ++j) {
                final Node item2 = node.getAttributes().item(j);
                if (item2 != node2 && !"xmlns".equals(item2.getPrefix()) && (item2.getPrefix() != null || "xmlns".equals(item2.getNodeName()))) {
                    switch (getRDFTermKind(item2)) {
                        case 5: {
                            addQualifierNode(addChildNode, "rdf:resource", item2.getNodeValue());
                        }
                        case 2:
                        case 6: {
                            break;
                        }
                        default: {
                            throw new XMPException("Unrecognized attribute of empty property element", 202);
                        }
                        case 0: {
                            if (!b4) {
                                addQualifierNode(addChildNode, item2.getNodeName(), item2.getNodeValue());
                                break;
                            }
                            if (!"xml:lang".equals(item2.getNodeName())) {
                                addChildNode(xmpMetaImpl, addChildNode, item2, item2.getNodeValue(), false);
                                break;
                            }
                            addQualifierNode(addChildNode, "xml:lang", item2.getNodeValue());
                            break;
                        }
                    }
                }
            }
            return;
        }
        throw new XMPException("Nested content not allowed with rdf:resource or property attributes", 202);
    }
    
    private static void rdf_LiteralPropertyElement(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node, final boolean b) {
        final int n = 202;
        int i = 0;
        final XMPNode addChildNode = addChildNode(xmpMetaImpl, xmpNode, node, null, b);
        for (int j = 0; j < node.getAttributes().getLength(); ++j) {
            final Node item = node.getAttributes().item(j);
            if (!"xmlns".equals(item.getPrefix()) && (item.getPrefix() != null || "xmlns".equals(item.getNodeName()))) {
                final String namespaceURI = item.getNamespaceURI();
                final String localName = item.getLocalName();
                if (!"xml:lang".equals(item.getNodeName())) {
                    if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceURI) || (!"ID".equals(localName) && !"datatype".equals(localName))) {
                        throw new XMPException("Invalid attribute for literal property element", n);
                    }
                }
                else {
                    addQualifierNode(addChildNode, "xml:lang", item.getNodeValue());
                }
            }
        }
        String string = "";
        while (i < node.getChildNodes().getLength()) {
            final Node item2 = node.getChildNodes().item(i);
            if (item2.getNodeType() != 3) {
                throw new XMPException("Invalid child of literal property element", n);
            }
            string = String.valueOf(string) + item2.getNodeValue();
            ++i;
        }
        addChildNode.setValue(string);
    }
    
    private static void rdf_NodeElement(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node, final boolean b) {
        final int rdfTermKind = getRDFTermKind(node);
        if (rdfTermKind != 8 && rdfTermKind != 0) {
            throw new XMPException("Node element must be rdf:Description or typed node", 202);
        }
        if (b && rdfTermKind == 0) {
            throw new XMPException("Top level typed node not allowed", 203);
        }
        rdf_NodeElementAttrs(xmpMetaImpl, xmpNode, node, b);
        rdf_PropertyElementList(xmpMetaImpl, xmpNode, node, b);
    }
    
    private static void rdf_NodeElementAttrs(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node, final boolean b) {
        final int n = 202;
        int n2 = 0;
        for (int i = 0; i < node.getAttributes().getLength(); ++i) {
            final Node item = node.getAttributes().item(i);
            if (!"xmlns".equals(item.getPrefix()) && (item.getPrefix() != null || "xmlns".equals(item.getNodeName()))) {
                final int rdfTermKind = getRDFTermKind(item);
                switch (rdfTermKind) {
                    default: {
                        throw new XMPException("Invalid nodeElement attribute", n);
                    }
                    case 2:
                    case 3:
                    case 6: {
                        if (n2 > 0) {
                            throw new XMPException("Mutally exclusive about, ID, nodeID attributes", n);
                        }
                        ++n2;
                        if (!b || rdfTermKind != 3) {
                            break;
                        }
                        if (xmpNode.getName() == null || xmpNode.getName().length() <= 0) {
                            xmpNode.setName(item.getNodeValue());
                            break;
                        }
                        if (!xmpNode.getName().equals(item.getNodeValue())) {
                            throw new XMPException("Mismatched top level rdf:about values", 203);
                        }
                        break;
                    }
                    case 0: {
                        addChildNode(xmpMetaImpl, xmpNode, item, item.getNodeValue(), b);
                        break;
                    }
                }
            }
        }
    }
    
    private static void rdf_NodeElementList(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node) {
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            final Node item = node.getChildNodes().item(i);
            if (!isWhitespaceNode(item)) {
                rdf_NodeElement(xmpMetaImpl, xmpNode, item, true);
            }
        }
    }
    
    private static void rdf_ParseTypeCollectionPropertyElement() {
        throw new XMPException("ParseTypeCollection property element not allowed", 203);
    }
    
    private static void rdf_ParseTypeLiteralPropertyElement() {
        throw new XMPException("ParseTypeLiteral property element not allowed", 203);
    }
    
    private static void rdf_ParseTypeOtherPropertyElement() {
        throw new XMPException("ParseTypeOther property element not allowed", 203);
    }
    
    private static void rdf_ParseTypeResourcePropertyElement(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node, final boolean b) {
        final XMPNode addChildNode = addChildNode(xmpMetaImpl, xmpNode, node, "", b);
        addChildNode.getOptions().setStruct(true);
        for (int i = 0; i < node.getAttributes().getLength(); ++i) {
            final Node item = node.getAttributes().item(i);
            if (!"xmlns".equals(item.getPrefix()) && (item.getPrefix() != null || "xmlns".equals(item.getNodeName()))) {
                final String localName = item.getLocalName();
                final String namespaceURI = item.getNamespaceURI();
                if (!"xml:lang".equals(item.getNodeName())) {
                    if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceURI) || (!"ID".equals(localName) && !"parseType".equals(localName))) {
                        throw new XMPException("Invalid attribute for ParseTypeResource property element", 202);
                    }
                }
                else {
                    addQualifierNode(addChildNode, "xml:lang", item.getNodeValue());
                }
            }
        }
        rdf_PropertyElementList(xmpMetaImpl, addChildNode, node, false);
        if (addChildNode.getHasValueChild()) {
            fixupQualifiedNode(addChildNode);
        }
    }
    
    private static void rdf_PropertyElement(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node, final boolean b) {
        final short n = 3;
        List<String> list = null;
        int i = 0;
        if (isPropertyElementName(getRDFTermKind(node))) {
            final NamedNodeMap attributes = node.getAttributes();
            for (int j = 0; j < attributes.getLength(); ++j) {
                final Node item = attributes.item(j);
                if (!"xmlns".equals(item.getPrefix())) {
                    if (item.getPrefix() != null) {
                        continue;
                    }
                    if (!"xmlns".equals(item.getNodeName())) {
                        continue;
                    }
                }
                if (list == null) {
                    list = new ArrayList<String>();
                }
                list.add(item.getNodeName());
            }
            if (list != null) {
                final Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()) {
                    attributes.removeNamedItem(iterator.next());
                }
            }
            if (attributes.getLength() <= n) {
                for (int k = 0; k < attributes.getLength(); ++k) {
                    final Node item2 = attributes.item(k);
                    final String localName = item2.getLocalName();
                    final String namespaceURI = item2.getNamespaceURI();
                    final String nodeValue = item2.getNodeValue();
                    if (!"xml:lang".equals(item2.getNodeName()) || (!"ID".equals(localName) || !"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceURI))) {
                        if ("datatype".equals(localName) && "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceURI)) {
                            rdf_LiteralPropertyElement(xmpMetaImpl, xmpNode, node, b);
                        }
                        else if ("parseType".equals(localName) && "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceURI)) {
                            if (!"Literal".equals(nodeValue)) {
                                if (!"Resource".equals(nodeValue)) {
                                    if (!"Collection".equals(nodeValue)) {
                                        rdf_ParseTypeOtherPropertyElement();
                                    }
                                    else {
                                        rdf_ParseTypeCollectionPropertyElement();
                                    }
                                }
                                else {
                                    rdf_ParseTypeResourcePropertyElement(xmpMetaImpl, xmpNode, node, b);
                                }
                            }
                            else {
                                rdf_ParseTypeLiteralPropertyElement();
                            }
                        }
                        else {
                            rdf_EmptyPropertyElement(xmpMetaImpl, xmpNode, node, b);
                        }
                        return;
                    }
                }
                if (!node.hasChildNodes()) {
                    rdf_EmptyPropertyElement(xmpMetaImpl, xmpNode, node, b);
                }
                else {
                    while (i < node.getChildNodes().getLength()) {
                        if (node.getChildNodes().item(i).getNodeType() != n) {
                            rdf_ResourcePropertyElement(xmpMetaImpl, xmpNode, node, b);
                            return;
                        }
                        ++i;
                    }
                    rdf_LiteralPropertyElement(xmpMetaImpl, xmpNode, node, b);
                }
            }
            else {
                rdf_EmptyPropertyElement(xmpMetaImpl, xmpNode, node, b);
            }
            return;
        }
        throw new XMPException("Invalid property element name", 202);
    }
    
    private static void rdf_PropertyElementList(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node, final boolean b) {
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            final Node item = node.getChildNodes().item(i);
            if (!isWhitespaceNode(item)) {
                if (item.getNodeType() != 1) {
                    throw new XMPException("Expected property element node not found", 202);
                }
                rdf_PropertyElement(xmpMetaImpl, xmpNode, item, b);
            }
        }
    }
    
    static void rdf_RDF(final XMPMetaImpl xmpMetaImpl, final Node node) {
        if (!node.hasAttributes()) {
            throw new XMPException("Invalid attributes of rdf:RDF element", 202);
        }
        rdf_NodeElementList(xmpMetaImpl, xmpMetaImpl.getRoot(), node);
    }
    
    private static void rdf_ResourcePropertyElement(final XMPMetaImpl xmpMetaImpl, final XMPNode xmpNode, final Node node, final boolean b) {
        final int n = 202;
        final boolean struct = true;
        if (b && "iX:changes".equals(node.getNodeName())) {
            return;
        }
        final XMPNode addChildNode = addChildNode(xmpMetaImpl, xmpNode, node, "", b);
        for (int i = 0; i < node.getAttributes().getLength(); ++i) {
            final Node item = node.getAttributes().item(i);
            if (!"xmlns".equals(item.getPrefix()) && (item.getPrefix() != null || "xmlns".equals(item.getNodeName()))) {
                final String localName = item.getLocalName();
                final String namespaceURI = item.getNamespaceURI();
                if (!"xml:lang".equals(item.getNodeName())) {
                    if (!"ID".equals(localName) || !"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceURI)) {
                        throw new XMPException("Invalid attribute for resource property element", n);
                    }
                }
                else {
                    addQualifierNode(addChildNode, "xml:lang", item.getNodeValue());
                }
            }
        }
        int j = 0;
        boolean b2 = false;
        while (j < node.getChildNodes().getLength()) {
            final Node item2 = node.getChildNodes().item(j);
            if (!isWhitespaceNode(item2)) {
                if (item2.getNodeType() == (struct ? 1 : 0) && !b2) {
                    final boolean equals = "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(item2.getNamespaceURI());
                    final String localName2 = item2.getLocalName();
                    if (equals && "Bag".equals(localName2)) {
                        addChildNode.getOptions().setArray(struct);
                    }
                    else if (equals && "Seq".equals(localName2)) {
                        addChildNode.getOptions().setArray(struct).setArrayOrdered(struct);
                    }
                    else if (equals && "Alt".equals(localName2)) {
                        addChildNode.getOptions().setArray(struct).setArrayOrdered(struct).setArrayAlternate(struct);
                    }
                    else {
                        addChildNode.getOptions().setStruct(struct);
                        if (!equals && !"Description".equals(localName2)) {
                            final String namespaceURI2 = item2.getNamespaceURI();
                            if (namespaceURI2 == null) {
                                throw new XMPException("All XML elements must be in a namespace", 203);
                            }
                            addQualifierNode(addChildNode, "rdf:type", String.valueOf(namespaceURI2) + ':' + localName2);
                        }
                    }
                    rdf_NodeElement(xmpMetaImpl, addChildNode, item2, false);
                    if (!addChildNode.getHasValueChild()) {
                        if (addChildNode.getOptions().isArrayAlternate()) {
                            XMPNodeUtils.detectAltText(addChildNode);
                        }
                    }
                    else {
                        fixupQualifiedNode(addChildNode);
                    }
                    b2 = struct;
                }
                else {
                    if (!b2) {
                        throw new XMPException("Children of resource property element must be XML elements", n);
                    }
                    throw new XMPException("Invalid child of resource property element", n);
                }
            }
            ++j;
        }
        if (b2) {
            return;
        }
        throw new XMPException("Missing child of resource property element", n);
    }
}
