#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform float Strength;

out vec4 fragColor;

void main(){
    vec2 center = vec2(0.5, 0.5);
    vec2 toCenter = texCoord - center;
    float dist = length(toCenter);
    vec2 direction = dist > 0.0001 ? normalize(toCenter) : vec2(0.0);

    float offset = dist * Strength * 0.02;

    float r = texture(DiffuseSampler, texCoord + direction * offset).r;
    float g = texture(DiffuseSampler, texCoord).g;
    float b = texture(DiffuseSampler, texCoord - direction * offset).b;

    fragColor = vec4(r, g, b, 1.0);
}
