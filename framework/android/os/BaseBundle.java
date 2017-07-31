package android.os;

import android.util.ArrayMap;
import android.util.Log;
import android.util.MathUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class BaseBundle
{
  static final int BUNDLE_MAGIC = 1279544898;
  static final boolean DEBUG = false;
  static final int FLAG_DEFUSABLE = 1;
  private static final boolean LOG_DEFUSABLE = false;
  private static final String TAG = "Bundle";
  private static volatile boolean sShouldDefuse = false;
  private ClassLoader mClassLoader;
  int mFlags;
  ArrayMap<String, Object> mMap = null;
  Parcel mParcelledData = null;
  
  BaseBundle()
  {
    this((ClassLoader)null, 0);
  }
  
  BaseBundle(int paramInt)
  {
    this((ClassLoader)null, paramInt);
  }
  
  BaseBundle(BaseBundle paramBaseBundle)
  {
    if (paramBaseBundle.mParcelledData != null) {
      if (paramBaseBundle.isEmptyParcel())
      {
        this.mParcelledData = NoImagePreloadHolder.EMPTY_PARCEL;
        if (paramBaseBundle.mMap == null) {
          break label111;
        }
      }
    }
    label111:
    for (this.mMap = new ArrayMap(paramBaseBundle.mMap);; this.mMap = null)
    {
      this.mClassLoader = paramBaseBundle.mClassLoader;
      return;
      this.mParcelledData = Parcel.obtain();
      this.mParcelledData.appendFrom(paramBaseBundle.mParcelledData, 0, paramBaseBundle.mParcelledData.dataSize());
      this.mParcelledData.setDataPosition(0);
      break;
      this.mParcelledData = null;
      break;
    }
  }
  
  BaseBundle(Parcel paramParcel)
  {
    readFromParcelInner(paramParcel);
  }
  
  BaseBundle(Parcel paramParcel, int paramInt)
  {
    readFromParcelInner(paramParcel, paramInt);
  }
  
  BaseBundle(ClassLoader paramClassLoader)
  {
    this(paramClassLoader, 0);
  }
  
  BaseBundle(ClassLoader paramClassLoader, int paramInt)
  {
    if (paramInt > 0) {}
    for (Object localObject = new ArrayMap(paramInt);; localObject = new ArrayMap())
    {
      this.mMap = ((ArrayMap)localObject);
      localObject = paramClassLoader;
      if (paramClassLoader == null) {
        localObject = getClass().getClassLoader();
      }
      this.mClassLoader = ((ClassLoader)localObject);
      return;
    }
  }
  
  private void readFromParcelInner(Parcel paramParcel, int paramInt)
  {
    if (paramInt < 0) {
      throw new RuntimeException("Bad length in parcel: " + paramInt);
    }
    if (paramInt == 0)
    {
      this.mParcelledData = NoImagePreloadHolder.EMPTY_PARCEL;
      return;
    }
    int i = paramParcel.readInt();
    if (i != 1279544898) {
      throw new IllegalStateException("Bad magic number for Bundle: 0x" + Integer.toHexString(i));
    }
    i = paramParcel.dataPosition();
    paramParcel.setDataPosition(MathUtils.addOrThrow(i, paramInt));
    Parcel localParcel = Parcel.obtain();
    localParcel.setDataPosition(0);
    localParcel.appendFrom(paramParcel, i, paramInt);
    localParcel.setDataPosition(0);
    this.mParcelledData = localParcel;
  }
  
  public static void setShouldDefuse(boolean paramBoolean)
  {
    sShouldDefuse = paramBoolean;
  }
  
  public void clear()
  {
    unparcel();
    this.mMap.clear();
  }
  
  public boolean containsKey(String paramString)
  {
    unparcel();
    return this.mMap.containsKey(paramString);
  }
  
  public Object get(String paramString)
  {
    unparcel();
    return this.mMap.get(paramString);
  }
  
  public boolean getBoolean(String paramString)
  {
    unparcel();
    return getBoolean(paramString, false);
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return paramBoolean;
    }
    try
    {
      boolean bool = ((Boolean)localObject).booleanValue();
      return bool;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Boolean", Boolean.valueOf(paramBoolean), localClassCastException);
    }
    return paramBoolean;
  }
  
  public boolean[] getBooleanArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      boolean[] arrayOfBoolean = (boolean[])localObject;
      return arrayOfBoolean;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "byte[]", localClassCastException);
    }
    return null;
  }
  
  byte getByte(String paramString)
  {
    unparcel();
    return getByte(paramString, (byte)0).byteValue();
  }
  
  Byte getByte(String paramString, byte paramByte)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return Byte.valueOf(paramByte);
    }
    try
    {
      Byte localByte = (Byte)localObject;
      return localByte;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Byte", Byte.valueOf(paramByte), localClassCastException);
    }
    return Byte.valueOf(paramByte);
  }
  
  byte[] getByteArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      byte[] arrayOfByte = (byte[])localObject;
      return arrayOfByte;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "byte[]", localClassCastException);
    }
    return null;
  }
  
  char getChar(String paramString)
  {
    unparcel();
    return getChar(paramString, '\000');
  }
  
  char getChar(String paramString, char paramChar)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return paramChar;
    }
    try
    {
      char c = ((Character)localObject).charValue();
      return c;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Character", Character.valueOf(paramChar), localClassCastException);
    }
    return paramChar;
  }
  
  char[] getCharArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      char[] arrayOfChar = (char[])localObject;
      return arrayOfChar;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "char[]", localClassCastException);
    }
    return null;
  }
  
  CharSequence getCharSequence(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    try
    {
      CharSequence localCharSequence = (CharSequence)localObject;
      return localCharSequence;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "CharSequence", localClassCastException);
    }
    return null;
  }
  
  CharSequence getCharSequence(String paramString, CharSequence paramCharSequence)
  {
    paramString = getCharSequence(paramString);
    if (paramString == null) {
      return paramCharSequence;
    }
    return paramString;
  }
  
  CharSequence[] getCharSequenceArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      CharSequence[] arrayOfCharSequence = (CharSequence[])localObject;
      return arrayOfCharSequence;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "CharSequence[]", localClassCastException);
    }
    return null;
  }
  
  ArrayList<CharSequence> getCharSequenceArrayList(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      ArrayList localArrayList = (ArrayList)localObject;
      return localArrayList;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "ArrayList<CharSequence>", localClassCastException);
    }
    return null;
  }
  
  ClassLoader getClassLoader()
  {
    return this.mClassLoader;
  }
  
  public double getDouble(String paramString)
  {
    unparcel();
    return getDouble(paramString, 0.0D);
  }
  
  public double getDouble(String paramString, double paramDouble)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return paramDouble;
    }
    try
    {
      double d = ((Double)localObject).doubleValue();
      return d;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Double", Double.valueOf(paramDouble), localClassCastException);
    }
    return paramDouble;
  }
  
  public double[] getDoubleArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      double[] arrayOfDouble = (double[])localObject;
      return arrayOfDouble;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "double[]", localClassCastException);
    }
    return null;
  }
  
  float getFloat(String paramString)
  {
    unparcel();
    return getFloat(paramString, 0.0F);
  }
  
  float getFloat(String paramString, float paramFloat)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return paramFloat;
    }
    try
    {
      float f = ((Float)localObject).floatValue();
      return f;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Float", Float.valueOf(paramFloat), localClassCastException);
    }
    return paramFloat;
  }
  
  float[] getFloatArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      float[] arrayOfFloat = (float[])localObject;
      return arrayOfFloat;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "float[]", localClassCastException);
    }
    return null;
  }
  
  public int getInt(String paramString)
  {
    unparcel();
    return getInt(paramString, 0);
  }
  
  public int getInt(String paramString, int paramInt)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return paramInt;
    }
    try
    {
      int i = ((Integer)localObject).intValue();
      return i;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Integer", Integer.valueOf(paramInt), localClassCastException);
    }
    return paramInt;
  }
  
  public int[] getIntArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      int[] arrayOfInt = (int[])localObject;
      return arrayOfInt;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "int[]", localClassCastException);
    }
    return null;
  }
  
  ArrayList<Integer> getIntegerArrayList(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      ArrayList localArrayList = (ArrayList)localObject;
      return localArrayList;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "ArrayList<Integer>", localClassCastException);
    }
    return null;
  }
  
  public long getLong(String paramString)
  {
    unparcel();
    return getLong(paramString, 0L);
  }
  
  public long getLong(String paramString, long paramLong)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return paramLong;
    }
    try
    {
      long l = ((Long)localObject).longValue();
      return l;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Long", Long.valueOf(paramLong), localClassCastException);
    }
    return paramLong;
  }
  
  public long[] getLongArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      long[] arrayOfLong = (long[])localObject;
      return arrayOfLong;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "long[]", localClassCastException);
    }
    return null;
  }
  
  ArrayMap<String, Object> getMap()
  {
    unparcel();
    return this.mMap;
  }
  
  public String getPairValue()
  {
    unparcel();
    int i = this.mMap.size();
    if (i > 1) {
      Log.w("Bundle", "getPairValue() used on Bundle with multiple pairs.");
    }
    if (i == 0) {
      return null;
    }
    Object localObject = this.mMap.valueAt(0);
    try
    {
      String str = (String)localObject;
      return str;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning("getPairValue()", localObject, "String", localClassCastException);
    }
    return null;
  }
  
  Serializable getSerializable(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      Serializable localSerializable = (Serializable)localObject;
      return localSerializable;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Serializable", localClassCastException);
    }
    return null;
  }
  
  short getShort(String paramString)
  {
    unparcel();
    return getShort(paramString, (short)0);
  }
  
  short getShort(String paramString, short paramShort)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return paramShort;
    }
    try
    {
      short s = ((Short)localObject).shortValue();
      return s;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "Short", Short.valueOf(paramShort), localClassCastException);
    }
    return paramShort;
  }
  
  short[] getShortArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      short[] arrayOfShort = (short[])localObject;
      return arrayOfShort;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "short[]", localClassCastException);
    }
    return null;
  }
  
  public String getString(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    try
    {
      String str = (String)localObject;
      return str;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "String", localClassCastException);
    }
    return null;
  }
  
  public String getString(String paramString1, String paramString2)
  {
    paramString1 = getString(paramString1);
    if (paramString1 == null) {
      return paramString2;
    }
    return paramString1;
  }
  
  public String[] getStringArray(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      String[] arrayOfString = (String[])localObject;
      return arrayOfString;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "String[]", localClassCastException);
    }
    return null;
  }
  
  ArrayList<String> getStringArrayList(String paramString)
  {
    unparcel();
    Object localObject = this.mMap.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      ArrayList localArrayList = (ArrayList)localObject;
      return localArrayList;
    }
    catch (ClassCastException localClassCastException)
    {
      typeWarning(paramString, localObject, "ArrayList<String>", localClassCastException);
    }
    return null;
  }
  
  public boolean isEmpty()
  {
    unparcel();
    return this.mMap.isEmpty();
  }
  
  public boolean isEmptyParcel()
  {
    return this.mParcelledData == NoImagePreloadHolder.EMPTY_PARCEL;
  }
  
  public boolean isParcelled()
  {
    return this.mParcelledData != null;
  }
  
  public Set<String> keySet()
  {
    unparcel();
    return this.mMap.keySet();
  }
  
  public void putAll(PersistableBundle paramPersistableBundle)
  {
    unparcel();
    paramPersistableBundle.unparcel();
    this.mMap.putAll(paramPersistableBundle.mMap);
  }
  
  void putAll(ArrayMap paramArrayMap)
  {
    unparcel();
    this.mMap.putAll(paramArrayMap);
  }
  
  public void putBoolean(String paramString, boolean paramBoolean)
  {
    unparcel();
    this.mMap.put(paramString, Boolean.valueOf(paramBoolean));
  }
  
  public void putBooleanArray(String paramString, boolean[] paramArrayOfBoolean)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfBoolean);
  }
  
  void putByte(String paramString, byte paramByte)
  {
    unparcel();
    this.mMap.put(paramString, Byte.valueOf(paramByte));
  }
  
  void putByteArray(String paramString, byte[] paramArrayOfByte)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfByte);
  }
  
  void putChar(String paramString, char paramChar)
  {
    unparcel();
    this.mMap.put(paramString, Character.valueOf(paramChar));
  }
  
  void putCharArray(String paramString, char[] paramArrayOfChar)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfChar);
  }
  
  void putCharSequence(String paramString, CharSequence paramCharSequence)
  {
    unparcel();
    this.mMap.put(paramString, paramCharSequence);
  }
  
  void putCharSequenceArray(String paramString, CharSequence[] paramArrayOfCharSequence)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfCharSequence);
  }
  
  void putCharSequenceArrayList(String paramString, ArrayList<CharSequence> paramArrayList)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayList);
  }
  
  public void putDouble(String paramString, double paramDouble)
  {
    unparcel();
    this.mMap.put(paramString, Double.valueOf(paramDouble));
  }
  
  public void putDoubleArray(String paramString, double[] paramArrayOfDouble)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfDouble);
  }
  
  void putFloat(String paramString, float paramFloat)
  {
    unparcel();
    this.mMap.put(paramString, Float.valueOf(paramFloat));
  }
  
  void putFloatArray(String paramString, float[] paramArrayOfFloat)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfFloat);
  }
  
  public void putInt(String paramString, int paramInt)
  {
    unparcel();
    this.mMap.put(paramString, Integer.valueOf(paramInt));
  }
  
  public void putIntArray(String paramString, int[] paramArrayOfInt)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfInt);
  }
  
  void putIntegerArrayList(String paramString, ArrayList<Integer> paramArrayList)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayList);
  }
  
  public void putLong(String paramString, long paramLong)
  {
    unparcel();
    this.mMap.put(paramString, Long.valueOf(paramLong));
  }
  
  public void putLongArray(String paramString, long[] paramArrayOfLong)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfLong);
  }
  
  void putSerializable(String paramString, Serializable paramSerializable)
  {
    unparcel();
    this.mMap.put(paramString, paramSerializable);
  }
  
  void putShort(String paramString, short paramShort)
  {
    unparcel();
    this.mMap.put(paramString, Short.valueOf(paramShort));
  }
  
  void putShortArray(String paramString, short[] paramArrayOfShort)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfShort);
  }
  
  public void putString(String paramString1, String paramString2)
  {
    unparcel();
    this.mMap.put(paramString1, paramString2);
  }
  
  public void putStringArray(String paramString, String[] paramArrayOfString)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayOfString);
  }
  
  void putStringArrayList(String paramString, ArrayList<String> paramArrayList)
  {
    unparcel();
    this.mMap.put(paramString, paramArrayList);
  }
  
  void readFromParcelInner(Parcel paramParcel)
  {
    readFromParcelInner(paramParcel, paramParcel.readInt());
  }
  
  public void remove(String paramString)
  {
    unparcel();
    this.mMap.remove(paramString);
  }
  
  void setClassLoader(ClassLoader paramClassLoader)
  {
    this.mClassLoader = paramClassLoader;
  }
  
  public int size()
  {
    unparcel();
    return this.mMap.size();
  }
  
  void typeWarning(String paramString1, Object paramObject, String paramString2, ClassCastException paramClassCastException)
  {
    typeWarning(paramString1, paramObject, paramString2, "<null>", paramClassCastException);
  }
  
  void typeWarning(String paramString1, Object paramObject1, String paramString2, Object paramObject2, ClassCastException paramClassCastException)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Key ");
    localStringBuilder.append(paramString1);
    localStringBuilder.append(" expected ");
    localStringBuilder.append(paramString2);
    localStringBuilder.append(" but value was a ");
    localStringBuilder.append(paramObject1.getClass().getName());
    localStringBuilder.append(".  The default value ");
    localStringBuilder.append(paramObject2);
    localStringBuilder.append(" was returned.");
    Log.w("Bundle", localStringBuilder.toString());
    Log.w("Bundle", "Attempt to cast generated internal exception:", paramClassCastException);
  }
  
  void unparcel()
  {
    try
    {
      try
      {
        localParcel = this.mParcelledData;
        if (localParcel == null) {
          return;
        }
        if (isEmptyParcel())
        {
          if (this.mMap == null) {
            this.mMap = new ArrayMap(1);
          }
          for (;;)
          {
            this.mParcelledData = null;
            return;
            this.mMap.erase();
          }
          localObject2 = finally;
        }
      }
      finally {}
      i = localParcel.readInt();
    }
    finally {}
    int i;
    if (i < 0) {
      return;
    }
    localArrayMap = this.mMap;
    if (localArrayMap == null) {
      localArrayMap = new ArrayMap(i);
    }
    for (;;)
    {
      try
      {
        localParcel.readArrayMapInternal(localArrayMap, i, this.mClassLoader);
      }
      catch (BadParcelableException localBadParcelableException)
      {
        if (!sShouldDefuse) {
          continue;
        }
        Log.w("Bundle", "Failed to parse Bundle, but defusing quietly", localBadParcelableException);
        localArrayMap.erase();
        this.mMap = localArrayMap;
        localParcel.recycle();
        this.mParcelledData = null;
        continue;
        throw localBadParcelableException;
      }
      finally
      {
        this.mMap = localArrayMap;
        localParcel.recycle();
        this.mParcelledData = null;
      }
      return;
      localArrayMap.erase();
      localArrayMap.ensureCapacity(i);
    }
  }
  
  void writeToParcelInner(Parcel paramParcel, int paramInt)
  {
    Parcel localParcel;
    try
    {
      localParcel = this.mParcelledData;
      if (localParcel == null) {
        break label59;
      }
      if (isEmptyParcel())
      {
        paramParcel.writeInt(0);
        return;
      }
    }
    finally {}
    paramInt = localParcel.dataSize();
    paramParcel.writeInt(paramInt);
    paramParcel.writeInt(1279544898);
    paramParcel.appendFrom(localParcel, 0, paramInt);
    return;
    label59:
    if ((this.mMap == null) || (this.mMap.size() <= 0))
    {
      paramParcel.writeInt(0);
      return;
    }
    paramInt = paramParcel.dataPosition();
    paramParcel.writeInt(-1);
    paramParcel.writeInt(1279544898);
    int i = paramParcel.dataPosition();
    paramParcel.writeArrayMapInternal(this.mMap);
    int j = paramParcel.dataPosition();
    paramParcel.setDataPosition(paramInt);
    paramParcel.writeInt(j - i);
    paramParcel.setDataPosition(j);
  }
  
  static final class NoImagePreloadHolder
  {
    public static final Parcel EMPTY_PARCEL = ;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/BaseBundle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */