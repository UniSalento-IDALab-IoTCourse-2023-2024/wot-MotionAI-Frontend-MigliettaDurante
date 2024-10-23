# Human Activity Recognition mediante SensorTile.box PRO di STMicroelectronics

## Descrizione del progetto
Il progetto Human Activity Recognition (HAR) implementa un sistema di riconoscimento delle attività umane
basato su Edge Computing, con l'uso della SensorTile.box PRO di STMicroelectronics.
Il sistema è in grado di rilevare in tempo reale attività come camminare, correre, fermarsi e guidare
direttamente sulla SensorTile, senza necessità di una connessione continua a un server remoto.

Il sistema sfrutta un modello di Machine Learning basato su un albero decisionale addestrato per riconoscere i
segnali raccolti
dai sensori di movimento (accelerometro, giroscopio) e utilizzare tali dati per classificare le attività.
Le previsioni sono visualizzate in tempo reale tramite un'applicazione Android che permette
un'interazione semplice e immediata con l'utente.
____________________________________________________________________________________________________________________

## Tecnologie utilizzate

- SensorTile.box PRO

### Backend
- STM32CubeProgrammer: È uno strumento per operazioni come flashing del firmware, lettura/scrittura della memoria, aggiornamento del bootloader e configurazione delle opzioni di protezione. Supporta interfacce come JTAG, SWD, UART, USB e SPI, ed è compatibile con dispositivi STM32. È disponibile per Windows, macOS e Linux
- STM32CubeIDE: Ambiente di sviluppo di STMicroelectronics per microcontrollori STM32, basato su Eclipse. Integra funzioni di STM32CubeMX per configurare pinout, clock e periferiche, generando automaticamente il codice di inizializzazione. Supporta sviluppo con GCC e debug con GDB, ed è disponibile per Windows, Linux e macOS.
- Bluetooth Low Energy: La tecnologia Bluetooth Low Energy (BLE) è progettata per applicazioni a basso consumo energetico, come sensori e dispositivi indossabili. Parte della specifica Bluetooth 4.0, è ottimizzata per trasmettere piccoli pacchetti di dati in modo intermittente. Supporta diverse topologie di rete, come punto-punto, a stella e mesh, ed è ideale per dispositivi IoT e applicazioni mediche che richiedono trasmissione dati a basso bitrate e lunga durata della batteria.
- BlueST-SDK: Libreria multipiattaforma (Android, iOS, Python) che facilita l'accesso ai dati da dispositivi BLE utilizzando il protocollo BlueST. Supporta sensori inerziali, ambientali, informazioni sulla batteria e motori, offrendo anche una console seriale su Bluetooth e servizi di configurazione. L'SDK semplifica lo sviluppo di applicazioni su Android, iOS e Linux, con esempi inclusi, e permette di registrare dati in formato CSV. Distribuito con licenza BSD a 3 clausole, è compatibile con l'app ST BlueMS per visualizzare i dati sui dispositivi mobili.

### Software
- Kotlin: Linguaggio sviluppato da JetBrains, compatibile con l'SDK di ST e molto usato in Android. Consente di creare interfacce grafiche tramite XML e programmaticamente, e supporta Jetpack Compose per un approccio dichiarativo. Le sue funzionalità, come le funzioni di ordine superiore, semplificano la gestione dello stato e collegano facilmente gli elementi UI alla logica dell'app.
- Android Studio: L'ambiente di sviluppo di JetBrains per la creazione di applicazioni Android offre strumenti per progettazione, debugging e ottimizzazione. Include un emulatore integrato per testare le app su dispositivi virtuali, senza hardware fisico, ed è compatibile con Kotlin e Java, permettendo agli sviluppatori di scegliere il linguaggio più adatto.

____________________________________________________________________________________________________________________

## Architettura del sistema
Il flusso logico delle operazioni si articola in quattro fasi principali: Acquisition, Processing, Communication e Visualization.

1. Acquisition: I dati vengono raccolti da sensori, come accelerometro e giroscopio, presenti nella SensorTile, che rilevano i movimenti. Questi dati vengono inviati alla fase successiva.
2. Processing: I dati grezzi vengono elaborati tramite il Machine Learning Core della scheda, che applica modelli di machine learning per riconoscere schemi e attività fisiche. L'output è una previsione delle attività.
3. Communication: I risultati elaborati vengono trasmessi via Bluetooth a un dispositivo mobile, usando lo STBlue-SDK per facilitare la connessione e il flusso di dati tra la scheda e l'app sviluppata in Kotlin.
4. Visualization: I risultati del riconoscimento delle attività vengono mostrati all'utente tramite un'interfaccia grafica nell'app mobile, permettendo il monitoraggio in tempo reale delle previsioni e dei dati raccolti.

## Utilizzo del sistema
1. Accendere la SensorTile.box PRO.
2. Avviare l’app Android e selezionare il dispositivo SensorTile tra quelli disponibili.
3. Eseguire le attività fisiche che si desidera classificare (es. camminare, correre, fermarsi, guidare).
4. Osservare le previsioni delle attività direttamente nell'app in tempo reale.


