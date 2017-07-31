package com.adobe.xmp;

import com.adobe.xmp.impl.Utils;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.impl.xpath.XMPPathSegment;

public final class XMPPathFactory
{
  private static void assertFieldNS(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty field namespace URI", 101);
    }
  }
  
  private static void assertFieldName(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty f name", 102);
    }
  }
  
  private static void assertQualNS(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty qualifier namespace URI", 101);
    }
  }
  
  private static void assertQualName(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty qualifier name", 102);
    }
  }
  
  public static String composeArrayItemPath(String paramString, int paramInt)
    throws XMPException
  {
    if (paramInt <= 0)
    {
      if (paramInt != -1) {
        throw new XMPException("Array index must be larger than zero", 104);
      }
    }
    else {
      return paramString + '[' + paramInt + ']';
    }
    return paramString + "[last()]";
  }
  
  public static String composeFieldSelector(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMPException
  {
    paramString2 = XMPPathParser.expandXPath(paramString2, paramString3);
    if (paramString2.size() == 2) {
      return paramString1 + '[' + paramString2.getSegment(1).getName() + "=\"" + paramString4 + "\"]";
    }
    throw new XMPException("The fieldName name must be simple", 102);
  }
  
  public static String composeLangSelector(String paramString1, String paramString2)
  {
    return paramString1 + "[?xml:lang=\"" + Utils.normalizeLangValue(paramString2) + "\"]";
  }
  
  public static String composeQualifierPath(String paramString1, String paramString2)
    throws XMPException
  {
    assertQualNS(paramString1);
    assertQualName(paramString2);
    paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
    if (paramString1.size() == 2) {
      return "/?" + paramString1.getSegment(1).getName();
    }
    throw new XMPException("The qualifier name must be simple", 102);
  }
  
  public static String composeStructFieldPath(String paramString1, String paramString2)
    throws XMPException
  {
    assertFieldNS(paramString1);
    assertFieldName(paramString2);
    paramString1 = XMPPathParser.expandXPath(paramString1, paramString2);
    if (paramString1.size() == 2) {
      return '/' + paramString1.getSegment(1).getName();
    }
    throw new XMPException("The field name must be simple", 102);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPPathFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */