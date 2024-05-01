package com.ustwo.lwp.wallpapers.util.owm;


import com.ustwo.lwp.wallpapers.util.WeatherVo;

//https://openweathermap.org/weather-conditions#Weather-Condition-Codes-2
public class OWMWeatherCode {
    private final int code;

    public OWMWeatherCode(int code) {
        this.code = code;
    }

    private static WeatherVo weatherVoFromWeather(int weatherId) {
        WeatherVo weatherVo = new WeatherVo();
        weatherVo.conditions = new int[]{getWeatherVoCode(weatherId)};
        return weatherVo;
    }
    private static int getWeatherVoCode(int weatherId){
        //           0                1            2              3           4         5       6            7               8            9
        //"UNKNOWN", "CLEAR", "CLOUDY", "FOGGY", "HAZY", "ICY", "RAINY", "SNOWY", "STORMY", "WINDY"
        //No ICY avalable. So, let it be drizzle
        //Windy is outta here
        if (weatherId < 200) return 0;
        if (weatherId < 300) return 8;
        if (weatherId < 400) return 5;
        if (weatherId < 600) return 6;
        if (weatherId < 700) return 7;
        if (weatherId == 701) return 3;
        if (weatherId == 711) return 3;
        if (weatherId == 721) return 4;
        if (weatherId == 731) return 4;
        if (weatherId == 741) return 3;
        if (weatherId == 751) return 4;
        if (weatherId == 761) return 4;
        if (weatherId == 762) return 4;
        if (weatherId == 771) return 8;
        if (weatherId == 781) return 8;
        if (weatherId >= 800 && weatherId <=802) return 1;
        if (weatherId < 900) return 2;
        return 0;
    }

    public static String getDescription(int weatherId) {
        if (weatherId < 200) return "Unknown";

        if (weatherId < 300) return "Thunderstorm";
        if (weatherId < 400) return "Drizzle";
        if (weatherId < 600) return "Rain";
        if (weatherId < 700) return "Snow";
        if (weatherId == 701) return "Mist";
        if (weatherId == 711) return "Smoke";
        if (weatherId == 721) return "Haze";
        if (weatherId == 731) return "Dust";
        if (weatherId == 741) return "Fog";
        if (weatherId == 751) return "Sand";
        if (weatherId == 761) return "Dust";
        if (weatherId == 762) return "Ash";
        if (weatherId == 771) return "Squall";
        if (weatherId == 781) return "Tornado";
        if (weatherId == 800) return "Clear";
        if (weatherId < 900) return "Clouds";
        return "Unknown";
    }
    public String getDescription() {
       return getDescription(code);
    }
    public WeatherVo getWeatherVo() {
        return weatherVoFromWeather(code);
    }
}
