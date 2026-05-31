package com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MapHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.MercenaryHelper.MercenaryType;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.MercenaryRecruit;

public class MercenaryDoor extends SpecialRoomDoor {
    private MercenaryType signType;
    private MercenaryType rememberedType;
    private String signTemplate;

    {
        sign = createMercenarySign(null, MapHelper.getInstance().getTheme().getTemplate());
    }

    @Override
    public void act(float delta) {
        refreshSign();
    }

    @Override
    public void draw(Batch batch, float alpha) {
        refreshSign();
        super.draw(batch, alpha);
    }

    @Override
    public void showMessage() {
        showSpecialRoomMessage("The mercenary den lies behind a locked door.\nMust find a key...", null);
    }

    private void refreshSign() {
        MercenaryType resolvedType = resolveMercenaryType();
        String resolvedTemplate = MapHelper.getInstance().getTheme().getTemplate();
        if (resolvedType == signType && resolvedTemplate.equals(signTemplate)) {
            return;
        }

        signType = resolvedType;
        signTemplate = resolvedTemplate;
        sign = createMercenarySign(resolvedType, resolvedTemplate);
    }

    private MercenaryType resolveMercenaryType() {
        if (leadsTo == null) {
            return rememberedType;
        }

        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!(unit instanceof MercenaryRecruit) || unit.isDead() || unit.getRoom() == null || !unit.getRoom().equals(leadsTo)) {
                continue;
            }

            rememberedType = ((MercenaryRecruit) unit).getMercenaryType();
            return rememberedType;
        }

        return rememberedType;
    }

    private GameSprite createMercenarySign(MercenaryType type, String template) {
        if (type == null) {
            return new GameSprite(
                    "images/tiles/" + template + "/door-sign-mercenary-warrior.png",
                    ConstantsHelper.TILE / 2,
                    ConstantsHelper.TILE / 2);
        }

        return new GameSprite(
                "images/tiles/" + template + "/door-sign-mercenary-" + getMercenarySignSuffix(type) + ".png",
                ConstantsHelper.TILE / 2,
                ConstantsHelper.TILE / 2);
    }

    private String getMercenarySignSuffix(MercenaryType type) {
        switch (type) {
            case BRUTE:
                return "warrior";
            case ROGUE:
                return "rogue";
            case WIZARD:
                return "wizard";
            case HUNTRESS:
                return "huntress";
            default:
                return "warrior";
        }
    }
}