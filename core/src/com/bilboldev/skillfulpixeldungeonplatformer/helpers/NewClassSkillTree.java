package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skill;
import com.bilboldev.skillfulpixeldungeonplatformer.units.skills.Skills;
import java.util.ArrayList;


public final class NewClassSkillTree {
    public static final class Node {
        public final int id, column, row, cost, requirement, opposite;
        public final HeroClass heroClass;
        public final String name;
        public final boolean dark;

        private Node(HeroClass heroClass, int id, String name, int column, int row, int cost, int requirement, int opposite) {
            this(heroClass, id, name, column, row, cost, requirement, opposite, false);
        }

        private Node(HeroClass heroClass, int id, String name, int column, int row, int cost, int requirement, int opposite, boolean dark) {
            this.heroClass = heroClass; this.id = id; this.name = name;
            this.column = column; this.row = row; this.cost = cost;
            this.requirement = requirement; this.opposite = opposite;
            this.dark = dark;
        }

        public boolean isMastery() { return opposite != 0; }
        public ArrayList<Integer> requirements() {
            ArrayList<Integer> result = new ArrayList<Integer>();
            if (requirement != 0) result.add(requirement);
            return result;
        }
    }

    private static final Node[] NODES = {
        new Node(HeroClass.NECROMANCER, Skills.MIND_SHOT, "Mind Shot", 1,1,1,0,0),
        new Node(HeroClass.NECROMANCER, Skills.MANA, "Spirituality", 1,2,1,0,0),
        new Node(HeroClass.NECROMANCER, Skills.MANA_REGENERATION, "Meditation", 2,1,2,Skills.MANA,0),
        new Node(HeroClass.NECROMANCER, Skills.RAISE_SKELETON, "Raise Skeleton", 1,2,1,0,0,true),
        new Node(HeroClass.NECROMANCER, Skills.CURSE, "Curse", 1,1,1,0,0,true),
        new Node(HeroClass.NECROMANCER, Skills.DRAIN_LIFE, "Drain Life", 2,1,2,Skills.CURSE,0,true),
        new Node(HeroClass.NECROMANCER, Skills.SPIRIT_BINDER, "Spirit Binder", 3,1,-1,Skills.MANA_REGENERATION,Skills.LICH),
        new Node(HeroClass.NECROMANCER, Skills.LICH, "Lich", 3,1,-1,Skills.DRAIN_LIFE,Skills.SPIRIT_BINDER,true),
        new Node(HeroClass.NECROMANCER, Skills.SUMMON_GHOST, "Summon Ghost", 4,1,4,Skills.SPIRIT_BINDER,0),
        new Node(HeroClass.NECROMANCER, Skills.MASTER_OF_DEATH, "Master of Death", 4,2,4,Skills.SPIRIT_BINDER,0),
        new Node(HeroClass.NECROMANCER, Skills.RAISE_SKELETON_ARCHER, "Raise Skeleton Archer", 4,1,4,Skills.LICH,0,true),
        new Node(HeroClass.NECROMANCER, Skills.CORPSE_EXPLOSION, "Corpse Explosion", 4,2,4,Skills.LICH,0,true),
        new Node(HeroClass.MERCENARY, Skills.WANTED, "Wanted", 1,1,1,0,0),
        new Node(HeroClass.MERCENARY, Skills.PACKRAT, "Packrat", 1,2,1,0,0),
        new Node(HeroClass.MERCENARY, Skills.STEADY_AIM, "Steady Aim", 2,1,2,Skills.WANTED,0),
        new Node(HeroClass.MERCENARY, Skills.QUICK_DRAW, "Quick Draw", 1,1,1,0,0,true),
        new Node(HeroClass.MERCENARY, Skills.HEAD_SHOT, "Head Shot", 2,1,2,Skills.QUICK_DRAW,0,true),
        new Node(HeroClass.MERCENARY, Skills.MARSHAL, "Marshal", 3,1,-1,Skills.STEADY_AIM,Skills.EXECUTIONER),
        new Node(HeroClass.MERCENARY, Skills.EXECUTIONER, "Executioner", 3,1,-1,Skills.HEAD_SHOT,Skills.MARSHAL,true),
        new Node(HeroClass.MERCENARY, Skills.I_AM_THE_LAW, "I Am the Law", 4,1,4,Skills.MARSHAL,0),
        new Node(HeroClass.MERCENARY, Skills.NO_WITNESSES, "No Witnesses", 4,1,4,Skills.EXECUTIONER,0,true),
        new Node(HeroClass.MERCENARY, Skills.EXECUTE, "Execute", 4,2,4,Skills.NO_WITNESSES,0,true)
    };

    private NewClassSkillTree() { }
    public static boolean isNewClass(HeroClass type) {
        return type == HeroClass.NECROMANCER || type == HeroClass.MERCENARY;
    }
    public static boolean isReserved(int id) { return id >= Skills.MIND_SHOT && id <= Skills.EXECUTE; }
    public static Node node(HeroClass type, int id) {
        for (Node node : NODES) if (node.heroClass == type && node.id == id) return node;
        return null;
    }
    public static Node uniqueNode(int id) {
        if (!isReserved(id)) return null;
        for (Node node : NODES) if (node.id == id) return node;
        return null;
    }
    public static ArrayList<Integer> ids(HeroClass type) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (Node node : NODES) if (node.heroClass == type) result.add(node.id);
        return result;
    }
    public static ArrayList<Integer> ids(HeroClass type, boolean dark) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (Node node : NODES) if (node.heroClass == type && node.dark == dark) result.add(node.id);
        return result;
    }
    public static boolean allows(HeroClass type, Skill skill) {
        if (skill == null || type == null) return false;
        Node unique = uniqueNode(skill.getId());
        if (unique != null) return unique.heroClass == type && skill.getSkillClass() == type;
        if (isNewClass(type)) return node(type, skill.getId()) != null || skill.getSkillClass() == HeroClass.NEUTRAL;
        return true;
    }
    public static int opposite(int id) {
        Node node = uniqueNode(id);
        return node == null ? 0 : node.opposite;
    }
    public static int[] masteries(HeroClass type) {
        if (type == HeroClass.NECROMANCER) return new int[]{Skills.SPIRIT_BINDER, Skills.LICH};
        if (type == HeroClass.MERCENARY) return new int[]{Skills.MARSHAL, Skills.EXECUTIONER};
        return null;
    }


    public static void configure(Skill skill) {
        Node node = uniqueNode(skill.getId());
        if (node == null || node.heroClass != skill.getSkillClass())
            throw new IllegalArgumentException("Skill does not match the new-class tree: " + skill.getId());
        skill.getRequires().clear();
        if (node.requirement != 0) skill.setRequires(node.requirement);
        skill.setLocksOut(node.opposite);
        skill.setTreeOffsets(100 + 450 * (node.column - 1), -480 - 125 * (node.row - 1));
    }
}
