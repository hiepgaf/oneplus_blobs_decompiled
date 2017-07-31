package android.os;

import java.util.Arrays;

public class PatternMatcher
  implements Parcelable
{
  public static final Parcelable.Creator<PatternMatcher> CREATOR = new Parcelable.Creator()
  {
    public PatternMatcher createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PatternMatcher(paramAnonymousParcel);
    }
    
    public PatternMatcher[] newArray(int paramAnonymousInt)
    {
      return new PatternMatcher[paramAnonymousInt];
    }
  };
  private static final int MAX_PATTERN_STORAGE = 2048;
  private static final int NO_MATCH = -1;
  private static final int PARSED_MODIFIER_ONE_OR_MORE = -8;
  private static final int PARSED_MODIFIER_RANGE_START = -5;
  private static final int PARSED_MODIFIER_RANGE_STOP = -6;
  private static final int PARSED_MODIFIER_ZERO_OR_MORE = -7;
  private static final int PARSED_TOKEN_CHAR_ANY = -4;
  private static final int PARSED_TOKEN_CHAR_SET_INVERSE_START = -2;
  private static final int PARSED_TOKEN_CHAR_SET_START = -1;
  private static final int PARSED_TOKEN_CHAR_SET_STOP = -3;
  public static final int PATTERN_ADVANCED_GLOB = 3;
  public static final int PATTERN_LITERAL = 0;
  public static final int PATTERN_PREFIX = 1;
  public static final int PATTERN_SIMPLE_GLOB = 2;
  private static final String TAG = "PatternMatcher";
  private static final int TOKEN_TYPE_ANY = 1;
  private static final int TOKEN_TYPE_INVERSE_SET = 3;
  private static final int TOKEN_TYPE_LITERAL = 0;
  private static final int TOKEN_TYPE_SET = 2;
  private static final int[] sParsedPatternScratch = new int['ࠀ'];
  private final int[] mParsedPattern;
  private final String mPattern;
  private final int mType;
  
  public PatternMatcher(Parcel paramParcel)
  {
    this.mPattern = paramParcel.readString();
    this.mType = paramParcel.readInt();
    this.mParsedPattern = paramParcel.createIntArray();
  }
  
  public PatternMatcher(String paramString, int paramInt)
  {
    this.mPattern = paramString;
    this.mType = paramInt;
    if (this.mType == 3)
    {
      this.mParsedPattern = parseAndVerifyAdvancedPattern(paramString);
      return;
    }
    this.mParsedPattern = null;
  }
  
  private static boolean isParsedModifier(int paramInt)
  {
    if ((paramInt == -8) || (paramInt == -7)) {}
    while ((paramInt == -6) || (paramInt == -5)) {
      return true;
    }
    return false;
  }
  
  static boolean matchAdvancedPattern(int[] paramArrayOfInt, String paramString)
  {
    int j = 0;
    int i1 = 0;
    int i3 = paramArrayOfInt.length;
    int i4 = paramString.length();
    int m = 0;
    int i2 = 0;
    while (j < i3)
    {
      int i = paramArrayOfInt[j];
      int n;
      int k;
      switch (i)
      {
      case -3: 
      default: 
        m = j;
        n = 0;
        j += 1;
        if (j >= i3)
        {
          k = 1;
          i = 1;
        }
        break;
      }
      for (;;)
      {
        if (i <= k) {
          break label246;
        }
        return false;
        n = 1;
        j += 1;
        break;
        if (i == -1) {}
        for (n = 2;; n = 3)
        {
          m = j + 1;
          do
          {
            i = j + 1;
            if (i >= i3) {
              break;
            }
            j = i;
          } while (paramArrayOfInt[i] != -3);
          i2 = i - 1;
          j = i + 1;
          break;
        }
        switch (paramArrayOfInt[j])
        {
        case -6: 
        default: 
          k = 1;
          i = 1;
          break;
        case -7: 
          i = 0;
          k = Integer.MAX_VALUE;
          j += 1;
          break;
        case -8: 
          i = 1;
          k = Integer.MAX_VALUE;
          j += 1;
          break;
        case -5: 
          j += 1;
          i = paramArrayOfInt[j];
          j += 1;
          k = paramArrayOfInt[j];
          j += 2;
        }
      }
      label246:
      i = matchChars(paramString, i1, i4, n, i, k, paramArrayOfInt, m, i2);
      if (i == -1) {
        return false;
      }
      i1 += i;
    }
    return (j >= i3) && (i1 >= i4);
  }
  
  private static boolean matchChar(String paramString, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, int paramInt4, int paramInt5)
  {
    if (paramInt1 >= paramInt2) {
      return false;
    }
    switch (paramInt3)
    {
    default: 
      return false;
    case 1: 
      return true;
    case 2: 
      paramInt2 = paramInt4;
      while (paramInt2 < paramInt5)
      {
        paramInt3 = paramString.charAt(paramInt1);
        if ((paramInt3 >= paramArrayOfInt[paramInt2]) && (paramInt3 <= paramArrayOfInt[(paramInt2 + 1)])) {
          return true;
        }
        paramInt2 += 2;
      }
      return false;
    case 3: 
      paramInt2 = paramInt4;
      while (paramInt2 < paramInt5)
      {
        paramInt3 = paramString.charAt(paramInt1);
        if ((paramInt3 >= paramArrayOfInt[paramInt2]) && (paramInt3 <= paramArrayOfInt[(paramInt2 + 1)])) {
          return false;
        }
        paramInt2 += 2;
      }
      return true;
    }
    return paramString.charAt(paramInt1) == paramArrayOfInt[paramInt4];
  }
  
  private static int matchChars(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, int paramInt6, int paramInt7)
  {
    int i = 0;
    while ((i < paramInt5) && (matchChar(paramString, paramInt1 + i, paramInt2, paramInt3, paramArrayOfInt, paramInt6, paramInt7))) {
      i += 1;
    }
    paramInt1 = i;
    if (i < paramInt4) {
      paramInt1 = -1;
    }
    return paramInt1;
  }
  
  static boolean matchGlobPattern(String paramString1, String paramString2)
  {
    int i3 = paramString1.length();
    if (i3 <= 0) {
      return paramString2.length() <= 0;
    }
    int i4 = paramString2.length();
    int k = 0;
    int i = 0;
    int j = paramString1.charAt(0);
    while ((k < i3) && (i < i4))
    {
      int i2 = k + 1;
      label73:
      int n;
      label82:
      int i1;
      int m;
      if (i2 < i3)
      {
        k = paramString1.charAt(i2);
        if (j != 92) {
          break label160;
        }
        n = 1;
        i1 = j;
        m = i2;
        j = k;
        if (n != 0)
        {
          m = i2 + 1;
          if (m >= i3) {
            break label166;
          }
          j = paramString1.charAt(m);
          i1 = k;
        }
      }
      for (;;)
      {
        if (j == 42)
        {
          j = i;
          if (n == 0)
          {
            j = i;
            if (i1 == 46)
            {
              if (m >= i3 - 1)
              {
                return true;
                k = 0;
                break label73;
                label160:
                n = 0;
                break label82;
                label166:
                j = 0;
                i1 = k;
                continue;
              }
              n = m + 1;
              i1 = paramString1.charAt(n);
              k = i;
              m = n;
              j = i1;
              if (i1 == 92)
              {
                m = n + 1;
                if (m >= i3) {
                  break label248;
                }
                j = paramString1.charAt(m);
                k = i;
              }
              for (;;)
              {
                if (paramString2.charAt(k) == j) {}
                label248:
                do
                {
                  if (k != i4) {
                    break label276;
                  }
                  return false;
                  j = 0;
                  k = i;
                  break;
                  i = k + 1;
                  k = i;
                } while (i >= i4);
                k = i;
              }
              label276:
              m += 1;
              if (m < i3) {}
              for (j = paramString1.charAt(m);; j = 0)
              {
                i = k + 1;
                k = m;
                break;
              }
            }
          }
          while (paramString2.charAt(j) == i1)
          {
            j += 1;
            i = j;
            if (j >= i4) {
              break;
            }
          }
          i = j;
          k = m + 1;
          if (k < i3)
          {
            j = paramString1.charAt(k);
            break;
          }
          j = 0;
          break;
        }
      }
      if ((i1 != 46) && (paramString2.charAt(i) != i1)) {
        return false;
      }
      i += 1;
      k = m;
    }
    if ((k >= i3) && (i >= i4)) {
      return true;
    }
    return (k == i3 - 2) && (paramString1.charAt(k) == '.') && (paramString1.charAt(k + 1) == '*');
  }
  
  static boolean matchPattern(String paramString1, String paramString2, int[] paramArrayOfInt, int paramInt)
  {
    if (paramString1 == null) {
      return false;
    }
    if (paramInt == 0) {
      return paramString2.equals(paramString1);
    }
    if (paramInt == 1) {
      return paramString1.startsWith(paramString2);
    }
    if (paramInt == 2) {
      return matchGlobPattern(paramString2, paramString1);
    }
    if (paramInt == 3) {
      return matchAdvancedPattern(paramArrayOfInt, paramString1);
    }
    return false;
  }
  
  static int[] parseAndVerifyAdvancedPattern(String paramString)
  {
    int j = 0;
    int i5;
    int i1;
    int n;
    int m;
    int i;
    try
    {
      i5 = paramString.length();
      i1 = 0;
      n = 0;
      m = 0;
      i = 0;
      if (j >= i5) {
        break label825;
      }
      if (i > 2045) {
        throw new IllegalArgumentException("Pattern is too large!");
      }
    }
    finally {}
    int i2 = paramString.charAt(j);
    int i3 = 0;
    int k = 0;
    switch (i2)
    {
    }
    label140:
    label173:
    label694:
    label825:
    label857:
    label880:
    label897:
    label914:
    label917:
    label920:
    label923:
    label926:
    label929:
    for (;;)
    {
      Object localObject;
      if (i1 != 0) {
        if (m != 0)
        {
          localObject = sParsedPatternScratch;
          k = i + 1;
          localObject[i] = i2;
          m = 0;
          i = k;
          break label880;
          if (paramString.charAt(j + 1) == '^')
          {
            localObject = sParsedPatternScratch;
            k = i + 1;
            localObject[i] = -2;
            j += 1;
            i = k;
            break label897;
          }
          localObject = sParsedPatternScratch;
          k = i + 1;
          localObject[i] = -1;
          i = k;
          break label897;
        }
      }
      do
      {
        k = sParsedPatternScratch[(i - 1)];
        if ((k == -1) || (k == -2)) {
          throw new IllegalArgumentException("You must define characters in a set.");
        }
        localObject = sParsedPatternScratch;
        int i4 = i + 1;
        localObject[i] = -3;
        i1 = 0;
        m = 0;
        k = i3;
        i = i4;
        break label914;
        if (i1 == 0)
        {
          if ((i == 0) || (isParsedModifier(sParsedPatternScratch[(i - 1)]))) {
            throw new IllegalArgumentException("Modifier must follow a token.");
          }
          localObject = sParsedPatternScratch;
          i3 = i + 1;
          localObject[i] = -5;
          j += 1;
          n = 1;
          i = i3;
          break label917;
          if (n != 0)
          {
            localObject = sParsedPatternScratch;
            i3 = i + 1;
            localObject[i] = -6;
            n = 0;
            i = i3;
            break label920;
            if (i1 == 0)
            {
              if ((i == 0) || (isParsedModifier(sParsedPatternScratch[(i - 1)]))) {
                throw new IllegalArgumentException("Modifier must follow a token.");
              }
              localObject = sParsedPatternScratch;
              i3 = i + 1;
              localObject[i] = -7;
              i = i3;
              break label923;
              if (i1 == 0)
              {
                if ((i == 0) || (isParsedModifier(sParsedPatternScratch[(i - 1)]))) {
                  throw new IllegalArgumentException("Modifier must follow a token.");
                }
                localObject = sParsedPatternScratch;
                i3 = i + 1;
                localObject[i] = -8;
                i = i3;
                break label926;
                if (i1 == 0)
                {
                  localObject = sParsedPatternScratch;
                  i3 = i + 1;
                  localObject[i] = -4;
                  i = i3;
                  break label929;
                  if (j + 1 >= i5) {
                    throw new IllegalArgumentException("Escape found at end of pattern!");
                  }
                  j += 1;
                  i2 = paramString.charAt(j);
                  k = 1;
                  break label140;
                  if ((j + 2 < i5) && (paramString.charAt(j + 1) == '-') && (paramString.charAt(j + 2) != ']'))
                  {
                    m = 1;
                    localObject = sParsedPatternScratch;
                    k = i + 1;
                    localObject[i] = i2;
                    j += 1;
                    i = k;
                    break label880;
                  }
                  localObject = sParsedPatternScratch;
                  k = i + 1;
                  localObject[i] = i2;
                  sParsedPatternScratch[k] = i2;
                  i = k + 1;
                  break label880;
                  if (n != 0)
                  {
                    i2 = paramString.indexOf('}', j);
                    if (i2 < 0) {
                      throw new IllegalArgumentException("Range not ended with '}'");
                    }
                    localObject = paramString.substring(j, i2);
                    j = ((String)localObject).indexOf(',');
                    if (j < 0) {
                      try
                      {
                        k = Integer.parseInt((String)localObject);
                        j = k;
                        if (k > j) {
                          throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                        }
                      }
                      catch (NumberFormatException paramString) {}
                    }
                  }
                  for (;;)
                  {
                    throw new IllegalArgumentException("Range number format incorrect", paramString);
                    k = Integer.parseInt(((String)localObject).substring(0, j));
                    if (j == ((String)localObject).length() - 1)
                    {
                      j = Integer.MAX_VALUE;
                      break label694;
                    }
                    j = Integer.parseInt(((String)localObject).substring(j + 1));
                    break label694;
                    localObject = sParsedPatternScratch;
                    i3 = i + 1;
                    localObject[i] = k;
                    try
                    {
                      localObject = sParsedPatternScratch;
                      i = i3 + 1;
                      localObject[i3] = j;
                      j = i2;
                    }
                    catch (NumberFormatException paramString) {}
                    if (k == 0) {
                      break label857;
                    }
                    localObject = sParsedPatternScratch;
                    k = i + 1;
                    localObject[i] = i2;
                    i = k;
                    break label880;
                    if (i1 != 0) {
                      throw new IllegalArgumentException("Set was not terminated!");
                    }
                    paramString = Arrays.copyOf(sParsedPatternScratch, i);
                    return paramString;
                  }
                  break label880;
                }
                break label929;
              }
              break label926;
            }
            break label923;
          }
          break label920;
        }
        break label917;
        k = 1;
        break label140;
        j += 1;
        break;
        if (i1 == 0) {
          break label173;
        }
        k = 1;
        break label140;
        j += 1;
        i1 = 1;
        break;
      } while (i1 != 0);
      k = 1;
      continue;
      continue;
      continue;
      continue;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public final String getPath()
  {
    return this.mPattern;
  }
  
  public final int getType()
  {
    return this.mType;
  }
  
  public boolean match(String paramString)
  {
    return matchPattern(paramString, this.mPattern, this.mParsedPattern, this.mType);
  }
  
  public String toString()
  {
    String str = "? ";
    switch (this.mType)
    {
    }
    for (;;)
    {
      return "PatternMatcher{" + str + this.mPattern + "}";
      str = "LITERAL: ";
      continue;
      str = "PREFIX: ";
      continue;
      str = "GLOB: ";
      continue;
      str = "ADVANCED: ";
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPattern);
    paramParcel.writeInt(this.mType);
    paramParcel.writeIntArray(this.mParsedPattern);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/PatternMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */