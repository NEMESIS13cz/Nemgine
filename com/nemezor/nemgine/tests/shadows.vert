#version 330 core

in vec3 position;
in vec2 texCoords;
in vec3 normal;

out vec4 colorFragment;
out vec4 fragPosLight;

uniform mat4 projection;
uniform mat4 lightProjection;
uniform mat4 transformation;
uniform mat4 lightTransformation;
uniform vec4 color;

void main() {

	colorFragment = color;
	vec4 world = projection * (transformation * vec4(position, 1.0));
	fragPosLight = lightProjection * (lightTransformation * vec4(position, 1.0));
	gl_Position = world;
}