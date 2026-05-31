package com.bilboldev.skillfulpixeldungeonplatformer.platform;

public interface WindowModeService {

    WindowModeService UNSUPPORTED = new WindowModeService() {
        @Override
        public boolean isSupported() {
            return false;
        }

        @Override
        public boolean supportsBorderlessWindowedMode() {
            return false;
        }

        @Override
        public boolean isWindowedModeEnabled() {
            return false;
        }

        @Override
        public boolean isBorderlessWindowedModeEnabled() {
            return false;
        }

        @Override
        public void setWindowedModeEnabled(boolean enabled) {
        }

        @Override
        public void setBorderlessWindowedModeEnabled(boolean enabled) {
        }

        @Override
        public void rememberWindowSize(int width, int height) {
        }
    };

    static WindowModeService unsupported() {
        return UNSUPPORTED;
    }

    boolean isSupported();

    boolean supportsBorderlessWindowedMode();

    boolean isWindowedModeEnabled();

    boolean isBorderlessWindowedModeEnabled();

    void setWindowedModeEnabled(boolean enabled);

    void setBorderlessWindowedModeEnabled(boolean enabled);

    void rememberWindowSize(int width, int height);
}