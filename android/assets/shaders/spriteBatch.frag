#version 330 core

#ifdef GL_ES
  precision mediump float;
#endif

uniform sampler2D u_texture;

in vec4 v_pos;
in vec4 v_color;
in vec2 v_texCoords;

layout(location=0) out vec4 fragOut;

void main()
{
  fragOut = v_color * texture(u_texture, v_texCoords);
}
