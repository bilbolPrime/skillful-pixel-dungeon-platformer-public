package com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms;

import com.bilboldev.skillfulpixeldungeonplatformer.helpers.ConstantsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.PhysicsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.RandomHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UnitHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.doors.Door;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.items.ItemOnScreen;
import com.bilboldev.skillfulpixeldungeonplatformer.units.environment.plants.Plant;
import com.bilboldev.skillfulpixeldungeonplatformer.units.hero.Hero;
import com.bilboldev.skillfulpixeldungeonplatformer.units.interactable.Interactable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class RoomRoutes {
    private static final float T = ConstantsHelper.TILE, U = ConstantsHelper.UNIT_DIMENSIONS;
    private static final float G = ConstantsHelper.GRAVITY;
    private static final float DOWN_G = G * PhysicsHelper.HERO_DESCENT_GRAVITY_MULTIPLIER;
    private static final float JUMP = Hero.BASE_JUMP_SPEED;
    private static final float APEX = JUMP * JUMP / (2f * G);
    private static final float LANDING_MARGIN = 12f;
    private static final float SPEED = minimumHeroSpeed() * 0.8f;

    private RoomRoutes() { }


    public static int minimumRoomHeightForJump(int standingFloor) {
        return 1 + (int) Math.ceil(jumpTopWithClearance(standingFloor) / T);
    }

    public static float jumpTopWithClearance(int standingFloor) {
        return standingFloor * T + APEX + U + LANDING_MARGIN;
    }

    private static float minimumHeroSpeed() {
        float speed = Float.MAX_VALUE;
        for (HeroClass hero : HeroClass.values()) if (hero != HeroClass.NEUTRAL) speed = Math.min(speed, hero.getMoveSpeed());
        return speed;
    }

    public static final class Result {
        public final boolean valid;
        public final String problem;
        private final List<Cell> inaccessible, noReturn;
        private Result(String problem, List<Cell> inaccessible, List<Cell> noReturn) {
            this.problem = problem;
            this.inaccessible = inaccessible;
            this.noReturn = noReturn;
            valid = problem == null && inaccessible.isEmpty() && noReturn.isEmpty();
        }
    }

    private static final class Cell {
        final int column, floor;
        Cell(int column, int floor) { this.column = column; this.floor = floor; }
        float x() { return column * T + (T - U) / 2f; }
        float y() { return floor * T; }
        @Override public String toString() { return column + "," + floor; }
    }

    public static Result check(Room room) {
        ArrayList<Cell> nodes = new ArrayList<>();
        for (int floor = ConstantsHelper.MIN_FLOOR; floor < room.getHeight(); floor++) {
            for (int column = 0; column < room.getWidth(); column++) {
                Cell cell = new Cell(column, floor);
                if (RoomGeometry.supports(room, cell.x(), cell.y(), U, U)) nodes.add(cell);
            }
        }
        ArrayList<Cell> inaccessible = new ArrayList<>(), noReturn = new ArrayList<>();
        if (nodes.isEmpty()) return new Result("no standing space", inaccessible, noReturn);
        boolean[][] routes = new boolean[nodes.size()][nodes.size()];
        for (int first = 0; first < nodes.size(); first++) {
            Cell from = nodes.get(first);
            for (int second = 0; second < nodes.size(); second++) {
                if (first == second) continue;
                Cell to = nodes.get(second);
                routes[first][second] = (from.floor == to.floor && Math.abs(from.column - to.column) == 1)
                        || canJump(room, from, to) || canWalkOff(room, from, to);
            }
        }
        boolean[] outward = visit(routes, false), returning = visit(routes, true);
        for (int index = 0; index < nodes.size(); index++) {
            if (!outward[index]) inaccessible.add(nodes.get(index));
            if (!returning[index]) noReturn.add(nodes.get(index));
        }
        String problem = null;
        for (Door door : room.getDoors()) {
            if (!RoomGeometry.supports(room, door.x, door.y, T, T + 7f)) {
                problem = "unsupported or obstructed arrival " + door.x / T + "," + door.y / T;
                break;
            }
        }
        if (problem == null && room.getSign() != null
                && !RoomGeometry.supports(room, room.getSign().getX(), room.getSign().getY(), T, T)) {
            problem = "unsupported sign approach";
        }
        if (problem == null && !inaccessible.isEmpty()) problem = "unreachable surface " + inaccessible.get(0);
        if (problem == null && !noReturn.isEmpty()) problem = "no ledge return from " + noReturn.get(0);
        return new Result(problem, inaccessible, noReturn);
    }

    private static boolean[] visit(boolean[][] routes, boolean reverse) {
        boolean[] visited = new boolean[routes.length];
        ArrayDeque<Integer> pending = new ArrayDeque<>();
        visited[0] = true; pending.add(0);
        while (!pending.isEmpty()) {
            int from = pending.remove();
            for (int to = 0; to < routes.length; to++) {
                if (!visited[to] && (reverse ? routes[to][from] : routes[from][to])) {
                    visited[to] = true; pending.add(to);
                }
            }
        }
        return visited;
    }

    private static boolean canJump(Room room, Cell from, Cell to) {
        float rise = to.y() - from.y();
        if (rise > APEX - LANDING_MARGIN || from.y() + APEX + U > (room.getHeight() - 1) * T) return false;
        float time = JUMP / G + (float) Math.sqrt(2f * (APEX - rise) / DOWN_G);
        if (Math.abs(to.x() - from.x()) + LANDING_MARGIN > SPEED * time) return false;
        return clearDescent(room, from.x(), to.x(), from.y() + APEX, to.floor, time, JUMP / G);
    }

    private static boolean canWalkOff(Room room, Cell from, Cell to) {
        if (to.floor >= from.floor || from.floor == ConstantsHelper.MIN_FLOOR) return false;
        float time = (float) Math.sqrt(2f * (from.y() - to.y()) / DOWN_G);
        for (int direction : new int[]{-1, 1}) {
            int adjacent = from.column + direction;
            if (adjacent < 0 || adjacent >= room.getWidth() || support(room, adjacent, from.floor)) continue;
            float edgeX = direction < 0 ? from.column * T - U - LANDING_MARGIN
                    : (from.column + 1) * T + LANDING_MARGIN;
            if (edgeX < 1f || edgeX + U > room.getWidth() * T - 1f
                    || (to.x() - edgeX) * direction < -LANDING_MARGIN
                    || Math.abs(to.x() - edgeX) + LANDING_MARGIN > SPEED * time) continue;
            if (clearDescent(room, edgeX, to.x(), from.y(), to.floor, time, 0f)) return true;
        }
        return false;
    }


    private static boolean clearDescent(Room room, float fromX, float toX, float apexY,
                                        int targetFloor, float totalTime, float apexTime) {
        for (int floor = targetFloor + 1; floor * T <= apexY; floor++) {
            float crossing = apexTime + (float) Math.sqrt(2f * (apexY - floor * T) / DOWN_G);
            float x = fromX + (toX - fromX) * crossing / totalTime;
            int left = (int) Math.floor(x / T), right = (int) Math.floor((x + U - 0.01f) / T);
            for (int column = left; column <= right; column++) if (support(room, column, floor)) return false;
        }
        return true;
    }

    private static boolean support(Room room, int column, int floor) {
        return column >= 0 && column < room.getWidth() && (floor == ConstantsHelper.MIN_FLOOR
                || room.getPlatforms().contains(UtilsHelper.platformKey(column, floor - 1)));
    }


    public static void ensureTraversable(Room room) {
        if (room.isBossArena()) return;
        RoomGeometry.normalize(room);
        for (int candidate = 0; candidate < 3; candidate++) {
            Result result = check(room);
            if (result.valid) return;
            if (candidate == 2) throw new IllegalStateException("Seed " + RandomHelper.getInstance().getRunSeed()
                    + ", room " + room.getIdentifier() + " (" + room.getClass().getSimpleName() + "): " + result.problem);
            repair(room, result, candidate == 1);
        }
    }

    private static void repair(Room room, Result result, boolean fallback) {
        for (Door door : room.getDoors()) {
            int column = Math.round(door.x / T), floor = Math.round(door.y / T);
            if (floor > ConstantsHelper.MIN_FLOOR) room.addPlatformSpan(column, column, floor - 1);
        }
        if (room.getSign() != null) {
            int column = Math.round(room.getSign().getX() / T), floor = Math.round(room.getSign().getY() / T);
            if (floor > ConstantsHelper.MIN_FLOOR) room.addPlatformSpan(column, column, floor - 1);
        }
        Set<String> repairedSpans = new HashSet<>();
        for (Cell cell : result.inaccessible) {
            int start = cell.column, end = cell.column;
            while (start > 0 && support(room, start - 1, cell.floor)) start--;
            while (end + 1 < room.getWidth() && support(room, end + 1, cell.floor)) end++;
            if (!repairedSpans.add(start + "_" + cell.floor)) continue;
            int anchor = fallback ? (start + end) / 2 : Math.max(start, Math.min(end, cell.column));
            for (int floor = ConstantsHelper.MIN_FLOOR + 2; floor < cell.floor; floor += 2) {
                for (int offset : new int[]{0, 1, -1, 2, -2}) {
                    int column = anchor + offset;
                    if (column < 1 || column >= room.getWidth() - 1 || crossesDoor(room, column, floor)) continue;
                    room.addPlatformSpan(column, column, floor - 1);
                    break;
                }
            }
        }
        Set<Integer> openedFloors = new HashSet<>();
        for (Cell cell : result.noReturn) {
            if (cell.floor <= ConstantsHelper.MIN_FLOOR || !openedFloors.add(cell.floor)) continue;
            int width = (int) room.getWidth();
            for (int index = 0; index < width - 1; index++) {
                int start = fallback ? width - 2 - index : index;
                if (protectedSupport(room, start, start + 1, cell.floor)) continue;
                for (int column = start; column <= start + 1; column++) {
                    String key = UtilsHelper.platformKey(column, cell.floor - 1);
                    room.getPlatforms().remove(key);
                    room.getWaterPlatforms().remove(key);
                }
                break;
            }
        }
    }

    private static boolean crossesDoor(Room room, int column, int floor) {
        for (Door door : room.getDoors()) if (column * T < door.x + T && (column + 1) * T > door.x
                && floor * T > door.y && floor * T < door.y + T + 7f) return true;
        return false;
    }

    private static boolean protectedSupport(Room room, int first, int last, int floor) {
        for (Door door : room.getDoors()) if (Math.abs(door.y - floor*T) < 1f
                && overlaps(first, last, door.x, T)) return true;
        if (room.getSign() != null && Math.abs(room.getSign().getY() - floor*T) < 1f
                && overlaps(first, last, room.getSign().getX(), T)) return true;
        for (Unit unit : UnitHelper.getInstance().getUnits()) if (room.getIdentifier().equals(unit.getRoom())
                && Math.abs(unit.floorY - floor*T) < 1f && overlaps(first, last, unit.x, U)) return true;
        return false;
    }

    private static boolean overlaps(int first, int last, float x, float width) {
        return x < (last + 1) * T && x + width > first * T;
    }

    public static void validateContent(Room room) {
        if (room.isBossArena()) return;
        for (Unit unit : UnitHelper.getInstance().getUnits()) {
            if (!room.getIdentifier().equals(unit.getRoom())
                    || !(unit instanceof ItemOnScreen || unit instanceof Interactable || unit instanceof Plant)) continue;
            if (!RoomGeometry.supports(room, unit.x, unit.floorY, U, U)) {
                throw new IllegalStateException("Unsupported content in " + room.getIdentifier() + ": "
                        + unit.getClass().getSimpleName() + " at " + unit.x / T + "," + unit.floorY / T);
            }
        }
    }
}
