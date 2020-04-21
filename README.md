# GasSMan - Administration Interface (Development mode)
## Technical Scope
GAS (Gruppo di Acquisto Solidale) Sales Management è un sistema Java basato su un'architettura MicroServices che alimenta due interfacce attraverso dei servizi REST : un bot di Telegram e un'interfaccia di amministrazione.
GasSMan ha come scopo quello di fornire ad un amminstratore di un GAS un'interfaccia per configurare la lista dei prodotti in distribuzione, e un bot grazie al quale gli inscritti possono consultare l'elenco dei prodotti disponibili ed effettuare gli ordini.

## Didactical Scope
Questo repository è un modulo di un progetto Java di esempio descritto nel libro (scritto in lingua italiana) **"Anche i microservizi nel loro piccolo s'incazzano - Guida alla comprensione dei principi di un'architettura in microservizi"** di [Giuseppe Vincenzi](https://gvincenzi.tumblr.com/).

## Technologies
Technologies used in this module of the project:
- Spring Boot
- Vaadin
- NodeJS
- Netflix Eureka Server
- Open Feign
- RabbitMQ
- Lombok
- Maven

## Installation
### Property file
Prima di lanciare il modulo, bisognerà creare il proprio file di properties, partendo dal file di default "application.yml" e chiamandolo "application-gassman-[NOME_DEL_PROFILO]" scegliendo un [NOME_DEL_PROFILO] a vostra scelta.
Nel file di properties troverete, tra le altre prooprietà, le seguenti chiavi da completare :
```yaml
rabbitmq :
    addresses: amqp://***
admin:
  username:
  password:
gassman:
  api:
    username:
    password:
```

La chiave `rabbitmq` dovrà contenere l'indirizzo AMQP della vostra installazione di RabbitMQ.
La chiave `admin` è una coppia username/password che sarà utilizzata per il login alla pagina di amministrazione : l'encoder che è utilizzato nell'implementazione proposta è `BCryptPasswordEncoder`.
Se volete ad esempio usera la coppia username/passwaord `api/gassman`, dovrete inserire nel file di propeties :
```yaml
rabbitmq :
    addresses: amqp://***
admin:
  username: api
  password: $2a$10$ID/NjgCJ2tm2BCFCIdaV2.Z.Ttz2KrD1FtKebdLMooMDXu8OIYAdy
```

La chiave `gassman>api` è usata invece dal modulo come credenziali per le chiamate REST in uscita. Usando le credenziali di esempio citate in precedenza qui dunque metteremmo :
```yaml
rabbitmq :
    addresses: amqp://***
admin:
  username: api
  password: $2a$10$ID/NjgCJ2tm2BCFCIdaV2.Z.Ttz2KrD1FtKebdLMooMDXu8OIYAdy
gassman:
  api:
    username: api
    password: gassman
```

### Spring Profile
Il modulo va lanciato specificando uno spring active profile "gassman-[NOME_DEL_PROFILO]": il nome sarà quello che avrete scelto come suffisso del vostro file di properties.

### Start GasSMan - Administration Interface (Development mode)
Per lanciare il modulo bisognerà necessariamente utilizzare un IDE (IntelliJ, Eclipse) : lanciando un comando install in MAVEN automaticamente il framework Vaadin lancerà un comando install di NPM (NodeJS).
Tutte le librerie verranno scaricate in locale e l'interfaccia sarà raggiungibile, come da configurazione, all'indirzzo: http://localhost:8884

Questo servizio deve essere lanciato con un Eureka Server già attivo e in ascolto, secondo quanto configurato nel file di properties (application.yml), sulla porta 8880 e con tutti gli altri servizi attivi (il telegram-bot-service et il mail-service sono facoltativi per il funzionamento di questo modulo solo).
Dopo aver correttamente lanciato questo modulo, potrete dunque andare sull'interfaccia di Eureka Server all'indirizzo http://localhost:8880 e verificare che il servizio gassman-admin-service è presente nella lista dei servizi attivi.
