package com.gamelib.primitives;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.gamelib.core.Camera;
import com.gamelib.core.Shape;
import com.gamelib.core.Vertex;

public class Axis extends Shape
{
	private float[] VERTS = {	-1, 0, 0, 1,
								 1, 0, 0, 1,
								 0,-1, 0, 1,
								 0, 1, 0, 1,
								 0, 0,-1, 1,
								 0, 0, 1, 1 };
	
	private byte[] INDICES = { 0,1,2,3,4,5 };
	
	private float[] COLORS = {	1.0f, 0.0f, 0.0f, 1.0f,
								1.0f, 0.0f, 0.0f, 1.0f,
								0.0f, 1.0f, 0.0f, 1.0f,
								0.0f, 1.0f, 0.0f, 1.0f,
								0.0f, 0.0f, 1.0f, 1.0f,
								0.0f, 0.0f, 1.0f, 1.0f };
	
	public Axis()
	{
		super();
		
		loadVerts(VERTS);
		loadIndices(INDICES);
		setColor(COLORS);
		
		System.out.println("Axes created");
	}
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render()
	{
		Camera.getGlobal().use();
		
		//System.out.println("Rendering axes");		
		
		GL30.glBindVertexArray(getVertArray());
		GL20.glEnableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glEnableVertexAttribArray(Vertex.COL_ATTRIB);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer());		
		GL11.glDrawElements(GL11.GL_LINES, INDICES.length, GL11.GL_UNSIGNED_BYTE, 0);		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL20.glDisableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.COL_ATTRIB);
		GL30.glBindVertexArray(0);
	}
}
