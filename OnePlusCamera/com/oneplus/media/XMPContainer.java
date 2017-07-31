package com.oneplus.media;

import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.properties.XMPProperty;

public abstract interface XMPContainer
{
  public abstract void clearXMPMeta();
  
  public abstract void deleteProperty(XMPPropertyKey paramXMPPropertyKey);
  
  public abstract XMPMeta getXMPMeta();
  
  public abstract XMPProperty getXMPProperty(XMPPropertyKey paramXMPPropertyKey);
  
  public abstract String registerXMPNamespace(String paramString1, String paramString2);
  
  public abstract void replaceXMPMeta(XMPMeta paramXMPMeta);
  
  public abstract void setXMPProperty(XMPPropertyKey paramXMPPropertyKey, Object paramObject);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/XMPContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */