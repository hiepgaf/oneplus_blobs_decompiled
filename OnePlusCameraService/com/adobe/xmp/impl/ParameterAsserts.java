package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;

class ParameterAsserts
  implements XMPConst
{
  public static void assertArrayName(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty array name", 4);
    }
  }
  
  public static void assertImplementation(XMPMeta paramXMPMeta)
    throws XMPException
  {
    if (paramXMPMeta != null)
    {
      if (!(paramXMPMeta instanceof XMPMetaImpl)) {}
    }
    else {
      throw new XMPException("Parameter must not be null", 4);
    }
    throw new XMPException("The XMPMeta-object is not compatible with this implementation", 4);
  }
  
  public static void assertNotNull(Object paramObject)
    throws XMPException
  {
    if (paramObject != null) {
      if ((paramObject instanceof String)) {
        break label23;
      }
    }
    label23:
    while (((String)paramObject).length() != 0)
    {
      return;
      throw new XMPException("Parameter must not be null", 4);
    }
    throw new XMPException("Parameter must not be null or empty", 4);
  }
  
  public static void assertPrefix(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty prefix", 4);
    }
  }
  
  public static void assertPropName(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty property name", 4);
    }
  }
  
  public static void assertSchemaNS(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty schema namespace URI", 4);
    }
  }
  
  public static void assertSpecificLang(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty specific language", 4);
    }
  }
  
  public static void assertStructName(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty array name", 4);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/ParameterAsserts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */