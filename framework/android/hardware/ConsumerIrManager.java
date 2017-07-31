package android.hardware;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public final class ConsumerIrManager
{
  private static final String TAG = "ConsumerIr";
  private final String mPackageName;
  private final IConsumerIrService mService;
  
  public ConsumerIrManager(Context paramContext)
  {
    this.mPackageName = paramContext.getPackageName();
    this.mService = IConsumerIrService.Stub.asInterface(ServiceManager.getService("consumer_ir"));
  }
  
  public CarrierFrequencyRange[] getCarrierFrequencies()
  {
    if (this.mService == null)
    {
      Log.w("ConsumerIr", "no consumer ir service.");
      return null;
    }
    try
    {
      int[] arrayOfInt = this.mService.getCarrierFrequencies();
      if (arrayOfInt.length % 2 != 0)
      {
        Log.w("ConsumerIr", "consumer ir service returned an uneven number of frequencies.");
        return null;
      }
      CarrierFrequencyRange[] arrayOfCarrierFrequencyRange = new CarrierFrequencyRange[arrayOfInt.length / 2];
      int i = 0;
      while (i < arrayOfInt.length)
      {
        arrayOfCarrierFrequencyRange[(i / 2)] = new CarrierFrequencyRange(arrayOfInt[i], arrayOfInt[(i + 1)]);
        i += 2;
      }
      return arrayOfCarrierFrequencyRange;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean hasIrEmitter()
  {
    if (this.mService == null)
    {
      Log.w("ConsumerIr", "no consumer ir service.");
      return false;
    }
    try
    {
      boolean bool = this.mService.hasIrEmitter();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void transmit(int paramInt, int[] paramArrayOfInt)
  {
    if (this.mService == null)
    {
      Log.w("ConsumerIr", "failed to transmit; no consumer ir service.");
      return;
    }
    try
    {
      this.mService.transmit(this.mPackageName, paramInt, paramArrayOfInt);
      return;
    }
    catch (RemoteException paramArrayOfInt)
    {
      throw paramArrayOfInt.rethrowFromSystemServer();
    }
  }
  
  public final class CarrierFrequencyRange
  {
    private final int mMaxFrequency;
    private final int mMinFrequency;
    
    public CarrierFrequencyRange(int paramInt1, int paramInt2)
    {
      this.mMinFrequency = paramInt1;
      this.mMaxFrequency = paramInt2;
    }
    
    public int getMaxFrequency()
    {
      return this.mMaxFrequency;
    }
    
    public int getMinFrequency()
    {
      return this.mMinFrequency;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/ConsumerIrManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */