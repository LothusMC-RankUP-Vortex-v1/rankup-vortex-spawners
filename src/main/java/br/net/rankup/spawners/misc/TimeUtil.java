package br.net.rankup.spawners.misc;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public TimeUtil() {}

    public String formatTime(int seconds) {
        long ms = seconds * 1000L;
        long segundos = ms / 1000L % 60L;
        long minutos = ms / 60000L % 60L;
        long horas = ms / 3600000L % 24L;
        long dias = TimeUnit.SECONDS.toDays((ms / 1000L));
        if(segundos != 0 && minutos == 0 && horas == 0 && dias == 0) {
            return segundos + "s";
        }
        if(minutos != 0 && horas == 0 && dias == 0) {
            if(segundos == 0) {
                return minutos + "m";
            }
            return transform(minutos, "m:") + transform(segundos, "s");
        }
        if(horas != 0 && dias == 0) {
            if(segundos == 0 && minutos == 0) {
                return horas + "h";
            }
            if(segundos == 0) {
                return horas +"h:"+ transform(minutos, "m");
            }
            return horas + "h:" + transform(minutos, "m") + transform(segundos, "s");
        }
        if(dias != 0) {
            if(segundos == 0 && minutos == 0 && horas == 0) {
                return dias + "d";
            }
            if(segundos == 0 && minutos == 0) {
                return dias + "d:" + transform(horas, "h");
            }
            if(segundos == 0) {
                return dias + "d:" + transform(horas, "h") + ":" + transform(minutos, "m");
            }
            return dias + "d:" + transform(horas, "h") + ":" + transform(minutos, "m") + ":" + transform(segundos, "s");
        }
        char s = String.valueOf(ms).charAt(0);
        return "0." + s + "s";
    }

    String transform(long i, String suffix) {
        return (i <= 9 ? "0" + i : i) + suffix;
    }

}