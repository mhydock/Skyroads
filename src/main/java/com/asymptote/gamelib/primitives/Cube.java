package com.asymptote.gamelib.primitives;

import com.asymptote.gamelib.graphics.Renderable;

public class Cube extends Renderable
{
	private final float[] VERTS = {	0,0,0,1,
									1,0,0,1,
									1,0,1,1,
									0,0,1,1,									
									
									0,1,0,1, 
									0,1,1,1,
									1,1,1,1,
									1,1,0,1 };
	
	private final byte[] INDICES = { 0, 1, 2, 0, 2, 3,		// bottom
									 0, 4, 7, 0, 7, 1,		// back
									 7, 6, 2, 7, 2, 1,		// right
									 6, 5, 3, 6, 3, 2,		// front
									 4, 0, 3, 4, 3, 5,		// left
									 4, 5, 6, 4, 6, 7 };	// top
		
	private final float DEFAULT_SIZE = 1;
	private float size = DEFAULT_SIZE;	
	
	public Cube()
	{
		super();
		
		loadVerts(VERTS);
		loadIndices(INDICES);
		
		setColor(DEFAULT_COLOR);
	}
	
	public Cube(float size)
	{
		super();
		
		this.size = (size > 0) ? size : DEFAULT_SIZE;
		
		float[] verts = new float[VERTS.length];
		
		for (int i = 0; i < verts.length; i++)
			verts[i] = VERTS[i]*size;
		
		loadVerts(verts);
		loadIndices(INDICES);
		
		setColor(DEFAULT_COLOR);
		
		System.out.println("Cube initialized");
	}
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub

	}
	
	public float getSize()
	{
		return size;
	}
}
