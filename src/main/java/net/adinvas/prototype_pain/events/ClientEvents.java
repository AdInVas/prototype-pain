package net.adinvas.prototype_pain.events;

import net.adinvas.prototype_pain.Keybinds;
import net.adinvas.prototype_pain.PlayerHealthProvider;
import net.adinvas.prototype_pain.PrototypePain;
import net.adinvas.prototype_pain.client.ContiousnessOverlay;
import net.adinvas.prototype_pain.client.OverlayController;
import net.adinvas.prototype_pain.client.PainOverlay;
import net.adinvas.prototype_pain.client.gui.HealthScreen;
import net.adinvas.prototype_pain.fluid_system.items.IMedicalFluidContainer;
import net.adinvas.prototype_pain.fluid_system.gui.FluidExchangeScreen;
import net.adinvas.prototype_pain.limbs.PlayerHealthData;
import net.adinvas.prototype_pain.network.GiveUpPacket;
import net.adinvas.prototype_pain.network.LegUsePacket;
import net.adinvas.prototype_pain.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(modid = PrototypePain.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    static int GiveUpTime = 40;
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ProfilerFiller profiler = mc.getProfiler();

        profiler.push("prototype_pain:client_misc");
        if (player==null)return;
        if (event.side== LogicalSide.CLIENT) {
            AtomicBoolean uncontious = new AtomicBoolean(false);
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                if (h.getContiousness()<=4){
                    uncontious.set(true);
                }
            });
            if (Keybinds.OPEN_PAIN_GUI.isDown()&&!uncontious.get()) {
                Keybinds.OPEN_PAIN_GUI.consumeClick();

                Player target = ModEvents.getLookedAtPlayer(player, 2);
                boolean self = target == null || player.isShiftKeyDown();

                if (self) {
                    Minecraft.getInstance().setScreen(new HealthScreen(player.getUUID()));
                } else {
                    Minecraft.getInstance().setScreen(new HealthScreen(target.getUUID()));
                }

            }



            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                if (h.getContiousness()<=4){
                    if (Keybinds.GIVE_UP.isDown()){
                        GiveUpTime--;
                    }else {
                        GiveUpTime = 40;
                    }
                }
            });
            if (GiveUpTime<=0){
                ModNetwork.CHANNEL.sendToServer(new GiveUpPacket());
                GiveUpTime = 40;
            }
            profiler.pop();
        }
        PainOverlay overlay = OverlayController.getOverlay(PainOverlay.class);
        ContiousnessOverlay conc = OverlayController.getOverlay(ContiousnessOverlay.class);

        overlay.calculate(player);
        conc.calculate(player);

        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                float contiousness = (100-h.getContiousness())/100;

                if (contiousness<=4){
                    mc.player.setYRot(mc.player.yRotO); // reset yaw
                    mc.player.setXRot(mc.player.xRotO); // reset pitch
                }
            });
    }

    @SubscribeEvent
    public static void onScreenKey(ScreenEvent.KeyPressed.Post event){
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Check if the current screen is an inventory or container
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            if (Keybinds.OPEN_FLUID_SCREEN.matches(event.getKeyCode(), event.getScanCode())) {
                ItemStack carried = screen.getMenu().getCarried();
                Slot hovered = screen.getSlotUnderMouse();

                if (hovered != null &&
                        carried.getItem() instanceof IMedicalFluidContainer &&
                        hovered.getItem().getItem() instanceof IMedicalFluidContainer) {

                    mc.setScreen(new FluidExchangeScreen(hovered.getItem(), carried,hovered.getSlotIndex()));
                    event.setCanceled(true); // prevent the inventory from also handling it
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInputUpdate(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        if (player!=null){
            player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h->{
                if (h.getContiousness()<5) { // your condition here
                    event.getInput().down = false;
                    event.getInput().forwardImpulse = 0;
                    event.getInput().jumping = false;
                    event.getInput().up = false;
                    event.getInput().left = false;
                    event.getInput().leftImpulse= 0;
                    event.getInput().right =false;
                    event.getInput().shiftKeyDown = false;
                }
                if (event.getInput().leftImpulse!=0||event.getInput().forwardImpulse!=0||event.getInput().jumping){
                    ModNetwork.CHANNEL.sendToServer(new LegUsePacket());
                }
            });
        }
    }
    private static final ResourceLocation pain_tex = new ResourceLocation(PrototypePain.MOD_ID,"textures/gui/icons/pain.png");

    @SubscribeEvent
    public static void onOpenInventory(ScreenEvent.Opening event) {
        if (!(event.getScreen() instanceof InventoryScreen)) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA).ifPresent(h -> {
            if (h.getContiousness() <= 4) {
                event.setCanceled(true); // block opening inventory
            }
        });
    }

    @SubscribeEvent
    public static void onContainerDraw(ScreenEvent.Render event){
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen))return;
        Slot hovered = screen.getSlotUnderMouse();

        ItemStack carried = screen.getMenu().getCarried();
        if (hovered!=null &&carried.getItem() instanceof IMedicalFluidContainer from && hovered.getItem().getItem() instanceof IMedicalFluidContainer to){
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();
            Component text = Component.translatable("prototype_pain.gui.fluid_screen",Component.keybind("key.prototype_pain.fluid_screen"));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, text,mouseX,mouseY);
        }
    }

    static int tick =0;
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()||event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()||event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            // cancel rendering of the vanilla health bar
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            float Oxygen = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                    .map(PlayerHealthData::getOxygen)
                    .orElse(0f);

            double Pain = player.getCapability(PlayerHealthProvider.PLAYER_HEALTH_DATA)
                            .map(PlayerHealthData::getTotalPain)
                                    .orElse(0d);

            GuiGraphics gui = event.getGuiGraphics();

            int x = mc.getWindow().getGuiScaledWidth() / 2 - 91; // same place as hearts
            int y = mc.getWindow().getGuiScaledHeight() - 39;

            gui.drawString(mc.font,"O₂ "+(int)Oxygen+"%",x,y,0xFFFFFF);
            gui.blit(pain_tex,x+50,y-2,0,0,10,10,10,10);
            gui.drawString(mc.font,Integer.valueOf((int) Pain)+"%",x+60,y,0xFFFFFF);
            event.setCanceled(true);
        }
    }
}