package android.printservice;

import android.graphics.drawable.Icon;
import android.os.RemoteException;
import android.print.PrinterId;
import android.util.Log;

public final class CustomPrinterIconCallback
{
  private static final String LOG_TAG = "CustomPrinterIconCB";
  private final IPrintServiceClient mObserver;
  private final PrinterId mPrinterId;
  
  CustomPrinterIconCallback(PrinterId paramPrinterId, IPrintServiceClient paramIPrintServiceClient)
  {
    this.mPrinterId = paramPrinterId;
    this.mObserver = paramIPrintServiceClient;
  }
  
  public boolean onCustomPrinterIconLoaded(Icon paramIcon)
  {
    try
    {
      this.mObserver.onCustomPrinterIconLoaded(this.mPrinterId, paramIcon);
      return true;
    }
    catch (RemoteException paramIcon)
    {
      Log.e("CustomPrinterIconCB", "Could not update icon", paramIcon);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/CustomPrinterIconCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */