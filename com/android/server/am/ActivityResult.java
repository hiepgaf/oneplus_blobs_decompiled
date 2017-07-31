package com.android.server.am;

import android.app.ResultInfo;
import android.content.Intent;

final class ActivityResult
  extends ResultInfo
{
  final ActivityRecord mFrom;
  
  public ActivityResult(ActivityRecord paramActivityRecord, String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    super(paramString, paramInt1, paramInt2, paramIntent);
    this.mFrom = paramActivityRecord;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/ActivityResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */