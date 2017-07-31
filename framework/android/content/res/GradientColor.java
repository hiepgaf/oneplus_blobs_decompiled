package android.content.res;

import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import com.android.internal.util.GrowingArrayUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class GradientColor
  extends ComplexColor
{
  private static final boolean DBG_GRADIENT = false;
  private static final String TAG = "GradientColor";
  private static final int TILE_MODE_CLAMP = 0;
  private static final int TILE_MODE_MIRROR = 2;
  private static final int TILE_MODE_REPEAT = 1;
  private int mCenterColor = 0;
  private float mCenterX = 0.0F;
  private float mCenterY = 0.0F;
  private int mChangingConfigurations;
  private int mDefaultColor;
  private int mEndColor = 0;
  private float mEndX = 0.0F;
  private float mEndY = 0.0F;
  private GradientColorFactory mFactory;
  private float mGradientRadius = 0.0F;
  private int mGradientType = 0;
  private boolean mHasCenterColor = false;
  private int[] mItemColors;
  private float[] mItemOffsets;
  private int[][] mItemsThemeAttrs;
  private Shader mShader = null;
  private int mStartColor = 0;
  private float mStartX = 0.0F;
  private float mStartY = 0.0F;
  private int[] mThemeAttrs;
  private int mTileMode = 0;
  
  private GradientColor() {}
  
  private GradientColor(GradientColor paramGradientColor)
  {
    if (paramGradientColor != null)
    {
      this.mChangingConfigurations = paramGradientColor.mChangingConfigurations;
      this.mDefaultColor = paramGradientColor.mDefaultColor;
      this.mShader = paramGradientColor.mShader;
      this.mGradientType = paramGradientColor.mGradientType;
      this.mCenterX = paramGradientColor.mCenterX;
      this.mCenterY = paramGradientColor.mCenterY;
      this.mStartX = paramGradientColor.mStartX;
      this.mStartY = paramGradientColor.mStartY;
      this.mEndX = paramGradientColor.mEndX;
      this.mEndY = paramGradientColor.mEndY;
      this.mStartColor = paramGradientColor.mStartColor;
      this.mCenterColor = paramGradientColor.mCenterColor;
      this.mEndColor = paramGradientColor.mEndColor;
      this.mHasCenterColor = paramGradientColor.mHasCenterColor;
      this.mGradientRadius = paramGradientColor.mGradientRadius;
      this.mTileMode = paramGradientColor.mTileMode;
      if (paramGradientColor.mItemColors != null) {
        this.mItemColors = ((int[])paramGradientColor.mItemColors.clone());
      }
      if (paramGradientColor.mItemOffsets != null) {
        this.mItemOffsets = ((float[])paramGradientColor.mItemOffsets.clone());
      }
      if (paramGradientColor.mThemeAttrs != null) {
        this.mThemeAttrs = ((int[])paramGradientColor.mThemeAttrs.clone());
      }
      if (paramGradientColor.mItemsThemeAttrs != null) {
        this.mItemsThemeAttrs = ((int[][])paramGradientColor.mItemsThemeAttrs.clone());
      }
    }
  }
  
  private void applyItemsAttrsTheme(Resources.Theme paramTheme)
  {
    if (this.mItemsThemeAttrs == null) {
      return;
    }
    int i = 0;
    int[][] arrayOfInt = this.mItemsThemeAttrs;
    int m = arrayOfInt.length;
    int j = 0;
    while (j < m)
    {
      int k = i;
      if (arrayOfInt[j] != null)
      {
        TypedArray localTypedArray = paramTheme.resolveAttributes(arrayOfInt[j], R.styleable.GradientColorItem);
        arrayOfInt[j] = localTypedArray.extractThemeAttrs(arrayOfInt[j]);
        if (arrayOfInt[j] != null) {
          i = 1;
        }
        this.mItemColors[j] = localTypedArray.getColor(0, this.mItemColors[j]);
        this.mItemOffsets[j] = localTypedArray.getFloat(1, this.mItemOffsets[j]);
        this.mChangingConfigurations |= localTypedArray.getChangingConfigurations();
        localTypedArray.recycle();
        k = i;
      }
      j += 1;
      i = k;
    }
    if (i == 0) {
      this.mItemsThemeAttrs = null;
    }
  }
  
  private void applyRootAttrsTheme(Resources.Theme paramTheme)
  {
    paramTheme = paramTheme.resolveAttributes(this.mThemeAttrs, R.styleable.GradientColor);
    this.mThemeAttrs = paramTheme.extractThemeAttrs(this.mThemeAttrs);
    updateRootElementState(paramTheme);
    this.mChangingConfigurations |= paramTheme.getChangingConfigurations();
    paramTheme.recycle();
  }
  
  private void applyTheme(Resources.Theme paramTheme)
  {
    if (this.mThemeAttrs != null) {
      applyRootAttrsTheme(paramTheme);
    }
    if (this.mItemsThemeAttrs != null) {
      applyItemsAttrsTheme(paramTheme);
    }
    onColorsChange();
  }
  
  public static GradientColor createFromXml(Resources paramResources, XmlResourceParser paramXmlResourceParser, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlResourceParser);
    int i;
    do
    {
      i = paramXmlResourceParser.next();
    } while ((i != 2) && (i != 1));
    if (i != 2) {
      throw new XmlPullParserException("No start tag found");
    }
    return createFromXmlInner(paramResources, paramXmlResourceParser, localAttributeSet, paramTheme);
  }
  
  static GradientColor createFromXmlInner(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    Object localObject = paramXmlPullParser.getName();
    if (!((String)localObject).equals("gradient")) {
      throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": invalid gradient color tag " + (String)localObject);
    }
    localObject = new GradientColor();
    ((GradientColor)localObject).inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    return (GradientColor)localObject;
  }
  
  private void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = Resources.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.GradientColor);
    updateRootElementState(localTypedArray);
    this.mChangingConfigurations |= localTypedArray.getChangingConfigurations();
    localTypedArray.recycle();
    validateXmlContent();
    inflateChildElements(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    onColorsChange();
  }
  
  private void inflateChildElements(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    int k = paramXmlPullParser.getDepth() + 1;
    float[] arrayOfFloat = new float[20];
    int[] arrayOfInt1 = new int[arrayOfFloat.length];
    int[][] arrayOfInt = new int[arrayOfFloat.length][];
    int i = 0;
    int j = 0;
    for (;;)
    {
      int m = paramXmlPullParser.next();
      if (m == 1) {
        break label271;
      }
      int n = paramXmlPullParser.getDepth();
      if ((n < k) && (m == 3)) {
        break label271;
      }
      if ((m == 2) && (n <= k) && (paramXmlPullParser.getName().equals("item")))
      {
        TypedArray localTypedArray = Resources.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.GradientColorItem);
        boolean bool1 = localTypedArray.hasValue(0);
        boolean bool2 = localTypedArray.hasValue(1);
        if ((!bool1) || (!bool2)) {
          break;
        }
        int[] arrayOfInt2 = localTypedArray.extractThemeAttrs();
        m = localTypedArray.getColor(0, 0);
        float f = localTypedArray.getFloat(1, 0.0F);
        this.mChangingConfigurations |= localTypedArray.getChangingConfigurations();
        localTypedArray.recycle();
        if (arrayOfInt2 != null) {
          j = 1;
        }
        arrayOfInt1 = GrowingArrayUtils.append(arrayOfInt1, i, m);
        arrayOfFloat = GrowingArrayUtils.append(arrayOfFloat, i, f);
        arrayOfInt = (int[][])GrowingArrayUtils.append(arrayOfInt, i, arrayOfInt2);
        i += 1;
      }
    }
    throw new XmlPullParserException(paramXmlPullParser.getPositionDescription() + ": <item> tag requires a 'color' attribute and a 'offset' " + "attribute!");
    label271:
    if (i > 0)
    {
      if (j == 0) {
        break label346;
      }
      this.mItemsThemeAttrs = new int[i][];
      System.arraycopy(arrayOfInt, 0, this.mItemsThemeAttrs, 0, i);
    }
    for (;;)
    {
      this.mItemColors = new int[i];
      this.mItemOffsets = new float[i];
      System.arraycopy(arrayOfInt1, 0, this.mItemColors, 0, i);
      System.arraycopy(arrayOfFloat, 0, this.mItemOffsets, 0, i);
      return;
      label346:
      this.mItemsThemeAttrs = null;
    }
  }
  
  private void onColorsChange()
  {
    Object localObject = null;
    int[] arrayOfInt1;
    if (this.mItemColors != null)
    {
      int j = this.mItemColors.length;
      int[] arrayOfInt2 = new int[j];
      float[] arrayOfFloat = new float[j];
      int i = 0;
      for (;;)
      {
        arrayOfInt1 = arrayOfInt2;
        localObject = arrayOfFloat;
        if (i >= j) {
          break;
        }
        arrayOfInt2[i] = this.mItemColors[i];
        arrayOfFloat[i] = this.mItemOffsets[i];
        i += 1;
      }
    }
    if (this.mHasCenterColor)
    {
      arrayOfInt1 = new int[3];
      arrayOfInt1[0] = this.mStartColor;
      arrayOfInt1[1] = this.mCenterColor;
      arrayOfInt1[2] = this.mEndColor;
      localObject = new float[3];
      localObject[0] = 0.0F;
      localObject[1] = 0.5F;
      localObject[2] = 1.0F;
      if (arrayOfInt1.length < 2) {
        Log.w("GradientColor", "<gradient> tag requires 2 color values specified!" + arrayOfInt1.length + " " + arrayOfInt1);
      }
      if (this.mGradientType != 0) {
        break label237;
      }
      this.mShader = new LinearGradient(this.mStartX, this.mStartY, this.mEndX, this.mEndY, arrayOfInt1, (float[])localObject, parseTileMode(this.mTileMode));
    }
    for (;;)
    {
      this.mDefaultColor = arrayOfInt1[0];
      return;
      arrayOfInt1 = new int[2];
      arrayOfInt1[0] = this.mStartColor;
      arrayOfInt1[1] = this.mEndColor;
      break;
      label237:
      if (this.mGradientType == 1) {
        this.mShader = new RadialGradient(this.mCenterX, this.mCenterY, this.mGradientRadius, arrayOfInt1, (float[])localObject, parseTileMode(this.mTileMode));
      } else {
        this.mShader = new SweepGradient(this.mCenterX, this.mCenterY, arrayOfInt1, (float[])localObject);
      }
    }
  }
  
  private static Shader.TileMode parseTileMode(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Shader.TileMode.CLAMP;
    case 0: 
      return Shader.TileMode.CLAMP;
    case 1: 
      return Shader.TileMode.REPEAT;
    }
    return Shader.TileMode.MIRROR;
  }
  
  private void updateRootElementState(TypedArray paramTypedArray)
  {
    this.mThemeAttrs = paramTypedArray.extractThemeAttrs();
    this.mStartX = paramTypedArray.getFloat(8, this.mStartX);
    this.mStartY = paramTypedArray.getFloat(9, this.mStartY);
    this.mEndX = paramTypedArray.getFloat(10, this.mEndX);
    this.mEndY = paramTypedArray.getFloat(11, this.mEndY);
    this.mCenterX = paramTypedArray.getFloat(3, this.mCenterX);
    this.mCenterY = paramTypedArray.getFloat(4, this.mCenterY);
    this.mGradientType = paramTypedArray.getInt(2, this.mGradientType);
    this.mStartColor = paramTypedArray.getColor(0, this.mStartColor);
    this.mHasCenterColor |= paramTypedArray.hasValue(7);
    this.mCenterColor = paramTypedArray.getColor(7, this.mCenterColor);
    this.mEndColor = paramTypedArray.getColor(1, this.mEndColor);
    this.mTileMode = paramTypedArray.getInt(6, this.mTileMode);
    this.mGradientRadius = paramTypedArray.getFloat(5, this.mGradientRadius);
  }
  
  private void validateXmlContent()
    throws XmlPullParserException
  {
    if ((this.mGradientRadius <= 0.0F) && (this.mGradientType == 1)) {
      throw new XmlPullParserException("<gradient> tag requires 'gradientRadius' attribute with radial type");
    }
  }
  
  public boolean canApplyTheme()
  {
    return (this.mThemeAttrs != null) || (this.mItemsThemeAttrs != null);
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.mChangingConfigurations;
  }
  
  public ConstantState<ComplexColor> getConstantState()
  {
    if (this.mFactory == null) {
      this.mFactory = new GradientColorFactory(this);
    }
    return this.mFactory;
  }
  
  public int getDefaultColor()
  {
    return this.mDefaultColor;
  }
  
  public Shader getShader()
  {
    return this.mShader;
  }
  
  public GradientColor obtainForTheme(Resources.Theme paramTheme)
  {
    if ((paramTheme != null) && (canApplyTheme()))
    {
      GradientColor localGradientColor = new GradientColor(this);
      localGradientColor.applyTheme(paramTheme);
      return localGradientColor;
    }
    return this;
  }
  
  private static class GradientColorFactory
    extends ConstantState<ComplexColor>
  {
    private final GradientColor mSrc;
    
    public GradientColorFactory(GradientColor paramGradientColor)
    {
      this.mSrc = paramGradientColor;
    }
    
    public int getChangingConfigurations()
    {
      return GradientColor.-get0(this.mSrc);
    }
    
    public GradientColor newInstance()
    {
      return this.mSrc;
    }
    
    public GradientColor newInstance(Resources paramResources, Resources.Theme paramTheme)
    {
      return this.mSrc.obtainForTheme(paramTheme);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/GradientColor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */