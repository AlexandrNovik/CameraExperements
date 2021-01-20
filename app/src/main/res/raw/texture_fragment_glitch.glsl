#version 300 es
    precision highp float;
    in vec2 v_texCoord;
    out vec4 outColor;
    uniform float     iTime;
    uniform sampler2D s_texture;

    float rand(float n)
    {
        return fract(sin(n) * 43758.5453123);
    }

    float rand(vec2 co)
    {
        return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
    }

    vec2 hash( vec2 x )  // replace this by something better
    {
        const vec2 k = vec2( 0.3183099, 0.3678794 );
        x = x*k + k.yx;
        return -1.0 + 2.0*fract( 16.0 * k*fract( x.x*x.y*(x.x+x.y)) );
    }

    float perlin_noise( in vec2 p )
    {
        vec2 i = floor( p );
        vec2 f = fract( p );

        vec2 u = f*f*(3.0-2.0*f);

        return mix( mix( dot( hash( i + vec2(0.0,0.0) ), f - vec2(0.0,0.0) ),
                         dot( hash( i + vec2(1.0,0.0) ), f - vec2(1.0,0.0) ), u.x),
                    mix( dot( hash( i + vec2(0.0,1.0) ), f - vec2(0.0,1.0) ),
                         dot( hash( i + vec2(1.0,1.0) ), f - vec2(1.0,1.0) ), u.x), u.y);
    }

    float noise(float p)
    {
        float fl = floor(p);
        float fc = fract(p);
        return mix(rand(fl), rand(fl + 1.0), fc);
    }

    float blockyNoise(vec2 uv, float threshold, float scale, float seed)
    {
        float scroll = floor(iTime + sin(11.0 *  iTime) + sin(iTime) ) * 0.77;
        vec2 noiseUV = uv.yy / scale + scroll;
        float noise2 = perlin_noise(noiseUV);

        float id = floor( noise2 * 10.0);
        id = noise(id + seed) - 0.5;

        if ( abs(id) > threshold )
            id = 0.0;

        return id;
    }

    vec3 banuba_color()
    {
        const vec3 colorA = vec3(1.0f, 0.78f, 0.278f);
        const vec3 colorB = vec3(1.0f, 0.78f, 0.278f);

        float time = fract(iTime);

        if (fract(rand(vec2(time, pow(time, colorA.g)))) < 0.4)
        {
            return colorA;
        }

        return colorB;

    }

    vec4 glitch( in vec2 fragCoord )
    {
        float rgbIntesnsity         = (0.1 + 0.1 * sin(iTime* 7.7));
        float displaceIntesnsity     = (0.2 + 0.3 * pow(sin(iTime * 1.8), 5.0));
        float interlaceIntesnsity     = (0.045);
        float dropoutIntensity         = (0.1);

        vec2 uv = fragCoord;

        float displace = blockyNoise((uv + vec2(uv.y, 0.0)), displaceIntesnsity, 0.5, 66.6);
        displace *= blockyNoise((uv.yx + vec2(0.0, uv.x)), displaceIntesnsity, 111.0, 13.7);

        uv.x += displace;

        vec2 offs = 0.1 * vec2(blockyNoise(uv.xy + vec2(uv.y, 0.0), rgbIntesnsity, 65.0, 341.0), 0.0);

        float colr = texture(s_texture, uv - offs).r;
        float colg = texture(s_texture, uv).g;
        float colb = texture(s_texture, uv + offs).b;

        vec3 mask = banuba_color();

        float maskNoise = blockyNoise(uv, interlaceIntesnsity, 90.0, iTime) * max(displace, offs.x);

        maskNoise = 1.0 - maskNoise;
        if (maskNoise == 1.0 || displace < 0.01)
        {
            return vec4(colr, colg, colb, 1.0);
        }

        return vec4(mask, 1.0);
    }

    void main()
    {
        outColor = glitch(v_texCoord);
    }