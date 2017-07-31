package com.fingerprints.extension.sensortest;

import android.os.Handler;
import android.os.RemoteException;
import com.fingerprints.extension.common.FingerprintExtensionBase;
import com.fingerprints.extension.util.Logger;
import java.util.List;

public class FingerprintSensorTest
  extends FingerprintExtensionBase
{
  private static final String SENSOR_TEST = "com.fingerprints.extension.sensortest.IFingerprintSensorTest";
  private CaptureCallback mCaptureCallback;
  private IFingerprintSensorTest mFingerprintSensorTest;
  private Handler mHandler;
  private ICaptureCallback mICaptureCallback = new ICaptureCallback.Stub()
  {
    public void onAcquired(final int paramAnonymousInt)
    {
      FingerprintSensorTest.-get1(FingerprintSensorTest.this).post(new Runnable()
      {
        public void run()
        {
          if (FingerprintSensorTest.-get0(FingerprintSensorTest.this) != null) {
            FingerprintSensorTest.-get0(FingerprintSensorTest.this).onAcquired(paramAnonymousInt);
          }
        }
      });
    }
    
    public void onError(final int paramAnonymousInt)
    {
      FingerprintSensorTest.-get1(FingerprintSensorTest.this).post(new Runnable()
      {
        public void run()
        {
          if (FingerprintSensorTest.-get0(FingerprintSensorTest.this) != null) {
            FingerprintSensorTest.-get0(FingerprintSensorTest.this).onError(paramAnonymousInt);
          }
        }
      });
    }
  };
  private ISensorTestCallback mISensorTestCallback = new ISensorTestCallback.Stub()
  {
    public void onResult(final SensorTestResult paramAnonymousSensorTestResult)
    {
      FingerprintSensorTest.-get1(FingerprintSensorTest.this).post(new Runnable()
      {
        public void run()
        {
          if (FingerprintSensorTest.-get2(FingerprintSensorTest.this) != null) {
            FingerprintSensorTest.-get2(FingerprintSensorTest.this).onResult(paramAnonymousSensorTestResult);
          }
        }
      });
    }
  };
  private Logger mLogger = new Logger(getClass().getSimpleName());
  private SensorTestCallback mSensorTestCallback;
  
  public FingerprintSensorTest()
    throws RemoteException
  {
    this.mLogger.enter("FingerprintSensorTest");
    this.mHandler = new Handler();
    this.mFingerprintSensorTest = IFingerprintSensorTest.Stub.asInterface(getFingerprintExtension("com.fingerprints.extension.sensortest.IFingerprintSensorTest"));
    if (this.mFingerprintSensorTest == null) {
      throw new RemoteException("Could not get com.fingerprints.extension.sensortest.IFingerprintSensorTest");
    }
    this.mLogger.exit("FingerprintSensorTest");
  }
  
  public void cancelCapture()
  {
    this.mLogger.enter("cancelCapture");
    if (this.mFingerprintSensorTest != null) {}
    try
    {
      this.mFingerprintSensorTest.cancelCapture();
      this.mLogger.exit("cancelCapture");
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
  
  public void cancelSensorTest()
  {
    this.mLogger.enter("cancelSensorTest");
    if (this.mFingerprintSensorTest != null) {}
    try
    {
      this.mFingerprintSensorTest.cancelSensorTest();
      this.mLogger.exit("cancelSensorTest");
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
  
  public void capture(CaptureCallback paramCaptureCallback, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mLogger.enter("capture");
    this.mCaptureCallback = paramCaptureCallback;
    if (this.mFingerprintSensorTest != null) {}
    try
    {
      this.mFingerprintSensorTest.capture(this.mICaptureCallback, paramBoolean1, paramBoolean2);
      this.mLogger.exit("capture");
      return;
    }
    catch (RemoteException paramCaptureCallback)
    {
      for (;;)
      {
        this.mLogger.e("RemoteException: ", paramCaptureCallback);
      }
    }
  }
  
  public SensorInfo getSensorInfo()
  {
    this.mLogger.enter("getSensorInfo");
    Object localObject3 = null;
    Object localObject1 = localObject3;
    if (this.mFingerprintSensorTest != null) {}
    try
    {
      localObject1 = this.mFingerprintSensorTest.getSensorInfo();
      this.mLogger.exit("getSensorInfo");
      return (SensorInfo)localObject1;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        this.mLogger.e("RemoteException: ", localRemoteException);
        Object localObject2 = localObject3;
      }
    }
  }
  
  public List<SensorTest> getSensorTests()
  {
    this.mLogger.enter("getSensorTests");
    Object localObject3 = null;
    Object localObject1 = localObject3;
    if (this.mFingerprintSensorTest != null) {}
    try
    {
      localObject1 = this.mFingerprintSensorTest.getSensorTests();
      this.mLogger.exit("getSensorTests");
      return (List<SensorTest>)localObject1;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        this.mLogger.e("getSensorTests: ", localRemoteException);
        Object localObject2 = localObject3;
      }
    }
  }
  
  public void runSensorTest(SensorTestCallback paramSensorTestCallback, SensorTest paramSensorTest, SensorTestInput paramSensorTestInput)
  {
    this.mLogger.enter("runSensorTest");
    this.mSensorTestCallback = paramSensorTestCallback;
    if (this.mFingerprintSensorTest != null) {}
    try
    {
      this.mFingerprintSensorTest.runSensorTest(this.mISensorTestCallback, paramSensorTest, paramSensorTestInput);
      this.mLogger.exit("runSensorTest");
      return;
    }
    catch (RemoteException paramSensorTestCallback)
    {
      for (;;)
      {
        this.mLogger.e("RemoteException: ", paramSensorTestCallback);
      }
    }
  }
  
  public static abstract interface CaptureCallback
  {
    public abstract void onAcquired(int paramInt);
    
    public abstract void onError(int paramInt);
  }
  
  public static abstract interface SensorTestCallback
  {
    public abstract void onResult(SensorTestResult paramSensorTestResult);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/sensortest/FingerprintSensorTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */