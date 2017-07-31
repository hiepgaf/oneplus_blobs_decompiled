package com.oneplus.camera;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.widget.ViewUtils;

final class DialogManagerImpl
  extends UIComponent
  implements DialogManager
{
  private static final DialogManager.DialogParams DEFAULT_DIALOG_PARAMS = new DialogManager.DialogParams();
  private DialogHandle m_CurrentHandle;
  private final DialogInterface.OnKeyListener m_DefaultKeyListener = new DialogInterface.OnKeyListener()
  {
    public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      switch (paramAnonymousInt)
      {
      default: 
        return false;
      }
      return true;
    }
  };
  private boolean m_IsUpdatingDialogRotation;
  private int m_MaxDefaultLandDialogWidth;
  private int m_MaxDefaultPortDialogWidth;
  
  DialogManagerImpl(CameraActivity paramCameraActivity)
  {
    super("Dialog manager", paramCameraActivity, false);
    enablePropertyLogs(PROP_HAS_DIALOG, 1);
  }
  
  private void dismissDialog(DialogHandle paramDialogHandle, boolean paramBoolean)
  {
    if ((paramDialogHandle == null) || (this.m_CurrentHandle != paramDialogHandle)) {
      return;
    }
    if (paramBoolean) {
      paramDialogHandle.dialog.cancel();
    }
    for (;;)
    {
      this.m_CurrentHandle = null;
      this.m_IsUpdatingDialogRotation = false;
      setReadOnly(PROP_HAS_DIALOG, Boolean.valueOf(false));
      return;
      paramDialogHandle.dialog.dismiss();
    }
  }
  
  private boolean showDialog(final DialogHandle paramDialogHandle)
  {
    if (!isRunningOrInitializing(true)) {
      return false;
    }
    Log.v(this.TAG, "showDialog() - Handle : ", paramDialogHandle);
    if (Handle.isValid(this.m_CurrentHandle))
    {
      Log.v(this.TAG, "showDialog() - Dismiss current dialog");
      dismissDialog(this.m_CurrentHandle, true);
    }
    Dialog localDialog = paramDialogHandle.dialog;
    localDialog.show();
    Window localWindow = localDialog.getWindow();
    localWindow.addFlags(1024);
    if (getRotation().isLandscape()) {
      if (paramDialogHandle.landscapeParams == null) {
        localObject1 = DEFAULT_DIALOG_PARAMS;
      }
    }
    DialogContainer localDialogContainer;
    ViewGroup localViewGroup;
    Object localObject2;
    for (;;)
    {
      localDialogContainer = new DialogContainer(getCameraActivity(), paramDialogHandle);
      localViewGroup = (ViewGroup)paramDialogHandle.dialog.findViewById(16908290);
      localObject2 = localViewGroup.getChildAt(0);
      if (localObject2 != null) {
        break;
      }
      Log.e(this.TAG, "showDialog() - No dialog view");
      localDialog.dismiss();
      return false;
      localObject1 = paramDialogHandle.landscapeParams;
      continue;
      if (paramDialogHandle.portraitParams == null) {
        localObject1 = DEFAULT_DIALOG_PARAMS;
      } else {
        localObject1 = paramDialogHandle.portraitParams;
      }
    }
    localViewGroup.removeView((View)localObject2);
    Object localObject1 = new RelativeLayout.LayoutParams(((DialogManager.DialogParams)localObject1).width, ((DialogManager.DialogParams)localObject1).height);
    FrameLayout localFrameLayout = new FrameLayout(getCameraActivity());
    localFrameLayout.addView((View)localObject2, new FrameLayout.LayoutParams(-1, -2));
    ((RelativeLayout.LayoutParams)localObject1).addRule(13);
    localDialogContainer.addView(localFrameLayout, (ViewGroup.LayoutParams)localObject1);
    localDialogContainer.setTag(paramDialogHandle);
    localViewGroup.setBackgroundColor(0);
    localViewGroup.addView(localDialogContainer, new FrameLayout.LayoutParams(-1, -2));
    localViewGroup.setClipChildren(false);
    for (localObject1 = localViewGroup.getParent(); (localObject1 instanceof ViewGroup); localObject1 = ((View)localObject1).getParent()) {
      ((ViewGroup)localObject1).setClipChildren(false);
    }
    paramDialogHandle.container = localDialogContainer;
    paramDialogHandle.dialogView = ((View)localObject2);
    if (((View)localObject2).getBackground() == null)
    {
      localObject1 = localDialog.getContext().obtainStyledAttributes(new int[] { 16842836, 16843920 });
      localObject2 = ((TypedArray)localObject1).getDrawable(0);
      if (localObject2 == null) {
        break label478;
      }
      localWindow.setBackgroundDrawable(null);
      localWindow.setClipToOutline(false);
      localFrameLayout.setBackground((Drawable)localObject2);
      localFrameLayout.setElevation(((TypedArray)localObject1).getDimensionPixelSize(1, 0));
    }
    for (;;)
    {
      localObject1 = new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return false;
        }
      };
      localViewGroup.setOnTouchListener((View.OnTouchListener)localObject1);
      localDialogContainer.setOnTouchListener((View.OnTouchListener)localObject1);
      localDialogContainer.setRotation(getRotation());
      if ((paramDialogHandle.flags & 0x1) == 0) {
        localDialog.setOnKeyListener(this.m_DefaultKeyListener);
      }
      localDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface paramAnonymousDialogInterface)
        {
          if ((DialogManagerImpl.-get2(DialogManagerImpl.this)) && (DialogManagerImpl.-get1(DialogManagerImpl.this) == paramDialogHandle))
          {
            DialogManagerImpl.-set1(DialogManagerImpl.this, false);
            if (DialogManagerImpl.this.getRotation().isLandscape()) {
              if (paramDialogHandle.landscapeParams == null) {
                paramAnonymousDialogInterface = DialogManagerImpl.-get0();
              }
            }
            for (;;)
            {
              ViewUtils.setSize(paramDialogHandle.dialogView, paramAnonymousDialogInterface.width, paramAnonymousDialogInterface.height);
              paramDialogHandle.container.setRotation(DialogManagerImpl.this.getRotation());
              paramDialogHandle.dialog.show();
              return;
              paramAnonymousDialogInterface = paramDialogHandle.landscapeParams;
              continue;
              if (paramDialogHandle.portraitParams == null) {
                paramAnonymousDialogInterface = DialogManagerImpl.-get0();
              } else {
                paramAnonymousDialogInterface = paramDialogHandle.portraitParams;
              }
            }
          }
          paramDialogHandle.complete();
          if (DialogManagerImpl.-get1(DialogManagerImpl.this) == paramDialogHandle)
          {
            DialogManagerImpl.-set0(DialogManagerImpl.this, null);
            DialogManagerImpl.-wrap0(DialogManagerImpl.this, DialogManagerImpl.PROP_HAS_DIALOG, Boolean.valueOf(false));
          }
          if (paramDialogHandle.dismissListener != null) {
            paramDialogHandle.dismissListener.onDismiss(paramAnonymousDialogInterface);
          }
        }
      });
      this.m_CurrentHandle = paramDialogHandle;
      setReadOnly(PROP_HAS_DIALOG, Boolean.valueOf(true));
      return true;
      label478:
      Log.w(this.TAG, "showDialog() - Fail to get original dialog background");
    }
  }
  
  private void updateDefaultDialogParams(ScreenSize paramScreenSize)
  {
    this.m_MaxDefaultPortDialogWidth = Math.min(paramScreenSize.getWidth(), paramScreenSize.getHeight());
    this.m_MaxDefaultLandDialogWidth = this.m_MaxDefaultPortDialogWidth;
  }
  
  protected void onDeinitialize()
  {
    dismissDialog(this.m_CurrentHandle, true);
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addCallback(CameraActivity.PROP_IS_RUNNING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          DialogManagerImpl.-wrap1(DialogManagerImpl.this, DialogManagerImpl.-get1(DialogManagerImpl.this), true);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_SCREEN_SIZE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<ScreenSize> paramAnonymousPropertyKey, PropertyChangeEventArgs<ScreenSize> paramAnonymousPropertyChangeEventArgs)
      {
        DialogManagerImpl.-wrap2(DialogManagerImpl.this, (ScreenSize)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    updateDefaultDialogParams(getScreenSize());
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    super.onRotationChanged(paramRotation1, paramRotation2);
    if (this.m_CurrentHandle != null)
    {
      this.m_IsUpdatingDialogRotation = true;
      this.m_CurrentHandle.dialog.dismiss();
    }
  }
  
  public Handle showDialog(Dialog paramDialog, DialogInterface.OnDismissListener paramOnDismissListener, DialogManager.DialogParams paramDialogParams1, DialogManager.DialogParams paramDialogParams2, int paramInt)
  {
    if (paramDialog == null)
    {
      Log.e(this.TAG, "showDialog() - No dialog");
      return null;
    }
    verifyAccess();
    if (!isRunningOrInitializing(true)) {
      return null;
    }
    if (paramDialogParams1 == null)
    {
      paramDialogParams1 = null;
      if (paramDialogParams2 != null) {
        break label78;
      }
    }
    label78:
    for (paramDialogParams2 = null;; paramDialogParams2 = paramDialogParams2.clone())
    {
      paramDialog = new DialogHandle(paramDialog, paramOnDismissListener, paramDialogParams1, paramDialogParams2, paramInt);
      if (showDialog(paramDialog)) {
        return paramDialog;
      }
      return null;
      paramDialogParams1 = paramDialogParams1.clone();
      break;
    }
    return paramDialog;
  }
  
  private final class DialogContainer
    extends RelativeLayout
  {
    private final DialogManagerImpl.DialogHandle m_DialogHandle;
    private Rotation m_Rotation;
    
    public DialogContainer(Context paramContext, DialogManagerImpl.DialogHandle paramDialogHandle)
    {
      super();
      this.m_DialogHandle = paramDialogHandle;
      this.m_Rotation = DialogManagerImpl.this.getCameraActivityRotation();
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (getChildCount() == 0) {
        return;
      }
      View localView = getChildAt(0);
      int i = localView.getMeasuredWidth();
      int j = localView.getMeasuredHeight();
      paramInt1 += (paramInt3 - paramInt1 - i) / 2;
      paramInt2 += (paramInt4 - paramInt2 - j) / 2;
      localView.layout(paramInt1, paramInt2, paramInt1 + i, paramInt2 + j);
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      DialogManager.DialogParams localDialogParams1;
      DialogManager.DialogParams localDialogParams2;
      int i;
      label59:
      int j;
      if (this.m_Rotation.isLandscape())
      {
        localDialogParams1 = this.m_DialogHandle.landscapeParams;
        localDialogParams2 = localDialogParams1;
        if (localDialogParams1 == null) {
          localDialogParams2 = DialogManagerImpl.-get0();
        }
        if (localDialogParams2.maxWidth >= 0) {
          break label169;
        }
        if (!this.m_Rotation.isLandscape()) {
          break label158;
        }
        i = DialogManagerImpl.-get3(DialogManagerImpl.this);
        if (this.m_Rotation.isLandscape() != DialogManagerImpl.this.getCameraActivityRotation().isLandscape()) {
          break label178;
        }
        j = i;
        if (View.MeasureSpec.getMode(paramInt1) != 0) {
          j = Math.min(i, View.MeasureSpec.getSize(paramInt1));
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(j, View.MeasureSpec.getMode(paramInt1)), paramInt2);
      }
      for (;;)
      {
        if (this.m_Rotation.isLandscape() != DialogManagerImpl.this.getCameraActivityRotation().isLandscape()) {
          break label215;
        }
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        return;
        localDialogParams1 = this.m_DialogHandle.portraitParams;
        break;
        label158:
        i = DialogManagerImpl.-get4(DialogManagerImpl.this);
        break label59;
        label169:
        i = localDialogParams2.maxWidth;
        break label59;
        label178:
        j = i;
        if (View.MeasureSpec.getMode(paramInt2) != 0) {
          j = Math.min(i, View.MeasureSpec.getSize(paramInt2));
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(j, View.MeasureSpec.getMode(paramInt2)), paramInt1);
      }
      label215:
      setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }
    
    public void setRotation(Rotation paramRotation)
    {
      if (this.m_Rotation != paramRotation)
      {
        this.m_Rotation = paramRotation;
        super.setRotation(DialogManagerImpl.this.getCameraActivityRotation().getDeviceOrientation() - paramRotation.getDeviceOrientation());
        requestLayout();
      }
    }
  }
  
  private final class DialogHandle
    extends Handle
  {
    public DialogManagerImpl.DialogContainer container;
    public final Dialog dialog;
    public View dialogView;
    public final DialogInterface.OnDismissListener dismissListener;
    public final int flags;
    public final DialogManager.DialogParams landscapeParams;
    public final DialogManager.DialogParams portraitParams;
    
    public DialogHandle(Dialog paramDialog, DialogInterface.OnDismissListener paramOnDismissListener, DialogManager.DialogParams paramDialogParams1, DialogManager.DialogParams paramDialogParams2, int paramInt)
    {
      super();
      this.dialog = paramDialog;
      this.dismissListener = paramOnDismissListener;
      this.flags = paramInt;
      this.portraitParams = paramDialogParams1;
      this.landscapeParams = paramDialogParams2;
    }
    
    public void complete()
    {
      closeDirectly();
    }
    
    protected void onClose(int paramInt)
    {
      DialogManagerImpl.-wrap3(DialogManagerImpl.this);
      DialogManagerImpl.-wrap1(DialogManagerImpl.this, this, false);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/DialogManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */