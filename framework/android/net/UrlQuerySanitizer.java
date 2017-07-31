package android.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

public class UrlQuerySanitizer
{
  private static final ValueSanitizer sAllButNulAndAngleBracketsLegal = new IllegalCharacterValueSanitizer(1439);
  private static final ValueSanitizer sAllButNulLegal;
  private static final ValueSanitizer sAllButWhitespaceLegal;
  private static final ValueSanitizer sAllIllegal = new IllegalCharacterValueSanitizer(0);
  private static final ValueSanitizer sAmpAndSpaceLegal;
  private static final ValueSanitizer sAmpLegal;
  private static final ValueSanitizer sSpaceLegal;
  private static final ValueSanitizer sURLLegal;
  private static final ValueSanitizer sUrlAndSpaceLegal;
  private boolean mAllowUnregisteredParamaters;
  private final HashMap<String, String> mEntries = new HashMap();
  private final ArrayList<ParameterValuePair> mEntriesList = new ArrayList();
  private boolean mPreferFirstRepeatedParameter;
  private final HashMap<String, ValueSanitizer> mSanitizers = new HashMap();
  private ValueSanitizer mUnregisteredParameterValueSanitizer = getAllIllegal();
  
  static
  {
    sAllButNulLegal = new IllegalCharacterValueSanitizer(1535);
    sAllButWhitespaceLegal = new IllegalCharacterValueSanitizer(1532);
    sURLLegal = new IllegalCharacterValueSanitizer(404);
    sUrlAndSpaceLegal = new IllegalCharacterValueSanitizer(405);
    sAmpLegal = new IllegalCharacterValueSanitizer(128);
    sAmpAndSpaceLegal = new IllegalCharacterValueSanitizer(129);
    sSpaceLegal = new IllegalCharacterValueSanitizer(1);
  }
  
  public UrlQuerySanitizer() {}
  
  public UrlQuerySanitizer(String paramString)
  {
    setAllowUnregisteredParamaters(true);
    parseUrl(paramString);
  }
  
  public static final ValueSanitizer getAllButNulAndAngleBracketsLegal()
  {
    return sAllButNulAndAngleBracketsLegal;
  }
  
  public static final ValueSanitizer getAllButNulLegal()
  {
    return sAllButNulLegal;
  }
  
  public static final ValueSanitizer getAllButWhitespaceLegal()
  {
    return sAllButWhitespaceLegal;
  }
  
  public static final ValueSanitizer getAllIllegal()
  {
    return sAllIllegal;
  }
  
  public static final ValueSanitizer getAmpAndSpaceLegal()
  {
    return sAmpAndSpaceLegal;
  }
  
  public static final ValueSanitizer getAmpLegal()
  {
    return sAmpLegal;
  }
  
  public static final ValueSanitizer getSpaceLegal()
  {
    return sSpaceLegal;
  }
  
  public static final ValueSanitizer getUrlAndSpaceLegal()
  {
    return sUrlAndSpaceLegal;
  }
  
  public static final ValueSanitizer getUrlLegal()
  {
    return sURLLegal;
  }
  
  protected void addSanitizedEntry(String paramString1, String paramString2)
  {
    this.mEntriesList.add(new ParameterValuePair(paramString1, paramString2));
    if ((this.mPreferFirstRepeatedParameter) && (this.mEntries.containsKey(paramString1))) {
      return;
    }
    this.mEntries.put(paramString1, paramString2);
  }
  
  protected void clear()
  {
    this.mEntries.clear();
    this.mEntriesList.clear();
  }
  
  protected int decodeHexDigit(char paramChar)
  {
    if ((paramChar >= '0') && (paramChar <= '9')) {
      return paramChar - '0';
    }
    if ((paramChar >= 'A') && (paramChar <= 'F')) {
      return paramChar - 'A' + 10;
    }
    if ((paramChar >= 'a') && (paramChar <= 'f')) {
      return paramChar - 'a' + 10;
    }
    return -1;
  }
  
  public boolean getAllowUnregisteredParamaters()
  {
    return this.mAllowUnregisteredParamaters;
  }
  
  public ValueSanitizer getEffectiveValueSanitizer(String paramString)
  {
    ValueSanitizer localValueSanitizer = getValueSanitizer(paramString);
    paramString = localValueSanitizer;
    if (localValueSanitizer == null)
    {
      paramString = localValueSanitizer;
      if (this.mAllowUnregisteredParamaters) {
        paramString = getUnregisteredParameterValueSanitizer();
      }
    }
    return paramString;
  }
  
  public List<ParameterValuePair> getParameterList()
  {
    return this.mEntriesList;
  }
  
  public Set<String> getParameterSet()
  {
    return this.mEntries.keySet();
  }
  
  public boolean getPreferFirstRepeatedParameter()
  {
    return this.mPreferFirstRepeatedParameter;
  }
  
  public ValueSanitizer getUnregisteredParameterValueSanitizer()
  {
    return this.mUnregisteredParameterValueSanitizer;
  }
  
  public String getValue(String paramString)
  {
    return (String)this.mEntries.get(paramString);
  }
  
  public ValueSanitizer getValueSanitizer(String paramString)
  {
    return (ValueSanitizer)this.mSanitizers.get(paramString);
  }
  
  public boolean hasParameter(String paramString)
  {
    return this.mEntries.containsKey(paramString);
  }
  
  protected boolean isHexDigit(char paramChar)
  {
    boolean bool = false;
    if (decodeHexDigit(paramChar) >= 0) {
      bool = true;
    }
    return bool;
  }
  
  protected void parseEntry(String paramString1, String paramString2)
  {
    paramString1 = unescape(paramString1);
    ValueSanitizer localValueSanitizer = getEffectiveValueSanitizer(paramString1);
    if (localValueSanitizer == null) {
      return;
    }
    addSanitizedEntry(paramString1, localValueSanitizer.sanitize(unescape(paramString2)));
  }
  
  public void parseQuery(String paramString)
  {
    clear();
    paramString = new StringTokenizer(paramString, "&");
    while (paramString.hasMoreElements())
    {
      String str = paramString.nextToken();
      if (str.length() > 0)
      {
        int i = str.indexOf('=');
        if (i < 0) {
          parseEntry(str, "");
        } else {
          parseEntry(str.substring(0, i), str.substring(i + 1));
        }
      }
    }
  }
  
  public void parseUrl(String paramString)
  {
    int i = paramString.indexOf('?');
    if (i >= 0) {}
    for (paramString = paramString.substring(i + 1);; paramString = "")
    {
      parseQuery(paramString);
      return;
    }
  }
  
  public void registerParameter(String paramString, ValueSanitizer paramValueSanitizer)
  {
    if (paramValueSanitizer == null) {
      this.mSanitizers.remove(paramString);
    }
    this.mSanitizers.put(paramString, paramValueSanitizer);
  }
  
  public void registerParameters(String[] paramArrayOfString, ValueSanitizer paramValueSanitizer)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      this.mSanitizers.put(paramArrayOfString[i], paramValueSanitizer);
      i += 1;
    }
  }
  
  public void setAllowUnregisteredParamaters(boolean paramBoolean)
  {
    this.mAllowUnregisteredParamaters = paramBoolean;
  }
  
  public void setPreferFirstRepeatedParameter(boolean paramBoolean)
  {
    this.mPreferFirstRepeatedParameter = paramBoolean;
  }
  
  public void setUnregisteredParameterValueSanitizer(ValueSanitizer paramValueSanitizer)
  {
    this.mUnregisteredParameterValueSanitizer = paramValueSanitizer;
  }
  
  public String unescape(String paramString)
  {
    int j = paramString.indexOf('%');
    int i = j;
    if (j < 0)
    {
      j = paramString.indexOf('+');
      i = j;
      if (j < 0) {
        return paramString;
      }
    }
    int k = paramString.length();
    StringBuilder localStringBuilder = new StringBuilder(k);
    localStringBuilder.append(paramString.substring(0, i));
    if (i < k)
    {
      char c2 = paramString.charAt(i);
      char c1;
      if (c2 == '+')
      {
        c1 = ' ';
        j = i;
      }
      for (;;)
      {
        localStringBuilder.append(c1);
        i = j + 1;
        break;
        c1 = c2;
        j = i;
        if (c2 == '%')
        {
          c1 = c2;
          j = i;
          if (i + 2 < k)
          {
            char c3 = paramString.charAt(i + 1);
            char c4 = paramString.charAt(i + 2);
            c1 = c2;
            j = i;
            if (isHexDigit(c3))
            {
              c1 = c2;
              j = i;
              if (isHexDigit(c4))
              {
                c1 = (char)(decodeHexDigit(c3) * 16 + decodeHexDigit(c4));
                j = i + 2;
              }
            }
          }
        }
      }
    }
    return localStringBuilder.toString();
  }
  
  public static class IllegalCharacterValueSanitizer
    implements UrlQuerySanitizer.ValueSanitizer
  {
    public static final int ALL_BUT_NUL_AND_ANGLE_BRACKETS_LEGAL = 1439;
    public static final int ALL_BUT_NUL_LEGAL = 1535;
    public static final int ALL_BUT_WHITESPACE_LEGAL = 1532;
    public static final int ALL_ILLEGAL = 0;
    public static final int ALL_OK = 2047;
    public static final int ALL_WHITESPACE_OK = 3;
    public static final int AMP_AND_SPACE_LEGAL = 129;
    public static final int AMP_LEGAL = 128;
    public static final int AMP_OK = 128;
    public static final int DQUOTE_OK = 8;
    public static final int GT_OK = 64;
    private static final String JAVASCRIPT_PREFIX = "javascript:";
    public static final int LT_OK = 32;
    private static final int MIN_SCRIPT_PREFIX_LENGTH = Math.min("javascript:".length(), "vbscript:".length());
    public static final int NON_7_BIT_ASCII_OK = 4;
    public static final int NUL_OK = 512;
    public static final int OTHER_WHITESPACE_OK = 2;
    public static final int PCT_OK = 256;
    public static final int SCRIPT_URL_OK = 1024;
    public static final int SPACE_LEGAL = 1;
    public static final int SPACE_OK = 1;
    public static final int SQUOTE_OK = 16;
    public static final int URL_AND_SPACE_LEGAL = 405;
    public static final int URL_LEGAL = 404;
    private static final String VBSCRIPT_PREFIX = "vbscript:";
    private int mFlags;
    
    public IllegalCharacterValueSanitizer(int paramInt)
    {
      this.mFlags = paramInt;
    }
    
    private boolean characterIsLegal(char paramChar)
    {
      switch (paramChar)
      {
      default: 
        if ((paramChar < ' ') || (paramChar >= '')) {
          break;
        }
      }
      while ((paramChar >= '') && ((this.mFlags & 0x4) != 0))
      {
        return true;
        return (this.mFlags & 0x1) != 0;
        if ((this.mFlags & 0x2) != 0) {
          return true;
        }
        return false;
        return (this.mFlags & 0x8) != 0;
        if ((this.mFlags & 0x10) != 0) {
          return true;
        }
        return false;
        return (this.mFlags & 0x20) != 0;
        if ((this.mFlags & 0x40) != 0) {
          return true;
        }
        return false;
        return (this.mFlags & 0x80) != 0;
        if ((this.mFlags & 0x100) != 0) {
          return true;
        }
        return false;
        return (this.mFlags & 0x200) != 0;
      }
      return false;
    }
    
    private boolean isWhitespace(char paramChar)
    {
      switch (paramChar)
      {
      default: 
        return false;
      }
      return true;
    }
    
    private String trimWhitespace(String paramString)
    {
      int j = 0;
      int m = paramString.length() - 1;
      int i = m;
      int k;
      for (;;)
      {
        k = i;
        if (j > m) {
          break;
        }
        k = i;
        if (!isWhitespace(paramString.charAt(j))) {
          break;
        }
        j += 1;
      }
      while ((k >= j) && (isWhitespace(paramString.charAt(k)))) {
        k -= 1;
      }
      if ((j == 0) && (k == m)) {
        return paramString;
      }
      return paramString.substring(j, k + 1);
    }
    
    public String sanitize(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      int i = paramString.length();
      if (((this.mFlags & 0x400) != 0) && (i >= MIN_SCRIPT_PREFIX_LENGTH))
      {
        str = paramString.toLowerCase(Locale.ROOT);
        if ((str.startsWith("javascript:")) || (str.startsWith("vbscript:"))) {
          return "";
        }
      }
      String str = paramString;
      if ((this.mFlags & 0x3) == 0)
      {
        str = trimWhitespace(paramString);
        i = str.length();
      }
      paramString = new StringBuilder(i);
      int j = 0;
      if (j < i)
      {
        char c2 = str.charAt(j);
        char c1 = c2;
        if (!characterIsLegal(c2)) {
          if ((this.mFlags & 0x1) == 0) {
            break label154;
          }
        }
        label154:
        for (c1 = ' ';; c1 = '_')
        {
          paramString.append(c1);
          j += 1;
          break;
        }
      }
      return paramString.toString();
    }
  }
  
  public class ParameterValuePair
  {
    public String mParameter;
    public String mValue;
    
    public ParameterValuePair(String paramString1, String paramString2)
    {
      this.mParameter = paramString1;
      this.mValue = paramString2;
    }
  }
  
  public static abstract interface ValueSanitizer
  {
    public abstract String sanitize(String paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/UrlQuerySanitizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */