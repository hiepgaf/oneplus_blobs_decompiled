package com.adobe.xmp.impl;

public class QName
{
  private String localName;
  private String prefix;
  
  public QName(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i < 0)
    {
      this.prefix = "";
      this.localName = paramString;
      return;
    }
    this.prefix = paramString.substring(0, i);
    this.localName = paramString.substring(i + 1);
  }
  
  public QName(String paramString1, String paramString2)
  {
    this.prefix = paramString1;
    this.localName = paramString2;
  }
  
  public String getLocalName()
  {
    return this.localName;
  }
  
  public String getPrefix()
  {
    return this.prefix;
  }
  
  public boolean hasPrefix()
  {
    if (this.prefix == null) {}
    while (this.prefix.length() <= 0) {
      return false;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/QName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */