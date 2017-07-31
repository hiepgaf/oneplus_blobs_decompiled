package android.support.v4.text;

import java.util.Locale;

public final class BidiFormatter
{
  private static final int DEFAULT_FLAGS = 2;
  private static final BidiFormatter DEFAULT_LTR_INSTANCE = new BidiFormatter(false, 2, DEFAULT_TEXT_DIRECTION_HEURISTIC);
  private static final BidiFormatter DEFAULT_RTL_INSTANCE = new BidiFormatter(true, 2, DEFAULT_TEXT_DIRECTION_HEURISTIC);
  private static TextDirectionHeuristicCompat DEFAULT_TEXT_DIRECTION_HEURISTIC = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR;
  private static final int DIR_LTR = -1;
  private static final int DIR_RTL = 1;
  private static final int DIR_UNKNOWN = 0;
  private static final String EMPTY_STRING = "";
  private static final int FLAG_STEREO_RESET = 2;
  private static final char LRE = '‪';
  private static final char LRM = '‎';
  private static final String LRM_STRING = Character.toString('‎');
  private static final char PDF = '‬';
  private static final char RLE = '‫';
  private static final char RLM = '‏';
  private static final String RLM_STRING = Character.toString('‏');
  private final TextDirectionHeuristicCompat mDefaultTextDirectionHeuristicCompat;
  private final int mFlags;
  private final boolean mIsRtlContext;
  
  private BidiFormatter(boolean paramBoolean, int paramInt, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat)
  {
    this.mIsRtlContext = paramBoolean;
    this.mFlags = paramInt;
    this.mDefaultTextDirectionHeuristicCompat = paramTextDirectionHeuristicCompat;
  }
  
  private static int getEntryDir(String paramString)
  {
    return new DirectionalityEstimator(paramString, false).getEntryDir();
  }
  
  private static int getExitDir(String paramString)
  {
    return new DirectionalityEstimator(paramString, false).getExitDir();
  }
  
  public static BidiFormatter getInstance()
  {
    return new Builder().build();
  }
  
  public static BidiFormatter getInstance(Locale paramLocale)
  {
    return new Builder(paramLocale).build();
  }
  
  public static BidiFormatter getInstance(boolean paramBoolean)
  {
    return new Builder(paramBoolean).build();
  }
  
  private static boolean isRtlLocale(Locale paramLocale)
  {
    boolean bool = true;
    if (TextUtilsCompat.getLayoutDirectionFromLocale(paramLocale) != 1) {
      bool = false;
    }
    return bool;
  }
  
  private String markAfter(String paramString, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat)
  {
    boolean bool = paramTextDirectionHeuristicCompat.isRtl(paramString, 0, paramString.length());
    if (this.mIsRtlContext) {
      if (this.mIsRtlContext) {
        break label49;
      }
    }
    for (;;)
    {
      return "";
      if (bool) {}
      while (getExitDir(paramString) == 1) {
        return LRM_STRING;
      }
      break;
      label49:
      if (!bool) {}
      while (getExitDir(paramString) == -1) {
        return RLM_STRING;
      }
    }
  }
  
  private String markBefore(String paramString, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat)
  {
    boolean bool = paramTextDirectionHeuristicCompat.isRtl(paramString, 0, paramString.length());
    if (this.mIsRtlContext) {
      if (this.mIsRtlContext) {
        break label49;
      }
    }
    for (;;)
    {
      return "";
      if (bool) {}
      while (getEntryDir(paramString) == 1) {
        return LRM_STRING;
      }
      break;
      label49:
      if (!bool) {}
      while (getEntryDir(paramString) == -1) {
        return RLM_STRING;
      }
    }
  }
  
  public boolean getStereoReset()
  {
    return (this.mFlags & 0x2) != 0;
  }
  
  public boolean isRtl(String paramString)
  {
    return this.mDefaultTextDirectionHeuristicCompat.isRtl(paramString, 0, paramString.length());
  }
  
  public boolean isRtlContext()
  {
    return this.mIsRtlContext;
  }
  
  public String unicodeWrap(String paramString)
  {
    return unicodeWrap(paramString, this.mDefaultTextDirectionHeuristicCompat, true);
  }
  
  public String unicodeWrap(String paramString, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat)
  {
    return unicodeWrap(paramString, paramTextDirectionHeuristicCompat, true);
  }
  
  public String unicodeWrap(String paramString, TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat, boolean paramBoolean)
  {
    boolean bool = paramTextDirectionHeuristicCompat.isRtl(paramString, 0, paramString.length());
    StringBuilder localStringBuilder = new StringBuilder();
    if (!getStereoReset()) {}
    while (bool == this.mIsRtlContext)
    {
      localStringBuilder.append(paramString);
      if (paramBoolean) {
        break label136;
      }
      return localStringBuilder.toString();
      if (paramBoolean)
      {
        if (!bool) {}
        for (paramTextDirectionHeuristicCompat = TextDirectionHeuristicsCompat.LTR;; paramTextDirectionHeuristicCompat = TextDirectionHeuristicsCompat.RTL)
        {
          localStringBuilder.append(markBefore(paramString, paramTextDirectionHeuristicCompat));
          break;
        }
      }
    }
    if (!bool) {}
    for (char c = '‪';; c = '‫')
    {
      localStringBuilder.append(c);
      localStringBuilder.append(paramString);
      localStringBuilder.append('‬');
      break;
    }
    label136:
    if (!bool) {}
    for (paramTextDirectionHeuristicCompat = TextDirectionHeuristicsCompat.LTR;; paramTextDirectionHeuristicCompat = TextDirectionHeuristicsCompat.RTL)
    {
      localStringBuilder.append(markAfter(paramString, paramTextDirectionHeuristicCompat));
      break;
    }
  }
  
  public String unicodeWrap(String paramString, boolean paramBoolean)
  {
    return unicodeWrap(paramString, this.mDefaultTextDirectionHeuristicCompat, paramBoolean);
  }
  
  public static final class Builder
  {
    private int mFlags;
    private boolean mIsRtlContext;
    private TextDirectionHeuristicCompat mTextDirectionHeuristicCompat;
    
    public Builder()
    {
      initialize(BidiFormatter.isRtlLocale(Locale.getDefault()));
    }
    
    public Builder(Locale paramLocale)
    {
      initialize(BidiFormatter.isRtlLocale(paramLocale));
    }
    
    public Builder(boolean paramBoolean)
    {
      initialize(paramBoolean);
    }
    
    private static BidiFormatter getDefaultInstanceFromContext(boolean paramBoolean)
    {
      if (!paramBoolean) {
        return BidiFormatter.DEFAULT_LTR_INSTANCE;
      }
      return BidiFormatter.DEFAULT_RTL_INSTANCE;
    }
    
    private void initialize(boolean paramBoolean)
    {
      this.mIsRtlContext = paramBoolean;
      this.mTextDirectionHeuristicCompat = BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC;
      this.mFlags = 2;
    }
    
    public BidiFormatter build()
    {
      if (this.mFlags != 2) {}
      while (this.mTextDirectionHeuristicCompat != BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC) {
        return new BidiFormatter(this.mIsRtlContext, this.mFlags, this.mTextDirectionHeuristicCompat, null);
      }
      return getDefaultInstanceFromContext(this.mIsRtlContext);
    }
    
    public Builder setTextDirectionHeuristic(TextDirectionHeuristicCompat paramTextDirectionHeuristicCompat)
    {
      this.mTextDirectionHeuristicCompat = paramTextDirectionHeuristicCompat;
      return this;
    }
    
    public Builder stereoReset(boolean paramBoolean)
    {
      if (!paramBoolean)
      {
        this.mFlags &= 0xFFFFFFFD;
        return this;
      }
      this.mFlags |= 0x2;
      return this;
    }
  }
  
  private static class DirectionalityEstimator
  {
    private static final byte[] DIR_TYPE_CACHE = new byte['܀'];
    private static final int DIR_TYPE_CACHE_SIZE = 1792;
    private int charIndex;
    private final boolean isHtml;
    private char lastChar;
    private final int length;
    private final String text;
    
    static
    {
      int i = 0;
      for (;;)
      {
        if (i >= 1792) {
          return;
        }
        DIR_TYPE_CACHE[i] = Character.getDirectionality(i);
        i += 1;
      }
    }
    
    DirectionalityEstimator(String paramString, boolean paramBoolean)
    {
      this.text = paramString;
      this.isHtml = paramBoolean;
      this.length = paramString.length();
    }
    
    private static byte getCachedDirectionality(char paramChar)
    {
      if (paramChar >= '܀') {
        return Character.getDirectionality(paramChar);
      }
      return DIR_TYPE_CACHE[paramChar];
    }
    
    private byte skipEntityBackward()
    {
      int i = this.charIndex;
      for (;;)
      {
        if (this.charIndex <= 0) {}
        do
        {
          this.charIndex = i;
          this.lastChar = ';';
          return 13;
          String str = this.text;
          int j = this.charIndex - 1;
          this.charIndex = j;
          this.lastChar = str.charAt(j);
          if (this.lastChar == '&') {
            break;
          }
        } while (this.lastChar == ';');
      }
      return 12;
    }
    
    private byte skipEntityForward()
    {
      if (this.charIndex >= this.length) {}
      for (;;)
      {
        return 12;
        String str = this.text;
        int i = this.charIndex;
        this.charIndex = (i + 1);
        i = str.charAt(i);
        this.lastChar = ((char)i);
        if (i != 59) {
          break;
        }
      }
    }
    
    private byte skipTagBackward()
    {
      int i = this.charIndex;
      for (;;)
      {
        label5:
        if (this.charIndex <= 0) {}
        String str;
        int j;
        do
        {
          this.charIndex = i;
          this.lastChar = '>';
          return 13;
          str = this.text;
          j = this.charIndex - 1;
          this.charIndex = j;
          this.lastChar = str.charAt(j);
          if (this.lastChar == '<') {
            break;
          }
        } while (this.lastChar == '>');
        if (this.lastChar == '"') {}
        while (this.lastChar == '\'')
        {
          j = this.lastChar;
          if (this.charIndex <= 0) {
            break label5;
          }
          str = this.text;
          int k = this.charIndex - 1;
          this.charIndex = k;
          k = str.charAt(k);
          this.lastChar = ((char)k);
          if (k != j) {
            break;
          }
          break label5;
          return 12;
        }
      }
    }
    
    private byte skipTagForward()
    {
      int i = this.charIndex;
      for (;;)
      {
        label5:
        if (this.charIndex >= this.length)
        {
          this.charIndex = i;
          this.lastChar = '<';
          return 13;
        }
        String str = this.text;
        int j = this.charIndex;
        this.charIndex = (j + 1);
        this.lastChar = str.charAt(j);
        if (this.lastChar != '>') {
          if (this.lastChar != '"') {
            break label134;
          }
        }
        label134:
        while (this.lastChar == '\'')
        {
          j = this.lastChar;
          if (this.charIndex >= this.length) {
            break label5;
          }
          str = this.text;
          int k = this.charIndex;
          this.charIndex = (k + 1);
          k = str.charAt(k);
          this.lastChar = ((char)k);
          if (k != j) {
            break;
          }
          break label5;
          return 12;
        }
      }
    }
    
    byte dirTypeBackward()
    {
      this.lastChar = this.text.charAt(this.charIndex - 1);
      byte b;
      if (!Character.isLowSurrogate(this.lastChar))
      {
        this.charIndex -= 1;
        b = getCachedDirectionality(this.lastChar);
        if (this.isHtml) {
          break label84;
        }
      }
      label84:
      do
      {
        return b;
        int i = Character.codePointBefore(this.text, this.charIndex);
        this.charIndex -= Character.charCount(i);
        return Character.getDirectionality(i);
        if (this.lastChar == '>') {
          break;
        }
      } while (this.lastChar != ';');
      return skipEntityBackward();
      return skipTagBackward();
    }
    
    byte dirTypeForward()
    {
      this.lastChar = this.text.charAt(this.charIndex);
      byte b;
      if (!Character.isHighSurrogate(this.lastChar))
      {
        this.charIndex += 1;
        b = getCachedDirectionality(this.lastChar);
        if (this.isHtml) {
          break label82;
        }
      }
      label82:
      do
      {
        return b;
        int i = Character.codePointAt(this.text, this.charIndex);
        this.charIndex += Character.charCount(i);
        return Character.getDirectionality(i);
        if (this.lastChar == '<') {
          break;
        }
      } while (this.lastChar != '&');
      return skipEntityForward();
      return skipTagForward();
    }
    
    int getEntryDir()
    {
      this.charIndex = 0;
      int j = 0;
      int k = 0;
      int i = 0;
      if (this.charIndex >= this.length)
      {
        label22:
        if (j == 0) {
          break label190;
        }
        if (k != 0) {
          break label192;
        }
      }
      for (;;)
      {
        if (this.charIndex <= 0)
        {
          return 0;
          if (j != 0) {
            break label22;
          }
          switch (dirTypeForward())
          {
          case 9: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          case 10: 
          case 11: 
          case 12: 
          case 13: 
          default: 
            j = i;
            break;
          case 14: 
          case 15: 
            i += 1;
            k = -1;
            break;
          case 16: 
          case 17: 
            i += 1;
            k = 1;
            break;
          case 18: 
            i -= 1;
            k = 0;
            break;
          case 0: 
            if (i != 0)
            {
              j = i;
              break;
            }
            return -1;
          case 1: 
          case 2: 
            if (i != 0)
            {
              j = i;
              break;
            }
            return 1;
            label190:
            return 0;
            label192:
            return k;
          }
        }
        switch (dirTypeBackward())
        {
        default: 
          break;
        case 14: 
        case 15: 
          if (j != i) {
            i -= 1;
          } else {
            return -1;
          }
          break;
        case 16: 
        case 17: 
          if (j != i) {
            i -= 1;
          } else {
            return 1;
          }
          break;
        case 18: 
          i += 1;
        }
      }
    }
    
    int getExitDir()
    {
      this.charIndex = this.length;
      int j = 0;
      int i = 0;
      for (;;)
      {
        if (this.charIndex <= 0) {
          return 0;
        }
        switch (dirTypeBackward())
        {
        case 9: 
        case 3: 
        case 4: 
        case 5: 
        case 6: 
        case 7: 
        case 8: 
        case 10: 
        case 11: 
        case 12: 
        case 13: 
        default: 
          if (j == 0) {
            j = i;
          }
          break;
        case 0: 
          if (i != 0)
          {
            if (j == 0) {
              j = i;
            }
          }
          else {
            return -1;
          }
          break;
        case 14: 
        case 15: 
          if (j != i) {
            i -= 1;
          } else {
            return -1;
          }
          break;
        case 1: 
        case 2: 
          if (i != 0)
          {
            if (j == 0) {
              j = i;
            }
          }
          else {
            return 1;
          }
          break;
        case 16: 
        case 17: 
          if (j != i) {
            i -= 1;
          } else {
            return 1;
          }
          break;
        case 18: 
          i += 1;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/text/BidiFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */