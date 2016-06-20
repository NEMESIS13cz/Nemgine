#version 330 core

in vec3 position;
in vec2 texCoords;
in vec3 normal;

out vec4 colorFragment;
out vec2 textureFragment;
out vec2 sunPosition;

uniform mat4 projection;
uniform mat4 transformation;
uniform vec4 sunColor;
uniform vec2 sunPosIn;
uniform mat4 sunTransform;

void main() {

	textureFragment = texCoords;
	colorFragment = sunColor;
	vec4 world = projection * (transformation * vec4(position, 1.0));
	sunPosition = sunPosIn;
	gl_Position = world;
}