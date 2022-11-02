# percy-appium-java

[Percy](https://percy.io) visual testing for Java Appium.

## Installation

npm install `@percy/cli`:

```sh-session
$ npm install --save-dev @percy/cli
```

Add percy-appium-java to your project dependencies. If you're using Maven:

``` xml
<dependency>
  <groupId>io.percy</groupId>
  <artifactId>percy-appium-java</artifactId>
  <version>0.0.1</version>
</dependency>
```

If you're using a different build system, see https://search.maven.org/artifact/io.percy/percy-appium-java for details for your specific system.

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
  - `fullScreen` - It indicates if the app is a full screen

### Migrating Config

If you have a previous Percy configuration file, migrate it to the newest version with the
[`config:migrate`](https://github.com/percy/cli/tree/master/packages/cli-config#percy-configmigrate-filepath-output) command:

```sh-session
$ percy config:migrate
```