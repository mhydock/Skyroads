#version 150 core

in vec4 pass_Color;

out vec4 out_Color;

void main(void)
{
	int x = int(gl_FragCoord.x);
	int y = int(gl_FragCoord.y);
	
	if (gl_FrontFacing)
	{
		out_Color = pass_Color;
	
		if ((x/10)%2 == 1 && (y/10)%2 == 0 ||
			(x/10)%2 == 0 && (y/10)%2 == 1)
			out_Color = vec4(1.0,0.0,0.5,1.0);
	}
	else
	{
		out_Color = vec4(1.0,0.0,0.5,1.0);
		
		if ((x/10)%2 == 0 && (y/10)%2 == 1 ||
			(x/10)%2 == 1 && (y/10)%2 == 0)
			out_Color = vec4(1.0,0.0,0.5,1.0);
	}
	
}