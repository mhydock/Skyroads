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

package com.asymptote.gamelib.graphics;

import java.nio.ByteBuffer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.asymptote.gamelib.graphics.Clock;

public abstract class GameWindow
{
	public final int DEFAULT_WIDTH = 640;
	public final int DEFAULT_HEIGHT = 480;
	
	private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    private int fbWidth = width;
    private int fbHeight = height;
    private boolean resizable;
    private boolean windowed = true;    
	
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
    private Callback debugProc;
    
    public GameWindow()
    {
    }
    
    public GameWindow(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
 
        try
        {
            initOpenGL();
            initCallbacks();

            init();
            loop();
        }
        finally
        {
            try {
                destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 
    private void initOpenGL() {
        GLFWErrorCallback.createPrint().set();
        
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        // Get the resolution of the primary monitor
        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);
        if (!windowed) {
            width = vidmode.width();
            height = vidmode.height();
            fbWidth = width;
            fbHeight = height;
        }

        window = glfwCreateWindow(width, height, title, !windowed ? monitor : 0L, NULL);
        if (window == NULL) {
            throw new AssertionError("Failed to create the GLFW window");
        }

        // Center our window
        glfwSetWindowPos(
            window,
            (vidmode.width() - width) / 2,
            (vidmode.height() - height) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        
        // Enable v-sync
        glfwSwapInterval(1);
    }
	
    private void initCallbacks()
    {
        glfwSetFramebufferSizeCallback(window, (long window, int width, int height) -> {
                if (width > 0 && height > 0 && (this.fbWidth != width || this.fbHeight != height)) {
                    this.fbWidth = width;
                    this.fbHeight = height;
                }
        });

        glfwSetWindowSizeCallback(window, (long window, int width, int height) -> {
                if (width > 0 && height > 0 && (this.width != width || this.height != height)) {
                    this.width = width;
                    this.height = height;
                }
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mods) -> {
                keyCallback(key, scancode, action, mods);
        });
        
        // Setup a mouse button callback. It will be called every time a mouse button is pressed, repeated or released.      
        glfwSetMouseButtonCallback(window, (long window, int button, int action, int mods) -> {
                mouseCallback(button, action, mods);                
        });
        
        // Setup a cursor position callback. It will be called every time the cursor is moved.
        glfwSetCursorPosCallback(window, (long window, double xpos, double ypos) -> {
                cursorCallback(xpos, ypos);
        });
        
        // Setup a window resize callback. It will be called every time the window is resized.
        glfwSetWindowSizeCallback(window, (long window, int width, int height) -> {
                resizeCallback(width, height);                
        });

        debugProc = GLUtil.setupDebugMessageCallback();        
    }
    
    private void destroy() {
        if (debugProc != null) {
            debugProc.free();
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
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
        
        glViewport(0, 0, fbWidth, fbHeight);
        
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        
		fps = 0;
		ups = 0;
		running = true;
		
		String title = this.title;
		while (!glfwWindowShouldClose(window) && running)
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
