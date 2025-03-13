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
        // Kriterium 1: Der Text soll mit einem gültigen Satzzeichen enden.
        if (!(text.endsWith(".") || text.endsWith("!") || text.endsWith("?"))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ungültige Interpunktion",
                    "Bitte beenden Sie Ihren Satz mit einem Punkt, Ausrufezeichen oder Fragezeichen!"));
        }

        // Kriterium 2: Der Text muss zwischen 100 und 300 Zeichen lang sein.
        if (text.length() < 100) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Text zu kurz",
                    "Die Beschreibung muss mindestens 100 Zeichen lang sein!"));
        }
        if (text.length() > 300) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Text zu lang",
                    "Die Beschreibung darf maximal 300 Zeichen lang sein!"));
        }

        // Kriterium 3: Grammatikprüfung mittels MyLanguageTool (Wrapper für LanguageTool)
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

