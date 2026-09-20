package com.bilboldev.skillfulpixeldungeonplatformer.misc.sounds;

public enum Sounds {
    INTRO("sounds/intro.mp3", true), DUNGEON("sounds/dungeon.mp3", true), HAPPY("surface.mp3", true),

    STEP("sounds/step.mp3", false), WATER("sounds/snd_water.mp3", false), MISS("sounds/miss.mp3", false), HIT("sounds/hit.mp3", false), OPEN_DOOR("sounds/door-open.mp3", false)
    , LEVEL_UP("sounds/level-up.mp3", false), MASTERY("sounds/snd_mastery.mp3", false), TRAP("sounds/trap.mp3", false), GOLD("sounds/gold.mp3", false), ITEM("sounds/item.mp3", false),
    DRINK("sounds/drink.mp3", false), EAT("sounds/eat.mp3", false), READ("sounds/read.mp3", false), ALERT("sounds/alert.mp3", false), CHALLENGE("sounds/snd_challenge.mp3", false), SHATTER("sounds/snd_shatter.mp3", false), PLANT("sounds/snd_plant.mp3", false)
    , ZAP("sounds/zap.mp3", false), LIGHTNING("sounds/snd_lightning.mp3", false), RAY("sounds/snd_ray.mp3", false), BLAST("sounds/blast.mp3", false), EXPLOSION("sounds/snd_blast.mp3", false), DEGRADE("sounds/degrade.mp3", false), CURSE("sounds/snd_cursed.mp3", false),
    BONES("sounds/snd_bones.mp3", false), TOMB("sounds/snd_tomb.mp3", false),
    CLICK("sounds/snd_click.mp3", false), BOSS("sounds/snd_boss.mp3", false), BADGE("sounds/snd_badge.mp3", false), DEATH("sounds/snd_death.mp3", false), DESCEND("sounds/snd_descend.mp3", false);

    private String soundPath;
    private boolean music;

    Sounds(String soundPath, boolean music){
        this.soundPath = soundPath;
        this.music = music;
    }

    public String getPath(){
        return this.soundPath;
    }

    public boolean isMusic() {
        return music;
    }
}

