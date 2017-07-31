package android.app;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.Transition.TransitionListenerAdapter;
import android.transition.TransitionSet;
import android.transition.Visibility;
import android.util.ArrayMap;
import android.view.GhostView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

abstract class ActivityTransitionCoordinator
  extends ResultReceiver
{
  protected static final String KEY_ELEVATION = "shared_element:elevation";
  protected static final String KEY_IMAGE_MATRIX = "shared_element:imageMatrix";
  static final String KEY_REMOTE_RECEIVER = "android:remoteReceiver";
  protected static final String KEY_SCALE_TYPE = "shared_element:scaleType";
  protected static final String KEY_SCREEN_BOTTOM = "shared_element:screenBottom";
  protected static final String KEY_SCREEN_LEFT = "shared_element:screenLeft";
  protected static final String KEY_SCREEN_RIGHT = "shared_element:screenRight";
  protected static final String KEY_SCREEN_TOP = "shared_element:screenTop";
  protected static final String KEY_SNAPSHOT = "shared_element:bitmap";
  protected static final String KEY_TRANSLATION_Z = "shared_element:translationZ";
  public static final int MSG_CANCEL = 106;
  public static final int MSG_EXIT_TRANSITION_COMPLETE = 104;
  public static final int MSG_HIDE_SHARED_ELEMENTS = 101;
  public static final int MSG_SET_REMOTE_RECEIVER = 100;
  public static final int MSG_SHARED_ELEMENT_DESTINATION = 107;
  public static final int MSG_START_EXIT_TRANSITION = 105;
  public static final int MSG_TAKE_SHARED_ELEMENTS = 103;
  protected static final ImageView.ScaleType[] SCALE_TYPE_VALUES = ;
  private static final String TAG = "ActivityTransitionCoordinator";
  protected final ArrayList<String> mAllSharedElementNames;
  private final FixedEpicenterCallback mEpicenterCallback = new FixedEpicenterCallback(null);
  private ArrayList<GhostViewListeners> mGhostViewListeners = new ArrayList();
  protected final boolean mIsReturning;
  private boolean mIsStartingTransition;
  protected SharedElementCallback mListener;
  private ArrayMap<View, Float> mOriginalAlphas = new ArrayMap();
  private Runnable mPendingTransition;
  protected ResultReceiver mResultReceiver;
  protected final ArrayList<String> mSharedElementNames = new ArrayList();
  private ArrayList<Matrix> mSharedElementParentMatrices;
  private boolean mSharedElementTransitionComplete;
  protected final ArrayList<View> mSharedElements = new ArrayList();
  protected ArrayList<View> mTransitioningViews = new ArrayList();
  private boolean mViewsTransitionComplete;
  private Window mWindow;
  
  public ActivityTransitionCoordinator(Window paramWindow, ArrayList<String> paramArrayList, SharedElementCallback paramSharedElementCallback, boolean paramBoolean)
  {
    super(new Handler());
    this.mWindow = paramWindow;
    this.mListener = paramSharedElementCallback;
    this.mAllSharedElementNames = paramArrayList;
    this.mIsReturning = paramBoolean;
  }
  
  private static SharedElementOriginalState getOldSharedElementState(View paramView, String paramString, Bundle paramBundle)
  {
    SharedElementOriginalState localSharedElementOriginalState = new SharedElementOriginalState();
    localSharedElementOriginalState.mLeft = paramView.getLeft();
    localSharedElementOriginalState.mTop = paramView.getTop();
    localSharedElementOriginalState.mRight = paramView.getRight();
    localSharedElementOriginalState.mBottom = paramView.getBottom();
    localSharedElementOriginalState.mMeasuredWidth = paramView.getMeasuredWidth();
    localSharedElementOriginalState.mMeasuredHeight = paramView.getMeasuredHeight();
    localSharedElementOriginalState.mTranslationZ = paramView.getTranslationZ();
    localSharedElementOriginalState.mElevation = paramView.getElevation();
    if (!(paramView instanceof ImageView)) {
      return localSharedElementOriginalState;
    }
    paramString = paramBundle.getBundle(paramString);
    if (paramString == null) {
      return localSharedElementOriginalState;
    }
    if (paramString.getInt("shared_element:scaleType", -1) < 0) {
      return localSharedElementOriginalState;
    }
    paramView = (ImageView)paramView;
    localSharedElementOriginalState.mScaleType = paramView.getScaleType();
    if (localSharedElementOriginalState.mScaleType == ImageView.ScaleType.MATRIX) {
      localSharedElementOriginalState.mMatrix = new Matrix(paramView.getImageMatrix());
    }
    return localSharedElementOriginalState;
  }
  
  private void getSharedElementParentMatrix(View paramView, Matrix paramMatrix)
  {
    if (this.mSharedElementParentMatrices == null) {}
    for (int i = -1; i < 0; i = this.mSharedElements.indexOf(paramView))
    {
      paramMatrix.reset();
      paramView = paramView.getParent();
      if ((paramView instanceof ViewGroup))
      {
        paramView = (ViewGroup)paramView;
        paramView.transformMatrixToLocal(paramMatrix);
        paramMatrix.postTranslate(paramView.getScrollX(), paramView.getScrollY());
      }
      return;
    }
    paramMatrix.set((Matrix)this.mSharedElementParentMatrices.get(i));
  }
  
  public static boolean isInTransitionGroup(ViewParent paramViewParent, ViewGroup paramViewGroup)
  {
    if ((paramViewParent != paramViewGroup) && ((paramViewParent instanceof ViewGroup)))
    {
      paramViewParent = (ViewGroup)paramViewParent;
      if (paramViewParent.isTransitionGroup()) {
        return true;
      }
    }
    else
    {
      return false;
    }
    return isInTransitionGroup(paramViewParent.getParent(), paramViewGroup);
  }
  
  private static boolean isNested(View paramView, ArrayMap<String, View> paramArrayMap)
  {
    paramView = paramView.getParent();
    boolean bool2 = false;
    for (;;)
    {
      boolean bool1 = bool2;
      if ((paramView instanceof View))
      {
        paramView = (View)paramView;
        if (paramArrayMap.containsValue(paramView)) {
          bool1 = true;
        }
      }
      else
      {
        return bool1;
      }
      paramView = paramView.getParent();
    }
  }
  
  protected static Transition mergeTransitions(Transition paramTransition1, Transition paramTransition2)
  {
    if (paramTransition1 == null) {
      return paramTransition2;
    }
    if (paramTransition2 == null) {
      return paramTransition1;
    }
    TransitionSet localTransitionSet = new TransitionSet();
    localTransitionSet.addTransition(paramTransition1);
    localTransitionSet.addTransition(paramTransition2);
    return localTransitionSet;
  }
  
  private static void noLayoutSuppressionForVisibilityTransitions(Transition paramTransition)
  {
    if ((paramTransition instanceof Visibility)) {
      ((Visibility)paramTransition).setSuppressLayout(false);
    }
    for (;;)
    {
      return;
      if ((paramTransition instanceof TransitionSet))
      {
        paramTransition = (TransitionSet)paramTransition;
        int j = paramTransition.getTransitionCount();
        int i = 0;
        while (i < j)
        {
          noLayoutSuppressionForVisibilityTransitions(paramTransition.getTransitionAt(i));
          i += 1;
        }
      }
    }
  }
  
  private static int scaleTypeToInt(ImageView.ScaleType paramScaleType)
  {
    int i = 0;
    while (i < SCALE_TYPE_VALUES.length)
    {
      if (paramScaleType == SCALE_TYPE_VALUES[i]) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private void setEpicenter(View paramView)
  {
    if (paramView == null)
    {
      this.mEpicenterCallback.setEpicenter(null);
      return;
    }
    Rect localRect = new Rect();
    paramView.getBoundsOnScreen(localRect);
    this.mEpicenterCallback.setEpicenter(localRect);
  }
  
  protected static void setOriginalSharedElementState(ArrayList<View> paramArrayList, ArrayList<SharedElementOriginalState> paramArrayList1)
  {
    int i = 0;
    while (i < paramArrayList1.size())
    {
      View localView = (View)paramArrayList.get(i);
      SharedElementOriginalState localSharedElementOriginalState = (SharedElementOriginalState)paramArrayList1.get(i);
      if (((localView instanceof ImageView)) && (localSharedElementOriginalState.mScaleType != null))
      {
        ImageView localImageView = (ImageView)localView;
        localImageView.setScaleType(localSharedElementOriginalState.mScaleType);
        if (localSharedElementOriginalState.mScaleType == ImageView.ScaleType.MATRIX) {
          localImageView.setImageMatrix(localSharedElementOriginalState.mMatrix);
        }
      }
      localView.setElevation(localSharedElementOriginalState.mElevation);
      localView.setTranslationZ(localSharedElementOriginalState.mTranslationZ);
      localView.measure(View.MeasureSpec.makeMeasureSpec(localSharedElementOriginalState.mMeasuredWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(localSharedElementOriginalState.mMeasuredHeight, 1073741824));
      localView.layout(localSharedElementOriginalState.mLeft, localSharedElementOriginalState.mTop, localSharedElementOriginalState.mRight, localSharedElementOriginalState.mBottom);
      i += 1;
    }
  }
  
  private void setSharedElementMatrices()
  {
    int j = this.mSharedElements.size();
    if (j > 0) {
      this.mSharedElementParentMatrices = new ArrayList(j);
    }
    int i = 0;
    while (i < j)
    {
      ViewGroup localViewGroup = (ViewGroup)((View)this.mSharedElements.get(i)).getParent();
      Matrix localMatrix = new Matrix();
      localViewGroup.transformMatrixToLocal(localMatrix);
      localMatrix.postTranslate(localViewGroup.getScrollX(), localViewGroup.getScrollY());
      this.mSharedElementParentMatrices.add(localMatrix);
      i += 1;
    }
  }
  
  private void setSharedElementState(View paramView, String paramString, Bundle paramBundle, Matrix paramMatrix, RectF paramRectF, int[] paramArrayOfInt)
  {
    paramString = paramBundle.getBundle(paramString);
    if (paramString == null) {
      return;
    }
    int i;
    if ((paramView instanceof ImageView))
    {
      i = paramString.getInt("shared_element:scaleType", -1);
      if (i >= 0)
      {
        paramBundle = (ImageView)paramView;
        ImageView.ScaleType localScaleType = SCALE_TYPE_VALUES[i];
        paramBundle.setScaleType(localScaleType);
        if (localScaleType == ImageView.ScaleType.MATRIX)
        {
          paramMatrix.setValues(paramString.getFloatArray("shared_element:imageMatrix"));
          paramBundle.setImageMatrix(paramMatrix);
        }
      }
    }
    paramView.setTranslationZ(paramString.getFloat("shared_element:translationZ"));
    paramView.setElevation(paramString.getFloat("shared_element:elevation"));
    float f2 = paramString.getFloat("shared_element:screenLeft");
    float f4 = paramString.getFloat("shared_element:screenTop");
    float f3 = paramString.getFloat("shared_element:screenRight");
    float f1 = paramString.getFloat("shared_element:screenBottom");
    if (paramArrayOfInt != null)
    {
      f2 -= paramArrayOfInt[0];
      f4 -= paramArrayOfInt[1];
      f3 -= paramArrayOfInt[0];
    }
    for (f1 -= paramArrayOfInt[1];; f1 = f4 + f1)
    {
      i = Math.round(f2);
      int j = Math.round(f4);
      int k = Math.round(f3) - i;
      int m = Math.round(f1) - j;
      paramView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), View.MeasureSpec.makeMeasureSpec(m, 1073741824));
      paramView.layout(i, j, i + k, j + m);
      return;
      getSharedElementParentMatrix(paramView, paramMatrix);
      paramRectF.set(f2, f4, f3, f1);
      paramMatrix.mapRect(paramRectF);
      f2 = paramRectF.left;
      f4 = paramRectF.top;
      paramView.getInverseMatrix().mapRect(paramRectF);
      f3 = paramRectF.width();
      f1 = paramRectF.height();
      paramView.setLeft(0);
      paramView.setTop(0);
      paramView.setRight(Math.round(f3));
      paramView.setBottom(Math.round(f1));
      paramRectF.set(0.0F, 0.0F, f3, f1);
      paramView.getMatrix().mapRect(paramRectF);
      f2 -= paramRectF.left;
      f4 -= paramRectF.top;
      f3 = f2 + f3;
    }
  }
  
  private void setSharedElements(ArrayMap<String, View> paramArrayMap)
  {
    for (int i = 1; !paramArrayMap.isEmpty(); i = 0)
    {
      int j = paramArrayMap.size() - 1;
      if (j >= 0)
      {
        View localView = (View)paramArrayMap.valueAt(j);
        String str = (String)paramArrayMap.keyAt(j);
        if ((i != 0) && ((localView == null) || (!localView.isAttachedToWindow()) || (str == null))) {
          paramArrayMap.removeAt(j);
        }
        for (;;)
        {
          j -= 1;
          break;
          if (!isNested(localView, paramArrayMap))
          {
            this.mSharedElementNames.add(str);
            this.mSharedElements.add(localView);
            paramArrayMap.removeAt(j);
          }
        }
      }
    }
  }
  
  private void showView(View paramView, boolean paramBoolean)
  {
    Float localFloat = (Float)this.mOriginalAlphas.remove(paramView);
    if (localFloat != null) {
      paramView.setAlpha(localFloat.floatValue());
    }
    if (paramBoolean) {
      paramView.setTransitionAlpha(1.0F);
    }
  }
  
  private void startInputWhenTransitionsComplete()
  {
    if ((this.mViewsTransitionComplete) && (this.mSharedElementTransitionComplete))
    {
      Object localObject = getDecor();
      if (localObject != null)
      {
        localObject = ((View)localObject).getViewRootImpl();
        if (localObject != null) {
          ((ViewRootImpl)localObject).setPausedForTransition(false);
        }
      }
      onTransitionsComplete();
    }
  }
  
  protected boolean cancelPendingTransitions()
  {
    this.mPendingTransition = null;
    return this.mIsStartingTransition;
  }
  
  protected Bundle captureSharedElementState()
  {
    Bundle localBundle = new Bundle();
    RectF localRectF = new RectF();
    Matrix localMatrix = new Matrix();
    int i = 0;
    while (i < this.mSharedElements.size())
    {
      captureSharedElementState((View)this.mSharedElements.get(i), (String)this.mSharedElementNames.get(i), localBundle, localMatrix, localRectF);
      i += 1;
    }
    return localBundle;
  }
  
  protected void captureSharedElementState(View paramView, String paramString, Bundle paramBundle, Matrix paramMatrix, RectF paramRectF)
  {
    Bundle localBundle = new Bundle();
    paramMatrix.reset();
    paramView.transformMatrixToGlobal(paramMatrix);
    paramRectF.set(0.0F, 0.0F, paramView.getWidth(), paramView.getHeight());
    paramMatrix.mapRect(paramRectF);
    localBundle.putFloat("shared_element:screenLeft", paramRectF.left);
    localBundle.putFloat("shared_element:screenRight", paramRectF.right);
    localBundle.putFloat("shared_element:screenTop", paramRectF.top);
    localBundle.putFloat("shared_element:screenBottom", paramRectF.bottom);
    localBundle.putFloat("shared_element:translationZ", paramView.getTranslationZ());
    localBundle.putFloat("shared_element:elevation", paramView.getElevation());
    Parcelable localParcelable = null;
    if (this.mListener != null) {
      localParcelable = this.mListener.onCaptureSharedElementSnapshot(paramView, paramMatrix, paramRectF);
    }
    if (localParcelable != null) {
      localBundle.putParcelable("shared_element:bitmap", localParcelable);
    }
    if ((paramView instanceof ImageView))
    {
      paramView = (ImageView)paramView;
      localBundle.putInt("shared_element:scaleType", scaleTypeToInt(paramView.getScaleType()));
      if (paramView.getScaleType() == ImageView.ScaleType.MATRIX)
      {
        paramMatrix = new float[9];
        paramView.getImageMatrix().getValues(paramMatrix);
        localBundle.putFloatArray("shared_element:imageMatrix", paramMatrix);
      }
    }
    paramBundle.putBundle(paramString, localBundle);
  }
  
  protected void clearState()
  {
    this.mWindow = null;
    this.mSharedElements.clear();
    this.mTransitioningViews = null;
    this.mOriginalAlphas.clear();
    this.mResultReceiver = null;
    this.mPendingTransition = null;
    this.mListener = null;
    this.mSharedElementParentMatrices = null;
  }
  
  protected Transition configureTransition(Transition paramTransition, boolean paramBoolean)
  {
    Transition localTransition = paramTransition;
    if (paramTransition != null)
    {
      paramTransition = paramTransition.clone();
      paramTransition.setEpicenterCallback(this.mEpicenterCallback);
      localTransition = setTargets(paramTransition, paramBoolean);
    }
    noLayoutSuppressionForVisibilityTransitions(localTransition);
    return localTransition;
  }
  
  public ArrayList<View> copyMappedViews()
  {
    return new ArrayList(this.mSharedElements);
  }
  
  protected ArrayList<View> createSnapshots(Bundle paramBundle, Collection<String> paramCollection)
  {
    int i = paramCollection.size();
    ArrayList localArrayList = new ArrayList(i);
    if (i == 0) {
      return localArrayList;
    }
    Context localContext = getWindow().getContext();
    int[] arrayOfInt = new int[2];
    Object localObject1 = getDecor();
    if (localObject1 != null) {
      ((ViewGroup)localObject1).getLocationOnScreen(arrayOfInt);
    }
    Matrix localMatrix = new Matrix();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramCollection = paramBundle.getBundle(str);
      localObject1 = null;
      Object localObject2 = null;
      if (paramCollection != null)
      {
        localObject1 = paramCollection.getParcelable("shared_element:bitmap");
        paramCollection = (Collection<String>)localObject2;
        if (localObject1 != null)
        {
          paramCollection = (Collection<String>)localObject2;
          if (this.mListener != null) {
            paramCollection = this.mListener.onCreateSnapshotView(localContext, (Parcelable)localObject1);
          }
        }
        localObject1 = paramCollection;
        if (paramCollection != null)
        {
          setSharedElementState(paramCollection, str, paramBundle, localMatrix, null, arrayOfInt);
          localObject1 = paramCollection;
        }
      }
      localArrayList.add(localObject1);
    }
    return localArrayList;
  }
  
  public ArrayList<String> getAcceptedNames()
  {
    return this.mSharedElementNames;
  }
  
  public ArrayList<String> getAllSharedElementNames()
  {
    return this.mAllSharedElementNames;
  }
  
  public ViewGroup getDecor()
  {
    if (this.mWindow == null) {
      return null;
    }
    return (ViewGroup)this.mWindow.getDecorView();
  }
  
  protected long getFadeDuration()
  {
    return getWindow().getTransitionBackgroundFadeDuration();
  }
  
  public ArrayList<String> getMappedNames()
  {
    ArrayList localArrayList = new ArrayList(this.mSharedElements.size());
    int i = 0;
    while (i < this.mSharedElements.size())
    {
      localArrayList.add(((View)this.mSharedElements.get(i)).getTransitionName());
      i += 1;
    }
    return localArrayList;
  }
  
  protected abstract Transition getViewsTransition();
  
  protected Window getWindow()
  {
    return this.mWindow;
  }
  
  protected void hideViews(ArrayList<View> paramArrayList)
  {
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      View localView = (View)paramArrayList.get(i);
      if (!this.mOriginalAlphas.containsKey(localView)) {
        this.mOriginalAlphas.put(localView, Float.valueOf(localView.getAlpha()));
      }
      localView.setAlpha(0.0F);
      i += 1;
    }
  }
  
  protected boolean isViewsTransitionComplete()
  {
    return this.mViewsTransitionComplete;
  }
  
  protected ArrayMap<String, View> mapSharedElements(ArrayList<String> paramArrayList, ArrayList<View> paramArrayList1)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if (paramArrayList != null)
    {
      int i = 0;
      while (i < paramArrayList.size())
      {
        localArrayMap.put((String)paramArrayList.get(i), (View)paramArrayList1.get(i));
        i += 1;
      }
    }
    paramArrayList = getDecor();
    if (paramArrayList != null) {
      paramArrayList.findNamedViews(localArrayMap);
    }
    return localArrayMap;
  }
  
  protected boolean moveSharedElementWithParent()
  {
    return true;
  }
  
  protected void moveSharedElementsFromOverlay()
  {
    int j = this.mGhostViewListeners.size();
    int i = 0;
    Object localObject;
    while (i < j)
    {
      localObject = (GhostViewListeners)this.mGhostViewListeners.get(i);
      ((ViewGroup)((GhostViewListeners)localObject).getView().getParent()).getViewTreeObserver().removeOnPreDrawListener((ViewTreeObserver.OnPreDrawListener)localObject);
      i += 1;
    }
    this.mGhostViewListeners.clear();
    if ((this.mWindow != null) && (this.mWindow.getSharedElementsUseOverlay()))
    {
      localObject = getDecor();
      if (localObject != null)
      {
        ((ViewGroup)localObject).getOverlay();
        j = this.mSharedElements.size();
        i = 0;
      }
    }
    else
    {
      while (i < j)
      {
        GhostView.removeGhost((View)this.mSharedElements.get(i));
        i += 1;
        continue;
        return;
      }
    }
  }
  
  protected void moveSharedElementsToOverlay()
  {
    ViewGroup localViewGroup1;
    int i;
    Object localObject;
    ViewGroup localViewGroup2;
    if ((this.mWindow != null) && (this.mWindow.getSharedElementsUseOverlay()))
    {
      setSharedElementMatrices();
      int j = this.mSharedElements.size();
      localViewGroup1 = getDecor();
      if (localViewGroup1 == null) {
        return;
      }
      boolean bool = moveSharedElementWithParent();
      Matrix localMatrix = new Matrix();
      i = 0;
      if (i >= j) {
        return;
      }
      localObject = (View)this.mSharedElements.get(i);
      localMatrix.reset();
      ((Matrix)this.mSharedElementParentMatrices.get(i)).invert(localMatrix);
      GhostView.addGhost((View)localObject, localViewGroup1, localMatrix);
      localViewGroup2 = (ViewGroup)((View)localObject).getParent();
      if ((bool) && (!isInTransitionGroup(localViewGroup2, localViewGroup1))) {
        break label138;
      }
    }
    for (;;)
    {
      i += 1;
      break;
      return;
      label138:
      localObject = new GhostViewListeners((View)localObject, localViewGroup2, localViewGroup1);
      localViewGroup2.getViewTreeObserver().addOnPreDrawListener((ViewTreeObserver.OnPreDrawListener)localObject);
      this.mGhostViewListeners.add(localObject);
    }
  }
  
  protected void notifySharedElementEnd(ArrayList<View> paramArrayList)
  {
    if (this.mListener != null) {
      this.mListener.onSharedElementEnd(this.mSharedElementNames, this.mSharedElements, paramArrayList);
    }
  }
  
  protected void onTransitionsComplete() {}
  
  protected void pauseInput()
  {
    ViewRootImpl localViewRootImpl = null;
    ViewGroup localViewGroup = getDecor();
    if (localViewGroup == null) {}
    for (;;)
    {
      if (localViewRootImpl != null) {
        localViewRootImpl.setPausedForTransition(true);
      }
      return;
      localViewRootImpl = localViewGroup.getViewRootImpl();
    }
  }
  
  protected void scheduleGhostVisibilityChange(final int paramInt)
  {
    final ViewGroup localViewGroup = getDecor();
    if (localViewGroup != null) {
      localViewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          localViewGroup.getViewTreeObserver().removeOnPreDrawListener(this);
          ActivityTransitionCoordinator.this.setGhostVisibility(paramInt);
          return true;
        }
      });
    }
  }
  
  protected void scheduleSetSharedElementEnd(final ArrayList<View> paramArrayList)
  {
    final ViewGroup localViewGroup = getDecor();
    if (localViewGroup != null) {
      localViewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          localViewGroup.getViewTreeObserver().removeOnPreDrawListener(this);
          ActivityTransitionCoordinator.this.notifySharedElementEnd(paramArrayList);
          return true;
        }
      });
    }
  }
  
  protected void setEpicenter()
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (!this.mAllSharedElementNames.isEmpty())
    {
      if (!this.mSharedElementNames.isEmpty()) {
        break label32;
      }
      localObject1 = localObject2;
    }
    for (;;)
    {
      setEpicenter((View)localObject1);
      return;
      label32:
      int i = this.mSharedElementNames.indexOf(this.mAllSharedElementNames.get(0));
      localObject1 = localObject2;
      if (i >= 0) {
        localObject1 = (View)this.mSharedElements.get(i);
      }
    }
  }
  
  protected void setGhostVisibility(int paramInt)
  {
    int j = this.mSharedElements.size();
    int i = 0;
    while (i < j)
    {
      GhostView localGhostView = GhostView.getGhost((View)this.mSharedElements.get(i));
      if (localGhostView != null) {
        localGhostView.setVisibility(paramInt);
      }
      i += 1;
    }
  }
  
  protected void setResultReceiver(ResultReceiver paramResultReceiver)
  {
    this.mResultReceiver = paramResultReceiver;
  }
  
  protected ArrayList<SharedElementOriginalState> setSharedElementState(Bundle paramBundle, ArrayList<View> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramBundle != null)
    {
      Matrix localMatrix = new Matrix();
      RectF localRectF = new RectF();
      int j = this.mSharedElements.size();
      int i = 0;
      while (i < j)
      {
        View localView = (View)this.mSharedElements.get(i);
        String str = (String)this.mSharedElementNames.get(i);
        localArrayList.add(getOldSharedElementState(localView, str, paramBundle));
        setSharedElementState(localView, str, paramBundle, localMatrix, localRectF, null);
        i += 1;
      }
    }
    if (this.mListener != null) {
      this.mListener.onSharedElementStart(this.mSharedElementNames, this.mSharedElements, paramArrayList);
    }
    return localArrayList;
  }
  
  protected Transition setTargets(Transition paramTransition, boolean paramBoolean)
  {
    if ((paramTransition == null) || ((paramBoolean) && ((this.mTransitioningViews == null) || (this.mTransitioningViews.isEmpty())))) {
      return null;
    }
    TransitionSet localTransitionSet = new TransitionSet();
    if (this.mTransitioningViews != null)
    {
      int i = this.mTransitioningViews.size() - 1;
      if (i >= 0)
      {
        View localView = (View)this.mTransitioningViews.get(i);
        if (paramBoolean) {
          localTransitionSet.addTarget(localView);
        }
        for (;;)
        {
          i -= 1;
          break;
          localTransitionSet.excludeTarget(localView, true);
        }
      }
    }
    localTransitionSet.addTransition(paramTransition);
    if ((paramBoolean) || (this.mTransitioningViews == null) || (this.mTransitioningViews.isEmpty())) {
      return localTransitionSet;
    }
    return new TransitionSet().addTransition(localTransitionSet);
  }
  
  protected void setTransitioningViewsVisiblity(int paramInt, boolean paramBoolean)
  {
    if (this.mTransitioningViews == null) {}
    for (int i = 0;; i = this.mTransitioningViews.size())
    {
      int j = 0;
      while (j < i)
      {
        View localView = (View)this.mTransitioningViews.get(j);
        localView.setTransitionVisibility(paramInt);
        if (paramBoolean) {
          localView.invalidate();
        }
        j += 1;
      }
    }
  }
  
  protected void sharedElementTransitionComplete()
  {
    this.mSharedElementTransitionComplete = true;
    startInputWhenTransitionsComplete();
  }
  
  protected void showViews(ArrayList<View> paramArrayList, boolean paramBoolean)
  {
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      showView((View)paramArrayList.get(i), paramBoolean);
      i += 1;
    }
  }
  
  protected void startTransition(Runnable paramRunnable)
  {
    if (this.mIsStartingTransition)
    {
      this.mPendingTransition = paramRunnable;
      return;
    }
    this.mIsStartingTransition = true;
    paramRunnable.run();
  }
  
  protected void stripOffscreenViews()
  {
    if (this.mTransitioningViews == null) {
      return;
    }
    Rect localRect = new Rect();
    int i = this.mTransitioningViews.size() - 1;
    while (i >= 0)
    {
      View localView = (View)this.mTransitioningViews.get(i);
      if (!localView.getGlobalVisibleRect(localRect))
      {
        this.mTransitioningViews.remove(i);
        showView(localView, true);
      }
      i -= 1;
    }
  }
  
  protected void transitionStarted()
  {
    this.mIsStartingTransition = false;
  }
  
  protected void viewsReady(ArrayMap<String, View> paramArrayMap)
  {
    paramArrayMap.retainAll(this.mAllSharedElementNames);
    if (this.mListener != null) {
      this.mListener.onMapSharedElements(this.mAllSharedElementNames, paramArrayMap);
    }
    setSharedElements(paramArrayMap);
    if ((getViewsTransition() != null) && (this.mTransitioningViews != null))
    {
      paramArrayMap = getDecor();
      if (paramArrayMap != null) {
        paramArrayMap.captureTransitioningViews(this.mTransitioningViews);
      }
      this.mTransitioningViews.removeAll(this.mSharedElements);
    }
    setEpicenter();
  }
  
  protected void viewsTransitionComplete()
  {
    this.mViewsTransitionComplete = true;
    startInputWhenTransitionsComplete();
  }
  
  protected class ContinueTransitionListener
    extends Transition.TransitionListenerAdapter
  {
    protected ContinueTransitionListener() {}
    
    public void onTransitionStart(Transition paramTransition)
    {
      ActivityTransitionCoordinator.-set0(ActivityTransitionCoordinator.this, false);
      paramTransition = ActivityTransitionCoordinator.-get0(ActivityTransitionCoordinator.this);
      ActivityTransitionCoordinator.-set1(ActivityTransitionCoordinator.this, null);
      if (paramTransition != null) {
        ActivityTransitionCoordinator.this.startTransition(paramTransition);
      }
    }
  }
  
  private static class FixedEpicenterCallback
    extends Transition.EpicenterCallback
  {
    private Rect mEpicenter;
    
    public Rect onGetEpicenter(Transition paramTransition)
    {
      return this.mEpicenter;
    }
    
    public void setEpicenter(Rect paramRect)
    {
      this.mEpicenter = paramRect;
    }
  }
  
  private static class GhostViewListeners
    implements ViewTreeObserver.OnPreDrawListener
  {
    private ViewGroup mDecor;
    private Matrix mMatrix = new Matrix();
    private View mParent;
    private View mView;
    
    public GhostViewListeners(View paramView1, View paramView2, ViewGroup paramViewGroup)
    {
      this.mView = paramView1;
      this.mParent = paramView2;
      this.mDecor = paramViewGroup;
    }
    
    public View getView()
    {
      return this.mView;
    }
    
    public boolean onPreDraw()
    {
      GhostView localGhostView = GhostView.getGhost(this.mView);
      if (localGhostView == null) {
        this.mParent.getViewTreeObserver().removeOnPreDrawListener(this);
      }
      for (;;)
      {
        return true;
        GhostView.calculateMatrix(this.mView, this.mDecor, this.mMatrix);
        localGhostView.setMatrix(this.mMatrix);
      }
    }
  }
  
  static class SharedElementOriginalState
  {
    int mBottom;
    float mElevation;
    int mLeft;
    Matrix mMatrix;
    int mMeasuredHeight;
    int mMeasuredWidth;
    int mRight;
    ImageView.ScaleType mScaleType;
    int mTop;
    float mTranslationZ;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityTransitionCoordinator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */