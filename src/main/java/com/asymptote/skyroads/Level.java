package skyroads;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.gamelib.primitives.Cube;

public class Level
{
	private final float[] DEFAULT_START = new float[] {0,0,0};
	
	private final float DRAW_DISTANCE = 100;
	
	public static final int HORZ_POS = 0;
	public static final int DISTANCE = 1;
	public static final int ALTITUDE = 2;
	
	public static final float GRAVITY_CONTANT = 65f;
	
	public static final float DEFAULT_GRAVITY = 500;
	public static final float DEFAULT_AIRLOSS = 1;
	public static final float DEFAULT_FUELLOSS = 1;
	public static final float DEFAULT_DEATHHEIGHT = -10;
	
	private List<Panel> panels;
	private Cube endMarker;
	
	private float gravity;
	private float airLoss;
	private float fuelLoss;
	
	private float[] start;
	private float[] finish;
	private float length;
	
	private float deathHeight;
	
	public static Level loadLevel(String path)
	{
		try
		{
			FileInputStream file = new FileInputStream(path);

			return new Level(file);
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File could not be found, so no level built.");
			return null;
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	private Level(InputStream stream) throws Exception
	{		
		Scanner reader = new Scanner(stream);
		panels = new ArrayList<Panel>();
		endMarker = (Cube)new Cube().setScale(2, 2, 2).setOrigin(-1f, -1f, 0).setColor(0xff0000ff);
		
		gravity = DEFAULT_GRAVITY; 
		airLoss = DEFAULT_AIRLOSS; 
		fuelLoss = DEFAULT_FUELLOSS;
		deathHeight = DEFAULT_DEATHHEIGHT;
		
		String line;
		while (reader.hasNext())
		{
			line = reader.nextLine();
			
			//System.out.println("processing - " + line);
			if (line.startsWith("p"))
			{
				Panel temp = makePanel(line);
				if (temp != null)
					panels.add(temp);
			}
			
			if (line.startsWith("env"))
				defineEnvironment(line);
			
			if (line.startsWith("s"))
				start = setMarker(line);
			
			if (line.startsWith("e"))
				finish = setMarker(line);
		}
		
		reader.close();
		
		if (start == null)
			start = DEFAULT_START;
		
		if (finish == null)
			throw new Exception("There is no end to this level, it is unwinnable.");
		
		// just the Z difference
		length = finish[DISTANCE] - start[DISTANCE];
		
		endMarker.setLocation(finish[0], finish[1], finish[2]);
	}
	
	private float[] setMarker(String line)
	{
		String[] param = line.split("\\s+");

		float[] s = new float[3];
		
		try
		{
			s[0] = Integer.parseInt(param[1]);
			s[1] = Integer.parseInt(param[2]);
			s[2] = Integer.parseInt(param[3]);
			
			return s;
		}
		catch (NumberFormatException e)
		{
			System.out.println("Incorrectly formatted marker position. All values must be integers.");
			return null;
		}		
	}
	
	private void defineEnvironment(String line)
	{
		//System.out.println("Redefining environment");
		
		String[] param = line.split("\\s+");
		
		try
		{
			gravity = Integer.parseInt(param[1]);
			airLoss = Integer.parseInt(param[2]);
			fuelLoss = Integer.parseInt(param[3]);
			deathHeight = Integer.parseInt(param[4]);
			
			//System.out.println(gravity + "  " + airLoss + "  " + fuelLoss);
		}
		catch (NumberFormatException e)
		{
			System.out.println(	"Level environment parameters are not in appropriate formats."+
								"Gravity, Air Loss rate, and Fuel Loss rate should be integers.");
			
		}
		catch (IndexOutOfBoundsException e)
		{
			System.out.println("Level environment definition incomplete. Using defaults for undefined parameters.");
		}
	}
	
	private Panel makePanel(String line)
	{
		String[] param = line.split("\\s+");
		
		//System.out.println("making panel - " + Arrays.toString(param));
		
		try
		{
			int horzPos = Integer.parseInt(param[1]);
			int distance = Integer.parseInt(param[2]);
			int altitude = Integer.parseInt(param[3]);
			
			int l = Integer.parseInt(param[4]);
			int w = Integer.parseInt(param[5]);
			int h = Integer.parseInt(param[6]);

			String type = param[7];
			
			float[] color = null;
			if (param.length == 12)
			{
				color = new float[4];
				color[0] = Float.parseFloat(param[8]);
				color[1] = Float.parseFloat(param[9]);
				color[2] = Float.parseFloat(param[10]);
				color[3] = Float.parseFloat(param[11]);
			}
			
			return new Panel(horzPos,distance,altitude,l,w,h,type,color);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Panel parameter incorrectly formatted. Make sure all position/dimensions are integers, and all color values are floats.");
			return null;
		}
		catch (IndexOutOfBoundsException e)
		{
			System.out.println("Not enough parameters to build a minimally defined panel. Make sure you define position, dimensions, and type at the least.");
			return null;
		}
		catch (Exception e)
		{
			System.out.println("I legit have no idea what happened.");
			e.printStackTrace();
			return null;
		}
	}
	
	public void render()
	{
		for (Panel p : panels)
		{
			//int d = p.getDistance();
			
			//if (d < depth + DRAW_DISTANCE)
				//if (depth <= d || depth <= d+p.getLength())
					p.render();
		}
		
		endMarker.render();
	}
		
	public float[] getStart()
	{
		return start;
	}
	
	public float[] getFinish()
	{
		return finish;
	}
	
	public float getGravityFactor()
	{
		return GRAVITY_CONTANT * gravity / DEFAULT_GRAVITY;
	}
	
	public float getGravity()
	{
		return gravity;
	}
	
	public float getAirLoss()
	{
		return airLoss;
	}
	
	public float getFuelLoss()
	{
		return fuelLoss;
	}	

	public float getDeathHeight()
	{
		return deathHeight;
	}
	
	public float getLength()
	{
		return length;
	}
		
	public Panel isColliding(float[] pos)
	{
		for (Panel panel : panels)
		{
			if (panel.isColliding(pos))
				return panel;
		}
		
		return null;
	}
	
	public boolean atEnd(float[] pos)
	{
		return 	(pos[0] < finish[0]+1 && pos[0] > finish[0]-1) &&
				(pos[1] < finish[1]+1 && pos[1] > finish[1]-1) &&
				(pos[2] < finish[2]+2 && pos[2] > finish[2]);
	}
}
