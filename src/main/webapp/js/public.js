document.addEventListener('DOMContentLoaded', function() {
    console.log('Public.js geladen');
    
    // Event-Listener für die Länderauswahl
    const landSelect = document.getElementById('land');
    if (landSelect) {
        console.log('Land-Auswahl gefunden');
        landSelect.addEventListener('change', function() {
            const selectedLand = this.value;
            console.log('Land ausgewählt:', selectedLand);
            if (selectedLand) {
                loadLatestDataForCountry(selectedLand);
            }
        });
    } else {
        console.error('Land-Auswahl nicht gefunden!');
    }
});

function loadLatestDataForCountry(land) {
    console.log('Lade Daten für Land:', land);
    fetch(`/api/artikel/latest/${encodeURIComponent(land)}`)
        .then(response => {
            console.log('Server-Antwort:', response.status);
            if (!response.ok) {
                throw new Error('Keine Daten gefunden');
            }
            return response.json();
        })
        .then(data => {
            console.log('Empfangene Daten:', data);
            // Aktualisiere die Anzeige mit den neuesten Daten
            document.getElementById('jahr').value = data.jahr;
            document.getElementById('co2Ausstoss').value = data.co2Ausstoss;
            document.getElementById('einheit').value = data.einheit;
            document.getElementById('beschreibung').value = data.beschreibung;
            console.log('Daten angezeigt');
        })
        .catch(error => {
            console.error('Fehler beim Laden der Daten:', error);
            // Setze alle Felder zurück
            document.getElementById('jahr').value = '';
            document.getElementById('co2Ausstoss').value = '';
            document.getElementById('einheit').value = '';
            document.getElementById('beschreibung').value = '';
        });
} 