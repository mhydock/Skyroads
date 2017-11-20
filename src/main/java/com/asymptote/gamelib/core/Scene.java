package com.asymptote.gamelib.core;

import com.asymptote.gamelib.core.GameObject;
import com.asymptote.gamelib.core.Input;

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
