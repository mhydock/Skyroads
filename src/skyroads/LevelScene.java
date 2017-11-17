package skyroads;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static com.gamelib.core.Utils.*;

import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.gamelib.core.Camera;
import com.gamelib.core.FrameBuffer;
import com.gamelib.core.Shader;
import com.gamelib.core.ShaderProgram;

public class LevelScene extends Scene
{
	private static final float C_DIST_OFFSET = -25;
	private static final float C_ALT_OFFSET = 20;
	
	private ShaderProgram baseProg;
	private ShaderProgram textProg;
	
	private Camera camera;
	private FrameBuffer fbo;
	private FrameQuad fQuad;
	
	private Level level;
	private Ship ship;
	
	private Player player;
	private LevelSceneUI hud;
	
	private float lastDist;
	private float deltaDist;	
	
	private boolean isFinished;
	
	public LevelScene(int width, int height)
	{		
		this.width = width;
		this.height = height;
		
		baseProg = new ShaderProgram("src/glsl/basic.vert", "src/glsl/basic.frag");		
		textProg = new ShaderProgram("src/glsl/fbo.vert", "src/glsl/texture.frag");

		camera = new Camera();
		camera.setFrustum(width, height, 60, 0.1f, 500.0f);		// depth buffering borks if zNear == 0
		fbo = new FrameBuffer(width, height, true);
		fQuad = (FrameQuad) new FrameQuad();
		
		hud = new LevelSceneUI(width, height);
	}
	
	public void resize(int width, int height)
	{
		camera.setFrustum(width, height, 60, 0.1f, 500.0f);		// depth buffering borks if zNear == 0
		fbo = new FrameBuffer(width, height, true);
	}
	
	public void keyInput(int key, boolean pressed)
	{
		if (key == Keyboard.KEY_LEFT)
			ship.moveLeft(pressed);
		if (key == Keyboard.KEY_RIGHT)
			ship.moveRight(pressed);
		if (key == Keyboard.KEY_UP)
			ship.accelerate(pressed);
		if (key == Keyboard.KEY_DOWN)
			ship.decelerate(pressed);
		
		if (key == Keyboard.KEY_SPACE)
		{
			if (pressed)
				ship.thrust();
			
			if (!pressed)
				ship.rising(false);
		}
		
		
		if (key == Keyboard.KEY_F9 && pressed)
		{
			reset();
			ship.setPosition(level.getStart());
		}
		if (key == Keyboard.KEY_RETURN && pressed)
		{
			reset();
			ship.setPosition(0,0,10);
		}	
	}
	
	public void startScene()
	{
		float[] start = level.getStart();
		
		ship.setPosition(start);
		
		lastDist = start[1];
		deltaDist = 0;
		
		//System.out.println(Arrays.toString(start));
		
		camera.moveTo(start[0], lastDist+C_DIST_OFFSET, start[2]+C_ALT_OFFSET);
		camera.lookAt(start[0], start[1], start[2], 0, 0, 1);
		
		player.reset();
	}
	
	public void setShip(Ship ship)
	{
		this.ship = ship;
		
		if (level != null)
		{
			ship.setGravity(level.getGravityFactor());

			player = new Player(ship,this.level);
			hud.watchPlayer(player);
		}
	}
	
	public void setLevel(Level level)
	{
		this.level = level;		
		
		if (ship != null)
		{
			ship.setGravity(level.getGravityFactor());

			player = new Player(ship,this.level);
			hud.watchPlayer(player);
		}
	}
	
	public void reset()
	{
		ship.reset();
		ship.setPosition(level.getStart());
		
		player.reset();
	}
	
	@Override
	public void update(double delta)
	{
		//System.out.println(ship.getAltitude() + "  " + level.getDeathHeight());
		
		ship.update(delta);
		
		if (ship.getAltitude() < level.getDeathHeight())
			reset();
		
		float newDist = ship.getDistance();
		
		deltaDist = newDist - lastDist;
		lastDist = newDist;
		
		camera.move(0, deltaDist, 0);
		
		player.loseAir((float)(delta*level.getAirLoss()));
		player.loseFuel((float)(delta*level.getFuelLoss()));
		
		checkCollisions();
	}

	@Override
	public void render()
	{		
		fbo.use(true);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		printGlError("Clearing FBO");
		hud.render();
		printGlError("Rendered HUD to FBO");
		fbo.use(false);
		
		
		baseProg.use();		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		printGlError("Clearing default frame buffer");

		if (level != null)
			level.render();
		
		ship.render();		
		baseProg.disable();
		
		printGlError("Rendered level/ship to default FB");
		
		textProg.use();
		glClear(GL_DEPTH_BUFFER_BIT);
		printGlError("Clearing default depth buffer");

		fbo.bind();		
		textProg.setValue("mytexture", fbo.layer());
		printGlError("Setting texture in shader: " + fbo.layer());

		fQuad.render();
		fbo.unbind();
		textProg.disable();
		
		printGlError("Rendered textured framebuffer quad");
	}
	
	private void checkAtEnd()
	{
		float[] shipLoc = ship.getPosition();
		
		if (level.atEnd(shipLoc))
			ship.setSpeed(0);
	}
	
	private void checkCollisions()
	{
		float[] shipLoc = ship.getPosition();
		float[] point = new float[shipLoc.length];
		Panel panel;
		
		List<float[]> points = ship.getBottom();
		
		for (float[] offset : points)
		{
			for (int i = 0; i < point.length; i++)
				point[i] = shipLoc[i] + offset[i];
			
			panel = level.isColliding(point);
			if (panel != null)
			{
				//System.out.println(Arrays.toString(offset) + " is colliding");
				ship.setPosition(shipLoc[0], shipLoc[1], panel.getAltitude()-offset[2]);
				ship.bounce(true);
				
				break;
			}
		}
		
		shipLoc = ship.getPosition();
		points = ship.getFront();
		for (float[] offset : points)
		{
			for (int i = 0; i < point.length; i++)
				point[i] = shipLoc[i] + offset[i];
			
			panel = level.isColliding(point);
			if (panel != null)
			{
				//System.out.println(Arrays.toString(offset) + " is colliding");
				ship.setPosition(shipLoc[0], panel.getDistance()-offset[1], shipLoc[2]);
				
				float speed = ship.getSpeed();
				ship.setSpeed(0);
				
				if (speed > ship.getMaxSpeed()/2)
					player.massiveCollision();
				
				break;
			}
		}
	}
}