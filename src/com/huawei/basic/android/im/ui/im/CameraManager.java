package com.huawei.basic.android.im.ui.im;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * 管理相机的类 
 *
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-4-23]
 */
public class CameraManager
{
    /**
     * 指定的视频宽度
     */
    public static final int VIDEO_RECORD_FRAME_WIDTH = 320;
    
    /**
     * 指定的视频高度
     */
    public static final int VIDEO_RECORD_FRAME_HEIGHT = 240;
    
    private static final String TAG = "VideoRecord";
    
    private static final int VIDEO_RECORD_FPS = 10;
    
    private Camera mPreviewCamera;
    
    private int mCurrentCameraId;
    
    private SurfaceHolder mSurfaceHolder;
    
    private int cameraCounts = getNumberOfCameras();
    
    private int mVideoScreenWidth;
    
    private int mVideoScreenHeight;
    
    private int mCameraOrientation;
    
    private boolean mLandScreen;
    
    /**
     * 
     * 构造方法
     * @param landscreen 是否横屏
     */
    public CameraManager(boolean landscreen)
    {
        mLandScreen = landscreen;
    }

    /**
     * 设置SurfaceHolder
     * 
     * @param surfaceHolder SurfaceHolder
     */
    public void setSurfaceHolder(SurfaceHolder surfaceHolder)
    {
        mSurfaceHolder = surfaceHolder;
    }

    /**
     * 打开一个相机镜头
     */
    public void openVideo()
    {
        Logger.i(TAG, "openCamera");
        // 当摄像头未启动时,才开启,否则不开启
        if (mPreviewCamera == null)
        {
            mPreviewCamera = openCamera(mCurrentCameraId);
            Logger.d(TAG, "Opened Camera is:" + mPreviewCamera + ", the id="
                    + mCurrentCameraId);
            
            getCameraInfo(mCurrentCameraId);
            
            if (mPreviewCamera != null)
            {
                
                // 设置一下Camera参数
                setCameraParams(mPreviewCamera);
                //                if (mLandScreen)
                //                {
                //                    mSurfaceHolder.setFixedSize(mVideoScreenWidth,
                //                            mVideoScreenHeight);
                //                }
            }
        }
        
    }

    /**
     * 切换摄像头
     * 
     * @throws IOException IOException
     */
    public void switchCamera() throws IOException
    {
        
        // 切换到下一个摄像头ID, 如果已经到达最多个数, 则从第一个开始
        int newCameraId = (mCurrentCameraId + 1) % cameraCounts;
        if (newCameraId != mCurrentCameraId)
        {
            releaseCamera();
            mCurrentCameraId = newCameraId;
            openVideo();
            startPreview();
        }
    }

    /**
     * 设置回调
     * @param previewCallback PreviewCallback回调
     */
    public void setPreviewCallback(PreviewCallback previewCallback)
    {
        // 设置PreviewCallback
        mPreviewCamera.setPreviewCallback(previewCallback);
    }

    /**
     * 开始取景
     * @throws IOException IOException
     */
    public void startPreview() throws IOException
    {
        if (null == mPreviewCamera)
        {
            openVideo();
        }
        mPreviewCamera.setPreviewDisplay(mSurfaceHolder);
        mPreviewCamera.startPreview();
    }

    /**
     * 停止取景
     */
    public void stopPreview()
    {
        mPreviewCamera.stopPreview();
        setPreviewCallback(null);
    }

    /**
     * 释放摄像头
     */
    public void releaseCamera()
    {
        if (null != mPreviewCamera)
        {
            mPreviewCamera.setPreviewCallback(null);
            mPreviewCamera.release();
            mPreviewCamera = null;
        }
    }

    /**
     * 获取视频的宽度
     * @return int 视频的宽度
     */
    public int getVideoScreenWidth()
    {
        return mVideoScreenWidth;
    }
    
    /**
     * 获取视频的高度
     * @return int 视频的高度
     */
    public int getVideoScreenHeight()
    {
        return mVideoScreenHeight;
    }
    
    /**
     * 获取视频的角度
     * @return int 视频的角度
     */
    public int getCameraOrientation()
    {
        return mCameraOrientation;
    }
    
    /**
     * 是否有多个相机镜头
     * 
     * @return 是否有多个相机镜头
     */
    public boolean multiCamera()
    {
        return cameraCounts > 1;
    }

    private Camera openCamera(int cameraId)
    {
        Method myOpenCamera;
        Camera result = null;
        Object[] args = new Object[] { cameraId };
        Class<?>[] classes = new Class[] { int.class };
        try
        {
            Class<Camera> cameraCls = Camera.class;
            myOpenCamera = cameraCls.getMethod("open", classes);
            if (myOpenCamera != null)
            {
                result = (Camera) myOpenCamera.invoke(cameraCls, args);
            }
        }
        catch (Exception ex)
        {
            Logger.e(TAG, "openCamera failed!", ex);
        }
        
        if (result == null)
        {
            result = Camera.open();
        }
        
        return result;
    }

    private void getCameraInfo(int cameraId)
    {
        if (!mLandScreen)
        {
            try
            {
                // 获取CameraInfo类, 并创建 一个CameraInfo的实例
                Class<?> cameraInfoCls = Class.forName("android.hardware.Camera$CameraInfo");
                Object info = cameraInfoCls.newInstance();
                
                Method[] mtds = Camera.class.getDeclaredMethods();
                Method getCameraInfo = null;
                for (Method method : mtds)
                {
                    String name = method.getName();
                    if ("getCameraInfo".equals(name))
                    {
                        getCameraInfo = method;
                        break;
                    }
                }
                
                if (getCameraInfo != null)
                {
                    // 通过CameraInfo方法取得CameraInfo
                    getCameraInfo.invoke(Camera.class, new Object[] { cameraId,
                            info });
                    
                    Field rotatoin = info.getClass().getField("orientation");
                    mCameraOrientation = rotatoin.getInt(info);
                    Logger.d(TAG, "CameraOrietation = " + mCameraOrientation);
                }
            }
            catch (Exception ex)
            {
                Logger.d(TAG,
                        "getCameraInfo failed, the err msg is:" + ex.getCause());
            }
        }
        if (0 == mCameraOrientation)
        {
            mCameraOrientation = 90;
        }
    }

    private void setCameraParams(Camera camera)
    {
        // 设置录制参数
        Camera.Parameters p = camera.getParameters();
        
        // 设置与预设的FPS值最接近的值
        List<Integer> supportRates = p.getSupportedPreviewFrameRates();
        if (null != supportRates && !supportRates.isEmpty())
        {
            p.setPreviewFrameRate(getNearlyFrameRate(supportRates));
        }
        if (!mLandScreen)
        {
            try
            {
                Method setDisplayOrientation = Camera.class.getMethod("setDisplayOrientation",
                        new Class[] { int.class });
                setDisplayOrientation.invoke(camera, new Object[] { 90 });
            }
            catch (Exception ex)
            {
                Logger.d(TAG, "setDisplayOrientation failed, the err msg is:"
                        + ex.getCause());
                
                p.set("orientation", "portrait");
                
                p.set("rotation", 90);
                
            }
            p.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        }
        p.setPreviewSize(VIDEO_RECORD_FRAME_WIDTH, VIDEO_RECORD_FRAME_HEIGHT);
        //        List<Size> sizes = p.getSupportedPreviewSizes();
        //        for (Size size : sizes)
        //        {
        //            Logger.i(TAG, "size:" + size.width + "," + size.height);
        //        }
        //        Logger.i(TAG, "getPreviewFormat:" + p.getPreviewFormat());
        camera.setParameters(p);
        // 重新获取摄像头的预览尺寸,并保存下来
        Camera.Parameters newParams = camera.getParameters();
        // 重新保存宽高值,后面会使用到
        mVideoScreenWidth = newParams.getPreviewSize().width;
        mVideoScreenHeight = newParams.getPreviewSize().height;
        //        Logger.i(TAG, "getPreviewSize():" + mVideoScreenWidth + ","
        //                + mVideoScreenHeight);
    }

    /**
     * 获取系统摄像头个数
     * @param camera Camera实例
     * @return 获取到的个数
     */
    private int getNumberOfCameras()
    {
        Method myCameraMethod;
        Integer result = null;
        Object[] args = null;
        Class<?>[] classes = null;
        try
        {
            Class<Camera> cameraCls = Camera.class;
            myCameraMethod = cameraCls.getMethod("getNumberOfCameras", classes);
            if (myCameraMethod != null)
            {
                result = (Integer) myCameraMethod.invoke(cameraCls, args);
            }
        }
        catch (Exception ex)
        {
            Logger.e(TAG,
                    "getNumberOfCameras failed, the err msg is:"
                            + ex.getCause());
            return 1;
        }
        if (result == null)
        {
            return 1;
        }
        return result.intValue();
    }

    /**
     * 从List中获取与预设值最近的FPS参数
     * @param supportRates Camera支持的FPS列表
     * @return 最接近预设值的Camera支持的FPS值
     */
    private int getNearlyFrameRate(List<Integer> supportRates)
    {
        int rateListSize = supportRates.size();
        if (null == supportRates || 0 == rateListSize)
        {
            // 没有获取到正确的Camera参数，则直接使用预设值
            return VIDEO_RECORD_FPS;
        }
        
        int finalRate = supportRates.get(0);
        int distance = Math.abs(finalRate - VIDEO_RECORD_FPS);
        
        // 遍历整个列表，找出与预设值最接近的值
        for (int idx = 1; idx < rateListSize; idx++)
        {
            int tempRate = supportRates.get(idx);
            int tempDistance = Math.abs(tempRate - VIDEO_RECORD_FPS);
            
            Logger.i(TAG, "Rate = " + tempRate + ", Distance = " + tempDistance);
            if (tempDistance < distance)
            {
                finalRate = tempRate;
                distance = tempDistance;
            }
            
            if (0 == distance)
            {
                // 如果当前已经有最接近的值了，则直接退出循环
                break;
            }
        }
        
        return finalRate;
    }
}
