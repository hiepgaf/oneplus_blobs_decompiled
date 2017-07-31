package com.oneplus.gallery;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.location.Location;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.oneplus.base.Handle;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.DialogManager;
import com.oneplus.gallery2.media.Media;
import com.oneplus.gallery2.media.Media.DetailsCallback;
import com.oneplus.gallery2.media.Media.SizeCallback;
import com.oneplus.gallery2.media.MediaDetails;
import com.oneplus.gallery2.media.MediaType;
import com.oneplus.gallery2.media.PhotoMediaDetails;
import com.oneplus.gallery2.media.SimplePhotoMediaDetails;
import com.oneplus.gallery2.media.SimpleVideoMediaDetails;
import com.oneplus.gallery2.media.VideoMedia;
import com.oneplus.gallery2.media.VideoMedia.DurationCallback;
import com.oneplus.io.FileUtils;
import com.oneplus.io.Path;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaDetailsDialog
{
  private final CameraActivity m_CameraActivity;
  private AlertDialog m_Dialog;
  private Handle m_DialogHandle;
  private DialogManager m_DialogManager;
  private boolean m_IsShown;
  private final Media m_Media;
  private MediaDetails m_MediaDetails;
  private Handle m_MediaDetailsObtainHandle;
  private Size m_MediaSize;
  private Handle m_MediaSizeObtainHandle;
  private DialogInterface.OnDismissListener m_OnDismissListener;
  private Long m_VideoDuration;
  private Handle m_VideoDurationHandle;
  
  public MediaDetailsDialog(CameraActivity paramCameraActivity, Media paramMedia)
  {
    if (paramCameraActivity == null) {
      throw new IllegalArgumentException("No camera activity.");
    }
    if (paramMedia == null) {
      throw new IllegalArgumentException("No media.");
    }
    this.m_CameraActivity = paramCameraActivity;
    this.m_Media = paramMedia;
  }
  
  private boolean checkMediaInfoState()
  {
    if ((this.m_MediaDetails == null) || (this.m_MediaSize == null)) {
      return false;
    }
    if ((this.m_Media.getType() == MediaType.VIDEO) && (this.m_VideoDuration == null)) {
      return false;
    }
    if (!this.m_IsShown) {
      return true;
    }
    Object localObject;
    int i;
    if (this.m_Dialog == null)
    {
      View localView = View.inflate(this.m_CameraActivity, 2130903080, null);
      localObject = (ViewGroup)localView.findViewById(2131362008);
      switch (-getcom-oneplus-gallery2-media-MediaTypeSwitchesValues()[this.m_Media.getType().ordinal()])
      {
      default: 
        return false;
      case 1: 
        preparePhotoDetails((ViewGroup)localObject);
        localObject = this.m_CameraActivity;
        if (((Boolean)this.m_CameraActivity.get(CameraActivity.PROP_IS_BLACK_MODE)).booleanValue())
        {
          i = 2131492908;
          label150:
          localObject = new AlertDialog.Builder((Context)localObject, i);
          ((AlertDialog.Builder)localObject).setTitle(2131558556).setView(localView).setPositiveButton(17039370, new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              MediaDetailsDialog.-set0(MediaDetailsDialog.this, false);
              paramAnonymousDialogInterface.dismiss();
            }
          }).setOnCancelListener(new DialogInterface.OnCancelListener()
          {
            public void onCancel(DialogInterface paramAnonymousDialogInterface)
            {
              MediaDetailsDialog.-set0(MediaDetailsDialog.this, false);
            }
          });
          if (this.m_OnDismissListener != null) {
            ((AlertDialog.Builder)localObject).setOnDismissListener(this.m_OnDismissListener);
          }
          this.m_Dialog = ((AlertDialog.Builder)localObject).create();
        }
        break;
      }
    }
    for (;;)
    {
      if (this.m_DialogManager != null)
      {
        this.m_DialogHandle = this.m_DialogManager.showDialog(this.m_Dialog, this.m_OnDismissListener, null, null, 1);
        return true;
        prepareVideoDetails((ViewGroup)localObject);
        break;
        i = 2131492906;
        break label150;
        if (this.m_Dialog.isShowing()) {
          return true;
        }
      }
    }
    this.m_Dialog.show();
    return true;
  }
  
  private View createDateTimeItem(ViewGroup paramViewGroup, int paramInt, long paramLong)
  {
    Date localDate = new Date(paramLong);
    return createStringItem(paramViewGroup, paramInt, DateFormat.getDateTimeInstance().format(localDate));
  }
  
  private View createDoubleItem(ViewGroup paramViewGroup, int paramInt, double paramDouble, String paramString)
  {
    return createStringItem(paramViewGroup, paramInt, String.format(Locale.US, paramString, new Object[] { Double.valueOf(paramDouble) }));
  }
  
  private MediaDetails createEmptyMediaDetails()
  {
    switch (-getcom-oneplus-gallery2-media-MediaTypeSwitchesValues()[this.m_Media.getType().ordinal()])
    {
    default: 
      return null;
    case 1: 
      return new SimplePhotoMediaDetails(null);
    }
    return new SimpleVideoMediaDetails(null);
  }
  
  private View createIntItem(ViewGroup paramViewGroup, int paramInt1, int paramInt2)
  {
    return createStringItem(paramViewGroup, paramInt1, Integer.toString(paramInt2));
  }
  
  private View createStringItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    if (paramObject != null) {
      return createStringItem(paramViewGroup, paramInt, paramObject.toString());
    }
    return null;
  }
  
  private View createStringItem(ViewGroup paramViewGroup, int paramInt, String paramString)
  {
    View.inflate(this.m_CameraActivity, 2130903081, paramViewGroup);
    paramViewGroup = (TextView)paramViewGroup.getChildAt(paramViewGroup.getChildCount() - 1);
    paramViewGroup.setText(String.format("%s: %s", new Object[] { this.m_CameraActivity.getString(paramInt), paramString }));
    if (((Boolean)this.m_CameraActivity.get(CameraActivity.PROP_IS_BLACK_MODE)).booleanValue()) {}
    for (paramInt = 2131492908;; paramInt = 2131492906)
    {
      paramString = this.m_CameraActivity.obtainStyledAttributes(paramInt, new int[] { 16842808 });
      paramViewGroup.setTextColor(paramString.getColor(0, -16777216));
      paramString.recycle();
      return paramViewGroup;
    }
  }
  
  private void preparePhotoDetails(ViewGroup paramViewGroup)
  {
    String str = this.m_Media.getFilePath();
    if (str != null) {
      createStringItem(paramViewGroup, 2131558557, Path.getFileNameWithoutExtension(str));
    }
    long l = this.m_Media.getTakenTime();
    if (l > 0L) {
      createDateTimeItem(paramViewGroup, 2131558558, l);
    }
    Object localObject = this.m_Media.getLocation();
    if ((localObject instanceof Location))
    {
      localObject = (Location)localObject;
      createStringItem(paramViewGroup, 2131558559, String.format(Locale.US, "%.6f, %.6f", new Object[] { Double.valueOf(((Location)localObject).getLatitude()), Double.valueOf(((Location)localObject).getLongitude()) }));
    }
    createStringItem(paramViewGroup, 2131558562, String.format(Locale.US, "%dx%d", new Object[] { Integer.valueOf(this.m_MediaSize.getWidth()), Integer.valueOf(this.m_MediaSize.getHeight()) }));
    l = this.m_Media.getFileSize();
    if (l > 0L) {
      createStringItem(paramViewGroup, 2131558564, FileUtils.getFileSizeDescription(l));
    }
    createStringItem(paramViewGroup, 2131558565, this.m_MediaDetails.get(PhotoMediaDetails.KEY_CAMERA_MANUFACTURER, null));
    createStringItem(paramViewGroup, 2131558566, this.m_MediaDetails.get(PhotoMediaDetails.KEY_CAMERA_MODEL, null));
    if (((Boolean)this.m_MediaDetails.get(PhotoMediaDetails.KEY_IS_FLASH_FIRED, Boolean.valueOf(false))).booleanValue())
    {
      localObject = this.m_CameraActivity.getString(2131558570);
      createStringItem(paramViewGroup, 2131558569, (String)localObject);
      double d = ((Double)this.m_MediaDetails.get(PhotoMediaDetails.KEY_FOCAL_LENGTH, Double.valueOf(NaN.0D))).doubleValue();
      if (!Double.isNaN(d)) {
        createDoubleItem(paramViewGroup, 2131558572, d, "%.2f mm");
      }
      if (((Integer)this.m_MediaDetails.get(PhotoMediaDetails.KEY_WHITE_BALANCE, Integer.valueOf(0))).intValue() != 1) {
        break label562;
      }
      localObject = this.m_CameraActivity.getString(2131558567);
      label384:
      createStringItem(paramViewGroup, 2131558573, (String)localObject);
      d = ((Double)this.m_MediaDetails.get(PhotoMediaDetails.KEY_APERTURE, Double.valueOf(NaN.0D))).doubleValue();
      if (!Double.isNaN(d)) {
        createDoubleItem(paramViewGroup, 2131558574, d, "f/%.1f");
      }
      localObject = (Rational)this.m_MediaDetails.get(PhotoMediaDetails.KEY_SHUTTER_SPEED, null);
      if (localObject != null)
      {
        if (((Rational)localObject).getNumerator() >= ((Rational)localObject).getDenominator()) {
          break label577;
        }
        createStringItem(paramViewGroup, 2131558575, localObject);
      }
    }
    for (;;)
    {
      int i = ((Integer)this.m_MediaDetails.get(PhotoMediaDetails.KEY_ISO_SPEED, Integer.valueOf(0))).intValue();
      if (i > 0) {
        createStringItem(paramViewGroup, 2131558576, Integer.valueOf(i));
      }
      if (str != null) {
        createStringItem(paramViewGroup, 2131558578, str);
      }
      return;
      localObject = this.m_CameraActivity.getString(2131558571);
      break;
      label562:
      localObject = this.m_CameraActivity.getString(2131558568);
      break label384;
      label577:
      if (((Rational)localObject).getDenominator() != 0)
      {
        i = ((Rational)localObject).getNumerator() / ((Rational)localObject).getDenominator();
        int j = ((Rational)localObject).getNumerator() % ((Rational)localObject).getDenominator();
        if (j != 0)
        {
          localObject = new Rational(j, ((Rational)localObject).getDenominator());
          createStringItem(paramViewGroup, 2131558575, String.format(Locale.US, "%d\"%s", new Object[] { Integer.valueOf(i), localObject }));
        }
        else
        {
          createStringItem(paramViewGroup, 2131558575, String.format(Locale.US, "%d\"", new Object[] { Integer.valueOf(i) }));
        }
      }
    }
  }
  
  private void prepareVideoDetails(ViewGroup paramViewGroup)
  {
    String str = this.m_Media.getFilePath();
    if (str != null) {
      createStringItem(paramViewGroup, 2131558557, Path.getFileNameWithoutExtension(str));
    }
    long l1 = this.m_Media.getTakenTime();
    if (l1 > 0L) {
      createDateTimeItem(paramViewGroup, 2131558558, l1);
    }
    Location localLocation = this.m_Media.getLocation();
    if (localLocation != null) {
      createStringItem(paramViewGroup, 2131558559, String.format(Locale.US, "%.6f, %.6f", new Object[] { Double.valueOf(localLocation.getLatitude()), Double.valueOf(localLocation.getLongitude()) }));
    }
    createStringItem(paramViewGroup, 2131558562, String.format(Locale.US, "%dx%d", new Object[] { Integer.valueOf(this.m_MediaSize.getWidth()), Integer.valueOf(this.m_MediaSize.getHeight()) }));
    l1 = this.m_VideoDuration.longValue();
    long l2;
    long l3;
    if (l1 > 0L)
    {
      l2 = l1 + 500L;
      l1 = l2 / 3600000L;
      l3 = l2 % 3600000L;
      l2 = l3 / 60000L;
      l3 = l3 % 60000L / 1000L;
      if (l1 >= 1L) {
        break label310;
      }
      createStringItem(paramViewGroup, 2131558577, String.format(Locale.US, "%02d:%02d", new Object[] { Long.valueOf(l2), Long.valueOf(l3) }));
    }
    for (;;)
    {
      l1 = this.m_Media.getFileSize();
      if (l1 > 0L) {
        createStringItem(paramViewGroup, 2131558564, FileUtils.getFileSizeDescription(l1));
      }
      if (str != null) {
        createStringItem(paramViewGroup, 2131558578, str);
      }
      return;
      label310:
      createStringItem(paramViewGroup, 2131558577, String.format(Locale.US, "%02d:%02d:%02d", new Object[] { Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(l3) }));
    }
  }
  
  public void dismiss()
  {
    if (!this.m_IsShown) {
      return;
    }
    this.m_IsShown = false;
    if (Handle.isValid(this.m_DialogHandle)) {
      this.m_DialogHandle = Handle.close(this.m_DialogHandle);
    }
    while (this.m_Dialog == null) {
      return;
    }
    this.m_Dialog.dismiss();
  }
  
  public void show()
  {
    show(null);
  }
  
  public void show(DialogInterface.OnDismissListener paramOnDismissListener)
  {
    if (this.m_IsShown) {
      return;
    }
    if (!((Boolean)this.m_CameraActivity.get(CameraActivity.PROP_IS_RUNNING)).booleanValue()) {
      return;
    }
    if (this.m_DialogManager == null) {
      this.m_DialogManager = ((DialogManager)this.m_CameraActivity.findComponent(DialogManager.class));
    }
    this.m_IsShown = true;
    this.m_OnDismissListener = paramOnDismissListener;
    if (!checkMediaInfoState())
    {
      if ((this.m_MediaDetails == null) && (!Handle.isValid(this.m_MediaDetailsObtainHandle))) {
        break label140;
      }
      if ((this.m_MediaSize == null) && (!Handle.isValid(this.m_MediaSizeObtainHandle))) {
        break label182;
      }
      label102:
      if ((this.m_Media.getType() == MediaType.VIDEO) && (this.m_VideoDuration == null) && (!Handle.isValid(this.m_VideoDurationHandle))) {
        break label229;
      }
    }
    for (;;)
    {
      checkMediaInfoState();
      return;
      label140:
      this.m_MediaDetailsObtainHandle = this.m_Media.getDetails(new Media.DetailsCallback()
      {
        public void onDetailsObtained(Media paramAnonymousMedia, MediaDetails paramAnonymousMediaDetails)
        {
          paramAnonymousMedia = paramAnonymousMediaDetails;
          if (paramAnonymousMediaDetails == null) {
            paramAnonymousMedia = MediaDetailsDialog.-wrap1(MediaDetailsDialog.this);
          }
          MediaDetailsDialog.-set1(MediaDetailsDialog.this, paramAnonymousMedia);
          MediaDetailsDialog.-wrap0(MediaDetailsDialog.this);
        }
      });
      if (Handle.isValid(this.m_MediaDetailsObtainHandle)) {
        break;
      }
      this.m_MediaDetails = createEmptyMediaDetails();
      break;
      label182:
      this.m_MediaSizeObtainHandle = this.m_Media.getSize(new Media.SizeCallback()
      {
        public void onSizeObtained(Media paramAnonymousMedia, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          MediaDetailsDialog.-set2(MediaDetailsDialog.this, new Size(paramAnonymousInt1, paramAnonymousInt2));
          MediaDetailsDialog.-wrap0(MediaDetailsDialog.this);
        }
      });
      if (Handle.isValid(this.m_MediaSizeObtainHandle)) {
        break label102;
      }
      this.m_MediaSize = new Size(0, 0);
      break label102;
      label229:
      this.m_VideoDurationHandle = ((VideoMedia)this.m_Media).getDuration(new VideoMedia.DurationCallback()
      {
        public void onDurationObtained(VideoMedia paramAnonymousVideoMedia, long paramAnonymousLong)
        {
          MediaDetailsDialog.-set3(MediaDetailsDialog.this, Long.valueOf(paramAnonymousLong));
          MediaDetailsDialog.-wrap0(MediaDetailsDialog.this);
        }
      });
      if (!Handle.isValid(this.m_VideoDurationHandle)) {
        this.m_VideoDuration = Long.valueOf(0L);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/MediaDetailsDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */