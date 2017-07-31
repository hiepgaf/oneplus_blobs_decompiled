package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;
import java.util.HashMap;
import java.util.Map;

public abstract class Options
{
  private Map optionNames = null;
  private int options = 0;
  
  public Options() {}
  
  public Options(int paramInt)
    throws XMPException
  {
    assertOptionsValid(paramInt);
    setOptions(paramInt);
  }
  
  private void assertOptionsValid(int paramInt)
    throws XMPException
  {
    int i = (getValidOptions() ^ 0xFFFFFFFF) & paramInt;
    if (i != 0) {
      throw new XMPException("The option bit(s) 0x" + Integer.toHexString(i) + " are invalid!", 103);
    }
    assertConsistency(paramInt);
  }
  
  private String getOptionName(int paramInt)
  {
    Map localMap = procureOptionNames();
    Integer localInteger = new Integer(paramInt);
    String str = (String)localMap.get(localInteger);
    if (str != null) {
      return str;
    }
    str = defineOptionName(paramInt);
    if (str == null) {
      return "<option name not defined>";
    }
    localMap.put(localInteger, str);
    return str;
  }
  
  private Map procureOptionNames()
  {
    if (this.optionNames != null) {}
    for (;;)
    {
      return this.optionNames;
      this.optionNames = new HashMap();
    }
  }
  
  protected void assertConsistency(int paramInt)
    throws XMPException
  {}
  
  public void clear()
  {
    this.options = 0;
  }
  
  public boolean containsAllOptions(int paramInt)
  {
    return (getOptions() & paramInt) == paramInt;
  }
  
  public boolean containsOneOf(int paramInt)
  {
    return (getOptions() & paramInt) != 0;
  }
  
  protected abstract String defineOptionName(int paramInt);
  
  public boolean equals(Object paramObject)
  {
    return getOptions() == ((Options)paramObject).getOptions();
  }
  
  protected boolean getOption(int paramInt)
  {
    return (this.options & paramInt) != 0;
  }
  
  public int getOptions()
  {
    return this.options;
  }
  
  public String getOptionsString()
  {
    if (this.options == 0) {
      return "<none>";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = this.options;
    if (i != 0)
    {
      int j = i - 1 & i;
      localStringBuffer.append(getOptionName(i ^ j));
      if (j == 0) {}
      for (;;)
      {
        i = j;
        break;
        localStringBuffer.append(" | ");
      }
    }
    return localStringBuffer.toString();
  }
  
  protected abstract int getValidOptions();
  
  public int hashCode()
  {
    return getOptions();
  }
  
  public boolean isExactly(int paramInt)
  {
    return getOptions() == paramInt;
  }
  
  public void setOption(int paramInt, boolean paramBoolean)
  {
    if (!paramBoolean) {}
    for (paramInt = this.options & (paramInt ^ 0xFFFFFFFF);; paramInt = this.options | paramInt)
    {
      this.options = paramInt;
      return;
    }
  }
  
  public void setOptions(int paramInt)
    throws XMPException
  {
    assertOptionsValid(paramInt);
    this.options = paramInt;
  }
  
  public String toString()
  {
    return "0x" + Integer.toHexString(this.options);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/options/Options.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */