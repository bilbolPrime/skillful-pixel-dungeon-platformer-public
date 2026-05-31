package com.bilboldev.skillfulpixeldungeonplatformer.library;

import com.bilboldev.skillfulpixeldungeonplatformer.misc.graphics.GameFilm;

public interface LibraryEntry {
    String getLibraryName();

    String getLibraryDescription();

    GameFilm getLibraryPreview();
}