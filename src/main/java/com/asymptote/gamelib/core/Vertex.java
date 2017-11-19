//==============================================================================
// Date Created:		08 April 2014
// Last Updated:		30 January 2015
//
// File name:			Vertex.java
// Author(s):			M. Matthew Hydock
//
// File description:	A class that represents a single vertex, with position,
//						color, and texture coordinates. Also includes a number
//						of constants, for use in loading/rendering vertices.
//						Now supports normals as well.
//==============================================================================

package com.asymptote.gamelib.core;

public class Vertex
{
	private final float[] xyzw = new float[4];
	private final float[] rgba = new float[4];
	private final float[] norm = new float[3];
	private final float[] st = new float[2];
	
	public static final int POS_ATTRIB = 0;
	public static final int COL_ATTRIB = 1;
	public static final int TEX_ATTRIB = 2;
	public static final int NOR_ATTRIB = 3;
	
	public static final int FLOAT_SIZE = 4;
	public static final int POS_COUNT = 4;
	public static final int COL_COUNT = 4;
	public static final int TEX_COUNT = 2;
	public static final int NOR_COUNT = 3;
	public static final int NUM_ELEMENTS = POS_COUNT + COL_COUNT + TEX_COUNT + NOR_COUNT;

	public static final int POS_OFFSET = 0;
	public static final int COL_OFFSET = POS_OFFSET + POS_COUNT*FLOAT_SIZE;
	public static final int TEX_OFFSET = COL_OFFSET + COL_COUNT*FLOAT_SIZE;
	public static final int NOR_OFFSET = TEX_OFFSET + TEX_COUNT*FLOAT_SIZE;
	public static final int STRIDE = NUM_ELEMENTS*FLOAT_SIZE;
	
	public float[] getInterleaved()
	{
		float[] out = new float[NUM_ELEMENTS];
		
		int i = 0;
		out[i++] = xyzw[0];
		out[i++] = xyzw[1];
		out[i++] = xyzw[2];
		out[i++] = xyzw[3];
		
		out[i++] = rgba[0];
		out[i++] = rgba[1];
		out[i++] = rgba[2];
		out[i++] = rgba[3];
		
		out[i++] = st[0];
		out[i++] = st[1];
		
		out[i++] = norm[0];
		out[i++] = norm[1];
		out[i++] = norm[2];
		
		return out;
	}
	
	public Vertex setXYZ(float x, float y, float z)
	{
		xyzw[0] = x;
		xyzw[1] = y;
		xyzw[2] = z;
		xyzw[3] = 1.0f;
		
		return this;
	}
	
	public float[] getXYZ()
	{
		return xyzw.clone();
	}
	
	public Vertex setRGBA(float r, float g, float b, float a)
	{
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = a;
		
		return this;
	}
	
	public float[] getRGBA()
	{
		return rgba.clone();
	}
	
	public Vertex setST(float s, float t)
	{
		st[0] = s;
		st[1] = t;
		
		return this;
	}
	
	public float[] getST()
	{
		return st.clone();
	}
	
	public Vertex setNormal(float x, float y, float z)
	{
		norm[0] = x;
		norm[1] = y;
		norm[2] = x;
		
		return this;
	}
	
	public float[] getNormal()
	{
		return norm.clone();
	}
}
