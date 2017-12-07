package com.asymptote.skyroads;

import com.asymptote.gamelib.core.Renderable;

public class Panel extends Renderable
{
	private int width;
	private int height;
	private int length;
	
	private int horzPos;
	private int distance;
	private int altitude;
	
	private PanelType type;
	private float[] color;
		
	public Panel(int horzPos, int distance, int altitude, int width, int length, int height, String type, float[] color)
	{
		super();
		
		this.horzPos = horzPos;
		this.distance = distance;
		this.altitude = altitude;
		
		this.width = width;
		this.length = length;
		this.height = height;
		
		this.type = PanelType.valueOf(type.toUpperCase());
		if (this.type == null) this.type = PanelType.NORMAL;
		
		this.color = color;
		if (this.type != PanelType.NORMAL) this.color = this.type.getValue();
		
		//System.out.println(this.type + "   " + this.color);
		
		createMesh();
	}
	
	private void createMesh()
	{		
		int x = horzPos;
		int y = distance;
		int z = altitude;
		
		float[] VERTS = {	x		,y+length	,z			,1, 
							x+width	,y+length	,z			,1,
							x+width	,y+length	,z-height	,1,
							x		,y+length	,z-height	,1,
							                       
							x		,y			,z			,1,
							x		,y			,z-height	,1,
							x+width	,y			,z-height	,1,
							x+width	,y			,z			,1	};

		byte[] INDICES = {	0, 1, 2, 0, 2, 3,		// bottom
							0, 4, 7, 0, 7, 1,		// back
							7, 6, 2, 7, 2, 1,		// right
							6, 5, 3, 6, 3, 2,		// front
							4, 0, 3, 4, 3, 5,		// left
							4, 5, 6, 4, 6, 7 };		// top
		
		loadVerts(VERTS);
		loadIndices(INDICES);
		
		if (color == null)
			setColor(DEFAULT_COLOR);
		else
			setColor(color);
		
		//System.out.println("done making panel");
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getLength()
	{
		return length;
	}

	public int getHorzPos()
	{
		return horzPos;
	}

	public int getDistance()
	{
		return distance;
	}

	public int getAltitude()
	{
		return altitude;
	}

	public PanelType getType()
	{
		return type;
	}
	
	public boolean isColliding(float[] pos)
	{
		return isColliding(pos[0], pos[1], pos[2]);
	}
	
	public boolean isColliding(float x, float y, float z)
	{
		return 	(x >= this.horzPos && x <= this.horzPos+width) &&
				(y >= this.distance && y <= this.distance+length) &&
				(z <= this.altitude && z >= this.altitude-height);
	}

	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString()
	{
		return "p " + horzPos + " " + distance + " " + altitude + " " + length + " " + width + " " + height + 
				" " + type.toString() + " " + color[0] + " " + color[1] + " " + color[2] + " " + color[3];
	}
}
