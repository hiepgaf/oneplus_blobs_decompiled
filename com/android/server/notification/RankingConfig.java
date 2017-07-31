package com.android.server.notification;

public abstract interface RankingConfig
{
  public abstract int getImportance(String paramString, int paramInt);
  
  public abstract int getPriority(String paramString, int paramInt);
  
  public abstract int getVisibilityOverride(String paramString, int paramInt);
  
  public abstract void setImportance(String paramString, int paramInt1, int paramInt2);
  
  public abstract void setPriority(String paramString, int paramInt1, int paramInt2);
  
  public abstract void setVisibilityOverride(String paramString, int paramInt1, int paramInt2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/RankingConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */