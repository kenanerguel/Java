document.addEventListener('DOMContentLoaded', function() {
    console.log('Admin.js geladen');
    
    const form = document.getElementById('co2Form');
    if (form) {
        console.log('Formular gefunden');
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const land = document.getElementById('land').value;
            const jahr = document.getElementById('jahr').value;
            const co2Ausstoss = document.getElementById('co2Ausstoss').value;
            const einheit = document.getElementById('einheit').value;
            const beschreibung = document.getElementById('beschreibung').value;
            
            console.log('Formulardaten:', { land, jahr, co2Ausstoss, einheit, beschreibung });
            
            if (!land || !jahr || !co2Ausstoss || !einheit) {
                alert('Bitte füllen Sie alle erforderlichen Felder aus.');
                return;
            }
            
            // Erstelle ein Objekt mit den zu aktualisierenden Daten
            const updatedData = {
                co2Ausstoss: parseFloat(co2Ausstoss),
                einheit: einheit,
                beschreibung: beschreibung
            };
            
            console.log('Zu sendende Daten:', updatedData);
            
            // Sende die Daten an den Server
            fetch(`/api/artikel/update/${encodeURIComponent(land)}/${jahr}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updatedData)
            })
            .then(response => {
                console.log('Server-Antwort:', response.status);
                if (!response.ok) {
                    throw new Error('Netzwerkantwort war nicht ok');
                }
                return response.json();
            })
            .then(data => {
                console.log('Empfangene Daten:', data);
                alert('CO₂-Ausstoß erfolgreich aktualisiert!');
                // Optional: Formular zurücksetzen oder andere Aktionen
            })
            .catch(error => {
                console.error('Fehler:', error);
                alert('Fehler beim Aktualisieren des CO₂-Ausstoßes');
            });
        });
    } else {
        console.error('Formular nicht gefunden!');
    }
}); 