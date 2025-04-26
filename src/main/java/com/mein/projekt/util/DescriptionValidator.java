package com.mein.projekt.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

@FacesValidator("descriptionValidator")
@ApplicationScoped
public class DescriptionValidator implements Validator<String> {

    @Inject
    public DescriptionValidator() {
    }

    @Override
    public void validate(FacesContext ctx, UIComponent cmp, String text) throws ValidatorException {
        // Prüfe ob der Wissenschaftlername nicht leer ist
        if (text == null || text.trim().isEmpty()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Wissenschaftler erforderlich",
                    "Bitte geben Sie den Namen des Wissenschaftlers an."));
        }

        // Prüfe die maximale Länge
        if (text.length() > 100) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Name zu lang",
                    "Der Name darf maximal 100 Zeichen lang sein."));
        }

        // Prüfe auf ungültige Zeichen
        if (!text.matches("^[\\p{L}\\s.-]+$")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ungültige Zeichen",
                    "Der Name darf nur Buchstaben, Leerzeichen, Punkte und Bindestriche enthalten."));
        }
    }
}

