#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform float BrightR;
uniform float BrightG;
uniform float BrightB;
uniform float DarkR;
uniform float DarkG;
uniform float DarkB;
uniform float Threshold;
uniform float Invert;
uniform float Intensity;

out vec4 fragColor;

void main(){
    vec4 diffuseColor = texture(DiffuseSampler, texCoord);
    float luminance = dot(diffuseColor.rgb, vec3(0.299, 0.587, 0.114));

    vec3 brightColor = vec3(BrightR, BrightG, BrightB);
    vec3 darkColor = vec3(DarkR, DarkG, DarkB);

    // Invert swaps which color the bright/dark halves map to, for the flash's middle phase
    vec3 highColor = mix(brightColor, darkColor, Invert);
    vec3 lowColor = mix(darkColor, brightColor, Invert);

    float edge = smoothstep(Threshold - 0.05, Threshold + 0.05, luminance);
    vec3 twoTone = mix(lowColor, highColor, edge);

    vec3 outColor = mix(diffuseColor.rgb, twoTone, Intensity);
    fragColor = vec4(outColor, diffuseColor.a);
}
