#version 150 core

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TexCoord;

uniform float width;
uniform float height;
uniform mat4 modelMat;

out vec4 pass_Color;
out vec2 pass_TexCoord;

void main(void)
{
	gl_Position = modelMat * in_Position;
	gl_Position = vec4(gl_Position.x/(width/2), gl_Position.y/(height/2), gl_Position.z/-100, gl_Position.w);
	//gl_Position = in_Position;
	
	pass_Color = in_Color;	
	pass_TexCoord = in_TexCoord;
}