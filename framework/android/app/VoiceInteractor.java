package android.app;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.DebugUtils;
import android.util.Log;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.IVoiceInteractorCallback;
import com.android.internal.app.IVoiceInteractorCallback.Stub;
import com.android.internal.app.IVoiceInteractorRequest;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.os.SomeArgs;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public final class VoiceInteractor
{
  static final boolean DEBUG = false;
  static final int MSG_ABORT_VOICE_RESULT = 4;
  static final int MSG_CANCEL_RESULT = 6;
  static final int MSG_COMMAND_RESULT = 5;
  static final int MSG_COMPLETE_VOICE_RESULT = 3;
  static final int MSG_CONFIRMATION_RESULT = 1;
  static final int MSG_PICK_OPTION_RESULT = 2;
  static final Request[] NO_REQUESTS = new Request[0];
  static final String TAG = "VoiceInteractor";
  final ArrayMap<IBinder, Request> mActiveRequests = new ArrayMap();
  Activity mActivity;
  final IVoiceInteractorCallback.Stub mCallback = new IVoiceInteractorCallback.Stub()
  {
    public void deliverAbortVoiceResult(IVoiceInteractorRequest paramAnonymousIVoiceInteractorRequest, Bundle paramAnonymousBundle)
    {
      VoiceInteractor.this.mHandlerCaller.sendMessage(VoiceInteractor.this.mHandlerCaller.obtainMessageOO(4, paramAnonymousIVoiceInteractorRequest, paramAnonymousBundle));
    }
    
    public void deliverCancel(IVoiceInteractorRequest paramAnonymousIVoiceInteractorRequest)
      throws RemoteException
    {
      VoiceInteractor.this.mHandlerCaller.sendMessage(VoiceInteractor.this.mHandlerCaller.obtainMessageOO(6, paramAnonymousIVoiceInteractorRequest, null));
    }
    
    public void deliverCommandResult(IVoiceInteractorRequest paramAnonymousIVoiceInteractorRequest, boolean paramAnonymousBoolean, Bundle paramAnonymousBundle)
    {
      HandlerCaller localHandlerCaller1 = VoiceInteractor.this.mHandlerCaller;
      HandlerCaller localHandlerCaller2 = VoiceInteractor.this.mHandlerCaller;
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandlerCaller1.sendMessage(localHandlerCaller2.obtainMessageIOO(5, i, paramAnonymousIVoiceInteractorRequest, paramAnonymousBundle));
        return;
      }
    }
    
    public void deliverCompleteVoiceResult(IVoiceInteractorRequest paramAnonymousIVoiceInteractorRequest, Bundle paramAnonymousBundle)
    {
      VoiceInteractor.this.mHandlerCaller.sendMessage(VoiceInteractor.this.mHandlerCaller.obtainMessageOO(3, paramAnonymousIVoiceInteractorRequest, paramAnonymousBundle));
    }
    
    public void deliverConfirmationResult(IVoiceInteractorRequest paramAnonymousIVoiceInteractorRequest, boolean paramAnonymousBoolean, Bundle paramAnonymousBundle)
    {
      HandlerCaller localHandlerCaller1 = VoiceInteractor.this.mHandlerCaller;
      HandlerCaller localHandlerCaller2 = VoiceInteractor.this.mHandlerCaller;
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandlerCaller1.sendMessage(localHandlerCaller2.obtainMessageIOO(1, i, paramAnonymousIVoiceInteractorRequest, paramAnonymousBundle));
        return;
      }
    }
    
    public void deliverPickOptionResult(IVoiceInteractorRequest paramAnonymousIVoiceInteractorRequest, boolean paramAnonymousBoolean, VoiceInteractor.PickOptionRequest.Option[] paramAnonymousArrayOfOption, Bundle paramAnonymousBundle)
    {
      HandlerCaller localHandlerCaller1 = VoiceInteractor.this.mHandlerCaller;
      HandlerCaller localHandlerCaller2 = VoiceInteractor.this.mHandlerCaller;
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandlerCaller1.sendMessage(localHandlerCaller2.obtainMessageIOOO(2, i, paramAnonymousIVoiceInteractorRequest, paramAnonymousArrayOfOption, paramAnonymousBundle));
        return;
      }
    }
  };
  Context mContext;
  final HandlerCaller mHandlerCaller;
  final HandlerCaller.Callback mHandlerCallerCallback = new HandlerCaller.Callback()
  {
    public void executeMessage(Message paramAnonymousMessage)
    {
      boolean bool2 = false;
      boolean bool1 = false;
      SomeArgs localSomeArgs = (SomeArgs)paramAnonymousMessage.obj;
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        VoiceInteractor.Request localRequest;
        Object localObject;
        do
        {
          do
          {
            do
            {
              return;
              localRequest = VoiceInteractor.this.pullRequest((IVoiceInteractorRequest)localSomeArgs.arg1, true);
            } while (localRequest == null);
            localObject = (VoiceInteractor.ConfirmationRequest)localRequest;
            if (paramAnonymousMessage.arg1 != 0) {
              bool1 = true;
            }
            ((VoiceInteractor.ConfirmationRequest)localObject).onConfirmationResult(bool1, (Bundle)localSomeArgs.arg2);
            localRequest.clear();
            return;
            if (paramAnonymousMessage.arg1 != 0) {}
            for (bool1 = true;; bool1 = false)
            {
              paramAnonymousMessage = VoiceInteractor.this.pullRequest((IVoiceInteractorRequest)localSomeArgs.arg1, bool1);
              if (paramAnonymousMessage == null) {
                break;
              }
              ((VoiceInteractor.PickOptionRequest)paramAnonymousMessage).onPickOptionResult(bool1, (VoiceInteractor.PickOptionRequest.Option[])localSomeArgs.arg2, (Bundle)localSomeArgs.arg3);
              if (!bool1) {
                break;
              }
              paramAnonymousMessage.clear();
              return;
            }
            paramAnonymousMessage = VoiceInteractor.this.pullRequest((IVoiceInteractorRequest)localSomeArgs.arg1, true);
          } while (paramAnonymousMessage == null);
          ((VoiceInteractor.CompleteVoiceRequest)paramAnonymousMessage).onCompleteResult((Bundle)localSomeArgs.arg2);
          paramAnonymousMessage.clear();
          return;
          paramAnonymousMessage = VoiceInteractor.this.pullRequest((IVoiceInteractorRequest)localSomeArgs.arg1, true);
        } while (paramAnonymousMessage == null);
        ((VoiceInteractor.AbortVoiceRequest)paramAnonymousMessage).onAbortResult((Bundle)localSomeArgs.arg2);
        paramAnonymousMessage.clear();
        return;
        if (paramAnonymousMessage.arg1 != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          localRequest = VoiceInteractor.this.pullRequest((IVoiceInteractorRequest)localSomeArgs.arg1, bool1);
          if (localRequest == null) {
            break;
          }
          localObject = (VoiceInteractor.CommandRequest)localRequest;
          if (paramAnonymousMessage.arg1 != 0) {
            bool2 = true;
          }
          ((VoiceInteractor.CommandRequest)localObject).onCommandResult(bool2, (Bundle)localSomeArgs.arg2);
          if (!bool1) {
            break;
          }
          localRequest.clear();
          return;
        }
        paramAnonymousMessage = VoiceInteractor.this.pullRequest((IVoiceInteractorRequest)localSomeArgs.arg1, true);
      } while (paramAnonymousMessage == null);
      paramAnonymousMessage.onCancel();
      paramAnonymousMessage.clear();
    }
  };
  final IVoiceInteractor mInteractor;
  boolean mRetaining;
  
  VoiceInteractor(IVoiceInteractor paramIVoiceInteractor, Context paramContext, Activity paramActivity, Looper paramLooper)
  {
    this.mInteractor = paramIVoiceInteractor;
    this.mContext = paramContext;
    this.mActivity = paramActivity;
    this.mHandlerCaller = new HandlerCaller(paramContext, paramLooper, this.mHandlerCallerCallback, true);
  }
  
  private ArrayList<Request> makeRequestList()
  {
    int j = this.mActiveRequests.size();
    if (j < 1) {
      return null;
    }
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      localArrayList.add((Request)this.mActiveRequests.valueAt(i));
      i += 1;
    }
    return localArrayList;
  }
  
  void attachActivity(Activity paramActivity)
  {
    this.mRetaining = false;
    if (this.mActivity == paramActivity) {
      return;
    }
    this.mContext = paramActivity;
    this.mActivity = paramActivity;
    ArrayList localArrayList = makeRequestList();
    if (localArrayList != null)
    {
      int i = 0;
      while (i < localArrayList.size())
      {
        Request localRequest = (Request)localArrayList.get(i);
        localRequest.mContext = paramActivity;
        localRequest.mActivity = paramActivity;
        localRequest.onAttached(paramActivity);
        i += 1;
      }
    }
  }
  
  void detachActivity()
  {
    ArrayList localArrayList = makeRequestList();
    int i;
    if (localArrayList != null)
    {
      i = 0;
      while (i < localArrayList.size())
      {
        Request localRequest = (Request)localArrayList.get(i);
        localRequest.onDetached();
        localRequest.mActivity = null;
        localRequest.mContext = null;
        i += 1;
      }
    }
    if (!this.mRetaining)
    {
      localArrayList = makeRequestList();
      if (localArrayList != null)
      {
        i = 0;
        while (i < localArrayList.size())
        {
          ((Request)localArrayList.get(i)).cancel();
          i += 1;
        }
      }
      this.mActiveRequests.clear();
    }
    this.mContext = null;
    this.mActivity = null;
  }
  
  void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = paramString + "    ";
    if (this.mActiveRequests.size() > 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Active voice requests:");
      int i = 0;
      while (i < this.mActiveRequests.size())
      {
        Request localRequest = (Request)this.mActiveRequests.valueAt(i);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("  #");
        paramPrintWriter.print(i);
        paramPrintWriter.print(": ");
        paramPrintWriter.println(localRequest);
        localRequest.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        i += 1;
      }
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("VoiceInteractor misc state:");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mInteractor=");
    paramPrintWriter.println(this.mInteractor.asBinder());
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mActivity=");
    paramPrintWriter.println(this.mActivity);
  }
  
  public Request getActiveRequest(String paramString)
  {
    synchronized (this.mActiveRequests)
    {
      int j = this.mActiveRequests.size();
      int i = 0;
      while (i < j)
      {
        Request localRequest = (Request)this.mActiveRequests.valueAt(i);
        if (paramString != localRequest.getName())
        {
          if (paramString != null)
          {
            boolean bool = paramString.equals(localRequest.getName());
            if (!bool) {}
          }
        }
        else {
          return localRequest;
        }
        i += 1;
      }
      return null;
    }
  }
  
  public Request[] getActiveRequests()
  {
    synchronized (this.mActiveRequests)
    {
      int j = this.mActiveRequests.size();
      if (j <= 0)
      {
        arrayOfRequest = NO_REQUESTS;
        return arrayOfRequest;
      }
      Request[] arrayOfRequest = new Request[j];
      int i = 0;
      while (i < j)
      {
        arrayOfRequest[i] = ((Request)this.mActiveRequests.valueAt(i));
        i += 1;
      }
      return arrayOfRequest;
    }
  }
  
  Request pullRequest(IVoiceInteractorRequest paramIVoiceInteractorRequest, boolean paramBoolean)
  {
    synchronized (this.mActiveRequests)
    {
      Request localRequest = (Request)this.mActiveRequests.get(paramIVoiceInteractorRequest.asBinder());
      if ((localRequest != null) && (paramBoolean)) {
        this.mActiveRequests.remove(paramIVoiceInteractorRequest.asBinder());
      }
      return localRequest;
    }
  }
  
  void retainInstance()
  {
    this.mRetaining = true;
  }
  
  public boolean submitRequest(Request paramRequest)
  {
    return submitRequest(paramRequest, null);
  }
  
  public boolean submitRequest(Request paramRequest, String arg2)
  {
    try
    {
      if (paramRequest.mRequestInterface != null) {
        throw new IllegalStateException("Given " + paramRequest + " is already active");
      }
    }
    catch (RemoteException paramRequest)
    {
      Log.w("VoiceInteractor", "Remove voice interactor service died", paramRequest);
      return false;
    }
    IVoiceInteractorRequest localIVoiceInteractorRequest = paramRequest.submit(this.mInteractor, this.mContext.getOpPackageName(), this.mCallback);
    paramRequest.mRequestInterface = localIVoiceInteractorRequest;
    paramRequest.mContext = this.mContext;
    paramRequest.mActivity = this.mActivity;
    paramRequest.mName = ???;
    synchronized (this.mActiveRequests)
    {
      this.mActiveRequests.put(localIVoiceInteractorRequest.asBinder(), paramRequest);
      return true;
    }
  }
  
  public boolean[] supportsCommands(String[] paramArrayOfString)
  {
    try
    {
      paramArrayOfString = this.mInteractor.supportsCommands(this.mContext.getOpPackageName(), paramArrayOfString);
      return paramArrayOfString;
    }
    catch (RemoteException paramArrayOfString)
    {
      throw new RuntimeException("Voice interactor has died", paramArrayOfString);
    }
  }
  
  public static class AbortVoiceRequest
    extends VoiceInteractor.Request
  {
    final Bundle mExtras;
    final VoiceInteractor.Prompt mPrompt;
    
    public AbortVoiceRequest(VoiceInteractor.Prompt paramPrompt, Bundle paramBundle)
    {
      this.mPrompt = paramPrompt;
      this.mExtras = paramBundle;
    }
    
    public AbortVoiceRequest(CharSequence paramCharSequence, Bundle paramBundle)
    {
      if (paramCharSequence != null) {
        localPrompt = new VoiceInteractor.Prompt(paramCharSequence);
      }
      this.mPrompt = localPrompt;
      this.mExtras = paramBundle;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPrompt=");
      paramPrintWriter.println(this.mPrompt);
      if (this.mExtras != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mExtras=");
        paramPrintWriter.println(this.mExtras);
      }
    }
    
    String getRequestTypeName()
    {
      return "AbortVoice";
    }
    
    public void onAbortResult(Bundle paramBundle) {}
    
    IVoiceInteractorRequest submit(IVoiceInteractor paramIVoiceInteractor, String paramString, IVoiceInteractorCallback paramIVoiceInteractorCallback)
      throws RemoteException
    {
      return paramIVoiceInteractor.startAbortVoice(paramString, paramIVoiceInteractorCallback, this.mPrompt, this.mExtras);
    }
  }
  
  public static class CommandRequest
    extends VoiceInteractor.Request
  {
    final Bundle mArgs;
    final String mCommand;
    
    public CommandRequest(String paramString, Bundle paramBundle)
    {
      this.mCommand = paramString;
      this.mArgs = paramBundle;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCommand=");
      paramPrintWriter.println(this.mCommand);
      if (this.mArgs != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mArgs=");
        paramPrintWriter.println(this.mArgs);
      }
    }
    
    String getRequestTypeName()
    {
      return "Command";
    }
    
    public void onCommandResult(boolean paramBoolean, Bundle paramBundle) {}
    
    IVoiceInteractorRequest submit(IVoiceInteractor paramIVoiceInteractor, String paramString, IVoiceInteractorCallback paramIVoiceInteractorCallback)
      throws RemoteException
    {
      return paramIVoiceInteractor.startCommand(paramString, paramIVoiceInteractorCallback, this.mCommand, this.mArgs);
    }
  }
  
  public static class CompleteVoiceRequest
    extends VoiceInteractor.Request
  {
    final Bundle mExtras;
    final VoiceInteractor.Prompt mPrompt;
    
    public CompleteVoiceRequest(VoiceInteractor.Prompt paramPrompt, Bundle paramBundle)
    {
      this.mPrompt = paramPrompt;
      this.mExtras = paramBundle;
    }
    
    public CompleteVoiceRequest(CharSequence paramCharSequence, Bundle paramBundle)
    {
      if (paramCharSequence != null) {
        localPrompt = new VoiceInteractor.Prompt(paramCharSequence);
      }
      this.mPrompt = localPrompt;
      this.mExtras = paramBundle;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPrompt=");
      paramPrintWriter.println(this.mPrompt);
      if (this.mExtras != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mExtras=");
        paramPrintWriter.println(this.mExtras);
      }
    }
    
    String getRequestTypeName()
    {
      return "CompleteVoice";
    }
    
    public void onCompleteResult(Bundle paramBundle) {}
    
    IVoiceInteractorRequest submit(IVoiceInteractor paramIVoiceInteractor, String paramString, IVoiceInteractorCallback paramIVoiceInteractorCallback)
      throws RemoteException
    {
      return paramIVoiceInteractor.startCompleteVoice(paramString, paramIVoiceInteractorCallback, this.mPrompt, this.mExtras);
    }
  }
  
  public static class ConfirmationRequest
    extends VoiceInteractor.Request
  {
    final Bundle mExtras;
    final VoiceInteractor.Prompt mPrompt;
    
    public ConfirmationRequest(VoiceInteractor.Prompt paramPrompt, Bundle paramBundle)
    {
      this.mPrompt = paramPrompt;
      this.mExtras = paramBundle;
    }
    
    public ConfirmationRequest(CharSequence paramCharSequence, Bundle paramBundle)
    {
      if (paramCharSequence != null) {
        localPrompt = new VoiceInteractor.Prompt(paramCharSequence);
      }
      this.mPrompt = localPrompt;
      this.mExtras = paramBundle;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPrompt=");
      paramPrintWriter.println(this.mPrompt);
      if (this.mExtras != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mExtras=");
        paramPrintWriter.println(this.mExtras);
      }
    }
    
    String getRequestTypeName()
    {
      return "Confirmation";
    }
    
    public void onConfirmationResult(boolean paramBoolean, Bundle paramBundle) {}
    
    IVoiceInteractorRequest submit(IVoiceInteractor paramIVoiceInteractor, String paramString, IVoiceInteractorCallback paramIVoiceInteractorCallback)
      throws RemoteException
    {
      return paramIVoiceInteractor.startConfirmation(paramString, paramIVoiceInteractorCallback, this.mPrompt, this.mExtras);
    }
  }
  
  public static class PickOptionRequest
    extends VoiceInteractor.Request
  {
    final Bundle mExtras;
    final Option[] mOptions;
    final VoiceInteractor.Prompt mPrompt;
    
    public PickOptionRequest(VoiceInteractor.Prompt paramPrompt, Option[] paramArrayOfOption, Bundle paramBundle)
    {
      this.mPrompt = paramPrompt;
      this.mOptions = paramArrayOfOption;
      this.mExtras = paramBundle;
    }
    
    public PickOptionRequest(CharSequence paramCharSequence, Option[] paramArrayOfOption, Bundle paramBundle)
    {
      if (paramCharSequence != null) {
        localPrompt = new VoiceInteractor.Prompt(paramCharSequence);
      }
      this.mPrompt = localPrompt;
      this.mOptions = paramArrayOfOption;
      this.mExtras = paramBundle;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mPrompt=");
      paramPrintWriter.println(this.mPrompt);
      if (this.mOptions != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Options:");
        int i = 0;
        while (i < this.mOptions.length)
        {
          paramFileDescriptor = this.mOptions[i];
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i);
          paramPrintWriter.println(":");
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    mLabel=");
          paramPrintWriter.println(paramFileDescriptor.mLabel);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("    mIndex=");
          paramPrintWriter.println(paramFileDescriptor.mIndex);
          if ((paramFileDescriptor.mSynonyms != null) && (paramFileDescriptor.mSynonyms.size() > 0))
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.println("    Synonyms:");
            int j = 0;
            while (j < paramFileDescriptor.mSynonyms.size())
            {
              paramPrintWriter.print(paramString);
              paramPrintWriter.print("      #");
              paramPrintWriter.print(j);
              paramPrintWriter.print(": ");
              paramPrintWriter.println(paramFileDescriptor.mSynonyms.get(j));
              j += 1;
            }
          }
          if (paramFileDescriptor.mExtras != null)
          {
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("    mExtras=");
            paramPrintWriter.println(paramFileDescriptor.mExtras);
          }
          i += 1;
        }
      }
      if (this.mExtras != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mExtras=");
        paramPrintWriter.println(this.mExtras);
      }
    }
    
    String getRequestTypeName()
    {
      return "PickOption";
    }
    
    public void onPickOptionResult(boolean paramBoolean, Option[] paramArrayOfOption, Bundle paramBundle) {}
    
    IVoiceInteractorRequest submit(IVoiceInteractor paramIVoiceInteractor, String paramString, IVoiceInteractorCallback paramIVoiceInteractorCallback)
      throws RemoteException
    {
      return paramIVoiceInteractor.startPickOption(paramString, paramIVoiceInteractorCallback, this.mPrompt, this.mOptions, this.mExtras);
    }
    
    public static final class Option
      implements Parcelable
    {
      public static final Parcelable.Creator<Option> CREATOR = new Parcelable.Creator()
      {
        public VoiceInteractor.PickOptionRequest.Option createFromParcel(Parcel paramAnonymousParcel)
        {
          return new VoiceInteractor.PickOptionRequest.Option(paramAnonymousParcel);
        }
        
        public VoiceInteractor.PickOptionRequest.Option[] newArray(int paramAnonymousInt)
        {
          return new VoiceInteractor.PickOptionRequest.Option[paramAnonymousInt];
        }
      };
      Bundle mExtras;
      final int mIndex;
      final CharSequence mLabel;
      ArrayList<CharSequence> mSynonyms;
      
      Option(Parcel paramParcel)
      {
        this.mLabel = paramParcel.readCharSequence();
        this.mIndex = paramParcel.readInt();
        this.mSynonyms = paramParcel.readCharSequenceList();
        this.mExtras = paramParcel.readBundle();
      }
      
      public Option(CharSequence paramCharSequence)
      {
        this.mLabel = paramCharSequence;
        this.mIndex = -1;
      }
      
      public Option(CharSequence paramCharSequence, int paramInt)
      {
        this.mLabel = paramCharSequence;
        this.mIndex = paramInt;
      }
      
      public Option addSynonym(CharSequence paramCharSequence)
      {
        if (this.mSynonyms == null) {
          this.mSynonyms = new ArrayList();
        }
        this.mSynonyms.add(paramCharSequence);
        return this;
      }
      
      public int countSynonyms()
      {
        if (this.mSynonyms != null) {
          return this.mSynonyms.size();
        }
        return 0;
      }
      
      public int describeContents()
      {
        return 0;
      }
      
      public Bundle getExtras()
      {
        return this.mExtras;
      }
      
      public int getIndex()
      {
        return this.mIndex;
      }
      
      public CharSequence getLabel()
      {
        return this.mLabel;
      }
      
      public CharSequence getSynonymAt(int paramInt)
      {
        CharSequence localCharSequence = null;
        if (this.mSynonyms != null) {
          localCharSequence = (CharSequence)this.mSynonyms.get(paramInt);
        }
        return localCharSequence;
      }
      
      public void setExtras(Bundle paramBundle)
      {
        this.mExtras = paramBundle;
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        paramParcel.writeCharSequence(this.mLabel);
        paramParcel.writeInt(this.mIndex);
        paramParcel.writeCharSequenceList(this.mSynonyms);
        paramParcel.writeBundle(this.mExtras);
      }
    }
  }
  
  public static class Prompt
    implements Parcelable
  {
    public static final Parcelable.Creator<Prompt> CREATOR = new Parcelable.Creator()
    {
      public VoiceInteractor.Prompt createFromParcel(Parcel paramAnonymousParcel)
      {
        return new VoiceInteractor.Prompt(paramAnonymousParcel);
      }
      
      public VoiceInteractor.Prompt[] newArray(int paramAnonymousInt)
      {
        return new VoiceInteractor.Prompt[paramAnonymousInt];
      }
    };
    private final CharSequence mVisualPrompt;
    private final CharSequence[] mVoicePrompts;
    
    Prompt(Parcel paramParcel)
    {
      this.mVoicePrompts = paramParcel.readCharSequenceArray();
      this.mVisualPrompt = paramParcel.readCharSequence();
    }
    
    public Prompt(CharSequence paramCharSequence)
    {
      this.mVoicePrompts = new CharSequence[] { paramCharSequence };
      this.mVisualPrompt = paramCharSequence;
    }
    
    public Prompt(CharSequence[] paramArrayOfCharSequence, CharSequence paramCharSequence)
    {
      if (paramArrayOfCharSequence == null) {
        throw new NullPointerException("voicePrompts must not be null");
      }
      if (paramArrayOfCharSequence.length == 0) {
        throw new IllegalArgumentException("voicePrompts must not be empty");
      }
      if (paramCharSequence == null) {
        throw new NullPointerException("visualPrompt must not be null");
      }
      this.mVoicePrompts = paramArrayOfCharSequence;
      this.mVisualPrompt = paramCharSequence;
    }
    
    public int countVoicePrompts()
    {
      return this.mVoicePrompts.length;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public CharSequence getVisualPrompt()
    {
      return this.mVisualPrompt;
    }
    
    public CharSequence getVoicePromptAt(int paramInt)
    {
      return this.mVoicePrompts[paramInt];
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      DebugUtils.buildShortClassTag(this, localStringBuilder);
      if ((this.mVisualPrompt != null) && (this.mVoicePrompts != null) && (this.mVoicePrompts.length == 1) && (this.mVisualPrompt.equals(this.mVoicePrompts[0])))
      {
        localStringBuilder.append(" ");
        localStringBuilder.append(this.mVisualPrompt);
      }
      for (;;)
      {
        localStringBuilder.append('}');
        return localStringBuilder.toString();
        if (this.mVisualPrompt != null)
        {
          localStringBuilder.append(" visual=");
          localStringBuilder.append(this.mVisualPrompt);
        }
        if (this.mVoicePrompts != null)
        {
          localStringBuilder.append(", voice=");
          int i = 0;
          while (i < this.mVoicePrompts.length)
          {
            if (i > 0) {
              localStringBuilder.append(" | ");
            }
            localStringBuilder.append(this.mVoicePrompts[i]);
            i += 1;
          }
        }
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeCharSequenceArray(this.mVoicePrompts);
      paramParcel.writeCharSequence(this.mVisualPrompt);
    }
  }
  
  public static abstract class Request
  {
    Activity mActivity;
    Context mContext;
    String mName;
    IVoiceInteractorRequest mRequestInterface;
    
    public void cancel()
    {
      if (this.mRequestInterface == null) {
        throw new IllegalStateException("Request " + this + " is no longer active");
      }
      try
      {
        this.mRequestInterface.cancel();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("VoiceInteractor", "Voice interactor has died", localRemoteException);
      }
    }
    
    void clear()
    {
      this.mRequestInterface = null;
      this.mContext = null;
      this.mActivity = null;
      this.mName = null;
    }
    
    void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mRequestInterface=");
      paramPrintWriter.println(this.mRequestInterface.asBinder());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mActivity=");
      paramPrintWriter.println(this.mActivity);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mName=");
      paramPrintWriter.println(this.mName);
    }
    
    public Activity getActivity()
    {
      return this.mActivity;
    }
    
    public Context getContext()
    {
      return this.mContext;
    }
    
    public String getName()
    {
      return this.mName;
    }
    
    String getRequestTypeName()
    {
      return "Request";
    }
    
    public void onAttached(Activity paramActivity) {}
    
    public void onCancel() {}
    
    public void onDetached() {}
    
    abstract IVoiceInteractorRequest submit(IVoiceInteractor paramIVoiceInteractor, String paramString, IVoiceInteractorCallback paramIVoiceInteractorCallback)
      throws RemoteException;
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      DebugUtils.buildShortClassTag(this, localStringBuilder);
      localStringBuilder.append(" ");
      localStringBuilder.append(getRequestTypeName());
      localStringBuilder.append(" name=");
      localStringBuilder.append(this.mName);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/VoiceInteractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */