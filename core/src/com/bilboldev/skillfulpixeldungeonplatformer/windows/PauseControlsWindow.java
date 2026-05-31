package com.bilboldev.skillfulpixeldungeonplatformer.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.GameSettingsHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.WindowHelper;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.misc.buttons.PauseMenuRowButton;

import java.util.ArrayList;

public class PauseControlsWindow extends Window {

    private static final float MENU_WIDTH = 1260f;
    private static final float BUTTON_WIDTH = 510f;
    private static final float BUTTON_HEIGHT = 100f;
    private static final float BUTTON_GAP = 14f;
    private static final float COLUMN_GAP = 20f;
    private static final float TOP_PADDING = 74f;
    private static final float SIDE_PADDING = 80f;
    private static final float BOTTOM_PADDING = 78f;
    private static final int COLUMN_COUNT = 3;

    private final ArrayList<PauseMenuRowButton> buttons;
    private final boolean showSaveExit;
    private BindingTarget pendingBinding;
    private PauseMenuRowButton pressedButton;
    private boolean ignoreNextTap;

    public PauseControlsWindow() {
        this(true);
    }

    public PauseControlsWindow(boolean showSaveExit) {
        super(MENU_WIDTH, 1000f);
        this.showSaveExit = showSaveExit;
        buttons = new ArrayList<>();
    }

    @Override
    public Window build() {
        BindingTarget[] targets = getDisplayTargets();
        int controlsCount = targets.length;
        int rowCount = (controlsCount + COLUMN_COUNT - 1) / COLUMN_COUNT;
        width = BUTTON_WIDTH * COLUMN_COUNT + COLUMN_GAP * (COLUMN_COUNT - 1) + SIDE_PADDING * 2f;
        height = rowCount * BUTTON_HEIGHT
            + (rowCount - 1) * BUTTON_GAP
            + BUTTON_GAP
            + BUTTON_HEIGHT
            + TOP_PADDING
            + BOTTOM_PADDING;
        super.build();
        buttons.clear();

        float firstButtonY = y + height - TOP_PADDING - BUTTON_HEIGHT;

        for (int index = 0; index < controlsCount; index++) {
            BindingTarget target = targets[index];
            final BindingTarget currentTarget = target;
            float buttonX = x + SIDE_PADDING + (index % COLUMN_COUNT) * (BUTTON_WIDTH + COLUMN_GAP);
            float buttonY = firstButtonY - (index / COLUMN_COUNT) * (BUTTON_HEIGHT + BUTTON_GAP);
            buttons.add(new PauseMenuRowButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT) {
                @Override
                public void clicked() {
                    if (currentTarget == BindingTarget.RESET_DEFAULTS) {
                        GameSettingsHelper.getInstance().resetDefaults();
                        pendingBinding = null;
                    }
                    else {
                        pendingBinding = currentTarget;
                    }
                    updateLabels();
                }
            });
        }

        float resetButtonY = firstButtonY - rowCount * (BUTTON_HEIGHT + BUTTON_GAP);
        buttons.add(new PauseMenuRowButton(x + SIDE_PADDING,
                resetButtonY,
                BUTTON_WIDTH * COLUMN_COUNT + COLUMN_GAP * (COLUMN_COUNT - 1),
                BUTTON_HEIGHT) {
            @Override
            public void clicked() {
                GameSettingsHelper.getInstance().resetDefaults();
                pendingBinding = null;
                updateLabels();
            }
        });

        updateLabels();
        return this;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        for (PauseMenuRowButton button : buttons) {
            button.draw(batch);
        }
    }

    @Override
    public boolean pointerDown(float x, float y, int button) {
        if (pendingBinding != null) {
            applyMouseBinding(button);
            ignoreNextTap = true;
            return true;
        }

        clearPressedButton();
        for (PauseMenuRowButton rowButton : buttons) {
            if (rowButton.isHitProjected(x, y)) {
                pressedButton = rowButton;
                rowButton.pressDown();
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean tap(float x, float y) {
        if (ignoreNextTap) {
            ignoreNextTap = false;
            return true;
        }

        if (pressedButton != null) {
            PauseMenuRowButton tappedButton = pressedButton;
            pressedButton = null;
            tappedButton.releasePress();
            if (tappedButton.isHitProjected(x, y)) {
                tappedButton.click();
                return true;
            }

            tappedButton.cancelPress();
            return true;
        }

        if (x < this.x || x > this.x + this.width || y < this.y || y > this.y + this.height) {
            WindowHelper.getInstance().replaceWindow(new PauseMenuWindow(showSaveExit).build());
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (pendingBinding == null || pendingBinding.mouseBinding) {
            WindowHelper.getInstance().replaceWindow(new PauseMenuWindow(showSaveExit).build());
            return true;
        }

        applyKeyBinding(keycode);
        return true;
    }

    private void applyKeyBinding(int keycode) {
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        switch (pendingBinding) {
            case LEFT:
                settings.setBinding(settings.getMoveLeftBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case RIGHT:
                settings.setBinding(settings.getMoveRightBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case ENTER_DOOR:
                settings.setBinding(settings.getEnterDoorBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case INTERACT:
                settings.setBinding(settings.getInteractBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case HEALTH_POTION:
                settings.setBinding(settings.getHealthPotionBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case MANA_POTION:
                settings.setBinding(settings.getManaPotionBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case EAT_FOOD:
                settings.setBinding(settings.getEatFoodBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case INVENTORY:
                settings.setBinding(settings.getInventoryBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case ATTACK:
                settings.setBinding(settings.getAttackBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case RANGED:
                settings.setBinding(settings.getRangedBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case QUICK_SKILL:
                settings.setBinding(settings.getQuickSkillBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case QUICK_SKILL_2:
                settings.setBinding(settings.getQuickSkill2Binding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case QUICK_SKILL_3:
                settings.setBinding(settings.getQuickSkill3Binding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case QUICK_SKILL_4:
                settings.setBinding(settings.getQuickSkill4Binding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case QUICK_SKILL_5:
                settings.setBinding(settings.getQuickSkill5Binding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case QUICK_SKILL_6:
                settings.setBinding(settings.getQuickSkill6Binding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case QUICK_SKILL_7:
                settings.setBinding(settings.getQuickSkill7Binding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case JUMP:
                settings.setBinding(settings.getJumpBinding(), GameSettingsHelper.INPUT_TYPE_KEY, keycode);
                break;
            case RESET_DEFAULTS:
                break;
            default:
                break;
        }

        pendingBinding = null;
        updateLabels();
    }

    private void applyMouseBinding(int button) {
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        switch (pendingBinding) {
            case LEFT:
                settings.setBinding(settings.getMoveLeftBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case RIGHT:
                settings.setBinding(settings.getMoveRightBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case ENTER_DOOR:
                settings.setBinding(settings.getEnterDoorBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case INTERACT:
                settings.setBinding(settings.getInteractBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case HEALTH_POTION:
                settings.setBinding(settings.getHealthPotionBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case MANA_POTION:
                settings.setBinding(settings.getManaPotionBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case EAT_FOOD:
                settings.setBinding(settings.getEatFoodBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case INVENTORY:
                settings.setBinding(settings.getInventoryBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case ATTACK:
                settings.setBinding(settings.getAttackBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case RANGED:
                settings.setBinding(settings.getRangedBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case QUICK_SKILL:
                settings.setBinding(settings.getQuickSkillBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case QUICK_SKILL_2:
                settings.setBinding(settings.getQuickSkill2Binding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case QUICK_SKILL_3:
                settings.setBinding(settings.getQuickSkill3Binding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case QUICK_SKILL_4:
                settings.setBinding(settings.getQuickSkill4Binding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case QUICK_SKILL_5:
                settings.setBinding(settings.getQuickSkill5Binding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case QUICK_SKILL_6:
                settings.setBinding(settings.getQuickSkill6Binding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case QUICK_SKILL_7:
                settings.setBinding(settings.getQuickSkill7Binding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case JUMP:
                settings.setBinding(settings.getJumpBinding(), GameSettingsHelper.INPUT_TYPE_MOUSE, button);
                break;
            case RESET_DEFAULTS:
                break;
        }

        pendingBinding = null;
        updateLabels();
    }

    private void updateLabels() {
        GameSettingsHelper settings = GameSettingsHelper.getInstance();
        BindingTarget[] targets = getDisplayTargets();
        for (int index = 0; index < targets.length; index++) {
            BindingTarget target = targets[index];
            boolean waiting = pendingBinding == target;
            buttons.get(index).setBindingRow(target.localizedLabel(), waiting ? target.waitingLabel() : target.currentLabel(settings), waiting);
        }

        buttons.get(targets.length).setCenteredText(BindingTarget.RESET_DEFAULTS.localizedLabel());
    }

    private BindingTarget[] getDisplayTargets() {
        return new BindingTarget[]{
                BindingTarget.LEFT,
                BindingTarget.ATTACK,
                BindingTarget.QUICK_SKILL,
                BindingTarget.RIGHT,
                BindingTarget.RANGED,
                BindingTarget.QUICK_SKILL_2,
                BindingTarget.JUMP,
                BindingTarget.EAT_FOOD,
                BindingTarget.QUICK_SKILL_3,
                BindingTarget.ENTER_DOOR,
                BindingTarget.HEALTH_POTION,
                BindingTarget.QUICK_SKILL_4,
                BindingTarget.INTERACT,
                BindingTarget.MANA_POTION,
                BindingTarget.QUICK_SKILL_5,
                BindingTarget.INVENTORY,
                BindingTarget.QUICK_SKILL_6,
                BindingTarget.QUICK_SKILL_7
        };
    }

    private enum BindingTarget {
        LEFT("Left", "windows.wndkeybindings.w", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getMoveLeftBinding());
            }
        },
        RIGHT("Right", "windows.wndkeybindings.e", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getMoveRightBinding());
            }
        },
        ENTER_DOOR("Enter Door", "windows.wndkeybindings.enter_door", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getEnterDoorBinding());
            }
        },
        INTERACT("Interact", "windows.wndkeybindings.tag_action", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getInteractBinding());
            }
        },
        HEALTH_POTION("Health Potion", "items.potions.potionofhealing.name", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getHealthPotionBinding());
            }
        },
        MANA_POTION("Mana Potion", "custom.generated.potion_of_mana_7278278513", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getManaPotionBinding());
            }
        },
        EAT_FOOD("Eat Food", "items.food.food.name", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getEatFoodBinding());
            }
        },
        INVENTORY("Inventory", "windows.wndkeybindings.inventory", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getInventoryBinding());
            }
        },
        ATTACK("Attack", "scenes.gamescene.attack", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getAttackBinding());
            }
        },
        RANGED("Ranged", "windows.wndkeybindings.ranged", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getRangedBinding());
            }
        },
        QUICK_SKILL("Quick Skill", "windows.wndkeybindings.quickslot_1", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getQuickSkillBinding());
            }
        },
        QUICK_SKILL_2("Quick Spell 2", "windows.wndkeybindings.quickslot_2", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getQuickSkill2Binding());
            }
        },
        QUICK_SKILL_3("Quick Spell 3", "windows.wndkeybindings.quickslot_3", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getQuickSkill3Binding());
            }
        },
        QUICK_SKILL_4("Quick Spell 4", "windows.wndkeybindings.quickslot_4", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getQuickSkill4Binding());
            }
        },
        QUICK_SKILL_5("Quick Spell 5", "windows.wndkeybindings.quickslot_5", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getQuickSkill5Binding());
            }
        },
        QUICK_SKILL_6("Quick Spell 6", "windows.wndkeybindings.quickslot_6", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getQuickSkill6Binding());
            }
        },
        QUICK_SKILL_7("Quick Spell 7", "windows.wndkeybindings.quickslot_7", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getQuickSkill7Binding());
            }
        },
        JUMP("Jump", "windows.wndkeybindings.jump", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return settings.bindingLabel(settings.getJumpBinding());
            }
        },
        RESET_DEFAULTS("Reset Defaults", "windows.wndkeybindings.default", false) {
            @Override
            String currentLabel(GameSettingsHelper settings) {
                return "RESET";
            }
        };

        private final String label;
        private final String messageKey;
        private final boolean mouseBinding;

        BindingTarget(String label, boolean mouseBinding) {
            this(label, null, mouseBinding);
        }

        BindingTarget(String label, String messageKey, boolean mouseBinding) {
            this.label = label;
            this.messageKey = messageKey;
            this.mouseBinding = mouseBinding;
        }

        abstract String currentLabel(GameSettingsHelper settings);

        String localizedLabel() {
            return messageKey == null ? Messages.maybeTranslate(label) : Messages.get(messageKey);
        }

        String waitingLabel() {
            return Messages.get("windows.wndkeybindings.press_or_click");
        }
    }

    private void clearPressedButton() {
        if (pressedButton != null) {
            pressedButton.cancelPress();
            pressedButton = null;
        }
    }
}