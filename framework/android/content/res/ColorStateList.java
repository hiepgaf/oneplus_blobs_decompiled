package android.content.res;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.util.SparseArray;
import android.util.StateSet;
import android.util.Xml;
import com.android.internal.R.styleable;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ColorStateList
  extends ComplexColor
  implements Parcelable
{
  public static final Parcelable.Creator<ColorStateList> CREATOR = new Parcelable.Creator()
  {
    public ColorStateList createFromParcel(Parcel paramAnonymousParcel)
    {
      int j = paramAnonymousParcel.readInt();
      int[][] arrayOfInt = new int[j][];
      int i = 0;
      while (i < j)
      {
        arrayOfInt[i] = paramAnonymousParcel.createIntArray();
        i += 1;
      }
      return new ColorStateList(arrayOfInt, paramAnonymousParcel.createIntArray());
    }
    
    public ColorStateList[] newArray(int paramAnonymousInt)
    {
      return new ColorStateList[paramAnonymousInt];
    }
  };
  private static final int DEFAULT_COLOR = -65536;
  private static final int[][] EMPTY = { new int[0] };
  private static final String TAG = "ColorStateList";
  private static final SparseArray<WeakReference<ColorStateList>> sCache = new SparseArray();
  private int mChangingConfigurations;
  private int[] mColors;
  private int mDefaultColor;
  private ColorStateListFactory mFactory;
  private boolean mIsOpaque;
  private int[][] mStateSpecs;
  private int[][] mThemeAttrs;
  
  private ColorStateList() {}
  
  private ColorStateList(ColorStateList paramColorStateList)
  {
    if (paramColorStateList != null)
    {
      this.mChangingConfigurations = paramColorStateList.mChangingConfigurations;
      this.mStateSpecs = paramColorStateList.mStateSpecs;
      this.mDefaultColor = paramColorStateList.mDefaultColor;
      this.mIsOpaque = paramColorStateList.mIsOpaque;
      this.mThemeAttrs = ((int[][])paramColorStateList.mThemeAttrs.clone());
      this.mColors = ((int[])paramColorStateList.mColors.clone());
    }
  }
  
  public ColorStateList(int[][] paramArrayOfInt, int[] paramArrayOfInt1)
  {
    this.mStateSpecs = paramArrayOfInt;
    this.mColors = paramArrayOfInt1;
    onColorsChanged();
  }
  
  private void applyTheme(Resources.Theme paramTheme)
  {
    if (this.mThemeAttrs == null) {
      return;
    }
    int i = 0;
    int[][] arrayOfInt = this.mThemeAttrs;
    int m = arrayOfInt.length;
    int j = 0;
    if (j < m)
    {
      int k = i;
      TypedArray localTypedArray;
      if (arrayOfInt[j] != null)
      {
        localTypedArray = paramTheme.resolveAttributes(arrayOfInt[j], R.styleable.ColorStateListItem);
        if (arrayOfInt[j][0] == 0) {
          break label177;
        }
      }
      label177:
      for (float f = Color.alpha(this.mColors[j]) / 255.0F;; f = 1.0F)
      {
        arrayOfInt[j] = localTypedArray.extractThemeAttrs(arrayOfInt[j]);
        if (arrayOfInt[j] != null) {
          i = 1;
        }
        k = localTypedArray.getColor(0, this.mColors[j]);
        f = localTypedArray.getFloat(1, f);
        this.mColors[j] = modulateColorAlpha(k, f);
        this.mChangingConfigurations |= localTypedArray.getChangingConfigurations();
        localTypedArray.recycle();
        k = i;
        j += 1;
        i = k;
        break;
      }
    }
    if (i == 0) {
      this.mThemeAttrs = null;
    }
    onColorsChanged();
  }
  
  @Deprecated
  public static ColorStateList createFromXml(Resources paramResources, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    return createFromXml(paramResources, paramXmlPullParser, null);
  }
  
  public static ColorStateList createFromXml(Resources paramResources, XmlPullParser paramXmlPullParser, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
    int i;
    do
    {
      i = paramXmlPullParser.next();
    } while ((i != 2) && (i != 1));
    if (i != 2) {
      throw new XmlPullParserException("No start tag found");
    }
    return createFromXmlInner(paramResources, paramXmlPullParser, localAttributeSet, paramTheme);
  }
  
  static ColorStateList createFromXmlInner(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    Object localObject = paramXmlPullParser.getName();
    if (!((String)localObject).equals("selector")) {
      throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": invalid color state list tag " + (String)localObject);
    }
    localObject = new ColorStateList();
    ((ColorStateList)localObject).inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    return (ColorStateList)localObject;
  }
  
  private void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    int i4 = paramXmlPullParser.getDepth() + 1;
    int i = 0;
    int m = -65536;
    int k = 0;
    int[][] arrayOfInt1 = (int[][])ArrayUtils.newUnpaddedArray(int[].class, 20);
    int[][] arrayOfInt2 = new int[arrayOfInt1.length][];
    int[] arrayOfInt3 = new int[arrayOfInt1.length];
    int j = 0;
    for (;;)
    {
      int n = paramXmlPullParser.next();
      if (n == 1) {
        break;
      }
      int i1 = paramXmlPullParser.getDepth();
      if ((i1 < i4) && (n == 3)) {
        break;
      }
      if ((n == 2) && (i1 <= i4) && (paramXmlPullParser.getName().equals("item")))
      {
        Object localObject = Resources.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.ColorStateListItem);
        int[] arrayOfInt4 = ((TypedArray)localObject).extractThemeAttrs();
        int i5 = ((TypedArray)localObject).getColor(0, -65281);
        float f = ((TypedArray)localObject).getFloat(1, 1.0F);
        int i2 = i | ((TypedArray)localObject).getChangingConfigurations();
        ((TypedArray)localObject).recycle();
        int i6 = paramAttributeSet.getAttributeCount();
        localObject = new int[i6];
        n = 0;
        i = 0;
        if (n < i6)
        {
          i1 = paramAttributeSet.getAttributeNameResource(n);
          int i3;
          switch (i1)
          {
          default: 
            i3 = i + 1;
            if (!paramAttributeSet.getAttributeBooleanValue(n, false)) {
              break;
            }
          }
          for (;;)
          {
            localObject[i] = i1;
            i = i3;
            for (;;)
            {
              n += 1;
              break;
            }
            i1 = -i1;
          }
        }
        localObject = StateSet.trimStateSet((int[])localObject, i);
        i = modulateColorAlpha(i5, f);
        if ((j == 0) || (localObject.length == 0)) {
          m = i;
        }
        if (arrayOfInt4 != null) {
          k = 1;
        }
        arrayOfInt3 = GrowingArrayUtils.append(arrayOfInt3, j, i);
        arrayOfInt2 = (int[][])GrowingArrayUtils.append(arrayOfInt2, j, arrayOfInt4);
        arrayOfInt1 = (int[][])GrowingArrayUtils.append(arrayOfInt1, j, localObject);
        j += 1;
        i = i2;
      }
    }
    this.mChangingConfigurations = i;
    this.mDefaultColor = m;
    if (k != 0)
    {
      this.mThemeAttrs = new int[j][];
      System.arraycopy(arrayOfInt2, 0, this.mThemeAttrs, 0, j);
    }
    for (;;)
    {
      this.mColors = new int[j];
      this.mStateSpecs = new int[j][];
      System.arraycopy(arrayOfInt3, 0, this.mColors, 0, j);
      System.arraycopy(arrayOfInt1, 0, this.mStateSpecs, 0, j);
      onColorsChanged();
      return;
      this.mThemeAttrs = null;
    }
  }
  
  private int modulateColorAlpha(int paramInt, float paramFloat)
  {
    if (paramFloat == 1.0F) {
      return paramInt;
    }
    return 0xFFFFFF & paramInt | MathUtils.constrain((int)(Color.alpha(paramInt) * paramFloat + 0.5F), 0, 255) << 24;
  }
  
  private void onColorsChanged()
  {
    int k = -65536;
    boolean bool2 = true;
    int[][] arrayOfInt = this.mStateSpecs;
    int[] arrayOfInt1 = this.mColors;
    int m = arrayOfInt.length;
    boolean bool1 = bool2;
    int j;
    int i;
    if (m > 0)
    {
      k = arrayOfInt1[0];
      j = m - 1;
      i = k;
      if (j > 0)
      {
        if (arrayOfInt[j].length != 0) {
          break label105;
        }
        i = arrayOfInt1[j];
      }
      j = 0;
    }
    for (;;)
    {
      k = i;
      bool1 = bool2;
      if (j < m)
      {
        if (Color.alpha(arrayOfInt1[j]) != 255)
        {
          bool1 = false;
          k = i;
        }
      }
      else
      {
        this.mDefaultColor = k;
        this.mIsOpaque = bool1;
        return;
        label105:
        j -= 1;
        break;
      }
      j += 1;
    }
  }
  
  public static ColorStateList valueOf(int paramInt)
  {
    for (;;)
    {
      int i;
      synchronized (sCache)
      {
        i = sCache.indexOfKey(paramInt);
        ColorStateList localColorStateList;
        if (i >= 0)
        {
          localColorStateList = (ColorStateList)((WeakReference)sCache.valueAt(i)).get();
          if (localColorStateList != null) {
            return localColorStateList;
          }
          sCache.removeAt(i);
        }
        i = sCache.size() - 1;
        if (i >= 0)
        {
          if (((WeakReference)sCache.valueAt(i)).get() == null) {
            sCache.removeAt(i);
          }
        }
        else
        {
          localColorStateList = new ColorStateList(EMPTY, new int[] { paramInt });
          sCache.put(paramInt, new WeakReference(localColorStateList));
          return localColorStateList;
        }
      }
      i -= 1;
    }
  }
  
  public boolean canApplyTheme()
  {
    return this.mThemeAttrs != null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mChangingConfigurations;
  }
  
  public int getColorForState(int[] paramArrayOfInt, int paramInt)
  {
    int j = this.mStateSpecs.length;
    int i = 0;
    while (i < j)
    {
      if (StateSet.stateSetMatches(this.mStateSpecs[i], paramArrayOfInt)) {
        return this.mColors[i];
      }
      i += 1;
    }
    return paramInt;
  }
  
  public int[] getColors()
  {
    return this.mColors;
  }
  
  public ConstantState<ComplexColor> getConstantState()
  {
    if (this.mFactory == null) {
      this.mFactory = new ColorStateListFactory(this);
    }
    return this.mFactory;
  }
  
  public int getDefaultColor()
  {
    return this.mDefaultColor;
  }
  
  public int[][] getStates()
  {
    return this.mStateSpecs;
  }
  
  public boolean hasState(int paramInt)
  {
    int[][] arrayOfInt = this.mStateSpecs;
    int k = arrayOfInt.length;
    int i = 0;
    while (i < k)
    {
      int[] arrayOfInt1 = arrayOfInt[i];
      int m = arrayOfInt1.length;
      int j = 0;
      while (j < m)
      {
        if ((arrayOfInt1[j] == paramInt) || (arrayOfInt1[j] == paramInt)) {
          return true;
        }
        j += 1;
      }
      i += 1;
    }
    return false;
  }
  
  public boolean isOpaque()
  {
    return this.mIsOpaque;
  }
  
  public boolean isStateful()
  {
    return this.mStateSpecs.length > 1;
  }
  
  public ColorStateList obtainForTheme(Resources.Theme paramTheme)
  {
    if ((paramTheme != null) && (canApplyTheme()))
    {
      ColorStateList localColorStateList = new ColorStateList(this);
      localColorStateList.applyTheme(paramTheme);
      return localColorStateList;
    }
    return this;
  }
  
  public String toString()
  {
    return "ColorStateList{mThemeAttrs=" + Arrays.deepToString(this.mThemeAttrs) + "mChangingConfigurations=" + this.mChangingConfigurations + "mStateSpecs=" + Arrays.deepToString(this.mStateSpecs) + "mColors=" + Arrays.toString(this.mColors) + "mDefaultColor=" + this.mDefaultColor + '}';
  }
  
  public ColorStateList withAlpha(int paramInt)
  {
    int[] arrayOfInt = new int[this.mColors.length];
    int j = arrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      arrayOfInt[i] = (this.mColors[i] & 0xFFFFFF | paramInt << 24);
      i += 1;
    }
    return new ColorStateList(this.mStateSpecs, arrayOfInt);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (canApplyTheme()) {
      Log.w("ColorStateList", "Wrote partially-resolved ColorStateList to parcel!");
    }
    int i = this.mStateSpecs.length;
    paramParcel.writeInt(i);
    paramInt = 0;
    while (paramInt < i)
    {
      paramParcel.writeIntArray(this.mStateSpecs[paramInt]);
      paramInt += 1;
    }
    paramParcel.writeIntArray(this.mColors);
  }
  
  private static class ColorStateListFactory
    extends ConstantState<ComplexColor>
  {
    private final ColorStateList mSrc;
    
    public ColorStateListFactory(ColorStateList paramColorStateList)
    {
      this.mSrc = paramColorStateList;
    }
    
    public int getChangingConfigurations()
    {
      return ColorStateList.-get0(this.mSrc);
    }
    
    public ColorStateList newInstance()
    {
      return this.mSrc;
    }
    
    public ColorStateList newInstance(Resources paramResources, Resources.Theme paramTheme)
    {
      return this.mSrc.obtainForTheme(paramTheme);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ColorStateList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */