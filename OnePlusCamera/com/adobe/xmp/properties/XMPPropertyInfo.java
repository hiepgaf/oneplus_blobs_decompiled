package com.adobe.xmp.properties;

import com.adobe.xmp.options.PropertyOptions;

public abstract interface XMPPropertyInfo
  extends XMPProperty
{
  public abstract String getNamespace();
  
  public abstract PropertyOptions getOptions();
  
  public abstract String getPath();
  
  public abstract Object getValue();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/properties/XMPPropertyInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */