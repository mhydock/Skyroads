//==============================================================================
// Date Created:		03 February 2015
// Last Updated:		03 February 2015
//
// File name:			Vertex.java
// Author(s):			M. Matthew Hydock
//
// File description:	A class that represents a single particle, with position,
//                      color, velocity, lifetime, and a delay.
//==============================================================================

package com.asymptote.gamelib.graphics;

public class Particle implements GameObject, IGLObject
{
	private float[] xyzw = new float[4];
	private float[] rgba = new float[4];
	
	private float[] velocity = new float[3];
	private float lifetime;
	private float delay;
	
	public Particle setXYZ(float x, float y, float z)
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
	
	public Particle setRGBA(float r, float g, float b, float a)
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
	
	public Particle setVelocity(float x, float y, float z)
	{
		velocity[0] = x;
		velocity[1] = y;
		velocity[2] = z;
		
		return this;
	}
	
	public float[] getVelocity()
	{
		return velocity.clone();
	}
	
	public Particle setLifetime(float lifetime)
	{
		this.lifetime = lifetime;
		
		return this;
	}
	
	public float getLifetime()
	{
		return lifetime;
	}
	
	public Particle setDelay(float delay)
	{
		this.delay = delay;
		
		return this;
	}
	
	public float getDelay()
	{
		return delay;
	}
	
	public boolean isDead()
	{
		return delay <= 0 && lifetime <= 0;
	}
	
	@Override
	public void free()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(double delta)
	{
		if (delay > 0)
		{
			delay -= delta;
			return;
		}
		
		lifetime -= delta;
		
		if (lifetime <= 0)
			return;
		
		// TODO Auto-generated method stub
		xyzw[0] += velocity[0]*delta;
		xyzw[1] += velocity[1]*delta;
		xyzw[2] += velocity[2]*delta;
	}
	
	@Override
	public void render()
	{
		// TODO Auto-generated method stub
		
	}

	
}
