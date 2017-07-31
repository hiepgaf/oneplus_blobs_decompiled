package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPDateTimeFactory;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathSegment;
import com.adobe.xmp.options.PropertyOptions;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class XMPNodeUtils
  implements XMPConst
{
  static final int CLT_FIRST_ITEM = 5;
  static final int CLT_MULTIPLE_GENERIC = 3;
  static final int CLT_NO_VALUES = 0;
  static final int CLT_SINGLE_GENERIC = 2;
  static final int CLT_SPECIFIC_MATCH = 1;
  static final int CLT_XDEFAULT = 4;
  
  static
  {
    boolean bool = false;
    if (XMPNodeUtils.class.desiredAssertionStatus()) {}
    for (;;)
    {
      $assertionsDisabled = bool;
      return;
      bool = true;
    }
  }
  
  static void appendLangItem(XMPNode paramXMPNode, String paramString1, String paramString2)
    throws XMPException
  {
    paramString2 = new XMPNode("[]", paramString2, null);
    paramString1 = new XMPNode("xml:lang", paramString1, null);
    paramString2.addQualifier(paramString1);
    if ("x-default".equals(paramString1.getValue()))
    {
      paramXMPNode.addChild(1, paramString2);
      return;
    }
    paramXMPNode.addChild(paramString2);
  }
  
  static Object[] chooseLocalizedText(XMPNode paramXMPNode, String paramString1, String paramString2)
    throws XMPException
  {
    Iterator localIterator;
    Object localObject3;
    Object localObject1;
    int i;
    if (paramXMPNode.getOptions().isArrayAltText())
    {
      if (!paramXMPNode.hasChildren()) {
        break label96;
      }
      localIterator = paramXMPNode.iterateChildren();
      localObject3 = null;
      localObject1 = null;
      i = 0;
      break label167;
    }
    for (;;)
    {
      if (!localIterator.hasNext()) {
        break label234;
      }
      Object localObject2 = (XMPNode)localIterator.next();
      if (!((XMPNode)localObject2).getOptions().isCompositeProperty()) {
        if (((XMPNode)localObject2).hasQualifier()) {
          break label126;
        }
      }
      label96:
      label126:
      while (!"xml:lang".equals(((XMPNode)localObject2).getQualifier(1).getName()))
      {
        throw new XMPException("Alt-text array item has no language qualifier", 102);
        throw new XMPException("Localized text array is not alt-text", 102);
        paramXMPNode = new Object[2];
        paramXMPNode[0] = new Integer(0);
        return paramXMPNode;
        throw new XMPException("Alt-text array item is not simple", 102);
      }
      String str = ((XMPNode)localObject2).getQualifier(1).getValue();
      if (!paramString2.equals(str))
      {
        if (paramString1 == null)
        {
          label167:
          if (!"x-default".equals(str)) {
            continue;
          }
          localObject3 = localObject2;
        }
      }
      else {
        return new Object[] { new Integer(1), localObject2 };
      }
      if (!str.startsWith(paramString1)) {
        break;
      }
      if (localObject1 != null) {
        localObject2 = localObject1;
      }
      i += 1;
      localObject1 = localObject2;
    }
    label234:
    if (i != 1)
    {
      if (i <= 1)
      {
        if (localObject3 != null) {
          break label315;
        }
        return new Object[] { new Integer(5), paramXMPNode.getChild(1) };
      }
    }
    else {
      return new Object[] { new Integer(2), localObject1 };
    }
    return new Object[] { new Integer(3), localObject1 };
    label315:
    return new Object[] { new Integer(4), localObject3 };
  }
  
  static void deleteNode(XMPNode paramXMPNode)
  {
    XMPNode localXMPNode = paramXMPNode.getParent();
    if (!paramXMPNode.getOptions().isQualifier())
    {
      localXMPNode.removeChild(paramXMPNode);
      if (!localXMPNode.hasChildren()) {
        break label36;
      }
    }
    label36:
    while (!localXMPNode.getOptions().isSchemaNode())
    {
      return;
      localXMPNode.removeQualifier(paramXMPNode);
      break;
    }
    localXMPNode.getParent().removeChild(localXMPNode);
  }
  
  static void detectAltText(XMPNode paramXMPNode)
  {
    if (!paramXMPNode.getOptions().isArrayAlternate()) {}
    for (;;)
    {
      return;
      if (paramXMPNode.hasChildren())
      {
        Iterator localIterator = paramXMPNode.iterateChildren();
        do
        {
          if (!localIterator.hasNext()) {
            break;
          }
        } while (!((XMPNode)localIterator.next()).getOptions().getHasLanguage());
        for (int i = 1; i != 0; i = 0)
        {
          paramXMPNode.getOptions().setArrayAltText(true);
          normalizeLangArray(paramXMPNode);
          return;
        }
      }
    }
  }
  
  static XMPNode findChildNode(XMPNode paramXMPNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    XMPNode localXMPNode;
    if (paramXMPNode.getOptions().isSchemaNode())
    {
      localXMPNode = paramXMPNode.findChildByName(paramString);
      if (localXMPNode == null) {
        break label95;
      }
      label20:
      if (!$assertionsDisabled) {
        break label128;
      }
    }
    label83:
    label95:
    label128:
    while ((localXMPNode != null) || (!paramBoolean))
    {
      return localXMPNode;
      if (paramXMPNode.getOptions().isStruct()) {
        break;
      }
      if (paramXMPNode.isImplicit())
      {
        if (paramXMPNode.getOptions().isArray()) {
          break label83;
        }
        if (!paramBoolean) {
          break;
        }
        paramXMPNode.getOptions().setStruct(true);
        break;
      }
      throw new XMPException("Named children only allowed for schemas and structs", 102);
      throw new XMPException("Named children not allowed for arrays", 102);
      if (!paramBoolean) {
        break label20;
      }
      localXMPNode = new XMPNode(paramString, new PropertyOptions());
      localXMPNode.setImplicit(true);
      paramXMPNode.addChild(localXMPNode);
      break label20;
    }
    throw new AssertionError();
  }
  
  private static int findIndexedItem(XMPNode paramXMPNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    int i;
    try
    {
      i = Integer.parseInt(paramString.substring(1, paramString.length() - 1));
      if (i >= 1)
      {
        if (!paramBoolean) {
          return i;
        }
      }
      else {
        throw new XMPException("Array index must be larger than zero", 102);
      }
    }
    catch (NumberFormatException paramXMPNode)
    {
      throw new XMPException("Array index not digits.", 102);
    }
    if (i == paramXMPNode.getChildrenLength() + 1)
    {
      paramString = new XMPNode("[]", null);
      paramString.setImplicit(true);
      paramXMPNode.addChild(paramString);
    }
    return i;
  }
  
  static XMPNode findNode(XMPNode paramXMPNode, XMPPath paramXMPPath, boolean paramBoolean, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    if (paramXMPPath == null) {}
    while (paramXMPPath.size() == 0) {
      throw new XMPException("Empty XMPPath", 102);
    }
    XMPNode localXMPNode1 = findSchemaNode(paramXMPNode, paramXMPPath.getSegment(0).getName(), paramBoolean);
    int i;
    if (localXMPNode1 != null)
    {
      if (localXMPNode1.isImplicit()) {
        break label113;
      }
      paramXMPNode = null;
      i = 1;
    }
    for (;;)
    {
      try
      {
        if (i < paramXMPPath.size())
        {
          localXMPNode1 = followXPathStep(localXMPNode1, paramXMPPath.getSegment(i), paramBoolean);
          if (localXMPNode1 != null)
          {
            boolean bool = localXMPNode1.isImplicit();
            if (bool) {
              continue;
            }
            localXMPNode2 = paramXMPNode;
            i += 1;
            paramXMPNode = localXMPNode2;
            continue;
            return null;
            label113:
            localXMPNode1.setImplicit(false);
            paramXMPNode = localXMPNode1;
            break;
          }
          if (!paramBoolean) {
            return null;
          }
          deleteNode(paramXMPNode);
          return null;
        }
      }
      catch (XMPException paramXMPPath)
      {
        if (paramXMPNode == null)
        {
          throw paramXMPPath;
          localXMPNode1.setImplicit(false);
          if (i != 1)
          {
            if (i >= paramXMPPath.size() - 1) {
              break label288;
            }
          }
          else
          {
            if ((!paramXMPPath.getSegment(i).isAlias()) || (paramXMPPath.getSegment(i).getAliasForm() == 0)) {
              continue;
            }
            localXMPNode1.getOptions().setOption(paramXMPPath.getSegment(i).getAliasForm(), true);
            break label288;
          }
          if ((paramXMPPath.getSegment(i).getKind() == 1) && (!localXMPNode1.getOptions().isCompositeProperty())) {
            localXMPNode1.getOptions().setStruct(true);
          }
        }
        else
        {
          deleteNode(paramXMPNode);
          continue;
          if (paramXMPNode == null) {
            return localXMPNode1;
          }
          localXMPNode1.getOptions().mergeWith(paramPropertyOptions);
          localXMPNode1.setOptions(localXMPNode1.getOptions());
          return localXMPNode1;
        }
      }
      label288:
      XMPNode localXMPNode2 = paramXMPNode;
      if (paramXMPNode == null) {
        localXMPNode2 = localXMPNode1;
      }
    }
  }
  
  private static XMPNode findQualifierNode(XMPNode paramXMPNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    XMPNode localXMPNode;
    if ($assertionsDisabled)
    {
      localXMPNode = paramXMPNode.findQualifierByName(paramString);
      if (localXMPNode == null) {
        break label36;
      }
    }
    label36:
    while (!paramBoolean)
    {
      return localXMPNode;
      if (!paramString.startsWith("?")) {
        break;
      }
      throw new AssertionError();
    }
    paramString = new XMPNode(paramString, null);
    paramString.setImplicit(true);
    paramXMPNode.addQualifier(paramString);
    return paramString;
  }
  
  static XMPNode findSchemaNode(XMPNode paramXMPNode, String paramString1, String paramString2, boolean paramBoolean)
    throws XMPException
  {
    if ($assertionsDisabled)
    {
      localObject = paramXMPNode.findChildByName(paramString1);
      if (localObject == null) {
        break label36;
      }
    }
    label36:
    while (!paramBoolean)
    {
      return (XMPNode)localObject;
      if (paramXMPNode.getParent() == null) {
        break;
      }
      throw new AssertionError();
    }
    XMPNode localXMPNode = new XMPNode(paramString1, new PropertyOptions().setSchemaNode(true));
    localXMPNode.setImplicit(true);
    Object localObject = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(paramString1);
    if (localObject != null) {}
    for (paramString1 = (String)localObject;; paramString1 = XMPMetaFactory.getSchemaRegistry().registerNamespace(paramString1, paramString2))
    {
      localXMPNode.setValue(paramString1);
      paramXMPNode.addChild(localXMPNode);
      return localXMPNode;
      if (paramString2 == null) {}
      while (paramString2.length() == 0) {
        throw new XMPException("Unregistered schema namespace URI", 101);
      }
    }
  }
  
  static XMPNode findSchemaNode(XMPNode paramXMPNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    return findSchemaNode(paramXMPNode, paramString, null, paramBoolean);
  }
  
  private static XMPNode followXPathStep(XMPNode paramXMPNode, XMPPathSegment paramXMPPathSegment, boolean paramBoolean)
    throws XMPException
  {
    Object localObject = null;
    int i = paramXMPPathSegment.getKind();
    if (i != 1)
    {
      if (i != 2)
      {
        if (!paramXMPNode.getOptions().isArray()) {
          break label88;
        }
        if (i == 3) {
          break label101;
        }
        if (i == 4) {
          break label136;
        }
        if (i == 6) {
          break label144;
        }
        if (i == 5) {
          break label166;
        }
        throw new XMPException("Unknown array indexing step in FollowXPathStep", 9);
      }
    }
    else
    {
      paramXMPPathSegment = findChildNode(paramXMPNode, paramXMPPathSegment.getName(), paramBoolean);
      return paramXMPPathSegment;
    }
    return findQualifierNode(paramXMPNode, paramXMPPathSegment.getName().substring(1), paramBoolean);
    label88:
    throw new XMPException("Indexing applied to non-array", 102);
    label101:
    i = findIndexedItem(paramXMPNode, paramXMPPathSegment.getName(), paramBoolean);
    for (;;)
    {
      paramXMPPathSegment = (XMPPathSegment)localObject;
      if (1 > i) {
        break;
      }
      paramXMPPathSegment = (XMPPathSegment)localObject;
      if (i > paramXMPNode.getChildrenLength()) {
        break;
      }
      return paramXMPNode.getChild(i);
      label136:
      i = paramXMPNode.getChildrenLength();
      continue;
      label144:
      paramXMPPathSegment = Utils.splitNameAndValue(paramXMPPathSegment.getName());
      i = lookupFieldSelector(paramXMPNode, paramXMPPathSegment[0], paramXMPPathSegment[1]);
      continue;
      label166:
      String[] arrayOfString = Utils.splitNameAndValue(paramXMPPathSegment.getName());
      i = lookupQualSelector(paramXMPNode, arrayOfString[0], arrayOfString[1], paramXMPPathSegment.getAliasForm());
    }
  }
  
  private static int lookupFieldSelector(XMPNode paramXMPNode, String paramString1, String paramString2)
    throws XMPException
  {
    int j = -1;
    int i = 1;
    if (i > paramXMPNode.getChildrenLength()) {}
    while (j >= 0) {
      return j;
    }
    XMPNode localXMPNode1 = paramXMPNode.getChild(i);
    if (localXMPNode1.getOptions().isStruct())
    {
      int k = 1;
      for (;;)
      {
        if (k > localXMPNode1.getChildrenLength()) {
          break label117;
        }
        XMPNode localXMPNode2 = localXMPNode1.getChild(k);
        if ((paramString1.equals(localXMPNode2.getName())) && (paramString2.equals(localXMPNode2.getValue()))) {
          break;
        }
        k += 1;
      }
    }
    throw new XMPException("Field selector must be used on array of struct", 102);
    j = i;
    label117:
    for (;;)
    {
      i += 1;
      break;
    }
  }
  
  static int lookupLanguageItem(XMPNode paramXMPNode, String paramString)
    throws XMPException
  {
    int i;
    XMPNode localXMPNode;
    if (paramXMPNode.getOptions().isArray())
    {
      i = 1;
      if (i > paramXMPNode.getChildrenLength()) {
        break label86;
      }
      localXMPNode = paramXMPNode.getChild(i);
      if (localXMPNode.hasQualifier()) {
        break label53;
      }
    }
    label53:
    while ((!"xml:lang".equals(localXMPNode.getQualifier(1).getName())) || (!paramString.equals(localXMPNode.getQualifier(1).getValue())))
    {
      i += 1;
      break;
      throw new XMPException("Language item must be used on array", 102);
    }
    return i;
    label86:
    return -1;
  }
  
  private static int lookupQualSelector(XMPNode paramXMPNode, String paramString1, String paramString2, int paramInt)
    throws XMPException
  {
    if (!"xml:lang".equals(paramString1)) {
      paramInt = 1;
    }
    while (paramInt < paramXMPNode.getChildrenLength())
    {
      Iterator localIterator = paramXMPNode.getChild(paramInt).iterateQualifier();
      while (localIterator.hasNext())
      {
        XMPNode localXMPNode = (XMPNode)localIterator.next();
        if ((paramString1.equals(localXMPNode.getName())) && (paramString2.equals(localXMPNode.getValue())))
        {
          return paramInt;
          int i = lookupLanguageItem(paramXMPNode, Utils.normalizeLangValue(paramString2));
          if (i >= 0) {}
          while ((paramInt & 0x1000) <= 0) {
            return i;
          }
          paramString1 = new XMPNode("[]", null);
          paramString1.addQualifier(new XMPNode("xml:lang", "x-default", null));
          paramXMPNode.addChild(1, paramString1);
          return 1;
        }
      }
      paramInt += 1;
    }
    return -1;
  }
  
  static void normalizeLangArray(XMPNode paramXMPNode)
  {
    int i;
    XMPNode localXMPNode;
    if (paramXMPNode.getOptions().isArrayAltText())
    {
      i = 2;
      if (i > paramXMPNode.getChildrenLength()) {
        break label73;
      }
      localXMPNode = paramXMPNode.getChild(i);
      if (localXMPNode.hasQualifier()) {
        break label41;
      }
    }
    label41:
    while (!"x-default".equals(localXMPNode.getQualifier(1).getValue()))
    {
      i += 1;
      break;
      return;
    }
    try
    {
      paramXMPNode.removeChild(i);
      paramXMPNode.addChild(1, localXMPNode);
      if (i != 2) {
        label73:
        return;
      }
    }
    catch (XMPException localXMPException)
    {
      while ($assertionsDisabled) {}
      throw new AssertionError();
    }
    paramXMPNode.getChild(2).setValue(localXMPNode.getValue());
  }
  
  static String serializeNodeValue(Object paramObject)
  {
    if (paramObject != null)
    {
      if ((paramObject instanceof Boolean)) {
        break label69;
      }
      if ((paramObject instanceof Integer)) {
        break label83;
      }
      if ((paramObject instanceof Long)) {
        break label97;
      }
      if ((paramObject instanceof Double)) {
        break label111;
      }
      if ((paramObject instanceof XMPDateTime)) {
        break label125;
      }
      if ((paramObject instanceof GregorianCalendar)) {
        break label136;
      }
      if ((paramObject instanceof byte[])) {
        break label150;
      }
      paramObject = paramObject.toString();
    }
    while (paramObject == null)
    {
      return null;
      paramObject = null;
      continue;
      label69:
      paramObject = XMPUtils.convertFromBoolean(((Boolean)paramObject).booleanValue());
      continue;
      label83:
      paramObject = XMPUtils.convertFromInteger(((Integer)paramObject).intValue());
      continue;
      label97:
      paramObject = XMPUtils.convertFromLong(((Long)paramObject).longValue());
      continue;
      label111:
      paramObject = XMPUtils.convertFromDouble(((Double)paramObject).doubleValue());
      continue;
      label125:
      paramObject = XMPUtils.convertFromDate((XMPDateTime)paramObject);
      continue;
      label136:
      paramObject = XMPUtils.convertFromDate(XMPDateTimeFactory.createFromCalendar((GregorianCalendar)paramObject));
      continue;
      label150:
      paramObject = XMPUtils.encodeBase64((byte[])paramObject);
    }
    return Utils.removeControlChars((String)paramObject);
  }
  
  static void setNodeValue(XMPNode paramXMPNode, Object paramObject)
  {
    paramObject = serializeNodeValue(paramObject);
    if (!paramXMPNode.getOptions().isQualifier()) {}
    while (!"xml:lang".equals(paramXMPNode.getName()))
    {
      paramXMPNode.setValue((String)paramObject);
      return;
    }
    paramXMPNode.setValue(Utils.normalizeLangValue((String)paramObject));
  }
  
  static PropertyOptions verifySetOptions(PropertyOptions paramPropertyOptions, Object paramObject)
    throws XMPException
  {
    if (paramPropertyOptions != null)
    {
      if (paramPropertyOptions.isArrayAltText()) {
        break label53;
      }
      label11:
      if (paramPropertyOptions.isArrayAlternate()) {
        break label62;
      }
      label18:
      if (paramPropertyOptions.isArrayOrdered()) {
        break label71;
      }
      label25:
      if (paramPropertyOptions.isCompositeProperty()) {
        break label80;
      }
    }
    label53:
    label62:
    label71:
    label80:
    while ((paramObject == null) || (paramObject.toString().length() <= 0))
    {
      paramPropertyOptions.assertConsistency(paramPropertyOptions.getOptions());
      return paramPropertyOptions;
      paramPropertyOptions = new PropertyOptions();
      break;
      paramPropertyOptions.setArrayAlternate(true);
      break label11;
      paramPropertyOptions.setArrayOrdered(true);
      break label18;
      paramPropertyOptions.setArray(true);
      break label25;
    }
    throw new XMPException("Structs and arrays can't have values", 103);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPNodeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */