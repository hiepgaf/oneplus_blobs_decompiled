package com.oneplus.media;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.properties.XMPProperty;
import com.oneplus.base.Log;

public class SimpleXMPContainer
  implements XMPContainer
{
  private static final String TAG = SimpleXMPContainer.class.getSimpleName();
  private XMPMeta m_XMPMeta;
  
  public SimpleXMPContainer(XMPMeta paramXMPMeta)
  {
    this.m_XMPMeta = paramXMPMeta;
    if (this.m_XMPMeta == null) {
      this.m_XMPMeta = XMPMetaFactory.create();
    }
  }
  
  public void clearXMPMeta()
  {
    this.m_XMPMeta = XMPMetaFactory.create();
  }
  
  public void deleteProperty(XMPPropertyKey paramXMPPropertyKey)
  {
    if (paramXMPPropertyKey == null) {
      return;
    }
    this.m_XMPMeta.deleteProperty(paramXMPPropertyKey.getSchemaNamespace(), paramXMPPropertyKey.getPropertyName());
  }
  
  public XMPMeta getXMPMeta()
  {
    return (XMPMeta)this.m_XMPMeta.clone();
  }
  
  public XMPProperty getXMPProperty(XMPPropertyKey paramXMPPropertyKey)
  {
    if (paramXMPPropertyKey == null) {
      return null;
    }
    try
    {
      paramXMPPropertyKey = this.m_XMPMeta.getProperty(paramXMPPropertyKey.getSchemaNamespace(), paramXMPPropertyKey.getPropertyName());
      return paramXMPPropertyKey;
    }
    catch (Throwable paramXMPPropertyKey)
    {
      Log.e(TAG, "getXMPMetaProperty() - Error to get property", paramXMPPropertyKey);
    }
    return null;
  }
  
  public String registerXMPNamespace(String paramString1, String paramString2)
  {
    XMPSchemaRegistry localXMPSchemaRegistry = XMPMetaFactory.getSchemaRegistry();
    localXMPSchemaRegistry.deleteNamespace(paramString1);
    String str = null;
    try
    {
      paramString2 = localXMPSchemaRegistry.registerNamespace(paramString1, paramString2);
      str = paramString2;
      Log.v(TAG, "registerXMPNamespace() - Namespace: ", paramString1, ", prefix: ", paramString2);
      return paramString2;
    }
    catch (Throwable paramString1)
    {
      Log.e(TAG, "registerXMPNamespace() - Error to register namespace", paramString1);
    }
    return str;
  }
  
  public void replaceXMPMeta(XMPMeta paramXMPMeta)
  {
    if (paramXMPMeta == null)
    {
      this.m_XMPMeta = null;
      return;
    }
    this.m_XMPMeta = ((XMPMeta)paramXMPMeta.clone());
  }
  
  public void setXMPProperty(XMPPropertyKey paramXMPPropertyKey, Object paramObject)
  {
    if (paramXMPPropertyKey == null) {
      return;
    }
    try
    {
      this.m_XMPMeta.setProperty(paramXMPPropertyKey.getSchemaNamespace(), paramXMPPropertyKey.getPropertyName(), paramObject);
      return;
    }
    catch (XMPException paramXMPPropertyKey)
    {
      Log.e(TAG, "setXMPMetaProperty() - Error to set property", paramXMPPropertyKey);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/SimpleXMPContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */