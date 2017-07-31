package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;

public class Utils
  implements XMPConst
{
  public static final int UUID_LENGTH = 36;
  public static final int UUID_SEGMENT_COUNT = 4;
  private static boolean[] xmlNameChars;
  private static boolean[] xmlNameStartChars;
  
  static {}
  
  static boolean checkUUIDFormat(String paramString)
  {
    int j;
    int k;
    int i;
    if (paramString != null)
    {
      j = 0;
      k = 0;
      i = 1;
      for (;;)
      {
        if (j >= paramString.length()) {
          break label82;
        }
        if (paramString.charAt(j) == '-') {
          break;
        }
        j += 1;
      }
    }
    return false;
    if (i == 0) {}
    for (;;)
    {
      i = 0;
      k += 1;
      break;
      if (j == 8) {}
      while ((j == 13) || (j == 18) || (j == 23))
      {
        i = 1;
        break;
      }
    }
    label82:
    if (i == 0) {}
    while ((4 != k) || (36 != j)) {
      return false;
    }
    return true;
  }
  
  public static String escapeXML(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 0;
    int i = 0;
    int k;
    if (i < paramString.length())
    {
      k = paramString.charAt(i);
      if (k != 60) {}
    }
    label30:
    for (i = 1;; i = 0)
    {
      StringBuffer localStringBuffer;
      label59:
      char c;
      if (i != 0)
      {
        localStringBuffer = new StringBuffer(paramString.length() * 4 / 3);
        i = j;
        if (i >= paramString.length()) {
          break label319;
        }
        c = paramString.charAt(i);
        if (paramBoolean2) {
          break label207;
        }
      }
      switch (c)
      {
      default: 
        localStringBuffer.append(c);
      case '<': 
      case '>': 
      case '&': 
        for (;;)
        {
          label131:
          i += 1;
          break label59;
          if ((k == 62) || (k == 38)) {
            break label30;
          }
          if (!paramBoolean2) {
            label158:
            if (paramBoolean1) {
              break label195;
            }
          }
          for (;;)
          {
            i += 1;
            break;
            if ((k == 9) || (k == 10)) {
              break label30;
            }
            if (k != 13) {
              break label158;
            }
            break label30;
            label195:
            if (k == 34) {
              break label30;
            }
          }
          return paramString;
          label207:
          if (c == '\t') {}
          for (;;)
          {
            localStringBuffer.append("&#x");
            localStringBuffer.append(Integer.toHexString(c).toUpperCase());
            localStringBuffer.append(';');
            break label131;
            if (c != '\n') {
              if (c != '\r') {
                break;
              }
            }
          }
          localStringBuffer.append("&lt;");
          continue;
          localStringBuffer.append("&gt;");
          continue;
          localStringBuffer.append("&amp;");
        }
      }
      if (!paramBoolean1) {}
      for (String str = "\"";; str = "&quot;")
      {
        localStringBuffer.append(str);
        break;
      }
      label319:
      return localStringBuffer.toString();
    }
  }
  
  private static void initCharTables()
  {
    xmlNameChars = new boolean['Ā'];
    xmlNameStartChars = new boolean['Ā'];
    int i = 0;
    if (i < xmlNameChars.length)
    {
      boolean[] arrayOfBoolean = xmlNameStartChars;
      label36:
      label42:
      label48:
      int j;
      if (97 > i)
      {
        if (65 <= i) {
          break label105;
        }
        if (i != 58) {
          break label114;
        }
        j = 1;
        label50:
        arrayOfBoolean[i] = j;
        arrayOfBoolean = xmlNameChars;
        if (97 <= i) {
          break label159;
        }
        label64:
        if (65 <= i) {
          break label168;
        }
        label70:
        if (48 <= i) {
          break label177;
        }
        label76:
        if (i != 58) {
          break label186;
        }
      }
      for (;;)
      {
        j = 1;
        label84:
        arrayOfBoolean[i] = j;
        i = (char)(i + 1);
        break;
        if (i > 122) {
          break label36;
        }
        break label48;
        label105:
        if (i > 90) {
          break label42;
        }
        break label48;
        label114:
        if (i == 95) {
          break label48;
        }
        if (192 > i) {
          label127:
          if (216 <= i) {
            break label149;
          }
        }
        label149:
        while (i > 246)
        {
          j = 0;
          break label50;
          if (i > 214) {
            break label127;
          }
          break;
        }
        break label48;
        label159:
        if (i > 122) {
          break label64;
        }
        continue;
        label168:
        if (i > 90) {
          break label70;
        }
        continue;
        label177:
        if (i > 57) {
          break label76;
        }
        continue;
        label186:
        if ((i != 95) && (i != 45) && (i != 46) && (i != 183))
        {
          if (192 > i) {
            label218:
            if (216 <= i) {
              break label240;
            }
          }
          label240:
          while (i > 246)
          {
            j = 0;
            break label84;
            if (i > 214) {
              break label218;
            }
            break;
          }
        }
      }
    }
  }
  
  static boolean isControlChar(char paramChar)
  {
    if (paramChar <= '\037') {
      if (paramChar != '\t') {
        break label23;
      }
    }
    label23:
    while ((paramChar == '\n') || (paramChar == '\r')) {
      for (;;)
      {
        return false;
        if (paramChar == '') {
          break;
        }
      }
    }
    return true;
  }
  
  static boolean isInternalProperty(String paramString1, String paramString2)
  {
    if (!"http://purl.org/dc/elements/1.1/".equals(paramString1))
    {
      if ("http://ns.adobe.com/xap/1.0/".equals(paramString1)) {
        break label159;
      }
      if ("http://ns.adobe.com/pdf/1.3/".equals(paramString1)) {
        break label217;
      }
      if ("http://ns.adobe.com/tiff/1.0/".equals(paramString1)) {
        break label266;
      }
      if ("http://ns.adobe.com/exif/1.0/".equals(paramString1)) {
        break label295;
      }
      if ("http://ns.adobe.com/exif/1.0/aux/".equals(paramString1)) {
        break label306;
      }
      if ("http://ns.adobe.com/photoshop/1.0/".equals(paramString1)) {
        break label308;
      }
      if ("http://ns.adobe.com/camera-raw-settings/1.0/".equals(paramString1)) {
        break label319;
      }
      if ("http://ns.adobe.com/StockPhoto/1.0/".equals(paramString1)) {
        break label350;
      }
      if ("http://ns.adobe.com/xap/1.0/mm/".equals(paramString1)) {
        break label352;
      }
      if ("http://ns.adobe.com/xap/1.0/t/".equals(paramString1)) {
        break label354;
      }
      if ("http://ns.adobe.com/xap/1.0/t/pg/".equals(paramString1)) {
        break label356;
      }
      if ("http://ns.adobe.com/xap/1.0/g/".equals(paramString1)) {
        break label358;
      }
      if ("http://ns.adobe.com/xap/1.0/g/img/".equals(paramString1)) {
        break label360;
      }
      if ("http://ns.adobe.com/xap/1.0/sType/Font#".equals(paramString1)) {
        break label362;
      }
    }
    label159:
    label217:
    label266:
    label295:
    label306:
    label308:
    while (!"photoshop:ICCProfile".equals(paramString2))
    {
      do
      {
        do
        {
          return false;
          if ("dc:format".equals(paramString2)) {}
          while ("dc:language".equals(paramString2)) {
            return true;
          }
          return false;
          if ("xmp:BaseURL".equals(paramString2)) {}
          while (("xmp:CreatorTool".equals(paramString2)) || ("xmp:Format".equals(paramString2)) || ("xmp:Locale".equals(paramString2)) || ("xmp:MetadataDate".equals(paramString2)) || ("xmp:ModifyDate".equals(paramString2))) {
            return true;
          }
          return false;
          if ("pdf:BaseURL".equals(paramString2)) {}
          while (("pdf:Creator".equals(paramString2)) || ("pdf:ModDate".equals(paramString2)) || ("pdf:PDFVersion".equals(paramString2)) || ("pdf:Producer".equals(paramString2))) {
            return true;
          }
          return false;
        } while (("tiff:ImageDescription".equals(paramString2)) || ("tiff:Artist".equals(paramString2)) || ("tiff:Copyright".equals(paramString2)));
        return true;
      } while ("exif:UserComment".equals(paramString2));
      return true;
      return true;
    }
    return true;
    label319:
    if ("crs:Version".equals(paramString2)) {}
    while (("crs:RawFileName".equals(paramString2)) || ("crs:ToneCurveName".equals(paramString2))) {
      return true;
    }
    return false;
    label350:
    return true;
    label352:
    return true;
    label354:
    return true;
    label356:
    return true;
    label358:
    return true;
    label360:
    return true;
    label362:
    return true;
  }
  
  private static boolean isNameChar(char paramChar)
  {
    if (paramChar > 'ÿ') {}
    while (xmlNameChars[paramChar] != 0) {
      return true;
    }
    return false;
  }
  
  private static boolean isNameStartChar(char paramChar)
  {
    if (paramChar > 'ÿ') {}
    while (xmlNameStartChars[paramChar] != 0) {
      return true;
    }
    return false;
  }
  
  public static boolean isXMLName(String paramString)
  {
    if (paramString.length() <= 0) {}
    while (isNameStartChar(paramString.charAt(0)))
    {
      int i = 1;
      for (;;)
      {
        if (i >= paramString.length()) {
          break label50;
        }
        if (!isNameChar(paramString.charAt(i))) {
          break;
        }
        i += 1;
      }
    }
    return false;
    return false;
    label50:
    return true;
  }
  
  public static boolean isXMLNameNS(String paramString)
  {
    int i;
    if (paramString.length() <= 0) {
      i = 1;
    }
    for (;;)
    {
      if (i >= paramString.length()) {
        break label73;
      }
      if (!isNameChar(paramString.charAt(i))) {}
      while (paramString.charAt(i) == ':')
      {
        return false;
        if (!isNameStartChar(paramString.charAt(0))) {}
        while (paramString.charAt(0) == ':') {
          return false;
        }
        break;
      }
      i += 1;
    }
    label73:
    return true;
  }
  
  public static String normalizeLangValue(String paramString)
  {
    StringBuffer localStringBuffer;
    int j;
    int i;
    int k;
    if (!"x-default".equals(paramString))
    {
      localStringBuffer = new StringBuffer();
      j = 1;
      i = 0;
      if (i >= paramString.length()) {
        break label138;
      }
      k = j;
      switch (paramString.charAt(i))
      {
      default: 
        if (j == 2)
        {
          localStringBuffer.append(Character.toUpperCase(paramString.charAt(i)));
          k = j;
        }
        break;
      }
    }
    for (;;)
    {
      i += 1;
      j = k;
      break;
      return paramString;
      localStringBuffer.append('-');
      k = j + 1;
      continue;
      localStringBuffer.append(Character.toLowerCase(paramString.charAt(i)));
      k = j;
    }
    label138:
    return localStringBuffer.toString();
  }
  
  static String removeControlChars(String paramString)
  {
    int i = 0;
    paramString = new StringBuffer(paramString);
    if (i < paramString.length())
    {
      if (!isControlChar(paramString.charAt(i))) {}
      for (;;)
      {
        i += 1;
        break;
        paramString.setCharAt(i, ' ');
      }
    }
    return paramString.toString();
  }
  
  static String[] splitNameAndValue(String paramString)
  {
    int j = paramString.indexOf('=');
    if (paramString.charAt(1) != '?') {}
    String str;
    StringBuffer localStringBuffer;
    for (int i = 1;; i = 2)
    {
      str = paramString.substring(i, j);
      i = j + 1;
      int k = paramString.charAt(i);
      i += 1;
      int m = paramString.length() - 2;
      localStringBuffer = new StringBuffer(m - j);
      while (i < m)
      {
        localStringBuffer.append(paramString.charAt(i));
        j = i + 1;
        i = j;
        if (paramString.charAt(j) == k) {
          i = j + 1;
        }
      }
    }
    return new String[] { str, localStringBuffer.toString() };
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */