#version 150
uniform sampler2D Sampler0;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0,texCoord);
    color.r *= 2.133;
    color.g *=1.8;
    color.b *=0.0;
    float gray = dot(color.rgb, vec3(0.299, 0.587, 1.714));
    color.rgb =vec3(1.0-gray);
    fragColor = color;
}


