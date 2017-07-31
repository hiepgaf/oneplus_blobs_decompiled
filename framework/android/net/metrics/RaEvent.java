package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class RaEvent
  implements Parcelable
{
  public static final Parcelable.Creator<RaEvent> CREATOR = new Parcelable.Creator()
  {
    public RaEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RaEvent(paramAnonymousParcel, null);
    }
    
    public RaEvent[] newArray(int paramAnonymousInt)
    {
      return new RaEvent[paramAnonymousInt];
    }
  };
  public static final long NO_LIFETIME = -1L;
  public final long dnsslLifetime;
  public final long prefixPreferredLifetime;
  public final long prefixValidLifetime;
  public final long rdnssLifetime;
  public final long routeInfoLifetime;
  public final long routerLifetime;
  
  public RaEvent(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6)
  {
    this.routerLifetime = paramLong1;
    this.prefixValidLifetime = paramLong2;
    this.prefixPreferredLifetime = paramLong3;
    this.routeInfoLifetime = paramLong4;
    this.rdnssLifetime = paramLong5;
    this.dnsslLifetime = paramLong6;
  }
  
  private RaEvent(Parcel paramParcel)
  {
    this.routerLifetime = paramParcel.readLong();
    this.prefixValidLifetime = paramParcel.readLong();
    this.prefixPreferredLifetime = paramParcel.readLong();
    this.routeInfoLifetime = paramParcel.readLong();
    this.rdnssLifetime = paramParcel.readLong();
    this.dnsslLifetime = paramParcel.readLong();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return "RaEvent(lifetimes: " + String.format("router=%ds, ", new Object[] { Long.valueOf(this.routerLifetime) }) + String.format("prefix_valid=%ds, ", new Object[] { Long.valueOf(this.prefixValidLifetime) }) + String.format("prefix_preferred=%ds, ", new Object[] { Long.valueOf(this.prefixPreferredLifetime) }) + String.format("route_info=%ds, ", new Object[] { Long.valueOf(this.routeInfoLifetime) }) + String.format("rdnss=%ds, ", new Object[] { Long.valueOf(this.rdnssLifetime) }) + String.format("dnssl=%ds)", new Object[] { Long.valueOf(this.dnsslLifetime) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.routerLifetime);
    paramParcel.writeLong(this.prefixValidLifetime);
    paramParcel.writeLong(this.prefixPreferredLifetime);
    paramParcel.writeLong(this.routeInfoLifetime);
    paramParcel.writeLong(this.rdnssLifetime);
    paramParcel.writeLong(this.dnsslLifetime);
  }
  
  public static class Builder
  {
    long dnsslLifetime = -1L;
    long prefixPreferredLifetime = -1L;
    long prefixValidLifetime = -1L;
    long rdnssLifetime = -1L;
    long routeInfoLifetime = -1L;
    long routerLifetime = -1L;
    
    private long updateLifetime(long paramLong1, long paramLong2)
    {
      if (paramLong1 == -1L) {
        return paramLong2;
      }
      return Math.min(paramLong1, paramLong2);
    }
    
    public RaEvent build()
    {
      return new RaEvent(this.routerLifetime, this.prefixValidLifetime, this.prefixPreferredLifetime, this.routeInfoLifetime, this.rdnssLifetime, this.dnsslLifetime);
    }
    
    public Builder updateDnsslLifetime(long paramLong)
    {
      this.dnsslLifetime = updateLifetime(this.dnsslLifetime, paramLong);
      return this;
    }
    
    public Builder updatePrefixPreferredLifetime(long paramLong)
    {
      this.prefixPreferredLifetime = updateLifetime(this.prefixPreferredLifetime, paramLong);
      return this;
    }
    
    public Builder updatePrefixValidLifetime(long paramLong)
    {
      this.prefixValidLifetime = updateLifetime(this.prefixValidLifetime, paramLong);
      return this;
    }
    
    public Builder updateRdnssLifetime(long paramLong)
    {
      this.rdnssLifetime = updateLifetime(this.rdnssLifetime, paramLong);
      return this;
    }
    
    public Builder updateRouteInfoLifetime(long paramLong)
    {
      this.routeInfoLifetime = updateLifetime(this.routeInfoLifetime, paramLong);
      return this;
    }
    
    public Builder updateRouterLifetime(long paramLong)
    {
      this.routerLifetime = updateLifetime(this.routerLifetime, paramLong);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/RaEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */