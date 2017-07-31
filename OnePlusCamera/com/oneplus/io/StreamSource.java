package com.oneplus.io;

import com.oneplus.base.Ref;
import java.io.IOException;
import java.io.InputStream;

public abstract interface StreamSource
{
  public abstract InputStream openInputStream(Ref<Boolean> paramRef)
    throws IOException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/StreamSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */