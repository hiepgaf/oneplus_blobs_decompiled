package com.adobe.xmp;

import com.adobe.xmp.impl.Base64;
import com.adobe.xmp.impl.ISO8601Converter;
import com.adobe.xmp.impl.XMPUtilsImpl;
import com.adobe.xmp.options.PropertyOptions;

public class XMPUtils
{
  public static void appendProperties(XMPMeta paramXMPMeta1, XMPMeta paramXMPMeta2, boolean paramBoolean1, boolean paramBoolean2)
    throws XMPException
  {
    appendProperties(paramXMPMeta1, paramXMPMeta2, paramBoolean1, paramBoolean2, false);
  }
  
  public static void appendProperties(XMPMeta paramXMPMeta1, XMPMeta paramXMPMeta2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws XMPException
  {
    XMPUtilsImpl.appendProperties(paramXMPMeta1, paramXMPMeta2, paramBoolean1, paramBoolean2, paramBoolean3);
  }
  
  public static String catenateArrayItems(XMPMeta paramXMPMeta, String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean)
    throws XMPException
  {
    return XMPUtilsImpl.catenateArrayItems(paramXMPMeta, paramString1, paramString2, paramString3, paramString4, paramBoolean);
  }
  
  public static String convertFromBoolean(boolean paramBoolean)
  {
    if (!paramBoolean) {
      return "False";
    }
    return "True";
  }
  
  public static String convertFromDate(XMPDateTime paramXMPDateTime)
  {
    return ISO8601Converter.render(paramXMPDateTime);
  }
  
  public static String convertFromDouble(double paramDouble)
  {
    return String.valueOf(paramDouble);
  }
  
  public static String convertFromInteger(int paramInt)
  {
    return String.valueOf(paramInt);
  }
  
  public static String convertFromLong(long paramLong)
  {
    return String.valueOf(paramLong);
  }
  
  public static boolean convertToBoolean(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty convert-string", 5);
    }
    paramString = paramString.toLowerCase();
    try
    {
      int i = Integer.parseInt(paramString);
      return i != 0;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      if ("true".equals(paramString)) {}
      while (("t".equals(paramString)) || ("on".equals(paramString)) || ("yes".equals(paramString))) {
        return true;
      }
    }
    return false;
  }
  
  public static XMPDateTime convertToDate(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      throw new XMPException("Empty convert-string", 5);
    }
    return ISO8601Converter.parse(paramString);
  }
  
  public static double convertToDouble(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      try
      {
        throw new XMPException("Empty convert-string", 5);
      }
      catch (NumberFormatException paramString)
      {
        throw new XMPException("Invalid double string", 5);
      }
    }
    double d = Double.parseDouble(paramString);
    return d;
  }
  
  public static int convertToInteger(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      try
      {
        throw new XMPException("Empty convert-string", 5);
      }
      catch (NumberFormatException paramString)
      {
        throw new XMPException("Invalid integer string", 5);
      }
    }
    if (!paramString.startsWith("0x")) {
      return Integer.parseInt(paramString);
    }
    int i = Integer.parseInt(paramString.substring(2), 16);
    return i;
  }
  
  public static long convertToLong(String paramString)
    throws XMPException
  {
    if (paramString == null) {}
    while (paramString.length() == 0) {
      try
      {
        throw new XMPException("Empty convert-string", 5);
      }
      catch (NumberFormatException paramString)
      {
        throw new XMPException("Invalid long string", 5);
      }
    }
    if (!paramString.startsWith("0x")) {
      return Long.parseLong(paramString);
    }
    long l = Long.parseLong(paramString.substring(2), 16);
    return l;
  }
  
  public static byte[] decodeBase64(String paramString)
    throws XMPException
  {
    try
    {
      paramString = Base64.decode(paramString.getBytes());
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw new XMPException("Invalid base64 string", 5, paramString);
    }
  }
  
  public static String encodeBase64(byte[] paramArrayOfByte)
  {
    return new String(Base64.encode(paramArrayOfByte));
  }
  
  public static void removeProperties(XMPMeta paramXMPMeta, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
    throws XMPException
  {
    XMPUtilsImpl.removeProperties(paramXMPMeta, paramString1, paramString2, paramBoolean1, paramBoolean2);
  }
  
  public static void separateArrayItems(XMPMeta paramXMPMeta, String paramString1, String paramString2, String paramString3, PropertyOptions paramPropertyOptions, boolean paramBoolean)
    throws XMPException
  {
    XMPUtilsImpl.separateArrayItems(paramXMPMeta, paramString1, paramString2, paramString3, paramPropertyOptions, paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */