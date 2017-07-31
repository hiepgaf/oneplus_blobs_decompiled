package android.printservice;

import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.print.PrintDocumentInfo;
import android.print.PrintJobId;
import android.util.Log;
import java.io.IOException;

public final class PrintDocument
{
  private static final String LOG_TAG = "PrintDocument";
  private final PrintDocumentInfo mInfo;
  private final PrintJobId mPrintJobId;
  private final IPrintServiceClient mPrintServiceClient;
  
  PrintDocument(PrintJobId paramPrintJobId, IPrintServiceClient paramIPrintServiceClient, PrintDocumentInfo paramPrintDocumentInfo)
  {
    this.mPrintJobId = paramPrintJobId;
    this.mPrintServiceClient = paramIPrintServiceClient;
    this.mInfo = paramPrintDocumentInfo;
  }
  
  public ParcelFileDescriptor getData()
  {
    PrintService.throwIfNotCalledOnMainThread();
    Object localObject5 = null;
    Object localObject1 = null;
    Object localObject3 = null;
    try
    {
      Object localObject6 = ParcelFileDescriptor.createPipe();
      ParcelFileDescriptor localParcelFileDescriptor = localObject6[0];
      localObject6 = localObject6[1];
      localObject3 = localObject6;
      localObject5 = localObject6;
      localObject1 = localObject6;
      this.mPrintServiceClient.writePrintJobData((ParcelFileDescriptor)localObject6, this.mPrintJobId);
      if (localObject6 != null) {}
      try
      {
        ((ParcelFileDescriptor)localObject6).close();
        return localParcelFileDescriptor;
      }
      catch (IOException localIOException1)
      {
        return localParcelFileDescriptor;
      }
      try
      {
        Object localObject2;
        RemoteException localRemoteException1;
        localIOException3.close();
        throw ((Throwable)localObject4);
      }
      catch (IOException localIOException4)
      {
        for (;;) {}
      }
    }
    catch (RemoteException localRemoteException2)
    {
      localObject2 = localObject3;
      Log.e("PrintDocument", "Error calling getting print job data!", localRemoteException2);
      if (localObject3 != null) {}
      try
      {
        ((ParcelFileDescriptor)localObject3).close();
        return null;
      }
      catch (IOException localIOException2)
      {
        return null;
      }
    }
    catch (IOException localIOException5)
    {
      do
      {
        localRemoteException1 = localRemoteException2;
        Log.e("PrintDocument", "Error calling getting print job data!", localIOException5);
      } while (localRemoteException2 == null);
      try
      {
        localRemoteException2.close();
        return null;
      }
      catch (IOException localIOException3)
      {
        return null;
      }
    }
    finally
    {
      if (localIOException3 == null) {}
    }
  }
  
  public PrintDocumentInfo getInfo()
  {
    PrintService.throwIfNotCalledOnMainThread();
    return this.mInfo;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/printservice/PrintDocument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */