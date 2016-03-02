#version 330 core

in vec2 textureFragment;
in vec4 colorFragment;

out vec4 finalColor;

uniform sampler2D texture_map;

void main() {
	
	vec4 texColor = texture(texture_map, vec2(textureFragment.x, textureFragment.y));
	finalColor = colorFragment * texColor.x; // font texture is stored as GL_RED -> red channel = intensity
}