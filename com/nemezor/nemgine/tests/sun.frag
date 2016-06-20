#version 330 core

in vec2 textureFragment;
in vec4 colorFragment;
in vec2 sunPosition;

out vec4 finalColor;

uniform sampler2D shadow_map;
uniform sampler2D sun_map;

void main() {
	finalColor = texture(shadow_map, textureFragment);
	vec4 sunMapColor = texture(sun_map, textureFragment) * colorFragment;
	finalColor += sunMapColor;
}