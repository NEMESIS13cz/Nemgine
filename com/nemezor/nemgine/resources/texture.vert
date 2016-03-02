#version 330 core

in vec3 position;
in vec2 texCoords;
in vec3 normal;

out vec4 colorFragment;
out vec2 textureFragment;

uniform mat4 projection;
uniform mat4 transformation;
uniform vec4 color;

void main() {

	textureFragment = texCoords;
	colorFragment = color;
	vec4 world = projection * (transformation * vec4(position, 1.0));
	gl_Position = world;
}