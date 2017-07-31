package com.oneplus.io;

public final class Path
{
  public static String combine(String... paramVarArgs)
  {
    if (paramVarArgs == null) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    if (i < paramVarArgs.length)
    {
      String str = paramVarArgs[i];
      if ((str != null) && (str.length() > 0))
      {
        if (localStringBuffer.length() <= 0) {
          break label89;
        }
        if ((localStringBuffer.charAt(localStringBuffer.length() - 1) != '/') && (str.charAt(0) != '/')) {
          localStringBuffer.append('/');
        }
      }
      for (;;)
      {
        localStringBuffer.append(str);
        i += 1;
        break;
        label89:
        if (str.charAt(0) != '/') {
          localStringBuffer.append('/');
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  public static String getDirectoryPath(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("No file or directory path.");
    }
    if (paramString.length() > 1) {}
    for (int i = paramString.lastIndexOf('/', paramString.length() - 2); i >= 0; i = -1) {
      return paramString.substring(0, i);
    }
    return "/";
  }
  
  public static String getExtension(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("No file path.");
    }
    int i = paramString.length() - 1;
    while (i >= 0)
    {
      if (paramString.charAt(i) == '.') {
        return paramString.substring(i);
      }
      i -= 1;
    }
    return "";
  }
  
  public static String getFileName(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("No file or directory path.");
    }
    int i = paramString.length() - 2;
    while (i > 0)
    {
      if (paramString.charAt(i) == '/') {
        return paramString.substring(i + 1);
      }
      i -= 1;
    }
    return paramString;
  }
  
  public static String getFileNameWithoutExtension(String paramString)
  {
    if ((paramString == null) || (paramString.length() < 1)) {
      throw new IllegalArgumentException("No file or directory path.");
    }
    int j = paramString.lastIndexOf("/");
    int k = paramString.lastIndexOf(".");
    int m = paramString.lastIndexOf(":");
    int i = j;
    if (j < 0) {
      i = 0;
    }
    j = i;
    if (m > i) {
      j = m;
    }
    if (k >= 0)
    {
      i = k;
      if (k > j) {}
    }
    else
    {
      i = paramString.length();
    }
    return paramString.substring(j + 1, i);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/Path.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */