package android.app.backup;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Slog;
import android.view.Display;
import android.view.WindowManager;
import java.io.File;

public class WallpaperBackupHelper
  extends FileBackupHelperBase
  implements BackupHelper
{
  private static final boolean DEBUG = false;
  private static final double MAX_HEIGHT_RATIO = 1.35D;
  private static final double MIN_HEIGHT_RATIO = 0.0D;
  private static final boolean REJECT_OUTSIZED_RESTORE = false;
  private static final String STAGE_FILE = new File(Environment.getUserSystemDirectory(0), "wallpaper-tmp").getAbsolutePath();
  private static final String TAG = "WallpaperBackupHelper";
  public static final String WALLPAPER_IMAGE = new File(Environment.getUserSystemDirectory(0), "wallpaper").getAbsolutePath();
  public static final String WALLPAPER_IMAGE_KEY = "/data/data/com.android.settings/files/wallpaper";
  public static final String WALLPAPER_INFO;
  public static final String WALLPAPER_INFO_KEY = "/data/system/wallpaper_info.xml";
  public static final String WALLPAPER_ORIG_IMAGE = new File(Environment.getUserSystemDirectory(0), "wallpaper_orig").getAbsolutePath();
  Context mContext;
  double mDesiredMinHeight;
  double mDesiredMinWidth;
  String[] mFiles;
  String[] mKeys;
  
  static
  {
    WALLPAPER_INFO = new File(Environment.getUserSystemDirectory(0), "wallpaper_info.xml").getAbsolutePath();
  }
  
  public WallpaperBackupHelper(Context paramContext, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mFiles = paramArrayOfString1;
    this.mKeys = paramArrayOfString2;
    paramArrayOfString1 = (WindowManager)paramContext.getSystemService("window");
    paramContext = (WallpaperManager)paramContext.getSystemService("wallpaper");
    paramArrayOfString1 = paramArrayOfString1.getDefaultDisplay();
    paramArrayOfString2 = new Point();
    paramArrayOfString1.getSize(paramArrayOfString2);
    this.mDesiredMinWidth = Math.min(paramArrayOfString2.x, paramArrayOfString2.y);
    this.mDesiredMinHeight = paramContext.getDesiredMinimumHeight();
    if (this.mDesiredMinHeight <= 0.0D) {
      this.mDesiredMinHeight = paramArrayOfString2.y;
    }
  }
  
  public void onRestoreFinished()
  {
    File localFile = new File(STAGE_FILE);
    if (localFile.exists())
    {
      Slog.d("WallpaperBackupHelper", "Applying restored wallpaper image.");
      localFile.renameTo(new File(WALLPAPER_ORIG_IMAGE));
    }
  }
  
  public void performBackup(ParcelFileDescriptor paramParcelFileDescriptor1, BackupDataOutput paramBackupDataOutput, ParcelFileDescriptor paramParcelFileDescriptor2)
  {
    performBackup_checked(paramParcelFileDescriptor1, paramBackupDataOutput, paramParcelFileDescriptor2, this.mFiles, this.mKeys);
  }
  
  public void restoreEntity(BackupDataInputStream paramBackupDataInputStream)
  {
    String str = paramBackupDataInputStream.getKey();
    if (isKeyInList(str, this.mKeys))
    {
      if (!str.equals("/data/data/com.android.settings/files/wallpaper")) {
        break label66;
      }
      if (writeFile(new File(STAGE_FILE), paramBackupDataInputStream))
      {
        paramBackupDataInputStream = new BitmapFactory.Options();
        paramBackupDataInputStream.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(STAGE_FILE, paramBackupDataInputStream);
      }
    }
    label66:
    while (!str.equals("/data/system/wallpaper_info.xml")) {
      return;
    }
    writeFile(new File(WALLPAPER_INFO), paramBackupDataInputStream);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/WallpaperBackupHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */