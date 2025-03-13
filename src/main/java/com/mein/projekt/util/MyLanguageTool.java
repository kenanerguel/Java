package com.mein.projekt.util;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.List;

public class MyLanguageTool {
    private final JLanguageTool langTool;

    public MyLanguageTool() {
        try {
            // Initialisiere LanguageTool für Deutsch
            Language language = Languages.getLanguageForShortCode("de-DE");
            langTool = new JLanguageTool(language);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Initialisieren von LanguageTool: " + e.getMessage(), e);
        }
    }

    /**
     * Prüft den übergebenen Text und liefert eine Liste von RuleMatch-Objekten zurück,
     * die die gefundenen Grammatik- oder Stilprobleme repräsentieren.
     *
     * @param text der zu prüfende Text
     * @return Liste der gefundenen Probleme
     * @throws IOException wenn ein Fehler beim Überprüfen auftritt
     */
    public List<RuleMatch> checkText(String text) throws IOException {
        return langTool.check(text);
    }
}

