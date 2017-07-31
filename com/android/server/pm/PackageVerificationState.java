package com.android.server.pm;

import android.util.SparseBooleanArray;

class PackageVerificationState
{
  private final PackageManagerService.InstallArgs mArgs;
  private boolean mExtendedTimeout;
  private boolean mHasOptionalVerifier;
  private boolean mOptionalVerificationComplete;
  private boolean mOptionalVerificationPassed;
  private int mOptionalVerifierUid;
  private boolean mRequiredVerificationComplete;
  private boolean mRequiredVerificationPassed;
  private final int mRequiredVerifierUid;
  private boolean mSufficientVerificationComplete;
  private boolean mSufficientVerificationPassed;
  private final SparseBooleanArray mSufficientVerifierUids;
  
  public PackageVerificationState(int paramInt, PackageManagerService.InstallArgs paramInstallArgs)
  {
    this.mRequiredVerifierUid = paramInt;
    this.mArgs = paramInstallArgs;
    this.mSufficientVerifierUids = new SparseBooleanArray();
    this.mExtendedTimeout = false;
  }
  
  public void addOptionalVerifier(int paramInt)
  {
    this.mOptionalVerifierUid = paramInt;
    this.mHasOptionalVerifier = true;
  }
  
  public void addSufficientVerifier(int paramInt)
  {
    this.mSufficientVerifierUids.put(paramInt, true);
  }
  
  public void extendTimeout()
  {
    if (!this.mExtendedTimeout) {
      this.mExtendedTimeout = true;
    }
  }
  
  public PackageManagerService.InstallArgs getInstallArgs()
  {
    return this.mArgs;
  }
  
  public boolean isInstallAllowed()
  {
    if (!this.mRequiredVerificationPassed) {
      return false;
    }
    if ((!this.mHasOptionalVerifier) || (this.mOptionalVerificationPassed))
    {
      if (this.mSufficientVerificationComplete) {
        return this.mSufficientVerificationPassed;
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  public boolean isVerificationComplete()
  {
    if (!this.mRequiredVerificationComplete) {
      return false;
    }
    if ((!this.mHasOptionalVerifier) || (this.mOptionalVerificationComplete))
    {
      if (this.mSufficientVerifierUids.size() == 0) {
        return true;
      }
    }
    else {
      return false;
    }
    return this.mSufficientVerificationComplete;
  }
  
  public boolean setVerifierResponse(int paramInt1, int paramInt2)
  {
    if (paramInt1 == this.mRequiredVerifierUid)
    {
      this.mRequiredVerificationComplete = true;
      switch (paramInt2)
      {
      default: 
        this.mRequiredVerificationPassed = false;
        return true;
      case 2: 
        this.mSufficientVerifierUids.clear();
      }
      this.mRequiredVerificationPassed = true;
      return true;
    }
    if ((this.mHasOptionalVerifier) && (paramInt1 == this.mOptionalVerifierUid))
    {
      this.mOptionalVerificationComplete = true;
      switch (paramInt2)
      {
      default: 
        this.mOptionalVerificationPassed = false;
        return true;
      }
      this.mOptionalVerificationPassed = true;
      return true;
    }
    if (this.mSufficientVerifierUids.get(paramInt1))
    {
      if (paramInt2 == 1)
      {
        this.mSufficientVerificationComplete = true;
        this.mSufficientVerificationPassed = true;
      }
      this.mSufficientVerifierUids.delete(paramInt1);
      if (this.mSufficientVerifierUids.size() == 0) {
        this.mSufficientVerificationComplete = true;
      }
      return true;
    }
    return false;
  }
  
  public boolean timeoutExtended()
  {
    return this.mExtendedTimeout;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageVerificationState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */