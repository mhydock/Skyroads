package com.asymptote.gamelib.core;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Matrix4f;

public abstract class Transformable {
    private Vector3f pos = new Vector3f(0, 0, 0);
	private Vector3f scale = new Vector3f(1, 1, 1);
	private Vector3f origin = new Vector3f(0, 0, 0);
    private Quaternionf orient = new Quaternionf();
    private Matrix4f model = new Matrix4f();

	private boolean changed = true;

	public boolean transformChanged()
	{
		return changed;
	}

	public Transformable setScale(float x, float y, float z)
	{
		scale.x = x;
		scale.y = y;
		scale.z = z;
		
		changed = true;
		
		return this;
	}
	
	public Transformable setOrigin(float x, float y, float z)
	{
		origin.x = x;
		origin.y = y;
		origin.z = z;
		
		changed = true;
		
		return this;
	}
	
	public Transformable setLocation(float x, float y, float z)
	{
		pos.x = x;
		pos.y = y;
		pos.z = z;
		
		changed = true;
		
		return this;
	}
		
	public Transformable setOrientation(float angle, float x, float y, float z)
	{
		double s = sin(toRadians(angle)/2);
		
		Vector3f v = new Vector3f(x,y,z);
		v.normalize();
		
		orient.w = (float)cos(toRadians(angle)/2);
		orient.x = (float)s*v.x;
		orient.y = (float)s*v.y;
		orient.z = (float)s*v.z;
		
		orient.normalize();
		
		changed = true;
		
		return this;
	}
	
	public Transformable move(float x, float y, float z)
	{
		pos.x += x;
		pos.y += y;
		pos.z += z;
		
		changed = true;
		
		return this;
	}
	
	public Transformable rotate(float angle, float x, float y, float z)
	{
		double r = toRadians(angle)/2;	// because Math hates degrees
		double l = sqrt(x*x+y*y+z*z);	// for normalization
		double s = sin(r)/l;			// because it's used multiple times
		
		//System.out.println(angle + "  " + rotAngle + "  " + (float)cos(rotAngle/2));
		
		Quaternionf q1 = new Quaternionf();
		
		q1.w = (float)cos(r);
		q1.x = (float)s*x;
		q1.y = (float)s*y;
		q1.z = (float)s*z;
		
		//System.out.println(q1.toString() + "  " + orient.toString());
		
		q1.mul(orient, orient);
		
		//System.out.println(orient.toString());
		
		changed = true;
		
		return this;
	}
	
	public Transformable resetTransformables()
	{
		setOrigin(0, 0, 0);
		setOrientation(0, 1, 1, 1);
		
		setScale(1, 1, 1);
		setLocation(0, 0, 0);
		
		orient.identity();
		
		changed = true;
		
		return this;
	}

	public Matrix4f getModelMatrix()
	{
		if (changed)
			recalculateModelMatrix();
		
		return model;
	}

	private void recalculateModelMatrix()
	{
		double tempAngle = acos(orient.w)*2;
		double s = sin(tempAngle/2);
		
		Vector3f temp = new Vector3f();
		temp.x = (float)(orient.x/s);
		temp.y = (float)(orient.y/s);
		temp.z = (float)(orient.z/s);
		
		if (tempAngle == 0)
			temp.set(1,0,0);
		
		model.identity()
			 .translate(origin)
			 .translate(pos)
			 .rotate((float)tempAngle, temp)
			 .scale(scale);
	}
}