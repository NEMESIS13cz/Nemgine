#version 330 core

in vec3 position;
in vec3 normal;

out vec3 surfaceNorm;
out vec3 lightVec;

uniform mat4 projection;
uniform mat4 transformation;
uniform vec3 light;

void main() {

	vec4 world = transformation * vec4(position, 1.0);
	gl_Position = projection * world;
	
	surfaceNorm = (transformation * vec4(normal, 0.0)).xyz;
	lightVec = light - world.xyz;
}