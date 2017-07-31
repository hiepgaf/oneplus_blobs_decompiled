package com.adobe.xmp.properties;

import com.adobe.xmp.options.AliasOptions;

public abstract interface XMPAliasInfo
{
  public abstract AliasOptions getAliasForm();
  
  public abstract String getNamespace();
  
  public abstract String getPrefix();
  
  public abstract String getPropName();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/properties/XMPAliasInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */