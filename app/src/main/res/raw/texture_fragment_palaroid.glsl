#version 300 es
    precision highp float;
    uniform float iTime;                 // shader playback time (in seconds)

    in vec2 v_texCoord;
    out vec4 outColor1;
    uniform sampler2D s_texture;

    float easeLinear(float t, float b, float c, float d)
    {
        return t / d * c + b;
    }

    vec4 grayscale(in vec4 color, float factor)
    {
        vec3 gray = vec3(0.3, 0.59, 0.11);
        float col = dot(color.xyz, gray);
        return vec4(mix(color.xyz, vec3(col), factor), color.w);
    }

    vec4 vhs_color_correction(in vec4 inColor, float add)
    {
        float avg = grayscale(inColor, 0.5).r + add;
        inColor.r *= abs(cos(avg));
        inColor.g *= abs(sin(avg));
        inColor.b *= abs(atan(avg) * sin(avg));
        return inColor + 0.3;
    }

    vec4 developed(in vec4 color)
    {
        return vhs_color_correction(color, 0.3);
    }

    void main()
    {
        vec2 uv = v_texCoord;

        const float animDuration = 5.0; // delay(0.5) + up(1.5) + hold(2.0) +  down(1.5)
        float time = mod(iTime, animDuration);

        time -= 0.0; // delay;

        float lerp = 0.4;

        if (time >= 0.0)
                {
                    if (time <= 1.5)
                    {
                        lerp = easeLinear(time, 0.4, 0.4, 1.5);
                    }
                    else if (time <= 3.5)
                    {
                        lerp = easeLinear(1.5, 0.4, 0.4, 1.5);
                    }
                    else if (time <= 5.0)
                    {
                        lerp = easeLinear(time - 3.5, 0.8, -0.4, 1.5);
        //                lerp = mix(0.8, 0.4, (time-3.5) / 1.5);
                    }
                }

        vec4 base = texture(s_texture, uv);
        outColor1 = mix(base, developed(base), lerp);
    }