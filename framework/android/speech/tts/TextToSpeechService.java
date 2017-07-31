package android.speech.tts;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public abstract class TextToSpeechService
  extends Service
{
  private static final boolean DBG = false;
  private static final String SYNTH_THREAD_NAME = "SynthThread";
  private static final String TAG = "TextToSpeechService";
  private AudioPlaybackHandler mAudioPlaybackHandler;
  private final ITextToSpeechService.Stub mBinder = new ITextToSpeechService.Stub()
  {
    private boolean checkNonNull(Object... paramAnonymousVarArgs)
    {
      int j = paramAnonymousVarArgs.length;
      int i = 0;
      while (i < j)
      {
        if (paramAnonymousVarArgs[i] == null) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    
    private String intern(String paramAnonymousString)
    {
      return paramAnonymousString.intern();
    }
    
    public String[] getClientDefaultLanguage()
    {
      return TextToSpeechService.-wrap1(TextToSpeechService.this);
    }
    
    public String getDefaultVoiceNameFor(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
    {
      if (!checkNonNull(new Object[] { paramAnonymousString1 })) {
        return null;
      }
      int i = TextToSpeechService.this.onIsLanguageAvailable(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
      if ((i == 0) || (i == 1)) {}
      while (i == 2) {
        return TextToSpeechService.this.onGetDefaultVoiceNameFor(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
      }
      return null;
    }
    
    public String[] getFeaturesForLanguage(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
    {
      paramAnonymousString1 = TextToSpeechService.this.onGetFeaturesForLanguage(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
      if (paramAnonymousString1 != null)
      {
        paramAnonymousString2 = new String[paramAnonymousString1.size()];
        paramAnonymousString1.toArray(paramAnonymousString2);
        return paramAnonymousString2;
      }
      return new String[0];
    }
    
    public String[] getLanguage()
    {
      return TextToSpeechService.this.onGetLanguage();
    }
    
    public List<Voice> getVoices()
    {
      return TextToSpeechService.this.onGetVoices();
    }
    
    public int isLanguageAvailable(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
    {
      if (!checkNonNull(new Object[] { paramAnonymousString1 })) {
        return -1;
      }
      return TextToSpeechService.this.onIsLanguageAvailable(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
    }
    
    public boolean isSpeaking()
    {
      if (!TextToSpeechService.-get3(TextToSpeechService.this).isSpeaking()) {
        return TextToSpeechService.-get0(TextToSpeechService.this).isSpeaking();
      }
      return true;
    }
    
    public int loadLanguage(IBinder paramAnonymousIBinder, String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
    {
      if (!checkNonNull(new Object[] { paramAnonymousString1 })) {
        return -1;
      }
      int i = TextToSpeechService.this.onIsLanguageAvailable(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
      if ((i == 0) || (i == 1)) {}
      while (i == 2)
      {
        paramAnonymousIBinder = new TextToSpeechService.LoadLanguageItem(TextToSpeechService.this, paramAnonymousIBinder, Binder.getCallingUid(), Binder.getCallingPid(), paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
        if (TextToSpeechService.-get3(TextToSpeechService.this).enqueueSpeechItem(1, paramAnonymousIBinder) == 0) {
          break;
        }
        return -1;
      }
      return i;
    }
    
    public int loadVoice(IBinder paramAnonymousIBinder, String paramAnonymousString)
    {
      if (!checkNonNull(new Object[] { paramAnonymousString })) {
        return -1;
      }
      int i = TextToSpeechService.this.onIsValidVoiceName(paramAnonymousString);
      if (i == 0)
      {
        paramAnonymousIBinder = new TextToSpeechService.LoadVoiceItem(TextToSpeechService.this, paramAnonymousIBinder, Binder.getCallingUid(), Binder.getCallingPid(), paramAnonymousString);
        if (TextToSpeechService.-get3(TextToSpeechService.this).enqueueSpeechItem(1, paramAnonymousIBinder) != 0) {
          return -1;
        }
      }
      return i;
    }
    
    public int playAudio(IBinder paramAnonymousIBinder, Uri paramAnonymousUri, int paramAnonymousInt, Bundle paramAnonymousBundle, String paramAnonymousString)
    {
      if (!checkNonNull(new Object[] { paramAnonymousIBinder, paramAnonymousUri, paramAnonymousBundle })) {
        return -1;
      }
      paramAnonymousIBinder = new TextToSpeechService.AudioSpeechItemV1(TextToSpeechService.this, paramAnonymousIBinder, Binder.getCallingUid(), Binder.getCallingPid(), paramAnonymousBundle, paramAnonymousString, paramAnonymousUri);
      return TextToSpeechService.-get3(TextToSpeechService.this).enqueueSpeechItem(paramAnonymousInt, paramAnonymousIBinder);
    }
    
    public int playSilence(IBinder paramAnonymousIBinder, long paramAnonymousLong, int paramAnonymousInt, String paramAnonymousString)
    {
      if (!checkNonNull(new Object[] { paramAnonymousIBinder })) {
        return -1;
      }
      paramAnonymousIBinder = new TextToSpeechService.SilenceSpeechItem(TextToSpeechService.this, paramAnonymousIBinder, Binder.getCallingUid(), Binder.getCallingPid(), paramAnonymousString, paramAnonymousLong);
      return TextToSpeechService.-get3(TextToSpeechService.this).enqueueSpeechItem(paramAnonymousInt, paramAnonymousIBinder);
    }
    
    public void setCallback(IBinder paramAnonymousIBinder, ITextToSpeechCallback paramAnonymousITextToSpeechCallback)
    {
      if (!checkNonNull(new Object[] { paramAnonymousIBinder })) {
        return;
      }
      TextToSpeechService.-get1(TextToSpeechService.this).setCallback(paramAnonymousIBinder, paramAnonymousITextToSpeechCallback);
    }
    
    public int speak(IBinder paramAnonymousIBinder, CharSequence paramAnonymousCharSequence, int paramAnonymousInt, Bundle paramAnonymousBundle, String paramAnonymousString)
    {
      if (!checkNonNull(new Object[] { paramAnonymousIBinder, paramAnonymousCharSequence, paramAnonymousBundle })) {
        return -1;
      }
      paramAnonymousIBinder = new TextToSpeechService.SynthesisSpeechItemV1(TextToSpeechService.this, paramAnonymousIBinder, Binder.getCallingUid(), Binder.getCallingPid(), paramAnonymousBundle, paramAnonymousString, paramAnonymousCharSequence);
      return TextToSpeechService.-get3(TextToSpeechService.this).enqueueSpeechItem(paramAnonymousInt, paramAnonymousIBinder);
    }
    
    public int stop(IBinder paramAnonymousIBinder)
    {
      if (!checkNonNull(new Object[] { paramAnonymousIBinder })) {
        return -1;
      }
      return TextToSpeechService.-get3(TextToSpeechService.this).stopForApp(paramAnonymousIBinder);
    }
    
    public int synthesizeToFileDescriptor(IBinder paramAnonymousIBinder, CharSequence paramAnonymousCharSequence, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, Bundle paramAnonymousBundle, String paramAnonymousString)
    {
      if (!checkNonNull(new Object[] { paramAnonymousIBinder, paramAnonymousCharSequence, paramAnonymousParcelFileDescriptor, paramAnonymousBundle })) {
        return -1;
      }
      paramAnonymousParcelFileDescriptor = ParcelFileDescriptor.adoptFd(paramAnonymousParcelFileDescriptor.detachFd());
      paramAnonymousIBinder = new TextToSpeechService.SynthesisToFileOutputStreamSpeechItemV1(TextToSpeechService.this, paramAnonymousIBinder, Binder.getCallingUid(), Binder.getCallingPid(), paramAnonymousBundle, paramAnonymousString, paramAnonymousCharSequence, new ParcelFileDescriptor.AutoCloseOutputStream(paramAnonymousParcelFileDescriptor));
      return TextToSpeechService.-get3(TextToSpeechService.this).enqueueSpeechItem(1, paramAnonymousIBinder);
    }
  };
  private CallbackMap mCallbacks;
  private TtsEngines mEngineHelper;
  private String mPackageName;
  private SynthHandler mSynthHandler;
  private final Object mVoicesInfoLock = new Object();
  
  private int getDefaultSpeechRate()
  {
    return getSecureSettingInt("tts_default_rate", 100);
  }
  
  private int getExpectedLanguageAvailableStatus(Locale paramLocale)
  {
    int i = 2;
    if (paramLocale.getVariant().isEmpty())
    {
      if (paramLocale.getCountry().isEmpty()) {
        i = 0;
      }
    }
    else {
      return i;
    }
    return 1;
  }
  
  private int getSecureSettingInt(String paramString, int paramInt)
  {
    return Settings.Secure.getInt(getContentResolver(), paramString, paramInt);
  }
  
  private String[] getSettingsLocale()
  {
    return TtsEngines.toOldLocaleStringFormat(this.mEngineHelper.getLocalePrefForEngine(this.mPackageName));
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if ("android.intent.action.TTS_SERVICE".equals(paramIntent.getAction())) {
      return this.mBinder;
    }
    return null;
  }
  
  public void onCreate()
  {
    super.onCreate();
    Object localObject = new SynthThread();
    ((SynthThread)localObject).start();
    this.mSynthHandler = new SynthHandler(((SynthThread)localObject).getLooper());
    this.mAudioPlaybackHandler = new AudioPlaybackHandler();
    this.mAudioPlaybackHandler.start();
    this.mEngineHelper = new TtsEngines(this);
    this.mCallbacks = new CallbackMap(null);
    this.mPackageName = getApplicationInfo().packageName;
    localObject = getSettingsLocale();
    onLoadLanguage(localObject[0], localObject[1], localObject[2]);
  }
  
  public void onDestroy()
  {
    this.mSynthHandler.quit();
    this.mAudioPlaybackHandler.quit();
    this.mCallbacks.kill();
    super.onDestroy();
  }
  
  public String onGetDefaultVoiceNameFor(String paramString1, String paramString2, String paramString3)
  {
    switch (onIsLanguageAvailable(paramString1, paramString2, paramString3))
    {
    default: 
      return null;
    case 0: 
      paramString1 = new Locale(paramString1);
    }
    for (;;)
    {
      paramString1 = TtsEngines.normalizeTTSLocale(paramString1).toLanguageTag();
      if (onIsValidVoiceName(paramString1) != 0) {
        break;
      }
      return paramString1;
      paramString1 = new Locale(paramString1, paramString2);
      continue;
      paramString1 = new Locale(paramString1, paramString2, paramString3);
    }
    return null;
  }
  
  protected Set<String> onGetFeaturesForLanguage(String paramString1, String paramString2, String paramString3)
  {
    return new HashSet();
  }
  
  protected abstract String[] onGetLanguage();
  
  public List<Voice> onGetVoices()
  {
    ArrayList localArrayList = new ArrayList();
    Locale[] arrayOfLocale = Locale.getAvailableLocales();
    int j = arrayOfLocale.length;
    int i = 0;
    if (i < j)
    {
      Locale localLocale = arrayOfLocale[i];
      int k = getExpectedLanguageAvailableStatus(localLocale);
      for (;;)
      {
        try
        {
          int m = onIsLanguageAvailable(localLocale.getISO3Language(), localLocale.getISO3Country(), localLocale.getVariant());
          if (m == k) {
            continue;
          }
        }
        catch (MissingResourceException localMissingResourceException)
        {
          Set localSet;
          continue;
        }
        i += 1;
        break;
        localSet = onGetFeaturesForLanguage(localLocale.getISO3Language(), localLocale.getISO3Country(), localLocale.getVariant());
        localArrayList.add(new Voice(onGetDefaultVoiceNameFor(localLocale.getISO3Language(), localLocale.getISO3Country(), localLocale.getVariant()), localLocale, 300, 300, false, localSet));
      }
    }
    return localArrayList;
  }
  
  protected abstract int onIsLanguageAvailable(String paramString1, String paramString2, String paramString3);
  
  public int onIsValidVoiceName(String paramString)
  {
    paramString = Locale.forLanguageTag(paramString);
    if (paramString == null) {
      return -1;
    }
    int i = getExpectedLanguageAvailableStatus(paramString);
    try
    {
      int j = onIsLanguageAvailable(paramString.getISO3Language(), paramString.getISO3Country(), paramString.getVariant());
      if (j != i) {
        return -1;
      }
      return 0;
    }
    catch (MissingResourceException paramString) {}
    return -1;
  }
  
  protected abstract int onLoadLanguage(String paramString1, String paramString2, String paramString3);
  
  public int onLoadVoice(String paramString)
  {
    paramString = Locale.forLanguageTag(paramString);
    if (paramString == null) {
      return -1;
    }
    int i = getExpectedLanguageAvailableStatus(paramString);
    try
    {
      if (onIsLanguageAvailable(paramString.getISO3Language(), paramString.getISO3Country(), paramString.getVariant()) != i) {
        return -1;
      }
      onLoadLanguage(paramString.getISO3Language(), paramString.getISO3Country(), paramString.getVariant());
      return 0;
    }
    catch (MissingResourceException paramString) {}
    return -1;
  }
  
  protected abstract void onStop();
  
  protected abstract void onSynthesizeText(SynthesisRequest paramSynthesisRequest, SynthesisCallback paramSynthesisCallback);
  
  static class AudioOutputParams
  {
    public final AudioAttributes mAudioAttributes;
    public final float mPan;
    public final int mSessionId;
    public final float mVolume;
    
    AudioOutputParams()
    {
      this.mSessionId = 0;
      this.mVolume = 1.0F;
      this.mPan = 0.0F;
      this.mAudioAttributes = null;
    }
    
    AudioOutputParams(int paramInt, float paramFloat1, float paramFloat2, AudioAttributes paramAudioAttributes)
    {
      this.mSessionId = paramInt;
      this.mVolume = paramFloat1;
      this.mPan = paramFloat2;
      this.mAudioAttributes = paramAudioAttributes;
    }
    
    static AudioOutputParams createFromV1ParamsBundle(Bundle paramBundle, boolean paramBoolean)
    {
      if (paramBundle == null) {
        return new AudioOutputParams();
      }
      AudioAttributes localAudioAttributes = (AudioAttributes)paramBundle.getParcelable("audioAttributes");
      Object localObject = localAudioAttributes;
      if (localAudioAttributes == null)
      {
        i = paramBundle.getInt("streamType", 3);
        localObject = new AudioAttributes.Builder().setLegacyStreamType(i);
        if (!paramBoolean) {
          break label96;
        }
      }
      label96:
      for (int i = 1;; i = 4)
      {
        localObject = ((AudioAttributes.Builder)localObject).setContentType(i).build();
        return new AudioOutputParams(paramBundle.getInt("sessionId", 0), paramBundle.getFloat("volume", 1.0F), paramBundle.getFloat("pan", 0.0F), (AudioAttributes)localObject);
      }
    }
  }
  
  private class AudioSpeechItemV1
    extends TextToSpeechService.SpeechItemV1
  {
    private final AudioPlaybackQueueItem mItem = new AudioPlaybackQueueItem(this, getCallerIdentity(), TextToSpeechService.this, paramUri, getAudioParams());
    
    public AudioSpeechItemV1(Object paramObject, int paramInt1, int paramInt2, Bundle paramBundle, String paramString, Uri paramUri)
    {
      super(paramObject, paramInt1, paramInt2, paramBundle, paramString);
    }
    
    TextToSpeechService.AudioOutputParams getAudioParams()
    {
      return TextToSpeechService.AudioOutputParams.createFromV1ParamsBundle(this.mParams, false);
    }
    
    public String getUtteranceId()
    {
      return getStringParam(this.mParams, "utteranceId", null);
    }
    
    public boolean isValid()
    {
      return true;
    }
    
    protected void playImpl()
    {
      TextToSpeechService.-get0(TextToSpeechService.this).enqueue(this.mItem);
    }
    
    protected void stopImpl() {}
  }
  
  private class CallbackMap
    extends RemoteCallbackList<ITextToSpeechCallback>
  {
    private final HashMap<IBinder, ITextToSpeechCallback> mCallerToCallback = new HashMap();
    
    private CallbackMap() {}
    
    private ITextToSpeechCallback getCallbackFor(Object arg1)
    {
      Object localObject1 = (IBinder)???;
      synchronized (this.mCallerToCallback)
      {
        localObject1 = (ITextToSpeechCallback)this.mCallerToCallback.get(localObject1);
        return (ITextToSpeechCallback)localObject1;
      }
    }
    
    public void dispatchOnAudioAvailable(Object paramObject, String paramString, byte[] paramArrayOfByte)
    {
      paramObject = getCallbackFor(paramObject);
      if (paramObject == null) {
        return;
      }
      try
      {
        ((ITextToSpeechCallback)paramObject).onAudioAvailable(paramString, paramArrayOfByte);
        return;
      }
      catch (RemoteException paramObject)
      {
        Log.e("TextToSpeechService", "Callback dispatchOnAudioAvailable(String, byte[]) failed: " + paramObject);
      }
    }
    
    public void dispatchOnBeginSynthesis(Object paramObject, String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      paramObject = getCallbackFor(paramObject);
      if (paramObject == null) {
        return;
      }
      try
      {
        ((ITextToSpeechCallback)paramObject).onBeginSynthesis(paramString, paramInt1, paramInt2, paramInt3);
        return;
      }
      catch (RemoteException paramObject)
      {
        Log.e("TextToSpeechService", "Callback dispatchOnBeginSynthesis(String, int, int, int) failed: " + paramObject);
      }
    }
    
    public void dispatchOnError(Object paramObject, String paramString, int paramInt)
    {
      paramObject = getCallbackFor(paramObject);
      if (paramObject == null) {
        return;
      }
      try
      {
        ((ITextToSpeechCallback)paramObject).onError(paramString, paramInt);
        return;
      }
      catch (RemoteException paramObject)
      {
        Log.e("TextToSpeechService", "Callback onError failed: " + paramObject);
      }
    }
    
    public void dispatchOnStart(Object paramObject, String paramString)
    {
      paramObject = getCallbackFor(paramObject);
      if (paramObject == null) {
        return;
      }
      try
      {
        ((ITextToSpeechCallback)paramObject).onStart(paramString);
        return;
      }
      catch (RemoteException paramObject)
      {
        Log.e("TextToSpeechService", "Callback onStart failed: " + paramObject);
      }
    }
    
    public void dispatchOnStop(Object paramObject, String paramString, boolean paramBoolean)
    {
      paramObject = getCallbackFor(paramObject);
      if (paramObject == null) {
        return;
      }
      try
      {
        ((ITextToSpeechCallback)paramObject).onStop(paramString, paramBoolean);
        return;
      }
      catch (RemoteException paramObject)
      {
        Log.e("TextToSpeechService", "Callback onStop failed: " + paramObject);
      }
    }
    
    public void dispatchOnSuccess(Object paramObject, String paramString)
    {
      paramObject = getCallbackFor(paramObject);
      if (paramObject == null) {
        return;
      }
      try
      {
        ((ITextToSpeechCallback)paramObject).onSuccess(paramString);
        return;
      }
      catch (RemoteException paramObject)
      {
        Log.e("TextToSpeechService", "Callback onDone failed: " + paramObject);
      }
    }
    
    public void kill()
    {
      synchronized (this.mCallerToCallback)
      {
        this.mCallerToCallback.clear();
        super.kill();
        return;
      }
    }
    
    public void onCallbackDied(ITextToSpeechCallback arg1, Object paramObject)
    {
      paramObject = (IBinder)paramObject;
      synchronized (this.mCallerToCallback)
      {
        this.mCallerToCallback.remove(paramObject);
        return;
      }
    }
    
    public void setCallback(IBinder paramIBinder, ITextToSpeechCallback paramITextToSpeechCallback)
    {
      localHashMap = this.mCallerToCallback;
      if (paramITextToSpeechCallback != null) {}
      for (;;)
      {
        try
        {
          register(paramITextToSpeechCallback, paramIBinder);
          paramIBinder = (ITextToSpeechCallback)this.mCallerToCallback.put(paramIBinder, paramITextToSpeechCallback);
          if ((paramIBinder != null) && (paramIBinder != paramITextToSpeechCallback)) {
            unregister(paramIBinder);
          }
          return;
        }
        finally {}
        paramIBinder = (ITextToSpeechCallback)this.mCallerToCallback.remove(paramIBinder);
      }
    }
  }
  
  private class LoadLanguageItem
    extends TextToSpeechService.SpeechItem
  {
    private final String mCountry;
    private final String mLanguage;
    private final String mVariant;
    
    public LoadLanguageItem(Object paramObject, int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3)
    {
      super(paramObject, paramInt1, paramInt2);
      this.mLanguage = paramString1;
      this.mCountry = paramString2;
      this.mVariant = paramString3;
    }
    
    public boolean isValid()
    {
      return true;
    }
    
    protected void playImpl()
    {
      TextToSpeechService.this.onLoadLanguage(this.mLanguage, this.mCountry, this.mVariant);
    }
    
    protected void stopImpl() {}
  }
  
  private class LoadVoiceItem
    extends TextToSpeechService.SpeechItem
  {
    private final String mVoiceName;
    
    public LoadVoiceItem(Object paramObject, int paramInt1, int paramInt2, String paramString)
    {
      super(paramObject, paramInt1, paramInt2);
      this.mVoiceName = paramString;
    }
    
    public boolean isValid()
    {
      return true;
    }
    
    protected void playImpl()
    {
      TextToSpeechService.this.onLoadVoice(this.mVoiceName);
    }
    
    protected void stopImpl() {}
  }
  
  private class SilenceSpeechItem
    extends TextToSpeechService.UtteranceSpeechItem
  {
    private final long mDuration;
    private final String mUtteranceId;
    
    public SilenceSpeechItem(Object paramObject, int paramInt1, int paramInt2, String paramString, long paramLong)
    {
      super(paramObject, paramInt1, paramInt2);
      this.mUtteranceId = paramString;
      this.mDuration = paramLong;
    }
    
    public String getUtteranceId()
    {
      return this.mUtteranceId;
    }
    
    public boolean isValid()
    {
      return true;
    }
    
    protected void playImpl()
    {
      TextToSpeechService.-get0(TextToSpeechService.this).enqueue(new SilencePlaybackQueueItem(this, getCallerIdentity(), this.mDuration));
    }
    
    protected void stopImpl() {}
  }
  
  private abstract class SpeechItem
  {
    private final Object mCallerIdentity;
    private final int mCallerPid;
    private final int mCallerUid;
    private boolean mStarted = false;
    private boolean mStopped = false;
    
    public SpeechItem(Object paramObject, int paramInt1, int paramInt2)
    {
      this.mCallerIdentity = paramObject;
      this.mCallerUid = paramInt1;
      this.mCallerPid = paramInt2;
    }
    
    public Object getCallerIdentity()
    {
      return this.mCallerIdentity;
    }
    
    public int getCallerPid()
    {
      return this.mCallerPid;
    }
    
    public int getCallerUid()
    {
      return this.mCallerUid;
    }
    
    protected boolean isStarted()
    {
      try
      {
        boolean bool = this.mStarted;
        return bool;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    protected boolean isStopped()
    {
      try
      {
        boolean bool = this.mStopped;
        return bool;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    public abstract boolean isValid();
    
    public void play()
    {
      try
      {
        if (this.mStarted) {
          throw new IllegalStateException("play() called twice");
        }
      }
      finally {}
      this.mStarted = true;
      playImpl();
    }
    
    protected abstract void playImpl();
    
    public void stop()
    {
      try
      {
        if (this.mStopped) {
          throw new IllegalStateException("stop() called twice");
        }
      }
      finally {}
      this.mStopped = true;
      stopImpl();
    }
    
    protected abstract void stopImpl();
  }
  
  private abstract class SpeechItemV1
    extends TextToSpeechService.UtteranceSpeechItem
  {
    protected final Bundle mParams;
    protected final String mUtteranceId;
    
    SpeechItemV1(Object paramObject, int paramInt1, int paramInt2, Bundle paramBundle, String paramString)
    {
      super(paramObject, paramInt1, paramInt2);
      this.mParams = paramBundle;
      this.mUtteranceId = paramString;
    }
    
    TextToSpeechService.AudioOutputParams getAudioParams()
    {
      return TextToSpeechService.AudioOutputParams.createFromV1ParamsBundle(this.mParams, true);
    }
    
    int getPitch()
    {
      return getIntParam(this.mParams, "pitch", 100);
    }
    
    int getSpeechRate()
    {
      return getIntParam(this.mParams, "rate", TextToSpeechService.-wrap0(TextToSpeechService.this));
    }
    
    public String getUtteranceId()
    {
      return this.mUtteranceId;
    }
    
    boolean hasLanguage()
    {
      return !TextUtils.isEmpty(getStringParam(this.mParams, "language", null));
    }
  }
  
  private class SynthHandler
    extends Handler
  {
    private TextToSpeechService.SpeechItem mCurrentSpeechItem = null;
    private int mFlushAll = 0;
    private List<Object> mFlushedObjects = new ArrayList();
    
    public SynthHandler(Looper paramLooper)
    {
      super();
    }
    
    private void endFlushingSpeechItems(Object paramObject)
    {
      localList = this.mFlushedObjects;
      if (paramObject == null) {}
      for (;;)
      {
        try
        {
          this.mFlushAll -= 1;
          return;
        }
        finally {}
        this.mFlushedObjects.remove(paramObject);
      }
    }
    
    private TextToSpeechService.SpeechItem getCurrentSpeechItem()
    {
      try
      {
        TextToSpeechService.SpeechItem localSpeechItem = this.mCurrentSpeechItem;
        return localSpeechItem;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    private boolean isFlushed(TextToSpeechService.SpeechItem paramSpeechItem)
    {
      synchronized (this.mFlushedObjects)
      {
        if (this.mFlushAll <= 0)
        {
          bool = this.mFlushedObjects.contains(paramSpeechItem.getCallerIdentity());
          return bool;
        }
        boolean bool = true;
      }
    }
    
    private TextToSpeechService.SpeechItem maybeRemoveCurrentSpeechItem(Object paramObject)
    {
      try
      {
        if ((this.mCurrentSpeechItem != null) && (this.mCurrentSpeechItem.getCallerIdentity() == paramObject))
        {
          paramObject = this.mCurrentSpeechItem;
          this.mCurrentSpeechItem = null;
          return (TextToSpeechService.SpeechItem)paramObject;
        }
        return null;
      }
      finally
      {
        paramObject = finally;
        throw ((Throwable)paramObject);
      }
    }
    
    private TextToSpeechService.SpeechItem setCurrentSpeechItem(TextToSpeechService.SpeechItem paramSpeechItem)
    {
      try
      {
        TextToSpeechService.SpeechItem localSpeechItem = this.mCurrentSpeechItem;
        this.mCurrentSpeechItem = paramSpeechItem;
        return localSpeechItem;
      }
      finally
      {
        paramSpeechItem = finally;
        throw paramSpeechItem;
      }
    }
    
    private void startFlushingSpeechItems(Object paramObject)
    {
      localList = this.mFlushedObjects;
      if (paramObject == null) {}
      for (;;)
      {
        try
        {
          this.mFlushAll += 1;
          return;
        }
        finally {}
        this.mFlushedObjects.add(paramObject);
      }
    }
    
    public int enqueueSpeechItem(int paramInt, final TextToSpeechService.SpeechItem paramSpeechItem)
    {
      TextToSpeechService.UtteranceProgressDispatcher localUtteranceProgressDispatcher = null;
      if ((paramSpeechItem instanceof TextToSpeechService.UtteranceProgressDispatcher)) {
        localUtteranceProgressDispatcher = (TextToSpeechService.UtteranceProgressDispatcher)paramSpeechItem;
      }
      if (!paramSpeechItem.isValid())
      {
        if (localUtteranceProgressDispatcher != null) {
          localUtteranceProgressDispatcher.dispatchOnError(-8);
        }
        return -1;
      }
      if (paramInt == 0) {
        stopForApp(paramSpeechItem.getCallerIdentity());
      }
      for (;;)
      {
        Message localMessage = Message.obtain(this, new Runnable()
        {
          public void run()
          {
            if (TextToSpeechService.SynthHandler.-wrap1(TextToSpeechService.SynthHandler.this, paramSpeechItem))
            {
              paramSpeechItem.stop();
              return;
            }
            TextToSpeechService.SynthHandler.-wrap0(TextToSpeechService.SynthHandler.this, paramSpeechItem);
            paramSpeechItem.play();
            TextToSpeechService.SynthHandler.-wrap0(TextToSpeechService.SynthHandler.this, null);
          }
        });
        localMessage.obj = paramSpeechItem.getCallerIdentity();
        if (!sendMessage(localMessage)) {
          break;
        }
        return 0;
        if (paramInt == 2) {
          stopAll();
        }
      }
      Log.w("TextToSpeechService", "SynthThread has quit");
      if (localUtteranceProgressDispatcher != null) {
        localUtteranceProgressDispatcher.dispatchOnError(-4);
      }
      return -1;
    }
    
    public boolean isSpeaking()
    {
      return getCurrentSpeechItem() != null;
    }
    
    public void quit()
    {
      getLooper().quit();
      TextToSpeechService.SpeechItem localSpeechItem = setCurrentSpeechItem(null);
      if (localSpeechItem != null) {
        localSpeechItem.stop();
      }
    }
    
    public int stopAll()
    {
      startFlushingSpeechItems(null);
      TextToSpeechService.SpeechItem localSpeechItem = setCurrentSpeechItem(null);
      if (localSpeechItem != null) {
        localSpeechItem.stop();
      }
      TextToSpeechService.-get0(TextToSpeechService.this).stop();
      sendMessage(Message.obtain(this, new Runnable()
      {
        public void run()
        {
          TextToSpeechService.SynthHandler.-wrap2(TextToSpeechService.SynthHandler.this, null);
        }
      }));
      return 0;
    }
    
    public int stopForApp(final Object paramObject)
    {
      if (paramObject == null) {
        return -1;
      }
      startFlushingSpeechItems(paramObject);
      TextToSpeechService.SpeechItem localSpeechItem = maybeRemoveCurrentSpeechItem(paramObject);
      if (localSpeechItem != null) {
        localSpeechItem.stop();
      }
      TextToSpeechService.-get0(TextToSpeechService.this).stopForApp(paramObject);
      sendMessage(Message.obtain(this, new Runnable()
      {
        public void run()
        {
          TextToSpeechService.SynthHandler.-wrap2(TextToSpeechService.SynthHandler.this, paramObject);
        }
      }));
      return 0;
    }
  }
  
  private class SynthThread
    extends HandlerThread
    implements MessageQueue.IdleHandler
  {
    private boolean mFirstIdle = true;
    
    public SynthThread()
    {
      super(0);
    }
    
    private void broadcastTtsQueueProcessingCompleted()
    {
      Intent localIntent = new Intent("android.speech.tts.TTS_QUEUE_PROCESSING_COMPLETED");
      TextToSpeechService.this.sendBroadcast(localIntent);
    }
    
    protected void onLooperPrepared()
    {
      getLooper().getQueue().addIdleHandler(this);
    }
    
    public boolean queueIdle()
    {
      if (this.mFirstIdle) {
        this.mFirstIdle = false;
      }
      for (;;)
      {
        return true;
        broadcastTtsQueueProcessingCompleted();
      }
    }
  }
  
  class SynthesisSpeechItemV1
    extends TextToSpeechService.SpeechItemV1
  {
    private final int mCallerUid;
    private final String[] mDefaultLocale;
    private final EventLoggerV1 mEventLogger;
    private AbstractSynthesisCallback mSynthesisCallback;
    private final SynthesisRequest mSynthesisRequest;
    private final CharSequence mText;
    
    public SynthesisSpeechItemV1(Object paramObject, int paramInt1, int paramInt2, Bundle paramBundle, String paramString, CharSequence paramCharSequence)
    {
      super(paramObject, paramInt1, paramInt2, paramBundle, paramString);
      this.mText = paramCharSequence;
      this.mCallerUid = paramInt1;
      this.mSynthesisRequest = new SynthesisRequest(this.mText, this.mParams);
      this.mDefaultLocale = TextToSpeechService.-wrap1(TextToSpeechService.this);
      setRequestParams(this.mSynthesisRequest);
      this.mEventLogger = new EventLoggerV1(this.mSynthesisRequest, paramInt1, paramInt2, TextToSpeechService.-get2(TextToSpeechService.this));
    }
    
    private String getCountry()
    {
      if (!hasLanguage()) {
        return this.mDefaultLocale[1];
      }
      return getStringParam(this.mParams, "country", "");
    }
    
    private String getVariant()
    {
      if (!hasLanguage()) {
        return this.mDefaultLocale[2];
      }
      return getStringParam(this.mParams, "variant", "");
    }
    
    private void setRequestParams(SynthesisRequest paramSynthesisRequest)
    {
      String str = getVoiceName();
      paramSynthesisRequest.setLanguage(getLanguage(), getCountry(), getVariant());
      if (!TextUtils.isEmpty(str)) {
        paramSynthesisRequest.setVoiceName(getVoiceName());
      }
      paramSynthesisRequest.setSpeechRate(getSpeechRate());
      paramSynthesisRequest.setCallerUid(this.mCallerUid);
      paramSynthesisRequest.setPitch(getPitch());
    }
    
    protected AbstractSynthesisCallback createSynthesisCallback()
    {
      return new PlaybackSynthesisCallback(getAudioParams(), TextToSpeechService.-get0(TextToSpeechService.this), this, getCallerIdentity(), this.mEventLogger, false);
    }
    
    public String getLanguage()
    {
      return getStringParam(this.mParams, "language", this.mDefaultLocale[0]);
    }
    
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public String getVoiceName()
    {
      return getStringParam(this.mParams, "voiceName", "");
    }
    
    public boolean isValid()
    {
      if (this.mText == null)
      {
        Log.e("TextToSpeechService", "null synthesis text");
        return false;
      }
      if (this.mText.length() >= TextToSpeech.getMaxSpeechInputLength())
      {
        Log.w("TextToSpeechService", "Text too long: " + this.mText.length() + " chars");
        return false;
      }
      return true;
    }
    
    protected void playImpl()
    {
      this.mEventLogger.onRequestProcessingStart();
      try
      {
        boolean bool = isStopped();
        if (bool) {
          return;
        }
        this.mSynthesisCallback = createSynthesisCallback();
        AbstractSynthesisCallback localAbstractSynthesisCallback = this.mSynthesisCallback;
        TextToSpeechService.this.onSynthesizeText(this.mSynthesisRequest, localAbstractSynthesisCallback);
        if ((!localAbstractSynthesisCallback.hasStarted()) || (localAbstractSynthesisCallback.hasFinished())) {
          return;
        }
      }
      finally {}
      ((AbstractSynthesisCallback)localObject).done();
    }
    
    protected void stopImpl()
    {
      try
      {
        AbstractSynthesisCallback localAbstractSynthesisCallback = this.mSynthesisCallback;
        if (localAbstractSynthesisCallback != null)
        {
          localAbstractSynthesisCallback.stop();
          TextToSpeechService.this.onStop();
          return;
        }
      }
      finally {}
      dispatchOnStop();
    }
  }
  
  private class SynthesisToFileOutputStreamSpeechItemV1
    extends TextToSpeechService.SynthesisSpeechItemV1
  {
    private final FileOutputStream mFileOutputStream;
    
    public SynthesisToFileOutputStreamSpeechItemV1(Object paramObject, int paramInt1, int paramInt2, Bundle paramBundle, String paramString, CharSequence paramCharSequence, FileOutputStream paramFileOutputStream)
    {
      super(paramObject, paramInt1, paramInt2, paramBundle, paramString, paramCharSequence);
      this.mFileOutputStream = paramFileOutputStream;
    }
    
    protected AbstractSynthesisCallback createSynthesisCallback()
    {
      return new FileSynthesisCallback(this.mFileOutputStream.getChannel(), this, false);
    }
    
    protected void playImpl()
    {
      dispatchOnStart();
      super.playImpl();
      try
      {
        this.mFileOutputStream.close();
        return;
      }
      catch (IOException localIOException)
      {
        Log.w("TextToSpeechService", "Failed to close output file", localIOException);
      }
    }
  }
  
  static abstract interface UtteranceProgressDispatcher
  {
    public abstract void dispatchOnAudioAvailable(byte[] paramArrayOfByte);
    
    public abstract void dispatchOnBeginSynthesis(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void dispatchOnError(int paramInt);
    
    public abstract void dispatchOnStart();
    
    public abstract void dispatchOnStop();
    
    public abstract void dispatchOnSuccess();
  }
  
  private abstract class UtteranceSpeechItem
    extends TextToSpeechService.SpeechItem
    implements TextToSpeechService.UtteranceProgressDispatcher
  {
    public UtteranceSpeechItem(Object paramObject, int paramInt1, int paramInt2)
    {
      super(paramObject, paramInt1, paramInt2);
    }
    
    public void dispatchOnAudioAvailable(byte[] paramArrayOfByte)
    {
      String str = getUtteranceId();
      if (str != null) {
        TextToSpeechService.-get1(TextToSpeechService.this).dispatchOnAudioAvailable(getCallerIdentity(), str, paramArrayOfByte);
      }
    }
    
    public void dispatchOnBeginSynthesis(int paramInt1, int paramInt2, int paramInt3)
    {
      String str = getUtteranceId();
      if (str != null) {
        TextToSpeechService.-get1(TextToSpeechService.this).dispatchOnBeginSynthesis(getCallerIdentity(), str, paramInt1, paramInt2, paramInt3);
      }
    }
    
    public void dispatchOnError(int paramInt)
    {
      String str = getUtteranceId();
      if (str != null) {
        TextToSpeechService.-get1(TextToSpeechService.this).dispatchOnError(getCallerIdentity(), str, paramInt);
      }
    }
    
    public void dispatchOnStart()
    {
      String str = getUtteranceId();
      if (str != null) {
        TextToSpeechService.-get1(TextToSpeechService.this).dispatchOnStart(getCallerIdentity(), str);
      }
    }
    
    public void dispatchOnStop()
    {
      String str = getUtteranceId();
      if (str != null) {
        TextToSpeechService.-get1(TextToSpeechService.this).dispatchOnStop(getCallerIdentity(), str, isStarted());
      }
    }
    
    public void dispatchOnSuccess()
    {
      String str = getUtteranceId();
      if (str != null) {
        TextToSpeechService.-get1(TextToSpeechService.this).dispatchOnSuccess(getCallerIdentity(), str);
      }
    }
    
    float getFloatParam(Bundle paramBundle, String paramString, float paramFloat)
    {
      if (paramBundle == null) {
        return paramFloat;
      }
      return paramBundle.getFloat(paramString, paramFloat);
    }
    
    int getIntParam(Bundle paramBundle, String paramString, int paramInt)
    {
      if (paramBundle == null) {
        return paramInt;
      }
      return paramBundle.getInt(paramString, paramInt);
    }
    
    String getStringParam(Bundle paramBundle, String paramString1, String paramString2)
    {
      if (paramBundle == null) {
        return paramString2;
      }
      return paramBundle.getString(paramString1, paramString2);
    }
    
    public abstract String getUtteranceId();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/TextToSpeechService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */