package com.adobe.xmp.impl.xpath;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.impl.Utils;
import com.adobe.xmp.options.AliasOptions;
import com.adobe.xmp.properties.XMPAliasInfo;

public final class XMPPathParser
{
  public static XMPPath expandXPath(String paramString1, String paramString2)
    throws XMPException
  {
    if (paramString1 == null) {}
    while (paramString2 == null) {
      throw new XMPException("Parameter must not be null", 4);
    }
    XMPPath localXMPPath = new XMPPath();
    PathPosition localPathPosition = new PathPosition();
    localPathPosition.path = paramString2;
    parseRootNode(paramString1, localPathPosition, localXMPPath);
    if (localPathPosition.stepEnd < paramString2.length())
    {
      localPathPosition.stepBegin = localPathPosition.stepEnd;
      skipPathDelimiter(paramString2, localPathPosition);
      localPathPosition.stepEnd = localPathPosition.stepBegin;
      if (paramString2.charAt(localPathPosition.stepBegin) == '[')
      {
        paramString1 = parseIndexSegment(localPathPosition);
        label96:
        if (paramString1.getKind() == 1) {
          break label129;
        }
        if (paramString1.getKind() == 6) {
          break label245;
        }
      }
      label129:
      label142:
      label227:
      label245:
      label307:
      do
      {
        for (;;)
        {
          localXMPPath.add(paramString1);
          break;
          paramString1 = parseStructSegment(localPathPosition);
          break label96;
          if (paramString1.getName().charAt(0) != '@') {
            if (paramString1.getName().charAt(0) == '?') {
              break label227;
            }
          }
          for (;;)
          {
            verifyQualName(localPathPosition.path.substring(localPathPosition.nameStart, localPathPosition.nameEnd));
            break;
            paramString1.setName("?" + paramString1.getName().substring(1));
            if ("?xml:lang".equals(paramString1.getName())) {
              break label142;
            }
            throw new XMPException("Only xml:lang allowed with '@'", 102);
            localPathPosition.nameStart += 1;
            paramString1.setKind(2);
          }
          if (paramString1.getName().charAt(1) == '@') {
            break label307;
          }
          if (paramString1.getName().charAt(1) == '?')
          {
            localPathPosition.nameStart += 1;
            paramString1.setKind(5);
            verifyQualName(localPathPosition.path.substring(localPathPosition.nameStart, localPathPosition.nameEnd));
          }
        }
        paramString1.setName("[?" + paramString1.getName().substring(2));
      } while (paramString1.getName().startsWith("[?xml:lang="));
      throw new XMPException("Only xml:lang allowed with '@'", 102);
    }
    return localXMPPath;
  }
  
  private static XMPPathSegment parseIndexSegment(PathPosition paramPathPosition)
    throws XMPException
  {
    paramPathPosition.stepEnd += 1;
    if ('0' > paramPathPosition.path.charAt(paramPathPosition.stepEnd)) {}
    label39:
    label40:
    int i;
    label116:
    label184:
    label197:
    label198:
    XMPPathSegment localXMPPathSegment;
    for (;;)
    {
      if (paramPathPosition.stepEnd >= paramPathPosition.path.length())
      {
        break label282;
        if (paramPathPosition.stepEnd >= paramPathPosition.path.length()) {
          break label330;
        }
        if (paramPathPosition.path.charAt(paramPathPosition.stepEnd) == ']') {
          break label342;
        }
        paramPathPosition.nameStart = (paramPathPosition.stepBegin + 1);
        paramPathPosition.nameEnd = paramPathPosition.stepEnd;
        paramPathPosition.stepEnd += 1;
        i = paramPathPosition.path.charAt(paramPathPosition.stepEnd);
        if (i != 39) {
          break label390;
        }
        for (paramPathPosition.stepEnd += 1;; paramPathPosition.stepEnd += 1)
        {
          if (paramPathPosition.stepEnd >= paramPathPosition.path.length()) {
            break label424;
          }
          if (paramPathPosition.path.charAt(paramPathPosition.stepEnd) == i) {
            break;
          }
        }
        if (paramPathPosition.path.charAt(paramPathPosition.stepEnd) <= '9') {
          if (paramPathPosition.stepEnd >= paramPathPosition.path.length())
          {
            break;
            localXMPPathSegment = new XMPPathSegment(null, 3);
            label208:
            if (paramPathPosition.stepEnd < paramPathPosition.path.length()) {
              break label504;
            }
          }
        }
      }
    }
    label282:
    label330:
    label342:
    label390:
    label424:
    label504:
    while (paramPathPosition.path.charAt(paramPathPosition.stepEnd) != ']')
    {
      throw new XMPException("Missing ']' for array index", 102);
      if (paramPathPosition.path.charAt(paramPathPosition.stepEnd) > '9') {
        break label198;
      }
      paramPathPosition.stepEnd += 1;
      break label184;
      if ('0' <= paramPathPosition.path.charAt(paramPathPosition.stepEnd)) {
        break label197;
      }
      break label198;
      if (paramPathPosition.path.charAt(paramPathPosition.stepEnd) == '=') {
        break label40;
      }
      paramPathPosition.stepEnd += 1;
      break;
      if (paramPathPosition.path.charAt(paramPathPosition.stepEnd) != ']') {
        break label39;
      }
      break label40;
      throw new XMPException("Missing ']' or '=' for array index", 102);
      if ("[last()".equals(paramPathPosition.path.substring(paramPathPosition.stepBegin, paramPathPosition.stepEnd)))
      {
        localXMPPathSegment = new XMPPathSegment(null, 4);
        break label208;
      }
      throw new XMPException("Invalid non-numeric array index", 102);
      if (i == 34) {
        break label116;
      }
      throw new XMPException("Invalid quote in array selector", 102);
      if (paramPathPosition.stepEnd + 1 >= paramPathPosition.path.length()) {}
      for (;;)
      {
        if (paramPathPosition.stepEnd < paramPathPosition.path.length())
        {
          paramPathPosition.stepEnd += 1;
          localXMPPathSegment = new XMPPathSegment(null, 6);
          break label208;
          if (paramPathPosition.path.charAt(paramPathPosition.stepEnd + 1) == i)
          {
            paramPathPosition.stepEnd += 1;
            break;
          }
        }
      }
      throw new XMPException("No terminating quote for array selector", 102);
    }
    paramPathPosition.stepEnd += 1;
    localXMPPathSegment.setName(paramPathPosition.path.substring(paramPathPosition.stepBegin, paramPathPosition.stepEnd));
    return localXMPPathSegment;
  }
  
  private static void parseRootNode(String paramString, PathPosition paramPathPosition, XMPPath paramXMPPath)
    throws XMPException
  {
    String str;
    for (;;)
    {
      if (paramPathPosition.stepEnd >= paramPathPosition.path.length()) {}
      while ("/[*".indexOf(paramPathPosition.path.charAt(paramPathPosition.stepEnd)) >= 0)
      {
        if (paramPathPosition.stepEnd == paramPathPosition.stepBegin) {
          break;
        }
        str = verifyXPathRoot(paramString, paramPathPosition.path.substring(paramPathPosition.stepBegin, paramPathPosition.stepEnd));
        paramPathPosition = XMPMetaFactory.getSchemaRegistry().findAlias(str);
        if (paramPathPosition == null) {
          break label194;
        }
        paramXMPPath.add(new XMPPathSegment(paramPathPosition.getNamespace(), Integer.MIN_VALUE));
        paramString = new XMPPathSegment(verifyXPathRoot(paramPathPosition.getNamespace(), paramPathPosition.getPropName()), 1);
        paramString.setAlias(true);
        paramString.setAliasForm(paramPathPosition.getAliasForm().getOptions());
        paramXMPPath.add(paramString);
        if (paramPathPosition.getAliasForm().isArrayAltText()) {
          break label222;
        }
        if (paramPathPosition.getAliasForm().isArray()) {
          break label257;
        }
        return;
      }
      paramPathPosition.stepEnd += 1;
    }
    throw new XMPException("Empty initial XMPPath step", 102);
    label194:
    paramXMPPath.add(new XMPPathSegment(paramString, Integer.MIN_VALUE));
    paramXMPPath.add(new XMPPathSegment(str, 1));
    return;
    label222:
    paramString = new XMPPathSegment("[?xml:lang='x-default']", 5);
    paramString.setAlias(true);
    paramString.setAliasForm(paramPathPosition.getAliasForm().getOptions());
    paramXMPPath.add(paramString);
    return;
    label257:
    paramString = new XMPPathSegment("[1]", 3);
    paramString.setAlias(true);
    paramString.setAliasForm(paramPathPosition.getAliasForm().getOptions());
    paramXMPPath.add(paramString);
  }
  
  private static XMPPathSegment parseStructSegment(PathPosition paramPathPosition)
    throws XMPException
  {
    paramPathPosition.nameStart = paramPathPosition.stepBegin;
    for (;;)
    {
      if (paramPathPosition.stepEnd >= paramPathPosition.path.length()) {}
      while ("/[*".indexOf(paramPathPosition.path.charAt(paramPathPosition.stepEnd)) >= 0)
      {
        paramPathPosition.nameEnd = paramPathPosition.stepEnd;
        if (paramPathPosition.stepEnd == paramPathPosition.stepBegin) {
          break;
        }
        return new XMPPathSegment(paramPathPosition.path.substring(paramPathPosition.stepBegin, paramPathPosition.stepEnd), 1);
      }
      paramPathPosition.stepEnd += 1;
    }
    throw new XMPException("Empty XMPPath segment", 102);
  }
  
  private static void skipPathDelimiter(String paramString, PathPosition paramPathPosition)
    throws XMPException
  {
    if (paramString.charAt(paramPathPosition.stepBegin) != '/') {}
    while (paramString.charAt(paramPathPosition.stepBegin) != '*')
    {
      return;
      paramPathPosition.stepBegin += 1;
      if (paramPathPosition.stepBegin >= paramString.length()) {
        throw new XMPException("Empty XMPPath segment", 102);
      }
    }
    paramPathPosition.stepBegin += 1;
    if (paramPathPosition.stepBegin >= paramString.length()) {}
    while (paramString.charAt(paramPathPosition.stepBegin) != '[') {
      throw new XMPException("Missing '[' after '*'", 102);
    }
  }
  
  private static void verifyQualName(String paramString)
    throws XMPException
  {
    int i = paramString.indexOf(':');
    if (i <= 0) {}
    do
    {
      throw new XMPException("Ill-formed qualified name", 102);
      paramString = paramString.substring(0, i);
    } while (!Utils.isXMLNameNS(paramString));
    if (XMPMetaFactory.getSchemaRegistry().getNamespaceURI(paramString) == null) {
      throw new XMPException("Unknown namespace prefix for qualified name", 102);
    }
  }
  
  private static void verifySimpleXMLName(String paramString)
    throws XMPException
  {
    if (Utils.isXMLName(paramString)) {
      return;
    }
    throw new XMPException("Bad XML name", 102);
  }
  
  private static String verifyXPathRoot(String paramString1, String paramString2)
    throws XMPException
  {
    if (paramString1 == null) {}
    while (paramString1.length() == 0) {
      throw new XMPException("Schema namespace URI is required", 101);
    }
    if (paramString2.charAt(0) == '?') {}
    while (paramString2.charAt(0) == '@') {
      throw new XMPException("Top level name must not be a qualifier", 102);
    }
    if (paramString2.indexOf('/') >= 0) {}
    while (paramString2.indexOf('[') >= 0) {
      throw new XMPException("Top level name must be simple", 102);
    }
    String str = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(paramString1);
    if (str != null)
    {
      int i = paramString2.indexOf(':');
      if (i >= 0)
      {
        verifySimpleXMLName(paramString2.substring(0, i));
        verifySimpleXMLName(paramString2.substring(i));
        str = paramString2.substring(0, i + 1);
        paramString1 = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(paramString1);
        if (paramString1 == null) {
          break label195;
        }
        if (!str.equals(paramString1)) {
          break label207;
        }
        return paramString2;
      }
    }
    else
    {
      throw new XMPException("Unregistered schema namespace URI", 101);
    }
    verifySimpleXMLName(paramString2);
    return str + paramString2;
    label195:
    throw new XMPException("Unknown schema namespace prefix", 101);
    label207:
    throw new XMPException("Schema namespace URI and prefix mismatch", 101);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/xpath/XMPPathParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */