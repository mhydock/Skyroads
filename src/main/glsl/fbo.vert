#version 150 core

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TexCoord;

out vec4 pass_Color;
out vec2 pass_TexCoord;

void main(void)
{
	gl_Position = in_Position;
		
	pass_Color = in_Color;	
	pass_TexCoord = in_TexCoord;
}