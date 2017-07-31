package com.oneplus.gallery2.contentdetection;

import android.content.res.Resources;
import com.oneplus.base.BaseApplication;
import java.util.HashSet;
import java.util.Set;

public final class ObjectTypeCategory
{
  public static final ObjectTypeCategory ANIMALS = new ObjectTypeCategory(1000, "Animals");
  public static final ObjectTypeCategory BUILDINGS = new ObjectTypeCategory(2000, "Buildings");
  public static final ObjectTypeCategory FOODS = new ObjectTypeCategory(3000, "Foods");
  public static final ObjectTypeCategory LANDSCAPES = new ObjectTypeCategory(4000, "Landscapes");
  public static final ObjectTypeCategory NIGHT = new ObjectTypeCategory(5000, "Night");
  public static final ObjectTypeCategory PEOPLE = new ObjectTypeCategory(6000, "People");
  public final int id;
  private volatile int m_DisplayNameResId;
  private final String m_DisplayNameResName;
  private final Set<ObjectType> m_Types = new HashSet();
  public final String name;
  
  private ObjectTypeCategory(int paramInt, String paramString)
  {
    this.id = paramInt;
    this.name = paramString;
    this.m_DisplayNameResName = ("object_type_category_" + paramString.toLowerCase().replace(' ', '_'));
  }
  
  void addType(ObjectType paramObjectType)
  {
    this.m_Types.add(paramObjectType);
  }
  
  public boolean contains(ObjectType paramObjectType)
  {
    if (paramObjectType == null) {}
    while (!this.m_Types.contains(paramObjectType)) {
      return false;
    }
    return true;
  }
  
  public String getDisplayName()
  {
    BaseApplication localBaseApplication = BaseApplication.current();
    Resources localResources = localBaseApplication.getResources();
    if (this.m_DisplayNameResId > 0) {}
    while (this.m_DisplayNameResId <= 0)
    {
      return this.name;
      this.m_DisplayNameResId = localResources.getIdentifier(this.m_DisplayNameResName, "string", localBaseApplication.getPackageName());
    }
    return localResources.getString(this.id);
  }
  
  public String toString()
  {
    return this.name + " (" + this.id + ")";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/contentdetection/ObjectTypeCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */