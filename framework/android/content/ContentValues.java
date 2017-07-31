package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public final class ContentValues
  implements Parcelable
{
  public static final Parcelable.Creator<ContentValues> CREATOR = new Parcelable.Creator()
  {
    public ContentValues createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ContentValues(paramAnonymousParcel.readHashMap(null), null);
    }
    
    public ContentValues[] newArray(int paramAnonymousInt)
    {
      return new ContentValues[paramAnonymousInt];
    }
  };
  public static final String TAG = "ContentValues";
  private HashMap<String, Object> mValues;
  
  public ContentValues()
  {
    this.mValues = new HashMap(8);
  }
  
  public ContentValues(int paramInt)
  {
    this.mValues = new HashMap(paramInt, 1.0F);
  }
  
  public ContentValues(ContentValues paramContentValues)
  {
    this.mValues = new HashMap(paramContentValues.mValues);
  }
  
  private ContentValues(HashMap<String, Object> paramHashMap)
  {
    this.mValues = paramHashMap;
  }
  
  public void clear()
  {
    this.mValues.clear();
  }
  
  public boolean containsKey(String paramString)
  {
    return this.mValues.containsKey(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ContentValues)) {
      return false;
    }
    return this.mValues.equals(((ContentValues)paramObject).mValues);
  }
  
  public Object get(String paramString)
  {
    return this.mValues.get(paramString);
  }
  
  public Boolean getAsBoolean(String paramString)
  {
    boolean bool = false;
    Object localObject = this.mValues.get(paramString);
    try
    {
      Boolean localBoolean = (Boolean)localObject;
      return localBoolean;
    }
    catch (ClassCastException localClassCastException)
    {
      if ((localObject instanceof CharSequence)) {
        return Boolean.valueOf(localObject.toString());
      }
      if ((localObject instanceof Number))
      {
        if (((Number)localObject).intValue() != 0) {
          bool = true;
        }
        return Boolean.valueOf(bool);
      }
      Log.e("ContentValues", "Cannot cast value for " + paramString + " to a Boolean: " + localObject, localClassCastException);
    }
    return null;
  }
  
  public Byte getAsByte(String paramString)
  {
    Object localObject = this.mValues.get(paramString);
    if (localObject != null) {}
    try
    {
      byte b = ((Number)localObject).byteValue();
      return Byte.valueOf(b);
    }
    catch (ClassCastException localClassCastException)
    {
      if (!(localObject instanceof CharSequence)) {
        break label87;
      }
      try
      {
        Byte localByte = Byte.valueOf(localObject.toString());
        return localByte;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("ContentValues", "Cannot parse Byte value for " + localObject + " at key " + paramString);
        return null;
      }
      label87:
      Log.e("ContentValues", "Cannot cast value for " + paramString + " to a Byte: " + localObject, localNumberFormatException);
    }
    return null;
    return null;
  }
  
  public byte[] getAsByteArray(String paramString)
  {
    paramString = this.mValues.get(paramString);
    if ((paramString instanceof byte[])) {
      return (byte[])paramString;
    }
    return null;
  }
  
  public Double getAsDouble(String paramString)
  {
    Object localObject = this.mValues.get(paramString);
    if (localObject != null) {}
    try
    {
      double d = ((Number)localObject).doubleValue();
      return Double.valueOf(d);
    }
    catch (ClassCastException localClassCastException)
    {
      if (!(localObject instanceof CharSequence)) {
        break label93;
      }
      try
      {
        Double localDouble = Double.valueOf(localObject.toString());
        return localDouble;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("ContentValues", "Cannot parse Double value for " + localObject + " at key " + paramString);
        return null;
      }
      label93:
      Log.e("ContentValues", "Cannot cast value for " + paramString + " to a Double: " + localObject, localNumberFormatException);
    }
    return null;
    return null;
  }
  
  public Float getAsFloat(String paramString)
  {
    Object localObject = this.mValues.get(paramString);
    if (localObject != null) {}
    try
    {
      float f = ((Number)localObject).floatValue();
      return Float.valueOf(f);
    }
    catch (ClassCastException localClassCastException)
    {
      if (!(localObject instanceof CharSequence)) {
        break label87;
      }
      try
      {
        Float localFloat = Float.valueOf(localObject.toString());
        return localFloat;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("ContentValues", "Cannot parse Float value for " + localObject + " at key " + paramString);
        return null;
      }
      label87:
      Log.e("ContentValues", "Cannot cast value for " + paramString + " to a Float: " + localObject, localNumberFormatException);
    }
    return null;
    return null;
  }
  
  public Integer getAsInteger(String paramString)
  {
    Object localObject = this.mValues.get(paramString);
    if (localObject != null) {}
    try
    {
      int i = ((Number)localObject).intValue();
      return Integer.valueOf(i);
    }
    catch (ClassCastException localClassCastException)
    {
      if (!(localObject instanceof CharSequence)) {
        break label87;
      }
      try
      {
        Integer localInteger = Integer.valueOf(localObject.toString());
        return localInteger;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("ContentValues", "Cannot parse Integer value for " + localObject + " at key " + paramString);
        return null;
      }
      label87:
      Log.e("ContentValues", "Cannot cast value for " + paramString + " to a Integer: " + localObject, localNumberFormatException);
    }
    return null;
    return null;
  }
  
  public Long getAsLong(String paramString)
  {
    Object localObject = this.mValues.get(paramString);
    if (localObject != null) {}
    try
    {
      long l = ((Number)localObject).longValue();
      return Long.valueOf(l);
    }
    catch (ClassCastException localClassCastException)
    {
      if (!(localObject instanceof CharSequence)) {
        break label93;
      }
      try
      {
        Long localLong = Long.valueOf(localObject.toString());
        return localLong;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("ContentValues", "Cannot parse Long value for " + localObject + " at key " + paramString);
        return null;
      }
      label93:
      Log.e("ContentValues", "Cannot cast value for " + paramString + " to a Long: " + localObject, localNumberFormatException);
    }
    return null;
    return null;
  }
  
  public Short getAsShort(String paramString)
  {
    Object localObject = this.mValues.get(paramString);
    if (localObject != null) {}
    try
    {
      short s = ((Number)localObject).shortValue();
      return Short.valueOf(s);
    }
    catch (ClassCastException localClassCastException)
    {
      if (!(localObject instanceof CharSequence)) {
        break label87;
      }
      try
      {
        Short localShort = Short.valueOf(localObject.toString());
        return localShort;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("ContentValues", "Cannot parse Short value for " + localObject + " at key " + paramString);
        return null;
      }
      label87:
      Log.e("ContentValues", "Cannot cast value for " + paramString + " to a Short: " + localObject, localNumberFormatException);
    }
    return null;
    return null;
  }
  
  public String getAsString(String paramString)
  {
    Object localObject1 = null;
    Object localObject2 = this.mValues.get(paramString);
    paramString = (String)localObject1;
    if (localObject2 != null) {
      paramString = localObject2.toString();
    }
    return paramString;
  }
  
  @Deprecated
  public ArrayList<String> getStringArrayList(String paramString)
  {
    return (ArrayList)this.mValues.get(paramString);
  }
  
  public int hashCode()
  {
    return this.mValues.hashCode();
  }
  
  public Set<String> keySet()
  {
    return this.mValues.keySet();
  }
  
  public void put(String paramString, Boolean paramBoolean)
  {
    this.mValues.put(paramString, paramBoolean);
  }
  
  public void put(String paramString, Byte paramByte)
  {
    this.mValues.put(paramString, paramByte);
  }
  
  public void put(String paramString, Double paramDouble)
  {
    this.mValues.put(paramString, paramDouble);
  }
  
  public void put(String paramString, Float paramFloat)
  {
    this.mValues.put(paramString, paramFloat);
  }
  
  public void put(String paramString, Integer paramInteger)
  {
    this.mValues.put(paramString, paramInteger);
  }
  
  public void put(String paramString, Long paramLong)
  {
    this.mValues.put(paramString, paramLong);
  }
  
  public void put(String paramString, Short paramShort)
  {
    this.mValues.put(paramString, paramShort);
  }
  
  public void put(String paramString1, String paramString2)
  {
    this.mValues.put(paramString1, paramString2);
  }
  
  public void put(String paramString, byte[] paramArrayOfByte)
  {
    this.mValues.put(paramString, paramArrayOfByte);
  }
  
  public void putAll(ContentValues paramContentValues)
  {
    this.mValues.putAll(paramContentValues.mValues);
  }
  
  public void putNull(String paramString)
  {
    this.mValues.put(paramString, null);
  }
  
  @Deprecated
  public void putStringArrayList(String paramString, ArrayList<String> paramArrayList)
  {
    this.mValues.put(paramString, paramArrayList);
  }
  
  public void remove(String paramString)
  {
    this.mValues.remove(paramString);
  }
  
  public int size()
  {
    return this.mValues.size();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.mValues.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = getAsString(str1);
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(" ");
      }
      localStringBuilder.append(str1).append("=").append(str2);
    }
    return localStringBuilder.toString();
  }
  
  public Set<Map.Entry<String, Object>> valueSet()
  {
    return this.mValues.entrySet();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeMap(this.mValues);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ContentValues.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */