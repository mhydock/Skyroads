package com.asymptote.gamelib.primitives;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.asymptote.gamelib.graphics.Camera;
import com.asymptote.gamelib.graphics.Vertex;

public class Circle extends Circular
{	
	private boolean isRing;
	
	public Circle(float radius, int slices, boolean ring)
	{
		super();

		this.slices = slices;
		this.isRing = ring;
		this.radius = radius;
		
		int verts = slices;
		
		if (!isRing && slices >= ARB_MIN)
			verts++;
		
		float[] vertices = makeRing(radius, slices, new float[] {0f,0f,0f});		
		short[] indices;
			
		if (!isRing)
		{
			if (slices < ARB_MIN)
			{
				indices = new short[(slices-2)*3];
				int index = 0;

				for (int i = 0; i < slices-2; i++)
				{
					indices[index++] = 0;
					indices[index++] = (short)(i+2);
					indices[index++] = (short)(i+1);
					
					//System.out.println(indices[i*3+0] + " -> " + indices[i*3+1] + " -> " + indices[i*3+2]);
				}
			}
			else
			{
				float[] temp = vertices;
				vertices = new float[verts*4];
				
				System.arraycopy(temp, 0, vertices, 0, temp.length);
				
				int v = slices*4;
				vertices[v+0] = 0.0f;
				vertices[v+1] = 0.0f;
				vertices[v+2] = 0.0f;
				vertices[v+3] = 1.0f;
				
				indices = new short[slices*3];
				int index = 0;
				for (int i = 0; i < slices-1; i++)
				{
					indices[index++] = (short)(slices);
					indices[index++] = (short)(i+1);	
					indices[index++] = (short)(i);
					//System.out.println(vertices[indices[i*3+0]*4+2] + " -> " + vertices[indices[i*3+1]*4+2] + " -> " + vertices[indices[i*3+2]*4+2]);
				}
				
				int i = slices-1;
				indices[index++] = (short)(slices);
				indices[index++] = (short)(0);
				indices[index++] = (short)(i);
				//System.out.println(vertices[indices[i*3+0]*4+2] + " -> " + vertices[indices[i*3+1]*4+2] + " -> " + vertices[indices[i*3+2]*4+2]);
			}
			
			loadVerts(vertices);
			loadIndices(indices);
		}
		else
			loadVerts(vertices);
		
				
		setColor(DEFAULT_COLOR);
	}
	
	
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void render()
	{
		if (!isRing)
		{
			//System.out.println("rendering filled circle");
		
			super.render();
		}
		else
		{
			Camera c = Camera.getGlobal();			
			c.setModel(getModel()).use();
						
			GL30.glBindVertexArray(getVertArray());
			GL20.glEnableVertexAttribArray(Vertex.POS_ATTRIB);
			GL20.glEnableVertexAttribArray(Vertex.COL_ATTRIB);
			GL20.glEnableVertexAttribArray(Vertex.TEX_ATTRIB);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getVertBuffer());		
			GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, slices);		
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			
			GL20.glDisableVertexAttribArray(Vertex.POS_ATTRIB);
			GL20.glDisableVertexAttribArray(Vertex.COL_ATTRIB);
			GL20.glDisableVertexAttribArray(Vertex.TEX_ATTRIB);
			GL30.glBindVertexArray(0);			

			c.resetModel();
		}
	}
}
