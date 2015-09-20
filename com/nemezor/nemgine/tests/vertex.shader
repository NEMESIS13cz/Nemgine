#version 330 core

in vec3 vertex;
in vec2 texCoords;
in vec3 normal;

out vec2 textureCoords;
out vec3 surfaceNorm;
out vec3 lightVec;

uniform mat4 projection;
uniform mat4 translation;
uniform vec3 light;

void main() {

	vec4 world = translation * vec4(vertex, 1.0);
	gl_Position = projection * world;
	textureCoords = texCoords;
	
	surfaceNorm = (translation * vec4(normal, 0.0)).xyz;
	lightVec = light - world.xyz;
}