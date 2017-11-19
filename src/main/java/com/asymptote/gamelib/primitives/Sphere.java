package com.asymptote.gamelib.primitives;

import java.util.ArrayList;

import static java.lang.Math.*;

public class Sphere extends Circular
{
	private static final int DEFAULT_RINGS = 10;
	
	private int rings;
	
	public Sphere(float radius, int rings, int slices)
	{
		super();
		
		// For sanitizing input, and for data retrieval?
		this.radius = radius > 0 ? radius : DEFAULT_RADIUS;
		this.rings = rings >= 1 ? rings : DEFAULT_RINGS;
		this.slices = slices > 2 ? slices : DEFAULT_SLICES;
		
		float[] vertices = makeVerts(this.radius, this.rings, this.slices);
		int[] indices = makeIndices(vertices.length/4, this.rings, this.slices);
		
		loadVerts(vertices);
		loadIndices(indices);
		
		setColor(DEFAULT_COLOR);
	}	
	
	private float[] makeVerts(float radius, int rings, int slices)
	{
		float ringAngle = (float)toRadians(180.0f / (rings+1));
		float totalAngle = 0;
		
		ArrayList<float[]> verts = new ArrayList<float[]>();
		for (int i = 0; i < rings; i++)
		{
			totalAngle += ringAngle;
			float r = (float)sin(totalAngle)*radius;
			float h = (float)cos(totalAngle)*radius;
			verts.add(makeRing(r, slices, new float[] {0f,h,0f}));
			
			//System.out.println("ring: " + i + "\ntotalAngle: " + totalAngle + "\nradius: " + r + "\nheight: " + h);
		}
		
		int numVerts = 8;	// including caps.
		for (float[] v : verts)
			numVerts += v.length;
		
		float[] vertices = new float[numVerts];
		int index = 0;
		
		vertices[index++] = 0;
		vertices[index++] = radius;
		vertices[index++] = 0;
		vertices[index++] = 1f;
		
		for (float[] v : verts)
			for (int i = 0; i < v.length; i++)
				vertices[index++] = v[i];
		
		vertices[index++] = 0;
		vertices[index++] = -radius;
		vertices[index++] = 0;
		vertices[index++] = 1f;
				
		return vertices;
	}
	
	public int[] makeIndices(int numVerts, int rings, int slices)
	{
		int[] indices = new int[(2*slices+(rings-1)*slices*2)*3]; // triangles for caps, quads (2x tris) for rings.
		int index = 0;
		
		for (int i = 0; i < slices-1; i++)
		{
			indices[index++] = 0;
			indices[index++] = i+2;
			indices[index++] = i+1;
		}
		
		indices[index++] = 0;
		indices[index++] = 1;
		indices[index++] = slices;
		
		//System.out.println("index: " + index);
		for (int i = 0; i < rings-1; i++)
		{
			int k = i*slices+1;
			
			for (int j = 0; j < slices-1; j++)
			{				
				indices[index++] = j+k;
				indices[index++] = j+k+1;
				indices[index++] = j+k+slices;
				
				indices[index++] = j+k+1;
				indices[index++] = j+k+slices+1;
				indices[index++] = j+k+slices;
			}
			
			indices[index++] = k+slices-1;
			indices[index++] = k;
			indices[index++] = k+slices-1+slices;
			
			indices[index++] = k;
			indices[index++] = k+slices;
			indices[index++] = k+slices-1+slices;
			
			//System.out.println("ring: " + i + "\nindex: " + index);
		}
		
		numVerts--;
		for (int i = 0; i < slices-1; i++)
		{
			indices[index++] = numVerts;
			indices[index++] = numVerts - (i+2);
			indices[index++] = numVerts - (i+1);
		}
		
		indices[index++] = numVerts;
		indices[index++] = numVerts-1;
		indices[index++] = numVerts-(slices);
		
		//System.out.println("index: " + index);

		return indices;
	}
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub

	}
}
