package at.petrak.hexcasting.xplat;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.player.FlightAbility;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPattern;
import at.petrak.hexcasting.common.command.PatternResLocArgument;
import at.petrak.hexcasting.common.network.IMessage;
import at.petrak.hexcasting.common.network.MsgColorizerUpdateAck;
import at.petrak.hexcasting.common.network.MsgSentinelStatusUpdateAck;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface IXplatAbstractions {
    Platform platform();

    boolean isPhysicalClient();


    void sendPacketToPlayer(ServerPlayer target, IMessage packet);

    void sendPacketToServer(IMessage packet);

    // Things that used to be caps

    /**
     * Irregardless of whether it can actually be brainswept (you need to do the checking yourself)
     */
    void brainsweep(Mob mob);

    void setColorizer(Player target, FrozenColorizer colorizer);

    void setSentinel(Player target, Sentinel sentinel);

    void setFlight(ServerPlayer target, FlightAbility flight);

    void setHarness(ServerPlayer target, @Nullable CastingHarness harness);

    void setPatterns(ServerPlayer target, List<ResolvedPattern> patterns);

    boolean isBrainswept(Mob mob);

    FlightAbility getFlight(ServerPlayer player);

    FrozenColorizer getColorizer(Player player);

    Sentinel getSentinel(Player player);

    CastingHarness getHarness(ServerPlayer player, InteractionHand hand);

    List<ResolvedPattern> getPatterns(ServerPlayer player);

    void clearCastingData(ServerPlayer player);

    // coooollooorrrs

    boolean isColorizer(ItemStack stack);

    int getRawColor(FrozenColorizer colorizer, float time, Vec3 position);

    default void syncSentinel(ServerPlayer player) {
        this.sendPacketToPlayer(player, new MsgSentinelStatusUpdateAck(this.getSentinel(player)));
    }

    default void syncColorizer(ServerPlayer player) {
        this.sendPacketToPlayer(player, new MsgColorizerUpdateAck(this.getColorizer(player)));
    }

    default void init() {
        HexAPI.LOGGER.info("Hello Hexcasting! This is {}!", this.platform());

        ArgumentTypes.register(
            "hexcasting:pattern",
            PatternResLocArgument.class,
            new EmptyArgumentSerializer<>(PatternResLocArgument::id)
        );
    }


    IXplatAbstractions INSTANCE = find();

    private static IXplatAbstractions find() {
        var providers = ServiceLoader.load(IXplatAbstractions.class).stream().toList();
        if (providers.size() != 1) {
            var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
            throw new IllegalStateException(
                "There should be exactly one IXplatAbstractions implementation on the classpath. Found: " + names);
        } else {
            var provider = providers.get(0);
            HexAPI.LOGGER.debug("Instantiating xplat impl: " + provider.type().getName());
            return provider.get();
        }
    }
}
