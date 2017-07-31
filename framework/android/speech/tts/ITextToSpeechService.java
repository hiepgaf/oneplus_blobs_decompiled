package android.speech.tts;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public abstract interface ITextToSpeechService
  extends IInterface
{
  public abstract String[] getClientDefaultLanguage()
    throws RemoteException;
  
  public abstract String getDefaultVoiceNameFor(String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract String[] getFeaturesForLanguage(String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract String[] getLanguage()
    throws RemoteException;
  
  public abstract List<Voice> getVoices()
    throws RemoteException;
  
  public abstract int isLanguageAvailable(String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract boolean isSpeaking()
    throws RemoteException;
  
  public abstract int loadLanguage(IBinder paramIBinder, String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract int loadVoice(IBinder paramIBinder, String paramString)
    throws RemoteException;
  
  public abstract int playAudio(IBinder paramIBinder, Uri paramUri, int paramInt, Bundle paramBundle, String paramString)
    throws RemoteException;
  
  public abstract int playSilence(IBinder paramIBinder, long paramLong, int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setCallback(IBinder paramIBinder, ITextToSpeechCallback paramITextToSpeechCallback)
    throws RemoteException;
  
  public abstract int speak(IBinder paramIBinder, CharSequence paramCharSequence, int paramInt, Bundle paramBundle, String paramString)
    throws RemoteException;
  
  public abstract int stop(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int synthesizeToFileDescriptor(IBinder paramIBinder, CharSequence paramCharSequence, ParcelFileDescriptor paramParcelFileDescriptor, Bundle paramBundle, String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITextToSpeechService
  {
    private static final String DESCRIPTOR = "android.speech.tts.ITextToSpeechService";
    static final int TRANSACTION_getClientDefaultLanguage = 8;
    static final int TRANSACTION_getDefaultVoiceNameFor = 15;
    static final int TRANSACTION_getFeaturesForLanguage = 10;
    static final int TRANSACTION_getLanguage = 7;
    static final int TRANSACTION_getVoices = 13;
    static final int TRANSACTION_isLanguageAvailable = 9;
    static final int TRANSACTION_isSpeaking = 5;
    static final int TRANSACTION_loadLanguage = 11;
    static final int TRANSACTION_loadVoice = 14;
    static final int TRANSACTION_playAudio = 3;
    static final int TRANSACTION_playSilence = 4;
    static final int TRANSACTION_setCallback = 12;
    static final int TRANSACTION_speak = 1;
    static final int TRANSACTION_stop = 6;
    static final int TRANSACTION_synthesizeToFileDescriptor = 2;
    
    public Stub()
    {
      attachInterface(this, "android.speech.tts.ITextToSpeechService");
    }
    
    public static ITextToSpeechService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.speech.tts.ITextToSpeechService");
      if ((localIInterface != null) && ((localIInterface instanceof ITextToSpeechService))) {
        return (ITextToSpeechService)localIInterface;
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
      Object localObject3;
      Object localObject1;
      Object localObject2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.speech.tts.ITextToSpeechService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        localObject3 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label250;
          }
        }
        for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          paramInt1 = speak((IBinder)localObject3, (CharSequence)localObject1, paramInt1, (Bundle)localObject2, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label365;
          }
          localObject2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label371;
          }
        }
        for (localObject3 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject3 = null)
        {
          paramInt1 = synthesizeToFileDescriptor(localIBinder, (CharSequence)localObject1, (ParcelFileDescriptor)localObject2, (Bundle)localObject3, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label310;
        }
      case 3: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        localObject3 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label469;
          }
        }
        for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          paramInt1 = playAudio((IBinder)localObject3, (Uri)localObject1, paramInt1, (Bundle)localObject2, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramInt1 = playSilence(paramParcel1.readStrongBinder(), paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        boolean bool = isSpeaking();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramInt1 = stop(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramParcel1 = getLanguage();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramParcel1 = getClientDefaultLanguage();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramInt1 = isLanguageAvailable(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramParcel1 = getFeaturesForLanguage(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramInt1 = loadLanguage(paramParcel1.readStrongBinder(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        setCallback(paramParcel1.readStrongBinder(), ITextToSpeechCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramParcel1 = getVoices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 14: 
        label250:
        label310:
        label365:
        label371:
        label469:
        paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
        paramInt1 = loadVoice(paramParcel1.readStrongBinder(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.speech.tts.ITextToSpeechService");
      paramParcel1 = getDefaultVoiceNameFor(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements ITextToSpeechService
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
      
      public String[] getClientDefaultLanguage()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getDefaultVoiceNameFor(String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = localParcel2.readString();
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String[] getFeaturesForLanguage(String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = localParcel2.createStringArray();
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.speech.tts.ITextToSpeechService";
      }
      
      public String[] getLanguage()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<Voice> getVoices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(Voice.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int isLanguageAvailable(String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isSpeaking()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/speech/tts/ITextToSpeechService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_5
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 44 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 47	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 87	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 53	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 53	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 53	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 53	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      public int loadLanguage(IBinder paramIBinder, String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int loadVoice(IBinder paramIBinder, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeString(paramString);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int playAudio(IBinder paramIBinder, Uri paramUri, int paramInt, Bundle paramBundle, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
            localParcel1.writeStrongBinder(paramIBinder);
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                this.mRemote.transact(3, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int playSilence(IBinder paramIBinder, long paramLong, int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setCallback(IBinder paramIBinder, ITextToSpeechCallback paramITextToSpeechCallback)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          localParcel1.writeStrongBinder(paramIBinder);
          paramIBinder = (IBinder)localObject;
          if (paramITextToSpeechCallback != null) {
            paramIBinder = paramITextToSpeechCallback.asBinder();
          }
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int speak(IBinder paramIBinder, CharSequence paramCharSequence, int paramInt, Bundle paramBundle, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
            localParcel1.writeStrongBinder(paramIBinder);
            if (paramCharSequence != null)
            {
              localParcel1.writeInt(1);
              TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                this.mRemote.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int stop(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int synthesizeToFileDescriptor(IBinder paramIBinder, CharSequence paramCharSequence, ParcelFileDescriptor paramParcelFileDescriptor, Bundle paramBundle, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.speech.tts.ITextToSpeechService");
            localParcel1.writeStrongBinder(paramIBinder);
            if (paramCharSequence != null)
            {
              localParcel1.writeInt(1);
              TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
              if (paramParcelFileDescriptor != null)
              {
                localParcel1.writeInt(1);
                paramParcelFileDescriptor.writeToParcel(localParcel1, 0);
                if (paramBundle == null) {
                  break label155;
                }
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                this.mRemote.transact(2, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                return i;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label155:
          localParcel1.writeInt(0);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/ITextToSpeechService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */