
# MCVE to demonstrate `requestAnimationFrame()` not being called in background browsers

## Prerequisites

- Podman
- Maven >= 3.6


## How to run the reproducer

### Start

```
[sel-term] $ scripts/start-browser
[run-term] $ mvn clean package
[run-term] $ java -jar target/requestanimationframe-1.0.0.jar
```


### Watch the browser using VNC

```
[new-term] $ echo "secret" | vncviewer -autopass 127.0.0.1:5900
```


### Expected output

This is what you shall see when the reproducer is able to reproduce the error:

```
Info: start 3 test threads
Info: get driver
Info: get driver
Info: get driver
Info: connect to driver http://localhost:4444
Info: connect to driver http://localhost:4444
Info: connect to driver http://localhost:4444
Info: click on button will enqueue requestAnimationFrame() after 1s ...
Info: click on button will enqueue requestAnimationFrame() after 1s ...
Info: click on button will enqueue requestAnimationFrame() after 1s ...
Info: wait for rendering-done selector
Info: wait for rendering-done selector
Info: wait for rendering-done selector
Info: selector found
Err :Timeout waiting for the selector By.cssSelector: #target.done to be visible
Err :Timeout waiting for the selector By.cssSelector: #target.done to be visible
Info: test successful
```

Logging related to Selenium and SLF4 was omitted for brevity. It usually starts with a timestamp, `INFO:` or `SLF4J:`. Relevant logs start with `Info:` and `Err :`.


### If the error were not repoducible (were fixed)

This is what you shall see when the bug were fixed:

```
...
Info: click on button will enqueue requestAnimationFrame() after 1s ...
Info: click on button will enqueue requestAnimationFrame() after 1s ...
Info: click on button will enqueue requestAnimationFrame() after 1s ...
Info: wait for rendering-done selector
Info: wait for rendering-done selector
Info: wait for rendering-done selector
Info: selector found
Info: selector found
Info: selector found
Info: test successful
```
