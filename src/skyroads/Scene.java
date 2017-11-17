package skyroads;

import com.gamelib.core.GameObject;

public abstract class Scene extends GameObject
{
	protected int width;
	protected int height;
	
	public abstract void startScene();	
	
	public abstract void resize(int width, int height);

	public abstract void keyInput(int key, boolean pressed);
	
	public abstract void update(double delta);
	
	public abstract void render();

}
