package com.fingerprints.extension.authenticator;

import android.os.Handler;
import android.os.RemoteException;
import com.fingerprints.extension.common.FingerprintExtensionBase;
import com.fingerprints.extension.util.Logger;

public class FingerprintAuthenticator
  extends FingerprintExtensionBase
{
  private static final String AUTHENTICATION = "com.fingerprints.extension.authenticator.IFingerprintAuthenticator";
  private IFingerprintAuthenticator mFingerprintAuthenticator;
  private Handler mHandler;
  private IVerifyUserCallback mIVerifyUserCallback = new IVerifyUserCallback.Stub()
  {
    public void onHelp(final int paramAnonymousInt)
    {
      FingerprintAuthenticator.-get0(FingerprintAuthenticator.this).post(new Runnable()
      {
        public void run()
        {
          if (FingerprintAuthenticator.-get1(FingerprintAuthenticator.this) != null) {
            FingerprintAuthenticator.-get1(FingerprintAuthenticator.this).onHelp(paramAnonymousInt);
          }
        }
      });
    }
    
    public void onResult(final int paramAnonymousInt, final long paramAnonymousLong1, long paramAnonymousLong2, final byte[] paramAnonymousArrayOfByte)
    {
      FingerprintAuthenticator.-get0(FingerprintAuthenticator.this).post(new Runnable()
      {
        public void run()
        {
          if (FingerprintAuthenticator.-get1(FingerprintAuthenticator.this) != null) {
            FingerprintAuthenticator.-get1(FingerprintAuthenticator.this).onResult(paramAnonymousInt, paramAnonymousLong1, paramAnonymousArrayOfByte, this.val$encapsulatedResult);
          }
        }
      });
    }
  };
  private Logger mLogger = new Logger(getClass().getSimpleName());
  private VerifyUserCallback mVerifyUserCallback;
  
  public FingerprintAuthenticator()
    throws RemoteException
  {
    this.mLogger.enter("FingerprintAuthenticator");
    this.mHandler = new Handler();
    this.mFingerprintAuthenticator = IFingerprintAuthenticator.Stub.asInterface(getFingerprintExtension("com.fingerprints.extension.authenticator.IFingerprintAuthenticator"));
    if (this.mFingerprintAuthenticator == null) {
      throw new RemoteException("Could not get com.fingerprints.extension.authenticator.IFingerprintAuthenticator");
    }
    this.mLogger.exit("FingerprintAuthenticator");
  }
  
  public void cancel()
  {
    this.mLogger.enter("cancel");
    if (this.mFingerprintAuthenticator != null) {}
    try
    {
      this.mFingerprintAuthenticator.cancel();
      this.mLogger.exit("cancel");
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        this.mLogger.e("RemoteException: ", localRemoteException);
      }
    }
  }
  
  public boolean isUserValid(long paramLong)
  {
    this.mLogger.enter("isUserValid");
    bool2 = false;
    bool1 = bool2;
    if (this.mFingerprintAuthenticator != null) {}
    for (;;)
    {
      try
      {
        int i = this.mFingerprintAuthenticator.isUserValid(paramLong);
        if (i == 0) {
          continue;
        }
        bool1 = true;
      }
      catch (RemoteException localRemoteException)
      {
        this.mLogger.e("RemoteException: ", localRemoteException);
        bool1 = bool2;
        continue;
      }
      this.mLogger.exit("isUserValid");
      return bool1;
      bool1 = false;
    }
  }
  
  public int verifyUser(VerifyUserCallback paramVerifyUserCallback, byte[] paramArrayOfByte, String paramString)
  {
    this.mLogger.enter("verifyUser");
    this.mVerifyUserCallback = paramVerifyUserCallback;
    int j = 0;
    int i = j;
    if (this.mFingerprintAuthenticator != null) {}
    try
    {
      i = this.mFingerprintAuthenticator.verifyUser(this.mIVerifyUserCallback, paramArrayOfByte, paramString.getBytes());
      this.mLogger.exit("verifyUser");
      return i;
    }
    catch (RemoteException paramVerifyUserCallback)
    {
      for (;;)
      {
        this.mLogger.e("RemoteException: ", paramVerifyUserCallback);
        i = j;
      }
    }
  }
  
  public static abstract interface VerifyUserCallback
  {
    public abstract void onHelp(int paramInt);
    
    public abstract void onResult(int paramInt, long paramLong1, long paramLong2, byte[] paramArrayOfByte);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/authenticator/FingerprintAuthenticator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */