package br.net.rankup.spawners.misc;

import java.util.concurrent.TimeUnit;

public class TimeFormat {
    public static String formatTime(final int seconds) {
        final long ms = seconds * 1000L;
        final long segundos = ms / 1000L % 60L;
        final long minutos = ms / 60000L % 60L;
        final long horas = ms / 3600000L % 24L;
        final long dias = TimeUnit.SECONDS.toDays(ms / 1000L);
        if(ms == 0) {
        	return "Gerando...";
        }
        
        if (segundos != 0L && minutos == 0L && horas == 0L && dias == 0L) {
            return segundos + "s";
        }
        if (minutos != 0L && horas == 0L && dias == 0L) {
            if (segundos == 0L) {
                return minutos + "m";
            }
            return minutos + "m e " + ((segundos < 10L) ? ("0" + segundos) : Long.valueOf(segundos)) + "s";
        }
        else if (horas != 0L && dias == 0L) {
            if (segundos == 0L && minutos == 0L) {
                return horas + " hora(s)";
            }
            if (segundos == 0L) {
                return horas + " hora(s) e " + transform(minutos, " min(s)");
            }
            return horas + " hora(s) e " + transform(minutos, " min(s) e ") + transform(segundos, " seg(s)");
        }
        else {
            if (dias == 0L) {
                final char s = String.valueOf(ms).charAt(0);
                return "0." + s + " seg(s)";
            }
            if (segundos == 0L && minutos == 0L && horas == 0L) {
                return dias + " dia(s)";
            }
            if (segundos == 0L && minutos == 0L) {
                return dias + " dia(s) e " + transform(horas, " hora(s)");
            }
            if (segundos == 0L) {
                return dias + " dia(s) e " + transform(horas, " horas ") + "e " + transform(minutos, " min(s)");
            }
            return dias + " dia(s) e " + transform(horas, " hora(s)") + " e " + transform(minutos, " min(s)") + " e " + transform(segundos, " segundo(s)");
        }
    }
    
    static String transform(final long i, final String suffix) {
        return ((i <= 9L) ? ("0" + i) : Long.valueOf(i)) + suffix;
    }

}
