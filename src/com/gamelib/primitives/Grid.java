package com.gamelib.primitives;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.gamelib.core.Camera;
import com.gamelib.core.Shape;
import com.gamelib.core.Vertex;

public class Grid extends Shape
{
	private final int DEFAULT_ROWS = 1;
	private final int DEFAULT_COLS = 1;
	
	private int rows;
	private int cols;
	private float width;
	private float height;
	private boolean isFilled;
	
	public Grid(int r, int c, float spacing, boolean filled)
	{
		this.rows = (r >= DEFAULT_ROWS)?r:DEFAULT_ROWS;
		this.cols = (c >= DEFAULT_COLS)?c:DEFAULT_COLS;
		this.width = (cols)*spacing;
		this.height = (rows)*spacing;		
		this.isFilled = filled;
		
		r++;
		c++;
		
		if (!filled)
		{
			float[] vertices = new float[(r+c)*2*4];
			int index = 0;
			for (int i = 0; i < r; i++)
			{
				vertices[index++] = -width/2;
				vertices[index++] = 0.0f;
				vertices[index++] = -height/2 + i*spacing;
				vertices[index++] = 1.0f;
				
				vertices[index++] = width/2;
				vertices[index++] = 0.0f;
				vertices[index++] = -height/2 + i*spacing;
				vertices[index++] = 1.0f;
			}
			
			for (int i = 0; i < c; i++)
			{
				vertices[index++] = -width/2 + i*spacing;
				vertices[index++] = 0.0f;
				vertices[index++] = -height/2;
				vertices[index++] = 1.0f;
				
				vertices[index++] = -width/2 + i*spacing;
				vertices[index++] = 0.0f;
				vertices[index++] = height/2;
				vertices[index++] = 1.0f;
			}
			
			loadVerts(vertices);
		}
		else
		{
			float[] vertices = new float[r*c*4];
			int index = 0;
			
			for (int i = 0; i < r; i++)
				for (int j = 0; j < c; j++)
				{
					vertices[index++] = -width/2 + j*spacing;
					vertices[index++] = 0.0f;
					vertices[index++] = -height/2 + i*spacing;
					vertices[index++] = 1.0f;
				}
			
			short[] indices = new short[rows*cols*6];
			
			index = 0;
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < cols; j++)
				{				
					indices[index++] = (short)(i*c+j+1);
					indices[index++] = (short)(i*c+j);
					indices[index++] = (short)((i+1)*c+j);
					
					indices[index++] = (short)(i*c+j+1);
					indices[index++] = (short)((i+1)*c+j);
					indices[index++] = (short)((i+1)*c+j+1);
				}
			
			loadVerts(vertices);
			loadIndices(indices);
		}
		
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
		if (isFilled)
		{
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
			GL11.glDrawArrays(GL11.GL_LINES, 0, ((rows+1)*(cols+1)*4));		
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			
			GL20.glDisableVertexAttribArray(Vertex.POS_ATTRIB);
			GL20.glDisableVertexAttribArray(Vertex.COL_ATTRIB);
			GL20.glDisableVertexAttribArray(Vertex.TEX_ATTRIB);
			GL30.glBindVertexArray(0);
			
			c.resetModel();	
		}
	}
}
