package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;

public final class PropertyOptions
  extends Options
{
  public static final int ARRAY = 512;
  public static final int ARRAY_ALTERNATE = 2048;
  public static final int ARRAY_ALT_TEXT = 4096;
  public static final int ARRAY_ORDERED = 1024;
  public static final int DELETE_EXISTING = 536870912;
  public static final int HAS_LANGUAGE = 64;
  public static final int HAS_QUALIFIERS = 16;
  public static final int HAS_TYPE = 128;
  public static final int NO_OPTIONS = 0;
  public static final int QUALIFIER = 32;
  public static final int SCHEMA_NODE = Integer.MIN_VALUE;
  public static final int STRUCT = 256;
  public static final int URI = 2;
  
  public PropertyOptions() {}
  
  public PropertyOptions(int paramInt)
    throws XMPException
  {
    super(paramInt);
  }
  
  public void assertConsistency(int paramInt)
    throws XMPException
  {
    if ((paramInt & 0x100) <= 0) {}
    do
    {
      while ((paramInt & 0x2) <= 0)
      {
        return;
        if ((paramInt & 0x200) > 0) {
          throw new XMPException("IsStruct and IsArray options are mutually exclusive", 103);
        }
      }
    } while ((paramInt & 0x300) <= 0);
    throw new XMPException("Structs and arrays can't have \"value\" options", 103);
  }
  
  protected String defineOptionName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 2: 
      return "URI";
    case 16: 
      return "HAS_QUALIFIER";
    case 32: 
      return "QUALIFIER";
    case 64: 
      return "HAS_LANGUAGE";
    case 128: 
      return "HAS_TYPE";
    case 256: 
      return "STRUCT";
    case 512: 
      return "ARRAY";
    case 1024: 
      return "ARRAY_ORDERED";
    case 2048: 
      return "ARRAY_ALTERNATE";
    case 4096: 
      return "ARRAY_ALT_TEXT";
    }
    return "SCHEMA_NODE";
  }
  
  public boolean equalArrayTypes(PropertyOptions paramPropertyOptions)
  {
    if (isArray() != paramPropertyOptions.isArray()) {}
    while ((isArrayOrdered() != paramPropertyOptions.isArrayOrdered()) || (isArrayAlternate() != paramPropertyOptions.isArrayAlternate()) || (isArrayAltText() != paramPropertyOptions.isArrayAltText())) {
      return false;
    }
    return true;
  }
  
  public boolean getHasLanguage()
  {
    return getOption(64);
  }
  
  public boolean getHasQualifiers()
  {
    return getOption(16);
  }
  
  public boolean getHasType()
  {
    return getOption(128);
  }
  
  protected int getValidOptions()
  {
    return -2147475470;
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
  
  public boolean isCompositeProperty()
  {
    return (getOptions() & 0x300) > 0;
  }
  
  public boolean isOnlyArrayOptions()
  {
    return (getOptions() & 0xE1FF) == 0;
  }
  
  public boolean isQualifier()
  {
    return getOption(32);
  }
  
  public boolean isSchemaNode()
  {
    return getOption(Integer.MIN_VALUE);
  }
  
  public boolean isSimple()
  {
    return (getOptions() & 0x300) == 0;
  }
  
  public boolean isStruct()
  {
    return getOption(256);
  }
  
  public boolean isURI()
  {
    return getOption(2);
  }
  
  public void mergeWith(PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    if (paramPropertyOptions == null) {
      return;
    }
    setOptions(getOptions() | paramPropertyOptions.getOptions());
  }
  
  public PropertyOptions setArray(boolean paramBoolean)
  {
    setOption(512, paramBoolean);
    return this;
  }
  
  public PropertyOptions setArrayAltText(boolean paramBoolean)
  {
    setOption(4096, paramBoolean);
    return this;
  }
  
  public PropertyOptions setArrayAlternate(boolean paramBoolean)
  {
    setOption(2048, paramBoolean);
    return this;
  }
  
  public PropertyOptions setArrayOrdered(boolean paramBoolean)
  {
    setOption(1024, paramBoolean);
    return this;
  }
  
  public PropertyOptions setHasLanguage(boolean paramBoolean)
  {
    setOption(64, paramBoolean);
    return this;
  }
  
  public PropertyOptions setHasQualifiers(boolean paramBoolean)
  {
    setOption(16, paramBoolean);
    return this;
  }
  
  public PropertyOptions setHasType(boolean paramBoolean)
  {
    setOption(128, paramBoolean);
    return this;
  }
  
  public PropertyOptions setQualifier(boolean paramBoolean)
  {
    setOption(32, paramBoolean);
    return this;
  }
  
  public PropertyOptions setSchemaNode(boolean paramBoolean)
  {
    setOption(Integer.MIN_VALUE, paramBoolean);
    return this;
  }
  
  public PropertyOptions setStruct(boolean paramBoolean)
  {
    setOption(256, paramBoolean);
    return this;
  }
  
  public PropertyOptions setURI(boolean paramBoolean)
  {
    setOption(2, paramBoolean);
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/options/PropertyOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */