package android.inputmethodservice;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputMethodSession;
import android.view.inputmethod.InputMethodSession.EventCallback;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.os.SomeArgs;
import com.android.internal.view.IInputMethodSession.Stub;

class IInputMethodSessionWrapper
  extends IInputMethodSession.Stub
  implements HandlerCaller.Callback
{
  private static final int DO_APP_PRIVATE_COMMAND = 100;
  private static final int DO_DISPLAY_COMPLETIONS = 65;
  private static final int DO_FINISH_INPUT = 60;
  private static final int DO_FINISH_SESSION = 110;
  private static final int DO_TOGGLE_SOFT_INPUT = 105;
  private static final int DO_UPDATE_CURSOR = 95;
  private static final int DO_UPDATE_CURSOR_ANCHOR_INFO = 99;
  private static final int DO_UPDATE_EXTRACTED_TEXT = 67;
  private static final int DO_UPDATE_SELECTION = 90;
  private static final int DO_VIEW_CLICKED = 115;
  private static final String TAG = "InputMethodWrapper";
  HandlerCaller mCaller = new HandlerCaller(paramContext, null, this, true);
  InputChannel mChannel;
  InputMethodSession mInputMethodSession;
  ImeInputEventReceiver mReceiver;
  
  public IInputMethodSessionWrapper(Context paramContext, InputMethodSession paramInputMethodSession, InputChannel paramInputChannel)
  {
    this.mInputMethodSession = paramInputMethodSession;
    this.mChannel = paramInputChannel;
    if (paramInputChannel != null) {
      this.mReceiver = new ImeInputEventReceiver(paramInputChannel, paramContext.getMainLooper());
    }
  }
  
  private void doFinishSession()
  {
    this.mInputMethodSession = null;
    if (this.mReceiver != null)
    {
      this.mReceiver.dispose();
      this.mReceiver = null;
    }
    if (this.mChannel != null)
    {
      this.mChannel.dispose();
      this.mChannel = null;
    }
  }
  
  public void appPrivateCommand(String paramString, Bundle paramBundle)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(100, paramString, paramBundle));
  }
  
  public void displayCompletions(CompletionInfo[] paramArrayOfCompletionInfo)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(65, paramArrayOfCompletionInfo));
  }
  
  public void executeMessage(Message paramMessage)
  {
    boolean bool = true;
    if (this.mInputMethodSession == null)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      ((SomeArgs)paramMessage.obj).recycle();
      return;
    }
    switch (paramMessage.what)
    {
    default: 
      Log.w("InputMethodWrapper", "Unhandled message code: " + paramMessage.what);
      return;
    case 60: 
      this.mInputMethodSession.finishInput();
      return;
    case 65: 
      this.mInputMethodSession.displayCompletions((CompletionInfo[])paramMessage.obj);
      return;
    case 67: 
      this.mInputMethodSession.updateExtractedText(paramMessage.arg1, (ExtractedText)paramMessage.obj);
      return;
    case 90: 
      paramMessage = (SomeArgs)paramMessage.obj;
      this.mInputMethodSession.updateSelection(paramMessage.argi1, paramMessage.argi2, paramMessage.argi3, paramMessage.argi4, paramMessage.argi5, paramMessage.argi6);
      paramMessage.recycle();
      return;
    case 95: 
      this.mInputMethodSession.updateCursor((Rect)paramMessage.obj);
      return;
    case 99: 
      this.mInputMethodSession.updateCursorAnchorInfo((CursorAnchorInfo)paramMessage.obj);
      return;
    case 100: 
      paramMessage = (SomeArgs)paramMessage.obj;
      this.mInputMethodSession.appPrivateCommand((String)paramMessage.arg1, (Bundle)paramMessage.arg2);
      paramMessage.recycle();
      return;
    case 105: 
      this.mInputMethodSession.toggleSoftInput(paramMessage.arg1, paramMessage.arg2);
      return;
    case 110: 
      doFinishSession();
      return;
    }
    InputMethodSession localInputMethodSession = this.mInputMethodSession;
    if (paramMessage.arg1 == 1) {}
    for (;;)
    {
      localInputMethodSession.viewClicked(bool);
      return;
      bool = false;
    }
  }
  
  public void finishInput()
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(60));
  }
  
  public void finishSession()
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(110));
  }
  
  public InputMethodSession getInternalInputMethodSession()
  {
    return this.mInputMethodSession;
  }
  
  public void toggleSoftInput(int paramInt1, int paramInt2)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageII(105, paramInt1, paramInt2));
  }
  
  public void updateCursor(Rect paramRect)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(95, paramRect));
  }
  
  public void updateCursorAnchorInfo(CursorAnchorInfo paramCursorAnchorInfo)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(99, paramCursorAnchorInfo));
  }
  
  public void updateExtractedText(int paramInt, ExtractedText paramExtractedText)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIO(67, paramInt, paramExtractedText));
  }
  
  public void updateSelection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIIIIII(90, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void viewClicked(boolean paramBoolean)
  {
    HandlerCaller localHandlerCaller1 = this.mCaller;
    HandlerCaller localHandlerCaller2 = this.mCaller;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandlerCaller1.executeOrSendMessage(localHandlerCaller2.obtainMessageI(115, i));
      return;
    }
  }
  
  private final class ImeInputEventReceiver
    extends InputEventReceiver
    implements InputMethodSession.EventCallback
  {
    private final SparseArray<InputEvent> mPendingEvents = new SparseArray();
    
    public ImeInputEventReceiver(InputChannel paramInputChannel, Looper paramLooper)
    {
      super(paramLooper);
    }
    
    public void finishedEvent(int paramInt, boolean paramBoolean)
    {
      paramInt = this.mPendingEvents.indexOfKey(paramInt);
      if (paramInt >= 0)
      {
        InputEvent localInputEvent = (InputEvent)this.mPendingEvents.valueAt(paramInt);
        this.mPendingEvents.removeAt(paramInt);
        finishInputEvent(localInputEvent, paramBoolean);
      }
    }
    
    public void onInputEvent(InputEvent paramInputEvent)
    {
      if (IInputMethodSessionWrapper.this.mInputMethodSession == null)
      {
        finishInputEvent(paramInputEvent, false);
        return;
      }
      int i = paramInputEvent.getSequenceNumber();
      this.mPendingEvents.put(i, paramInputEvent);
      if ((paramInputEvent instanceof KeyEvent))
      {
        paramInputEvent = (KeyEvent)paramInputEvent;
        IInputMethodSessionWrapper.this.mInputMethodSession.dispatchKeyEvent(i, paramInputEvent, this);
        return;
      }
      paramInputEvent = (MotionEvent)paramInputEvent;
      if (paramInputEvent.isFromSource(4))
      {
        IInputMethodSessionWrapper.this.mInputMethodSession.dispatchTrackballEvent(i, paramInputEvent, this);
        return;
      }
      IInputMethodSessionWrapper.this.mInputMethodSession.dispatchGenericMotionEvent(i, paramInputEvent, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/IInputMethodSessionWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */