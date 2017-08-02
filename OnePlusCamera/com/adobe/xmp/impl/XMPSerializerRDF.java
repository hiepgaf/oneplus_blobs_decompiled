// 
// Decompiled by Procyon v0.5.30
// 

package com.adobe.xmp.impl;

import java.io.IOException;
import java.io.OutputStream;
import com.adobe.xmp.XMPMeta;
import java.util.Iterator;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.io.OutputStreamWriter;
import com.adobe.xmp.options.SerializeOptions;
import java.util.Set;

public class XMPSerializerRDF
{
    private static final int DEFAULT_PAD = 2048;
    private static final String PACKET_HEADER = "<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>";
    private static final String PACKET_TRAILER = "<?xpacket end=\"";
    private static final String PACKET_TRAILER2 = "\"?>";
    static final Set RDF_ATTR_QUALIFIER;
    private static final String RDF_RDF_END = "</rdf:RDF>";
    private static final String RDF_RDF_START = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">";
    private static final String RDF_SCHEMA_END = "</rdf:Description>";
    private static final String RDF_SCHEMA_START = "<rdf:Description rdf:about=";
    private static final String RDF_STRUCT_END = "</rdf:Description>";
    private static final String RDF_STRUCT_START = "<rdf:Description";
    private static final String RDF_XMPMETA_END = "</x:xmpmeta>";
    private static final String RDF_XMPMETA_START = "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"";
    private SerializeOptions options;
    private CountOutputStream outputStream;
    private int padding;
    private int unicodeSize;
    private OutputStreamWriter writer;
    private XMPMetaImpl xmp;
    
    static {
        RDF_ATTR_QUALIFIER = new HashSet(Arrays.asList("xml:lang", "rdf:resource", "rdf:ID", "rdf:bagID", "rdf:nodeID"));
    }
    
    public XMPSerializerRDF() {
        this.unicodeSize = 1;
    }
    
    private void addPadding(final int n) {
        final int n2 = 100;
        final char c = ' ';
        if (this.options.getExactPacketLength()) {
            final int n3 = this.outputStream.getBytesWritten() + this.unicodeSize * n;
            if (n3 > this.padding) {
                throw new XMPException("Can't fit into specified packet size", 107);
            }
            this.padding -= n3;
        }
        this.padding /= this.unicodeSize;
        final int length = this.options.getNewline().length();
        if (this.padding < length) {
            this.writeChars(this.padding, c);
        }
        else {
            this.padding -= length;
            while (this.padding >= length + 100) {
                this.writeChars(n2, c);
                this.writeNewline();
                this.padding -= length + 100;
            }
            this.writeChars(this.padding, c);
            this.writeNewline();
        }
    }
    
    private void appendNodeValue(final String s, final boolean b) {
        this.write(Utils.escapeXML(s, b, true));
    }
    
    private boolean canBeRDFAttrProp(final XMPNode xmpNode) {
        return !xmpNode.hasQualifier() && !xmpNode.getOptions().isURI() && !xmpNode.getOptions().isCompositeProperty() && !"[]".equals(xmpNode.getName());
    }
    
    private void declareNamespace(String prefix, String namespaceURI, final Set set, final int n) {
        if (namespaceURI == null) {
            final QName qName = new QName(prefix);
            if (!qName.hasPrefix()) {
                return;
            }
            prefix = qName.getPrefix();
            namespaceURI = XMPMetaFactory.getSchemaRegistry().getNamespaceURI(String.valueOf(prefix) + ":");
            this.declareNamespace(prefix, namespaceURI, set, n);
        }
        if (!set.contains(prefix)) {
            this.writeNewline();
            this.writeIndent(n);
            this.write("xmlns:");
            this.write(prefix);
            this.write("=\"");
            this.write(namespaceURI);
            this.write(34);
            set.add(prefix);
        }
    }
    
    private void declareUsedNamespaces(final XMPNode xmpNode, final Set set, final int n) {
        if (!xmpNode.getOptions().isSchemaNode()) {
            if (xmpNode.getOptions().isStruct()) {
                final Iterator iterateChildren = xmpNode.iterateChildren();
                while (iterateChildren.hasNext()) {
                    this.declareNamespace(iterateChildren.next().getName(), null, set, n);
                }
            }
        }
        else {
            this.declareNamespace(xmpNode.getValue().substring(0, xmpNode.getValue().length() - 1), xmpNode.getName(), set, n);
        }
        final Iterator iterateChildren2 = xmpNode.iterateChildren();
        while (iterateChildren2.hasNext()) {
            this.declareUsedNamespaces(iterateChildren2.next(), set, n);
        }
        final Iterator iterateQualifier = xmpNode.iterateQualifier();
        while (iterateQualifier.hasNext()) {
            final XMPNode xmpNode2 = iterateQualifier.next();
            this.declareNamespace(xmpNode2.getName(), null, set, n);
            this.declareUsedNamespaces(xmpNode2, set, n);
        }
    }
    
    private void emitRDFArrayTag(final XMPNode xmpNode, final boolean b, final int n) {
        if (b || xmpNode.hasChildren()) {
            this.writeIndent(n);
            String s;
            if (!b) {
                s = "</rdf:";
            }
            else {
                s = "<rdf:";
            }
            this.write(s);
            if (!xmpNode.getOptions().isArrayAlternate()) {
                if (!xmpNode.getOptions().isArrayOrdered()) {
                    this.write("Bag");
                }
                else {
                    this.write("Seq");
                }
            }
            else {
                this.write("Alt");
            }
            if (b && !xmpNode.hasChildren()) {
                this.write("/>");
            }
            else {
                this.write(">");
            }
            this.writeNewline();
        }
    }
    
    private String serializeAsRDF() {
        final int n = 1;
        if (!this.options.getOmitPacketWrapper()) {
            this.writeIndent(0);
            this.write("<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>");
            this.writeNewline();
        }
        this.writeIndent(0);
        this.write("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"");
        if (!this.options.getOmitVersionAttribute()) {
            this.write(XMPMetaFactory.getVersionInfo().getMessage());
        }
        this.write("\">");
        this.writeNewline();
        this.writeIndent(n);
        this.write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">");
        this.writeNewline();
        if (!this.options.getUseCompactFormat()) {
            this.serializePrettyRDFSchemas();
        }
        else {
            this.serializeCompactRDFSchemas();
        }
        this.writeIndent(n);
        this.write("</rdf:RDF>");
        this.writeNewline();
        this.writeIndent(0);
        this.write("</x:xmpmeta>");
        this.writeNewline();
        String string = "";
        if (!this.options.getOmitPacketWrapper()) {
            final int baseIndent = this.options.getBaseIndent();
            String string2 = string;
            for (int i = baseIndent; i > 0; --i) {
                string2 = String.valueOf(string2) + this.options.getIndent();
            }
            final StringBuilder sb = new StringBuilder(String.valueOf(String.valueOf(string2) + "<?xpacket end=\""));
            char c;
            if (!this.options.getReadOnlyPacket()) {
                c = 'w';
            }
            else {
                c = 'r';
            }
            string = String.valueOf(sb.append(c).toString()) + "\"?>";
        }
        return string;
    }
    
    private void serializeCompactRDFArrayProp(final XMPNode xmpNode, final int n) {
        this.write(62);
        this.writeNewline();
        this.emitRDFArrayTag(xmpNode, true, n + 1);
        if (xmpNode.getOptions().isArrayAltText()) {
            XMPNodeUtils.normalizeLangArray(xmpNode);
        }
        this.serializeCompactRDFElementProps(xmpNode, n + 2);
        this.emitRDFArrayTag(xmpNode, false, n + 1);
    }
    
    private boolean serializeCompactRDFAttrProps(final XMPNode xmpNode, final int n) {
        final boolean b = true;
        final Iterator iterateChildren = xmpNode.iterateChildren();
        boolean b2 = b;
        while (iterateChildren.hasNext()) {
            final XMPNode xmpNode2 = iterateChildren.next();
            if (!this.canBeRDFAttrProp(xmpNode2)) {
                b2 = false;
            }
            else {
                this.writeNewline();
                this.writeIndent(n);
                this.write(xmpNode2.getName());
                this.write("=\"");
                this.appendNodeValue(xmpNode2.getValue(), b);
                this.write(34);
            }
        }
        return b2;
    }
    
    private void serializeCompactRDFElementProps(final XMPNode xmpNode, final int n) {
        final int n2 = 1;
        final Iterator iterateChildren = xmpNode.iterateChildren();
        while (iterateChildren.hasNext()) {
            final XMPNode xmpNode2 = iterateChildren.next();
            if (!this.canBeRDFAttrProp(xmpNode2)) {
                final String name = xmpNode2.getName();
                String s;
                if (!"[]".equals(name)) {
                    s = name;
                }
                else {
                    s = "rdf:li";
                }
                this.writeIndent(n);
                this.write(60);
                this.write(s);
                final Iterator iterateQualifier = xmpNode2.iterateQualifier();
                boolean equals = false;
                boolean b = false;
                while (iterateQualifier.hasNext()) {
                    final XMPNode xmpNode3 = iterateQualifier.next();
                    if (XMPSerializerRDF.RDF_ATTR_QUALIFIER.contains(xmpNode3.getName())) {
                        equals = "rdf:resource".equals(xmpNode3.getName());
                        this.write(32);
                        this.write(xmpNode3.getName());
                        this.write("=\"");
                        this.appendNodeValue(xmpNode3.getValue(), n2 != 0);
                        this.write(34);
                    }
                    else {
                        b = (n2 != 0);
                    }
                }
                int n3;
                int booleanValue;
                if (!b) {
                    if (xmpNode2.getOptions().isCompositeProperty()) {
                        if (!xmpNode2.getOptions().isArray()) {
                            n3 = (this.serializeCompactRDFStructProp(xmpNode2, n, equals) ? 1 : 0);
                            booleanValue = n2;
                        }
                        else {
                            this.serializeCompactRDFArrayProp(xmpNode2, n);
                            booleanValue = n2;
                            n3 = n2;
                        }
                    }
                    else {
                        final Object[] serializeCompactRDFSimpleProp = this.serializeCompactRDFSimpleProp(xmpNode2);
                        n3 = (((boolean)serializeCompactRDFSimpleProp[0]) ? 1 : 0);
                        booleanValue = (((boolean)serializeCompactRDFSimpleProp[n2]) ? 1 : 0);
                    }
                }
                else {
                    this.serializeCompactRDFGeneralQualifier(n, xmpNode2);
                    booleanValue = n2;
                    n3 = n2;
                }
                if (n3 == 0) {
                    continue;
                }
                if (booleanValue != 0) {
                    this.writeIndent(n);
                }
                this.write("</");
                this.write(s);
                this.write(62);
                this.writeNewline();
            }
        }
    }
    
    private void serializeCompactRDFGeneralQualifier(final int n, final XMPNode xmpNode) {
        this.write(" rdf:parseType=\"Resource\">");
        this.writeNewline();
        this.serializePrettyRDFProperty(xmpNode, true, n + 1);
        final Iterator iterateQualifier = xmpNode.iterateQualifier();
        while (iterateQualifier.hasNext()) {
            this.serializePrettyRDFProperty(iterateQualifier.next(), false, n + 1);
        }
    }
    
    private void serializeCompactRDFSchemas() {
        final int n = 3;
        final int n2 = 2;
        this.writeIndent(n2);
        this.write("<rdf:Description rdf:about=");
        this.writeTreeName();
        final HashSet<String> set = new HashSet<String>();
        set.add("xml");
        set.add("rdf");
        final Iterator iterateChildren = this.xmp.getRoot().iterateChildren();
        while (iterateChildren.hasNext()) {
            this.declareUsedNamespaces(iterateChildren.next(), set, 4);
        }
        final boolean b = true;
        final Iterator iterateChildren2 = this.xmp.getRoot().iterateChildren();
        boolean b2 = b;
        while (iterateChildren2.hasNext()) {
            b2 &= this.serializeCompactRDFAttrProps(iterateChildren2.next(), n);
        }
        if (b2) {
            this.write("/>");
            this.writeNewline();
            return;
        }
        this.write(62);
        this.writeNewline();
        final Iterator iterateChildren3 = this.xmp.getRoot().iterateChildren();
        while (iterateChildren3.hasNext()) {
            this.serializeCompactRDFElementProps(iterateChildren3.next(), n);
        }
        this.writeIndent(n2);
        this.write("</rdf:Description>");
        this.writeNewline();
    }
    
    private Object[] serializeCompactRDFSimpleProp(final XMPNode xmpNode) {
        final int n = 1;
        Boolean b = Boolean.TRUE;
        Boolean b2 = Boolean.TRUE;
        if (!xmpNode.getOptions().isURI()) {
            if (xmpNode.getValue() != null && xmpNode.getValue().length() != 0) {
                this.write(62);
                this.appendNodeValue(xmpNode.getValue(), false);
                b2 = Boolean.FALSE;
            }
            else {
                this.write("/>");
                this.writeNewline();
                b = Boolean.FALSE;
            }
        }
        else {
            this.write(" rdf:resource=\"");
            this.appendNodeValue(xmpNode.getValue(), n != 0);
            this.write("\"/>");
            this.writeNewline();
            b = Boolean.FALSE;
        }
        final Object[] array = { b, null };
        array[n] = b2;
        return array;
    }
    
    private boolean serializeCompactRDFStructProp(final XMPNode xmpNode, final int n, final boolean b) {
        final boolean b2 = true;
        final float n2 = Float.MIN_VALUE;
        boolean b3 = false;
        final Iterator iterateChildren = xmpNode.iterateChildren();
        int n3 = 0;
        float n4 = 0.0f;
        int n5 = 0;
        float n6 = 0.0f;
        while (true) {
            while (iterateChildren.hasNext()) {
                int n7;
                float n8;
                int n9;
                float n10;
                if (!this.canBeRDFAttrProp(iterateChildren.next())) {
                    n7 = (b2 ? 1 : 0);
                    n8 = n2;
                    n9 = n5;
                    n10 = n6;
                }
                else {
                    n7 = n3;
                    n8 = n4;
                    n9 = (b2 ? 1 : 0);
                    n10 = n2;
                }
                if (n9 != 0 && n7 != 0) {
                    if (b && n7 != 0) {
                        throw new XMPException("Can't mix rdf:resource qualifier and element fields", 202);
                    }
                    if (xmpNode.hasChildren()) {
                        if (n7 != 0) {
                            if (n9 != 0) {
                                this.write(62);
                                this.writeNewline();
                                this.writeIndent(n + 1);
                                this.write("<rdf:Description");
                                this.serializeCompactRDFAttrProps(xmpNode, n + 2);
                                this.write(">");
                                this.writeNewline();
                                this.serializeCompactRDFElementProps(xmpNode, n + 1);
                                this.writeIndent(n + 1);
                                this.write("</rdf:Description>");
                                this.writeNewline();
                                b3 = b2;
                            }
                            else {
                                this.write(" rdf:parseType=\"Resource\">");
                                this.writeNewline();
                                this.serializeCompactRDFElementProps(xmpNode, n + 1);
                                b3 = b2;
                            }
                        }
                        else {
                            this.serializeCompactRDFAttrProps(xmpNode, n + 1);
                            this.write("/>");
                            this.writeNewline();
                        }
                    }
                    else {
                        this.write(" rdf:parseType=\"Resource\"/>");
                        this.writeNewline();
                    }
                    return b3;
                }
                else {
                    n5 = n9;
                    n6 = n10;
                    n3 = n7;
                    n4 = n8;
                }
            }
            int n7 = n3;
            int n9 = n5;
            continue;
        }
    }
    
    private void serializePrettyRDFProperty(final XMPNode xmpNode, final boolean b, final int n) {
        final boolean b2 = true;
        final boolean b3 = true;
        final String name = xmpNode.getName();
        String s;
        if (!b) {
            if (!"[]".equals(name)) {
                s = name;
            }
            else {
                s = "rdf:li";
            }
        }
        else {
            s = "rdf:value";
        }
        this.writeIndent(n);
        this.write(60);
        this.write(s);
        final Iterator iterateQualifier = xmpNode.iterateQualifier();
        boolean b4 = false;
        boolean equals = false;
        while (iterateQualifier.hasNext()) {
            final XMPNode xmpNode2 = iterateQualifier.next();
            if (XMPSerializerRDF.RDF_ATTR_QUALIFIER.contains(xmpNode2.getName())) {
                equals = "rdf:resource".equals(xmpNode2.getName());
                if (b) {
                    continue;
                }
                this.write(32);
                this.write(xmpNode2.getName());
                this.write("=\"");
                this.appendNodeValue(xmpNode2.getValue(), true);
                this.write(34);
            }
            else {
                b4 = true;
            }
        }
        boolean b5;
        int n2;
        if (b4 && !b) {
            if (equals) {
                throw new XMPException("Can't mix rdf:resource and general qualifiers", 202);
            }
            this.write(" rdf:parseType=\"Resource\">");
            this.writeNewline();
            this.serializePrettyRDFProperty(xmpNode, true, n + 1);
            final Iterator iterateQualifier2 = xmpNode.iterateQualifier();
            while (iterateQualifier2.hasNext()) {
                final XMPNode xmpNode3 = iterateQualifier2.next();
                if (!XMPSerializerRDF.RDF_ATTR_QUALIFIER.contains(xmpNode3.getName())) {
                    this.serializePrettyRDFProperty(xmpNode3, false, n + 1);
                }
            }
            b5 = b3;
            n2 = (b2 ? 1 : 0);
        }
        else if (xmpNode.getOptions().isCompositeProperty()) {
            if (!xmpNode.getOptions().isArray()) {
                if (equals) {
                    final Iterator iterateChildren = xmpNode.iterateChildren();
                    while (iterateChildren.hasNext()) {
                        final XMPNode xmpNode4 = iterateChildren.next();
                        if (!this.canBeRDFAttrProp(xmpNode4)) {
                            throw new XMPException("Can't mix rdf:resource and complex fields", 202);
                        }
                        this.writeNewline();
                        this.writeIndent(n + 1);
                        this.write(32);
                        this.write(xmpNode4.getName());
                        this.write("=\"");
                        this.appendNodeValue(xmpNode4.getValue(), true);
                        this.write(34);
                    }
                    this.write("/>");
                    this.writeNewline();
                    n2 = 0;
                    b5 = b3;
                }
                else if (xmpNode.hasChildren()) {
                    this.write(" rdf:parseType=\"Resource\">");
                    this.writeNewline();
                    final Iterator iterateChildren2 = xmpNode.iterateChildren();
                    while (iterateChildren2.hasNext()) {
                        this.serializePrettyRDFProperty(iterateChildren2.next(), false, n + 1);
                    }
                    b5 = b3;
                    n2 = (b2 ? 1 : 0);
                }
                else {
                    this.write(" rdf:parseType=\"Resource\"/>");
                    this.writeNewline();
                    n2 = 0;
                    b5 = b3;
                }
            }
            else {
                this.write(62);
                this.writeNewline();
                this.emitRDFArrayTag(xmpNode, true, n + 1);
                if (xmpNode.getOptions().isArrayAltText()) {
                    XMPNodeUtils.normalizeLangArray(xmpNode);
                }
                final Iterator iterateChildren3 = xmpNode.iterateChildren();
                while (iterateChildren3.hasNext()) {
                    this.serializePrettyRDFProperty(iterateChildren3.next(), false, n + 2);
                }
                this.emitRDFArrayTag(xmpNode, false, n + 1);
                b5 = b3;
                n2 = (b2 ? 1 : 0);
            }
        }
        else if (!xmpNode.getOptions().isURI()) {
            if (xmpNode.getValue() != null && !"".equals(xmpNode.getValue())) {
                this.write(62);
                this.appendNodeValue(xmpNode.getValue(), false);
                b5 = false;
                n2 = (b2 ? 1 : 0);
            }
            else {
                this.write("/>");
                this.writeNewline();
                n2 = 0;
                b5 = b3;
            }
        }
        else {
            this.write(" rdf:resource=\"");
            this.appendNodeValue(xmpNode.getValue(), true);
            this.write("\"/>");
            this.writeNewline();
            n2 = 0;
            b5 = b3;
        }
        if (n2 != 0) {
            if (b5) {
                this.writeIndent(n);
            }
            this.write("</");
            this.write(s);
            this.write(62);
            this.writeNewline();
        }
    }
    
    private void serializePrettyRDFSchema(final XMPNode xmpNode) {
        final int n = 2;
        this.writeIndent(n);
        this.write("<rdf:Description rdf:about=");
        this.writeTreeName();
        final HashSet<String> set = new HashSet<String>();
        set.add("xml");
        set.add("rdf");
        this.declareUsedNamespaces(xmpNode, set, 4);
        this.write(62);
        this.writeNewline();
        final Iterator iterateChildren = xmpNode.iterateChildren();
        while (iterateChildren.hasNext()) {
            this.serializePrettyRDFProperty(iterateChildren.next(), false, 3);
        }
        this.writeIndent(n);
        this.write("</rdf:Description>");
        this.writeNewline();
    }
    
    private void serializePrettyRDFSchemas() {
        if (this.xmp.getRoot().getChildrenLength() <= 0) {
            this.writeIndent(2);
            this.write("<rdf:Description rdf:about=");
            this.writeTreeName();
            this.write("/>");
            this.writeNewline();
        }
        else {
            final Iterator iterateChildren = this.xmp.getRoot().iterateChildren();
            while (iterateChildren.hasNext()) {
                this.serializePrettyRDFSchema(iterateChildren.next());
            }
        }
    }
    
    private void write(final int n) {
        this.writer.write(n);
    }
    
    private void write(final String s) {
        this.writer.write(s);
    }
    
    private void writeChars(int i, final char c) {
        while (i > 0) {
            this.writer.write(c);
            --i;
        }
    }
    
    private void writeIndent(final int n) {
        for (int i = this.options.getBaseIndent() + n; i > 0; --i) {
            this.writer.write(this.options.getIndent());
        }
    }
    
    private void writeNewline() {
        this.writer.write(this.options.getNewline());
    }
    
    private void writeTreeName() {
        final int n = 34;
        this.write(n);
        final String name = this.xmp.getRoot().getName();
        if (name != null) {
            this.appendNodeValue(name, true);
        }
        this.write(n);
    }
    
    protected void checkOptionsConsistence() {
        final int n = 103;
        if (this.options.getEncodeUTF16BE() | this.options.getEncodeUTF16LE()) {
            this.unicodeSize = 2;
        }
        if (!this.options.getExactPacketLength()) {
            if (!this.options.getReadOnlyPacket()) {
                if (!this.options.getOmitPacketWrapper()) {
                    if (this.padding == 0) {
                        this.padding = this.unicodeSize * 2048;
                    }
                    if (this.options.getIncludeThumbnailPad() && !this.xmp.doesPropertyExist("http://ns.adobe.com/xap/1.0/", "Thumbnails")) {
                        this.padding += this.unicodeSize * 10000;
                    }
                }
                else {
                    if (this.options.getIncludeThumbnailPad()) {
                        throw new XMPException("Inconsistent options for non-packet serialize", n);
                    }
                    this.padding = 0;
                }
            }
            else {
                if (this.options.getOmitPacketWrapper() | this.options.getIncludeThumbnailPad()) {
                    throw new XMPException("Inconsistent options for read-only packet", n);
                }
                this.padding = 0;
            }
        }
        else {
            if (this.options.getOmitPacketWrapper() | this.options.getIncludeThumbnailPad()) {
                throw new XMPException("Inconsistent options for exact size serialize", n);
            }
            if ((this.options.getPadding() & this.unicodeSize - 1) != 0x0) {
                throw new XMPException("Exact size must be a multiple of the Unicode element", n);
            }
        }
    }

  
  public void serialize(XMPMeta paramXMPMeta, OutputStream paramOutputStream, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    try
    {
      this.outputStream = new CountOutputStream(paramOutputStream);
      this.writer = new OutputStreamWriter(this.outputStream, paramSerializeOptions.getEncoding());
      this.xmp = ((XMPMetaImpl)paramXMPMeta);
      this.options = paramSerializeOptions;
      this.padding = paramSerializeOptions.getPadding();
      this.writer = new OutputStreamWriter(this.outputStream, paramSerializeOptions.getEncoding());
      checkOptionsConsistence();
      paramXMPMeta = serializeAsRDF();
      this.writer.flush();
      addPadding(paramXMPMeta.length());
      write(paramXMPMeta);
      this.writer.flush();
      this.outputStream.close();
      return;
    }
    catch (IOException paramXMPMeta)
    {
      throw new XMPException("Error writing to the OutputStream", 0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPSerializerRDF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */