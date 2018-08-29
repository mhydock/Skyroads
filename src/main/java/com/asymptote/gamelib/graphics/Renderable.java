//==============================================================================
// Date Created:		09 April 2014
// Last Updated:		23 July 2014
//
// File name:			Renderable.java
// Author(s):			M. Matthew Hydock
//
// File description:	An abstract class for rendering generic shapes. The base
//						of all primitives, and anything else that would need to
//						load and render vertex data.
//==============================================================================

package com.asymptote.gamelib.graphics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
	
import com.asymptote.gamelib.core.Camera;
import com.asymptote.gamelib.core.GameObject;
import com.asymptote.gamelib.core.Transformable;

public abstract class Renderable extends Transformable implements GameObject, IGLObject
{
	public static final float[] DEFAULT_COLOR = {0.5f,0.5f,0.5f,1.0f};

	private FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
		
	private int vaoID;
	private int vboID;
	private int vboIndexID;
	
	private int numVerts;
	private int numIndices;
	
	private int vertStorageMode;
	private int indexStorageMode;
	
	private boolean isFilled;
	
	public Renderable()
	{
		vaoID = GL30.glGenVertexArrays();
		vboID = GL15.glGenBuffers();
		
		vertStorageMode = GL15.GL_STATIC_DRAW;
		indexStorageMode = GL11.GL_UNSIGNED_BYTE;
		
		isFilled = true;
		
		GL30.glBindVertexArray(vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(0), vertStorageMode);
		GL20.glVertexAttribPointer(Vertex.POS_ATTRIB, Vertex.POS_COUNT, GL11.GL_FLOAT, false, Vertex.STRIDE, Vertex.POS_OFFSET);
		GL20.glVertexAttribPointer(Vertex.COL_ATTRIB, Vertex.COL_COUNT, GL11.GL_FLOAT, false, Vertex.STRIDE, Vertex.COL_OFFSET);
		GL20.glVertexAttribPointer(Vertex.TEX_ATTRIB, Vertex.TEX_COUNT, GL11.GL_FLOAT, false, Vertex.STRIDE, Vertex.TEX_OFFSET);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);		
		
		GL30.glBindVertexArray(0);
	}

	public void free()
	{
		GL30.glBindVertexArray(vaoID);
		
		GL20.glDisableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.COL_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.TEX_ATTRIB);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboID);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboIndexID);
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoID);
	}
	
	protected void loadInterleaved(Vertex[] verts)
	{
		numVerts = verts.length;
		
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(verts.length*Vertex.NUM_ELEMENTS);

		for (Vertex vert : verts)
			vertBuffer.put(vert.getInterleaved());
		
		vertBuffer.flip();
		
		replaceVertexBuffer(vertBuffer);
	}
	
	protected void loadInterleaved(float[] verts)
	{
		numVerts = verts.length/Vertex.NUM_ELEMENTS;
		
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(verts.length);
		vertBuffer.put(verts);
		vertBuffer.flip();
		
		replaceVertexBuffer(vertBuffer);
	}
	
	protected void loadVerts(float[] verts)
	{
		int oldNumVerts = numVerts;
		numVerts = verts.length/Vertex.POS_COUNT;
		
		if (numVerts != oldNumVerts)
		{
			//System.out.println("New number of vertices doesn't match old number. Need to rebuild buffer.");
			
			FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(numVerts*Vertex.NUM_ELEMENTS);
			replaceVertexBuffer(vertBuffer);
			
			setColor(DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2], DEFAULT_COLOR[3]);
		}
		
		//System.out.println("numVerts: " + numVerts);
		
		placeInterleavedData(verts, Vertex.POS_COUNT, Vertex.POS_OFFSET);
	}
	
	protected void loadIndices(byte[] indices)
	{
		numIndices = indices.length;

		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		if (vboIndexID != 0)
		{
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL15.glDeleteBuffers(vboIndexID);
		}
			
		vboIndexID = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndexID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, vertStorageMode);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		indexStorageMode = GL11.GL_UNSIGNED_BYTE;
	}
	
	protected void loadIndices(short[] indices)
	{
		numIndices = indices.length;
		
		ShortBuffer indicesBuffer = BufferUtils.createShortBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();		
		
		if (vboIndexID != 0)
		{
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL15.glDeleteBuffers(vboIndexID);
		}
			
		vboIndexID = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndexID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, vertStorageMode);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		indexStorageMode = GL11.GL_UNSIGNED_SHORT;
	}
	
	protected void loadIndices(int[] indices)
	{
		numIndices = indices.length;
		
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();		
		
		if (vboIndexID != 0)
		{
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL15.glDeleteBuffers(vboIndexID);
		}
			
		vboIndexID = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndexID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, vertStorageMode);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		indexStorageMode = GL11.GL_UNSIGNED_INT;
	}
	
	protected void loadTexCoords(float[] coords)
	{
		int verts = coords.length/Vertex.TEX_COUNT;
		
		if (numVerts != verts)
		{
			System.out.println("Number of texture coordinates does not match number of vertices. Not applying texture coordinates...");
			return;
		}
		
		placeInterleavedData(coords, Vertex.TEX_COUNT, Vertex.TEX_OFFSET);
	}
	
	public Renderable setStatic(boolean isStatic)
	{
		if (isStatic)
			vertStorageMode = GL15.GL_STATIC_DRAW;
		else
			vertStorageMode = GL15.GL_STREAM_DRAW;
		
		return this;
	}
	
	public Renderable setFillMode(boolean isFilled)
	{
		this.isFilled = isFilled;
		
		return this;
	}
	
	public boolean isFilled()
	{
		return isFilled;
	}
	
	public Renderable setColor(int color)
	{
		// Extract the channels from the int.
		float a = color & 0xFF;
		float b = (color >>> 8) & 0xFF;
		float g = (color >>> 16) & 0xFF;
		float r = (color >>> 24) & 0xFF;
		
		// Normalize the channels.
		r /= 255f;
		g /= 255f;
		b /= 255f;
		a /= 255f;
		
		return setColor(r, g, b, a);
	}
	
	public Renderable setColor(float r, float g, float b, float a)
	{
		float[] color = new float[numVerts*Vertex.COL_COUNT];
		
		for (int i = 0; i < color.length; i += 4)
		{
			color[i] = r;
			color[i+1] = g;
			color[i+2] = b;
			color[i+3] = a;
		}	
		
		return setColor(color);		
	}
	
	public Renderable setColor(float[] color)
	{
		if (color == null)
			return setColor(DEFAULT_COLOR);
		
		if (color.length < Vertex.COL_COUNT)
			throw new RuntimeException("Array too small to represent a RGBA color.");
		if (color.length % Vertex.COL_COUNT != 0)
			throw new RuntimeException("Array size not a multiple of 4 (components are missing).");
		
		if (color.length == Vertex.COL_COUNT)
			setColor(color[0], color[1], color[2], color[3]);
		else if (color.length/Vertex.COL_COUNT != numVerts)
			throw new RuntimeException("Number of specified colors does not match number of vertices.");
		else
			placeInterleavedData(color, Vertex.COL_COUNT, Vertex.COL_OFFSET);
		
		return this;
	}
	
	public int getVertArray()
	{
		return vaoID;
	}
	
	public int getVertBuffer()
	{
		return vboID;
	}
	
	public int getIndexBuffer()
	{
		return vboIndexID;
	}
	
	public int getNumVerts()
	{
		return numVerts;
	}

	public int getNumIndices()
	{
		return numIndices;
	}
	
	public int getVertMode()
	{
		return vertStorageMode;
	}
	
	public int getIndexMode()
	{
		return indexStorageMode;
	}
	
	public void render()
	{
		Camera c = Camera.getGlobal();
		
		c.setModelMatrix(getModelMatrix());
		c.use();
		c.resetModelMatrix();
				
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glEnableVertexAttribArray(Vertex.COL_ATTRIB);
		GL20.glEnableVertexAttribArray(Vertex.TEX_ATTRIB);
		
		if (isFilled)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		else
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndexID);
		GL11.glDrawElements(GL11.GL_TRIANGLES, numIndices, indexStorageMode, 0);		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL20.glDisableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.COL_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.TEX_ATTRIB);
		GL30.glBindVertexArray(0);
	}
	
	public FloatBuffer getModelBuffer()
	{
		if (transformChanged())
		{	
			modelBuffer.clear();
			getModelMatrix().get(modelBuffer);
			modelBuffer.flip();
			modelBuffer.limit(16);
		}
		
		return modelBuffer;
	}
	
	private void replaceVertexBuffer(FloatBuffer buffer)
	{
		//System.out.println("vaoID: " + vaoID + "   vboID: " + vboID);
		
		GL30.glBindVertexArray(vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, vertStorageMode);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);		
		
		GL30.glBindVertexArray(0);
		
		int error = GL11.glGetError();
		if (error != GL11.GL_NO_ERROR)
			System.out.println("Error while replacing vertex buffers: " + error);
	}
	
	private void placeInterleavedData(float[] dataArray, int datumSize, int offset)
	{
		float[] data = new float[datumSize];
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(data.length);
		
		GL30.glBindVertexArray(vaoID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

		for (int i = 0; i < dataArray.length/datumSize; i++)
		{
			for (int j = 0; j < datumSize; j++)
				data[j] = dataArray[i*datumSize + j];
			
			dataBuffer.rewind();
			dataBuffer.put(data);
			dataBuffer.flip();
			
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset + i*Vertex.STRIDE, dataBuffer);
			
			//System.out.println("Iteration: " + i);
			
			int error = GL11.glGetError();
			if (error != GL11.GL_NO_ERROR)
				System.out.println("\nError while substituting vertex buffers: " + error);
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public Renderable setScale(float x, float y, float z)
	{
		return (Renderable)super.setScale(x, y, z);
	}
	
	public Renderable setOrigin(float x, float y, float z)
	{
		return (Renderable)super.setOrigin(x, y, z);
	}
	
	public Renderable setLocation(float x, float y, float z)
	{
		return (Renderable)super.setLocation(x, y, z);
	}
		
	public Renderable setOrientation(float angle, float x, float y, float z)
	{
		return (Renderable)super.setOrientation(angle, x, y, z);
	}
	
	public Renderable move(float x, float y, float z)
	{
		return (Renderable)super.move(x, y, z);
	}
	
	public Renderable rotate(float angle, float x, float y, float z)
	{
		return (Renderable)super.rotate(angle, x, y, z);
	}
	
	public Renderable resetTransformables()
	{
		return (Renderable)super.resetTransformables();
	}
}
