#version 330 core

in vec3 position;
in vec2 texCoords;
in vec3 normal;

out vec2 textureCoords;
out vec3 surfaceNorm;

uniform mat4 projection;
uniform mat4 transformation;

void main() {

	vec4 world = transformation * vec4(position, 1.0);
	gl_Position = projection * world;
}