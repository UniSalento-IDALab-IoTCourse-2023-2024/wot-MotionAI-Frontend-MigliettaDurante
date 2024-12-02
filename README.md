# Human Activity Recognition mediante SensorTile.box PRO di STMicroelectronics

## Descrizione del progetto
Il progetto Human Activity Recognition (HAR) implementa un sistema di riconoscimento delle attività umane
basato su Edge Computing, con l'uso della SensorTile.box PRO di STMicroelectronics.
Il sistema è in grado di rilevare in tempo reale attività come camminare, correre, fermarsi e guidare effettuando l'inferenza
direttamente sulla SensorTile.box PRO e/o sullo smartphone sul quale si sta eseguendo l'applicazione in questione.

Il sistema di activity recognition su smartphone sfrutta un modello di Machine Learning di tipo Random Forest addestrato per riconoscere i
segnali raccolti dall'accelerometro presente sulla SensorTile.box PRO e utilizzare tali dati per classificare le attività.
I riconoscimenti sono visualizzati in tempo reale tramite questa applicazione Android che permette un'interazione semplice e immediata con l'utente.

## Tecnologie utilizzate

- **SensorTile.box PRO:** La scheda di STMicroelectronics è progettata per prototipi di monitoraggio ambientale e 
rilevamento dei movimenti. Integra sensori come accelerometro, giroscopio, magnetometro, sensori di temperatura, 
pressione e un microfono digitale. Dispone di un microcontrollore per l'elaborazione e supporta 
Bluetooth Low Energy (BLE) e NFC. È compatibile con l'SDK BlueST-SDK disponibile alla repository: [BlueSTSDK_Android](https://github.com/STMicroelectronics/BlueSTSDK_Android)

## Funzionalità dell'applicazione
L'applicazione Android permette di:
- Connettersi alla SensorTile.box PRO tramite Bluetooth Low Energy.
- Visualizzare i dati grezzi dell'accelerometro, del magnetometro e del giroscopio in tempo reale.
- Visualizzare le previsioni delle attività umane riconosciute dalla SensorTile.box PRO in tempo reale.
- Effettuare inferenza sui dati dell'accelerometro tramite un modello di Random Forest pre-addestrato eseguendolo direttamente sullo smartphone.

## Funzionalità tramite l'SDK MotionAI-Backend
Tramite l'SDK MotionAI-Backend disponibile alla seguente repository: [wot-MotionAI-Backend-MigliettaDurante](https://github.com/UniSalento-IDALab-IoTCourse-2023-2024/wot-MotionAI-Backend-MigliettaDurante), 
l'applicazione è dotata di diverse funzionalità aggiuntive:
- Registrazione e autenticazione degli utenti.
- Memorizzazione dei riconoscimenti delle attività umane con relative durate in un database remoto.
- Visualizzazione di uno storico delle attività riconosciute nelle giornate precedenti con relative durate.
- Visualizzazione di statistiche relative alle attività riconosciute sottoforma di stime settimanali.
- Possibilità di eliminare i propri dati memorizzati nel database.

## Utilizzo del sistema
1. Avviare l'app Android.
2. Effettuare il login o registrarsi.
3. Visualizzare l'attività giornaliera tramite la dashboard oppure collegarsi alla SensorTile.box PRO.
4. Eseguire le attività fisiche che si desidera classificare (es. camminare, correre, fermarsi, guidare).
5. Osservare i riconoscimenti delle attività direttamente nell'app in tempo reale.

## Link Sito Web GitHub Page: [wot-MotionAI-Presentation-MigliettaDurante](https://unisalento-idalab-iotcourse-2023-2024.github.io/wot-MotionAI-Presentation-MigliettaDurante/)


