# Rae
Rae Discord bot includes music commands. Writed with KCommando JDA.

[KCommando Framework](https://github.com/MusaBrt/KCommando)

### File Path Scheme
```
Rae
 | lib/
 | Rae.jar
 | config.json
 | data.dat
```
Data File -> Mute times are kept here to avoid data loss.

Config File -> Token, owners, cooldown time etc.

## Config File

A bit complicated.

```json
{
  "token": "TOKEN",
  "prefix": ".",
  "cooldown": 500,
  "owners": ["YOUR-ID"],
  "yasaklikelimeler": "",
  "yasakmesaji": "⛔",
  "yasakmesajigonder": false,
  "hosgeldinmesaji": "Hoş geldin {{member}}! ",
  "hosgeldinmesajigonder": false,
  "muterolename": "Muted"
}

```

## How To Install & Run?

```
git clone https://github.com/MusaBrt/Rae/
cd Rae/
mvn install
..
cd target/Rae/
java -jar Rae.jar
```
After this commands, your Rae build is ready in `target/Rae/`

For run -> `java -jar Rae.jar`
You must fill the config.json file. 

