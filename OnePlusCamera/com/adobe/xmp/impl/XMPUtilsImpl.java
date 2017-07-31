package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.impl.xpath.XMPPathSegment;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPAliasInfo;
import java.util.Iterator;

public class XMPUtilsImpl
  implements XMPConst
{
  private static final String COMMAS = ",，､﹐﹑、،՝";
  private static final String CONTROLS = "  ";
  private static final String QUOTES = "\"[]«»〝〞〟―‹›";
  private static final String SEMICOLA = ";；﹔؛;";
  private static final String SPACES = " 　〿";
  private static final int UCK_COMMA = 2;
  private static final int UCK_CONTROL = 5;
  private static final int UCK_NORMAL = 0;
  private static final int UCK_QUOTE = 4;
  private static final int UCK_SEMICOLON = 3;
  private static final int UCK_SPACE = 1;
  
  static
  {
    boolean bool = false;
    if (XMPUtilsImpl.class.desiredAssertionStatus()) {}
    for (;;)
    {
      $assertionsDisabled = bool;
      return;
      bool = true;
    }
  }
  
  public static void appendProperties(XMPMeta paramXMPMeta1, XMPMeta paramXMPMeta2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws XMPException
  {
    ParameterAsserts.assertImplementation(paramXMPMeta1);
    ParameterAsserts.assertImplementation(paramXMPMeta2);
    paramXMPMeta1 = (XMPMetaImpl)paramXMPMeta1;
    paramXMPMeta2 = (XMPMetaImpl)paramXMPMeta2;
    Iterator localIterator1 = paramXMPMeta1.getRoot().iterateChildren();
    while (localIterator1.hasNext())
    {
      XMPNode localXMPNode1 = (XMPNode)localIterator1.next();
      paramXMPMeta1 = XMPNodeUtils.findSchemaNode(paramXMPMeta2.getRoot(), localXMPNode1.getName(), false);
      int i;
      Iterator localIterator2;
      if (paramXMPMeta1 != null)
      {
        i = 0;
        localIterator2 = localXMPNode1.iterateChildren();
      }
      for (;;)
      {
        label77:
        if (!localIterator2.hasNext()) {
          break label178;
        }
        XMPNode localXMPNode2 = (XMPNode)localIterator2.next();
        if (paramBoolean1) {}
        while (!Utils.isInternalProperty(localXMPNode1.getName(), localXMPNode2.getName()))
        {
          appendSubtree(paramXMPMeta2, localXMPNode2, paramXMPMeta1, paramBoolean2, paramBoolean3);
          break label77;
          paramXMPMeta1 = new XMPNode(localXMPNode1.getName(), localXMPNode1.getValue(), new PropertyOptions().setSchemaNode(true));
          paramXMPMeta2.getRoot().addChild(paramXMPMeta1);
          i = 1;
          break;
        }
      }
      label178:
      if (!paramXMPMeta1.hasChildren())
      {
        if (i != 0) {}
        while (paramBoolean3)
        {
          paramXMPMeta2.getRoot().removeChild(paramXMPMeta1);
          break;
        }
      }
    }
  }
  
  private static void appendSubtree(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode1, XMPNode paramXMPNode2, boolean paramBoolean1, boolean paramBoolean2)
    throws XMPException
  {
    XMPNode localXMPNode = XMPNodeUtils.findChildNode(paramXMPNode2, paramXMPNode1.getName(), false);
    int i;
    label24:
    Object localObject;
    if (!paramBoolean2)
    {
      i = 0;
      if (paramBoolean2) {
        break label132;
      }
      if (localXMPNode == null) {
        break label149;
      }
      if (paramBoolean1) {
        break label161;
      }
      localObject = paramXMPNode1.getOptions();
      if (localObject != localXMPNode.getOptions()) {
        break label194;
      }
      if (((PropertyOptions)localObject).isStruct()) {
        break label195;
      }
      if (((PropertyOptions)localObject).isArrayAltText()) {
        break label249;
      }
      if (((PropertyOptions)localObject).isArray()) {
        break label443;
      }
    }
    for (;;)
    {
      return;
      if (!paramXMPNode1.getOptions().isSimple())
      {
        if (paramXMPNode1.hasChildren()) {
          break label126;
        }
        i = 1;
        break;
      }
      if (paramXMPNode1.getValue() == null) {}
      while (paramXMPNode1.getValue().length() == 0)
      {
        i = 1;
        break;
      }
      i = 0;
      break;
      label126:
      i = 0;
      break;
      label132:
      if (i == 0) {
        break label24;
      }
      if (localXMPNode != null)
      {
        paramXMPNode2.removeChild(localXMPNode);
        return;
        label149:
        paramXMPNode2.addChild((XMPNode)paramXMPNode1.clone());
        return;
        label161:
        paramXMPMetaImpl.setNode(localXMPNode, paramXMPNode1.getValue(), paramXMPNode1.getOptions(), true);
        paramXMPNode2.removeChild(localXMPNode);
        paramXMPNode2.addChild((XMPNode)paramXMPNode1.clone());
        return;
        label194:
        return;
        label195:
        paramXMPNode1 = paramXMPNode1.iterateChildren();
        while (paramXMPNode1.hasNext())
        {
          appendSubtree(paramXMPMetaImpl, (XMPNode)paramXMPNode1.next(), localXMPNode, paramBoolean1, paramBoolean2);
          if ((paramBoolean2) && (!localXMPNode.hasChildren())) {
            paramXMPNode2.removeChild(localXMPNode);
          }
        }
        continue;
        label249:
        paramXMPMetaImpl = paramXMPNode1.iterateChildren();
        for (;;)
        {
          label254:
          if (!paramXMPMetaImpl.hasNext()) {
            break label441;
          }
          paramXMPNode1 = (XMPNode)paramXMPMetaImpl.next();
          if ((paramXMPNode1.hasQualifier()) && ("xml:lang".equals(paramXMPNode1.getQualifier(1).getName())))
          {
            i = XMPNodeUtils.lookupLanguageItem(localXMPNode, paramXMPNode1.getQualifier(1).getValue());
            if (!paramBoolean2)
            {
              label316:
              if (i != -1) {
                break label395;
              }
              if ("x-default".equals(paramXMPNode1.getQualifier(1).getValue())) {
                break label397;
              }
            }
            label395:
            label397:
            while (!localXMPNode.hasChildren())
            {
              paramXMPNode1.cloneSubtree(localXMPNode);
              break label254;
              if (paramXMPNode1.getValue() == null)
              {
                if (i == -1) {
                  break label254;
                }
                localXMPNode.removeChild(i);
                if (localXMPNode.hasChildren()) {
                  break label254;
                }
                paramXMPNode2.removeChild(localXMPNode);
                break label254;
              }
              if (paramXMPNode1.getValue().length() == 0) {
                break;
              }
              break label316;
              break label254;
            }
            localObject = new XMPNode(paramXMPNode1.getName(), paramXMPNode1.getValue(), paramXMPNode1.getOptions());
            paramXMPNode1.cloneSubtree((XMPNode)localObject);
            localXMPNode.addChild(1, (XMPNode)localObject);
          }
        }
        label441:
        continue;
        label443:
        paramXMPNode1 = paramXMPNode1.iterateChildren();
        paramXMPMetaImpl = localXMPNode;
        while (paramXMPNode1.hasNext())
        {
          localXMPNode = (XMPNode)paramXMPNode1.next();
          localObject = paramXMPMetaImpl.iterateChildren();
          i = 0;
          while (((Iterator)localObject).hasNext()) {
            if (itemValuesMatch(localXMPNode, (XMPNode)((Iterator)localObject).next())) {
              i = 1;
            }
          }
          if (i == 0)
          {
            paramXMPMetaImpl = (XMPNode)localXMPNode.clone();
            paramXMPNode2.addChild(paramXMPMetaImpl);
          }
        }
      }
    }
  }
  
  private static String applyQuotes(String paramString, char paramChar1, char paramChar2, boolean paramBoolean)
  {
    int j;
    label10:
    int k;
    if (paramString != null)
    {
      j = 0;
      i = 0;
      if (j < paramString.length())
      {
        k = classifyCharacter(paramString.charAt(j));
        if (j == 0) {
          break label64;
        }
        label35:
        if (k == 1) {
          break label73;
        }
        if (k != 3) {
          break label90;
        }
      }
    }
    for (;;)
    {
      if (j >= paramString.length())
      {
        return paramString;
        paramString = "";
        break;
        label64:
        if (k != 4)
        {
          break label35;
          label73:
          if (i == 0)
          {
            i = 1;
            label81:
            j += 1;
            break label10;
            label90:
            if (k != 5)
            {
              if (k != 2) {}
              for (;;)
              {
                i = 0;
                break label81;
                if (!paramBoolean) {
                  break;
                }
              }
            }
          }
        }
      }
    }
    StringBuffer localStringBuffer = new StringBuffer(paramString.length() + 2);
    int i = 0;
    while ((i <= j) && (classifyCharacter(paramString.charAt(j)) != 4)) {
      i += 1;
    }
    localStringBuffer.append(paramChar1).append(paramString.substring(0, i));
    if (i < paramString.length())
    {
      localStringBuffer.append(paramString.charAt(i));
      if (classifyCharacter(paramString.charAt(i)) != 4) {}
      for (;;)
      {
        i += 1;
        break;
        if (isSurroundingQuote(paramString.charAt(i), paramChar1, paramChar2)) {
          localStringBuffer.append(paramString.charAt(i));
        }
      }
    }
    localStringBuffer.append(paramChar2);
    return localStringBuffer.toString();
  }
  
  public static String catenateArrayItems(XMPMeta paramXMPMeta, String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    ParameterAsserts.assertImplementation(paramXMPMeta);
    if (paramString3 == null)
    {
      paramString3 = "; ";
      label19:
      if (paramString4 != null) {
        break label85;
      }
      label24:
      paramString4 = "\"";
      label28:
      paramXMPMeta = (XMPMetaImpl)paramXMPMeta;
      paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
      paramString1 = XMPNodeUtils.findNode(paramXMPMeta.getRoot(), paramString1, false, null);
      if (paramString1 == null) {
        break label96;
      }
      if (paramString1.getOptions().isArray()) {
        break label99;
      }
    }
    label85:
    label96:
    label99:
    while (paramString1.getOptions().isArrayAlternate())
    {
      throw new XMPException("Named property must be non-alternate array", 4);
      if (paramString3.length() == 0) {
        break;
      }
      break label19;
      if (paramString4.length() == 0) {
        break label24;
      }
      break label28;
      return "";
    }
    checkSeparator(paramString3);
    char c1 = paramString4.charAt(0);
    char c2 = checkQuotes(paramString4, c1);
    paramXMPMeta = new StringBuffer();
    paramString1 = paramString1.iterateChildren();
    while (paramString1.hasNext())
    {
      paramString2 = (XMPNode)paramString1.next();
      if (!paramString2.getOptions().isCompositeProperty())
      {
        paramXMPMeta.append(applyQuotes(paramString2.getValue(), c1, c2, paramBoolean));
        if (paramString1.hasNext()) {
          paramXMPMeta.append(paramString3);
        }
      }
      else
      {
        throw new XMPException("Array items must be simple", 4);
      }
    }
    return paramXMPMeta.toString();
  }
  
  private static char checkQuotes(String paramString, char paramChar)
    throws XMPException
  {
    char c;
    if (classifyCharacter(paramChar) == 4)
    {
      if (paramString.length() == 1) {
        break label52;
      }
      c = paramString.charAt(1);
      if (classifyCharacter(c) != 4) {
        break label57;
      }
    }
    while (c == getClosingQuote(paramChar))
    {
      return c;
      throw new XMPException("Invalid quoting character", 4);
      label52:
      c = paramChar;
    }
    label57:
    throw new XMPException("Invalid quoting character", 4);
    throw new XMPException("Mismatched quote pair", 4);
  }
  
  private static void checkSeparator(String paramString)
    throws XMPException
  {
    int j = 0;
    int i = 0;
    if (i < paramString.length())
    {
      int k = classifyCharacter(paramString.charAt(i));
      if (k != 3)
      {
        if (k != 1) {}
      }
      else
      {
        for (;;)
        {
          i += 1;
          break;
          if (j != 0) {
            break label47;
          }
          j = 1;
        }
        label47:
        throw new XMPException("Separator can have only one semicolon", 4);
      }
      throw new XMPException("Separator can have only spaces and one semicolon", 4);
    }
    if (j != 0) {
      return;
    }
    throw new XMPException("Separator must have one semicolon", 4);
  }
  
  private static int classifyCharacter(char paramChar)
  {
    if (" 　〿".indexOf(paramChar) >= 0) {
      return 1;
    }
    if (' ' > paramChar) {}
    for (;;)
    {
      if (",，､﹐﹑、،՝".indexOf(paramChar) >= 0) {
        break label57;
      }
      if (";；﹔؛;".indexOf(paramChar) >= 0) {
        break label59;
      }
      if ("\"[]«»〝〞〟―‹›".indexOf(paramChar) < 0) {
        break label61;
      }
      return 4;
      if (paramChar <= '​') {
        break;
      }
    }
    label57:
    return 2;
    label59:
    return 3;
    label61:
    if ('〈' > paramChar)
    {
      label68:
      if ('‘' <= paramChar) {
        break label93;
      }
      label75:
      if (paramChar >= ' ') {
        break label103;
      }
    }
    label93:
    label103:
    while ("  ".indexOf(paramChar) >= 0)
    {
      return 5;
      if (paramChar > '』') {
        break label68;
      }
      break;
      if (paramChar <= '‟') {
        break;
      }
      break label75;
    }
    return 0;
  }
  
  private static char getClosingQuote(char paramChar)
  {
    switch (paramChar)
    {
    default: 
      return '\000';
    case '"': 
      return '"';
    case '[': 
      return ']';
    case '«': 
      return '»';
    case '»': 
      return '«';
    case '―': 
      return '―';
    case '‘': 
      return '’';
    case '‚': 
      return '‛';
    case '“': 
      return '”';
    case '„': 
      return '‟';
    case '‹': 
      return '›';
    case '›': 
      return '‹';
    case '〈': 
      return '〉';
    case '《': 
      return '》';
    case '「': 
      return '」';
    case '『': 
      return '』';
    }
    return '〟';
  }
  
  private static boolean isClosingingQuote(char paramChar1, char paramChar2, char paramChar3)
  {
    if (paramChar1 == paramChar3) {
      break label14;
    }
    for (;;)
    {
      return true;
      if (paramChar2 != '〝') {
        label14:
        if (paramChar1 != '〟') {
          return false;
        }
      } else {
        if (paramChar1 != '〞') {
          break;
        }
      }
    }
  }
  
  private static boolean isSurroundingQuote(char paramChar1, char paramChar2, char paramChar3)
  {
    if (paramChar1 == paramChar2) {}
    while (isClosingingQuote(paramChar1, paramChar2, paramChar3)) {
      return true;
    }
    return false;
  }
  
  private static boolean itemValuesMatch(XMPNode paramXMPNode1, XMPNode paramXMPNode2)
    throws XMPException
  {
    Object localObject1 = paramXMPNode1.getOptions();
    if (!((PropertyOptions)localObject1).equals(paramXMPNode2.getOptions()))
    {
      if (((PropertyOptions)localObject1).getOptions() == 0) {
        break label103;
      }
      if (((PropertyOptions)localObject1).isStruct()) {
        break label174;
      }
      if (!$assertionsDisabled) {
        break label241;
      }
      paramXMPNode1 = paramXMPNode1.iterateChildren();
    }
    label103:
    label148:
    label150:
    label174:
    label241:
    label259:
    for (;;)
    {
      Object localObject2;
      if (paramXMPNode1.hasNext())
      {
        localObject1 = (XMPNode)paramXMPNode1.next();
        localObject2 = paramXMPNode2.iterateChildren();
        do
        {
          if (!((Iterator)localObject2).hasNext()) {
            break;
          }
        } while (!itemValuesMatch((XMPNode)localObject1, (XMPNode)((Iterator)localObject2).next()));
      }
      for (int i = 1;; i = 0)
      {
        if (i != 0) {
          break label259;
        }
        return false;
        return false;
        if (paramXMPNode1.getValue().equals(paramXMPNode2.getValue()))
        {
          if (paramXMPNode1.getOptions().getHasLanguage() != paramXMPNode2.getOptions().getHasLanguage()) {
            break label148;
          }
          if (paramXMPNode1.getOptions().getHasLanguage()) {
            break label150;
          }
        }
        for (;;)
        {
          return true;
          return false;
          return false;
          if (!paramXMPNode1.getQualifier(1).getValue().equals(paramXMPNode2.getQualifier(1).getValue()))
          {
            return false;
            if (paramXMPNode1.getChildrenLength() == paramXMPNode2.getChildrenLength()) {
              paramXMPNode1 = paramXMPNode1.iterateChildren();
            }
            while (paramXMPNode1.hasNext())
            {
              localObject1 = (XMPNode)paramXMPNode1.next();
              localObject2 = XMPNodeUtils.findChildNode(paramXMPNode2, ((XMPNode)localObject1).getName(), false);
              if (localObject2 == null) {}
              while (!itemValuesMatch((XMPNode)localObject1, (XMPNode)localObject2))
              {
                return false;
                return false;
              }
            }
          }
        }
        if (((PropertyOptions)localObject1).isArray()) {
          break;
        }
        throw new AssertionError();
      }
    }
  }
  
  public static void removeProperties(XMPMeta paramXMPMeta, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
    throws XMPException
  {
    ParameterAsserts.assertImplementation(paramXMPMeta);
    paramXMPMeta = (XMPMetaImpl)paramXMPMeta;
    if (paramString2 == null) {
      if (paramString1 != null) {
        break label174;
      }
    }
    label111:
    label174:
    while (paramString1.length() <= 0)
    {
      paramXMPMeta = paramXMPMeta.getRoot().iterateChildren();
      for (;;)
      {
        if (paramXMPMeta.hasNext()) {
          if (removeSchemaChildren((XMPNode)paramXMPMeta.next(), paramBoolean1))
          {
            paramXMPMeta.remove();
            continue;
            if (paramString2.length() <= 0) {
              break;
            }
            if (paramString1 == null) {}
            while (paramString1.length() == 0) {
              throw new XMPException("Property name requires schema namespace", 4);
            }
            paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
            paramXMPMeta = XMPNodeUtils.findNode(paramXMPMeta.getRoot(), paramString1, false, null);
            if (paramXMPMeta != null) {
              break label111;
            }
          }
        }
      }
      return;
      if (paramBoolean1) {}
      while (!Utils.isInternalProperty(paramString1.getSegment(0).getName(), paramString1.getSegment(1).getName()))
      {
        paramString1 = paramXMPMeta.getParent();
        paramString1.removeChild(paramXMPMeta);
        if ((!paramString1.getOptions().isSchemaNode()) || (paramString1.hasChildren())) {
          break;
        }
        paramString1.getParent().removeChild(paramString1);
        return;
      }
      return;
    }
    paramString2 = XMPNodeUtils.findSchemaNode(paramXMPMeta.getRoot(), paramString1, false);
    label195:
    int i;
    if (paramString2 == null)
    {
      if (!paramBoolean2) {
        break label282;
      }
      paramString1 = XMPMetaFactory.getSchemaRegistry().findAliases(paramString1);
      i = 0;
      label213:
      if (i < paramString1.length)
      {
        paramString2 = paramString1[i];
        paramString2 = XMPPathParser.expandXPath(paramString2.getNamespace(), paramString2.getPropName());
        paramString2 = XMPNodeUtils.findNode(paramXMPMeta.getRoot(), paramString2, false, null);
        if (paramString2 != null) {
          break label284;
        }
      }
    }
    for (;;)
    {
      i += 1;
      break label213;
      break;
      if (!removeSchemaChildren(paramString2, paramBoolean1)) {
        break label195;
      }
      paramXMPMeta.getRoot().removeChild(paramString2);
      break label195;
      label282:
      break;
      label284:
      paramString2.getParent().removeChild(paramString2);
    }
  }
  
  private static boolean removeSchemaChildren(XMPNode paramXMPNode, boolean paramBoolean)
  {
    Iterator localIterator = paramXMPNode.iterateChildren();
    while (localIterator.hasNext())
    {
      XMPNode localXMPNode = (XMPNode)localIterator.next();
      if (paramBoolean) {}
      while (!Utils.isInternalProperty(paramXMPNode.getName(), localXMPNode.getName()))
      {
        localIterator.remove();
        break;
      }
    }
    return !paramXMPNode.hasChildren();
  }
  
  public static void separateArrayItems(XMPMeta paramXMPMeta, String paramString1, String paramString2, String paramString3, PropertyOptions paramPropertyOptions, boolean paramBoolean)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    int i1;
    int i;
    int i2;
    int i4;
    if (paramString3 != null)
    {
      ParameterAsserts.assertImplementation(paramXMPMeta);
      paramString2 = separateFindCreateArray(paramString1, paramString2, paramPropertyOptions, (XMPMetaImpl)paramXMPMeta);
      i1 = 0;
      i = 0;
      i2 = 0;
      i4 = paramString3.length();
    }
    while (i2 < i4)
    {
      int n = i2;
      int m = i1;
      int j;
      label93:
      char c;
      if (n < i4)
      {
        j = paramString3.charAt(n);
        i1 = classifyCharacter(j);
        if (i1 == 0)
        {
          i = j;
          m = i1;
        }
      }
      else
      {
        if (n >= i4) {
          break;
        }
        if (m != 4) {
          break label236;
        }
        c = getClosingQuote(i);
        i1 = n + 1;
        paramXMPMeta = "";
        j = i;
        n = m;
        label130:
        paramString1 = paramXMPMeta;
        m = i1;
        if (i1 >= i4) {
          break label352;
        }
        j = paramString3.charAt(i1);
        n = classifyCharacter(j);
        if (n == 4) {
          break label475;
        }
        label164:
        paramXMPMeta = paramXMPMeta + j;
      }
      for (;;)
      {
        i1 += 1;
        break label130;
        throw new XMPException("Parameter must not be null", 4);
        m = i1;
        i = j;
        if (i1 == 4) {
          break label93;
        }
        n += 1;
        m = i1;
        i = j;
        break;
        label236:
        i1 = m;
        m = n;
        if (m < i4)
        {
          j = paramString3.charAt(m);
          i2 = classifyCharacter(j);
          if (i2 == 0) {
            i = j;
          }
          do
          {
            m += 1;
            i1 = i2;
            break;
            i = j;
          } while (i2 == 4);
          if (i2 == 2) {
            break label393;
          }
          label304:
          i = j;
          i1 = i2;
          if (i2 == 1)
          {
            if (m + 1 < i4) {
              break label405;
            }
            i1 = i2;
            i = j;
          }
        }
        for (;;)
        {
          paramString1 = paramString3.substring(n, m);
          n = i1;
          j = i;
          label352:
          i1 = 1;
          while (i1 <= paramString2.getChildrenLength())
          {
            i3 = i1;
            if (paramString1.equals(paramString2.getChild(i1).getValue())) {
              break label608;
            }
            i1 += 1;
          }
          label393:
          i = j;
          if (paramBoolean) {
            break;
          }
          break label304;
          label405:
          j = paramString3.charAt(m + 1);
          i3 = classifyCharacter(j);
          i = j;
          if (i3 == 0) {
            break;
          }
          i = j;
          if (i3 == 4) {
            break;
          }
          i = j;
          i1 = i2;
          if (i3 == 2)
          {
            i = j;
            if (paramBoolean) {
              break;
            }
            i = j;
            i1 = i2;
          }
        }
        label475:
        if (!isSurroundingQuote(j, i, c)) {
          break label164;
        }
        if (i1 + 1 >= i4) {}
        int k;
        for (m = 59;; m = k)
        {
          if (j == m) {
            break label553;
          }
          if (!isClosingingQuote(j, i, c)) {
            break label582;
          }
          m = i1 + 1;
          paramString1 = paramXMPMeta;
          break;
          k = paramString3.charAt(i1 + 1);
          classifyCharacter(k);
        }
        label553:
        paramXMPMeta = paramXMPMeta + j;
        i1 += 1;
        continue;
        label582:
        paramXMPMeta = paramXMPMeta + j;
      }
      int i3 = -1;
      label608:
      i2 = m;
      i = j;
      i1 = n;
      if (i3 < 0)
      {
        paramString2.addChild(new XMPNode("[]", paramString1, null));
        i2 = m;
        i = j;
        i1 = n;
      }
    }
  }
  
  private static XMPNode separateFindCreateArray(String paramString1, String paramString2, PropertyOptions paramPropertyOptions, XMPMetaImpl paramXMPMetaImpl)
    throws XMPException
  {
    paramPropertyOptions = XMPNodeUtils.verifySetOptions(paramPropertyOptions, null);
    if (paramPropertyOptions.isOnlyArrayOptions())
    {
      paramString2 = XMPPathParser.expandXPath(paramString1, paramString2);
      paramString1 = XMPNodeUtils.findNode(paramXMPMetaImpl.getRoot(), paramString2, false, null);
      if (paramString1 != null) {
        break label68;
      }
      paramString1 = XMPNodeUtils.findNode(paramXMPMetaImpl.getRoot(), paramString2, true, paramPropertyOptions.setArray(true));
      if (paramString1 == null) {
        break label120;
      }
    }
    label68:
    do
    {
      return paramString1;
      throw new XMPException("Options can only provide array form", 103);
      paramString2 = paramString1.getOptions();
      if (!paramString2.isArray()) {}
      while (paramString2.isArrayAlternate()) {
        throw new XMPException("Named property must be non-alternate array", 102);
      }
    } while (!paramPropertyOptions.equalArrayTypes(paramString2));
    throw new XMPException("Mismatch of specified and existing array form", 102);
    label120:
    throw new XMPException("Failed to create named array", 102);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/XMPUtilsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */