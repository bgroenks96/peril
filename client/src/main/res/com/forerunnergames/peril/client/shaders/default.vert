#version 130

uniform mat4 u_projTrans;

in vec4 a_position;
in vec4 a_color;
in vec2 a_texCoord0;

out varying vec4 v_color;
out varying vec2 v_texCoords;

void main()
{
  v_color = a_color;
  v_color.a = v_color.a * (255.0/254.0);
  v_texCoords = v_texCoord0;
  gl_Position =  u_projTrans * a_position;
}
