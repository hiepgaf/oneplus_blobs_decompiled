package com.oneplus.media;

public class XMPPropertyKey
{
  private String m_Namespace;
  private String m_PropertyName;
  
  public XMPPropertyKey(String paramString1, String paramString2)
  {
    this.m_Namespace = paramString1;
    this.m_PropertyName = paramString2;
  }
  
  public String getPropertyName()
  {
    return this.m_PropertyName;
  }
  
  public String getSchemaNamespace()
  {
    return this.m_Namespace;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/XMPPropertyKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */