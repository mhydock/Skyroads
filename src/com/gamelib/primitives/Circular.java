package com.gamelib.primitives;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import com.gamelib.core.Shape;

public abstract class Circular extends Shape
{
	protected static final int ARB_MIN = 36;
	
	protected static final float DEFAULT_RADIUS = 1;
	protected static final int DEFAULT_SLICES = 10;
	
	protected float radius;
	protected int slices;
	
	protected static float[] makeRing(float radius, int slices, float[] origin)
	{
		float[] verts = new float[4*slices];
		float sliceAngle = (float)toRadians(360f/slices);
		float totalAngle = 0;
		
		for (int i = 0; i < slices; i++)
		{
			verts[i*4+0] = (float)cos(totalAngle) * radius + origin[0];
			verts[i*4+1] = origin[1];
			verts[i*4+2] = (float)sin(totalAngle) * radius + origin[2];
			verts[i*4+3] = 1.0f;
			
			totalAngle += sliceAngle;
		}
		
		return verts;
	}
}
