#ifdef GL_ES
  precision mediump float;
#endif

uniform sampler2D u_texture;

in vec4 v_color;
in vec2 v_texCoords;

void main()
{
  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
}
