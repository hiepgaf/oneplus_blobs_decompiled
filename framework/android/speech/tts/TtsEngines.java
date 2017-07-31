package android.speech.tts;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import org.xmlpull.v1.XmlPullParserException;

public class TtsEngines
{
  private static final boolean DBG = false;
  private static final String LOCALE_DELIMITER_NEW = "_";
  private static final String LOCALE_DELIMITER_OLD = "-";
  private static final String TAG = "TtsEngines";
  private static final String XML_TAG_NAME = "tts-engine";
  private static final Map<String, String> sNormalizeCountry;
  private static final Map<String, String> sNormalizeLanguage;
  private final Context mContext;
  
  static
  {
    int j = 0;
    HashMap localHashMap = new HashMap();
    String[] arrayOfString = Locale.getISOLanguages();
    int k = arrayOfString.length;
    int i = 0;
    for (;;)
    {
      String str;
      if (i < k) {
        str = arrayOfString[i];
      }
      try
      {
        localHashMap.put(new Locale(str).getISO3Language(), str);
        i += 1;
        continue;
        sNormalizeLanguage = Collections.unmodifiableMap(localHashMap);
        localHashMap = new HashMap();
        arrayOfString = Locale.getISOCountries();
        k = arrayOfString.length;
        i = j;
        for (;;)
        {
          if (i < k) {
            str = arrayOfString[i];
          }
          try
          {
            localHashMap.put(new Locale("", str).getISO3Country(), str);
            i += 1;
            continue;
            sNormalizeCountry = Collections.unmodifiableMap(localHashMap);
            return;
          }
          catch (MissingResourceException localMissingResourceException1)
          {
            for (;;) {}
          }
        }
      }
      catch (MissingResourceException localMissingResourceException2)
      {
        for (;;) {}
      }
    }
  }
  
  public TtsEngines(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private TextToSpeech.EngineInfo getEngineInfo(ResolveInfo paramResolveInfo, PackageManager paramPackageManager)
  {
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    if (localServiceInfo != null)
    {
      TextToSpeech.EngineInfo localEngineInfo = new TextToSpeech.EngineInfo();
      localEngineInfo.name = localServiceInfo.packageName;
      paramPackageManager = localServiceInfo.loadLabel(paramPackageManager);
      if (TextUtils.isEmpty(paramPackageManager)) {}
      for (paramPackageManager = localEngineInfo.name;; paramPackageManager = paramPackageManager.toString())
      {
        localEngineInfo.label = paramPackageManager;
        localEngineInfo.icon = localServiceInfo.getIconResource();
        localEngineInfo.priority = paramResolveInfo.priority;
        localEngineInfo.system = isSystemEngine(localServiceInfo);
        return localEngineInfo;
      }
    }
    return null;
  }
  
  private boolean isSystemEngine(ServiceInfo paramServiceInfo)
  {
    boolean bool2 = false;
    paramServiceInfo = paramServiceInfo.applicationInfo;
    boolean bool1 = bool2;
    if (paramServiceInfo != null)
    {
      bool1 = bool2;
      if ((paramServiceInfo.flags & 0x1) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static Locale normalizeTTSLocale(Locale paramLocale)
  {
    Object localObject2 = paramLocale.getLanguage();
    Object localObject1 = localObject2;
    if (!TextUtils.isEmpty((CharSequence)localObject2))
    {
      str1 = (String)sNormalizeLanguage.get(localObject2);
      localObject1 = localObject2;
      if (str1 != null) {
        localObject1 = str1;
      }
    }
    String str1 = paramLocale.getCountry();
    localObject2 = str1;
    if (!TextUtils.isEmpty(str1))
    {
      String str2 = (String)sNormalizeCountry.get(str1);
      localObject2 = str1;
      if (str2 != null) {
        localObject2 = str2;
      }
    }
    return new Locale((String)localObject1, (String)localObject2, paramLocale.getVariant());
  }
  
  private static String parseEnginePrefFromList(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString1)) {
      return null;
    }
    paramString1 = paramString1.split(",");
    int j = paramString1.length;
    int i = 0;
    while (i < j)
    {
      Object localObject = paramString1[i];
      int k = ((String)localObject).indexOf(':');
      if ((k > 0) && (paramString2.equals(((String)localObject).substring(0, k)))) {
        return ((String)localObject).substring(k + 1);
      }
      i += 1;
    }
    return null;
  }
  
  private String settingsActivityFromServiceInfo(ServiceInfo paramServiceInfo, PackageManager paramPackageManager)
  {
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    try
    {
      XmlResourceParser localXmlResourceParser = paramServiceInfo.loadXmlMetaData(paramPackageManager, "android.speech.tts");
      if (localXmlResourceParser == null)
      {
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        Log.w("TtsEngines", "No meta-data found for :" + paramServiceInfo);
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.getResourcesForApplication(paramServiceInfo.applicationInfo);
      int i;
      do
      {
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        i = localXmlResourceParser.next();
        if (i == 1) {
          break;
        }
      } while (i != 2);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      if (!"tts-engine".equals(localXmlResourceParser.getName()))
      {
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        Log.w("TtsEngines", "Package " + paramServiceInfo + " uses unknown tag :" + localXmlResourceParser.getName());
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.obtainAttributes(Xml.asAttributeSet(localXmlResourceParser), R.styleable.TextToSpeechEngine);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      String str = paramPackageManager.getString(0);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramPackageManager.recycle();
      if (localXmlResourceParser != null) {
        localXmlResourceParser.close();
      }
      return str;
      if (localXmlResourceParser != null) {
        localXmlResourceParser.close();
      }
      return null;
    }
    catch (IOException paramPackageManager)
    {
      localObject1 = localObject2;
      Log.w("TtsEngines", "Error parsing metadata for " + paramServiceInfo + ":" + paramPackageManager);
      return null;
    }
    catch (XmlPullParserException paramPackageManager)
    {
      localObject1 = localObject3;
      Log.w("TtsEngines", "Error parsing metadata for " + paramServiceInfo + ":" + paramPackageManager);
      return null;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      localObject1 = localObject4;
      Log.w("TtsEngines", "Could not load resources for : " + paramServiceInfo);
      return null;
    }
    finally
    {
      if (localObject1 != null) {
        ((XmlResourceParser)localObject1).close();
      }
    }
  }
  
  public static String[] toOldLocaleStringFormat(Locale paramLocale)
  {
    String[] arrayOfString = new String[3];
    arrayOfString[0] = "";
    arrayOfString[1] = "";
    arrayOfString[2] = "";
    try
    {
      arrayOfString[0] = paramLocale.getISO3Language();
      arrayOfString[1] = paramLocale.getISO3Country();
      arrayOfString[2] = paramLocale.getVariant();
      return arrayOfString;
    }
    catch (MissingResourceException paramLocale) {}
    return tmp60_54;
  }
  
  private String updateValueInCommaSeparatedList(String paramString1, String paramString2, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (TextUtils.isEmpty(paramString1)) {
      localStringBuilder.append(paramString2).append(':').append(paramString3);
    }
    for (;;)
    {
      return localStringBuilder.toString();
      paramString1 = paramString1.split(",");
      int i = 1;
      int m = 0;
      int i1 = paramString1.length;
      int k = 0;
      if (k < i1)
      {
        String str = paramString1[k];
        int i2 = str.indexOf(':');
        int j = i;
        int n = m;
        if (i2 > 0)
        {
          if (!paramString2.equals(str.substring(0, i2))) {
            break label167;
          }
          if (i == 0) {
            break label156;
          }
          i = 0;
        }
        for (;;)
        {
          n = 1;
          localStringBuilder.append(paramString2).append(':').append(paramString3);
          j = i;
          k += 1;
          i = j;
          m = n;
          break;
          label156:
          localStringBuilder.append(',');
        }
        label167:
        if (i != 0) {
          i = 0;
        }
        for (;;)
        {
          localStringBuilder.append(str);
          j = i;
          n = m;
          break;
          localStringBuilder.append(',');
        }
      }
      if (m == 0)
      {
        localStringBuilder.append(',');
        localStringBuilder.append(paramString2).append(':').append(paramString3);
      }
    }
  }
  
  public String getDefaultEngine()
  {
    String str = Settings.Secure.getString(this.mContext.getContentResolver(), "tts_default_synth");
    if (isEngineInstalled(str)) {
      return str;
    }
    return getHighestRankedEngineName();
  }
  
  public TextToSpeech.EngineInfo getEngineInfo(String paramString)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Intent localIntent = new Intent("android.intent.action.TTS_SERVICE");
    localIntent.setPackage(paramString);
    paramString = localPackageManager.queryIntentServices(localIntent, 65536);
    if ((paramString != null) && (paramString.size() == 1)) {
      return getEngineInfo((ResolveInfo)paramString.get(0), localPackageManager);
    }
    return null;
  }
  
  public List<TextToSpeech.EngineInfo> getEngines()
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Object localObject = localPackageManager.queryIntentServices(new Intent("android.intent.action.TTS_SERVICE"), 65536);
    if (localObject == null) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList(((List)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      TextToSpeech.EngineInfo localEngineInfo = getEngineInfo((ResolveInfo)((Iterator)localObject).next(), localPackageManager);
      if (localEngineInfo != null) {
        localArrayList.add(localEngineInfo);
      }
    }
    Collections.sort(localArrayList, EngineInfoComparator.INSTANCE);
    return localArrayList;
  }
  
  public String getHighestRankedEngineName()
  {
    List localList = getEngines();
    if ((localList.size() > 0) && (((TextToSpeech.EngineInfo)localList.get(0)).system)) {
      return ((TextToSpeech.EngineInfo)localList.get(0)).name;
    }
    return null;
  }
  
  public Locale getLocalePrefForEngine(String paramString)
  {
    return getLocalePrefForEngine(paramString, Settings.Secure.getString(this.mContext.getContentResolver(), "tts_default_locale"));
  }
  
  public Locale getLocalePrefForEngine(String paramString1, String paramString2)
  {
    String str = parseEnginePrefFromList(paramString2, paramString1);
    if (TextUtils.isEmpty(str)) {
      return Locale.getDefault();
    }
    paramString2 = parseLocaleString(str);
    paramString1 = paramString2;
    if (paramString2 == null)
    {
      Log.w("TtsEngines", "Failed to parse locale " + str + ", returning en_US instead");
      paramString1 = Locale.US;
    }
    return paramString1;
  }
  
  public Intent getSettingsIntent(String paramString)
  {
    Object localObject1 = this.mContext.getPackageManager();
    Object localObject2 = new Intent("android.intent.action.TTS_SERVICE");
    ((Intent)localObject2).setPackage(paramString);
    localObject2 = ((PackageManager)localObject1).queryIntentServices((Intent)localObject2, 65664);
    if ((localObject2 != null) && (((List)localObject2).size() == 1))
    {
      localObject2 = ((ResolveInfo)((List)localObject2).get(0)).serviceInfo;
      if (localObject2 != null)
      {
        localObject1 = settingsActivityFromServiceInfo((ServiceInfo)localObject2, (PackageManager)localObject1);
        if (localObject1 != null)
        {
          localObject2 = new Intent();
          ((Intent)localObject2).setClassName(paramString, (String)localObject1);
          return (Intent)localObject2;
        }
      }
    }
    return null;
  }
  
  public boolean isEngineInstalled(String paramString)
  {
    boolean bool = false;
    if (paramString == null) {
      return false;
    }
    if (getEngineInfo(paramString) != null) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isLocaleSetToDefaultForEngine(String paramString)
  {
    return TextUtils.isEmpty(parseEnginePrefFromList(Settings.Secure.getString(this.mContext.getContentResolver(), "tts_default_locale"), paramString));
  }
  
  public Locale parseLocaleString(String paramString)
  {
    Object localObject2 = "";
    String str1 = "";
    String str3 = "";
    Object localObject1 = str1;
    String str2 = str3;
    if (!TextUtils.isEmpty(paramString))
    {
      String[] arrayOfString = paramString.split("[-_]");
      String str4 = arrayOfString[0].toLowerCase();
      if (arrayOfString.length == 0)
      {
        Log.w("TtsEngines", "Failed to convert " + paramString + " to a valid Locale object. Only" + " separators");
        return null;
      }
      if (arrayOfString.length > 3)
      {
        Log.w("TtsEngines", "Failed to convert " + paramString + " to a valid Locale object. Too" + " many separators");
        return null;
      }
      if (arrayOfString.length >= 2) {
        str1 = arrayOfString[1].toUpperCase();
      }
      localObject1 = str1;
      localObject2 = str4;
      str2 = str3;
      if (arrayOfString.length >= 3)
      {
        str2 = arrayOfString[2];
        localObject2 = str4;
        localObject1 = str1;
      }
    }
    str1 = (String)sNormalizeLanguage.get(localObject2);
    if (str1 != null) {
      localObject2 = str1;
    }
    str1 = (String)sNormalizeCountry.get(localObject1);
    if (str1 != null) {
      localObject1 = str1;
    }
    localObject1 = new Locale((String)localObject2, (String)localObject1, str2);
    try
    {
      ((Locale)localObject1).getISO3Language();
      ((Locale)localObject1).getISO3Country();
      return (Locale)localObject1;
    }
    catch (MissingResourceException localMissingResourceException)
    {
      Log.w("TtsEngines", "Failed to convert " + paramString + " to a valid Locale object.");
    }
    return null;
  }
  
  /* Error */
  public void updateLocalePrefForEngine(String paramString, Locale paramLocale)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 81	android/speech/tts/TtsEngines:mContext	Landroid/content/Context;
    //   6: invokevirtual 295	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   9: ldc_w 382
    //   12: invokestatic 302	android/provider/Settings$Secure:getString	(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;
    //   15: astore_3
    //   16: aload_2
    //   17: ifnull +37 -> 54
    //   20: aload_2
    //   21: invokevirtual 437	java/util/Locale:toString	()Ljava/lang/String;
    //   24: astore_2
    //   25: aload_0
    //   26: aload_3
    //   27: aload_1
    //   28: aload_2
    //   29: invokespecial 439	android/speech/tts/TtsEngines:updateValueInCommaSeparatedList	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   32: astore_1
    //   33: aload_0
    //   34: getfield 81	android/speech/tts/TtsEngines:mContext	Landroid/content/Context;
    //   37: invokevirtual 295	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   40: ldc_w 382
    //   43: aload_1
    //   44: invokevirtual 440	java/lang/String:toString	()Ljava/lang/String;
    //   47: invokestatic 444	android/provider/Settings$Secure:putString	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;)Z
    //   50: pop
    //   51: aload_0
    //   52: monitorexit
    //   53: return
    //   54: ldc 68
    //   56: astore_2
    //   57: goto -32 -> 25
    //   60: astore_1
    //   61: aload_0
    //   62: monitorexit
    //   63: aload_1
    //   64: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	this	TtsEngines
    //   0	65	1	paramString	String
    //   0	65	2	paramLocale	Locale
    //   15	12	3	str	String
    // Exception table:
    //   from	to	target	type
    //   2	16	60	finally
    //   20	25	60	finally
    //   25	51	60	finally
  }
  
  private static class EngineInfoComparator
    implements Comparator<TextToSpeech.EngineInfo>
  {
    static EngineInfoComparator INSTANCE = new EngineInfoComparator();
    
    public int compare(TextToSpeech.EngineInfo paramEngineInfo1, TextToSpeech.EngineInfo paramEngineInfo2)
    {
      if ((!paramEngineInfo1.system) || (paramEngineInfo2.system))
      {
        if ((!paramEngineInfo2.system) || (paramEngineInfo1.system)) {
          return paramEngineInfo2.priority - paramEngineInfo1.priority;
        }
      }
      else {
        return -1;
      }
      return 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/TtsEngines.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */