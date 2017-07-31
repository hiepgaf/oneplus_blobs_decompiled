package android.content.res;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Pools.SynchronizedPool;
import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import java.util.Arrays;

public class TypedArray
{
  private AssetManager mAssets;
  int[] mData;
  int[] mIndices;
  int mLength;
  private final DisplayMetrics mMetrics;
  private boolean mRecycled;
  private final Resources mResources;
  Resources.Theme mTheme;
  TypedValue mValue = new TypedValue();
  XmlBlock.Parser mXml;
  
  TypedArray(Resources paramResources, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    this.mResources = paramResources;
    this.mMetrics = this.mResources.getDisplayMetrics();
    this.mAssets = this.mResources.getAssets();
    this.mData = paramArrayOfInt1;
    this.mIndices = paramArrayOfInt2;
    this.mLength = paramInt;
  }
  
  private boolean getValueAt(int paramInt, TypedValue paramTypedValue)
  {
    Object localObject = this.mData;
    int i = localObject[(paramInt + 0)];
    if (i == 0) {
      return false;
    }
    paramTypedValue.type = i;
    paramTypedValue.data = localObject[(paramInt + 1)];
    paramTypedValue.assetCookie = localObject[(paramInt + 2)];
    paramTypedValue.resourceId = localObject[(paramInt + 3)];
    paramTypedValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(localObject[(paramInt + 4)]);
    paramTypedValue.density = localObject[(paramInt + 5)];
    if (i == 3) {}
    for (localObject = loadStringValueAt(paramInt);; localObject = null)
    {
      paramTypedValue.string = ((CharSequence)localObject);
      return true;
    }
  }
  
  private CharSequence loadStringValueAt(int paramInt)
  {
    int[] arrayOfInt = this.mData;
    int i = arrayOfInt[(paramInt + 2)];
    if (i < 0)
    {
      if (this.mXml != null) {
        return this.mXml.getPooledString(arrayOfInt[(paramInt + 1)]);
      }
      return null;
    }
    return this.mAssets.getPooledStringForCookie(i, arrayOfInt[(paramInt + 1)]);
  }
  
  static TypedArray obtain(Resources paramResources, int paramInt)
  {
    TypedArray localTypedArray = (TypedArray)paramResources.mTypedArrayPool.acquire();
    if (localTypedArray != null)
    {
      localTypedArray.mLength = paramInt;
      localTypedArray.mRecycled = false;
      localTypedArray.mAssets = paramResources.getAssets();
      int i = paramInt * 6;
      if (localTypedArray.mData.length >= i) {
        return localTypedArray;
      }
      localTypedArray.mData = new int[i];
      localTypedArray.mIndices = new int[paramInt + 1];
      return localTypedArray;
    }
    return new TypedArray(paramResources, new int[paramInt * 6], new int[paramInt + 1], paramInt);
  }
  
  public int[] extractThemeAttrs()
  {
    return extractThemeAttrs(null);
  }
  
  public int[] extractThemeAttrs(int[] paramArrayOfInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    Object localObject2 = null;
    int[] arrayOfInt = this.mData;
    int j = length();
    int i = 0;
    if (i < j)
    {
      int k = i * 6;
      if (arrayOfInt[(k + 0)] != 2) {
        localObject1 = localObject2;
      }
      do
      {
        i += 1;
        localObject2 = localObject1;
        break;
        arrayOfInt[(k + 0)] = 0;
        k = arrayOfInt[(k + 1)];
        localObject1 = localObject2;
      } while (k == 0);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        if ((paramArrayOfInt == null) || (paramArrayOfInt.length != j)) {
          break label132;
        }
        localObject1 = paramArrayOfInt;
        Arrays.fill(paramArrayOfInt, 0);
      }
      for (;;)
      {
        localObject1[i] = k;
        break;
        label132:
        localObject1 = new int[j];
      }
    }
    return (int[])localObject2;
  }
  
  public boolean getBoolean(int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt *= 6;
    Object localObject = this.mData;
    int i = localObject[(paramInt + 0)];
    if (i == 0) {
      return paramBoolean;
    }
    if ((i >= 16) && (i <= 31))
    {
      paramBoolean = bool;
      if (localObject[(paramInt + 1)] != 0) {
        paramBoolean = true;
      }
      return paramBoolean;
    }
    localObject = this.mValue;
    if (getValueAt(paramInt, (TypedValue)localObject))
    {
      StrictMode.noteResourceMismatch(localObject);
      return XmlUtils.convertValueToBoolean(((TypedValue)localObject).coerceToString(), paramBoolean);
    }
    throw new RuntimeException("getBoolean of bad type: 0x" + Integer.toHexString(i));
  }
  
  public int getChangingConfigurations()
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    int j = 0;
    int[] arrayOfInt = this.mData;
    int k = length();
    int i = 0;
    if (i < k)
    {
      int m = i * 6;
      if (arrayOfInt[(m + 0)] == 0) {}
      for (;;)
      {
        i += 1;
        break;
        j |= ActivityInfo.activityInfoConfigNativeToJava(arrayOfInt[(m + 4)]);
      }
    }
    return j;
  }
  
  public int getColor(int paramInt1, int paramInt2)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    int i = paramInt1 * 6;
    Object localObject = this.mData;
    int j = localObject[(i + 0)];
    if (j == 0) {
      return paramInt2;
    }
    if ((j >= 16) && (j <= 31)) {
      return localObject[(i + 1)];
    }
    if (j == 3)
    {
      localObject = this.mValue;
      if (getValueAt(i, (TypedValue)localObject)) {
        return this.mResources.loadColorStateList((TypedValue)localObject, ((TypedValue)localObject).resourceId, this.mTheme).getDefaultColor();
      }
      return paramInt2;
    }
    if (j == 2)
    {
      localObject = this.mValue;
      getValueAt(i, (TypedValue)localObject);
      throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt1 + ": " + localObject);
    }
    throw new UnsupportedOperationException("Can't convert value at index " + paramInt1 + " to color: type=0x" + Integer.toHexString(j));
  }
  
  public ColorStateList getColorStateList(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    TypedValue localTypedValue = this.mValue;
    if (getValueAt(paramInt * 6, localTypedValue))
    {
      if (localTypedValue.type == 2) {
        throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt + ": " + localTypedValue);
      }
      return this.mResources.loadColorStateList(localTypedValue, localTypedValue.resourceId, this.mTheme);
    }
    return null;
  }
  
  public ComplexColor getComplexColor(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    TypedValue localTypedValue = this.mValue;
    if (getValueAt(paramInt * 6, localTypedValue))
    {
      if (localTypedValue.type == 2) {
        throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt + ": " + localTypedValue);
      }
      return this.mResources.loadComplexColor(localTypedValue, localTypedValue.resourceId, this.mTheme);
    }
    return null;
  }
  
  public float getDimension(int paramInt, float paramFloat)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    int i = paramInt * 6;
    Object localObject = this.mData;
    int j = localObject[(i + 0)];
    if (j == 0) {
      return paramFloat;
    }
    if (j == 5) {
      return TypedValue.complexToDimension(localObject[(i + 1)], this.mMetrics);
    }
    if (j == 2)
    {
      localObject = this.mValue;
      getValueAt(i, (TypedValue)localObject);
      throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt + ": " + localObject);
    }
    throw new UnsupportedOperationException("Can't convert value at index " + paramInt + " to dimension: type=0x" + Integer.toHexString(j));
  }
  
  public int getDimensionPixelOffset(int paramInt1, int paramInt2)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    int i = paramInt1 * 6;
    Object localObject = this.mData;
    int j = localObject[(i + 0)];
    if (j == 0) {
      return paramInt2;
    }
    if (j == 5) {
      return TypedValue.complexToDimensionPixelOffset(localObject[(i + 1)], this.mMetrics);
    }
    if (j == 2)
    {
      localObject = this.mValue;
      getValueAt(i, (TypedValue)localObject);
      throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt1 + ": " + localObject);
    }
    throw new UnsupportedOperationException("Can't convert value at index " + paramInt1 + " to dimension: type=0x" + Integer.toHexString(j));
  }
  
  public int getDimensionPixelSize(int paramInt1, int paramInt2)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    int i = paramInt1 * 6;
    Object localObject = this.mData;
    int j = localObject[(i + 0)];
    if (j == 0) {
      return paramInt2;
    }
    if (j == 5) {
      return TypedValue.complexToDimensionPixelSize(localObject[(i + 1)], this.mMetrics);
    }
    if (j == 2)
    {
      localObject = this.mValue;
      getValueAt(i, (TypedValue)localObject);
      throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt1 + ": " + localObject);
    }
    throw new UnsupportedOperationException("Can't convert value at index " + paramInt1 + " to dimension: type=0x" + Integer.toHexString(j));
  }
  
  public Drawable getDrawable(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    TypedValue localTypedValue = this.mValue;
    if (getValueAt(paramInt * 6, localTypedValue))
    {
      if (localTypedValue.type == 2) {
        throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt + ": " + localTypedValue);
      }
      return this.mResources.loadDrawable(localTypedValue, localTypedValue.resourceId, this.mTheme);
    }
    return null;
  }
  
  public float getFloat(int paramInt, float paramFloat)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt *= 6;
    Object localObject = this.mData;
    int i = localObject[(paramInt + 0)];
    if (i == 0) {
      return paramFloat;
    }
    if (i == 4) {
      return Float.intBitsToFloat(localObject[(paramInt + 1)]);
    }
    if ((i >= 16) && (i <= 31)) {
      return localObject[(paramInt + 1)];
    }
    localObject = this.mValue;
    if (getValueAt(paramInt, (TypedValue)localObject))
    {
      CharSequence localCharSequence = ((TypedValue)localObject).coerceToString();
      if (localCharSequence != null)
      {
        StrictMode.noteResourceMismatch(localObject);
        return Float.parseFloat(localCharSequence.toString());
      }
    }
    throw new RuntimeException("getFloat of bad type: 0x" + Integer.toHexString(i));
  }
  
  public float getFraction(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    int i = paramInt1 * 6;
    Object localObject = this.mData;
    int j = localObject[(i + 0)];
    if (j == 0) {
      return paramFloat;
    }
    if (j == 6) {
      return TypedValue.complexToFraction(localObject[(i + 1)], paramInt2, paramInt3);
    }
    if (j == 2)
    {
      localObject = this.mValue;
      getValueAt(i, (TypedValue)localObject);
      throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt1 + ": " + localObject);
    }
    throw new UnsupportedOperationException("Can't convert value at index " + paramInt1 + " to fraction: type=0x" + Integer.toHexString(j));
  }
  
  public int getIndex(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    return this.mIndices[(paramInt + 1)];
  }
  
  public int getIndexCount()
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    return this.mIndices[0];
  }
  
  public int getInt(int paramInt1, int paramInt2)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt1 *= 6;
    Object localObject = this.mData;
    int i = localObject[(paramInt1 + 0)];
    if (i == 0) {
      return paramInt2;
    }
    if ((i >= 16) && (i <= 31)) {
      return localObject[(paramInt1 + 1)];
    }
    localObject = this.mValue;
    if (getValueAt(paramInt1, (TypedValue)localObject))
    {
      StrictMode.noteResourceMismatch(localObject);
      return XmlUtils.convertValueToInt(((TypedValue)localObject).coerceToString(), paramInt2);
    }
    throw new RuntimeException("getInt of bad type: 0x" + Integer.toHexString(i));
  }
  
  public int getInteger(int paramInt1, int paramInt2)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    int i = paramInt1 * 6;
    Object localObject = this.mData;
    int j = localObject[(i + 0)];
    if (j == 0) {
      return paramInt2;
    }
    if ((j >= 16) && (j <= 31)) {
      return localObject[(i + 1)];
    }
    if (j == 2)
    {
      localObject = this.mValue;
      getValueAt(i, (TypedValue)localObject);
      throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt1 + ": " + localObject);
    }
    throw new UnsupportedOperationException("Can't convert value at index " + paramInt1 + " to integer: type=0x" + Integer.toHexString(j));
  }
  
  public int getLayoutDimension(int paramInt1, int paramInt2)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt1 *= 6;
    int[] arrayOfInt = this.mData;
    int i = arrayOfInt[(paramInt1 + 0)];
    if ((i >= 16) && (i <= 31)) {
      return arrayOfInt[(paramInt1 + 1)];
    }
    if (i == 5) {
      return TypedValue.complexToDimensionPixelSize(arrayOfInt[(paramInt1 + 1)], this.mMetrics);
    }
    return paramInt2;
  }
  
  public int getLayoutDimension(int paramInt, String paramString)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    int i = paramInt * 6;
    int[] arrayOfInt = this.mData;
    int j = arrayOfInt[(i + 0)];
    if ((j >= 16) && (j <= 31)) {
      return arrayOfInt[(i + 1)];
    }
    if (j == 5) {
      return TypedValue.complexToDimensionPixelSize(arrayOfInt[(i + 1)], this.mMetrics);
    }
    if (j == 2)
    {
      paramString = this.mValue;
      getValueAt(i, paramString);
      throw new UnsupportedOperationException("Failed to resolve attribute at index " + paramInt + ": " + paramString);
    }
    throw new UnsupportedOperationException(getPositionDescription() + ": You must supply a " + paramString + " attribute.");
  }
  
  public String getNonConfigurationString(int paramInt1, int paramInt2)
  {
    String str = null;
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt1 *= 6;
    Object localObject = this.mData;
    int i = localObject[(paramInt1 + 0)];
    if ((paramInt2 & ActivityInfo.activityInfoConfigNativeToJava(localObject[(paramInt1 + 4)])) != 0) {
      return null;
    }
    if (i == 0) {
      return null;
    }
    if (i == 3) {
      return loadStringValueAt(paramInt1).toString();
    }
    localObject = this.mValue;
    if (getValueAt(paramInt1, (TypedValue)localObject))
    {
      localObject = ((TypedValue)localObject).coerceToString();
      if (localObject != null) {
        str = ((CharSequence)localObject).toString();
      }
      return str;
    }
    throw new RuntimeException("getNonConfigurationString of bad type: 0x" + Integer.toHexString(i));
  }
  
  public String getNonResourceString(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt *= 6;
    int[] arrayOfInt = this.mData;
    if ((arrayOfInt[(paramInt + 0)] == 3) && (arrayOfInt[(paramInt + 2)] < 0)) {
      return this.mXml.getPooledString(arrayOfInt[(paramInt + 1)]).toString();
    }
    return null;
  }
  
  public String getPositionDescription()
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    if (this.mXml != null) {
      return this.mXml.getPositionDescription();
    }
    return "<internal>";
  }
  
  public int getResourceId(int paramInt1, int paramInt2)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt1 *= 6;
    int[] arrayOfInt = this.mData;
    if (arrayOfInt[(paramInt1 + 0)] != 0)
    {
      paramInt1 = arrayOfInt[(paramInt1 + 3)];
      if (paramInt1 != 0) {
        return paramInt1;
      }
    }
    return paramInt2;
  }
  
  public Resources getResources()
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    return this.mResources;
  }
  
  public String getString(int paramInt)
  {
    String str = null;
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt *= 6;
    int i = this.mData[(paramInt + 0)];
    if (i == 0) {
      return null;
    }
    if (i == 3) {
      return loadStringValueAt(paramInt).toString();
    }
    Object localObject = this.mValue;
    if (getValueAt(paramInt, (TypedValue)localObject))
    {
      localObject = ((TypedValue)localObject).coerceToString();
      if (localObject != null) {
        str = ((CharSequence)localObject).toString();
      }
      return str;
    }
    throw new RuntimeException("getString of bad type: 0x" + Integer.toHexString(i));
  }
  
  public CharSequence getText(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt *= 6;
    int i = this.mData[(paramInt + 0)];
    if (i == 0) {
      return null;
    }
    if (i == 3) {
      return loadStringValueAt(paramInt);
    }
    TypedValue localTypedValue = this.mValue;
    if (getValueAt(paramInt, localTypedValue)) {
      return localTypedValue.coerceToString();
    }
    throw new RuntimeException("getText of bad type: 0x" + Integer.toHexString(i));
  }
  
  public CharSequence[] getTextArray(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    TypedValue localTypedValue = this.mValue;
    if (getValueAt(paramInt * 6, localTypedValue)) {
      return this.mResources.getTextArray(localTypedValue.resourceId);
    }
    return null;
  }
  
  public int getThemeAttributeId(int paramInt1, int paramInt2)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt1 *= 6;
    int[] arrayOfInt = this.mData;
    if (arrayOfInt[(paramInt1 + 0)] == 2) {
      return arrayOfInt[(paramInt1 + 1)];
    }
    return paramInt2;
  }
  
  public int getType(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    return this.mData[(paramInt * 6 + 0)];
  }
  
  public boolean getValue(int paramInt, TypedValue paramTypedValue)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    return getValueAt(paramInt * 6, paramTypedValue);
  }
  
  public boolean hasValue(int paramInt)
  {
    boolean bool = false;
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    if (this.mData[(paramInt * 6 + 0)] != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasValueOrEmpty(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    paramInt *= 6;
    int[] arrayOfInt = this.mData;
    return (arrayOfInt[(paramInt + 0)] != 0) || (arrayOfInt[(paramInt + 1)] == 1);
  }
  
  public int length()
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    return this.mLength;
  }
  
  public TypedValue peekValue(int paramInt)
  {
    if (this.mRecycled) {
      throw new RuntimeException("Cannot make calls to a recycled instance!");
    }
    TypedValue localTypedValue = this.mValue;
    if (getValueAt(paramInt * 6, localTypedValue)) {
      return localTypedValue;
    }
    return null;
  }
  
  public void recycle()
  {
    if (this.mRecycled) {
      throw new RuntimeException(toString() + " recycled twice!");
    }
    this.mRecycled = true;
    this.mXml = null;
    this.mTheme = null;
    this.mAssets = null;
    this.mResources.mTypedArrayPool.release(this);
  }
  
  public String toString()
  {
    return Arrays.toString(this.mData);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/TypedArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */