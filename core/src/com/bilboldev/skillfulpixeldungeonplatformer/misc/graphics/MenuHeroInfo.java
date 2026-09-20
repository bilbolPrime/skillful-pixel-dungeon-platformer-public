package com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.BuildHeroHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.FontHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.HeroMenuContent;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.ActionButton;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.Button;
import com.bilboldev.skillfulpixeldungeonplatformer.units.classes.HeroClass;
import java.util.ArrayList;
import java.util.List;


public final class MenuHeroInfo {
    private static final Color INK = DesktopMenuStyle.INK, GOLD = DesktopMenuStyle.GOLD;
    private static final Color DARK_INK = DesktopMenuStyle.VIOLET;
    private static final float LEFT = 64, WIDTH = 544;
    private final ArrayList<ActionButton> controls = new ArrayList<>();
    private final ArrayList<DetailButton> details = new ArrayList<>();
    private final ArrayList<SkillTree> trees = new ArrayList<>();
    private final MenuUiFade fade = new MenuUiFade();
    private final Matrix4 previous = new Matrix4(), shifted = new Matrix4();
    private final Runnable controlsChanged;
    private final InfoButton play;
    private HeroMenuContent content;
    private FontHelper.FittedTextBlock heading, summary;
    private float reveal;
    private Button selectedControl;
    private final Vector3 tooltipAnchor = new Vector3();
    private final Rectangle tooltipBounds = new Rectangle();
    private HeroMenuContent.Entry tooltipEntry;

    public MenuHeroInfo(Runnable controlsChanged) {
        this.controlsChanged = controlsChanged;
        play = new InfoButton(96, 96, 480, 80, Messages.maybeTranslate("PLAY")) {
            @Override public void click() { if (canClick()) BuildHeroHelper.getSingleton().play(content.heroClass); }
            @Override public void draw(Batch batch) {
                Color edge = canClick() ? GOLD : Color.GRAY;
                DesktopMenuStyle.card(batch, x, y, getWidth(), getHeight(), edge,
                        canClick() && (selectedControl == this || isShowingPressFeedback()));
                DesktopMenuStyle.hover(batch, this, edge);
                FontHelper.getSingleton().writeRaw(edge, batch, label.size,
                        x + (getWidth() - label.width) / 2, y + (getHeight() + label.height) / 2, label.text);
            }
        };
        controls.add(play);
    }

    public List<ActionButton> controls() { return controls; }
    public HeroClass heroClass() { return content == null ? null : content.heroClass; }
    public HeroMenuContent content() { return content; }
    public boolean ready() { return content != null && reveal >= 1f; }

    public void rebindLanguage() {
        play.fitLabel(Messages.maybeTranslate("PLAY"));
        if (content == null) return;
        float previousReveal = reveal;
        show(content.heroClass);
        reveal = previousReveal;
    }

    public void show(HeroClass heroClass) {
        content = heroClass == null ? null : new HeroMenuContent(heroClass);
        details.clear(); trees.clear(); controls.clear(); controls.add(play); reveal = 0;
        tooltipEntry = null;
        if (content != null) {
            heading = FontHelper.getSingleton().fitLabelToBounds(heroClass.getName(), 4.2f, WIDTH, 60);
            summary = FontHelper.getSingleton().fitLabelToBounds(heroClass.getDescription(), 3.4f, WIDTH, 248);

            for (int i = 0; i < 2; i++)
                addDetail(new DetailButton(LEFT + i * 280, 712, 264, 72, content.stats.get(i), false));
            addEquipment(content.equipment);
            trees.add(new SkillTree(false, 1080, content.skills));
            trees.add(new SkillTree(true, 584, content.darkSkills));
        }
        if (controlsChanged != null) controlsChanged.run();
    }

    private void addDetail(DetailButton detail) { details.add(detail); controls.add(detail); }
    private void addEquipment(List<HeroMenuContent.Entry> entries) {
        float width = (WIDTH - Math.max(0, entries.size() - 1) * 12) / Math.max(1, entries.size());
        for (int i = 0; i < entries.size(); i++)
            addDetail(new DetailButton(LEFT + i * (width + 12), 340, width, 100, entries.get(i), true));
    }

    public void act(float delta) {
        if (content == null || Float.isNaN(delta) || Float.isInfinite(delta) || delta <= 0) return;
        reveal = Math.min(1, reveal + Math.min(0.1f, delta) / 0.18f);
    }

    public void draw(Batch batch, float parentOpacity, Button selected) {
        selectedControl = selected;
        tooltipEntry = null;
        tooltipBounds.set(0, 0, 0, 0);

        play.draw(batch);
        if (content == null) return;
        fade.begin(batch, parentOpacity * reveal);
        float packed = batch.getPackedColor();
        previous.set(batch.getTransformMatrix());
        shifted.set(previous).translate(0, (1f - reveal) * 16f, 0);
        batch.setTransformMatrix(shifted);
        try {
            DesktopMenuStyle.shade(batch, 24, 688, 624, 480, 48, .8f);
            DesktopMenuStyle.rule(batch, LEFT, 1144, 160, GOLD);
            FontHelper.getSingleton().writeRaw(GOLD, batch, heading.size, LEFT, 1116, heading.text);
            FontHelper.getSingleton().writeRaw(INK, batch, summary.size, LEFT, 1056, summary.text);

            DesktopMenuStyle.shade(batch, 24, 316, 624, 148, 32, .72f);
            for (DetailButton detail : details) detail.draw(batch);
            for (SkillTree tree : trees) tree.draw(batch);
            batch.setTransformMatrix(previous);
            if (ready()) for (DetailButton detail : details)
                if (detail.iconOnly && DesktopMenuStyle.hovered(batch, detail)) { drawTooltip(batch, detail); break; }
        } finally {
            batch.setTransformMatrix(previous);
            batch.setPackedColor(packed);
            fade.end(batch);
        }
    }

    public void dispose() { fade.dispose(); }

    private void drawTooltip(Batch batch, DetailButton detail) {
        OrthographicCamera camera = GameHelper.GetSingleton().getUICamera();
        tooltipAnchor.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tooltipAnchor);
        float width = Math.max(detail.caption.width, detail.tooltipBody.width) + 40;
        float height = detail.caption.height + detail.tooltipBody.height + 56;
        float left = camera.position.x - camera.viewportWidth / 2, bottom = camera.position.y - camera.viewportHeight / 2;

        float x = MathUtils.clamp(tooltipAnchor.x + 28, left + 16, left + camera.viewportWidth - width - 16);
        float y = MathUtils.clamp(tooltipAnchor.y + 24, bottom + 16, bottom + camera.viewportHeight - height - 16);
        tooltipBounds.set(x, y, width, height); tooltipEntry = detail.entry;
        DesktopMenuStyle.shade(batch, x - 16, y - 16, width + 32, height + 32, 24, .7f);
        DesktopMenuStyle.card(batch, x, y, width, height, GOLD, false);
        FontHelper.getSingleton().writeRaw(GOLD, batch, detail.caption.size, x + 20, y + height - 20, detail.caption.text);
        FontHelper.getSingleton().writeRaw(INK, batch, detail.tooltipBody.size,
                x + 20, y + height - 36 - detail.caption.height, detail.tooltipBody.text);
    }

    private class InfoButton extends ActionButton {
        FontHelper.FittedTextBlock label;
        InfoButton(float x, float y, float width, float height, String label) {
            super(x, y, width, height, "images/misc/black.png", "images/misc/black.png");
            fitLabel(label); enableUiPressFeedback();
        }
        void fitLabel(String text) {
            label = FontHelper.getSingleton().fitLabelToBounds(text, 2.6f, getWidth() - 24, getHeight() - 16);
        }
        @Override public boolean canClick() { return ready(); }
        @Override public boolean isHitProjected(float x, float y) {
            return canClick() && x >= this.x && x <= this.x + getWidth() && y >= this.y && y <= this.y + getHeight();
        }
    }

    private final class DetailButton extends InfoButton {
        final HeroMenuContent owner = content;
        final HeroMenuContent.Entry entry;
        final GameSprite icon;
        final boolean iconOnly;
        final FontHelper.FittedTextBlock caption, tooltipBody;
        Color accent = DesktopMenuStyle.EDGE;
        DetailButton(float x, float y, float width, float height, HeroMenuContent.Entry entry, boolean iconOnly) {
            super(x, y, width, height, "");
            this.entry = entry; this.iconOnly = iconOnly;
            float size = Math.min(iconOnly ? 84 : 60, Math.min(width - 12, height - 16));
            icon = new GameSprite(entry.icon, size, size);
            if (!iconOnly) {
                FontHelper fonts = FontHelper.getSingleton();
                float labelWidth = width - size - 28, labelSize = 2.6f;

                if (entry.skillId >= 0 && Messages.lang() == Languages.ENGLISH)
                    for (String word : entry.label.split("\\s+"))
                        labelSize = fonts.fitSize(word, labelSize, labelWidth, height - 12);
                label = fonts.fitLabelToBounds(entry.label, labelSize, labelWidth, height - 12);
            }
            caption = iconOnly ? FontHelper.getSingleton().fitLabelToBounds(entry.label, 3f, 680, 72) : null;
            tooltipBody = iconOnly ? FontHelper.getSingleton().fitLabelToBounds(entry.description, 2.8f, 680, 300) : null;
        }
        @Override public boolean canClick() { return ready() && owner == content; }
        @Override public void click() { if (canClick()) BuildHeroHelper.getSingleton().showDetail(entry.icon, entry.description); }
        @Override public void draw(Batch batch) {
            DesktopMenuStyle.card(batch, x, y, getWidth(), getHeight(), accent,
                    selectedControl == this || isShowingPressFeedback());
            DesktopMenuStyle.hover(batch, this, accent);
            icon.setPosition(x + (iconOnly ? (getWidth() - icon.getWidth()) / 2 : 8), y + (getHeight() - icon.getHeight()) / 2);
            icon.draw(batch);
            if (!iconOnly) FontHelper.getSingleton().writeRaw(INK, batch, label.size,
                    x + icon.getWidth() + 20, y + (getHeight() + label.height) / 2, label.text);
        }
    }


    private final class SkillTree {
        static final float X = 688, WIDTH = 1748, HEIGHT = 464;
        static final float FIRST_X = 100, LAST_RIGHT = 1850, FIRST_TOP = -380, LAST_BOTTOM = -855;
        final boolean dark;
        final float top, scaleX, scaleY, originX, originY;
        float bottom;
        final ArrayList<DetailButton> nodes = new ArrayList<>();
        final Matrix4 saved = new Matrix4(), transformed = new Matrix4();
        final GameSprite icon;
        SkillTree(boolean dark, float top, List<HeroMenuContent.Entry> entries) {
            this.dark = dark; this.top = top;
            icon = new GameSprite(dark ? "images/skills/dark-skills.png" : "images/skills/light-skills.png", 48, 48);
            scaleX = (WIDTH - 56) / (LAST_RIGHT - FIRST_X);
            scaleY = (HEIGHT - 96) / (FIRST_TOP - LAST_BOTTOM);
            originX = X + 28 - FIRST_X * scaleX;
            originY = top - 80 - FIRST_TOP * scaleY;
            bottom = top - 80;
            for (HeroMenuContent.Entry entry : entries) {
                DetailButton node = new DetailButton(originX + content.heroClass.getSkillButtonXOffset(entry.skillId, dark) * scaleX,
                        originY + content.heroClass.getSkillButtonYOffset(entry.skillId, dark) * scaleY,
                        400 * scaleX, 100 * scaleY, entry, false);
                node.accent = dark ? DARK_INK : GOLD;
                bottom = Math.min(bottom, node.y);
                nodes.add(node); controls.add(node);
            }
        }
        void draw(Batch batch) {
            Color accent = dark ? DARK_INK : GOLD;
            DesktopMenuStyle.shade(batch, X - 16, bottom - 32, WIDTH + 32, top - bottom + 48, 56, .52f);
            DesktopMenuStyle.card(batch, X + 24, top - 64, 56, 56, accent, false);
            icon.setPosition(X + 28, top - 60); icon.draw(batch);
            DesktopMenuStyle.rule(batch, X + 100, top - 36, 228, accent);
            saved.set(batch.getTransformMatrix());
            transformed.set(saved).translate(originX, originY, 0).scale(scaleX, scaleY, 1);
            batch.setTransformMatrix(transformed);
            float packed = batch.getPackedColor();
            int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
            int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
            try {

                batch.setColor(accent);
                batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ZERO, GL20.GL_ONE);
                if (dark) content.heroClass.drawDarkSkillsBranches(batch, 0, 0, 2000, 0);
                else content.heroClass.drawBranches(batch, 0, 0, 2000, 0);
            } finally {
                batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
                batch.setTransformMatrix(saved); batch.setPackedColor(packed);
            }
            for (DetailButton node : nodes) node.draw(batch);
        }
    }
}
