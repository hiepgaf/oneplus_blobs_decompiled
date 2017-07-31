package android.content.res;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.text.Annotation;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan.WithDensity;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.SparseArray;

final class StringBlock
{
  private static final String TAG = "AssetManager";
  private static final boolean localLOGV = false;
  private final long mNative;
  private final boolean mOwnsNative;
  private SparseArray<CharSequence> mSparseStrings;
  private CharSequence[] mStrings;
  StyleIDs mStyleIDs = null;
  private final boolean mUseSparse;
  
  StringBlock(long paramLong, boolean paramBoolean)
  {
    this.mNative = paramLong;
    this.mUseSparse = paramBoolean;
    this.mOwnsNative = false;
  }
  
  public StringBlock(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mNative = nativeCreate(paramArrayOfByte, paramInt1, paramInt2);
    this.mUseSparse = paramBoolean;
    this.mOwnsNative = true;
  }
  
  public StringBlock(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    this.mNative = nativeCreate(paramArrayOfByte, 0, paramArrayOfByte.length);
    this.mUseSparse = paramBoolean;
    this.mOwnsNative = true;
  }
  
  private static void addParagraphSpan(Spannable paramSpannable, Object paramObject, int paramInt1, int paramInt2)
  {
    int k = paramSpannable.length();
    int i = paramInt1;
    if (paramInt1 != 0)
    {
      i = paramInt1;
      if (paramInt1 != k)
      {
        i = paramInt1;
        if (paramSpannable.charAt(paramInt1 - 1) != '\n')
        {
          paramInt1 -= 1;
          i = paramInt1;
          if (paramInt1 > 0)
          {
            if (paramSpannable.charAt(paramInt1 - 1) != '\n') {
              break label146;
            }
            i = paramInt1;
          }
        }
      }
    }
    int j = paramInt2;
    if (paramInt2 != 0)
    {
      j = paramInt2;
      if (paramInt2 != k)
      {
        j = paramInt2;
        if (paramSpannable.charAt(paramInt2 - 1) != '\n') {
          paramInt1 = paramInt2 + 1;
        }
      }
    }
    for (;;)
    {
      j = paramInt1;
      if (paramInt1 < k)
      {
        if (paramSpannable.charAt(paramInt1 - 1) == '\n') {
          j = paramInt1;
        }
      }
      else
      {
        paramSpannable.setSpan(paramObject, i, j, 51);
        return;
        label146:
        paramInt1 -= 1;
        break;
      }
      paramInt1 += 1;
    }
  }
  
  private CharSequence applyStyles(String paramString, int[] paramArrayOfInt, StyleIDs paramStyleIDs)
  {
    if (paramArrayOfInt.length == 0) {
      return paramString;
    }
    paramString = new SpannableString(paramString);
    int j = 0;
    if (j < paramArrayOfInt.length)
    {
      int i = paramArrayOfInt[j];
      if (i == StyleIDs.-get1(paramStyleIDs)) {
        paramString.setSpan(new StyleSpan(1), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
      }
      label932:
      for (;;)
      {
        j += 3;
        break;
        if (i == StyleIDs.-get2(paramStyleIDs))
        {
          paramString.setSpan(new StyleSpan(2), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
        }
        else if (i == StyleIDs.-get10(paramStyleIDs))
        {
          paramString.setSpan(new UnderlineSpan(), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
        }
        else if (i == StyleIDs.-get9(paramStyleIDs))
        {
          paramString.setSpan(new TypefaceSpan("monospace"), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
        }
        else if (i == StyleIDs.-get0(paramStyleIDs))
        {
          paramString.setSpan(new RelativeSizeSpan(1.25F), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
        }
        else if (i == StyleIDs.-get5(paramStyleIDs))
        {
          paramString.setSpan(new RelativeSizeSpan(0.8F), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
        }
        else if (i == StyleIDs.-get7(paramStyleIDs))
        {
          paramString.setSpan(new SubscriptSpan(), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
        }
        else if (i == StyleIDs.-get8(paramStyleIDs))
        {
          paramString.setSpan(new SuperscriptSpan(), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
        }
        else if (i == StyleIDs.-get6(paramStyleIDs))
        {
          paramString.setSpan(new StrikethroughSpan(), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
        }
        else if (i == StyleIDs.-get3(paramStyleIDs))
        {
          addParagraphSpan(paramString, new BulletSpan(10), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1);
        }
        else if (i == StyleIDs.-get4(paramStyleIDs))
        {
          paramString.setSpan(TextUtils.TruncateAt.MARQUEE, paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 18);
        }
        else
        {
          String str1 = nativeGetString(this.mNative, i);
          if (str1.startsWith("font;"))
          {
            String str2 = subtag(str1, ";height=");
            if (str2 != null) {
              addParagraphSpan(paramString, new Height(Integer.parseInt(str2)), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1);
            }
            str2 = subtag(str1, ";size=");
            if (str2 != null) {
              paramString.setSpan(new AbsoluteSizeSpan(Integer.parseInt(str2), true), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
            }
            str2 = subtag(str1, ";fgcolor=");
            if (str2 != null) {
              paramString.setSpan(getColor(str2, true), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
            }
            str2 = subtag(str1, ";color=");
            if (str2 != null) {
              paramString.setSpan(getColor(str2, true), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
            }
            str2 = subtag(str1, ";bgcolor=");
            if (str2 != null) {
              paramString.setSpan(getColor(str2, false), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
            }
            str1 = subtag(str1, ";face=");
            if (str1 != null) {
              paramString.setSpan(new TypefaceSpan(str1), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
            }
          }
          else if (str1.startsWith("a;"))
          {
            str1 = subtag(str1, ";href=");
            if (str1 != null) {
              paramString.setSpan(new URLSpan(str1), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
            }
          }
          else if (str1.startsWith("annotation;"))
          {
            int m = str1.length();
            for (int k = str1.indexOf(';');; k = i)
            {
              if (k >= m) {
                break label932;
              }
              int i1 = str1.indexOf('=', k);
              if (i1 < 0) {
                break;
              }
              int n = str1.indexOf(';', i1);
              i = n;
              if (n < 0) {
                i = m;
              }
              paramString.setSpan(new Annotation(str1.substring(k + 1, i1), str1.substring(i1 + 1, i)), paramArrayOfInt[(j + 1)], paramArrayOfInt[(j + 2)] + 1, 33);
            }
          }
        }
      }
    }
    return new SpannedString(paramString);
  }
  
  private static CharacterStyle getColor(String paramString, boolean paramBoolean)
  {
    int j = -16777216;
    int i = j;
    if (!TextUtils.isEmpty(paramString))
    {
      if (!paramString.startsWith("@")) {
        break label93;
      }
      Resources localResources = Resources.getSystem();
      int k = localResources.getIdentifier(paramString.substring(1), "color", "android");
      i = j;
      if (k != 0)
      {
        paramString = localResources.getColorStateList(k, null);
        if (paramBoolean) {
          return new TextAppearanceSpan(null, 0, 0, paramString, null);
        }
        i = paramString.getDefaultColor();
      }
    }
    while (paramBoolean)
    {
      return new ForegroundColorSpan(i);
      try
      {
        label93:
        i = Color.parseColor(paramString);
      }
      catch (IllegalArgumentException paramString)
      {
        i = -16777216;
      }
    }
    return new BackgroundColorSpan(i);
  }
  
  private static native long nativeCreate(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native void nativeDestroy(long paramLong);
  
  private static native int nativeGetSize(long paramLong);
  
  private static native String nativeGetString(long paramLong, int paramInt);
  
  private static native int[] nativeGetStyle(long paramLong, int paramInt);
  
  private static String subtag(String paramString1, String paramString2)
  {
    int i = paramString1.indexOf(paramString2);
    if (i < 0) {
      return null;
    }
    i += paramString2.length();
    int j = paramString1.indexOf(';', i);
    if (j < 0) {
      return paramString1.substring(i);
    }
    return paramString1.substring(i, j);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      super.finalize();
      return;
    }
    finally
    {
      if (this.mOwnsNative) {
        nativeDestroy(this.mNative);
      }
    }
  }
  
  public CharSequence get(int paramInt)
  {
    for (;;)
    {
      int i;
      String str;
      int[] arrayOfInt;
      int j;
      try
      {
        if (this.mStrings != null)
        {
          localObject1 = this.mStrings[paramInt];
          if (localObject1 != null) {
            return (CharSequence)localObject1;
          }
        }
        else if (this.mSparseStrings != null)
        {
          localObject1 = (CharSequence)this.mSparseStrings.get(paramInt);
          if (localObject1 != null) {
            return (CharSequence)localObject1;
          }
        }
        else
        {
          i = nativeGetSize(this.mNative);
          if ((!this.mUseSparse) || (i <= 250)) {
            continue;
          }
          this.mSparseStrings = new SparseArray();
        }
        str = nativeGetString(this.mNative, paramInt);
        Object localObject1 = str;
        arrayOfInt = nativeGetStyle(this.mNative, paramInt);
        if (arrayOfInt == null) {
          break label571;
        }
        if (this.mStyleIDs != null) {
          break label604;
        }
        this.mStyleIDs = new StyleIDs();
        break label604;
        if (i >= arrayOfInt.length) {
          break label557;
        }
        j = arrayOfInt[i];
        if (j == StyleIDs.-get1(this.mStyleIDs)) {
          break label609;
        }
        if (j == StyleIDs.-get2(this.mStyleIDs))
        {
          break label609;
          this.mStrings = new CharSequence[i];
          continue;
        }
        if (j == StyleIDs.-get10(this.mStyleIDs)) {
          break label609;
        }
      }
      finally {}
      if ((j != StyleIDs.-get9(this.mStyleIDs)) && (j != StyleIDs.-get0(this.mStyleIDs)) && (j != StyleIDs.-get5(this.mStyleIDs)) && (j != StyleIDs.-get7(this.mStyleIDs)) && (j != StyleIDs.-get8(this.mStyleIDs)) && (j != StyleIDs.-get6(this.mStyleIDs)) && (j != StyleIDs.-get3(this.mStyleIDs)) && (j != StyleIDs.-get4(this.mStyleIDs)))
      {
        Object localObject3 = nativeGetString(this.mNative, j);
        if (((String)localObject3).equals("b"))
        {
          StyleIDs.-set1(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("i"))
        {
          StyleIDs.-set2(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("u"))
        {
          StyleIDs.-set10(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("tt"))
        {
          StyleIDs.-set9(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("big"))
        {
          StyleIDs.-set0(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("small"))
        {
          StyleIDs.-set5(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("sup"))
        {
          StyleIDs.-set8(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("sub"))
        {
          StyleIDs.-set7(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("strike"))
        {
          StyleIDs.-set6(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("li"))
        {
          StyleIDs.-set3(this.mStyleIDs, j);
        }
        else if (((String)localObject3).equals("marquee"))
        {
          StyleIDs.-set4(this.mStyleIDs, j);
          break label609;
          label557:
          localObject3 = applyStyles(str, arrayOfInt, this.mStyleIDs);
          label571:
          if (this.mStrings != null) {
            this.mStrings[paramInt] = localObject3;
          }
          for (;;)
          {
            return (CharSequence)localObject3;
            this.mSparseStrings.put(paramInt, localObject3);
          }
          label604:
          i = 0;
          continue;
        }
      }
      label609:
      i += 3;
    }
  }
  
  private static class Height
    implements LineHeightSpan.WithDensity
  {
    private static float sProportion = 0.0F;
    private int mSize;
    
    public Height(int paramInt)
    {
      this.mSize = paramInt;
    }
    
    public void chooseHeight(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Paint.FontMetricsInt paramFontMetricsInt)
    {
      chooseHeight(paramCharSequence, paramInt1, paramInt2, paramInt3, paramInt4, paramFontMetricsInt, null);
    }
    
    public void chooseHeight(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Paint.FontMetricsInt paramFontMetricsInt, TextPaint paramTextPaint)
    {
      paramInt2 = this.mSize;
      paramInt1 = paramInt2;
      if (paramTextPaint != null) {
        paramInt1 = (int)(paramInt2 * paramTextPaint.density);
      }
      if (paramFontMetricsInt.bottom - paramFontMetricsInt.top < paramInt1)
      {
        paramFontMetricsInt.top = (paramFontMetricsInt.bottom - paramInt1);
        paramFontMetricsInt.ascent -= paramInt1;
        return;
      }
      if (sProportion == 0.0F)
      {
        paramCharSequence = new Paint();
        paramCharSequence.setTextSize(100.0F);
        paramTextPaint = new Rect();
        paramCharSequence.getTextBounds("ABCDEFG", 0, 7, paramTextPaint);
        sProportion = paramTextPaint.top / paramCharSequence.ascent();
      }
      paramInt2 = (int)Math.ceil(-paramFontMetricsInt.top * sProportion);
      if (paramInt1 - paramFontMetricsInt.descent >= paramInt2)
      {
        paramFontMetricsInt.top = (paramFontMetricsInt.bottom - paramInt1);
        paramFontMetricsInt.ascent = (paramFontMetricsInt.descent - paramInt1);
        return;
      }
      if (paramInt1 >= paramInt2)
      {
        paramInt2 = -paramInt2;
        paramFontMetricsInt.ascent = paramInt2;
        paramFontMetricsInt.top = paramInt2;
        paramInt1 = paramFontMetricsInt.top + paramInt1;
        paramFontMetricsInt.descent = paramInt1;
        paramFontMetricsInt.bottom = paramInt1;
        return;
      }
      paramInt1 = -paramInt1;
      paramFontMetricsInt.ascent = paramInt1;
      paramFontMetricsInt.top = paramInt1;
      paramFontMetricsInt.descent = 0;
      paramFontMetricsInt.bottom = 0;
    }
  }
  
  static final class StyleIDs
  {
    private int bigId = -1;
    private int boldId = -1;
    private int italicId = -1;
    private int listItemId = -1;
    private int marqueeId = -1;
    private int smallId = -1;
    private int strikeId = -1;
    private int subId = -1;
    private int supId = -1;
    private int ttId = -1;
    private int underlineId = -1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/StringBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */