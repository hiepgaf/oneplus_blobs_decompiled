package com.oneplus.camera.ui;

import com.oneplus.base.BasicBaseObject;
import com.oneplus.base.PropertyKey;

public abstract class ListItem
  extends BasicBaseObject
{
  public static final PropertyKey<Boolean> PROP_IS_CHECKED = new PropertyKey("IsChecked", Boolean.class, ListItem.class, 0, null);
  public static final PropertyKey<Boolean> PROP_IS_ENABLED = new PropertyKey("IsEnabled", Boolean.class, ListItem.class, 2, Boolean.valueOf(true));
  public static final PropertyKey<CharSequence> PROP_SUBTITLE = new PropertyKey("SubTitle", CharSequence.class, ListItem.class, 0, null);
  public static final PropertyKey<CharSequence> PROP_SUMMARY = new PropertyKey("Summary", CharSequence.class, ListItem.class, 0, null);
  public static final PropertyKey<CharSequence> PROP_TITLE = new PropertyKey("Title", CharSequence.class, ListItem.class, 0, null);
  private Object m_Tag;
  
  public Object getTag()
  {
    return this.m_Tag;
  }
  
  public void setTag(Object paramObject)
  {
    this.m_Tag = paramObject;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ListItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */