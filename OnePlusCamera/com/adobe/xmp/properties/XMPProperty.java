package com.adobe.xmp.properties;

import com.adobe.xmp.options.PropertyOptions;

public abstract interface XMPProperty
{
  public abstract String getLanguage();
  
  public abstract PropertyOptions getOptions();
  
  public abstract Object getValue();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/properties/XMPProperty.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */