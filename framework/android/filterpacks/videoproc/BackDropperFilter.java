package android.filterpacks.videoproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLEnvironment;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import java.util.Arrays;
import java.util.List;

public class BackDropperFilter
  extends Filter
{
  private static final float DEFAULT_ACCEPT_STDDEV = 0.85F;
  private static final float DEFAULT_ADAPT_RATE_BG = 0.0F;
  private static final float DEFAULT_ADAPT_RATE_FG = 0.0F;
  private static final String DEFAULT_AUTO_WB_SCALE = "0.25";
  private static final float[] DEFAULT_BG_FIT_TRANSFORM = { 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F };
  private static final float DEFAULT_EXPOSURE_CHANGE = 1.0F;
  private static final int DEFAULT_HIER_LRG_EXPONENT = 3;
  private static final float DEFAULT_HIER_LRG_SCALE = 0.7F;
  private static final int DEFAULT_HIER_MID_EXPONENT = 2;
  private static final float DEFAULT_HIER_MID_SCALE = 0.6F;
  private static final int DEFAULT_HIER_SML_EXPONENT = 0;
  private static final float DEFAULT_HIER_SML_SCALE = 0.5F;
  private static final float DEFAULT_LEARNING_ADAPT_RATE = 0.2F;
  private static final int DEFAULT_LEARNING_DONE_THRESHOLD = 20;
  private static final int DEFAULT_LEARNING_DURATION = 40;
  private static final int DEFAULT_LEARNING_VERIFY_DURATION = 10;
  private static final float DEFAULT_MASK_BLEND_BG = 0.65F;
  private static final float DEFAULT_MASK_BLEND_FG = 0.95F;
  private static final int DEFAULT_MASK_HEIGHT_EXPONENT = 8;
  private static final float DEFAULT_MASK_VERIFY_RATE = 0.25F;
  private static final int DEFAULT_MASK_WIDTH_EXPONENT = 8;
  private static final float DEFAULT_UV_SCALE_FACTOR = 1.35F;
  private static final float DEFAULT_WHITE_BALANCE_BLUE_CHANGE = 0.0F;
  private static final float DEFAULT_WHITE_BALANCE_RED_CHANGE = 0.0F;
  private static final int DEFAULT_WHITE_BALANCE_TOGGLE = 0;
  private static final float DEFAULT_Y_SCALE_FACTOR = 0.4F;
  private static final String DISTANCE_STORAGE_SCALE = "0.6";
  private static final String MASK_SMOOTH_EXPONENT = "2.0";
  private static final String MIN_VARIANCE = "3.0";
  private static final String RGB_TO_YUV_MATRIX = "0.299, -0.168736,  0.5,      0.000, 0.587, -0.331264, -0.418688, 0.000, 0.114,  0.5,      -0.081312, 0.000, 0.000,  0.5,       0.5,      1.000 ";
  private static final String TAG = "BackDropperFilter";
  private static final String VARIANCE_STORAGE_SCALE = "5.0";
  private static final String mAutomaticWhiteBalance = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float pyramid_depth;\nuniform bool autowb_toggle;\nvarying vec2 v_texcoord;\nvoid main() {\n   vec4 mean_video = texture2D(tex_sampler_0, v_texcoord, pyramid_depth);\n   vec4 mean_bg = texture2D(tex_sampler_1, v_texcoord, pyramid_depth);\n   float green_normalizer = mean_video.g / mean_bg.g;\n   vec4 adjusted_value = vec4(mean_bg.r / mean_video.r * green_normalizer, 1., \n                         mean_bg.b / mean_video.b * green_normalizer, 1.) * auto_wb_scale; \n   gl_FragColor = autowb_toggle ? adjusted_value : vec4(auto_wb_scale);\n}\n";
  private static final String mBgDistanceShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n\n  float dist_y = gauss_dist_y(fg.r, mean.r, variance.r);\n  float dist_uv = gauss_dist_uv(fg.gb, mean.gb, variance.gb);\n  gl_FragColor = vec4(0.5*fg.rg, dist_scale*dist_y, dist_scale*dist_uv);\n}\n";
  private static final String mBgMaskShader = "uniform sampler2D tex_sampler_0;\nuniform float accept_variance;\nuniform vec2 yuv_weights;\nuniform float scale_lrg;\nuniform float scale_mid;\nuniform float scale_sml;\nuniform float exp_lrg;\nuniform float exp_mid;\nuniform float exp_sml;\nvarying vec2 v_texcoord;\nbool is_fg(vec2 dist_yc, float accept_variance) {\n  return ( dot(yuv_weights, dist_yc) >= accept_variance );\n}\nvoid main() {\n  vec4 dist_lrg_sc = texture2D(tex_sampler_0, v_texcoord, exp_lrg);\n  vec4 dist_mid_sc = texture2D(tex_sampler_0, v_texcoord, exp_mid);\n  vec4 dist_sml_sc = texture2D(tex_sampler_0, v_texcoord, exp_sml);\n  vec2 dist_lrg = inv_dist_scale * dist_lrg_sc.ba;\n  vec2 dist_mid = inv_dist_scale * dist_mid_sc.ba;\n  vec2 dist_sml = inv_dist_scale * dist_sml_sc.ba;\n  vec2 norm_dist = 0.75 * dist_sml / accept_variance;\n  bool is_fg_lrg = is_fg(dist_lrg, accept_variance * scale_lrg);\n  bool is_fg_mid = is_fg_lrg || is_fg(dist_mid, accept_variance * scale_mid);\n  float is_fg_sml =\n      float(is_fg_mid || is_fg(dist_sml, accept_variance * scale_sml));\n  float alpha = 0.5 * is_fg_sml + 0.3 * float(is_fg_mid) + 0.2 * float(is_fg_lrg);\n  gl_FragColor = vec4(alpha, norm_dist, is_fg_sml);\n}\n";
  private static final String mBgSubtractForceShader = "  vec4 ghost_rgb = (fg_adjusted * 0.7 + vec4(0.3,0.3,0.4,0.))*0.65 + \n                   0.35*bg_rgb;\n  float glow_start = 0.75 * mask_blend_bg; \n  float glow_max   = mask_blend_bg; \n  gl_FragColor = mask.a < glow_start ? bg_rgb : \n                 mask.a < glow_max ? mix(bg_rgb, vec4(0.9,0.9,1.0,1.0), \n                                     (mask.a - glow_start) / (glow_max - glow_start) ) : \n                 mask.a < mask_blend_fg ? mix(vec4(0.9,0.9,1.0,1.0), ghost_rgb, \n                                    (mask.a - glow_max) / (mask_blend_fg - glow_max) ) : \n                 ghost_rgb;\n}\n";
  private static final String mBgSubtractShader = "uniform mat3 bg_fit_transform;\nuniform float mask_blend_bg;\nuniform float mask_blend_fg;\nuniform float exposure_change;\nuniform float whitebalancered_change;\nuniform float whitebalanceblue_change;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec2 bg_texcoord = (bg_fit_transform * vec3(v_texcoord, 1.)).xy;\n  vec4 bg_rgb = texture2D(tex_sampler_1, bg_texcoord);\n  vec4 wb_auto_scale = texture2D(tex_sampler_3, v_texcoord) * exposure_change / auto_wb_scale;\n  vec4 wb_manual_scale = vec4(1. + whitebalancered_change, 1., 1. + whitebalanceblue_change, 1.);\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord);\n  vec4 fg_adjusted = fg_rgb * wb_manual_scale * wb_auto_scale;\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n  float alpha = smoothstep(mask_blend_bg, mask_blend_fg, mask.a);\n  gl_FragColor = mix(bg_rgb, fg_adjusted, alpha);\n";
  private static final String[] mDebugOutputNames = { "debug1", "debug2" };
  private static final String[] mInputNames = { "video", "background" };
  private static final String mMaskVerifyShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float verify_rate;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 lastmask = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  float newmask = mix(lastmask.a, mask.a, verify_rate);\n  gl_FragColor = vec4(0., 0., 0., newmask);\n}\n";
  private static final String[] mOutputNames = { "video" };
  private static String mSharedUtilShader = "precision mediump float;\nuniform float fg_adapt_rate;\nuniform float bg_adapt_rate;\nconst mat4 coeff_yuv = mat4(0.299, -0.168736,  0.5,      0.000, 0.587, -0.331264, -0.418688, 0.000, 0.114,  0.5,      -0.081312, 0.000, 0.000,  0.5,       0.5,      1.000 );\nconst float dist_scale = 0.6;\nconst float inv_dist_scale = 1. / dist_scale;\nconst float var_scale=5.0;\nconst float inv_var_scale = 1. / var_scale;\nconst float min_variance = inv_var_scale *3.0/ 256.;\nconst float auto_wb_scale = 0.25;\n\nfloat gauss_dist_y(float y, float mean, float variance) {\n  float dist = (y - mean) * (y - mean) / variance;\n  return dist;\n}\nfloat gauss_dist_uv(vec2 uv, vec2 mean, vec2 variance) {\n  vec2 dist = (uv - mean) * (uv - mean) / variance;\n  return dist.r + dist.g;\n}\nfloat local_adapt_rate(float alpha) {\n  return mix(bg_adapt_rate, fg_adapt_rate, alpha);\n}\n\n";
  private static final String mUpdateBgModelMeanShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 new_mean = mix(mean, fg, alpha);\n  gl_FragColor = new_mean;\n}\n";
  private static final String mUpdateBgModelVarianceShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_3, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 cur_variance = (fg-mean)*(fg-mean);\n  vec4 new_variance = mix(variance, cur_variance, alpha);\n  new_variance = max(new_variance, vec4(min_variance));\n  gl_FragColor = var_scale * new_variance;\n}\n";
  private final int BACKGROUND_FILL_CROP = 2;
  private final int BACKGROUND_FIT = 1;
  private final int BACKGROUND_STRETCH = 0;
  private ShaderProgram copyShaderProgram;
  private boolean isOpen;
  @GenerateFieldPort(hasDefault=true, name="acceptStddev")
  private float mAcceptStddev = 0.85F;
  @GenerateFieldPort(hasDefault=true, name="adaptRateBg")
  private float mAdaptRateBg = 0.0F;
  @GenerateFieldPort(hasDefault=true, name="adaptRateFg")
  private float mAdaptRateFg = 0.0F;
  @GenerateFieldPort(hasDefault=true, name="learningAdaptRate")
  private float mAdaptRateLearning = 0.2F;
  private GLFrame mAutoWB;
  @GenerateFieldPort(hasDefault=true, name="autowbToggle")
  private int mAutoWBToggle = 0;
  private ShaderProgram mAutomaticWhiteBalanceProgram;
  private MutableFrameFormat mAverageFormat;
  @GenerateFieldPort(hasDefault=true, name="backgroundFitMode")
  private int mBackgroundFitMode = 2;
  private boolean mBackgroundFitModeChanged;
  private ShaderProgram mBgDistProgram;
  private GLFrame mBgInput;
  private ShaderProgram mBgMaskProgram;
  private GLFrame[] mBgMean;
  private ShaderProgram mBgSubtractProgram;
  private ShaderProgram mBgUpdateMeanProgram;
  private ShaderProgram mBgUpdateVarianceProgram;
  private GLFrame[] mBgVariance;
  @GenerateFieldPort(hasDefault=true, name="chromaScale")
  private float mChromaScale = 1.35F;
  private ShaderProgram mCopyOutProgram;
  private GLFrame mDistance;
  @GenerateFieldPort(hasDefault=true, name="exposureChange")
  private float mExposureChange = 1.0F;
  private int mFrameCount;
  @GenerateFieldPort(hasDefault=true, name="hierLrgExp")
  private int mHierarchyLrgExp = 3;
  @GenerateFieldPort(hasDefault=true, name="hierLrgScale")
  private float mHierarchyLrgScale = 0.7F;
  @GenerateFieldPort(hasDefault=true, name="hierMidExp")
  private int mHierarchyMidExp = 2;
  @GenerateFieldPort(hasDefault=true, name="hierMidScale")
  private float mHierarchyMidScale = 0.6F;
  @GenerateFieldPort(hasDefault=true, name="hierSmlExp")
  private int mHierarchySmlExp = 0;
  @GenerateFieldPort(hasDefault=true, name="hierSmlScale")
  private float mHierarchySmlScale = 0.5F;
  @GenerateFieldPort(hasDefault=true, name="learningDoneListener")
  private LearningDoneListener mLearningDoneListener = null;
  @GenerateFieldPort(hasDefault=true, name="learningDuration")
  private int mLearningDuration = 40;
  @GenerateFieldPort(hasDefault=true, name="learningVerifyDuration")
  private int mLearningVerifyDuration = 10;
  private final boolean mLogVerbose = Log.isLoggable("BackDropperFilter", 2);
  @GenerateFieldPort(hasDefault=true, name="lumScale")
  private float mLumScale = 0.4F;
  private GLFrame mMask;
  private GLFrame mMaskAverage;
  @GenerateFieldPort(hasDefault=true, name="maskBg")
  private float mMaskBg = 0.65F;
  @GenerateFieldPort(hasDefault=true, name="maskFg")
  private float mMaskFg = 0.95F;
  private MutableFrameFormat mMaskFormat;
  @GenerateFieldPort(hasDefault=true, name="maskHeightExp")
  private int mMaskHeightExp = 8;
  private GLFrame[] mMaskVerify;
  private ShaderProgram mMaskVerifyProgram;
  @GenerateFieldPort(hasDefault=true, name="maskWidthExp")
  private int mMaskWidthExp = 8;
  private MutableFrameFormat mMemoryFormat;
  @GenerateFieldPort(hasDefault=true, name="mirrorBg")
  private boolean mMirrorBg = false;
  @GenerateFieldPort(hasDefault=true, name="orientation")
  private int mOrientation = 0;
  private FrameFormat mOutputFormat;
  private boolean mPingPong;
  @GenerateFinalPort(hasDefault=true, name="provideDebugOutputs")
  private boolean mProvideDebugOutputs = false;
  private int mPyramidDepth;
  private float mRelativeAspect;
  private boolean mStartLearning;
  private int mSubsampleLevel;
  @GenerateFieldPort(hasDefault=true, name="useTheForce")
  private boolean mUseTheForce = false;
  @GenerateFieldPort(hasDefault=true, name="maskVerifyRate")
  private float mVerifyRate = 0.25F;
  private GLFrame mVideoInput;
  @GenerateFieldPort(hasDefault=true, name="whitebalanceblueChange")
  private float mWhiteBalanceBlueChange = 0.0F;
  @GenerateFieldPort(hasDefault=true, name="whitebalanceredChange")
  private float mWhiteBalanceRedChange = 0.0F;
  private long startTime = -1L;
  
  public BackDropperFilter(String paramString)
  {
    super(paramString);
    paramString = SystemProperties.get("ro.media.effect.bgdropper.adj");
    if (paramString.length() > 0) {}
    try
    {
      this.mAcceptStddev += Float.parseFloat(paramString);
      if (this.mLogVerbose) {
        Log.v("BackDropperFilter", "Adjusting accept threshold by " + paramString + ", now " + this.mAcceptStddev);
      }
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.e("BackDropperFilter", "Badly formatted property ro.media.effect.bgdropper.adj: " + paramString);
    }
  }
  
  private void allocateFrames(FrameFormat paramFrameFormat, FilterContext paramFilterContext)
  {
    if (!createMemoryFormat(paramFrameFormat)) {
      return;
    }
    if (this.mLogVerbose) {
      Log.v("BackDropperFilter", "Allocating BackDropperFilter frames");
    }
    int j = this.mMaskFormat.getSize();
    paramFrameFormat = new byte[j];
    byte[] arrayOfByte1 = new byte[j];
    byte[] arrayOfByte2 = new byte[j];
    int i = 0;
    while (i < j)
    {
      paramFrameFormat[i] = -128;
      arrayOfByte1[i] = 10;
      arrayOfByte2[i] = 0;
      i += 1;
    }
    i = 0;
    while (i < 2)
    {
      this.mBgMean[i] = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mMaskFormat));
      this.mBgMean[i].setData(paramFrameFormat, 0, j);
      this.mBgVariance[i] = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mMaskFormat));
      this.mBgVariance[i].setData(arrayOfByte1, 0, j);
      this.mMaskVerify[i] = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mMaskFormat));
      this.mMaskVerify[i].setData(arrayOfByte2, 0, j);
      i += 1;
    }
    if (this.mLogVerbose) {
      Log.v("BackDropperFilter", "Done allocating texture for Mean and Variance objects!");
    }
    this.mDistance = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mMaskFormat));
    this.mMask = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mMaskFormat));
    this.mAutoWB = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mAverageFormat));
    this.mVideoInput = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mMemoryFormat));
    this.mBgInput = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mMemoryFormat));
    this.mMaskAverage = ((GLFrame)paramFilterContext.getFrameManager().newFrame(this.mAverageFormat));
    this.mBgDistProgram = new ShaderProgram(paramFilterContext, mSharedUtilShader + "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n\n  float dist_y = gauss_dist_y(fg.r, mean.r, variance.r);\n  float dist_uv = gauss_dist_uv(fg.gb, mean.gb, variance.gb);\n  gl_FragColor = vec4(0.5*fg.rg, dist_scale*dist_y, dist_scale*dist_uv);\n}\n");
    this.mBgDistProgram.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
    this.mBgMaskProgram = new ShaderProgram(paramFilterContext, mSharedUtilShader + "uniform sampler2D tex_sampler_0;\nuniform float accept_variance;\nuniform vec2 yuv_weights;\nuniform float scale_lrg;\nuniform float scale_mid;\nuniform float scale_sml;\nuniform float exp_lrg;\nuniform float exp_mid;\nuniform float exp_sml;\nvarying vec2 v_texcoord;\nbool is_fg(vec2 dist_yc, float accept_variance) {\n  return ( dot(yuv_weights, dist_yc) >= accept_variance );\n}\nvoid main() {\n  vec4 dist_lrg_sc = texture2D(tex_sampler_0, v_texcoord, exp_lrg);\n  vec4 dist_mid_sc = texture2D(tex_sampler_0, v_texcoord, exp_mid);\n  vec4 dist_sml_sc = texture2D(tex_sampler_0, v_texcoord, exp_sml);\n  vec2 dist_lrg = inv_dist_scale * dist_lrg_sc.ba;\n  vec2 dist_mid = inv_dist_scale * dist_mid_sc.ba;\n  vec2 dist_sml = inv_dist_scale * dist_sml_sc.ba;\n  vec2 norm_dist = 0.75 * dist_sml / accept_variance;\n  bool is_fg_lrg = is_fg(dist_lrg, accept_variance * scale_lrg);\n  bool is_fg_mid = is_fg_lrg || is_fg(dist_mid, accept_variance * scale_mid);\n  float is_fg_sml =\n      float(is_fg_mid || is_fg(dist_sml, accept_variance * scale_sml));\n  float alpha = 0.5 * is_fg_sml + 0.3 * float(is_fg_mid) + 0.2 * float(is_fg_lrg);\n  gl_FragColor = vec4(alpha, norm_dist, is_fg_sml);\n}\n");
    this.mBgMaskProgram.setHostValue("accept_variance", Float.valueOf(this.mAcceptStddev * this.mAcceptStddev));
    float f1 = this.mLumScale;
    float f2 = this.mChromaScale;
    this.mBgMaskProgram.setHostValue("yuv_weights", new float[] { f1, f2 });
    this.mBgMaskProgram.setHostValue("scale_lrg", Float.valueOf(this.mHierarchyLrgScale));
    this.mBgMaskProgram.setHostValue("scale_mid", Float.valueOf(this.mHierarchyMidScale));
    this.mBgMaskProgram.setHostValue("scale_sml", Float.valueOf(this.mHierarchySmlScale));
    this.mBgMaskProgram.setHostValue("exp_lrg", Float.valueOf(this.mSubsampleLevel + this.mHierarchyLrgExp));
    this.mBgMaskProgram.setHostValue("exp_mid", Float.valueOf(this.mSubsampleLevel + this.mHierarchyMidExp));
    this.mBgMaskProgram.setHostValue("exp_sml", Float.valueOf(this.mSubsampleLevel + this.mHierarchySmlExp));
    if (this.mUseTheForce) {}
    for (this.mBgSubtractProgram = new ShaderProgram(paramFilterContext, mSharedUtilShader + "uniform mat3 bg_fit_transform;\nuniform float mask_blend_bg;\nuniform float mask_blend_fg;\nuniform float exposure_change;\nuniform float whitebalancered_change;\nuniform float whitebalanceblue_change;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec2 bg_texcoord = (bg_fit_transform * vec3(v_texcoord, 1.)).xy;\n  vec4 bg_rgb = texture2D(tex_sampler_1, bg_texcoord);\n  vec4 wb_auto_scale = texture2D(tex_sampler_3, v_texcoord) * exposure_change / auto_wb_scale;\n  vec4 wb_manual_scale = vec4(1. + whitebalancered_change, 1., 1. + whitebalanceblue_change, 1.);\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord);\n  vec4 fg_adjusted = fg_rgb * wb_manual_scale * wb_auto_scale;\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n  float alpha = smoothstep(mask_blend_bg, mask_blend_fg, mask.a);\n  gl_FragColor = mix(bg_rgb, fg_adjusted, alpha);\n" + "  vec4 ghost_rgb = (fg_adjusted * 0.7 + vec4(0.3,0.3,0.4,0.))*0.65 + \n                   0.35*bg_rgb;\n  float glow_start = 0.75 * mask_blend_bg; \n  float glow_max   = mask_blend_bg; \n  gl_FragColor = mask.a < glow_start ? bg_rgb : \n                 mask.a < glow_max ? mix(bg_rgb, vec4(0.9,0.9,1.0,1.0), \n                                     (mask.a - glow_start) / (glow_max - glow_start) ) : \n                 mask.a < mask_blend_fg ? mix(vec4(0.9,0.9,1.0,1.0), ghost_rgb, \n                                    (mask.a - glow_max) / (mask_blend_fg - glow_max) ) : \n                 ghost_rgb;\n}\n");; this.mBgSubtractProgram = new ShaderProgram(paramFilterContext, mSharedUtilShader + "uniform mat3 bg_fit_transform;\nuniform float mask_blend_bg;\nuniform float mask_blend_fg;\nuniform float exposure_change;\nuniform float whitebalancered_change;\nuniform float whitebalanceblue_change;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec2 bg_texcoord = (bg_fit_transform * vec3(v_texcoord, 1.)).xy;\n  vec4 bg_rgb = texture2D(tex_sampler_1, bg_texcoord);\n  vec4 wb_auto_scale = texture2D(tex_sampler_3, v_texcoord) * exposure_change / auto_wb_scale;\n  vec4 wb_manual_scale = vec4(1. + whitebalancered_change, 1., 1. + whitebalanceblue_change, 1.);\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord);\n  vec4 fg_adjusted = fg_rgb * wb_manual_scale * wb_auto_scale;\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n  float alpha = smoothstep(mask_blend_bg, mask_blend_fg, mask.a);\n  gl_FragColor = mix(bg_rgb, fg_adjusted, alpha);\n" + "}\n"))
    {
      this.mBgSubtractProgram.setHostValue("bg_fit_transform", DEFAULT_BG_FIT_TRANSFORM);
      this.mBgSubtractProgram.setHostValue("mask_blend_bg", Float.valueOf(this.mMaskBg));
      this.mBgSubtractProgram.setHostValue("mask_blend_fg", Float.valueOf(this.mMaskFg));
      this.mBgSubtractProgram.setHostValue("exposure_change", Float.valueOf(this.mExposureChange));
      this.mBgSubtractProgram.setHostValue("whitebalanceblue_change", Float.valueOf(this.mWhiteBalanceBlueChange));
      this.mBgSubtractProgram.setHostValue("whitebalancered_change", Float.valueOf(this.mWhiteBalanceRedChange));
      this.mBgUpdateMeanProgram = new ShaderProgram(paramFilterContext, mSharedUtilShader + "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 new_mean = mix(mean, fg, alpha);\n  gl_FragColor = new_mean;\n}\n");
      this.mBgUpdateMeanProgram.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
      this.mBgUpdateVarianceProgram = new ShaderProgram(paramFilterContext, mSharedUtilShader + "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_3, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 cur_variance = (fg-mean)*(fg-mean);\n  vec4 new_variance = mix(variance, cur_variance, alpha);\n  new_variance = max(new_variance, vec4(min_variance));\n  gl_FragColor = var_scale * new_variance;\n}\n");
      this.mBgUpdateVarianceProgram.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
      this.mCopyOutProgram = ShaderProgram.createIdentity(paramFilterContext);
      this.mAutomaticWhiteBalanceProgram = new ShaderProgram(paramFilterContext, mSharedUtilShader + "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float pyramid_depth;\nuniform bool autowb_toggle;\nvarying vec2 v_texcoord;\nvoid main() {\n   vec4 mean_video = texture2D(tex_sampler_0, v_texcoord, pyramid_depth);\n   vec4 mean_bg = texture2D(tex_sampler_1, v_texcoord, pyramid_depth);\n   float green_normalizer = mean_video.g / mean_bg.g;\n   vec4 adjusted_value = vec4(mean_bg.r / mean_video.r * green_normalizer, 1., \n                         mean_bg.b / mean_video.b * green_normalizer, 1.) * auto_wb_scale; \n   gl_FragColor = autowb_toggle ? adjusted_value : vec4(auto_wb_scale);\n}\n");
      this.mAutomaticWhiteBalanceProgram.setHostValue("pyramid_depth", Float.valueOf(this.mPyramidDepth));
      this.mAutomaticWhiteBalanceProgram.setHostValue("autowb_toggle", Integer.valueOf(this.mAutoWBToggle));
      this.mMaskVerifyProgram = new ShaderProgram(paramFilterContext, mSharedUtilShader + "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float verify_rate;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 lastmask = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  float newmask = mix(lastmask.a, mask.a, verify_rate);\n  gl_FragColor = vec4(0., 0., 0., newmask);\n}\n");
      this.mMaskVerifyProgram.setHostValue("verify_rate", Float.valueOf(this.mVerifyRate));
      if (this.mLogVerbose) {
        Log.v("BackDropperFilter", "Shader width set to " + this.mMemoryFormat.getWidth());
      }
      this.mRelativeAspect = 1.0F;
      this.mFrameCount = 0;
      this.mStartLearning = true;
      return;
    }
  }
  
  private boolean createMemoryFormat(FrameFormat paramFrameFormat)
  {
    if (this.mMemoryFormat != null) {
      return false;
    }
    if ((paramFrameFormat.getWidth() == 0) || (paramFrameFormat.getHeight() == 0)) {
      throw new RuntimeException("Attempting to process input frame with unknown size");
    }
    this.mMaskFormat = paramFrameFormat.mutableCopy();
    int i = (int)Math.pow(2.0D, this.mMaskWidthExp);
    int j = (int)Math.pow(2.0D, this.mMaskHeightExp);
    this.mMaskFormat.setDimensions(i, j);
    this.mPyramidDepth = Math.max(this.mMaskWidthExp, this.mMaskHeightExp);
    this.mMemoryFormat = this.mMaskFormat.mutableCopy();
    int k = Math.max(this.mMaskWidthExp, pyramidLevel(paramFrameFormat.getWidth()));
    int m = Math.max(this.mMaskHeightExp, pyramidLevel(paramFrameFormat.getHeight()));
    this.mPyramidDepth = Math.max(k, m);
    int n = Math.max(i, (int)Math.pow(2.0D, k));
    int i1 = Math.max(j, (int)Math.pow(2.0D, m));
    this.mMemoryFormat.setDimensions(n, i1);
    this.mSubsampleLevel = (this.mPyramidDepth - Math.max(this.mMaskWidthExp, this.mMaskHeightExp));
    if (this.mLogVerbose)
    {
      Log.v("BackDropperFilter", "Mask frames size " + i + " x " + j);
      Log.v("BackDropperFilter", "Pyramid levels " + k + " x " + m);
      Log.v("BackDropperFilter", "Memory frames size " + n + " x " + i1);
    }
    this.mAverageFormat = paramFrameFormat.mutableCopy();
    this.mAverageFormat.setDimensions(1, 1);
    return true;
  }
  
  private int pyramidLevel(int paramInt)
  {
    return (int)Math.floor(Math.log10(paramInt) / Math.log10(2.0D)) - 1;
  }
  
  private void updateBgScaling(Frame paramFrame1, Frame paramFrame2, boolean paramBoolean)
  {
    float f1 = paramFrame1.getFormat().getWidth() / paramFrame1.getFormat().getHeight() / (paramFrame2.getFormat().getWidth() / paramFrame2.getFormat().getHeight());
    float f5;
    float f6;
    float f7;
    float f8;
    float f2;
    float f3;
    float f4;
    if ((f1 != this.mRelativeAspect) || (paramBoolean))
    {
      this.mRelativeAspect = f1;
      f5 = 0.0F;
      f6 = 1.0F;
      f7 = 0.0F;
      f8 = 1.0F;
      f1 = f5;
      f2 = f6;
      f3 = f7;
      f4 = f8;
      switch (this.mBackgroundFitMode)
      {
      default: 
        f4 = f8;
        f3 = f7;
        f2 = f6;
        f1 = f5;
      case 0: 
        f7 = f1;
        f5 = f2;
        f6 = f3;
        f8 = f4;
        if (this.mMirrorBg)
        {
          if (this.mLogVerbose) {
            Log.v("BackDropperFilter", "Mirroring the background!");
          }
          if ((this.mOrientation != 0) && (this.mOrientation != 180)) {
            break label485;
          }
          f5 = -f2;
          f7 = 1.0F - f1;
          f8 = f4;
          f6 = f3;
        }
        break;
      }
    }
    for (;;)
    {
      if (this.mLogVerbose) {
        Log.v("BackDropperFilter", "bgTransform: xMin, yMin, xWidth, yWidth : " + f7 + ", " + f6 + ", " + f5 + ", " + f8 + ", mRelAspRatio = " + this.mRelativeAspect);
      }
      this.mBgSubtractProgram.setHostValue("bg_fit_transform", new float[] { f5, 0.0F, 0.0F, 0.0F, f8, 0.0F, f7, f6, 1.0F });
      return;
      if (this.mRelativeAspect > 1.0F)
      {
        f1 = 0.5F - this.mRelativeAspect * 0.5F;
        f2 = 1.0F * this.mRelativeAspect;
        f3 = f7;
        f4 = f8;
        break;
      }
      f3 = 0.5F - 0.5F / this.mRelativeAspect;
      f4 = 1.0F / this.mRelativeAspect;
      f1 = f5;
      f2 = f6;
      break;
      if (this.mRelativeAspect > 1.0F)
      {
        f3 = 0.5F - 0.5F / this.mRelativeAspect;
        f4 = 1.0F / this.mRelativeAspect;
        f1 = f5;
        f2 = f6;
        break;
      }
      f1 = 0.5F - this.mRelativeAspect * 0.5F;
      f2 = this.mRelativeAspect;
      f3 = f7;
      f4 = f8;
      break;
      label485:
      f8 = -f4;
      f6 = 1.0F - f3;
      f7 = f1;
      f5 = f2;
    }
  }
  
  public void close(FilterContext paramFilterContext)
  {
    if (this.mMemoryFormat == null) {
      return;
    }
    if (this.mLogVerbose) {
      Log.v("BackDropperFilter", "Filter Closing!");
    }
    int i = 0;
    while (i < 2)
    {
      this.mBgMean[i].release();
      this.mBgVariance[i].release();
      this.mMaskVerify[i].release();
      i += 1;
    }
    this.mDistance.release();
    this.mMask.release();
    this.mAutoWB.release();
    this.mVideoInput.release();
    this.mBgInput.release();
    this.mMaskAverage.release();
    this.mMemoryFormat = null;
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (paramString.equals("backgroundFitMode")) {
      this.mBackgroundFitModeChanged = true;
    }
    do
    {
      return;
      if (paramString.equals("acceptStddev"))
      {
        this.mBgMaskProgram.setHostValue("accept_variance", Float.valueOf(this.mAcceptStddev * this.mAcceptStddev));
        return;
      }
      if (paramString.equals("hierLrgScale"))
      {
        this.mBgMaskProgram.setHostValue("scale_lrg", Float.valueOf(this.mHierarchyLrgScale));
        return;
      }
      if (paramString.equals("hierMidScale"))
      {
        this.mBgMaskProgram.setHostValue("scale_mid", Float.valueOf(this.mHierarchyMidScale));
        return;
      }
      if (paramString.equals("hierSmlScale"))
      {
        this.mBgMaskProgram.setHostValue("scale_sml", Float.valueOf(this.mHierarchySmlScale));
        return;
      }
      if (paramString.equals("hierLrgExp"))
      {
        this.mBgMaskProgram.setHostValue("exp_lrg", Float.valueOf(this.mSubsampleLevel + this.mHierarchyLrgExp));
        return;
      }
      if (paramString.equals("hierMidExp"))
      {
        this.mBgMaskProgram.setHostValue("exp_mid", Float.valueOf(this.mSubsampleLevel + this.mHierarchyMidExp));
        return;
      }
      if (paramString.equals("hierSmlExp"))
      {
        this.mBgMaskProgram.setHostValue("exp_sml", Float.valueOf(this.mSubsampleLevel + this.mHierarchySmlExp));
        return;
      }
      if ((paramString.equals("lumScale")) || (paramString.equals("chromaScale")))
      {
        float f1 = this.mLumScale;
        float f2 = this.mChromaScale;
        this.mBgMaskProgram.setHostValue("yuv_weights", new float[] { f1, f2 });
        return;
      }
      if (paramString.equals("maskBg"))
      {
        this.mBgSubtractProgram.setHostValue("mask_blend_bg", Float.valueOf(this.mMaskBg));
        return;
      }
      if (paramString.equals("maskFg"))
      {
        this.mBgSubtractProgram.setHostValue("mask_blend_fg", Float.valueOf(this.mMaskFg));
        return;
      }
      if (paramString.equals("exposureChange"))
      {
        this.mBgSubtractProgram.setHostValue("exposure_change", Float.valueOf(this.mExposureChange));
        return;
      }
      if (paramString.equals("whitebalanceredChange"))
      {
        this.mBgSubtractProgram.setHostValue("whitebalancered_change", Float.valueOf(this.mWhiteBalanceRedChange));
        return;
      }
      if (paramString.equals("whitebalanceblueChange"))
      {
        this.mBgSubtractProgram.setHostValue("whitebalanceblue_change", Float.valueOf(this.mWhiteBalanceBlueChange));
        return;
      }
    } while (!paramString.equals("autowbToggle"));
    this.mAutomaticWhiteBalanceProgram.setHostValue("autowb_toggle", Integer.valueOf(this.mAutoWBToggle));
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    paramFrameFormat = paramFrameFormat.mutableCopy();
    if (!Arrays.asList(mOutputNames).contains(paramString)) {
      paramFrameFormat.setDimensions(0, 0);
    }
    return paramFrameFormat;
  }
  
  public void prepare(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v("BackDropperFilter", "Preparing BackDropperFilter!");
    }
    this.mBgMean = new GLFrame[2];
    this.mBgVariance = new GLFrame[2];
    this.mMaskVerify = new GLFrame[2];
    this.copyShaderProgram = ShaderProgram.createIdentity(paramFilterContext);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("video");
    Object localObject1 = pullInput("background");
    allocateFrames(localFrame.getFormat(), paramFilterContext);
    if (this.mStartLearning)
    {
      if (this.mLogVerbose) {
        Log.v("BackDropperFilter", "Starting learning");
      }
      this.mBgUpdateMeanProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
      this.mBgUpdateMeanProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
      this.mBgUpdateVarianceProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
      this.mBgUpdateVarianceProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
      this.mFrameCount = 0;
    }
    int i;
    int j;
    label140:
    boolean bool;
    label150:
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    GLFrame localGLFrame;
    if (this.mPingPong)
    {
      i = 0;
      if (!this.mPingPong) {
        break label999;
      }
      j = 1;
      if (!this.mPingPong) {
        break label1004;
      }
      bool = false;
      this.mPingPong = bool;
      updateBgScaling(localFrame, (Frame)localObject1, this.mBackgroundFitModeChanged);
      this.mBackgroundFitModeChanged = false;
      this.copyShaderProgram.process(localFrame, this.mVideoInput);
      this.copyShaderProgram.process((Frame)localObject1, this.mBgInput);
      this.mVideoInput.generateMipMap();
      this.mVideoInput.setTextureParameter(10241, 9985);
      this.mBgInput.generateMipMap();
      this.mBgInput.setTextureParameter(10241, 9985);
      if (this.mStartLearning)
      {
        this.copyShaderProgram.process(this.mVideoInput, this.mBgMean[i]);
        this.mStartLearning = false;
      }
      localObject2 = this.mVideoInput;
      localObject3 = this.mBgMean[i];
      localObject4 = this.mBgVariance[i];
      localObject5 = this.mBgDistProgram;
      localGLFrame = this.mDistance;
      ((ShaderProgram)localObject5).process(new Frame[] { localObject2, localObject3, localObject4 }, localGLFrame);
      this.mDistance.generateMipMap();
      this.mDistance.setTextureParameter(10241, 9985);
      this.mBgMaskProgram.process(this.mDistance, this.mMask);
      this.mMask.generateMipMap();
      this.mMask.setTextureParameter(10241, 9985);
      localObject2 = this.mVideoInput;
      localObject3 = this.mBgInput;
      localObject4 = this.mAutomaticWhiteBalanceProgram;
      localObject5 = this.mAutoWB;
      ((ShaderProgram)localObject4).process(new Frame[] { localObject2, localObject3 }, (Frame)localObject5);
      if (this.mFrameCount > this.mLearningDuration) {
        break label1138;
      }
      pushOutput("video", localFrame);
      if (this.mFrameCount != this.mLearningDuration - this.mLearningVerifyDuration) {
        break label1010;
      }
      this.copyShaderProgram.process(this.mMask, this.mMaskVerify[j]);
      this.mBgUpdateMeanProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateBg));
      this.mBgUpdateMeanProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateFg));
      this.mBgUpdateVarianceProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateBg));
      this.mBgUpdateVarianceProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateFg));
      label548:
      if (this.mFrameCount == this.mLearningDuration)
      {
        this.copyShaderProgram.process(this.mMaskVerify[j], this.mMaskAverage);
        int k = this.mMaskAverage.getData().array()[3] & 0xFF;
        if (this.mLogVerbose) {
          Log.v("BackDropperFilter", String.format("Mask_average is %d, threshold is %d", new Object[] { Integer.valueOf(k), Integer.valueOf(20) }));
        }
        if (k < 20) {
          break label1102;
        }
        this.mStartLearning = true;
      }
      label645:
      if ((this.mFrameCount >= this.mLearningDuration - this.mLearningVerifyDuration) && (this.mAdaptRateBg <= 0.0D)) {
        break label1214;
      }
    }
    for (;;)
    {
      localObject1 = this.mVideoInput;
      localObject2 = this.mBgMean[i];
      localObject3 = this.mMask;
      localObject4 = this.mBgUpdateMeanProgram;
      localObject5 = this.mBgMean[j];
      ((ShaderProgram)localObject4).process(new Frame[] { localObject1, localObject2, localObject3 }, (Frame)localObject5);
      this.mBgMean[j].generateMipMap();
      this.mBgMean[j].setTextureParameter(10241, 9985);
      localObject1 = this.mVideoInput;
      localObject2 = this.mBgMean[i];
      localObject3 = this.mBgVariance[i];
      localObject4 = this.mMask;
      localObject5 = this.mBgUpdateVarianceProgram;
      localGLFrame = this.mBgVariance[j];
      ((ShaderProgram)localObject5).process(new Frame[] { localObject1, localObject2, localObject3, localObject4 }, localGLFrame);
      this.mBgVariance[j].generateMipMap();
      this.mBgVariance[j].setTextureParameter(10241, 9985);
      label999:
      label1004:
      label1010:
      label1102:
      label1138:
      label1214:
      do
      {
        if (this.mProvideDebugOutputs)
        {
          localObject1 = paramFilterContext.getFrameManager().newFrame(localFrame.getFormat());
          this.mCopyOutProgram.process(localFrame, (Frame)localObject1);
          pushOutput("debug1", (Frame)localObject1);
          ((Frame)localObject1).release();
          localFrame = paramFilterContext.getFrameManager().newFrame(this.mMemoryFormat);
          this.mCopyOutProgram.process(this.mMask, localFrame);
          pushOutput("debug2", localFrame);
          localFrame.release();
        }
        this.mFrameCount += 1;
        if ((this.mLogVerbose) && (this.mFrameCount % 30 == 0))
        {
          if (this.startTime != -1L) {
            break label1227;
          }
          paramFilterContext.getGLEnvironment().activate();
          GLES20.glFinish();
          this.startTime = SystemClock.elapsedRealtime();
        }
        return;
        i = 1;
        break;
        j = 0;
        break label140;
        bool = true;
        break label150;
        if (this.mFrameCount <= this.mLearningDuration - this.mLearningVerifyDuration) {
          break label548;
        }
        localObject1 = this.mMaskVerify[i];
        localObject2 = this.mMask;
        localObject3 = this.mMaskVerifyProgram;
        localObject4 = this.mMaskVerify[j];
        ((ShaderProgram)localObject3).process(new Frame[] { localObject1, localObject2 }, (Frame)localObject4);
        this.mMaskVerify[j].generateMipMap();
        this.mMaskVerify[j].setTextureParameter(10241, 9985);
        break label548;
        if (this.mLogVerbose) {
          Log.v("BackDropperFilter", "Learning done");
        }
        if (this.mLearningDoneListener == null) {
          break label645;
        }
        this.mLearningDoneListener.onLearningDone(this);
        break label645;
        localObject2 = paramFilterContext.getFrameManager().newFrame(localFrame.getFormat());
        localObject3 = this.mMask;
        localObject4 = this.mAutoWB;
        this.mBgSubtractProgram.process(new Frame[] { localFrame, localObject1, localObject3, localObject4 }, (Frame)localObject2);
        pushOutput("video", (Frame)localObject2);
        ((Frame)localObject2).release();
        break label645;
      } while (this.mAdaptRateFg <= 0.0D);
    }
    label1227:
    paramFilterContext.getGLEnvironment().activate();
    GLES20.glFinish();
    long l = SystemClock.elapsedRealtime();
    Log.v("BackDropperFilter", "Avg. frame duration: " + String.format("%.2f", new Object[] { Double.valueOf((l - this.startTime) / 30.0D) }) + " ms. Avg. fps: " + String.format("%.2f", new Object[] { Double.valueOf(1000.0D / ((l - this.startTime) / 30.0D)) }));
    this.startTime = l;
  }
  
  public void relearn()
  {
    try
    {
      this.mStartLearning = true;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setupPorts()
  {
    int j = 0;
    Object localObject = ImageFormat.create(3, 0);
    String[] arrayOfString = mInputNames;
    int k = arrayOfString.length;
    int i = 0;
    while (i < k)
    {
      addMaskedInputPort(arrayOfString[i], (FrameFormat)localObject);
      i += 1;
    }
    localObject = mOutputNames;
    k = localObject.length;
    i = 0;
    while (i < k)
    {
      addOutputBasedOnInput(localObject[i], "video");
      i += 1;
    }
    if (this.mProvideDebugOutputs)
    {
      localObject = mDebugOutputNames;
      k = localObject.length;
      i = j;
      while (i < k)
      {
        addOutputBasedOnInput(localObject[i], "video");
        i += 1;
      }
    }
  }
  
  public static abstract interface LearningDoneListener
  {
    public abstract void onLearningDone(BackDropperFilter paramBackDropperFilter);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/videoproc/BackDropperFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */