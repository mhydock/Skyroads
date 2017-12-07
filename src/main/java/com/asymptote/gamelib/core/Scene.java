package com.asymptote.gamelib.graphics;

import com.asymptote.gamelib.graphics.GameObject;
import com.asymptote.gamelib.graphics.Input;

public interface Scene extends GameObject
{
	int width();

	int height();
	
	void startScene();	

	void resize(int width, int height);

	void handleInput(Input input, boolean active);

	void update(double delta);

	void render();

}
