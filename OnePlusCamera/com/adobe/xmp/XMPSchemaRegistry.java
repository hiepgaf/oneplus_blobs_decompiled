package com.adobe.xmp;

import com.adobe.xmp.properties.XMPAliasInfo;
import java.util.Map;

public abstract interface XMPSchemaRegistry
{
  public abstract void deleteNamespace(String paramString);
  
  public abstract XMPAliasInfo findAlias(String paramString);
  
  public abstract XMPAliasInfo[] findAliases(String paramString);
  
  public abstract Map getAliases();
  
  public abstract String getNamespacePrefix(String paramString);
  
  public abstract String getNamespaceURI(String paramString);
  
  public abstract Map getNamespaces();
  
  public abstract Map getPrefixes();
  
  public abstract String registerNamespace(String paramString1, String paramString2)
    throws XMPException;
  
  public abstract XMPAliasInfo resolveAlias(String paramString1, String paramString2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPSchemaRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */