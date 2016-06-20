#version 330 core

in vec3 position;
in vec2 texCoords;
in vec3 normal;

out vec4 colorFragment;
out vec4 fragPosLight;
out vec3 worldVec;
out vec3 lightDirection;
out vec3 surfaceNorm;

uniform mat4 projection;
uniform mat4 lightProjection;
uniform mat4 transformation;
uniform mat4 lightTransformation;
uniform vec4 color;
uniform vec3 lightDir;

void main() {

	colorFragment = color;
	vec4 world = transformation * vec4(position, 1.0);
	fragPosLight = lightProjection * (lightTransformation * vec4(position, 1.0));
	worldVec = world.xyz;
	lightDirection = lightDir;
	surfaceNorm = (transformation * vec4(normal, 0.0)).xyz;
	gl_Position = projection * world;
}