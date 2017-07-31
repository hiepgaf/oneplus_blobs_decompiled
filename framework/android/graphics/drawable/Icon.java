package android.graphics.drawable;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.Executor;

public final class Icon
  implements Parcelable
{
  public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator()
  {
    public Icon createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Icon(paramAnonymousParcel, null);
    }
    
    public Icon[] newArray(int paramAnonymousInt)
    {
      return new Icon[paramAnonymousInt];
    }
  };
  static final PorterDuff.Mode DEFAULT_TINT_MODE = Drawable.DEFAULT_TINT_MODE;
  public static final int MIN_ASHMEM_ICON_SIZE = 131072;
  private static final String TAG = "Icon";
  public static final int TYPE_BITMAP = 1;
  public static final int TYPE_DATA = 3;
  public static final int TYPE_RESOURCE = 2;
  public static final int TYPE_URI = 4;
  private static final int VERSION_STREAM_SERIALIZER = 1;
  private int mInt1;
  private int mInt2;
  private Object mObj1;
  private String mString1;
  private ColorStateList mTintList;
  private PorterDuff.Mode mTintMode = DEFAULT_TINT_MODE;
  private final int mType;
  
  private Icon(int paramInt)
  {
    this.mType = paramInt;
  }
  
  private Icon(Parcel paramParcel)
  {
    this(paramParcel.readInt());
    switch (this.mType)
    {
    default: 
      throw new RuntimeException("invalid " + getClass().getSimpleName() + " type in parcel: " + this.mType);
    case 1: 
      this.mObj1 = ((Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel));
    }
    for (;;)
    {
      if (paramParcel.readInt() == 1) {
        this.mTintList = ((ColorStateList)ColorStateList.CREATOR.createFromParcel(paramParcel));
      }
      this.mTintMode = PorterDuff.intToMode(paramParcel.readInt());
      return;
      Object localObject = paramParcel.readString();
      int i = paramParcel.readInt();
      this.mString1 = ((String)localObject);
      this.mInt1 = i;
      continue;
      i = paramParcel.readInt();
      localObject = paramParcel.readBlob();
      if (i != localObject.length) {
        throw new RuntimeException("internal unparceling error: blob length (" + localObject.length + ") != expected length (" + i + ")");
      }
      this.mInt1 = i;
      this.mObj1 = localObject;
      continue;
      this.mString1 = paramParcel.readString();
    }
  }
  
  public static Icon createFromStream(InputStream paramInputStream)
    throws IOException
  {
    paramInputStream = new DataInputStream(paramInputStream);
    if (paramInputStream.readInt() >= 1) {}
    switch (paramInputStream.readByte())
    {
    default: 
      return null;
    case 1: 
      return createWithBitmap(BitmapFactory.decodeStream(paramInputStream));
    case 3: 
      int i = paramInputStream.readInt();
      byte[] arrayOfByte = new byte[i];
      paramInputStream.read(arrayOfByte, 0, i);
      return createWithData(arrayOfByte, 0, i);
    case 2: 
      return createWithResource(paramInputStream.readUTF(), paramInputStream.readInt());
    }
    return createWithContentUri(paramInputStream.readUTF());
  }
  
  public static Icon createWithBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      throw new IllegalArgumentException("Bitmap must not be null.");
    }
    Icon localIcon = new Icon(1);
    localIcon.setBitmap(paramBitmap);
    return localIcon;
  }
  
  public static Icon createWithContentUri(Uri paramUri)
  {
    if (paramUri == null) {
      throw new IllegalArgumentException("Uri must not be null.");
    }
    Icon localIcon = new Icon(4);
    localIcon.mString1 = paramUri.toString();
    return localIcon;
  }
  
  public static Icon createWithContentUri(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Uri must not be null.");
    }
    Icon localIcon = new Icon(4);
    localIcon.mString1 = paramString;
    return localIcon;
  }
  
  public static Icon createWithData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("Data must not be null.");
    }
    Icon localIcon = new Icon(3);
    localIcon.mObj1 = paramArrayOfByte;
    localIcon.mInt1 = paramInt2;
    localIcon.mInt2 = paramInt1;
    return localIcon;
  }
  
  public static Icon createWithFilePath(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Path must not be null.");
    }
    Icon localIcon = new Icon(4);
    localIcon.mString1 = paramString;
    return localIcon;
  }
  
  public static Icon createWithResource(Context paramContext, int paramInt)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("Context must not be null.");
    }
    Icon localIcon = new Icon(2);
    localIcon.mInt1 = paramInt;
    localIcon.mString1 = paramContext.getPackageName();
    return localIcon;
  }
  
  public static Icon createWithResource(Resources paramResources, int paramInt)
  {
    if (paramResources == null) {
      throw new IllegalArgumentException("Resource must not be null.");
    }
    Icon localIcon = new Icon(2);
    localIcon.mInt1 = paramInt;
    localIcon.mString1 = paramResources.getResourcePackageName(paramInt);
    return localIcon;
  }
  
  public static Icon createWithResource(String paramString, int paramInt)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Resource package name must not be null.");
    }
    Icon localIcon = new Icon(2);
    localIcon.mInt1 = paramInt;
    localIcon.mString1 = paramString;
    return localIcon;
  }
  
  private Drawable loadDrawableInner(Context paramContext)
  {
    switch (this.mType)
    {
    default: 
    case 1: 
    case 2: 
      for (;;)
      {
        return null;
        return new BitmapDrawable(paramContext.getResources(), getBitmap());
        if (getResources() == null)
        {
          localObject2 = getResPackage();
          localObject1 = localObject2;
          if (TextUtils.isEmpty((CharSequence)localObject2)) {
            localObject1 = paramContext.getPackageName();
          }
          if (!"android".equals(localObject1)) {
            break label115;
          }
          this.mObj1 = Resources.getSystem();
        }
        try
        {
          paramContext = getResources().getDrawable(getResId(), paramContext.getTheme());
          return paramContext;
        }
        catch (RuntimeException paramContext)
        {
          Log.e("Icon", String.format("Unable to load resource 0x%08x from pkg=%s", new Object[] { Integer.valueOf(getResId()), getResPackage() }), paramContext);
        }
        localObject2 = paramContext.getPackageManager();
        try
        {
          localObject3 = ((PackageManager)localObject2).getApplicationInfo((String)localObject1, 8192);
          if (localObject3 == null) {
            continue;
          }
          this.mObj1 = ((PackageManager)localObject2).getResourcesForApplication((ApplicationInfo)localObject3);
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          Log.e("Icon", String.format("Unable to find pkg=%s for icon %s", new Object[] { localObject1, this }), paramContext);
        }
      }
    case 3: 
      label115:
      return new BitmapDrawable(paramContext.getResources(), BitmapFactory.decodeByteArray(getDataBytes(), getDataOffset(), getDataLength()));
    }
    Object localObject3 = getUri();
    Object localObject2 = ((Uri)localObject3).getScheme();
    Object localObject1 = null;
    if (("content".equals(localObject2)) || ("file".equals(localObject2))) {}
    for (;;)
    {
      try
      {
        localObject2 = paramContext.getContentResolver().openInputStream((Uri)localObject3);
        localObject1 = localObject2;
      }
      catch (Exception localException)
      {
        Log.w("Icon", "Unable to load image from URI: " + localObject3, localException);
        continue;
      }
      if (localObject1 == null) {
        break;
      }
      return new BitmapDrawable(paramContext.getResources(), BitmapFactory.decodeStream((InputStream)localObject1));
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(new File(this.mString1));
        localObject1 = localFileInputStream;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Log.w("Icon", "Unable to load image from path: " + localObject3, localFileNotFoundException);
      }
    }
  }
  
  private void setBitmap(Bitmap paramBitmap)
  {
    this.mObj1 = paramBitmap;
  }
  
  private static final String typeToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN";
    case 1: 
      return "BITMAP";
    case 3: 
      return "DATA";
    case 2: 
      return "RESOURCE";
    }
    return "URI";
  }
  
  public void convertToAshmem()
  {
    if ((this.mType == 1) && (getBitmap().isMutable()) && (getBitmap().getAllocationByteCount() >= 131072)) {
      setBitmap(getBitmap().createAshmemBitmap());
    }
  }
  
  public int describeContents()
  {
    if ((this.mType == 1) || (this.mType == 3)) {
      return 1;
    }
    return 0;
  }
  
  public Bitmap getBitmap()
  {
    if (this.mType != 1) {
      throw new IllegalStateException("called getBitmap() on " + this);
    }
    return (Bitmap)this.mObj1;
  }
  
  public byte[] getDataBytes()
  {
    if (this.mType != 3) {
      throw new IllegalStateException("called getDataBytes() on " + this);
    }
    try
    {
      byte[] arrayOfByte = (byte[])this.mObj1;
      return arrayOfByte;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getDataLength()
  {
    if (this.mType != 3) {
      throw new IllegalStateException("called getDataLength() on " + this);
    }
    try
    {
      int i = this.mInt1;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getDataOffset()
  {
    if (this.mType != 3) {
      throw new IllegalStateException("called getDataOffset() on " + this);
    }
    try
    {
      int i = this.mInt2;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getResId()
  {
    if (this.mType != 2) {
      throw new IllegalStateException("called getResId() on " + this);
    }
    return this.mInt1;
  }
  
  public String getResPackage()
  {
    if (this.mType != 2) {
      throw new IllegalStateException("called getResPackage() on " + this);
    }
    return this.mString1;
  }
  
  public Resources getResources()
  {
    if (this.mType != 2) {
      throw new IllegalStateException("called getResources() on " + this);
    }
    return (Resources)this.mObj1;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public Uri getUri()
  {
    return Uri.parse(getUriString());
  }
  
  public String getUriString()
  {
    if (this.mType != 4) {
      throw new IllegalStateException("called getUriString() on " + this);
    }
    return this.mString1;
  }
  
  public boolean hasTint()
  {
    return (this.mTintList != null) || (this.mTintMode != DEFAULT_TINT_MODE);
  }
  
  public Drawable loadDrawable(Context paramContext)
  {
    paramContext = loadDrawableInner(paramContext);
    if ((paramContext != null) && ((this.mTintList != null) || (this.mTintMode != DEFAULT_TINT_MODE)))
    {
      paramContext.mutate();
      paramContext.setTintList(this.mTintList);
      paramContext.setTintMode(this.mTintMode);
    }
    return paramContext;
  }
  
  public Drawable loadDrawableAsUser(Context paramContext, int paramInt)
  {
    Object localObject2;
    Object localObject1;
    if (this.mType == 2)
    {
      localObject2 = getResPackage();
      localObject1 = localObject2;
      if (TextUtils.isEmpty((CharSequence)localObject2)) {
        localObject1 = paramContext.getPackageName();
      }
      if ((getResources() == null) && (!getResPackage().equals("android"))) {
        break label56;
      }
    }
    for (;;)
    {
      return loadDrawable(paramContext);
      label56:
      localObject2 = paramContext.getPackageManager();
      try
      {
        this.mObj1 = ((PackageManager)localObject2).getResourcesForApplicationAsUser((String)localObject1, paramInt);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("Icon", String.format("Unable to find pkg=%s user=%d", new Object[] { getResPackage(), Integer.valueOf(paramInt) }), localNameNotFoundException);
      }
    }
  }
  
  public void loadDrawableAsync(Context paramContext, OnDrawableLoadedListener paramOnDrawableLoadedListener, Handler paramHandler)
  {
    new LoadDrawableTask(paramContext, paramHandler, paramOnDrawableLoadedListener).runAsync();
  }
  
  public void loadDrawableAsync(Context paramContext, Message paramMessage)
  {
    if (paramMessage.getTarget() == null) {
      throw new IllegalArgumentException("callback message must have a target handler");
    }
    new LoadDrawableTask(paramContext, paramMessage).runAsync();
  }
  
  public boolean sameAs(Icon paramIcon)
  {
    boolean bool = false;
    if (paramIcon == this) {
      return true;
    }
    if (this.mType != paramIcon.getType()) {
      return false;
    }
    switch (this.mType)
    {
    default: 
      return false;
    case 1: 
      return getBitmap() == paramIcon.getBitmap();
    case 3: 
      if ((getDataLength() == paramIcon.getDataLength()) && (getDataOffset() == paramIcon.getDataOffset())) {
        return getDataBytes() == paramIcon.getDataBytes();
      }
      return false;
    case 2: 
      if (getResId() == paramIcon.getResId()) {
        bool = Objects.equals(getResPackage(), paramIcon.getResPackage());
      }
      return bool;
    }
    return Objects.equals(getUriString(), paramIcon.getUriString());
  }
  
  public Icon setTint(int paramInt)
  {
    return setTintList(ColorStateList.valueOf(paramInt));
  }
  
  public Icon setTintList(ColorStateList paramColorStateList)
  {
    this.mTintList = paramColorStateList;
    return this;
  }
  
  public Icon setTintMode(PorterDuff.Mode paramMode)
  {
    this.mTintMode = paramMode;
    return this;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Icon(typ=").append(typeToString(this.mType));
    switch (this.mType)
    {
    }
    while (this.mTintList != null)
    {
      localStringBuilder.append(" tint=");
      String str = "";
      int[] arrayOfInt = this.mTintList.getColors();
      int j = arrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        localStringBuilder.append(String.format("%s0x%08x", new Object[] { str, Integer.valueOf(arrayOfInt[i]) }));
        str = "|";
        i += 1;
      }
      localStringBuilder.append(" size=").append(getBitmap().getWidth()).append("x").append(getBitmap().getHeight());
      continue;
      localStringBuilder.append(" pkg=").append(getResPackage()).append(" id=").append(String.format("0x%08x", new Object[] { Integer.valueOf(getResId()) }));
      continue;
      localStringBuilder.append(" len=").append(getDataLength());
      if (getDataOffset() != 0)
      {
        localStringBuilder.append(" off=").append(getDataOffset());
        continue;
        localStringBuilder.append(" uri=").append(getUriString());
      }
    }
    if (this.mTintMode != DEFAULT_TINT_MODE) {
      localStringBuilder.append(" mode=").append(this.mTintMode);
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    switch (this.mType)
    {
    default: 
      if (this.mTintList == null) {
        paramParcel.writeInt(0);
      }
      break;
    }
    for (;;)
    {
      paramParcel.writeInt(PorterDuff.modeToInt(this.mTintMode));
      return;
      getBitmap();
      getBitmap().writeToParcel(paramParcel, paramInt);
      break;
      paramParcel.writeString(getResPackage());
      paramParcel.writeInt(getResId());
      break;
      paramParcel.writeInt(getDataLength());
      paramParcel.writeBlob(getDataBytes(), getDataOffset(), getDataLength());
      break;
      paramParcel.writeString(getUriString());
      break;
      paramParcel.writeInt(1);
      this.mTintList.writeToParcel(paramParcel, paramInt);
    }
  }
  
  public void writeToStream(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream = new DataOutputStream(paramOutputStream);
    paramOutputStream.writeInt(1);
    paramOutputStream.writeByte(this.mType);
    switch (this.mType)
    {
    default: 
      return;
    case 1: 
      getBitmap().compress(Bitmap.CompressFormat.PNG, 100, paramOutputStream);
      return;
    case 3: 
      paramOutputStream.writeInt(getDataLength());
      paramOutputStream.write(getDataBytes(), getDataOffset(), getDataLength());
      return;
    case 2: 
      paramOutputStream.writeUTF(getResPackage());
      paramOutputStream.writeInt(getResId());
      return;
    }
    paramOutputStream.writeUTF(getUriString());
  }
  
  private class LoadDrawableTask
    implements Runnable
  {
    final Context mContext;
    final Message mMessage;
    
    public LoadDrawableTask(Context paramContext, Handler paramHandler, final Icon.OnDrawableLoadedListener paramOnDrawableLoadedListener)
    {
      this.mContext = paramContext;
      this.mMessage = Message.obtain(paramHandler, new Runnable()
      {
        public void run()
        {
          paramOnDrawableLoadedListener.onDrawableLoaded((Drawable)Icon.LoadDrawableTask.this.mMessage.obj);
        }
      });
    }
    
    public LoadDrawableTask(Context paramContext, Message paramMessage)
    {
      this.mContext = paramContext;
      this.mMessage = paramMessage;
    }
    
    public void run()
    {
      this.mMessage.obj = Icon.this.loadDrawable(this.mContext);
      this.mMessage.sendToTarget();
    }
    
    public void runAsync()
    {
      AsyncTask.THREAD_POOL_EXECUTOR.execute(this);
    }
  }
  
  public static abstract interface OnDrawableLoadedListener
  {
    public abstract void onDrawableLoaded(Drawable paramDrawable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/Icon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */