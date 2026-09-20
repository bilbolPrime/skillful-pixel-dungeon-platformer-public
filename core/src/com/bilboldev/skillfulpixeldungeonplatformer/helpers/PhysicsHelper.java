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
    private static final float MAX_GAME_DELTA = 0.1f;
    private static final float LAST_MELEE_DRAW_TIME = 0.2f;
    private static final float AIR_CONTROL_LERP = 0.18f;
    private static final float HERO_AIR_BRAKE_RATE = 8f;
    public static final float HERO_DESCENT_GRAVITY_MULTIPLIER = 1.3f;
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


    public static float boundGameDelta(float delta) {
        return delta > 0f ? Math.min(delta, MAX_GAME_DELTA) : 0f;
    }

    public void step(float delta) {
        delta = boundGameDelta(delta);
        ensureRoom(MapHelper.getInstance().getActiveRoom());
        processPendingBodyChanges();
        updateBodyActivation();

        accumulator += delta;
        while (accumulator >= TIME_STEP) {

            Unit hero = UnitHelper.getInstance().getHero();
            Body heroBody = unitBodies.get(hero);
            if (heroBody != null) heroBody.setGravityScale(getGravityScale(hero));
            captureRenderTransforms(true);
            world.step(TIME_STEP, 6, 2);
            captureRenderTransforms(false);
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
            resetRenderTransform(body);
            return;
        }

        body.setTransform(toWorld(unit.x + ConstantsHelper.UNIT_DIMENSIONS / 2f), toWorld(unit.y) + data.halfExtentMeters, 0f);
        resetRenderTransform(body);
    }


    public void resetRenderTransform(Unit unit) {
        Body body = unit instanceof ThrownProjectile ? projectileBodies.get(unit) : unitBodies.get(unit);
        if (body != null) resetRenderTransform(body);
    }

    private void resetRenderTransform(Body body) {
        PhysicsBodyData data = (PhysicsBodyData)body.getUserData();
        data.previousX = data.currentX = body.getPosition().x;
        data.previousY = data.currentY = body.getPosition().y;
    }

    private void captureRenderTransforms(boolean beforeStep) {
        for (Body body : unitBodies.values()) captureRenderTransform(body, beforeStep);
        for (Body body : projectileBodies.values()) captureRenderTransform(body, beforeStep);
    }

    private void captureRenderTransform(Body body, boolean beforeStep) {
        if (!body.isActive()) return;
        PhysicsBodyData data = (PhysicsBodyData)body.getUserData();
        if (beforeStep) {
            data.previousX = body.getPosition().x;
            data.previousY = body.getPosition().y;
        } else {
            data.currentX = body.getPosition().x;
            data.currentY = body.getPosition().y;
        }
    }

    public float getRenderX(Unit unit) { return unit.x + renderOffset(unit, true); }
    public float getRenderY(Unit unit) { return unit.y + renderOffset(unit, false); }

    private float renderOffset(Unit unit, boolean horizontal) {
        Body body = unit instanceof ThrownProjectile ? projectileBodies.get(unit) : unitBodies.get(unit);
        if (body == null || !body.isActive()) return 0f;
        PhysicsBodyData data = (PhysicsBodyData)body.getUserData();
        float previous = horizontal ? data.previousX : data.previousY;
        float current = horizontal ? data.currentX : data.currentY;
        float alpha = MathUtils.clamp(accumulator / TIME_STEP, 0f, 1f);
        return toPixels(MathUtils.lerp(previous, current, alpha) - current);
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
        if (unit.isHero) return hasHeroFootSupport(body, data);
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


    public boolean hasTerrainFootContact(Unit unit) {
        Body body = unitBodies.get(unit);
        if (body == null || !body.isActive() || unit.isCanFly() || body.getLinearVelocity().y > 0.001f) return false;
        for (Contact contact : world.getContactList()) {
            if (!contact.isEnabled() || !contact.isTouching()) continue;
            Fixture a = contact.getFixtureA(), b = contact.getFixtureB();
            boolean bodyIsB = b.getBody() == body;
            if (!bodyIsB && a.getBody() != body) continue;
            if (getTerrainData(bodyIsB ? a : b) == null) continue;
            float supportNormalY = contact.getWorldManifold().getNormal().y * (bodyIsB ? 1f : -1f);
            if (supportNormalY >= 0.5f) return true;
        }
        return false;
    }

    private boolean hasHeroFootSupport(Body body, PhysicsBodyData data) {

        if (body.getLinearVelocity().y > 0.001f || body.getLinearVelocity().y <= -toWorldSpeed(50f)) return false;
        final boolean[] supported = new boolean[]{false};
        final float bottom = body.getPosition().y - data.halfExtentMeters;
        float startY = bottom + 0.02f;
        com.badlogic.gdx.physics.box2d.RayCastCallback sample = new com.badlogic.gdx.physics.box2d.RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                TerrainFixtureData terrain = getTerrainData(fixture);
                if (terrain == null || normal.y < 0.5f || point.y > bottom + 0.02f
                        || (terrain.oneWay && bottom < terrain.topY)) return -1f;
                supported[0] = true;
                return fraction;
            }
        };
        float halfFoot = Math.max(0f, data.halfExtentMeters - toWorld(2f));
        for (int foot = -1; foot <= 1; foot++) {
            float sampleX = body.getPosition().x + foot * halfFoot;
            world.rayCast(sample, sampleX, startY, sampleX, startY - toWorld(6f));
            if (supported[0]) return true;
        }
        return false;
    }

    public float getFloorY(Unit unit) {
        if (isGrounded(unit)) {
            return unit.y;
        }

        return MapHelper.getInstance().calculateFloorY(unit.x, unit.y);
    }


    public boolean canReachInteraction(Unit hero, float targetX, float targetFloorY) {
        Body body = unitBodies.get(hero);

        if (body == null || !isActiveRoom(hero.getRoom())
                || Float.isNaN(targetX) || Float.isNaN(targetFloorY)
                || Math.abs(hero.y - targetFloorY) > ConstantsHelper.TILE * 0.25f) return false;
        float targetSupport = interactionSupportAt(targetX, targetFloorY);
        if (Float.isNaN(targetSupport)) return false;
        PhysicsBodyData data = (PhysicsBodyData) body.getUserData();
        float centerX = hero.x + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float halfFoot = Math.max(0f, toPixels(data.halfExtentMeters) - 2f);
        boolean compatibleSupport = false;
        for (int foot = -1; foot <= 1; foot++) {
            float support = interactionSupportAt(centerX + foot * halfFoot, hero.y);
            if (!Float.isNaN(support) && Math.abs(support - targetSupport) <= 8f) {
                compatibleSupport = true;
                break;
            }
        }
        if (!compatibleSupport) return false;
        final boolean[] blocked = {false};
        float startY = hero.y + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        float endY = targetFloorY + ConstantsHelper.UNIT_DIMENSIONS / 2f;
        if (Math.abs(centerX - targetX) + Math.abs(startY - endY) < 0.01f) return true;
        world.rayCast(new com.badlogic.gdx.physics.box2d.RayCastCallback() {
            @Override public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (getTerrainData(fixture) == null) return -1f;
                blocked[0] = true;
                return 0f;
            }
        }, toWorld(centerX), toWorld(startY), toWorld(targetX), toWorld(endY));
        return !blocked[0];
    }


    public boolean hasCorpseLineOfSight(Room room, float x1, float y1, float x2, float y2) {
        return hasSolidLineOfSight(room, x1, y1, x2, y2);
    }


    public boolean hasSolidLineOfSight(Room room, float x1, float y1, float x2, float y2) {
        if (room == null || !room.getIdentifier().equals(activeRoomIdentifier)
                || !Float.isFinite(x1) || !Float.isFinite(y1) || !Float.isFinite(x2) || !Float.isFinite(y2)) return false;
        for (Body body : terrainBodies) for (Fixture fixture : body.getFixtureList()) {
            if (fixture.testPoint(toWorld(x1), toWorld(y1)) || fixture.testPoint(toWorld(x2), toWorld(y2))) return false;
        }
        if (Math.abs(x1 - x2) + Math.abs(y1 - y2) < 0.001f) return true;
        final boolean[] blocked = {false};
        world.rayCast((fixture, point, normal, fraction) -> {
            if (getTerrainData(fixture) == null) return -1f;
            blocked[0] = true;
            return 0f;
        }, toWorld(x1), toWorld(y1), toWorld(x2), toWorld(y2));
        return !blocked[0];
    }


    public boolean isCorpseActionSpaceClear(Room room, final Rectangle area) {
        return isTerrainSpaceClear(room, area, false);
    }


    public boolean isUnitRestoreSpaceClear(Room room, final Rectangle area) {
        return isTerrainSpaceClear(room, area, true);
    }

    private boolean isTerrainSpaceClear(Room room, final Rectangle area, final boolean ignoreOneWay) {
        if (room == null || !room.getIdentifier().equals(activeRoomIdentifier) || area == null
                || !Float.isFinite(area.x) || !Float.isFinite(area.y) || area.width <= 0f || area.height <= 0f
                || !Float.isFinite(area.width) || !Float.isFinite(area.height)) return false;
        final boolean[] blocked = {false};
        final Vector2 vertex = new Vector2();
        world.QueryAABB(fixture -> {
            TerrainFixtureData terrain = getTerrainData(fixture);
            if (terrain == null || ignoreOneWay && terrain.oneWay) return true;
            PolygonShape shape = (PolygonShape)fixture.getShape();
            float left = Float.POSITIVE_INFINITY, right = Float.NEGATIVE_INFINITY;
            float bottom = Float.POSITIVE_INFINITY, top = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < shape.getVertexCount(); i++) {
                shape.getVertex(i, vertex);
                Vector2 point = fixture.getBody().getWorldPoint(vertex);
                left = Math.min(left, toPixels(point.x)); right = Math.max(right, toPixels(point.x));
                bottom = Math.min(bottom, toPixels(point.y)); top = Math.max(top, toPixels(point.y));
            }
            blocked[0] = area.x < right && area.x + area.width > left
                    && area.y < top && area.y + area.height > bottom;
            return !blocked[0];
        }, toWorld(area.x), toWorld(area.y), toWorld(area.x + area.width), toWorld(area.y + area.height));
        return !blocked[0];
    }

    private float interactionSupportAt(float x, float feetY) {
        final float[] top = {Float.NaN};
        world.rayCast(new com.badlogic.gdx.physics.box2d.RayCastCallback() {
            @Override public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (getTerrainData(fixture) == null || normal.y < 0.5f) return -1f;
                top[0] = toPixels(point.y);
                return fraction;
            }
        }, toWorld(x), toWorld(feetY + 8f), toWorld(x), toWorld(feetY - ConstantsHelper.TILE * 0.25f));
        return top[0];
    }

    public void applyMovement(Unit unit, boolean grounded, boolean allowDirectionalInput, float delta) {
        Body body = unitBodies.get(unit);
        if (body == null) {
            return;
        }

        delta = boundGameDelta(delta);
        ((PhysicsBodyData)body.getUserData()).movementDelta = delta;
        if (delta == 0f) return;

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
            if (allowDirectionalInput && (inputVelocityX != 0f || unit.isHero)) {
                unit.airMomentumX = steerAirVelocity(unit, inputVelocityX, delta);
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

        float scale = unit.isLevitating() ? 0.5f : 1f;
        Body body = unitBodies.get(unit);
        if (unit.isHero && body != null && body.getLinearVelocity().y < -0.001f) {
            scale *= HERO_DESCENT_GRAVITY_MULTIPLIER;
        }
        return scale;
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
            if (!room.isBossArena() && !com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.RoomGeometry
                    .validPlatform(room, tileX, tileY - 1)) continue;
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
                entry.getKey().afterPhysicsStep();
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
            if (projectile.collidesWithTerrain() && (projectile.hitsBothSidesOfPlatforms()
                    || shouldEnableTerrainContact(projectileFixture.getBody(), terrainData, projectileData))) {
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

        if (bodyData.bodyKind == BodyKind.UNIT && ((Unit)bodyData.reference).isDroppingThroughPlatform()) return false;

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

        if (bodyData.bodyKind == BodyKind.UNIT && ((Unit)bodyData.reference).isDroppingThroughPlatform()) return false;

        if (bodyData.flying) {
            return false;
        }

        float bodyBottom = body.getPosition().y - bodyData.halfExtentMeters;
        if (body.getLinearVelocity().y > 0.01f) {
            return false;
        }

        return bodyBottom >= terrain.topY - toWorld(8f);
    }

    private float steerAirVelocity(Unit unit, float inputVelocityX, float delta) {
        if (delta <= 0f) return unit.airMomentumX;
        if (inputVelocityX == 0f) {
            if (!unit.isHero) return unit.airMomentumX;
            float speed = toWorldSpeed(unit.getSpeedX());
            return MathUtils.clamp(unit.airMomentumX * (float)Math.exp(-HERO_AIR_BRAKE_RATE * delta), -speed, speed);
        }
        float alpha = 1f - (float)Math.pow(1f - AIR_CONTROL_LERP, delta * 60f);
        float speed = toWorldSpeed(unit.getSpeedX());
        return MathUtils.clamp(MathUtils.lerp(unit.airMomentumX, inputVelocityX, alpha), -speed, speed);
    }

    private float getDesiredHorizontalVelocity(Unit unit) {
        float inputVelocityX = 0f;
        inputVelocityX += unit.movingLeft ? -toWorldSpeed(unit.getSpeedX()) : 0f;
        inputVelocityX += unit.movingRight ? toWorldSpeed(unit.getSpeedX()) : 0f;

        if (isGrounded(unit) || unit.isCanFly()) {
            return inputVelocityX + toWorldSpeed(unit.momentX);
        }

        float airControlVelocity = unit.airMomentumX;
        if (inputVelocityX != 0f || unit.isHero) {
            Body body = unitBodies.get(unit);
            float delta = body == null ? TIME_STEP : ((PhysicsBodyData)body.getUserData()).movementDelta;
            airControlVelocity = steerAirVelocity(unit, inputVelocityX, delta);
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
        private float movementDelta = TIME_STEP;
        private float previousX, previousY, currentX, currentY;

        private PhysicsBodyData(BodyKind bodyKind, Object reference, float halfExtentMeters, boolean flying) {
            this.bodyKind = bodyKind;
            this.reference = reference;
            this.halfExtentMeters = halfExtentMeters;
            this.flying = flying;
        }
    }
}
