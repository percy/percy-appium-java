# percy-appium-app

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.percy/percy-appium-app/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.percy/percy-appium-app)
![Test](https://github.com/percy/percy-appium-java/workflows/Test/badge.svg)

[Percy](https://percy.io) visual testing for Java Appium.

## Installation

npm install `@percy/cli`:

```sh-session
$ npm install --save-dev @percy/cli
```
Note: Minimum required version for @percy/cli is 1.15.0 for this package to work correctly.

Add percy-appium-app to your project dependencies. If you're using Maven:

``` xml
<dependency>
  <groupId>io.percy</groupId>
  <artifactId>percy-appium-app</artifactId>
  <version>1.0.1</version>
</dependency>
```

If you're using a different build system, see https://search.maven.org/artifact/io.percy/percy-appium-app for details for your specific system.

> Note: This package is tested on Java versions 8, 11, 12, 13, 17 as part of unit tests. It should ideally work on all Java 8+ versions.


## Usage

This is an example test using the `percy.screenshot` function.

``` java
// import ...
import io.percy.appium.AppPercy;

public class Example {
  private static AppPercy percy;

  public static void main(String[] args) throws MalformedURLException, InterruptedException {
    DesiredCapabilities caps = new DesiredCapabilities();
    // Add caps here

    AndroidDriver<AndroidElement> driver = new AndroidDriver<AndroidElement>(
      new URL("http://hub.browserstack.com/wd/hub"), caps);

    percy = new AppPercy(driver);
    percy.screenshot("First Screenshot");
    driver.quit();
  }
}
```

Running the test above normally will result in the following log:

```sh-session
[percy] Percy is not running, disabling screenshots
```

When running with [`percy
app:exec`](https://github.com/percy/cli/tree/master/packages/cli-app#percy-appexec), and your project's
`PERCY_TOKEN`, a new Percy build will be created and screenshots will be uploaded to your project.

```sh-session
$ export PERCY_TOKEN=[your-project-token]
$ percy exec -- [java test command]
[percy] Percy has started!
[percy] Created build #1: https://percy.io/[your-project]
[percy] Screenshot taken "Java example"
[percy] Stopping percy...
[percy] Finalized build #1: https://percy.io/[your-project]
[percy] Done!
```

## Configuration

The screenshot method arguments:

`percy.screenshot(name, fullScreen)`

- `name` (**required**) - The screenshot name; must be unique to each screenshot
- Additional screenshot options (overrides any project options):
  - `fullScreen ` - (**optional**) It indicates if the app is a full screen
  - `options` - (**optional**) screenshot params:
    Use `ScreenshotOptions` to set following params to override
      - `deviceName` - Device name on which screenshot is taken
      - `statusBarHeight` - int; Height of status bar for the device
      - `navBarHeight` - int; Height of navigation bar for the device
      - `orientation`  - Orientation of the application
      - `FullPage`: boolean; [Experimental] only supported on App Automate driver sessions [ needs @percy/cli 1.20.2+ ]
      - `ScreenLengths`: int; [Experimental] max screen lengths for fullPage [ needs @percy/cli 1.20.2+ ]
### Migrating Config

If you have a previous Percy configuration file, migrate it to the newest version with the
[`config:migrate`](https://github.com/percy/cli/tree/master/packages/cli-config#percy-configmigrate-filepath-output) command:

```sh-session
$ percy config:migrate
```
