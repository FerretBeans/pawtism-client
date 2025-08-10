package dev.ferretbeans.pawtism;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PawtismClient implements ModInitializer {
	public static final String MOD_ID = "pawtism-client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("guh");
    }
}
