package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.SeniorMonk;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.DM300;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Elemental;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Monk;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfKing;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarfWarlock;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.DwarvenUndead;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Golem;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.city.Succubus;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.AcidicScorpio;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.BurningFist;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.EvilEye;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.Larva;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.RottingFist;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.Scorpio;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.halls.YogDzewa;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.caves.Spinner;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Bat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Brute;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Shaman;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.ShieldedBrute;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.prison.Tengu;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.AlbinoRat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Bandit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Crab;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Gnoll;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Goo;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Piranha;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Rat;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Skeleton;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Swarm;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.sewers.Thief;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MobLibraryHelper {
    private static final String[] THEME_SEQUENCE = new String[]{"Sewers", "Prison", "Caves", "City", "Halls"};

    private static MobLibraryHelper instance;
    private final Map<String, ArrayList<Class<? extends Mob>>> themeMobClasses;

    private MobLibraryHelper() {
        themeMobClasses = new LinkedHashMap<>();

        addTheme("Sewers", Rat.class, AlbinoRat.class, Gnoll.class, Crab.class, Piranha.class, Swarm.class, Goo.class);
        addTheme("Prison", Skeleton.class, Thief.class, Bandit.class, Shaman.class, Bat.class, Brute.class, ShieldedBrute.class, Tengu.class);
        addTheme("Caves", Spinner.class, Elemental.class, Monk.class, SeniorMonk.class, DM300.class);
        addTheme("City", DwarfWarlock.class, Golem.class, Succubus.class, DwarvenUndead.class, DwarfKing.class);
        addTheme("Halls", EvilEye.class, Scorpio.class, AcidicScorpio.class, Larva.class, BurningFist.class, RottingFist.class, YogDzewa.class);
    }

    public static MobLibraryHelper getInstance() {
        if (instance == null) {
            instance = new MobLibraryHelper();
        }

        return instance;
    }

    @SafeVarargs
    private final void addTheme(String themeName, Class<? extends Mob>... mobClasses) {
        ArrayList<Class<? extends Mob>> mobs = new ArrayList<>();
        for (Class<? extends Mob> mobClass : mobClasses) {
            mobs.add(mobClass);
        }
        themeMobClasses.put(themeName, mobs);
    }

    public int getThemeCount() {
        return THEME_SEQUENCE.length;
    }

    public String getThemeName(int index) {
        if (index < 0) {
            return THEME_SEQUENCE[0];
        }
        if (index >= THEME_SEQUENCE.length) {
            return THEME_SEQUENCE[THEME_SEQUENCE.length - 1];
        }
        return THEME_SEQUENCE[index];
    }

    public ArrayList<Mob> getBestiary(int themeIndex) {
        ArrayList<Mob> mobs = new ArrayList<>();
        ArrayList<Class<? extends Mob>> mobClasses = themeMobClasses.get(getThemeName(themeIndex));
        if (mobClasses == null) {
            return mobs;
        }

        for (Class<? extends Mob> mobClass : mobClasses) {
            try {
                mobs.add(mobClass.getDeclaredConstructor().newInstance());
            } catch (Exception exception) {
                if (Gdx.app != null) {
                    Gdx.app.log("MobLibraryHelper", "Skipping library entry for " + mobClass.getSimpleName(), exception);
                }
            }
        }

        return mobs;
    }
}