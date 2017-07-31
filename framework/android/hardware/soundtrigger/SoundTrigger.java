package android.hardware.soundtrigger;

import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.system.OsConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class SoundTrigger
{
  public static final int RECOGNITION_MODE_USER_AUTHENTICATION = 4;
  public static final int RECOGNITION_MODE_USER_IDENTIFICATION = 2;
  public static final int RECOGNITION_MODE_VOICE_TRIGGER = 1;
  public static final int RECOGNITION_STATUS_ABORT = 1;
  public static final int RECOGNITION_STATUS_FAILURE = 2;
  public static final int RECOGNITION_STATUS_SUCCESS = 0;
  public static final int SERVICE_STATE_DISABLED = 1;
  public static final int SERVICE_STATE_ENABLED = 0;
  public static final int SOUNDMODEL_STATUS_UPDATED = 0;
  public static final int STATUS_BAD_VALUE = -OsConstants.EINVAL;
  public static final int STATUS_DEAD_OBJECT = -OsConstants.EPIPE;
  public static final int STATUS_ERROR = Integer.MIN_VALUE;
  public static final int STATUS_INVALID_OPERATION = -OsConstants.ENOSYS;
  public static final int STATUS_NO_INIT;
  public static final int STATUS_OK = 0;
  public static final int STATUS_PERMISSION_DENIED = -OsConstants.EPERM;
  
  static
  {
    STATUS_NO_INIT = -OsConstants.ENODEV;
  }
  
  public static SoundTriggerModule attachModule(int paramInt, StatusListener paramStatusListener, Handler paramHandler)
  {
    if (paramStatusListener == null) {
      return null;
    }
    return new SoundTriggerModule(paramInt, paramStatusListener, paramHandler);
  }
  
  public static native int listModules(ArrayList<ModuleProperties> paramArrayList);
  
  public static class ConfidenceLevel
    implements Parcelable
  {
    public static final Parcelable.Creator<ConfidenceLevel> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.ConfidenceLevel createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.ConfidenceLevel.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.ConfidenceLevel[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.ConfidenceLevel[paramAnonymousInt];
      }
    };
    public final int confidenceLevel;
    public final int userId;
    
    public ConfidenceLevel(int paramInt1, int paramInt2)
    {
      this.userId = paramInt1;
      this.confidenceLevel = paramInt2;
    }
    
    private static ConfidenceLevel fromParcel(Parcel paramParcel)
    {
      return new ConfidenceLevel(paramParcel.readInt(), paramParcel.readInt());
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (ConfidenceLevel)paramObject;
      if (this.confidenceLevel != ((ConfidenceLevel)paramObject).confidenceLevel) {
        return false;
      }
      return this.userId == ((ConfidenceLevel)paramObject).userId;
    }
    
    public int hashCode()
    {
      return (this.confidenceLevel + 31) * 31 + this.userId;
    }
    
    public String toString()
    {
      return "ConfidenceLevel [userId=" + this.userId + ", confidenceLevel=" + this.confidenceLevel + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.userId);
      paramParcel.writeInt(this.confidenceLevel);
    }
  }
  
  public static class GenericRecognitionEvent
    extends SoundTrigger.RecognitionEvent
  {
    public static final Parcelable.Creator<GenericRecognitionEvent> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.GenericRecognitionEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.GenericRecognitionEvent.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.GenericRecognitionEvent[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.GenericRecognitionEvent[paramAnonymousInt];
      }
    };
    
    public GenericRecognitionEvent(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean2, AudioFormat paramAudioFormat, byte[] paramArrayOfByte)
    {
      super(paramInt2, paramBoolean1, paramInt3, paramInt4, paramInt5, paramBoolean2, paramAudioFormat, paramArrayOfByte);
    }
    
    private static GenericRecognitionEvent fromParcelForGeneric(Parcel paramParcel)
    {
      paramParcel = SoundTrigger.RecognitionEvent.fromParcel(paramParcel);
      return new GenericRecognitionEvent(paramParcel.status, paramParcel.soundModelHandle, paramParcel.captureAvailable, paramParcel.captureSession, paramParcel.captureDelayMs, paramParcel.capturePreambleMs, paramParcel.triggerInData, paramParcel.captureFormat, paramParcel.data);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      SoundTrigger.RecognitionEvent localRecognitionEvent = (SoundTrigger.RecognitionEvent)paramObject;
      return super.equals(paramObject);
    }
    
    public String toString()
    {
      return "GenericRecognitionEvent ::" + super.toString();
    }
  }
  
  public static class GenericSoundModel
    extends SoundTrigger.SoundModel
    implements Parcelable
  {
    public static final Parcelable.Creator<GenericSoundModel> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.GenericSoundModel createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.GenericSoundModel.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.GenericSoundModel[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.GenericSoundModel[paramAnonymousInt];
      }
    };
    
    public GenericSoundModel(UUID paramUUID1, UUID paramUUID2, byte[] paramArrayOfByte)
    {
      super(paramUUID2, 1, paramArrayOfByte);
    }
    
    private static GenericSoundModel fromParcel(Parcel paramParcel)
    {
      UUID localUUID2 = UUID.fromString(paramParcel.readString());
      UUID localUUID1 = null;
      if (paramParcel.readInt() >= 0) {
        localUUID1 = UUID.fromString(paramParcel.readString());
      }
      return new GenericSoundModel(localUUID2, localUUID1, paramParcel.readBlob());
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("GenericSoundModel [uuid=").append(this.uuid).append(", vendorUuid=").append(this.vendorUuid).append(", type=").append(this.type).append(", data=");
      if (this.data == null) {}
      for (int i = 0;; i = this.data.length) {
        return i + "]";
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.uuid.toString());
      if (this.vendorUuid == null) {
        paramParcel.writeInt(-1);
      }
      for (;;)
      {
        paramParcel.writeBlob(this.data);
        return;
        paramParcel.writeInt(this.vendorUuid.toString().length());
        paramParcel.writeString(this.vendorUuid.toString());
      }
    }
  }
  
  public static class Keyphrase
    implements Parcelable
  {
    public static final Parcelable.Creator<Keyphrase> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.Keyphrase createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.Keyphrase.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.Keyphrase[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.Keyphrase[paramAnonymousInt];
      }
    };
    public final int id;
    public final String locale;
    public final int recognitionModes;
    public final String text;
    public final int[] users;
    
    public Keyphrase(int paramInt1, int paramInt2, String paramString1, String paramString2, int[] paramArrayOfInt)
    {
      this.id = paramInt1;
      this.recognitionModes = paramInt2;
      this.locale = paramString1;
      this.text = paramString2;
      this.users = paramArrayOfInt;
    }
    
    private static Keyphrase fromParcel(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      int j = paramParcel.readInt();
      String str1 = paramParcel.readString();
      String str2 = paramParcel.readString();
      int[] arrayOfInt = null;
      int k = paramParcel.readInt();
      if (k >= 0)
      {
        arrayOfInt = new int[k];
        paramParcel.readIntArray(arrayOfInt);
      }
      return new Keyphrase(i, j, str1, str2, arrayOfInt);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (Keyphrase)paramObject;
      if (this.text == null)
      {
        if (((Keyphrase)paramObject).text != null) {
          return false;
        }
      }
      else if (!this.text.equals(((Keyphrase)paramObject).text)) {
        return false;
      }
      if (this.id != ((Keyphrase)paramObject).id) {
        return false;
      }
      if (this.locale == null)
      {
        if (((Keyphrase)paramObject).locale != null) {
          return false;
        }
      }
      else if (!this.locale.equals(((Keyphrase)paramObject).locale)) {
        return false;
      }
      if (this.recognitionModes != ((Keyphrase)paramObject).recognitionModes) {
        return false;
      }
      return Arrays.equals(this.users, ((Keyphrase)paramObject).users);
    }
    
    public int hashCode()
    {
      int j = 0;
      int i;
      int k;
      if (this.text == null)
      {
        i = 0;
        k = this.id;
        if (this.locale != null) {
          break label68;
        }
      }
      for (;;)
      {
        return ((((i + 31) * 31 + k) * 31 + j) * 31 + this.recognitionModes) * 31 + Arrays.hashCode(this.users);
        i = this.text.hashCode();
        break;
        label68:
        j = this.locale.hashCode();
      }
    }
    
    public String toString()
    {
      return "Keyphrase [id=" + this.id + ", recognitionModes=" + this.recognitionModes + ", locale=" + this.locale + ", text=" + this.text + ", users=" + Arrays.toString(this.users) + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.id);
      paramParcel.writeInt(this.recognitionModes);
      paramParcel.writeString(this.locale);
      paramParcel.writeString(this.text);
      if (this.users != null)
      {
        paramParcel.writeInt(this.users.length);
        paramParcel.writeIntArray(this.users);
        return;
      }
      paramParcel.writeInt(-1);
    }
  }
  
  public static class KeyphraseRecognitionEvent
    extends SoundTrigger.RecognitionEvent
  {
    public static final Parcelable.Creator<KeyphraseRecognitionEvent> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.KeyphraseRecognitionEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.KeyphraseRecognitionEvent.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.KeyphraseRecognitionEvent[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.KeyphraseRecognitionEvent[paramAnonymousInt];
      }
    };
    public final SoundTrigger.KeyphraseRecognitionExtra[] keyphraseExtras;
    
    public KeyphraseRecognitionEvent(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean2, AudioFormat paramAudioFormat, byte[] paramArrayOfByte, SoundTrigger.KeyphraseRecognitionExtra[] paramArrayOfKeyphraseRecognitionExtra)
    {
      super(paramInt2, paramBoolean1, paramInt3, paramInt4, paramInt5, paramBoolean2, paramAudioFormat, paramArrayOfByte);
      this.keyphraseExtras = paramArrayOfKeyphraseRecognitionExtra;
    }
    
    private static KeyphraseRecognitionEvent fromParcelForKeyphrase(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      int j = paramParcel.readInt();
      boolean bool1;
      int k;
      int m;
      int n;
      if (paramParcel.readByte() == 1)
      {
        bool1 = true;
        k = paramParcel.readInt();
        m = paramParcel.readInt();
        n = paramParcel.readInt();
        if (paramParcel.readByte() != 1) {
          break label146;
        }
      }
      label146:
      for (boolean bool2 = true;; bool2 = false)
      {
        AudioFormat localAudioFormat = null;
        if (paramParcel.readByte() == 1)
        {
          int i1 = paramParcel.readInt();
          int i2 = paramParcel.readInt();
          int i3 = paramParcel.readInt();
          localAudioFormat = new AudioFormat.Builder().setChannelMask(i3).setEncoding(i2).setSampleRate(i1).build();
        }
        return new KeyphraseRecognitionEvent(i, j, bool1, k, m, n, bool2, localAudioFormat, paramParcel.readBlob(), (SoundTrigger.KeyphraseRecognitionExtra[])paramParcel.createTypedArray(SoundTrigger.KeyphraseRecognitionExtra.CREATOR));
        bool1 = false;
        break;
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!super.equals(paramObject)) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (KeyphraseRecognitionEvent)paramObject;
      return Arrays.equals(this.keyphraseExtras, ((KeyphraseRecognitionEvent)paramObject).keyphraseExtras);
    }
    
    public int hashCode()
    {
      return super.hashCode() * 31 + Arrays.hashCode(this.keyphraseExtras);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("KeyphraseRecognitionEvent [keyphraseExtras=").append(Arrays.toString(this.keyphraseExtras)).append(", status=").append(this.status).append(", soundModelHandle=").append(this.soundModelHandle).append(", captureAvailable=").append(this.captureAvailable).append(", captureSession=").append(this.captureSession).append(", captureDelayMs=").append(this.captureDelayMs).append(", capturePreambleMs=").append(this.capturePreambleMs).append(", triggerInData=").append(this.triggerInData);
      Object localObject;
      if (this.captureFormat == null)
      {
        localObject = "";
        localStringBuilder = localStringBuilder.append((String)localObject);
        if (this.captureFormat != null) {
          break label212;
        }
        localObject = "";
        label133:
        localStringBuilder = localStringBuilder.append((String)localObject);
        if (this.captureFormat != null) {
          break label241;
        }
        localObject = "";
        label149:
        localObject = localStringBuilder.append((String)localObject).append(", data=");
        if (this.data != null) {
          break label270;
        }
      }
      label212:
      label241:
      label270:
      for (int i = 0;; i = this.data.length)
      {
        return i + "]";
        localObject = ", sampleRate=" + this.captureFormat.getSampleRate();
        break;
        localObject = ", encoding=" + this.captureFormat.getEncoding();
        break label133;
        localObject = ", channelMask=" + this.captureFormat.getChannelMask();
        break label149;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.status);
      paramParcel.writeInt(this.soundModelHandle);
      int i;
      if (this.captureAvailable)
      {
        i = 1;
        paramParcel.writeByte((byte)i);
        paramParcel.writeInt(this.captureSession);
        paramParcel.writeInt(this.captureDelayMs);
        paramParcel.writeInt(this.capturePreambleMs);
        if (!this.triggerInData) {
          break label138;
        }
        i = 1;
        label64:
        paramParcel.writeByte((byte)i);
        if (this.captureFormat == null) {
          break label143;
        }
        paramParcel.writeByte((byte)1);
        paramParcel.writeInt(this.captureFormat.getSampleRate());
        paramParcel.writeInt(this.captureFormat.getEncoding());
        paramParcel.writeInt(this.captureFormat.getChannelMask());
      }
      for (;;)
      {
        paramParcel.writeBlob(this.data);
        paramParcel.writeTypedArray(this.keyphraseExtras, paramInt);
        return;
        i = 0;
        break;
        label138:
        i = 0;
        break label64;
        label143:
        paramParcel.writeByte((byte)0);
      }
    }
  }
  
  public static class KeyphraseRecognitionExtra
    implements Parcelable
  {
    public static final Parcelable.Creator<KeyphraseRecognitionExtra> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.KeyphraseRecognitionExtra createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.KeyphraseRecognitionExtra.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.KeyphraseRecognitionExtra[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.KeyphraseRecognitionExtra[paramAnonymousInt];
      }
    };
    public final int coarseConfidenceLevel;
    public final SoundTrigger.ConfidenceLevel[] confidenceLevels;
    public final int id;
    public final int recognitionModes;
    
    public KeyphraseRecognitionExtra(int paramInt1, int paramInt2, int paramInt3, SoundTrigger.ConfidenceLevel[] paramArrayOfConfidenceLevel)
    {
      this.id = paramInt1;
      this.recognitionModes = paramInt2;
      this.coarseConfidenceLevel = paramInt3;
      this.confidenceLevels = paramArrayOfConfidenceLevel;
    }
    
    private static KeyphraseRecognitionExtra fromParcel(Parcel paramParcel)
    {
      return new KeyphraseRecognitionExtra(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt(), (SoundTrigger.ConfidenceLevel[])paramParcel.createTypedArray(SoundTrigger.ConfidenceLevel.CREATOR));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (KeyphraseRecognitionExtra)paramObject;
      if (!Arrays.equals(this.confidenceLevels, ((KeyphraseRecognitionExtra)paramObject).confidenceLevels)) {
        return false;
      }
      if (this.id != ((KeyphraseRecognitionExtra)paramObject).id) {
        return false;
      }
      if (this.recognitionModes != ((KeyphraseRecognitionExtra)paramObject).recognitionModes) {
        return false;
      }
      return this.coarseConfidenceLevel == ((KeyphraseRecognitionExtra)paramObject).coarseConfidenceLevel;
    }
    
    public int hashCode()
    {
      return (((Arrays.hashCode(this.confidenceLevels) + 31) * 31 + this.id) * 31 + this.recognitionModes) * 31 + this.coarseConfidenceLevel;
    }
    
    public String toString()
    {
      return "KeyphraseRecognitionExtra [id=" + this.id + ", recognitionModes=" + this.recognitionModes + ", coarseConfidenceLevel=" + this.coarseConfidenceLevel + ", confidenceLevels=" + Arrays.toString(this.confidenceLevels) + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.id);
      paramParcel.writeInt(this.recognitionModes);
      paramParcel.writeInt(this.coarseConfidenceLevel);
      paramParcel.writeTypedArray(this.confidenceLevels, paramInt);
    }
  }
  
  public static class KeyphraseSoundModel
    extends SoundTrigger.SoundModel
    implements Parcelable
  {
    public static final Parcelable.Creator<KeyphraseSoundModel> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.KeyphraseSoundModel createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.KeyphraseSoundModel.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.KeyphraseSoundModel[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.KeyphraseSoundModel[paramAnonymousInt];
      }
    };
    public final SoundTrigger.Keyphrase[] keyphrases;
    
    public KeyphraseSoundModel(UUID paramUUID1, UUID paramUUID2, byte[] paramArrayOfByte, SoundTrigger.Keyphrase[] paramArrayOfKeyphrase)
    {
      super(paramUUID2, 0, paramArrayOfByte);
      this.keyphrases = paramArrayOfKeyphrase;
    }
    
    private static KeyphraseSoundModel fromParcel(Parcel paramParcel)
    {
      UUID localUUID2 = UUID.fromString(paramParcel.readString());
      UUID localUUID1 = null;
      if (paramParcel.readInt() >= 0) {
        localUUID1 = UUID.fromString(paramParcel.readString());
      }
      return new KeyphraseSoundModel(localUUID2, localUUID1, paramParcel.readBlob(), (SoundTrigger.Keyphrase[])paramParcel.createTypedArray(SoundTrigger.Keyphrase.CREATOR));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!super.equals(paramObject)) {
        return false;
      }
      if (!(paramObject instanceof KeyphraseSoundModel)) {
        return false;
      }
      paramObject = (KeyphraseSoundModel)paramObject;
      return Arrays.equals(this.keyphrases, ((KeyphraseSoundModel)paramObject).keyphrases);
    }
    
    public int hashCode()
    {
      return super.hashCode() * 31 + Arrays.hashCode(this.keyphrases);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("KeyphraseSoundModel [keyphrases=").append(Arrays.toString(this.keyphrases)).append(", uuid=").append(this.uuid).append(", vendorUuid=").append(this.vendorUuid).append(", type=").append(this.type).append(", data=");
      if (this.data == null) {}
      for (int i = 0;; i = this.data.length) {
        return i + "]";
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.uuid.toString());
      if (this.vendorUuid == null) {
        paramParcel.writeInt(-1);
      }
      for (;;)
      {
        paramParcel.writeBlob(this.data);
        paramParcel.writeTypedArray(this.keyphrases, paramInt);
        return;
        paramParcel.writeInt(this.vendorUuid.toString().length());
        paramParcel.writeString(this.vendorUuid.toString());
      }
    }
  }
  
  public static class ModuleProperties
    implements Parcelable
  {
    public static final Parcelable.Creator<ModuleProperties> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.ModuleProperties createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.ModuleProperties.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.ModuleProperties[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.ModuleProperties[paramAnonymousInt];
      }
    };
    public final String description;
    public final int id;
    public final String implementor;
    public final int maxBufferMs;
    public final int maxKeyphrases;
    public final int maxSoundModels;
    public final int maxUsers;
    public final int powerConsumptionMw;
    public final int recognitionModes;
    public final boolean returnsTriggerInEvent;
    public final boolean supportsCaptureTransition;
    public final boolean supportsConcurrentCapture;
    public final UUID uuid;
    public final int version;
    
    ModuleProperties(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean1, int paramInt7, boolean paramBoolean2, int paramInt8, boolean paramBoolean3)
    {
      this.id = paramInt1;
      this.implementor = paramString1;
      this.description = paramString2;
      this.uuid = UUID.fromString(paramString3);
      this.version = paramInt2;
      this.maxSoundModels = paramInt3;
      this.maxKeyphrases = paramInt4;
      this.maxUsers = paramInt5;
      this.recognitionModes = paramInt6;
      this.supportsCaptureTransition = paramBoolean1;
      this.maxBufferMs = paramInt7;
      this.supportsConcurrentCapture = paramBoolean2;
      this.powerConsumptionMw = paramInt8;
      this.returnsTriggerInEvent = paramBoolean3;
    }
    
    private static ModuleProperties fromParcel(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      String str1 = paramParcel.readString();
      String str2 = paramParcel.readString();
      String str3 = paramParcel.readString();
      int j = paramParcel.readInt();
      int k = paramParcel.readInt();
      int m = paramParcel.readInt();
      int n = paramParcel.readInt();
      int i1 = paramParcel.readInt();
      boolean bool1;
      int i2;
      boolean bool2;
      label79:
      int i3;
      if (paramParcel.readByte() == 1)
      {
        bool1 = true;
        i2 = paramParcel.readInt();
        if (paramParcel.readByte() != 1) {
          break label135;
        }
        bool2 = true;
        i3 = paramParcel.readInt();
        if (paramParcel.readByte() != 1) {
          break label141;
        }
      }
      label135:
      label141:
      for (boolean bool3 = true;; bool3 = false)
      {
        return new ModuleProperties(i, str1, str2, str3, j, k, m, n, i1, bool1, i2, bool2, i3, bool3);
        bool1 = false;
        break;
        bool2 = false;
        break label79;
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      return "ModuleProperties [id=" + this.id + ", implementor=" + this.implementor + ", description=" + this.description + ", uuid=" + this.uuid + ", version=" + this.version + ", maxSoundModels=" + this.maxSoundModels + ", maxKeyphrases=" + this.maxKeyphrases + ", maxUsers=" + this.maxUsers + ", recognitionModes=" + this.recognitionModes + ", supportsCaptureTransition=" + this.supportsCaptureTransition + ", maxBufferMs=" + this.maxBufferMs + ", supportsConcurrentCapture=" + this.supportsConcurrentCapture + ", powerConsumptionMw=" + this.powerConsumptionMw + ", returnsTriggerInEvent=" + this.returnsTriggerInEvent + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      paramParcel.writeInt(this.id);
      paramParcel.writeString(this.implementor);
      paramParcel.writeString(this.description);
      paramParcel.writeString(this.uuid.toString());
      paramParcel.writeInt(this.version);
      paramParcel.writeInt(this.maxSoundModels);
      paramParcel.writeInt(this.maxKeyphrases);
      paramParcel.writeInt(this.maxUsers);
      paramParcel.writeInt(this.recognitionModes);
      if (this.supportsCaptureTransition)
      {
        paramInt = 1;
        paramParcel.writeByte((byte)paramInt);
        paramParcel.writeInt(this.maxBufferMs);
        if (!this.supportsConcurrentCapture) {
          break label144;
        }
        paramInt = 1;
        label109:
        paramParcel.writeByte((byte)paramInt);
        paramParcel.writeInt(this.powerConsumptionMw);
        if (!this.returnsTriggerInEvent) {
          break label149;
        }
      }
      label144:
      label149:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        return;
        paramInt = 0;
        break;
        paramInt = 0;
        break label109;
      }
    }
  }
  
  public static class RecognitionConfig
    implements Parcelable
  {
    public static final Parcelable.Creator<RecognitionConfig> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.RecognitionConfig createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.RecognitionConfig.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.RecognitionConfig[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.RecognitionConfig[paramAnonymousInt];
      }
    };
    public final boolean allowMultipleTriggers;
    public final boolean captureRequested;
    public final byte[] data;
    public final SoundTrigger.KeyphraseRecognitionExtra[] keyphrases;
    
    public RecognitionConfig(boolean paramBoolean1, boolean paramBoolean2, SoundTrigger.KeyphraseRecognitionExtra[] paramArrayOfKeyphraseRecognitionExtra, byte[] paramArrayOfByte)
    {
      this.captureRequested = paramBoolean1;
      this.allowMultipleTriggers = paramBoolean2;
      this.keyphrases = paramArrayOfKeyphraseRecognitionExtra;
      this.data = paramArrayOfByte;
    }
    
    private static RecognitionConfig fromParcel(Parcel paramParcel)
    {
      boolean bool1;
      if (paramParcel.readByte() == 1)
      {
        bool1 = true;
        if (paramParcel.readByte() != 1) {
          break label49;
        }
      }
      label49:
      for (boolean bool2 = true;; bool2 = false)
      {
        return new RecognitionConfig(bool1, bool2, (SoundTrigger.KeyphraseRecognitionExtra[])paramParcel.createTypedArray(SoundTrigger.KeyphraseRecognitionExtra.CREATOR), paramParcel.readBlob());
        bool1 = false;
        break;
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      return "RecognitionConfig [captureRequested=" + this.captureRequested + ", allowMultipleTriggers=" + this.allowMultipleTriggers + ", keyphrases=" + Arrays.toString(this.keyphrases) + ", data=" + Arrays.toString(this.data) + "]";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int j = 1;
      if (this.captureRequested)
      {
        i = 1;
        paramParcel.writeByte((byte)i);
        if (!this.allowMultipleTriggers) {
          break label57;
        }
      }
      label57:
      for (int i = j;; i = 0)
      {
        paramParcel.writeByte((byte)i);
        paramParcel.writeTypedArray(this.keyphrases, paramInt);
        paramParcel.writeBlob(this.data);
        return;
        i = 0;
        break;
      }
    }
  }
  
  public static class RecognitionEvent
    implements Parcelable
  {
    public static final Parcelable.Creator<RecognitionEvent> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.RecognitionEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.RecognitionEvent.fromParcel(paramAnonymousParcel);
      }
      
      public SoundTrigger.RecognitionEvent[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.RecognitionEvent[paramAnonymousInt];
      }
    };
    public final boolean captureAvailable;
    public final int captureDelayMs;
    public AudioFormat captureFormat;
    public final int capturePreambleMs;
    public final int captureSession;
    public final byte[] data;
    public final int soundModelHandle;
    public final int status;
    public final boolean triggerInData;
    
    public RecognitionEvent(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean2, AudioFormat paramAudioFormat, byte[] paramArrayOfByte)
    {
      this.status = paramInt1;
      this.soundModelHandle = paramInt2;
      this.captureAvailable = paramBoolean1;
      this.captureSession = paramInt3;
      this.captureDelayMs = paramInt4;
      this.capturePreambleMs = paramInt5;
      this.triggerInData = paramBoolean2;
      this.captureFormat = paramAudioFormat;
      this.data = paramArrayOfByte;
    }
    
    protected static RecognitionEvent fromParcel(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      int j = paramParcel.readInt();
      boolean bool1;
      int k;
      int m;
      int n;
      if (paramParcel.readByte() == 1)
      {
        bool1 = true;
        k = paramParcel.readInt();
        m = paramParcel.readInt();
        n = paramParcel.readInt();
        if (paramParcel.readByte() != 1) {
          break label136;
        }
      }
      label136:
      for (boolean bool2 = true;; bool2 = false)
      {
        AudioFormat localAudioFormat = null;
        if (paramParcel.readByte() == 1)
        {
          int i1 = paramParcel.readInt();
          int i2 = paramParcel.readInt();
          int i3 = paramParcel.readInt();
          localAudioFormat = new AudioFormat.Builder().setChannelMask(i3).setEncoding(i2).setSampleRate(i1).build();
        }
        return new RecognitionEvent(i, j, bool1, k, m, n, bool2, localAudioFormat, paramParcel.readBlob());
        bool1 = false;
        break;
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (RecognitionEvent)paramObject;
      if (this.captureAvailable != ((RecognitionEvent)paramObject).captureAvailable) {
        return false;
      }
      if (this.captureDelayMs != ((RecognitionEvent)paramObject).captureDelayMs) {
        return false;
      }
      if (this.capturePreambleMs != ((RecognitionEvent)paramObject).capturePreambleMs) {
        return false;
      }
      if (this.captureSession != ((RecognitionEvent)paramObject).captureSession) {
        return false;
      }
      if (!Arrays.equals(this.data, ((RecognitionEvent)paramObject).data)) {
        return false;
      }
      if (this.soundModelHandle != ((RecognitionEvent)paramObject).soundModelHandle) {
        return false;
      }
      if (this.status != ((RecognitionEvent)paramObject).status) {
        return false;
      }
      if (this.triggerInData != ((RecognitionEvent)paramObject).triggerInData) {
        return false;
      }
      if (this.captureFormat == null)
      {
        if (((RecognitionEvent)paramObject).captureFormat != null) {
          return false;
        }
      }
      else
      {
        if (((RecognitionEvent)paramObject).captureFormat == null) {
          return false;
        }
        if (this.captureFormat.getSampleRate() != ((RecognitionEvent)paramObject).captureFormat.getSampleRate()) {
          return false;
        }
        if (this.captureFormat.getEncoding() != ((RecognitionEvent)paramObject).captureFormat.getEncoding()) {
          return false;
        }
        if (this.captureFormat.getChannelMask() != ((RecognitionEvent)paramObject).captureFormat.getChannelMask()) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      int j = 1231;
      int i;
      int k;
      int m;
      int n;
      if (this.captureAvailable)
      {
        i = 1231;
        k = this.captureDelayMs;
        m = this.capturePreambleMs;
        n = this.captureSession;
        if (!this.triggerInData) {
          break label146;
        }
      }
      for (;;)
      {
        j = ((((i + 31) * 31 + k) * 31 + m) * 31 + n) * 31 + j;
        i = j;
        if (this.captureFormat != null) {
          i = ((j * 31 + this.captureFormat.getSampleRate()) * 31 + this.captureFormat.getEncoding()) * 31 + this.captureFormat.getChannelMask();
        }
        return ((i * 31 + Arrays.hashCode(this.data)) * 31 + this.soundModelHandle) * 31 + this.status;
        i = 1237;
        break;
        label146:
        j = 1237;
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("RecognitionEvent [status=").append(this.status).append(", soundModelHandle=").append(this.soundModelHandle).append(", captureAvailable=").append(this.captureAvailable).append(", captureSession=").append(this.captureSession).append(", captureDelayMs=").append(this.captureDelayMs).append(", capturePreambleMs=").append(this.capturePreambleMs).append(", triggerInData=").append(this.triggerInData);
      Object localObject;
      if (this.captureFormat == null)
      {
        localObject = "";
        localStringBuilder = localStringBuilder.append((String)localObject);
        if (this.captureFormat != null) {
          break label197;
        }
        localObject = "";
        label118:
        localStringBuilder = localStringBuilder.append((String)localObject);
        if (this.captureFormat != null) {
          break label226;
        }
        localObject = "";
        label134:
        localObject = localStringBuilder.append((String)localObject).append(", data=");
        if (this.data != null) {
          break label255;
        }
      }
      label197:
      label226:
      label255:
      for (int i = 0;; i = this.data.length)
      {
        return i + "]";
        localObject = ", sampleRate=" + this.captureFormat.getSampleRate();
        break;
        localObject = ", encoding=" + this.captureFormat.getEncoding();
        break label118;
        localObject = ", channelMask=" + this.captureFormat.getChannelMask();
        break label134;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.status);
      paramParcel.writeInt(this.soundModelHandle);
      if (this.captureAvailable)
      {
        paramInt = 1;
        paramParcel.writeByte((byte)paramInt);
        paramParcel.writeInt(this.captureSession);
        paramParcel.writeInt(this.captureDelayMs);
        paramParcel.writeInt(this.capturePreambleMs);
        if (!this.triggerInData) {
          break label129;
        }
        paramInt = 1;
        label64:
        paramParcel.writeByte((byte)paramInt);
        if (this.captureFormat == null) {
          break label134;
        }
        paramParcel.writeByte((byte)1);
        paramParcel.writeInt(this.captureFormat.getSampleRate());
        paramParcel.writeInt(this.captureFormat.getEncoding());
        paramParcel.writeInt(this.captureFormat.getChannelMask());
      }
      for (;;)
      {
        paramParcel.writeBlob(this.data);
        return;
        paramInt = 0;
        break;
        label129:
        paramInt = 0;
        break label64;
        label134:
        paramParcel.writeByte((byte)0);
      }
    }
  }
  
  public static class SoundModel
  {
    public static final int TYPE_GENERIC_SOUND = 1;
    public static final int TYPE_KEYPHRASE = 0;
    public static final int TYPE_UNKNOWN = -1;
    public final byte[] data;
    public final int type;
    public final UUID uuid;
    public final UUID vendorUuid;
    
    public SoundModel(UUID paramUUID1, UUID paramUUID2, int paramInt, byte[] paramArrayOfByte)
    {
      this.uuid = paramUUID1;
      this.vendorUuid = paramUUID2;
      this.type = paramInt;
      this.data = paramArrayOfByte;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (!(paramObject instanceof SoundModel)) {
        return false;
      }
      paramObject = (SoundModel)paramObject;
      if (!Arrays.equals(this.data, ((SoundModel)paramObject).data)) {
        return false;
      }
      if (this.type != ((SoundModel)paramObject).type) {
        return false;
      }
      if (this.uuid == null)
      {
        if (((SoundModel)paramObject).uuid != null) {
          return false;
        }
      }
      else if (!this.uuid.equals(((SoundModel)paramObject).uuid)) {
        return false;
      }
      if (this.vendorUuid == null)
      {
        if (((SoundModel)paramObject).vendorUuid != null) {
          return false;
        }
      }
      else if (!this.vendorUuid.equals(((SoundModel)paramObject).vendorUuid)) {
        return false;
      }
      return true;
    }
    
    public int hashCode()
    {
      int j = 0;
      int k = Arrays.hashCode(this.data);
      int m = this.type;
      int i;
      if (this.uuid == null)
      {
        i = 0;
        if (this.vendorUuid != null) {
          break label64;
        }
      }
      for (;;)
      {
        return (((k + 31) * 31 + m) * 31 + i) * 31 + j;
        i = this.uuid.hashCode();
        break;
        label64:
        j = this.vendorUuid.hashCode();
      }
    }
  }
  
  public static class SoundModelEvent
    implements Parcelable
  {
    public static final Parcelable.Creator<SoundModelEvent> CREATOR = new Parcelable.Creator()
    {
      public SoundTrigger.SoundModelEvent createFromParcel(Parcel paramAnonymousParcel)
      {
        return SoundTrigger.SoundModelEvent.-wrap0(paramAnonymousParcel);
      }
      
      public SoundTrigger.SoundModelEvent[] newArray(int paramAnonymousInt)
      {
        return new SoundTrigger.SoundModelEvent[paramAnonymousInt];
      }
    };
    public final byte[] data;
    public final int soundModelHandle;
    public final int status;
    
    SoundModelEvent(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      this.status = paramInt1;
      this.soundModelHandle = paramInt2;
      this.data = paramArrayOfByte;
    }
    
    private static SoundModelEvent fromParcel(Parcel paramParcel)
    {
      return new SoundModelEvent(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readBlob());
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (SoundModelEvent)paramObject;
      if (!Arrays.equals(this.data, ((SoundModelEvent)paramObject).data)) {
        return false;
      }
      if (this.soundModelHandle != ((SoundModelEvent)paramObject).soundModelHandle) {
        return false;
      }
      return this.status == ((SoundModelEvent)paramObject).status;
    }
    
    public int hashCode()
    {
      return ((Arrays.hashCode(this.data) + 31) * 31 + this.soundModelHandle) * 31 + this.status;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("SoundModelEvent [status=").append(this.status).append(", soundModelHandle=").append(this.soundModelHandle).append(", data=");
      if (this.data == null) {}
      for (int i = 0;; i = this.data.length) {
        return i + "]";
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.status);
      paramParcel.writeInt(this.soundModelHandle);
      paramParcel.writeBlob(this.data);
    }
  }
  
  public static abstract interface StatusListener
  {
    public abstract void onRecognition(SoundTrigger.RecognitionEvent paramRecognitionEvent);
    
    public abstract void onServiceDied();
    
    public abstract void onServiceStateChange(int paramInt);
    
    public abstract void onSoundModelUpdate(SoundTrigger.SoundModelEvent paramSoundModelEvent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/soundtrigger/SoundTrigger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */