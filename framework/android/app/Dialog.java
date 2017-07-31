package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.Window.Callback;
import android.view.Window.OnWindowDismissedCallback;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.app.WindowDecorActionBar;
import com.android.internal.policy.PhoneWindow;
import java.lang.ref.WeakReference;

public class Dialog
  implements DialogInterface, Window.Callback, KeyEvent.Callback, View.OnCreateContextMenuListener, Window.OnWindowDismissedCallback
{
  private static final int CANCEL = 68;
  private static final String DIALOG_HIERARCHY_TAG = "android:dialogHierarchy";
  private static final String DIALOG_SHOWING_TAG = "android:dialogShowing";
  private static final int DISMISS = 67;
  private static final int SHOW = 69;
  private static final String TAG = "Dialog";
  private ActionBar mActionBar;
  private ActionMode mActionMode;
  private int mActionModeTypeStarting = 0;
  private String mCancelAndDismissTaken;
  private Message mCancelMessage;
  protected boolean mCancelable = true;
  private boolean mCanceled = false;
  final Context mContext;
  private boolean mCreated = false;
  View mDecor;
  private final Runnable mDismissAction = new -void__init__android_content_Context_context_int_themeResId_boolean_createContextThemeWrapper_LambdaImpl0();
  private Message mDismissMessage;
  private final Handler mHandler = new Handler();
  private final Handler mListenersHandler;
  private DialogInterface.OnKeyListener mOnKeyListener;
  private Activity mOwnerActivity;
  private SearchEvent mSearchEvent;
  private Message mShowMessage;
  private boolean mShowing = false;
  final Window mWindow;
  private final WindowManager mWindowManager;
  
  public Dialog(Context paramContext)
  {
    this(paramContext, 0, true);
  }
  
  public Dialog(Context paramContext, int paramInt)
  {
    this(paramContext, paramInt, true);
  }
  
  Dialog(Context paramContext, int paramInt, boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = paramInt;
      if (paramInt == 0)
      {
        TypedValue localTypedValue = new TypedValue();
        paramContext.getTheme().resolveAttribute(16843528, localTypedValue, true);
        i = localTypedValue.resourceId;
      }
    }
    for (this.mContext = new ContextThemeWrapper(paramContext, i);; this.mContext = paramContext)
    {
      this.mWindowManager = ((WindowManager)paramContext.getSystemService("window"));
      paramContext = new PhoneWindow(this.mContext);
      this.mWindow = paramContext;
      paramContext.setCallback(this);
      paramContext.setOnWindowDismissedCallback(this);
      paramContext.setWindowManager(this.mWindowManager, null, null);
      paramContext.setGravity(17);
      this.mListenersHandler = new ListenersHandler(this);
      return;
    }
  }
  
  protected Dialog(Context paramContext, boolean paramBoolean, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    this(paramContext);
    setOnCancelListener(paramOnCancelListener);
  }
  
  @Deprecated
  protected Dialog(Context paramContext, boolean paramBoolean, Message paramMessage)
  {
    this(paramContext);
    this.mCancelMessage = paramMessage;
  }
  
  private ComponentName getAssociatedActivity()
  {
    Activity localActivity = this.mOwnerActivity;
    Context localContext = getContext();
    while ((localActivity == null) && (localContext != null)) {
      if ((localContext instanceof Activity)) {
        localActivity = (Activity)localContext;
      } else if ((localContext instanceof ContextWrapper)) {
        localContext = ((ContextWrapper)localContext).getBaseContext();
      } else {
        localContext = null;
      }
    }
    if (localActivity == null) {
      return null;
    }
    return localActivity.getComponentName();
  }
  
  private void sendDismissMessage()
  {
    if (this.mDismissMessage != null) {
      Message.obtain(this.mDismissMessage).sendToTarget();
    }
  }
  
  private void sendShowMessage()
  {
    if (this.mShowMessage != null) {
      Message.obtain(this.mShowMessage).sendToTarget();
    }
  }
  
  public void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    this.mWindow.addContentView(paramView, paramLayoutParams);
  }
  
  public void cancel()
  {
    if ((!this.mCanceled) && (this.mCancelMessage != null))
    {
      this.mCanceled = true;
      Message.obtain(this.mCancelMessage).sendToTarget();
    }
    dismiss();
  }
  
  public void closeOptionsMenu()
  {
    if (this.mWindow.hasFeature(0)) {
      this.mWindow.closePanel(0);
    }
  }
  
  public void create()
  {
    if (!this.mCreated) {
      dispatchOnCreate(null);
    }
  }
  
  public void dismiss()
  {
    if (Looper.myLooper() == this.mHandler.getLooper())
    {
      dismissDialog();
      return;
    }
    this.mHandler.post(this.mDismissAction);
  }
  
  void dismissDialog()
  {
    if ((this.mDecor != null) && (this.mShowing))
    {
      if (this.mWindow.isDestroyed()) {
        Log.e("Dialog", "Tried to dismissDialog() but the Dialog's window was already destroyed!");
      }
    }
    else {
      return;
    }
    try
    {
      this.mWindowManager.removeViewImmediate(this.mDecor);
      return;
    }
    finally
    {
      if (this.mActionMode != null) {
        this.mActionMode.finish();
      }
      this.mDecor = null;
      this.mWindow.closeAllPanels();
      onStop();
      this.mShowing = false;
      sendDismissMessage();
    }
  }
  
  public boolean dispatchGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (this.mWindow.superDispatchGenericMotionEvent(paramMotionEvent)) {
      return true;
    }
    return onGenericMotionEvent(paramMotionEvent);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    KeyEvent.DispatcherState localDispatcherState = null;
    if ((this.mOnKeyListener != null) && (this.mOnKeyListener.onKey(this, paramKeyEvent.getKeyCode(), paramKeyEvent))) {
      return true;
    }
    if (this.mWindow.superDispatchKeyEvent(paramKeyEvent)) {
      return true;
    }
    if (this.mDecor != null) {
      localDispatcherState = this.mDecor.getKeyDispatcherState();
    }
    return paramKeyEvent.dispatch(this, localDispatcherState, this);
  }
  
  public boolean dispatchKeyShortcutEvent(KeyEvent paramKeyEvent)
  {
    if (this.mWindow.superDispatchKeyShortcutEvent(paramKeyEvent)) {
      return true;
    }
    return onKeyShortcut(paramKeyEvent.getKeyCode(), paramKeyEvent);
  }
  
  void dispatchOnCreate(Bundle paramBundle)
  {
    if (!this.mCreated)
    {
      onCreate(paramBundle);
      this.mCreated = true;
    }
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    paramAccessibilityEvent.setClassName(getClass().getName());
    paramAccessibilityEvent.setPackageName(this.mContext.getPackageName());
    WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
    boolean bool;
    if (localLayoutParams.width == -1) {
      if (localLayoutParams.height == -1) {
        bool = true;
      }
    }
    for (;;)
    {
      paramAccessibilityEvent.setFullScreen(bool);
      return false;
      bool = false;
      continue;
      bool = false;
    }
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mWindow.superDispatchTouchEvent(paramMotionEvent)) {
      return true;
    }
    return onTouchEvent(paramMotionEvent);
  }
  
  public boolean dispatchTrackballEvent(MotionEvent paramMotionEvent)
  {
    if (this.mWindow.superDispatchTrackballEvent(paramMotionEvent)) {
      return true;
    }
    return onTrackballEvent(paramMotionEvent);
  }
  
  public View findViewById(int paramInt)
  {
    return this.mWindow.findViewById(paramInt);
  }
  
  public ActionBar getActionBar()
  {
    return this.mActionBar;
  }
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  public View getCurrentFocus()
  {
    View localView = null;
    if (this.mWindow != null) {
      localView = this.mWindow.getCurrentFocus();
    }
    return localView;
  }
  
  public LayoutInflater getLayoutInflater()
  {
    return getWindow().getLayoutInflater();
  }
  
  public final Activity getOwnerActivity()
  {
    return this.mOwnerActivity;
  }
  
  public final SearchEvent getSearchEvent()
  {
    return this.mSearchEvent;
  }
  
  public final int getVolumeControlStream()
  {
    return getWindow().getVolumeControlStream();
  }
  
  public Window getWindow()
  {
    return this.mWindow;
  }
  
  public void hide()
  {
    if (this.mDecor != null) {
      this.mDecor.setVisibility(8);
    }
  }
  
  public void invalidateOptionsMenu()
  {
    if (this.mWindow.hasFeature(0)) {
      this.mWindow.invalidatePanelMenu(0);
    }
  }
  
  public boolean isShowing()
  {
    return this.mShowing;
  }
  
  public void onActionModeFinished(ActionMode paramActionMode)
  {
    if (paramActionMode == this.mActionMode) {
      this.mActionMode = null;
    }
  }
  
  public void onActionModeStarted(ActionMode paramActionMode)
  {
    this.mActionMode = paramActionMode;
  }
  
  public void onAttachedToWindow() {}
  
  public void onBackPressed()
  {
    if (this.mCancelable) {
      cancel();
    }
  }
  
  public void onContentChanged() {}
  
  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }
  
  public void onContextMenuClosed(Menu paramMenu) {}
  
  protected void onCreate(Bundle paramBundle) {}
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo) {}
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    return true;
  }
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    if (paramInt == 0) {
      return onCreateOptionsMenu(paramMenu);
    }
    return false;
  }
  
  public View onCreatePanelView(int paramInt)
  {
    return null;
  }
  
  public void onDetachedFromWindow() {}
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4)
    {
      paramKeyEvent.startTracking();
      return true;
    }
    return false;
  }
  
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt != 4) || (!paramKeyEvent.isTracking()) || (paramKeyEvent.isCanceled())) {
      return false;
    }
    onBackPressed();
    return true;
  }
  
  public boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem)
  {
    return false;
  }
  
  public boolean onMenuOpened(int paramInt, Menu paramMenu)
  {
    if (paramInt == 8) {
      this.mActionBar.dispatchMenuVisibilityChanged(true);
    }
    return true;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }
  
  public void onOptionsMenuClosed(Menu paramMenu) {}
  
  public void onPanelClosed(int paramInt, Menu paramMenu)
  {
    if (paramInt == 8) {
      this.mActionBar.dispatchMenuVisibilityChanged(false);
    }
  }
  
  public boolean onPrepareOptionsMenu(Menu paramMenu)
  {
    return true;
  }
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    boolean bool = false;
    if ((paramInt == 0) && (paramMenu != null))
    {
      if (onPrepareOptionsMenu(paramMenu)) {
        bool = paramMenu.hasVisibleItems();
      }
      return bool;
    }
    return true;
  }
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    Bundle localBundle = paramBundle.getBundle("android:dialogHierarchy");
    if (localBundle == null) {
      return;
    }
    dispatchOnCreate(paramBundle);
    this.mWindow.restoreHierarchyState(localBundle);
    if (paramBundle.getBoolean("android:dialogShowing")) {
      show();
    }
  }
  
  public Bundle onSaveInstanceState()
  {
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("android:dialogShowing", this.mShowing);
    if (this.mCreated) {
      localBundle.putBundle("android:dialogHierarchy", this.mWindow.saveHierarchyState());
    }
    return localBundle;
  }
  
  public boolean onSearchRequested()
  {
    SearchManager localSearchManager = (SearchManager)this.mContext.getSystemService("search");
    ComponentName localComponentName = getAssociatedActivity();
    if ((localComponentName != null) && (localSearchManager.getSearchableInfo(localComponentName) != null))
    {
      localSearchManager.startSearch(null, false, localComponentName, null, false);
      dismiss();
      return true;
    }
    return false;
  }
  
  public boolean onSearchRequested(SearchEvent paramSearchEvent)
  {
    this.mSearchEvent = paramSearchEvent;
    return onSearchRequested();
  }
  
  protected void onStart()
  {
    if (this.mActionBar != null) {
      this.mActionBar.setShowHideAnimationEnabled(true);
    }
  }
  
  protected void onStop()
  {
    if (this.mActionBar != null) {
      this.mActionBar.setShowHideAnimationEnabled(false);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mCancelable) && (this.mShowing) && (this.mWindow.shouldCloseOnTouch(this.mContext, paramMotionEvent)))
    {
      cancel();
      return true;
    }
    return false;
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public void onWindowAttributesChanged(WindowManager.LayoutParams paramLayoutParams)
  {
    if (this.mDecor != null) {
      this.mWindowManager.updateViewLayout(this.mDecor, paramLayoutParams);
    }
  }
  
  public void onWindowDismissed(boolean paramBoolean)
  {
    dismiss();
  }
  
  public void onWindowFocusChanged(boolean paramBoolean) {}
  
  public ActionMode onWindowStartingActionMode(ActionMode.Callback paramCallback)
  {
    if ((this.mActionBar != null) && (this.mActionModeTypeStarting == 0)) {
      return this.mActionBar.startActionMode(paramCallback);
    }
    return null;
  }
  
  public ActionMode onWindowStartingActionMode(ActionMode.Callback paramCallback, int paramInt)
  {
    try
    {
      this.mActionModeTypeStarting = paramInt;
      paramCallback = onWindowStartingActionMode(paramCallback);
      return paramCallback;
    }
    finally
    {
      this.mActionModeTypeStarting = 0;
    }
  }
  
  public void openContextMenu(View paramView)
  {
    paramView.showContextMenu();
  }
  
  public void openOptionsMenu()
  {
    if (this.mWindow.hasFeature(0)) {
      this.mWindow.openPanel(0, null);
    }
  }
  
  public void registerForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(this);
  }
  
  public final boolean requestWindowFeature(int paramInt)
  {
    return getWindow().requestFeature(paramInt);
  }
  
  public void setCancelMessage(Message paramMessage)
  {
    this.mCancelMessage = paramMessage;
  }
  
  public void setCancelable(boolean paramBoolean)
  {
    this.mCancelable = paramBoolean;
  }
  
  public void setCanceledOnTouchOutside(boolean paramBoolean)
  {
    if ((!paramBoolean) || (this.mCancelable)) {}
    for (;;)
    {
      this.mWindow.setCloseOnTouchOutside(paramBoolean);
      return;
      this.mCancelable = true;
    }
  }
  
  public void setContentView(int paramInt)
  {
    this.mWindow.setContentView(paramInt);
  }
  
  public void setContentView(View paramView)
  {
    this.mWindow.setContentView(paramView);
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    this.mWindow.setContentView(paramView, paramLayoutParams);
  }
  
  public void setDismissMessage(Message paramMessage)
  {
    this.mDismissMessage = paramMessage;
  }
  
  public final void setFeatureDrawable(int paramInt, Drawable paramDrawable)
  {
    getWindow().setFeatureDrawable(paramInt, paramDrawable);
  }
  
  public final void setFeatureDrawableAlpha(int paramInt1, int paramInt2)
  {
    getWindow().setFeatureDrawableAlpha(paramInt1, paramInt2);
  }
  
  public final void setFeatureDrawableResource(int paramInt1, int paramInt2)
  {
    getWindow().setFeatureDrawableResource(paramInt1, paramInt2);
  }
  
  public final void setFeatureDrawableUri(int paramInt, Uri paramUri)
  {
    getWindow().setFeatureDrawableUri(paramInt, paramUri);
  }
  
  public void setOnCancelListener(DialogInterface.OnCancelListener paramOnCancelListener)
  {
    if (this.mCancelAndDismissTaken != null) {
      throw new IllegalStateException("OnCancelListener is already taken by " + this.mCancelAndDismissTaken + " and can not be replaced.");
    }
    if (paramOnCancelListener != null)
    {
      this.mCancelMessage = this.mListenersHandler.obtainMessage(68, paramOnCancelListener);
      return;
    }
    this.mCancelMessage = null;
  }
  
  public void setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener)
  {
    if (this.mCancelAndDismissTaken != null) {
      throw new IllegalStateException("OnDismissListener is already taken by " + this.mCancelAndDismissTaken + " and can not be replaced.");
    }
    if (paramOnDismissListener != null)
    {
      this.mDismissMessage = this.mListenersHandler.obtainMessage(67, paramOnDismissListener);
      return;
    }
    this.mDismissMessage = null;
  }
  
  public void setOnKeyListener(DialogInterface.OnKeyListener paramOnKeyListener)
  {
    this.mOnKeyListener = paramOnKeyListener;
  }
  
  public void setOnShowListener(DialogInterface.OnShowListener paramOnShowListener)
  {
    if (paramOnShowListener != null)
    {
      this.mShowMessage = this.mListenersHandler.obtainMessage(69, paramOnShowListener);
      return;
    }
    this.mShowMessage = null;
  }
  
  public final void setOwnerActivity(Activity paramActivity)
  {
    this.mOwnerActivity = paramActivity;
    getWindow().setVolumeControlStream(this.mOwnerActivity.getVolumeControlStream());
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(this.mContext.getText(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mWindow.setTitle(paramCharSequence);
    this.mWindow.getAttributes().setTitle(paramCharSequence);
  }
  
  public final void setVolumeControlStream(int paramInt)
  {
    getWindow().setVolumeControlStream(paramInt);
  }
  
  public void show()
  {
    if (this.mShowing)
    {
      if (this.mDecor != null)
      {
        if (this.mWindow.hasFeature(8)) {
          this.mWindow.invalidatePanelMenu(8);
        }
        this.mDecor.setVisibility(0);
      }
      return;
    }
    this.mCanceled = false;
    if (!this.mCreated) {
      dispatchOnCreate(null);
    }
    for (;;)
    {
      onStart();
      this.mDecor = this.mWindow.getDecorView();
      if ((this.mActionBar == null) && (this.mWindow.hasFeature(8)))
      {
        localObject = this.mContext.getApplicationInfo();
        this.mWindow.setDefaultIcon(((ApplicationInfo)localObject).icon);
        this.mWindow.setDefaultLogo(((ApplicationInfo)localObject).logo);
        this.mActionBar = new WindowDecorActionBar(this);
      }
      WindowManager.LayoutParams localLayoutParams = this.mWindow.getAttributes();
      Object localObject = localLayoutParams;
      if ((localLayoutParams.softInputMode & 0x100) == 0)
      {
        localObject = new WindowManager.LayoutParams();
        ((WindowManager.LayoutParams)localObject).copyFrom(localLayoutParams);
        ((WindowManager.LayoutParams)localObject).softInputMode |= 0x100;
      }
      this.mWindowManager.addView(this.mDecor, (ViewGroup.LayoutParams)localObject);
      this.mShowing = true;
      sendShowMessage();
      return;
      localObject = this.mContext.getResources().getConfiguration();
      this.mWindow.getDecorView().dispatchConfigurationChanged((Configuration)localObject);
    }
  }
  
  public boolean takeCancelAndDismissListeners(String paramString, DialogInterface.OnCancelListener paramOnCancelListener, DialogInterface.OnDismissListener paramOnDismissListener)
  {
    if (this.mCancelAndDismissTaken != null) {
      this.mCancelAndDismissTaken = null;
    }
    while ((this.mCancelMessage == null) && (this.mDismissMessage == null))
    {
      setOnCancelListener(paramOnCancelListener);
      setOnDismissListener(paramOnDismissListener);
      this.mCancelAndDismissTaken = paramString;
      return true;
    }
    return false;
  }
  
  public void takeKeyEvents(boolean paramBoolean)
  {
    this.mWindow.takeKeyEvents(paramBoolean);
  }
  
  public void unregisterForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(null);
  }
  
  private static final class ListenersHandler
    extends Handler
  {
    private final WeakReference<DialogInterface> mDialog;
    
    public ListenersHandler(Dialog paramDialog)
    {
      this.mDialog = new WeakReference(paramDialog);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 67: 
        ((DialogInterface.OnDismissListener)paramMessage.obj).onDismiss((DialogInterface)this.mDialog.get());
        return;
      case 68: 
        ((DialogInterface.OnCancelListener)paramMessage.obj).onCancel((DialogInterface)this.mDialog.get());
        return;
      }
      ((DialogInterface.OnShowListener)paramMessage.obj).onShow((DialogInterface)this.mDialog.get());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/Dialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */