package com.android.settings;

import android.accounts.Account;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.Log;
import android.widget.ImageView;
import java.util.ArrayList;

public class AccountPreference
  extends Preference
{
  public static final int SYNC_DISABLED = 1;
  public static final int SYNC_ENABLED = 0;
  public static final int SYNC_ERROR = 2;
  public static final int SYNC_IN_PROGRESS = 3;
  private static final String TAG = "AccountPreference";
  private Account mAccount;
  private ArrayList<String> mAuthorities;
  private boolean mShowTypeIcon;
  private int mStatus;
  private ImageView mSyncStatusIcon;
  
  public AccountPreference(Context paramContext, Account paramAccount, Drawable paramDrawable, ArrayList<String> paramArrayList, boolean paramBoolean)
  {
    super(paramContext);
    this.mAccount = paramAccount;
    this.mAuthorities = paramArrayList;
    this.mShowTypeIcon = paramBoolean;
    if (paramBoolean) {
      setIcon(paramDrawable);
    }
    for (;;)
    {
      setTitle(this.mAccount.name);
      setSummary("");
      setPersistent(false);
      setSyncStatus(1, false);
      return;
      setIcon(getSyncStatusIcon(1));
    }
  }
  
  private String getSyncContentDescription(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.e("AccountPreference", "Unknown sync status: " + paramInt);
      return getContext().getString(2131627056);
    case 0: 
      return getContext().getString(2131627053);
    case 1: 
      return getContext().getString(2131627054);
    case 2: 
      return getContext().getString(2131627056);
    }
    return getContext().getString(2131627055);
  }
  
  private int getSyncStatusIcon(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.e("AccountPreference", "Unknown sync status: " + paramInt);
      return 2130838008;
    case 0: 
    case 3: 
      return 2130837857;
    case 1: 
      return 2130838007;
    }
    return 2130838008;
  }
  
  private int getSyncStatusMessage(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.e("AccountPreference", "Unknown sync status: " + paramInt);
      return 2131627073;
    case 0: 
      return 2131627071;
    case 1: 
      return 2131627072;
    case 2: 
      return 2131627073;
    }
    return 2131627075;
  }
  
  public Account getAccount()
  {
    return this.mAccount;
  }
  
  public ArrayList<String> getAuthorities()
  {
    return this.mAuthorities;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (!this.mShowTypeIcon)
    {
      this.mSyncStatusIcon = ((ImageView)paramPreferenceViewHolder.findViewById(16908294));
      this.mSyncStatusIcon.setImageResource(getSyncStatusIcon(this.mStatus));
      this.mSyncStatusIcon.setContentDescription(getSyncContentDescription(this.mStatus));
    }
  }
  
  public void setSyncStatus(int paramInt, boolean paramBoolean)
  {
    this.mStatus = paramInt;
    if ((!this.mShowTypeIcon) && (this.mSyncStatusIcon != null))
    {
      this.mSyncStatusIcon.setImageResource(getSyncStatusIcon(paramInt));
      this.mSyncStatusIcon.setContentDescription(getSyncContentDescription(this.mStatus));
    }
    if (paramBoolean) {
      setSummary(getSyncStatusMessage(paramInt));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/settings/AccountPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */