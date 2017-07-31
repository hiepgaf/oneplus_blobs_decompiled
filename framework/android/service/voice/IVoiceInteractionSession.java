package android.service.voice;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractionSessionShowCallback.Stub;

public abstract interface IVoiceInteractionSession
  extends IInterface
{
  public abstract void closeSystemDialogs()
    throws RemoteException;
  
  public abstract void destroy()
    throws RemoteException;
  
  public abstract void handleAssist(Bundle paramBundle, AssistStructure paramAssistStructure, AssistContent paramAssistContent, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void handleScreenshot(Bitmap paramBitmap)
    throws RemoteException;
  
  public abstract void hide()
    throws RemoteException;
  
  public abstract void onLockscreenShown()
    throws RemoteException;
  
  public abstract void show(Bundle paramBundle, int paramInt, IVoiceInteractionSessionShowCallback paramIVoiceInteractionSessionShowCallback)
    throws RemoteException;
  
  public abstract void taskFinished(Intent paramIntent, int paramInt)
    throws RemoteException;
  
  public abstract void taskStarted(Intent paramIntent, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IVoiceInteractionSession
  {
    private static final String DESCRIPTOR = "android.service.voice.IVoiceInteractionSession";
    static final int TRANSACTION_closeSystemDialogs = 7;
    static final int TRANSACTION_destroy = 9;
    static final int TRANSACTION_handleAssist = 3;
    static final int TRANSACTION_handleScreenshot = 4;
    static final int TRANSACTION_hide = 2;
    static final int TRANSACTION_onLockscreenShown = 8;
    static final int TRANSACTION_show = 1;
    static final int TRANSACTION_taskFinished = 6;
    static final int TRANSACTION_taskStarted = 5;
    
    public Stub()
    {
      attachInterface(this, "android.service.voice.IVoiceInteractionSession");
    }
    
    public static IVoiceInteractionSession asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.voice.IVoiceInteractionSession");
      if ((localIInterface != null) && ((localIInterface instanceof IVoiceInteractionSession))) {
        return (IVoiceInteractionSession)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.voice.IVoiceInteractionSession");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          show(paramParcel2, paramParcel1.readInt(), IVoiceInteractionSessionShowCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
        hide();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
        AssistStructure localAssistStructure;
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label263;
          }
          localAssistStructure = (AssistStructure)AssistStructure.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label269;
          }
        }
        for (AssistContent localAssistContent = (AssistContent)AssistContent.CREATOR.createFromParcel(paramParcel1);; localAssistContent = null)
        {
          handleAssist(paramParcel2, localAssistStructure, localAssistContent, paramParcel1.readInt(), paramParcel1.readInt());
          return true;
          paramParcel2 = null;
          break;
          localAssistStructure = null;
          break label218;
        }
      case 4: 
        paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          handleScreenshot(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          taskStarted(paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          taskFinished(paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
        closeSystemDialogs();
        return true;
      case 8: 
        label218:
        label263:
        label269:
        paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
        onLockscreenShown();
        return true;
      }
      paramParcel1.enforceInterface("android.service.voice.IVoiceInteractionSession");
      destroy();
      return true;
    }
    
    private static class Proxy
      implements IVoiceInteractionSession
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void closeSystemDialogs()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.voice.IVoiceInteractionSession");
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void destroy()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.voice.IVoiceInteractionSession");
          this.mRemote.transact(9, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.voice.IVoiceInteractionSession";
      }
      
      public void handleAssist(Bundle paramBundle, AssistStructure paramAssistStructure, AssistContent paramAssistContent, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.service.voice.IVoiceInteractionSession");
            if (paramBundle != null)
            {
              localParcel.writeInt(1);
              paramBundle.writeToParcel(localParcel, 0);
              if (paramAssistStructure != null)
              {
                localParcel.writeInt(1);
                paramAssistStructure.writeToParcel(localParcel, 0);
                if (paramAssistContent == null) {
                  break label124;
                }
                localParcel.writeInt(1);
                paramAssistContent.writeToParcel(localParcel, 0);
                localParcel.writeInt(paramInt1);
                localParcel.writeInt(paramInt2);
                this.mRemote.transact(3, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
          continue;
          label124:
          localParcel.writeInt(0);
        }
      }
      
      /* Error */
      public void handleScreenshot(Bitmap paramBitmap)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 73	android/graphics/Bitmap:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/service/voice/IVoiceInteractionSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_4
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 43 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 46	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 46	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramBitmap	Bitmap
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void hide()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.voice.IVoiceInteractionSession");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onLockscreenShown()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.voice.IVoiceInteractionSession");
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void show(Bundle paramBundle, int paramInt, IVoiceInteractionSessionShowCallback paramIVoiceInteractionSessionShowCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +63 -> 79
        //   19: aload 5
        //   21: iconst_1
        //   22: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 5
        //   28: iconst_0
        //   29: invokevirtual 62	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: aload 5
        //   34: iload_2
        //   35: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   38: aload 4
        //   40: astore_1
        //   41: aload_3
        //   42: ifnull +10 -> 52
        //   45: aload_3
        //   46: invokeinterface 81 1 0
        //   51: astore_1
        //   52: aload 5
        //   54: aload_1
        //   55: invokevirtual 84	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   58: aload_0
        //   59: getfield 19	android/service/voice/IVoiceInteractionSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   62: iconst_1
        //   63: aload 5
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 43 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 46	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 5
        //   81: iconst_0
        //   82: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   85: goto -53 -> 32
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 46	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramBundle	Bundle
        //   0	96	2	paramInt	int
        //   0	96	3	paramIVoiceInteractionSessionShowCallback	IVoiceInteractionSessionShowCallback
        //   1	38	4	localObject	Object
        //   6	84	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	88	finally
        //   19	32	88	finally
        //   32	38	88	finally
        //   45	52	88	finally
        //   52	73	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void taskFinished(Intent paramIntent, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +39 -> 50
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 89	android/content/Intent:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/service/voice/IVoiceInteractionSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 6
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 43 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 46	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   55: goto -30 -> 25
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 46	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramIntent	Intent
        //   0	65	2	paramInt	int
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	58	finally
        //   14	25	58	finally
        //   25	45	58	finally
        //   50	55	58	finally
      }
      
      /* Error */
      public void taskStarted(Intent paramIntent, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 33
        //   7: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 89	android/content/Intent:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/service/voice/IVoiceInteractionSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_5
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 43 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 46	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 46	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramIntent	Intent
        //   0	64	2	paramInt	int
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/voice/IVoiceInteractionSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */