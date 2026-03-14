package io.github.mills520.weatherrythme;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WeatherRythmeFabricMod implements ModInitializer {
    public static final String MOD_ID = "weatherrythme";
    private static final int MIN_INTERVAL_TICKS = 5 * 60 * 20;
    private static final int MAX_INTERVAL_TICKS = 15 * 60 * 20;

    private final Map<UUID, Integer> nextWeatherChangeByLevel = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    private void onServerTick(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension() != Level.OVERWORLD) {
                continue;
            }

            UUID key = UUID.nameUUIDFromBytes(
                level.dimension().location().toString().getBytes(StandardCharsets.UTF_8)
            );

            int ticksRemaining = nextWeatherChangeByLevel.getOrDefault(key, 0) - 1;
            if (ticksRemaining > 0) {
                nextWeatherChangeByLevel.put(key, ticksRemaining);
                continue;
            }

            applyRandomWeather(level);
            nextWeatherChangeByLevel.put(key, nextInterval(level.getRandom()));
        }
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
