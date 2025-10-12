#version 150
uniform sampler2D Sampler0;
uniform float Intensity;
uniform float BlurScale;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    float BlurRadius = Intensity*8;
    vec4 sum = vec4(0.0);
    float weights[3] = float[](0.227027, 0.316216, 0.07027);
    sum += texture(Sampler0, texCoord) * weights[0];
    for (int i = 1; i < 2; i++) {
        float j = float(i);
        sum += texture(Sampler0, texCoord + vec2(BlurScale * j * BlurRadius, 0.0))*weights[i];
        sum += texture(Sampler0, texCoord - vec2(BlurScale * j * BlurRadius, 0.0))*weights[i];
    }
    fragColor =sum;
}