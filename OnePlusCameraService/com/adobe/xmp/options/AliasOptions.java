package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;

public final class AliasOptions
  extends Options
{
  public static final int PROP_ARRAY = 512;
  public static final int PROP_ARRAY_ALTERNATE = 2048;
  public static final int PROP_ARRAY_ALT_TEXT = 4096;
  public static final int PROP_ARRAY_ORDERED = 1024;
  public static final int PROP_DIRECT = 0;
  
  public AliasOptions() {}
  
  public AliasOptions(int paramInt)
    throws XMPException
  {
    super(paramInt);
  }
  
  protected String defineOptionName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return "PROP_DIRECT";
    case 512: 
      return "ARRAY";
    case 1024: 
      return "ARRAY_ORDERED";
    case 2048: 
      return "ARRAY_ALTERNATE";
    }
    return "ARRAY_ALT_TEXT";
  }
  
  protected int getValidOptions()
  {
    return 7680;
  }
  
  public boolean isArray()
  {
    return getOption(512);
  }
  
  public boolean isArrayAltText()
  {
    return getOption(4096);
  }
  
  public boolean isArrayAlternate()
  {
    return getOption(2048);
  }
  
  public boolean isArrayOrdered()
  {
    return getOption(1024);
  }
  
  public boolean isSimple()
  {
    return getOptions() == 0;
  }
  
  public AliasOptions setArray(boolean paramBoolean)
  {
    setOption(512, paramBoolean);
    return this;
  }
  
  public AliasOptions setArrayAltText(boolean paramBoolean)
  {
    setOption(7680, paramBoolean);
    return this;
  }
  
  public AliasOptions setArrayAlternate(boolean paramBoolean)
  {
    setOption(3584, paramBoolean);
    return this;
  }
  
  public AliasOptions setArrayOrdered(boolean paramBoolean)
  {
    setOption(1536, paramBoolean);
    return this;
  }
  
  public PropertyOptions toPropertyOptions()
    throws XMPException
  {
    return new PropertyOptions(getOptions());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/options/AliasOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */