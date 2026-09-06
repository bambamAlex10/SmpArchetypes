package com.mc3699.smparch.registry;

import com.mc3699.smparch.SMPArch;

import net.mc3699.provenance.ProvenanceRegistries;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SMPParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister
            .create(BuiltInRegistries.PARTICLE_TYPE, SMPArch.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> QUEENBEE = PARTICLES
            .register("queenbee_particle", () -> new SimpleParticleType(false));

    public static class queenbeeParticle extends TextureSheetParticle {
        private final SpriteSet sprites;

        protected queenbeeParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, double dx,
                double dy, double dz) {
            super(level, x, y, z, dx, dy, dz);
            this.sprites = sprites;
            this.lifetime = 80;

            this.pickSprite(sprites);

            this.quadSize *= 1.0f;
            this.gravity = .3f;
            this.hasPhysics = true;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public void tick() {
            super.tick();

            this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);

            int frame = (this.age / 5) % 4;
            int fakeAge = frame * (this.lifetime / 4);

            this.setSprite(this.sprites.get(fakeAge, this.lifetime));
            // this.setSpriteFromAge(sprites);
        }

        public static class Provider implements ParticleProvider<SimpleParticleType> {
            private final SpriteSet sprites;

            public Provider(SpriteSet sprites) {
                this.sprites = sprites;
            }

            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                    double dx, double dy, double dz) {
                return new queenbeeParticle(level, x, y, z, sprites, dx, dy, dz);
            }
        }
    }

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIREDASH = PARTICLES
            .register("firedash_particle", () -> new SimpleParticleType(false));

    public static class firedashParticle extends TextureSheetParticle {
        private final SpriteSet sprites;

        protected firedashParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, double dx,
                double dy, double dz) {
            super(level, x, y, z, dx, dy, dz);
            this.sprites = sprites;
            this.lifetime = 80;

            this.pickSprite(sprites);

            this.quadSize *= 1.0f;
            this.gravity = .3f;
            this.hasPhysics = true;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public void tick() {
            super.tick();

            this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);

            int frame = (this.age / 5) % 4;
            int fakeAge = frame * (this.lifetime / 4);

            this.setSprite(this.sprites.get(fakeAge, this.lifetime));
            // this.setSpriteFromAge(sprites);
        }

        public static class Provider implements ParticleProvider<SimpleParticleType> {
            private final SpriteSet sprites;

            public Provider(SpriteSet sprites) {
                this.sprites = sprites;
            }

            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                    double dx, double dy, double dz) {
                return new firedashParticle(level, x, y, z, sprites, dx, dy, dz);
            }
        }
    }

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VIBRATO = PARTICLES
            .register("vibrato_particle", () -> new SimpleParticleType(false));

    public static class VibratoParticle extends TextureSheetParticle {
        private final SpriteSet sprites;

        protected VibratoParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, double dx,
                double dy, double dz) {
            super(level, x, y, z, dx, dy, dz);
            this.sprites = sprites;
            this.lifetime = 80;

            this.pickSprite(sprites);

            this.quadSize *= 1.0f;
            this.gravity = -.1f;
            this.hasPhysics = true;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public void tick() {
            super.tick();

            this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);

            int frame = (this.age / 5) % 4;
            int fakeAge = frame * (this.lifetime / 4);

            this.setSprite(this.sprites.get(fakeAge, this.lifetime));
            // this.setSpriteFromAge(sprites);
        }

        public static class Provider implements ParticleProvider<SimpleParticleType> {
            private final SpriteSet sprites;

            public Provider(SpriteSet sprites) {
                this.sprites = sprites;
            }

            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                    double dx, double dy, double dz) {
                return new VibratoParticle(level, x, y, z, sprites, dx, dy, dz);
            }
        }
    }

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> NOTES = PARTICLES.register("note_particle",
            () -> new SimpleParticleType(false));

    public static class noteParticle extends TextureSheetParticle {
        private final SpriteSet sprites;

        protected noteParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, double dx, double dy,
                double dz) {
            super(level, x, y, z, dx, dy, dz);
            this.sprites = sprites;
            this.lifetime = 20;

            this.pickSprite(sprites);

            this.quadSize *= 1.5f;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public void tick() {
            super.tick();
            this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);
        }

        public static class Provider implements ParticleProvider<SimpleParticleType> {
            private final SpriteSet sprites;

            public Provider(SpriteSet sprites) {
                this.sprites = sprites;
            }

            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                    double dx, double dy, double dz) {
                return new noteParticle(level, x, y, z, sprites, dx, dy, dz);
            }
        }
    }

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MAJIK = PARTICLES
            .register("majik", () -> new SimpleParticleType(false));

public static class majikParticles extends TextureSheetParticle {
        private final SpriteSet sprites;

        protected majikParticles(ClientLevel level, double x, double y, double z, SpriteSet sprites, double dx, double dy,
                double dz) {
            super(level, x, y, z, dx, dy, dz);
            this.sprites = sprites;
            this.lifetime = 20;

            this.pickSprite(sprites);

            this.quadSize *= 1.5f;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public void tick() {
            super.tick();
            this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);
        }

        public static class Provider implements ParticleProvider<SimpleParticleType> {
            private final SpriteSet sprites;

            public Provider(SpriteSet sprites) {
                this.sprites = sprites;
            }

            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                    double dx, double dy, double dz) {
                return new noteParticle(level, x, y, z, sprites, dx, dy, dz);
            }
        }
    }

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }

}
