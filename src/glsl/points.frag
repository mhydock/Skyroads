#version 150 core

const vec4 nul_color = vec4(0.75, 0.75, 1.0, 0.0);
const vec4 color_mid = vec4(1.0, 1.0, 1.0, 0.1);
const vec4 color_edg = vec4(0.75, 0.75, 1.0, 0.7);

const vec4 black = vec4(0.1, 0.1, 0.1, 1.0);
const vec4 white = vec4(1.0, 1.0, 1.0, 1.0);

const float out_limit = .23;
const float edge_limit = .24;

out vec4 out_color;

void main(void)
{
	vec2 pos = gl_PointCoord.st - vec2(0.5);
	float dist_squared = dot(pos,pos);	
	
	if (dist_squared > edge_limit)
		discard;
	else if (dist_squared > out_limit)
		out_color = mix(color_edg, nul_color, smoothstep(out_limit, edge_limit, dist_squared));
	else
		out_color = mix(color_mid, color_edg, smoothstep(0.0, out_limit, dist_squared));
		
	
	pos = gl_PointCoord.st - vec2(1.0, 0.0);
	dist_squared = dot(pos,pos);
	vec4 shade = mix(white, black, smoothstep(0.0, 0.75, dist_squared));
	out_color = mix(out_color, shade, 0.3);
	
	
	pos = gl_PointCoord.st - vec2(0.7, 0.3);
	dist_squared = dot(pos,pos);	

	if (dist_squared < 0.04)
		out_color = mix(white, out_color, smoothstep(0.0, 0.05, dist_squared));
}