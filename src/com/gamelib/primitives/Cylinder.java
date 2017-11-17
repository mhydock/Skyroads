package com.gamelib.primitives;


public class Cylinder extends Circular
{
	private static final float DEFAULT_HEIGHT = 1;
	
	private float height;
	private boolean isCapped;
	
	public Cylinder(float radius, float height, int slices)
	{
		this(radius, height, slices, true);
	}
	
	public Cylinder(float radius, float height, int slices, boolean isCapped)
	{
		super();
		
		this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
		this.height = height > 0 ? height : DEFAULT_HEIGHT;
		this.slices = slices > 2 ? slices : DEFAULT_SLICES;
		this.isCapped = isCapped;
		
		float[] vertices = makeVerts(this.radius, this.height, this.slices);
		int[] indices = makeIndices(vertices.length/4, this.slices);
		
		loadVerts(vertices);
		loadIndices(indices);
		
		setColor(DEFAULT_COLOR);
	}
	
	private float[] makeVerts(float radius, float height, int slices)
	{
		float[] vertices;
		
		if (isCapped && slices >= ARB_MIN)
			vertices = new float[(slices*2+2)*4];
		else
			vertices = new float[slices*2*4];
		
		int offset = 0;
		float[] temp = makeRing(radius, slices, new float[] {0f,height/2,0f});
		System.arraycopy(temp, 0, vertices, 0, temp.length);
		offset = temp.length;
		
		if (isCapped && slices >= ARB_MIN)
		{
			vertices[offset+0] = 0f;
			vertices[offset+1] = height/2;
			vertices[offset+2] = 0f;
			vertices[offset+3] = 1f;
			offset += 4;
		}
		
		temp = makeRing(radius, slices, new float[] {0f,-height/2,0f});
		System.arraycopy(temp, 0, vertices, offset, temp.length);
		offset += temp.length;

		if (isCapped && slices >= ARB_MIN)
		{
			vertices[offset+0] = 0f;
			vertices[offset+1] = -height/2;
			vertices[offset+2] = 0f;
			vertices[offset+3] = 1f;
		}
		
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
		
		return vertices;
	}
	
	private int[] makeIndices(int numVerts, int slices)
	{
		int[] indices;
		int index = 0;
		
		int ringVerts = (isCapped && slices < ARB_MIN)?slices:slices+1;
		
		if (isCapped)
			if (slices >= ARB_MIN)
				indices = new int[slices*4*3];		// (top + bottom + 2xFace/side) * 3xVerts/face
			else
				indices = new int[((slices-2)*2+slices*2)*3];	// (top + bottom + 2xFace/side) * 3xVerts/face
		else
			indices = new int[(slices*2)*3];	// (2xFace/side) * 3xVerts/face
		
		if (isCapped)
		{
			if (slices < ARB_MIN)
			{				
				for (int i = 0; i < slices-2; i++)
				{
					indices[index++] = 0;
					indices[index++] = (i+2);				
					indices[index++] = (i+1);
				}
			}
			else
			{				
				for (int i = 0; i < slices-1; i++)
				{
					indices[index++] = slices;
					indices[index++] = i+1;				
					indices[index++] = i;
				}
				
				indices[index++] = slices;
				indices[index++] = 0;
				indices[index++] = slices-1;
			}
		}
		
		for (int i = 0; i < slices-1; i++)
		{				
			indices[index++] = i;
			indices[index++] = i+1;
			indices[index++] = i+ringVerts;
			
			indices[index++] = i+1;
			indices[index++] = i+ringVerts+1;
			indices[index++] = i+ringVerts;
		}
		
		indices[index++] = slices-1;
		indices[index++] = 0;
		indices[index++] = slices-1+ringVerts;
		
		indices[index++] = 0;
		indices[index++] = ringVerts;
		indices[index++] = slices-1+ringVerts;
		
		if (isCapped)
		{
			int j = numVerts - ringVerts;

			if (slices < ARB_MIN)
			{		
				for (int i = 0; i < slices-2; i++)
				{
					indices[index++] = j;
					indices[index++] = j+(i+1);
					indices[index++] = j+(i+2);				
				}
			}
			else
			{
				for (int i = 0; i < slices-1; i++)
				{
					indices[index++] = j+slices;
					indices[index++] = j+i;
					indices[index++] = j+i+1;				
				}
				
				indices[index++] = j+slices;
				indices[index++] = j+slices-1;
				indices[index++] = j;
			}
		}
		
		/*
		for (int i = 0; i < indices.length; i++)
		{
			System.out.print(indices[i]);
			
			if ((i+1)%3 == 0)
				System.out.println();
			else
				System.out.print(" -> ");
		}
		*/
		
		return indices;
	}
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub

	}
}
