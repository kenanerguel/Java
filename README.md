# Like Hero To Zero

**Web-App für CO₂-Emissionsdaten**  
_Öffentliches Frontend & geschütztes Backend für Wissenschaftler:innen_

---

## Inhalt

- [Überblick](#überblick)
- [Features](#features)
- [Tech-Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Architektur](#architektur)
- [Screenshots](#screenshots)
- [Datenquelle](#datenquelle)
- [Contributing](#contributing)
- [Lizenz](#lizenz)

---

## Überblick

Dieses Projekt ist eine Webanwendung für das Nachhaltigkeitsprojekt **„Like Hero To Zero“**.  
Ziel:  
- **Öffentlich**: Bürger:innen können aktuelle CO₂-Emissionsdaten ihres Landes einsehen (ohne Login).
- **Backend**: Wissenschaftler:innen können neue Daten einpflegen oder Fehler korrigieren (mit Login).

Die Anwendung wurde im Rahmen eines agilen Projekts für eine PR-Agentur und verschiedene Naturverbände entwickelt.

---

## Features

- **CO₂-Daten-Portal**:  
  - Auswahl von Ländern, Anzeige der aktuellsten CO₂-Emissionsdaten.
  - Responsive UI, optimiert für Desktop und Mobile.
- **Backend für Wissenschaftler:innen**:  
  - Login-geschützt.
  - Neue Datensätze anlegen, bestehende korrigieren.
  - (Optional) Freigabeprozess für neue/editiere Daten (MoSCoW: COULD).
- **Persistente Speicherung**:  
  - MySQL-Datenbank, JPA/Hibernate.
- **Moderne UI**:  
  - JSF, PrimeFaces, CDI/Beans.

---

## Tech-Stack

- **Frontend**: JSF (Jakarta Faces), PrimeFaces, HTML5, CSS3
- **Backend**: Java 17, CDI/Beans, JPA (Hibernate)
- **Datenbank**: MySQL 8.x
- **Build/Deploy**: Maven, Tomcat 11.x

---

## Getting Started

### Voraussetzungen

- Java 17+
- Maven 3.8+
- MySQL 8.x (lokal oder Docker)
- Tomcat 11.x

### Setup

1. **Repo klonen**
   ```bash
   git clone https://github.com/dein-username/like-hero-to-zero.git
   cd like-hero-to-zero
   ```

2. **Datenbank einrichten**
   - MySQL-User & DB anlegen (siehe `src/main/resources/init_database.sql`)
   - Beispiel (Docker):
     ```bash
     docker run --name like_hero_mysql -e MYSQL_ROOT_PASSWORD=deinpass -e MYSQL_DATABASE=co2db -p 3306:3306 -d mysql:8.0
     ```

3. **Konfiguration prüfen**
   - DB-URL, User, Passwort in `src/main/resources/META-INF/persistence.xml` und ggf. in `context.xml` anpassen.

4. **Build & Deploy**
   ```bash
   mvn clean package
   ```
   - WAR-File nach Tomcat `webapps/` kopieren oder direkt aus der IDE deployen.

5. **Starten**
   - Im Browser: [http://localhost:8080/like-hero-to-zero/](http://localhost:8080/like-hero-to-zero/)

---

## Architektur

- **Frontend**:  
  - Öffentliche Seite (`index.xhtml`): CO₂-Datenanzeige
  - Login/Backend (`login.xhtml`, `backoffice.xhtml`): Datenpflege für Wissenschaftler:innen
- **Backend**:  
  - JPA-Entities: User, Artikel (CO₂-Datensatz), Country
  - Beans/Controller: Trennung von Logik und View
- **Persistenz**:  
  - MySQL, Hibernate/JPA, automatische Schema-Generierung

**UML-Strukturdiagramm**:  
*(Hier Screenshot oder Diagramm einfügen)*

---

## Screenshots

### XAMPP Control Panel (MySQL & Apache)
![XAMPP Control Panel](Screenshot/xampp-controlpanel.png)

### Tomcat Run/Debug Konfiguration in IntelliJ IDEA
![Tomcat Run/Debug Konfiguration](Screenshot/tomcat-runconfig.png)

*(Weitere Beispiel-Screenshots der Startseite, Login, Backend können hier ergänzt werden)*

---

## Datenquelle

- [World Bank Open Data – CO2 Emissions (kt)](https://aws.amazon.com/marketplace/pp/prodview-qf3r4b6jpivte#usage)

---

## Contributing

- Forke das Repo
- Feature-Branch erstellen (`git checkout -b feature/DeinFeature`)
- Änderungen commiten (`git commit -m 'Beschreibung'`)
- Pull Request stellen

---

## Lizenz

MIT License  
(c) 2025 Like Hero To Zero Team

---
