package dev.ferretbeans.pawtism;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PawtismClientClient implements ClientModInitializer {
    //mod shit i forgot the term for it lol
	public static final String MOD_ID = "pawtism-client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier miau = Identifier.of(MOD_ID, "miau");

	private static final DecimalFormat decimal = new DecimalFormat("#.#");
	private static final DecimalFormat dc = new DecimalFormat("#");

	private static final double[] pos = new double[3];

	private int ping = -1;

	private boolean loggedServerAddress = false;

    //get the minecraft instance
    MinecraftClient client = MinecraftClient.getInstance();
	ClientPlayerEntity player = client.player;

    public void startup() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
        }).build();
        DiscordRPC.discordInitialize("1404156522254045304", handlers, true);
    }

    public void createNewPresence() {
        DiscordRichPresence rich = new DiscordRichPresence.Builder("🐾").setDetails("being silly >w<").setBigImage("671eb0df9f6897d3774beae52e9c4a56edf42226_full_1_", "woaw").build();
        DiscordRPC.discordUpdatePresence(rich);
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Pawtism has been loaded >:3");

        startup();
        createNewPresence();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            loggedServerAddress = false;
            ping = -1;  // reset ping on disconnect
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!loggedServerAddress && client.player != null && client.world != null && !client.isInSingleplayer()) {
                if (client.getNetworkHandler() != null) {
                    ClientConnection connection = client.getNetworkHandler().getConnection();
                    SocketAddress socketAddress = connection.getAddress();

                    if (socketAddress instanceof InetSocketAddress inetSocket) {
                        String host = inetSocket.getHostString();
                        int port = inetSocket.getPort();

                        loggedServerAddress = true;

                        // Async ping with exception handling here (outside pingServer)
                        CompletableFuture.runAsync(() -> {
                            int result;
                            try {
                                result = pingServer(host, port, 500);
                            } catch (IOException e) {
                                result = -1;
                            }
                            ping = result;

                            if (ping >= 0) {
                                LOGGER.info("Ping to server {}:{} = {} ms", host, port, ping);
                            } else {
                                LOGGER.info("Ping failed to server {}:{}", host, port);
                            }
                        });
                    }
                }
            }
        });

        //so basically this just gets the value 0 from fps.java and then updates it in there
        //to then go to the text drawer
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            Fps.fps = client.getCurrentFps();
        });

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                Vec3d playerPos = client.player.getPos();
                pos[0] = playerPos.x;
                pos[1] = playerPos.y;
                pos[2] = playerPos.z;            
			}
        });

		//draws text to screen
        HudElementRegistry.addFirst(miau, ((context, tickCounter) -> {
			//font size :3
			context.getMatrices().pushMatrix();
			context.getMatrices().scale(0.7f, 0.7f);

			//top left stuff
			Text tps = Text.literal("TPS: ").append(Text.literal(decimal.format(statictps.tpscurr)).styled(style -> style.withColor(0xC2C2C2C2)));
			Text fpstext = Text.literal("FPS: ").styled(style -> style.withColor(0xFFFFFFFF));
			Text number = Text.literal(String.valueOf(Fps.fps)).styled(style -> style.withColor(0xC2C2C2C2));
			Text fps = fpstext.copy().append(number);
			Text lag = Text.literal("Ping: ").append(Text.literal(String.valueOf(ping)).styled(style -> style.withColor(0xC2C2C2C2)));

			Text coords = Text.literal("Pos: ").styled(style -> style.withColor(0xFFFFFFFF));
			Text posval = Text.literal(dc.format(pos[0]) + ", " + dc.format(pos[1]) + ", " + dc.format(pos[2])).styled(style -> style.withColor(0xC2C2C2));
			Text position = coords.copy().append(posval);

			//actual info >w<
			context.drawText(client.textRenderer, "pawtism :3", 2, 2, 0xFFFFFFFF, false);
            context.drawText(client.textRenderer, fps, 2, 12, 0xFFFFFFFF, false);
            context.drawText(client.textRenderer, tps, 1, 22, 0xFFFFFFFF, false);
			context.drawText(client.textRenderer, lag, 2, 32, 0xFFFFFFFF, false);
			context.drawText(client.textRenderer, position, 2, 42, 0xFFFFFFFF, false);

			context.getMatrices().popMatrix();
        }));
    }
	
	public static int pingServer(String host, int port, int timeoutMillis) throws IOException {
        long start = System.currentTimeMillis();
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), timeoutMillis);
        socket.close();
        return (int) (System.currentTimeMillis() - start);
    }
}