/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.graphics.drawable;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


/**
 * A simple color gradient for buttons, backgrounds, etc. See
 * <a href="{@docRoot}reference/available-resources.html#gradientdrawable">Gradient</a>
 * in the Resources topic to learn how to specify this type as an XML resource.
 */
public class GradientDrawable extends Drawable {

	/**
     * Shape is a rectangle, possibly with rounded corners
     */
    public static final int RECTANGLE = 0;
    

    //public static final int OVAL = 1; 
    //public static final int LINE = 2;
    //public static final int RING = 3;


    public static final int LINEAR_GRADIENT = 0;
    //public static final int RADIAL_GRADIENT = 1;
    //public static final int SWEEP_GRADIENT  = 2;

    private final GradientState mGradientState;
    
    private final Paint mFillPaint = new Paint();
    private Rect        mPadding;
    
    //private int         mAlpha = 0xFF;  // modified by the caller
    
    //private Paint       mStrokePaint;   // optional, set by the caller
    //private ColorFilter mColorFilter;   // optional, set by the caller
    //private boolean     mDither;
    //private final Path  mPath = new Path();
    private final Rect mRect = new Rect();
    //private Paint       mLayerPaint;    // internal, used if we use saveLayer()
    //private boolean     mRectIsDirty;   // internal state

    /**
     * Controls how the gradient is oriented relative to the drawable's bounds
     */
      
     public final static int TOP_BOTTOM = 0; /** draw the gradient from the top to the bottom */ 
     //public final static int TR_BL = 1;  /** draw the gradient from the top-right to the bottom-left */   
     public final static int RIGHT_LEFT = 2; /** draw the gradient from the right to the left */
     //public final static int BR_TL = 3; /** draw the gradient from the bottom-right to the top-left */  
     public final static int BOTTOM_TOP = 4;  /** draw the gradient from the bottom to the top */  
     //public final static int BL_TR = 5; /** draw the gradient from the bottom-left to the top-right */
     public final static int LEFT_RIGHT = 6; /** draw the gradient from the left to the right */
     //public final static int TL_BR = 7; /** draw the gradient from the top-left to the bottom-right */
    

 	public static int midColor(int color1, int color2, int prop, int max) {
		int red 	=  (((color1 >> 16) & 0xff) * prop + ((color2 >> 16) & 0xff) * (max - prop)) / max; 
		int green 	=  (((color1 >> 8)  & 0xff) * prop + ((color2 >> 8 ) & 0xff) * (max - prop)) / max; 
		int blue 	=  (((color1 >> 0)  & 0xff) * prop + ((color2 >> 0 ) & 0xff) * (max - prop)) / max; 
		
		int midColor 	= red << 16 | green << 8 | blue; 
		
		return midColor;
	}
     
    public GradientDrawable() {
        this(new GradientState(TOP_BOTTOM, null));
    }
    
    public GradientDrawable(int orientation, int color1, int color2) {
    	this(orientation, new int[] {color1, color2});
    }
    
    
    /**
     * Create a new gradient drawable given an orientation and an array
     * of colors for the gradient.
     */
    public GradientDrawable(int orientation, int[] colors) {
        this(new GradientState(orientation, colors));
    }
    
    // Override
    public boolean getPadding(Rect padding) {
        if (mPadding != null) {
            padding.set(mPadding);
            return true;
        } else {
            return super.getPadding(padding);
        }
    }

    /**
     * Specify radii for each of the 4 corners. For each corner, the array
     * contains 2 values, [X_radius, Y_radius]. The corners are ordered
     * top-left, top-right, bottom-right, bottom-left
     */
    public void setCornerArc(int arcWidth, int arcHeight) {
        mGradientState.setCornerArc(arcWidth, arcHeight);
    }
    
    /**
     * Specify radius for the corners of the gradient. If this is > 0, then the
     * drawable is drawn in a round-rectangle, rather than a rectangle.
    
    public void setCornerRadius(float radius) {
        mGradientState.setCornerRadius(radius);
    }
    
    
    /**
     * Set the stroke width and color for the drawable. If width is zero,
     * then no stroke is drawn.
     
    public void setStroke(int width, int color) {
        setStroke(width, color, 0, 0);
    }
    
    public void setStroke(int width, int color, float dashWidth, float dashGap) {
        mGradientState.setStroke(width, color, dashWidth, dashGap);

        if (mStrokePaint == null)  {
            mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStrokePaint.setStyle(Paint.STROKE);
        }
        mStrokePaint.setStrokeWidth(width);
        mStrokePaint.setColor(color);
        
        
        DashPathEffect e = null;
        if (dashWidth > 0) {
            e = new DashPathEffect(new float[] { dashWidth, dashGap }, 0);
        }
        mStrokePaint.setPathEffect(e);
        
    }
    */
    
    public void setSize(int width, int height) {
        mGradientState.setSize(width, height); 
    }
    
    public void setShape(int shape) {
        mGradientState.setShape(shape);
    }

    public void setGradientType(int gradient) {
        mGradientState.setGradientType(gradient);
        //mRectIsDirty = true;
    }

    //public void setGradientCenter(float x, float y) {
    //    mGradientState.setGradientCenter(x, y);
    //}

    //public void setGradientRadius(float gradientRadius) {
    //    mGradientState.setGradientRadius(gradientRadius);
    //}

    //public void setUseLevel(boolean useLevel) {
    //    mGradientState.mUseLevel = useLevel;
    //}
    
    //private int modulateAlpha(int alpha) {
    //   int scale = mAlpha + (mAlpha >> 7);
    //    return alpha * scale >> 8;
    //}

    // Override
    
    public void draw(Canvas canvas) {
        
    	//Log.i("GradientDrawable", "draw "+getBounds());
    	
    	if (!ensureValidRect()) {
            // nothing to draw
            return;
        }
       
        // remember the alpha values, in case we temporarily overwrite them
        // when we modulate them with mAlpha
        //final int prevFillAlpha = mFillPaint.getAlpha();
        //final int prevStrokeAlpha = mStrokePaint != null ?  mStrokePaint.getAlpha() : 0;
        // compute the modulate alpha values
        //final int currFillAlpha = modulateAlpha(prevFillAlpha);
        //final int currStrokeAlpha = modulateAlpha(prevStrokeAlpha);

        //final boolean haveStroke = currStrokeAlpha > 0 && mStrokePaint.getStrokeWidth() > 0;
        //final boolean haveFill = currFillAlpha > 0;
        final GradientState st = mGradientState;
        
        /*  we need a layer iff we're drawing both a fill and stroke, and the
            stroke is non-opaque, and our shapetype actually supports
            fill+stroke. Otherwise we can just draw the stroke (if any) on top
            of the fill (if any) without worrying about blending artifacts.
         */


        /*  Drawing with a layer is slower than direct drawing, but it
            allows us to apply paint effects like alpha and colorfilter to
            the result of multiple separate draws. In our case, if the user
            asks for a non-opaque alpha value (via setAlpha), and we're
            stroking, then we need to apply the alpha AFTER we've drawn
            both the fill and the stroke.
                 final boolean useLayer = haveStroke && haveFill && st.mShape != LINE &&
                                  currStrokeAlpha < 255;
        
        
        if (useLayer) {
            if (mLayerPaint == null) {
                mLayerPaint = new Paint();
            }
            mLayerPaint.setDither(mDither);
            mLayerPaint.setAlpha(mAlpha);
            mLayerPaint.setColorFilter(mColorFilter);

            float rad = mStrokePaint.getStrokeWidth();
            canvas.saveLayer(mRect.left - rad, mRect.top - rad,
                             mRect.right + rad, mRect.bottom + rad,
                             mLayerPaint, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);

            // don't perform the filter in our individual paints
            // since the layer will do it for us
            mFillPaint.setColorFilter(null);
            mStrokePaint.setColorFilter(null);
        } else {
            //if we're not using a layer, apply the dither/filter to our
            //    individual paints
            
            mFillPaint.setAlpha(currFillAlpha);
            mFillPaint.setDither(mDither);
            mFillPaint.setColorFilter(mColorFilter);
            if (haveStroke) {
                mStrokePaint.setAlpha(currStrokeAlpha);
                mStrokePaint.setDither(mDither);
                mStrokePaint.setColorFilter(mColorFilter);
            }
        }
    */

        switch (st.mShape) {
            
        	case RECTANGLE:
        		mFillPaint.setStyle(Paint.FILL);
        		
        		Rect b = getBounds();
        		
        		int max = st.mOrientation == TOP_BOTTOM ? b.height() : b.width();

        		for ( int i = 0; i < max; i++ ) {
        			
        			int color = midColor(st.mColors[0], st.mColors[1], max*(max - 1 - i)/(max - 1) , max); 

        			mFillPaint.setColor(color);
        			
        			if( st.mOrientation == TOP_BOTTOM ) {
        				canvas.drawLine(b.left, b.top + i, b.right, b.top + i, mFillPaint);
        			}
        			else {
        				canvas.drawLine(b.left + i, b.top, b.left + i, b.bottom, mFillPaint);
        			}
        		}
        		
        		
        		
        		
        		
        		//canvas.drawRect(0,0, st.mWidth, st.mHeight, mFillPaint);
        		//canvas.drawRect(mRect, mFillPaint);
        		
            	/*
                if (st.mRadiusArray != null) {
                    mPath.reset();
                    mPath.addRoundRect(mRect, st.mRadiusArray, Path.Direction.CW);
                    canvas.drawPath(mPath, mFillPaint);
                    if (haveStroke) {
                        canvas.drawPath(mPath, mStrokePaint);
                    }
                }
                else {
                    float rad = st.mRadius;
                    canvas.drawRoundRect(mRect, rad, rad, mFillPaint);
                    if (haveStroke) {
                        canvas.drawRoundRect(mRect, rad, rad, mStrokePaint);
                    }
                }
                break;
                */
                
                /*
            case OVAL:
                canvas.drawOval(mRect, mFillPaint);
                if (haveStroke) {
                    canvas.drawOval(mRect, mStrokePaint);
                }
                break;
            case LINE: {
                RectF r = mRect;
                float y = r.centerY();
                canvas.drawLine(r.left, y, r.right, y, mStrokePaint);
                break;
            }
            case RING:
                Path ring = buildRing(st);
                canvas.drawPath(ring, mFillPaint);
                if (haveStroke) {
                    canvas.drawPath(ring, mStrokePaint);
                }
                break;
                */
        }
        
        /*
        if (useLayer) {
            canvas.restore();
        } else {
            mFillPaint.setAlpha(prevFillAlpha);
            if (haveStroke) {
                mStrokePaint.setAlpha(prevStrokeAlpha);
            }
        }
        */
    }
    
    /*
    private Path buildRing(GradientState st) {
        float sweep = st.mUseLevelForShape ? (360.0f * getLevel() / 10000.0f) : 360f;
        
        RectF bounds = new RectF(mRect);

        float x = bounds.width() / 2.0f;
        float y = bounds.height() / 2.0f;

        float thickness = bounds.width() / st.mThickness;
        // inner radius
        float radius = bounds.width() / st.mInnerRadius;

        RectF innerBounds = new RectF(bounds);
        innerBounds.inset(x - radius, y - radius);

        bounds = new RectF(innerBounds);
        bounds.inset(-thickness, -thickness);

        Path path = new Path();
        // arcTo treats the sweep angle mod 360, so check for that, since we
        // think 360 means draw the entire oval
        if (sweep < 360 && sweep > -360) {
            path.setFillType(Path.FillType.EVEN_ODD);
            // inner top
            path.moveTo(x + radius, y);
            // outer top
            path.lineTo(x + radius + thickness, y);
            // outer arc
            path.arcTo(bounds, 0.0f, sweep, false);
            // inner arc
            path.arcTo(innerBounds, sweep, -sweep, false);
            path.close();
        } else {
            // add the entire ovals
            path.addOval(bounds, Path.Direction.CW);
            path.addOval(innerBounds, Path.Direction.CCW);
        }

        return path;
    }
    

    public void setColor(int argb) {
        mGradientState.setSolidColor(argb);
        mFillPaint.setColor(argb);
    }
    */

    // Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mGradientState.mChangingConfigurations;
    }
    
    // Override
    public void setAlpha(int alpha) {
        //mAlpha = alpha;
    }

    // Override
    // public void setDither(boolean dither) {
    //    
    //}

    // Override
    //public void setColorFilter(ColorFilter cf) {
    //    mColorFilter = cf;
    //}

    // Override
    public int getOpacity() {
        // XXX need to figure out the actual opacity...
        //return PixelFormat.TRANSLUCENT;
    	return (-1);
    }

    // Override
    protected void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
       //mRectIsDirty = true;
    }

    // Override
    protected boolean onLevelChange(int level) {
        super.onLevelChange(level);
       // mRectIsDirty = true;
        invalidateSelf();
        return true;
    }

    /**
     * This checks mRectIsDirty, and if it is true, recomputes both our drawing
     * rectangle (mRect) and the gradient itself, since it depends on our
     * rectangle too.
     * @return true if the resulting rectangle is not empty, false otherwise
     */
    
    private boolean ensureValidRect() {
        //if (mRectIsDirty) {
        //    mRectIsDirty = false;

        //Rect bounds = getBounds();
         
    	mRect.set(getBounds());
            /*
            
            float inset = 0;
            
            if (mStrokePaint != null) {
                inset = mStrokePaint.getStrokeWidth() * 0.5f;
            }

            final GradientState st = mGradientState;

            mRect.set(bounds.left + inset, bounds.top + inset,
                      bounds.right - inset, bounds.bottom - inset);
            
            if (st.mColors != null) {
                RectF r = mRect;
                float x0, x1, y0, y1;

                if (st.mGradient == LINEAR_GRADIENT) {
                    final float level = st.mUseLevel ? (float) getLevel() / 10000.0f : 1.0f;                    
                    switch (st.mOrientation) {
                    case TOP_BOTTOM:
                        x0 = r.left;            y0 = r.top;
                        x1 = x0;                y1 = level * r.bottom;
                        break;
                    case TR_BL:
                        x0 = r.right;           y0 = r.top;
                        x1 = level * r.left;    y1 = level * r.bottom;
                        break;
                    case RIGHT_LEFT:
                        x0 = r.right;           y0 = r.top;
                        x1 = level * r.left;    y1 = y0;
                        break;
                    case BR_TL:
                        x0 = r.right;           y0 = r.bottom;
                        x1 = level * r.left;    y1 = level * r.top;
                        break;
                    case BOTTOM_TOP:
                        x0 = r.left;            y0 = r.bottom;
                        x1 = x0;                y1 = level * r.top;
                        break;
                    case BL_TR:
                        x0 = r.left;            y0 = r.bottom;
                        x1 = level * r.right;   y1 = level * r.top;
                        break;
                    case LEFT_RIGHT:
                        x0 = r.left;            y0 = r.top;
                        x1 = level * r.right;   y1 = y0;
                        break;
                    default:// TL_BR 
                        x0 = r.left;            y0 = r.top;
                        x1 = level * r.right;   y1 = level * r.bottom;
                        break;
                    }

                    mFillPaint.setShader(new LinearGradient(x0, y0, x1, y1,
                                                            st.mColors, st.mPositions,
                                                            Shader.TileMode.CLAMP));
                } else if (st.mGradient == RADIAL_GRADIENT) {
                    x0 = r.left + (r.right - r.left) * st.mCenterX;
                    y0 = r.top + (r.bottom - r.top) * st.mCenterY;

                    final float level = st.mUseLevel ? (float) getLevel() / 10000.0f : 1.0f;

                    mFillPaint.setShader(new RadialGradient(x0, y0,
                            level * st.mGradientRadius, st.mColors, null,
                            Shader.TileMode.CLAMP));
                } else if (st.mGradient == SWEEP_GRADIENT) {
                    x0 = r.left + (r.right - r.left) * st.mCenterX;
                    y0 = r.top + (r.bottom - r.top) * st.mCenterY;

                    float[] positions = null;
                    int[] colors = st.mColors;

                    if (st.mUseLevel) {
                        final int length = st.mColors.length;
                        colors = new int[length + 1];
                        System.arraycopy(st.mColors, 0, colors, 0, length);
                        colors[length] = st.mColors[length - 1];

                        final float fraction = 1.0f / (float) (length - 1);
                        positions = new float[length + 1];
                        final float level = (float) getLevel() / 10000.0f;
                        for (int i = 0; i < length; i++) {
                            positions[i] = i * fraction * level;
                        }
                        positions[length] = 1.0f;
                    }
                    mFillPaint.setShader(new SweepGradient(x0, y0, colors, positions));
                }
            }
        }
        */
        return !mRect.isEmpty();
    }
    

    // Override
    /*
    public void inflate(Resources r, XmlPullParser parser,
            AttributeSet attrs)
            throws XmlPullParserException, IOException {
        
        final GradientState st = mGradientState;
        
        TypedArray a = r.obtainAttributes(attrs,
                com.android.internal.R.styleable.GradientDrawable);

        super.inflateWithAttributes(r, parser, a,
                com.android.internal.R.styleable.GradientDrawable_visible);
        
        int shapeType = a.getInt(
                com.android.internal.R.styleable.GradientDrawable_shape, RECTANGLE);
        
        if (shapeType == RING) {
            st.mInnerRadius = a.getFloat(
                    com.android.internal.R.styleable.GradientDrawable_innerRadiusRatio, 3.0f);
            st.mThickness = a.getFloat(
                    com.android.internal.R.styleable.GradientDrawable_thicknessRatio, 9.0f);
            st.mUseLevelForShape = a.getBoolean(
                    com.android.internal.R.styleable.GradientDrawable_useLevel, true);
        }
        
        a.recycle();
        
        setShape(shapeType);
        
        int type;

        final int innerDepth = parser.getDepth()+1;
        int depth;
        while ((type=parser.next()) != XmlPullParser.END_DOCUMENT
               && ((depth=parser.getDepth()) >= innerDepth
                       || type != XmlPullParser.END_TAG)) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            if (depth > innerDepth) {
                continue;
            }
            
            String name = parser.getName();
            
            if (name.equals("size")) {
                a = r.obtainAttributes(attrs,
                        com.android.internal.R.styleable.GradientDrawableSize);
                int width = a.getDimensionPixelSize(
                        com.android.internal.R.styleable.GradientDrawableSize_width, -1);
                int height = a.getDimensionPixelSize(
                        com.android.internal.R.styleable.GradientDrawableSize_height, -1);
                a.recycle();
                setSize(width, height);
            } else if (name.equals("gradient")) {
                a = r.obtainAttributes(attrs,
                        com.android.internal.R.styleable.GradientDrawableGradient);
                int startColor = a.getColor(
                        com.android.internal.R.styleable.GradientDrawableGradient_startColor, 0);
                boolean hasCenterColor = a
                        .hasValue(com.android.internal.R.styleable.GradientDrawableGradient_centerColor);
                int centerColor = a.getColor(
                        com.android.internal.R.styleable.GradientDrawableGradient_centerColor, 0);
                int endColor = a.getColor(
                        com.android.internal.R.styleable.GradientDrawableGradient_endColor, 0);
                int gradientType = a.getInt(
                        com.android.internal.R.styleable.GradientDrawableGradient_type,
                        LINEAR_GRADIENT);

                st.mCenterX = getFloatOrFraction(
                        a,
                        com.android.internal.R.styleable.GradientDrawableGradient_centerX,
                        0.5f);

                st.mCenterY = getFloatOrFraction(
                        a,
                        com.android.internal.R.styleable.GradientDrawableGradient_centerY,
                        0.5f);

                st.mUseLevel = a.getBoolean(
                        com.android.internal.R.styleable.GradientDrawableGradient_useLevel, false);
                st.mGradient = gradientType;

                if (gradientType == LINEAR_GRADIENT) {
                    int angle = (int)a.getFloat(
                            com.android.internal.R.styleable.GradientDrawableGradient_angle, 0);
                    angle %= 360;
                    if (angle % 45 != 0) {
                        throw new XmlPullParserException(a.getPositionDescription()
                                + "<gradient> tag requires 'angle' attribute to "
                                + "be a multiple of 45");
                    }

                    switch (angle) {
                    case 0:
                        st.mOrientation = Orientation.LEFT_RIGHT;
                        break;
                    case 45:
                        st.mOrientation = Orientation.BL_TR;
                        break;
                    case 90:
                        st.mOrientation = Orientation.BOTTOM_TOP;
                        break;
                    case 135:
                        st.mOrientation = Orientation.BR_TL;
                        break;
                    case 180:
                        st.mOrientation = Orientation.RIGHT_LEFT;
                        break;
                    case 225:
                        st.mOrientation = Orientation.TR_BL;
                        break;
                    case 270:
                        st.mOrientation = Orientation.TOP_BOTTOM;
                        break;
                    case 315:
                        st.mOrientation = Orientation.TL_BR;
                        break;
                    }
                } else {
                    TypedValue tv = a.peekValue(
                            com.android.internal.R.styleable.GradientDrawableGradient_gradientRadius);
                    if (tv != null) {
                        boolean radiusRel = tv.type == TypedValue.TYPE_FRACTION;
                        st.mGradientRadius = radiusRel ?
                                tv.getFraction(1.0f, 1.0f) : tv.getFloat();
                    } else if (gradientType == RADIAL_GRADIENT) {
                        throw new XmlPullParserException(
                                a.getPositionDescription()
                                + "<gradient> tag requires 'gradientRadius' "
                                + "attribute with radial type");
                    }
                }

                a.recycle();

                if (hasCenterColor) {
                    st.mColors = new int[3];
                    st.mColors[0] = startColor;
                    st.mColors[1] = centerColor;
                    st.mColors[2] = endColor;
                    
                    st.mPositions = new float[3];
                    st.mPositions[0] = 0.0f;
                    // Since 0.5f is default value, try to take the one that isn't 0.5f
                    st.mPositions[1] = st.mCenterX != 0.5f ? st.mCenterX : st.mCenterY;
                    st.mPositions[2] = 1f;
                } else {
                    st.mColors = new int[2];
                    st.mColors[0] = startColor;
                    st.mColors[1] = endColor;
                }
                
            } else if (name.equals("solid")) {
                a = r.obtainAttributes(attrs,
                        com.android.internal.R.styleable.GradientDrawableSolid);
                int argb = a.getColor(
                        com.android.internal.R.styleable.GradientDrawableSolid_color, 0);
                a.recycle();
                setColor(argb);
            } else if (name.equals("stroke")) {
                a = r.obtainAttributes(attrs,
                        com.android.internal.R.styleable.GradientDrawableStroke);
                int width = a.getDimensionPixelSize(
                        com.android.internal.R.styleable.GradientDrawableStroke_width, 0);
                int color = a.getColor(
                        com.android.internal.R.styleable.GradientDrawableStroke_color, 0);
                float dashWidth = a.getDimension(
                        com.android.internal.R.styleable.GradientDrawableStroke_dashWidth, 0);
                if (dashWidth != 0.0f) {
                    float dashGap = a.getDimension(
                            com.android.internal.R.styleable.GradientDrawableStroke_dashGap, 0);
                    setStroke(width, color, dashWidth, dashGap);
                } else {
                    setStroke(width, color);
                }
                a.recycle();
            } else if (name.equals("corners")) {
                a = r.obtainAttributes(attrs,
                        com.android.internal.R.styleable.DrawableCorners);
                int radius = a.getDimensionPixelSize(
                        com.android.internal.R.styleable.DrawableCorners_radius, 0);
                setCornerRadius(radius);
                int topLeftRadius = a.getDimensionPixelSize(
                        com.android.internal.R.styleable.DrawableCorners_topLeftRadius, radius);
                int topRightRadius = a.getDimensionPixelSize(
                        com.android.internal.R.styleable.DrawableCorners_topRightRadius, radius);
                int bottomLeftRadius = a.getDimensionPixelSize(
                        com.android.internal.R.styleable.DrawableCorners_bottomLeftRadius, radius);
                int bottomRightRadius = a.getDimensionPixelSize(
                        com.android.internal.R.styleable.DrawableCorners_bottomRightRadius, radius);
                if (topLeftRadius != radius && topRightRadius != radius &&
                        bottomLeftRadius != radius && bottomRightRadius != radius) {
                    setCornerRadii(new float[] {
                            topLeftRadius, topLeftRadius,
                            topRightRadius, topRightRadius,
                            bottomLeftRadius, bottomLeftRadius,
                            bottomRightRadius, bottomRightRadius
                    });
                }
                a.recycle();
            } else if (name.equals("padding")) {
                a = r.obtainAttributes(attrs,
                        com.android.internal.R.styleable.GradientDrawablePadding);
                mPadding = new Rect(
                        a.getDimensionPixelOffset(
                                com.android.internal.R.styleable.GradientDrawablePadding_left, 0),
                        a.getDimensionPixelOffset(
                                com.android.internal.R.styleable.GradientDrawablePadding_top, 0),
                        a.getDimensionPixelOffset(
                                com.android.internal.R.styleable.GradientDrawablePadding_right, 0),
                        a.getDimensionPixelOffset(
                                com.android.internal.R.styleable.GradientDrawablePadding_bottom, 0));
                a.recycle();
                mGradientState.mPadding = mPadding;
            } else {
                Log.w("drawable", "Bad element under <shape>: " + name);
            }
        }
    }
    */

    /*
    private static float getFloatOrFraction(TypedArray a, int index, float defaultValue) {
        TypedValue tv = a.peekValue(index);
        float v = defaultValue;
        if (tv != null) {
            boolean vIsFraction = tv.type == TypedValue.TYPE_FRACTION;
            v = vIsFraction ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
        }
        return v;
    }
    */
    
    // Override
    public int getIntrinsicWidth() {
        return mGradientState.mWidth;
    }

    // Override
    public int getIntrinsicHeight() {
        return mGradientState.mHeight;
    }
    
    // Override
    public ConstantState getConstantState() {
        mGradientState.mChangingConfigurations = super.getChangingConfigurations();
        return mGradientState;
    }

    final static class GradientState extends ConstantState {
    	
        public int          mChangingConfigurations;
        public int          mShape = RECTANGLE;
        public int          mGradient = LINEAR_GRADIENT;
        public int  		mOrientation;
        public int[]        mColors;
        //public float[]      mPositions;
        //public boolean      mHasSolidColor;
        //public int          mSolidColor;
        //public int          mStrokeWidth = -1;   // if >= 0 use stroking.
        //public int          mStrokeColor;
        //public float        mStrokeDashWidth;
        //public float        mStrokeDashGap;

        public int			mArcWidth;
        public int			mArcHeight;
        public Rect         mPadding;
        public int          mWidth = -1;
        public int          mHeight = -1;
        
        //public float        mInnerRadius;
        //public float        mThickness;
        //private float       mCenterX = 0.5f;
        //private float       mCenterY = 0.5f;
        //private float       mGradientRadius = 0.5f;
        //private boolean     mUseLevel;
        //private boolean     mUseLevelForShape;
        
        
        GradientState() {
            mOrientation = TOP_BOTTOM;
        }

        GradientState(int orientation, int[] colors) {
            mOrientation = orientation;
            mColors = colors;
        }

        // Override
        public Drawable newDrawable() {
            return new GradientDrawable(this);
        }
        
        // Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }

        public void setShape(int shape) {
            mShape = shape;
        }

        public void setGradientType(int gradient) {
            mGradient = gradient;
        }

        //public void setGradientCenter(float x, float y) {
        //    mCenterX = x;
        //    mCenterY = y;
        //}

        //public void setSolidColor(int argb) {
        //    mHasSolidColor = true;
        //    mSolidColor = argb;
        //    mColors = null;
        //}

        //public void setStroke(int width, int color) {
        //    mStrokeWidth = width;
        //    mStrokeColor = color;
        //}
        
        //public void setStroke(int width, int color, float dashWidth, float dashGap) {
        //    mStrokeWidth = width;
        //    mStrokeColor = color;
        //    mStrokeDashWidth = dashWidth;
        //    mStrokeDashGap = dashGap;
        //}

        public void setCornerArc(int arcWidth, int arcHeight) {
        	mArcWidth = arcWidth;
            mArcHeight = arcHeight;
        }
        
        public void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        //public void setGradientRadius(float gradientRadius) {
        //    mGradientRadius = gradientRadius;
        //}
    }

    private GradientDrawable(GradientState state) {
    	
    	
    	mGradientState = state;
    	mPadding = state.mPadding;
    	
    	/*
        if (state.mHasSolidColor) {
            mFillPaint.setColor(state.mSolidColor);
        }
        
        
        if (state.mStrokeWidth >= 0) {
            mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStrokePaint.setStyle(Paint.STROKE);
            mStrokePaint.setStrokeWidth(state.mStrokeWidth);
            mStrokePaint.setColor(state.mStrokeColor);

            
            if (state.mStrokeDashWidth != 0.0f) {
                DashPathEffect e = new DashPathEffect(new float[] {
                        state.mStrokeDashWidth, state.mStrokeDashGap}, 0);
                mStrokePaint.setPathEffect(e);
            }
            
        }
       // mRectIsDirty = true;
       
        */
    }
    
    protected boolean onStateChange(int[] state) { 
    	
    	Log.i("GradientDrawable", "onStateChange");
    	
    	return false;
    	}
}

