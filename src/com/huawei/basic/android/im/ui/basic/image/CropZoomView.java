package com.huawei.basic.android.im.ui.basic.image;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * 自定义图片view，可以剪裁<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-4-23]
 */
public class CropZoomView extends View implements Observer
{
    private static final int CROP_BAGE_WIDHT = 5;
    
    /**
     * 图片的Bitmap
     */
    private Bitmap mBitmap;
    
    //这个对象是一个比例值
    private float mAspectQuotient;
    
    private ZoomState mZoomState;
    
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    
    private final Rect mRectSrc = new Rect();
    
    private final Rect mRectDst = new Rect();
    
    /**
     * 绿色边框画笔
     */
    private Paint rectPaint = new Paint();
    
    /**
     * 边框外透明背景画笔
     */
    private Paint outlinePaint = new Paint();
    
    /**
     * 裁剪框rect
     */
    private Rect mCropBageRec;
    
    private boolean mNeedshowCropArea;
    
    /**
     * 裁剪框左边rect
     */
    private Rect mCropLeftRect;
    
    /**
     * 裁剪框上边rect
     */
    private Rect mCropTopRect;
    
    /**
     * 裁剪框右边rect
     */
    private Rect mCropRightRect;
    
    /**
     * 裁剪框下边rect
     */
    private Rect mCropBottomRect;
    
    /**
     * 裁剪框外上面rect
     */
    private Rect mTopRect;
    
    /**
     * 裁剪框外下面rect
     */
    private Rect mBottomRect;
    
    /**
     * 
     * [构造简要说明]
     * @param context context
     * @param attrs attrs
     */
    public CropZoomView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    /**
     * 
     * 设置是否要显示裁剪区域<BR>
     * [功能详细描述]
     * @param needshowCropArea 是否显示
     */
    public void setNeedShowCropArea(boolean needshowCropArea)
    {
        mNeedshowCropArea = needshowCropArea;
        rectPaint.setColor(0xFF6bb000);
        outlinePaint.setColor(0xFF000000);
        outlinePaint.setAlpha(80);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //这个方法只有在放大缩小或移动时才会调用判断中的代码
        if (null != mBitmap && null != mZoomState)
        {
            final int viewWidth;
            final int viewHeight;
            final int bitmapWidth;
            final int bitmapHeight;
            final float panX;
            final float panY;
            final float zoomX;
            final float zoomY;
            
            //获取当前组件的宽度和高度
            viewHeight = this.getHeight();
            viewWidth = this.getWidth();
            
            //获得图片的宽度和高度
            bitmapWidth = mBitmap.getWidth();
            bitmapHeight = mBitmap.getHeight();
            panX = mZoomState.getPanX();
            panY = mZoomState.getPanY();
            zoomX = mZoomState.getZoomX(mAspectQuotient) * viewWidth
                    / bitmapWidth;
            zoomY = mZoomState.getZoomY(mAspectQuotient) * viewHeight
                    / bitmapHeight;
            mRectSrc.left = (int) (panX * bitmapWidth - viewWidth / (zoomX * 2));
            mRectSrc.top = (int) (panY * bitmapHeight - viewHeight
                    / (zoomY * 2));
            mRectSrc.right = (int) (mRectSrc.left + viewWidth / zoomX);
            mRectSrc.bottom = (int) (mRectSrc.top + viewHeight / zoomY);
            mRectDst.left = getLeft();
            mRectDst.top = getTop();
            mRectDst.right = getRight();
            mRectDst.bottom = getBottom();
            
            if (mRectSrc.left < 0)
            {
                mRectDst.left += -mRectSrc.left * zoomX;
                mRectSrc.left = 0;
            }
            if (mRectSrc.right > bitmapWidth)
            {
                mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
                mRectSrc.right = bitmapWidth;
            }
            if (mRectSrc.top < 0)
            {
                mRectDst.top += -mRectSrc.top * zoomY;
                mRectSrc.top = 0;
            }
            if (mRectSrc.bottom > bitmapHeight)
            {
                mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
                mRectSrc.bottom = bitmapHeight;
            }
            
            //画图片
            canvas.drawBitmap(mBitmap, mRectSrc, mRectDst, mPaint);
            
            //画裁剪框，以后需要对这个画法进行优化
            if (mNeedshowCropArea)
            {
                //算出裁剪框位置
                if (null == mCropBageRec)
                {
                    initRect(viewWidth, viewHeight);
                }
                
                canvas.drawRect(mCropLeftRect, rectPaint);
                canvas.drawRect(mCropTopRect, rectPaint);
                canvas.drawRect(mCropRightRect, rectPaint);
                canvas.drawRect(mCropBottomRect, rectPaint);
                
                canvas.drawRect(mTopRect, outlinePaint);
                canvas.drawRect(mBottomRect, outlinePaint);
            }
            
        }
    }
    
    private void initRect(int viewWidth, int viewHeight)
    {
        mCropBageRec = new Rect();
        //算出画框的区域，格式为以屏幕的宽画一个正方形，空余部分上面占1/3，下面为2/3
        mCropBageRec.left = 0;
        mCropBageRec.top = (viewHeight - viewWidth) / 3;
        mCropBageRec.right = viewWidth;
        mCropBageRec.bottom = viewWidth + (viewHeight - viewWidth) / 3;
        
        mTopRect = new Rect();
        mTopRect.left = 0;
        mTopRect.top = 0;
        mTopRect.right = mCropBageRec.right;
        mTopRect.bottom = mCropBageRec.top;
        
        mBottomRect = new Rect();
        mBottomRect.left = 0;
        mBottomRect.top = mCropBageRec.bottom;
        mBottomRect.right = mCropBageRec.right;
        mBottomRect.bottom = viewHeight;
        
        mCropLeftRect = new Rect();
        mCropLeftRect.left = 0;
        mCropLeftRect.top = mCropBageRec.top;
        mCropLeftRect.right = mCropLeftRect.left + CROP_BAGE_WIDHT;
        mCropLeftRect.bottom = mCropBageRec.bottom;
        
        mCropTopRect = new Rect();
        mCropTopRect.left = 0;
        mCropTopRect.top = mCropBageRec.top;
        mCropTopRect.right = mCropBageRec.right;
        mCropTopRect.bottom = mCropLeftRect.top + CROP_BAGE_WIDHT;
        
        mCropRightRect = new Rect();
        mCropRightRect.left = mCropBageRec.right - CROP_BAGE_WIDHT;
        mCropRightRect.top = mCropBageRec.top;
        mCropRightRect.right = mCropBageRec.right;
        mCropRightRect.bottom = mCropBageRec.bottom;
        
        mCropBottomRect = new Rect();
        mCropBottomRect.left = 0;
        mCropBottomRect.top = mCropBageRec.bottom - CROP_BAGE_WIDHT;
        mCropBottomRect.right = mCropBageRec.right;
        mCropBottomRect.bottom = mCropBageRec.bottom;
        
    }
    
    /**
     * 
     * 获取裁剪图片<BR>
     * [功能详细描述]
     * @return Bitmap
     */
    public Bitmap cropImage()
    {
        if (mRectDst.width() > 0 && mRectDst.height() > 0)
        {
            //先判断需要裁减的框里是否有图片
            if (!(mCropBageRec.top > mRectDst.bottom || mCropBageRec.bottom < mRectDst.top))
            {
                //获取裁剪框中，图片所在区域的矩形的坐标【裁剪框为坐标点】
                Rect cropIm = new Rect();
                cropIm.left = mRectDst.left;
                cropIm.top = Math.max(mRectDst.top - mCropBageRec.top, 0);
                cropIm.right = mRectDst.right;
                if (mRectDst.bottom < mCropBageRec.bottom)
                {
                    cropIm.bottom = mRectDst.bottom - mCropBageRec.top;
                }
                else
                {
                    cropIm.bottom = mCropBageRec.height();
                }
                //截取的图片绘制在这个bitmap上
                Bitmap croppedImage = Bitmap.createBitmap(mCropBageRec.width(),
                        mCropBageRec.height(),
                        Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(croppedImage);
                //如果图片放大或缩小，mRectSrc会改变
                canvas.drawBitmap(mBitmap, mRectSrc, cropIm, mPaint);
                return croppedImage;
            }
        }
        return null;
    }
    
    private void calculateAspectQuotient()
    {
        if (mBitmap != null)
        {
            mAspectQuotient = (((float) mBitmap.getWidth()) / mBitmap.getHeight())
                    / (((float) getWidth()) / getHeight());
        }
    }
    
    /**
     * 
     * 设置bitmap<BR>
     * [功能详细描述]
     * @param bitmap 图片资源
     */
    public void setImage(Bitmap bitmap)
    {
        if (null != mBitmap && !mBitmap.isRecycled())
        {
            mBitmap.recycle();
            mBitmap = null;
        }
        this.mBitmap = bitmap;
        calculateAspectQuotient();
        invalidate();
    }
    
    /**
     * 
     * 获取图片bitmap<BR>
     * [功能详细描述] 
     * @return bitmap
     */
    public Bitmap getBitmap()
    {
        return mBitmap;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Observable observable, Object data)
    {
        invalidate();
    }
    
    /**
     * 
     * 设置zoomState<BR>
     * [功能详细描述]
     * @param zoomState zoomState
     */
    public void setZoomState(ZoomState zoomState)
    {
        if (mZoomState != null)
        {
            mZoomState.deleteObserver(this);
        }
        mZoomState = zoomState;
        mZoomState.addObserver(this);
        invalidate();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        calculateAspectQuotient();
    }
    
}
