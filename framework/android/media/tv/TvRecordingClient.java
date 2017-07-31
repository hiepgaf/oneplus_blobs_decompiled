package android.media.tv;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public class TvRecordingClient
{
  private static final boolean DEBUG = false;
  private static final String TAG = "TvRecordingClient";
  private final RecordingCallback mCallback;
  private final Handler mHandler;
  private boolean mIsRecordingStarted;
  private boolean mIsTuned;
  private final Queue<Pair<String, Bundle>> mPendingAppPrivateCommands = new ArrayDeque();
  private TvInputManager.Session mSession;
  private MySessionCallback mSessionCallback;
  private final TvInputManager mTvInputManager;
  
  public TvRecordingClient(Context paramContext, String paramString, RecordingCallback paramRecordingCallback, Handler paramHandler)
  {
    this.mCallback = paramRecordingCallback;
    paramString = paramHandler;
    if (paramHandler == null) {
      paramString = new Handler(Looper.getMainLooper());
    }
    this.mHandler = paramString;
    this.mTvInputManager = ((TvInputManager)paramContext.getSystemService("tv_input"));
  }
  
  private void resetInternal()
  {
    this.mSessionCallback = null;
    this.mPendingAppPrivateCommands.clear();
    if (this.mSession != null)
    {
      this.mSession.release();
      this.mSession = null;
    }
  }
  
  public void release()
  {
    resetInternal();
  }
  
  public void sendAppPrivateCommand(String paramString, Bundle paramBundle)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("action cannot be null or an empty string");
    }
    if (this.mSession != null)
    {
      this.mSession.sendAppPrivateCommand(paramString, paramBundle);
      return;
    }
    Log.w("TvRecordingClient", "sendAppPrivateCommand - session not yet created (action \"" + paramString + "\" pending)");
    this.mPendingAppPrivateCommands.add(Pair.create(paramString, paramBundle));
  }
  
  public void startRecording(Uri paramUri)
  {
    if (!this.mIsTuned) {
      throw new IllegalStateException("startRecording failed - not yet tuned");
    }
    if (this.mSession != null)
    {
      this.mSession.startRecording(paramUri);
      this.mIsRecordingStarted = true;
    }
  }
  
  public void stopRecording()
  {
    if (!this.mIsRecordingStarted) {
      Log.w("TvRecordingClient", "stopRecording failed - recording not yet started");
    }
    if (this.mSession != null) {
      this.mSession.stopRecording();
    }
  }
  
  public void tune(String paramString, Uri paramUri)
  {
    tune(paramString, paramUri, null);
  }
  
  public void tune(String paramString, Uri paramUri, Bundle paramBundle)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("inputId cannot be null or an empty string");
    }
    if (this.mIsRecordingStarted) {
      throw new IllegalStateException("tune failed - recording already started");
    }
    if ((this.mSessionCallback != null) && (TextUtils.equals(this.mSessionCallback.mInputId, paramString))) {
      if (this.mSession != null) {
        this.mSession.tune(paramUri, paramBundle);
      }
    }
    do
    {
      return;
      this.mSessionCallback.mChannelUri = paramUri;
      this.mSessionCallback.mConnectionParams = paramBundle;
      return;
      resetInternal();
      this.mSessionCallback = new MySessionCallback(paramString, paramUri, paramBundle);
    } while (this.mTvInputManager == null);
    this.mTvInputManager.createRecordingSession(paramString, this.mSessionCallback, this.mHandler);
  }
  
  private class MySessionCallback
    extends TvInputManager.SessionCallback
  {
    Uri mChannelUri;
    Bundle mConnectionParams;
    final String mInputId;
    
    MySessionCallback(String paramString, Uri paramUri, Bundle paramBundle)
    {
      this.mInputId = paramString;
      this.mChannelUri = paramUri;
      this.mConnectionParams = paramBundle;
    }
    
    public void onError(TvInputManager.Session paramSession, int paramInt)
    {
      if (this != TvRecordingClient.-get3(TvRecordingClient.this))
      {
        Log.w("TvRecordingClient", "onError - session not created");
        return;
      }
      TvRecordingClient.-get0(TvRecordingClient.this).onError(paramInt);
    }
    
    public void onRecordingStopped(TvInputManager.Session paramSession, Uri paramUri)
    {
      if (this != TvRecordingClient.-get3(TvRecordingClient.this))
      {
        Log.w("TvRecordingClient", "onRecordingStopped - session not created");
        return;
      }
      TvRecordingClient.-set0(TvRecordingClient.this, false);
      TvRecordingClient.-get0(TvRecordingClient.this).onRecordingStopped(paramUri);
    }
    
    public void onSessionCreated(TvInputManager.Session paramSession)
    {
      if (this != TvRecordingClient.-get3(TvRecordingClient.this))
      {
        Log.w("TvRecordingClient", "onSessionCreated - session already created");
        if (paramSession != null) {
          paramSession.release();
        }
        return;
      }
      TvRecordingClient.-set2(TvRecordingClient.this, paramSession);
      if (paramSession != null)
      {
        paramSession = TvRecordingClient.-get1(TvRecordingClient.this).iterator();
        while (paramSession.hasNext())
        {
          Pair localPair = (Pair)paramSession.next();
          TvRecordingClient.-get2(TvRecordingClient.this).sendAppPrivateCommand((String)localPair.first, (Bundle)localPair.second);
        }
        TvRecordingClient.-get1(TvRecordingClient.this).clear();
        TvRecordingClient.-get2(TvRecordingClient.this).tune(this.mChannelUri, this.mConnectionParams);
      }
      do
      {
        return;
        TvRecordingClient.-set3(TvRecordingClient.this, null);
      } while (TvRecordingClient.-get0(TvRecordingClient.this) == null);
      TvRecordingClient.-get0(TvRecordingClient.this).onConnectionFailed(this.mInputId);
    }
    
    public void onSessionEvent(TvInputManager.Session paramSession, String paramString, Bundle paramBundle)
    {
      if (this != TvRecordingClient.-get3(TvRecordingClient.this))
      {
        Log.w("TvRecordingClient", "onSessionEvent - session not created");
        return;
      }
      if (TvRecordingClient.-get0(TvRecordingClient.this) != null) {
        TvRecordingClient.-get0(TvRecordingClient.this).onEvent(this.mInputId, paramString, paramBundle);
      }
    }
    
    public void onSessionReleased(TvInputManager.Session paramSession)
    {
      if (this != TvRecordingClient.-get3(TvRecordingClient.this))
      {
        Log.w("TvRecordingClient", "onSessionReleased - session not created");
        return;
      }
      TvRecordingClient.-set1(TvRecordingClient.this, false);
      TvRecordingClient.-set0(TvRecordingClient.this, false);
      TvRecordingClient.-set3(TvRecordingClient.this, null);
      TvRecordingClient.-set2(TvRecordingClient.this, null);
      if (TvRecordingClient.-get0(TvRecordingClient.this) != null) {
        TvRecordingClient.-get0(TvRecordingClient.this).onDisconnected(this.mInputId);
      }
    }
    
    void onTuned(TvInputManager.Session paramSession, Uri paramUri)
    {
      if (this != TvRecordingClient.-get3(TvRecordingClient.this))
      {
        Log.w("TvRecordingClient", "onTuned - session not created");
        return;
      }
      TvRecordingClient.-set1(TvRecordingClient.this, true);
      TvRecordingClient.-get0(TvRecordingClient.this).onTuned(paramUri);
    }
  }
  
  public static abstract class RecordingCallback
  {
    public void onConnectionFailed(String paramString) {}
    
    public void onDisconnected(String paramString) {}
    
    public void onError(int paramInt) {}
    
    public void onEvent(String paramString1, String paramString2, Bundle paramBundle) {}
    
    public void onRecordingStopped(Uri paramUri) {}
    
    public void onTuned(Uri paramUri) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvRecordingClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */