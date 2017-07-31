package com.android.server.am;

import android.os.Binder;
import android.os.SystemClock;
import android.util.TimeUtils;

public final class ContentProviderConnection
  extends Binder
{
  public final ProcessRecord client;
  public final long createTime;
  public boolean dead;
  public int numStableIncs;
  public int numUnstableIncs;
  public final ContentProviderRecord provider;
  public int stableCount;
  public int unstableCount;
  public boolean waiting;
  
  public ContentProviderConnection(ContentProviderRecord paramContentProviderRecord, ProcessRecord paramProcessRecord)
  {
    this.provider = paramContentProviderRecord;
    this.client = paramProcessRecord;
    this.createTime = SystemClock.elapsedRealtime();
  }
  
  public String toClientString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    toClientString(localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public void toClientString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(this.client.toShortString());
    paramStringBuilder.append(" s");
    paramStringBuilder.append(this.stableCount);
    paramStringBuilder.append("/");
    paramStringBuilder.append(this.numStableIncs);
    paramStringBuilder.append(" u");
    paramStringBuilder.append(this.unstableCount);
    paramStringBuilder.append("/");
    paramStringBuilder.append(this.numUnstableIncs);
    if (this.waiting) {
      paramStringBuilder.append(" WAITING");
    }
    if (this.dead) {
      paramStringBuilder.append(" DEAD");
    }
    long l = SystemClock.elapsedRealtime();
    paramStringBuilder.append(" ");
    TimeUtils.formatDuration(l - this.createTime, paramStringBuilder);
  }
  
  public String toShortString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    toShortString(localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public void toShortString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(this.provider.toShortString());
    paramStringBuilder.append("->");
    toClientString(paramStringBuilder);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("ContentProviderConnection{");
    toShortString(localStringBuilder);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ContentProviderConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */