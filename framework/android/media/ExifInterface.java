package android.media;

import android.content.res.AssetManager.AssetInputStream;
import android.os.Build;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import android.util.Pair;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.io.IoUtils;

public class ExifInterface
{
  private static final Charset ASCII;
  private static final short BYTE_ALIGN_II = 18761;
  private static final short BYTE_ALIGN_MM = 19789;
  private static final boolean DEBUG = false;
  private static final boolean DEBUG_ONEPLUS = Build.DEBUG_ONEPLUS;
  private static final byte[] EXIF_ASCII_PREFIX;
  private static final ExifTag[][] EXIF_TAGS;
  private static final byte[] IDENTIFIER_EXIF_APP1;
  private static final int IFD_EXIF_HINT = 1;
  private static final ExifTag[] IFD_EXIF_TAGS;
  private static final int IFD_FORMAT_BYTE = 1;
  private static final int[] IFD_FORMAT_BYTES_PER_FORMAT;
  private static final int IFD_FORMAT_DOUBLE = 12;
  private static final String[] IFD_FORMAT_NAMES;
  private static final int IFD_FORMAT_SBYTE = 6;
  private static final int IFD_FORMAT_SINGLE = 11;
  private static final int IFD_FORMAT_SLONG = 9;
  private static final int IFD_FORMAT_SRATIONAL = 10;
  private static final int IFD_FORMAT_SSHORT = 8;
  private static final int IFD_FORMAT_STRING = 2;
  private static final int IFD_FORMAT_ULONG = 4;
  private static final int IFD_FORMAT_UNDEFINED = 7;
  private static final int IFD_FORMAT_URATIONAL = 5;
  private static final int IFD_FORMAT_USHORT = 3;
  private static final int IFD_GPS_HINT = 2;
  private static final ExifTag[] IFD_GPS_TAGS;
  private static final int IFD_INTEROPERABILITY_HINT = 3;
  private static final ExifTag[] IFD_INTEROPERABILITY_TAGS;
  private static final ExifTag[] IFD_POINTER_TAGS;
  private static final int[] IFD_POINTER_TAG_HINTS;
  private static final int IFD_THUMBNAIL_HINT = 4;
  private static final ExifTag[] IFD_THUMBNAIL_TAGS;
  private static final int IFD_TIFF_HINT = 0;
  private static final ExifTag[] IFD_TIFF_TAGS;
  private static final ExifTag JPEG_INTERCHANGE_FORMAT_LENGTH_TAG;
  private static final ExifTag JPEG_INTERCHANGE_FORMAT_TAG;
  private static final byte[] JPEG_SIGNATURE = { -1, -40, -1 };
  private static final int JPEG_SIGNATURE_SIZE = 3;
  private static final byte MARKER = -1;
  private static final byte MARKER_APP1 = -31;
  private static final byte MARKER_COM = -2;
  private static final byte MARKER_EOI = -39;
  private static final byte MARKER_SOF0 = -64;
  private static final byte MARKER_SOF1 = -63;
  private static final byte MARKER_SOF10 = -54;
  private static final byte MARKER_SOF11 = -53;
  private static final byte MARKER_SOF13 = -51;
  private static final byte MARKER_SOF14 = -50;
  private static final byte MARKER_SOF15 = -49;
  private static final byte MARKER_SOF2 = -62;
  private static final byte MARKER_SOF3 = -61;
  private static final byte MARKER_SOF5 = -59;
  private static final byte MARKER_SOF6 = -58;
  private static final byte MARKER_SOF7 = -57;
  private static final byte MARKER_SOF9 = -55;
  private static final byte MARKER_SOI = -40;
  private static final byte MARKER_SOS = -38;
  public static final int ORIENTATION_FLIP_HORIZONTAL = 2;
  public static final int ORIENTATION_FLIP_VERTICAL = 4;
  public static final int ORIENTATION_NORMAL = 1;
  public static final int ORIENTATION_ROTATE_180 = 3;
  public static final int ORIENTATION_ROTATE_270 = 8;
  public static final int ORIENTATION_ROTATE_90 = 6;
  public static final int ORIENTATION_TRANSPOSE = 5;
  public static final int ORIENTATION_TRANSVERSE = 7;
  public static final int ORIENTATION_UNDEFINED = 0;
  private static final String TAG = "ExifInterface";
  @Deprecated
  public static final String TAG_APERTURE = "FNumber";
  public static final String TAG_APERTURE_VALUE = "ApertureValue";
  public static final String TAG_ARTIST = "Artist";
  public static final String TAG_BITS_PER_SAMPLE = "BitsPerSample";
  public static final String TAG_BRIGHTNESS_VALUE = "BrightnessValue";
  public static final String TAG_CFA_PATTERN = "CFAPattern";
  public static final String TAG_COLOR_SPACE = "ColorSpace";
  public static final String TAG_COMPONENTS_CONFIGURATION = "ComponentsConfiguration";
  public static final String TAG_COMPRESSED_BITS_PER_PIXEL = "CompressedBitsPerPixel";
  public static final String TAG_COMPRESSION = "Compression";
  public static final String TAG_CONTRAST = "Contrast";
  public static final String TAG_COPYRIGHT = "Copyright";
  public static final String TAG_CUSTOM_RENDERED = "CustomRendered";
  public static final String TAG_DATETIME = "DateTime";
  public static final String TAG_DATETIME_DIGITIZED = "DateTimeDigitized";
  public static final String TAG_DATETIME_ORIGINAL = "DateTimeOriginal";
  public static final String TAG_DEVICE_SETTING_DESCRIPTION = "DeviceSettingDescription";
  public static final String TAG_DIGITAL_ZOOM_RATIO = "DigitalZoomRatio";
  private static final String TAG_EXIF_IFD_POINTER = "ExifIFDPointer";
  public static final String TAG_EXIF_VERSION = "ExifVersion";
  public static final String TAG_EXPOSURE_BIAS_VALUE = "ExposureBiasValue";
  public static final String TAG_EXPOSURE_INDEX = "ExposureIndex";
  public static final String TAG_EXPOSURE_MODE = "ExposureMode";
  public static final String TAG_EXPOSURE_PROGRAM = "ExposureProgram";
  public static final String TAG_EXPOSURE_TIME = "ExposureTime";
  public static final String TAG_FILE_SOURCE = "FileSource";
  public static final String TAG_FLASH = "Flash";
  public static final String TAG_FLASHPIX_VERSION = "FlashpixVersion";
  public static final String TAG_FLASH_ENERGY = "FlashEnergy";
  public static final String TAG_FOCAL_LENGTH = "FocalLength";
  public static final String TAG_FOCAL_LENGTH_IN_35MM_FILM = "FocalLengthIn35mmFilm";
  public static final String TAG_FOCAL_PLANE_RESOLUTION_UNIT = "FocalPlaneResolutionUnit";
  public static final String TAG_FOCAL_PLANE_X_RESOLUTION = "FocalPlaneXResolution";
  public static final String TAG_FOCAL_PLANE_Y_RESOLUTION = "FocalPlaneYResolution";
  public static final String TAG_F_NUMBER = "FNumber";
  public static final String TAG_GAIN_CONTROL = "GainControl";
  public static final String TAG_GPS_ALTITUDE = "GPSAltitude";
  public static final String TAG_GPS_ALTITUDE_REF = "GPSAltitudeRef";
  public static final String TAG_GPS_AREA_INFORMATION = "GPSAreaInformation";
  public static final String TAG_GPS_DATESTAMP = "GPSDateStamp";
  public static final String TAG_GPS_DEST_BEARING = "GPSDestBearing";
  public static final String TAG_GPS_DEST_BEARING_REF = "GPSDestBearingRef";
  public static final String TAG_GPS_DEST_DISTANCE = "GPSDestDistance";
  public static final String TAG_GPS_DEST_DISTANCE_REF = "GPSDestDistanceRef";
  public static final String TAG_GPS_DEST_LATITUDE = "GPSDestLatitude";
  public static final String TAG_GPS_DEST_LATITUDE_REF = "GPSDestLatitudeRef";
  public static final String TAG_GPS_DEST_LONGITUDE = "GPSDestLongitude";
  public static final String TAG_GPS_DEST_LONGITUDE_REF = "GPSDestLongitudeRef";
  public static final String TAG_GPS_DIFFERENTIAL = "GPSDifferential";
  public static final String TAG_GPS_DOP = "GPSDOP";
  public static final String TAG_GPS_IMG_DIRECTION = "GPSImgDirection";
  public static final String TAG_GPS_IMG_DIRECTION_REF = "GPSImgDirectionRef";
  private static final String TAG_GPS_INFO_IFD_POINTER = "GPSInfoIFDPointer";
  public static final String TAG_GPS_LATITUDE = "GPSLatitude";
  public static final String TAG_GPS_LATITUDE_REF = "GPSLatitudeRef";
  public static final String TAG_GPS_LONGITUDE = "GPSLongitude";
  public static final String TAG_GPS_LONGITUDE_REF = "GPSLongitudeRef";
  public static final String TAG_GPS_MAP_DATUM = "GPSMapDatum";
  public static final String TAG_GPS_MEASURE_MODE = "GPSMeasureMode";
  public static final String TAG_GPS_PROCESSING_METHOD = "GPSProcessingMethod";
  public static final String TAG_GPS_SATELLITES = "GPSSatellites";
  public static final String TAG_GPS_SPEED = "GPSSpeed";
  public static final String TAG_GPS_SPEED_REF = "GPSSpeedRef";
  public static final String TAG_GPS_STATUS = "GPSStatus";
  public static final String TAG_GPS_TIMESTAMP = "GPSTimeStamp";
  public static final String TAG_GPS_TRACK = "GPSTrack";
  public static final String TAG_GPS_TRACK_REF = "GPSTrackRef";
  public static final String TAG_GPS_VERSION_ID = "GPSVersionID";
  private static final String TAG_HAS_THUMBNAIL = "HasThumbnail";
  public static final String TAG_IMAGE_DESCRIPTION = "ImageDescription";
  public static final String TAG_IMAGE_LENGTH = "ImageLength";
  public static final String TAG_IMAGE_UNIQUE_ID = "ImageUniqueID";
  public static final String TAG_IMAGE_WIDTH = "ImageWidth";
  private static final String TAG_INTEROPERABILITY_IFD_POINTER = "InteroperabilityIFDPointer";
  public static final String TAG_INTEROPERABILITY_INDEX = "InteroperabilityIndex";
  @Deprecated
  public static final String TAG_ISO = "ISOSpeedRatings";
  public static final String TAG_ISO_SPEED_RATINGS = "ISOSpeedRatings";
  public static final String TAG_JPEG_INTERCHANGE_FORMAT = "JPEGInterchangeFormat";
  public static final String TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = "JPEGInterchangeFormatLength";
  public static final String TAG_LIGHT_SOURCE = "LightSource";
  public static final String TAG_MAKE = "Make";
  public static final String TAG_MAKER_NOTE = "MakerNote";
  public static final String TAG_MAX_APERTURE_VALUE = "MaxApertureValue";
  public static final String TAG_METERING_MODE = "MeteringMode";
  public static final String TAG_MODEL = "Model";
  public static final String TAG_OECF = "OECF";
  public static final String TAG_ORIENTATION = "Orientation";
  public static final String TAG_PHOTOMETRIC_INTERPRETATION = "PhotometricInterpretation";
  public static final String TAG_PIXEL_X_DIMENSION = "PixelXDimension";
  public static final String TAG_PIXEL_Y_DIMENSION = "PixelYDimension";
  public static final String TAG_PLANAR_CONFIGURATION = "PlanarConfiguration";
  public static final String TAG_PRIMARY_CHROMATICITIES = "PrimaryChromaticities";
  public static final String TAG_REFERENCE_BLACK_WHITE = "ReferenceBlackWhite";
  public static final String TAG_RELATED_SOUND_FILE = "RelatedSoundFile";
  public static final String TAG_RESOLUTION_UNIT = "ResolutionUnit";
  public static final String TAG_ROWS_PER_STRIP = "RowsPerStrip";
  public static final String TAG_SAMPLES_PER_PIXEL = "SamplesPerPixel";
  public static final String TAG_SATURATION = "Saturation";
  public static final String TAG_SCENE_CAPTURE_TYPE = "SceneCaptureType";
  public static final String TAG_SCENE_TYPE = "SceneType";
  public static final String TAG_SENSING_METHOD = "SensingMethod";
  public static final String TAG_SHARPNESS = "Sharpness";
  public static final String TAG_SHUTTER_SPEED_VALUE = "ShutterSpeedValue";
  public static final String TAG_SOFTWARE = "Software";
  public static final String TAG_SPATIAL_FREQUENCY_RESPONSE = "SpatialFrequencyResponse";
  public static final String TAG_SPECTRAL_SENSITIVITY = "SpectralSensitivity";
  public static final String TAG_STRIP_BYTE_COUNTS = "StripByteCounts";
  public static final String TAG_STRIP_OFFSETS = "StripOffsets";
  public static final String TAG_SUBJECT_AREA = "SubjectArea";
  public static final String TAG_SUBJECT_DISTANCE = "SubjectDistance";
  public static final String TAG_SUBJECT_DISTANCE_RANGE = "SubjectDistanceRange";
  public static final String TAG_SUBJECT_LOCATION = "SubjectLocation";
  public static final String TAG_SUBSEC_TIME = "SubSecTime";
  public static final String TAG_SUBSEC_TIME_DIG = "SubSecTimeDigitized";
  public static final String TAG_SUBSEC_TIME_DIGITIZED = "SubSecTimeDigitized";
  public static final String TAG_SUBSEC_TIME_ORIG = "SubSecTimeOriginal";
  public static final String TAG_SUBSEC_TIME_ORIGINAL = "SubSecTimeOriginal";
  private static final String TAG_THUMBNAIL_DATA = "ThumbnailData";
  public static final String TAG_THUMBNAIL_IMAGE_LENGTH = "ThumbnailImageLength";
  public static final String TAG_THUMBNAIL_IMAGE_WIDTH = "ThumbnailImageWidth";
  private static final String TAG_THUMBNAIL_LENGTH = "ThumbnailLength";
  private static final String TAG_THUMBNAIL_OFFSET = "ThumbnailOffset";
  public static final String TAG_TRANSFER_FUNCTION = "TransferFunction";
  public static final String TAG_USER_COMMENT = "UserComment";
  public static final String TAG_WHITE_BALANCE = "WhiteBalance";
  public static final String TAG_WHITE_POINT = "WhitePoint";
  public static final String TAG_X_RESOLUTION = "XResolution";
  public static final String TAG_Y_CB_CR_COEFFICIENTS = "YCbCrCoefficients";
  public static final String TAG_Y_CB_CR_POSITIONING = "YCbCrPositioning";
  public static final String TAG_Y_CB_CR_SUB_SAMPLING = "YCbCrSubSampling";
  public static final String TAG_Y_RESOLUTION = "YResolution";
  public static final int WHITEBALANCE_AUTO = 0;
  public static final int WHITEBALANCE_MANUAL = 1;
  private static boolean m_Is_Raw_Input_Stream;
  private static final HashMap[] sExifTagMapsForReading;
  private static final HashMap[] sExifTagMapsForWriting;
  private static SimpleDateFormat sFormatter;
  private static final Pattern sGpsTimestampPattern = Pattern.compile("^([0-9][0-9]):([0-9][0-9]):([0-9][0-9])$");
  private static final Pattern sNonZeroTimePattern;
  private static final HashSet<String> sTagSetForCompatibility;
  private final AssetManager.AssetInputStream mAssetInputStream;
  private final HashMap[] mAttributes = new HashMap[EXIF_TAGS.length];
  private ByteOrder mExifByteOrder = ByteOrder.BIG_ENDIAN;
  private final String mFilename;
  private boolean mHasThumbnail;
  private final boolean mIsInputStream;
  private boolean mIsRaw;
  private boolean mIsSupportedFile;
  private final FileDescriptor mSeekableFileDescriptor;
  private byte[] mThumbnailBytes;
  private int mThumbnailLength;
  private int mThumbnailOffset;
  
  static
  {
    IFD_FORMAT_NAMES = new String[] { "", "BYTE", "STRING", "USHORT", "ULONG", "URATIONAL", "SBYTE", "UNDEFINED", "SSHORT", "SLONG", "SRATIONAL", "SINGLE", "DOUBLE" };
    IFD_FORMAT_BYTES_PER_FORMAT = new int[] { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8 };
    EXIF_ASCII_PREFIX = new byte[] { 65, 83, 67, 73, 73, 0, 0, 0 };
    m_Is_Raw_Input_Stream = false;
    IFD_TIFF_TAGS = new ExifTag[] { new ExifTag("ImageWidth", 256, 3, 4, null), new ExifTag("ImageLength", 257, 3, 4, null), new ExifTag("BitsPerSample", 258, 3, null), new ExifTag("Compression", 259, 3, null), new ExifTag("PhotometricInterpretation", 262, 3, null), new ExifTag("ImageDescription", 270, 2, null), new ExifTag("Make", 271, 2, null), new ExifTag("Model", 272, 2, null), new ExifTag("StripOffsets", 273, 3, 4, null), new ExifTag("Orientation", 274, 3, null), new ExifTag("SamplesPerPixel", 277, 3, null), new ExifTag("RowsPerStrip", 278, 3, 4, null), new ExifTag("StripByteCounts", 279, 3, 4, null), new ExifTag("XResolution", 282, 5, null), new ExifTag("YResolution", 283, 5, null), new ExifTag("PlanarConfiguration", 284, 3, null), new ExifTag("ResolutionUnit", 296, 3, null), new ExifTag("TransferFunction", 301, 3, null), new ExifTag("Software", 305, 2, null), new ExifTag("DateTime", 306, 2, null), new ExifTag("Artist", 315, 2, null), new ExifTag("WhitePoint", 318, 5, null), new ExifTag("PrimaryChromaticities", 319, 5, null), new ExifTag("JPEGInterchangeFormat", 513, 4, null), new ExifTag("JPEGInterchangeFormatLength", 514, 4, null), new ExifTag("YCbCrCoefficients", 529, 5, null), new ExifTag("YCbCrSubSampling", 530, 3, null), new ExifTag("YCbCrPositioning", 531, 3, null), new ExifTag("ReferenceBlackWhite", 532, 5, null), new ExifTag("Copyright", 33432, 2, null), new ExifTag("ExifIFDPointer", 34665, 4, null), new ExifTag("GPSInfoIFDPointer", 34853, 4, null) };
    IFD_EXIF_TAGS = new ExifTag[] { new ExifTag("ExposureTime", 33434, 5, null), new ExifTag("FNumber", 33437, 5, null), new ExifTag("ExposureProgram", 34850, 3, null), new ExifTag("SpectralSensitivity", 34852, 2, null), new ExifTag("ISOSpeedRatings", 34855, 3, null), new ExifTag("OECF", 34856, 7, null), new ExifTag("ExifVersion", 36864, 2, null), new ExifTag("DateTimeOriginal", 36867, 2, null), new ExifTag("DateTimeDigitized", 36868, 2, null), new ExifTag("ComponentsConfiguration", 37121, 7, null), new ExifTag("CompressedBitsPerPixel", 37122, 5, null), new ExifTag("ShutterSpeedValue", 37377, 10, null), new ExifTag("ApertureValue", 37378, 5, null), new ExifTag("BrightnessValue", 37379, 10, null), new ExifTag("ExposureBiasValue", 37380, 10, null), new ExifTag("MaxApertureValue", 37381, 5, null), new ExifTag("SubjectDistance", 37382, 5, null), new ExifTag("MeteringMode", 37383, 3, null), new ExifTag("LightSource", 37384, 3, null), new ExifTag("Flash", 37385, 3, null), new ExifTag("FocalLength", 37386, 5, null), new ExifTag("SubjectArea", 37396, 3, null), new ExifTag("MakerNote", 37500, 7, null), new ExifTag("UserComment", 37510, 7, null), new ExifTag("SubSecTime", 37520, 2, null), new ExifTag("SubSecTimeOriginal", 37521, 2, null), new ExifTag("SubSecTimeDigitized", 37522, 2, null), new ExifTag("FlashpixVersion", 40960, 7, null), new ExifTag("ColorSpace", 40961, 3, null), new ExifTag("PixelXDimension", 40962, 3, 4, null), new ExifTag("PixelYDimension", 40963, 3, 4, null), new ExifTag("RelatedSoundFile", 40964, 2, null), new ExifTag("InteroperabilityIFDPointer", 40965, 4, null), new ExifTag("FlashEnergy", 41483, 5, null), new ExifTag("SpatialFrequencyResponse", 41484, 7, null), new ExifTag("FocalPlaneXResolution", 41486, 5, null), new ExifTag("FocalPlaneYResolution", 41487, 5, null), new ExifTag("FocalPlaneResolutionUnit", 41488, 3, null), new ExifTag("SubjectLocation", 41492, 3, null), new ExifTag("ExposureIndex", 41493, 5, null), new ExifTag("SensingMethod", 41495, 3, null), new ExifTag("FileSource", 41728, 7, null), new ExifTag("SceneType", 41729, 7, null), new ExifTag("CFAPattern", 41730, 7, null), new ExifTag("CustomRendered", 41985, 3, null), new ExifTag("ExposureMode", 41986, 3, null), new ExifTag("WhiteBalance", 41987, 3, null), new ExifTag("DigitalZoomRatio", 41988, 5, null), new ExifTag("FocalLengthIn35mmFilm", 41989, 3, null), new ExifTag("SceneCaptureType", 41990, 3, null), new ExifTag("GainControl", 41991, 3, null), new ExifTag("Contrast", 41992, 3, null), new ExifTag("Saturation", 41993, 3, null), new ExifTag("Sharpness", 41994, 3, null), new ExifTag("DeviceSettingDescription", 41995, 7, null), new ExifTag("SubjectDistanceRange", 41996, 3, null), new ExifTag("ImageUniqueID", 42016, 2, null) };
    IFD_GPS_TAGS = new ExifTag[] { new ExifTag("GPSVersionID", 0, 1, null), new ExifTag("GPSLatitudeRef", 1, 2, null), new ExifTag("GPSLatitude", 2, 5, null), new ExifTag("GPSLongitudeRef", 3, 2, null), new ExifTag("GPSLongitude", 4, 5, null), new ExifTag("GPSAltitudeRef", 5, 1, null), new ExifTag("GPSAltitude", 6, 5, null), new ExifTag("GPSTimeStamp", 7, 5, null), new ExifTag("GPSSatellites", 8, 2, null), new ExifTag("GPSStatus", 9, 2, null), new ExifTag("GPSMeasureMode", 10, 2, null), new ExifTag("GPSDOP", 11, 5, null), new ExifTag("GPSSpeedRef", 12, 2, null), new ExifTag("GPSSpeed", 13, 5, null), new ExifTag("GPSTrackRef", 14, 2, null), new ExifTag("GPSTrack", 15, 5, null), new ExifTag("GPSImgDirectionRef", 16, 2, null), new ExifTag("GPSImgDirection", 17, 5, null), new ExifTag("GPSMapDatum", 18, 2, null), new ExifTag("GPSDestLatitudeRef", 19, 2, null), new ExifTag("GPSDestLatitude", 20, 5, null), new ExifTag("GPSDestLongitudeRef", 21, 2, null), new ExifTag("GPSDestLongitude", 22, 5, null), new ExifTag("GPSDestBearingRef", 23, 2, null), new ExifTag("GPSDestBearing", 24, 5, null), new ExifTag("GPSDestDistanceRef", 25, 2, null), new ExifTag("GPSDestDistance", 26, 5, null), new ExifTag("GPSProcessingMethod", 27, 7, null), new ExifTag("GPSAreaInformation", 28, 7, null), new ExifTag("GPSDateStamp", 29, 2, null), new ExifTag("GPSDifferential", 30, 3, null) };
    IFD_INTEROPERABILITY_TAGS = new ExifTag[] { new ExifTag("InteroperabilityIndex", 1, 2, null) };
    IFD_THUMBNAIL_TAGS = new ExifTag[] { new ExifTag("ThumbnailImageWidth", 256, 3, 4, null), new ExifTag("ThumbnailImageLength", 257, 3, 4, null), new ExifTag("BitsPerSample", 258, 3, null), new ExifTag("Compression", 259, 3, null), new ExifTag("PhotometricInterpretation", 262, 3, null), new ExifTag("ImageDescription", 270, 2, null), new ExifTag("Make", 271, 2, null), new ExifTag("Model", 272, 2, null), new ExifTag("StripOffsets", 3, 4, null), new ExifTag("Orientation", 274, 3, null), new ExifTag("SamplesPerPixel", 277, 3, null), new ExifTag("RowsPerStrip", 278, 3, 4, null), new ExifTag("StripByteCounts", 279, 3, 4, null), new ExifTag("XResolution", 282, 5, null), new ExifTag("YResolution", 283, 5, null), new ExifTag("PlanarConfiguration", 284, 3, null), new ExifTag("ResolutionUnit", 296, 3, null), new ExifTag("TransferFunction", 301, 3, null), new ExifTag("Software", 305, 2, null), new ExifTag("DateTime", 306, 2, null), new ExifTag("Artist", 315, 2, null), new ExifTag("WhitePoint", 318, 5, null), new ExifTag("PrimaryChromaticities", 319, 5, null), new ExifTag("JPEGInterchangeFormat", 513, 4, null), new ExifTag("JPEGInterchangeFormatLength", 514, 4, null), new ExifTag("YCbCrCoefficients", 529, 5, null), new ExifTag("YCbCrSubSampling", 530, 3, null), new ExifTag("YCbCrPositioning", 531, 3, null), new ExifTag("ReferenceBlackWhite", 532, 5, null), new ExifTag("Copyright", 33432, 2, null), new ExifTag("ExifIFDPointer", 34665, 4, null), new ExifTag("GPSInfoIFDPointer", 34853, 4, null) };
    EXIF_TAGS = new ExifTag[][] { IFD_TIFF_TAGS, IFD_EXIF_TAGS, IFD_GPS_TAGS, IFD_INTEROPERABILITY_TAGS, IFD_THUMBNAIL_TAGS };
    IFD_POINTER_TAGS = new ExifTag[] { new ExifTag("ExifIFDPointer", 34665, 4, null), new ExifTag("GPSInfoIFDPointer", 34853, 4, null), new ExifTag("InteroperabilityIFDPointer", 40965, 4, null) };
    IFD_POINTER_TAG_HINTS = new int[] { 1, 2, 3 };
    JPEG_INTERCHANGE_FORMAT_TAG = new ExifTag("JPEGInterchangeFormat", 513, 4, null);
    JPEG_INTERCHANGE_FORMAT_LENGTH_TAG = new ExifTag("JPEGInterchangeFormatLength", 514, 4, null);
    sExifTagMapsForReading = new HashMap[EXIF_TAGS.length];
    sExifTagMapsForWriting = new HashMap[EXIF_TAGS.length];
    sTagSetForCompatibility = new HashSet(Arrays.asList(new String[] { "FNumber", "DigitalZoomRatio", "ExposureTime", "SubjectDistance", "GPSTimeStamp" }));
    ASCII = Charset.forName("US-ASCII");
    IDENTIFIER_EXIF_APP1 = "Exif\000\000".getBytes(ASCII);
    System.loadLibrary("media_jni");
    nativeInitRaw();
    sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    sFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    int i = 0;
    while (i < EXIF_TAGS.length)
    {
      sExifTagMapsForReading[i] = new HashMap();
      sExifTagMapsForWriting[i] = new HashMap();
      ExifTag[] arrayOfExifTag = EXIF_TAGS[i];
      int j = 0;
      int k = arrayOfExifTag.length;
      while (j < k)
      {
        ExifTag localExifTag = arrayOfExifTag[j];
        sExifTagMapsForReading[i].put(Integer.valueOf(localExifTag.number), localExifTag);
        sExifTagMapsForWriting[i].put(localExifTag.name, localExifTag);
        j += 1;
      }
      i += 1;
    }
    sNonZeroTimePattern = Pattern.compile(".*[1-9].*");
  }
  
  /* Error */
  public ExifInterface(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 803	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: getstatic 689	android/media/ExifInterface:EXIF_TAGS	[[Landroid/media/ExifInterface$ExifTag;
    //   8: arraylength
    //   9: anewarray 699	java/util/HashMap
    //   12: putfield 805	android/media/ExifInterface:mAttributes	[Ljava/util/HashMap;
    //   15: aload_0
    //   16: getstatic 810	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   19: putfield 812	android/media/ExifInterface:mExifByteOrder	Ljava/nio/ByteOrder;
    //   22: aload_1
    //   23: ifnonnull +14 -> 37
    //   26: new 814	java/lang/IllegalArgumentException
    //   29: dup
    //   30: ldc_w 816
    //   33: invokespecial 817	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   36: athrow
    //   37: aload_0
    //   38: aconst_null
    //   39: putfield 819	android/media/ExifInterface:mAssetInputStream	Landroid/content/res/AssetManager$AssetInputStream;
    //   42: aload_0
    //   43: aconst_null
    //   44: putfield 821	android/media/ExifInterface:mFilename	Ljava/lang/String;
    //   47: aload_1
    //   48: invokestatic 825	android/media/ExifInterface:isSeekableFD	(Ljava/io/FileDescriptor;)Z
    //   51: ifeq +45 -> 96
    //   54: aload_0
    //   55: aload_1
    //   56: putfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   59: aload_1
    //   60: invokestatic 833	android/system/Os:dup	(Ljava/io/FileDescriptor;)Ljava/io/FileDescriptor;
    //   63: astore_1
    //   64: aload_0
    //   65: iconst_0
    //   66: putfield 835	android/media/ExifInterface:mIsInputStream	Z
    //   69: aconst_null
    //   70: astore_3
    //   71: new 837	java/io/FileInputStream
    //   74: dup
    //   75: aload_1
    //   76: invokespecial 839	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   79: astore_1
    //   80: aload_0
    //   81: aload_1
    //   82: invokespecial 843	android/media/ExifInterface:loadAttributes	(Ljava/io/InputStream;)V
    //   85: aload_1
    //   86: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   89: return
    //   90: astore_1
    //   91: aload_1
    //   92: invokevirtual 853	android/system/ErrnoException:rethrowAsIOException	()Ljava/io/IOException;
    //   95: athrow
    //   96: aload_0
    //   97: aconst_null
    //   98: putfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   101: goto -37 -> 64
    //   104: astore_2
    //   105: aload_3
    //   106: astore_1
    //   107: aload_1
    //   108: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   111: aload_2
    //   112: athrow
    //   113: astore_2
    //   114: goto -7 -> 107
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	117	0	this	ExifInterface
    //   0	117	1	paramFileDescriptor	FileDescriptor
    //   104	8	2	localObject1	Object
    //   113	1	2	localObject2	Object
    //   70	36	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   59	64	90	android/system/ErrnoException
    //   71	80	104	finally
    //   80	85	113	finally
  }
  
  public ExifInterface(InputStream paramInputStream)
    throws IOException
  {
    if (paramInputStream == null) {
      throw new IllegalArgumentException("inputStream cannot be null");
    }
    this.mFilename = null;
    if ((paramInputStream instanceof AssetManager.AssetInputStream))
    {
      this.mAssetInputStream = ((AssetManager.AssetInputStream)paramInputStream);
      this.mSeekableFileDescriptor = null;
    }
    for (;;)
    {
      this.mIsInputStream = true;
      loadAttributes(paramInputStream);
      return;
      if (((paramInputStream instanceof FileInputStream)) && (isSeekableFD(((FileInputStream)paramInputStream).getFD())))
      {
        this.mAssetInputStream = null;
        this.mSeekableFileDescriptor = ((FileInputStream)paramInputStream).getFD();
      }
      else
      {
        this.mAssetInputStream = null;
        this.mSeekableFileDescriptor = null;
      }
    }
  }
  
  public ExifInterface(String paramString)
    throws IOException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("filename cannot be null");
    }
    Object localObject1 = null;
    this.mAssetInputStream = null;
    this.mFilename = paramString;
    this.mIsInputStream = false;
    try
    {
      paramString = new FileInputStream(paramString);
      try
      {
        if (isSeekableFD(paramString.getFD())) {}
        for (this.mSeekableFileDescriptor = paramString.getFD();; this.mSeekableFileDescriptor = null)
        {
          loadAttributes(paramString);
          IoUtils.closeQuietly(paramString);
          return;
        }
        IoUtils.closeQuietly(paramString);
      }
      finally {}
    }
    finally
    {
      paramString = (String)localObject2;
      Object localObject3 = localObject4;
    }
    throw ((Throwable)localObject2);
  }
  
  private void addDefaultValuesForCompatibility()
  {
    String str = getAttribute("DateTimeOriginal");
    if (str != null) {
      this.mAttributes[0].put("DateTime", ExifAttribute.createString(str));
    }
    if (getAttribute("ImageWidth") == null) {
      this.mAttributes[0].put("ImageWidth", ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (getAttribute("ImageLength") == null) {
      this.mAttributes[0].put("ImageLength", ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (getAttribute("Orientation") == null) {
      this.mAttributes[0].put("Orientation", ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (getAttribute("LightSource") == null) {
      this.mAttributes[1].put("LightSource", ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
  }
  
  private static float convertRationalLatLonToFloat(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = paramString1.split(",");
      String[] arrayOfString = paramString1[0].split("/");
      double d1 = Double.parseDouble(arrayOfString[0].trim()) / Double.parseDouble(arrayOfString[1].trim());
      arrayOfString = paramString1[1].split("/");
      double d2 = Double.parseDouble(arrayOfString[0].trim()) / Double.parseDouble(arrayOfString[1].trim());
      paramString1 = paramString1[2].split("/");
      double d3 = Double.parseDouble(paramString1[0].trim()) / Double.parseDouble(paramString1[1].trim());
      d1 = d2 / 60.0D + d1 + d3 / 3600.0D;
      if (!paramString2.equals("S"))
      {
        boolean bool = paramString2.equals("W");
        if (!bool) {}
      }
      else
      {
        return (float)-d1;
      }
      return (float)d1;
    }
    catch (NumberFormatException|ArrayIndexOutOfBoundsException paramString1)
    {
      throw new IllegalArgumentException();
    }
  }
  
  private ExifAttribute getExifAttribute(String paramString)
  {
    int i = 0;
    while (i < EXIF_TAGS.length)
    {
      Object localObject = this.mAttributes[i].get(paramString);
      if (localObject != null) {
        return (ExifAttribute)localObject;
      }
      i += 1;
    }
    return null;
  }
  
  private static int getIfdHintFromTagNumber(int paramInt)
  {
    int i = 0;
    while (i < IFD_POINTER_TAG_HINTS.length)
    {
      if (IFD_POINTER_TAGS[i].number == paramInt) {
        return IFD_POINTER_TAG_HINTS[i];
      }
      i += 1;
    }
    return -1;
  }
  
  /* Error */
  private void getJpegAttributes(InputStream paramInputStream)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: new 924	java/io/DataInputStream
    //   6: dup
    //   7: aload_1
    //   8: invokespecial 926	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   11: astore 6
    //   13: aload 6
    //   15: invokevirtual 930	java/io/DataInputStream:readByte	()B
    //   18: istore_2
    //   19: iload_2
    //   20: iconst_m1
    //   21: if_icmpeq +79 -> 100
    //   24: new 800	java/io/IOException
    //   27: dup
    //   28: new 932	java/lang/StringBuilder
    //   31: dup
    //   32: invokespecial 933	java/lang/StringBuilder:<init>	()V
    //   35: ldc_w 935
    //   38: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: iload_2
    //   42: sipush 255
    //   45: iand
    //   46: invokestatic 943	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   49: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 946	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   58: athrow
    //   59: astore 7
    //   61: aload 6
    //   63: astore_1
    //   64: aload 7
    //   66: astore 6
    //   68: aload_1
    //   69: ifnull +28 -> 97
    //   72: getstatic 609	android/media/ExifInterface:m_Is_Raw_Input_Stream	Z
    //   75: ifeq +22 -> 97
    //   78: getstatic 573	android/media/ExifInterface:DEBUG_ONEPLUS	Z
    //   81: ifeq +12 -> 93
    //   84: ldc -122
    //   86: ldc_w 949
    //   89: invokestatic 955	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   92: pop
    //   93: aload_1
    //   94: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   97: aload 6
    //   99: athrow
    //   100: aload 6
    //   102: invokevirtual 930	java/io/DataInputStream:readByte	()B
    //   105: bipush -40
    //   107: if_icmpeq +524 -> 631
    //   110: new 800	java/io/IOException
    //   113: dup
    //   114: new 932	java/lang/StringBuilder
    //   117: dup
    //   118: invokespecial 933	java/lang/StringBuilder:<init>	()V
    //   121: ldc_w 935
    //   124: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: iload_2
    //   128: sipush 255
    //   131: iand
    //   132: invokestatic 943	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   135: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: invokevirtual 946	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   141: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   144: athrow
    //   145: aload 6
    //   147: invokevirtual 930	java/io/DataInputStream:readByte	()B
    //   150: istore_3
    //   151: iload_3
    //   152: iconst_m1
    //   153: if_icmpeq +38 -> 191
    //   156: new 800	java/io/IOException
    //   159: dup
    //   160: new 932	java/lang/StringBuilder
    //   163: dup
    //   164: invokespecial 933	java/lang/StringBuilder:<init>	()V
    //   167: ldc_w 957
    //   170: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: iload_3
    //   174: sipush 255
    //   177: iand
    //   178: invokestatic 943	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   181: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: invokevirtual 946	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   187: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   190: athrow
    //   191: aload 6
    //   193: invokevirtual 930	java/io/DataInputStream:readByte	()B
    //   196: istore_3
    //   197: iload_3
    //   198: bipush -39
    //   200: if_icmpeq +9 -> 209
    //   203: iload_3
    //   204: bipush -38
    //   206: if_icmpne +35 -> 241
    //   209: aload 6
    //   211: ifnull +29 -> 240
    //   214: getstatic 609	android/media/ExifInterface:m_Is_Raw_Input_Stream	Z
    //   217: ifeq +23 -> 240
    //   220: getstatic 573	android/media/ExifInterface:DEBUG_ONEPLUS	Z
    //   223: ifeq +12 -> 235
    //   226: ldc -122
    //   228: ldc_w 949
    //   231: invokestatic 955	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   234: pop
    //   235: aload 6
    //   237: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   240: return
    //   241: aload 6
    //   243: invokevirtual 961	java/io/DataInputStream:readUnsignedShort	()I
    //   246: iconst_2
    //   247: isub
    //   248: istore 4
    //   250: iload_2
    //   251: iconst_1
    //   252: iadd
    //   253: iconst_1
    //   254: iadd
    //   255: iconst_2
    //   256: iadd
    //   257: istore 5
    //   259: iload 4
    //   261: ifge +377 -> 638
    //   264: new 800	java/io/IOException
    //   267: dup
    //   268: ldc_w 963
    //   271: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   274: athrow
    //   275: iload_2
    //   276: ifge +319 -> 595
    //   279: new 800	java/io/IOException
    //   282: dup
    //   283: ldc_w 963
    //   286: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   289: athrow
    //   290: iload 5
    //   292: istore_3
    //   293: iload 4
    //   295: istore_2
    //   296: iload 4
    //   298: bipush 6
    //   300: if_icmplt -25 -> 275
    //   303: bipush 6
    //   305: newarray <illegal type>
    //   307: astore 7
    //   309: aload_1
    //   310: aload 7
    //   312: invokevirtual 969	java/io/InputStream:read	([B)I
    //   315: bipush 6
    //   317: if_icmpeq +14 -> 331
    //   320: new 800	java/io/IOException
    //   323: dup
    //   324: ldc_w 971
    //   327: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   330: athrow
    //   331: iload 5
    //   333: bipush 6
    //   335: iadd
    //   336: istore 5
    //   338: iload 4
    //   340: bipush 6
    //   342: isub
    //   343: istore 4
    //   345: iload 5
    //   347: istore_3
    //   348: iload 4
    //   350: istore_2
    //   351: aload 7
    //   353: getstatic 732	android/media/ExifInterface:IDENTIFIER_EXIF_APP1	[B
    //   356: invokestatic 974	java/util/Arrays:equals	([B[B)Z
    //   359: ifeq -84 -> 275
    //   362: iload 4
    //   364: ifgt +14 -> 378
    //   367: new 800	java/io/IOException
    //   370: dup
    //   371: ldc_w 971
    //   374: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   377: athrow
    //   378: iload 4
    //   380: newarray <illegal type>
    //   382: astore 7
    //   384: aload 6
    //   386: aload 7
    //   388: invokevirtual 975	java/io/DataInputStream:read	([B)I
    //   391: iload 4
    //   393: if_icmpeq +14 -> 407
    //   396: new 800	java/io/IOException
    //   399: dup
    //   400: ldc_w 971
    //   403: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   406: athrow
    //   407: aload_0
    //   408: aload 7
    //   410: iload 5
    //   412: invokespecial 979	android/media/ExifInterface:readExifSegment	([BI)V
    //   415: iload 5
    //   417: iload 4
    //   419: iadd
    //   420: istore_3
    //   421: iconst_0
    //   422: istore_2
    //   423: goto -148 -> 275
    //   426: iload 4
    //   428: newarray <illegal type>
    //   430: astore 7
    //   432: aload 6
    //   434: aload 7
    //   436: invokevirtual 975	java/io/DataInputStream:read	([B)I
    //   439: iload 4
    //   441: if_icmpeq +14 -> 455
    //   444: new 800	java/io/IOException
    //   447: dup
    //   448: ldc_w 971
    //   451: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   454: athrow
    //   455: iconst_0
    //   456: istore 4
    //   458: iload 5
    //   460: istore_3
    //   461: iload 4
    //   463: istore_2
    //   464: aload_0
    //   465: ldc_w 499
    //   468: invokevirtual 870	android/media/ExifInterface:getAttribute	(Ljava/lang/String;)Ljava/lang/String;
    //   471: ifnonnull -196 -> 275
    //   474: aload_0
    //   475: getfield 805	android/media/ExifInterface:mAttributes	[Ljava/util/HashMap;
    //   478: iconst_1
    //   479: aaload
    //   480: ldc_w 499
    //   483: new 577	java/lang/String
    //   486: dup
    //   487: aload 7
    //   489: getstatic 553	android/media/ExifInterface:ASCII	Ljava/nio/charset/Charset;
    //   492: invokespecial 982	java/lang/String:<init>	([BLjava/nio/charset/Charset;)V
    //   495: invokestatic 874	android/media/ExifInterface$ExifAttribute:createString	(Ljava/lang/String;)Landroid/media/ExifInterface$ExifAttribute;
    //   498: invokevirtual 780	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   501: pop
    //   502: iload 5
    //   504: istore_3
    //   505: iload 4
    //   507: istore_2
    //   508: goto -233 -> 275
    //   511: aload 6
    //   513: iconst_1
    //   514: invokevirtual 985	java/io/DataInputStream:skipBytes	(I)I
    //   517: iconst_1
    //   518: if_icmpeq +14 -> 532
    //   521: new 800	java/io/IOException
    //   524: dup
    //   525: ldc_w 987
    //   528: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   531: athrow
    //   532: aload_0
    //   533: getfield 805	android/media/ExifInterface:mAttributes	[Ljava/util/HashMap;
    //   536: iconst_0
    //   537: aaload
    //   538: ldc_w 346
    //   541: aload 6
    //   543: invokevirtual 961	java/io/DataInputStream:readUnsignedShort	()I
    //   546: i2l
    //   547: aload_0
    //   548: getfield 812	android/media/ExifInterface:mExifByteOrder	Ljava/nio/ByteOrder;
    //   551: invokestatic 878	android/media/ExifInterface$ExifAttribute:createULong	(JLjava/nio/ByteOrder;)Landroid/media/ExifInterface$ExifAttribute;
    //   554: invokevirtual 780	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   557: pop
    //   558: aload_0
    //   559: getfield 805	android/media/ExifInterface:mAttributes	[Ljava/util/HashMap;
    //   562: iconst_0
    //   563: aaload
    //   564: ldc_w 352
    //   567: aload 6
    //   569: invokevirtual 961	java/io/DataInputStream:readUnsignedShort	()I
    //   572: i2l
    //   573: aload_0
    //   574: getfield 812	android/media/ExifInterface:mExifByteOrder	Ljava/nio/ByteOrder;
    //   577: invokestatic 878	android/media/ExifInterface$ExifAttribute:createULong	(JLjava/nio/ByteOrder;)Landroid/media/ExifInterface$ExifAttribute;
    //   580: invokevirtual 780	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   583: pop
    //   584: iload 4
    //   586: iconst_5
    //   587: isub
    //   588: istore_2
    //   589: iload 5
    //   591: istore_3
    //   592: goto -317 -> 275
    //   595: aload 6
    //   597: iload_2
    //   598: invokevirtual 985	java/io/DataInputStream:skipBytes	(I)I
    //   601: iload_2
    //   602: if_icmpeq +14 -> 616
    //   605: new 800	java/io/IOException
    //   608: dup
    //   609: ldc_w 989
    //   612: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   615: athrow
    //   616: iload_3
    //   617: iload_2
    //   618: iadd
    //   619: istore_2
    //   620: goto -475 -> 145
    //   623: astore 6
    //   625: aload 7
    //   627: astore_1
    //   628: goto -560 -> 68
    //   631: iconst_1
    //   632: iconst_1
    //   633: iadd
    //   634: istore_2
    //   635: goto -490 -> 145
    //   638: iload_3
    //   639: lookupswitch	default:+129->768, -64:+-128->511, -63:+-128->511, -62:+-128->511, -61:+-128->511, -59:+-128->511, -58:+-128->511, -57:+-128->511, -55:+-128->511, -54:+-128->511, -53:+-128->511, -51:+-128->511, -50:+-128->511, -49:+-128->511, -31:+-349->290, -2:+-213->426
    //   768: iload 5
    //   770: istore_3
    //   771: iload 4
    //   773: istore_2
    //   774: goto -499 -> 275
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	777	0	this	ExifInterface
    //   0	777	1	paramInputStream	InputStream
    //   18	756	2	i	int
    //   150	621	3	j	int
    //   248	524	4	k	int
    //   257	512	5	m	int
    //   11	585	6	localObject1	Object
    //   623	1	6	localObject2	Object
    //   1	1	7	localObject3	Object
    //   59	6	7	localObject4	Object
    //   307	319	7	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   13	19	59	finally
    //   24	59	59	finally
    //   100	145	59	finally
    //   145	151	59	finally
    //   156	191	59	finally
    //   191	197	59	finally
    //   241	250	59	finally
    //   264	275	59	finally
    //   279	290	59	finally
    //   303	331	59	finally
    //   351	362	59	finally
    //   367	378	59	finally
    //   378	407	59	finally
    //   407	415	59	finally
    //   426	455	59	finally
    //   464	502	59	finally
    //   511	532	59	finally
    //   532	584	59	finally
    //   595	616	59	finally
    //   3	13	623	finally
  }
  
  private static Pair<Integer, Integer> guessDataFormat(String paramString)
  {
    Object localObject;
    if (paramString.contains(","))
    {
      localObject = paramString.split(",");
      paramString = guessDataFormat(localObject[0]);
      if (((Integer)paramString.first).intValue() == 2) {
        return paramString;
      }
      int i = 1;
      if (i < localObject.length)
      {
        Pair localPair = guessDataFormat(localObject[i]);
        int j = -1;
        int m = -1;
        if ((localPair.first == paramString.first) || (localPair.second == paramString.first)) {
          j = ((Integer)paramString.first).intValue();
        }
        int k = m;
        if (((Integer)paramString.second).intValue() != -1) {
          if (localPair.first != paramString.second)
          {
            k = m;
            if (localPair.second != paramString.second) {}
          }
          else
          {
            k = ((Integer)paramString.second).intValue();
          }
        }
        if ((j == -1) && (k == -1)) {
          return new Pair(Integer.valueOf(2), Integer.valueOf(-1));
        }
        if (j == -1) {
          paramString = new Pair(Integer.valueOf(k), Integer.valueOf(-1));
        }
        for (;;)
        {
          i += 1;
          break;
          if (k == -1) {
            paramString = new Pair(Integer.valueOf(j), Integer.valueOf(-1));
          }
        }
      }
      return paramString;
    }
    if (paramString.contains("/"))
    {
      paramString = paramString.split("/");
      if (paramString.length != 2) {}
    }
    for (;;)
    {
      long l1;
      long l2;
      try
      {
        l1 = Long.parseLong(paramString[0]);
        l2 = Long.parseLong(paramString[1]);
        if ((l1 >= 0L) && (l2 >= 0L)) {
          break label496;
        }
        return new Pair(Integer.valueOf(10), Integer.valueOf(-1));
      }
      catch (NumberFormatException paramString) {}
      return new Pair(Integer.valueOf(5), Integer.valueOf(-1));
      paramString = new Pair(Integer.valueOf(10), Integer.valueOf(5));
      return paramString;
      return new Pair(Integer.valueOf(2), Integer.valueOf(-1));
      try
      {
        localObject = Long.valueOf(Long.parseLong(paramString));
        if ((((Long)localObject).longValue() >= 0L) && (((Long)localObject).longValue() <= 65535L)) {
          return new Pair(Integer.valueOf(3), Integer.valueOf(4));
        }
        if (((Long)localObject).longValue() < 0L) {
          return new Pair(Integer.valueOf(9), Integer.valueOf(-1));
        }
        localObject = new Pair(Integer.valueOf(4), Integer.valueOf(-1));
        return (Pair<Integer, Integer>)localObject;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        try
        {
          Double.parseDouble(paramString);
          paramString = new Pair(Integer.valueOf(12), Integer.valueOf(-1));
          return paramString;
        }
        catch (NumberFormatException paramString)
        {
          return new Pair(Integer.valueOf(2), Integer.valueOf(-1));
        }
      }
      label496:
      if (l1 <= 2147483647L) {
        if (l2 <= 2147483647L) {}
      }
    }
  }
  
  private boolean handleRawResult(HashMap paramHashMap)
  {
    boolean bool = false;
    if (paramHashMap == null) {
      return false;
    }
    this.mIsRaw = true;
    Object localObject = (String)paramHashMap.remove("HasThumbnail");
    if (localObject != null) {
      bool = ((String)localObject).equalsIgnoreCase("true");
    }
    this.mHasThumbnail = bool;
    localObject = (String)paramHashMap.remove("ThumbnailOffset");
    if (localObject != null) {
      this.mThumbnailOffset = Integer.parseInt((String)localObject);
    }
    localObject = (String)paramHashMap.remove("ThumbnailLength");
    if (localObject != null) {
      this.mThumbnailLength = Integer.parseInt((String)localObject);
    }
    this.mThumbnailBytes = ((byte[])paramHashMap.remove("ThumbnailData"));
    paramHashMap = paramHashMap.entrySet().iterator();
    while (paramHashMap.hasNext())
    {
      localObject = (Map.Entry)paramHashMap.next();
      setAttribute((String)((Map.Entry)localObject).getKey(), (String)((Map.Entry)localObject).getValue());
    }
    return true;
  }
  
  private static boolean isJpegInputStream(BufferedInputStream paramBufferedInputStream)
    throws IOException
  {
    paramBufferedInputStream.mark(3);
    byte[] arrayOfByte = new byte[3];
    if (paramBufferedInputStream.read(arrayOfByte) != 3) {
      throw new EOFException();
    }
    boolean bool = Arrays.equals(JPEG_SIGNATURE, arrayOfByte);
    paramBufferedInputStream.reset();
    return bool;
  }
  
  private static boolean isSeekableFD(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    try
    {
      Os.lseek(paramFileDescriptor, 0L, OsConstants.SEEK_CUR);
      return true;
    }
    catch (ErrnoException paramFileDescriptor) {}
    return false;
  }
  
  /* Error */
  private void loadAttributes(InputStream paramInputStream)
    throws IOException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: iload_2
    //   3: getstatic 689	android/media/ExifInterface:EXIF_TAGS	[[Landroid/media/ExifInterface$ExifTag;
    //   6: arraylength
    //   7: if_icmpge +23 -> 30
    //   10: aload_0
    //   11: getfield 805	android/media/ExifInterface:mAttributes	[Ljava/util/HashMap;
    //   14: iload_2
    //   15: new 699	java/util/HashMap
    //   18: dup
    //   19: invokespecial 767	java/util/HashMap:<init>	()V
    //   22: aastore
    //   23: iload_2
    //   24: iconst_1
    //   25: iadd
    //   26: istore_2
    //   27: goto -25 -> 2
    //   30: iconst_1
    //   31: putstatic 609	android/media/ExifInterface:m_Is_Raw_Input_Stream	Z
    //   34: aload_0
    //   35: getfield 819	android/media/ExifInterface:mAssetInputStream	Landroid/content/res/AssetManager$AssetInputStream;
    //   38: ifnull +27 -> 65
    //   41: aload_0
    //   42: aload_0
    //   43: getfield 819	android/media/ExifInterface:mAssetInputStream	Landroid/content/res/AssetManager$AssetInputStream;
    //   46: invokevirtual 1116	android/content/res/AssetManager$AssetInputStream:getNativeAsset	()J
    //   49: invokestatic 1120	android/media/ExifInterface:nativeGetRawAttributesFromAsset	(J)Ljava/util/HashMap;
    //   52: invokespecial 1122	android/media/ExifInterface:handleRawResult	(Ljava/util/HashMap;)Z
    //   55: istore_3
    //   56: iload_3
    //   57: ifeq +74 -> 131
    //   60: aload_0
    //   61: invokespecial 1124	android/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   64: return
    //   65: aload_0
    //   66: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   69: ifnull +24 -> 93
    //   72: aload_0
    //   73: aload_0
    //   74: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   77: invokestatic 1128	android/media/ExifInterface:nativeGetRawAttributesFromFileDescriptor	(Ljava/io/FileDescriptor;)Ljava/util/HashMap;
    //   80: invokespecial 1122	android/media/ExifInterface:handleRawResult	(Ljava/util/HashMap;)Z
    //   83: istore_3
    //   84: iload_3
    //   85: ifeq +46 -> 131
    //   88: aload_0
    //   89: invokespecial 1124	android/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   92: return
    //   93: new 1093	java/io/BufferedInputStream
    //   96: dup
    //   97: aload_1
    //   98: iconst_3
    //   99: invokespecial 1131	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;I)V
    //   102: astore_1
    //   103: aload_1
    //   104: checkcast 1093	java/io/BufferedInputStream
    //   107: invokestatic 1133	android/media/ExifInterface:isJpegInputStream	(Ljava/io/BufferedInputStream;)Z
    //   110: ifne +21 -> 131
    //   113: aload_0
    //   114: aload_1
    //   115: invokestatic 1137	android/media/ExifInterface:nativeGetRawAttributesFromInputStream	(Ljava/io/InputStream;)Ljava/util/HashMap;
    //   118: invokespecial 1122	android/media/ExifInterface:handleRawResult	(Ljava/util/HashMap;)Z
    //   121: istore_3
    //   122: iload_3
    //   123: ifeq +63 -> 186
    //   126: aload_0
    //   127: invokespecial 1124	android/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   130: return
    //   131: iconst_0
    //   132: putstatic 609	android/media/ExifInterface:m_Is_Raw_Input_Stream	Z
    //   135: aload_0
    //   136: aload_1
    //   137: invokespecial 1139	android/media/ExifInterface:getJpegAttributes	(Ljava/io/InputStream;)V
    //   140: aload_0
    //   141: iconst_1
    //   142: putfield 1141	android/media/ExifInterface:mIsSupportedFile	Z
    //   145: aload_0
    //   146: invokespecial 1124	android/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   149: return
    //   150: astore_1
    //   151: aload_0
    //   152: iconst_0
    //   153: putfield 1141	android/media/ExifInterface:mIsSupportedFile	Z
    //   156: ldc -122
    //   158: ldc_w 1143
    //   161: aload_1
    //   162: invokestatic 1146	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   165: pop
    //   166: aload_0
    //   167: invokespecial 1124	android/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   170: return
    //   171: astore_1
    //   172: aload_0
    //   173: invokespecial 1124	android/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   176: aload_1
    //   177: athrow
    //   178: astore_1
    //   179: goto -7 -> 172
    //   182: astore_1
    //   183: goto -32 -> 151
    //   186: goto -55 -> 131
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	189	0	this	ExifInterface
    //   0	189	1	paramInputStream	InputStream
    //   1	26	2	i	int
    //   55	68	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	23	150	java/io/IOException
    //   30	56	150	java/io/IOException
    //   65	84	150	java/io/IOException
    //   93	103	150	java/io/IOException
    //   131	145	150	java/io/IOException
    //   2	23	171	finally
    //   30	56	171	finally
    //   65	84	171	finally
    //   93	103	171	finally
    //   131	145	171	finally
    //   151	166	171	finally
    //   103	122	178	finally
    //   103	122	182	java/io/IOException
  }
  
  private static native HashMap nativeGetRawAttributesFromAsset(long paramLong);
  
  private static native HashMap nativeGetRawAttributesFromFileDescriptor(FileDescriptor paramFileDescriptor);
  
  private static native HashMap nativeGetRawAttributesFromInputStream(InputStream paramInputStream);
  
  private static native byte[] nativeGetThumbnailFromAsset(long paramLong, int paramInt1, int paramInt2);
  
  private static native void nativeInitRaw();
  
  private void printAttributes()
  {
    int i = 0;
    while (i < this.mAttributes.length)
    {
      Log.d("ExifInterface", "The size of tag group[" + i + "]: " + this.mAttributes[i].size());
      Iterator localIterator = this.mAttributes[i].entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        ExifAttribute localExifAttribute = (ExifAttribute)localEntry.getValue();
        Log.d("ExifInterface", "tagName: " + localEntry.getKey() + ", tagType: " + localExifAttribute.toString() + ", tagValue: '" + localExifAttribute.getStringValue(this.mExifByteOrder) + "'");
      }
      i += 1;
    }
  }
  
  private void readExifSegment(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    ByteOrderAwarenessDataInputStream localByteOrderAwarenessDataInputStream = new ByteOrderAwarenessDataInputStream(paramArrayOfByte);
    int i = localByteOrderAwarenessDataInputStream.readShort();
    switch (i)
    {
    default: 
      throw new IOException("Invalid byte order: " + Integer.toHexString(i));
    }
    for (this.mExifByteOrder = ByteOrder.LITTLE_ENDIAN;; this.mExifByteOrder = ByteOrder.BIG_ENDIAN)
    {
      localByteOrderAwarenessDataInputStream.setByteOrder(this.mExifByteOrder);
      i = localByteOrderAwarenessDataInputStream.readUnsignedShort();
      if (i == 42) {
        break;
      }
      throw new IOException("Invalid exif start: " + Integer.toHexString(i));
    }
    long l = localByteOrderAwarenessDataInputStream.readUnsignedInt();
    if ((l < 8L) || (l >= paramArrayOfByte.length)) {
      throw new IOException("Invalid first Ifd offset: " + l);
    }
    l -= 8L;
    if ((l > 0L) && (localByteOrderAwarenessDataInputStream.skip(l) != l)) {
      throw new IOException("Couldn't jump to first Ifd: " + l);
    }
    readImageFileDirectory(localByteOrderAwarenessDataInputStream, 0);
    String str1 = getAttribute(JPEG_INTERCHANGE_FORMAT_TAG.name);
    String str2 = getAttribute(JPEG_INTERCHANGE_FORMAT_LENGTH_TAG.name);
    if ((str1 != null) && (str2 != null)) {}
    try
    {
      i = Integer.parseInt(str1);
      int j = Math.min(i + Integer.parseInt(str2), paramArrayOfByte.length) - i;
      if ((i > 0) && (j > 0))
      {
        this.mHasThumbnail = true;
        this.mThumbnailOffset = (paramInt + i);
        this.mThumbnailLength = j;
        if ((this.mFilename == null) && (this.mAssetInputStream == null) && (this.mSeekableFileDescriptor == null))
        {
          paramArrayOfByte = new byte[j];
          localByteOrderAwarenessDataInputStream.seek(i);
          localByteOrderAwarenessDataInputStream.readFully(paramArrayOfByte);
          this.mThumbnailBytes = paramArrayOfByte;
        }
      }
      return;
    }
    catch (NumberFormatException paramArrayOfByte) {}
  }
  
  private void readImageFileDirectory(ByteOrderAwarenessDataInputStream paramByteOrderAwarenessDataInputStream, int paramInt)
    throws IOException
  {
    if (paramByteOrderAwarenessDataInputStream.peek() + 2L > ByteOrderAwarenessDataInputStream.-get0(paramByteOrderAwarenessDataInputStream)) {
      return;
    }
    int k = paramByteOrderAwarenessDataInputStream.readShort();
    if (paramByteOrderAwarenessDataInputStream.peek() + k * 12 > ByteOrderAwarenessDataInputStream.-get0(paramByteOrderAwarenessDataInputStream)) {
      return;
    }
    int i = 0;
    long l1;
    if (i < k)
    {
      int i1 = paramByteOrderAwarenessDataInputStream.readUnsignedShort();
      int m = paramByteOrderAwarenessDataInputStream.readUnsignedShort();
      int n = paramByteOrderAwarenessDataInputStream.readInt();
      long l3 = paramByteOrderAwarenessDataInputStream.peek() + 4L;
      ExifTag localExifTag = (ExifTag)sExifTagMapsForReading[paramInt].get(Integer.valueOf(i1));
      l1 = 0L;
      int j = 0;
      if (localExifTag == null)
      {
        Log.w("ExifInterface", "Skip the tag entry since tag number is not defined: " + i1);
        label135:
        if (j != 0) {
          break label263;
        }
        paramByteOrderAwarenessDataInputStream.seek(l3);
      }
      for (;;)
      {
        i = (short)(i + 1);
        break;
        if ((m <= 0) || (m >= IFD_FORMAT_BYTES_PER_FORMAT.length))
        {
          Log.w("ExifInterface", "Skip the tag entry since data format is invalid: " + m);
          break label135;
        }
        l1 = n * IFD_FORMAT_BYTES_PER_FORMAT[m];
        if ((l1 < 0L) || (l1 > 2147483647L))
        {
          Log.w("ExifInterface", "Skip the tag entry since number of components is invalid: " + n);
          break label135;
        }
        j = 1;
        break label135;
        label263:
        long l2;
        if (l1 > 4L)
        {
          l2 = paramByteOrderAwarenessDataInputStream.readUnsignedInt();
          if (l2 + l1 <= ByteOrderAwarenessDataInputStream.-get0(paramByteOrderAwarenessDataInputStream)) {
            paramByteOrderAwarenessDataInputStream.seek(l2);
          }
        }
        else
        {
          j = getIfdHintFromTagNumber(i1);
          if (j < 0) {
            break label512;
          }
          l2 = -1L;
          l1 = l2;
          switch (m)
          {
          default: 
            l1 = l2;
          case 5: 
          case 6: 
          case 7: 
            label368:
            if ((l1 > 0L) && (l1 < ByteOrderAwarenessDataInputStream.-get0(paramByteOrderAwarenessDataInputStream)))
            {
              paramByteOrderAwarenessDataInputStream.seek(l1);
              readImageFileDirectory(paramByteOrderAwarenessDataInputStream, j);
            }
            break;
          }
        }
        for (;;)
        {
          paramByteOrderAwarenessDataInputStream.seek(l3);
          break;
          Log.w("ExifInterface", "Skip the tag entry since data offset is invalid: " + l2);
          paramByteOrderAwarenessDataInputStream.seek(l3);
          break;
          l1 = paramByteOrderAwarenessDataInputStream.readUnsignedShort();
          break label368;
          l1 = paramByteOrderAwarenessDataInputStream.readShort();
          break label368;
          l1 = paramByteOrderAwarenessDataInputStream.readUnsignedInt();
          break label368;
          l1 = paramByteOrderAwarenessDataInputStream.readInt();
          break label368;
          Log.w("ExifInterface", "Skip jump into the IFD since its offset is invalid: " + l1);
        }
        label512:
        byte[] arrayOfByte = new byte[IFD_FORMAT_BYTES_PER_FORMAT[m] * n];
        paramByteOrderAwarenessDataInputStream.readFully(arrayOfByte);
        this.mAttributes[paramInt].put(localExifTag.name, new ExifAttribute(m, n, arrayOfByte, null));
        if (paramByteOrderAwarenessDataInputStream.peek() != l3) {
          paramByteOrderAwarenessDataInputStream.seek(l3);
        }
      }
    }
    if (paramByteOrderAwarenessDataInputStream.peek() + 4L <= ByteOrderAwarenessDataInputStream.-get0(paramByteOrderAwarenessDataInputStream))
    {
      l1 = paramByteOrderAwarenessDataInputStream.readUnsignedInt();
      if ((l1 > 8L) && (l1 < ByteOrderAwarenessDataInputStream.-get0(paramByteOrderAwarenessDataInputStream)))
      {
        paramByteOrderAwarenessDataInputStream.seek(l1);
        readImageFileDirectory(paramByteOrderAwarenessDataInputStream, 4);
      }
    }
  }
  
  private void removeAttribute(String paramString)
  {
    int i = 0;
    while (i < EXIF_TAGS.length)
    {
      this.mAttributes[i].remove(paramString);
      i += 1;
    }
  }
  
  /* Error */
  private void saveJpegAttributes(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 5
    //   6: new 924	java/io/DataInputStream
    //   9: dup
    //   10: aload_1
    //   11: invokespecial 926	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   14: astore_1
    //   15: new 9	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream
    //   18: dup
    //   19: aload_2
    //   20: getstatic 810	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   23: invokespecial 1266	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:<init>	(Ljava/io/OutputStream;Ljava/nio/ByteOrder;)V
    //   26: astore_2
    //   27: aload_1
    //   28: invokevirtual 930	java/io/DataInputStream:readByte	()B
    //   31: iconst_m1
    //   32: if_icmpeq +72 -> 104
    //   35: new 800	java/io/IOException
    //   38: dup
    //   39: ldc_w 1268
    //   42: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   45: athrow
    //   46: astore 6
    //   48: aload_2
    //   49: astore 5
    //   51: aload 6
    //   53: astore_2
    //   54: aload_1
    //   55: ifnull +22 -> 77
    //   58: getstatic 573	android/media/ExifInterface:DEBUG_ONEPLUS	Z
    //   61: ifeq +12 -> 73
    //   64: ldc -122
    //   66: ldc_w 1270
    //   69: invokestatic 955	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   72: pop
    //   73: aload_1
    //   74: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   77: aload 5
    //   79: ifnull +23 -> 102
    //   82: getstatic 573	android/media/ExifInterface:DEBUG_ONEPLUS	Z
    //   85: ifeq +12 -> 97
    //   88: ldc -122
    //   90: ldc_w 1272
    //   93: invokestatic 955	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   96: pop
    //   97: aload 5
    //   99: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   102: aload_2
    //   103: athrow
    //   104: aload_2
    //   105: iconst_m1
    //   106: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   109: aload_1
    //   110: invokevirtual 930	java/io/DataInputStream:readByte	()B
    //   113: bipush -40
    //   115: if_icmpeq +14 -> 129
    //   118: new 800	java/io/IOException
    //   121: dup
    //   122: ldc_w 1268
    //   125: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   128: athrow
    //   129: aload_2
    //   130: bipush -40
    //   132: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   135: aload_2
    //   136: iconst_m1
    //   137: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   140: aload_2
    //   141: bipush -31
    //   143: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   146: aload_0
    //   147: aload_2
    //   148: bipush 6
    //   150: invokespecial 1279	android/media/ExifInterface:writeExifSegment	(Landroid/media/ExifInterface$ByteOrderAwarenessDataOutputStream;I)I
    //   153: pop
    //   154: sipush 4096
    //   157: newarray <illegal type>
    //   159: astore 5
    //   161: aload_1
    //   162: invokevirtual 930	java/io/DataInputStream:readByte	()B
    //   165: iconst_m1
    //   166: if_icmpeq +14 -> 180
    //   169: new 800	java/io/IOException
    //   172: dup
    //   173: ldc_w 1268
    //   176: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   179: athrow
    //   180: aload_1
    //   181: invokevirtual 930	java/io/DataInputStream:readByte	()B
    //   184: istore_3
    //   185: iload_3
    //   186: lookupswitch	default:+377->563, -39:+261->447, -38:+261->447, -31:+78->264
    //   220: aload_2
    //   221: iconst_m1
    //   222: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   225: aload_2
    //   226: iload_3
    //   227: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   230: aload_1
    //   231: invokevirtual 961	java/io/DataInputStream:readUnsignedShort	()I
    //   234: istore_3
    //   235: aload_2
    //   236: iload_3
    //   237: invokevirtual 1282	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeUnsignedShort	(I)V
    //   240: iload_3
    //   241: iconst_2
    //   242: isub
    //   243: istore 4
    //   245: iload 4
    //   247: istore_3
    //   248: iload 4
    //   250: ifge +260 -> 510
    //   253: new 800	java/io/IOException
    //   256: dup
    //   257: ldc_w 963
    //   260: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   263: athrow
    //   264: aload_1
    //   265: invokevirtual 961	java/io/DataInputStream:readUnsignedShort	()I
    //   268: iconst_2
    //   269: isub
    //   270: istore 4
    //   272: iload 4
    //   274: ifge +14 -> 288
    //   277: new 800	java/io/IOException
    //   280: dup
    //   281: ldc_w 963
    //   284: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   287: athrow
    //   288: bipush 6
    //   290: newarray <illegal type>
    //   292: astore 6
    //   294: iload 4
    //   296: bipush 6
    //   298: if_icmplt +67 -> 365
    //   301: aload_1
    //   302: aload 6
    //   304: invokevirtual 975	java/io/DataInputStream:read	([B)I
    //   307: bipush 6
    //   309: if_icmpeq +14 -> 323
    //   312: new 800	java/io/IOException
    //   315: dup
    //   316: ldc_w 971
    //   319: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   322: athrow
    //   323: aload 6
    //   325: getstatic 732	android/media/ExifInterface:IDENTIFIER_EXIF_APP1	[B
    //   328: invokestatic 974	java/util/Arrays:equals	([B[B)Z
    //   331: ifeq +34 -> 365
    //   334: aload_1
    //   335: iload 4
    //   337: bipush 6
    //   339: isub
    //   340: i2l
    //   341: invokevirtual 1283	java/io/DataInputStream:skip	(J)J
    //   344: iload 4
    //   346: bipush 6
    //   348: isub
    //   349: i2l
    //   350: lcmp
    //   351: ifeq -190 -> 161
    //   354: new 800	java/io/IOException
    //   357: dup
    //   358: ldc_w 963
    //   361: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   364: athrow
    //   365: aload_2
    //   366: iconst_m1
    //   367: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   370: aload_2
    //   371: iload_3
    //   372: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   375: aload_2
    //   376: iload 4
    //   378: iconst_2
    //   379: iadd
    //   380: invokevirtual 1282	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeUnsignedShort	(I)V
    //   383: iload 4
    //   385: istore_3
    //   386: iload 4
    //   388: bipush 6
    //   390: if_icmplt +15 -> 405
    //   393: iload 4
    //   395: bipush 6
    //   397: isub
    //   398: istore_3
    //   399: aload_2
    //   400: aload 6
    //   402: invokevirtual 1286	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:write	([B)V
    //   405: iload_3
    //   406: ifle -245 -> 161
    //   409: aload_1
    //   410: aload 5
    //   412: iconst_0
    //   413: iload_3
    //   414: aload 5
    //   416: arraylength
    //   417: invokestatic 1223	java/lang/Math:min	(II)I
    //   420: invokevirtual 1289	java/io/DataInputStream:read	([BII)I
    //   423: istore 4
    //   425: iload 4
    //   427: iflt -266 -> 161
    //   430: aload_2
    //   431: aload 5
    //   433: iconst_0
    //   434: iload 4
    //   436: invokevirtual 1292	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:write	([BII)V
    //   439: iload_3
    //   440: iload 4
    //   442: isub
    //   443: istore_3
    //   444: goto -39 -> 405
    //   447: aload_2
    //   448: iconst_m1
    //   449: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   452: aload_2
    //   453: iload_3
    //   454: invokevirtual 1275	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:writeByte	(I)V
    //   457: aload_1
    //   458: aload_2
    //   459: invokestatic 1298	libcore/io/Streams:copy	(Ljava/io/InputStream;Ljava/io/OutputStream;)I
    //   462: pop
    //   463: aload_1
    //   464: ifnull +22 -> 486
    //   467: getstatic 573	android/media/ExifInterface:DEBUG_ONEPLUS	Z
    //   470: ifeq +12 -> 482
    //   473: ldc -122
    //   475: ldc_w 1270
    //   478: invokestatic 955	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   481: pop
    //   482: aload_1
    //   483: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   486: aload_2
    //   487: ifnull +22 -> 509
    //   490: getstatic 573	android/media/ExifInterface:DEBUG_ONEPLUS	Z
    //   493: ifeq +12 -> 505
    //   496: ldc -122
    //   498: ldc_w 1272
    //   501: invokestatic 955	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   504: pop
    //   505: aload_2
    //   506: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   509: return
    //   510: iload_3
    //   511: ifle -350 -> 161
    //   514: aload_1
    //   515: aload 5
    //   517: iconst_0
    //   518: iload_3
    //   519: aload 5
    //   521: arraylength
    //   522: invokestatic 1223	java/lang/Math:min	(II)I
    //   525: invokevirtual 1289	java/io/DataInputStream:read	([BII)I
    //   528: istore 4
    //   530: iload 4
    //   532: iflt -371 -> 161
    //   535: aload_2
    //   536: aload 5
    //   538: iconst_0
    //   539: iload 4
    //   541: invokevirtual 1292	android/media/ExifInterface$ByteOrderAwarenessDataOutputStream:write	([BII)V
    //   544: iload_3
    //   545: iload 4
    //   547: isub
    //   548: istore_3
    //   549: goto -39 -> 510
    //   552: astore_2
    //   553: aload 6
    //   555: astore_1
    //   556: goto -502 -> 54
    //   559: astore_2
    //   560: goto -506 -> 54
    //   563: goto -343 -> 220
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	566	0	this	ExifInterface
    //   0	566	1	paramInputStream	InputStream
    //   0	566	2	paramOutputStream	OutputStream
    //   184	365	3	i	int
    //   243	305	4	j	int
    //   4	533	5	localObject1	Object
    //   1	1	6	localObject2	Object
    //   46	6	6	localObject3	Object
    //   292	262	6	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   27	46	46	finally
    //   104	129	46	finally
    //   129	161	46	finally
    //   161	180	46	finally
    //   180	185	46	finally
    //   220	240	46	finally
    //   253	264	46	finally
    //   264	272	46	finally
    //   277	288	46	finally
    //   288	294	46	finally
    //   301	323	46	finally
    //   323	365	46	finally
    //   365	383	46	finally
    //   399	405	46	finally
    //   409	425	46	finally
    //   430	439	46	finally
    //   447	463	46	finally
    //   514	530	46	finally
    //   535	544	46	finally
    //   6	15	552	finally
    //   15	27	559	finally
  }
  
  private boolean updateAttribute(String paramString, ExifAttribute paramExifAttribute)
  {
    boolean bool = false;
    int i = 0;
    while (i < EXIF_TAGS.length)
    {
      if (this.mAttributes[i].containsKey(paramString))
      {
        this.mAttributes[i].put(paramString, paramExifAttribute);
        bool = true;
      }
      i += 1;
    }
    return bool;
  }
  
  private int writeExifSegment(ByteOrderAwarenessDataOutputStream paramByteOrderAwarenessDataOutputStream, int paramInt)
    throws IOException
  {
    int[] arrayOfInt = new int[EXIF_TAGS.length];
    Object localObject1 = new int[EXIF_TAGS.length];
    Object localObject2 = IFD_POINTER_TAGS;
    int i = 0;
    int j = localObject2.length;
    while (i < j)
    {
      removeAttribute(localObject2[i].name);
      i += 1;
    }
    removeAttribute(JPEG_INTERCHANGE_FORMAT_TAG.name);
    removeAttribute(JPEG_INTERCHANGE_FORMAT_LENGTH_TAG.name);
    i = 0;
    while (i < EXIF_TAGS.length)
    {
      localObject2 = this.mAttributes[i].entrySet().toArray();
      j = 0;
      k = localObject2.length;
      while (j < k)
      {
        Map.Entry localEntry = (Map.Entry)localObject2[j];
        if (localEntry.getValue() == null) {
          this.mAttributes[i].remove(localEntry.getKey());
        }
        j += 1;
      }
      i += 1;
    }
    if (!this.mAttributes[3].isEmpty()) {
      this.mAttributes[1].put(IFD_POINTER_TAGS[2].name, ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (!this.mAttributes[1].isEmpty()) {
      this.mAttributes[0].put(IFD_POINTER_TAGS[0].name, ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (!this.mAttributes[2].isEmpty()) {
      this.mAttributes[0].put(IFD_POINTER_TAGS[1].name, ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (this.mHasThumbnail)
    {
      this.mAttributes[0].put(JPEG_INTERCHANGE_FORMAT_TAG.name, ExifAttribute.createULong(0L, this.mExifByteOrder));
      this.mAttributes[0].put(JPEG_INTERCHANGE_FORMAT_LENGTH_TAG.name, ExifAttribute.createULong(this.mThumbnailLength, this.mExifByteOrder));
    }
    i = 0;
    while (i < EXIF_TAGS.length)
    {
      j = 0;
      localObject2 = this.mAttributes[i].entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        k = ((ExifAttribute)((Map.Entry)((Iterator)localObject2).next()).getValue()).size();
        if (k > 4) {
          j += k;
        }
      }
      localObject1[i] += j;
      i += 1;
    }
    i = 8;
    j = 0;
    while (j < EXIF_TAGS.length)
    {
      k = i;
      if (!this.mAttributes[j].isEmpty())
      {
        arrayOfInt[j] = i;
        k = i + (this.mAttributes[j].size() * 12 + 2 + 4 + localObject1[j]);
      }
      j += 1;
      i = k;
    }
    j = i;
    if (this.mHasThumbnail)
    {
      this.mAttributes[0].put(JPEG_INTERCHANGE_FORMAT_TAG.name, ExifAttribute.createULong(i, this.mExifByteOrder));
      this.mThumbnailOffset = (paramInt + i);
      j = i + this.mThumbnailLength;
    }
    int k = j + 8;
    if (!this.mAttributes[1].isEmpty()) {
      this.mAttributes[0].put(IFD_POINTER_TAGS[0].name, ExifAttribute.createULong(arrayOfInt[1], this.mExifByteOrder));
    }
    if (!this.mAttributes[2].isEmpty()) {
      this.mAttributes[0].put(IFD_POINTER_TAGS[1].name, ExifAttribute.createULong(arrayOfInt[2], this.mExifByteOrder));
    }
    if (!this.mAttributes[3].isEmpty()) {
      this.mAttributes[1].put(IFD_POINTER_TAGS[2].name, ExifAttribute.createULong(arrayOfInt[3], this.mExifByteOrder));
    }
    paramByteOrderAwarenessDataOutputStream.writeUnsignedShort(k);
    paramByteOrderAwarenessDataOutputStream.write(IDENTIFIER_EXIF_APP1);
    short s;
    if (this.mExifByteOrder == ByteOrder.BIG_ENDIAN)
    {
      s = 19789;
      paramByteOrderAwarenessDataOutputStream.writeShort(s);
      paramByteOrderAwarenessDataOutputStream.setByteOrder(this.mExifByteOrder);
      paramByteOrderAwarenessDataOutputStream.writeUnsignedShort(42);
      paramByteOrderAwarenessDataOutputStream.writeUnsignedInt(8L);
      paramInt = 0;
    }
    for (;;)
    {
      if (paramInt >= EXIF_TAGS.length) {
        break label1116;
      }
      if (!this.mAttributes[paramInt].isEmpty())
      {
        paramByteOrderAwarenessDataOutputStream.writeUnsignedShort(this.mAttributes[paramInt].size());
        i = arrayOfInt[paramInt] + 2 + this.mAttributes[paramInt].size() * 12 + 4;
        localObject1 = this.mAttributes[paramInt].entrySet().iterator();
        for (;;)
        {
          if (!((Iterator)localObject1).hasNext()) {
            break label1001;
          }
          localObject2 = (Map.Entry)((Iterator)localObject1).next();
          int m = ((ExifTag)sExifTagMapsForWriting[paramInt].get(((Map.Entry)localObject2).getKey())).number;
          localObject2 = (ExifAttribute)((Map.Entry)localObject2).getValue();
          j = ((ExifAttribute)localObject2).size();
          paramByteOrderAwarenessDataOutputStream.writeUnsignedShort(m);
          paramByteOrderAwarenessDataOutputStream.writeUnsignedShort(((ExifAttribute)localObject2).format);
          paramByteOrderAwarenessDataOutputStream.writeInt(((ExifAttribute)localObject2).numberOfComponents);
          if (j > 4)
          {
            paramByteOrderAwarenessDataOutputStream.writeUnsignedInt(i);
            i += j;
            continue;
            s = 18761;
            break;
          }
          paramByteOrderAwarenessDataOutputStream.write(((ExifAttribute)localObject2).bytes);
          if (j < 4) {
            while (j < 4)
            {
              paramByteOrderAwarenessDataOutputStream.writeByte(0);
              j += 1;
            }
          }
        }
        label1001:
        if ((paramInt != 0) || (this.mAttributes[4].isEmpty())) {
          paramByteOrderAwarenessDataOutputStream.writeUnsignedInt(0L);
        }
        for (;;)
        {
          localObject1 = this.mAttributes[paramInt].entrySet().iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (ExifAttribute)((Map.Entry)((Iterator)localObject1).next()).getValue();
            if (((ExifAttribute)localObject2).bytes.length > 4) {
              paramByteOrderAwarenessDataOutputStream.write(((ExifAttribute)localObject2).bytes, 0, ((ExifAttribute)localObject2).bytes.length);
            }
          }
          paramByteOrderAwarenessDataOutputStream.writeUnsignedInt(arrayOfInt[4]);
        }
      }
      paramInt += 1;
    }
    label1116:
    if (this.mHasThumbnail) {
      paramByteOrderAwarenessDataOutputStream.write(getThumbnail());
    }
    paramByteOrderAwarenessDataOutputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
    return k;
  }
  
  public double getAltitude(double paramDouble)
  {
    int i = -1;
    double d = getAttributeDouble("GPSAltitude", -1.0D);
    int j = getAttributeInt("GPSAltitudeRef", -1);
    if ((d >= 0.0D) && (j >= 0))
    {
      if (j == 1) {}
      for (;;)
      {
        return i * d;
        i = 1;
      }
    }
    return paramDouble;
  }
  
  public String getAttribute(String paramString)
  {
    ExifAttribute localExifAttribute = getExifAttribute(paramString);
    if (localExifAttribute != null)
    {
      if (!sTagSetForCompatibility.contains(paramString)) {
        return localExifAttribute.getStringValue(this.mExifByteOrder);
      }
      if (paramString.equals("GPSTimeStamp"))
      {
        if ((localExifAttribute.format != 5) && (localExifAttribute.format != 10)) {
          return null;
        }
        paramString = (Rational[])ExifAttribute.-wrap0(localExifAttribute, this.mExifByteOrder);
        if (paramString.length != 3) {
          return null;
        }
        return String.format("%02d:%02d:%02d", new Object[] { Integer.valueOf((int)((float)paramString[0].numerator / (float)paramString[0].denominator)), Integer.valueOf((int)((float)paramString[1].numerator / (float)paramString[1].denominator)), Integer.valueOf((int)((float)paramString[2].numerator / (float)paramString[2].denominator)) });
      }
      try
      {
        paramString = Double.toString(localExifAttribute.getDoubleValue(this.mExifByteOrder));
        return paramString;
      }
      catch (NumberFormatException paramString)
      {
        return null;
      }
    }
    return null;
  }
  
  public double getAttributeDouble(String paramString, double paramDouble)
  {
    paramString = getExifAttribute(paramString);
    if (paramString == null) {
      return paramDouble;
    }
    try
    {
      double d = paramString.getDoubleValue(this.mExifByteOrder);
      return d;
    }
    catch (NumberFormatException paramString) {}
    return paramDouble;
  }
  
  public int getAttributeInt(String paramString, int paramInt)
  {
    paramString = getExifAttribute(paramString);
    if (paramString == null) {
      return paramInt;
    }
    try
    {
      int i = paramString.getIntValue(this.mExifByteOrder);
      return i;
    }
    catch (NumberFormatException paramString) {}
    return paramInt;
  }
  
  /* Error */
  public long getDateTime()
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc -79
    //   3: invokevirtual 870	android/media/ExifInterface:getAttribute	(Ljava/lang/String;)Ljava/lang/String;
    //   6: astore 5
    //   8: aload 5
    //   10: ifnull +112 -> 122
    //   13: getstatic 793	android/media/ExifInterface:sNonZeroTimePattern	Ljava/util/regex/Pattern;
    //   16: aload 5
    //   18: invokevirtual 1388	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
    //   21: invokevirtual 1393	java/util/regex/Matcher:matches	()Z
    //   24: ifeq +98 -> 122
    //   27: new 1395	java/text/ParsePosition
    //   30: dup
    //   31: iconst_0
    //   32: invokespecial 1397	java/text/ParsePosition:<init>	(I)V
    //   35: astore 6
    //   37: getstatic 751	android/media/ExifInterface:sFormatter	Ljava/text/SimpleDateFormat;
    //   40: aload 5
    //   42: aload 6
    //   44: invokevirtual 1401	java/text/SimpleDateFormat:parse	(Ljava/lang/String;Ljava/text/ParsePosition;)Ljava/util/Date;
    //   47: astore 5
    //   49: aload 5
    //   51: ifnonnull +7 -> 58
    //   54: ldc2_w 1252
    //   57: lreturn
    //   58: aload 5
    //   60: invokevirtual 1406	java/util/Date:getTime	()J
    //   63: lstore_3
    //   64: aload_0
    //   65: ldc_w 470
    //   68: invokevirtual 870	android/media/ExifInterface:getAttribute	(Ljava/lang/String;)Ljava/lang/String;
    //   71: astore 5
    //   73: lload_3
    //   74: lstore_1
    //   75: aload 5
    //   77: ifnull +33 -> 110
    //   80: aload 5
    //   82: invokestatic 1409	java/lang/Long:valueOf	(Ljava/lang/String;)Ljava/lang/Long;
    //   85: invokevirtual 1025	java/lang/Long:longValue	()J
    //   88: lstore_1
    //   89: lload_1
    //   90: ldc2_w 1410
    //   93: lcmp
    //   94: ifle +12 -> 106
    //   97: lload_1
    //   98: ldc2_w 1412
    //   101: ldiv
    //   102: lstore_1
    //   103: goto -14 -> 89
    //   106: lload_3
    //   107: lload_1
    //   108: ladd
    //   109: lstore_1
    //   110: lload_1
    //   111: lreturn
    //   112: astore 5
    //   114: ldc2_w 1252
    //   117: lreturn
    //   118: astore 5
    //   120: lload_3
    //   121: lreturn
    //   122: ldc2_w 1252
    //   125: lreturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	ExifInterface
    //   74	37	1	l1	long
    //   63	58	3	l2	long
    //   6	75	5	localObject	Object
    //   112	1	5	localIllegalArgumentException	IllegalArgumentException
    //   118	1	5	localNumberFormatException	NumberFormatException
    //   35	8	6	localParsePosition	ParsePosition
    // Exception table:
    //   from	to	target	type
    //   37	49	112	java/lang/IllegalArgumentException
    //   58	73	112	java/lang/IllegalArgumentException
    //   80	89	112	java/lang/IllegalArgumentException
    //   97	103	112	java/lang/IllegalArgumentException
    //   80	89	118	java/lang/NumberFormatException
    //   97	103	118	java/lang/NumberFormatException
  }
  
  public long getGpsDateTime()
  {
    Object localObject1 = getAttribute("GPSDateStamp");
    Object localObject2 = getAttribute("GPSTimeStamp");
    if ((localObject1 == null) || (localObject2 == null)) {}
    while ((!sNonZeroTimePattern.matcher((CharSequence)localObject1).matches()) && (!sNonZeroTimePattern.matcher((CharSequence)localObject2).matches())) {
      return -1L;
    }
    localObject1 = (String)localObject1 + ' ' + (String)localObject2;
    localObject2 = new ParsePosition(0);
    try
    {
      localObject1 = sFormatter.parse((String)localObject1, (ParsePosition)localObject2);
      if (localObject1 == null) {
        return -1L;
      }
      long l = ((Date)localObject1).getTime();
      return l;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return -1L;
  }
  
  public boolean getLatLong(float[] paramArrayOfFloat)
  {
    String str1 = getAttribute("GPSLatitude");
    String str2 = getAttribute("GPSLatitudeRef");
    String str3 = getAttribute("GPSLongitude");
    String str4 = getAttribute("GPSLongitudeRef");
    if ((str1 != null) && (str2 != null) && (str3 != null) && (str4 != null)) {
      try
      {
        paramArrayOfFloat[0] = convertRationalLatLonToFloat(str1, str2);
        paramArrayOfFloat[1] = convertRationalLatLonToFloat(str3, str4);
        return true;
      }
      catch (IllegalArgumentException paramArrayOfFloat) {}
    }
    return false;
  }
  
  public byte[] getThumbnail()
  {
    if (!this.mHasThumbnail) {
      return null;
    }
    if (this.mThumbnailBytes != null) {
      return this.mThumbnailBytes;
    }
    byte[] arrayOfByte = null;
    Object localObject5 = null;
    Object localObject1 = null;
    Object localObject3 = arrayOfByte;
    Object localObject4 = localObject5;
    try
    {
      if (this.mAssetInputStream != null)
      {
        localObject3 = arrayOfByte;
        localObject4 = localObject5;
        localObject1 = nativeGetThumbnailFromAsset(this.mAssetInputStream.getNativeAsset(), this.mThumbnailOffset, this.mThumbnailLength);
        IoUtils.closeQuietly(null);
        return (byte[])localObject1;
      }
      localObject3 = arrayOfByte;
      localObject4 = localObject5;
      if (this.mFilename == null) {
        break label127;
      }
      localObject3 = arrayOfByte;
      localObject4 = localObject5;
      localObject1 = new FileInputStream(this.mFilename);
    }
    catch (IOException|ErrnoException localIOException)
    {
      Object localObject2;
      for (;;)
      {
        return null;
        localObject3 = arrayOfByte;
        localObject4 = localObject5;
        if (this.mSeekableFileDescriptor != null)
        {
          localObject3 = arrayOfByte;
          localObject4 = localObject5;
          localObject2 = Os.dup(this.mSeekableFileDescriptor);
          localObject3 = arrayOfByte;
          localObject4 = localObject5;
          Os.lseek((FileDescriptor)localObject2, 0L, OsConstants.SEEK_SET);
          localObject3 = arrayOfByte;
          localObject4 = localObject5;
          localObject2 = new FileInputStream((FileDescriptor)localObject2);
        }
      }
      localObject3 = localObject2;
      localObject4 = localObject2;
      if (((FileInputStream)localObject2).skip(this.mThumbnailOffset) == this.mThumbnailOffset) {
        break label231;
      }
      localObject3 = localObject2;
      localObject4 = localObject2;
      throw new IOException("Corrupted image");
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject4);
    }
    if (localObject1 == null)
    {
      localObject3 = localObject1;
      localObject4 = localObject1;
      throw new FileNotFoundException();
    }
    label127:
    label231:
    localObject3 = localAutoCloseable;
    localObject4 = localAutoCloseable;
    arrayOfByte = new byte[this.mThumbnailLength];
    localObject3 = localAutoCloseable;
    localObject4 = localAutoCloseable;
    if (localAutoCloseable.read(arrayOfByte) != this.mThumbnailLength)
    {
      localObject3 = localAutoCloseable;
      localObject4 = localAutoCloseable;
      throw new IOException("Corrupted image");
    }
    IoUtils.closeQuietly(localAutoCloseable);
    return arrayOfByte;
  }
  
  public long[] getThumbnailRange()
  {
    if (!this.mHasThumbnail) {
      return null;
    }
    return new long[] { this.mThumbnailOffset, this.mThumbnailLength };
  }
  
  public boolean hasThumbnail()
  {
    return this.mHasThumbnail;
  }
  
  /* Error */
  public void saveAttributes()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 1141	android/media/ExifInterface:mIsSupportedFile	Z
    //   4: ifeq +10 -> 14
    //   7: aload_0
    //   8: getfield 1035	android/media/ExifInterface:mIsRaw	Z
    //   11: ifeq +14 -> 25
    //   14: new 800	java/io/IOException
    //   17: dup
    //   18: ldc_w 1439
    //   21: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   24: athrow
    //   25: aload_0
    //   26: getfield 835	android/media/ExifInterface:mIsInputStream	Z
    //   29: ifne +17 -> 46
    //   32: aload_0
    //   33: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   36: ifnonnull +21 -> 57
    //   39: aload_0
    //   40: getfield 821	android/media/ExifInterface:mFilename	Ljava/lang/String;
    //   43: ifnonnull +14 -> 57
    //   46: new 800	java/io/IOException
    //   49: dup
    //   50: ldc_w 1441
    //   53: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   56: athrow
    //   57: aload_0
    //   58: aload_0
    //   59: invokevirtual 1338	android/media/ExifInterface:getThumbnail	()[B
    //   62: putfield 1057	android/media/ExifInterface:mThumbnailBytes	[B
    //   65: aconst_null
    //   66: astore 9
    //   68: aconst_null
    //   69: astore_1
    //   70: aconst_null
    //   71: astore 11
    //   73: aconst_null
    //   74: astore 8
    //   76: aconst_null
    //   77: astore 10
    //   79: aconst_null
    //   80: astore 7
    //   82: aconst_null
    //   83: astore 5
    //   85: aconst_null
    //   86: astore 6
    //   88: aconst_null
    //   89: astore_2
    //   90: aload 9
    //   92: astore_3
    //   93: aload 10
    //   95: astore 4
    //   97: aload_0
    //   98: getfield 821	android/media/ExifInterface:mFilename	Ljava/lang/String;
    //   101: ifnull +119 -> 220
    //   104: aload 9
    //   106: astore_3
    //   107: aload 10
    //   109: astore 4
    //   111: new 1443	java/io/File
    //   114: dup
    //   115: new 932	java/lang/StringBuilder
    //   118: dup
    //   119: invokespecial 933	java/lang/StringBuilder:<init>	()V
    //   122: aload_0
    //   123: getfield 821	android/media/ExifInterface:mFilename	Ljava/lang/String;
    //   126: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: ldc_w 1445
    //   132: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: invokevirtual 946	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   138: invokespecial 1446	java/io/File:<init>	(Ljava/lang/String;)V
    //   141: astore_2
    //   142: new 1443	java/io/File
    //   145: dup
    //   146: aload_0
    //   147: getfield 821	android/media/ExifInterface:mFilename	Ljava/lang/String;
    //   150: invokespecial 1446	java/io/File:<init>	(Ljava/lang/String;)V
    //   153: aload_2
    //   154: invokevirtual 1450	java/io/File:renameTo	(Ljava/io/File;)Z
    //   157: ifne +437 -> 594
    //   160: new 800	java/io/IOException
    //   163: dup
    //   164: new 932	java/lang/StringBuilder
    //   167: dup
    //   168: invokespecial 933	java/lang/StringBuilder:<init>	()V
    //   171: ldc_w 1452
    //   174: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   177: aload_2
    //   178: invokevirtual 1455	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   181: invokevirtual 939	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: invokevirtual 946	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   187: invokespecial 947	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   190: athrow
    //   191: astore_1
    //   192: aload 6
    //   194: astore 4
    //   196: aload 8
    //   198: astore_2
    //   199: aload_2
    //   200: astore_3
    //   201: aload_1
    //   202: invokevirtual 853	android/system/ErrnoException:rethrowAsIOException	()Ljava/io/IOException;
    //   205: athrow
    //   206: astore_2
    //   207: aload_3
    //   208: astore_1
    //   209: aload_1
    //   210: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   213: aload 4
    //   215: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   218: aload_2
    //   219: athrow
    //   220: aload 9
    //   222: astore_3
    //   223: aload 10
    //   225: astore 4
    //   227: aload 11
    //   229: astore_1
    //   230: aload_0
    //   231: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   234: ifnull +81 -> 315
    //   237: aload 9
    //   239: astore_3
    //   240: aload 10
    //   242: astore 4
    //   244: ldc_w 1457
    //   247: ldc_w 1459
    //   250: invokestatic 1463	java/io/File:createTempFile	(Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;
    //   253: astore 11
    //   255: aload 9
    //   257: astore_3
    //   258: aload 10
    //   260: astore 4
    //   262: aload_0
    //   263: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   266: lconst_0
    //   267: getstatic 1429	android/system/OsConstants:SEEK_SET	I
    //   270: invokestatic 1113	android/system/Os:lseek	(Ljava/io/FileDescriptor;JI)J
    //   273: pop2
    //   274: aload 9
    //   276: astore_3
    //   277: aload 10
    //   279: astore 4
    //   281: new 837	java/io/FileInputStream
    //   284: dup
    //   285: aload_0
    //   286: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   289: invokespecial 839	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   292: astore_1
    //   293: new 1465	java/io/FileOutputStream
    //   296: dup
    //   297: aload 11
    //   299: invokespecial 1468	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   302: astore_2
    //   303: aload_1
    //   304: aload_2
    //   305: invokestatic 1298	libcore/io/Streams:copy	(Ljava/io/InputStream;Ljava/io/OutputStream;)I
    //   308: pop
    //   309: aload_2
    //   310: astore 5
    //   312: aload 11
    //   314: astore_2
    //   315: aload_1
    //   316: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   319: aload 5
    //   321: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   324: aconst_null
    //   325: astore_1
    //   326: aconst_null
    //   327: astore 4
    //   329: aconst_null
    //   330: astore 9
    //   332: aconst_null
    //   333: astore_3
    //   334: aconst_null
    //   335: astore 6
    //   337: aconst_null
    //   338: astore 7
    //   340: aconst_null
    //   341: astore 8
    //   343: new 837	java/io/FileInputStream
    //   346: dup
    //   347: aload_2
    //   348: invokespecial 1469	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   351: astore 5
    //   353: aload 6
    //   355: astore 4
    //   357: aload 7
    //   359: astore_3
    //   360: aload_0
    //   361: getfield 821	android/media/ExifInterface:mFilename	Ljava/lang/String;
    //   364: ifnull +54 -> 418
    //   367: aload 6
    //   369: astore 4
    //   371: aload 7
    //   373: astore_3
    //   374: new 1465	java/io/FileOutputStream
    //   377: dup
    //   378: aload_0
    //   379: getfield 821	android/media/ExifInterface:mFilename	Ljava/lang/String;
    //   382: invokespecial 1470	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   385: astore_1
    //   386: aload_1
    //   387: astore 4
    //   389: aload_1
    //   390: astore_3
    //   391: aload_0
    //   392: aload 5
    //   394: aload_1
    //   395: invokespecial 1472	android/media/ExifInterface:saveJpegAttributes	(Ljava/io/InputStream;Ljava/io/OutputStream;)V
    //   398: aload 5
    //   400: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   403: aload_1
    //   404: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   407: aload_2
    //   408: invokevirtual 1475	java/io/File:delete	()Z
    //   411: pop
    //   412: aload_0
    //   413: aconst_null
    //   414: putfield 1057	android/media/ExifInterface:mThumbnailBytes	[B
    //   417: return
    //   418: aload 8
    //   420: astore_1
    //   421: aload 6
    //   423: astore 4
    //   425: aload 7
    //   427: astore_3
    //   428: aload_0
    //   429: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   432: ifnull -46 -> 386
    //   435: aload 6
    //   437: astore 4
    //   439: aload 7
    //   441: astore_3
    //   442: aload_0
    //   443: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   446: lconst_0
    //   447: getstatic 1429	android/system/OsConstants:SEEK_SET	I
    //   450: invokestatic 1113	android/system/Os:lseek	(Ljava/io/FileDescriptor;JI)J
    //   453: pop2
    //   454: aload 6
    //   456: astore 4
    //   458: aload 7
    //   460: astore_3
    //   461: new 1465	java/io/FileOutputStream
    //   464: dup
    //   465: aload_0
    //   466: getfield 827	android/media/ExifInterface:mSeekableFileDescriptor	Ljava/io/FileDescriptor;
    //   469: invokespecial 1476	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   472: astore_1
    //   473: goto -87 -> 386
    //   476: astore 5
    //   478: aload 9
    //   480: astore_3
    //   481: aload 4
    //   483: astore_1
    //   484: aload 5
    //   486: astore 4
    //   488: aload 4
    //   490: invokevirtual 853	android/system/ErrnoException:rethrowAsIOException	()Ljava/io/IOException;
    //   493: athrow
    //   494: astore 4
    //   496: aload_1
    //   497: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   500: aload_3
    //   501: invokestatic 849	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   504: aload_2
    //   505: invokevirtual 1475	java/io/File:delete	()Z
    //   508: pop
    //   509: aload 4
    //   511: athrow
    //   512: astore 6
    //   514: aload 5
    //   516: astore_1
    //   517: aload 4
    //   519: astore_3
    //   520: aload 6
    //   522: astore 4
    //   524: goto -28 -> 496
    //   527: astore 4
    //   529: aload 5
    //   531: astore_1
    //   532: goto -44 -> 488
    //   535: astore_2
    //   536: aload 7
    //   538: astore 4
    //   540: goto -331 -> 209
    //   543: astore_2
    //   544: aload 7
    //   546: astore 4
    //   548: goto -339 -> 209
    //   551: astore_3
    //   552: aload_2
    //   553: astore 4
    //   555: aload_3
    //   556: astore_2
    //   557: goto -348 -> 209
    //   560: astore_1
    //   561: aload 8
    //   563: astore_2
    //   564: aload 6
    //   566: astore 4
    //   568: goto -369 -> 199
    //   571: astore_3
    //   572: aload_1
    //   573: astore_2
    //   574: aload_3
    //   575: astore_1
    //   576: aload 6
    //   578: astore 4
    //   580: goto -381 -> 199
    //   583: astore_3
    //   584: aload_2
    //   585: astore 4
    //   587: aload_1
    //   588: astore_2
    //   589: aload_3
    //   590: astore_1
    //   591: goto -392 -> 199
    //   594: aload 11
    //   596: astore_1
    //   597: goto -282 -> 315
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	600	0	this	ExifInterface
    //   69	1	1	localObject1	Object
    //   191	11	1	localErrnoException1	ErrnoException
    //   208	324	1	localObject2	Object
    //   560	13	1	localErrnoException2	ErrnoException
    //   575	22	1	localObject3	Object
    //   89	111	2	localObject4	Object
    //   206	13	2	localObject5	Object
    //   302	203	2	localObject6	Object
    //   535	1	2	localObject7	Object
    //   543	10	2	localObject8	Object
    //   556	33	2	localObject9	Object
    //   92	428	3	localObject10	Object
    //   551	5	3	localObject11	Object
    //   571	4	3	localErrnoException3	ErrnoException
    //   583	7	3	localErrnoException4	ErrnoException
    //   95	394	4	localObject12	Object
    //   494	24	4	localObject13	Object
    //   522	1	4	localObject14	Object
    //   527	1	4	localErrnoException5	ErrnoException
    //   538	48	4	localObject15	Object
    //   83	316	5	localObject16	Object
    //   476	54	5	localErrnoException6	ErrnoException
    //   86	369	6	localObject17	Object
    //   512	65	6	localObject18	Object
    //   80	465	7	localObject19	Object
    //   74	488	8	localObject20	Object
    //   66	413	9	localObject21	Object
    //   77	201	10	localObject22	Object
    //   71	524	11	localFile	java.io.File
    // Exception table:
    //   from	to	target	type
    //   142	191	191	android/system/ErrnoException
    //   97	104	206	finally
    //   111	142	206	finally
    //   201	206	206	finally
    //   230	237	206	finally
    //   244	255	206	finally
    //   262	274	206	finally
    //   281	293	206	finally
    //   343	353	476	android/system/ErrnoException
    //   343	353	494	finally
    //   488	494	494	finally
    //   360	367	512	finally
    //   374	386	512	finally
    //   391	398	512	finally
    //   428	435	512	finally
    //   442	454	512	finally
    //   461	473	512	finally
    //   360	367	527	android/system/ErrnoException
    //   374	386	527	android/system/ErrnoException
    //   391	398	527	android/system/ErrnoException
    //   428	435	527	android/system/ErrnoException
    //   442	454	527	android/system/ErrnoException
    //   461	473	527	android/system/ErrnoException
    //   142	191	535	finally
    //   293	303	543	finally
    //   303	309	551	finally
    //   97	104	560	android/system/ErrnoException
    //   111	142	560	android/system/ErrnoException
    //   230	237	560	android/system/ErrnoException
    //   244	255	560	android/system/ErrnoException
    //   262	274	560	android/system/ErrnoException
    //   281	293	560	android/system/ErrnoException
    //   293	303	571	android/system/ErrnoException
    //   303	309	583	android/system/ErrnoException
  }
  
  public void setAttribute(String paramString1, String paramString2)
  {
    Object localObject1 = paramString2;
    if (paramString2 != null)
    {
      localObject1 = paramString2;
      if (sTagSetForCompatibility.contains(paramString1))
      {
        if (!paramString1.equals("GPSTimeStamp")) {
          break label216;
        }
        localObject1 = sGpsTimestampPattern.matcher(paramString2);
        if (!((Matcher)localObject1).find())
        {
          Log.w("ExifInterface", "Invalid value for " + paramString1 + " : " + paramString2);
          return;
        }
        localObject1 = Integer.parseInt(((Matcher)localObject1).group(1)) + "/1," + Integer.parseInt(((Matcher)localObject1).group(2)) + "/1," + Integer.parseInt(((Matcher)localObject1).group(3)) + "/1";
      }
    }
    int j = 0;
    label153:
    if (j < EXIF_TAGS.length)
    {
      if ((j != 4) || (this.mHasThumbnail))
      {
        paramString2 = sExifTagMapsForWriting[j].get(paramString1);
        if (paramString2 != null)
        {
          if (localObject1 != null) {
            break label290;
          }
          this.mAttributes[j].remove(paramString1);
        }
      }
      for (;;)
      {
        label207:
        j += 1;
        break label153;
        try
        {
          label216:
          double d = Double.parseDouble(paramString2);
          localObject1 = (10000.0D * d) + "/10000";
        }
        catch (NumberFormatException localNumberFormatException)
        {
          Log.w("ExifInterface", "Invalid value for " + paramString1 + " : " + paramString2);
          return;
        }
        label290:
        paramString2 = (ExifTag)paramString2;
        Object localObject2 = guessDataFormat(localNumberFormatException);
        if ((paramString2.primaryFormat == ((Integer)((Pair)localObject2).first).intValue()) || (paramString2.primaryFormat == ((Integer)((Pair)localObject2).second).intValue())) {}
        for (int i = paramString2.primaryFormat;; i = paramString2.secondaryFormat) {
          switch (i)
          {
          case 6: 
          case 8: 
          case 11: 
          default: 
            Log.w("ExifInterface", "Data format isn't one of expected formats: " + i);
            break label207;
            if ((paramString2.secondaryFormat == -1) || ((paramString2.secondaryFormat != ((Integer)((Pair)localObject2).first).intValue()) && (paramString2.secondaryFormat != ((Integer)((Pair)localObject2).second).intValue()))) {
              break label491;
            }
          }
        }
        label491:
        if ((paramString2.primaryFormat == 1) || (paramString2.primaryFormat == 7)) {}
        while (paramString2.primaryFormat == 2)
        {
          i = paramString2.primaryFormat;
          break;
        }
        Object localObject3 = new StringBuilder().append("Given tag (").append(paramString1).append(") value didn't match with one of expected ").append("formats: ").append(IFD_FORMAT_NAMES[paramString2.primaryFormat]);
        if (paramString2.secondaryFormat == -1)
        {
          paramString2 = "";
          label579:
          localObject3 = ((StringBuilder)localObject3).append(paramString2).append(" (guess: ").append(IFD_FORMAT_NAMES[((Integer)localObject2.first).intValue()]);
          if (((Integer)((Pair)localObject2).second).intValue() != -1) {
            break label685;
          }
        }
        label685:
        for (paramString2 = "";; paramString2 = ", " + IFD_FORMAT_NAMES[((Integer)localObject2.second).intValue()])
        {
          Log.w("ExifInterface", paramString2 + ")");
          break;
          paramString2 = ", " + IFD_FORMAT_NAMES[paramString2.secondaryFormat];
          break label579;
        }
        this.mAttributes[j].put(paramString1, ExifAttribute.createByte(localNumberFormatException));
        continue;
        this.mAttributes[j].put(paramString1, ExifAttribute.createString(localNumberFormatException));
        continue;
        paramString2 = localNumberFormatException.split(",");
        localObject2 = new int[paramString2.length];
        i = 0;
        while (i < paramString2.length)
        {
          localObject2[i] = Integer.parseInt(paramString2[i]);
          i += 1;
        }
        this.mAttributes[j].put(paramString1, ExifAttribute.createUShort((int[])localObject2, this.mExifByteOrder));
        continue;
        paramString2 = localNumberFormatException.split(",");
        localObject2 = new int[paramString2.length];
        i = 0;
        while (i < paramString2.length)
        {
          localObject2[i] = Integer.parseInt(paramString2[i]);
          i += 1;
        }
        this.mAttributes[j].put(paramString1, ExifAttribute.createSLong((int[])localObject2, this.mExifByteOrder));
        continue;
        paramString2 = localNumberFormatException.split(",");
        localObject2 = new long[paramString2.length];
        i = 0;
        while (i < paramString2.length)
        {
          localObject2[i] = Long.parseLong(paramString2[i]);
          i += 1;
        }
        this.mAttributes[j].put(paramString1, ExifAttribute.createULong((long[])localObject2, this.mExifByteOrder));
        continue;
        paramString2 = localNumberFormatException.split(",");
        localObject2 = new Rational[paramString2.length];
        i = 0;
        while (i < paramString2.length)
        {
          localObject3 = paramString2[i].split("/");
          localObject2[i] = new Rational(Long.parseLong(localObject3[0]), Long.parseLong(localObject3[1]), null);
          i += 1;
        }
        this.mAttributes[j].put(paramString1, ExifAttribute.createURational((Rational[])localObject2, this.mExifByteOrder));
        continue;
        paramString2 = localNumberFormatException.split(",");
        localObject2 = new Rational[paramString2.length];
        i = 0;
        while (i < paramString2.length)
        {
          localObject3 = paramString2[i].split("/");
          localObject2[i] = new Rational(Long.parseLong(localObject3[0]), Long.parseLong(localObject3[1]), null);
          i += 1;
        }
        this.mAttributes[j].put(paramString1, ExifAttribute.createSRational((Rational[])localObject2, this.mExifByteOrder));
        continue;
        paramString2 = localNumberFormatException.split(",");
        localObject2 = new double[paramString2.length];
        i = 0;
        while (i < paramString2.length)
        {
          localObject2[i] = Double.parseDouble(paramString2[i]);
          i += 1;
        }
        this.mAttributes[j].put(paramString1, ExifAttribute.createDouble((double[])localObject2, this.mExifByteOrder));
      }
    }
  }
  
  private static class ByteOrderAwarenessDataInputStream
    extends ByteArrayInputStream
  {
    private static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
    private static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    private ByteOrder mByteOrder = ByteOrder.BIG_ENDIAN;
    private final long mLength;
    private long mPosition;
    
    public ByteOrderAwarenessDataInputStream(byte[] paramArrayOfByte)
    {
      super();
      this.mLength = paramArrayOfByte.length;
      this.mPosition = 0L;
    }
    
    public long peek()
    {
      return this.mPosition;
    }
    
    public byte readByte()
      throws IOException
    {
      this.mPosition += 1L;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = super.read();
      if (i < 0) {
        throw new EOFException();
      }
      return (byte)i;
    }
    
    public double readDouble()
      throws IOException
    {
      return Double.longBitsToDouble(readLong());
    }
    
    public float readFloat()
      throws IOException
    {
      return Float.intBitsToFloat(readInt());
    }
    
    public void readFully(byte[] paramArrayOfByte)
      throws IOException
    {
      this.mPosition += paramArrayOfByte.length;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      if (super.read(paramArrayOfByte, 0, paramArrayOfByte.length) != paramArrayOfByte.length) {
        throw new IOException("Couldn't read up to the length of buffer");
      }
    }
    
    public int readInt()
      throws IOException
    {
      this.mPosition += 4L;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = super.read();
      int j = super.read();
      int k = super.read();
      int m = super.read();
      if ((i | j | k | m) < 0) {
        throw new EOFException();
      }
      if (this.mByteOrder == LITTLE_ENDIAN) {
        return (m << 24) + (k << 16) + (j << 8) + i;
      }
      if (this.mByteOrder == BIG_ENDIAN) {
        return (i << 24) + (j << 16) + (k << 8) + m;
      }
      throw new IOException("Invalid byte order: " + this.mByteOrder);
    }
    
    public long readLong()
      throws IOException
    {
      this.mPosition += 8L;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = super.read();
      int j = super.read();
      int k = super.read();
      int m = super.read();
      int n = super.read();
      int i1 = super.read();
      int i2 = super.read();
      int i3 = super.read();
      if ((i | j | k | m | n | i1 | i2 | i3) < 0) {
        throw new EOFException();
      }
      if (this.mByteOrder == LITTLE_ENDIAN) {
        return (i3 << 56) + (i2 << 48) + (i1 << 40) + (n << 32) + (m << 24) + (k << 16) + (j << 8) + i;
      }
      if (this.mByteOrder == BIG_ENDIAN) {
        return (i << 56) + (j << 48) + (k << 40) + (m << 32) + (n << 24) + (i1 << 16) + (i2 << 8) + i3;
      }
      throw new IOException("Invalid byte order: " + this.mByteOrder);
    }
    
    public short readShort()
      throws IOException
    {
      this.mPosition += 2L;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = super.read();
      int j = super.read();
      if ((i | j) < 0) {
        throw new EOFException();
      }
      if (this.mByteOrder == LITTLE_ENDIAN) {
        return (short)((j << 8) + i);
      }
      if (this.mByteOrder == BIG_ENDIAN) {
        return (short)((i << 8) + j);
      }
      throw new IOException("Invalid byte order: " + this.mByteOrder);
    }
    
    public long readUnsignedInt()
      throws IOException
    {
      return readInt() & 0xFFFFFFFF;
    }
    
    public int readUnsignedShort()
      throws IOException
    {
      this.mPosition += 2L;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = super.read();
      int j = super.read();
      if ((i | j) < 0) {
        throw new EOFException();
      }
      if (this.mByteOrder == LITTLE_ENDIAN) {
        return (j << 8) + i;
      }
      if (this.mByteOrder == BIG_ENDIAN) {
        return (i << 8) + j;
      }
      throw new IOException("Invalid byte order: " + this.mByteOrder);
    }
    
    public void seek(long paramLong)
      throws IOException
    {
      this.mPosition = 0L;
      reset();
      if (skip(paramLong) != paramLong) {
        throw new IOException("Couldn't seek up to the byteCount");
      }
    }
    
    public void setByteOrder(ByteOrder paramByteOrder)
    {
      this.mByteOrder = paramByteOrder;
    }
    
    public long skip(long paramLong)
    {
      paramLong = super.skip(Math.min(paramLong, this.mLength - this.mPosition));
      this.mPosition += paramLong;
      return paramLong;
    }
  }
  
  private static class ByteOrderAwarenessDataOutputStream
    extends FilterOutputStream
  {
    private ByteOrder mByteOrder;
    private final OutputStream mOutputStream;
    
    public ByteOrderAwarenessDataOutputStream(OutputStream paramOutputStream, ByteOrder paramByteOrder)
    {
      super();
      this.mOutputStream = paramOutputStream;
      this.mByteOrder = paramByteOrder;
    }
    
    public void setByteOrder(ByteOrder paramByteOrder)
    {
      this.mByteOrder = paramByteOrder;
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      this.mOutputStream.write(paramArrayOfByte);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      this.mOutputStream.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public void writeByte(int paramInt)
      throws IOException
    {
      this.mOutputStream.write(paramInt);
    }
    
    public void writeInt(int paramInt)
      throws IOException
    {
      if (this.mByteOrder == ByteOrder.LITTLE_ENDIAN)
      {
        this.mOutputStream.write(paramInt >>> 0 & 0xFF);
        this.mOutputStream.write(paramInt >>> 8 & 0xFF);
        this.mOutputStream.write(paramInt >>> 16 & 0xFF);
        this.mOutputStream.write(paramInt >>> 24 & 0xFF);
      }
      while (this.mByteOrder != ByteOrder.BIG_ENDIAN) {
        return;
      }
      this.mOutputStream.write(paramInt >>> 24 & 0xFF);
      this.mOutputStream.write(paramInt >>> 16 & 0xFF);
      this.mOutputStream.write(paramInt >>> 8 & 0xFF);
      this.mOutputStream.write(paramInt >>> 0 & 0xFF);
    }
    
    public void writeShort(short paramShort)
      throws IOException
    {
      if (this.mByteOrder == ByteOrder.LITTLE_ENDIAN)
      {
        this.mOutputStream.write(paramShort >>> 0 & 0xFF);
        this.mOutputStream.write(paramShort >>> 8 & 0xFF);
      }
      while (this.mByteOrder != ByteOrder.BIG_ENDIAN) {
        return;
      }
      this.mOutputStream.write(paramShort >>> 8 & 0xFF);
      this.mOutputStream.write(paramShort >>> 0 & 0xFF);
    }
    
    public void writeUnsignedInt(long paramLong)
      throws IOException
    {
      writeInt((int)paramLong);
    }
    
    public void writeUnsignedShort(int paramInt)
      throws IOException
    {
      writeShort((short)paramInt);
    }
  }
  
  private static class ExifAttribute
  {
    public final byte[] bytes;
    public final int format;
    public final int numberOfComponents;
    
    private ExifAttribute(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      this.format = paramInt1;
      this.numberOfComponents = paramInt2;
      this.bytes = paramArrayOfByte;
    }
    
    public static ExifAttribute createByte(String paramString)
    {
      if ((paramString.length() == 1) && (paramString.charAt(0) >= '0') && (paramString.charAt(0) <= '1'))
      {
        byte[] arrayOfByte = new byte[1];
        arrayOfByte[0] = ((byte)(paramString.charAt(0) - '0'));
        return new ExifAttribute(1, arrayOfByte.length, arrayOfByte);
      }
      paramString = paramString.getBytes(ExifInterface.-get0());
      return new ExifAttribute(1, paramString.length, paramString);
    }
    
    public static ExifAttribute createDouble(double paramDouble, ByteOrder paramByteOrder)
    {
      return createDouble(new double[] { paramDouble }, paramByteOrder);
    }
    
    public static ExifAttribute createDouble(double[] paramArrayOfDouble, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.-get2()[12] * paramArrayOfDouble.length]);
      localByteBuffer.order(paramByteOrder);
      int i = 0;
      int j = paramArrayOfDouble.length;
      while (i < j)
      {
        localByteBuffer.putDouble(paramArrayOfDouble[i]);
        i += 1;
      }
      return new ExifAttribute(12, paramArrayOfDouble.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createSLong(int paramInt, ByteOrder paramByteOrder)
    {
      return createSLong(new int[] { paramInt }, paramByteOrder);
    }
    
    public static ExifAttribute createSLong(int[] paramArrayOfInt, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.-get2()[9] * paramArrayOfInt.length]);
      localByteBuffer.order(paramByteOrder);
      int i = 0;
      int j = paramArrayOfInt.length;
      while (i < j)
      {
        localByteBuffer.putInt(paramArrayOfInt[i]);
        i += 1;
      }
      return new ExifAttribute(9, paramArrayOfInt.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createSRational(ExifInterface.Rational paramRational, ByteOrder paramByteOrder)
    {
      return createSRational(new ExifInterface.Rational[] { paramRational }, paramByteOrder);
    }
    
    public static ExifAttribute createSRational(ExifInterface.Rational[] paramArrayOfRational, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.-get2()[10] * paramArrayOfRational.length]);
      localByteBuffer.order(paramByteOrder);
      int i = 0;
      int j = paramArrayOfRational.length;
      while (i < j)
      {
        paramByteOrder = paramArrayOfRational[i];
        localByteBuffer.putInt((int)paramByteOrder.numerator);
        localByteBuffer.putInt((int)paramByteOrder.denominator);
        i += 1;
      }
      return new ExifAttribute(10, paramArrayOfRational.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createString(String paramString)
    {
      paramString = (paramString + '\000').getBytes(ExifInterface.-get0());
      return new ExifAttribute(2, paramString.length, paramString);
    }
    
    public static ExifAttribute createULong(long paramLong, ByteOrder paramByteOrder)
    {
      return createULong(new long[] { paramLong }, paramByteOrder);
    }
    
    public static ExifAttribute createULong(long[] paramArrayOfLong, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.-get2()[4] * paramArrayOfLong.length]);
      localByteBuffer.order(paramByteOrder);
      int i = 0;
      int j = paramArrayOfLong.length;
      while (i < j)
      {
        localByteBuffer.putInt((int)paramArrayOfLong[i]);
        i += 1;
      }
      return new ExifAttribute(4, paramArrayOfLong.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createURational(ExifInterface.Rational paramRational, ByteOrder paramByteOrder)
    {
      return createURational(new ExifInterface.Rational[] { paramRational }, paramByteOrder);
    }
    
    public static ExifAttribute createURational(ExifInterface.Rational[] paramArrayOfRational, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.-get2()[5] * paramArrayOfRational.length]);
      localByteBuffer.order(paramByteOrder);
      int i = 0;
      int j = paramArrayOfRational.length;
      while (i < j)
      {
        paramByteOrder = paramArrayOfRational[i];
        localByteBuffer.putInt((int)paramByteOrder.numerator);
        localByteBuffer.putInt((int)paramByteOrder.denominator);
        i += 1;
      }
      return new ExifAttribute(5, paramArrayOfRational.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createUShort(int paramInt, ByteOrder paramByteOrder)
    {
      return createUShort(new int[] { paramInt }, paramByteOrder);
    }
    
    public static ExifAttribute createUShort(int[] paramArrayOfInt, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.-get2()[3] * paramArrayOfInt.length]);
      localByteBuffer.order(paramByteOrder);
      int i = 0;
      int j = paramArrayOfInt.length;
      while (i < j)
      {
        localByteBuffer.putShort((short)paramArrayOfInt[i]);
        i += 1;
      }
      return new ExifAttribute(3, paramArrayOfInt.length, localByteBuffer.array());
    }
    
    private Object getValue(ByteOrder paramByteOrder)
    {
      ExifInterface.ByteOrderAwarenessDataInputStream localByteOrderAwarenessDataInputStream;
      try
      {
        localByteOrderAwarenessDataInputStream = new ExifInterface.ByteOrderAwarenessDataInputStream(this.bytes);
        localByteOrderAwarenessDataInputStream.setByteOrder(paramByteOrder);
        switch (this.format)
        {
        case 1: 
        case 6: 
          if ((this.bytes.length == 1) && (this.bytes[0] >= 0) && (this.bytes[0] <= 1)) {
            return new String(new char[] { (char)(this.bytes[0] + 48) });
          }
          return new String(this.bytes, ExifInterface.-get0());
        }
      }
      catch (IOException paramByteOrder)
      {
        int k;
        int m;
        int j;
        Log.w("ExifInterface", "IOException occurred during reading a value", paramByteOrder);
        return null;
      }
      k = 0;
      int i = k;
      if (this.numberOfComponents >= ExifInterface.-get1().length)
      {
        m = 1;
        i = 0;
        j = m;
        if (i < ExifInterface.-get1().length)
        {
          if (this.bytes[i] == ExifInterface.-get1()[i]) {
            break label573;
          }
          j = 0;
        }
        i = k;
        if (j != 0) {
          i = ExifInterface.-get1().length;
        }
      }
      paramByteOrder = new StringBuilder();
      for (;;)
      {
        if (i < this.numberOfComponents)
        {
          j = this.bytes[i];
          if (j != 0) {}
        }
        else
        {
          return paramByteOrder.toString();
        }
        if (j >= 32)
        {
          paramByteOrder.append((char)j);
        }
        else
        {
          paramByteOrder.append('?');
          break label580;
          paramByteOrder = new int[this.numberOfComponents];
          i = 0;
          while (i < this.numberOfComponents)
          {
            paramByteOrder[i] = localByteOrderAwarenessDataInputStream.readUnsignedShort();
            i += 1;
          }
          paramByteOrder = new long[this.numberOfComponents];
          i = 0;
          while (i < this.numberOfComponents)
          {
            paramByteOrder[i] = localByteOrderAwarenessDataInputStream.readUnsignedInt();
            i += 1;
          }
          paramByteOrder = new ExifInterface.Rational[this.numberOfComponents];
          i = 0;
          while (i < this.numberOfComponents)
          {
            paramByteOrder[i] = new ExifInterface.Rational(localByteOrderAwarenessDataInputStream.readUnsignedInt(), localByteOrderAwarenessDataInputStream.readUnsignedInt(), null);
            i += 1;
          }
          paramByteOrder = new int[this.numberOfComponents];
          i = 0;
          while (i < this.numberOfComponents)
          {
            paramByteOrder[i] = localByteOrderAwarenessDataInputStream.readShort();
            i += 1;
          }
          paramByteOrder = new int[this.numberOfComponents];
          i = 0;
          while (i < this.numberOfComponents)
          {
            paramByteOrder[i] = localByteOrderAwarenessDataInputStream.readInt();
            i += 1;
          }
          paramByteOrder = new ExifInterface.Rational[this.numberOfComponents];
          i = 0;
          while (i < this.numberOfComponents)
          {
            paramByteOrder[i] = new ExifInterface.Rational(localByteOrderAwarenessDataInputStream.readInt(), localByteOrderAwarenessDataInputStream.readInt(), null);
            i += 1;
          }
          paramByteOrder = new double[this.numberOfComponents];
          i = 0;
          while (i < this.numberOfComponents)
          {
            paramByteOrder[i] = localByteOrderAwarenessDataInputStream.readFloat();
            i += 1;
          }
          paramByteOrder = new double[this.numberOfComponents];
          i = 0;
          while (i < this.numberOfComponents)
          {
            paramByteOrder[i] = localByteOrderAwarenessDataInputStream.readDouble();
            i += 1;
          }
          return paramByteOrder;
          return null;
          label573:
          i += 1;
          break;
        }
        label580:
        i += 1;
      }
      return paramByteOrder;
      return paramByteOrder;
      return paramByteOrder;
      return paramByteOrder;
      return paramByteOrder;
      return paramByteOrder;
      return paramByteOrder;
    }
    
    public double getDoubleValue(ByteOrder paramByteOrder)
    {
      paramByteOrder = getValue(paramByteOrder);
      if (paramByteOrder == null) {
        throw new NumberFormatException("NULL can't be converted to a double value");
      }
      if ((paramByteOrder instanceof String)) {
        return Double.parseDouble((String)paramByteOrder);
      }
      if ((paramByteOrder instanceof long[]))
      {
        paramByteOrder = (long[])paramByteOrder;
        if (paramByteOrder.length == 1) {
          return paramByteOrder[0];
        }
        throw new NumberFormatException("There are more than one component");
      }
      if ((paramByteOrder instanceof int[]))
      {
        paramByteOrder = (int[])paramByteOrder;
        if (paramByteOrder.length == 1) {
          return paramByteOrder[0];
        }
        throw new NumberFormatException("There are more than one component");
      }
      if ((paramByteOrder instanceof double[]))
      {
        paramByteOrder = (double[])paramByteOrder;
        if (paramByteOrder.length == 1) {
          return paramByteOrder[0];
        }
        throw new NumberFormatException("There are more than one component");
      }
      if ((paramByteOrder instanceof ExifInterface.Rational[]))
      {
        paramByteOrder = (ExifInterface.Rational[])paramByteOrder;
        if (paramByteOrder.length == 1) {
          return paramByteOrder[0].calculate();
        }
        throw new NumberFormatException("There are more than one component");
      }
      throw new NumberFormatException("Couldn't find a double value");
    }
    
    public int getIntValue(ByteOrder paramByteOrder)
    {
      paramByteOrder = getValue(paramByteOrder);
      if (paramByteOrder == null) {
        throw new NumberFormatException("NULL can't be converted to a integer value");
      }
      if ((paramByteOrder instanceof String)) {
        return Integer.parseInt((String)paramByteOrder);
      }
      if ((paramByteOrder instanceof long[]))
      {
        paramByteOrder = (long[])paramByteOrder;
        if (paramByteOrder.length == 1) {
          return (int)paramByteOrder[0];
        }
        throw new NumberFormatException("There are more than one component");
      }
      if ((paramByteOrder instanceof int[]))
      {
        paramByteOrder = (int[])paramByteOrder;
        if (paramByteOrder.length == 1) {
          return paramByteOrder[0];
        }
        throw new NumberFormatException("There are more than one component");
      }
      throw new NumberFormatException("Couldn't find a integer value");
    }
    
    public String getStringValue(ByteOrder paramByteOrder)
    {
      Object localObject = getValue(paramByteOrder);
      if (localObject == null) {
        return null;
      }
      if ((localObject instanceof String)) {
        return (String)localObject;
      }
      paramByteOrder = new StringBuilder();
      int i;
      if ((localObject instanceof long[]))
      {
        localObject = (long[])localObject;
        i = 0;
        while (i < localObject.length)
        {
          paramByteOrder.append(localObject[i]);
          if (i + 1 != localObject.length) {
            paramByteOrder.append(",");
          }
          i += 1;
        }
        return paramByteOrder.toString();
      }
      if ((localObject instanceof int[]))
      {
        localObject = (int[])localObject;
        i = 0;
        while (i < localObject.length)
        {
          paramByteOrder.append(localObject[i]);
          if (i + 1 != localObject.length) {
            paramByteOrder.append(",");
          }
          i += 1;
        }
        return paramByteOrder.toString();
      }
      if ((localObject instanceof double[]))
      {
        localObject = (double[])localObject;
        i = 0;
        while (i < localObject.length)
        {
          paramByteOrder.append(localObject[i]);
          if (i + 1 != localObject.length) {
            paramByteOrder.append(",");
          }
          i += 1;
        }
        return paramByteOrder.toString();
      }
      if ((localObject instanceof ExifInterface.Rational[]))
      {
        localObject = (ExifInterface.Rational[])localObject;
        i = 0;
        while (i < localObject.length)
        {
          paramByteOrder.append(localObject[i].numerator);
          paramByteOrder.append('/');
          paramByteOrder.append(localObject[i].denominator);
          if (i + 1 != localObject.length) {
            paramByteOrder.append(",");
          }
          i += 1;
        }
        return paramByteOrder.toString();
      }
      return null;
    }
    
    public int size()
    {
      return ExifInterface.-get2()[this.format] * this.numberOfComponents;
    }
    
    public String toString()
    {
      return "(" + ExifInterface.-get3()[this.format] + ", data length:" + this.bytes.length + ")";
    }
  }
  
  private static class ExifTag
  {
    public final String name;
    public final int number;
    public final int primaryFormat;
    public final int secondaryFormat;
    
    private ExifTag(String paramString, int paramInt1, int paramInt2)
    {
      this.name = paramString;
      this.number = paramInt1;
      this.primaryFormat = paramInt2;
      this.secondaryFormat = -1;
    }
    
    private ExifTag(String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      this.name = paramString;
      this.number = paramInt1;
      this.primaryFormat = paramInt2;
      this.secondaryFormat = paramInt3;
    }
  }
  
  private static class Rational
  {
    public final long denominator;
    public final long numerator;
    
    private Rational(long paramLong1, long paramLong2)
    {
      if (paramLong2 == 0L)
      {
        this.numerator = 0L;
        this.denominator = 1L;
        return;
      }
      this.numerator = paramLong1;
      this.denominator = paramLong2;
    }
    
    public double calculate()
    {
      return this.numerator / this.denominator;
    }
    
    public String toString()
    {
      return this.numerator + "/" + this.denominator;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/ExifInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */