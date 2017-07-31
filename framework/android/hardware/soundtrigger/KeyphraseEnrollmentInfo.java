package android.hardware.soundtrigger;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

public class KeyphraseEnrollmentInfo
{
  public static final String ACTION_MANAGE_VOICE_KEYPHRASES = "com.android.intent.action.MANAGE_VOICE_KEYPHRASES";
  public static final String EXTRA_VOICE_KEYPHRASE_ACTION = "com.android.intent.extra.VOICE_KEYPHRASE_ACTION";
  public static final String EXTRA_VOICE_KEYPHRASE_HINT_TEXT = "com.android.intent.extra.VOICE_KEYPHRASE_HINT_TEXT";
  public static final String EXTRA_VOICE_KEYPHRASE_LOCALE = "com.android.intent.extra.VOICE_KEYPHRASE_LOCALE";
  private static final String TAG = "KeyphraseEnrollmentInfo";
  private static final String VOICE_KEYPHRASE_META_DATA = "android.voice_enrollment";
  private final Map<KeyphraseMetadata, String> mKeyphrasePackageMap;
  private final KeyphraseMetadata[] mKeyphrases;
  private String mParseError;
  
  public KeyphraseEnrollmentInfo(PackageManager paramPackageManager)
  {
    Object localObject1 = paramPackageManager.queryIntentActivities(new Intent("com.android.intent.action.MANAGE_VOICE_KEYPHRASES"), 65536);
    if ((localObject1 == null) || (((List)localObject1).isEmpty()))
    {
      this.mParseError = "No enrollment applications found";
      this.mKeyphrasePackageMap = Collections.emptyMap();
      this.mKeyphrases = null;
      return;
    }
    LinkedList localLinkedList = new LinkedList();
    this.mKeyphrasePackageMap = new HashMap();
    localObject1 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (ResolveInfo)((Iterator)localObject1).next();
      try
      {
        ApplicationInfo localApplicationInfo = paramPackageManager.getApplicationInfo(((ResolveInfo)localObject2).activityInfo.packageName, 128);
        if ((localApplicationInfo.privateFlags & 0x8) != 0) {
          break label233;
        }
        Slog.w("KeyphraseEnrollmentInfo", localApplicationInfo.packageName + "is not a privileged system app");
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        localObject2 = "error parsing voice enrollment meta-data for " + ((ResolveInfo)localObject2).activityInfo.packageName;
        localLinkedList.add((String)localObject2 + ": " + localNameNotFoundException);
        Slog.w("KeyphraseEnrollmentInfo", (String)localObject2, localNameNotFoundException);
      }
      continue;
      label233:
      if (!"android.permission.MANAGE_VOICE_KEYPHRASES".equals(localNameNotFoundException.permission)) {
        Slog.w("KeyphraseEnrollmentInfo", localNameNotFoundException.packageName + " does not require MANAGE_VOICE_KEYPHRASES");
      } else {
        this.mKeyphrasePackageMap.put(getKeyphraseMetadataFromApplicationInfo(paramPackageManager, localNameNotFoundException, localLinkedList), localNameNotFoundException.packageName);
      }
    }
    if (this.mKeyphrasePackageMap.isEmpty())
    {
      localLinkedList.add("No suitable enrollment application found");
      Slog.w("KeyphraseEnrollmentInfo", "No suitable enrollment application found");
    }
    for (this.mKeyphrases = null;; this.mKeyphrases = ((KeyphraseMetadata[])this.mKeyphrasePackageMap.keySet().toArray(new KeyphraseMetadata[this.mKeyphrasePackageMap.size()])))
    {
      if (!localLinkedList.isEmpty()) {
        this.mParseError = TextUtils.join("\n", localLinkedList);
      }
      return;
    }
  }
  
  private KeyphraseMetadata getKeyphraseFromTypedArray(TypedArray paramTypedArray, String paramString, List<String> paramList)
  {
    int j = paramTypedArray.getInt(0, -1);
    if (j <= 0)
    {
      paramTypedArray = "No valid searchKeyphraseId specified in meta-data for " + paramString;
      paramList.add(paramTypedArray);
      Slog.w("KeyphraseEnrollmentInfo", paramTypedArray);
      return null;
    }
    String str = paramTypedArray.getString(1);
    if (str == null)
    {
      paramTypedArray = "No valid searchKeyphrase specified in meta-data for " + paramString;
      paramList.add(paramTypedArray);
      Slog.w("KeyphraseEnrollmentInfo", paramTypedArray);
      return null;
    }
    Object localObject = paramTypedArray.getString(2);
    if (localObject == null)
    {
      paramTypedArray = "No valid searchKeyphraseSupportedLocales specified in meta-data for " + paramString;
      paramList.add(paramTypedArray);
      Slog.w("KeyphraseEnrollmentInfo", paramTypedArray);
      return null;
    }
    ArraySet localArraySet = new ArraySet();
    int i;
    if (!TextUtils.isEmpty((CharSequence)localObject)) {
      try
      {
        localObject = ((String)localObject).split(",");
        i = 0;
        while (i < localObject.length)
        {
          localArraySet.add(Locale.forLanguageTag(localObject[i]));
          i += 1;
        }
        i = paramTypedArray.getInt(3, -1);
      }
      catch (Exception paramTypedArray)
      {
        paramTypedArray = "Error reading searchKeyphraseSupportedLocales from meta-data for " + paramString;
        paramList.add(paramTypedArray);
        Slog.w("KeyphraseEnrollmentInfo", paramTypedArray);
        return null;
      }
    }
    if (i < 0)
    {
      paramTypedArray = "No valid searchKeyphraseRecognitionFlags specified in meta-data for " + paramString;
      paramList.add(paramTypedArray);
      Slog.w("KeyphraseEnrollmentInfo", paramTypedArray);
      return null;
    }
    return new KeyphraseMetadata(j, str, localArraySet, i);
  }
  
  private KeyphraseMetadata getKeyphraseMetadataFromApplicationInfo(PackageManager paramPackageManager, ApplicationInfo paramApplicationInfo, List<String> paramList)
  {
    localObject5 = null;
    localObject7 = null;
    localObject1 = null;
    localObject3 = null;
    str = paramApplicationInfo.packageName;
    Object localObject9 = null;
    Object localObject10 = null;
    Object localObject8 = null;
    localObject2 = localObject8;
    localObject4 = localObject9;
    localObject6 = localObject10;
    try
    {
      XmlResourceParser localXmlResourceParser = paramApplicationInfo.loadXmlMetaData(paramPackageManager, "android.voice_enrollment");
      if (localXmlResourceParser == null)
      {
        localObject2 = localObject8;
        localObject3 = localXmlResourceParser;
        localObject4 = localObject9;
        localObject5 = localXmlResourceParser;
        localObject6 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        paramPackageManager = "No android.voice_enrollment meta-data for " + str;
        localObject2 = localObject8;
        localObject3 = localXmlResourceParser;
        localObject4 = localObject9;
        localObject5 = localXmlResourceParser;
        localObject6 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        paramList.add(paramPackageManager);
        localObject2 = localObject8;
        localObject3 = localXmlResourceParser;
        localObject4 = localObject9;
        localObject5 = localXmlResourceParser;
        localObject6 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        Slog.w("KeyphraseEnrollmentInfo", paramPackageManager);
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      localObject2 = localObject8;
      localObject3 = localXmlResourceParser;
      localObject4 = localObject9;
      localObject5 = localXmlResourceParser;
      localObject6 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.getResourcesForApplication(paramApplicationInfo);
      localObject2 = localObject8;
      localObject3 = localXmlResourceParser;
      localObject4 = localObject9;
      localObject5 = localXmlResourceParser;
      localObject6 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramApplicationInfo = Xml.asAttributeSet(localXmlResourceParser);
      int i;
      do
      {
        localObject2 = localObject8;
        localObject3 = localXmlResourceParser;
        localObject4 = localObject9;
        localObject5 = localXmlResourceParser;
        localObject6 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        i = localXmlResourceParser.next();
      } while ((i != 1) && (i != 2));
      localObject2 = localObject8;
      localObject3 = localXmlResourceParser;
      localObject4 = localObject9;
      localObject5 = localXmlResourceParser;
      localObject6 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      if (!"voice-enrollment-application".equals(localXmlResourceParser.getName()))
      {
        localObject2 = localObject8;
        localObject3 = localXmlResourceParser;
        localObject4 = localObject9;
        localObject5 = localXmlResourceParser;
        localObject6 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        paramPackageManager = "Meta-data does not start with voice-enrollment-application tag for " + str;
        localObject2 = localObject8;
        localObject3 = localXmlResourceParser;
        localObject4 = localObject9;
        localObject5 = localXmlResourceParser;
        localObject6 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        paramList.add(paramPackageManager);
        localObject2 = localObject8;
        localObject3 = localXmlResourceParser;
        localObject4 = localObject9;
        localObject5 = localXmlResourceParser;
        localObject6 = localObject10;
        localObject7 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        Slog.w("KeyphraseEnrollmentInfo", paramPackageManager);
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      localObject2 = localObject8;
      localObject3 = localXmlResourceParser;
      localObject4 = localObject9;
      localObject5 = localXmlResourceParser;
      localObject6 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramApplicationInfo = paramPackageManager.obtainAttributes(paramApplicationInfo, R.styleable.VoiceEnrollmentApplication);
      localObject2 = localObject8;
      localObject3 = localXmlResourceParser;
      localObject4 = localObject9;
      localObject5 = localXmlResourceParser;
      localObject6 = localObject10;
      localObject7 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramPackageManager = getKeyphraseFromTypedArray(paramApplicationInfo, str, paramList);
      localObject2 = paramPackageManager;
      localObject3 = localXmlResourceParser;
      localObject4 = paramPackageManager;
      localObject5 = localXmlResourceParser;
      localObject6 = paramPackageManager;
      localObject7 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramApplicationInfo.recycle();
      paramApplicationInfo = paramPackageManager;
      if (localXmlResourceParser != null)
      {
        localXmlResourceParser.close();
        paramApplicationInfo = paramPackageManager;
      }
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      localObject1 = localObject3;
      paramApplicationInfo = "Error parsing keyphrase enrollment meta-data for " + str;
      localObject1 = localObject3;
      paramList.add(paramApplicationInfo + ": " + paramPackageManager);
      localObject1 = localObject3;
      Slog.w("KeyphraseEnrollmentInfo", paramApplicationInfo, paramPackageManager);
      paramApplicationInfo = (ApplicationInfo)localObject2;
      return (KeyphraseMetadata)localObject2;
    }
    catch (IOException paramPackageManager)
    {
      localObject1 = localObject5;
      paramApplicationInfo = "Error parsing keyphrase enrollment meta-data for " + str;
      localObject1 = localObject5;
      paramList.add(paramApplicationInfo + ": " + paramPackageManager);
      localObject1 = localObject5;
      Slog.w("KeyphraseEnrollmentInfo", paramApplicationInfo, paramPackageManager);
      paramApplicationInfo = (ApplicationInfo)localObject4;
      return (KeyphraseMetadata)localObject4;
    }
    catch (XmlPullParserException paramPackageManager)
    {
      localObject1 = localObject7;
      paramApplicationInfo = "Error parsing keyphrase enrollment meta-data for " + str;
      localObject1 = localObject7;
      paramList.add(paramApplicationInfo + ": " + paramPackageManager);
      localObject1 = localObject7;
      Slog.w("KeyphraseEnrollmentInfo", paramApplicationInfo, paramPackageManager);
      paramApplicationInfo = (ApplicationInfo)localObject6;
      return (KeyphraseMetadata)localObject6;
    }
    finally
    {
      if (localObject1 == null) {
        break label890;
      }
      ((XmlResourceParser)localObject1).close();
    }
    return paramApplicationInfo;
  }
  
  public KeyphraseMetadata getKeyphraseMetadata(String paramString, Locale paramLocale)
  {
    int i = 0;
    if ((this.mKeyphrases != null) && (this.mKeyphrases.length > 0))
    {
      KeyphraseMetadata[] arrayOfKeyphraseMetadata = this.mKeyphrases;
      int j = arrayOfKeyphraseMetadata.length;
      while (i < j)
      {
        KeyphraseMetadata localKeyphraseMetadata = arrayOfKeyphraseMetadata[i];
        if ((localKeyphraseMetadata.supportsPhrase(paramString)) && (localKeyphraseMetadata.supportsLocale(paramLocale))) {
          return localKeyphraseMetadata;
        }
        i += 1;
      }
    }
    Slog.w("KeyphraseEnrollmentInfo", "No Enrollment application supports the given keyphrase/locale");
    return null;
  }
  
  public Intent getManageKeyphraseIntent(int paramInt, String paramString, Locale paramLocale)
  {
    if ((this.mKeyphrasePackageMap == null) || (this.mKeyphrasePackageMap.isEmpty()))
    {
      Slog.w("KeyphraseEnrollmentInfo", "No enrollment application exists");
      return null;
    }
    KeyphraseMetadata localKeyphraseMetadata = getKeyphraseMetadata(paramString, paramLocale);
    if (localKeyphraseMetadata != null) {
      return new Intent("com.android.intent.action.MANAGE_VOICE_KEYPHRASES").setPackage((String)this.mKeyphrasePackageMap.get(localKeyphraseMetadata)).putExtra("com.android.intent.extra.VOICE_KEYPHRASE_HINT_TEXT", paramString).putExtra("com.android.intent.extra.VOICE_KEYPHRASE_LOCALE", paramLocale.toLanguageTag()).putExtra("com.android.intent.extra.VOICE_KEYPHRASE_ACTION", paramInt);
    }
    return null;
  }
  
  public String getParseError()
  {
    return this.mParseError;
  }
  
  public KeyphraseMetadata[] listKeyphraseMetadata()
  {
    return this.mKeyphrases;
  }
  
  public String toString()
  {
    return "KeyphraseEnrollmentInfo [Keyphrases=" + this.mKeyphrasePackageMap.toString() + ", ParseError=" + this.mParseError + "]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/soundtrigger/KeyphraseEnrollmentInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */