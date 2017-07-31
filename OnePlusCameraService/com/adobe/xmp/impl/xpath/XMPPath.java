package com.adobe.xmp.impl.xpath;

import java.util.ArrayList;
import java.util.List;

public class XMPPath
{
  public static final int ARRAY_INDEX_STEP = 3;
  public static final int ARRAY_LAST_STEP = 4;
  public static final int FIELD_SELECTOR_STEP = 6;
  public static final int QUALIFIER_STEP = 2;
  public static final int QUAL_SELECTOR_STEP = 5;
  public static final int SCHEMA_NODE = Integer.MIN_VALUE;
  public static final int STEP_ROOT_PROP = 1;
  public static final int STEP_SCHEMA = 0;
  public static final int STRUCT_FIELD_STEP = 1;
  private List segments = new ArrayList(5);
  
  public void add(XMPPathSegment paramXMPPathSegment)
  {
    this.segments.add(paramXMPPathSegment);
  }
  
  public XMPPathSegment getSegment(int paramInt)
  {
    return (XMPPathSegment)this.segments.get(paramInt);
  }
  
  public int size()
  {
    return this.segments.size();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 1;
    if (i < size())
    {
      localStringBuffer.append(getSegment(i));
      if (i >= size() - 1) {}
      for (;;)
      {
        i += 1;
        break;
        int j = getSegment(i + 1).getKind();
        if (j == 1) {}
        while (j == 2)
        {
          localStringBuffer.append('/');
          break;
        }
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/impl/xpath/XMPPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */