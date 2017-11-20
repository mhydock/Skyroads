//==============================================================================
// Date Created:		09 April 2014
// Last Updated:		23 July 2014
//
// File name:			Shape.java
// Author(s):			M. Matthew Hydock
//
// File description:	An abstract class for rendering generic shapes. The base
//						of all primitives, and anything else that would need to
//						load and render vertex data.
//==============================================================================

package com.asymptote.gamelib.core;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
	
public abstract class Shape implements GameObject, IGLObject
{
	public static final float[] DEFAULT_COLOR = {0.5f,0.5f,0.5f,1.0f};
	
	private Vector3f pos = new Vector3f(0, 0, 0);
	private Vector3f scale = new Vector3f(1, 1, 1);

	private Vector3f origin = new Vector3f(0, 0, 0);

	private Quaternionf orient = new Quaternionf();

	private Matrix4f model = new Matrix4f();
	private FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
	
	private boolean changed = true;
	
	private int vaoID;
	private int vboID;
	private int vboIndexID;
	
	private int numVerts;
	private int numIndices;
	
	private int vertStorageMode;
	private int indexStorageMode;
	
	private boolean isFilled;
	
	public Shape()
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
	
	public Shape setStatic(boolean isStatic)
	{
		if (isStatic)
			vertStorageMode = GL15.GL_STATIC_DRAW;
		else
			vertStorageMode = GL15.GL_STREAM_DRAW;
		
		return this;
	}
	
	public Shape setFillMode(boolean isFilled)
	{
		this.isFilled = isFilled;
		
		return this;
	}
	
	public boolean isFilled()
	{
		return isFilled;
	}
	
	public Shape setColor(int color)
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
	
	public Shape setColor(float r, float g, float b, float a)
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
	
	public Shape setColor(float[] color)
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
	
	public Shape setScale(float x, float y, float z)
	{
		scale.x = x;
		scale.y = y;
		scale.z = z;
		
		changed = true;
		
		return this;
	}
	
	public Shape setOrigin(float x, float y, float z)
	{
		origin.x = -x;
		origin.y = -y;
		origin.z = -z;
		
		changed = true;
		
		return this;
	}
	
	public Shape setLocation(float x, float y, float z)
	{
		pos.x = x;
		pos.y = y;
		pos.z = z;
		
		changed = true;
		
		return this;
	}
		
	public Shape setOrientation(float angle, float x, float y, float z)
	{
		double s = sin(toRadians(angle)/2);
		
		Vector3f v = new Vector3f(x,y,z);
		v.normalize();
		
		orient.w = (float)cos(toRadians(angle)/2);
		orient.x = (float)s*v.x;
		orient.y = (float)s*v.y;
		orient.z = (float)s*v.z;
		
		orient.normalize();
		
		changed = true;
		
		return this;
	}
	
	public Shape move(float x, float y, float z)
	{
		pos.x += x;
		pos.y += y;
		pos.z += z;
		
		changed = true;
		
		return this;
	}
	
	public Shape rotate(float angle, float x, float y, float z)
	{
		double r = toRadians(angle)/2;	// because Math hates degrees
		double l = sqrt(x*x+y*y+z*z);	// for normalization
		double s = sin(r)/l;			// because it's used multiple times
		
		//System.out.println(angle + "  " + rotAngle + "  " + (float)cos(rotAngle/2));
		
		Quaternionf q1 = new Quaternionf();
		
		q1.w = (float)cos(r);
		q1.x = (float)s*x;
		q1.y = (float)s*y;
		q1.z = (float)s*z;
		
		//System.out.println(q1.toString() + "  " + orient.toString());
		
		q1.mul(orient, orient);
		
		//System.out.println(orient.toString());
		
		changed = true;
		
		return this;
	}
	
	public Shape resetTransforms()
	{
		setOrigin(0, 0, 0);
		setOrientation(0, 1, 1, 1);
		
		setScale(1, 1, 1);
		setLocation(0, 0, 0);
		
		orient.identity();
		
		changed = true;
		
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
		
		if (changed)
			recalculateModel();
		
		c.setModel(model);
		c.use();
		c.resetModel();
				
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
	
	public Matrix4f getModel()
	{
		if (changed)
			recalculateModel();
		
		return model;
	}
	
	public FloatBuffer getModelBuffer()
	{
		if (changed)
		{
			recalculateModel();
		
			modelBuffer.clear();
			model.get(modelBuffer);
			modelBuffer.flip();
			modelBuffer.limit(16);
		}
		
		return modelBuffer;
	}
	
	protected void recalculateModel()
	{
		double tempAngle = acos(orient.w)*2;
		double s = sin(tempAngle/2);
		
		Vector3f temp = new Vector3f();
		temp.x = (float)(orient.x/s);
		temp.y = (float)(orient.y/s);
		temp.z = (float)(orient.z/s);
		
		if (tempAngle == 0)
			temp.set(1,0,0);
		
		model.identity()
			 .translate(-origin.x, -origin.y, -origin.z)
			 .translate(pos)
			 .rotate((float)tempAngle, temp)
			 .scale(scale);
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
}
