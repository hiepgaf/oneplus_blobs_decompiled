package com.adobe.xmp;

import java.util.Iterator;

public abstract interface XMPIterator
  extends Iterator
{
  public abstract void skipSiblings();
  
  public abstract void skipSubtree();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */