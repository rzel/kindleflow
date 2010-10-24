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

package android.graphics;


/**
 * The Matrix class holds a 3x3 matrix for transforming coordinates.
 * Matrix does not have a constructor, so it must be explicitly initialized
 * using either reset() - to construct an identity matrix, or one of the set..()
 * functions (e.g. setTranslate, setRotate, etc.).
 */
public class Matrix {

    public static final int MSCALE_X = 0;   //!< use with getValues/setValues
    public static final int MSKEW_X  = 1;   //!< use with getValues/setValues
    public static final int MTRANS_X = 2;   //!< use with getValues/setValues
    public static final int MSKEW_Y  = 3;   //!< use with getValues/setValues
    public static final int MSCALE_Y = 4;   //!< use with getValues/setValues
    public static final int MTRANS_Y = 5;   //!< use with getValues/setValues
    public static final int MPERSP_0 = 6;   //!< use with getValues/setValues
    public static final int MPERSP_1 = 7;   //!< use with getValues/setValues
    public static final int MPERSP_2 = 8;   //!< use with getValues/setValues

    /* package */ int native_instance;
    
    public float[][] matrix = new float[3][3];
    
/*
    *//**
     * Create an identity matrix
     *//*
    public Matrix() {
        native_instance = native_create(0);
    }

    *//**
     * Create a matrix that is a (deep) copy of src
     * @param src The matrix to copy into this matrix
     *//*
    public Matrix(Matrix src) {
        native_instance = native_create(src != null ? src.native_instance : 0);
    }

    *//**
     * Returns true if the matrix is identity.
     * This maybe faster than testing if (getType() == 0)
     */
    public boolean isIdentity() {
    

    	
         if(matrix[0][0] == 1 && matrix[0][1] == 0 && matrix[0][2] == 0 &&
            matrix[1][0] == 0 && matrix[1][1] == 1 && matrix[1][2] == 0 &&
            matrix[2][0] == 0 && matrix[2][1] == 0 && matrix[2][2] == 1) {
         	return true;
         }
         else {
        	 return false;
	     }

    }

    /**
     * Returns true if will map a rectangle to another rectangle. This can be
     * true if the matrix is identity, scale-only, or rotates a multiple of 90
     * degrees.
     *//*
    public boolean rectStaysRect() {
        return native_rectStaysRect(native_instance);
    }

    *//**
     * (deep) copy the src matrix into this matrix. If src is null, reset this
     * matrix to the identity matrix.
     */
    public void set(Matrix src) {
        if (src == null) {
            reset();
        } else {
        	
   	     matrix[0][0] = src.matrix[0][0]; 
	     matrix[0][1] = src.matrix[0][1]; 
	     matrix[0][2] = src.matrix[0][2]; 
	                               
	     matrix[1][0] = src.matrix[1][0]; 
	     matrix[1][1] = src.matrix[1][1]; 
	     matrix[1][2] = src.matrix[1][2]; 
	                               
	     matrix[2][0] = src.matrix[2][0]; 
	     matrix[2][1] = src.matrix[2][1]; 
	     matrix[2][2] = src.matrix[2][2]; 
        	

        }
    }
    
    /** Returns true iff obj is a Matrix and its values equal our values.
    */
    public boolean equals(Object obj) {
        return obj != null &&
               obj instanceof Matrix &&
               equals( (Matrix)obj );
    }
    
    private boolean equals(Matrix src) {
  	     if( matrix[0][0] == src.matrix[0][0] && 
		     matrix[0][1] == src.matrix[0][1] &&
		     matrix[0][2] == src.matrix[0][2] && 
		                               
		     matrix[1][0] == src.matrix[1][0] && 
		     matrix[1][1] == src.matrix[1][1] && 
		     matrix[1][2] == src.matrix[1][2] &&
		                               
		     matrix[2][0] == src.matrix[2][0] && 
		     matrix[2][1] == src.matrix[2][1] && 
		     matrix[2][2] == src.matrix[2][2]) {
  	    	 return true;
  	     }
  	     
  	     return false;
    }

    /** Set the matrix to identity */
    public void reset() {
    	matrix[0][0] = 1; matrix[0][1] = 0; matrix[0][2] = 0;                       
    	matrix[1][0] = 0; matrix[1][1] = 1; matrix[1][2] = 0;                         
    	matrix[2][0] = 0; matrix[2][1] = 0; matrix[2][2] = 1; 
    }

    /** Set the matrix to translate by (dx, dy). *//*
    public void setTranslate(float dx, float dy) {
        native_setTranslate(native_instance, dx, dy);
    }

    *//**
     * Set the matrix to scale by sx and sy, with a pivot point at (px, py).
     * The pivot point is the coordinate that should remain unchanged by the
     * specified transformation.
     *//*
    public void setScale(float sx, float sy, float px, float py) {
        native_setScale(native_instance, sx, sy, px, py);
    }

    *//** Set the matrix to scale by sx and sy. *//*
    public void setScale(float sx, float sy) {
        native_setScale(native_instance, sx, sy);
    }

    *//**
     * Set the matrix to rotate by the specified number of degrees, with a pivot
     * point at (px, py). The pivot point is the coordinate that should remain
     * unchanged by the specified transformation.
     *//*
    public void setRotate(float degrees, float px, float py) {
        native_setRotate(native_instance, degrees, px, py);
    }

    *//**
     * Set the matrix to rotate about (0,0) by the specified number of degrees.
     *//*
    public void setRotate(float degrees) {
        native_setRotate(native_instance, degrees);
    }

    *//**
     * Set the matrix to rotate by the specified sine and cosine values, with a
     * pivot point at (px, py). The pivot point is the coordinate that should
     * remain unchanged by the specified transformation.
     *//*
    public void setSinCos(float sinValue, float cosValue, float px, float py) {
        native_setSinCos(native_instance, sinValue, cosValue, px, py);
    }

    *//** Set the matrix to rotate by the specified sine and cosine values. *//*
    public void setSinCos(float sinValue, float cosValue) {
        native_setSinCos(native_instance, sinValue, cosValue);
    }

    *//**
     * Set the matrix to skew by sx and sy, with a pivot point at (px, py).
     * The pivot point is the coordinate that should remain unchanged by the
     * specified transformation.
     *//*
    public void setSkew(float kx, float ky, float px, float py) {
        native_setSkew(native_instance, kx, ky, px, py);
    }

    *//** Set the matrix to skew by sx and sy. *//*
    public void setSkew(float kx, float ky) {
        native_setSkew(native_instance, kx, ky);
    }

    *//**
     * Set the matrix to the concatenation of the two specified matrices,
     * returning true if the the result can be represented. Either of the two
     * matrices may also be the target matrix. this = a * b
     *//*
    public boolean setConcat(Matrix a, Matrix b) {
        return native_setConcat(native_instance, a.native_instance,
                                b.native_instance);
    }

    *//**
     * Preconcats the matrix with the specified translation.
     * M' = M * T(dx, dy)
     *//*
    public boolean preTranslate(float dx, float dy) {
        return native_preTranslate(native_instance, dx, dy);
    }

    *//**
     * Preconcats the matrix with the specified scale.
     * M' = M * S(sx, sy, px, py)
     *//*
    public boolean preScale(float sx, float sy, float px, float py) {
        return native_preScale(native_instance, sx, sy, px, py);
    }

    *//**
     * Preconcats the matrix with the specified scale.
     * M' = M * S(sx, sy)
     *//*
    public boolean preScale(float sx, float sy) {
        return native_preScale(native_instance, sx, sy);
    }

    *//**
     * Preconcats the matrix with the specified rotation.
     * M' = M * R(degrees, px, py)
     *//*
    public boolean preRotate(float degrees, float px, float py) {
        return native_preRotate(native_instance, degrees, px, py);
    }

    *//**
     * Preconcats the matrix with the specified rotation.
     * M' = M * R(degrees)
     *//*
    public boolean preRotate(float degrees) {
        return native_preRotate(native_instance, degrees);
    }

    *//**
     * Preconcats the matrix with the specified skew.
     * M' = M * K(kx, ky, px, py)
     *//*
    public boolean preSkew(float kx, float ky, float px, float py) {
        return native_preSkew(native_instance, kx, ky, px, py);
    }

    *//**
     * Preconcats the matrix with the specified skew.
     * M' = M * K(kx, ky)
     *//*
    public boolean preSkew(float kx, float ky) {
        return native_preSkew(native_instance, kx, ky);
    }

    *//**
     * Preconcats the matrix with the specified matrix.
     * M' = M * other
     */
    public boolean preConcat(Matrix other) {
        
    	final float[][] newSelf = new float[3][3];
    	
    	for (int i=0; i<3; i++)
            for (int j=0; j<3; j++)
              for (int k=0; k<3; k++)
            	  newSelf[i][j] += matrix[i][k] * other.matrix[k][j];
    	
    	for (int i=0; i<3; i++)
            for (int j=0; j<3; j++)
            	matrix[i][j] = newSelf[i][j];

    	
    	return true;
    }

    /**
     * Postconcats the matrix with the specified translation.
     * M' = T(dx, dy) * M
     *//*
    public boolean postTranslate(float dx, float dy) {
        return native_postTranslate(native_instance, dx, dy);
    }

    *//**
     * Postconcats the matrix with the specified scale.
     * M' = S(sx, sy, px, py) * M
     *//*
    public boolean postScale(float sx, float sy, float px, float py) {
        return native_postScale(native_instance, sx, sy, px, py);
    }

    *//**
     * Postconcats the matrix with the specified scale.
     * M' = S(sx, sy) * M
     *//*
    public boolean postScale(float sx, float sy) {
        return native_postScale(native_instance, sx, sy);
    }

    *//**
     * Postconcats the matrix with the specified rotation.
     * M' = R(degrees, px, py) * M
     *//*
    public boolean postRotate(float degrees, float px, float py) {
        return native_postRotate(native_instance, degrees, px, py);
    }

    *//**
     * Postconcats the matrix with the specified rotation.
     * M' = R(degrees) * M
     *//*
    public boolean postRotate(float degrees) {
        return native_postRotate(native_instance, degrees);
    }

    *//**
     * Postconcats the matrix with the specified skew.
     * M' = K(kx, ky, px, py) * M
     *//*
    public boolean postSkew(float kx, float ky, float px, float py) {
        return native_postSkew(native_instance, kx, ky, px, py);
    }

    *//**
     * Postconcats the matrix with the specified skew.
     * M' = K(kx, ky) * M
     *//*
    public boolean postSkew(float kx, float ky) {
        return native_postSkew(native_instance, kx, ky);
    }

    *//**
     * Postconcats the matrix with the specified matrix.
     * M' = other * M
     *//*
    public boolean postConcat(Matrix other) {
        return native_postConcat(native_instance, other.native_instance);
    }

    *//** Controlls how the src rect should align into the dst rect for
        setRectToRect().
    *//*
    public enum ScaleToFit {
        *//**
         * Scale in X and Y independently, so that src matches dst exactly.
         * This may change the aspect ratio of the src.
         *//*
        FILL    (0),
        *//**
         * Compute a scale that will maintain the original src aspect ratio,
         * but will also ensure that src fits entirely inside dst. At least one
         * axis (X or Y) will fit exactly. START aligns the result to the
         * left and top edges of dst.
         *//*
        START   (1),
        *//**
         * Compute a scale that will maintain the original src aspect ratio,
         * but will also ensure that src fits entirely inside dst. At least one
         * axis (X or Y) will fit exactly. The result is centered inside dst.
         *//*
        CENTER  (2),
        *//**
         * Compute a scale that will maintain the original src aspect ratio,
         * but will also ensure that src fits entirely inside dst. At least one
         * axis (X or Y) will fit exactly. END aligns the result to the
         * right and bottom edges of dst.
         *//*
        END     (3);

        // the native values must match those in SkMatrix.h 
        ScaleToFit(int nativeInt) {
            this.nativeInt = nativeInt;
        }
        final int nativeInt;
    }

    *//**
     * Set the matrix to the scale and translate values that map the source
     * rectangle to the destination rectangle, returning true if the the result
     * can be represented.
     *
     * @param src the source rectangle to map from.
     * @param dst the destination rectangle to map to.
     * @param stf the ScaleToFit option
     * @return true if the matrix can be represented by the rectangle mapping.
     *//*
    public boolean setRectToRect(RectF src, RectF dst, ScaleToFit stf) {
        if (dst == null || src == null) {
            throw new NullPointerException();
        }
        return native_setRectToRect(native_instance, src, dst, stf.nativeInt);
    }
    
    // private helper to perform range checks on arrays of "points"
    private static void checkPointArrays(float[] src, int srcIndex,
                                         float[] dst, int dstIndex,
                                         int pointCount) {
        // check for too-small and too-big indices
        int srcStop = srcIndex + (pointCount << 1);
        int dstStop = dstIndex + (pointCount << 1);
        if ((pointCount | srcIndex | dstIndex | srcStop | dstStop) < 0 ||
                srcStop > src.length || dstStop > dst.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    *//**
     * Set the matrix such that the specified src points would map to the
     * specified dst points. The "points" are represented as an array of floats,
     * order [x0, y0, x1, y1, ...], where each "point" is 2 float values.
     *
     * @param src   The array of src [x,y] pairs (points)
     * @param srcIndex Index of the first pair of src values
     * @param dst   The array of dst [x,y] pairs (points)
     * @param dstIndex Index of the first pair of dst values
     * @param pointCount The number of pairs/points to be used. Must be [0..4]
     * @return true if the matrix was set to the specified transformation
     *//*
    public boolean setPolyToPoly(float[] src, int srcIndex,
                                 float[] dst, int dstIndex,
                                 int pointCount) {
        if (pointCount > 4) {
            throw new IllegalArgumentException();
        }
        checkPointArrays(src, srcIndex, dst, dstIndex, pointCount);
        return native_setPolyToPoly(native_instance, src, srcIndex,
                                    dst, dstIndex, pointCount);
    }

    *//**
     * If this matrix can be inverted, return true and if inverse is not null,
     * set inverse to be the inverse of this matrix. If this matrix cannot be
     * inverted, ignore inverse and return false.
     *//*
    public boolean invert(Matrix inverse) {
        return native_invert(native_instance, inverse.native_instance);
    }

    *//**
    * Apply this matrix to the array of 2D points specified by src, and write
     * the transformed points into the array of points specified by dst. The
     * two arrays represent their "points" as pairs of floats [x, y].
     *
     * @param dst   The array of dst points (x,y pairs)
     * @param dstIndex The index of the first [x,y] pair of dst floats
     * @param src   The array of src points (x,y pairs)
     * @param srcIndex The index of the first [x,y] pair of src floats
     * @param pointCount The number of points (x,y pairs) to transform
     *//*
    public void mapPoints(float[] dst, int dstIndex, float[] src, int srcIndex,
                          int pointCount) {
        checkPointArrays(src, srcIndex, dst, dstIndex, pointCount);
        native_mapPoints(native_instance, dst, dstIndex, src, srcIndex,
                         pointCount, true);
    }
    
    *//**
    * Apply this matrix to the array of 2D vectors specified by src, and write
     * the transformed vectors into the array of vectors specified by dst. The
     * two arrays represent their "vectors" as pairs of floats [x, y].
     *
     * @param dst   The array of dst vectors (x,y pairs)
     * @param dstIndex The index of the first [x,y] pair of dst floats
     * @param src   The array of src vectors (x,y pairs)
     * @param srcIndex The index of the first [x,y] pair of src floats
     * @param vectorCount The number of vectors (x,y pairs) to transform
     *//*
    public void mapVectors(float[] dst, int dstIndex, float[] src, int srcIndex,
                          int vectorCount) {
        checkPointArrays(src, srcIndex, dst, dstIndex, vectorCount);
        native_mapPoints(native_instance, dst, dstIndex, src, srcIndex,
                         vectorCount, false);
    }
    
    *//**
     * Apply this matrix to the array of 2D points specified by src, and write
     * the transformed points into the array of points specified by dst. The
     * two arrays represent their "points" as pairs of floats [x, y].
     *
     * @param dst   The array of dst points (x,y pairs)
     * @param src   The array of src points (x,y pairs)
     *//*
    public void mapPoints(float[] dst, float[] src) {
        if (dst.length != src.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        mapPoints(dst, 0, src, 0, dst.length >> 1);
    }

    *//**
     * Apply this matrix to the array of 2D vectors specified by src, and write
     * the transformed vectors into the array of vectors specified by dst. The
     * two arrays represent their "vectors" as pairs of floats [x, y].
     *
     * @param dst   The array of dst vectors (x,y pairs)
     * @param src   The array of src vectors (x,y pairs)
     *//*
    public void mapVectors(float[] dst, float[] src) {
        if (dst.length != src.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        mapVectors(dst, 0, src, 0, dst.length >> 1);
    }

    *//**
     * Apply this matrix to the array of 2D points, and write the transformed
     * points back into the array
     *
     * @param pts The array [x0, y0, x1, y1, ...] of points to transform.
     *//*
    public void mapPoints(float[] pts) {
        mapPoints(pts, 0, pts, 0, pts.length >> 1);
    }

    *//**
     * Apply this matrix to the array of 2D vectors, and write the transformed
     * vectors back into the array.
     * @param vecs The array [x0, y0, x1, y1, ...] of vectors to transform.
     *//*
    public void mapVectors(float[] vecs) {
        mapVectors(vecs, 0, vecs, 0, vecs.length >> 1);
    }

  

    /**
     * Return the mean radius of a circle after it has been mapped by
     * this matrix. NOTE: in perspective this value assumes the circle
     * has its center at the origin.
     *//*
    public float mapRadius(float radius) {
        return native_mapRadius(native_instance, radius);
    }
    
    *//** Copy 9 values from the matrix into the array.
    */
    public void getValues(float[] values) {
        if (values.length < 9) {
            throw new ArrayIndexOutOfBoundsException();
        }
        
        values[0] = matrix[0][0];
        values[1] = matrix[0][1];
        values[2] = matrix[0][2];
        
        values[3] = matrix[1][0];
        values[4] = matrix[1][1];
        values[5] = matrix[1][2];
        
        values[6] = matrix[2][0];
        values[7] = matrix[2][1];
        values[8] = matrix[2][2];
    }

    /** Copy 9 values from the array into the matrix.
        Depending on the implementation of Matrix, these may be
        transformed into 16.16 integers in the Matrix, such that
        a subsequent call to getValues() will not yield exactly
        the same values.
    */
    public void setValues(float[] values) {
        
    	if (values.length < 9) {
            throw new ArrayIndexOutOfBoundsException();
        }
        
	     matrix[0][0]= values[0] ; 
	     matrix[0][1]= values[1] ; 
	     matrix[0][2]= values[2] ; 
	                              
	     matrix[1][0]= values[3] ; 
	     matrix[1][1]= values[4] ; 
	     matrix[1][2]= values[5] ; 
	                              
	     matrix[2][0]= values[6] ; 
	     matrix[2][1]= values[7] ; 
	     matrix[2][2]= values[8] ; 
    }

    public String toString() {
        return "Matrix{" + toShortString() + "}";
                
    }

    
    public String toShortString() {
        float[] values = new float[9];
        getValues(values);
        return "[" +
                values[0] + ", " + values[1] + ", " + values[2] + "][" +
                values[3] + ", " + values[4] + ", " + values[5] + "][" +
                values[6] + ", " + values[7] + ", " + values[8] + "]";
                
    }

    /*
    protected void finalize() throws Throwable {
        finalizer(native_instance);
    }
    
    final int ni() {
        return native_instance;
    }
    */


}
