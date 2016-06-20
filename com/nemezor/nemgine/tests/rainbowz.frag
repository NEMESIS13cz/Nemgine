#version 330 core

in vec4 depthFragment;

out vec4 finalColor;

void main() {

	finalColor = depthFragment;
}