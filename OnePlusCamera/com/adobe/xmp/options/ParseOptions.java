package com.adobe.xmp.options;

public final class ParseOptions
  extends Options
{
  public static final int ACCEPT_LATIN_1 = 16;
  public static final int FIX_CONTROL_CHARS = 8;
  public static final int OMIT_NORMALIZATION = 32;
  public static final int REQUIRE_XMP_META = 1;
  public static final int STRICT_ALIASING = 4;
  
  public ParseOptions()
  {
    setOption(24, true);
  }
  
  protected String defineOptionName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      return "REQUIRE_XMP_META";
    case 4: 
      return "STRICT_ALIASING";
    case 8: 
      return "FIX_CONTROL_CHARS";
    case 16: 
      return "ACCEPT_LATIN_1";
    }
    return "OMIT_NORMALIZATION";
  }
  
  public boolean getAcceptLatin1()
  {
    return getOption(16);
  }
  
  public boolean getFixControlChars()
  {
    return getOption(8);
  }
  
  public boolean getOmitNormalization()
  {
    return getOption(32);
  }
  
  public boolean getRequireXMPMeta()
  {
    return getOption(1);
  }
  
  public boolean getStrictAliasing()
  {
    return getOption(4);
  }
  
  protected int getValidOptions()
  {
    return 61;
  }
  
  public ParseOptions setAcceptLatin1(boolean paramBoolean)
  {
    setOption(16, paramBoolean);
    return this;
  }
  
  public ParseOptions setFixControlChars(boolean paramBoolean)
  {
    setOption(8, paramBoolean);
    return this;
  }
  
  public ParseOptions setOmitNormalization(boolean paramBoolean)
  {
    setOption(32, paramBoolean);
    return this;
  }
  
  public ParseOptions setRequireXMPMeta(boolean paramBoolean)
  {
    setOption(1, paramBoolean);
    return this;
  }
  
  public ParseOptions setStrictAliasing(boolean paramBoolean)
  {
    setOption(4, paramBoolean);
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/options/ParseOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */