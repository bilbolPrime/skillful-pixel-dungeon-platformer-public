package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.party.PartySession;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import java.util.Map;

public final class PartyLobbyWindow extends ChoiceDialogWindow {
    private String lastStatus;
    private float refreshDelay;
    private final int revision, page, pageSize;
    public PartyLobbyWindow() { this(0); }
    private PartyLobbyWindow(int page) {
        super("images/weapons/sword.png", PartySession.status(), 1500, 500);
        pageSize = 4;
        this.page = Math.max(0, Math.min(page, Math.max(0, (PartySession.hosts().size() - 1) / pageSize)));
        revision = PartySession.lobbyRevision();
        lastStatus = PartySession.status();
        if (PartySession.hosting()) {
            addChoice(Messages.get("party.start"), PartySession::start, PartySession.connected());

        } else if (!PartySession.connected()) {
            int index = 0;
            for (Map.Entry<String, Integer> host : PartySession.hosts().entrySet()) {
                if (index++ / pageSize != this.page) continue;
                addChoice(host.getKey(), () -> PartySession.chooseHost(host.getKey()));
            }
            if (this.page > 0) addChoice(Messages.get("party.previous"), () -> WindowHelper.getInstance().addWindow(new PartyLobbyWindow(this.page - 1).build()));
            if ((this.page + 1) * pageSize < PartySession.hosts().size()) addChoice(Messages.get("party.next"), () -> WindowHelper.getInstance().addWindow(new PartyLobbyWindow(this.page + 1).build()));
            addChoice(Messages.get("party.manual"), PartySession::enterAddress);
        }
        addChoice(Messages.get("party.cancel"), PartySession::cancel);
    }
    @Override public void draw(Batch batch) {
        super.draw(batch);
        refreshDelay += com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        if (refreshDelay > 1f && WindowHelper.getInstance().topWindow() == this && !PartySession.running()) {
            refreshDelay = 0;
            if (!lastStatus.equals(PartySession.status()) || revision != PartySession.lobbyRevision())
                com.badlogic.gdx.Gdx.app.postRunnable(() -> {
                    if (WindowHelper.getInstance().topWindow() == this) WindowHelper.getInstance().replaceWindow(new PartyLobbyWindow(page).build());
                });
        }
    }
    @Override public boolean click(float x, float y) {
        if (x < this.x || x > this.x + width || y < this.y || y > this.y + height) { PartySession.cancel(); return true; }
        return super.click(x, y);
    }
    @Override public boolean keyDown(int key) {
        if (key == com.badlogic.gdx.Input.Keys.ESCAPE || key == com.badlogic.gdx.Input.Keys.BACK || com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper.getInstance().getInteractBinding().matchesKey(key)) { PartySession.cancel(); return true; }
        return super.keyDown(key);
    }
}
