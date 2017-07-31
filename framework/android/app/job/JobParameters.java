package android.app.job;

import android.net.Uri;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;

public class JobParameters
  implements Parcelable
{
  public static final Parcelable.Creator<JobParameters> CREATOR = new Parcelable.Creator()
  {
    public JobParameters createFromParcel(Parcel paramAnonymousParcel)
    {
      return new JobParameters(paramAnonymousParcel, null);
    }
    
    public JobParameters[] newArray(int paramAnonymousInt)
    {
      return new JobParameters[paramAnonymousInt];
    }
  };
  public static final int REASON_CANCELED = 0;
  public static final int REASON_CONSTRAINTS_NOT_SATISFIED = 1;
  public static final int REASON_DEVICE_IDLE = 4;
  public static final int REASON_PREEMPT = 2;
  public static final int REASON_TIMEOUT = 3;
  private final IBinder callback;
  private final PersistableBundle extras;
  private final int jobId;
  private final String[] mTriggeredContentAuthorities;
  private final Uri[] mTriggeredContentUris;
  private final boolean overrideDeadlineExpired;
  private int stopReason;
  
  public JobParameters(IBinder paramIBinder, int paramInt, PersistableBundle paramPersistableBundle, boolean paramBoolean, Uri[] paramArrayOfUri, String[] paramArrayOfString)
  {
    this.jobId = paramInt;
    this.extras = paramPersistableBundle;
    this.callback = paramIBinder;
    this.overrideDeadlineExpired = paramBoolean;
    this.mTriggeredContentUris = paramArrayOfUri;
    this.mTriggeredContentAuthorities = paramArrayOfString;
  }
  
  private JobParameters(Parcel paramParcel)
  {
    this.jobId = paramParcel.readInt();
    this.extras = paramParcel.readPersistableBundle();
    this.callback = paramParcel.readStrongBinder();
    if (paramParcel.readInt() == 1) {}
    for (;;)
    {
      this.overrideDeadlineExpired = bool;
      this.mTriggeredContentUris = ((Uri[])paramParcel.createTypedArray(Uri.CREATOR));
      this.mTriggeredContentAuthorities = paramParcel.createStringArray();
      this.stopReason = paramParcel.readInt();
      return;
      bool = false;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public IJobCallback getCallback()
  {
    return IJobCallback.Stub.asInterface(this.callback);
  }
  
  public PersistableBundle getExtras()
  {
    return this.extras;
  }
  
  public int getJobId()
  {
    return this.jobId;
  }
  
  public int getStopReason()
  {
    return this.stopReason;
  }
  
  public String[] getTriggeredContentAuthorities()
  {
    return this.mTriggeredContentAuthorities;
  }
  
  public Uri[] getTriggeredContentUris()
  {
    return this.mTriggeredContentUris;
  }
  
  public boolean isOverrideDeadlineExpired()
  {
    return this.overrideDeadlineExpired;
  }
  
  public void setStopReason(int paramInt)
  {
    this.stopReason = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.jobId);
    paramParcel.writePersistableBundle(this.extras);
    paramParcel.writeStrongBinder(this.callback);
    if (this.overrideDeadlineExpired) {}
    for (int i = 1;; i = 0)
    {
      paramParcel.writeInt(i);
      paramParcel.writeTypedArray(this.mTriggeredContentUris, paramInt);
      paramParcel.writeStringArray(this.mTriggeredContentAuthorities);
      paramParcel.writeInt(this.stopReason);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/job/JobParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */