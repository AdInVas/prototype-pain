#version 150
uniform sampler2D Sampler0;
uniform float Intensity;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    float CA_Strength =pow(Intensity,3.0);
    vec2 ndc = (texCoord - 0.5) * 2.0;
    float dist = dot(ndc, ndc);

    float falloff = 2.2;
    float min_intensity = 0.05;
    float aberration_factor = max(min_intensity, dist * falloff);
    float offset_amount = 1.0 * CA_Strength * aberration_factor;

    vec2 offset_red   = -ndc * offset_amount;  // Red channel shifts outwards
    vec2 offset_green = ndc * offset_amount ; // Green channel shifts slightly inwards (optional, or just smaller outwards)
    vec2 offset_blue  = -ndc * offset_amount*0.8;

    vec4 color_red   = texture(Sampler0, texCoord + offset_red);
    vec4 color_green = texture(Sampler0, texCoord + offset_green);
    vec4 color_blue  = texture(Sampler0, texCoord + offset_blue);
    fragColor = vec4(color_red.r, color_green.g, color_blue.b, 1.0);
}