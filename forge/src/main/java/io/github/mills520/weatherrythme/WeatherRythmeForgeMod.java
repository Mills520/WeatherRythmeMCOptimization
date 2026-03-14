package io.github.mills520.weatherrythme;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(WeatherRythmeForgeMod.MOD_ID)
public class WeatherRythmeForgeMod {
    public static final String MOD_ID = "weatherrythme";
    private static final int MIN_INTERVAL_TICKS = 5 * 60 * 20;
    private static final int MAX_INTERVAL_TICKS = 15 * 60 * 20;

    private final Map<UUID, Integer> nextWeatherChangeByLevel = new HashMap<>();

    public WeatherRythmeForgeMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.LevelTickEvent event) {
        if (!(event.level instanceof ServerLevel level)) {
            return;
        }
        if (event.phase != TickEvent.Phase.END || level.dimension() != Level.OVERWORLD) {
            return;
        }

        UUID key = UUID.nameUUIDFromBytes(level.dimension().location().toString().getBytes());
        int ticksRemaining = nextWeatherChangeByLevel.getOrDefault(key, 0) - 1;
        if (ticksRemaining > 0) {
            nextWeatherChangeByLevel.put(key, ticksRemaining);
            return;
        }

        applyRandomWeather(level);
        nextWeatherChangeByLevel.put(key, nextInterval(level.getRandom()));
    }

    private void applyRandomWeather(ServerLevel level) {
        RandomSource random = level.getRandom();
        boolean makeRain = random.nextBoolean();
        boolean makeThunder = makeRain && random.nextBoolean();

        int clearDuration = makeRain ? 0 : nextInterval(random);
        int rainDuration = makeRain ? nextInterval(random) : 0;
        int thunderDuration = makeThunder ? rainDuration : 0;

        level.setWeatherParameters(clearDuration, rainDuration, makeRain, makeThunder);
    }

    private int nextInterval(RandomSource random) {
        return MIN_INTERVAL_TICKS + random.nextInt(MAX_INTERVAL_TICKS - MIN_INTERVAL_TICKS + 1);
    }
}
