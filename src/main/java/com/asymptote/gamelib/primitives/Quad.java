package com.asymptote.gamelib.primitives;

import com.asymptote.gamelib.core.Renderable;

public class Quad extends Renderable
{
	private static float[] VERTS = {0,1,0,1,
									0,0,0,1,
									1,0,0,1,
									1,1,0,1 };
	
	private static byte[] INDICES = { 0,1,2, 2,3,0 };
	
	private static float[] TEXCOORDS = {0,0,
										0,1,
								  		1,1,
								  		1,0 };
	
	public Quad()
	{
		super();
		
		loadVerts(VERTS);
		loadIndices(INDICES);
		loadTexCoords(TEXCOORDS);
		
		setColor(DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2], DEFAULT_COLOR[3]);

	}

	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub
		
	}
}
