package com.mc3699.smparch.util.CPMPlugin;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
 
import java.lang.reflect.*;
import java.util.function.Function;
import java.util.function.Supplier;
 
/**
 * Bridges smparch to Customizable Player Models via reflection, without a compile dependency.
 *
 * CPM's API contract (confirmed from IClientAPI source, 0.6.27a):
 *   PlayerRenderer has NO render method. The sequence is:
 *     1. setGameProfile(profile)      - which player's model to mirror
 *     2. setRenderModel(model)        - the vanilla HumanoidModel instance we're about to draw
 *     3. preRender(buffers, PLAYER)   - CPM binds its custom model to that model instance
 *     4. model.renderToBuffer(...)    - WE draw the vanilla model; CPM's hooks intercept
 *                                       this call and draw the custom model instead
 *     5. postRender()                 - MUST run if preRender ran, or CPM's shared state
 *                                       is left poisoned (the old itemTransforms cascade)
 *
 * All of that is wrapped in {@link #renderCPMModel}. Callers should not touch the raw
 * Methods; the old getters are gone on purpose.
 */
public class CPMReflectionHelper {
 
    private static Object clientApi;
    private static Object playerRenderer;
    private static boolean initialized = false;
    private static boolean apiReady = false;
 
    // Cached reflective handles (interface methods; parameter types are erased to Object)
    private static Method setGameProfile;
    private static Method setRenderModel;
    private static Method preRender;
    private static Method postRender;
    private static Method getDefaultTexture;
    private static Method getDefaultRenderType;
    private static Object animModePlayer; // AnimationEngine.AnimationMode.PLAYER
 
    // Diagnostics: log each distinct failure once, not 60x/second
    private static boolean renderErrorLogged = false;
    private static boolean missingModelLogged = false;
 
    // ------------------------------------------------------------------ init
 
    public static boolean isCPMLoaded() {
        return ModList.get().isLoaded("cpm");
    }
 
    public static boolean isApiReady() {
        return apiReady;
    }
 
    /** Must be called during InterModEnqueueEvent so CPM reliably receives the IMC message. */
    public static void initialize() {
        if (!isCPMLoaded() || FMLEnvironment.dist != Dist.CLIENT || initialized)
            return;
        initialized = true;
 
        try {
            Class<?> icpmPlugin = Class.forName("com.tom.cpm.api.ICPMPlugin");
 
            Object pluginProxy = Proxy.newProxyInstance(
                    icpmPlugin.getClassLoader(),
                    new Class<?>[] { icpmPlugin },
                    (proxy, method, args) -> {
                        String name = method.getName();
                        try {
                            if (name.equals("initClient")) {
                                clientApi = args[0];
                                apiReady = buildMethods();
                                log(apiReady ? "CPM: API ready." : "\u00a7cCPM: method caching failed.");
                                return null;
                            }
                            if (name.equals("initServer"))
                                return null;
                            if (name.equals("getOwnerModId"))
                                return "smparch";
 
                            // Benign defaults for anything else CPM asks of the plugin
                            Class<?> ret = method.getReturnType();
                            if (ret == void.class) return null;
                            if (ret == boolean.class) return false;
                            if (ret == byte.class) return (byte) 0;
                            if (ret == short.class) return (short) 0;
                            if (ret == int.class) return 0;
                            if (ret == long.class) return 0L;
                            if (ret == float.class) return 0.0f;
                            if (ret == double.class) return 0.0d;
                            if (ret == char.class) return '\0';
                            if (ret == String.class) return "";
                            if (ret.isArray()) return Array.newInstance(ret.getComponentType(), 0);
                            return null;
                        } catch (Throwable t) {
                            logError("CPM proxy error in " + name, t);
                            return null;
                        }
                    });
 
            Class<?> imcClass = Class.forName("net.neoforged.fml.InterModComms");
            Method sendTo = imcClass.getMethod("sendTo", String.class, String.class, Supplier.class);
            Supplier<Object> innerSupplier = () -> pluginProxy;
            Supplier<Object> outerSupplier = () -> innerSupplier;
            sendTo.invoke(null, "cpm", "api", outerSupplier);
 
            log("CPM: IMC message sent. Waiting for API...");
        } catch (Throwable t) {
            logError("CPM init error", t);
        }
    }
 
    private static boolean buildMethods() {
        try {
            Method createRenderer = clientApi.getClass().getMethod(
                    "createPlayerRenderer",
                    Class.class, Class.class, Class.class, Class.class, Class.class);
            playerRenderer = createRenderer.invoke(clientApi,
                    HumanoidModel.class, ResourceLocation.class, RenderType.class,
                    MultiBufferSource.class, GameProfile.class);
 
            Class<?> iface = Class.forName("com.tom.cpm.api.IClientAPI$PlayerRenderer");
 
            // Exact contract methods. Generic params erase to Object, so match by name+arity.
            setGameProfile       = findMethod(iface, "setGameProfile", 1);
            setRenderModel       = findMethod(iface, "setRenderModel", 1);
            preRender            = findMethod(iface, "preRender", 2);
            postRender           = findMethod(iface, "postRender", 0);
            getDefaultTexture    = findMethod(iface, "getDefaultTexture", 0);
            getDefaultRenderType = findMethod(iface, "getDefaultRenderType", 0);
 
            // Render type factory CPM uses when it needs one from a texture
            Method setRenderType = findMethod(iface, "setRenderType", 1);
            setRenderType.invoke(playerRenderer,
                    (Function<ResourceLocation, RenderType>) RenderType::entityTranslucent);
 
            // Patch: CPM's AnimationState NPEs in getMainPose() if syncState is null
            // for a renderer that isn't backed by a real tracked player.
            Method getAnimationState = findMethod(iface, "getAnimationState", 0);
            Object animState = getAnimationState.invoke(playerRenderer);
            Field syncField = animState.getClass().getField("syncState");
            if (syncField.get(animState) == null) {
                Class<?> sas = Class.forName("com.tom.cpm.shared.animation.ServerAnimationState");
                syncField.set(animState, sas.getDeclaredConstructor().newInstance());
            }
 
            // AnimationMode.PLAYER — second param of preRender is the concrete enum class
            Type[] paramTypes = preRender.getGenericParameterTypes();
            if (!(paramTypes[1] instanceof Class<?> animClass) || !animClass.isEnum()) {
                throw new IllegalStateException("Could not extract AnimationMode class from preRender");
            }
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Object player = Enum.valueOf((Class<Enum>) animClass, "PLAYER");
            animModePlayer = player;
 
            return true;
        } catch (Throwable t) {
            logError("CPM method build error", t);
            playerRenderer = null;
            setGameProfile = null;
            setRenderModel = null;
            preRender = null;
            postRender = null;
            getDefaultTexture = null;
            getDefaultRenderType = null;
            animModePlayer = null;
            return false;
        }
    }
 
    private static Method findMethod(Class<?> clazz, String name, int paramCount) throws NoSuchMethodException {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == paramCount) {
                m.setAccessible(true);
                return m;
            }
        }
        throw new NoSuchMethodException(name + "/" + paramCount + " not found in " + clazz.getName());
    }
 
    // ---------------------------------------------------------------- render
 
    /**
     * Draws {@code model} as {@code profile}'s CPM model. Call from your entity renderer
     * AFTER you have positioned the pose stack and called {@code model.setupAnim(...)}.
     *
     * @return true if something was drawn. false means: API not ready, null args, CPM has
     *         no model/texture for this profile (check your profile sync!), or CPM threw
     *         (logged once with the real cause).
     */
    public static boolean renderCPMModel(GameProfile profile, HumanoidModel<?> model,
            PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
 
        if (!apiReady || profile == null || model == null)
            return false;
 
        boolean preRendered = false;
        try {
            setGameProfile.invoke(playerRenderer, profile);
            setRenderModel.invoke(playerRenderer, model);
            preRender.invoke(playerRenderer, buffers, animModePlayer);
            preRendered = true;
 
            ResourceLocation tex = (ResourceLocation) getDefaultTexture.invoke(playerRenderer);
            if (tex == null) {
                // CPM has no model data for this profile on THIS client.
                // If observers hit this while the spawner doesn't, the ghost's
                // GameProfile is not being synced to other clients.
                if (!missingModelLogged) {
                    missingModelLogged = true;
                    log("\u00a7cCPM: no texture/model for profile "
                            + profile.getName() + " (" + profile.getId() + ") — is the profile synced to this client?");
                }
                return false;
            }
 
            RenderType rt = (RenderType) getDefaultRenderType.invoke(playerRenderer);
            if (rt == null)
                rt = RenderType.entityTranslucent(tex);
            VertexConsumer vc = buffers.getBuffer(rt);
 
            // Render into an isolated PoseStack seeded with the current transform, so a
            // failure inside CPM's intercepted renderToBuffer can never unbalance the
            // frame's stack ("Pose stack not empty" crash).
            PoseStack local = new PoseStack();
            local.last().pose().mul(poseStack.last().pose());
            local.last().normal().mul(poseStack.last().normal());
 
            model.renderToBuffer(local, vc, packedLight, OverlayTexture.NO_OVERLAY, -1);
            return true;
        } catch (Throwable t) {
            logError("CPM render failed", t);
            return false;
        } finally {
            if (preRendered) {
                try {
                    postRender.invoke(playerRenderer);
                } catch (Throwable t) {
                    logError("CPM postRender failed", t);
                }
            }
        }
    }
 
    /**
     * Runs {@code renderBody} bracketed by CPM's preRender/postRender, with postRender
     * GUARANTEED to run if preRender ran — even if the body throws. Use this from entity
     * renderers whose body is {@code super.render(...)} (so layers / held items still work).
     *
     * The body should render into an ISOLATED PoseStack (seed a fresh one from the real
     * stack) so that a throw mid-body cannot unbalance the frame.
     *
     * @return true if the CPM path was ATTEMPTED (even if the body then threw — in that
     *         case partial geometry may have been emitted, so do NOT render a fallback).
     *         false only if CPM was never engaged (not ready / null args): safe to fall
     *         back to vanilla rendering.
     */
    public static boolean renderWithCPM(GameProfile profile, HumanoidModel<?> model,
            MultiBufferSource buffers, Runnable renderBody) {
        if (!apiReady || profile == null || model == null)
            return false;
 
        boolean preRendered = false;
        try {
            setGameProfile.invoke(playerRenderer, profile);
            setRenderModel.invoke(playerRenderer, model);
            preRender.invoke(playerRenderer, buffers, animModePlayer);
            preRendered = true;
 
            renderBody.run();
            return true;
        } catch (Throwable t) {
            logError("CPM render failed", t);
            return preRendered; // attempted iff we got past preRender
        } finally {
            if (preRendered) {
                try {
                    postRender.invoke(playerRenderer);
                } catch (Throwable t) {
                    logError("CPM postRender failed", t);
                }
            }
        }
    }
 
    /** Texture lookup only (e.g. for layers). Prefer {@link #renderCPMModel} for drawing. */
    public static ResourceLocation getCPMTexture(GameProfile profile, HumanoidModel<?> model) {
        if (!apiReady || profile == null)
            return null;
        try {
            setGameProfile.invoke(playerRenderer, profile);
            if (model != null)
                setRenderModel.invoke(playerRenderer, model);
            return (ResourceLocation) getDefaultTexture.invoke(playerRenderer);
        } catch (Throwable t) {
            logError("CPM getCPMTexture error", t);
            return null;
        }
    }
 
    // --------------------------------------------------------------- logging
 
    private static void log(String msg) {
        System.out.println(msg);
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            mc.execute(() -> {
                if (mc.player != null)
                    mc.player.displayClientMessage(Component.literal(msg), false);
            });
        }
    }
 
    /** Logs the REAL cause (unwraps InvocationTargetException) with a full stack trace, once per session for render errors. */
    private static void logError(String context, Throwable t) {
        Throwable cause = t instanceof InvocationTargetException ite && ite.getCause() != null
                ? ite.getCause() : t;
        boolean isRender = context.startsWith("CPM render") || context.startsWith("CPM postRender");
        if (isRender) {
            if (renderErrorLogged) return;
            renderErrorLogged = true;
        }
        System.err.println("[smparch] " + context + ": " + cause);
        cause.printStackTrace();
        log("\u00a7c[smparch] " + context + ": " + cause.getClass().getSimpleName()
                + (cause.getMessage() != null ? " - " + cause.getMessage() : "") + " (see log)");
    }
}