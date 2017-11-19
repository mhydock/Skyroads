package com.asymptote.gamelib.primitives;

public class Cone extends Circular
{
	private static final float DEFAULT_HEIGHT = 1;
	
	private float height;	
	
	public Cone (float radius, float height, int slices)
	{
		super();
		
		this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
		this.height = height > 0 ? height : DEFAULT_HEIGHT;
		this.slices = slices > 2 ? slices : DEFAULT_SLICES;
		
		int verts = slices+1;
		
		if (slices >= ARB_MIN)
			verts++;
		
		float[] vertices = new float[verts*4];
		
		if (slices < ARB_MIN)
			System.arraycopy(makeRing(radius, slices, new float[] {0f,0f,0f}), 0, vertices, 0, slices*4);
		else
		{
			System.arraycopy(makeRing(radius, slices, new float[] {0f,0f,0f}), 0, vertices, 4, slices*4);
			vertices[0] = 0.0f;
			vertices[1] = 0.0f;
			vertices[2] = 0.0f;
			vertices[3] = 1.0f;
		}
		
		vertices[vertices.length-4] = 0.0f;
		vertices[vertices.length-3] = height;
		vertices[vertices.length-2] = 0.0f;
		vertices[vertices.length-1] = 1.0f;
		
		/*
		for (int i = 0; i < vertices.length; i++)
		{
			System.out.print(vertices[i]);
			
			if ((i+1)%4 == 0)
				System.out.println();
			else
				System.out.print(" , ");
		}
		*/
		
		short[] indices;
		int index = 0;
		
		if (slices < ARB_MIN)
		{
			indices = new short[(2*slices-2)*3];
			
			for (int i = 0; i < slices-2; i++)
			{
				indices[index++] = 0;
				indices[index++] = (short)(i+1);
				indices[index++] = (short)(i+2);	
			}
			
			for (int i = 0; i < slices-1; i++)
			{
				indices[index++] = (short)slices;
				indices[index++] = (short)(i+1);
				indices[index++] = (short)i;
			}
			
			indices[index++] = (short)slices;
			indices[index++] = (short)0;
			indices[index++] = (short)(slices-1);
		}
		else
		{	
			indices = new short[(2*slices)*3];
			
			for (int i = 0; i < slices; i++)
			{
				indices[index++] = 0;
				indices[index++] = (short)(i+1);
				indices[index++] = (short)(i+2);
				
				indices[index++] = (short)(verts-1);
				indices[index++] = (short)(i+2);
				indices[index++] = (short)(i+1);
			}
			
			indices[index++] = 0;
			indices[index++] = 1;
			indices[index++] = (short)slices;
			
			indices[index++] = (short)(verts-1);
			indices[index++] = 1;
			indices[index++] = (short)slices;
		}
		
		loadVerts(vertices);
		loadIndices(indices);
		
		setColor(DEFAULT_COLOR);
	}
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub

	}
	
	public float getHeight()
	{
		return height;
	}
}
