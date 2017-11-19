package skyroads;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import static java.lang.Math.*;

import com.gamelib.core.Shape;
import com.gamelib.core.Vertex;

public class RadialMeter extends Shape
{
	private static final short RESET = Short.MAX_VALUE;
	
	private float startAngle;
	private float endAngle;
	private float gapSize;
	private float radius;
	private float radDist;
	
	private int slices;
	private float percent;
	private boolean isFilled;
	
	public RadialMeter(float start, float end, float radius, float radDist, int slices, float gapSize, boolean filled)
	{
		super();
		
		this.startAngle = (float)toRadians(start);
		this.endAngle = (float)toRadians(end);
		
		this.gapSize = (gapSize >= 0) ? (float)toRadians(gapSize) : 0;		
		this.slices = (slices >= 2) ? slices : 2;
		
		this.radius = (radius > 0) ? radius : 1;
		this.radDist = (radDist >= 0) ? radDist : 0;
		
		this.isFilled = filled;
		this.percent = 0;
		
		makeMesh();
	}
	
	public float getPercent()
	{
		return percent;
	}
	
	public void setPercent(float curr)
	{
		percent = curr;
	}
	
	public void setPercent(float max, float curr)
	{
		percent = curr/max;
	}
	
	public void setPercent(float min, float max, float curr)
	{
		percent = (curr-min)/(max-min);
	}
	
	private float[] makeVerts()
	{
		int dir = (endAngle-startAngle > 0) ? 1 : -1;

		//System.out.println((dir > 0)?"counter-clockwise":"clockwise");
		
		float totAngle = abs(endAngle-startAngle);
		float currAngle = startAngle;
		float sliceAngle = totAngle/slices;
		sliceAngle -= gapSize;

		float gap = dir*gapSize;
		float slice = dir*sliceAngle;		

		currAngle += gap/2;
		//System.out.println(gapSize + "  " + gap);
		//System.out.println(sliceAngle + "  " + slice);		
		
		int numVerts = slices;
		if (gapSize == 0)			// Triangle fan
		{
			numVerts += 1;
			if (radDist > 0)		// Quads
				numVerts *= 2;
		}
		else
		{
			if (radDist > 0)		// Quads
				numVerts *= 4;
			else					// Triangles
				numVerts = numVerts * 2 + 1;
		}

		float[] verts = new float[numVerts * 4];
		
		int i = 0;

		// Center vertex
		if (radDist == 0)
		{
			verts[i++] = 0.0f;
			verts[i++] = 0.0f;
			verts[i++] = 0.0f;
			verts[i++] = 1.0f;
		}
		
		float nextAngle = 0;
		while (i < verts.length)
		{	
			verts[i++] = (float)(cos(currAngle) * radius);
			verts[i++] = (float)(sin(currAngle) * radius);
			verts[i++] = 0.0f;
			verts[i++] = 1.0f;				
			
			if (radDist > 0)
			{
				verts[i++] = (float)(cos(currAngle) * radDist);
				verts[i++] = (float)(sin(currAngle) * radDist);
				verts[i++] = 0.0f;
				verts[i++] = 1.0f;	
			}
			
			if (gapSize > 0)
			{
				nextAngle = currAngle + slice;				
				verts[i++] = (float)(cos(nextAngle) * radius);
				verts[i++] = (float)(sin(nextAngle) * radius);
				verts[i++] = 0.0f;
				verts[i++] = 1.0f;
				
				if (radDist > 0)
				{
					verts[i++] = (float)(cos(nextAngle) * radDist);
					verts[i++] = (float)(sin(nextAngle) * radDist);
					verts[i++] = 0.0f;
					verts[i++] = 1.0f;	
				}
				
				currAngle = nextAngle + gap;
			}
			else
				currAngle += slice;
		}
		
		System.out.println("generated " + (verts.length/4) + " vertices");
		return verts;
	}
	
	private short[] makeIndexes()
	{
		short[] indexes = null;
		
		if (radDist == 0 && gapSize > 0)
		{
			int mult = 4;
			indexes = new short[slices*mult];
			
			int i = 0;
			
			for (int j = 1; i < indexes.length; j += 2)
			{
				indexes[i++] = 0;
				indexes[i++] = (short)j;
				indexes[i++] = (short)(j+1);
				indexes[i++] = RESET;
			}
		}		
		if (radDist > 0 && gapSize > 0)
		{
			indexes = new short[slices * 5]; // one quad + RESET => 5
			
			int i = 0;
			for (int j = 0; i < indexes.length; j += 4)
			{
				indexes[i++] = (short)j;
				indexes[i++] = (short)(j+1);
				indexes[i++] = (short)(j+3);
				indexes[i++] = (short)(j+2);
				indexes[i++] = RESET;
			}			
		}
		if (radDist > 0 && gapSize == 0 && !isFilled)
		{
			indexes = new short[slices * 4 + 2];
			
			int i = 0;
			indexes[i++] = 0;
			indexes[i++] = 1;
			
			for (int j = 0; i < indexes.length; j += 2)
			{
				indexes[i++] = (short)j;
				indexes[i++] = (short)(j+2);
				indexes[i++] = (short)(j+1);
				indexes[i++] = (short)(j+3);
			}			
		}
		
		if (indexes == null) indexes = new short[0];

		System.out.println("generated " + indexes.length + " indices");
		
		return indexes;
	}
	
	private void makeMesh()
	{
		float[] verts = makeVerts();
		short[] indexes = makeIndexes();
		
		/*
		for (int i = 0; i < verts.length; i++)
		{
			if (i > 0 && i % 4 != 0)
				System.out.print(", ");
			else
				System.out.println();

			System.out.print(verts[i]);
		}
		
		System.out.println();
		
		for (int i = 0; i < indexes.length; i++)
		{
			if (i > 0 && i % 5 != 0)
				System.out.print(", ");
			else
				System.out.println();

			System.out.print(indexes[i]);
		}
		
		System.out.println();
		*/
		
		loadVerts(verts);
		loadIndices(indexes);
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

		if (isFilled())
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		else
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		if (gapSize == 0 && radDist == 0)
			drawFan();
		if (gapSize > 0 && radDist == 0)
			drawTris();
		if (gapSize == 0 && radDist > 0)
			drawStrip();
		if (gapSize > 0 && radDist > 0)
			drawQuads();		
		
		GL20.glDisableVertexAttribArray(Vertex.POS_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.COL_ATTRIB);
		GL20.glDisableVertexAttribArray(Vertex.TEX_ATTRIB);
		GL30.glBindVertexArray(0);
	}
	
	private void drawFan()
	{
		double per = percent * getNumVerts();
		int tris = (int)ceil(per);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getVertBuffer());		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, tris);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void drawTris()
	{
		double per = percent * getNumIndices();

		per /= 3;
		per = ceil(per);
		per *= 3;
		
		int tris = (int)per;
		
		//System.out.println("need to draw " + tris/3 + " triangles");
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer());		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, tris);			
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private void drawQuads()
	{
		double per = percent * getNumIndices();

		per /= 5;
		per = ceil(per);
		per *= 5;
		
		int tris = (int)per;
		
		//System.out.println("need to draw " + tris/5 + " triangles");
		
		GL31.glPrimitiveRestartIndex(RESET);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer());
		
		if (isFilled)
			GL11.glDrawElements(GL11.GL_TRIANGLE_FAN, tris, getIndexMode(), 0);	
		else
			GL11.glDrawElements(GL11.GL_LINE_LOOP, tris, getIndexMode(), 0);	
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private void drawStrip()
	{
		if (isFilled)
		{			
			double per = percent * (getNumVerts() - 2);

			per /= 2;
			per = ceil(per);
			per *= 2;
			
			int segs = (int)per;
			
			if (segs > 0)
			{				
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getVertBuffer());
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, segs + 2);		
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			}
		}
		else
		{
			double per = percent * (getNumIndices() - 2);

			per /= 4;
			per = ceil(per);
			per *= 4;
			
			int segs = (int)per - 2;	// plus 2 for beginning segment, minus 4 for end cap.
			
			if (segs > 0)
			{				
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer());
				GL11.glDrawElements(GL11.GL_LINES, segs, getIndexMode(), 0);
				GL11.glDrawElements(GL11.GL_LINE_STRIP, 4, getIndexMode(), segs*2);		
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			}	
		}		
	}
}
