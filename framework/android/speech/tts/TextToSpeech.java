package android.speech.tts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Set;

public class TextToSpeech
{
  public static final String ACTION_TTS_QUEUE_PROCESSING_COMPLETED = "android.speech.tts.TTS_QUEUE_PROCESSING_COMPLETED";
  public static final int ERROR = -1;
  public static final int ERROR_INVALID_REQUEST = -8;
  public static final int ERROR_NETWORK = -6;
  public static final int ERROR_NETWORK_TIMEOUT = -7;
  public static final int ERROR_NOT_INSTALLED_YET = -9;
  public static final int ERROR_OUTPUT = -5;
  public static final int ERROR_SERVICE = -4;
  public static final int ERROR_SYNTHESIS = -3;
  public static final int LANG_AVAILABLE = 0;
  public static final int LANG_COUNTRY_AVAILABLE = 1;
  public static final int LANG_COUNTRY_VAR_AVAILABLE = 2;
  public static final int LANG_MISSING_DATA = -1;
  public static final int LANG_NOT_SUPPORTED = -2;
  public static final int QUEUE_ADD = 1;
  static final int QUEUE_DESTROY = 2;
  public static final int QUEUE_FLUSH = 0;
  public static final int STOPPED = -2;
  public static final int SUCCESS = 0;
  private static final String TAG = "TextToSpeech";
  private Connection mConnectingServiceConnection;
  private final Context mContext;
  private volatile String mCurrentEngine = null;
  private final Map<String, Uri> mEarcons;
  private final TtsEngines mEnginesHelper;
  private OnInitListener mInitListener;
  private final Bundle mParams = new Bundle();
  private String mRequestedEngine;
  private Connection mServiceConnection;
  private final Object mStartLock = new Object();
  private final boolean mUseFallback;
  private volatile UtteranceProgressListener mUtteranceProgressListener;
  private final Map<CharSequence, Uri> mUtterances;
  
  public TextToSpeech(Context paramContext, OnInitListener paramOnInitListener)
  {
    this(paramContext, paramOnInitListener, null);
  }
  
  public TextToSpeech(Context paramContext, OnInitListener paramOnInitListener, String paramString)
  {
    this(paramContext, paramOnInitListener, paramString, null, true);
  }
  
  public TextToSpeech(Context paramContext, OnInitListener paramOnInitListener, String paramString1, String paramString2, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mInitListener = paramOnInitListener;
    this.mRequestedEngine = paramString1;
    this.mUseFallback = paramBoolean;
    this.mEarcons = new HashMap();
    this.mUtterances = new HashMap();
    this.mUtteranceProgressListener = null;
    this.mEnginesHelper = new TtsEngines(this.mContext);
    initTts();
  }
  
  private boolean connectToEngine(String paramString)
  {
    Connection localConnection = new Connection(null);
    Intent localIntent = new Intent("android.intent.action.TTS_SERVICE");
    localIntent.setPackage(paramString);
    if (!this.mContext.bindService(localIntent, localConnection, 1))
    {
      Log.e("TextToSpeech", "Failed to bind to " + paramString);
      return false;
    }
    Log.i("TextToSpeech", "Sucessfully bound to " + paramString);
    this.mConnectingServiceConnection = localConnection;
    return true;
  }
  
  private Bundle convertParamsHashMaptoBundle(HashMap<String, String> paramHashMap)
  {
    if ((paramHashMap == null) || (paramHashMap.isEmpty())) {
      return null;
    }
    Bundle localBundle = new Bundle();
    copyIntParam(localBundle, paramHashMap, "streamType");
    copyIntParam(localBundle, paramHashMap, "sessionId");
    copyStringParam(localBundle, paramHashMap, "utteranceId");
    copyFloatParam(localBundle, paramHashMap, "volume");
    copyFloatParam(localBundle, paramHashMap, "pan");
    copyStringParam(localBundle, paramHashMap, "networkTts");
    copyStringParam(localBundle, paramHashMap, "embeddedTts");
    copyIntParam(localBundle, paramHashMap, "networkTimeoutMs");
    copyIntParam(localBundle, paramHashMap, "networkRetriesCount");
    if (!TextUtils.isEmpty(this.mCurrentEngine))
    {
      paramHashMap = paramHashMap.entrySet().iterator();
      while (paramHashMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramHashMap.next();
        String str = (String)localEntry.getKey();
        if ((str != null) && (str.startsWith(this.mCurrentEngine))) {
          localBundle.putString(str, (String)localEntry.getValue());
        }
      }
    }
    return localBundle;
  }
  
  private void copyFloatParam(Bundle paramBundle, HashMap<String, String> paramHashMap, String paramString)
  {
    paramHashMap = (String)paramHashMap.get(paramString);
    if (!TextUtils.isEmpty(paramHashMap)) {}
    try
    {
      paramBundle.putFloat(paramString, Float.parseFloat(paramHashMap));
      return;
    }
    catch (NumberFormatException paramBundle) {}
  }
  
  private void copyIntParam(Bundle paramBundle, HashMap<String, String> paramHashMap, String paramString)
  {
    paramHashMap = (String)paramHashMap.get(paramString);
    if (!TextUtils.isEmpty(paramHashMap)) {}
    try
    {
      paramBundle.putInt(paramString, Integer.parseInt(paramHashMap));
      return;
    }
    catch (NumberFormatException paramBundle) {}
  }
  
  private void copyStringParam(Bundle paramBundle, HashMap<String, String> paramHashMap, String paramString)
  {
    paramHashMap = (String)paramHashMap.get(paramString);
    if (paramHashMap != null) {
      paramBundle.putString(paramString, paramHashMap);
    }
  }
  
  private void dispatchOnInit(int paramInt)
  {
    synchronized (this.mStartLock)
    {
      if (this.mInitListener != null)
      {
        this.mInitListener.onInit(paramInt);
        this.mInitListener = null;
      }
      return;
    }
  }
  
  private IBinder getCallerIdentity()
  {
    return this.mServiceConnection.getCallerIdentity();
  }
  
  public static int getMaxSpeechInputLength()
  {
    return 4000;
  }
  
  private Bundle getParams(Bundle paramBundle)
  {
    if ((paramBundle == null) || (paramBundle.isEmpty())) {
      return this.mParams;
    }
    Bundle localBundle = new Bundle(this.mParams);
    localBundle.putAll(paramBundle);
    verifyIntegerBundleParam(localBundle, "streamType");
    verifyIntegerBundleParam(localBundle, "sessionId");
    verifyStringBundleParam(localBundle, "utteranceId");
    verifyFloatBundleParam(localBundle, "volume");
    verifyFloatBundleParam(localBundle, "pan");
    verifyBooleanBundleParam(localBundle, "networkTts");
    verifyBooleanBundleParam(localBundle, "embeddedTts");
    verifyIntegerBundleParam(localBundle, "networkTimeoutMs");
    verifyIntegerBundleParam(localBundle, "networkRetriesCount");
    return localBundle;
  }
  
  private Voice getVoice(ITextToSpeechService paramITextToSpeechService, String paramString)
    throws RemoteException
  {
    paramITextToSpeechService = paramITextToSpeechService.getVoices();
    if (paramITextToSpeechService == null)
    {
      Log.w("TextToSpeech", "getVoices returned null");
      return null;
    }
    paramITextToSpeechService = paramITextToSpeechService.iterator();
    while (paramITextToSpeechService.hasNext())
    {
      Voice localVoice = (Voice)paramITextToSpeechService.next();
      if (localVoice.getName().equals(paramString)) {
        return localVoice;
      }
    }
    Log.w("TextToSpeech", "Could not find voice " + paramString + " in voice list");
    return null;
  }
  
  private int initTts()
  {
    if (this.mRequestedEngine != null) {
      if (this.mEnginesHelper.isEngineInstalled(this.mRequestedEngine))
      {
        if (connectToEngine(this.mRequestedEngine))
        {
          this.mCurrentEngine = this.mRequestedEngine;
          return 0;
        }
        if (!this.mUseFallback)
        {
          this.mCurrentEngine = null;
          dispatchOnInit(-1);
          return -1;
        }
      }
      else if (!this.mUseFallback)
      {
        Log.i("TextToSpeech", "Requested engine not installed: " + this.mRequestedEngine);
        this.mCurrentEngine = null;
        dispatchOnInit(-1);
        return -1;
      }
    }
    String str1 = getDefaultEngine();
    String str2;
    if ((str1 == null) || (str1.equals(this.mRequestedEngine)))
    {
      str2 = this.mEnginesHelper.getHighestRankedEngineName();
      if ((str2 != null) && (!str2.equals(this.mRequestedEngine))) {
        break label179;
      }
    }
    label179:
    while ((str2.equals(str1)) || (!connectToEngine(str2)))
    {
      this.mCurrentEngine = null;
      dispatchOnInit(-1);
      return -1;
      if (!connectToEngine(str1)) {
        break;
      }
      this.mCurrentEngine = str1;
      return 0;
    }
    this.mCurrentEngine = str2;
    return 0;
  }
  
  private Uri makeResourceUri(String paramString, int paramInt)
  {
    return new Uri.Builder().scheme("android.resource").encodedAuthority(paramString).appendEncodedPath(String.valueOf(paramInt)).build();
  }
  
  private <R> R runAction(Action<R> paramAction, R paramR, String paramString)
  {
    return (R)runAction(paramAction, paramR, paramString, true, true);
  }
  
  private <R> R runAction(Action<R> paramAction, R paramR, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    synchronized (this.mStartLock)
    {
      if (this.mServiceConnection == null)
      {
        Log.w("TextToSpeech", paramString + " failed: not bound to TTS engine");
        return paramR;
      }
      paramAction = this.mServiceConnection.runAction(paramAction, paramR, paramString, paramBoolean1, paramBoolean2);
      return paramAction;
    }
  }
  
  private <R> R runActionNoReconnect(Action<R> paramAction, R paramR, String paramString, boolean paramBoolean)
  {
    return (R)runAction(paramAction, paramR, paramString, false, paramBoolean);
  }
  
  private static boolean verifyBooleanBundleParam(Bundle paramBundle, String paramString)
  {
    if (paramBundle.containsKey(paramString))
    {
      if (!(paramBundle.get(paramString) instanceof Boolean)) {}
      for (boolean bool = paramBundle.get(paramString) instanceof String; !bool; bool = true)
      {
        paramBundle.remove(paramString);
        Log.w("TextToSpeech", "Synthesis request paramter " + paramString + " containst value " + " with invalid type. Should be a Boolean or String");
        return false;
      }
    }
    return true;
  }
  
  private static boolean verifyFloatBundleParam(Bundle paramBundle, String paramString)
  {
    if (paramBundle.containsKey(paramString))
    {
      if (!(paramBundle.get(paramString) instanceof Float)) {}
      for (boolean bool = paramBundle.get(paramString) instanceof Double; !bool; bool = true)
      {
        paramBundle.remove(paramString);
        Log.w("TextToSpeech", "Synthesis request paramter " + paramString + " containst value " + " with invalid type. Should be a Float or a Double");
        return false;
      }
    }
    return true;
  }
  
  private static boolean verifyIntegerBundleParam(Bundle paramBundle, String paramString)
  {
    if (paramBundle.containsKey(paramString))
    {
      if (!(paramBundle.get(paramString) instanceof Integer)) {}
      for (boolean bool = paramBundle.get(paramString) instanceof Long; !bool; bool = true)
      {
        paramBundle.remove(paramString);
        Log.w("TextToSpeech", "Synthesis request paramter " + paramString + " containst value " + " with invalid type. Should be an Integer or a Long");
        return false;
      }
    }
    return true;
  }
  
  private static boolean verifyStringBundleParam(Bundle paramBundle, String paramString)
  {
    if ((paramBundle.containsKey(paramString)) && (!(paramBundle.get(paramString) instanceof String)))
    {
      paramBundle.remove(paramString);
      Log.w("TextToSpeech", "Synthesis request paramter " + paramString + " containst value " + " with invalid type. Should be a String");
      return false;
    }
    return true;
  }
  
  public int addEarcon(String paramString, File paramFile)
  {
    synchronized (this.mStartLock)
    {
      this.mEarcons.put(paramString, Uri.fromFile(paramFile));
      return 0;
    }
  }
  
  @Deprecated
  public int addEarcon(String paramString1, String paramString2)
  {
    synchronized (this.mStartLock)
    {
      this.mEarcons.put(paramString1, Uri.parse(paramString2));
      return 0;
    }
  }
  
  public int addEarcon(String paramString1, String paramString2, int paramInt)
  {
    synchronized (this.mStartLock)
    {
      this.mEarcons.put(paramString1, makeResourceUri(paramString2, paramInt));
      return 0;
    }
  }
  
  public int addSpeech(CharSequence paramCharSequence, File paramFile)
  {
    synchronized (this.mStartLock)
    {
      this.mUtterances.put(paramCharSequence, Uri.fromFile(paramFile));
      return 0;
    }
  }
  
  public int addSpeech(CharSequence paramCharSequence, String paramString, int paramInt)
  {
    synchronized (this.mStartLock)
    {
      this.mUtterances.put(paramCharSequence, makeResourceUri(paramString, paramInt));
      return 0;
    }
  }
  
  public int addSpeech(String paramString1, String paramString2)
  {
    synchronized (this.mStartLock)
    {
      this.mUtterances.put(paramString1, Uri.parse(paramString2));
      return 0;
    }
  }
  
  public int addSpeech(String paramString1, String paramString2, int paramInt)
  {
    synchronized (this.mStartLock)
    {
      this.mUtterances.put(paramString1, makeResourceUri(paramString2, paramInt));
      return 0;
    }
  }
  
  @Deprecated
  public boolean areDefaultsEnforced()
  {
    return false;
  }
  
  public Set<Locale> getAvailableLanguages()
  {
    (Set)runAction(new Action()
    {
      public Set<Locale> run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        Object localObject = paramAnonymousITextToSpeechService.getVoices();
        if (localObject == null) {
          return new HashSet();
        }
        paramAnonymousITextToSpeechService = new HashSet();
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext()) {
          paramAnonymousITextToSpeechService.add(((Voice)((Iterator)localObject).next()).getLocale());
        }
        return paramAnonymousITextToSpeechService;
      }
    }, null, "getAvailableLanguages");
  }
  
  public String getCurrentEngine()
  {
    return this.mCurrentEngine;
  }
  
  public String getDefaultEngine()
  {
    return this.mEnginesHelper.getDefaultEngine();
  }
  
  @Deprecated
  public Locale getDefaultLanguage()
  {
    (Locale)runAction(new Action()
    {
      public Locale run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        paramAnonymousITextToSpeechService = paramAnonymousITextToSpeechService.getClientDefaultLanguage();
        return new Locale(paramAnonymousITextToSpeechService[0], paramAnonymousITextToSpeechService[1], paramAnonymousITextToSpeechService[2]);
      }
    }, null, "getDefaultLanguage");
  }
  
  public Voice getDefaultVoice()
  {
    (Voice)runAction(new Action()
    {
      public Voice run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        Object localObject = paramAnonymousITextToSpeechService.getClientDefaultLanguage();
        if ((localObject == null) || (localObject.length == 0))
        {
          Log.e("TextToSpeech", "service.getClientDefaultLanguage() returned empty array");
          return null;
        }
        String str2 = localObject[0];
        if (localObject.length > 1)
        {
          str1 = localObject[1];
          if (localObject.length <= 2) {
            break label72;
          }
        }
        label72:
        for (localObject = localObject[2];; localObject = "")
        {
          if (paramAnonymousITextToSpeechService.isLanguageAvailable(str2, str1, (String)localObject) >= 0) {
            break label78;
          }
          return null;
          str1 = "";
          break;
        }
        label78:
        String str1 = paramAnonymousITextToSpeechService.getDefaultVoiceNameFor(str2, str1, (String)localObject);
        if (TextUtils.isEmpty(str1)) {
          return null;
        }
        paramAnonymousITextToSpeechService = paramAnonymousITextToSpeechService.getVoices();
        if (paramAnonymousITextToSpeechService == null) {
          return null;
        }
        paramAnonymousITextToSpeechService = paramAnonymousITextToSpeechService.iterator();
        while (paramAnonymousITextToSpeechService.hasNext())
        {
          localObject = (Voice)paramAnonymousITextToSpeechService.next();
          if (((Voice)localObject).getName().equals(str1)) {
            return (Voice)localObject;
          }
        }
        return null;
      }
    }, null, "getDefaultVoice");
  }
  
  public List<EngineInfo> getEngines()
  {
    return this.mEnginesHelper.getEngines();
  }
  
  @Deprecated
  public Set<String> getFeatures(final Locale paramLocale)
  {
    (Set)runAction(new Action()
    {
      public Set<String> run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        try
        {
          paramAnonymousITextToSpeechService = paramAnonymousITextToSpeechService.getFeaturesForLanguage(paramLocale.getISO3Language(), paramLocale.getISO3Country(), paramLocale.getVariant());
          if (paramAnonymousITextToSpeechService != null)
          {
            HashSet localHashSet = new HashSet();
            Collections.addAll(localHashSet, paramAnonymousITextToSpeechService);
            return localHashSet;
          }
        }
        catch (MissingResourceException paramAnonymousITextToSpeechService)
        {
          Log.w("TextToSpeech", "Couldn't retrieve 3 letter ISO 639-2/T language and/or ISO 3166 country code for locale: " + paramLocale, paramAnonymousITextToSpeechService);
          return null;
        }
        return null;
      }
    }, null, "getFeatures");
  }
  
  @Deprecated
  public Locale getLanguage()
  {
    (Locale)runAction(new Action()
    {
      public Locale run(ITextToSpeechService paramAnonymousITextToSpeechService)
      {
        return new Locale(TextToSpeech.-get2(TextToSpeech.this).getString("language", ""), TextToSpeech.-get2(TextToSpeech.this).getString("country", ""), TextToSpeech.-get2(TextToSpeech.this).getString("variant", ""));
      }
    }, null, "getLanguage");
  }
  
  public Voice getVoice()
  {
    (Voice)runAction(new Action()
    {
      public Voice run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        String str = TextToSpeech.-get2(TextToSpeech.this).getString("voiceName", "");
        if (TextUtils.isEmpty(str)) {
          return null;
        }
        return TextToSpeech.-wrap2(TextToSpeech.this, paramAnonymousITextToSpeechService, str);
      }
    }, null, "getVoice");
  }
  
  public Set<Voice> getVoices()
  {
    (Set)runAction(new Action()
    {
      public Set<Voice> run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        paramAnonymousITextToSpeechService = paramAnonymousITextToSpeechService.getVoices();
        if (paramAnonymousITextToSpeechService != null) {
          return new HashSet(paramAnonymousITextToSpeechService);
        }
        return new HashSet();
      }
    }, null, "getVoices");
  }
  
  public int isLanguageAvailable(final Locale paramLocale)
  {
    ((Integer)runAction(new Action()
    {
      public Integer run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        try
        {
          String str1 = paramLocale.getISO3Language();
          String str2;
          return Integer.valueOf(-2);
        }
        catch (MissingResourceException paramAnonymousITextToSpeechService)
        {
          try
          {
            str2 = paramLocale.getISO3Country();
            return Integer.valueOf(paramAnonymousITextToSpeechService.isLanguageAvailable(str1, str2, paramLocale.getVariant()));
          }
          catch (MissingResourceException paramAnonymousITextToSpeechService)
          {
            Log.w("TextToSpeech", "Couldn't retrieve ISO 3166 country code for locale: " + paramLocale, paramAnonymousITextToSpeechService);
          }
          paramAnonymousITextToSpeechService = paramAnonymousITextToSpeechService;
          Log.w("TextToSpeech", "Couldn't retrieve ISO 639-2/T language code for locale: " + paramLocale, paramAnonymousITextToSpeechService);
          return Integer.valueOf(-2);
        }
      }
    }, Integer.valueOf(-2), "isLanguageAvailable")).intValue();
  }
  
  public boolean isSpeaking()
  {
    ((Boolean)runAction(new Action()
    {
      public Boolean run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        return Boolean.valueOf(paramAnonymousITextToSpeechService.isSpeaking());
      }
    }, Boolean.valueOf(false), "isSpeaking")).booleanValue();
  }
  
  public int playEarcon(final String paramString1, final int paramInt, final Bundle paramBundle, final String paramString2)
  {
    ((Integer)runAction(new Action()
    {
      public Integer run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        Uri localUri = (Uri)TextToSpeech.-get1(TextToSpeech.this).get(paramString1);
        if (localUri == null) {
          return Integer.valueOf(-1);
        }
        return Integer.valueOf(paramAnonymousITextToSpeechService.playAudio(TextToSpeech.-wrap1(TextToSpeech.this), localUri, paramInt, TextToSpeech.-wrap0(TextToSpeech.this, paramBundle), paramString2));
      }
    }, Integer.valueOf(-1), "playEarcon")).intValue();
  }
  
  @Deprecated
  public int playEarcon(String paramString, int paramInt, HashMap<String, String> paramHashMap)
  {
    Object localObject = null;
    Bundle localBundle = convertParamsHashMaptoBundle(paramHashMap);
    if (paramHashMap == null) {}
    for (paramHashMap = (HashMap<String, String>)localObject;; paramHashMap = (String)paramHashMap.get("utteranceId")) {
      return playEarcon(paramString, paramInt, localBundle, paramHashMap);
    }
  }
  
  @Deprecated
  public int playSilence(long paramLong, int paramInt, HashMap<String, String> paramHashMap)
  {
    Object localObject = null;
    if (paramHashMap == null) {}
    for (paramHashMap = (HashMap<String, String>)localObject;; paramHashMap = (String)paramHashMap.get("utteranceId")) {
      return playSilentUtterance(paramLong, paramInt, paramHashMap);
    }
  }
  
  public int playSilentUtterance(final long paramLong, int paramInt, final String paramString)
  {
    ((Integer)runAction(new Action()
    {
      public Integer run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        return Integer.valueOf(paramAnonymousITextToSpeechService.playSilence(TextToSpeech.-wrap1(TextToSpeech.this), paramLong, paramString, this.val$utteranceId));
      }
    }, Integer.valueOf(-1), "playSilentUtterance")).intValue();
  }
  
  public int setAudioAttributes(AudioAttributes paramAudioAttributes)
  {
    if (paramAudioAttributes != null) {
      synchronized (this.mStartLock)
      {
        this.mParams.putParcelable("audioAttributes", paramAudioAttributes);
        return 0;
      }
    }
    return -1;
  }
  
  @Deprecated
  public int setEngineByPackageName(String paramString)
  {
    this.mRequestedEngine = paramString;
    return initTts();
  }
  
  public int setLanguage(final Locale paramLocale)
  {
    ((Integer)runAction(new Action()
    {
      public Integer run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        if (paramLocale == null) {
          return Integer.valueOf(-2);
        }
        String str1;
        String str3;
        int i;
        String str2;
        try
        {
          localObject = paramLocale.getISO3Language();
          if (paramAnonymousITextToSpeechService.loadVoice(TextToSpeech.-wrap1(TextToSpeech.this), str2) != -1) {
            break label284;
          }
        }
        catch (MissingResourceException paramAnonymousITextToSpeechService)
        {
          try
          {
            str1 = paramLocale.getISO3Country();
            str3 = paramLocale.getVariant();
            i = paramAnonymousITextToSpeechService.isLanguageAvailable((String)localObject, str1, str3);
            if (i < 0) {
              break label457;
            }
            str2 = paramAnonymousITextToSpeechService.getDefaultVoiceNameFor((String)localObject, str1, str3);
            if (!TextUtils.isEmpty(str2)) {
              break label199;
            }
            Log.w("TextToSpeech", "Couldn't find the default voice for " + (String)localObject + "-" + str1 + "-" + str3);
            return Integer.valueOf(-2);
          }
          catch (MissingResourceException paramAnonymousITextToSpeechService)
          {
            Log.w("TextToSpeech", "Couldn't retrieve ISO 3166 country code for locale: " + paramLocale, paramAnonymousITextToSpeechService);
            return Integer.valueOf(-2);
          }
          paramAnonymousITextToSpeechService = paramAnonymousITextToSpeechService;
          Log.w("TextToSpeech", "Couldn't retrieve ISO 639-2/T language code for locale: " + paramLocale, paramAnonymousITextToSpeechService);
          return Integer.valueOf(-2);
        }
        label199:
        Log.w("TextToSpeech", "The service claimed " + (String)localObject + "-" + str1 + "-" + str3 + " was available with voice name " + str2 + " but loadVoice returned ERROR");
        return Integer.valueOf(-2);
        label284:
        localVoice = TextToSpeech.-wrap2(TextToSpeech.this, paramAnonymousITextToSpeechService, str2);
        if (localVoice == null)
        {
          Log.w("TextToSpeech", "getDefaultVoiceNameFor returned " + str2 + " for locale " + (String)localObject + "-" + str1 + "-" + str3 + " but getVoice returns null");
          return Integer.valueOf(-2);
        }
        paramAnonymousITextToSpeechService = "";
        try
        {
          localObject = localVoice.getLocale().getISO3Language();
          paramAnonymousITextToSpeechService = (ITextToSpeechService)localObject;
        }
        catch (MissingResourceException localMissingResourceException1)
        {
          for (;;)
          {
            Log.w("TextToSpeech", "Couldn't retrieve ISO 639-2/T language code for locale: " + localVoice.getLocale(), localMissingResourceException1);
          }
        }
        Object localObject = "";
        try
        {
          str1 = localVoice.getLocale().getISO3Country();
          localObject = str1;
        }
        catch (MissingResourceException localMissingResourceException2)
        {
          for (;;)
          {
            Log.w("TextToSpeech", "Couldn't retrieve ISO 3166 country code for locale: " + localVoice.getLocale(), localMissingResourceException2);
          }
        }
        TextToSpeech.-get2(TextToSpeech.this).putString("voiceName", str2);
        TextToSpeech.-get2(TextToSpeech.this).putString("language", paramAnonymousITextToSpeechService);
        TextToSpeech.-get2(TextToSpeech.this).putString("country", (String)localObject);
        TextToSpeech.-get2(TextToSpeech.this).putString("variant", localVoice.getLocale().getVariant());
        label457:
        return Integer.valueOf(i);
      }
    }, Integer.valueOf(-2), "setLanguage")).intValue();
  }
  
  @Deprecated
  public int setOnUtteranceCompletedListener(OnUtteranceCompletedListener paramOnUtteranceCompletedListener)
  {
    this.mUtteranceProgressListener = UtteranceProgressListener.from(paramOnUtteranceCompletedListener);
    return 0;
  }
  
  public int setOnUtteranceProgressListener(UtteranceProgressListener paramUtteranceProgressListener)
  {
    this.mUtteranceProgressListener = paramUtteranceProgressListener;
    return 0;
  }
  
  public int setPitch(float paramFloat)
  {
    if (paramFloat > 0.0F)
    {
      int i = (int)(100.0F * paramFloat);
      if (i > 0) {
        synchronized (this.mStartLock)
        {
          this.mParams.putInt("pitch", i);
          return 0;
        }
      }
    }
    return -1;
  }
  
  public int setSpeechRate(float paramFloat)
  {
    if (paramFloat > 0.0F)
    {
      int i = (int)(100.0F * paramFloat);
      if (i > 0) {
        synchronized (this.mStartLock)
        {
          this.mParams.putInt("rate", i);
          return 0;
        }
      }
    }
    return -1;
  }
  
  public int setVoice(final Voice paramVoice)
  {
    ((Integer)runAction(new Action()
    {
      public Integer run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        int i = paramAnonymousITextToSpeechService.loadVoice(TextToSpeech.-wrap1(TextToSpeech.this), paramVoice.getName());
        if (i == 0)
        {
          TextToSpeech.-get2(TextToSpeech.this).putString("voiceName", paramVoice.getName());
          paramAnonymousITextToSpeechService = "";
        }
        try
        {
          localObject = paramVoice.getLocale().getISO3Language();
          paramAnonymousITextToSpeechService = (ITextToSpeechService)localObject;
        }
        catch (MissingResourceException localMissingResourceException1)
        {
          for (;;)
          {
            Object localObject;
            Log.w("TextToSpeech", "Couldn't retrieve ISO 639-2/T language code for locale: " + paramVoice.getLocale(), localMissingResourceException1);
          }
        }
        localObject = "";
        try
        {
          String str = paramVoice.getLocale().getISO3Country();
          localObject = str;
        }
        catch (MissingResourceException localMissingResourceException2)
        {
          for (;;)
          {
            Log.w("TextToSpeech", "Couldn't retrieve ISO 3166 country code for locale: " + paramVoice.getLocale(), localMissingResourceException2);
          }
        }
        TextToSpeech.-get2(TextToSpeech.this).putString("language", paramAnonymousITextToSpeechService);
        TextToSpeech.-get2(TextToSpeech.this).putString("country", (String)localObject);
        TextToSpeech.-get2(TextToSpeech.this).putString("variant", paramVoice.getLocale().getVariant());
        return Integer.valueOf(i);
      }
    }, Integer.valueOf(-2), "setVoice")).intValue();
  }
  
  public void shutdown()
  {
    synchronized (this.mStartLock)
    {
      if (this.mConnectingServiceConnection != null)
      {
        this.mContext.unbindService(this.mConnectingServiceConnection);
        this.mConnectingServiceConnection = null;
        return;
      }
      runActionNoReconnect(new Action()
      {
        public Void run(ITextToSpeechService paramAnonymousITextToSpeechService)
          throws RemoteException
        {
          paramAnonymousITextToSpeechService.setCallback(TextToSpeech.-wrap1(TextToSpeech.this), null);
          paramAnonymousITextToSpeechService.stop(TextToSpeech.-wrap1(TextToSpeech.this));
          TextToSpeech.-get3(TextToSpeech.this).disconnect();
          TextToSpeech.-set2(TextToSpeech.this, null);
          TextToSpeech.-set1(TextToSpeech.this, null);
          return null;
        }
      }, null, "shutdown", false);
      return;
    }
  }
  
  public int speak(final CharSequence paramCharSequence, final int paramInt, final Bundle paramBundle, final String paramString)
  {
    ((Integer)runAction(new Action()
    {
      public Integer run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        Uri localUri = (Uri)TextToSpeech.-get6(TextToSpeech.this).get(paramCharSequence);
        if (localUri != null) {
          return Integer.valueOf(paramAnonymousITextToSpeechService.playAudio(TextToSpeech.-wrap1(TextToSpeech.this), localUri, paramInt, TextToSpeech.-wrap0(TextToSpeech.this, paramBundle), paramString));
        }
        return Integer.valueOf(paramAnonymousITextToSpeechService.speak(TextToSpeech.-wrap1(TextToSpeech.this), paramCharSequence, paramInt, TextToSpeech.-wrap0(TextToSpeech.this, paramBundle), paramString));
      }
    }, Integer.valueOf(-1), "speak")).intValue();
  }
  
  @Deprecated
  public int speak(String paramString, int paramInt, HashMap<String, String> paramHashMap)
  {
    Object localObject = null;
    Bundle localBundle = convertParamsHashMaptoBundle(paramHashMap);
    if (paramHashMap == null) {}
    for (paramHashMap = (HashMap<String, String>)localObject;; paramHashMap = (String)paramHashMap.get("utteranceId")) {
      return speak(paramString, paramInt, localBundle, paramHashMap);
    }
  }
  
  public int stop()
  {
    ((Integer)runAction(new Action()
    {
      public Integer run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        return Integer.valueOf(paramAnonymousITextToSpeechService.stop(TextToSpeech.-wrap1(TextToSpeech.this)));
      }
    }, Integer.valueOf(-1), "stop")).intValue();
  }
  
  public int synthesizeToFile(final CharSequence paramCharSequence, final Bundle paramBundle, final File paramFile, final String paramString)
  {
    ((Integer)runAction(new Action()
    {
      public Integer run(ITextToSpeechService paramAnonymousITextToSpeechService)
        throws RemoteException
      {
        try
        {
          if ((!paramFile.exists()) || (paramFile.canWrite()))
          {
            ParcelFileDescriptor localParcelFileDescriptor = ParcelFileDescriptor.open(paramFile, 738197504);
            int i = paramAnonymousITextToSpeechService.synthesizeToFileDescriptor(TextToSpeech.-wrap1(TextToSpeech.this), paramCharSequence, localParcelFileDescriptor, TextToSpeech.-wrap0(TextToSpeech.this, paramBundle), paramString);
            localParcelFileDescriptor.close();
            return Integer.valueOf(i);
          }
          Log.e("TextToSpeech", "Can't write to " + paramFile);
          return Integer.valueOf(-1);
        }
        catch (IOException paramAnonymousITextToSpeechService)
        {
          Log.e("TextToSpeech", "Closing file " + paramFile + " failed", paramAnonymousITextToSpeechService);
          return Integer.valueOf(-1);
        }
        catch (FileNotFoundException paramAnonymousITextToSpeechService)
        {
          Log.e("TextToSpeech", "Opening file " + paramFile + " failed", paramAnonymousITextToSpeechService);
        }
        return Integer.valueOf(-1);
      }
    }, Integer.valueOf(-1), "synthesizeToFile")).intValue();
  }
  
  @Deprecated
  public int synthesizeToFile(String paramString1, HashMap<String, String> paramHashMap, String paramString2)
  {
    return synthesizeToFile(paramString1, convertParamsHashMaptoBundle(paramHashMap), new File(paramString2), (String)paramHashMap.get("utteranceId"));
  }
  
  private static abstract interface Action<R>
  {
    public abstract R run(ITextToSpeechService paramITextToSpeechService)
      throws RemoteException;
  }
  
  private class Connection
    implements ServiceConnection
  {
    private final ITextToSpeechCallback.Stub mCallback = new ITextToSpeechCallback.Stub()
    {
      public void onAudioAvailable(String paramAnonymousString, byte[] paramAnonymousArrayOfByte)
      {
        UtteranceProgressListener localUtteranceProgressListener = TextToSpeech.-get5(TextToSpeech.this);
        if (localUtteranceProgressListener != null) {
          localUtteranceProgressListener.onAudioAvailable(paramAnonymousString, paramAnonymousArrayOfByte);
        }
      }
      
      public void onBeginSynthesis(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        UtteranceProgressListener localUtteranceProgressListener = TextToSpeech.-get5(TextToSpeech.this);
        if (localUtteranceProgressListener != null) {
          localUtteranceProgressListener.onBeginSynthesis(paramAnonymousString, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
        }
      }
      
      public void onError(String paramAnonymousString, int paramAnonymousInt)
      {
        UtteranceProgressListener localUtteranceProgressListener = TextToSpeech.-get5(TextToSpeech.this);
        if (localUtteranceProgressListener != null) {
          localUtteranceProgressListener.onError(paramAnonymousString);
        }
      }
      
      public void onStart(String paramAnonymousString)
      {
        UtteranceProgressListener localUtteranceProgressListener = TextToSpeech.-get5(TextToSpeech.this);
        if (localUtteranceProgressListener != null) {
          localUtteranceProgressListener.onStart(paramAnonymousString);
        }
      }
      
      public void onStop(String paramAnonymousString, boolean paramAnonymousBoolean)
        throws RemoteException
      {
        UtteranceProgressListener localUtteranceProgressListener = TextToSpeech.-get5(TextToSpeech.this);
        if (localUtteranceProgressListener != null) {
          localUtteranceProgressListener.onStop(paramAnonymousString, paramAnonymousBoolean);
        }
      }
      
      public void onSuccess(String paramAnonymousString)
      {
        UtteranceProgressListener localUtteranceProgressListener = TextToSpeech.-get5(TextToSpeech.this);
        if (localUtteranceProgressListener != null) {
          localUtteranceProgressListener.onDone(paramAnonymousString);
        }
      }
    };
    private boolean mEstablished;
    private SetupConnectionAsyncTask mOnSetupConnectionAsyncTask;
    private ITextToSpeechService mService;
    
    private Connection() {}
    
    private boolean clearServiceConnection()
    {
      Object localObject1 = TextToSpeech.-get4(TextToSpeech.this);
      boolean bool = false;
      try
      {
        if (this.mOnSetupConnectionAsyncTask != null)
        {
          bool = this.mOnSetupConnectionAsyncTask.cancel(false);
          this.mOnSetupConnectionAsyncTask = null;
        }
        this.mService = null;
        if (TextToSpeech.-get3(TextToSpeech.this) == this) {
          TextToSpeech.-set2(TextToSpeech.this, null);
        }
        return bool;
      }
      finally {}
    }
    
    public void disconnect()
    {
      TextToSpeech.-get0(TextToSpeech.this).unbindService(this);
      clearServiceConnection();
    }
    
    public IBinder getCallerIdentity()
    {
      return this.mCallback;
    }
    
    public boolean isEstablished()
    {
      if (this.mService != null) {
        return this.mEstablished;
      }
      return false;
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      synchronized (TextToSpeech.-get4(TextToSpeech.this))
      {
        TextToSpeech.-set0(TextToSpeech.this, null);
        Log.i("TextToSpeech", "Connected to " + paramComponentName);
        if (this.mOnSetupConnectionAsyncTask != null) {
          this.mOnSetupConnectionAsyncTask.cancel(false);
        }
        this.mService = ITextToSpeechService.Stub.asInterface(paramIBinder);
        TextToSpeech.-set2(TextToSpeech.this, this);
        this.mEstablished = false;
        this.mOnSetupConnectionAsyncTask = new SetupConnectionAsyncTask(paramComponentName);
        this.mOnSetupConnectionAsyncTask.execute(new Void[0]);
        return;
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      Log.i("TextToSpeech", "Asked to disconnect from " + paramComponentName);
      if (clearServiceConnection()) {
        TextToSpeech.-wrap4(TextToSpeech.this, -1);
      }
    }
    
    public <R> R runAction(TextToSpeech.Action<R> paramAction, R paramR, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      synchronized (TextToSpeech.-get4(TextToSpeech.this))
      {
        try
        {
          if (this.mService == null)
          {
            Log.w("TextToSpeech", paramString + " failed: not connected to TTS engine");
            return paramR;
          }
          if ((!paramBoolean2) || (isEstablished()))
          {
            paramAction = paramAction.run(this.mService);
            return paramAction;
          }
          Log.w("TextToSpeech", paramString + " failed: TTS engine connection not fully set up");
          return paramR;
        }
        catch (RemoteException paramAction)
        {
          Log.e("TextToSpeech", paramString + " failed", paramAction);
          if (paramBoolean1)
          {
            disconnect();
            TextToSpeech.-wrap3(TextToSpeech.this);
          }
          return paramR;
        }
      }
    }
    
    private class SetupConnectionAsyncTask
      extends AsyncTask<Void, Void, Integer>
    {
      private final ComponentName mName;
      
      public SetupConnectionAsyncTask(ComponentName paramComponentName)
      {
        this.mName = paramComponentName;
      }
      
      protected Integer doInBackground(Void... arg1)
      {
        synchronized (TextToSpeech.-get4(TextToSpeech.this))
        {
          boolean bool = isCancelled();
          if (bool) {
            return null;
          }
          try
          {
            TextToSpeech.Connection.-get2(TextToSpeech.Connection.this).setCallback(TextToSpeech.Connection.this.getCallerIdentity(), TextToSpeech.Connection.-get0(TextToSpeech.Connection.this));
            if (TextToSpeech.-get2(TextToSpeech.this).getString("language") == null)
            {
              Object localObject1 = TextToSpeech.Connection.-get2(TextToSpeech.Connection.this).getClientDefaultLanguage();
              TextToSpeech.-get2(TextToSpeech.this).putString("language", localObject1[0]);
              TextToSpeech.-get2(TextToSpeech.this).putString("country", localObject1[1]);
              TextToSpeech.-get2(TextToSpeech.this).putString("variant", localObject1[2]);
              localObject1 = TextToSpeech.Connection.-get2(TextToSpeech.Connection.this).getDefaultVoiceNameFor(localObject1[0], localObject1[1], localObject1[2]);
              TextToSpeech.-get2(TextToSpeech.this).putString("voiceName", (String)localObject1);
            }
            Log.i("TextToSpeech", "Set up connection to " + this.mName);
            return Integer.valueOf(0);
          }
          catch (RemoteException localRemoteException)
          {
            Log.e("TextToSpeech", "Error connecting to service, setCallback() failed");
            return Integer.valueOf(-1);
          }
        }
      }
      
      protected void onPostExecute(Integer paramInteger)
      {
        synchronized (TextToSpeech.-get4(TextToSpeech.this))
        {
          if (TextToSpeech.Connection.-get1(TextToSpeech.Connection.this) == this) {
            TextToSpeech.Connection.-set1(TextToSpeech.Connection.this, null);
          }
          TextToSpeech.Connection.-set0(TextToSpeech.Connection.this, true);
          TextToSpeech.-wrap4(TextToSpeech.this, paramInteger.intValue());
          return;
        }
      }
    }
  }
  
  public class Engine
  {
    public static final String ACTION_CHECK_TTS_DATA = "android.speech.tts.engine.CHECK_TTS_DATA";
    public static final String ACTION_GET_SAMPLE_TEXT = "android.speech.tts.engine.GET_SAMPLE_TEXT";
    public static final String ACTION_INSTALL_TTS_DATA = "android.speech.tts.engine.INSTALL_TTS_DATA";
    public static final String ACTION_TTS_DATA_INSTALLED = "android.speech.tts.engine.TTS_DATA_INSTALLED";
    @Deprecated
    public static final int CHECK_VOICE_DATA_BAD_DATA = -1;
    public static final int CHECK_VOICE_DATA_FAIL = 0;
    @Deprecated
    public static final int CHECK_VOICE_DATA_MISSING_DATA = -2;
    @Deprecated
    public static final int CHECK_VOICE_DATA_MISSING_VOLUME = -3;
    public static final int CHECK_VOICE_DATA_PASS = 1;
    @Deprecated
    public static final String DEFAULT_ENGINE = "com.svox.pico";
    public static final float DEFAULT_PAN = 0.0F;
    public static final int DEFAULT_PITCH = 100;
    public static final int DEFAULT_RATE = 100;
    public static final int DEFAULT_STREAM = 3;
    public static final float DEFAULT_VOLUME = 1.0F;
    public static final String EXTRA_AVAILABLE_VOICES = "availableVoices";
    @Deprecated
    public static final String EXTRA_CHECK_VOICE_DATA_FOR = "checkVoiceDataFor";
    public static final String EXTRA_SAMPLE_TEXT = "sampleText";
    @Deprecated
    public static final String EXTRA_TTS_DATA_INSTALLED = "dataInstalled";
    public static final String EXTRA_UNAVAILABLE_VOICES = "unavailableVoices";
    @Deprecated
    public static final String EXTRA_VOICE_DATA_FILES = "dataFiles";
    @Deprecated
    public static final String EXTRA_VOICE_DATA_FILES_INFO = "dataFilesInfo";
    @Deprecated
    public static final String EXTRA_VOICE_DATA_ROOT_DIRECTORY = "dataRoot";
    public static final String INTENT_ACTION_TTS_SERVICE = "android.intent.action.TTS_SERVICE";
    @Deprecated
    public static final String KEY_FEATURE_EMBEDDED_SYNTHESIS = "embeddedTts";
    public static final String KEY_FEATURE_NETWORK_RETRIES_COUNT = "networkRetriesCount";
    @Deprecated
    public static final String KEY_FEATURE_NETWORK_SYNTHESIS = "networkTts";
    public static final String KEY_FEATURE_NETWORK_TIMEOUT_MS = "networkTimeoutMs";
    public static final String KEY_FEATURE_NOT_INSTALLED = "notInstalled";
    public static final String KEY_PARAM_AUDIO_ATTRIBUTES = "audioAttributes";
    public static final String KEY_PARAM_COUNTRY = "country";
    public static final String KEY_PARAM_ENGINE = "engine";
    public static final String KEY_PARAM_LANGUAGE = "language";
    public static final String KEY_PARAM_PAN = "pan";
    public static final String KEY_PARAM_PITCH = "pitch";
    public static final String KEY_PARAM_RATE = "rate";
    public static final String KEY_PARAM_SESSION_ID = "sessionId";
    public static final String KEY_PARAM_STREAM = "streamType";
    public static final String KEY_PARAM_UTTERANCE_ID = "utteranceId";
    public static final String KEY_PARAM_VARIANT = "variant";
    public static final String KEY_PARAM_VOICE_NAME = "voiceName";
    public static final String KEY_PARAM_VOLUME = "volume";
    public static final String SERVICE_META_DATA = "android.speech.tts";
    public static final int USE_DEFAULTS = 0;
    
    public Engine() {}
  }
  
  public static class EngineInfo
  {
    public int icon;
    public String label;
    public String name;
    public int priority;
    public boolean system;
    
    public String toString()
    {
      return "EngineInfo{name=" + this.name + "}";
    }
  }
  
  public static abstract interface OnInitListener
  {
    public abstract void onInit(int paramInt);
  }
  
  @Deprecated
  public static abstract interface OnUtteranceCompletedListener
  {
    public abstract void onUtteranceCompleted(String paramString);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/TextToSpeech.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */