#version 150

uniform sampler2D Sampler0; // The Color Buffer (we won't use this, but keep it declared)
uniform sampler2D Sampler1; // The Depth Buffer


uniform float DistanceMax;
uniform float DistanceMin;

in vec2 texCoord;
out vec4 fragColor;

// Uniforms for the camera projection (still useful for linearizing)
// Function to convert non-linear depth buffer value to linear meters
// (Same function as before, but now we'll decide if we want to use the result)
float LinearizeDepth(float depth) {
    float z = depth * 2.0 - 1.0;
    return (2.0 * 0.1 * 50) / (50 + 0.1 - z * (50 - 0.1));
}

void main() {
    // 1. Read Depth and Original Color
    float depthValue = texture(Sampler1, texCoord).r;
    float viewSpaceZ = LinearizeDepth(depthValue);

    // --- SPHERICAL COMPENSATION LOGIC ---

    // 2. Calculate Radial Distance from Screen Center (0.0 at center, max 0.707 at corners)
    vec2 offset = texCoord - vec2(0.5, 0.5);
    float radialDist = length(offset);

    // 3. Define the compensation multiplier
    // This scales the radial distance to a meaningful number of blocks (e.g., 10 blocks)
    // The distortion factor determines how much the edges are 'pushed' away.
    // A value of 2.0 means the edges will look 2 blocks further than the center.
    float DistortionFactor = -3.140;

    // Calculate the distance offset: 0.0 at the center, up to DistortionFactor * 0.707 at the corner.
    float distanceOffset = radialDist * DistortionFactor;

    // 4. Adjust the effective Z-Depth
    // Subtract the offset from the true depth. This makes the object appear CLOSER to the fade point
    // at the edges of the screen, causing the fade to happen earlier (closer to the player).
    float compensatedZ = viewSpaceZ - distanceOffset;

    // --- BLINDNESS FADE ---

    float blindStartDistance = DistanceMin;
    float blindEndDistance = DistanceMax;

    // Use the compensated Z-depth for a spherical appearance.
    float darknessFactor = smoothstep(blindStartDistance, blindEndDistance, compensatedZ);
    float colorFactor = smoothstep(blindStartDistance, blindEndDistance/2, compensatedZ);
    vec4 color = texture(Sampler0,texCoord);
    vec3 originalC = color.rgb;
    color.rgb = color.rgb;
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec3 grayCol = vec3(gray);

    color.rgb = mix(originalC,grayCol,colorFactor);
    color.rgb = mix(color.rgb,vec3(0,0,0),max(darknessFactor,0.55));
    fragColor = color;
}