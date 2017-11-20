package com.asymptote.skyroads;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL31.GL_PRIMITIVE_RESTART;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;

import com.asymptote.gamelib.core.Clock;
import com.asymptote.gamelib.core.GameWindow;
import com.asymptote.gamelib.core.Input;
import com.asymptote.gamelib.core.Shape;
import com.asymptote.gamelib.primitives.Axis;
import com.asymptote.gamelib.primitives.Grid;

public class Main extends GameWindow
{
	private LevelScene scene;
	
	private Level level;
	private Ship ship;
	
	public Main()
	{
		//Clock.setGoalFPS(30);
		
		init();
		
		glEnable(GL_BLEND);
		glEnable(GL_MULTISAMPLE);
		glEnable(GL_PROGRAM_POINT_SIZE);
		glEnable(GL_PRIMITIVE_RESTART);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		int error = GL11.glGetError();
		if (error != GL11.GL_NO_ERROR)
			System.out.println("Error while initializing graphics context: " + error);
		
		this.title = "SkyRoads";
		this.showFPS(true);
		this.showUPS(true);
		
		level = Level.loadLevel("res/testlevel");
		
		if (level == null)
			System.out.println("level creation failed");
		
		ship = new Ship();
		
		scene = new LevelScene(getWidth(), getHeight());
		scene.setLevel(level);
		scene.setShip(ship);
		
		/*
		shapes = new ArrayList<Shape>();
		shapes.add(new Axis());
		shapes.add(new Grid(20, 20, 1, false).setColor(0x00dd00ff));
		*/
		
		scene.startScene();
	}
	
	@Override
    protected void init()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void keyCallback(int key, int scancode, int action, int mods)
    {
        if (key == GLFW.GLFW_KEY_F10)
            stop();
		
		Input input = key == GLFW_KEY_UP ? Input.UP : 
					  key == GLFW_KEY_LEFT ? Input.LEFT :
					  key == GLFW_KEY_DOWN ? Input.DOWN :
					  key == GLFW_KEY_RIGHT ? Input.RIGHT :
					  key == GLFW_KEY_SPACE ? Input.JUMP :
					  key == GLFW_KEY_E ? Input.RESET :
					  key == GLFW_KEY_R ? Input.RESTART :
					  key == GLFW_KEY_ENTER ? Input.ACCEPT :
					  key == GLFW_KEY_ESCAPE ? Input.CANCEL : Input.NONE;

 		scene.handleInput(input, action != GLFW.GLFW_RELEASE);        
    }

    @Override
    protected void mouseCallback(int button, int action, int mods)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void cursorCallback(double xpos, double ypos)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void resizeCallback(int width, int height)
    {
        // TODO Auto-generated method stub
        
    }

	@Override
	protected void update(double delta)
	{
		scene.update(delta);		
	}

	@Override
	protected void render()
	{
	    scene.render();
		
		/*
		for (Shape shape : shapes)
			shape.render();
		*/
	}

	@Override
	protected void cleanup()
	{
		// TODO Auto-generated method stub
		
	}    
    
    public static void main(String[] args)
    {
        Main game = new Main();
        game.run();
    }

}
