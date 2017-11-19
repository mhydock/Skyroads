package com.gamelib.primitives;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.gamelib.core.Camera;
import com.gamelib.core.Shape;
import com.gamelib.core.Vertex;

public class PointCloud extends Shape
{
	private List<float[]> points;	
	private boolean dirty;
	
	public PointCloud()
	{
		points = new ArrayList<float[]>();
	}
	
	public PointCloud(List<float[]> points)
	{
		points = new ArrayList<float[]>();
		
		addPoints(points);
	}
	
	public void addPoint(float[] point)
	{
		points.add(fixPoint(point));
		
		dirty = true;
	}
	
	public void addPoints(List<float[]> points)
	{
		for (float[] p : points)
			this.points.add(fixPoint(p));
		
		dirty = true;
	}
	
	public void dropAllPoints()
	{
		points.clear();
		
		dirty = true;
	}
	
	private float[] fixPoint(float[] point)
	{
		if (point.length == 4)
			return point;
		
		float[] newP = new float[4];
		for (int i = 0; i < newP.length; i++)
		{
			if (i < point.length)
				newP[i] = point[i];
			else
			{
				if (i == newP.length - 1)
					newP[i] = 1;
				else
					newP[i] = 0; 
			}
		}
		
		return newP;
	}
	
	private void loadPoints()
	{
		float[] allPoints = new float[points.size()*4];
		
		int i = 0;
		for (float[] p : points)
		{
			allPoints[i++] = p[0];
			allPoints[i++] = p[1];
			allPoints[i++] = p[2];
			allPoints[i++] = p[3];
		}
		
		loadVerts(allPoints);
	}
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void render()
	{
		if (dirty)
		{
			loadPoints();
			dirty = false;
		}
		
		Camera c = Camera.getGlobal();		
		c.setModel(getModel()).use();
		
		GL30.glBindVertexArray(getVertArray());
		GL20.glEnableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glEnableVertexAttribArray(Vertex.COL_ATTRIB);
		GL20.glEnableVertexAttribArray(Vertex.TEX_ATTRIB);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer());
		GL11.glDrawArrays(GL11.GL_POINTS, 0, points.size());		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL20.glDisableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.COL_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.TEX_ATTRIB);
		GL30.glBindVertexArray(0);
		
		c.resetModel();
	}
}
