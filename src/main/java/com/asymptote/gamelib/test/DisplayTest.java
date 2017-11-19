package com.gamelib.test;

import java.util.List;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.gamelib.core.GameWindow;
import com.gamelib.core.Camera;
import com.gamelib.core.ShaderProgram;
import com.gamelib.core.Shape;
import com.gamelib.primitives.Axis;
import com.gamelib.primitives.Circle;
import com.gamelib.primitives.Cone;
import com.gamelib.primitives.Cube;
import com.gamelib.primitives.Cylinder;
import com.gamelib.primitives.Grid;
import com.gamelib.primitives.Sphere;

import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;

public class DisplayTest extends GameWindow
{
	private final float ROT_SCALE = 1f;

	private List<Shape> objects = new ArrayList<Shape>();
	private ShaderProgram prog;
	private Camera camera;
	
	private float rotateY = 0;
	private boolean turnLeft = false;
	private boolean turnRight = false;
	private boolean mouseDown = false;
	
	private float rotateX = 0;
	private boolean turnBack = false;
	private boolean turnForward = false;
	
	private float zoom = 1;
	private boolean zoomIn = false;
	private boolean zoomOut = false;
	
	private int lastX = -1;
	private int lastY = -1;
	
	private float rotate = 0;
	private Vector3f rotAxis = new Vector3f(1, 0, 0);
	
	private Vector3f lastPos;
	private Vector3f currPos;
	
	private DisplayTest()
	{
	}
	
	protected void init()
	{		
		prog = new ShaderProgram("src/glsl/basic.vert", "src/glsl/basic.frag");
		
		camera = new Camera();
		camera.setFrustum(getWidth(), getHeight(), 60, 0.1f, 20.0f);		// depth buffering borks if zNear == 0
		camera.moveTo(5, 5, 5);
		camera.lookAt(0, 0, 0);
		
		float s2o2 = (float)sqrt(2)/2;
		float a = 4;
		float b = (float)(a*cos(toRadians(30)));
		float c = (float)(a*sin(toRadians(30)));
		
		System.out.println(s2o2);
		
		objects.add(new Axis());

		Cube cube = new SpinningCube();
		
		objects.add(cube);
		
		Circle circle = new Circle(1, 30, true);
		circle.setScale(b,b,b).setColor(0x008800ff).setLocation(0, -c, 0);
		objects.add(circle);
		
		circle = new Circle(1, 30, true);
		circle.setScale(a,a,a).setColor(0x008800ff);
		objects.add(circle);
		
		circle = new Circle(1, 30, true);
		circle.setScale(b,b,b).setColor(0x008800ff).setLocation(0, c, 0);
		objects.add(circle);
		
		//----------------------------------------------------------

		circle = new Circle(1, 30, true);
		circle.setScale(b,b,b).setColor(0x008800ff).setOrientation(90, 1, 0, 0).setLocation(0, 0, -c);
		objects.add(circle);
		
		circle = new Circle(1, 30, true);
		circle.setScale(a,a,a).setColor(0x008800ff).setOrientation(90, 1, 0, 0);
		objects.add(circle);
		
		circle = new Circle(1, 30, true);
		circle.setScale(b,b,b).setColor(0x008800ff).setOrientation(90, 1, 0, 0).setLocation(0, 0, c);
		objects.add(circle);
		
		//----------------------------------------------------------
		
		circle = new Circle(1, 30, true);
		circle.setScale(b,b,b).setColor(0x008800ff).setOrientation(90, 0, 0, 1).setLocation(-c, 0 ,0);
		objects.add(circle);
		
		circle = new Circle(1, 30, true);
		circle.setScale(a,a,a).setColor(0x008800ff).setOrientation(90, 0, 0, 1);
		objects.add(circle);
		
		circle = new Circle(1, 30, true);
		circle.setScale(b,b,b).setColor(0x008800ff).setOrientation(90, 0, 0, 1).setLocation(c, 0 ,0);
		objects.add(circle);
				
		Cylinder cylinder = new Cylinder(0.5f, 1, 10, true);
		cylinder.setColor(0xff0000ff).setLocation(3, 0.5f, 0);
		objects.add(cylinder);
		
		Sphere sphere = new Sphere(0.5f, 30, 30);
		sphere.setColor(0x00ff00ff).setLocation(-3, 0.5f, 0);
		objects.add(sphere);
		
		Cone cone = new Cone(.5f, 1, 3);
		cone.setColor(0xffff00ff).setLocation(-3, 0, 2);
		objects.add(cone);

		cone = new Cone(.5f, 1, 4);
		cone.setColor(0xffff00ff).setLocation(-1.5f, 0, 2);
		objects.add(cone);
		
		cone = new Cone(.5f, 1, 5);
		cone.setColor(0xffff00ff).setLocation(0, 0, 2);
		objects.add(cone);
		
		cone = new Cone(.5f, 1, 6);
		cone.setColor(0xffff00ff).setLocation(1.5f, 0, 2);
		objects.add(cone);
		
		cone = new Cone(.5f, 1, 10);
		cone.setColor(0xffff00ff).setLocation(3, 0, 2);
		objects.add(cone);
		
		Grid grid = new Grid(10, 10, 1, true);
		grid.setColor(0x0055ffff).setFillMode(false);
		objects.add(grid);
	}
	
	private Vector3f trackBallMapping(int x, int y, int width, int height)
	{
		Vector3f v = new Vector3f();
		float d;
		
		v.x = (2.0f*x - width)/width;
		v.y = (height - 2.0f*y)/height;
		d = (float) sqrt(v.x*v.x + v.y*v.y);
		v.z = (float) cos((Math.PI/2.0f)*((d < 1.0f)?d:1.0f));
		
		v.normalize(v);
		
		System.out.println(v.toString());
		
		return v;
	}
	
	private void mouseRotate(int x, int y, int width, int height)
	{
		if (x == lastX && y == lastY)
		{
			rotate = 0;
			return;
		}
		
		System.out.println(x + "  " + y);
		
		if (lastPos == null)
		{
			lastPos = trackBallMapping(x, y, width, height);
			lastX = x;
			lastY = y;
			return;
		}
		
		Vector3f dr = new Vector3f();
		
		currPos = trackBallMapping(x, y, width, height);
		currPos.sub(lastPos, dr);
		lastPos.cross(currPos, rotAxis);
		rotAxis.normalize(rotAxis);
		
		rotate = 90.0f * (float)sqrt(dr.length());
		
		lastPos.x = currPos.x;
		lastPos.y = currPos.y;
		lastPos.z = currPos.z;
		
		lastX = x;
		lastY = y;
		
		System.out.println(lastPos.toString() + "   " + currPos.toString());
		System.out.println(rotate + "   " + rotAxis.toString());
	}
	
	private void generateRotation()
	{
		Quaternionf q1 = new Quaternionf();
		q1.w = (float)cos(toRadians(rotateX)/2);
		q1.x = (float)sin(toRadians(rotateX)/2);
		q1.y = 0;
		q1.z = 0;
		
		Quaternionf q2 = new Quaternionf();
		q2.w = (float)cos(toRadians(rotateY)/2);
		q2.x = 0;
		q2.y = (float)sin(toRadians(rotateY)/2);
		q2.z = 0;
		
		Quaternionf total = new Quaternionf();
		q1.mul(q2, total);
		
		double temp = acos(total.w)*2;
		rotAxis.x = (float)(total.x/sin(temp/2));
		rotAxis.y = (float)(total.y/sin(temp/2));
		rotAxis.z = (float)(total.z/sin(temp/2));
		rotate = (float)toDegrees(temp);
	}
	
	@Override
	protected void keyCallback(int key, int scancode, int action, int mods)
	{
	    if (key == GLFW.GLFW_KEY_LEFT)
			turnLeft = action != GLFW.GLFW_RELEASE;
		if (key == GLFW.GLFW_KEY_RIGHT)
			turnRight = action != GLFW.GLFW_RELEASE;
		if (key == GLFW.GLFW_KEY_UP)
			turnBack = action != GLFW.GLFW_RELEASE;
		if (key == GLFW.GLFW_KEY_DOWN)
			turnForward = action != GLFW.GLFW_RELEASE;
		if (key == GLFW.GLFW_KEY_EQUAL)
			zoomIn = action != GLFW.GLFW_RELEASE;
		if (key == GLFW.GLFW_KEY_MINUS)
			zoomOut = action != GLFW.GLFW_RELEASE;
		
		// Floating perspective
		if (key == GLFW.GLFW_KEY_KP_0)
		{
			camera.moveTo(5, 5, 5);
			camera.lookAt(0, 0, 0);
			zoom = 1;
			rotateX = 0;
			rotateY = 0;
		}
		
		// Front
		if (key == GLFW.GLFW_KEY_KP_1)
		{
			camera.moveTo(0, 0, 8.66f);
			camera.lookAt(0, 0, 0);
			zoom = 1;
			rotateX = 0;
			rotateY = 0;
		}
		
		// Side
		if (key == GLFW.GLFW_KEY_KP_3)
		{
			camera.moveTo(8.66f, 0, 0);
			camera.lookAt(0, 0, 0);
			zoom = 1;
			rotateX = 0;
			rotateY = 0;
		}
		
		// Above (+Z pointing down)
		if (key == GLFW.GLFW_KEY_KP_7)
		{
			camera.moveTo(0, 8.66f, 0);
			camera.lookAt(0, 0, 0, 0, 0, -1);
			zoom = 1;
			rotateX = 0;
			rotateY = 0;
		}
		
		//System.out.println("turnLeft: " + turnLeft);
		//System.out.println("turnRight: " + turnRight);
	}	
    
     @Override
     protected void mouseCallback(int button, int action, int mods)
     {
         if (button == GLFW.GLFW_MOUSE_BUTTON_1)
             mouseDown = action != GLFW.GLFW_RELEASE;
     }
     
     @Override
     protected void cursorCallback(double xpos, double ypos)
     {
         if (mouseDown)
             mouseRotate((int)xpos,(int)ypos,getWidth(),getHeight());
         else
         {
             lastPos = null;
             rotate = 0;
         }
     }

     @Override
     protected void resizeCallback(int width, int height)
     {
         // TODO Auto-generated method stub
         
     }
	
	@Override
	protected void update(double delta)
	{
		if (turnLeft)
			rotateY = -2;
		if (turnRight)
			rotateY = 2;
		
		if (turnBack)
			rotateX = -2;
		if (turnForward)
			rotateX = 2;
		
		if (turnLeft || turnRight || turnBack || turnForward)
			generateRotation();
		
		if (zoomIn)
			zoom += 0.01f;
		if (zoomOut)
			zoom -= 0.01f;
		
		if (zoom < 0.000001f)
			zoom = 0.000001f;
		
		for (Shape shape : objects)
			shape.update(delta);
	}
	
	@Override
	protected void render()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		prog.use();
		
		if ((turnLeft || turnRight || turnBack || turnForward) && rotate != 0)
			camera.rotateAbout(rotate, rotAxis.x, rotAxis.y, rotAxis.z, 0, 0, 0);

		if (zoomIn || zoomOut)
			camera.scale(zoom, zoom, zoom);
		
		for (Shape shape : objects)
			shape.render();
		
		prog.disable();
	}
	
	@Override
	protected void cleanup()
	{
		prog.free();
		
		for (Shape shape : objects)
			shape.free();
	}
	
	public static void main (String[] args)
	{
		DisplayTest dt = new DisplayTest();
		dt.run();
	}
}
