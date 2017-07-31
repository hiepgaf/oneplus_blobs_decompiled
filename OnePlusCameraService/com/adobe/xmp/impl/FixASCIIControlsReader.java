package com.adobe.xmp.impl;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public class FixASCIIControlsReader
  extends PushbackReader
{
  private static final int BUFFER_SIZE = 8;
  private static final int STATE_AMP = 1;
  private static final int STATE_DIG1 = 4;
  private static final int STATE_ERROR = 5;
  private static final int STATE_HASH = 2;
  private static final int STATE_HEX = 3;
  private static final int STATE_START = 0;
  private int control = 0;
  private int digits = 0;
  private int state = 0;
  
  public FixASCIIControlsReader(Reader paramReader)
  {
    super(paramReader, 8);
  }
  
  private char processChar(char paramChar)
  {
    switch (this.state)
    {
    default: 
      return paramChar;
    case 0: 
      if (paramChar != '&') {
        return paramChar;
      }
      this.state = 1;
      return paramChar;
    case 1: 
      if (paramChar != '#')
      {
        this.state = 5;
        return paramChar;
      }
      this.state = 2;
      return paramChar;
    case 2: 
      if (paramChar != 'x') {
        if ('0' <= paramChar) {
          break label117;
        }
      }
      while (paramChar > '9')
      {
        this.state = 5;
        return paramChar;
        this.control = 0;
        this.digits = 0;
        this.state = 3;
        return paramChar;
      }
      this.control = Character.digit(paramChar, 10);
      this.digits = 1;
      this.state = 4;
      return paramChar;
    case 4: 
      if ('0' > paramChar) {
        if (paramChar == ';') {
          break label220;
        }
      }
      while (!Utils.isControlChar((char)this.control))
      {
        this.state = 5;
        return paramChar;
        if (paramChar > '9') {
          break;
        }
        this.control = (this.control * 10 + Character.digit(paramChar, 10));
        this.digits += 1;
        if (this.digits > 5)
        {
          this.state = 5;
          return paramChar;
        }
        this.state = 4;
        return paramChar;
      }
      this.state = 0;
      return (char)this.control;
    case 3: 
      label117:
      label220:
      if ('0' > paramChar)
      {
        if ('a' <= paramChar) {
          break label322;
        }
        if ('A' <= paramChar) {
          break label331;
        }
        label260:
        if (paramChar == ';') {
          break label347;
        }
      }
      label322:
      label331:
      label340:
      label347:
      while (!Utils.isControlChar((char)this.control))
      {
        this.state = 5;
        return paramChar;
        if (paramChar > '9') {
          break;
        }
        do
        {
          for (;;)
          {
            this.control = (this.control * 16 + Character.digit(paramChar, 16));
            this.digits += 1;
            if (this.digits <= 4) {
              break label340;
            }
            this.state = 5;
            return paramChar;
            if (paramChar > 'f') {
              break;
            }
          }
        } while (paramChar <= 'F');
        break label260;
        this.state = 3;
        return paramChar;
      }
      this.state = 0;
      return (char)this.control;
    }
    this.state = 0;
    return paramChar;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    char[] arrayOfChar = new char[8];
    int j = 1;
    int m = 0;
    int n = 0;
    int k = paramInt1;
    paramInt1 = n;
    if (j == 0) {
      label26:
      if (m <= 0) {
        break label178;
      }
    }
    label91:
    label163:
    label178:
    while (j != 0)
    {
      return m;
      if (m >= paramInt2) {
        break label26;
      }
      if (super.read(arrayOfChar, paramInt1, 1) != 1) {}
      for (j = 0;; j = 1)
      {
        if (j != 0) {
          break label91;
        }
        if (paramInt1 <= 0) {
          break;
        }
        unread(arrayOfChar, 0, paramInt1);
        this.state = 5;
        j = 1;
        paramInt1 = 0;
        break;
      }
      int i = processChar(arrayOfChar[paramInt1]);
      if (this.state != 0)
      {
        if (this.state == 5) {
          break label163;
        }
        paramInt1 += 1;
        break;
      }
      if (!Utils.isControlChar(i)) {}
      for (paramInt1 = i;; paramInt1 = 32)
      {
        paramArrayOfChar[k] = ((char)paramInt1);
        m += 1;
        k += 1;
        paramInt1 = 0;
        break;
      }
      unread(arrayOfChar, 0, paramInt1 + 1);
      paramInt1 = 0;
      break;
    }
    return -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/FixASCIIControlsReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */