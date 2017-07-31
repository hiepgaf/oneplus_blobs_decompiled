package com.android.server.wm;

import android.content.res.CompatibilityInfo;

final class StartingData
{
  final CompatibilityInfo compatInfo;
  final int icon;
  final int labelRes;
  final int logo;
  final CharSequence nonLocalizedLabel;
  final String pkg;
  final int theme;
  final int windowFlags;
  
  StartingData(String paramString, int paramInt1, CompatibilityInfo paramCompatibilityInfo, CharSequence paramCharSequence, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.pkg = paramString;
    this.theme = paramInt1;
    this.compatInfo = paramCompatibilityInfo;
    this.nonLocalizedLabel = paramCharSequence;
    this.labelRes = paramInt2;
    this.icon = paramInt3;
    this.logo = paramInt4;
    this.windowFlags = paramInt5;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/StartingData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */