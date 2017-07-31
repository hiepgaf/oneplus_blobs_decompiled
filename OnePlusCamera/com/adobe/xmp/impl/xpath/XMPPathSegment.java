package com.adobe.xmp.impl.xpath;

public class XMPPathSegment
{
  private boolean alias;
  private int aliasForm;
  private int kind;
  private String name;
  
  public XMPPathSegment(String paramString)
  {
    this.name = paramString;
  }
  
  public XMPPathSegment(String paramString, int paramInt)
  {
    this.name = paramString;
    this.kind = paramInt;
  }
  
  public int getAliasForm()
  {
    return this.aliasForm;
  }
  
  public int getKind()
  {
    return this.kind;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public boolean isAlias()
  {
    return this.alias;
  }
  
  public void setAlias(boolean paramBoolean)
  {
    this.alias = paramBoolean;
  }
  
  public void setAliasForm(int paramInt)
  {
    this.aliasForm = paramInt;
  }
  
  public void setKind(int paramInt)
  {
    this.kind = paramInt;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public String toString()
  {
    switch (this.kind)
    {
    default: 
      return this.name;
    case 1: 
    case 2: 
    case 3: 
    case 4: 
      return this.name;
    }
    return this.name;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/xpath/XMPPathSegment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */