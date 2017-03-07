package application;


import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;

public class ExplosionTest {
    public static ParticleEmitter createExplosion() {
        ParticleEmitter res = new ParticleEmitter("name", ParticleMesh.Type.Triangle, 10);
        return res;
    }
}
