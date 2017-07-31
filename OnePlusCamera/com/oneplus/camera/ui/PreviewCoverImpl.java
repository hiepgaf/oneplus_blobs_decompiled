package com.oneplus.camera.ui;

import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.UIComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PreviewCoverImpl
  extends UIComponent
  implements PreviewCover
{
  private List<PreviewCoverHandle> m_PreviewCoverHandles = new ArrayList();
  private Map<PreviewCover.Style, Set<PreviewCover.OnStateChangedListener>> m_PreviewCoverListeners = new HashMap();
  private Map<PreviewCover.Style, PreviewCoverProducer> m_PreviewCoverProducers = new HashMap();
  
  protected PreviewCoverImpl(CameraActivity paramCameraActivity)
  {
    this("Base Preview Cover", paramCameraActivity);
  }
  
  protected PreviewCoverImpl(String paramString, CameraActivity paramCameraActivity)
  {
    super(paramString, paramCameraActivity, false);
  }
  
  private PreviewCoverProducer getPreviewCoverProducer(final PreviewCover.Style paramStyle)
  {
    if (paramStyle == null) {
      return null;
    }
    Object localObject1 = (PreviewCoverProducer)this.m_PreviewCoverProducers.get(paramStyle);
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      localObject2 = getCameraActivity();
      switch (-getcom-oneplus-camera-ui-PreviewCover$StyleSwitchesValues()[paramStyle.ordinal()])
      {
      }
    }
    for (;;)
    {
      ((PreviewCoverProducer)localObject1).addCallback(PreviewCoverProducer.PROP_STATE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PreviewCoverProducer.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<PreviewCoverProducer.State> paramAnonymousPropertyChangeEventArgs)
        {
          PreviewCoverImpl.-wrap1(PreviewCoverImpl.this, paramStyle, (PreviewCoverProducer.State)paramAnonymousPropertyChangeEventArgs.getOldValue(), (PreviewCoverProducer.State)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
      this.m_PreviewCoverProducers.put(paramStyle, localObject1);
      localObject2 = localObject1;
      return (PreviewCoverProducer)localObject2;
      localObject1 = new ColorPreviewCoverProducer((CameraActivity)localObject2, -16777216);
      continue;
      localObject1 = new EmptyPreviewCoverProducer();
      continue;
      localObject1 = new BlurPreviewCoverProducer((CameraActivity)localObject2);
      continue;
      localObject1 = new FlipBlurPreviewCoverProducer((CameraActivity)localObject2);
      continue;
      localObject1 = new NormalPreviewCoverProducer((CameraActivity)localObject2);
    }
  }
  
  private PreviewCover.UIState mappingToExternalUIState(PreviewCoverProducer paramPreviewCoverProducer, PreviewCoverProducer.State paramState)
  {
    if (paramPreviewCoverProducer == null) {
      return PreviewCover.UIState.CLOSED;
    }
    PreviewCover.UIState localUIState = PreviewCover.UIState.CLOSED;
    switch (-getcom-oneplus-camera-ui-PreviewCoverProducer$StateSwitchesValues()[paramState.ordinal()])
    {
    default: 
      paramState = localUIState;
    case 1: 
      do
      {
        return paramState;
        paramState = PreviewCover.UIState.OPENING;
      } while (paramPreviewCoverProducer.isAlphaBlending());
      return PreviewCover.UIState.OPENED;
    case 3: 
    case 4: 
      return PreviewCover.UIState.PREPARE_TO_OPEN;
    case 5: 
      return PreviewCover.UIState.OPENED;
    case 2: 
      return PreviewCover.UIState.CLOSING;
    }
    return PreviewCover.UIState.CLOSED;
  }
  
  private void onPreviewCoverHandleClose(PreviewCoverHandle paramPreviewCoverHandle, int paramInt)
  {
    if (this.m_PreviewCoverHandles.remove(paramPreviewCoverHandle))
    {
      int j = 1;
      Iterator localIterator = this.m_PreviewCoverHandles.iterator();
      do
      {
        i = j;
        if (!localIterator.hasNext()) {
          break;
        }
      } while (((PreviewCoverHandle)localIterator.next()).style != paramPreviewCoverHandle.style);
      int i = 0;
      if (i != 0) {
        getPreviewCoverProducer(paramPreviewCoverHandle.style).hidePreviewCover(paramInt);
      }
    }
  }
  
  private void onPreviewCoverProducerStateChanged(PreviewCover.Style paramStyle, PreviewCoverProducer.State paramState1, PreviewCoverProducer.State paramState2)
  {
    Log.v(this.TAG, "onPreviewCoverProducerStateChanged() - Style: ", paramStyle, ", old value: ", paramState1, ", new value: ", paramState2);
    Object localObject = getPreviewCoverProducer(paramStyle);
    paramState1 = mappingToExternalUIState((PreviewCoverProducer)localObject, paramState1);
    paramState2 = mappingToExternalUIState((PreviewCoverProducer)localObject, paramState2);
    localObject = (Set)this.m_PreviewCoverListeners.get(paramStyle);
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((PreviewCover.OnStateChangedListener)((Iterator)localObject).next()).onStateChanged(paramState1, paramState2);
      }
    }
    if (paramStyle != null)
    {
      paramStyle = (Set)this.m_PreviewCoverListeners.get(null);
      if (paramStyle != null)
      {
        paramStyle = paramStyle.iterator();
        while (paramStyle.hasNext()) {
          ((PreviewCover.OnStateChangedListener)paramStyle.next()).onStateChanged(paramState1, paramState2);
        }
      }
    }
  }
  
  public void addOnStateChangedListener(PreviewCover.Style paramStyle, PreviewCover.OnStateChangedListener paramOnStateChangedListener)
  {
    Set localSet = (Set)this.m_PreviewCoverListeners.get(paramStyle);
    Object localObject = localSet;
    if (localSet == null)
    {
      localObject = new HashSet();
      this.m_PreviewCoverListeners.put(paramStyle, localObject);
    }
    ((Set)localObject).add(paramOnStateChangedListener);
  }
  
  public PreviewCover.UIState getPreviewCoverState(PreviewCover.Style paramStyle)
  {
    paramStyle = getPreviewCoverProducer(paramStyle);
    return mappingToExternalUIState(paramStyle, (PreviewCoverProducer.State)paramStyle.get(PreviewCoverProducer.PROP_STATE));
  }
  
  protected void onDeinitialize()
  {
    Iterator localIterator = this.m_PreviewCoverProducers.values().iterator();
    while (localIterator.hasNext()) {
      ((PreviewCoverProducer)localIterator.next()).release();
    }
    this.m_PreviewCoverProducers.clear();
    this.m_PreviewCoverHandles.clear();
    this.m_PreviewCoverListeners.clear();
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraActivity().addCallback(CameraActivity.PROP_IS_RUNNING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if ((((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) || (PreviewCoverImpl.-get1(PreviewCoverImpl.this).isEmpty())) {}
        for (;;)
        {
          return;
          Log.v(PreviewCoverImpl.-get0(PreviewCoverImpl.this), "onInitialize() - Clear preview cover handles when pausing: ", Integer.valueOf(PreviewCoverImpl.-get1(PreviewCoverImpl.this).size()));
          while (!PreviewCoverImpl.-get1(PreviewCoverImpl.this).isEmpty()) {
            Handle.close((PreviewCoverImpl.PreviewCoverHandle)PreviewCoverImpl.-get1(PreviewCoverImpl.this).get(0), 1);
          }
        }
      }
    });
  }
  
  public void removeOnStateChangedListener(PreviewCover.Style paramStyle, PreviewCover.OnStateChangedListener paramOnStateChangedListener)
  {
    Set localSet = (Set)this.m_PreviewCoverListeners.get(paramStyle);
    if (localSet == null) {
      return;
    }
    if ((localSet.remove(paramOnStateChangedListener)) && (localSet.size() == 0)) {
      this.m_PreviewCoverListeners.remove(paramStyle);
    }
  }
  
  public Handle showPreviewCover(PreviewCover.Style paramStyle, int paramInt)
  {
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_RUNNING)).booleanValue())
    {
      Log.w(this.TAG, "showPreviewCover() - Activity is not running.");
      return null;
    }
    if (getPreviewCoverProducer(paramStyle) == null) {
      return null;
    }
    if (getPreviewCoverProducer(paramStyle).showPreviewCover(0))
    {
      paramStyle = new PreviewCoverHandle(paramStyle);
      this.m_PreviewCoverHandles.add(paramStyle);
      return paramStyle;
    }
    return null;
  }
  
  private class PreviewCoverHandle
    extends Handle
  {
    final PreviewCover.Style style;
    
    PreviewCoverHandle(PreviewCover.Style paramStyle)
    {
      super();
      this.style = paramStyle;
    }
    
    protected void onClose(int paramInt)
    {
      PreviewCoverImpl.-wrap0(PreviewCoverImpl.this, this, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/PreviewCoverImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */