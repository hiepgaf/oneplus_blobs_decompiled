package com.oneplus.camera.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewStub;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.Settings;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraApplication;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.UIComponent.ViewRotationCallback;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.capturemode.PhotoCaptureMode;
import com.oneplus.camera.widget.RotateRelativeLayout;

final class CameraWizardImpl
  extends UIComponent
{
  private static final long DURATION_ANIMATION = 200L;
  private static final String SETTINGS_KEY_IS_WIZARD_SIMPLE_MODES_SWITCH = "Wizard.SimpleModesSwitch";
  private static final String SETTINGS_KEY_IS_WIZARD_SMILE_CAPTURE = "Wizard.SmileCapture";
  protected static final String[] SETTINGS_KEY_WIZARD_LIST = { "Wizard.SimpleModesSwitch", "Wizard.SmileCapture" };
  private CameraActivity m_CameraActivity;
  private View m_CameraWizardContainer;
  private CaptureModeManager m_CaptureModeManager;
  private CaptureModeSwitcher m_CaptureModeSwitcher;
  private Handle m_CaptureUIDisableHandle;
  private Settings m_Settings;
  private View m_SimpleModesSwitch;
  private Handle m_SmileCaptureUiDisableHandle;
  private View m_SmileCaptureView;
  private View m_TouchReceiver;
  
  CameraWizardImpl(CameraActivity paramCameraActivity)
  {
    super("Camera Wizard", paramCameraActivity, true);
  }
  
  private void hideCameraWizardContainer()
  {
    if (this.m_CameraWizardContainer != null) {
      setViewVisibility(this.m_CameraWizardContainer, false, 0L, null);
    }
  }
  
  private void inflateCameraWizardContainer()
  {
    if (this.m_CameraWizardContainer != null) {
      return;
    }
    this.m_CameraWizardContainer = this.m_CameraActivity.findViewById(2131361836);
    if ((this.m_CameraWizardContainer instanceof ViewStub))
    {
      this.m_CameraWizardContainer = ((ViewStub)this.m_CameraWizardContainer).inflate();
      this.m_TouchReceiver = this.m_CameraWizardContainer.findViewById(2131361914);
      this.m_TouchReceiver.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
    }
  }
  
  private void rotateSimpleModesSwitch(Rotation paramRotation)
  {
    View localView = this.m_CameraWizardContainer.findViewById(2131361917);
    int i = this.m_CameraActivity.getResources().getDimensionPixelSize(2131296603);
    switch (-getcom-oneplus-base-RotationSwitchesValues()[paramRotation.ordinal()])
    {
    default: 
      return;
    case 3: 
      setLayoutParams(localView, i, 0, 0, 0, 0, 0, new int[] { 9, 15 }, true);
      return;
    case 1: 
      setLayoutParams(localView, 0, 0, i, 0, 0, 0, new int[] { 11, 15 }, true);
      return;
    case 2: 
      setLayoutParams(localView, 0, 0, 0, i, 0, 0, new int[] { 12, 14 }, true);
      return;
    }
    setLayoutParams(localView, 0, i, 0, 0, 0, 0, new int[] { 14 }, true);
  }
  
  private void rotateSmileCaptureContent(Rotation paramRotation)
  {
    Object localObject;
    Resources localResources;
    boolean bool;
    int i;
    int j;
    int k;
    label89:
    int m;
    label106:
    int n;
    int i1;
    int i2;
    label143:
    int i3;
    int i4;
    int i5;
    int i6;
    label236:
    label247:
    label258:
    PointF localPointF;
    if (this.m_SmileCaptureView != null)
    {
      localObject = (TextView)this.m_SmileCaptureView.findViewById(2131361922);
      localResources = this.m_CameraActivity.getResources();
      bool = ((Boolean)CameraApplication.current().get(CameraApplication.PROP_IS_RTL_LAYOUT)).booleanValue();
      i = (int)localResources.getDimension(2131296608);
      j = (int)localResources.getDimension(2131296607);
      k = (int)localResources.getDimension(2131296606);
      if (bool)
      {
        f = localResources.getDimension(2131296621);
        m = (int)f;
        if (!bool) {
          break label236;
        }
        f = localResources.getDimension(2131296622);
        n = (int)f;
        i1 = (int)localResources.getDimension(2131296618);
        i2 = (int)localResources.getDimension(2131296620);
        if (!bool) {
          break label247;
        }
        f = localResources.getDimension(2131296630);
        i3 = (int)f;
        if (!bool) {
          break label258;
        }
      }
      for (f = localResources.getDimension(2131296631);; f = localResources.getDimension(2131296628))
      {
        i4 = (int)f;
        i5 = (int)localResources.getDimension(2131296627);
        i6 = (int)localResources.getDimension(2131296629);
        switch (-getcom-oneplus-base-RotationSwitchesValues()[paramRotation.ordinal()])
        {
        default: 
          return;
          f = localResources.getDimension(2131296617);
          break label89;
          f = localResources.getDimension(2131296619);
          break label106;
          f = localResources.getDimension(2131296626);
          break label143;
        }
      }
      paramRotation = new Rect(i3, i5, i4, i6);
      if (!bool) {
        break label431;
      }
      f = localResources.getDimension(2131296625);
      localPointF = new PointF(f, localResources.getDimension(2131296624));
      setLayoutParams((View)localObject, 0, 0, i, i, j, k, new int[] { 11, 12 }, true);
      localObject = new PointF();
      if (!bool) {
        break label786;
      }
    }
    label431:
    label562:
    label628:
    label689:
    label755:
    label786:
    for (float f = localResources.getDimension(2131296612);; f = localResources.getDimension(2131296610))
    {
      ((PointF)localObject).set(f, localResources.getDimension(2131296611));
      paramRotation = new SmileIndicatorDrawable(localResources.getColor(2131230823), localResources.getColor(2131230824), (PointF)localObject, localResources.getDimension(2131296609), paramRotation, localPointF, localResources.getDimension(2131296613));
      this.m_SmileCaptureView.setBackground(paramRotation);
      return;
      f = localResources.getDimension(2131296623);
      break;
      paramRotation = new Rect(i3, i5, i4, i6);
      if (bool) {}
      for (f = localResources.getDimension(2131296625);; f = localResources.getDimension(2131296623))
      {
        localPointF = new PointF(f, localResources.getDimension(2131296624));
        setLayoutParams((View)localObject, i, 0, 0, i, j, k, new int[] { 9, 12 }, true);
        break;
      }
      paramRotation = new Rect(m, i1, n, i2);
      if (bool)
      {
        f = localResources.getDimension(2131296616);
        localPointF = new PointF(f, localResources.getDimension(2131296615));
        if (!bool) {
          break label628;
        }
        setLayoutParams((View)localObject, i, i, 0, 0, j, k, new int[] { 9, 10 }, true);
      }
      for (;;)
      {
        break;
        f = localResources.getDimension(2131296614);
        break label562;
        setLayoutParams((View)localObject, 0, 0, i, i, j, k, new int[] { 11, 10 }, true);
      }
      paramRotation = new Rect(m, i1, n, i2);
      if (bool)
      {
        f = localResources.getDimension(2131296616);
        localPointF = new PointF(f, localResources.getDimension(2131296615));
        if (!bool) {
          break label755;
        }
        setLayoutParams((View)localObject, i, 0, 0, i, j, k, new int[] { 9, 12 }, true);
      }
      for (;;)
      {
        break;
        f = localResources.getDimension(2131296614);
        break label689;
        setLayoutParams((View)localObject, 0, 0, i, i, j, k, new int[] { 11, 12 }, true);
      }
    }
  }
  
  private void setLayoutParams(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt, boolean paramBoolean)
  {
    if ((paramView.getLayoutParams() instanceof RelativeLayout.LayoutParams))
    {
      if (paramBoolean) {}
      for (RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(paramView.getLayoutParams());; localLayoutParams = (RelativeLayout.LayoutParams)paramView.getLayoutParams())
      {
        localLayoutParams.setMargins(paramInt1, paramInt2, paramInt3, paramInt4);
        if ((paramInt6 != 0) || (paramInt5 != 0))
        {
          localLayoutParams.height = paramInt6;
          localLayoutParams.width = paramInt5;
        }
        if (paramArrayOfInt == null) {
          break;
        }
        paramInt1 = 0;
        while (paramInt1 < paramArrayOfInt.length)
        {
          localLayoutParams.addRule(paramArrayOfInt[paramInt1]);
          paramInt1 += 1;
        }
      }
      paramView.setLayoutParams(localLayoutParams);
      paramView.requestLayout();
    }
  }
  
  private void showSimpleModesSwitch()
  {
    if (this.m_CameraWizardContainer == null) {
      inflateCameraWizardContainer();
    }
    if (this.m_CameraWizardContainer != null)
    {
      if (this.m_SimpleModesSwitch == null) {
        this.m_SimpleModesSwitch = this.m_CameraWizardContainer.findViewById(2131361915);
      }
      if ((this.m_SimpleModesSwitch instanceof ViewStub))
      {
        this.m_SimpleModesSwitch = ((ViewStub)this.m_SimpleModesSwitch).inflate();
        this.m_SimpleModesSwitch.setBackgroundColor(this.m_CameraActivity.getResources().getColor(2131230823));
        ((TextView)this.m_SimpleModesSwitch.findViewById(2131361920)).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = (Camera)CameraWizardImpl.-get0(CameraWizardImpl.this).get(CameraActivity.PROP_CAMERA);
            CameraWizardImpl.-get3(CameraWizardImpl.this).set("Wizard.SimpleModesSwitch", Boolean.valueOf(true));
            CameraWizardImpl.-wrap3(CameraWizardImpl.this, CameraWizardImpl.-get4(CameraWizardImpl.this), false, 200L, null);
            if (Handle.isValid(CameraWizardImpl.-get2(CameraWizardImpl.this))) {
              CameraWizardImpl.-set0(CameraWizardImpl.this, Handle.close(CameraWizardImpl.-get2(CameraWizardImpl.this)));
            }
            if ((paramAnonymousView.get(Camera.PROP_LENS_FACING) != Camera.LensFacing.FRONT) || (CameraWizardImpl.-get3(CameraWizardImpl.this).getBoolean("Wizard.SmileCapture", false))) {}
            while (!((Boolean)CameraWizardImpl.-wrap0(CameraWizardImpl.this).get(Camera.PROP_IS_SMILE_CAPTURE_SUPPORTED)).booleanValue())
            {
              CameraWizardImpl.-wrap3(CameraWizardImpl.this, CameraWizardImpl.-get1(CameraWizardImpl.this), false, 200L, null);
              return;
            }
            CameraWizardImpl.-wrap4(CameraWizardImpl.this);
          }
        });
        if (!Handle.isValid(this.m_CaptureUIDisableHandle)) {
          this.m_CaptureUIDisableHandle = this.m_CameraActivity.disableCaptureUI("CameraWizardSimpleSwitch", 1);
        }
        setViewVisibility(this.m_SimpleModesSwitch, true, 200L, null);
        setViewVisibility(this.m_CameraWizardContainer, true, 200L, null);
      }
    }
  }
  
  private void showSmileCapture()
  {
    if (this.m_Settings.getBoolean("Wizard.SmileCapture", false))
    {
      Log.v(this.TAG, "showSmileCapture() - Smile capture already shown.");
      return;
    }
    if (this.m_CaptureModeSwitcher == null)
    {
      this.m_CaptureModeSwitcher = ((CaptureModeSwitcher)findComponent(CaptureModeSwitcher.class));
      if (this.m_CaptureModeSwitcher != null)
      {
        this.m_CaptureModeSwitcher.addCallback(CaptureModeSwitcher.PROP_IS_CAPTURE_MODE_PANEL_OPEN, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
          {
            if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
              CameraWizardImpl.-wrap4(CameraWizardImpl.this);
            }
          }
        });
        if (((Boolean)this.m_CaptureModeSwitcher.get(CaptureModeSwitcher.PROP_IS_CAPTURE_MODE_PANEL_OPEN)).booleanValue()) {
          Log.v(this.TAG, "showSmileCapture() - CaptureModeSwitcher is showing");
        }
      }
    }
    else if (((Boolean)this.m_CaptureModeSwitcher.get(CaptureModeSwitcher.PROP_IS_CAPTURE_MODE_PANEL_OPEN)).booleanValue())
    {
      Log.v(this.TAG, "showSmileCapture() - CaptureModeSwitcher is showing");
      return;
    }
    if ((this.m_SimpleModesSwitch != null) && (this.m_SimpleModesSwitch.getVisibility() == 0))
    {
      Log.v(this.TAG, "showSmileCapture() - switch wizard is showing");
      return;
    }
    Camera localCamera = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    if (localCamera == null)
    {
      Log.v(this.TAG, "showSmileCapture() - Camera is null");
      return;
    }
    if (localCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.BACK)
    {
      Log.v(this.TAG, "showSmileCapture() - There is no smile capture for back camera");
      return;
    }
    if (this.m_CaptureModeManager == null)
    {
      this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
      if (this.m_CaptureModeManager == null)
      {
        Log.v(this.TAG, "showSmileCapture - Capture mode manager is empty, show failed");
        return;
      }
    }
    if (!((CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE) instanceof PhotoCaptureMode))
    {
      Log.v(this.TAG, "showSmileCapture() - Not in photo mode");
      return;
    }
    if (this.m_SmileCaptureView != null)
    {
      if (this.m_SmileCaptureView.getVisibility() != 0)
      {
        setViewVisibility(this.m_SmileCaptureView, true, 0L, null);
        setViewVisibility(this.m_CameraWizardContainer, true, 0L, null);
        this.m_SmileCaptureUiDisableHandle = this.m_CameraActivity.disableCaptureUI("CameraWizardSmileCapture", 1);
        return;
      }
      Log.v(this.TAG, "showSmileCapture() - Smile capture is showing");
      return;
    }
    inflateCameraWizardContainer();
    if (this.m_CameraWizardContainer != null)
    {
      if (this.m_SmileCaptureView == null) {
        this.m_SmileCaptureView = this.m_CameraWizardContainer.findViewById(2131361916);
      }
      if ((this.m_SmileCaptureView instanceof ViewStub)) {
        this.m_SmileCaptureView = ((ViewStub)this.m_SmileCaptureView).inflate();
      }
      ((RotateRelativeLayout)this.m_SmileCaptureView).setRotation(getRotation());
      rotateSmileCaptureContent(getRotation());
      ((TextView)this.m_SmileCaptureView.findViewById(2131361922)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          CameraWizardImpl.-get3(CameraWizardImpl.this).set("Wizard.SmileCapture", Boolean.valueOf(true));
          CameraWizardImpl.-wrap3(CameraWizardImpl.this, CameraWizardImpl.-get6(CameraWizardImpl.this), false, 200L, null);
          CameraWizardImpl.-wrap3(CameraWizardImpl.this, CameraWizardImpl.-get1(CameraWizardImpl.this), false, 200L, null);
          if (Handle.isValid(CameraWizardImpl.-get5(CameraWizardImpl.this))) {
            CameraWizardImpl.-set1(CameraWizardImpl.this, Handle.close(CameraWizardImpl.-get5(CameraWizardImpl.this)));
          }
        }
      });
      if (!Handle.isValid(this.m_SmileCaptureUiDisableHandle)) {
        this.m_SmileCaptureUiDisableHandle = this.m_CameraActivity.disableCaptureUI("CameraWizardSmileCapture", 1);
      }
      setViewVisibility(this.m_SmileCaptureView, true, 200L, null);
      setViewVisibility(this.m_CameraWizardContainer, true, 200L, null);
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    int i = paramMessage.what;
    super.handleMessage(paramMessage);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_CameraActivity = getCameraActivity();
    this.m_Settings = ((Settings)this.m_CameraActivity.get(CameraActivity.PROP_SETTINGS));
    this.m_Settings.getBoolean("Wizard.SimpleModesSwitch", false);
    this.m_Settings.getBoolean("Wizard.SmileCapture", false);
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    super.onRotationChanged(paramRotation1, paramRotation2);
    if (this.m_SimpleModesSwitch != null) {
      rotateLayout((RotateRelativeLayout)this.m_SimpleModesSwitch, 600L, new UIComponent.ViewRotationCallback()
      {
        public void onRotated(View paramAnonymousView, Rotation paramAnonymousRotation)
        {
          CameraWizardImpl.-wrap1(CameraWizardImpl.this, paramAnonymousRotation);
        }
      });
    }
    if (this.m_SmileCaptureView != null) {
      rotateLayout((RotateRelativeLayout)this.m_SmileCaptureView, 600L, new UIComponent.ViewRotationCallback()
      {
        public void onRotated(View paramAnonymousView, Rotation paramAnonymousRotation)
        {
          CameraWizardImpl.-wrap2(CameraWizardImpl.this, paramAnonymousRotation);
        }
      });
    }
  }
  
  public static class SmileIndicatorDrawable
    extends Drawable
  {
    private final Paint m_BackgroundPaint;
    private final PointF m_Center;
    private Paint m_HollowPaint;
    private Paint m_IndicatorPaint;
    private final float m_Radius;
    private PointF m_StickCircleCenter;
    private float m_StickCircleRadius;
    private Rect m_StickRect;
    
    public SmileIndicatorDrawable(int paramInt1, int paramInt2, PointF paramPointF1, float paramFloat1, Rect paramRect, PointF paramPointF2, float paramFloat2)
    {
      this.m_Center = paramPointF1;
      this.m_Radius = paramFloat1;
      this.m_StickRect = paramRect;
      this.m_StickCircleCenter = paramPointF2;
      this.m_StickCircleRadius = paramFloat2;
      this.m_BackgroundPaint = new Paint();
      this.m_BackgroundPaint.setStyle(Paint.Style.FILL);
      this.m_BackgroundPaint.setColor(paramInt1);
      this.m_HollowPaint = new Paint();
      this.m_HollowPaint.setColor(paramInt2);
      this.m_HollowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
      this.m_HollowPaint.setAntiAlias(true);
      this.m_IndicatorPaint = new Paint();
      this.m_IndicatorPaint.setStyle(Paint.Style.FILL);
      this.m_IndicatorPaint.setColor(-1);
      this.m_IndicatorPaint.setAntiAlias(true);
    }
    
    public void draw(Canvas paramCanvas)
    {
      Rect localRect = getBounds();
      Bitmap localBitmap = Bitmap.createBitmap(paramCanvas.getWidth(), paramCanvas.getHeight(), Bitmap.Config.ARGB_8888);
      Canvas localCanvas = new Canvas(localBitmap);
      localCanvas.drawRect(localRect.left, localRect.top, localRect.right, localRect.bottom, this.m_BackgroundPaint);
      localCanvas.drawCircle(this.m_Center.x, this.m_Center.y, this.m_Radius, this.m_HollowPaint);
      paramCanvas.drawBitmap(localBitmap, 0.0F, 0.0F, this.m_BackgroundPaint);
      paramCanvas.drawRect(this.m_StickRect, this.m_IndicatorPaint);
      paramCanvas.drawCircle(this.m_StickCircleCenter.x, this.m_StickCircleCenter.y, this.m_StickCircleRadius, this.m_IndicatorPaint);
    }
    
    public int getOpacity()
    {
      return 0;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CameraWizardImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */