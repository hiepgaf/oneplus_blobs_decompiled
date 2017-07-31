package com.android.server.notification;

public abstract interface RankingHandler
{
  public abstract void requestReconsideration(RankingReconsideration paramRankingReconsideration);
  
  public abstract void requestSort();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/RankingHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */