package android.inputmethodservice;

import android.R.styleable;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.graphics.Region;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class InputMethodService
  extends AbstractInputMethodService
{
  public static final int BACK_DISPOSITION_DEFAULT = 0;
  public static final int BACK_DISPOSITION_WILL_DISMISS = 2;
  public static final int BACK_DISPOSITION_WILL_NOT_DISMISS = 1;
  static final boolean DEBUG = false;
  public static final int IME_ACTIVE = 1;
  public static final int IME_VISIBLE = 2;
  static final int MOVEMENT_DOWN = -1;
  static final int MOVEMENT_UP = -2;
  static final String TAG = "InputMethodService";
  final View.OnClickListener mActionClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      paramAnonymousView = InputMethodService.this.getCurrentInputEditorInfo();
      InputConnection localInputConnection = InputMethodService.this.getCurrentInputConnection();
      if ((paramAnonymousView != null) && (localInputConnection != null))
      {
        if (paramAnonymousView.actionId == 0) {
          break label43;
        }
        localInputConnection.performEditorAction(paramAnonymousView.actionId);
      }
      label43:
      while ((paramAnonymousView.imeOptions & 0xFF) == 1) {
        return;
      }
      localInputConnection.performEditorAction(paramAnonymousView.imeOptions & 0xFF);
    }
  };
  int mBackDisposition;
  FrameLayout mCandidatesFrame;
  boolean mCandidatesViewStarted;
  int mCandidatesVisibility;
  CompletionInfo[] mCurCompletions;
  ViewGroup mExtractAccessories;
  View mExtractAction;
  ExtractEditText mExtractEditText;
  FrameLayout mExtractFrame;
  View mExtractView;
  boolean mExtractViewHidden;
  ExtractedText mExtractedText;
  int mExtractedToken;
  boolean mFullscreenApplied;
  ViewGroup mFullscreenArea;
  boolean mHardwareAccelerated = false;
  InputMethodManager mImm;
  boolean mInShowWindow;
  LayoutInflater mInflater;
  boolean mInitialized;
  InputBinding mInputBinding;
  InputConnection mInputConnection;
  EditorInfo mInputEditorInfo;
  FrameLayout mInputFrame;
  boolean mInputStarted;
  View mInputView;
  boolean mInputViewStarted;
  final ViewTreeObserver.OnComputeInternalInsetsListener mInsetsComputer = new ViewTreeObserver.OnComputeInternalInsetsListener()
  {
    public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo paramAnonymousInternalInsetsInfo)
    {
      if (InputMethodService.this.isExtractViewShown())
      {
        View localView = InputMethodService.this.getWindow().getWindow().getDecorView();
        Rect localRect = paramAnonymousInternalInsetsInfo.contentInsets;
        int i = localView.getHeight();
        paramAnonymousInternalInsetsInfo.visibleInsets.top = i;
        localRect.top = i;
        paramAnonymousInternalInsetsInfo.touchableRegion.setEmpty();
        paramAnonymousInternalInsetsInfo.setTouchableInsets(0);
        return;
      }
      InputMethodService.this.onComputeInsets(InputMethodService.this.mTmpInsets);
      paramAnonymousInternalInsetsInfo.contentInsets.top = InputMethodService.this.mTmpInsets.contentTopInsets;
      paramAnonymousInternalInsetsInfo.visibleInsets.top = InputMethodService.this.mTmpInsets.visibleTopInsets;
      paramAnonymousInternalInsetsInfo.touchableRegion.set(InputMethodService.this.mTmpInsets.touchableRegion);
      paramAnonymousInternalInsetsInfo.setTouchableInsets(InputMethodService.this.mTmpInsets.touchableInsets);
    }
  };
  boolean mIsFullscreen;
  boolean mIsInputViewShown;
  boolean mLastShowInputRequested;
  View mRootView;
  private SettingsObserver mSettingsObserver;
  boolean mShouldClearInsetOfPreviousIme;
  int mShowInputFlags;
  boolean mShowInputRequested;
  InputConnection mStartedInputConnection;
  int mStatusIcon;
  int mTheme = 0;
  TypedArray mThemeAttrs;
  final Insets mTmpInsets = new Insets();
  final int[] mTmpLocation = new int[2];
  IBinder mToken;
  SoftInputWindow mWindow;
  boolean mWindowAdded;
  boolean mWindowCreated;
  boolean mWindowVisible;
  boolean mWindowWasVisible;
  
  private void clearInsetOfPreviousIme()
  {
    if (!this.mShouldClearInsetOfPreviousIme) {
      return;
    }
    this.mImm.clearLastInputMethodWindowForTransition(this.mToken);
    this.mShouldClearInsetOfPreviousIme = false;
  }
  
  private boolean dispatchOnShowInputRequested(int paramInt, boolean paramBoolean)
  {
    paramBoolean = onShowInputRequested(paramInt, paramBoolean);
    if (paramBoolean)
    {
      this.mShowInputFlags = paramInt;
      return paramBoolean;
    }
    this.mShowInputFlags = 0;
    return paramBoolean;
  }
  
  private void doHideWindow()
  {
    this.mImm.setImeWindowStatus(this.mToken, 0, this.mBackDisposition);
    hideWindow();
  }
  
  private void finishViews()
  {
    if (this.mInputViewStarted) {
      onFinishInputView(false);
    }
    for (;;)
    {
      this.mInputViewStarted = false;
      this.mCandidatesViewStarted = false;
      return;
      if (this.mCandidatesViewStarted) {
        onFinishCandidatesView(false);
      }
    }
  }
  
  private ExtractEditText getExtractEditTextIfVisible()
  {
    if ((isExtractViewShown()) && (isInputViewShown())) {
      return this.mExtractEditText;
    }
    return null;
  }
  
  private int getIconForImeAction(int paramInt)
  {
    switch (paramInt & 0xFF)
    {
    default: 
      return 17302370;
    case 2: 
      return 17302367;
    case 3: 
      return 17302371;
    case 4: 
      return 17302372;
    case 5: 
      return 17302368;
    case 6: 
      return 17302366;
    }
    return 17302369;
  }
  
  private boolean handleBack(boolean paramBoolean)
  {
    if (this.mShowInputRequested)
    {
      if (paramBoolean) {
        requestHideSelf(0);
      }
      return true;
    }
    if (this.mWindowVisible)
    {
      if (this.mCandidatesVisibility == 0) {
        if (paramBoolean) {
          setCandidatesViewShown(false);
        }
      }
      while (!paramBoolean) {
        return true;
      }
      doHideWindow();
      return true;
    }
    return false;
  }
  
  private void onToggleSoftInput(int paramInt1, int paramInt2)
  {
    if (isInputViewShown())
    {
      requestHideSelf(paramInt2);
      return;
    }
    requestShowSelf(paramInt1);
  }
  
  private void requestShowSelf(int paramInt)
  {
    this.mImm.showSoftInputFromInputMethod(this.mToken, paramInt);
  }
  
  private void resetStateForNewConfiguration()
  {
    int i = 0;
    boolean bool1 = this.mWindowVisible;
    int j = this.mShowInputFlags;
    boolean bool2 = this.mShowInputRequested;
    Object localObject = this.mCurCompletions;
    initViews();
    this.mInputViewStarted = false;
    this.mCandidatesViewStarted = false;
    if (this.mInputStarted) {
      doStartInput(getCurrentInputConnection(), getCurrentInputEditorInfo(), true);
    }
    if (bool1)
    {
      if (!bool2) {
        break label143;
      }
      if (!dispatchOnShowInputRequested(j, true)) {
        break label136;
      }
      showWindow(true);
      if (localObject != null)
      {
        this.mCurCompletions = ((CompletionInfo[])localObject);
        onDisplayCompletions((CompletionInfo[])localObject);
      }
    }
    for (;;)
    {
      bool1 = onEvaluateInputViewShown();
      localObject = this.mImm;
      IBinder localIBinder = this.mToken;
      if (bool1) {
        i = 2;
      }
      ((InputMethodManager)localObject).setImeWindowStatus(localIBinder, i | 0x1, this.mBackDisposition);
      return;
      label136:
      doHideWindow();
      continue;
      label143:
      if (this.mCandidatesVisibility == 0) {
        showWindow(false);
      } else {
        doHideWindow();
      }
    }
  }
  
  void doFinishInput()
  {
    if (this.mInputViewStarted) {
      onFinishInputView(true);
    }
    for (;;)
    {
      this.mInputViewStarted = false;
      this.mCandidatesViewStarted = false;
      if (this.mInputStarted) {
        onFinishInput();
      }
      this.mInputStarted = false;
      this.mStartedInputConnection = null;
      this.mCurCompletions = null;
      return;
      if (this.mCandidatesViewStarted) {
        onFinishCandidatesView(true);
      }
    }
  }
  
  boolean doMovementKey(int paramInt1, KeyEvent paramKeyEvent, int paramInt2)
  {
    ExtractEditText localExtractEditText = getExtractEditTextIfVisible();
    MovementMethod localMovementMethod;
    Object localObject;
    if (localExtractEditText != null)
    {
      localMovementMethod = localExtractEditText.getMovementMethod();
      localObject = localExtractEditText.getLayout();
      if ((localMovementMethod != null) && (localObject != null)) {
        if (paramInt2 == -1)
        {
          if (localMovementMethod.onKeyDown(localExtractEditText, localExtractEditText.getText(), paramInt1, paramKeyEvent))
          {
            reportExtractedMovement(paramInt1, 1);
            return true;
          }
        }
        else if (paramInt2 == -2)
        {
          if (localMovementMethod.onKeyUp(localExtractEditText, localExtractEditText.getText(), paramInt1, paramKeyEvent)) {
            return true;
          }
        }
        else
        {
          if (!localMovementMethod.onKeyOther(localExtractEditText, localExtractEditText.getText(), paramKeyEvent)) {
            break label150;
          }
          reportExtractedMovement(paramInt1, paramInt2);
        }
      }
    }
    for (;;)
    {
      switch (paramInt1)
      {
      default: 
        return false;
        label150:
        localObject = KeyEvent.changeAction(paramKeyEvent, 0);
        if (localMovementMethod.onKeyDown(localExtractEditText, localExtractEditText.getText(), paramInt1, (KeyEvent)localObject))
        {
          paramKeyEvent = KeyEvent.changeAction(paramKeyEvent, 1);
          localMovementMethod.onKeyUp(localExtractEditText, localExtractEditText.getText(), paramInt1, paramKeyEvent);
          for (;;)
          {
            paramInt2 -= 1;
            if (paramInt2 <= 0) {
              break;
            }
            localMovementMethod.onKeyDown(localExtractEditText, localExtractEditText.getText(), paramInt1, (KeyEvent)localObject);
            localMovementMethod.onKeyUp(localExtractEditText, localExtractEditText.getText(), paramInt1, paramKeyEvent);
          }
          reportExtractedMovement(paramInt1, paramInt2);
        }
        break;
      }
    }
    return true;
  }
  
  void doStartInput(InputConnection paramInputConnection, EditorInfo paramEditorInfo, boolean paramBoolean)
  {
    if (!paramBoolean) {
      doFinishInput();
    }
    this.mInputStarted = true;
    this.mStartedInputConnection = paramInputConnection;
    this.mInputEditorInfo = paramEditorInfo;
    initialize();
    onStartInput(paramEditorInfo, paramBoolean);
    if (this.mWindowVisible)
    {
      if (!this.mShowInputRequested) {
        break label67;
      }
      this.mInputViewStarted = true;
      onStartInputView(this.mInputEditorInfo, paramBoolean);
      startExtractingText(true);
    }
    label67:
    while (this.mCandidatesVisibility != 0) {
      return;
    }
    this.mCandidatesViewStarted = true;
    onStartCandidatesView(this.mInputEditorInfo, paramBoolean);
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramFileDescriptor = new PrintWriterPrinter(paramPrintWriter);
    paramFileDescriptor.println("Input method service state for " + this + ":");
    paramFileDescriptor.println("  mWindowCreated=" + this.mWindowCreated + " mWindowAdded=" + this.mWindowAdded);
    paramFileDescriptor.println("  mWindowVisible=" + this.mWindowVisible + " mWindowWasVisible=" + this.mWindowWasVisible + " mInShowWindow=" + this.mInShowWindow);
    paramFileDescriptor.println("  Configuration=" + getResources().getConfiguration());
    paramFileDescriptor.println("  mToken=" + this.mToken);
    paramFileDescriptor.println("  mInputBinding=" + this.mInputBinding);
    paramFileDescriptor.println("  mInputConnection=" + this.mInputConnection);
    paramFileDescriptor.println("  mStartedInputConnection=" + this.mStartedInputConnection);
    paramFileDescriptor.println("  mInputStarted=" + this.mInputStarted + " mInputViewStarted=" + this.mInputViewStarted + " mCandidatesViewStarted=" + this.mCandidatesViewStarted);
    if (this.mInputEditorInfo != null)
    {
      paramFileDescriptor.println("  mInputEditorInfo:");
      this.mInputEditorInfo.dump(paramFileDescriptor, "    ");
      paramFileDescriptor.println("  mShowInputRequested=" + this.mShowInputRequested + " mLastShowInputRequested=" + this.mLastShowInputRequested + " mShowInputFlags=0x" + Integer.toHexString(this.mShowInputFlags));
      paramFileDescriptor.println("  mCandidatesVisibility=" + this.mCandidatesVisibility + " mFullscreenApplied=" + this.mFullscreenApplied + " mIsFullscreen=" + this.mIsFullscreen + " mExtractViewHidden=" + this.mExtractViewHidden);
      if (this.mExtractedText == null) {
        break label867;
      }
      paramFileDescriptor.println("  mExtractedText:");
      paramFileDescriptor.println("    text=" + this.mExtractedText.text.length() + " chars" + " startOffset=" + this.mExtractedText.startOffset);
      paramFileDescriptor.println("    selectionStart=" + this.mExtractedText.selectionStart + " selectionEnd=" + this.mExtractedText.selectionEnd + " flags=0x" + Integer.toHexString(this.mExtractedText.flags));
    }
    for (;;)
    {
      paramFileDescriptor.println("  mExtractedToken=" + this.mExtractedToken);
      paramFileDescriptor.println("  mIsInputViewShown=" + this.mIsInputViewShown + " mStatusIcon=" + this.mStatusIcon);
      paramFileDescriptor.println("Last computed insets:");
      paramFileDescriptor.println("  contentTopInsets=" + this.mTmpInsets.contentTopInsets + " visibleTopInsets=" + this.mTmpInsets.visibleTopInsets + " touchableInsets=" + this.mTmpInsets.touchableInsets + " touchableRegion=" + this.mTmpInsets.touchableRegion);
      paramFileDescriptor.println(" mShouldClearInsetOfPreviousIme=" + this.mShouldClearInsetOfPreviousIme);
      paramFileDescriptor.println(" mSettingsObserver=" + this.mSettingsObserver);
      return;
      paramFileDescriptor.println("  mInputEditorInfo: null");
      break;
      label867:
      paramFileDescriptor.println("  mExtractedText: null");
    }
  }
  
  public boolean enableHardwareAcceleration()
  {
    if (this.mWindow != null) {
      throw new IllegalStateException("Must be called before onCreate()");
    }
    if (ActivityManager.isHighEndGfx())
    {
      this.mHardwareAccelerated = true;
      return true;
    }
    return false;
  }
  
  public final void exposeContent(InputContentInfo paramInputContentInfo, InputConnection paramInputConnection)
  {
    if (paramInputConnection == null) {
      return;
    }
    if (getCurrentInputConnection() != paramInputConnection) {
      return;
    }
    this.mImm.exposeContent(this.mToken, paramInputContentInfo, getCurrentInputEditorInfo());
  }
  
  public int getBackDisposition()
  {
    return this.mBackDisposition;
  }
  
  public int getCandidatesHiddenVisibility()
  {
    if (isExtractViewShown()) {
      return 8;
    }
    return 4;
  }
  
  public InputBinding getCurrentInputBinding()
  {
    return this.mInputBinding;
  }
  
  public InputConnection getCurrentInputConnection()
  {
    InputConnection localInputConnection = this.mStartedInputConnection;
    if (localInputConnection != null) {
      return localInputConnection;
    }
    return this.mInputConnection;
  }
  
  public EditorInfo getCurrentInputEditorInfo()
  {
    return this.mInputEditorInfo;
  }
  
  public boolean getCurrentInputStarted()
  {
    return this.mInputStarted;
  }
  
  public int getInputMethodWindowRecommendedHeight()
  {
    return this.mImm.getInputMethodWindowVisibleHeight();
  }
  
  public LayoutInflater getLayoutInflater()
  {
    return this.mInflater;
  }
  
  public int getMaxWidth()
  {
    return ((WindowManager)getSystemService("window")).getDefaultDisplay().getWidth();
  }
  
  public CharSequence getTextForImeAction(int paramInt)
  {
    switch (paramInt & 0xFF)
    {
    default: 
      return getText(17040500);
    case 1: 
      return null;
    case 2: 
      return getText(17040494);
    case 3: 
      return getText(17040495);
    case 4: 
      return getText(17040496);
    case 5: 
      return getText(17040497);
    case 6: 
      return getText(17040498);
    }
    return getText(17040499);
  }
  
  public Dialog getWindow()
  {
    return this.mWindow;
  }
  
  public void hideStatusIcon()
  {
    this.mStatusIcon = 0;
    this.mImm.hideStatusIcon(this.mToken);
  }
  
  public void hideWindow()
  {
    finishViews();
    if (this.mWindowVisible)
    {
      this.mWindow.hide();
      this.mWindowVisible = false;
      onWindowHidden();
      this.mWindowWasVisible = false;
    }
    updateFullscreenMode();
  }
  
  void initViews()
  {
    this.mInitialized = false;
    this.mWindowCreated = false;
    this.mShowInputRequested = false;
    this.mShowInputFlags = 0;
    this.mThemeAttrs = obtainStyledAttributes(R.styleable.InputMethodService);
    this.mRootView = this.mInflater.inflate(17367150, null);
    this.mRootView.setSystemUiVisibility(768);
    this.mWindow.setContentView(this.mRootView);
    this.mRootView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsComputer);
    this.mRootView.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsComputer);
    if (Settings.Global.getInt(getContentResolver(), "fancy_ime_animations", 0) != 0) {
      this.mWindow.getWindow().setWindowAnimations(16974575);
    }
    this.mFullscreenArea = ((ViewGroup)this.mRootView.findViewById(16909192));
    this.mExtractViewHidden = false;
    this.mExtractFrame = ((FrameLayout)this.mRootView.findViewById(16908316));
    this.mExtractView = null;
    this.mExtractEditText = null;
    this.mExtractAccessories = null;
    this.mExtractAction = null;
    this.mFullscreenApplied = false;
    this.mCandidatesFrame = ((FrameLayout)this.mRootView.findViewById(16908317));
    this.mInputFrame = ((FrameLayout)this.mRootView.findViewById(16908318));
    this.mInputView = null;
    this.mIsInputViewShown = false;
    this.mExtractFrame.setVisibility(8);
    this.mCandidatesVisibility = getCandidatesHiddenVisibility();
    this.mCandidatesFrame.setVisibility(this.mCandidatesVisibility);
    this.mInputFrame.setVisibility(8);
  }
  
  void initialize()
  {
    if (!this.mInitialized)
    {
      this.mInitialized = true;
      onInitializeInterface();
    }
  }
  
  public boolean isExtractViewShown()
  {
    return (this.mIsFullscreen) && (!this.mExtractViewHidden);
  }
  
  public boolean isFullscreenMode()
  {
    return this.mIsFullscreen;
  }
  
  public boolean isInputViewShown()
  {
    if (this.mIsInputViewShown) {
      return this.mWindowVisible;
    }
    return false;
  }
  
  public boolean isShowInputRequested()
  {
    return this.mShowInputRequested;
  }
  
  public void onAppPrivateCommand(String paramString, Bundle paramBundle) {}
  
  public void onBindInput() {}
  
  public void onComputeInsets(Insets paramInsets)
  {
    int[] arrayOfInt = this.mTmpLocation;
    if (this.mInputFrame.getVisibility() == 0)
    {
      this.mInputFrame.getLocationInWindow(arrayOfInt);
      if (!isFullscreenMode()) {
        break label104;
      }
    }
    label104:
    for (paramInsets.contentTopInsets = getWindow().getWindow().getDecorView().getHeight();; paramInsets.contentTopInsets = arrayOfInt[1])
    {
      if (this.mCandidatesFrame.getVisibility() == 0) {
        this.mCandidatesFrame.getLocationInWindow(arrayOfInt);
      }
      paramInsets.visibleTopInsets = arrayOfInt[1];
      paramInsets.touchableInsets = 2;
      paramInsets.touchableRegion.setEmpty();
      return;
      arrayOfInt[1] = getWindow().getWindow().getDecorView().getHeight();
      break;
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    resetStateForNewConfiguration();
  }
  
  public void onConfigureWindow(Window paramWindow, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = this.mWindow.getWindow().getAttributes().height;
    if (paramBoolean1) {}
    for (int i = -1;; i = -2)
    {
      if ((this.mIsInputViewShown) && (j != i)) {
        Log.w("InputMethodService", "Window size has been changed. This may cause jankiness of resizing window: " + j + " -> " + i);
      }
      this.mWindow.getWindow().setLayout(-1, i);
      return;
    }
  }
  
  public void onCreate()
  {
    this.mTheme = Resources.selectSystemTheme(this.mTheme, getApplicationInfo().targetSdkVersion, 16973908, 16973951, 16974142, 16974142);
    super.setTheme(this.mTheme);
    super.onCreate();
    this.mImm = ((InputMethodManager)getSystemService("input_method"));
    this.mSettingsObserver = SettingsObserver.createAndRegister(this);
    if (this.mImm.getInputMethodWindowVisibleHeight() > 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mShouldClearInsetOfPreviousIme = bool;
      this.mInflater = ((LayoutInflater)getSystemService("layout_inflater"));
      this.mWindow = new SoftInputWindow(this, "InputMethod", this.mTheme, null, null, this.mDispatcherState, 2011, 80, false);
      if (this.mHardwareAccelerated) {
        this.mWindow.getWindow().addFlags(16777216);
      }
      initViews();
      this.mWindow.getWindow().setLayout(-1, -2);
      return;
    }
  }
  
  public View onCreateCandidatesView()
  {
    return null;
  }
  
  public View onCreateExtractTextView()
  {
    return this.mInflater.inflate(17367151, null);
  }
  
  public AbstractInputMethodService.AbstractInputMethodImpl onCreateInputMethodInterface()
  {
    return new InputMethodImpl();
  }
  
  public AbstractInputMethodService.AbstractInputMethodSessionImpl onCreateInputMethodSessionInterface()
  {
    return new InputMethodSessionImpl();
  }
  
  public View onCreateInputView()
  {
    return null;
  }
  
  protected void onCurrentInputMethodSubtypeChanged(InputMethodSubtype paramInputMethodSubtype) {}
  
  public void onDestroy()
  {
    super.onDestroy();
    this.mRootView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsComputer);
    doFinishInput();
    if (this.mWindowAdded)
    {
      this.mWindow.getWindow().setWindowAnimations(0);
      this.mWindow.dismiss();
    }
    if (this.mSettingsObserver != null)
    {
      this.mSettingsObserver.unregister();
      this.mSettingsObserver = null;
    }
  }
  
  public void onDisplayCompletions(CompletionInfo[] paramArrayOfCompletionInfo) {}
  
  public boolean onEvaluateFullscreenMode()
  {
    if (getResources().getConfiguration().orientation != 2) {
      return false;
    }
    return (this.mInputEditorInfo == null) || ((this.mInputEditorInfo.imeOptions & 0x2000000) == 0);
  }
  
  public boolean onEvaluateInputViewShown()
  {
    if (this.mSettingsObserver == null)
    {
      Log.w("InputMethodService", "onEvaluateInputViewShown: mSettingsObserver must not be null here.");
      return false;
    }
    if (SettingsObserver.-wrap0(this.mSettingsObserver)) {
      return true;
    }
    Configuration localConfiguration = getResources().getConfiguration();
    return (localConfiguration.keyboard == 1) || (localConfiguration.hardKeyboardHidden == 2);
  }
  
  public boolean onExtractTextContextMenuItem(int paramInt)
  {
    InputConnection localInputConnection = getCurrentInputConnection();
    if (localInputConnection != null) {
      localInputConnection.performContextMenuAction(paramInt);
    }
    return true;
  }
  
  public void onExtractedCursorMovement(int paramInt1, int paramInt2)
  {
    if ((this.mExtractEditText == null) || (paramInt2 == 0)) {
      return;
    }
    if (this.mExtractEditText.hasVerticalScrollBar()) {
      setCandidatesViewShown(false);
    }
  }
  
  public void onExtractedDeleteText(int paramInt1, int paramInt2)
  {
    InputConnection localInputConnection = getCurrentInputConnection();
    if (localInputConnection != null)
    {
      localInputConnection.finishComposingText();
      localInputConnection.setSelection(paramInt1, paramInt1);
      localInputConnection.deleteSurroundingText(0, paramInt2 - paramInt1);
    }
  }
  
  public void onExtractedReplaceText(int paramInt1, int paramInt2, CharSequence paramCharSequence)
  {
    InputConnection localInputConnection = getCurrentInputConnection();
    if (localInputConnection != null)
    {
      localInputConnection.setComposingRegion(paramInt1, paramInt2);
      localInputConnection.commitText(paramCharSequence, 1);
    }
  }
  
  public void onExtractedSelectionChanged(int paramInt1, int paramInt2)
  {
    InputConnection localInputConnection = getCurrentInputConnection();
    if (localInputConnection != null) {
      localInputConnection.setSelection(paramInt1, paramInt2);
    }
  }
  
  public void onExtractedSetSpan(Object paramObject, int paramInt1, int paramInt2, int paramInt3)
  {
    InputConnection localInputConnection = getCurrentInputConnection();
    if (localInputConnection != null)
    {
      if (!localInputConnection.setSelection(paramInt1, paramInt2)) {
        return;
      }
      CharSequence localCharSequence = localInputConnection.getSelectedText(1);
      if ((localCharSequence instanceof Spannable))
      {
        ((Spannable)localCharSequence).setSpan(paramObject, 0, localCharSequence.length(), paramInt3);
        localInputConnection.setComposingRegion(paramInt1, paramInt2);
        localInputConnection.commitText(localCharSequence, 1);
      }
    }
  }
  
  public void onExtractedTextClicked()
  {
    if (this.mExtractEditText == null) {
      return;
    }
    if (this.mExtractEditText.hasVerticalScrollBar()) {
      setCandidatesViewShown(false);
    }
  }
  
  public void onExtractingInputChanged(EditorInfo paramEditorInfo)
  {
    if (paramEditorInfo.inputType == 0) {
      requestHideSelf(2);
    }
  }
  
  public void onFinishCandidatesView(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      InputConnection localInputConnection = getCurrentInputConnection();
      if (localInputConnection != null) {
        localInputConnection.finishComposingText();
      }
    }
  }
  
  public void onFinishInput()
  {
    InputConnection localInputConnection = getCurrentInputConnection();
    if (localInputConnection != null) {
      localInputConnection.finishComposingText();
    }
  }
  
  public void onFinishInputView(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      InputConnection localInputConnection = getCurrentInputConnection();
      if (localInputConnection != null) {
        localInputConnection.finishComposingText();
      }
    }
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public void onInitializeInterface() {}
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getKeyCode() == 4)
    {
      ExtractEditText localExtractEditText = getExtractEditTextIfVisible();
      if ((localExtractEditText != null) && (localExtractEditText.handleBackInTextActionModeIfNeeded(paramKeyEvent))) {
        return true;
      }
      if (handleBack(false))
      {
        paramKeyEvent.startTracking();
        return true;
      }
      return false;
    }
    return doMovementKey(paramInt, paramKeyEvent, -1);
  }
  
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    return doMovementKey(paramInt1, paramKeyEvent, paramInt2);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getKeyCode() == 4)
    {
      ExtractEditText localExtractEditText = getExtractEditTextIfVisible();
      if ((localExtractEditText != null) && (localExtractEditText.handleBackInTextActionModeIfNeeded(paramKeyEvent))) {
        return true;
      }
      if ((paramKeyEvent.isTracking()) && (!paramKeyEvent.isCanceled())) {}
    }
    else
    {
      return doMovementKey(paramInt, paramKeyEvent, -2);
    }
    return handleBack(true);
  }
  
  public boolean onShowInputRequested(int paramInt, boolean paramBoolean)
  {
    if (!onEvaluateInputViewShown()) {
      return false;
    }
    if ((paramInt & 0x1) == 0)
    {
      if ((!paramBoolean) && (onEvaluateFullscreenMode())) {
        return false;
      }
      if ((!SettingsObserver.-wrap0(this.mSettingsObserver)) && (getResources().getConfiguration().keyboard != 1)) {
        return false;
      }
    }
    return true;
  }
  
  public void onStartCandidatesView(EditorInfo paramEditorInfo, boolean paramBoolean) {}
  
  public void onStartInput(EditorInfo paramEditorInfo, boolean paramBoolean) {}
  
  public void onStartInputView(EditorInfo paramEditorInfo, boolean paramBoolean) {}
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public void onUnbindInput() {}
  
  @Deprecated
  public void onUpdateCursor(Rect paramRect) {}
  
  public void onUpdateCursorAnchorInfo(CursorAnchorInfo paramCursorAnchorInfo) {}
  
  public void onUpdateExtractedText(int paramInt, ExtractedText paramExtractedText)
  {
    if (this.mExtractedToken != paramInt) {
      return;
    }
    if ((paramExtractedText != null) && (this.mExtractEditText != null))
    {
      this.mExtractedText = paramExtractedText;
      this.mExtractEditText.setExtractedText(paramExtractedText);
    }
  }
  
  public void onUpdateExtractingViews(EditorInfo paramEditorInfo)
  {
    int i = 1;
    if (!isExtractViewShown()) {
      return;
    }
    if (this.mExtractAccessories == null) {
      return;
    }
    if (paramEditorInfo.actionLabel == null)
    {
      if (((paramEditorInfo.imeOptions & 0xFF) == 1) || ((paramEditorInfo.imeOptions & 0x20000000) != 0)) {
        break label142;
      }
      if (paramEditorInfo.inputType == 0) {
        break label137;
      }
      if (i == 0) {
        break label210;
      }
      this.mExtractAccessories.setVisibility(0);
      if (this.mExtractAction != null)
      {
        if (!(this.mExtractAction instanceof ImageButton)) {
          break label165;
        }
        ((ImageButton)this.mExtractAction).setImageResource(getIconForImeAction(paramEditorInfo.imeOptions));
        if (paramEditorInfo.actionLabel == null) {
          break label147;
        }
        this.mExtractAction.setContentDescription(paramEditorInfo.actionLabel);
        label120:
        this.mExtractAction.setOnClickListener(this.mActionClickListener);
      }
    }
    label137:
    label142:
    label147:
    label165:
    label210:
    do
    {
      return;
      i = 1;
      break;
      i = 0;
      break;
      i = 0;
      break;
      this.mExtractAction.setContentDescription(getTextForImeAction(paramEditorInfo.imeOptions));
      break label120;
      if (paramEditorInfo.actionLabel != null)
      {
        ((TextView)this.mExtractAction).setText(paramEditorInfo.actionLabel);
        break label120;
      }
      ((TextView)this.mExtractAction).setText(getTextForImeAction(paramEditorInfo.imeOptions));
      break label120;
      this.mExtractAccessories.setVisibility(8);
    } while (this.mExtractAction == null);
    this.mExtractAction.setOnClickListener(null);
  }
  
  public void onUpdateExtractingVisibility(EditorInfo paramEditorInfo)
  {
    if ((paramEditorInfo.inputType == 0) || ((paramEditorInfo.imeOptions & 0x10000000) != 0))
    {
      setExtractViewShown(false);
      return;
    }
    setExtractViewShown(true);
  }
  
  public void onUpdateSelection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    ExtractEditText localExtractEditText = this.mExtractEditText;
    if ((localExtractEditText != null) && (isFullscreenMode()) && (this.mExtractedText != null))
    {
      paramInt1 = this.mExtractedText.startOffset;
      localExtractEditText.startInternalChanges();
      paramInt2 = paramInt3 - paramInt1;
      paramInt4 -= paramInt1;
      paramInt3 = localExtractEditText.getText().length();
      if (paramInt2 >= 0) {
        break label85;
      }
      paramInt1 = 0;
      if (paramInt4 >= 0) {
        break label97;
      }
      paramInt2 = 0;
    }
    for (;;)
    {
      localExtractEditText.setSelection(paramInt1, paramInt2);
      localExtractEditText.finishInternalChanges();
      return;
      label85:
      paramInt1 = paramInt2;
      if (paramInt2 <= paramInt3) {
        break;
      }
      paramInt1 = paramInt3;
      break;
      label97:
      paramInt2 = paramInt4;
      if (paramInt4 > paramInt3) {
        paramInt2 = paramInt3;
      }
    }
  }
  
  public void onViewClicked(boolean paramBoolean) {}
  
  public void onWindowHidden() {}
  
  public void onWindowShown() {}
  
  void reportExtractedMovement(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = 0;
    switch (paramInt1)
    {
    default: 
      paramInt2 = j;
      paramInt1 = i;
    }
    for (;;)
    {
      onExtractedCursorMovement(paramInt1, paramInt2);
      return;
      paramInt1 = -paramInt2;
      paramInt2 = j;
      continue;
      paramInt1 = paramInt2;
      paramInt2 = j;
      continue;
      paramInt2 = -paramInt2;
      paramInt1 = i;
      continue;
      paramInt1 = i;
    }
  }
  
  public void requestHideSelf(int paramInt)
  {
    this.mImm.hideSoftInputFromInputMethod(this.mToken, paramInt);
  }
  
  public boolean sendDefaultEditorAction(boolean paramBoolean)
  {
    EditorInfo localEditorInfo = getCurrentInputEditorInfo();
    if ((localEditorInfo != null) && ((!paramBoolean) || ((localEditorInfo.imeOptions & 0x40000000) == 0)) && ((localEditorInfo.imeOptions & 0xFF) != 1))
    {
      InputConnection localInputConnection = getCurrentInputConnection();
      if (localInputConnection != null) {
        localInputConnection.performEditorAction(localEditorInfo.imeOptions & 0xFF);
      }
      return true;
    }
    return false;
  }
  
  public void sendDownUpKeyEvents(int paramInt)
  {
    InputConnection localInputConnection = getCurrentInputConnection();
    if (localInputConnection == null) {
      return;
    }
    long l = SystemClock.uptimeMillis();
    localInputConnection.sendKeyEvent(new KeyEvent(l, l, 0, paramInt, 0, 0, -1, 0, 6));
    localInputConnection.sendKeyEvent(new KeyEvent(l, SystemClock.uptimeMillis(), 1, paramInt, 0, 0, -1, 0, 6));
  }
  
  public void sendKeyChar(char paramChar)
  {
    switch (paramChar)
    {
    default: 
      if ((paramChar >= '0') && (paramChar <= '9')) {
        sendDownUpKeyEvents(paramChar - '0' + 7);
      }
      break;
    }
    InputConnection localInputConnection;
    do
    {
      do
      {
        return;
      } while (sendDefaultEditorAction(true));
      sendDownUpKeyEvents(66);
      return;
      localInputConnection = getCurrentInputConnection();
    } while (localInputConnection == null);
    localInputConnection.commitText(String.valueOf(paramChar), 1);
  }
  
  public void setBackDisposition(int paramInt)
  {
    this.mBackDisposition = paramInt;
  }
  
  public void setCandidatesView(View paramView)
  {
    this.mCandidatesFrame.removeAllViews();
    this.mCandidatesFrame.addView(paramView, new FrameLayout.LayoutParams(-1, -2));
  }
  
  public void setCandidatesViewShown(boolean paramBoolean)
  {
    updateCandidatesVisibility(paramBoolean);
    if ((!this.mShowInputRequested) && (this.mWindowVisible != paramBoolean))
    {
      if (paramBoolean) {
        showWindow(false);
      }
    }
    else {
      return;
    }
    doHideWindow();
  }
  
  public void setExtractView(View paramView)
  {
    this.mExtractFrame.removeAllViews();
    this.mExtractFrame.addView(paramView, new FrameLayout.LayoutParams(-1, -1));
    this.mExtractView = paramView;
    if (paramView != null)
    {
      this.mExtractEditText = ((ExtractEditText)paramView.findViewById(16908325));
      this.mExtractEditText.setIME(this);
      this.mExtractAction = paramView.findViewById(16909194);
      if (this.mExtractAction != null) {
        this.mExtractAccessories = ((ViewGroup)paramView.findViewById(16909193));
      }
      startExtractingText(false);
      return;
    }
    this.mExtractEditText = null;
    this.mExtractAccessories = null;
    this.mExtractAction = null;
  }
  
  public void setExtractViewShown(boolean paramBoolean)
  {
    if (this.mExtractViewHidden == paramBoolean) {
      if (!paramBoolean) {
        break label24;
      }
    }
    label24:
    for (paramBoolean = false;; paramBoolean = true)
    {
      this.mExtractViewHidden = paramBoolean;
      updateExtractFrameVisibility();
      return;
    }
  }
  
  public void setInputView(View paramView)
  {
    this.mInputFrame.removeAllViews();
    this.mInputFrame.addView(paramView, new FrameLayout.LayoutParams(-1, -2));
    this.mInputView = paramView;
  }
  
  public void setTheme(int paramInt)
  {
    if (this.mWindow != null) {
      throw new IllegalStateException("Must be called before onCreate()");
    }
    this.mTheme = paramInt;
  }
  
  public void showStatusIcon(int paramInt)
  {
    this.mStatusIcon = paramInt;
    this.mImm.showStatusIcon(this.mToken, getPackageName(), paramInt);
  }
  
  public void showWindow(boolean paramBoolean)
  {
    if (this.mInShowWindow)
    {
      Log.w("InputMethodService", "Re-entrance in to showWindow");
      return;
    }
    try
    {
      this.mWindowWasVisible = this.mWindowVisible;
      this.mInShowWindow = true;
      showWindowInner(paramBoolean);
      return;
    }
    catch (WindowManager.BadTokenException localBadTokenException)
    {
      this.mWindowVisible = false;
      this.mWindowAdded = false;
      throw localBadTokenException;
    }
    finally
    {
      this.mWindowWasVisible = true;
      this.mInShowWindow = false;
    }
  }
  
  void showWindowInner(boolean paramBoolean)
  {
    int k = 2;
    int m = 0;
    int j;
    if (this.mWindowVisible)
    {
      i = 1;
      if (!isInputViewShown()) {
        break label193;
      }
      j = 2;
      label24:
      j = i | j;
      this.mWindowVisible = true;
      i = m;
      if (!this.mShowInputRequested)
      {
        i = m;
        if (this.mInputStarted)
        {
          i = m;
          if (paramBoolean)
          {
            i = 1;
            this.mShowInputRequested = true;
          }
        }
      }
      initialize();
      updateFullscreenMode();
      updateInputViewShown();
      if ((!this.mWindowAdded) || (!this.mWindowCreated)) {
        break label198;
      }
      label93:
      if (!this.mShowInputRequested) {
        break label232;
      }
      if (!this.mInputViewStarted)
      {
        this.mInputViewStarted = true;
        onStartInputView(this.mInputEditorInfo, false);
      }
      label121:
      if (i != 0) {
        startExtractingText(false);
      }
      if (!isInputViewShown()) {
        break label256;
      }
    }
    label193:
    label198:
    label232:
    label256:
    for (int i = k;; i = 0)
    {
      i |= 0x1;
      if (j != i) {
        this.mImm.setImeWindowStatus(this.mToken, i, this.mBackDisposition);
      }
      if ((j & 0x1) == 0)
      {
        onWindowShown();
        this.mWindow.show();
        this.mShouldClearInsetOfPreviousIme = false;
      }
      return;
      i = 0;
      break;
      j = 0;
      break label24;
      this.mWindowAdded = true;
      this.mWindowCreated = true;
      initialize();
      View localView = onCreateCandidatesView();
      if (localView == null) {
        break label93;
      }
      setCandidatesView(localView);
      break label93;
      if (this.mCandidatesViewStarted) {
        break label121;
      }
      this.mCandidatesViewStarted = true;
      onStartCandidatesView(this.mInputEditorInfo, false);
      break label121;
    }
  }
  
  /* Error */
  void startExtractingText(boolean paramBoolean)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: getfield 202	android/inputmethodservice/InputMethodService:mExtractEditText	Landroid/inputmethodservice/ExtractEditText;
    //   7: astore 5
    //   9: aload 5
    //   11: ifnull +243 -> 254
    //   14: aload_0
    //   15: invokevirtual 1088	android/inputmethodservice/InputMethodService:getCurrentInputStarted	()Z
    //   18: ifeq +236 -> 254
    //   21: aload_0
    //   22: invokevirtual 730	android/inputmethodservice/InputMethodService:isFullscreenMode	()Z
    //   25: ifeq +229 -> 254
    //   28: aload_0
    //   29: aload_0
    //   30: getfield 494	android/inputmethodservice/InputMethodService:mExtractedToken	I
    //   33: iconst_1
    //   34: iadd
    //   35: putfield 494	android/inputmethodservice/InputMethodService:mExtractedToken	I
    //   38: new 1090	android/view/inputmethod/ExtractedTextRequest
    //   41: dup
    //   42: invokespecial 1091	android/view/inputmethod/ExtractedTextRequest:<init>	()V
    //   45: astore 7
    //   47: aload 7
    //   49: aload_0
    //   50: getfield 494	android/inputmethodservice/InputMethodService:mExtractedToken	I
    //   53: putfield 1094	android/view/inputmethod/ExtractedTextRequest:token	I
    //   56: aload 7
    //   58: iconst_1
    //   59: putfield 1095	android/view/inputmethod/ExtractedTextRequest:flags	I
    //   62: aload 7
    //   64: bipush 10
    //   66: putfield 1098	android/view/inputmethod/ExtractedTextRequest:hintMaxLines	I
    //   69: aload 7
    //   71: sipush 10000
    //   74: putfield 1101	android/view/inputmethod/ExtractedTextRequest:hintMaxChars	I
    //   77: aload_0
    //   78: invokevirtual 244	android/inputmethodservice/InputMethodService:getCurrentInputConnection	()Landroid/view/inputmethod/InputConnection;
    //   81: astore 6
    //   83: aload 6
    //   85: ifnonnull +170 -> 255
    //   88: aload_0
    //   89: aload 4
    //   91: putfield 452	android/inputmethodservice/InputMethodService:mExtractedText	Landroid/view/inputmethod/ExtractedText;
    //   94: aload_0
    //   95: getfield 452	android/inputmethodservice/InputMethodService:mExtractedText	Landroid/view/inputmethod/ExtractedText;
    //   98: ifnull +8 -> 106
    //   101: aload 6
    //   103: ifnonnull +43 -> 146
    //   106: ldc 39
    //   108: new 337	java/lang/StringBuilder
    //   111: dup
    //   112: invokespecial 338	java/lang/StringBuilder:<init>	()V
    //   115: ldc_w 1103
    //   118: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: aload_0
    //   122: getfield 452	android/inputmethodservice/InputMethodService:mExtractedText	Landroid/view/inputmethod/ExtractedText;
    //   125: invokevirtual 347	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   128: ldc_w 1105
    //   131: invokevirtual 344	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: aload 6
    //   136: invokevirtual 347	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   139: invokevirtual 353	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   142: invokestatic 1108	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   145: pop
    //   146: aload_0
    //   147: invokevirtual 248	android/inputmethodservice/InputMethodService:getCurrentInputEditorInfo	()Landroid/view/inputmethod/EditorInfo;
    //   150: astore 4
    //   152: aload 5
    //   154: invokevirtual 988	android/inputmethodservice/ExtractEditText:startInternalChanges	()V
    //   157: aload_0
    //   158: aload 4
    //   160: invokevirtual 1110	android/inputmethodservice/InputMethodService:onUpdateExtractingVisibility	(Landroid/view/inputmethod/EditorInfo;)V
    //   163: aload_0
    //   164: aload 4
    //   166: invokevirtual 1112	android/inputmethodservice/InputMethodService:onUpdateExtractingViews	(Landroid/view/inputmethod/EditorInfo;)V
    //   169: aload 4
    //   171: getfield 908	android/view/inputmethod/EditorInfo:inputType	I
    //   174: istore_3
    //   175: iload_3
    //   176: istore_2
    //   177: iload_3
    //   178: bipush 15
    //   180: iand
    //   181: iconst_1
    //   182: if_icmpne +19 -> 201
    //   185: iload_3
    //   186: istore_2
    //   187: ldc_w 1113
    //   190: iload_3
    //   191: iand
    //   192: ifeq +9 -> 201
    //   195: iload_3
    //   196: ldc_w 1114
    //   199: ior
    //   200: istore_2
    //   201: aload 5
    //   203: iload_2
    //   204: invokevirtual 1117	android/inputmethodservice/ExtractEditText:setInputType	(I)V
    //   207: aload 5
    //   209: aload 4
    //   211: getfield 1120	android/view/inputmethod/EditorInfo:hintText	Ljava/lang/CharSequence;
    //   214: invokevirtual 1123	android/inputmethodservice/ExtractEditText:setHint	(Ljava/lang/CharSequence;)V
    //   217: aload_0
    //   218: getfield 452	android/inputmethodservice/InputMethodService:mExtractedText	Landroid/view/inputmethod/ExtractedText;
    //   221: ifnull +49 -> 270
    //   224: aload 5
    //   226: iconst_1
    //   227: invokevirtual 1126	android/inputmethodservice/ExtractEditText:setEnabled	(Z)V
    //   230: aload 5
    //   232: aload_0
    //   233: getfield 452	android/inputmethodservice/InputMethodService:mExtractedText	Landroid/view/inputmethod/ExtractedText;
    //   236: invokevirtual 950	android/inputmethodservice/ExtractEditText:setExtractedText	(Landroid/view/inputmethod/ExtractedText;)V
    //   239: aload 5
    //   241: invokevirtual 996	android/inputmethodservice/ExtractEditText:finishInternalChanges	()V
    //   244: iload_1
    //   245: ifeq +9 -> 254
    //   248: aload_0
    //   249: aload 4
    //   251: invokevirtual 1128	android/inputmethodservice/InputMethodService:onExtractingInputChanged	(Landroid/view/inputmethod/EditorInfo;)V
    //   254: return
    //   255: aload 6
    //   257: aload 7
    //   259: iconst_1
    //   260: invokeinterface 1132 3 0
    //   265: astore 4
    //   267: goto -179 -> 88
    //   270: aload 5
    //   272: iconst_0
    //   273: invokevirtual 1126	android/inputmethodservice/ExtractEditText:setEnabled	(Z)V
    //   276: aload 5
    //   278: ldc_w 1134
    //   281: invokevirtual 1135	android/inputmethodservice/ExtractEditText:setText	(Ljava/lang/CharSequence;)V
    //   284: goto -45 -> 239
    //   287: astore 4
    //   289: aload 5
    //   291: invokevirtual 996	android/inputmethodservice/ExtractEditText:finishInternalChanges	()V
    //   294: aload 4
    //   296: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	297	0	this	InputMethodService
    //   0	297	1	paramBoolean	boolean
    //   176	28	2	i	int
    //   174	26	3	j	int
    //   1	265	4	localObject1	Object
    //   287	8	4	localObject2	Object
    //   7	283	5	localExtractEditText	ExtractEditText
    //   81	175	6	localInputConnection	InputConnection
    //   45	213	7	localExtractedTextRequest	android.view.inputmethod.ExtractedTextRequest
    // Exception table:
    //   from	to	target	type
    //   152	175	287	finally
    //   201	239	287	finally
    //   270	284	287	finally
  }
  
  public void switchInputMethod(String paramString)
  {
    this.mImm.setInputMethod(this.mToken, paramString);
  }
  
  void updateCandidatesVisibility(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 0;; i = getCandidatesHiddenVisibility())
    {
      if (this.mCandidatesVisibility != i)
      {
        this.mCandidatesFrame.setVisibility(i);
        this.mCandidatesVisibility = i;
      }
      return;
    }
  }
  
  void updateExtractFrameVisibility()
  {
    int j = 1;
    int i;
    label26:
    boolean bool;
    label35:
    TypedArray localTypedArray;
    if (isFullscreenMode()) {
      if (this.mExtractViewHidden)
      {
        i = 4;
        this.mExtractFrame.setVisibility(i);
        if (this.mCandidatesVisibility != 0) {
          break label120;
        }
        bool = true;
        updateCandidatesVisibility(bool);
        if ((this.mWindowWasVisible) && (this.mFullscreenArea.getVisibility() != i))
        {
          localTypedArray = this.mThemeAttrs;
          if (i != 0) {
            break label125;
          }
        }
      }
    }
    for (;;)
    {
      j = localTypedArray.getResourceId(j, 0);
      if (j != 0) {
        this.mFullscreenArea.startAnimation(AnimationUtils.loadAnimation(this, j));
      }
      this.mFullscreenArea.setVisibility(i);
      return;
      i = 0;
      break;
      i = 0;
      this.mExtractFrame.setVisibility(8);
      break label26;
      label120:
      bool = false;
      break label35;
      label125:
      j = 2;
    }
  }
  
  public void updateFullscreenMode()
  {
    boolean bool1;
    int i;
    label25:
    Object localObject;
    if (this.mShowInputRequested)
    {
      bool1 = onEvaluateFullscreenMode();
      if (this.mLastShowInputRequested == this.mShowInputRequested) {
        break label84;
      }
      i = 1;
      if ((this.mIsFullscreen != bool1) || (!this.mFullscreenApplied)) {
        break label89;
      }
      if (i != 0)
      {
        localObject = this.mWindow.getWindow();
        if (!this.mShowInputRequested) {
          break label251;
        }
      }
    }
    label84:
    label89:
    label251:
    for (boolean bool2 = false;; bool2 = true)
    {
      onConfigureWindow((Window)localObject, bool1, bool2);
      this.mLastShowInputRequested = this.mShowInputRequested;
      return;
      bool1 = false;
      break;
      i = 0;
      break label25;
      i = 1;
      this.mIsFullscreen = bool1;
      localObject = getCurrentInputConnection();
      if (localObject != null) {
        ((InputConnection)localObject).reportFullscreenMode(bool1);
      }
      this.mFullscreenApplied = true;
      initialize();
      localObject = (LinearLayout.LayoutParams)this.mFullscreenArea.getLayoutParams();
      if (bool1)
      {
        this.mFullscreenArea.setBackgroundDrawable(this.mThemeAttrs.getDrawable(0));
        ((LinearLayout.LayoutParams)localObject).height = 0;
      }
      for (((LinearLayout.LayoutParams)localObject).weight = 1.0F;; ((LinearLayout.LayoutParams)localObject).weight = 0.0F)
      {
        ((ViewGroup)this.mFullscreenArea.getParent()).updateViewLayout(this.mFullscreenArea, (ViewGroup.LayoutParams)localObject);
        if (bool1)
        {
          if (this.mExtractView == null)
          {
            localObject = onCreateExtractTextView();
            if (localObject != null) {
              setExtractView((View)localObject);
            }
          }
          startExtractingText(false);
        }
        updateExtractFrameVisibility();
        break;
        this.mFullscreenArea.setBackgroundDrawable(null);
        ((LinearLayout.LayoutParams)localObject).height = -2;
      }
    }
  }
  
  public void updateInputViewShown()
  {
    boolean bool;
    Object localObject;
    if (this.mShowInputRequested)
    {
      bool = onEvaluateInputViewShown();
      if ((this.mIsInputViewShown != bool) && (this.mWindowVisible))
      {
        this.mIsInputViewShown = bool;
        localObject = this.mInputFrame;
        if (!bool) {
          break label79;
        }
      }
    }
    label79:
    for (int i = 0;; i = 8)
    {
      ((FrameLayout)localObject).setVisibility(i);
      if (this.mInputView == null)
      {
        initialize();
        localObject = onCreateInputView();
        if (localObject != null) {
          setInputView((View)localObject);
        }
      }
      return;
      bool = false;
      break;
    }
  }
  
  public class InputMethodImpl
    extends AbstractInputMethodService.AbstractInputMethodImpl
  {
    public InputMethodImpl()
    {
      super();
    }
    
    public void attachToken(IBinder paramIBinder)
    {
      if (InputMethodService.this.mToken == null)
      {
        InputMethodService.this.mToken = paramIBinder;
        InputMethodService.this.mWindow.setToken(paramIBinder);
      }
    }
    
    public void bindInput(InputBinding paramInputBinding)
    {
      InputMethodService.this.mInputBinding = paramInputBinding;
      InputMethodService.this.mInputConnection = paramInputBinding.getConnection();
      paramInputBinding = InputMethodService.this.getCurrentInputConnection();
      if (paramInputBinding != null) {
        paramInputBinding.reportFullscreenMode(InputMethodService.this.mIsFullscreen);
      }
      InputMethodService.this.initialize();
      InputMethodService.this.onBindInput();
    }
    
    public void changeInputMethodSubtype(InputMethodSubtype paramInputMethodSubtype)
    {
      InputMethodService.this.onCurrentInputMethodSubtypeChanged(paramInputMethodSubtype);
    }
    
    public void hideSoftInput(int paramInt, ResultReceiver paramResultReceiver)
    {
      paramInt = 0;
      boolean bool = InputMethodService.this.isInputViewShown();
      InputMethodService.this.mShowInputFlags = 0;
      InputMethodService.this.mShowInputRequested = false;
      InputMethodService.-wrap2(InputMethodService.this);
      InputMethodService.-wrap1(InputMethodService.this);
      if (paramResultReceiver != null)
      {
        if (bool == InputMethodService.this.isInputViewShown()) {
          break label64;
        }
        paramInt = 3;
      }
      for (;;)
      {
        paramResultReceiver.send(paramInt, null);
        return;
        label64:
        if (!bool) {
          paramInt = 1;
        }
      }
    }
    
    public void restartInput(InputConnection paramInputConnection, EditorInfo paramEditorInfo)
    {
      InputMethodService.this.doStartInput(paramInputConnection, paramEditorInfo, true);
    }
    
    public void showSoftInput(int paramInt, ResultReceiver paramResultReceiver)
    {
      int i = 2;
      boolean bool1 = InputMethodService.this.isInputViewShown();
      if (InputMethodService.-wrap0(InputMethodService.this, paramInt, false)) {}
      try
      {
        InputMethodService.this.showWindow(true);
        InputMethodService.-wrap1(InputMethodService.this);
        boolean bool2 = InputMethodService.this.isInputViewShown();
        InputMethodManager localInputMethodManager = InputMethodService.this.mImm;
        IBinder localIBinder = InputMethodService.this.mToken;
        if (bool2)
        {
          paramInt = 2;
          localInputMethodManager.setImeWindowStatus(localIBinder, paramInt | 0x1, InputMethodService.this.mBackDisposition);
          if (paramResultReceiver != null)
          {
            if (bool1 == InputMethodService.this.isInputViewShown()) {
              break label119;
            }
            paramInt = i;
          }
        }
        for (;;)
        {
          paramResultReceiver.send(paramInt, null);
          return;
          paramInt = 0;
          break;
          label119:
          if (bool1) {
            paramInt = 0;
          } else {
            paramInt = 1;
          }
        }
      }
      catch (WindowManager.BadTokenException localBadTokenException)
      {
        for (;;) {}
      }
    }
    
    public void startInput(InputConnection paramInputConnection, EditorInfo paramEditorInfo)
    {
      InputMethodService.this.doStartInput(paramInputConnection, paramEditorInfo, false);
    }
    
    public void unbindInput()
    {
      InputMethodService.this.onUnbindInput();
      InputMethodService.this.mInputBinding = null;
      InputMethodService.this.mInputConnection = null;
    }
  }
  
  public class InputMethodSessionImpl
    extends AbstractInputMethodService.AbstractInputMethodSessionImpl
  {
    public InputMethodSessionImpl()
    {
      super();
    }
    
    public void appPrivateCommand(String paramString, Bundle paramBundle)
    {
      if (!isEnabled()) {
        return;
      }
      InputMethodService.this.onAppPrivateCommand(paramString, paramBundle);
    }
    
    public void displayCompletions(CompletionInfo[] paramArrayOfCompletionInfo)
    {
      if (!isEnabled()) {
        return;
      }
      InputMethodService.this.mCurCompletions = paramArrayOfCompletionInfo;
      InputMethodService.this.onDisplayCompletions(paramArrayOfCompletionInfo);
    }
    
    public void finishInput()
    {
      if (!isEnabled()) {
        return;
      }
      InputMethodService.this.doFinishInput();
    }
    
    public void toggleSoftInput(int paramInt1, int paramInt2)
    {
      InputMethodService.-wrap3(InputMethodService.this, paramInt1, paramInt2);
    }
    
    public void updateCursor(Rect paramRect)
    {
      if (!isEnabled()) {
        return;
      }
      InputMethodService.this.onUpdateCursor(paramRect);
    }
    
    public void updateCursorAnchorInfo(CursorAnchorInfo paramCursorAnchorInfo)
    {
      if (!isEnabled()) {
        return;
      }
      InputMethodService.this.onUpdateCursorAnchorInfo(paramCursorAnchorInfo);
    }
    
    public void updateExtractedText(int paramInt, ExtractedText paramExtractedText)
    {
      if (!isEnabled()) {
        return;
      }
      InputMethodService.this.onUpdateExtractedText(paramInt, paramExtractedText);
    }
    
    public void updateSelection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      if (!isEnabled()) {
        return;
      }
      InputMethodService.this.onUpdateSelection(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    
    public void viewClicked(boolean paramBoolean)
    {
      if (!isEnabled()) {
        return;
      }
      InputMethodService.this.onViewClicked(paramBoolean);
    }
  }
  
  public static final class Insets
  {
    public static final int TOUCHABLE_INSETS_CONTENT = 1;
    public static final int TOUCHABLE_INSETS_FRAME = 0;
    public static final int TOUCHABLE_INSETS_REGION = 3;
    public static final int TOUCHABLE_INSETS_VISIBLE = 2;
    public int contentTopInsets;
    public int touchableInsets;
    public final Region touchableRegion = new Region();
    public int visibleTopInsets;
  }
  
  private static final class SettingsObserver
    extends ContentObserver
  {
    private final InputMethodService mService;
    private int mShowImeWithHardKeyboard = 0;
    
    private SettingsObserver(InputMethodService paramInputMethodService)
    {
      super();
      this.mService = paramInputMethodService;
    }
    
    public static SettingsObserver createAndRegister(InputMethodService paramInputMethodService)
    {
      SettingsObserver localSettingsObserver = new SettingsObserver(paramInputMethodService);
      paramInputMethodService.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("show_ime_with_hard_keyboard"), false, localSettingsObserver);
      return localSettingsObserver;
    }
    
    private boolean shouldShowImeWithHardKeyboard()
    {
      if (this.mShowImeWithHardKeyboard == 0) {
        if (Settings.Secure.getInt(this.mService.getContentResolver(), "show_ime_with_hard_keyboard", 0) == 0) {
          break label86;
        }
      }
      label86:
      for (int i = 2;; i = 1)
      {
        this.mShowImeWithHardKeyboard = i;
        switch (this.mShowImeWithHardKeyboard)
        {
        default: 
          Log.e("InputMethodService", "Unexpected mShowImeWithHardKeyboard=" + this.mShowImeWithHardKeyboard);
          return false;
        }
      }
      return true;
      return false;
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (Settings.Secure.getUriFor("show_ime_with_hard_keyboard").equals(paramUri)) {
        if (Settings.Secure.getInt(this.mService.getContentResolver(), "show_ime_with_hard_keyboard", 0) == 0) {
          break label43;
        }
      }
      label43:
      for (int i = 2;; i = 1)
      {
        this.mShowImeWithHardKeyboard = i;
        InputMethodService.-wrap4(this.mService);
        return;
      }
    }
    
    public String toString()
    {
      return "SettingsObserver{mShowImeWithHardKeyboard=" + this.mShowImeWithHardKeyboard + "}";
    }
    
    void unregister()
    {
      this.mService.getContentResolver().unregisterContentObserver(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/inputmethodservice/InputMethodService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */