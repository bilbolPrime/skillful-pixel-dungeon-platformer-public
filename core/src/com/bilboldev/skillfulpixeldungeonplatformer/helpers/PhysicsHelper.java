package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.physics.box2d.World;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.Room;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.projectiles.ThrownProjectile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public final class PhysicsHelper {
    public static final float PIXELS_PER_METER = 32f;

    private static final float TIME_STEP = 1f / 60f;
    private static final float LAST_MELEE_DRAW_TIME = 0.2f;
    private static final float AIR_CONTROL_LERP = 0.18f;
    private static final short CATEGORY_TERRAIN = 0x0001;
    private static final short CATEGORY_UNIT = 0x0002;
    private static final short CATEGORY_PROJECTILE = 0x0004;

    private static PhysicsHelper instance;

    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera debugCamera;
    private final ArrayList<Body> terrainBodies;
    private final HashMap<Unit, Body> unitBodies;
    private final HashMap<ThrownProjectile, Body> projectileBodies;
    private final ArrayList<Unit> pendingRegistrations;
    private final ArrayList<Body> pendingBodyDestructions;
    private final ArrayList<Unit> pendingBodySyncs;

    private boolean debugEnabled;
    private float accumulator;
    private Rectangle lastMeleeArea;
    private float lastMeleeAreaTimer;
    private String activeRoomIdentifier;

    public static PhysicsHelper getInstance() {
        if (instance == null) {
            instance = new PhysicsHelper();
        }

        return instance;
    }

    public static void reset() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    private PhysicsHelper() {
        world = new World(new Vector2(0f, -toWorldSpeed(ConstantsHelper.GRAVITY)), true);
        debugRenderer = new Box2DDebugRenderer(true, true, false, false, false, true);
        shapeRenderer = new ShapeRenderer();
        debugCamera = new OrthographicCamera();
        terrainBodies = new ArrayList<Body>();
        unitBodies = new HashMap<Unit, Body>();
        projectileBodies = new HashMap<ThrownProjectile, Body>();
        pendingRegistrations = new ArrayList<Unit>();
        pendingBodyDestructions = new ArrayList<Body>();
        pendingBodySyncs = new ArrayList<Unit>();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                handleProjectileContact(contact.getFixtureA(), contact.getFixtureB());
                handleProjectileContact(contact.getFixtureB(), contact.getFixtureA());
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                TerrainFixtureData terrain = getTerrainData(contact.getFixtureA());
                PhysicsBodyData body = getBodyData(contact.getFixtureB());
                if (terrain != null && body != null && body.bodyKind == BodyKind.UNIT) {
                    contact.setEnabled(shouldEnableTerrainContact(contact, true, contact.getFixtureB().getBody(), terrain, body));
                    return;
                }

                terrain = getTerrainData(contact.getFixtureB());
                body = getBodyData(contact.getFixtureA());
                if (terrain != null && body != null && body.bodyKind == BodyKind.UNIT) {
                    contact.setEnabled(shouldEnableTerrainContact(contact, false, contact.getFixtureA().getBody(), terrain, body));
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }

    public void dispose() {
        clearTerrain();
        destroyBodies(unitBodies.values().iterator());
        destroyBodies(projectileBodies.values().iterator());
        unitBodies.clear();
        projectileBodies.clear();
        world.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
    }

    public void toggleDebug() {
        debugEnabled = !debugEnabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public boolean usesPhysics(Unit unit) {
        return unit != null && !(unit instanceof ThrownProjectile) && unit.getRoom() != null && !unit.showOnly;
    }

    public boolean hasBody(Unit unit) {
        return unitBodies.containsKey(unit) || projectileBodies.containsKey(unit);
    }

    public void register(Unit unit) {
        if (unit == null) {
            return;
        }

        if (world.isLocked()) {
            queueRegistration(unit);
            return;
        }

        registerImmediate(unit);
    }

    private void registerImmediate(Unit unit) {
        if (unit == null) {
            return;
        }

        if (unit instanceof ThrownProjectile) {
            registerProjectileImmediate((ThrownProjectile) unit);
            return;
        }

        if (!usesPhysics(unit) || unitBodies.containsKey(unit)) {
            return;
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(toWorld(unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f), toWorld(unit.y + ConstantsHelper.UNIT_DIMENSIONS / 2f));
        Body body = world.createBody(bodyDef);
        body.setGravityScale(getGravityScale(unit));
        body.setLinearDamping(unit.isCanFly() ? 8f : 0f);

        PolygonShape shape = new PolygonShape();
        float halfExtent = toWorld(unit.getCollisionWidth() / 2f);
        shape.setAsBox(halfExtent, halfExtent);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = CATEGORY_UNIT;
        fixtureDef.filter.maskBits = CATEGORY_PROJECTILE;
        if (!unit.isCanFly()) {
            fixtureDef.filter.maskBits |= CATEGORY_TERRAIN;
        }

        Fixture fixture = body.createFixture(fixtureDef);
        PhysicsBodyData data = new PhysicsBodyData(BodyKind.UNIT, unit, halfExtent, unit.isCanFly());
        body.setUserData(data);
        fixture.setUserData(data);
        shape.dispose();

        unitBodies.put(unit, body);
        syncBodyToUnit(unit);
    }

    public void unregister(Unit unit) {
        if (unit == null) {
            return;
        }

        pendingRegistrations.remove(unit);

        Body body = unit instanceof ThrownProjectile ? projectileBodies.remove(unit) : unitBodies.remove(unit);
        if (body != null) {
            destroyBody(body);
        }
    }

    public void ensureRoom(Room room) {
        if (room == null) {
            return;
        }

        processPendingBodyChanges();

        if (room.getIdentifier().equals(activeRoomIdentifier)) {
            return;
        }

        activeRoomIdentifier = room.getIdentifier();
        rebuildTerrain(room);
        syncAllBodiesToUnits();
        updateBodyActivation();
    }

    public void step(float delta) {
        ensureRoom(MapHelper.getInstance().getActiveRoom());
        processPendingBodyChanges();
        updateBodyActivation();

        accumulator += Math.min(delta, 0.25f);
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }

        processPendingBodyChanges();

        syncAllUnitsFromBodies();

        if (lastMeleeAreaTimer > 0f) {
            lastMeleeAreaTimer = Math.max(0f, lastMeleeAreaTimer - delta);
        }
    }

    public void syncBodyToUnit(Unit unit) {
        if (unit == null) {
            return;
        }

        if (world.isLocked()) {
            queueBodySync(unit);
            return;
        }

        Body body = unit instanceof ThrownProjectile ? projectileBodies.get(unit) : unitBodies.get(unit);
        if (body == null) {
            return;
        }

        PhysicsBodyData data = (PhysicsBodyData) body.getUserData();
        if (unit instanceof ThrownProjectile) {
            body.setTransform(toWorld(unit.x) + data.halfExtentMeters, toWorld(unit.y) + data.halfExtentMeters, 0f);
            body.setLinearVelocity(toWorldSpeed(unit.speedX), toWorldSpeed(unit.speedY));
            return;
        }

        body.setTransform(toWorld(unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f), toWorld(unit.y) + data.halfExtentMeters, 0f);
    }

    public void syncUnitFromPhysics(Unit unit) {
        Body body = unitBodies.get(unit);
        if (body == null) {
            return;
        }

        PhysicsBodyData data = (PhysicsBodyData) body.getUserData();
        unit.x = toPixels(body.getPosition().x) - ConstantsHelper.UNIT_DIMENSIONS / 2f;
        unit.y = toPixels(body.getPosition().y - data.halfExtentMeters);
        unit.speedY = toPixelSpeed(body.getLinearVelocity().y);
    }

    public void syncProjectileFromPhysics(ThrownProjectile projectile) {
        Body body = projectileBodies.get(projectile);
        if (body == null) {
            return;
        }

        PhysicsBodyData data = (PhysicsBodyData) body.getUserData();
        projectile.x = toPixels(body.getPosition().x - data.halfExtentMeters);
        projectile.y = toPixels(body.getPosition().y - data.halfExtentMeters);
        projectile.speedX = toPixelSpeed(body.getLinearVelocity().x);
        projectile.speedY = toPixelSpeed(body.getLinearVelocity().y);
    }

    public boolean isGrounded(Unit unit) {
        Body body = unitBodies.get(unit);
        if (body == null || unit.isCanFly() || !body.isActive()) {
            return false;
        }

        PhysicsBodyData data = (PhysicsBodyData) body.getUserData();
        final boolean[] grounded = new boolean[]{false};
        final float startX = body.getPosition().x;
        final float startY = body.getPosition().y - data.halfExtentMeters + 0.02f;
        world.rayCast(new com.badlogic.gdx.physics.box2d.RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                TerrainFixtureData terrainData = getTerrainData(fixture);
                if (terrainData == null) {
                    return -1f;
                }

                grounded[0] = true;
                return fraction;
            }
        }, startX, startY, startX, startY - toWorld(6f));

        return grounded[0] && Math.abs(body.getLinearVelocity().y) < toWorldSpeed(50f);
    }

    public float getFloorY(Unit unit) {
        if (isGrounded(unit)) {
            return unit.y;
        }

        return MapHelper.getInstance().calculateFloorY(unit.x, unit.y);
    }

    public void applyMovement(Unit unit, boolean grounded, boolean allowDirectionalInput) {
        Body body = unitBodies.get(unit);
        if (body == null) {
            return;
        }

        body.setGravityScale(getGravityScale(unit));

        float inputVelocityX = 0f;
        if (allowDirectionalInput) {
            inputVelocityX += unit.movingLeft ? -toWorldSpeed(unit.getSpeedX()) : 0f;
            inputVelocityX += unit.movingRight ? toWorldSpeed(unit.getSpeedX()) : 0f;
        }

        float desiredVelocityX;
        if (grounded || unit.isCanFly()) {
            desiredVelocityX = inputVelocityX + toWorldSpeed(unit.momentX);
        } else {
            if (allowDirectionalInput && inputVelocityX != 0f) {
                unit.airMomentumX = MathUtils.lerp(unit.airMomentumX, inputVelocityX, AIR_CONTROL_LERP);
                unit.airMomentumX = MathUtils.clamp(unit.airMomentumX, -toWorldSpeed(unit.getSpeedX()), toWorldSpeed(unit.getSpeedX()));
            }
            desiredVelocityX = unit.airMomentumX + toWorldSpeed(unit.momentX);
        }

        float desiredVelocityY = body.getLinearVelocity().y;
        if (unit.isCanFly()) {
            desiredVelocityY = toWorldSpeed(unit.speedY);
        }

        if (desiredVelocityX != 0f && unit.getRoom() != null) {
            float candidateX = unit.x + toPixelSpeed(desiredVelocityX) * TIME_STEP;
            if (!UnitHelper.getInstance().freeSpace(unit, Math.round(candidateX), Math.round(unit.y), unit.getRoom())) {
                desiredVelocityX = 0f;
            }
        }

        body.setLinearVelocity(desiredVelocityX, desiredVelocityY);
    }

    public void jump(Unit unit, float jumpSpeed) {
        Body body = unitBodies.get(unit);
        if (body == null) {
            return;
        }

        body.setLinearVelocity(body.getLinearVelocity().x, toWorldSpeed(jumpSpeed));
        unit.speedY = jumpSpeed;
    }

    public void setVerticalSpeed(Unit unit, float verticalSpeed) {
        Body body = unitBodies.get(unit);
        if (body == null) {
            return;
        }

        body.setLinearVelocity(body.getLinearVelocity().x, toWorldSpeed(verticalSpeed));
        unit.speedY = verticalSpeed;
    }

    public float getHorizontalSpeed(Unit unit) {
        Body body = unitBodies.get(unit);
        if (body == null) {
            return 0f;
        }

        return body.getLinearVelocity().x;
    }

    private float getGravityScale(Unit unit) {
        if (unit.isCanFly()) {
            return 0f;
        }

        return unit.isLevitating() ? 0.5f : 1f;
    }

    public String describeHorizontalMovementBlockers(Unit unit) {
        if (unit == null || unit.showOnly() || unit.getRoom() == null) {
            return "none";
        }

        if (!UnitHelper.getInstance().isUnitMovementCollisionBlockingEnabled()) {
            return "disabled";
        }

        Body body = unitBodies.get(unit);
        if (body == null) {
            return "none";
        }

        if (!body.isActive()) {
            return "inactive-body";
        }

        if (unit.movingLeft == unit.movingRight && Math.abs(unit.momentX) < 1f) {
            return unit.movingLeft ? "opposed-input" : "none";
        }

        float desiredVelocityX = getDesiredHorizontalVelocity(unit);
        if (Math.abs(desiredVelocityX) < toWorldSpeed(5f)) {
            return "none";
        }

        float candidateX = unit.x + toPixelSpeed(desiredVelocityX) * TIME_STEP;
        ArrayList<Unit> blockingUnits = UnitHelper.getInstance().getMovementBlockers(unit,
                Math.round(candidateX),
                Math.round(unit.y),
                unit.getRoom());

        if (!blockingUnits.isEmpty()) {
            return "units " + formatBlockingUnits(blockingUnits);
        }

        if (isPinnedAtHorizontalEdge(candidateX)) {
            return "edge";
        }

        if (Math.abs(body.getLinearVelocity().x) < toWorldSpeed(5f) && hasTerrainAhead(unit, desiredVelocityX)) {
            return "terrain";
        }

        return "none";
    }

    public Unit queryFirstHit(Unit source, Rectangle area) {
        noteMeleeArea(area);
        if (!isActiveUnit(source)) {
            return null;
        }

        final Unit[] target = new Unit[]{null};
        world.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                PhysicsBodyData data = getBodyData(fixture);
                if (data == null || data.bodyKind != BodyKind.UNIT) {
                    return true;
                }

                Unit candidate = (Unit) data.reference;
                if (!canHit(source, candidate)) {
                    return true;
                }

                target[0] = candidate;
                return false;
            }
        }, toWorld(area.x), toWorld(area.y), toWorld(area.x + area.width), toWorld(area.y + area.height));

        return target[0];
    }

    public void renderDebug(OrthographicCamera worldCamera) {
        if (!debugEnabled) {
            return;
        }

        debugCamera.setToOrtho(false, worldCamera.viewportWidth / PIXELS_PER_METER, worldCamera.viewportHeight / PIXELS_PER_METER);
        debugCamera.position.set(worldCamera.position.x / PIXELS_PER_METER, worldCamera.position.y / PIXELS_PER_METER, 0f);
        debugCamera.update();
        debugRenderer.render(world, debugCamera.combined);

        if (lastMeleeArea == null || lastMeleeAreaTimer <= 0f) {
            return;
        }

        shapeRenderer.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(lastMeleeArea.x, lastMeleeArea.y, lastMeleeArea.width, lastMeleeArea.height);
        shapeRenderer.end();
    }

    private void registerProjectileImmediate(ThrownProjectile projectile) {
        if (projectile == null || projectileBodies.containsKey(projectile)) {
            return;
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = true;
        float sizePixels = projectile.getPhysicsCollisionSize();
        bodyDef.position.set(toWorld(projectile.x + sizePixels / 2f), toWorld(projectile.y + sizePixels / 2f));
        Body body = world.createBody(bodyDef);
        body.setGravityScale(projectile.getPhysicsGravityScale());
        body.setLinearDamping(projectile.getPhysicsLinearDamping());

        PolygonShape shape = new PolygonShape();
        float halfExtent = toWorld(sizePixels / 2f);
        shape.setAsBox(halfExtent, halfExtent);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = CATEGORY_PROJECTILE;
        fixtureDef.filter.maskBits = CATEGORY_UNIT;
        if (projectile.collidesWithTerrain()) {
            fixtureDef.filter.maskBits |= CATEGORY_TERRAIN;
        }

        Fixture fixture = body.createFixture(fixtureDef);
        PhysicsBodyData data = new PhysicsBodyData(BodyKind.PROJECTILE, projectile, halfExtent, false);
        body.setUserData(data);
        fixture.setUserData(data);
        shape.dispose();

        projectileBodies.put(projectile, body);
        syncBodyToUnit(projectile);
    }

    private void rebuildTerrain(Room room) {
        clearTerrain();

        addPlatformSegment(0, ConstantsHelper.MIN_FLOOR, (int) room.getWidth(), false);

        HashSet<String> platforms = room.getPlatforms();
        HashMap<Integer, ArrayList<Integer>> platformRows = new HashMap<Integer, ArrayList<Integer>>();
        for (String platform : platforms) {
            int tileX = Integer.parseInt(platform.split("_")[0]);
            int tileY = Integer.parseInt(platform.split("_")[1]) + 1;
            if (!platformRows.containsKey(tileY)) {
                platformRows.put(tileY, new ArrayList<Integer>());
            }

            platformRows.get(tileY).add(tileX);
        }

        for (Map.Entry<Integer, ArrayList<Integer>> row : platformRows.entrySet()) {
            addPlatformRowSegments(row.getValue(), row.getKey());
        }

        addBoundaryWalls(room);
    }

    private void addPlatformRowSegments(ArrayList<Integer> tileXs, int tileY) {
        if (tileXs.isEmpty()) {
            return;
        }

        Collections.sort(tileXs);
        int startTileX = tileXs.get(0);
        int previousTileX = startTileX;
        for (int index = 1; index < tileXs.size(); index++) {
            int tileX = tileXs.get(index);
            if (tileX == previousTileX + 1) {
                previousTileX = tileX;
                continue;
            }

            addPlatformSegment(startTileX, tileY, previousTileX - startTileX + 1, true);
            startTileX = tileX;
            previousTileX = tileX;
        }

        addPlatformSegment(startTileX, tileY, previousTileX - startTileX + 1, true);
    }

    private void addPlatformSegment(int startTileX, int tileY, int tileCount, boolean oneWay) {
        if (tileCount <= 0) {
            return;
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        float halfWidth = toWorld(ConstantsHelper.TILE * tileCount / 2f);
        float halfHeight = toWorld(4f);
        float centerX = startTileX * ConstantsHelper.TILE + ConstantsHelper.TILE * tileCount / 2f;
        shape.setAsBox(halfWidth, halfHeight, new Vector2(toWorld(centerX), toWorld(tileY * ConstantsHelper.TILE)), 0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = CATEGORY_TERRAIN;
        fixtureDef.filter.maskBits = CATEGORY_UNIT | CATEGORY_PROJECTILE;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(new TerrainFixtureData(oneWay, toWorld(tileY * ConstantsHelper.TILE)));
        shape.dispose();
        terrainBodies.add(body);
    }

    private void addBoundaryWalls(Room room) {
        addWallFixture(0f, room.getHeight() * ConstantsHelper.TILE / 2f, 4f, room.getHeight() * ConstantsHelper.TILE / 2f);
        addWallFixture(room.getWidth() * ConstantsHelper.TILE, room.getHeight() * ConstantsHelper.TILE / 2f, 4f, room.getHeight() * ConstantsHelper.TILE / 2f);
    }

    private void addWallFixture(float centerX, float centerY, float halfWidthPixels, float halfHeightPixels) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(toWorld(halfWidthPixels), toWorld(halfHeightPixels), new Vector2(toWorld(centerX), toWorld(centerY)), 0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = CATEGORY_TERRAIN;
        fixtureDef.filter.maskBits = CATEGORY_UNIT | CATEGORY_PROJECTILE;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(new TerrainFixtureData(false, 0f));
        shape.dispose();
        terrainBodies.add(body);
    }

    private void updateBodyActivation() {
        ArrayList<Unit> currentUnits = new ArrayList<Unit>(UnitHelper.getInstance().getUnits());
        for (Unit unit : currentUnits) {
            if (usesPhysics(unit) && !unitBodies.containsKey(unit)) {
                register(unit);
            }
        }

        for (Map.Entry<Unit, Body> entry : unitBodies.entrySet()) {
            Unit unit = entry.getKey();
            Body body = entry.getValue();
            boolean active = isActiveUnit(unit);
            if (body.isActive() != active) {
                body.setActive(active);
            }
            if (active && !body.isAwake()) {
                syncBodyToUnit(unit);
            }
        }

        for (Map.Entry<ThrownProjectile, Body> entry : projectileBodies.entrySet()) {
            ThrownProjectile projectile = entry.getKey();
            Body body = entry.getValue();
            boolean active = isActiveProjectile(projectile);
            if (body.isActive() != active) {
                body.setActive(active);
            }
            if (active && !body.isAwake()) {
                syncBodyToUnit(projectile);
            }
        }
    }

    private void syncAllUnitsFromBodies() {
        for (Map.Entry<Unit, Body> entry : unitBodies.entrySet()) {
            if (entry.getValue().isActive()) {
                syncUnitFromPhysics(entry.getKey());
            }
        }

        for (Map.Entry<ThrownProjectile, Body> entry : projectileBodies.entrySet()) {
            if (entry.getValue().isActive()) {
                syncProjectileFromPhysics(entry.getKey());
            }
        }
    }

    private void syncAllBodiesToUnits() {
        for (Unit unit : unitBodies.keySet()) {
            syncBodyToUnit(unit);
        }

        for (ThrownProjectile projectile : projectileBodies.keySet()) {
            syncBodyToUnit(projectile);
        }
    }

    private void clearTerrain() {
        destroyBodies(terrainBodies.iterator());
        terrainBodies.clear();
    }

    private void destroyBodies(Iterator<Body> bodies) {
        ArrayList<Body> toDestroy = new ArrayList<Body>();
        while (bodies.hasNext()) {
            toDestroy.add(bodies.next());
        }

        for (Body body : toDestroy) {
            destroyBody(body);
        }
    }

    private void queueRegistration(Unit unit) {
        if (unit == null || pendingRegistrations.contains(unit)) {
            return;
        }

        pendingRegistrations.add(unit);
    }

    private void queueBodySync(Unit unit) {
        if (unit == null || pendingBodySyncs.contains(unit)) {
            return;
        }

        pendingBodySyncs.add(unit);
    }

    private void destroyBody(Body body) {
        if (body == null) {
            return;
        }

        if (world.isLocked()) {
            if (!pendingBodyDestructions.contains(body)) {
                pendingBodyDestructions.add(body);
            }
            return;
        }

        world.destroyBody(body);
    }

    private void processPendingBodyChanges() {
        if (world.isLocked()) {
            return;
        }

        if (!pendingBodyDestructions.isEmpty()) {
            ArrayList<Body> bodiesToDestroy = new ArrayList<Body>(pendingBodyDestructions);
            pendingBodyDestructions.clear();
            for (Body body : bodiesToDestroy) {
                world.destroyBody(body);
            }
        }

        if (!pendingBodySyncs.isEmpty()) {
            ArrayList<Unit> unitsToSync = new ArrayList<Unit>(pendingBodySyncs);
            pendingBodySyncs.clear();
            for (Unit unit : unitsToSync) {
                syncBodyToUnit(unit);
            }
        }

        if (!pendingRegistrations.isEmpty()) {
            ArrayList<Unit> unitsToRegister = new ArrayList<Unit>(pendingRegistrations);
            pendingRegistrations.clear();
            for (Unit unit : unitsToRegister) {
                registerImmediate(unit);
            }
        }
    }

    private void handleProjectileContact(Fixture projectileFixture, Fixture otherFixture) {
        PhysicsBodyData projectileData = getBodyData(projectileFixture);
        if (projectileData == null || projectileData.bodyKind != BodyKind.PROJECTILE) {
            return;
        }

        ThrownProjectile projectile = (ThrownProjectile) projectileData.reference;
        if (!isActiveProjectile(projectile)) {
            return;
        }

        TerrainFixtureData terrainData = getTerrainData(otherFixture);
        if (terrainData != null) {
            if (projectile.collidesWithTerrain() && shouldEnableTerrainContact(projectileFixture.getBody(), terrainData, projectileData)) {
                projectile.onTerrainCollision();
            }
            return;
        }

        PhysicsBodyData otherData = getBodyData(otherFixture);
        if (otherData == null || otherData.bodyKind != BodyKind.UNIT) {
            return;
        }

        Unit target = (Unit) otherData.reference;
        if (!canHit(projectile.getOwner(), target) || projectile.getOwner() == target) {
            return;
        }

        if (projectile.getRoom() == null || !projectile.getRoom().equals(target.getRoom())) {
            return;
        }

        projectile.onUnitCollision(target);
    }

    private boolean canHit(Unit source, Unit target) {
        if (source == null || target == null) {
            return false;
        }

        if (!isActiveUnit(source) || !isActiveUnit(target) || target == source) {
            return false;
        }

        if (source.isFriendly && target.isFriendly) {
            return false;
        }

        if (!source.isFriendly && !target.isFriendly) {
            return false;
        }

        if (source.getRoom() == null || target.getRoom() == null || !source.getRoom().equals(target.getRoom())) {
            return false;
        }

        return true;
    }

    private void noteMeleeArea(Rectangle area) {
        lastMeleeArea = new Rectangle(area);
        lastMeleeAreaTimer = LAST_MELEE_DRAW_TIME;
    }

    private boolean shouldEnableTerrainContact(Contact contact, boolean terrainIsFixtureA, Body body, TerrainFixtureData terrain, PhysicsBodyData bodyData) {
        if (!terrain.oneWay) {
            return true;
        }

        if (bodyData.flying) {
            return false;
        }

        WorldManifold worldManifold = contact.getWorldManifold();
        float terrainToBodyNormalY = terrainIsFixtureA ? worldManifold.getNormal().y : -worldManifold.getNormal().y;
        if (terrainToBodyNormalY < 0.5f) {
            return false;
        }

        float bodyBottom = body.getPosition().y - bodyData.halfExtentMeters;
        if (body.getLinearVelocity().y > 0.01f) {
            return false;
        }

        return bodyBottom >= terrain.topY - toWorld(8f);
    }

    private boolean shouldEnableTerrainContact(Body body, TerrainFixtureData terrain, PhysicsBodyData bodyData) {
        if (!terrain.oneWay) {
            return true;
        }

        if (bodyData.flying) {
            return false;
        }

        float bodyBottom = body.getPosition().y - bodyData.halfExtentMeters;
        if (body.getLinearVelocity().y > 0.01f) {
            return false;
        }

        return bodyBottom >= terrain.topY - toWorld(8f);
    }

    private float getDesiredHorizontalVelocity(Unit unit) {
        float inputVelocityX = 0f;
        inputVelocityX += unit.movingLeft ? -toWorldSpeed(unit.getSpeedX()) : 0f;
        inputVelocityX += unit.movingRight ? toWorldSpeed(unit.getSpeedX()) : 0f;

        if (isGrounded(unit) || unit.isCanFly()) {
            return inputVelocityX + toWorldSpeed(unit.momentX);
        }

        float airControlVelocity = unit.airMomentumX;
        if (inputVelocityX != 0f) {
            airControlVelocity = MathUtils.lerp(airControlVelocity, inputVelocityX, AIR_CONTROL_LERP);
            airControlVelocity = MathUtils.clamp(airControlVelocity, -toWorldSpeed(unit.getSpeedX()), toWorldSpeed(unit.getSpeedX()));
        }

        return airControlVelocity + toWorldSpeed(unit.momentX);
    }

    private boolean isPinnedAtHorizontalEdge(float candidateX) {
        return candidateX <= 1f
                || candidateX >= MapHelper.getInstance().getWidth() * ConstantsHelper.TILE - 1 - ConstantsHelper.UNIT_DIMENSIONS;
    }

    private boolean hasTerrainAhead(Unit unit, float desiredVelocityX) {
        Body body = unitBodies.get(unit);
        if (body == null || desiredVelocityX == 0f) {
            return false;
        }

        PhysicsBodyData bodyData = (PhysicsBodyData) body.getUserData();
        if (bodyData == null) {
            return false;
        }

        final boolean[] blocked = new boolean[]{false};
        final float direction = Math.signum(desiredVelocityX);
        float rayStartX = body.getPosition().x + direction * bodyData.halfExtentMeters;
        float rayEndX = rayStartX + direction * toWorld(6f);
        float rayY = body.getPosition().y;

        world.rayCast(new com.badlogic.gdx.physics.box2d.RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                TerrainFixtureData terrainData = getTerrainData(fixture);
                if (terrainData == null) {
                    return -1f;
                }

                if (terrainData.oneWay) {
                    return -1f;
                }

                blocked[0] = true;
                return fraction;
            }
        }, rayStartX, rayY, rayEndX, rayY);

        return blocked[0];
    }

    private String formatBlockingUnits(ArrayList<Unit> blockingUnits) {
        StringBuilder builder = new StringBuilder();
        int shown = Math.min(blockingUnits.size(), 2);
        for (int index = 0; index < shown; index++) {
            if (index > 0) {
                builder.append('/');
            }

            builder.append(blockingUnits.get(index).getClass().getSimpleName());
        }

        if (blockingUnits.size() > shown) {
            builder.append("+").append(blockingUnits.size() - shown);
        }

        return builder.toString();
    }

    private boolean isActiveRoom(String roomIdentifier) {
        return activeRoomIdentifier != null && activeRoomIdentifier.equals(roomIdentifier);
    }

    private boolean isActiveUnit(Unit unit) {
        return unit != null && !unit.showOnly() && isActiveRoom(unit.getRoom());
    }

    private boolean isActiveProjectile(ThrownProjectile projectile) {
        return projectile != null && !projectile.isUsed() && isActiveRoom(projectile.getRoom());
    }

    private TerrainFixtureData getTerrainData(Fixture fixture) {
        Object userData = fixture.getUserData();
        return userData instanceof TerrainFixtureData ? (TerrainFixtureData) userData : null;
    }

    private PhysicsBodyData getBodyData(Fixture fixture) {
        Object userData = fixture.getUserData();
        return userData instanceof PhysicsBodyData ? (PhysicsBodyData) userData : null;
    }

    public static float toWorld(float pixels) {
        return pixels / PIXELS_PER_METER;
    }

    public static float toPixels(float meters) {
        return meters * PIXELS_PER_METER;
    }

    public static float toWorldSpeed(float pixelsPerSecond) {
        return pixelsPerSecond / PIXELS_PER_METER;
    }

    public static float toPixelSpeed(float metersPerSecond) {
        return metersPerSecond * PIXELS_PER_METER;
    }

    private enum BodyKind {
        UNIT,
        PROJECTILE
    }

    private static final class TerrainFixtureData {
        private final boolean oneWay;
        private final float topY;

        private TerrainFixtureData(boolean oneWay, float topY) {
            this.oneWay = oneWay;
            this.topY = topY;
        }
    }

    private static final class PhysicsBodyData {
        private final BodyKind bodyKind;
        private final Object reference;
        private final float halfExtentMeters;
        private final boolean flying;

        private PhysicsBodyData(BodyKind bodyKind, Object reference, float halfExtentMeters, boolean flying) {
            this.bodyKind = bodyKind;
            this.reference = reference;
            this.halfExtentMeters = halfExtentMeters;
            this.flying = flying;
        }
    }
}
