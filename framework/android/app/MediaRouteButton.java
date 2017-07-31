package android.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteGroup;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.android.internal.R.styleable;
import com.android.internal.app.MediaRouteDialogPresenter;

public class MediaRouteButton
  extends View
{
  private static final int[] ACTIVATED_STATE_SET = { 16843518 };
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  private boolean mAttachedToWindow;
  private final MediaRouterCallback mCallback;
  private boolean mCheatSheetEnabled;
  private View.OnClickListener mExtendedSettingsClickListener;
  private boolean mIsConnecting;
  private int mMinHeight;
  private int mMinWidth;
  private boolean mRemoteActive;
  private Drawable mRemoteIndicator;
  private int mRouteTypes;
  private final MediaRouter mRouter;
  
  public MediaRouteButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public MediaRouteButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843693);
  }
  
  public MediaRouteButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public MediaRouteButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mRouter = ((MediaRouter)paramContext.getSystemService("media_router"));
    this.mCallback = new MediaRouterCallback(null);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MediaRouteButton, paramInt1, paramInt2);
    setRemoteIndicatorDrawable(paramContext.getDrawable(3));
    this.mMinWidth = paramContext.getDimensionPixelSize(0, 0);
    this.mMinHeight = paramContext.getDimensionPixelSize(1, 0);
    paramInt1 = paramContext.getInteger(2, 1);
    paramContext.recycle();
    setClickable(true);
    setLongClickable(true);
    setRouteTypes(paramInt1);
  }
  
  private Activity getActivity()
  {
    for (Context localContext = getContext(); (localContext instanceof ContextWrapper); localContext = localContext.getBaseContext()) {
      if ((localContext instanceof Activity)) {
        return localContext;
      }
    }
    throw new IllegalStateException("The MediaRouteButton's Context is not an Activity.");
  }
  
  private void refreshRoute()
  {
    MediaRouter.RouteInfo localRouteInfo;
    boolean bool1;
    if (this.mAttachedToWindow)
    {
      localRouteInfo = this.mRouter.getSelectedRoute();
      if (localRouteInfo.isDefault()) {
        break label101;
      }
      bool1 = localRouteInfo.matchesTypes(this.mRouteTypes);
      if (!bool1) {
        break label106;
      }
    }
    label101:
    label106:
    for (boolean bool2 = localRouteInfo.isConnecting();; bool2 = false)
    {
      int i = 0;
      if (this.mRemoteActive != bool1)
      {
        this.mRemoteActive = bool1;
        i = 1;
      }
      if (this.mIsConnecting != bool2)
      {
        this.mIsConnecting = bool2;
        i = 1;
      }
      if (i != 0) {
        refreshDrawableState();
      }
      setEnabled(this.mRouter.isRouteAvailable(this.mRouteTypes, 1));
      return;
      bool1 = false;
      break;
    }
  }
  
  private void setRemoteIndicatorDrawable(Drawable paramDrawable)
  {
    if (this.mRemoteIndicator != null)
    {
      this.mRemoteIndicator.setCallback(null);
      unscheduleDrawable(this.mRemoteIndicator);
    }
    this.mRemoteIndicator = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      paramDrawable.setState(getDrawableState());
      if (getVisibility() != 0) {
        break label67;
      }
    }
    label67:
    for (boolean bool = true;; bool = false)
    {
      paramDrawable.setVisible(bool, false);
      refreshDrawableState();
      return;
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mRemoteIndicator;
    if ((localDrawable != null) && (localDrawable.isStateful()) && (localDrawable.setState(getDrawableState()))) {
      invalidateDrawable(localDrawable);
    }
  }
  
  public int getRouteTypes()
  {
    return this.mRouteTypes;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mRemoteIndicator != null) {
      this.mRemoteIndicator.jumpToCurrentState();
    }
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mAttachedToWindow = true;
    if (this.mRouteTypes != 0) {
      this.mRouter.addCallback(this.mRouteTypes, this.mCallback, 8);
    }
    refreshRoute();
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (this.mIsConnecting) {
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET);
    }
    while (!this.mRemoteActive) {
      return arrayOfInt;
    }
    mergeDrawableStates(arrayOfInt, ACTIVATED_STATE_SET);
    return arrayOfInt;
  }
  
  public void onDetachedFromWindow()
  {
    this.mAttachedToWindow = false;
    if (this.mRouteTypes != 0) {
      this.mRouter.removeCallback(this.mCallback);
    }
    super.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.mRemoteIndicator == null) {
      return;
    }
    int i1 = getPaddingLeft();
    int i2 = getWidth();
    int i3 = getPaddingRight();
    int k = getPaddingTop();
    int m = getHeight();
    int n = getPaddingBottom();
    int i = this.mRemoteIndicator.getIntrinsicWidth();
    int j = this.mRemoteIndicator.getIntrinsicHeight();
    i1 += (i2 - i3 - i1 - i) / 2;
    k += (m - n - k - j) / 2;
    this.mRemoteIndicator.setBounds(i1, k, i1 + i, k + j);
    this.mRemoteIndicator.draw(paramCanvas);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int k = 0;
    int j = View.MeasureSpec.getSize(paramInt1);
    int i = View.MeasureSpec.getSize(paramInt2);
    int n = View.MeasureSpec.getMode(paramInt1);
    int m = View.MeasureSpec.getMode(paramInt2);
    paramInt2 = this.mMinWidth;
    if (this.mRemoteIndicator != null)
    {
      paramInt1 = this.mRemoteIndicator.getIntrinsicWidth() + getPaddingLeft() + getPaddingRight();
      paramInt2 = Math.max(paramInt2, paramInt1);
      int i1 = this.mMinHeight;
      paramInt1 = k;
      if (this.mRemoteIndicator != null) {
        paramInt1 = this.mRemoteIndicator.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
      }
      k = Math.max(i1, paramInt1);
      switch (n)
      {
      case 0: 
      default: 
        paramInt1 = paramInt2;
        switch (m)
        {
        case 0: 
        default: 
          label142:
          paramInt2 = k;
        }
        break;
      }
    }
    for (;;)
    {
      setMeasuredDimension(paramInt1, paramInt2);
      return;
      paramInt1 = 0;
      break;
      paramInt1 = j;
      break label142;
      paramInt1 = Math.min(j, paramInt2);
      break label142;
      paramInt2 = i;
      continue;
      paramInt2 = Math.min(i, k);
    }
  }
  
  public boolean performClick()
  {
    boolean bool = super.performClick();
    if (!bool) {
      playSoundEffect(0);
    }
    if (!showDialogInternal()) {
      return bool;
    }
    return true;
  }
  
  public boolean performLongClick()
  {
    if (super.performLongClick()) {
      return true;
    }
    if (!this.mCheatSheetEnabled) {
      return false;
    }
    Object localObject = getContentDescription();
    if (TextUtils.isEmpty((CharSequence)localObject)) {
      return false;
    }
    int[] arrayOfInt = new int[2];
    Rect localRect = new Rect();
    getLocationOnScreen(arrayOfInt);
    getWindowVisibleDisplayFrame(localRect);
    Context localContext = getContext();
    int i = getWidth();
    int j = getHeight();
    int k = arrayOfInt[1];
    int m = j / 2;
    int n = localContext.getResources().getDisplayMetrics().widthPixels;
    localObject = Toast.makeText(localContext, (CharSequence)localObject, 0);
    if (k + m < localRect.height()) {
      ((Toast)localObject).setGravity(8388661, n - arrayOfInt[0] - i / 2, j);
    }
    for (;;)
    {
      ((Toast)localObject).show();
      performHapticFeedback(0);
      return true;
      ((Toast)localObject).setGravity(81, 0, j);
    }
  }
  
  void setCheatSheetEnabled(boolean paramBoolean)
  {
    this.mCheatSheetEnabled = paramBoolean;
  }
  
  public void setExtendedSettingsClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mExtendedSettingsClickListener = paramOnClickListener;
  }
  
  public void setRouteTypes(int paramInt)
  {
    if (this.mRouteTypes != paramInt)
    {
      if ((this.mAttachedToWindow) && (this.mRouteTypes != 0)) {
        this.mRouter.removeCallback(this.mCallback);
      }
      this.mRouteTypes = paramInt;
      if ((this.mAttachedToWindow) && (paramInt != 0)) {
        this.mRouter.addCallback(paramInt, this.mCallback, 8);
      }
      refreshRoute();
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    Drawable localDrawable;
    if (this.mRemoteIndicator != null)
    {
      localDrawable = this.mRemoteIndicator;
      if (getVisibility() != 0) {
        break label34;
      }
    }
    label34:
    for (boolean bool = true;; bool = false)
    {
      localDrawable.setVisible(bool, false);
      return;
    }
  }
  
  public void showDialog()
  {
    showDialogInternal();
  }
  
  boolean showDialogInternal()
  {
    boolean bool = false;
    if (!this.mAttachedToWindow) {
      return false;
    }
    if (MediaRouteDialogPresenter.showDialogFragment(getActivity(), this.mRouteTypes, this.mExtendedSettingsClickListener) != null) {
      bool = true;
    }
    return bool;
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mRemoteIndicator);
  }
  
  private final class MediaRouterCallback
    extends MediaRouter.SimpleCallback
  {
    private MediaRouterCallback() {}
    
    public void onRouteAdded(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo)
    {
      MediaRouteButton.-wrap0(MediaRouteButton.this);
    }
    
    public void onRouteChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo)
    {
      MediaRouteButton.-wrap0(MediaRouteButton.this);
    }
    
    public void onRouteGrouped(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo, MediaRouter.RouteGroup paramRouteGroup, int paramInt)
    {
      MediaRouteButton.-wrap0(MediaRouteButton.this);
    }
    
    public void onRouteRemoved(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo)
    {
      MediaRouteButton.-wrap0(MediaRouteButton.this);
    }
    
    public void onRouteSelected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo)
    {
      MediaRouteButton.-wrap0(MediaRouteButton.this);
    }
    
    public void onRouteUngrouped(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo, MediaRouter.RouteGroup paramRouteGroup)
    {
      MediaRouteButton.-wrap0(MediaRouteButton.this);
    }
    
    public void onRouteUnselected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo)
    {
      MediaRouteButton.-wrap0(MediaRouteButton.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/MediaRouteButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */