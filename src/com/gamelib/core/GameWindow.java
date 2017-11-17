//==============================================================================
// Date Created:		02 July 2014
// Last Updated:		03 July 2014
//
// File name:			GameWindow.java
// Author(s):			M. Matthew Hydock
//
// File description:	An abstract class providing basic game window management
//						functionality, such as a gameloop, frame limiting, etc.
//==============================================================================

package com.gamelib.core;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
 





import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.gamelib.core.Clock;

public abstract class GameWindow
{
	public final int DEFAULT_WIDTH = 640;
	public final int DEFAULT_HEIGHT = 480;
	
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
    private boolean resizable;
	
	private double fps;
	private double ups;
	
	// For FPS calculations.
	private double deltaFPS;
	private int lastFPS;
	private int currFPS;
	
	// For UPS calculations.
	private double deltaUPS;
	private int lastUPS;
	private int currUPS;
	
	private boolean showFPS;
	private boolean showUPS;
	private boolean precise;
	
	protected String title;	
 
    // The window handle
    private long window;
    private boolean running;

    // We need to strongly reference callback instances.
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback cursorCallback;
    private GLFWMouseButtonCallback mouseCallback;
    private GLFWWindowSizeCallback resizeCallback;
    private GLFWErrorCallback errorCallback;
 
    public GameWindow()
    {        
    }
    
    public GameWindow(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    public void run() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
 
        try
        {
            initOpenGL();
            initCallbacks();
            
            init();
            loop();
 
            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
            mouseCallback.release();
            cursorCallback.release();
            resizeCallback.release();
        }
        finally
        {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }
 
    private void initOpenGL() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
 
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints();
        //glfwWindowHint(GLFW_VERSION_MAJOR, 3);
        //glfwWindowHint(GLFW_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GL_TRUE : GL_FALSE); // the window will be resizable
 
        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window"); 
 
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - width) / 2,
            (GLFWvidmode.height(vidmode) - height) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        
        GLContext.createFromCurrent();        
    }
	
    private void initCallbacks()
    {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback()
        {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                keyCallback(key, scancode, action, mods);
            }
        });
        
        // Setup a mouse button callback. It will be called every time a mouse button is pressed, repeated or released.      
        glfwSetMouseButtonCallback(window, mouseCallback = new GLFWMouseButtonCallback()
        {
            @Override
            public void invoke(long window, int button, int action, int mods)
            {
                mouseCallback(button, action, mods);                
            }            
        });
        
        // Setup a cursor position callback. It will be called every time the cursor is moved.
        glfwSetCursorPosCallback(window, cursorCallback = new GLFWCursorPosCallback()
        {
            @Override
            public void invoke(long window, double xpos, double ypos)
            {
                cursorCallback(xpos, ypos);
            }            
        });
        
        // Setup a window resize callback. It will be called every time the window is resized.
        glfwSetWindowSizeCallback(window, resizeCallback = new GLFWWindowSizeCallback()
        {            
            @Override
            public void invoke(long window, int width, int height)
            {
                resizeCallback(width, height);                
            }
        });
    }
    
    protected abstract void init();
    
    protected abstract void update(double delta);
    
    protected abstract void render();

    protected abstract void cleanup();
    
    protected abstract void keyCallback(int key, int scancode, int action, int mods);
    
    protected abstract void mouseCallback(int button, int action, int mods);
    
    protected abstract void cursorCallback(double xpos, double ypos);	

    protected abstract void resizeCallback(int width, int height);

	public void loop()
	{
        // Make the window visible
        glfwShowWindow(window);
        
        glViewport(0, 0, width, height);
        
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        
		fps = 0;
		ups = 0;
		running = true;
		
		String title = this.title;
		while (glfwWindowShouldClose(window) == GL_FALSE && running)
		{
			Clock.update();
			
			if (showFPS && precise)
				fps = calcPreciseFPS(Clock.delta());
			if (showFPS && !precise)
				fps = calcFPS(Clock.delta());
			
			render();
			glfwPollEvents();

			while (Clock.accum() >= Clock.fixDelta())
			{
				update(Clock.fixDelta());

				Clock.step();
				
				if (showUPS)
					ups = calcUPS(Clock.fixDelta());
			}
			
			glfwSwapBuffers(window);
			
			if (showFPS || showUPS)
			{
				title = this.title + " -- ";
				
				if (showFPS)
				{
					title += "FPS: " + String.format("%.2f", fps);
					if (showUPS)
						title += "  |  ";
				}
				
				if (showUPS)
					title += "UPS: " + ups;
				
				glfwSetWindowTitle(window, title);
			}

		}
		
		cleanup();
	}
	
	private double calcPreciseFPS(double elapsedTime)
    {
        return 1.0/elapsedTime;
    }
    
    private int calcFPS(double elapsedTime)
    {
        deltaFPS += elapsedTime;
        
        if (deltaFPS > 1)
        {
            lastFPS = currFPS;
            currFPS = 0;
            deltaFPS = 0;
        }
        
        currFPS++;
        
        return lastFPS;
    }
    
    private int calcUPS(double elapsedTime)
    {
        deltaUPS += elapsedTime;
        
        if (deltaUPS > 1)
        {
            lastUPS = currUPS;
            currUPS = 0;
            deltaUPS = 0;
        }
        
        currUPS++;
        
        return lastUPS;
    }
	
	public int getWidth()
	{
		return width;
	}
	
	public void setWidth(int width)
	{
		this.width = (width > 10) ? width : this.width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height)
	{
		this.height = (height > 10) ? height : this.height;
	}
	
	public double getFPS()
	{
		return fps;
	}
	
	public double getUPS()
	{
		return ups;
	}
	
	public void showFPS(boolean show)
	{
		showFPS = show;
	}
	
	public void showUPS(boolean show)
	{
		showUPS = show;
	}
	
	public void preciseFPS(boolean precise)
	{
		this.precise = precise;
	}
	
	public void stop()
    {
        running = false;
    }
	
	public void pause()
	{
	}
}
