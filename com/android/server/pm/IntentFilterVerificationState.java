package com.android.server.pm;

import android.content.pm.PackageParser.ActivityIntentInfo;
import android.util.ArraySet;
import android.util.Slog;
import java.util.ArrayList;

public class IntentFilterVerificationState
{
  public static final int STATE_UNDEFINED = 0;
  public static final int STATE_VERIFICATION_FAILURE = 3;
  public static final int STATE_VERIFICATION_PENDING = 1;
  public static final int STATE_VERIFICATION_SUCCESS = 2;
  static final String TAG = IntentFilterVerificationState.class.getName();
  private ArrayList<PackageParser.ActivityIntentInfo> mFilters = new ArrayList();
  private ArraySet<String> mHosts = new ArraySet();
  private String mPackageName;
  private int mRequiredVerifierUid = 0;
  private int mState;
  private int mUserId;
  private boolean mVerificationComplete;
  
  public IntentFilterVerificationState(int paramInt1, int paramInt2, String paramString)
  {
    this.mRequiredVerifierUid = paramInt1;
    this.mUserId = paramInt2;
    this.mPackageName = paramString;
    this.mState = 0;
    this.mVerificationComplete = false;
  }
  
  public void addFilter(PackageParser.ActivityIntentInfo paramActivityIntentInfo)
  {
    this.mFilters.add(paramActivityIntentInfo);
    this.mHosts.addAll(paramActivityIntentInfo.getHostsList());
  }
  
  public ArrayList<PackageParser.ActivityIntentInfo> getFilters()
  {
    return this.mFilters;
  }
  
  public String getHostsString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int j = this.mHosts.size();
    int i = 0;
    while (i < j)
    {
      if (i > 0) {
        localStringBuilder.append(" ");
      }
      String str2 = (String)this.mHosts.valueAt(i);
      String str1 = str2;
      if (str2.startsWith("*.")) {
        str1 = str2.substring(2);
      }
      localStringBuilder.append(str1);
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public boolean isVerificationComplete()
  {
    return this.mVerificationComplete;
  }
  
  public boolean isVerified()
  {
    boolean bool = false;
    if (this.mVerificationComplete)
    {
      if (this.mState == 2) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public void setPendingState()
  {
    setState(1);
  }
  
  public void setState(int paramInt)
  {
    if ((paramInt > 3) || (paramInt < 0))
    {
      this.mState = 0;
      return;
    }
    this.mState = paramInt;
  }
  
  public boolean setVerifierResponse(int paramInt1, int paramInt2)
  {
    if (this.mRequiredVerifierUid == paramInt1)
    {
      paramInt1 = 0;
      if (paramInt2 == 1) {
        paramInt1 = 2;
      }
      for (;;)
      {
        this.mVerificationComplete = true;
        setState(paramInt1);
        return true;
        if (paramInt2 == -1) {
          paramInt1 = 3;
        }
      }
    }
    Slog.d(TAG, "Cannot set verifier response with callerUid:" + paramInt1 + " and code:" + paramInt2 + " as required verifierUid is:" + this.mRequiredVerifierUid);
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/IntentFilterVerificationState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */