package android.os;

import android.icu.util.ULocale;
import com.android.internal.annotations.GuardedBy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

public final class LocaleList
  implements Parcelable
{
  public static final Parcelable.Creator<LocaleList> CREATOR;
  private static final Locale EN_LATN;
  private static final Locale LOCALE_AR_XB;
  private static final Locale LOCALE_EN_XA;
  private static final int NUM_PSEUDO_LOCALES = 2;
  private static final String STRING_AR_XB = "ar-XB";
  private static final String STRING_EN_XA = "en-XA";
  @GuardedBy("sLock")
  private static LocaleList sDefaultAdjustedLocaleList = null;
  @GuardedBy("sLock")
  private static LocaleList sDefaultLocaleList;
  private static final Locale[] sEmptyList = new Locale[0];
  private static final LocaleList sEmptyLocaleList = new LocaleList(new Locale[0]);
  @GuardedBy("sLock")
  private static Locale sLastDefaultLocale = null;
  @GuardedBy("sLock")
  private static LocaleList sLastExplicitlySetLocaleList;
  private static final Object sLock;
  private final Locale[] mList;
  private final String mStringRepresentation;
  
  static
  {
    CREATOR = new Parcelable.Creator()
    {
      public LocaleList createFromParcel(Parcel paramAnonymousParcel)
      {
        return LocaleList.forLanguageTags(paramAnonymousParcel.readString());
      }
      
      public LocaleList[] newArray(int paramAnonymousInt)
      {
        return new LocaleList[paramAnonymousInt];
      }
    };
    LOCALE_EN_XA = new Locale("en", "XA");
    LOCALE_AR_XB = new Locale("ar", "XB");
    EN_LATN = Locale.forLanguageTag("en-Latn");
    sLock = new Object();
    sLastExplicitlySetLocaleList = null;
    sDefaultLocaleList = null;
  }
  
  public LocaleList(Locale paramLocale, LocaleList paramLocaleList)
  {
    if (paramLocale == null) {
      throw new NullPointerException("topLocale is null");
    }
    int j;
    int m;
    label30:
    int k;
    if (paramLocaleList == null)
    {
      j = 0;
      m = -1;
      i = 0;
      k = m;
      if (i < j)
      {
        if (!paramLocale.equals(paramLocaleList.mList[i])) {
          break label137;
        }
        k = i;
      }
      if (k != -1) {
        break label144;
      }
    }
    Locale[] arrayOfLocale;
    label137:
    label144:
    for (int i = 1;; i = 0)
    {
      m = j + i;
      arrayOfLocale = new Locale[m];
      arrayOfLocale[0] = ((Locale)paramLocale.clone());
      if (k != -1) {
        break label149;
      }
      i = 0;
      while (i < j)
      {
        arrayOfLocale[(i + 1)] = ((Locale)paramLocaleList.mList[i].clone());
        i += 1;
      }
      j = paramLocaleList.mList.length;
      break;
      i += 1;
      break label30;
    }
    label149:
    i = 0;
    while (i < k)
    {
      arrayOfLocale[(i + 1)] = ((Locale)paramLocaleList.mList[i].clone());
      i += 1;
    }
    i = k + 1;
    while (i < j)
    {
      arrayOfLocale[i] = ((Locale)paramLocaleList.mList[i].clone());
      i += 1;
    }
    paramLocale = new StringBuilder();
    i = 0;
    while (i < m)
    {
      paramLocale.append(arrayOfLocale[i].toLanguageTag());
      if (i < m - 1) {
        paramLocale.append(',');
      }
      i += 1;
    }
    this.mList = arrayOfLocale;
    this.mStringRepresentation = paramLocale.toString();
  }
  
  public LocaleList(Locale... paramVarArgs)
  {
    if (paramVarArgs.length == 0)
    {
      this.mList = sEmptyList;
      this.mStringRepresentation = "";
      return;
    }
    Locale[] arrayOfLocale = new Locale[paramVarArgs.length];
    HashSet localHashSet = new HashSet();
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramVarArgs.length)
    {
      Locale localLocale = paramVarArgs[i];
      if (localLocale == null) {
        throw new NullPointerException("list[" + i + "] is null");
      }
      if (localHashSet.contains(localLocale)) {
        throw new IllegalArgumentException("list[" + i + "] is a repetition");
      }
      localLocale = (Locale)localLocale.clone();
      arrayOfLocale[i] = localLocale;
      localStringBuilder.append(localLocale.toLanguageTag());
      if (i < paramVarArgs.length - 1) {
        localStringBuilder.append(',');
      }
      localHashSet.add(localLocale);
      i += 1;
    }
    this.mList = arrayOfLocale;
    this.mStringRepresentation = localStringBuilder.toString();
  }
  
  private Locale computeFirstMatch(Collection<String> paramCollection, boolean paramBoolean)
  {
    int i = computeFirstMatchIndex(paramCollection, paramBoolean);
    if (i == -1) {
      return null;
    }
    return this.mList[i];
  }
  
  private int computeFirstMatchIndex(Collection<String> paramCollection, boolean paramBoolean)
  {
    if (this.mList.length == 1) {
      return 0;
    }
    if (this.mList.length == 0) {
      return -1;
    }
    int j = Integer.MAX_VALUE;
    int i = j;
    if (paramBoolean)
    {
      int k = findFirstMatchIndex(EN_LATN);
      if (k == 0) {
        return 0;
      }
      i = j;
      if (k < Integer.MAX_VALUE) {
        i = k;
      }
    }
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      j = findFirstMatchIndex(Locale.forLanguageTag((String)paramCollection.next()));
      if (j == 0) {
        return 0;
      }
      if (j < i) {
        i = j;
      }
    }
    if (i == Integer.MAX_VALUE) {
      return 0;
    }
    return i;
  }
  
  private int findFirstMatchIndex(Locale paramLocale)
  {
    int i = 0;
    while (i < this.mList.length)
    {
      if (matchScore(paramLocale, this.mList[i]) > 0) {
        return i;
      }
      i += 1;
    }
    return Integer.MAX_VALUE;
  }
  
  public static LocaleList forLanguageTags(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return getEmptyLocaleList();
    }
    paramString = paramString.split(",");
    Locale[] arrayOfLocale = new Locale[paramString.length];
    int i = 0;
    while (i < arrayOfLocale.length)
    {
      arrayOfLocale[i] = Locale.forLanguageTag(paramString[i]);
      i += 1;
    }
    return new LocaleList(arrayOfLocale);
  }
  
  public static LocaleList getAdjustedDefault()
  {
    getDefault();
    synchronized (sLock)
    {
      LocaleList localLocaleList = sDefaultAdjustedLocaleList;
      return localLocaleList;
    }
  }
  
  public static LocaleList getDefault()
  {
    Object localObject2 = Locale.getDefault();
    synchronized (sLock)
    {
      if (!((Locale)localObject2).equals(sLastDefaultLocale))
      {
        sLastDefaultLocale = (Locale)localObject2;
        if ((sDefaultLocaleList != null) && (((Locale)localObject2).equals(sDefaultLocaleList.get(0))))
        {
          localObject2 = sDefaultLocaleList;
          return (LocaleList)localObject2;
        }
        sDefaultLocaleList = new LocaleList((Locale)localObject2, sLastExplicitlySetLocaleList);
        sDefaultAdjustedLocaleList = sDefaultLocaleList;
      }
      localObject2 = sDefaultLocaleList;
      return (LocaleList)localObject2;
    }
  }
  
  public static LocaleList getEmptyLocaleList()
  {
    return sEmptyLocaleList;
  }
  
  private static String getLikelyScript(Locale paramLocale)
  {
    String str = paramLocale.getScript();
    if (!str.isEmpty()) {
      return str;
    }
    return ULocale.addLikelySubtags(ULocale.forLocale(paramLocale)).getScript();
  }
  
  private static boolean isPseudoLocale(String paramString)
  {
    if (!"en-XA".equals(paramString)) {
      return "ar-XB".equals(paramString);
    }
    return true;
  }
  
  private static boolean isPseudoLocale(Locale paramLocale)
  {
    if (!LOCALE_EN_XA.equals(paramLocale)) {
      return LOCALE_AR_XB.equals(paramLocale);
    }
    return true;
  }
  
  public static boolean isPseudoLocalesOnly(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      return true;
    }
    if (paramArrayOfString.length > 3) {
      return false;
    }
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      if ((str.isEmpty()) || (isPseudoLocale(str))) {
        i += 1;
      } else {
        return false;
      }
    }
    return true;
  }
  
  private static int matchScore(Locale paramLocale1, Locale paramLocale2)
  {
    int i = 0;
    if (paramLocale1.equals(paramLocale2)) {
      return 1;
    }
    if (!paramLocale1.getLanguage().equals(paramLocale2.getLanguage())) {
      return 0;
    }
    if ((isPseudoLocale(paramLocale1)) || (isPseudoLocale(paramLocale2))) {
      return 0;
    }
    String str = getLikelyScript(paramLocale1);
    if (str.isEmpty())
    {
      paramLocale1 = paramLocale1.getCountry();
      if ((paramLocale1.isEmpty()) || (paramLocale1.equals(paramLocale2.getCountry()))) {
        i = 1;
      }
      return i;
    }
    if (str.equals(getLikelyScript(paramLocale2))) {
      return 1;
    }
    return 0;
  }
  
  public static void setDefault(LocaleList paramLocaleList)
  {
    setDefault(paramLocaleList, 0);
  }
  
  public static void setDefault(LocaleList paramLocaleList, int paramInt)
  {
    if (paramLocaleList == null) {
      throw new NullPointerException("locales is null");
    }
    if (paramLocaleList.isEmpty()) {
      throw new IllegalArgumentException("locales is empty");
    }
    synchronized (sLock)
    {
      sLastDefaultLocale = paramLocaleList.get(paramInt);
      Locale.setDefault(sLastDefaultLocale);
      sLastExplicitlySetLocaleList = paramLocaleList;
      sDefaultLocaleList = paramLocaleList;
      if (paramInt == 0)
      {
        sDefaultAdjustedLocaleList = sDefaultLocaleList;
        return;
      }
      sDefaultAdjustedLocaleList = new LocaleList(sLastDefaultLocale, sDefaultLocaleList);
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof LocaleList)) {
      return false;
    }
    paramObject = ((LocaleList)paramObject).mList;
    if (this.mList.length != paramObject.length) {
      return false;
    }
    int i = 0;
    while (i < this.mList.length)
    {
      if (!this.mList[i].equals(paramObject[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public Locale get(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.mList.length)) {
      return this.mList[paramInt];
    }
    return null;
  }
  
  public Locale getFirstMatch(String[] paramArrayOfString)
  {
    return computeFirstMatch(Arrays.asList(paramArrayOfString), false);
  }
  
  public int getFirstMatchIndex(String[] paramArrayOfString)
  {
    return computeFirstMatchIndex(Arrays.asList(paramArrayOfString), false);
  }
  
  public int getFirstMatchIndexWithEnglishSupported(Collection<String> paramCollection)
  {
    return computeFirstMatchIndex(paramCollection, true);
  }
  
  public int getFirstMatchIndexWithEnglishSupported(String[] paramArrayOfString)
  {
    return getFirstMatchIndexWithEnglishSupported(Arrays.asList(paramArrayOfString));
  }
  
  public Locale getFirstMatchWithEnglishSupported(String[] paramArrayOfString)
  {
    return computeFirstMatch(Arrays.asList(paramArrayOfString), true);
  }
  
  public int hashCode()
  {
    int j = 1;
    int i = 0;
    while (i < this.mList.length)
    {
      j = j * 31 + this.mList[i].hashCode();
      i += 1;
    }
    return j;
  }
  
  public int indexOf(Locale paramLocale)
  {
    int i = 0;
    while (i < this.mList.length)
    {
      if (this.mList[i].equals(paramLocale)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  public boolean isEmpty()
  {
    boolean bool = false;
    if (this.mList.length == 0) {
      bool = true;
    }
    return bool;
  }
  
  public int size()
  {
    return this.mList.length;
  }
  
  public String toLanguageTags()
  {
    return this.mStringRepresentation;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    int i = 0;
    while (i < this.mList.length)
    {
      localStringBuilder.append(this.mList[i]);
      if (i < this.mList.length - 1) {
        localStringBuilder.append(',');
      }
      i += 1;
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mStringRepresentation);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/LocaleList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */