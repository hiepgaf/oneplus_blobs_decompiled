package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.XMPVersionInfo;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.options.SerializeOptions;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class XMPSerializerRDF
{
  private static final int DEFAULT_PAD = 2048;
  private static final String PACKET_HEADER = "<?xpacket begin=\"﻿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>";
  private static final String PACKET_TRAILER = "<?xpacket end=\"";
  private static final String PACKET_TRAILER2 = "\"?>";
  static final Set RDF_ATTR_QUALIFIER = new HashSet(Arrays.asList(new String[] { "xml:lang", "rdf:resource", "rdf:ID", "rdf:bagID", "rdf:nodeID" }));
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
  private int unicodeSize = 1;
  private OutputStreamWriter writer;
  private XMPMetaImpl xmp;
  
  private void addPadding(int paramInt)
    throws XMPException, IOException
  {
    if (!this.options.getExactPacketLength()) {}
    for (;;)
    {
      this.padding /= this.unicodeSize;
      paramInt = this.options.getNewline().length();
      if (this.padding >= paramInt) {
        break label101;
      }
      writeChars(this.padding, ' ');
      return;
      paramInt = this.outputStream.getBytesWritten() + this.unicodeSize * paramInt;
      if (paramInt > this.padding) {
        break;
      }
      this.padding -= paramInt;
    }
    throw new XMPException("Can't fit into specified packet size", 107);
    label101:
    for (this.padding -= paramInt; this.padding >= paramInt + 100; this.padding -= paramInt + 100)
    {
      writeChars(100, ' ');
      writeNewline();
    }
    writeChars(this.padding, ' ');
    writeNewline();
  }
  
  private void appendNodeValue(String paramString, boolean paramBoolean)
    throws IOException
  {
    write(Utils.escapeXML(paramString, paramBoolean, true));
  }
  
  private boolean canBeRDFAttrProp(XMPNode paramXMPNode)
  {
    if (paramXMPNode.hasQualifier()) {}
    while ((paramXMPNode.getOptions().isURI()) || (paramXMPNode.getOptions().isCompositeProperty()) || ("[]".equals(paramXMPNode.getName()))) {
      return false;
    }
    return true;
  }
  
  private void declareNamespace(String paramString1, String paramString2, Set paramSet, int paramInt)
    throws IOException
  {
    if (paramString2 != null) {}
    while (paramSet.contains(paramString1))
    {
      return;
      paramString1 = new QName(paramString1);
      if (!paramString1.hasPrefix()) {
        return;
      }
      paramString1 = paramString1.getPrefix();
      paramString2 = XMPMetaFactory.getSchemaRegistry().getNamespaceURI(paramString1 + ":");
      declareNamespace(paramString1, paramString2, paramSet, paramInt);
    }
    writeNewline();
    writeIndent(paramInt);
    write("xmlns:");
    write(paramString1);
    write("=\"");
    write(paramString2);
    write(34);
    paramSet.add(paramString1);
  }
  
  private void declareUsedNamespaces(XMPNode paramXMPNode, Set paramSet, int paramInt)
    throws IOException
  {
    if (!paramXMPNode.getOptions().isSchemaNode()) {
      if (paramXMPNode.getOptions().isStruct()) {
        break label85;
      }
    }
    Object localObject;
    for (;;)
    {
      localObject = paramXMPNode.iterateChildren();
      while (((Iterator)localObject).hasNext()) {
        declareUsedNamespaces((XMPNode)((Iterator)localObject).next(), paramSet, paramInt);
      }
      declareNamespace(paramXMPNode.getValue().substring(0, paramXMPNode.getValue().length() - 1), paramXMPNode.getName(), paramSet, paramInt);
      continue;
      label85:
      localObject = paramXMPNode.iterateChildren();
      while (((Iterator)localObject).hasNext()) {
        declareNamespace(((XMPNode)((Iterator)localObject).next()).getName(), null, paramSet, paramInt);
      }
    }
    paramXMPNode = paramXMPNode.iterateQualifier();
    while (paramXMPNode.hasNext())
    {
      localObject = (XMPNode)paramXMPNode.next();
      declareNamespace(((XMPNode)localObject).getName(), null, paramSet, paramInt);
      declareUsedNamespaces((XMPNode)localObject, paramSet, paramInt);
    }
  }
  
  private void emitRDFArrayTag(XMPNode paramXMPNode, boolean paramBoolean, int paramInt)
    throws IOException
  {
    String str;
    if (paramBoolean)
    {
      writeIndent(paramInt);
      if (paramBoolean) {
        break label75;
      }
      str = "</rdf:";
      label18:
      write(str);
      if (paramXMPNode.getOptions().isArrayAlternate()) {
        break label83;
      }
      if (paramXMPNode.getOptions().isArrayOrdered()) {
        break label93;
      }
      write("Bag");
      label51:
      if (paramBoolean) {
        break label103;
      }
      label55:
      write(">");
    }
    for (;;)
    {
      writeNewline();
      return;
      if (paramXMPNode.hasChildren()) {
        break;
      }
      return;
      label75:
      str = "<rdf:";
      break label18;
      label83:
      write("Alt");
      break label51;
      label93:
      write("Seq");
      break label51;
      label103:
      if (paramXMPNode.hasChildren()) {
        break label55;
      }
      write("/>");
    }
  }
  
  private String serializeAsRDF()
    throws IOException, XMPException
  {
    if (this.options.getOmitPacketWrapper())
    {
      writeIndent(0);
      write("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"");
      if (!this.options.getOmitVersionAttribute()) {
        break label133;
      }
      label31:
      write("\">");
      writeNewline();
      writeIndent(1);
      write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">");
      writeNewline();
      if (this.options.getUseCompactFormat()) {
        break label148;
      }
      serializePrettyRDFSchemas();
    }
    for (;;)
    {
      writeIndent(1);
      write("</rdf:RDF>");
      writeNewline();
      writeIndent(0);
      write("</x:xmpmeta>");
      writeNewline();
      if (!this.options.getOmitPacketWrapper()) {
        break label155;
      }
      return "";
      writeIndent(0);
      write("<?xpacket begin=\"﻿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>");
      writeNewline();
      break;
      label133:
      write(XMPMetaFactory.getVersionInfo().getMessage());
      break label31;
      label148:
      serializeCompactRDFSchemas();
    }
    label155:
    int i = this.options.getBaseIndent();
    Object localObject = "";
    while (i > 0)
    {
      localObject = localObject + this.options.getIndent();
      i -= 1;
    }
    localObject = new StringBuilder(String.valueOf(localObject + "<?xpacket end=\""));
    if (!this.options.getReadOnlyPacket()) {}
    for (char c = 'w';; c = 'r') {
      return ((StringBuilder)localObject).append(c).toString() + "\"?>";
    }
  }
  
  private void serializeCompactRDFArrayProp(XMPNode paramXMPNode, int paramInt)
    throws IOException, XMPException
  {
    write(62);
    writeNewline();
    emitRDFArrayTag(paramXMPNode, true, paramInt + 1);
    if (!paramXMPNode.getOptions().isArrayAltText()) {}
    for (;;)
    {
      serializeCompactRDFElementProps(paramXMPNode, paramInt + 2);
      emitRDFArrayTag(paramXMPNode, false, paramInt + 1);
      return;
      XMPNodeUtils.normalizeLangArray(paramXMPNode);
    }
  }
  
  private boolean serializeCompactRDFAttrProps(XMPNode paramXMPNode, int paramInt)
    throws IOException
  {
    paramXMPNode = paramXMPNode.iterateChildren();
    boolean bool = true;
    while (paramXMPNode.hasNext())
    {
      XMPNode localXMPNode = (XMPNode)paramXMPNode.next();
      if (!canBeRDFAttrProp(localXMPNode))
      {
        bool = false;
      }
      else
      {
        writeNewline();
        writeIndent(paramInt);
        write(localXMPNode.getName());
        write("=\"");
        appendNodeValue(localXMPNode.getValue(), true);
        write(34);
      }
    }
    return bool;
  }
  
  private void serializeCompactRDFElementProps(XMPNode paramXMPNode, int paramInt)
    throws IOException, XMPException
  {
    Iterator localIterator1 = paramXMPNode.iterateChildren();
    while (localIterator1.hasNext())
    {
      Object localObject = (XMPNode)localIterator1.next();
      if (!canBeRDFAttrProp((XMPNode)localObject))
      {
        paramXMPNode = ((XMPNode)localObject).getName();
        Iterator localIterator2;
        boolean bool1;
        int i;
        if (!"[]".equals(paramXMPNode))
        {
          writeIndent(paramInt);
          write(60);
          write(paramXMPNode);
          localIterator2 = ((XMPNode)localObject).iterateQualifier();
          bool1 = false;
          i = 0;
        }
        for (;;)
        {
          if (!localIterator2.hasNext()) {
            break label182;
          }
          XMPNode localXMPNode = (XMPNode)localIterator2.next();
          if (RDF_ATTR_QUALIFIER.contains(localXMPNode.getName()))
          {
            bool1 = "rdf:resource".equals(localXMPNode.getName());
            write(32);
            write(localXMPNode.getName());
            write("=\"");
            appendNodeValue(localXMPNode.getValue(), true);
            write(34);
            continue;
            paramXMPNode = "rdf:li";
            break;
          }
          i = 1;
        }
        label182:
        boolean bool2;
        if (i == 0)
        {
          if (!((XMPNode)localObject).getOptions().isCompositeProperty()) {
            break label273;
          }
          if (((XMPNode)localObject).getOptions().isArray()) {
            break label308;
          }
          bool2 = serializeCompactRDFStructProp((XMPNode)localObject, paramInt, bool1);
          bool1 = true;
          label222:
          if (!bool2) {
            break label322;
          }
          if (bool1) {
            break label324;
          }
        }
        for (;;)
        {
          write("</");
          write(paramXMPNode);
          write(62);
          writeNewline();
          break;
          serializeCompactRDFGeneralQualifier(paramInt, (XMPNode)localObject);
          bool1 = true;
          bool2 = true;
          break label222;
          label273:
          localObject = serializeCompactRDFSimpleProp((XMPNode)localObject);
          bool2 = ((Boolean)localObject[0]).booleanValue();
          bool1 = ((Boolean)localObject[1]).booleanValue();
          break label222;
          label308:
          serializeCompactRDFArrayProp((XMPNode)localObject, paramInt);
          bool1 = true;
          bool2 = true;
          break label222;
          label322:
          break;
          label324:
          writeIndent(paramInt);
        }
      }
    }
  }
  
  private void serializeCompactRDFGeneralQualifier(int paramInt, XMPNode paramXMPNode)
    throws IOException, XMPException
  {
    write(" rdf:parseType=\"Resource\">");
    writeNewline();
    serializePrettyRDFProperty(paramXMPNode, true, paramInt + 1);
    paramXMPNode = paramXMPNode.iterateQualifier();
    while (paramXMPNode.hasNext()) {
      serializePrettyRDFProperty((XMPNode)paramXMPNode.next(), false, paramInt + 1);
    }
  }
  
  private void serializeCompactRDFSchemas()
    throws IOException, XMPException
  {
    writeIndent(2);
    write("<rdf:Description rdf:about=");
    writeTreeName();
    Object localObject = new HashSet();
    ((Set)localObject).add("xml");
    ((Set)localObject).add("rdf");
    Iterator localIterator = this.xmp.getRoot().iterateChildren();
    while (localIterator.hasNext()) {
      declareUsedNamespaces((XMPNode)localIterator.next(), (Set)localObject, 4);
    }
    localObject = this.xmp.getRoot().iterateChildren();
    for (boolean bool = true; ((Iterator)localObject).hasNext(); bool = serializeCompactRDFAttrProps((XMPNode)((Iterator)localObject).next(), 3) & bool) {}
    if (bool)
    {
      write("/>");
      writeNewline();
      return;
    }
    write(62);
    writeNewline();
    localObject = this.xmp.getRoot().iterateChildren();
    while (((Iterator)localObject).hasNext()) {
      serializeCompactRDFElementProps((XMPNode)((Iterator)localObject).next(), 3);
    }
    writeIndent(2);
    write("</rdf:Description>");
    writeNewline();
  }
  
  private Object[] serializeCompactRDFSimpleProp(XMPNode paramXMPNode)
    throws IOException
  {
    Boolean localBoolean2 = Boolean.TRUE;
    Boolean localBoolean1 = Boolean.TRUE;
    if (!paramXMPNode.getOptions().isURI())
    {
      if (paramXMPNode.getValue() != null) {
        break label87;
      }
      write("/>");
      writeNewline();
      paramXMPNode = Boolean.FALSE;
    }
    for (;;)
    {
      return new Object[] { paramXMPNode, localBoolean1 };
      write(" rdf:resource=\"");
      appendNodeValue(paramXMPNode.getValue(), true);
      write("\"/>");
      writeNewline();
      paramXMPNode = Boolean.FALSE;
      continue;
      label87:
      if (paramXMPNode.getValue().length() == 0) {
        break;
      }
      write(62);
      appendNodeValue(paramXMPNode.getValue(), false);
      localBoolean1 = Boolean.FALSE;
      paramXMPNode = localBoolean2;
    }
  }
  
  private boolean serializeCompactRDFStructProp(XMPNode paramXMPNode, int paramInt, boolean paramBoolean)
    throws XMPException, IOException
  {
    Iterator localIterator = paramXMPNode.iterateChildren();
    int j = 0;
    int i = 0;
    int k;
    if (localIterator.hasNext())
    {
      if (!canBeRDFAttrProp((XMPNode)localIterator.next()))
      {
        k = 1;
        j = i;
        i = k;
        label50:
        if (j != 0) {
          break label80;
        }
      }
      label80:
      while (i == 0)
      {
        k = j;
        j = i;
        i = k;
        break;
        i = j;
        j = 1;
        break label50;
      }
    }
    for (;;)
    {
      if (!paramBoolean) {}
      while (paramXMPNode.hasChildren())
      {
        if (i == 0) {
          break label208;
        }
        if (j == 0) {
          break label230;
        }
        write(62);
        writeNewline();
        writeIndent(paramInt + 1);
        write("<rdf:Description");
        serializeCompactRDFAttrProps(paramXMPNode, paramInt + 2);
        write(">");
        writeNewline();
        serializeCompactRDFElementProps(paramXMPNode, paramInt + 1);
        writeIndent(paramInt + 1);
        write("</rdf:Description>");
        writeNewline();
        return true;
        if (i != 0) {
          throw new XMPException("Can't mix rdf:resource qualifier and element fields", 202);
        }
      }
      write(" rdf:parseType=\"Resource\"/>");
      writeNewline();
      return false;
      label208:
      serializeCompactRDFAttrProps(paramXMPNode, paramInt + 1);
      write("/>");
      writeNewline();
      return false;
      label230:
      write(" rdf:parseType=\"Resource\">");
      writeNewline();
      serializeCompactRDFElementProps(paramXMPNode, paramInt + 1);
      return true;
      k = i;
      i = j;
      j = k;
    }
  }
  
  private void serializePrettyRDFProperty(XMPNode paramXMPNode, boolean paramBoolean, int paramInt)
    throws IOException, XMPException
  {
    String str = paramXMPNode.getName();
    Object localObject;
    int i;
    int k;
    if (!paramBoolean)
    {
      if ("[]".equals(str)) {
        break label159;
      }
      writeIndent(paramInt);
      write(60);
      write(str);
      localObject = paramXMPNode.iterateQualifier();
      i = 0;
      k = 0;
    }
    for (;;)
    {
      if (!((Iterator)localObject).hasNext()) {
        break label173;
      }
      XMPNode localXMPNode = (XMPNode)((Iterator)localObject).next();
      if (RDF_ATTR_QUALIFIER.contains(localXMPNode.getName()))
      {
        boolean bool = "rdf:resource".equals(localXMPNode.getName());
        k = bool;
        if (paramBoolean) {
          continue;
        }
        write(32);
        write(localXMPNode.getName());
        write("=\"");
        appendNodeValue(localXMPNode.getValue(), true);
        write(34);
        k = bool;
        continue;
        str = "rdf:value";
        break;
        label159:
        str = "rdf:li";
        break;
      }
      i = 1;
    }
    label173:
    if (i == 0) {}
    int j;
    while (paramXMPNode.getOptions().isCompositeProperty())
    {
      if (paramXMPNode.getOptions().isArray()) {
        break label507;
      }
      if (k == 0) {
        break label598;
      }
      paramXMPNode = paramXMPNode.iterateChildren();
      for (;;)
      {
        if (!paramXMPNode.hasNext()) {
          break label693;
        }
        localObject = (XMPNode)paramXMPNode.next();
        if (!canBeRDFAttrProp((XMPNode)localObject)) {
          break;
        }
        writeNewline();
        writeIndent(paramInt + 1);
        write(32);
        write(((XMPNode)localObject).getName());
        write("=\"");
        appendNodeValue(((XMPNode)localObject).getValue(), true);
        write(34);
      }
      if (!paramBoolean)
      {
        if (k == 0)
        {
          write(" rdf:parseType=\"Resource\">");
          writeNewline();
          serializePrettyRDFProperty(paramXMPNode, true, paramInt + 1);
          paramXMPNode = paramXMPNode.iterateQualifier();
          while (paramXMPNode.hasNext())
          {
            localObject = (XMPNode)paramXMPNode.next();
            if (!RDF_ATTR_QUALIFIER.contains(((XMPNode)localObject).getName())) {
              serializePrettyRDFProperty((XMPNode)localObject, false, paramInt + 1);
            }
          }
        }
        throw new XMPException("Can't mix rdf:resource and general qualifiers", 202);
        i = 1;
        j = 1;
      }
    }
    while (j == 0)
    {
      return;
      if (!paramXMPNode.getOptions().isURI()) {
        if (paramXMPNode.getValue() != null) {
          break label470;
        }
      }
      label470:
      while ("".equals(paramXMPNode.getValue()))
      {
        write("/>");
        writeNewline();
        j = 0;
        i = 1;
        break;
        write(" rdf:resource=\"");
        appendNodeValue(paramXMPNode.getValue(), true);
        write("\"/>");
        writeNewline();
        j = 0;
        i = 1;
        break;
      }
      write(62);
      appendNodeValue(paramXMPNode.getValue(), false);
      i = 0;
      j = 1;
      continue;
      label507:
      write(62);
      writeNewline();
      emitRDFArrayTag(paramXMPNode, true, paramInt + 1);
      if (!paramXMPNode.getOptions().isArrayAltText()) {}
      for (;;)
      {
        localObject = paramXMPNode.iterateChildren();
        while (((Iterator)localObject).hasNext()) {
          serializePrettyRDFProperty((XMPNode)((Iterator)localObject).next(), false, paramInt + 2);
        }
        XMPNodeUtils.normalizeLangArray(paramXMPNode);
      }
      emitRDFArrayTag(paramXMPNode, false, paramInt + 1);
      i = 1;
      j = 1;
      continue;
      label598:
      if (paramXMPNode.hasChildren())
      {
        write(" rdf:parseType=\"Resource\">");
        writeNewline();
        paramXMPNode = paramXMPNode.iterateChildren();
        while (paramXMPNode.hasNext()) {
          serializePrettyRDFProperty((XMPNode)paramXMPNode.next(), false, paramInt + 1);
        }
      }
      write(" rdf:parseType=\"Resource\"/>");
      writeNewline();
      j = 0;
      i = 1;
      continue;
      i = 1;
      j = 1;
      continue;
      throw new XMPException("Can't mix rdf:resource and complex fields", 202);
      label693:
      write("/>");
      writeNewline();
      j = 0;
      i = 1;
    }
    if (i == 0) {}
    for (;;)
    {
      write("</");
      write(str);
      write(62);
      writeNewline();
      return;
      writeIndent(paramInt);
    }
  }
  
  private void serializePrettyRDFSchema(XMPNode paramXMPNode)
    throws IOException, XMPException
  {
    writeIndent(2);
    write("<rdf:Description rdf:about=");
    writeTreeName();
    HashSet localHashSet = new HashSet();
    localHashSet.add("xml");
    localHashSet.add("rdf");
    declareUsedNamespaces(paramXMPNode, localHashSet, 4);
    write(62);
    writeNewline();
    paramXMPNode = paramXMPNode.iterateChildren();
    while (paramXMPNode.hasNext()) {
      serializePrettyRDFProperty((XMPNode)paramXMPNode.next(), false, 3);
    }
    writeIndent(2);
    write("</rdf:Description>");
    writeNewline();
  }
  
  private void serializePrettyRDFSchemas()
    throws IOException, XMPException
  {
    if (this.xmp.getRoot().getChildrenLength() <= 0)
    {
      writeIndent(2);
      write("<rdf:Description rdf:about=");
      writeTreeName();
      write("/>");
      writeNewline();
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.xmp.getRoot().iterateChildren();
      while (localIterator.hasNext()) {
        serializePrettyRDFSchema((XMPNode)localIterator.next());
      }
    }
  }
  
  private void write(int paramInt)
    throws IOException
  {
    this.writer.write(paramInt);
  }
  
  private void write(String paramString)
    throws IOException
  {
    this.writer.write(paramString);
  }
  
  private void writeChars(int paramInt, char paramChar)
    throws IOException
  {
    while (paramInt > 0)
    {
      this.writer.write(paramChar);
      paramInt -= 1;
    }
  }
  
  private void writeIndent(int paramInt)
    throws IOException
  {
    paramInt = this.options.getBaseIndent() + paramInt;
    while (paramInt > 0)
    {
      this.writer.write(this.options.getIndent());
      paramInt -= 1;
    }
  }
  
  private void writeNewline()
    throws IOException
  {
    this.writer.write(this.options.getNewline());
  }
  
  private void writeTreeName()
    throws IOException
  {
    write(34);
    String str = this.xmp.getRoot().getName();
    if (str == null) {}
    for (;;)
    {
      write(34);
      return;
      appendNodeValue(str, true);
    }
  }
  
  protected void checkOptionsConsistence()
    throws XMPException
  {
    if (!(this.options.getEncodeUTF16BE() | this.options.getEncodeUTF16LE()))
    {
      if (this.options.getExactPacketLength()) {
        break label74;
      }
      if (this.options.getReadOnlyPacket()) {
        break label135;
      }
      if (this.options.getOmitPacketWrapper()) {
        break label172;
      }
      if (this.padding == 0) {
        break label201;
      }
      label55:
      if (this.options.getIncludeThumbnailPad()) {
        break label216;
      }
    }
    label74:
    label122:
    label135:
    label172:
    label201:
    label216:
    while (this.xmp.doesPropertyExist("http://ns.adobe.com/xap/1.0/", "Thumbnails"))
    {
      do
      {
        return;
        this.unicodeSize = 2;
        break;
        if ((this.options.getOmitPacketWrapper() | this.options.getIncludeThumbnailPad())) {
          break label122;
        }
      } while ((this.options.getPadding() & this.unicodeSize - 1) == 0);
      throw new XMPException("Exact size must be a multiple of the Unicode element", 103);
      throw new XMPException("Inconsistent options for exact size serialize", 103);
      if (!(this.options.getOmitPacketWrapper() | this.options.getIncludeThumbnailPad()))
      {
        this.padding = 0;
        return;
      }
      throw new XMPException("Inconsistent options for read-only packet", 103);
      if (!this.options.getIncludeThumbnailPad())
      {
        this.padding = 0;
        return;
      }
      throw new XMPException("Inconsistent options for non-packet serialize", 103);
      this.padding = (this.unicodeSize * 2048);
      break label55;
    }
    this.padding += this.unicodeSize * 10000;
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