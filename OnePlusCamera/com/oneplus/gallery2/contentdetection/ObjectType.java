package com.oneplus.gallery2.contentdetection;

import android.content.res.Resources;
import android.util.SparseArray;
import com.oneplus.base.BaseApplication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ObjectType
{
  public static final ObjectType AIRPLANE = new ObjectType(1, "Airplane", new ObjectTypeCategory[0]);
  private static final SparseArray<ObjectType> ALL_TYPES = new SparseArray();
  public static final ObjectType BACKGROUND = new ObjectType(0, "Background", new ObjectTypeCategory[0]);
  public static final ObjectType BICYCLE = new ObjectType(2, "Bicycle", new ObjectTypeCategory[0]);
  public static final ObjectType BIRD = new ObjectType(3, "Bird", new ObjectTypeCategory[] { ObjectTypeCategory.ANIMALS });
  public static final ObjectType BOAT = new ObjectType(4, "Boat", new ObjectTypeCategory[0]);
  public static final ObjectType BOTTLE = new ObjectType(5, "Bottle", new ObjectTypeCategory[0]);
  public static final ObjectType BUS = new ObjectType(6, "Bus", new ObjectTypeCategory[0]);
  public static final ObjectType CAR = new ObjectType(7, "Car", new ObjectTypeCategory[0]);
  public static final ObjectType CAT = new ObjectType(8, "Cat", new ObjectTypeCategory[] { ObjectTypeCategory.ANIMALS });
  public static final ObjectType CHAIR = new ObjectType(9, "Chair", new ObjectTypeCategory[0]);
  public static final ObjectType COW = new ObjectType(10, "Cow", new ObjectTypeCategory[] { ObjectTypeCategory.ANIMALS });
  public static final ObjectType DINING_TABLE = new ObjectType(11, "Dining table", new ObjectTypeCategory[0]);
  public static final ObjectType DOG = new ObjectType(12, "Dog", new ObjectTypeCategory[] { ObjectTypeCategory.ANIMALS });
  public static final ObjectType HORSE = new ObjectType(13, "Horse", new ObjectTypeCategory[] { ObjectTypeCategory.ANIMALS });
  public static final ObjectType MOTORBIKE = new ObjectType(14, "Motorbike", new ObjectTypeCategory[0]);
  public static final ObjectType PERSON = new ObjectType(15, "Person", new ObjectTypeCategory[] { ObjectTypeCategory.PEOPLE });
  public static final ObjectType POTTED_PLANT = new ObjectType(16, "Potted plant", new ObjectTypeCategory[0]);
  public static final ObjectType SHEEP = new ObjectType(17, "Sheep", new ObjectTypeCategory[] { ObjectTypeCategory.ANIMALS });
  public static final ObjectType SOFA = new ObjectType(18, "Sofa", new ObjectTypeCategory[0]);
  public static final ObjectType TRAIN = new ObjectType(19, "Train", new ObjectTypeCategory[0]);
  public static final ObjectType TV = new ObjectType(20, "TV", new ObjectTypeCategory[0]);
  public final List<ObjectTypeCategory> categories;
  public final int id;
  private final String m_KeywordListResName;
  public final String name;
  
  private ObjectType(int paramInt, String paramString, ObjectTypeCategory... paramVarArgs)
  {
    this.categories = Arrays.asList(paramVarArgs);
    this.id = paramInt;
    this.name = paramString;
    this.m_KeywordListResName = ("object_type_keywords_" + paramString.toLowerCase().replace(' ', '_'));
    ALL_TYPES.put(paramInt, this);
  }
  
  public static ObjectType fromId(int paramInt)
  {
    return (ObjectType)ALL_TYPES.get(paramInt);
  }
  
  public static int getAll(List<ObjectType> paramList)
  {
    int i = 0;
    int j = ALL_TYPES.size() - 1;
    if (j >= 0)
    {
      ObjectType localObjectType = (ObjectType)ALL_TYPES.valueAt(j);
      if (localObjectType == null) {}
      for (;;)
      {
        j -= 1;
        break;
        paramList.add(localObjectType);
        i += 1;
      }
    }
    return i;
  }
  
  public List<String> getKeywords()
  {
    Object localObject = BaseApplication.current();
    Resources localResources = ((BaseApplication)localObject).getResources();
    localObject = ((BaseApplication)localObject).getPackageName();
    int i = localResources.getIdentifier(this.m_KeywordListResName, "array", (String)localObject);
    if (i <= 0) {
      return Arrays.asList(new String[] { this.name });
    }
    ArrayList localArrayList = new ArrayList();
    String[] arrayOfString1 = localResources.getStringArray(i);
    i = arrayOfString1.length;
    int j;
    int k;
    do
    {
      j = i - 1;
      if (j < 0) {
        break;
      }
      k = localResources.getIdentifier(arrayOfString1[j], "string", (String)localObject);
      i = j;
    } while (k <= 0);
    String[] arrayOfString2 = localResources.getString(k).split("\\,");
    for (i = arrayOfString2.length;; i = k)
    {
      k = i - 1;
      i = j;
      if (k < 0) {
        break;
      }
      localArrayList.add(arrayOfString2[k]);
    }
    return localArrayList;
  }
  
  public String toString()
  {
    return this.name + " (" + this.id + ")";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/contentdetection/ObjectType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */