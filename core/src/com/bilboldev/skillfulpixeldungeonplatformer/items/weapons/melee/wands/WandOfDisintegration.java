package com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.wands;

import com.badlogic.gdx.math.Rectangle;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.EffectsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds.Sounds;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.misc.effects.BeamEffect;

import java.util.ArrayList;

public class WandOfDisintegration extends Wand {
    private static final float RANGE_TILES = 7f;
    private static final float BEAM_HEIGHT = 12f;

    {
        manaCost = 6;
        speed = 4f / 7f;
        name = "Wand of Disintegration";
        description = "A wand that fires a piercing beam which grows more destructive the more foes it cuts through.";
        gs = new GameSprite("images/wands/WAND_TEAK.png", 45, 45);
    }

    @Override
    protected Sounds getCastSound() {
        return Sounds.RAY;
    }

    @Override
    public void addProjectile(float variance) {
        Rectangle beamArea = getBeamArea(RANGE_TILES, BEAM_HEIGHT);
        ArrayList<Unit> targets = getHostilesInBeam(RANGE_TILES, BEAM_HEIGHT);
        EffectsHelper.getInstance().add(new BeamEffect().init(beamArea.x, beamArea.y, beamArea.width, beamArea.height));

        if (targets.isEmpty()) {
            return;
        }

        float damage = scalePower(4f + targets.size() * 2f + RandomHelper.getInstance().randomInt(3));
        for (Unit target : targets) {
            target.takeDamage(owner, this, damage);
        }
    }
}