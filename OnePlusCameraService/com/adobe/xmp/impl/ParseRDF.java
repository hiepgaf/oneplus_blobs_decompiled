package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.options.PropertyOptions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParseRDF
  implements XMPError, XMPConst
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
  
  static
  {
    boolean bool = false;
    if (ParseRDF.class.desiredAssertionStatus()) {}
    for (;;)
    {
      $assertionsDisabled = bool;
      return;
      bool = true;
    }
  }
  
  private static XMPNode addChildNode(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    XMPSchemaRegistry localXMPSchemaRegistry = XMPMetaFactory.getSchemaRegistry();
    String str = paramNode.getNamespaceURI();
    if (str == null) {
      throw new XMPException("XML namespace required for all elements and attributes", 202);
    }
    Object localObject;
    boolean bool1;
    label99:
    boolean bool2;
    if (!"http://purl.org/dc/1.1/".equals(str))
    {
      localObject = localXMPSchemaRegistry.getNamespacePrefix(str);
      if (localObject == null) {
        break label163;
      }
      paramNode = localObject + paramNode.getLocalName();
      localObject = new PropertyOptions();
      if (paramBoolean) {
        break label203;
      }
      bool1 = false;
      bool2 = "rdf:li".equals(paramNode);
      boolean bool3 = "rdf:value".equals(paramNode);
      paramXMPMetaImpl = new XMPNode(paramNode, paramString, (PropertyOptions)localObject);
      paramXMPMetaImpl.setAlias(bool1);
      if (!bool3) {
        break label257;
      }
      paramXMPNode.addChild(1, paramXMPMetaImpl);
      label144:
      if (bool3) {
        break label265;
      }
    }
    for (;;)
    {
      if (bool2) {
        break label301;
      }
      return paramXMPMetaImpl;
      str = "http://purl.org/dc/elements/1.1/";
      break;
      label163:
      if (paramNode.getPrefix() == null) {}
      for (localObject = "_dflt";; localObject = paramNode.getPrefix())
      {
        localObject = localXMPSchemaRegistry.registerNamespace(str, (String)localObject);
        break;
      }
      label203:
      paramXMPNode = XMPNodeUtils.findSchemaNode(paramXMPMetaImpl.getRoot(), str, "_dflt", true);
      paramXMPNode.setImplicit(false);
      if (localXMPSchemaRegistry.findAlias(paramNode) == null)
      {
        bool1 = false;
        break label99;
      }
      paramXMPMetaImpl.getRoot().setHasAliases(true);
      paramXMPNode.setHasAliases(true);
      bool1 = true;
      break label99;
      label257:
      paramXMPNode.addChild(paramXMPMetaImpl);
      break label144;
      label265:
      if (paramBoolean) {}
      while (!paramXMPNode.getOptions().isStruct()) {
        throw new XMPException("Misplaced rdf:value element", 202);
      }
      paramXMPNode.setHasValueChild(true);
    }
    label301:
    if (paramXMPNode.getOptions().isArray())
    {
      paramXMPMetaImpl.setName("[]");
      return paramXMPMetaImpl;
    }
    throw new XMPException("Misplaced rdf:li element", 202);
  }
  
  private static XMPNode addQualifierNode(XMPNode paramXMPNode, String paramString1, String paramString2)
    throws XMPException
  {
    if (!"xml:lang".equals(paramString1)) {}
    for (;;)
    {
      paramString1 = new XMPNode(paramString1, paramString2, null);
      paramXMPNode.addQualifier(paramString1);
      return paramString1;
      paramString2 = Utils.normalizeLangValue(paramString2);
    }
  }
  
  private static void fixupQualifiedNode(XMPNode paramXMPNode)
    throws XMPException
  {
    int i = 1;
    Object localObject;
    if ($assertionsDisabled)
    {
      localObject = paramXMPNode.getChild(1);
      if (!$assertionsDisabled) {
        break label82;
      }
      label20:
      if (((XMPNode)localObject).getOptions().getHasLanguage()) {
        break label102;
      }
    }
    for (;;)
    {
      if (i <= ((XMPNode)localObject).getQualifierLength())
      {
        paramXMPNode.addQualifier(((XMPNode)localObject).getQualifier(i));
        i += 1;
        continue;
        if (!paramXMPNode.getOptions().isStruct()) {}
        while (!paramXMPNode.hasChildren()) {
          throw new AssertionError();
        }
        break;
        label82:
        if ("rdf:value".equals(((XMPNode)localObject).getName())) {
          break label20;
        }
        throw new AssertionError();
        label102:
        if (!paramXMPNode.getOptions().getHasLanguage())
        {
          XMPNode localXMPNode = ((XMPNode)localObject).getQualifier(1);
          ((XMPNode)localObject).removeQualifier(localXMPNode);
          paramXMPNode.addQualifier(localXMPNode);
        }
        else
        {
          throw new XMPException("Redundant xml:lang for rdf:value element", 203);
        }
      }
    }
    i = 2;
    while (i <= paramXMPNode.getChildrenLength())
    {
      paramXMPNode.addQualifier(paramXMPNode.getChild(i));
      i += 1;
    }
    if ($assertionsDisabled) {}
    while ((paramXMPNode.getOptions().isStruct()) || (paramXMPNode.getHasValueChild()))
    {
      paramXMPNode.setHasValueChild(false);
      paramXMPNode.getOptions().setStruct(false);
      paramXMPNode.getOptions().mergeWith(((XMPNode)localObject).getOptions());
      paramXMPNode.setValue(((XMPNode)localObject).getValue());
      paramXMPNode.removeChildren();
      localObject = ((XMPNode)localObject).iterateChildren();
      while (((Iterator)localObject).hasNext()) {
        paramXMPNode.addChild((XMPNode)((Iterator)localObject).next());
      }
    }
    throw new AssertionError();
  }
  
  private static int getRDFTermKind(Node paramNode)
  {
    String str3 = paramNode.getLocalName();
    String str2 = paramNode.getNamespaceURI();
    String str1;
    if (str2 != null)
    {
      str1 = str2;
      if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str1)) {
        break label98;
      }
    }
    label98:
    do
    {
      return 0;
      if ("about".equals(str3)) {}
      while ("ID".equals(str3))
      {
        str1 = str2;
        if (!(paramNode instanceof Attr)) {
          break;
        }
        str1 = str2;
        if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(((Attr)paramNode).getOwnerElement().getNamespaceURI())) {
          break;
        }
        str1 = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        break;
      }
      str1 = str2;
      break;
      if ("li".equals(str3)) {
        break label221;
      }
      if ("parseType".equals(str3)) {
        break label224;
      }
      if ("Description".equals(str3)) {
        break label226;
      }
      if ("about".equals(str3)) {
        break label229;
      }
      if ("resource".equals(str3)) {
        break label231;
      }
      if ("RDF".equals(str3)) {
        break label233;
      }
      if ("ID".equals(str3)) {
        break label235;
      }
      if ("nodeID".equals(str3)) {
        break label237;
      }
      if ("datatype".equals(str3)) {
        break label240;
      }
      if ("aboutEach".equals(str3)) {
        break label243;
      }
      if ("aboutEachPrefix".equals(str3)) {
        break label246;
      }
    } while (!"bagID".equals(str3));
    return 12;
    label221:
    return 9;
    label224:
    return 4;
    label226:
    return 8;
    label229:
    return 3;
    label231:
    return 5;
    label233:
    return 1;
    label235:
    return 2;
    label237:
    return 6;
    label240:
    return 7;
    label243:
    return 10;
    label246:
    return 11;
  }
  
  private static boolean isCoreSyntaxTerm(int paramInt)
  {
    if (1 > paramInt) {}
    while (paramInt > 7) {
      return false;
    }
    return true;
  }
  
  private static boolean isOldTerm(int paramInt)
  {
    if (10 > paramInt) {}
    while (paramInt > 12) {
      return false;
    }
    return true;
  }
  
  private static boolean isPropertyElementName(int paramInt)
  {
    boolean bool = false;
    if (paramInt == 8) {}
    while (isOldTerm(paramInt)) {
      return false;
    }
    if (!isCoreSyntaxTerm(paramInt)) {
      bool = true;
    }
    return bool;
  }
  
  private static boolean isWhitespaceNode(Node paramNode)
  {
    if (paramNode.getNodeType() == 3)
    {
      paramNode = paramNode.getNodeValue();
      int i = 0;
      for (;;)
      {
        if (i >= paramNode.length()) {
          break label49;
        }
        if (!Character.isWhitespace(paramNode.charAt(i))) {
          break;
        }
        i += 1;
      }
    }
    return false;
    return false;
    label49:
    return true;
  }
  
  static XMPMetaImpl parse(Node paramNode)
    throws XMPException
  {
    XMPMetaImpl localXMPMetaImpl = new XMPMetaImpl();
    rdf_RDF(localXMPMetaImpl, paramNode);
    return localXMPMetaImpl;
  }
  
  private static void rdf_EmptyPropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int i = 0;
    int m = 0;
    int k = 0;
    int j = 0;
    int n;
    Object localObject1;
    int i1;
    if (!paramNode.hasChildNodes())
    {
      n = 0;
      localObject1 = null;
      if (n >= paramNode.getAttributes().getLength()) {
        break label541;
      }
      localObject2 = paramNode.getAttributes().item(n);
      if (!"xmlns".equals(((Node)localObject2).getPrefix())) {
        break label141;
      }
      i1 = j;
      j = k;
      k = m;
      m = i;
      i = i1;
    }
    for (;;)
    {
      i1 = n + 1;
      n = m;
      m = k;
      k = j;
      j = i;
      i = n;
      n = i1;
      break;
      throw new XMPException("Nested content not allowed with rdf:resource or property attributes", 202);
      label141:
      if (((Node)localObject2).getPrefix() != null) {}
      while (!"xmlns".equals(((Node)localObject2).getNodeName())) {
        switch (getRDFTermKind((Node)localObject2))
        {
        case 1: 
        case 3: 
        case 4: 
        default: 
          throw new XMPException("Unrecognized attribute of empty property element", 202);
        }
      }
      i1 = i;
      i = j;
      j = k;
      k = m;
      m = i1;
      continue;
      i1 = i;
      i = j;
      j = k;
      k = m;
      m = i1;
      continue;
      if (k == 0)
      {
        if (j == 0)
        {
          if (j == 0) {
            break label341;
          }
          i1 = 1;
          m = i;
          i = j;
          j = k;
          k = i1;
        }
      }
      else {
        throw new XMPException("Empty property element can't have both rdf:resource and rdf:nodeID", 202);
      }
      throw new XMPException("Empty property element can't have both rdf:value and rdf:resource", 203);
      label341:
      localObject1 = localObject2;
      i1 = 1;
      m = i;
      i = j;
      j = k;
      k = i1;
      continue;
      if (m == 0)
      {
        k = m;
        m = i;
        i1 = 1;
        i = j;
        j = i1;
      }
      else
      {
        throw new XMPException("Empty property element can't have both rdf:resource and rdf:nodeID", 202);
        if (!"value".equals(((Node)localObject2).getLocalName())) {}
        for (;;)
        {
          if ("xml:lang".equals(((Node)localObject2).getNodeName()))
          {
            i1 = i;
            i = j;
            j = k;
            k = m;
            m = i1;
            break;
            if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(((Node)localObject2).getNamespaceURI()))
            {
              if (m == 0)
              {
                j = k;
                k = m;
                m = i;
                localObject1 = localObject2;
                i = 1;
                break;
              }
              throw new XMPException("Empty property element can't have both rdf:value and rdf:resource", 203);
            }
          }
        }
        i1 = 1;
        i = j;
        j = k;
        k = m;
        m = i1;
      }
    }
    label541:
    Object localObject2 = addChildNode(paramXMPMetaImpl, paramXMPNode, paramNode, "", paramBoolean);
    if (j != 0)
    {
      if (localObject1 != null) {
        break label645;
      }
      paramXMPNode = "";
      label567:
      ((XMPNode)localObject2).setValue(paramXMPNode);
      if (j == 0) {
        break label656;
      }
      i = 0;
      label581:
      j = 0;
      label584:
      if (j >= paramNode.getAttributes().getLength()) {
        return;
      }
      paramXMPNode = paramNode.getAttributes().item(j);
      if (paramXMPNode != localObject1) {
        break label688;
      }
    }
    for (;;)
    {
      label620:
      j += 1;
      break label584;
      if (m != 0) {
        break;
      }
      if (i == 0)
      {
        i = 0;
        break label581;
        label645:
        paramXMPNode = ((Node)localObject1).getNodeValue();
        break label567;
        label656:
        ((XMPNode)localObject2).getOptions().setURI(true);
        i = 0;
        break label581;
      }
      ((XMPNode)localObject2).getOptions().setStruct(true);
      i = 1;
      break label581;
      label688:
      if (!"xmlns".equals(paramXMPNode.getPrefix()))
      {
        if (paramXMPNode.getPrefix() != null) {}
        for (;;)
        {
          switch (getRDFTermKind(paramXMPNode))
          {
          case 2: 
          case 6: 
          case 1: 
          case 3: 
          case 4: 
          default: 
            throw new XMPException("Unrecognized attribute of empty property element", 202);
            if ("xmlns".equals(paramXMPNode.getNodeName())) {
              break label620;
            }
          }
        }
        addQualifierNode((XMPNode)localObject2, "rdf:resource", paramXMPNode.getNodeValue());
        continue;
        if (i != 0)
        {
          if (!"xml:lang".equals(paramXMPNode.getNodeName())) {
            addChildNode(paramXMPMetaImpl, (XMPNode)localObject2, paramXMPNode, paramXMPNode.getNodeValue(), false);
          }
        }
        else
        {
          addQualifierNode((XMPNode)localObject2, paramXMPNode.getNodeName(), paramXMPNode.getNodeValue());
          continue;
        }
        addQualifierNode((XMPNode)localObject2, "xml:lang", paramXMPNode.getNodeValue());
      }
    }
  }
  
  private static void rdf_LiteralPropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int j = 0;
    paramXMPNode = addChildNode(paramXMPMetaImpl, paramXMPNode, paramNode, null, paramBoolean);
    int i = 0;
    label78:
    Object localObject;
    while (i < paramNode.getAttributes().getLength())
    {
      paramXMPMetaImpl = paramNode.getAttributes().item(i);
      if ("xmlns".equals(paramXMPMetaImpl.getPrefix()))
      {
        i += 1;
      }
      else
      {
        String str;
        if (paramXMPMetaImpl.getPrefix() != null)
        {
          localObject = paramXMPMetaImpl.getNamespaceURI();
          str = paramXMPMetaImpl.getLocalName();
          if ("xml:lang".equals(paramXMPMetaImpl.getNodeName())) {
            break label151;
          }
          if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(localObject)) {
            break label167;
          }
        }
        for (;;)
        {
          throw new XMPException("Invalid attribute for literal property element", 202);
          if ("xmlns".equals(paramXMPMetaImpl.getNodeName())) {
            break;
          }
          break label78;
          label151:
          addQualifierNode(paramXMPNode, "xml:lang", paramXMPMetaImpl.getNodeValue());
          break;
          label167:
          if (("ID".equals(str)) || ("datatype".equals(str))) {
            break;
          }
        }
      }
    }
    paramXMPMetaImpl = "";
    i = j;
    while (i < paramNode.getChildNodes().getLength())
    {
      localObject = paramNode.getChildNodes().item(i);
      if (((Node)localObject).getNodeType() != 3) {
        throw new XMPException("Invalid child of literal property element", 202);
      }
      paramXMPMetaImpl = paramXMPMetaImpl + ((Node)localObject).getNodeValue();
      i += 1;
    }
    paramXMPNode.setValue(paramXMPMetaImpl);
  }
  
  private static void rdf_NodeElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int i = getRDFTermKind(paramNode);
    if (i == 8) {
      if (paramBoolean) {
        break label51;
      }
    }
    label51:
    while (i != 0)
    {
      rdf_NodeElementAttrs(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
      rdf_PropertyElementList(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
      return;
      if (i == 0) {
        break;
      }
      throw new XMPException("Node element must be rdf:Description or typed node", 202);
    }
    throw new XMPException("Top level typed node not allowed", 203);
  }
  
  private static void rdf_NodeElementAttrs(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int k = 0;
    int i = 0;
    if (i < paramNode.getAttributes().getLength())
    {
      Node localNode = paramNode.getAttributes().item(i);
      int j;
      if ("xmlns".equals(localNode.getPrefix())) {
        j = k;
      }
      for (;;)
      {
        label57:
        i += 1;
        k = j;
        break;
        if (localNode.getPrefix() != null) {}
        int m;
        for (;;)
        {
          m = getRDFTermKind(localNode);
          switch (m)
          {
          case 1: 
          case 4: 
          case 5: 
          default: 
            throw new XMPException("Invalid nodeElement attribute", 202);
            j = k;
            if ("xmlns".equals(localNode.getNodeName())) {
              break label57;
            }
          }
        }
        if (k <= 0)
        {
          k += 1;
          j = k;
          if (!paramBoolean) {
            continue;
          }
          j = k;
          if (m != 3) {
            continue;
          }
          if (paramXMPNode.getName() != null) {
            break label237;
          }
        }
        label237:
        while (paramXMPNode.getName().length() <= 0)
        {
          paramXMPNode.setName(localNode.getNodeValue());
          j = k;
          break;
          throw new XMPException("Mutally exclusive about, ID, nodeID attributes", 202);
        }
        j = k;
        if (!paramXMPNode.getName().equals(localNode.getNodeValue()))
        {
          throw new XMPException("Mismatched top level rdf:about values", 203);
          addChildNode(paramXMPMetaImpl, paramXMPNode, localNode, localNode.getNodeValue(), paramBoolean);
          j = k;
        }
      }
    }
  }
  
  private static void rdf_NodeElementList(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode)
    throws XMPException
  {
    int i = 0;
    if (i < paramNode.getChildNodes().getLength())
    {
      Node localNode = paramNode.getChildNodes().item(i);
      if (isWhitespaceNode(localNode)) {}
      for (;;)
      {
        i += 1;
        break;
        rdf_NodeElement(paramXMPMetaImpl, paramXMPNode, localNode, true);
      }
    }
  }
  
  private static void rdf_ParseTypeCollectionPropertyElement()
    throws XMPException
  {
    throw new XMPException("ParseTypeCollection property element not allowed", 203);
  }
  
  private static void rdf_ParseTypeLiteralPropertyElement()
    throws XMPException
  {
    throw new XMPException("ParseTypeLiteral property element not allowed", 203);
  }
  
  private static void rdf_ParseTypeOtherPropertyElement()
    throws XMPException
  {
    throw new XMPException("ParseTypeOther property element not allowed", 203);
  }
  
  private static void rdf_ParseTypeResourcePropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    paramXMPNode = addChildNode(paramXMPMetaImpl, paramXMPNode, paramNode, "", paramBoolean);
    paramXMPNode.getOptions().setStruct(true);
    int i = 0;
    while (i < paramNode.getAttributes().getLength())
    {
      Node localNode = paramNode.getAttributes().item(i);
      if ("xmlns".equals(localNode.getPrefix()))
      {
        i += 1;
      }
      else
      {
        label89:
        String str1;
        if (localNode.getPrefix() != null)
        {
          str1 = localNode.getLocalName();
          String str2 = localNode.getNamespaceURI();
          if ("xml:lang".equals(localNode.getNodeName())) {
            break label166;
          }
          if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str2)) {
            break label183;
          }
        }
        for (;;)
        {
          throw new XMPException("Invalid attribute for ParseTypeResource property element", 202);
          if ("xmlns".equals(localNode.getNodeName())) {
            break;
          }
          break label89;
          label166:
          addQualifierNode(paramXMPNode, "xml:lang", localNode.getNodeValue());
          break;
          label183:
          if (("ID".equals(str1)) || ("parseType".equals(str1))) {
            break;
          }
        }
      }
    }
    rdf_PropertyElementList(paramXMPMetaImpl, paramXMPNode, paramNode, false);
    if (!paramXMPNode.getHasValueChild()) {
      return;
    }
    fixupQualifiedNode(paramXMPNode);
  }
  
  private static void rdf_PropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    Object localObject1 = null;
    int j = 0;
    NamedNodeMap localNamedNodeMap;
    Object localObject3;
    if (isPropertyElementName(getRDFTermKind(paramNode)))
    {
      localNamedNodeMap = paramNode.getAttributes();
      i = 0;
      if (i >= localNamedNodeMap.getLength()) {
        break label166;
      }
      localObject3 = localNamedNodeMap.item(i);
      if (!"xmlns".equals(((Node)localObject3).getPrefix())) {
        break label117;
      }
      label66:
      if (localObject1 == null) {
        break label154;
      }
    }
    Object localObject2;
    for (;;)
    {
      ((List)localObject1).add(((Node)localObject3).getNodeName());
      localObject2 = localObject1;
      for (;;)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        throw new XMPException("Invalid property element name", 202);
        label117:
        localObject2 = localObject1;
        if (((Node)localObject3).getPrefix() == null)
        {
          if ("xmlns".equals(((Node)localObject3).getNodeName())) {
            break label66;
          }
          localObject2 = localObject1;
        }
      }
      label154:
      localObject1 = new ArrayList();
    }
    label166:
    String str;
    if (localObject1 == null)
    {
      if (localNamedNodeMap.getLength() > 3) {
        break label320;
      }
      i = 0;
      if (i >= localNamedNodeMap.getLength()) {
        break label445;
      }
      localObject1 = localNamedNodeMap.item(i);
      localObject2 = ((Node)localObject1).getLocalName();
      localObject3 = ((Node)localObject1).getNamespaceURI();
      str = ((Node)localObject1).getNodeValue();
      if ("xml:lang".equals(((Node)localObject1).getNodeName())) {
        break label328;
      }
      label250:
      if ("datatype".equals(localObject2)) {
        break label362;
      }
      label261:
      if ("parseType".equals(localObject2)) {
        break label381;
      }
    }
    label320:
    label328:
    label362:
    label381:
    while (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(localObject3))
    {
      rdf_EmptyPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
      return;
      localObject1 = ((List)localObject1).iterator();
      while (((Iterator)localObject1).hasNext()) {
        localNamedNodeMap.removeNamedItem((String)((Iterator)localObject1).next());
      }
      break;
      rdf_EmptyPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
      return;
      if (!"ID".equals(localObject2)) {}
      for (;;)
      {
        i += 1;
        break;
        if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(localObject3)) {
          break label250;
        }
      }
      if (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(localObject3)) {
        break label261;
      }
      rdf_LiteralPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
      return;
    }
    if (!"Literal".equals(str))
    {
      if (!"Resource".equals(str))
      {
        if ("Collection".equals(str)) {
          break label441;
        }
        rdf_ParseTypeOtherPropertyElement();
      }
    }
    else
    {
      rdf_ParseTypeLiteralPropertyElement();
      return;
    }
    rdf_ParseTypeResourcePropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
    return;
    label441:
    rdf_ParseTypeCollectionPropertyElement();
    return;
    label445:
    int i = j;
    if (!paramNode.hasChildNodes())
    {
      rdf_EmptyPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
      return;
    }
    while (paramNode.getChildNodes().item(i).getNodeType() == 3)
    {
      i += 1;
      if (i >= paramNode.getChildNodes().getLength())
      {
        rdf_LiteralPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
        return;
      }
    }
    rdf_ResourcePropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
  }
  
  private static void rdf_PropertyElementList(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int i = 0;
    while (i < paramNode.getChildNodes().getLength())
    {
      Node localNode = paramNode.getChildNodes().item(i);
      if (!isWhitespaceNode(localNode))
      {
        if (localNode.getNodeType() == 1) {
          rdf_PropertyElement(paramXMPMetaImpl, paramXMPNode, localNode, paramBoolean);
        }
      }
      else
      {
        i += 1;
        continue;
      }
      throw new XMPException("Expected property element node not found", 202);
    }
  }
  
  static void rdf_RDF(XMPMetaImpl paramXMPMetaImpl, Node paramNode)
    throws XMPException
  {
    if (!paramNode.hasAttributes()) {
      throw new XMPException("Invalid attributes of rdf:RDF element", 202);
    }
    rdf_NodeElementList(paramXMPMetaImpl, paramXMPMetaImpl.getRoot(), paramNode);
  }
  
  private static void rdf_ResourcePropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    if (!paramBoolean) {}
    Node localNode;
    while (!"iX:changes".equals(paramNode.getNodeName()))
    {
      paramXMPNode = addChildNode(paramXMPMetaImpl, paramXMPNode, paramNode, "", paramBoolean);
      i = 0;
      for (;;)
      {
        if (i >= paramNode.getAttributes().getLength()) {
          break label208;
        }
        localNode = paramNode.getAttributes().item(i);
        if (!"xmlns".equals(localNode.getPrefix())) {
          break;
        }
        i += 1;
      }
    }
    return;
    label100:
    String str1;
    String str2;
    if (localNode.getPrefix() != null)
    {
      str1 = localNode.getLocalName();
      str2 = localNode.getNamespaceURI();
      if ("xml:lang".equals(localNode.getNodeName())) {
        break label177;
      }
      if ("ID".equals(str1)) {
        break label194;
      }
    }
    for (;;)
    {
      throw new XMPException("Invalid attribute for resource property element", 202);
      if ("xmlns".equals(localNode.getNodeName())) {
        break;
      }
      break label100;
      label177:
      addQualifierNode(paramXMPNode, "xml:lang", localNode.getNodeValue());
      break;
      label194:
      if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str2)) {
        break;
      }
    }
    label208:
    int i = 0;
    int j = 0;
    while (i < paramNode.getChildNodes().getLength())
    {
      localNode = paramNode.getChildNodes().item(i);
      if (isWhitespaceNode(localNode))
      {
        i += 1;
      }
      else
      {
        if (localNode.getNodeType() != 1) {}
        while (j == 0)
        {
          throw new XMPException("Children of resource property element must be XML elements", 202);
          if (j == 0)
          {
            paramBoolean = "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(localNode.getNamespaceURI());
            str1 = localNode.getLocalName();
            if (!paramBoolean)
            {
              label324:
              if (paramBoolean) {
                break label399;
              }
              label328:
              if (paramBoolean) {
                break label426;
              }
              label332:
              paramXMPNode.getOptions().setStruct(true);
              if (!paramBoolean) {
                break label457;
              }
              label345:
              rdf_NodeElement(paramXMPMetaImpl, paramXMPNode, localNode, false);
              if (paramXMPNode.getHasValueChild()) {
                break label532;
              }
              if (paramXMPNode.getOptions().isArrayAlternate()) {
                break label539;
              }
            }
            for (;;)
            {
              j = 1;
              break;
              if (!"Bag".equals(str1)) {
                break label324;
              }
              paramXMPNode.getOptions().setArray(true);
              break label345;
              label399:
              if (!"Seq".equals(str1)) {
                break label328;
              }
              paramXMPNode.getOptions().setArray(true).setArrayOrdered(true);
              break label345;
              label426:
              if (!"Alt".equals(str1)) {
                break label332;
              }
              paramXMPNode.getOptions().setArray(true).setArrayOrdered(true).setArrayAlternate(true);
              break label345;
              label457:
              if ("Description".equals(str1)) {
                break label345;
              }
              str2 = localNode.getNamespaceURI();
              if (str2 != null)
              {
                addQualifierNode(paramXMPNode, "rdf:type", str2 + ':' + str1);
                break label345;
              }
              throw new XMPException("All XML elements must be in a namespace", 203);
              label532:
              fixupQualifiedNode(paramXMPNode);
              continue;
              label539:
              XMPNodeUtils.detectAltText(paramXMPNode);
            }
          }
        }
        throw new XMPException("Invalid child of resource property element", 202);
      }
    }
    if (j != 0) {
      return;
    }
    throw new XMPException("Missing child of resource property element", 202);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/ParseRDF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */