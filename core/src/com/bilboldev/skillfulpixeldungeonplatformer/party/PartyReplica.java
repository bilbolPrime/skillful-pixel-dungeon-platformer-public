package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper.*;
import com.bilboldev.skillfulpixeldungeonplatformer.items.Item;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.Weapon;
import com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.ranged.Gun;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.NecromancerMinion;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.RaisedSkeletonArcher;
import com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.summons.SummonedGhost;
import java.io.*;
import java.util.*;

public final class PartyReplica {
    private static final int MAX_ENTITIES = 4096, MAX_RECORDS = 20000;
    private final Map<String, String> records = new TreeMap<>();
    private final Map<String, Unit> units = new HashMap<>();
    private final Map<String, String> actorTypes = new HashMap<>();
    private final Map<String, Motion> motion = new HashMap<>();
    private final Map<String, Shot> shots = new HashMap<>();
    private final Map<String, Shot> launches = new LinkedHashMap<>();
    private final Map<String, Contact> contacts = new LinkedHashMap<>();
    private int contactSequence;
    private int revision, received;
    private boolean applied;

    public static final class World {
        public int depth, generated;
        public String room;
        public long random0, random1;
        public float time;
        public ItemIdentityHelper.ItemIdentitySaveData identity;
        public QuestManager.QuestSaveData quests;
        public HashMap<Integer, Integer> keys;
    }
    public static final class Dynamics {
        public long[] values;
        public String[] references;
        public Brain[] brains;
        public int[] combatLinks;
        public boolean visible = true;
        public String displaySprite;
        public float displayWidth, displayHeight;
        public float[] impact, attackRange;
        public ArrayList<BuffState> buffs = new ArrayList<>();
    }
    public static final class Brain {
        public int type = -1, alias = -1;
        public long[] values;
        public String[] references;
    }
    public static final class BuffState {
        public int type, source;
        public long[] values;
        public String[] references;
    }
    public static final class GunDamage {
        public String group;
        public float[] values;
        public String[] victims;
    }
    public static final class Shot {
        public int type;
        public String owner, room;
        public float x, y, vx, vy, gravity, damping, damage, time;
        public ItemSaveData weapon, payload;
        public long[] values;
        public String[] references;
        public String[] hitTargets;
        public boolean ended;
    }
    public static final class Contact {
        public String shot, target;
        public float x, y, damage, time;
    }
    void launched(ThrownProjectile projectile) { launches.put(projectile.getPersistentId(), shot(projectile)); }
    void retired(ThrownProjectile projectile) {
        Shot launch = launches.get(projectile.getPersistentId());
        if (launch != null) launch.ended = true;
    }
    void contact(ThrownProjectile projectile, Unit target, float damage) {
        Contact contact = new Contact(); contact.shot = projectile.getPersistentId(); contact.target = target == null ? null : target.getPersistentId();
        contact.x = projectile.x; contact.y = projectile.y; contact.damage = Math.max(0, damage); contact.time = PartySession.clock();
        contacts.put("contact:" + (++contactSequence), contact);
        if (contacts.size() > 512) contacts.remove(contacts.keySet().iterator().next());
    }
    private Shot shot(ThrownProjectile projectile) {
        SaveHelper save = SaveHelper.getInstance();
        Shot shot = new Shot();
        shot.type = PartyObjects.shotId(projectile); shot.owner = projectile.getOwner().getPersistentId();
        shot.room = projectile.getRoom(); shot.x = projectile.x; shot.y = projectile.y;
        shot.vx = projectile.speedX; shot.vy = projectile.speedY;
        shot.gravity = projectile.getPhysicsGravityScale(); shot.damping = projectile.getPhysicsLinearDamping();
        shot.damage = projectile.partyDamage(); shot.time = PartySession.clock();
        Hero owner = projectile.getOwner() instanceof Hero ? (Hero)projectile.getOwner() : PartySession.player(0);
        shot.weapon = save.snapshotItem(projectile.partyWeapon(), owner); shot.payload = save.snapshotItem(projectile.partyPayload(), owner);
        shot.values = PartyDynamics.capture(projectile); shot.references = PartyDynamics.references(projectile);
        return shot;
    }
    private static final class Motion {
        float x, y, vx, vy, floor;
        Motion(float x, float y, float vx, float vy, float floor) { this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.floor = floor; }
    }

    public boolean hasState() { return applied; }

    public void write(DataOutputStream output) throws IOException {
        Map<String, String> next = capture();
        output.writeInt(++revision);
        List<String> changed = new ArrayList<>(), removed = new ArrayList<>();
        for (String key : next.keySet()) if (!Objects.equals(next.get(key), records.get(key))) changed.add(key);
        for (String key : records.keySet()) if (!next.containsKey(key)) removed.add(key);
        output.writeInt(changed.size());
        for (String key : changed) { output.writeUTF(key); output.writeUTF(next.get(key)); }
        output.writeInt(removed.size());
        for (String key : removed) output.writeUTF(key);
        ArrayList<Unit> moving = new ArrayList<>();
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) if (!(unit instanceof ThrownProjectile)
                && (unit.isHero || next.containsKey("unit:" + unit.getPersistentId()))
                && Objects.equals(unit.getRoom(), MapHelper.getInstance().getActiveRoomIdentifier())) moving.add(unit);
        output.writeInt(moving.size());
        for (Unit unit : moving) {
            output.writeUTF(unit.getPersistentId()); output.writeFloat(unit.x); output.writeFloat(unit.y);
            output.writeFloat(PhysicsHelper.toPixelSpeed(PhysicsHelper.getInstance().getHorizontalSpeed(unit)));
            output.writeFloat(unit.speedY); output.writeFloat(unit.floorY);
        }
        records.clear(); records.putAll(next);
    }

    private Map<String, String> capture() {
        Map<String, String> next = new TreeMap<>();
        Iterator<Contact> expired = contacts.values().iterator();
        while (expired.hasNext()) if (PartySession.clock() - expired.next().time > 2f) expired.remove();
        for (Map.Entry<String, Contact> contact : contacts.entrySet()) put(next, contact.getKey(), contact.getValue());
        for (Map.Entry<String, Shot> launch : launches.entrySet()) put(next, "shot:" + launch.getKey(), launch.getValue());
        launches.clear();
        SaveHelper save = SaveHelper.getInstance();
        World world = new World();
        world.depth = MapHelper.getInstance().getDepth(); world.generated = MapHelper.getInstance().getGeneratedDepthCount();
        world.room = MapHelper.getInstance().getActiveRoomIdentifier(); world.time = PartySession.clock();
        long[] random = RandomHelper.getInstance().partyRandomState(); world.random0 = random[0]; world.random1 = random[1];
        world.identity = ItemIdentityHelper.getInstance().snapshot(); world.quests = QuestManager.getInstance().snapshot();
        world.keys = MapHelper.getInstance().getKeyCountsByDepthSnapshot();
        put(next, "world", world);
        for (int i = 0; i < 2; i++) {
            final int player = i;
            PartySession.scoped(i, () -> {
                Hero hero = PartySession.player(player);
                HeroSaveData data = save.snapshotHero(hero);
                data.x = data.y = data.floorY = 0;
                put(next, "hero" + player, data); put(next, "bag" + player, save.snapshotInventory(hero));
                for (UnitSaveData entity : save.snapshotUnits()) {
                    if (Objects.equals(entity.roomId, MapHelper.getInstance().getActiveRoomIdentifier())) entity.x = entity.y = entity.floorY = 0;
                    entity.hp = 1; entity.facingRight = true;
                    put(next, "unit:" + entity.id, entity);
                }
                for (RoomSaveData room : save.snapshotRooms(hero)) {
                    String key = "room:" + room.roomId;
                    if (!next.containsKey(key) || room.corpses != null) put(next, key, room);
                }
            });
        }
        for (DoorSaveData door : save.snapshotDoors()) put(next, "door:" + door.roomId + ":" + (int)door.x + ":" + (int)door.y, door);
        IdentityHashMap<Object, String> gunGroups = new IdentityHashMap<>();
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            String id = unit.getPersistentId();
            if (unit instanceof ThrownProjectile) {
                ThrownProjectile projectile = (ThrownProjectile)unit;
                if (projectile instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile) {
                    com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile gun = (com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile)projectile;
                    Object group = gun.partyDamageGroup();
                    if (group == null) group = gun;
                    if (!gunGroups.containsKey(group)) gunGroups.put(group, id);
                    GunDamage data = new GunDamage(); data.group = gunGroups.get(group); data.values = gun.partyDamageState(); data.victims = gun.partyDamageVictims();
                    put(next, "gun:" + id, data);
                }
                if (projectile.isUsed()) {
                    Dynamics impact = new Dynamics(); impact.impact = new float[]{projectile.x, projectile.y, projectile.speedX, projectile.speedY}; impact.values = PartyDynamics.capture(projectile); impact.references = PartyDynamics.references(projectile);
                    put(next, "spent:" + id, impact);
                }
                String key = "shot:" + id;

                if (records.containsKey(key)) next.put(key, records.get(key));
                else if (!next.containsKey(key)) put(next, key, shot(projectile));
                put(next, "hits:" + id, projectile.partyHitTargets());
                continue;
            }
            if (!unit.isHero && !next.containsKey("unit:" + id)) {
                String prior = records.get("unit:" + id);
                if (prior != null) next.put("unit:" + id, prior);
                else continue;
            }
            Dynamics state = new Dynamics(); state.visible = unit.isVisible(); state.values = PartyDynamics.capture(unit); state.references = PartyDynamics.references(unit);
            if (unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.SpriteRewardOnScreen) {
                com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite sprite = ((com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.SpriteRewardOnScreen)unit).partyDisplaySprite();
                state.displaySprite = sprite.spriteString; state.displayWidth = sprite.getWidth(); state.displayHeight = sprite.getHeight();
            }
            if (!unit.isHero && unit.getWeapon() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack) state.attackRange = new float[]{unit.getWeapon().min(), unit.getWeapon().max()};
            if (unit instanceof Hero) state.combatLinks = ((Hero)unit).partyCombatLinks();
            if (unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob) {
                com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI[] brains = ((com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob)unit).partyBrains();
                state.brains = new Brain[brains.length];
                for (int i = 0; i < brains.length; i++) {
                    Brain row = state.brains[i] = new Brain();
                    if (brains[i] == null) continue;
                    for (int prior = 0; prior < i; prior++) if (brains[prior] == brains[i]) row.alias = prior;
                    row.type = PartyObjects.brainId(brains[i]); row.values = PartyDynamics.capture(brains[i]); row.references = PartyDynamics.references(brains[i]);
                }
            }
            for (Buff buff : unit.getBuffs()) {
                BuffState row = new BuffState(); row.type = PartyObjects.buffId(buff); row.values = PartyDynamics.capture(buff);
                row.references = PartyDynamics.references(buff); row.source = PartySession.index(UnitHelper.getInstance().getHero());
                state.buffs.add(row);
            }
            put(next, "state:" + id, state);
        }
        return next;
    }
    private static void put(Map<String, String> records, String key, Object value) { records.put(key, PartyCodec.encode(value)); }

    public void read(DataInputStream input) throws IOException {
        int nextRevision = input.readInt();
        if (nextRevision <= received) throw new IOException("state_revision");
        Map<String, String> next = new TreeMap<>(records);
        Set<String> changed = new HashSet<>(), removed = new HashSet<>();
        int count = bounded(input.readInt(), MAX_RECORDS);
        for (int i = 0; i < count; i++) {
            String key = input.readUTF(), value = input.readUTF();
            if (key.length() > 220 || !changed.add(key)) throw new IOException("state_key");
            next.put(key, value);
        }
        count = bounded(input.readInt(), MAX_RECORDS);
        for (int i = 0; i < count; i++) { String key = input.readUTF(); if (!removed.add(key) || changed.contains(key)) throw new IOException("state_remove"); next.remove(key); }
        if (next.size() > MAX_RECORDS) throw new IOException("state_count");
        Map<String, Motion> positions = new HashMap<>();
        count = bounded(input.readInt(), MAX_ENTITIES);
        for (int i = 0; i < count; i++) {
            String id = input.readUTF();
            Motion position = new Motion(finite(input.readFloat()), finite(input.readFloat()), finite(input.readFloat()), finite(input.readFloat()), finite(input.readFloat()));
            if (id.length() > 160 || positions.put(id, position) != null) throw new IOException("motion_id");
        }
        Hero local = PartySession.player(PartySession.localIndex());
        boolean left = local.movingLeft, right = local.movingRight;
        apply(next, changed, removed, positions);

        if (!local.isDead()) { local.movingLeft = left; local.movingRight = right; }
        records.clear(); records.putAll(next); motion.putAll(positions); received = nextRevision; applied = true;
    }

    private void apply(Map<String, String> next, Set<String> changed, Set<String> removed, Map<String, Motion> positions) throws IOException {
        SaveHelper save = SaveHelper.getInstance();
        World world = PartyCodec.decode(World.class, next.get("world"));
        if (world.depth < 1 || world.depth > 30 || world.generated < world.depth || world.generated > 30
                || world.room == null || world.room.length() > 160) throw new IOException("world");
        if (world.depth != MapHelper.getInstance().getDepth() || !world.room.equals(MapHelper.getInstance().getActiveRoomIdentifier())) {
            PartySession.scoped(0, () -> {
                MapHelper.getInstance().setRestoringGeneratedLevels(true);
                try {
                    for (int depth = MapHelper.getInstance().getGeneratedDepthCount() + 1; depth <= world.generated; depth++) MapHelper.getInstance().enterDepth(depth);
                    MapHelper.getInstance().enterDepth(world.depth); MapHelper.getInstance().goToRoom(world.room);
                } finally { MapHelper.getInstance().setRestoringGeneratedLevels(false); }
            });
        }
        ItemIdentityHelper.getInstance().restore(world.identity, RandomHelper.getInstance().getRunSeed());
        QuestManager.getInstance().restore(world.quests); MapHelper.getInstance().restoreKeyCountsByDepth(world.keys);
        units.clear();
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            String id = unit.getPersistentId(); units.put(id, unit);
            if (actorTypes.containsKey(id) && !Objects.equals(actorTypes.get(id), unitType(unit)) && next.containsKey("unit:" + id)) changed.add("unit:" + id);
        }
        for (String id : actorTypes.keySet()) if (!units.containsKey(id) && next.containsKey("unit:" + id)) changed.add("unit:" + id);
        for (String id : shots.keySet()) if (!units.containsKey(id) && next.containsKey("shot:" + id)) changed.add("shot:" + id);
        for (int i = 0; i < 2; i++) {
            final int player = i;
            HeroSaveData hero = changed.contains("hero" + i) ? PartyCodec.decode(HeroSaveData.class, next.get("hero" + i)) : null;
            InventorySaveData bag = changed.contains("bag" + i) ? PartyCodec.decode(InventorySaveData.class, next.get("bag" + i)) : null;
            if (hero != null && (hero.level < 1 || hero.level > 1000 || hero.unlockedSkillIds.size() > 128)) throw new IOException("hero");
            if (bag != null && bag.items.size() > 64) throw new IOException("inventory");
            PartySession.scoped(i, () -> {
                Hero target = PartySession.player(player);
                float x = target.x, y = target.y;
                if (hero != null) save.restoreHero(target, hero, world.room);
                if (bag != null) save.restoreInventory(bag, target);
                target.x = x; target.y = y;
                units.put(target.getPersistentId(), target);
            });
        }
        ArrayList<RoomSaveData> rooms = new ArrayList<>(); ArrayList<DoorSaveData> doors = new ArrayList<>();
        List<String> entityKeys = new ArrayList<>();
        for (String key : changed) {
            if (key.startsWith("room:")) rooms.add(PartyCodec.decode(RoomSaveData.class, next.get(key)));
            else if (key.startsWith("door:")) doors.add(PartyCodec.decode(DoorSaveData.class, next.get(key)));
            else if (key.startsWith("unit:")) entityKeys.add(key);
        }
        for (RoomSaveData row : rooms) {
            if (!save.matchesGeneratedRoom(row)) throw new IOException("room_layout");
        }
        save.restoreRoomStates(rooms); save.restoreDoorStates(doors);

        for (String key : entityKeys) {
            UnitSaveData row = PartyCodec.decode(UnitSaveData.class, next.get(key));
            if (!Objects.equals(key, "unit:" + row.id) || row.id.length() > 160 || !(row.minion == null ? save.isValidSavedUnit(row) : SaveRegistry.isOwnedMinionId(row.className))) throw new IOException("unit");
            Unit unit = units.get(row.id);

            if (unit != null && !Objects.equals(row.className, unitType(unit))) { remove(row.id); unit = null; }
            if (unit == null) {
                unit = createUnit(row, next);
                if (unit == null) throw new IOException("unit_type");
                motion.remove(row.id);
                unit.setPersistentId(row.id); unit.setRoom(row.roomId); UnitHelper.getInstance().addUnit(unit); units.put(row.id, unit);
            }
            actorTypes.put(row.id, row.className);
            float x = unit.x, y = unit.y;
            save.applyUnitState(unit, row);
            if (unit instanceof NecromancerMinion && row.minion != null) {
                NecromancerMinion minion = (NecromancerMinion)unit;
                ((com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack)minion.getWeapon()).setDamageRange(row.minion.minDamage, row.minion.maxDamage);
                Hero owner = PartySession.owner(row.minion.ownerId);
                minion.restoreSpiritBinder(owner, row.minion.spiritBinderApplied); minion.restoreLich(owner, row.minion.lichApplied);
            }
            if (row.item != null && unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen)
                ((com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen)unit).partyItem(save.createItem(row.item));
            if (positions.containsKey(row.id)) { unit.x = x; unit.y = y; }
        }
        for (String key : removed) if (key.startsWith("unit:") || key.startsWith("shot:")) remove(key.substring(5));
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            if ((SaveRegistry.getUnitId(unit) != null || unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MerchantRoom.ItemForPurchase) && !unit.isHero && !next.containsKey("unit:" + unit.getPersistentId()) && !next.containsKey("shot:" + unit.getPersistentId())) remove(unit.getPersistentId());
        }
        for (String key : entityKeys) if (next.containsKey("state:" + key.substring(5))) changed.add("state:" + key.substring(5));
        for (int i = 0; i < 2; i++) if (changed.contains("hero" + i) || changed.contains("bag" + i)) changed.add("state:player-" + i);
        for (String key : changed) if (key.startsWith("state:")) {
            Unit unit = units.get(key.substring(6));
            if (unit == null) throw new IOException("missing_unit");
            Dynamics dynamics = PartyCodec.decode(Dynamics.class, next.get(key));
            if (dynamics.buffs.size() > 64) throw new IOException("buff_count");
            List<Buff> buffs = new ArrayList<>();
            for (int i = 0; i < dynamics.buffs.size(); i++) {
                BuffState state = dynamics.buffs.get(i);
                Buff buff = i < unit.getBuffs().size() && PartyObjects.buffId(unit.getBuffs().get(i)) == state.type
                        ? unit.getBuffs().get(i) : PartyObjects.buff(state.type, PartySession.player(state.source == 1 ? 1 : 0));
                PartyDynamics.apply(buff, state.values); PartyDynamics.references(buff, state.references, units); buffs.add(buff);
            }
            unit.getBuffs().clear(); unit.getBuffs().addAll(buffs);
            PartyDynamics.apply(unit, dynamics.values); PartyDynamics.references(unit, dynamics.references, units);
            if (unit instanceof Hero) ((Hero)unit).partyCombatLinks(dynamics.combatLinks);
            unit.setVisible(dynamics.visible);
            if (dynamics.attackRange != null) {
                if (dynamics.attackRange.length != 2 || !(unit.getWeapon() instanceof com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack)) throw new IOException("attack_range");
                ((com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack)unit.getWeapon()).setDamageRange(finite(dynamics.attackRange[0]), finite(dynamics.attackRange[1]));
            }
            if (unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob) {
                if (dynamics.brains == null || dynamics.brains.length != 4) throw new IOException("brains");
                com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI[] brains = ((com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob)unit).partyBrains();
                for (int i = 0; i < brains.length; i++) {
                    Brain row = dynamics.brains[i];
                    if (row.type == -1) { brains[i] = null; continue; }
                    if (row.alias >= 0) { if (row.alias >= i) throw new IOException("brain_alias"); brains[i] = brains[row.alias]; continue; }
                    if (brains[i] == null || PartyObjects.brainId(brains[i]) != row.type) brains[i] = PartyObjects.brain(row.type, unit);
                    PartyDynamics.apply(brains[i], row.values); PartyDynamics.references(brains[i], row.references, units);
                }
                ((com.bilboldev.skillfulpixeldungeonplatformer.units.mobs.Mob)unit).partyBrains(brains);
            }
        }
        for (String key : changed) if (key.startsWith("shot:")) {
            String id = key.substring(5);
            Shot shot = PartyCodec.decode(Shot.class, next.get(key));
            Unit owner = units.get(shot.owner);
            Item weapon = shot.weapon == null ? null : save.createItem(shot.weapon);
            ThrownProjectile projectile = PartyObjects.shot(shot.type, owner, weapon instanceof Gun ? (Gun)weapon : null, shot.damage);
            projectile.setOwner(owner); if (weapon instanceof Weapon) projectile.setAttackingItem((Weapon)weapon);
            projectile.setPersistentId(id); projectile.setRoom(shot.room); projectile.x = shot.x; projectile.y = shot.y;
            Item payload = shot.payload == null ? null : save.createItem(shot.payload);
            projectile.partyPayload(payload);
            if (payload != null || projectile.getClass() == ThrownProjectile.class && weapon != null) {
                Item art = payload != null ? payload : weapon;
                projectile.setGameSprite(new com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameSprite(art.getGameSprite().spriteString, 45, 45));
            }
            PartyDynamics.apply(projectile, shot.values); PartyDynamics.references(projectile, shot.references, units);
            if (shot.ended) projectile.markUsed();
            units.put(id, projectile); shots.put(id, shot); UnitHelper.getInstance().addUnit(projectile);
        }
        for (String key : changed) if (key.startsWith("contact:")) {
            Contact contact = PartyCodec.decode(Contact.class, next.get(key));
            float contactX = finite(contact.x), contactY = finite(contact.y);

            if (finite(contact.damage) <= 0) {
                Unit marker = new Unit(); marker.x = contactX; marker.y = contactY;
                EffectsHelper.getInstance().spark(marker);
            }
        }
        for (String key : changed) if (key.startsWith("gun:")) {
            Unit unit = units.get(key.substring(4));
            GunDamage damage = PartyCodec.decode(GunDamage.class, next.get(key));
            if (!(unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile)) throw new IOException("gun_unit");
            ((com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile)unit).partyDamageState(damage.values, damage.victims, units);
        }
        for (String key : changed) if (key.startsWith("gun:")) {
            GunDamage damage = PartyCodec.decode(GunDamage.class, next.get(key));
            Unit sibling = units.get(damage.group), unit = units.get(key.substring(4));
            if (!(sibling instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile)) throw new IOException("gun_group");
            ((com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile)unit).partyDamageLink((com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.GunProjectile)sibling);
        }
        for (String key : changed) if (key.startsWith("spent:")) {
            Unit unit = units.get(key.substring(6)); Dynamics state = PartyCodec.decode(Dynamics.class, next.get(key));
            if (!(unit instanceof ThrownProjectile)) throw new IOException("spent_unit");
            PartyDynamics.apply(unit, state.values); PartyDynamics.references(unit, state.references, units);
            if (state.impact == null || state.impact.length != 4) throw new IOException("impact");
            unit.x = finite(state.impact[0]); unit.y = finite(state.impact[1]); unit.speedX = finite(state.impact[2]); unit.speedY = finite(state.impact[3]);
        }
        for (String key : changed) if (key.startsWith("hits:")) {
            Unit unit = units.get(key.substring(5));
            if (unit instanceof ThrownProjectile) ((ThrownProjectile)unit).partyHitTargets(PartyCodec.decode(String[].class, next.get(key)), units);
        }
        for (Map.Entry<String, Motion> entry : positions.entrySet()) {
            Unit unit = units.get(entry.getKey());
            if (unit == null) throw new IOException("motion_unit");
            Motion position = entry.getValue(); unit.floorY = position.floor;

            if (!motion.containsKey(entry.getKey()) || !validPosition(unit.x) || !validPosition(unit.y)
                    || Math.abs(unit.x - position.x) > ConstantsHelper.TILE * 3 || Math.abs(unit.y - position.y) > ConstantsHelper.TILE * 3) {
                unit.x = position.x; unit.y = position.y;
            }
        }
    }

    private Unit minion(UnitSaveData row) throws IOException {
        Hero owner = null;
        for (int i = 0; i < 2; i++) if (PartySession.player(i).getPersistentId().equals(row.minion.ownerId)) owner = PartySession.player(i);
        if (owner == null || row.minion.createdAtLevel < 1 || row.minion.createdAtLevel > 1000) throw new IOException("minion_owner");
        NecromancerMinion unit = SaveRegistry.createOwnedMinion(row.className, owner, row.minion.createdAtLevel);
        if (unit == null) throw new IOException("minion_type");
        unit.setMaxHP(row.minion.maxHp);
        ((com.bilboldev.skillfulpixeldungeonplatformer.items.weapons.melee.MeleeAttack)unit.getWeapon()).setDamageRange(row.minion.minDamage, row.minion.maxDamage);
        unit.restoreSpiritBinder(owner, row.minion.spiritBinderApplied); unit.restoreLich(owner, row.minion.lichApplied);
        if (unit instanceof SummonedGhost && row.minion.remainingLifetime != null) ((SummonedGhost)unit).restoreLifetime(row.minion.remainingLifetime);
        if (unit instanceof RaisedSkeletonArcher && row.minion.shotCooldown != null) ((RaisedSkeletonArcher)unit).restoreShotCooldown(row.minion.shotCooldown);
        return unit;
    }
    private void remove(String id) {
        Unit unit = units.remove(id); if (unit != null) UnitHelper.getInstance().removeUnit(unit);
        motion.remove(id); shots.remove(id); actorTypes.remove(id);
    }
    private static String unitType(Unit unit) {
        return unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.MerchantRoom.ItemForPurchase
                ? SaveRegistry.MERCHANT_ROOM_ITEM_FOR_PURCHASE_ID : SaveRegistry.getUnitId(unit);
    }
    private Unit createUnit(UnitSaveData row, Map<String, String> next) throws IOException {
        if (row.minion != null) return minion(row);
        SaveHelper save = SaveHelper.getInstance();
        if (Objects.equals(row.className, SaveRegistry.getUnitId(com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.SpriteRewardOnScreen.class))) {
            Dynamics state = PartyCodec.decode(Dynamics.class, next.get("state:" + row.id));
            if (state.displaySprite == null || !state.displaySprite.startsWith("images/") || state.displaySprite.contains("..")
                    || state.displaySprite.length() > 250 || finite(state.displayWidth) <= 0 || state.displayWidth > 4096
                    || finite(state.displayHeight) <= 0 || state.displayHeight > 4096) throw new IOException("reward_appearance");
            return new com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.SpriteRewardOnScreen(save.createItem(row.item), state.displaySprite, state.displayWidth, state.displayHeight);
        }
        if (row.item != null && !SaveRegistry.isMerchantRoomItemForPurchaseId(row.className)
                && !Objects.equals(row.className, SaveRegistry.getUnitId(com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen.class))) {
            Unit unit = SaveRegistry.createUnit(row.className);
            if (!(unit instanceof com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen)) throw new IOException("item_actor");
            ((com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen)unit).partyItem(save.createItem(row.item));
            return unit;
        }
        return save.createUnit(row);
    }
    public void animate(float delta) {
        float fraction = Math.min(1, delta * 20f);
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
            if (unit instanceof ThrownProjectile) {
                Shot shot = shots.get(unit.getPersistentId());
                if (shot != null) ((ThrownProjectile)unit).partyVisualStep(delta, shot.gravity, shot.damping);
            } else {
                Motion position = motion.get(unit.getPersistentId());
                if (position != null) { unit.x += (position.x - unit.x) * fraction; unit.y += (position.y - unit.y) * fraction; }
                unit.partyVisualStep(delta);
            }
        }
    }
    public void becomeClient() {
        for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) units.put(unit.getPersistentId(), unit);
        try {
            for (Map.Entry<String, String> entry : records.entrySet()) if (entry.getKey().startsWith("shot:"))
                shots.put(entry.getKey().substring(5), PartyCodec.decode(Shot.class, entry.getValue()));
        } catch (IOException invalid) { throw new IllegalStateException(invalid); }
    }
    public void takeAuthority() {
        for (Map.Entry<String, Motion> entry : motion.entrySet()) {
            Unit unit = units.get(entry.getKey()); if (unit == null) continue;
            Motion position = entry.getValue(); unit.x = position.x; unit.y = position.y; unit.speedY = position.vy;
            PhysicsHelper.getInstance().syncBodyToUnit(unit);
            PhysicsHelper.getInstance().partyVelocity(unit, position.vx, position.vy);
            unit.floorY = MapHelper.getInstance().calculateFloorY(unit.x, unit.y);
        }
        try {
            World world = PartyCodec.decode(World.class, records.get("world"));
            RandomHelper.getInstance().partyRandomState(world.random0, world.random1);
            PartySession.restoreClock(world.time);

            for (Map.Entry<String, Shot> entry : shots.entrySet()) {
                Unit unit = units.get(entry.getKey()); Shot launch = entry.getValue();
                if (unit instanceof ThrownProjectile && !((ThrownProjectile)unit).isUsed()) {
                    PartyDynamics.apply(unit, launch.values);
                    ((ThrownProjectile)unit).partySeek(launch.x, launch.y, launch.vx, launch.vy,
                            Math.max(0, world.time - launch.time), launch.gravity, launch.damping);
                }
            }
            for (Unit unit : UnitHelper.getInstance().getUnitsSnapshot()) {
                PhysicsHelper.getInstance().syncBodyToUnit(unit);
                if (unit instanceof ThrownProjectile) PhysicsHelper.getInstance().partyVelocity(unit, unit.speedX, unit.speedY);
            }
        } catch (IOException invalid) { throw new IllegalStateException(invalid); }
        revision = received;
    }
    private static int bounded(int count, int maximum) throws IOException { if (count < 0 || count > maximum) throw new IOException("count"); return count; }
    private static boolean validPosition(float value) { return !Float.isNaN(value) && !Float.isInfinite(value) && Math.abs(value) <= 10000000f; }
    private static float finite(float value) throws IOException { if (!validPosition(value)) throw new IOException("position"); return value; }
}
