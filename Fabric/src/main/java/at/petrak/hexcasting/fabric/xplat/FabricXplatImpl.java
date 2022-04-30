package at.petrak.hexcasting.fabric.xplat;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.player.FlightAbility;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPattern;
import at.petrak.hexcasting.common.network.IMessage;
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import at.petrak.hexcasting.xplat.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FabricXplatImpl implements IXplatAbstractions {
    @Override
    public Platform platform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public void sendPacketToPlayer(ServerPlayer target, IMessage packet) {
        ServerPlayNetworking.send(target, packet.getFabricId(), packet.toBuf());
    }

    @Override
    public void sendPacketToServer(IMessage packet) {
        ClientPlayNetworking.send(packet.getFabricId(), packet.toBuf());
    }

    @Override
    public void brainsweep(Mob mob) {
        var cc = HexCardinalComponents.BRAINSWEPT.get(mob);
        cc.setBrainswept(true);
        // CC API does the syncing for us
    }

    @Override
    public void setColorizer(Player target, FrozenColorizer colorizer) {
        var cc = HexCardinalComponents.FAVORED_COLORIZER.get(target);
        cc.setColorizer(colorizer);
    }

    @Override
    public void setSentinel(Player target, Sentinel sentinel) {
        var cc = HexCardinalComponents.SENTINEL.get(target);
        cc.setSentinel(sentinel);
    }

    @Override
    public void setFlight(ServerPlayer target, FlightAbility flight) {
        var cc = HexCardinalComponents.FLIGHT.get(target);
        cc.setFlight(flight);
    }

    @Override
    public void setHarness(ServerPlayer target, CastingHarness harness) {
        var cc = HexCardinalComponents.HARNESS.get(target);
        cc.setHarness(harness);
    }

    @Override
    public void setPatterns(ServerPlayer target, List<ResolvedPattern> patterns) {
        var cc = HexCardinalComponents.PATTERNS.get(target);
        cc.setPatterns(patterns);
    }

    @Override
    public boolean isBrainswept(Mob mob) {
        var cc = HexCardinalComponents.BRAINSWEPT.get(mob);
        return cc.isBrainswept();
    }

    @Override
    public FlightAbility getFlight(ServerPlayer player) {
        var cc = HexCardinalComponents.FLIGHT.get(player);
        return cc.getFlight();
    }

    @Override
    public FrozenColorizer getColorizer(Player player) {
        var cc = HexCardinalComponents.FAVORED_COLORIZER.get(player);
        return cc.getColorizer();
    }

    @Override
    public Sentinel getSentinel(Player player) {
        var cc = HexCardinalComponents.SENTINEL.get(player);
        return cc.getSentinel();
    }

    @Override
    public CastingHarness getHarness(ServerPlayer player, InteractionHand hand) {
        var cc = HexCardinalComponents.HARNESS.get(player);
        return cc.getHarness(hand);
    }

    @Override
    public List<ResolvedPattern> getPatterns(ServerPlayer player) {
        var cc = HexCardinalComponents.PATTERNS.get(player);
        return cc.getPatterns();
    }

    @Override
    public void clearCastingData(ServerPlayer player) {
        this.setHarness(player, null);
        this.setPatterns(player, List.of());
    }

    @Override
    public boolean isColorizer(ItemStack stack) {
        return HexCardinalComponents.COLORIZER.isProvidedBy(stack);
    }

    @Override
    public int getRawColor(FrozenColorizer colorizer, float time, Vec3 position) {
        var cc = HexCardinalComponents.COLORIZER.maybeGet(colorizer.item());
        return cc.map(col -> col.color(colorizer.owner(), time, position)).orElse(0xff_ff00dc);
    }
}
