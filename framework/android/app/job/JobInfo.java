package android.app.job;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.TimeUtils;
import java.util.ArrayList;
import java.util.Objects;

public class JobInfo
  implements Parcelable
{
  public static final int BACKOFF_POLICY_EXPONENTIAL = 1;
  public static final int BACKOFF_POLICY_LINEAR = 0;
  public static final Parcelable.Creator<JobInfo> CREATOR = new Parcelable.Creator()
  {
    public JobInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new JobInfo(paramAnonymousParcel, null);
    }
    
    public JobInfo[] newArray(int paramAnonymousInt)
    {
      return new JobInfo[paramAnonymousInt];
    }
  };
  public static final int DEFAULT_BACKOFF_POLICY = 1;
  public static final long DEFAULT_INITIAL_BACKOFF_MILLIS = 30000L;
  public static final int FLAG_WILL_BE_FOREGROUND = 1;
  public static final long MAX_BACKOFF_DELAY_MILLIS = 18000000L;
  private static final long MIN_FLEX_MILLIS = 300000L;
  private static final long MIN_PERIOD_MILLIS = 900000L;
  public static final int NETWORK_TYPE_ANY = 1;
  public static final int NETWORK_TYPE_NONE = 0;
  public static final int NETWORK_TYPE_NOT_ROAMING = 3;
  public static final int NETWORK_TYPE_UNMETERED = 2;
  public static final int PRIORITY_ADJ_ALWAYS_RUNNING = -80;
  public static final int PRIORITY_ADJ_OFTEN_RUNNING = -40;
  public static final int PRIORITY_DEFAULT = 0;
  public static final int PRIORITY_FOREGROUND_APP = 30;
  public static final int PRIORITY_SYNC_EXPEDITED = 10;
  public static final int PRIORITY_SYNC_INITIALIZATION = 20;
  public static final int PRIORITY_TOP_APP = 40;
  private static String TAG = "JobInfo";
  private final int backoffPolicy;
  private final PersistableBundle extras;
  private final int flags;
  private final long flexMillis;
  private final boolean hasEarlyConstraint;
  private final boolean hasLateConstraint;
  private final long initialBackoffMillis;
  private final long intervalMillis;
  private final boolean isPeriodic;
  private final boolean isPersisted;
  private final int jobId;
  private final long maxExecutionDelayMillis;
  private final long minLatencyMillis;
  private final int networkType;
  private final int priority;
  private final boolean requireCharging;
  private final boolean requireDeviceIdle;
  private final ComponentName service;
  private final long triggerContentMaxDelay;
  private final long triggerContentUpdateDelay;
  private final TriggerContentUri[] triggerContentUris;
  
  private JobInfo(Builder paramBuilder)
  {
    this.jobId = Builder.-get10(paramBuilder);
    this.extras = Builder.-get1(paramBuilder);
    this.service = Builder.-get11(paramBuilder);
    this.requireCharging = Builder.-get16(paramBuilder);
    this.requireDeviceIdle = Builder.-get17(paramBuilder);
    if (Builder.-get20(paramBuilder) != null) {
      arrayOfTriggerContentUri = (TriggerContentUri[])Builder.-get20(paramBuilder).toArray(new TriggerContentUri[Builder.-get20(paramBuilder).size()]);
    }
    this.triggerContentUris = arrayOfTriggerContentUri;
    this.triggerContentUpdateDelay = Builder.-get19(paramBuilder);
    this.triggerContentMaxDelay = Builder.-get18(paramBuilder);
    this.networkType = Builder.-get14(paramBuilder);
    this.minLatencyMillis = Builder.-get13(paramBuilder);
    this.maxExecutionDelayMillis = Builder.-get12(paramBuilder);
    this.isPeriodic = Builder.-get8(paramBuilder);
    this.isPersisted = Builder.-get9(paramBuilder);
    this.intervalMillis = Builder.-get7(paramBuilder);
    this.flexMillis = Builder.-get3(paramBuilder);
    this.initialBackoffMillis = Builder.-get6(paramBuilder);
    this.backoffPolicy = Builder.-get0(paramBuilder);
    this.hasEarlyConstraint = Builder.-get4(paramBuilder);
    this.hasLateConstraint = Builder.-get5(paramBuilder);
    this.priority = Builder.-get15(paramBuilder);
    this.flags = Builder.-get2(paramBuilder);
  }
  
  private JobInfo(Parcel paramParcel)
  {
    this.jobId = paramParcel.readInt();
    this.extras = paramParcel.readPersistableBundle();
    this.service = ((ComponentName)paramParcel.readParcelable(null));
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.requireCharging = bool1;
      if (paramParcel.readInt() != 1) {
        break label232;
      }
      bool1 = true;
      label59:
      this.requireDeviceIdle = bool1;
      this.triggerContentUris = ((TriggerContentUri[])paramParcel.createTypedArray(TriggerContentUri.CREATOR));
      this.triggerContentUpdateDelay = paramParcel.readLong();
      this.triggerContentMaxDelay = paramParcel.readLong();
      this.networkType = paramParcel.readInt();
      this.minLatencyMillis = paramParcel.readLong();
      this.maxExecutionDelayMillis = paramParcel.readLong();
      if (paramParcel.readInt() != 1) {
        break label237;
      }
      bool1 = true;
      label128:
      this.isPeriodic = bool1;
      if (paramParcel.readInt() != 1) {
        break label242;
      }
      bool1 = true;
      label143:
      this.isPersisted = bool1;
      this.intervalMillis = paramParcel.readLong();
      this.flexMillis = paramParcel.readLong();
      this.initialBackoffMillis = paramParcel.readLong();
      this.backoffPolicy = paramParcel.readInt();
      if (paramParcel.readInt() != 1) {
        break label247;
      }
      bool1 = true;
      label190:
      this.hasEarlyConstraint = bool1;
      if (paramParcel.readInt() != 1) {
        break label252;
      }
    }
    label232:
    label237:
    label242:
    label247:
    label252:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.hasLateConstraint = bool1;
      this.priority = paramParcel.readInt();
      this.flags = paramParcel.readInt();
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label59;
      bool1 = false;
      break label128;
      bool1 = false;
      break label143;
      bool1 = false;
      break label190;
    }
  }
  
  public static final long getMinFlexMillis()
  {
    return 300000L;
  }
  
  public static final long getMinPeriodMillis()
  {
    return 900000L;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getBackoffPolicy()
  {
    return this.backoffPolicy;
  }
  
  public PersistableBundle getExtras()
  {
    return this.extras;
  }
  
  public int getFlags()
  {
    return this.flags;
  }
  
  public long getFlexMillis()
  {
    long l1 = getIntervalMillis();
    long l2 = 5L * l1 / 100L;
    l2 = Math.max(this.flexMillis, Math.max(l2, getMinFlexMillis()));
    if (l2 <= l1) {
      return l2;
    }
    return l1;
  }
  
  public int getId()
  {
    return this.jobId;
  }
  
  public long getInitialBackoffMillis()
  {
    return this.initialBackoffMillis;
  }
  
  public long getIntervalMillis()
  {
    if (this.intervalMillis >= getMinPeriodMillis()) {
      return this.intervalMillis;
    }
    return getMinPeriodMillis();
  }
  
  public long getMaxExecutionDelayMillis()
  {
    return this.maxExecutionDelayMillis;
  }
  
  public long getMinLatencyMillis()
  {
    return this.minLatencyMillis;
  }
  
  public int getNetworkType()
  {
    return this.networkType;
  }
  
  public int getPriority()
  {
    return this.priority;
  }
  
  public ComponentName getService()
  {
    return this.service;
  }
  
  public long getTriggerContentMaxDelay()
  {
    return this.triggerContentMaxDelay;
  }
  
  public long getTriggerContentUpdateDelay()
  {
    return this.triggerContentUpdateDelay;
  }
  
  public TriggerContentUri[] getTriggerContentUris()
  {
    return this.triggerContentUris;
  }
  
  public boolean hasEarlyConstraint()
  {
    return this.hasEarlyConstraint;
  }
  
  public boolean hasLateConstraint()
  {
    return this.hasLateConstraint;
  }
  
  public boolean isPeriodic()
  {
    return this.isPeriodic;
  }
  
  public boolean isPersisted()
  {
    return this.isPersisted;
  }
  
  public boolean isRequireCharging()
  {
    return this.requireCharging;
  }
  
  public boolean isRequireDeviceIdle()
  {
    return this.requireDeviceIdle;
  }
  
  public String toString()
  {
    return "(job:" + this.jobId + "/" + this.service.flattenToShortString() + ")";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 1;
    paramParcel.writeInt(this.jobId);
    paramParcel.writePersistableBundle(this.extras);
    paramParcel.writeParcelable(this.service, paramInt);
    int i;
    if (this.requireCharging)
    {
      i = 1;
      paramParcel.writeInt(i);
      if (!this.requireDeviceIdle) {
        break label216;
      }
      i = 1;
      label51:
      paramParcel.writeInt(i);
      paramParcel.writeTypedArray(this.triggerContentUris, paramInt);
      paramParcel.writeLong(this.triggerContentUpdateDelay);
      paramParcel.writeLong(this.triggerContentMaxDelay);
      paramParcel.writeInt(this.networkType);
      paramParcel.writeLong(this.minLatencyMillis);
      paramParcel.writeLong(this.maxExecutionDelayMillis);
      if (!this.isPeriodic) {
        break label221;
      }
      paramInt = 1;
      label114:
      paramParcel.writeInt(paramInt);
      if (!this.isPersisted) {
        break label226;
      }
      paramInt = 1;
      label128:
      paramParcel.writeInt(paramInt);
      paramParcel.writeLong(this.intervalMillis);
      paramParcel.writeLong(this.flexMillis);
      paramParcel.writeLong(this.initialBackoffMillis);
      paramParcel.writeInt(this.backoffPolicy);
      if (!this.hasEarlyConstraint) {
        break label231;
      }
      paramInt = 1;
      label174:
      paramParcel.writeInt(paramInt);
      if (!this.hasLateConstraint) {
        break label236;
      }
    }
    label216:
    label221:
    label226:
    label231:
    label236:
    for (paramInt = j;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.priority);
      paramParcel.writeInt(this.flags);
      return;
      i = 0;
      break;
      i = 0;
      break label51;
      paramInt = 0;
      break label114;
      paramInt = 0;
      break label128;
      paramInt = 0;
      break label174;
    }
  }
  
  public static final class Builder
  {
    private int mBackoffPolicy = 1;
    private boolean mBackoffPolicySet = false;
    private PersistableBundle mExtras = PersistableBundle.EMPTY;
    private int mFlags;
    private long mFlexMillis;
    private boolean mHasEarlyConstraint;
    private boolean mHasLateConstraint;
    private long mInitialBackoffMillis = 30000L;
    private long mIntervalMillis;
    private boolean mIsPeriodic;
    private boolean mIsPersisted;
    private final int mJobId;
    private final ComponentName mJobService;
    private long mMaxExecutionDelayMillis;
    private long mMinLatencyMillis;
    private int mNetworkType;
    private int mPriority = 0;
    private boolean mRequiresCharging;
    private boolean mRequiresDeviceIdle;
    private long mTriggerContentMaxDelay = -1L;
    private long mTriggerContentUpdateDelay = -1L;
    private ArrayList<JobInfo.TriggerContentUri> mTriggerContentUris;
    
    public Builder(int paramInt, ComponentName paramComponentName)
    {
      this.mJobService = paramComponentName;
      this.mJobId = paramInt;
    }
    
    public Builder addTriggerContentUri(JobInfo.TriggerContentUri paramTriggerContentUri)
    {
      if (this.mTriggerContentUris == null) {
        this.mTriggerContentUris = new ArrayList();
      }
      this.mTriggerContentUris.add(paramTriggerContentUri);
      return this;
    }
    
    public JobInfo build()
    {
      if ((this.mHasEarlyConstraint) || (this.mHasLateConstraint)) {}
      while ((this.mRequiresCharging) || (this.mRequiresDeviceIdle) || (this.mNetworkType != 0) || (this.mTriggerContentUris != null))
      {
        this.mExtras = new PersistableBundle(this.mExtras);
        if ((!this.mIsPeriodic) || (this.mMaxExecutionDelayMillis == 0L)) {
          break;
        }
        throw new IllegalArgumentException("Can't call setOverrideDeadline() on a periodic job.");
      }
      throw new IllegalArgumentException("You're trying to build a job with no constraints, this is not allowed.");
      if ((this.mIsPeriodic) && (this.mMinLatencyMillis != 0L)) {
        throw new IllegalArgumentException("Can't call setMinimumLatency() on a periodic job");
      }
      if ((this.mIsPeriodic) && (this.mTriggerContentUris != null)) {
        throw new IllegalArgumentException("Can't call addTriggerContentUri() on a periodic job");
      }
      if ((this.mIsPersisted) && (this.mTriggerContentUris != null)) {
        throw new IllegalArgumentException("Can't call addTriggerContentUri() on a persisted job");
      }
      if ((this.mBackoffPolicySet) && (this.mRequiresDeviceIdle)) {
        throw new IllegalArgumentException("An idle mode job will not respect any back-off policy, so calling setBackoffCriteria with setRequiresDeviceIdle is an error.");
      }
      JobInfo localJobInfo = new JobInfo(this, null);
      if (localJobInfo.isPeriodic())
      {
        StringBuilder localStringBuilder;
        if (JobInfo.-get2(localJobInfo) != localJobInfo.getIntervalMillis())
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Specified interval for ").append(String.valueOf(this.mJobId)).append(" is ");
          TimeUtils.formatDuration(this.mIntervalMillis, localStringBuilder);
          localStringBuilder.append(". Clamped to ");
          TimeUtils.formatDuration(localJobInfo.getIntervalMillis(), localStringBuilder);
          Log.w(JobInfo.-get0(), localStringBuilder.toString());
        }
        if (JobInfo.-get1(localJobInfo) != localJobInfo.getFlexMillis())
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Specified flex for ").append(String.valueOf(this.mJobId)).append(" is ");
          TimeUtils.formatDuration(this.mFlexMillis, localStringBuilder);
          localStringBuilder.append(". Clamped to ");
          TimeUtils.formatDuration(localJobInfo.getFlexMillis(), localStringBuilder);
          Log.w(JobInfo.-get0(), localStringBuilder.toString());
        }
      }
      return localJobInfo;
    }
    
    public Builder setBackoffCriteria(long paramLong, int paramInt)
    {
      this.mBackoffPolicySet = true;
      this.mInitialBackoffMillis = paramLong;
      this.mBackoffPolicy = paramInt;
      return this;
    }
    
    public Builder setExtras(PersistableBundle paramPersistableBundle)
    {
      this.mExtras = paramPersistableBundle;
      return this;
    }
    
    public Builder setFlags(int paramInt)
    {
      this.mFlags = paramInt;
      return this;
    }
    
    public Builder setMinimumLatency(long paramLong)
    {
      this.mMinLatencyMillis = paramLong;
      this.mHasEarlyConstraint = true;
      return this;
    }
    
    public Builder setOverrideDeadline(long paramLong)
    {
      this.mMaxExecutionDelayMillis = paramLong;
      this.mHasLateConstraint = true;
      return this;
    }
    
    public Builder setPeriodic(long paramLong)
    {
      return setPeriodic(paramLong, paramLong);
    }
    
    public Builder setPeriodic(long paramLong1, long paramLong2)
    {
      this.mIsPeriodic = true;
      this.mIntervalMillis = paramLong1;
      this.mFlexMillis = paramLong2;
      this.mHasLateConstraint = true;
      this.mHasEarlyConstraint = true;
      return this;
    }
    
    public Builder setPersisted(boolean paramBoolean)
    {
      this.mIsPersisted = paramBoolean;
      return this;
    }
    
    public Builder setPriority(int paramInt)
    {
      this.mPriority = paramInt;
      return this;
    }
    
    public Builder setRequiredNetworkType(int paramInt)
    {
      this.mNetworkType = paramInt;
      return this;
    }
    
    public Builder setRequiresCharging(boolean paramBoolean)
    {
      this.mRequiresCharging = paramBoolean;
      return this;
    }
    
    public Builder setRequiresDeviceIdle(boolean paramBoolean)
    {
      this.mRequiresDeviceIdle = paramBoolean;
      return this;
    }
    
    public Builder setTriggerContentMaxDelay(long paramLong)
    {
      this.mTriggerContentMaxDelay = paramLong;
      return this;
    }
    
    public Builder setTriggerContentUpdateDelay(long paramLong)
    {
      this.mTriggerContentUpdateDelay = paramLong;
      return this;
    }
  }
  
  public static final class TriggerContentUri
    implements Parcelable
  {
    public static final Parcelable.Creator<TriggerContentUri> CREATOR = new Parcelable.Creator()
    {
      public JobInfo.TriggerContentUri createFromParcel(Parcel paramAnonymousParcel)
      {
        return new JobInfo.TriggerContentUri(paramAnonymousParcel, null);
      }
      
      public JobInfo.TriggerContentUri[] newArray(int paramAnonymousInt)
      {
        return new JobInfo.TriggerContentUri[paramAnonymousInt];
      }
    };
    public static final int FLAG_NOTIFY_FOR_DESCENDANTS = 1;
    private final int mFlags;
    private final Uri mUri;
    
    public TriggerContentUri(Uri paramUri, int paramInt)
    {
      this.mUri = paramUri;
      this.mFlags = paramInt;
    }
    
    private TriggerContentUri(Parcel paramParcel)
    {
      this.mUri = ((Uri)Uri.CREATOR.createFromParcel(paramParcel));
      this.mFlags = paramParcel.readInt();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof TriggerContentUri)) {
        return false;
      }
      paramObject = (TriggerContentUri)paramObject;
      boolean bool1 = bool2;
      if (Objects.equals(((TriggerContentUri)paramObject).mUri, this.mUri))
      {
        bool1 = bool2;
        if (((TriggerContentUri)paramObject).mFlags == this.mFlags) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public int getFlags()
    {
      return this.mFlags;
    }
    
    public Uri getUri()
    {
      return this.mUri;
    }
    
    public int hashCode()
    {
      if (this.mUri == null) {}
      for (int i = 0;; i = this.mUri.hashCode()) {
        return i ^ this.mFlags;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      this.mUri.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.mFlags);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/job/JobInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */