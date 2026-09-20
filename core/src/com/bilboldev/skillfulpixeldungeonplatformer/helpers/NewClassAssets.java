package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;


public final class NewClassAssets {
    public static final String NECROMANCER = "images/units/necromancer/necromancer.png";
    public static final String MERCENARY = "images/units/mercenary/mercenary.png";
    public static final String NECROMANCER_PORTRAIT = "images/units/necromancer/portrait.png";
    public static final String MERCENARY_PORTRAIT = "images/units/mercenary/portrait.png";
    public static final String NECROMANCER_JUMP = "images/units/necromancer/button-jump.png";
    public static final String MERCENARY_JUMP = "images/units/mercenary/button-jump.png";
    public static final String ITEMS = "images/classes/sibling-items.png";
    public static final String SKILLS = "images/classes/sibling-skills.png";
    public static final String MIND_SHOT = SkillArt.MIND_SHOT.key();
    public static final String BULLET_ICON = "nc:bullet-icon";
    public static final String SPIRITUALITY = "images/skills/mana.png";
    public static final String MEDITATION = "images/skills/mana-regeneration.png";

    private static final String ITEM_PREFIX = "nc:item:";
    private static final String SKILL_PREFIX = "nc:skill:";

    public enum ItemArt {
        HANDGUN(200), PISTOL(201), BLUNDERBUSS(202), RIFLE(203), MORTAR(204), BULLET(208), CORPSE(224);
        public final int atlasIndex;
        ItemArt(int atlasIndex) { this.atlasIndex = atlasIndex; }
        public String key() { return ITEM_PREFIX + name(); }
    }

    public enum SkillArt {
        EXECUTIONER(160), MARSHAL(161), WANTED(162), PACKRAT(163), STEADY_AIM(164),
        QUICK_DRAW(165), HEAD_SHOT(166), I_AM_THE_LAW(167), EXECUTE(168), NO_WITNESSES(169),
        SPIRIT_BINDER(176), LICH(177), RAISE_SKELETON(178), DRAIN_LIFE(179), SUMMON_GHOST(180),
        MASTER_OF_DEATH(181), CURSE(182), RAISE_SKELETON_ARCHER(183), CORPSE_EXPLOSION(184), MIND_SHOT(185);
        public final int atlasIndex;
        SkillArt(int atlasIndex) { this.atlasIndex = atlasIndex; }
        public String key() { return SKILL_PREFIX + name(); }
    }

    private NewClassAssets() { }


    public static TextureRegion region(String key) {
        if (key == null) return null;
        if (BULLET_ICON.equals(key)) {

            TextureRegion bullet = atlasRegion(ITEMS, ItemArt.BULLET.atlasIndex);
            return new TextureRegion(bullet, 0, 3, 13, 13);
        }
        if (key.startsWith(ITEM_PREFIX))
            return atlasRegion(ITEMS, ItemArt.valueOf(key.substring(ITEM_PREFIX.length())).atlasIndex);
        if (key.startsWith(SKILL_PREFIX))
            return atlasRegion(SKILLS, SkillArt.valueOf(key.substring(SKILL_PREFIX.length())).atlasIndex);
        return null;
    }

    private static TextureRegion atlasRegion(String atlas, int index) {

        return new TextureRegion(AssetHelper.getInstance().getTexture(atlas),
                index % 8 * 16, index / 8 * 16, 16, 16);
    }

    public static GameFilm heroFilm(String sheet) {
        if (!NECROMANCER.equals(sheet) && !MERCENARY.equals(sheet))
            throw new IllegalArgumentException("Unknown new class hero sheet: " + sheet);
        GameFilm film = new GameFilm(sheet, 256, 128, 1f);
        film.clipSizeX = 12;
        film.clipSizeY = 15;

        film.yClipOffset = 1;
        return film;
    }
}
