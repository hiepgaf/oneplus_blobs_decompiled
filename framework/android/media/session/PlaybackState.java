package android.media.session;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public final class PlaybackState
  implements Parcelable
{
  public static final long ACTION_FAST_FORWARD = 64L;
  public static final long ACTION_PAUSE = 2L;
  public static final long ACTION_PLAY = 4L;
  public static final long ACTION_PLAY_FROM_MEDIA_ID = 1024L;
  public static final long ACTION_PLAY_FROM_SEARCH = 2048L;
  public static final long ACTION_PLAY_FROM_URI = 8192L;
  public static final long ACTION_PLAY_PAUSE = 512L;
  public static final long ACTION_PREPARE = 16384L;
  public static final long ACTION_PREPARE_FROM_MEDIA_ID = 32768L;
  public static final long ACTION_PREPARE_FROM_SEARCH = 65536L;
  public static final long ACTION_PREPARE_FROM_URI = 131072L;
  public static final long ACTION_REWIND = 8L;
  public static final long ACTION_SEEK_TO = 256L;
  public static final long ACTION_SET_RATING = 128L;
  public static final long ACTION_SKIP_TO_NEXT = 32L;
  public static final long ACTION_SKIP_TO_PREVIOUS = 16L;
  public static final long ACTION_SKIP_TO_QUEUE_ITEM = 4096L;
  public static final long ACTION_STOP = 1L;
  public static final Parcelable.Creator<PlaybackState> CREATOR = new Parcelable.Creator()
  {
    public PlaybackState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PlaybackState(paramAnonymousParcel, null);
    }
    
    public PlaybackState[] newArray(int paramAnonymousInt)
    {
      return new PlaybackState[paramAnonymousInt];
    }
  };
  public static final long PLAYBACK_POSITION_UNKNOWN = -1L;
  public static final int STATE_BUFFERING = 6;
  public static final int STATE_CONNECTING = 8;
  public static final int STATE_ERROR = 7;
  public static final int STATE_FAST_FORWARDING = 4;
  public static final int STATE_NONE = 0;
  public static final int STATE_PAUSED = 2;
  public static final int STATE_PLAYING = 3;
  public static final int STATE_REWINDING = 5;
  public static final int STATE_SKIPPING_TO_NEXT = 10;
  public static final int STATE_SKIPPING_TO_PREVIOUS = 9;
  public static final int STATE_SKIPPING_TO_QUEUE_ITEM = 11;
  public static final int STATE_STOPPED = 1;
  private static final String TAG = "PlaybackState";
  private final long mActions;
  private final long mActiveItemId;
  private final long mBufferedPosition;
  private List<CustomAction> mCustomActions;
  private final CharSequence mErrorMessage;
  private final Bundle mExtras;
  private final long mPosition;
  private final float mSpeed;
  private final int mState;
  private final long mUpdateTime;
  
  private PlaybackState(int paramInt, long paramLong1, long paramLong2, float paramFloat, long paramLong3, long paramLong4, List<CustomAction> paramList, long paramLong5, CharSequence paramCharSequence, Bundle paramBundle)
  {
    this.mState = paramInt;
    this.mPosition = paramLong1;
    this.mSpeed = paramFloat;
    this.mUpdateTime = paramLong2;
    this.mBufferedPosition = paramLong3;
    this.mActions = paramLong4;
    this.mCustomActions = new ArrayList(paramList);
    this.mActiveItemId = paramLong5;
    this.mErrorMessage = paramCharSequence;
    this.mExtras = paramBundle;
  }
  
  private PlaybackState(Parcel paramParcel)
  {
    this.mState = paramParcel.readInt();
    this.mPosition = paramParcel.readLong();
    this.mSpeed = paramParcel.readFloat();
    this.mUpdateTime = paramParcel.readLong();
    this.mBufferedPosition = paramParcel.readLong();
    this.mActions = paramParcel.readLong();
    this.mCustomActions = paramParcel.createTypedArrayList(CustomAction.CREATOR);
    this.mActiveItemId = paramParcel.readLong();
    this.mErrorMessage = paramParcel.readCharSequence();
    this.mExtras = paramParcel.readBundle();
  }
  
  private static long getActionForRccFlag(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0L;
    case 1: 
      return 16L;
    case 2: 
      return 8L;
    case 4: 
      return 4L;
    case 8: 
      return 512L;
    case 16: 
      return 2L;
    case 32: 
      return 1L;
    case 64: 
      return 64L;
    case 128: 
      return 32L;
    case 256: 
      return 256L;
    }
    return 128L;
  }
  
  public static long getActionsFromRccControlFlags(int paramInt)
  {
    long l2 = 0L;
    long l1 = 1L;
    while (l1 <= paramInt)
    {
      long l3 = l2;
      if ((paramInt & l1) != 0L) {
        l3 = l2 | getActionForRccFlag((int)l1);
      }
      l1 <<= 1;
      l2 = l3;
    }
    return l2;
  }
  
  public static int getRccControlFlagsFromActions(long paramLong)
  {
    int i = 0;
    long l = 1L;
    while ((l <= paramLong) && (l < 2147483647L))
    {
      int j = i;
      if ((l & paramLong) != 0L) {
        j = i | getRccFlagForAction(l);
      }
      l <<= 1;
      i = j;
    }
    return i;
  }
  
  private static int getRccFlagForAction(long paramLong)
  {
    if (paramLong < 2147483647L) {}
    for (int i = (int)paramLong;; i = 0) {
      switch (i)
      {
      default: 
        return 0;
      }
    }
    return 1;
    return 2;
    return 4;
    return 8;
    return 16;
    return 32;
    return 64;
    return 128;
    return 256;
    return 512;
  }
  
  public static int getRccStateFromState(int paramInt)
  {
    switch (paramInt)
    {
    case 8: 
    default: 
      return -1;
    case 6: 
      return 8;
    case 7: 
      return 9;
    case 4: 
      return 4;
    case 0: 
      return 0;
    case 2: 
      return 2;
    case 3: 
      return 3;
    case 5: 
      return 5;
    case 9: 
      return 7;
    case 10: 
      return 6;
    }
    return 1;
  }
  
  public static int getStateFromRccState(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return -1;
    case 8: 
      return 6;
    case 9: 
      return 7;
    case 4: 
      return 4;
    case 0: 
      return 0;
    case 2: 
      return 2;
    case 3: 
      return 3;
    case 5: 
      return 5;
    case 7: 
      return 9;
    case 6: 
      return 10;
    }
    return 1;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getActions()
  {
    return this.mActions;
  }
  
  public long getActiveQueueItemId()
  {
    return this.mActiveItemId;
  }
  
  public long getBufferedPosition()
  {
    return this.mBufferedPosition;
  }
  
  public List<CustomAction> getCustomActions()
  {
    return this.mCustomActions;
  }
  
  public CharSequence getErrorMessage()
  {
    return this.mErrorMessage;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public long getLastPositionUpdateTime()
  {
    return this.mUpdateTime;
  }
  
  public float getPlaybackSpeed()
  {
    return this.mSpeed;
  }
  
  public long getPosition()
  {
    return this.mPosition;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("PlaybackState {");
    localStringBuilder.append("state=").append(this.mState);
    localStringBuilder.append(", position=").append(this.mPosition);
    localStringBuilder.append(", buffered position=").append(this.mBufferedPosition);
    localStringBuilder.append(", speed=").append(this.mSpeed);
    localStringBuilder.append(", updated=").append(this.mUpdateTime);
    localStringBuilder.append(", actions=").append(this.mActions);
    localStringBuilder.append(", custom actions=").append(this.mCustomActions);
    localStringBuilder.append(", active item id=").append(this.mActiveItemId);
    localStringBuilder.append(", error=").append(this.mErrorMessage);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mState);
    paramParcel.writeLong(this.mPosition);
    paramParcel.writeFloat(this.mSpeed);
    paramParcel.writeLong(this.mUpdateTime);
    paramParcel.writeLong(this.mBufferedPosition);
    paramParcel.writeLong(this.mActions);
    paramParcel.writeTypedList(this.mCustomActions);
    paramParcel.writeLong(this.mActiveItemId);
    paramParcel.writeCharSequence(this.mErrorMessage);
    paramParcel.writeBundle(this.mExtras);
  }
  
  public static final class Builder
  {
    private long mActions;
    private long mActiveItemId = -1L;
    private long mBufferedPosition;
    private final List<PlaybackState.CustomAction> mCustomActions = new ArrayList();
    private CharSequence mErrorMessage;
    private Bundle mExtras;
    private long mPosition;
    private float mSpeed;
    private int mState;
    private long mUpdateTime;
    
    public Builder() {}
    
    public Builder(PlaybackState paramPlaybackState)
    {
      if (paramPlaybackState == null) {
        return;
      }
      this.mState = PlaybackState.-get8(paramPlaybackState);
      this.mPosition = PlaybackState.-get6(paramPlaybackState);
      this.mBufferedPosition = PlaybackState.-get2(paramPlaybackState);
      this.mSpeed = PlaybackState.-get7(paramPlaybackState);
      this.mActions = PlaybackState.-get0(paramPlaybackState);
      if (PlaybackState.-get3(paramPlaybackState) != null) {
        this.mCustomActions.addAll(PlaybackState.-get3(paramPlaybackState));
      }
      this.mErrorMessage = PlaybackState.-get4(paramPlaybackState);
      this.mUpdateTime = PlaybackState.-get9(paramPlaybackState);
      this.mActiveItemId = PlaybackState.-get1(paramPlaybackState);
      this.mExtras = PlaybackState.-get5(paramPlaybackState);
    }
    
    public Builder addCustomAction(PlaybackState.CustomAction paramCustomAction)
    {
      if (paramCustomAction == null) {
        throw new IllegalArgumentException("You may not add a null CustomAction to PlaybackState.");
      }
      this.mCustomActions.add(paramCustomAction);
      return this;
    }
    
    public Builder addCustomAction(String paramString1, String paramString2, int paramInt)
    {
      return addCustomAction(new PlaybackState.CustomAction(paramString1, paramString2, paramInt, null, null));
    }
    
    public PlaybackState build()
    {
      return new PlaybackState(this.mState, this.mPosition, this.mUpdateTime, this.mSpeed, this.mBufferedPosition, this.mActions, this.mCustomActions, this.mActiveItemId, this.mErrorMessage, this.mExtras, null);
    }
    
    public Builder setActions(long paramLong)
    {
      this.mActions = paramLong;
      return this;
    }
    
    public Builder setActiveQueueItemId(long paramLong)
    {
      this.mActiveItemId = paramLong;
      return this;
    }
    
    public Builder setBufferedPosition(long paramLong)
    {
      this.mBufferedPosition = paramLong;
      return this;
    }
    
    public Builder setErrorMessage(CharSequence paramCharSequence)
    {
      this.mErrorMessage = paramCharSequence;
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      this.mExtras = paramBundle;
      return this;
    }
    
    public Builder setState(int paramInt, long paramLong, float paramFloat)
    {
      return setState(paramInt, paramLong, paramFloat, SystemClock.elapsedRealtime());
    }
    
    public Builder setState(int paramInt, long paramLong1, float paramFloat, long paramLong2)
    {
      this.mState = paramInt;
      this.mPosition = paramLong1;
      this.mUpdateTime = paramLong2;
      this.mSpeed = paramFloat;
      return this;
    }
  }
  
  public static final class CustomAction
    implements Parcelable
  {
    public static final Parcelable.Creator<CustomAction> CREATOR = new Parcelable.Creator()
    {
      public PlaybackState.CustomAction createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PlaybackState.CustomAction(paramAnonymousParcel, null);
      }
      
      public PlaybackState.CustomAction[] newArray(int paramAnonymousInt)
      {
        return new PlaybackState.CustomAction[paramAnonymousInt];
      }
    };
    private final String mAction;
    private final Bundle mExtras;
    private final int mIcon;
    private final CharSequence mName;
    
    private CustomAction(Parcel paramParcel)
    {
      this.mAction = paramParcel.readString();
      this.mName = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.mIcon = paramParcel.readInt();
      this.mExtras = paramParcel.readBundle();
    }
    
    private CustomAction(String paramString, CharSequence paramCharSequence, int paramInt, Bundle paramBundle)
    {
      this.mAction = paramString;
      this.mName = paramCharSequence;
      this.mIcon = paramInt;
      this.mExtras = paramBundle;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String getAction()
    {
      return this.mAction;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public int getIcon()
    {
      return this.mIcon;
    }
    
    public CharSequence getName()
    {
      return this.mName;
    }
    
    public String toString()
    {
      return "Action:mName='" + this.mName + ", mIcon=" + this.mIcon + ", mExtras=" + this.mExtras;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.mAction);
      TextUtils.writeToParcel(this.mName, paramParcel, paramInt);
      paramParcel.writeInt(this.mIcon);
      paramParcel.writeBundle(this.mExtras);
    }
    
    public static final class Builder
    {
      private final String mAction;
      private Bundle mExtras;
      private final int mIcon;
      private final CharSequence mName;
      
      public Builder(String paramString, CharSequence paramCharSequence, int paramInt)
      {
        if (TextUtils.isEmpty(paramString)) {
          throw new IllegalArgumentException("You must specify an action to build a CustomAction.");
        }
        if (TextUtils.isEmpty(paramCharSequence)) {
          throw new IllegalArgumentException("You must specify a name to build a CustomAction.");
        }
        if (paramInt == 0) {
          throw new IllegalArgumentException("You must specify an icon resource id to build a CustomAction.");
        }
        this.mAction = paramString;
        this.mName = paramCharSequence;
        this.mIcon = paramInt;
      }
      
      public PlaybackState.CustomAction build()
      {
        return new PlaybackState.CustomAction(this.mAction, this.mName, this.mIcon, this.mExtras, null);
      }
      
      public Builder setExtras(Bundle paramBundle)
      {
        this.mExtras = paramBundle;
        return this;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/session/PlaybackState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */