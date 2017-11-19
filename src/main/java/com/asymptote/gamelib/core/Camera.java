//==============================================================================
// Date Created:		08 April 2014
// Last Updated:		22 July 2014
//
// File name:			Camera.java
// Author(s):			M. Matthew Hydock
//
// File description:	A class to manage camera position/orientation.
//==============================================================================

package com.asymptote.gamelib.core;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static java.lang.Math.*;

public class Camera
{
	private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

	private static Camera global;
	
	private Vector3f pos = new Vector3f(0,0,0);
	private Vector3f scale = new Vector3f(1,1,1);
	
	private Matrix4f orient;
	
	float fov;
	float aspect;
	float zNear;
	float zFar;

	Matrix4f proj;
	Matrix4f view;
	Matrix4f model;
	Matrix4f mvp;
		
	boolean projChanged;
	boolean viewChanged;
	boolean modelChanged;
	
	FloatBuffer mvpBuffer;
	
	public Camera()
	{
		view = new Matrix4f();
		mvp = new Matrix4f();	
		
		orient = new Matrix4f();
		
		projChanged = true;
		viewChanged = true;
		modelChanged = true;
		
		mvpBuffer = BufferUtils.createFloatBuffer(16);
		
		if (global == null)
			global = this;
	}
	
	public static Camera getGlobal()
	{
		return global;
	}
	
	public Camera use()
	{
		global = this;
		
		ShaderProgram prog = ShaderProgram.getGlobal();
		if (prog != null)
			prog.use();
		
		//System.out.println(model.toString());
		
		return this;
	}
	
	public Camera setOrtho(float left, float right, float top, float bottom, float near, float far)
	{
		return this;
	}
	
	public Camera setFrustum(float width, float height, float fov, float zNear, float zFar)
	{
		//proj = new Matrix4f();
		//proj.setFrustum(pos.x-(width/2), pos.x+(width/2), pos.y-(height/2), pos.y+(height/2), zNear, zFar);
		
		this.fov = fov;
		this.aspect = width/height;
		this.zNear = zNear;
		this.zFar = zFar;
		
		float zm = zFar-zNear;
		float zp = zFar+zNear;
		
		float y_scale = 1.0f/(float)Math.tan(Math.toRadians(fov/2));
		float x_scale = y_scale/aspect;
		
		proj = new Matrix4f();
		
		proj.m00 = x_scale;
		proj.m11 = y_scale;
		proj.m22 = -zp/zm;
		proj.m23 = -1;
		proj.m32 = -(2*zFar*zNear)/zm;
		proj.m33 = 0;
		
		projChanged = true;
		
		return this;
	}
	
	public Camera moveTo(float x, float y, float z)
	{
		pos.x = x;
		pos.y = y;
		pos.z = z;
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera move(float x, float y, float z)
	{
		pos.x += x;
		pos.y += y;
		pos.z += z;
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera roll(float angle)
	{		
		orient.rotate((float)Math.toRadians(angle), Z_AXIS);
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera pitch(float angle)
	{	
		orient.rotate((float)Math.toRadians(angle), X_AXIS);
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera yaw(float angle)
	{		
		orient.rotate((float)Math.toRadians(angle), Y_AXIS);
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera rotateCamera(float w, float x, float y, float z)
	{
		//System.out.println("rotating camera using angle: " + (float)Math.toRadians(w));
		Matrix4f temp = new Matrix4f().identity();
		temp.rotate((float)Math.toRadians(w), new Vector3f(x, y, z));
		
		//System.out.println("temp rotation matrix:\n" + temp.toString());
		
		orient.mul(temp);
		//System.out.println("orientation matrix:\n" + orient.toString());
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera lookAt(float x, float y, float z)
	{
		lookAt(x, y, z, 0, 1, 0);
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera lookAt(float x, float y, float z, float upX, float upY, float upZ)
	{
		// Vector from eye to point (the new z-axis)
		Vector3f forward = new Vector3f(x-pos.x, y-pos.y, z-pos.z);
		forward.normalize();
		
		// Base up, to establish where to place side.
		Vector3f baseUp = new Vector3f(upX,upY,upZ);
		baseUp.normalize();
		
		// The x and y axis of the new orthonormal basis.
		Vector3f right = new Vector3f();
		Vector3f up = new Vector3f();
		
		// Obtain and normalize the new x-axis.
		forward.cross(baseUp, right);
		
		// obtain and normalize the new y-axis.
		right.cross(forward, up);
		
		orient.m00 = right.x;
		orient.m10 = right.y;
		orient.m20 = right.z;
		
		orient.m01 = up.x;
		orient.m11 = up.y;
		orient.m21 = up.z;
		
		orient.m02 = -forward.x;
		orient.m12 = -forward.y;
		orient.m22 = -forward.z;
		
		/*
		//System.out.println(forward.toString() + "\n" + up.toString() + "\n" + right.toString());

		Vector3f axis = Vector3f.cross(new Vector3f(0,0,-1), forward, null);

		double dot = Vector3f.dot(new Vector3f(0,0,-1), forward);
		double r = acos(dot)/2;
		double s = sin(r);
		
		System.out.println("r: " + r + "\ns: " + s);
		
		orient.w = (float)cos(r);
		orient.x = (float)s*axis.x;	
		orient.y = (float)s*axis.y;	
		orient.z = (float)s*axis.z;		

		System.out.println(orient.toString());
		*/
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera lookAt(float eyeX, float eyeY, float eyeZ,
					   float atX,  float atY,  float atZ,
					   float upX,  float upY,  float upZ)
	{
		return moveTo(eyeX, eyeY, eyeZ).lookAt(atX, atY, atZ, upX, upY, upZ);
	}	
	
	public Camera rotateAbout(float w, float x, float y, float z, float ox, float oy, float oz)
	{
		//System.out.println("angle in degrees: " + w);
		rotateCamera(w, x, y, z);

		double rot = toRadians(-w);		// because Math hates degrees
		double l = sqrt(x*x+y*y+z*z);	// for normalization
		double s = sin(rot/2)/l;		// because it's used multiple times
				
		// Rotation quaternion.
		Quaternionf q1 = new Quaternionf((float)(x*s),
										 (float)(y*s),
										 (float)(z*s),
										 (float)cos(rot/2));
		
		// Turn position into vector, with point as origin.
		Quaternionf q2 = new Quaternionf(pos.x-ox, pos.y-oy, pos.z-oz, 0);
		
		Quaternionf q3 = new Quaternionf();
		Quaternionf q4 = new Quaternionf();
		
		// Rotate position vector. THIS IS NOT COMMUTATIVE.
		q1.mul(q2, q3);
		//Quaternion.mulInverse(q3, q1, q4);
		q3.div(q1, q4);
		
		// Extract location, shift back.
		pos.x = q4.x + ox;
		pos.y = q4.y + oy;
		pos.z = q4.z + oz;		
		
		//System.out.println("quaternion value: " + q1.toString());
		//System.out.println("quaternion value: " + q2.toString());
		//System.out.println("quaternion value: " + q3.toString());
		//System.out.println("quaternion value: " + q4.toString());
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera scale(float x, float y, float z)
	{
		scale.x = x;
		scale.y = y;
		scale.z = z;
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera resetView()
	{
		pos.set(0, 0, 0);
		scale.set(1, 1, 1);		
		orient.identity();
		
		viewChanged = true;
		
		return this;
	}
	
	public Camera resetModel()
	{
		model = null;
		
		modelChanged = true;
		
		return this;
	}
	
	public Camera setModel(Matrix4f model)
	{
		this.model = model;
		
		modelChanged = true;
		
		return this;
	}
	
	private void recalcView()
	{
		//System.out.println("Pos:\n" + pos.toString());

		view.identity().translate(-pos.x, -pos.y, -pos.z);
		orient.mul(view, view);
		view.scale(scale);

		//System.out.println("View:\n" + view.toString());

		viewChanged = false;		
	}
	
	private void recalcMVP()
	{
		if (viewChanged)
			recalcView();
		
		if (model != null)
		{
			view.mul(model, mvp);
			//System.out.println("ModelView:\n" + mvp.toString());
			proj.mul(mvp, mvp);
		}
		else
			proj.mul(view, mvp);
		
		//System.out.println("ModelViewProjection:\n" + mvp.toString());
	}
	
	public FloatBuffer getMVP()
	{
		if (modelChanged || viewChanged || projChanged)
		{
			recalcMVP();
			
			mvpBuffer.clear();
			mvp.get(mvpBuffer);
			mvpBuffer.flip();
			mvpBuffer.limit(16);

			/*
			System.out.println("\nBuffer:");
			for (int i = 0; i < mvpBuffer.capacity(); i++)
				System.out.print(i + ": " + mvpBuffer.get(i) + "| ");
			*/
			
			modelChanged = false;
			viewChanged = false;
			projChanged = false;
		}
		
		return mvpBuffer;
	}
}
