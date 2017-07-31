package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;

class ParseState
{
  private int pos = 0;
  private String str;
  
  public ParseState(String paramString)
  {
    this.str = paramString;
  }
  
  public char ch()
  {
    if (this.pos >= this.str.length()) {
      return '\000';
    }
    return this.str.charAt(this.pos);
  }
  
  public char ch(int paramInt)
  {
    if (paramInt >= this.str.length()) {
      return '\000';
    }
    return this.str.charAt(paramInt);
  }
  
  public int gatherInt(String paramString, int paramInt)
    throws XMPException
  {
    int i = ch(this.pos);
    int j = 0;
    int k = 0;
    for (;;)
    {
      if (48 > i) {}
      while (i > 57)
      {
        if (j != 0) {
          break;
        }
        throw new XMPException(paramString, 5);
      }
      k = k * 10 + (i - 48);
      j = 1;
      this.pos += 1;
      i = ch(this.pos);
    }
    if (k <= paramInt)
    {
      if (k >= 0) {
        return k;
      }
    }
    else {
      return paramInt;
    }
    return 0;
  }
  
  public boolean hasNext()
  {
    return this.pos < this.str.length();
  }
  
  public int length()
  {
    return this.str.length();
  }
  
  public int pos()
  {
    return this.pos;
  }
  
  public void skip()
  {
    this.pos += 1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/ParseState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */