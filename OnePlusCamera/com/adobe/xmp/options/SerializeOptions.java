package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;

public final class SerializeOptions
  extends Options
{
  public static final int ENCODE_UTF16BE = 2;
  public static final int ENCODE_UTF16LE = 3;
  public static final int ENCODE_UTF8 = 0;
  private static final int ENCODING_MASK = 3;
  public static final int EXACT_PACKET_LENGTH = 512;
  public static final int INCLUDE_THUMBNAIL_PAD = 256;
  private static final int LITTLEENDIAN_BIT = 1;
  public static final int OMIT_PACKET_WRAPPER = 16;
  public static final int READONLY_PACKET = 32;
  public static final int SORT = 4096;
  public static final int USE_COMPACT_FORMAT = 64;
  private static final int UTF16_BIT = 2;
  private int baseIndent = 0;
  private String indent = "  ";
  private String newline = "\n";
  private boolean omitVersionAttribute = false;
  private int padding = 2048;
  
  public SerializeOptions() {}
  
  public SerializeOptions(int paramInt)
    throws XMPException
  {
    super(paramInt);
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    try
    {
      SerializeOptions localSerializeOptions = new SerializeOptions(getOptions());
      localSerializeOptions.setBaseIndent(this.baseIndent);
      localSerializeOptions.setIndent(this.indent);
      localSerializeOptions.setNewline(this.newline);
      localSerializeOptions.setPadding(this.padding);
      return localSerializeOptions;
    }
    catch (XMPException localXMPException) {}
    return null;
  }
  
  protected String defineOptionName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 16: 
      return "OMIT_PACKET_WRAPPER";
    case 32: 
      return "READONLY_PACKET";
    case 64: 
      return "USE_COMPACT_FORMAT";
    case 256: 
      return "INCLUDE_THUMBNAIL_PAD";
    case 512: 
      return "EXACT_PACKET_LENGTH";
    }
    return "NORMALIZED";
  }
  
  public int getBaseIndent()
  {
    return this.baseIndent;
  }
  
  public boolean getEncodeUTF16BE()
  {
    return (getOptions() & 0x3) == 2;
  }
  
  public boolean getEncodeUTF16LE()
  {
    return (getOptions() & 0x3) == 3;
  }
  
  public String getEncoding()
  {
    if (!getEncodeUTF16BE())
    {
      if (!getEncodeUTF16LE()) {
        return "UTF-8";
      }
    }
    else {
      return "UTF-16BE";
    }
    return "UTF-16LE";
  }
  
  public boolean getExactPacketLength()
  {
    return getOption(512);
  }
  
  public boolean getIncludeThumbnailPad()
  {
    return getOption(256);
  }
  
  public String getIndent()
  {
    return this.indent;
  }
  
  public String getNewline()
  {
    return this.newline;
  }
  
  public boolean getOmitPacketWrapper()
  {
    return getOption(16);
  }
  
  public boolean getOmitVersionAttribute()
  {
    return this.omitVersionAttribute;
  }
  
  public int getPadding()
  {
    return this.padding;
  }
  
  public boolean getReadOnlyPacket()
  {
    return getOption(32);
  }
  
  public boolean getSort()
  {
    return getOption(4096);
  }
  
  public boolean getUseCompactFormat()
  {
    return getOption(64);
  }
  
  protected int getValidOptions()
  {
    return 4976;
  }
  
  public SerializeOptions setBaseIndent(int paramInt)
  {
    this.baseIndent = paramInt;
    return this;
  }
  
  public SerializeOptions setEncodeUTF16BE(boolean paramBoolean)
  {
    setOption(3, false);
    setOption(2, paramBoolean);
    return this;
  }
  
  public SerializeOptions setEncodeUTF16LE(boolean paramBoolean)
  {
    setOption(3, false);
    setOption(3, paramBoolean);
    return this;
  }
  
  public SerializeOptions setExactPacketLength(boolean paramBoolean)
  {
    setOption(512, paramBoolean);
    return this;
  }
  
  public SerializeOptions setIncludeThumbnailPad(boolean paramBoolean)
  {
    setOption(256, paramBoolean);
    return this;
  }
  
  public SerializeOptions setIndent(String paramString)
  {
    this.indent = paramString;
    return this;
  }
  
  public SerializeOptions setNewline(String paramString)
  {
    this.newline = paramString;
    return this;
  }
  
  public SerializeOptions setOmitPacketWrapper(boolean paramBoolean)
  {
    setOption(16, paramBoolean);
    return this;
  }
  
  public SerializeOptions setPadding(int paramInt)
  {
    this.padding = paramInt;
    return this;
  }
  
  public SerializeOptions setReadOnlyPacket(boolean paramBoolean)
  {
    setOption(32, paramBoolean);
    return this;
  }
  
  public SerializeOptions setSort(boolean paramBoolean)
  {
    setOption(4096, paramBoolean);
    return this;
  }
  
  public SerializeOptions setUseCompactFormat(boolean paramBoolean)
  {
    setOption(64, paramBoolean);
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/options/SerializeOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */