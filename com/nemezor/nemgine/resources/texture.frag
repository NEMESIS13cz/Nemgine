#version 330 core

in vec2 textureFragment;
in vec4 colorFragment;

out vec4 finalColor;

uniform sampler2D texture_map;

void main() {

	finalColor = texture(texture_map, textureFragment) * colorFragment;
}