#version 330 core

in vec3 position;
in vec3 normal;

out vec3 surfaceNorm;
out float lightVec[12];
out float light_color[12];
out vec3 worldVec;

uniform mat4 projection;
uniform mat4 transformation;
uniform float light[12];
uniform float light_color_vertex[12];

void main() {

	vec4 world = transformation * vec4(position, 1.0);
	gl_Position = projection * world;
	
	surfaceNorm = (transformation * vec4(normal, 0.0)).xyz;
	worldVec = world.xyz;
	lightVec = light;
	light_color = light_color_vertex;
}