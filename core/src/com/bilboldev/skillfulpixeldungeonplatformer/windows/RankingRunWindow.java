package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.UtilsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RankingRunWindow extends Window {

    private static final float HEADER_LEFT = 110f;
    private static final float HEADER_TOP = 120f;
    private static final float COLUMN_TOP_OFFSET = 280f;
    private static final float COLUMN_LEFT = 110f;
    private static final float COLUMN_GAP = 120f;
    private static final float COLUMN_RIGHT = 110f;
    private static final float TEXT_LINE_HEIGHT = 55f;
    private static final float BOTTOM_PADDING = 110f;

    private final SaveHelper.RankingRunData rankingRunData;
    private String headerText;
    private String inventoryText;
    private String usedSkillsText;

    public RankingRunWindow(SaveHelper.RankingRunData rankingRunData, float width, float height) {
        super(width, height);
        this.rankingRunData = rankingRunData;
    }

    @Override
    public Window build() {
        String heroClassName = rankingRunData.heroClassDisplayName != null
            ? Messages.maybeTranslate(rankingRunData.heroClassDisplayName)
            : Messages.maybeTranslate("Unknown hero");
        headerText = heroClassName
            + "\n" + Messages.maybeTranslate("Depth reached: %d", rankingRunData.depthReached)
            + "\n" + Messages.maybeTranslate("Fell on %s", formatTimestamp(rankingRunData.endedAtMillis));

        float columnWidth = (width - COLUMN_LEFT - COLUMN_RIGHT - COLUMN_GAP) / 2f;
        inventoryText = UtilsHelper.multiLine(joinLines(rankingRunData.inventorySnapshot, "No inventory recorded."), 3, columnWidth);
        usedSkillsText = UtilsHelper.multiLine(joinLines(rankingRunData.usedSkills, "No active skills used."), 3, columnWidth);

        int inventoryLines = Math.max(1, inventoryText.split("\n").length);
        int skillLines = Math.max(1, usedSkillsText.split("\n").length);
        height = Math.max(height, COLUMN_TOP_OFFSET + Math.max(inventoryLines, skillLines) * TEXT_LINE_HEIGHT + BOTTOM_PADDING);
        super.build();
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        FontHelper.getSingleton().writeWhiteRaw(batch, 4f, x + HEADER_LEFT, y + height - HEADER_TOP, headerText);

        float columnWidth = (width - COLUMN_LEFT - COLUMN_RIGHT - COLUMN_GAP) / 2f;
        float inventoryX = x + COLUMN_LEFT;
        float skillsX = inventoryX + columnWidth + COLUMN_GAP;
        float sectionY = y + height - COLUMN_TOP_OFFSET;

        FontHelper.getSingleton().write(Color.GOLDENROD, batch, 3f, inventoryX, sectionY, Messages.maybeTranslate("INVENTORY SNAPSHOT"));
        FontHelper.getSingleton().writeRaw(Color.LIGHT_GRAY, batch, 3f, inventoryX, sectionY - 70f, inventoryText);

        FontHelper.getSingleton().write(Color.GOLDENROD, batch, 3f, skillsX, sectionY, Messages.maybeTranslate("SKILLS USED"));
        FontHelper.getSingleton().writeRaw(Color.LIGHT_GRAY, batch, 3f, skillsX, sectionY - 70f, usedSkillsText);
    }

    private String joinLines(java.util.ArrayList<String> lines, String fallback) {
        if (lines == null || lines.isEmpty()) {
            return Messages.maybeTranslate(fallback);
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                builder.append('\n');
            }
            builder.append(Messages.maybeTranslate(lines.get(i)));
        }
        return builder.toString();
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp <= 0L) {
            return Messages.maybeTranslate("Unknown");
        }

        return new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.US).format(new Date(timestamp));
    }
}
