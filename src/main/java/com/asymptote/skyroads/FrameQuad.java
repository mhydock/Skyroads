package com.asymptote.skyroads;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.asymptote.gamelib.core.Shape;
import com.asymptote.gamelib.core.Vertex;

public class FrameQuad extends Shape
{

	public FrameQuad()
	{
		Vertex[] verts = {
				new Vertex().setXYZ(-1f,  1f, 0).setST(0, 1),
				new Vertex().setXYZ(-1f, -1f, 0).setST(0, 0),
				new Vertex().setXYZ( 1f, -1f, 0).setST(1, 0),
				new Vertex().setXYZ( 1f,  1f, 0).setST(1, 1)
		};
		
		loadInterleaved(verts);
	}
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void render()
	{		
		GL30.glBindVertexArray(getVertArray());
		GL20.glEnableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glEnableVertexAttribArray(Vertex.COL_ATTRIB);
		GL20.glEnableVertexAttribArray(Vertex.TEX_ATTRIB);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getVertBuffer());		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, 4);		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL20.glDisableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.COL_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.TEX_ATTRIB);
		GL30.glBindVertexArray(0);
	}
}
