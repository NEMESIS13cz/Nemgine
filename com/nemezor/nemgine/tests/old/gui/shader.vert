#version 330 core

in vec3 position;
in vec3 normal;

out vec3 surfaceNorm;
out vec3 lightVector;
out vec3 lightColor;
out vec3 worldVec;

uniform mat4 projection;
uniform mat4 transformation;
uniform vec3 lightVectorIn;
uniform vec4 lightColorIn;

void main() {

	vec4 world = transformation * vec4(position, 1.0);
	gl_Position = projection * world;
	
	surfaceNorm = (transformation * vec4(normal, 0.0)).xyz;
	worldVec = world.xyz;
	lightVector = lightVectorIn;
	lightColor = lightColorIn.xyz;
}