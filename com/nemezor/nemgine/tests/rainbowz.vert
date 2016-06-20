#version 330 core

in vec3 position;
in vec3 normal;

out vec4 finalColor;

uniform mat4 projection;
uniform mat4 transformation;
uniform vec4 color;

void main() {

	vec4 world = projection * (transformation * vec4(position, 1.0));
	finalColor = color;
	gl_Position = world;
}