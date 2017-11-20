package com.asymptote.skyroads;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import com.asymptote.gamelib.core.FrameBuffer;
import com.asymptote.gamelib.core.ShaderProgram;
import com.asymptote.gamelib.core.Shape;
import com.asymptote.gamelib.primitives.Circle;
import com.asymptote.gamelib.primitives.FrameQuad;

import static com.asymptote.gamelib.core.Utils.printGlError;

public class LevelSceneUI extends Shape
{
	private static final int SLICES = 30;
	private static final int PROG_SLICES = 200;

	private static final float L_GAP = 2f;
	private static final float S_GAP = .01f;
	
	private static final int BACKGROUND = 0x110a33dd;
	private static final int OUTLINE = 0x661188ff;
	private static final int FILL = 0x772299ff;
	
	private int width;
	private int height;
	
	private Player player;
	private RadialMeter airMeter;
	private RadialMeter fuelMeter;
	private RadialMeter speedMeter;
	private RadialMeter progressMeter;
	
	private RadialMeter airOutline;
	private RadialMeter fuelOutline;
	private RadialMeter speedOutline;
	private RadialMeter progressOutline;
	
	private RadialMeter airBackground;
	private RadialMeter fuelBackground;
	private RadialMeter speedBackground;
	private RadialMeter progressBackground;
	
	private Circle canJump;
	private Circle cantJump;
	private Circle jumpOutline;
	
	private ShaderProgram hudProg;
	private ShaderProgram fboProg;
	
	private FrameBuffer bgFBO;
	private FrameBuffer olFBO;
	private FrameQuad bgQuad;
	private FrameQuad olQuad;
	
	
	public LevelSceneUI(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		airBackground = new RadialMeter(-10, 90, 50, 25, SLICES/2, S_GAP, true);
		fuelBackground = new RadialMeter(190, 90, 50, 25, SLICES/2, S_GAP, true);
		speedBackground = new RadialMeter(190, -10, 100, 60, SLICES, L_GAP, true);		
		progressBackground = new RadialMeter(190, -10, 120, 110, PROG_SLICES, 0, true);
		
		airBackground.setLocation(0,-height*.45f,-1).setColor(BACKGROUND);
		fuelBackground.setLocation(0,-height*.45f,-1).setColor(BACKGROUND);
		speedBackground.setLocation(0,-height*.45f,-1).setColor(BACKGROUND);
		progressBackground.setLocation(0,-height*.45f,-1).setColor(BACKGROUND);
		
		airBackground.setPercent(1);
		fuelBackground.setPercent(1);
		speedBackground.setPercent(1);
		progressBackground.setPercent(1);
		
		airOutline = new RadialMeter(-10, 90, 50, 25, SLICES/2, S_GAP, false);
		fuelOutline = new RadialMeter(190, 90, 50, 25, SLICES/2, S_GAP, false);
		speedOutline = new RadialMeter(190, -10, 100, 60, SLICES, L_GAP, false);		
		progressOutline = new RadialMeter(190, -10, 120, 110, PROG_SLICES, 0, false);

		
		airOutline.setLocation(0,-height*.45f,1).setColor(OUTLINE);
		fuelOutline.setLocation(0,-height*.45f,1).setColor(OUTLINE);
		speedOutline.setLocation(0,-height*.45f,1).setColor(OUTLINE);
		progressOutline.setLocation(0,-height*.45f,1).setColor(OUTLINE);
		
		airOutline.setPercent(1);
		fuelOutline.setPercent(1);
		speedOutline.setPercent(1);
		progressOutline.setPercent(1);
		
		airMeter = new RadialMeter(-10, 90, 50, 25, SLICES/2, S_GAP, true);
		fuelMeter = new RadialMeter(190, 90, 50, 25, SLICES/2, S_GAP, true);
		speedMeter = new RadialMeter(190, -10, 100, 60, SLICES, L_GAP, true);	
		progressMeter = new RadialMeter(190, -10, 120, 110, PROG_SLICES, 0, true);
		
		airMeter.setLocation(0,-height*.45f,0).setColor(FILL);
		fuelMeter.setLocation(0,-height*.45f,0).setColor(FILL);
		speedMeter.setLocation(0,-height*.45f,0).setColor(FILL);
		progressMeter.setLocation(0,-height*.45f,0).setColor(FILL);
		
		canJump = new Circle(15, 20, false);
		canJump.setLocation(0,-height*.45f,0).rotate(90, 1, 0, 0).setColor(FILL);
		
		cantJump = new Circle(15, 20, false);
		cantJump.setLocation(0,-height*.45f,-1).rotate(90, 1, 0, 0).setColor(BACKGROUND);
		
		jumpOutline = new Circle(15, 20, true);
		jumpOutline.setLocation(0,-height*.45f,1).rotate(90, 1, 0, 0).setColor(OUTLINE);
		
		hudProg = new ShaderProgram("src/glsl/sprite.vert", "src/glsl/sprite.frag");
		fboProg = new ShaderProgram("src/glsl/fbo.vert", "src/glsl/texture.frag");

		bgFBO = new FrameBuffer(width,height,true);
		olFBO = new FrameBuffer(width,height,true);
		
		bgQuad = new FrameQuad();		
		olQuad = new FrameQuad();		
		
		renderLayers();
	}
	
	public void watchPlayer(Player player)
	{
		this.player = player;
	}
	
	@Override
	public void update(double delta)
	{
		// TODO Auto-generated method stub

	}
	
	@Override
	public void render()
	{
		airMeter.setPercent(player.getAirPercent());
		fuelMeter.setPercent(player.getFuelPercent());
		speedMeter.setPercent(player.getShipMaxSpeed(), player.getShipSpeed());
		progressMeter.setPercent(player.getProgress());
		
//		System.out.println("Air: " + airMeter.getPercent());
//		System.out.println("Fuel: " + fuelMeter.getPercent());
//		System.out.println("Speed: " + speedMeter.getPercent());
//		System.out.println("Progress: " + progressMeter.getPercent());
		
		fboProg.use();
		glClear(GL_DEPTH_BUFFER_BIT);
		bgFBO.bind();		
		fboProg.setValue("mytexture", bgFBO.layer());
		bgQuad.render();
		bgFBO.unbind();
		fboProg.disable();
		
		hudProg.use();
		glClear(GL_DEPTH_BUFFER_BIT);
		hudProg.setValue("width", (float)width);
		hudProg.setValue("height", (float)height);
		
		hudProg.setMatrix("modelMat", airMeter.getModelBuffer());
		airMeter.render();
	
		hudProg.setMatrix("modelMat", fuelMeter.getModelBuffer());
		fuelMeter.render();

		hudProg.setMatrix("modelMat", speedMeter.getModelBuffer());
		speedMeter.render();		

		hudProg.setMatrix("modelMat", progressMeter.getModelBuffer());
		progressMeter.render();		
		
		if (player.canJump())
		{
			hudProg.setMatrix("modelMat", canJump.getModelBuffer());
			canJump.render();
		}
		
		hudProg.disable();
		
		fboProg.use();
		glClear(GL_DEPTH_BUFFER_BIT);
		olFBO.bind();		
		fboProg.setValue("mytexture", olFBO.layer());
		olQuad.render();
		olFBO.unbind();
		fboProg.disable();
	}
	
	private void renderLayers()
	{
		hudProg.use();
		hudProg.setValue("width", (float)width);
		hudProg.setValue("height", (float)height);
		
		bgFBO.use(true);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		hudProg.setMatrix("modelMat", airBackground.getModelBuffer());
		airBackground.render();	
		hudProg.setMatrix("modelMat", fuelBackground.getModelBuffer());
		fuelBackground.render();
		hudProg.setMatrix("modelMat", speedBackground.getModelBuffer());
		speedBackground.render();		
		hudProg.setMatrix("modelMat", progressBackground.getModelBuffer());
		progressBackground.render();		
		hudProg.setMatrix("modelMat", cantJump.getModelBuffer());
		cantJump.render();
		bgFBO.use(false);
		
		olFBO.use(true);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		hudProg.setMatrix("modelMat", airOutline.getModelBuffer());
		airOutline.render();
		hudProg.setMatrix("modelMat", fuelOutline.getModelBuffer());
		fuelOutline.render();
		hudProg.setMatrix("modelMat", speedOutline.getModelBuffer());
		speedOutline.render();
		hudProg.setMatrix("modelMat", progressOutline.getModelBuffer());
		progressOutline.render();
		hudProg.setMatrix("modelMat", jumpOutline.getModelBuffer());
		jumpOutline.render();
		olFBO.use(false);
		
		hudProg.disable();
	}
}
