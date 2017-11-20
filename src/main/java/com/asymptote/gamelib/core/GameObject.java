//==============================================================================
// Date Created:		03 July 2014
// Last Updated:		03 July 2014
//
// File name:			GameObject.java
// Author(s):			M. Matthew Hydock
//
// File description:	An abstract class providing stubs for all game objects.
//==============================================================================

package com.asymptote.gamelib.core;

public interface GameObject
{
	void update(double delta);
	
	void render();
}
