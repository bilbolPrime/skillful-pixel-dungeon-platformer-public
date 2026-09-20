package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.*;
import com.bilboldev.skillfulpixeldungeonplatformer.levels.rooms.*;


public final class RoomStructure {
    private final String theme;
    private final RoomPiers piers;
    private final RoomLighting lighting = new RoomLighting();
    private final TextureRegion pixel;
    public RoomStructure(String theme) {
        this.theme = theme;
        piers = new RoomPiers(new TextureRegion(TextureHelper.GetSingleton().getTexture("images/tiles/" + theme + "/wall.png")));
        pixel = new TextureRegion(TextureHelper.GetSingleton().getSolidPixel());
    }
    public void draw(Batch batch, Room room) {
        if (!room.getLayout().hasMountedFixtures()) return;
        piers.draw(batch, room.getLayout().piers(), room.getLayout().family());
        float packed = batch.getPackedColor(), r = batch.getColor().r, g = batch.getColor().g, b = batch.getColor().b, a = batch.getColor().a;
        RoomFixtureObservation observation = MapHelper.getInstance().getRoomFixtureObservation();
        int index = 0;
        try {
            for (RoomLayout.Anchor lamp : room.getLayout().fixtureAnchors()) {
                float x = lamp.x, y = lamp.y;
                float lightY = y + RoomLighting.MOUNTED_SOURCE_OFFSET_Y;
                boolean torch = theme.equals("prison"), halls = theme.equals("halls"), city = theme.equals("city");
                boolean green = (index++ & 1) == 0;
                RoomSnapshot.PropKind kind = torch ? RoomSnapshot.PropKind.TORCH : halls
                        ? (green ? RoomSnapshot.PropKind.HALLS_GREEN : RoomSnapshot.PropKind.HALLS_RED)
                        : city ? RoomSnapshot.PropKind.CITY_LAMP : RoomSnapshot.PropKind.LAMP;
                observation.beginLight(kind, pixel, x-16, y-24, 32, 56, x, lightY);
                batch.setColor(r*.13f, g*.17f, b*.18f, a);
                batch.draw(pixel, x-16, y-24, 32, 56);
                observation.focalPiece(pixel, x-16, y-24, 32, 56, .13f, .17f, .18f, 1f);
                batch.setColor(r*.40f, g*.37f, b*.28f, a);
                batch.draw(pixel, x-16, y+24, 32, 8);
                observation.focalPiece(pixel, x-16, y+24, 32, 8, .40f, .37f, .28f, 1f);
                batch.setColor(r*(halls ? (green ? .40f : .78f) : .88f), g*(halls ? (green ? .75f : .28f) : .67f), b*.32f, a);
                batch.draw(pixel, x-8, y-16, 16, torch ? 40 : 32);
                observation.focalPiece(pixel, x-8, y-16, 16, torch ? 40 : 32,
                        halls ? (green ? .40f : .78f) : .88f, halls ? (green ? .75f : .28f) : .67f, .32f, 1f);
                observation.endFocal(true);
                batch.setPackedColor(packed);
                lighting.draw(batch, room, x, lightY, 1.5f*ConstantsHelper.TILE,
                        halls ? (green ? .35f : .85f) : .90f, halls ? (green ? .80f : .25f) : .65f, .30f);
                if (torch) observation.torch(x, lightY);
                else if (halls) observation.hallsLamp(x, lightY, green);
                else if (city) observation.cityLamp(x, lightY);
                else observation.lamp(x, lightY);
            }
        } finally { observation.endFocal(false); batch.setPackedColor(packed); }
    }
}
