package android.bluetooth;

import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanResult;
import android.os.RemoteException;
import java.util.List;

public class BluetoothGattCallbackWrapper
  extends IBluetoothGattCallback.Stub
{
  public void onBatchScanResults(List<ScanResult> paramList)
    throws RemoteException
  {}
  
  public void onCharacteristicRead(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    throws RemoteException
  {}
  
  public void onCharacteristicWrite(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {}
  
  public void onClientConnectionState(int paramInt1, int paramInt2, boolean paramBoolean, String paramString)
    throws RemoteException
  {}
  
  public void onClientRegistered(int paramInt1, int paramInt2)
    throws RemoteException
  {}
  
  public void onConfigureMTU(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {}
  
  public void onDescriptorRead(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    throws RemoteException
  {}
  
  public void onDescriptorWrite(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {}
  
  public void onExecuteWrite(String paramString, int paramInt)
    throws RemoteException
  {}
  
  public void onFoundOrLost(boolean paramBoolean, ScanResult paramScanResult)
    throws RemoteException
  {}
  
  public void onMultiAdvertiseCallback(int paramInt, boolean paramBoolean, AdvertiseSettings paramAdvertiseSettings)
    throws RemoteException
  {}
  
  public void onNotify(String paramString, int paramInt, byte[] paramArrayOfByte)
    throws RemoteException
  {}
  
  public void onReadRemoteRssi(String paramString, int paramInt1, int paramInt2)
    throws RemoteException
  {}
  
  public void onScanManagerErrorCallback(int paramInt)
    throws RemoteException
  {}
  
  public void onScanResult(ScanResult paramScanResult)
    throws RemoteException
  {}
  
  public void onSearchComplete(String paramString, List<BluetoothGattService> paramList, int paramInt)
    throws RemoteException
  {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothGattCallbackWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */