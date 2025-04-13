package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.List;

@FacesValidator("descriptionValidator")
@ApplicationScoped
public class DescriptionValidator implements Validator<String> {

    private final MyLanguageTool myLangTool;

    @Inject
    public DescriptionValidator() {
        this.myLangTool = new MyLanguageTool();
    }

    @Override
    public void validate(FacesContext ctx, UIComponent cmp, String text) throws ValidatorException {
        // Prüfe ob die Beschreibung nicht leer ist
        if (text == null || text.trim().isEmpty()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Beschreibung erforderlich",
                    "Bitte geben Sie eine Beschreibung oder Quelle an."));
        }

        // Prüfe die maximale Länge
        if (text.length() > 500) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Text zu lang",
                    "Die Beschreibung darf maximal 500 Zeichen lang sein!"));
        }

        // Optional: Grammatikprüfung
        try {
            List<RuleMatch> matches = myLangTool.checkText(text);
            if (matches != null && !matches.isEmpty()) {
                StringBuilder str = new StringBuilder("Grammatikfehler: ");
                for (RuleMatch match : matches) {
                    str.append(match.getFromPos())
                            .append("-")
                            .append(match.getToPos())
                            .append(": ")
                            .append(match.getMessage())
                            .append("\n")
                            .append("Vorgeschlagene Korrektur(en): ")
                            .append(match.getSuggestedReplacements())
                            .append("\n");
                }
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Grammatikfehler", str.toString()));
            }
        } catch (IOException e) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fehler bei der Grammatikprüfung", e.getMessage()));
        }
    }
}

