package com.adobe.xmp.options;

public final class IteratorOptions
  extends Options
{
  public static final int INCLUDE_ALIASES = 2048;
  public static final int JUST_CHILDREN = 256;
  public static final int JUST_LEAFNAME = 1024;
  public static final int JUST_LEAFNODES = 512;
  public static final int OMIT_QUALIFIERS = 4096;
  
  protected String defineOptionName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 256: 
      return "JUST_CHILDREN";
    case 512: 
      return "JUST_LEAFNODES";
    case 1024: 
      return "JUST_LEAFNAME";
    }
    return "OMIT_QUALIFIERS";
  }
  
  protected int getValidOptions()
  {
    return 5888;
  }
  
  public boolean isJustChildren()
  {
    return getOption(256);
  }
  
  public boolean isJustLeafname()
  {
    return getOption(1024);
  }
  
  public boolean isJustLeafnodes()
  {
    return getOption(512);
  }
  
  public boolean isOmitQualifiers()
  {
    return getOption(4096);
  }
  
  public IteratorOptions setJustChildren(boolean paramBoolean)
  {
    setOption(256, paramBoolean);
    return this;
  }
  
  public IteratorOptions setJustLeafname(boolean paramBoolean)
  {
    setOption(1024, paramBoolean);
    return this;
  }
  
  public IteratorOptions setJustLeafnodes(boolean paramBoolean)
  {
    setOption(512, paramBoolean);
    return this;
  }
  
  public IteratorOptions setOmitQualifiers(boolean paramBoolean)
  {
    setOption(4096, paramBoolean);
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/options/IteratorOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */